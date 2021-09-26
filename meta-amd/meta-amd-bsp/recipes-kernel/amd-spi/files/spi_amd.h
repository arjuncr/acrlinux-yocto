#ifndef SPI_AMD_H
#define SPI_AMD_H

#define DRIVER_NAME	"spi_amd"
#define SPI_VERSION	"1.0"

#define AMD_SPI_CTRL0_REG		0x00
 #define AMD_SPI_EXEC_CMD		(0x1 << 16)
 #define AMD_SPI_OPCODE_MASK		0xFF
 #define AMD_SPI_FIFO_CLEAR		(0x1 << 20)
 #define AMD_SPI_BUSY			(0x1 << 31)
#define AMD_SPI_ALT_CS_REG		0x1D
 #define AMD_SPI_ALT_CS_MASK		0x3
#define AMD_SPI_FIFO_BASE		0x80
#define AMD_SPI_TX_COUNT_REG		0x48
#define AMD_SPI_RX_COUNT_REG		0x4B
#define AMD_SPI_STATUS_REG		0x4C

#define AMD_PCI_LPC_SPI_BASE_ADDR_REG	0xA0
#define AMD_SPI_BASE_ADDR_MASK		~0x3F
#define AMD_SPI_MEM_SIZE		200

#define PCI_DEVICE_ID_AMD_LPC_BRIDGE	0x790E

#define TRANSMIT	1
#define RECEIVE		2

#endif /* SPI_AMD_H  */
