DESCRIPTION = "Firmware files for Bluetooth"
LICENSE = "TI-TSPA"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f39eac9f4573be5b012e8313831e72a9"

PV = "8.7.1+git${SRCPV}"

CLEANBROKEN = "1"

SRCREV = "0ee619b598d023fffc77679f099bc2a4815510e4"
BRANCH = "master"
SRC_URI = "git://git.ti.com/ti-bt/service-packs.git;branch=${BRANCH}"

S = "${WORKDIR}/git"

do_compile() {
    :
}

do_install() {
    install -d ${D}${base_libdir}/firmware/ti-connectivity
    oe_runmake "DEST_DIR=${D}" "BASE_LIB_DIR=${base_libdir}" install
}

FILES_${PN} += "${base_libdir}/firmware/ti-connectivity/*"
