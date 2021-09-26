# meta-seattle

This is the location for AMD Seattle BSP.

## Overview of AMD 64-bit ARM-based processor

The AMD Opteron A1100-Series features AMDs first 64-bit ARM-based processor, codenamed "Seattle".

## Yocto Project Compatible

This BSP is compatible with the Yocto Project as per the requirements
listed here:

  https://www.yoctoproject.org/webform/yocto-project-compatible-registration

## Dependencies

This layer depends on:

[bitbake](https://github.com/openembedded/bitbake) layer,
[oe-core](https://github.com/openembedded/openembedded-core) layer,
meta-amd/meta-amd-bsp layer

## Building the meta-seattle BSP layer

The following instructions require a Poky installation (or equivalent).

Initialize a build using the 'oe-init-build-env' script in Poky.
    $ source oe-init-build-env <build_dir>

Once initialized configure bblayers.conf by adding the 'meta-seattle'
and 'common' layers. e.g.:

    BBLAYERS ?= " \
        <path to layer>/oe-core/meta \
        <path to layer>/meta-amd/meta-seattle \
        <path to layer>/meta-amd/common \
        "

To build a specific target BSP configure the associated machine in local.conf.
There are two machines defined in order to offer support for Little Endian ("seattle",
default machine) and Big Endian ("seattle-be").

    MACHINE ?= "seattle"

Build the target file system image using bitbake:

    $ bitbake core-image-minimal

Once complete the images for the target machine will be available in the output
directory 'tmp/deploy/images'.


## Booting the images

Booting the images using UEFI firmware

At power-on, the UEFI firmware starts a UEFI bootloader which looks up the EFI
System Partition (ESP) for a script named startup.nsh.
If the script specifies an executable file in the ESP, that file is executed
as a UEFI application.

If no UEFI application can be started, or if the boot process is interrupted
by ESC, the EFI Shell is started with prompt "Shell>". Here you can execute
shell commands or UEFI applications interactively.

A UEFI application can be e.g. a Linux kernel built with an EFI stub. By executing
the EFI-stubbed kernel as an application with arguments, you can control
how to boot Linux, and which rootfs to use.

Boot existing Linux images or install HDD on a different machine,
mount EFI partition and copy kernel image under this partition.

Boot Linux images from UEFI shell with rootfs in RAM
Before that, copy rootfs under EFI partition(FAT).

Shell> FS0:\Image initrd=\core-image-minimal.ext2.gz root=/dev/ram0 rw
 console=ttyAMA0,115200n8 ramdisk_size=524280

Boot Linux images from UEFI shell with rootfs on a HDD ext2/3/4 partition.
Before that, install rootfs under a /dev/sda<X> ext2/3/4 formated partition.

Shell> FS0:\Image root=/dev/sda<X> rw console=ttyAMA0,115200n8

References:
1) https://www.kernel.org/doc/Documentation/efi-stub.txt
2) http://www.uefi.org/specifications

## How to Run 32-bit Applications on aarch64

Since multilib is not yet available for aarch64, this BSP offers some alternatives
in order to run 32-bit legacy applications on an aarch64.

Each method that helps to setup the environment to run 32-bit applications requires
a few extra tools. Two of those metods are described in this section, one requiring
chroot and another one qemu installation.

meta-seattle BSP includes two scripts that help you to setup environment for 32bit
applications that can be found under following path meta-seattle/recipes-support/32b-env/

### Using chroot tool to run 32-bit applications on aarch64

This solution requires the chroot tool on the target, a 32b-built rootfs, and
a build for the seattle machine.

root@seattle:~# scp <user>@<host_ip>:/homes/rootfs32b.tar.gz ./

root@seattle:~# tar -zxf rootfs32.tar.gz

root@seattle:~# set_32b_env_chroot.sh -r ./rootfs32

- 32b environment started
@seattle:~# ./hello32d

Starting from this point, any 32-bit application can be executed, but not 64-bit
applications which will fail due to wrong path to 32-bit libraries. To run a 64-bit
application, chroot mode must be quit by using the "exit" command.

### Using qemu to run 32-bit applications on aarch64

This example requires an existing 32b-built rootfs and a build for the seattle machine.
It is also expected that qemu-arm is already installed!!!

root@seattle:~# scp <user>@<host_ip>:/homes/rootfs32b.tar.gz ./

root@seattle:~# source /usr/bin/set_32b_env_qemu.sh -r ./rootfs32

Starting from this point, any 32-bit or 64-bit application can be executed.

## Limitations

In order to enable 32-bit support in the aarch64 kernel, COMPAT mode is enabled
(CONFIG_COMPAT=y), page size set to 4K, and VirtualAddress set to 48-bit.

It might be possible to use 64K page sizes and 42-bit VirtualAddress if it is possible
to rebuild the 32-bit application using binutils version 2.25.

If 32-bit support is not required, COMPAT mode can be disabled (CONFIG_COMPAT=n) and
page sizes set to 64K and VirtualAddress to 42-bit. Any change of the kernel
confguration requires of course a kernel rebuild.
