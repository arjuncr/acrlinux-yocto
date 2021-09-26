DESCRIPTION = "Linux kernel with BigEndian support for Seattle SoC, AMD 64bit ARMv8 Cortex-A57"

require linux-seattle.inc

SRCREV = "e152349de59b43b2a75f2c332b44171df461d5a0"
SRC_URI = "git://git.yoctoproject.org/linux-yocto-3.19;branch="standard/qemuarm64" \
           file://01-arm64-boot-BE-kernels-from-UEFI.patch \
           file://02-319-Enable-32-bit-EL0-with-64K-and-4K-page-s.patch \
           file://03-arm64-don-t-set-READ_IMPLIES_EXEC-for-EM_AARCH64-ELF.patch \
           file://319-Update-xgbe-drivers-for-B0-board.patch \
           file://defconfig \
           "
SRC_URI_append_seattle-be = "file://bigendian.cfg \
                            "

INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_kernel-dev += "debug-files"
INSANE_SKIP_kernel-dev += "arch"
