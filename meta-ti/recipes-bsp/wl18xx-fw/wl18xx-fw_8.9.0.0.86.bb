DESCRIPTION = "Firmware files for use with TI wl18xx"
LICENSE = "TI-TSPA"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4977a0fe767ee17765ae63c435a32a9e"

SRC_URI = " \
	git://git.ti.com/wilink8-wlan/wl18xx_fw.git;protocol=git;branch=${BRANCH} \
	file://0001-Add-Makefile-to-install-firmware-files.patch \
"

SRCREV = "5ec05007f2662f460f881c5868311fd3ab7e6e71"
BRANCH = "master"

S = "${WORKDIR}/git"

CLEANBROKEN = "1"

do_compile() {
    :
}

do_install() {
    oe_runmake 'DEST_DIR=${D}' install
}

FILES_${PN} = "/lib/firmware/ti-connectivity/*"
