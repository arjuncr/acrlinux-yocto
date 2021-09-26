DESCRIPTION = "An oslo.config enabled dogpile.cache"
HOMEPAGE = "https://github.com/openstack/oslo.cache"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

PV = "1.14.0+git${SRCPV}"
SRCREV = "f5b6ddf7d18a7e06e19712ca7a2509d658a08c4d"

SRCNAME = "oslo.cache"
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
        python-dogpile.cache \
        python-six \
        python-oslo.config \
        python-oslo.i18n \
        python-oslo.log \
        python-oslo.utils \
        "
