DESCRIPTION = "Angular JavaScript library packaged for setuptools"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Angular-Bootstrap"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-Angular-Bootstrap"

SRC_URI[md5sum] = "1d7d02580f022c36628f99fbf40e39c9"
SRC_URI[sha256sum] = "61b0a86aad66bf0d333ed24496f51d77cb20ad5bf664140d86e880b15a231166"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

RDEPENDS_${PN} += " \
        "
