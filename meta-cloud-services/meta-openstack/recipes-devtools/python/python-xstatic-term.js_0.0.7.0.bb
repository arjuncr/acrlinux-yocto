DESCRIPTION = "Angular JavaScript library packaged for setuptools3"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-term.js/0.0.4.2"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-term.js"

SRC_URI[md5sum] = "7434ecf6f5680c293cf3806245dc946b"
SRC_URI[sha256sum] = "b5f3ab69cb638391f04254913a11b2aab08e2d51c5b81bb6a564c5a6d442bd31"

inherit setuptools3 pypi

DEPENDS += " \
        python3-pip \
        "

RDEPENDS_${PN} += " \
        "
