DESCRIPTION = "A python package that provides useful locks."
HOMEPAGE = "https://github.com/harlowja/fasteners"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4476c4be31402271e101d9a4a3430d52"

SRCNAME = "fasteners"

PV = "0.15+git${SRCPV}"
SRCREV = "75148cb679c351966cd4ad5e9d177048a0ea8380"

SRC_URI = "git://github.com/harlowja/${SRCNAME}.git"

S = "${WORKDIR}/git"

DEPENDS += " \
        ${PYTHON_PN}-pip \
        ${PYTHON_PN}-pbr \
        "

# RDEPENDS_default:
RDEPENDS_${PN} += " \
        ${PYTHON_PN}-pbr \
        "
