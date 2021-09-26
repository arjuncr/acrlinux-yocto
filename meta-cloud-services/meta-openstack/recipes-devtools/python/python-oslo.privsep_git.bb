DESCRIPTION = "OpenStack library for privilege separation"
HOMEPAGE = "https://github.com/openstack/oslo.privsep"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCNAME = "oslo.privsep"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

PV = "1.22.1+git${SRCPV}"
SRCREV = "d27bb5371c90e0f8b1bdf1bc24f16e1532b3e595"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pbr \
        "

# RDEPENDS_default:
RDEPENDS_${PN} += " \
        python-oslo.log \
        python-oslo.i18n \
        python-oslo.config \
        python-oslo.utils \
        python-enum34 \
        python-cffi \
        python-eventlet \
        python-greenlet \
        python-msgpack \
        "
