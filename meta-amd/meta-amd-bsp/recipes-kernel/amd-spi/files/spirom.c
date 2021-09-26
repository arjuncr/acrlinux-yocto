/*****************************************************************************
*
* spirom.c - SPI ROM client driver
*
* Copyright (c) 2014, Advanced Micro Devices, Inc.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*   
***************************************************************************/
#include <linux/init.h>
#include <linux/module.h>
#include <linux/ioctl.h>
#include <linux/fs.h>
#include <linux/device.h>
#include <linux/err.h>
#include <linux/list.h>
#include <linux/errno.h>
#include <linux/mutex.h>
#include <linux/slab.h>
#include <linux/spi/spi.h>
#include <linux/pci.h>
#include <linux/delay.h>

#include <linux/uaccess.h>

#include "spirom.h"

#define SPIROM_VERSION	"0.2"

/*
 * SPI has a character major number assigned.  We allocate minor numbers
 * dynamically using a bitmask.  You must use hotplug tools, such as udev
 * (or mdev with busybox) to create and destroy the /dev/spiromB.C device
 * nodes, since there is no fixed association of minor numbers with any
 * particular SPI bus or device.
 */
#define SPIROM_MAJOR			153	/* assigned */
#define N_SPI_MINORS			32	/* ... up to 256 */

#define SPI_BUS		0
#define SPI_BUS_CS1	0

static unsigned long	minors[N_SPI_MINORS / BITS_PER_LONG];


struct spirom_data {
	dev_t			devt;
	spinlock_t		spi_lock;
	struct spi_device	*spi;
	struct list_head	device_entry;
	struct completion	done;

	struct mutex		buf_lock;
	unsigned		users;
};

static LIST_HEAD(device_list);
static DEFINE_MUTEX(device_list_lock);

/*-------------------------------------------------------------------------*/

/*
 * We can't use the standard synchronous wrappers for file I/O; we
 * need to protect against async removal of the underlying spi_device.
 */
static void spirom_complete(void *arg)
{
	complete(arg);
}

static ssize_t
spirom_sync(struct spirom_data *spirom, struct spi_message *message)
{
	int status;

	message->complete = spirom_complete;
	message->context = &spirom->done;

	spin_lock_irq(&spirom->spi_lock);
	if (spirom->spi == NULL)
		status = -ESHUTDOWN;
	else
		status = spi_async(spirom->spi, message);
	spin_unlock_irq(&spirom->spi_lock);

	if (status == 0) {
		/*
		 * There might be cases where the controller driver has been
		 * unloaded in the middle of a transaction. So we might end up
		 * in a situation where we will be waiting for an event which
		 * will never happen. So we provide a timeout of 1 second for
		 * situations like this.
		 */
		wait_for_completion_timeout(&spirom->done, HZ);
		status = message->status;
		if (status == 0)
			status = message->actual_length;
	}
	return status;
}

static int spirom_message(struct spirom_data *spirom,
		struct spi_ioc_transfer *u_trans, unsigned long arg)
{
	struct spi_message msg;
	struct spi_transfer *transfer;
	u8 *buffer;
	int status = u_trans->len;

	buffer = u_trans->buf;
	spi_message_init(&msg);

	/* The very first spi_transfer will contain the command only */
	transfer = kzalloc(sizeof(struct spi_transfer), GFP_KERNEL);
	if (!transfer)
		return -ENOMEM;

	transfer->tx_buf = buffer;
	transfer->len = 1;
	buffer += transfer->len;
	spi_message_add_tail(transfer, &msg);

	/*
	 * If the command expects an address as its argument, we populate
	 * it in the very next spi_transfer.
	 */
	if (u_trans->addr_present) {
		transfer = kzalloc(sizeof(struct spi_transfer), GFP_KERNEL);
		if (!transfer)
			return -ENOMEM;

		transfer->tx_buf = buffer;
		transfer->len = 3; // 3-byte address
		buffer += transfer->len;
		spi_message_add_tail(transfer, &msg);
	}

	/*
	 * Next is data, which can have a maximum of 64-bytes, the size limited
	 * by the number of bytes that can stored in the controller FIFO.
	 */
	if (u_trans->len) {
		transfer = kzalloc(sizeof(struct spi_transfer), GFP_KERNEL);
		if (!transfer)
			return -ENOMEM;

		if (u_trans->direction == TRANSMIT)
			transfer->tx_buf = buffer;
		else if (u_trans->direction == RECEIVE)
			transfer->rx_buf = buffer;

		transfer->len = u_trans->len;
		/* No need to increment buffer pointer */
		spi_message_add_tail(transfer, &msg);
	}

	status = spirom_sync(spirom, &msg);

	if (u_trans->direction == RECEIVE) {
		/*
		 * The received data should have been populated in
		 * u_trans->buf, so we just need to copy it into the
		 * user-space buffer.
		 */
		buffer = u_trans->buf;
		if (u_trans->addr_present) {
			buffer += 4; // 1-byte command and 3-byte address
			if(__copy_to_user((u8 __user *)
				(((struct spi_ioc_transfer *)arg)->buf) + 4,
				buffer, u_trans->len)) {
				status = -EFAULT;
			}
		} else {
			buffer += 1; // 1-byte command only
			if(__copy_to_user((u8 __user *)
				(((struct spi_ioc_transfer *)arg)->buf) + 1,
				buffer, u_trans->len)) {
				status = -EFAULT;
			}
		}
	}

	/* Done with everything, free the memory taken by spi_transfer */
	while (msg.transfers.next != &msg.transfers) {
		transfer = list_entry(msg.transfers.next, struct spi_transfer,
				      transfer_list);
		msg.transfers.next = transfer->transfer_list.next;
		transfer->transfer_list.next->prev = &msg.transfers;
		kfree(transfer);
	}

	return status;
}

static long
spirom_ioctl(struct file *filp, unsigned int cmd, unsigned long arg)
{
	int			err = 0;
	int			retval = 0;
	struct spirom_data	*spirom;
	struct spi_device	*spi;
	u32			tmp;
	struct spi_ioc_transfer	*ioc;

	/* Check type and command number */
	if (_IOC_TYPE(cmd) != SPI_IOC_MAGIC)
		return -ENOTTY;

	/* Check access direction once here; don't repeat below.
	 * IOC_DIR is from the user perspective, while access_ok is
	 * from the kernel perspective; so they look reversed.
	 */
	if (_IOC_DIR(cmd) & _IOC_READ)
		err = !access_ok((void __user *)arg, _IOC_SIZE(cmd));
	if (err == 0 && _IOC_DIR(cmd) & _IOC_WRITE)
		err = !access_ok((void __user *)arg, _IOC_SIZE(cmd));
	if (err)
		return -EFAULT;

	/* guard against device removal before, or while,
	 * we issue this ioctl.
	 */
	spirom = filp->private_data;
	spin_lock_irq(&spirom->spi_lock);
	spi = spi_dev_get(spirom->spi);
	spin_unlock_irq(&spirom->spi_lock);

	if (spi == NULL)
		return -ESHUTDOWN;

	/* use the buffer lock here for triple duty:
	 *  - prevent I/O (from us) so calling spi_setup() is safe;
	 *  - prevent concurrent SPI_IOC_WR_* from morphing
	 *    data fields while SPI_IOC_RD_* reads them;
	 *  - SPI_IOC_MESSAGE needs the buffer locked "normally".
	 */
	mutex_lock(&spirom->buf_lock);

	/* segmented and/or full-duplex I/O request */
	if (_IOC_NR(cmd) != _IOC_NR(SPI_IOC_MESSAGE(0)) ||
	    _IOC_DIR(cmd) !=_IOC_WRITE) {
		retval = -ENOTTY;
		goto out;
	}

	tmp = sizeof(struct spi_ioc_transfer);

	/* copy into scratch area */
	ioc = kzalloc(tmp, GFP_KERNEL);
	if (!ioc) {
		retval = -ENOMEM;
		goto out;
	}
	if (__copy_from_user(ioc, (struct spi_ioc_transfer __user *)arg,
	    tmp)) {
		kfree(ioc);
		retval = -EFAULT;
		goto out;
	}

	/* translate to spi_message, execute */
	retval = spirom_message(spirom, ioc, arg);
	kfree(ioc);

out:
	mutex_unlock(&spirom->buf_lock);
	spi_dev_put(spi);
	return retval;
}

static int spirom_open(struct inode *inode, struct file *filp)
{
	struct spirom_data	*spirom;
	int			status = -ENXIO;

	mutex_lock(&device_list_lock);

	list_for_each_entry(spirom, &device_list, device_entry) {
		if (spirom->devt == inode->i_rdev) {
			status = 0;
			break;
		}
	}
	if (status == 0) {
		if (status == 0) {
			spirom->users++;
			filp->private_data = spirom;
			nonseekable_open(inode, filp);
		}
	} else
		pr_debug("spirom: nothing for minor %d\n", iminor(inode));

	mutex_unlock(&device_list_lock);
	return status;
}

static int spirom_release(struct inode *inode, struct file *filp)
{
	struct spirom_data	*spirom;
	int			status = 0;

	mutex_lock(&device_list_lock);
	spirom = filp->private_data;
	filp->private_data = NULL;

	/* last close? */
	spirom->users--;
	if (!spirom->users) {
		int		dofree;

		/* ... after we unbound from the underlying device? */
		spin_lock_irq(&spirom->spi_lock);
		dofree = (spirom->spi == NULL);
		spin_unlock_irq(&spirom->spi_lock);

		if (dofree)
			kfree(spirom);
	}
	mutex_unlock(&device_list_lock);

	return status;
}

static const struct file_operations spirom_fops = {
	.owner =	THIS_MODULE,
	.unlocked_ioctl = spirom_ioctl,
	.open =		spirom_open,
	.release =	spirom_release,
};

static int __init add_spi_device_to_bus(void)
{
	struct spi_master *spi_master;
	struct spi_device *spi_device;
	struct spi_board_info spi_info;

	spi_master = spi_busnum_to_master(SPI_BUS);
	if (!spi_master) {
		printk(KERN_ALERT "Please make sure to \'modprobe "
			"spi_amd\' driver first\n");
		return -1;
	}
	memset(&spi_info, 0, sizeof(struct spi_board_info));

	strlcpy(spi_info.modalias, "spirom", SPI_NAME_SIZE);
	spi_info.bus_num = SPI_BUS; //Bus number of SPI master
	spi_info.chip_select = SPI_BUS_CS1; //CS on which SPI device is connected

	spi_device = spi_new_device(spi_master, &spi_info);
	if (!spi_device)
		return -ENODEV;

	return 0;
}

/*-------------------------------------------------------------------------*/

/* The main reason to have this class is to make mdev/udev create the
 * /dev/spiromB.C character device nodes exposing our userspace API.
 * It also simplifies memory management.
 */

static struct class *spirom_class;

/*-------------------------------------------------------------------------*/

static int spirom_probe(struct spi_device *spi)
{
	struct spirom_data	*spirom;
	int			status;
	unsigned long		minor;

	/* Allocate driver data */
	spirom = kzalloc(sizeof(*spirom), GFP_KERNEL);
	if (!spirom)
		return -ENOMEM;

	/* Initialize the driver data */
	spirom->spi = spi;
	spin_lock_init(&spirom->spi_lock);
	mutex_init(&spirom->buf_lock);

	INIT_LIST_HEAD(&spirom->device_entry);
	init_completion(&spirom->done);

	/* If we can allocate a minor number, hook up this device.
	 * Reusing minors is fine so long as udev or mdev is working.
	 */
	mutex_lock(&device_list_lock);
	minor = find_first_zero_bit(minors, N_SPI_MINORS);
	if (minor < N_SPI_MINORS) {
		struct device *dev;

		spirom->devt = MKDEV(SPIROM_MAJOR, minor);
		dev = device_create(spirom_class, &spi->dev, spirom->devt,
				    spirom, "spirom%d.%d",
				    spi->master->bus_num, spi->chip_select);
		status = IS_ERR(dev) ? PTR_ERR(dev) : 0;
	} else {
		dev_dbg(&spi->dev, "no minor number available!\n");
		status = -ENODEV;
	}
	if (status == 0) {
		set_bit(minor, minors);
		list_add(&spirom->device_entry, &device_list);
	}
	mutex_unlock(&device_list_lock);

	if (status == 0)
		spi_set_drvdata(spi, spirom);
	else
		kfree(spirom);

	return status;
}

static int spirom_remove(struct spi_device *spi)
{
	struct spirom_data	*spirom = spi_get_drvdata(spi);

	/* make sure ops on existing fds can abort cleanly */
	spin_lock_irq(&spirom->spi_lock);
	spirom->spi = NULL;
	spi_set_drvdata(spi, NULL);
	spin_unlock_irq(&spirom->spi_lock);

	/* prevent new opens */
	mutex_lock(&device_list_lock);
	list_del(&spirom->device_entry);
	clear_bit(MINOR(spirom->devt), minors);
	device_destroy(spirom_class, spirom->devt);
	if (spirom->users == 0)
		kfree(spirom);
	mutex_unlock(&device_list_lock);

	return 0;
}

static struct spi_driver spirom_spi = {
	.driver = {
		.name =		"spirom",
		.owner =	THIS_MODULE,
	},
	.probe =	spirom_probe,
	.remove =	spirom_remove,

	/* NOTE:  suspend/resume methods are not necessary here.
	 * We don't do anything except pass the requests to/from
	 * the underlying controller.  The refrigerator handles
	 * most issues; the controller driver handles the rest.
	 */
};

/*-------------------------------------------------------------------------*/

static int __init spirom_init(void)
{
	int status;

	pr_info("AMD SPIROM Driver v%s\n", SPIROM_VERSION);

	/* Claim our 256 reserved device numbers.  Then register a class
	 * that will key udev/mdev to add/remove /dev nodes.  Last, register
	 * the driver which manages those device numbers.
	 */
	BUILD_BUG_ON(N_SPI_MINORS > 256);
	status = register_chrdev(SPIROM_MAJOR, "spi", &spirom_fops);
	if (status < 0)
		return status;

	spirom_class = class_create(THIS_MODULE, "spirom");
	if (IS_ERR(spirom_class)) {
		unregister_chrdev(SPIROM_MAJOR, spirom_spi.driver.name);
		return PTR_ERR(spirom_class);
	}

	status = spi_register_driver(&spirom_spi);
	if (status < 0) {
		class_destroy(spirom_class);
		unregister_chrdev(SPIROM_MAJOR, spirom_spi.driver.name);
	}

	status = add_spi_device_to_bus();
	if (status < 0) {
		spi_unregister_driver(&spirom_spi);
		class_destroy(spirom_class);
		unregister_chrdev(SPIROM_MAJOR, spirom_spi.driver.name);
	}

	return status;
}
module_init(spirom_init);

static void __exit spirom_exit(void)
{
	spi_unregister_driver(&spirom_spi);
	class_destroy(spirom_class);
	unregister_chrdev(SPIROM_MAJOR, spirom_spi.driver.name);
}
module_exit(spirom_exit);

MODULE_AUTHOR("Arindam Nath <arindam.nath@amd.com>");
MODULE_DESCRIPTION("User mode SPI ROM interface");
MODULE_LICENSE("GPL v2");
MODULE_ALIAS("spi:spirom");
