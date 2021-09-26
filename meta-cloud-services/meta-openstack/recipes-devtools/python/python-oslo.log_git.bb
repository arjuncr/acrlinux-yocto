DESCRIPTION = "Oslo Log Library"
HOMEPAGE = "https://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRCNAME = "oslo.log"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

PV = "3.30.0+git${SRCPV}"
SRCREV = "ad776e84b51223b85b7dfa85a7e77bb2f3848f4b"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pip \
        python-babel \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        bash \
        python-pbr \
        python-six \
        python-oslo.config \
        python-oslo.context \
        python-oslo.i18n \
        python-oslo.utils \
        python-oslo.serialization \
        python-pyinotify \
        python-debtcollector \
        python-dateutil \
        python-monotonic \
        "
