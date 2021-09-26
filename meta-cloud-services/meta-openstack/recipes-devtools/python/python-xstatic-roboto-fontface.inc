DESCRIPTION = "A simple package providing the Roboto fontface."
HOMEPAGE = "https://github.com/choffmeister/roboto-fontface-bower"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PYPI_PACKAGE = "XStatic-roboto-fontface"

SRC_URI[md5sum] = "32273ba68fa063623461db0b0767524f"
SRC_URI[sha256sum] = "6d272df58e20eec3a15bca279163f38614c7074e2fecb537a58b29d109e83f62"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

RDEPENDS_${PN} += " \
        "
