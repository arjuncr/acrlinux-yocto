DESCRIPTION = "oslo.service library"
HOMEPAGE = "https://wiki.openstack.org/wiki/Oslo"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

PV = "1.25.0"
SRCREV = "0020bef6a503905aca5cdb70aee54e1c5f2ff472"

SRCNAME = "oslo.service"
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

RDEPENDS_${PN} += " \
        python-pbr \
        python-webob \
        python-eventlet \
        python-greenlet \
        python-monotonic \
        python-oslo.utils \
        python-oslo.concurrency \
        python-oslo.config \
        python-oslo.log \
        python-six \
        python-oslo.i18n \
        python-pastedeploy \
        python-paste \
        python-routes \
        "
	
