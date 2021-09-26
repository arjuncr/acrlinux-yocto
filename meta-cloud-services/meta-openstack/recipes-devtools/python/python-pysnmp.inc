DESCRIPTION = "A pure-Python SNMPv1/v2c/v3 library"
HOMEPAGE = "https://pypi.python.org/pypi/pysnmp"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ae098273b2cf8b4af164ac20e32bddf7"

SRC_URI[md5sum] = "1f75d3e392a050e84348904fc1be3212"
SRC_URI[sha256sum] = "c46e65d99a604f690b3d5800e2f6e26e1ed9a3c7f7e17e7b4b4d897150f7077f"

inherit pypi

RDEPENDS_${PN} += "${PYTHON_PN}-pycrypto \
                   ${PYTHON_PN}-pyasn1 \
"
