DESCRIPTION = "oslo.reports library"
HOMEPAGE = "https://wiki.openstack.org/wiki/Oslo"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

PV = "1.22.0+git${SRCPV}"
SRCREV = "a837f40bb0c31958d3ce99e2f9a6eb2fe651f4e6"

SRCNAME = "oslo.reports"
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
        python-pbr \
        python-six \
        python-jinja2 \
        python-psutil \
        python-oslo.i18n \
        python-oslo.utils \
        python-oslo.serialization \
        "
