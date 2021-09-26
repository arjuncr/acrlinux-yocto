SUMMARY = "TI Emulation CTools AET library"
DESCRIPTION = "The AETLIB library provides programmatic access to the Advanced Event Triggering capability on C6x processor cores"

LICENSE = "BSD"

PV = "4.19.0.0"

LIC_FILES_CHKSUM = "file://../ctoolslib_manifest.html;md5=b7dd369a2e07ef9a71795ee5a9bd01bd"

SRC_URI = "git://git.ti.com/sdo-emu/ctoolslib.git;protocol=git;branch=opencl_aetlib_build"
SRCREV = "de7954abab0f0caa8a2c7b53095c30226d901a31"

DEPENDS = "ti-cgt6x-native"
PR = "r0"

S = "${WORKDIR}/git/aet"

DEVICE=""
DEVICE_dra7xx = "DRA7xx"
DEVICE_keystone = "C66AK2Hxx"

EXTRA_OEMAKE = "C6X_C_DIR=${STAGING_DIR_NATIVE}/usr/share/ti/cgt-c6x/include"

do_install() {
    install -d ${D}${datadir}/ti/ctoolslib/aet
    install -d ${D}${datadir}/ti/ctoolslib/aet/include
    install -d ${D}${datadir}/ti/ctoolslib/aet/lib
    cp -rP --preserve=mode,links,timestamps --no-preserve=ownership ${S}/include/* ${D}${datadir}/ti/ctoolslib/aet/include/
    install -m 0644 ${S}/build/c66/libaet.ae66 ${D}${datadir}/ti/ctoolslib/aet/lib/
}

COMPATIBLE_MACHINE = "dra7xx|keystone"
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN}-dev += "\
    ${datadir}/ti/ctoolslib/aet \
"

ALLOW_EMPTY_${PN} = "1"
