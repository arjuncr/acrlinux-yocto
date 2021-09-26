PR := "${INC_PR}.1"

KMACHINE_amd ?= "common-pc-64"
SRCREV_meta_amd ?= "7f765dcb29003bafc9c0ac770147940be6c420b2"
LINUX_VERSION_amd ?= "5.4.69"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto-${LINUX_VERSION}:"

SRC_URI_append_amd = " file://amd-xgbe-patches.scc \
                   file://amd-emmc-patches.scc \
                   file://amd-spi-patches.scc \
                   file://amd-usb-patches.scc \
                   file://amd-wdt-patches.scc \
                   file://logo.cfg \
                   file://console.cfg \
                   file://sound.cfg \
                   file://hid.cfg \
                   file://usb-serial.cfg \
                   file://wdt-driver.cfg \
                   file://wifi-drivers.cfg \
                   file://disable-intel-graphics.cfg \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'file://enable-bluetooth.cfg', 'file://disable-bluetooth.cfg', d)} \
                   ${@bb.utils.contains('DISTRO', 'poky-amd', 'file://enable-kgdb.cfg', 'file://disable-kgdb.cfg', d)}"

KERNEL_FEATURES_append_amd = " cfg/smp.scc"
