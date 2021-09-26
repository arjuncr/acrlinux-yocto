DESCRIPTION = "XenAPI library for OpenStack projects."
HOMEPAGE = "https://github.com/openstack/os-xenapi"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCNAME = "os-xenapi"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=master"

PV = "0.3.1+git${SRCPV}"
SRCREV = "7dce682e2ab0c14236dbc58a38c925536b3b6f8d"
S = "${WORKDIR}/git"

inherit setuptools3

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-babel \
        python-eventlet \
        python-oslo.concurrency \
        python-oslo.log \
        python-oslo.utils \
        python-oslo.i18n \
        python-six \
        "
