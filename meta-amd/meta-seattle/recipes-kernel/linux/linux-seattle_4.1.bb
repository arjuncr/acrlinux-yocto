DESCRIPTION = "Linux kernel for AMD Seattle ARMv8 Cortex-A57"

require linux-seattle.inc

SRCREV = "4e30e64c44df9e59bd13239951bb8d2b5b276e6f"
SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.1;branch="standard/qemuarm64" \
           file://02-412-Enable-32-bit-EL0-with-64K-and-4K-page-s.patch \
           file://03-arm64-don-t-set-READ_IMPLIES_EXEC-for-EM_AARCH64-ELF.patch \
           file://412-1-styx-linux-tracking.git-2a3f98071e81b66033f6272f6c632023d1dcb1d2.patch \
           file://412-2-styx-linux-tracking.git-390adff766de2d7117ec666674d114dfd5b5a911.patch \
           file://412-3-styx-linux-tracking.git-427c918b150e5f9c25ea36b3d640e511a08abb5f.patch \
           file://412-4-styx-linux-tracking.git-d1072e3d950aa6e348313a31395091003611f794.patch \
           file://412-5-styx-linux-tracking.git-2a80b31ff435cd274a61d685a4861bf0da461c90.patch \
           file://412-6-styx-linux-tracking.git-1c9b07fb461d87b41854fef3a07fff65e0d95113.patch \
           file://412-7-styx-linux-tracking.git-f9a9d954f23b967cd26338afda9a0a96afe62c25.patch \
           file://412-styx-Fix-build-issues-after-porting-PCI-patches-to-4.1.2-.patch \
           file://defconfig \
           "

