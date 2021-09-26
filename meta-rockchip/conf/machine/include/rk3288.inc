# Copyright (C) 2015 Romain Perier
# Released under the MIT license (see COPYING.MIT for the terms)

SOC_FAMILY = "rk3288"

require conf/machine/include/tune-cortexa17.inc
require conf/machine/include/soc-family.inc
require conf/machine/include/rockchip-defaults.inc

KBUILD_DEFCONFIG ?= "multi_v7_defconfig"
KERNEL_IMAGETYPE = "zImage"

SERIAL_CONSOLES = "115200;ttyS2"

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot"
SPL_BINARY ?= "idbloader.img"

