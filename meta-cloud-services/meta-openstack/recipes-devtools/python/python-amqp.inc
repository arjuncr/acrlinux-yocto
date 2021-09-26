DESCRIPTION = "Low-level AMQP client for Python"
HOMEPAGE = "https://pypi.python.org/pypi/amqp/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d6ba772ac59c08a25a12ce15bd5f27b"

SRC_URI[md5sum] = "9e3d5f1f69d2106c2b3f77d275a79924"
SRC_URI[sha256sum] = "6816eed27521293ee03aa9ace300a07215b11fee4e845588a9b863a7ba30addb"

inherit pypi

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-vine \
        "
