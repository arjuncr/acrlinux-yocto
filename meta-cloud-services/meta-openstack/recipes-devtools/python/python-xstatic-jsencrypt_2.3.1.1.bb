DESCRIPTION = "JSEncrypt JavaScript library packaged for setuptools3"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-JSEncrypt"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-JSEncrypt"

SRC_URI[md5sum] = "4b03331d4b2f6c12e2e4b8ee6056bda0"
SRC_URI[sha256sum] = "a277912a4f70d1d2f58c8d94b992d244e69fcf851a2cbed5d83cb4fc422a72f2"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
