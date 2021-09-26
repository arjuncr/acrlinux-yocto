# Features

This section lists the features supported on the AMD machines. In each
machine column, a 'Y' represents that the feature in this row is supported
on this machine.

| Category                        | Feature                                 | E3000 | ROME |
|:--------------------------------|:----------------------------------------|:-----:|:----:|
| Images                          |                                         |       |      |
|                                 | core-image-sato                         |       |      |
|                                 | core-image-base                         | Y     | Y    |
| Images Types                    |                                         |       |      |
|                                 | WIC                                     | Y     | Y    |
|                                 | ISO                                     | Y     | Y    |
| Board Devices                   |                                         |       |      |
|                                 | USB 2.0 Host                            | Y     | Y    |
|                                 | USB 3.0 Host (MSC)                      | Y     | Y    |
|                                 | USB 3.1 Host                            |       |      |
|                                 | NVMe                                    |       | Y    |
|                                 | M.2 SATA                                |       |      |
|                                 | I2C                                     | Y     | Y    |
|                                 | UART                                    | Y     | Y    |
|                                 | eMMC                                    | Y     |      |
|                                 | SMP                                     | Y     | Y    |
|                                 | SPI                                     | Y     | Y    |
| I/O                             |                                         |       |      |
|                                 | USB Host                                | Y     | Y    |
|                                 | USB Mass Storage                        | Y     | Y    |
|                                 | Audio                                   |       |      |
|                                 | UART                                    | Y     | Y    |
|                                 | Bluetooth                               |       |      |
|                                 | USB Wi-Fi                               | Y     |      |
|                                 | HDD/SATA                                | Y     | Y    |
|                                 | SD/MMC                                  | Y     |      |
| Networking                      |                                         |       |      |
|                                 | Ethernet                                | Y     |      |
|                                 | SGMII                                   | Y     |      |
|                                 | RGMII                                   | Y     |      |
| Network Protocols               |                                         |       |      |
|                                 | IPv4                                    | Y     | Y    |
|                                 | IPv6                                    | Y     | Y    |
| General Purpose Kernel Features |                                         |       |      |
|                                 | Control Groups                          | Y     | Y    |
|                                 | CPU Hot Plugging                        | Y     | Y    |
|                                 | High Resolution Timers (HRT)            | Y     | Y    |
|                                 | POSIX Message Queues & Semaphores       | Y     | Y    |
|                                 | Prioritized OOM Killer                  | Y     | Y    |
|                                 | Symmetric Multi-Processing (SMP)        | Y     | Y    |
|                                 | Native POSIX Thread Library             | Y     | Y    |
| Kernel Preemption               |                                         |       |      |
|                                 | Preemptive Kernel (Low-Latency Desktop) | Y     | Y    |
|                                 | Fully Preemptible Kernel (RT)           | Y     | Y    |
| Filesystems                     |                                         |       |      |
|                                 | Devtmpfs                                | Y     | Y    |
|                                 | EXT2                                    | Y     | Y    |
|                                 | EXT3                                    | Y     | Y    |
|                                 | EXT4                                    | Y     | Y    |
|                                 | FAT                                     | Y     | Y    |
|                                 | NFSv3                                   | Y     | Y    |
|                                 | ProcFS                                  | Y     | Y    |
|                                 | RamFS                                   | Y     | Y    |
|                                 | SysFS                                   | Y     | Y    |
|                                 | tmpfs                                   | Y     | Y    |
| HID (Input Devices)             |                                         |       |      |
|                                 | Input Core (CONFIG_INPUT)               | Y     | Y    |
|                                 | Mouse Interface                         |       |      |
|                                 | Keyboards                               | Y     | Y    |
|                                 | Touchscreen                             |       |      |
| Display Device Support          |                                         |       |      |
|                                 | DP                                      |       |      |
|                                 | HDMI                                    |       |      |
| Kernel Debug/Trace              |                                         |       |      |
|                                 | KGDB                                    | Y     | Y    |
|                                 | LTTng - Kernel Tracing                  | Y     | Y    |
|                                 | LTTng - Userspace Tracing               | Y     | Y    |
| USB Protocols                   |                                         |       |      |
|                                 | USB 2.0                                 | Y     | Y    |
|                                 | USB 3.0                                 | Y     | Y    |
|                                 | USB 3.1                                 |       |      |
| Sound Support                   |                                         |       |      |
|                                 | ALSA                                    |       |      |
| Multimedia Support              |                                         |       |      |
|                                 | Accelerated gstreamer                   |       |      |
|                                 | Unaccelerated gstreamer                 |       |      |
|                                 | OMX                                     |       |      |
|                                 | VDPAU                                   |       |      |
|                                 | VAAPI                                   |       |      |
|                                 | mesa (accelerated graphics)             |       |      |
|                                 | unaccelerated graphics                  |       |      |
|                                 | Vulkan                                  |       |      |
|                                 | multi-display                           |       |      |
|                                 | CodeXL                                  |       |      |
|                                 | RGP                                     |       |      |
|                                 | MP4                                     |       |      |
|                                 | MPEG2                                   |       |      |
|                                 | MPEG4                                   |       |      |
|                                 | VC-1                                    |       |      |
|                                 | H.264                                   |       |      |
|                                 | H.265                                   |       |      |
|                                 | ROCm-OpenCL                             |       |      |
| Network Security                |                                         |       |      |
|                                 | IPSEC (strongswan)                      | Y     |      |
|                                 | DPDK                                    | Y     |      |
| Kernel Virtualization           |                                         |       |      |
|                                 | KVM                                     | Y     | Y    |
