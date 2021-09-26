DESCRIPTION = "A python package that works to provide a nice set of testing utilities for the kazoo library."
HOMEPAGE = "https://github.com/yahoo/Zake"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=33573af7505a94ff3d122a7920b2c735"

SRC_URI[md5sum] = "bd8db293a78c22171ecfdd54f4d65c63"
SRC_URI[sha256sum] = "2e5bcb215e366e682fd05dd1df4f2e6affceefa5d3781c2987a21fd597659a21"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        python-kazoo \
        "
