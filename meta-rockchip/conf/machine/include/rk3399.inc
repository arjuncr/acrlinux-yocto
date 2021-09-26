# Copyright (C) 2020 Garmin Ltd. or its subsidaries
# Released under the MIT license (see COPYING.MIT for the terms)

SOC_FAMILY = "rk3399"

DEFAULTTUNE ?= "cortexa72-cortexa53-crypto"

require conf/machine/include/tune-cortexa72-cortexa53.inc
require conf/machine/include/soc-family.inc
require conf/machine/include/rockchip-defaults.inc

KBUILD_DEFCONFIG ?= "defconfig"
KERNEL_CLASSES = "kernel-fitimage"
KERNEL_IMAGETYPE = "fitImage"

ATF_PLATFORM ?= "rk3399"
ATF_TARGET ?= "bl31"
ATF_SUFFIX ?= "elf"

UBOOT_SUFFIX ?= "itb"
UBOOT_ENTRYPOINT ?= "0x06000000"

SERIAL_CONSOLES = "115200;ttyS2"

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot"
SPL_BINARY ?= "idbloader.img"

