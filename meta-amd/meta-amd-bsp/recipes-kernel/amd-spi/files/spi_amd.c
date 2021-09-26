/*****************************************************************************
*
* Copyright (c) 2013, Advanced Micro Devices, Inc.
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of Advanced Micro Devices, Inc. nor the names of
*       its contributors may be used to endorse or promote products derived
*       from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL ADVANCED MICRO DEVICES, INC. BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*
***************************************************************************/
#include <linux/init.h>
#include <linux/module.h>
#include <linux/pci.h>
#include <linux/spi/spi.h>
#include <linux/kthread.h>

#include "spi_amd.h"

struct amd_platform_data {
	u8 chip_select;
};

struct amd_spi {
	void __iomem *io_remap_addr;
	unsigned long io_base_addr;
	u32 rom_addr;
	struct spi_master *master;
	struct amd_platform_data controller_data;
	struct task_struct *kthread_spi;
	struct list_head msg_queue;
	wait_queue_head_t wq;
};

static struct pci_device_id amd_spi_pci_device_id[] = {
	{ PCI_DEVICE(PCI_VENDOR_ID_AMD, PCI_DEVICE_ID_AMD_LPC_BRIDGE) },
	{}
};
MODULE_DEVICE_TABLE(pci, amd_spi_pci_device_id);

static inline u8 amd_spi_readreg8(struct spi_master *master, int idx)
{
	struct amd_spi *amd_spi = spi_master_get_devdata(master);

	return ioread8((u8 *)amd_spi->io_remap_addr + idx);
}

static inline void amd_spi_writereg8(struct spi_master *master, int idx,
				     u8 val)
{
	struct amd_spi *amd_spi = spi_master_get_devdata(master);

	iowrite8(val, ((u8 *)amd_spi->io_remap_addr + idx));
}

static inline void amd_spi_setclear_reg8(struct spi_master *master, int idx,
					 u8 set, u8 clear)
{
	u8 tmp = amd_spi_readreg8(master, idx);
	tmp = (tmp & ~clear) | set;
	amd_spi_writereg8(master, idx, tmp);
}

static inline u32 amd_spi_readreg32(struct spi_master *master, int idx)
{
	struct amd_spi *amd_spi = spi_master_get_devdata(master);

	return ioread32((u8 *)amd_spi->io_remap_addr + idx);
}

static inline void amd_spi_writereg32(struct spi_master *master, int idx,
					u32 val)
{
	struct amd_spi *amd_spi = spi_master_get_devdata(master);

	iowrite32(val, ((u8 *)amd_spi->io_remap_addr + idx));
}

static inline void amd_spi_setclear_reg32(struct spi_master *master, int idx,
					  u32 set, u32 clear)
{
	u32 tmp = amd_spi_readreg32(master, idx);
	tmp = (tmp & ~clear) | set;
	amd_spi_writereg32(master, idx, tmp);
}

static void amd_spi_select_chip(struct spi_master *master)
{
	struct amd_spi *amd_spi = spi_master_get_devdata(master);
	u8 chip_select = amd_spi->controller_data.chip_select;

	amd_spi_setclear_reg8(master, AMD_SPI_ALT_CS_REG, chip_select,
			      AMD_SPI_ALT_CS_MASK);
}


static void amd_spi_clear_fifo_ptr(struct spi_master *master)
{
	amd_spi_setclear_reg32(master, AMD_SPI_CTRL0_REG, AMD_SPI_FIFO_CLEAR,
			       AMD_SPI_FIFO_CLEAR);
}

static void amd_spi_set_opcode(struct spi_master *master, u8 cmd_opcode)
{
	amd_spi_setclear_reg32(master, AMD_SPI_CTRL0_REG, cmd_opcode,
			       AMD_SPI_OPCODE_MASK);
}

static inline void amd_spi_set_rx_count(struct spi_master *master,
					u8 rx_count)
{
	amd_spi_setclear_reg8(master, AMD_SPI_RX_COUNT_REG, rx_count, 0xff);
}

static inline void amd_spi_set_tx_count(struct spi_master *master,
					u8 tx_count)
{
	amd_spi_setclear_reg8(master, AMD_SPI_TX_COUNT_REG, tx_count, 0xff);
}

static void amd_spi_execute_opcode(struct spi_master *master)
{
	struct amd_spi *amd_spi = spi_master_get_devdata(master);
	bool spi_busy;

	/* Set ExecuteOpCode bit in the CTRL0 register */
	amd_spi_setclear_reg32(master, AMD_SPI_CTRL0_REG, AMD_SPI_EXEC_CMD,
			       AMD_SPI_EXEC_CMD);

	/* poll for SPI bus to become idle */
	spi_busy = (ioread32((u8 *)amd_spi->io_remap_addr +
		    AMD_SPI_CTRL0_REG) & AMD_SPI_BUSY) == AMD_SPI_BUSY;
	while (spi_busy) {
		set_current_state(TASK_INTERRUPTIBLE);
		schedule();
		set_current_state(TASK_RUNNING);
		spi_busy = (ioread32((u8 *)amd_spi->io_remap_addr +
			    AMD_SPI_CTRL0_REG) & AMD_SPI_BUSY) == AMD_SPI_BUSY;
	}
}

/* Helper function */
#ifdef CONFIG_SPI_DEBUG
static void amd_spi_dump_reg(struct spi_master *master)
{
	struct amd_spi *amd_spi = spi_master_get_devdata(master);

	printk(KERN_DEBUG DRIVER_NAME ": SPI CTRL 0 registers: 0x%.8x\n",
		ioread32((u8 *)amd_spi->io_remap_addr + AMD_SPI_CTRL0_REG));
	/*
	 * We cannot read CTRL1 register, because reading it would
	 * inadvertently increment the FIFO pointer.
	 */
	printk(KERN_DEBUG DRIVER_NAME ": SPI ALT CS registers: 0x%.2x\n",
		ioread8((u8 *)amd_spi->io_remap_addr + AMD_SPI_ALT_CS_REG));
	printk(KERN_DEBUG DRIVER_NAME ": SPI Tx Byte Count: 0x%.2x\n",
		ioread8((u8 *)amd_spi->io_remap_addr + AMD_SPI_TX_COUNT_REG));
	printk(KERN_DEBUG DRIVER_NAME ": SPI Rx Byte Count: 0x%.2x\n",
		ioread8((u8 *)amd_spi->io_remap_addr + AMD_SPI_RX_COUNT_REG));
	printk(KERN_DEBUG DRIVER_NAME ": SPI Status registers: 0x%.8x\n",
		ioread32((u8 *)amd_spi->io_remap_addr + AMD_SPI_STATUS_REG));
}
#else
static void amd_spi_dump_reg(struct spi_master *master) {}
#endif


static int amd_spi_master_setup(struct spi_device *spi)
{
	struct spi_master *master = spi->master;
	struct amd_spi *amd_spi = spi_master_get_devdata(master);

	amd_spi->controller_data.chip_select = spi->chip_select;

	amd_spi_select_chip(master);

	return 0;
}

static int amd_spi_master_transfer(struct spi_master *master,
				   struct spi_message *msg)
{
	struct amd_spi *amd_spi = spi_master_get_devdata(master);

	/*
	 * Add new message to the queue and let the kernel thread know
	 * about it.
	 */
	list_add_tail(&msg->queue, &amd_spi->msg_queue);
	wake_up_interruptible(&amd_spi->wq);

	return 0;
}
static int amd_spi_thread(void *t)
{
	struct amd_spi *amd_spi = t;
	struct spi_master *master = amd_spi->master;
	struct spi_transfer *transfer = NULL;
	struct spi_message *message = NULL;
	int direction = 0,i = 0,saved_index = 0;
	int opcode_found = 0,recv_flag = 0,tx_len = 0,rx_len = 0;
	u8 cmd_opcode = 0;
	long timeout = 0;
	u8 *buffer = NULL;

	/*
	 * What we do here is actually pretty simple. We pick one message
	 * at a time from the message queue set up by the controller, and
	 * then process all the spi_transfers of that spi_message in one go.
	 * We then remove the message from the queue, and complete the
	 * transaction. This might not be the best approach, but this is how
	 * we chose to implement this. Note that out SPI controller has FIFO
	 * size of 70 bytes, but we consider it to contain a maximum of
	 * 64-bytes of data and 3-bytes of address.
	 */
	while (1) {
		/*
		 * Let us wait on a wait queue till the message queue is empty.
		 */
		do {
			timeout = wait_event_interruptible_timeout(amd_spi->wq,
					 !list_empty(&amd_spi->msg_queue),1000);

			/* check stop condition */		
			if (kthread_should_stop()) {
				set_current_state(TASK_RUNNING);
				return 0;
			}
		} while(timeout == 0);

		/*
		 * Else, pull the very first message from the queue and process
		 * all transfers within that message. And process the messages
		 * in a pure linear fashion. We also remove the spi_message
		 * from the queue.
		 */
		message = list_entry(amd_spi->msg_queue.next,
				     struct spi_message, queue);
		list_del_init(&message->queue);

		/* We store the CS# line to be used for this spi_message */
		amd_spi->controller_data.chip_select =
						message->spi->chip_select;

		/* Setting all variables to default value. */
		direction = i = 0;
		opcode_found = 0;
		recv_flag = tx_len = rx_len = 0;
		cmd_opcode = 0;
		buffer = NULL;
		saved_index = 0;

		amd_spi_select_chip(master);

		/*
		 * This loop extracts spi_transfers from the spi message,
		 * programs the command into command register. Pointer variable
		 * *buffer* points to either tx_buf or rx_buf of spi_transfer
		 * depending on direction of transfer. Also programs FIFO of
		 * controller if data has to be transmitted.
		 */
		list_for_each_entry(transfer, &message->transfers,
				    transfer_list)
		{
			if(transfer->rx_buf != NULL)
				direction = RECEIVE;
			else if(transfer->tx_buf != NULL)
				direction = TRANSMIT;

			switch (direction) {
			case TRANSMIT:
				buffer = (u8 *)transfer->tx_buf;

				if(opcode_found != 1) {
					/* Store no. of bytes to be sent into
					 * FIFO  */
					tx_len = transfer->len - 1;
					/* Store opcode  */
					cmd_opcode = *(u8 *)transfer->tx_buf;
					/* Pointing to start of TX data  */
					buffer++;
					/* Program the command register*/
					amd_spi_set_opcode(master, cmd_opcode);
					opcode_found = 1;
				} else {
					/* Store no. of bytes to be sent into
					 * FIFO  */
					tx_len = transfer->len;
				}

				/* Write data into the FIFO. */
				for (i = 0; i < tx_len; i++) {
					iowrite8(buffer[i],
						 ((u8 *)amd_spi->io_remap_addr +
						 AMD_SPI_FIFO_BASE +
						 i + saved_index));
				}

				/* Set no. of bytes to be transmitted */
				amd_spi_set_tx_count(master,
						     tx_len + saved_index);

				/*
				 * Saving the index, from where next
				 * spi_transfer's data will be stored in FIFO.
				 */
				saved_index = i;
				break;
			case RECEIVE:
				/* Store no. of bytes to be received from
				 * FIFO */
				rx_len = transfer->len;
				buffer = (u8 *)transfer->rx_buf;
				recv_flag=1;
				break;
			}
		}

		/* Set the RX count to the number of bytes to expect in
		 * response */
		amd_spi_set_rx_count(master, rx_len );
		amd_spi_clear_fifo_ptr(master);
		amd_spi_dump_reg(master);
		/* Executing command */
		amd_spi_execute_opcode(master);
		amd_spi_dump_reg(master);

		if(recv_flag == 1) {
			/* Read data from FIFO to receive buffer  */
			for (i = 0; i < rx_len; i++) {
				buffer[i] = ioread8((u8 *)amd_spi->io_remap_addr
						    + AMD_SPI_FIFO_BASE
						    + tx_len + i);
			}

			recv_flag = 0;
		}

		/* Update statistics */
		message->actual_length = tx_len + rx_len + 1 ;
		/* complete the transaction */
		message->status = 0;
		spi_finalize_current_message(master);
	}

	return 0;
}

static int amd_spi_pci_probe(struct pci_dev *pdev,
			     const struct pci_device_id *id)
{
	struct device *dev = &pdev->dev;
	struct spi_master *master;
	struct amd_spi *amd_spi;
	u32 io_base_addr;
	int err = 0;

	/* Allocate storage for spi_master and driver private data */
	master = spi_alloc_master(dev, sizeof(struct amd_spi));
	if (master == NULL) {
		dev_err(dev, "Error allocating SPI master\n");
		return -ENOMEM;
	}

	amd_spi = spi_master_get_devdata(master);
	amd_spi->master = master;

	/*
	 * Lets first get the base address of SPI registers. The SPI Base
	 * Address is stored at offset 0xA0 into the LPC PCI configuration
	 * space. As per the specification, it is stored at bits 6:31 of the
	 * register. The address is aligned at 64-byte boundary,
	 * so we should just mask the lower 6 bits and get the address.
	 */
	pci_read_config_dword(pdev, AMD_PCI_LPC_SPI_BASE_ADDR_REG,
			      &io_base_addr);
	amd_spi->io_base_addr = io_base_addr & AMD_SPI_BASE_ADDR_MASK;
	amd_spi->io_remap_addr = ioremap_nocache(amd_spi->io_base_addr,
						 AMD_SPI_MEM_SIZE);
	if (amd_spi->io_remap_addr == NULL) {
		dev_err(dev, "ioremap of SPI registers failed\n");
		err = -ENOMEM;
		goto err_free_master;
	}
	dev_dbg(dev, "io_base_addr: 0x%.8lx, io_remap_address: %p\n",
		amd_spi->io_base_addr, amd_spi->io_remap_addr);
	INIT_LIST_HEAD(&amd_spi->msg_queue);
	init_waitqueue_head(&amd_spi->wq);
	amd_spi->kthread_spi = kthread_run(amd_spi_thread, amd_spi,
					   "amd_spi_thread");

	/* Now lets initialize the fields of spi_master */
	master->bus_num = 0;	/*
				 * This should be the same as passed in
				 * spi_board_info structure
				 */
	master->num_chipselect = 4; /* Can be overwritten later during setup */
	master->mode_bits = 0;
	master->flags = 0;
	master->setup = amd_spi_master_setup;
	master->transfer_one_message = amd_spi_master_transfer;
	/* Register the controller with SPI framework */
	err = spi_register_master(master);
	if (err) {
		dev_err(dev, "error registering SPI controller\n");
		goto err_iounmap;
	}
	pci_set_drvdata(pdev, amd_spi);

	return 0;

err_iounmap:
	iounmap(amd_spi->io_remap_addr);
err_free_master:
	spi_master_put(master);

	return 0;
}

static void amd_spi_pci_remove(struct pci_dev *pdev)
{
	struct amd_spi *amd_spi = pci_get_drvdata(pdev);

	kthread_stop(amd_spi->kthread_spi);
	iounmap(amd_spi->io_remap_addr);
	spi_unregister_master(amd_spi->master);
	spi_master_put(amd_spi->master);
	pci_set_drvdata(pdev, NULL);
}

static struct pci_driver amd_spi_pci_driver = {
	.name = "amd_spi",
	.id_table = amd_spi_pci_device_id,
	.probe = amd_spi_pci_probe,
	.remove = amd_spi_pci_remove,
};

static int __init amd_spi_init(void)
{
	int ret;

	pr_info("AMD SPI Driver v%s\n", SPI_VERSION);

	ret = pci_register_driver(&amd_spi_pci_driver);
	if (ret)
		return ret;

	return 0;
}
module_init(amd_spi_init);

static void __exit amd_spi_exit(void)
{
	pci_unregister_driver(&amd_spi_pci_driver);
}
module_exit(amd_spi_exit);

MODULE_LICENSE("Dual BSD/GPL");
MODULE_AUTHOR("Arindam Nath <arindam.nath@amd.com>");
MODULE_AUTHOR("Sanjay Mehta <sanju.mehta@amd.com>");
MODULE_DESCRIPTION("AMD SPI Master Controller Driver");
