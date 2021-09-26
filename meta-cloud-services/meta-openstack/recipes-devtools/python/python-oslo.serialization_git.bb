DESCRIPTION = "Oslo Serialization API"
HOMEPAGE = "https://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRCNAME = "oslo.serialization"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

PV = "2.20.0+git${SRCPV}"
SRCREV = "e56d91427c11a3813a0154d47e804018e580086e"
S = "${WORKDIR}/git"

inherit setuptools3

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        python-pbr \
        python-six \
        python-oslo.utils \
        python-pytz \
        python-msgpack \
        "
