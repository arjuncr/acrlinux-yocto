DESCRIPTION = "oslo.concurrency library"
HOMEPAGE = "http://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PV = "3.21.1+git${SRCPV}"
SRCREV = "8adf9b1f0d69dca7372b967ef4f894487f1a9d64"

SRCNAME = "oslo.concurrency"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

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
        python-fasteners \
        python-oslo.config \
        python-oslo.i18n \
        python-oslo.utils \
        python-six \
        python-enum34 \
        "
