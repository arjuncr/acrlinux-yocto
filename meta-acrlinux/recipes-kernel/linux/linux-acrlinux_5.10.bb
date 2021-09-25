require recipes-kernel/linux/linux-yocto_5.4.bb

SRC_URI += "file://x86_64_defconfig"

PREFERRED_PROVIDER_virtual/kernel = "linux-acrlinux"

COMPATIBLE_MACHINE = "acrlinux_qemu_x86-64"

