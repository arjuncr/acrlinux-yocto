DESCRIPTION = "oslo.versionedobjects library"
HOMEPAGE = "https://wiki.openstack.org/wiki/Oslo"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

PV = "1.26.0+git${SRCPV}"
SRCREV = "78cd10662f20c4ae43e20a2dfa844cfd4e5cae26"

SRCNAME = "oslo.versionedobjects"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

S = "${WORKDIR}/git"

inherit setuptools3

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
        python-six \
        python-oslo.concurrency \
        python-oslo.config \
        python-oslo.context \
        python-oslo.messaging \
        python-oslo.serialization \
        python-oslo.utils \
        python-oslo.log \
        python-oslo.i18n \
        python-webob \
        python-iso8601 \
        python-netaddr \
       "
