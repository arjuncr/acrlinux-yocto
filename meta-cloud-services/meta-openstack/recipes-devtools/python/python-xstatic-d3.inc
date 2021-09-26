DESCRIPTION = "D3 JavaScript library packaged for setuptools"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-D3"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-D3"

SRC_URI[md5sum] = "2729f32f218b122999c26daeaf67a0a8"
SRC_URI[sha256sum] = "176e93eee7192e07fc54334ddb1a6b64fcfc8cde6abb23f65727856bb9dd1829"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
