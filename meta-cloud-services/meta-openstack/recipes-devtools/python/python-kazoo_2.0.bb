DESCRIPTION = "Higher Level Zookeeper Client"
HOMEPAGE = "https://kazoo.readthedocs.org"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

# Archived version so we have to overwrite what the pypi class will derive
PYPI_SRC_URI = "https://pypi.python.org/packages/source/k/${SRCNAME}/kazoo-${PV}.zip"

SRC_URI[md5sum] = "4b172de456c102b0e33f661e7e2b3583"
SRC_URI[sha256sum] = "f0c42cc7752a331ba59269827bd19cb271210399a9dcab32b6a91465b4431a18"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
