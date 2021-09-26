DESCRIPTION = "Retrying"
HOMEPAGE = "https://github.com/rholder/retrying"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI[md5sum] = "2a126aeef8b21324ecdeac15ff46ef17"
SRC_URI[sha256sum] = "08c039560a6da2fe4f2c426d0766e284d3b736e355f8dd24b37367b0bb41973b"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
