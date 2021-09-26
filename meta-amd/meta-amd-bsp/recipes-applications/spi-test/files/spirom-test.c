/*****************************************************************************
*
* Copyright (c) 2014, Advanced Micro Devices, Inc.
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
#include <stdint.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <dirent.h>
#include <signal.h>

#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/stat.h>

#include <readline/readline.h>

#include "spirom.h"

#define SPI_APP_VERSION "1.0"

static int device_opened = 0;
static char filename[20];
static int fd = -1;

char *show_prompt(void)
{
	return "$ ";
}

void sighandler(int sig)
{
	/* Do nothing. That is the idea. */
}

void show_license(void)
{
	printf("/*****************************************************************************\n"
	       "*\n"
	       "* Copyright (c) 2014, Advanced Micro Devices, Inc.\n"
	       "* All rights reserved.\n"
	       "*\n"
	       "* Redistribution and use in source and binary forms, with or without\n"
	       "* modification, are permitted provided that the following conditions are met:\n"
	       "*     * Redistributions of source code must retain the above copyright\n"
	       "*       notice, this list of conditions and the following disclaimer.\n"
	       "*     * Redistributions in binary form must reproduce the above copyright\n"
	       "*       notice, this list of conditions and the following disclaimer in the\n"
	       "*       documentation and/or other materials provided with the distribution.\n"
	       "*     * Neither the name of Advanced Micro Devices, Inc. nor the names of\n"
	       "*       its contributors may be used to endorse or promote products derived\n"
	       "*       from this software without specific prior written permission.\n"
	       "*\n"
	       "* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND\n"
	       "* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n"
	       "* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n"
	       "* DISCLAIMED. IN NO EVENT SHALL ADVANCED MICRO DEVICES, INC. BE LIABLE FOR ANY\n"
	       "* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\n"
	       "* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\n"
	       "* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\n"
	       "* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
	       "* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\n"
	       "* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n"
	       "*\n"
	       "*\n"
	       "***************************************************************************/\n");
}

void print_usage(void)
{
	printf("\nCommands Supported ->\n");
	printf(" enumerate				: List all SPI device nodes available\n");
	printf(" setdevice <dev_id>			: Set the SPI device number to access\n");
	printf(" wren					: Enable Write operation on SPI device\n");
	printf(" wrdi					: Disable Write operation on SPI device\n");
	printf(" chiperase				: Erase entire ROM chip\n");
	printf(" rdsr					: Read status register of ROM device\n");
	printf(" rdid					: Read device identification string\n");
	printf(" sectorerase <addr> <num_sectors>	: Erase a fixed number of sectors starting at the address\n"
	       "                                          specified\n");
	printf(" blockerase <addr> <num_blocks>		: Erase a fixed number of blocks starting at the address\n"
	       "                                          specified\n");
	printf(" read <addr> <num_bytes> <filename>	: Read a fixed number of bytes starting at address\n"
	       "                                          specified, and output the contents into file\n");
	printf(" write <addr> <num_bytes> <filename>	: Read a fixed number of bytes from file and output\n"
	       "                                          the contents to the device starting at the address\n"
	       "                                          specified\n");
	printf(" license				: Displays the terms of LICENSE for this application\n");
	printf(" help					: Displays help text\n");
	printf(" exit					: Exits the application\n\n");
}

void parse_cmd(const char *cmdline)
{
	struct spi_ioc_transfer tr;
	unsigned int bytes_chunks;
	unsigned int remaining_bytes;
	int addr;
	int ret;

	if (strncmp(cmdline, "enumerate", 9) == 0) {
		DIR *dir;
		struct dirent *dir_entry;
		int device_found = 0;

		/* Get the directory handle */
		if ((dir = opendir("/dev")) == NULL) {
			printf("\n\nFailed to open directory /dev. Probably you "
			       "do not have right privilege!\n\n");
			exit(EXIT_FAILURE);
		}

		/* Iterate over all the directory entries */
		while ((dir_entry = readdir(dir)) != NULL) {
			/*
			 * If the file is a character device, and its signature
			 * matches spirom, then we print the corresponding file.
			 */
			if ((dir_entry->d_type == DT_CHR) &&
			    (strncmp(dir_entry->d_name, "spirom", 6) == 0)) {
				printf("/dev/%s\n", dir_entry->d_name);
				device_found = 1;
			}
		}

		printf("\n");

		/*
		 * In case we did not find even a single entry, we print a
		 * message and exit.
		 */
		if (!device_found) {
			printf("\n\nNo spirom device nodes found, load spirom "
			       "kernel module and try again\n\n");
			exit(EXIT_FAILURE);
		}
	} else if (strncmp(cmdline, "setdevice", 9) == 0) {
		char input[2 + 1];
		int file_desc;

		cmdline += 10;
		memset(input, 0, 3);
		if (sscanf(cmdline, "%s", input) < 1) {
			printf("\nInvalid inputs, please try again\n\n");
			return;
		}

		memset(filename, 0, 20);
		snprintf(filename, 19, "/dev/spirom%s", input);
		file_desc = open(filename, O_RDWR);
		if (file_desc < 0) {
			printf("\nError opening file %s\n\n", filename);
			return;
		}

		/* Once we have validated inputs, we store them into the global
		 * variables used at other places in the program.
		 */
		fd = file_desc;
		device_opened = 1;
		printf("\nSPI device set to /dev/spirom%s\n\n", input);
	} else if (strncmp(cmdline, "wren", 4) == 0) {
		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		/* command without data */
		tr.buf[0] = ROM_WREN;
		tr.direction = 0;
		tr.len = 0;
		tr.addr_present = 0;

		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1)
			printf("\nError executing WREN command\n\n");
		else
			printf("\n...WREN completed successfully\n\n");
	} else if (strncmp(cmdline, "wrdi", 4) == 0) {
		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		/* command without data */
		tr.buf[0] = ROM_WRDI;
		tr.direction = 0;
		tr.len = 0;
		tr.addr_present = 0;

		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1)
			printf("\nError executing WRDI command\n\n");
		else
			printf("\n...WRDI completed successfully\n\n");
	} else if (strncmp(cmdline, "chiperase", 9) == 0) {
		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		tr.buf[0] = ROM_RDSR;
		tr.direction = RECEIVE;
		tr.addr_present = 0;
		tr.len = 1;

		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1) {
			printf("\nError executing RDSR command\n\n");;
			return;
		} else if ((tr.buf[1] & 0x02) == 0x00) {
			printf("\nCannot execute CHIPERASE command, write is disabled\n\n");
			return;
		}

		/* Command without data */
		tr.buf[0] = ROM_CHIP_ERASE;
		tr.direction = 0;
		tr.len = 0;
		tr.addr_present = 0;
		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1) {
			printf("\nError executing CHIPERASE command\n\n");
			return;
		}

		printf("\n\nCHIPERASE operation in progress, please do not "
		       " stop in between.\n\n");

		/* Make sure WIP has been reset */
		while (1) {
			memset(&tr, 0, sizeof(struct spi_ioc_transfer));
			tr.buf[0] = ROM_RDSR;
			tr.direction = RECEIVE;
			tr.addr_present = 0;
			tr.len = 1;

			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing RDSR command\n\n");
				return;
			}

			if ((tr.buf[1] & 0x01) == 0x00)
				break;
		}

		printf("\n\n...CHIPERASE completed successfully\n\n");
		/* Restore signal handler to default */
	} else if (strncmp(cmdline, "rdsr", 4) == 0) {
		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		/* Command with response */
		tr.buf[0] = ROM_RDSR;
		tr.direction = RECEIVE;
		tr.addr_present = 0;
		tr.len = 1;

		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1) {
			printf("\nError executing RDSR command\n\n");
			return;
		}

		/*
		 * The 1-byte response will be stored in tr.buf,
		 * so print it out
		 */
		printf("\nRDSR command returned: 0x%.2x\n\n", tr.buf[1]);
	} else if (strncmp(cmdline, "rdid", 4) == 0) {
		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		/* Command with response */
		tr.buf[0] = ROM_RDID;
		tr.direction = RECEIVE;
		tr.addr_present = 0;
		tr.len = 3;

		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1) {
			printf("\nError executing RDID command\n\n");
			return;
		}

		/*
		 * The 3-bytes response will be stored in tr.buf,
		 * so print it out
		 */
		printf("\nRDID command returned: 0x%.2x%.2x%.2x\n", tr.buf[1],
			tr.buf[2], tr.buf[3]);
	} else if (strncmp(cmdline, "sectorerase", 11) == 0) {
		int nsectors;
		int i;

		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		cmdline += 12;
		if (sscanf(cmdline, "0x%x 0x%x", &addr, &nsectors) < 2) {
			printf("\nInvalid inputs, please try again\n\n");
			return;
		}

		tr.buf[0] = ROM_RDSR;
		tr.direction = RECEIVE;
		tr.addr_present = 0;
		tr.len = 1;

		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1) {
			printf("\nError executing RDSR command\n\n");
			return;
		} else if ((tr.buf[1] & 0x02) == 0x00) {
			printf("\nCannot execute SECTORERASE command, write is disabled\n\n");
			return;
		}

		printf("\n\nSECTORERASE operation in progress, please do not "
		       " stop in between.\n\n");

		for (i = 0; i < nsectors; i++) {
			/* Write Enable before Sector Erase */
			tr.buf[0] = ROM_WREN;
			tr.direction = 0;
			tr.len = 0;
			tr.addr_present = 0;
			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing WREN command\n\n");
				return;
			}

			/* Command with address but no data */
			memset(&tr, 0, sizeof(struct spi_ioc_transfer));
			tr.buf[0] = ROM_SECTOR_ERASE;
			tr.buf[3] = addr & 0xff;
			tr.buf[2] = (addr >> 8) & 0xff;
			tr.buf[1] = (addr >> 16) & 0xff;
			tr.addr_present = 1;
			tr.direction = 0;
			tr.len = 0;

			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing SECTORERASE command\n\n");
				return;
			}

			/* point to the next 4k sector */
			addr += 4 * 1024;

			/*
			 * Before the next loop, we need to make sure that WIP
			 * bit in the output of RDSR has been reset.
			 */
			while (1) {
				memset(&tr, 0, sizeof(struct spi_ioc_transfer));
				tr.buf[0] = ROM_RDSR;
				tr.direction = RECEIVE;
				tr.addr_present = 0;
				tr.len = 1;

				ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
				if (ret < 1) {
					printf("\nError executing RDSR command\n\n");
					return;
				}

				if ((tr.buf[1] & 0x01) == 0x00)
					break;
			}
		}

		printf("\n\n...SECTORERASE completed successfully\n\n");
	} else if (strncmp(cmdline, "blockerase", 10) == 0) {
		int nblocks;
		int i;

		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		cmdline += 11;
		if (sscanf(cmdline, "0x%x 0x%x", &addr, &nblocks) < 2) {
			printf("\nInvalid inputs, please try again\n\n");
			return;
		}

		tr.buf[0] = ROM_RDSR;
		tr.direction = RECEIVE;
		tr.addr_present = 0;
		tr.len = 1;

		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1) {
			printf("\nError executing RDSR command\n\n");
			return;
		} else if ((tr.buf[1] & 0x02) == 0x00) {
			printf("\nError executing BLOCKERASE command, write is disabled\n\n");
			return;
		}

		printf("\n\nBLOCKERASE operation in progress, please do not "
		       " stop in between.\n\n");

		for (i = 0; i < nblocks; i++) {
			/* Write Enable before Block Erase */
			tr.buf[0] = ROM_WREN;
			tr.direction = 0;
			tr.len = 0;
			tr.addr_present = 0;
			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing WREN command\n\n");
				return;
			}

			/* Command with address but no data */
			memset(&tr, 0, sizeof(struct spi_ioc_transfer));
			tr.buf[0] = ROM_BLOCK_ERASE;
			tr.buf[3] = addr & 0xff;
			tr.buf[2] = (addr >> 8) & 0xff;
			tr.buf[1] = (addr >> 16) & 0xff;
			tr.addr_present = 1;
			tr.direction = 0;
			tr.len = 0;

			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing BLOCKERASE command\n\n");
				return;
			}

			/* point to the next 64k block */
			addr += 64 * 1024;

			/*
			 * Before the next loop, we need to make sure that WIP
			 * bit in the output of RDSR has been reset.
			 */
			while (1) {
				memset(&tr, 0, sizeof(struct spi_ioc_transfer));
				tr.buf[0] = ROM_RDSR;
				tr.direction = RECEIVE;
				tr.addr_present = 0;
				tr.len = 1;

				ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
				if (ret < 1) {
					printf("\nError executing RDSR command\n\n");
					return;
				}

				if ((tr.buf[1] & 0x01) == 0x00)
					break;
			}
		}

		printf("\n\n...BLOCKERASE completed successfully\n\n");
	} else if (strncmp(cmdline, "read", 4) == 0) {
		int nbytes;
		int outfile_fd;
		int i;

		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		cmdline += 5;
		memset(filename, 0, 20);
		if (sscanf(cmdline, "0x%x 0x%x %s", &addr, &nbytes, filename) < 3) {
			printf("\nInvalid inputs, please try again\n\n");
			return;
		}

		/*
		 * Open the output file for writing. Create a new file if not
		 * there, and empty the file before writing if file already
		 * exists.
		 */
		outfile_fd = open(filename, O_WRONLY | O_CREAT | O_TRUNC, 0644);
		if (outfile_fd < 0) {
			printf("\nError opening file %s for writing\n\n", filename);
			return;
		}

		/*
		 * We will break down the bytes to be received in chunks of
		 * of 64-bytes. Data might not be a even multiple of 64. So
		 * in that case, we will have some remaining bytes <4. We
		 * handle that separately.
		 */
		bytes_chunks = nbytes / 64;
		remaining_bytes = nbytes % 64;

		printf("\n\nREAD operation in progress.\n\n");

		for (i = 0; i < bytes_chunks; i++) {
			/* Command with address and data */
			memset(&tr, 0, sizeof(struct spi_ioc_transfer));
			tr.buf[0] = ROM_READ;
			tr.direction = RECEIVE;
			/*
			 * We will store the address into the buffer in little
			 * endian order.
			 */
			tr.buf[3] = addr & 0xff;
			tr.buf[2] = (addr >> 8) & 0xff;
			tr.buf[1] = (addr >> 16) & 0xff;
			tr.len = 64;
			tr.addr_present = 1;

			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing READ command\n\n");
				return;
			}

			/* Write the data read to output file */
			if (write(outfile_fd, &tr.buf[4], tr.len) < 0) {
				printf("\nError writing to file %s\n\n", filename);
				return;
			}
			addr += 64;
		}

		if (remaining_bytes) {
			memset(&tr, 0, sizeof(struct spi_ioc_transfer));
			tr.buf[0] = ROM_READ;
			tr.direction = RECEIVE;
			tr.buf[3] = addr & 0xff;
			tr.buf[2] = (addr >> 8) & 0xff;
			tr.buf[1] = (addr >> 16) & 0xff;
			tr.len = remaining_bytes;
			tr.addr_present = 1;

			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing READ command\n\n");
				return;
			}

			if (write(outfile_fd, &tr.buf[4], tr.len) < 0) {
				printf("\nError writing to file %s\n\n", filename);
				return;
			}
		}

		printf("\n\n...READ completed successfully\n\n");
		close(outfile_fd);
	} else if (strncmp(cmdline, "write", 5) == 0) {
		int nbytes;
		int infile_fd;
		int i;

		if (!device_opened) {
			printf("\nSPI device needs to be set before you can "
			       "perform this operation\n\n");
			return;
		}

		cmdline += 6;
		memset(filename, 0, 20);
		if (sscanf(cmdline, "0x%x 0x%x %s", &addr, &nbytes, filename) < 3) {
			printf("\nInvalid inputs, please try again\n\n");
			return;
		}

		/* Open the input file for reading*/
		infile_fd = open(filename, O_RDONLY);
		if (infile_fd < 0) {
			printf("\nError opening file %s for reading\n\n", filename);
			return;
		}

		/*
		 * We will break down the bytes to be transmitted in chunks of
		 * of 64-bytes. Like for read, we might not have data in an
		 * even multiple of 64 bytes. So we will handle the remaining
		 * bytes in the end.
		 */
		tr.buf[0] = ROM_RDSR;
		tr.direction = RECEIVE;
		tr.addr_present = 0;
		tr.len = 1;

		ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
		if (ret < 1) {
			printf("\nError executing RDSR command\n\n");
			return;
		} else if ((tr.buf[1] & 0x02) == 0x00) {
			printf("\nCannot execute WRITE command, write is disabled\n\n");
			return;
		}

		bytes_chunks = nbytes / 64;
		remaining_bytes = nbytes % 64;

		printf("\n\nWRITE operation in progress, please do not "
		       " stop in between.\n\n");

		for (i = 0; i < bytes_chunks; i++) {
			tr.buf[0] = ROM_WREN;
			tr.direction = 0;
			tr.len = 0;
			tr.addr_present = 0;
			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing WREN command\n\n");
				return;
			}

			/* Command with data and address */
			memset(&tr, 0, sizeof(struct spi_ioc_transfer));
			tr.buf[0] = ROM_WRITE;
			tr.direction = TRANSMIT;
			/*
			 * We will store the address into the buffer in little
			 * endian order.
			 */
			tr.buf[3] = addr & 0xff;
			tr.buf[2] = (addr >> 8) & 0xff;
			tr.buf[1] = (addr >> 16) & 0xff;
			tr.len = 64;
			tr.addr_present = 1;

			/* Read 64 bytes from input file to buffer */
			if (read(infile_fd, &tr.buf[4], tr.len) < 0) {
				printf("\nError reading from file %s\n\n", filename);
				return;
			}

			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing WRITE command\n\n");
				return;
			}

			addr += 64;

			/*
			 * Before the next loop, we need to make sure that WIP
			 * bit in the output of RDSR has been reset.
			 */
			while (1) {
				memset(&tr, 0, sizeof(struct spi_ioc_transfer));
				tr.buf[0] = ROM_RDSR;
				tr.direction = RECEIVE;
				tr.addr_present = 0;
				tr.len = 1;

				ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
				if (ret < 1) {
					printf("\nError executing RDSR command\n\n");
					return;
				}

				if ((tr.buf[1] & 0x01) == 0x00)
					break;
			}
		}

		if (remaining_bytes) {
			tr.buf[0] = ROM_WREN;
			tr.direction = 0;
			tr.len = 0;
			tr.addr_present = 0;
			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing WREN command\n\n");
				return;
			}

			memset(&tr, 0, sizeof(struct spi_ioc_transfer));
			tr.buf[0] = ROM_WRITE;
			tr.direction = TRANSMIT;
			tr.buf[3] = addr & 0xff;
			tr.buf[2] = (addr >> 8) & 0xff;
			tr.buf[1] = (addr >> 16) & 0xff;
			tr.len = remaining_bytes;
			tr.addr_present = 1;

			if (read(infile_fd, &tr.buf[4], tr.len) < 0) {
				printf("\nError reading from file %s\n\n", filename);
				return;
			}

			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
			if (ret < 1) {
				printf("\nError executing WRITE command\n\n");
				return;
			}

			while (1) {
				memset(&tr, 0, sizeof(struct spi_ioc_transfer));
				tr.buf[0] = ROM_RDSR;
				tr.direction = RECEIVE;
				tr.addr_present = 0;
				tr.len = 1;

				ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
				if (ret < 1) {
					printf("\nError executing RDSR command\n\n");
					return;
				}

				if ((tr.buf[1] & 0x01) == 0x00)
					break;
			}
		}

		printf("\n\n...WRITE completed successfully\n\n");
		close(infile_fd);
	} else if (strncmp(cmdline, "license", 7) == 0) {
		show_license();
	} else if (strncmp(cmdline, "exit", 4) == 0) {
		printf("\nExiting...\n");
		close(fd);
		exit(EXIT_SUCCESS);
	} else if (strncmp(cmdline, "help", 4) == 0) {
		print_usage();
	} else {
		printf("\nUnknown command\n");
		print_usage();
	}
}

int main(void)
{
	char *cmdline= NULL;

	printf("SPI sample application version: %s\n", SPI_APP_VERSION);
	printf("Copyright (c) 2014, Advanced Micro Devices, Inc.\n"
	       "This sample application comes with ABSOLUTELY NO WARRANTY;\n"
	       "This is free software, and you are welcome to redistribute it\n"
	       "under certain conditions; type `license` for details.\n\n");

	/* Set the signal handler */
	signal(SIGINT, sighandler);

	while (1) {
		cmdline = readline(show_prompt());
		parse_cmd(cmdline);
		/* Free the memory malloc'ed by readline */
		free(cmdline);
	}

	/* Restore the default signal handler */
	signal(SIGINT, SIG_DFL);

	/* Should never reach here */
	return 0;
}
