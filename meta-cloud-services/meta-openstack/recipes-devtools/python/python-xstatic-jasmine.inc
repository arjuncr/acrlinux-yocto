DESCRIPTION = "Jasmine JavaScript library packaged for setuptools "
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Jasmine"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-Jasmine"

SRC_URI[md5sum] = "9378ce965dfcb4ddb64855cc717d4ec8"
SRC_URI[sha256sum] = "d1e8bf3c0e1cf683cc2c0aad95f197e9e51ea747fb91291445223a2b7dbc4740"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
