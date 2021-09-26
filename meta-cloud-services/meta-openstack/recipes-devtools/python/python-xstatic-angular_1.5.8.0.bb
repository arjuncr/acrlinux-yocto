DESCRIPTION = "Angular JavaScript library packaged for setuptools3"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Angular"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-Angular"

SRC_URI[md5sum] = "15384e734161d31ff18a644f9632f25b"
SRC_URI[sha256sum] = "b1dcdd7a66d3041625698bba2ac480ffc2447b05f551f10fcae2ac33139eb033"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
