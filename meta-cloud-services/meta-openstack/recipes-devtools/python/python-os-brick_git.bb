DESCRIPTION = "OpenStack Cinder brick library for managing local volume attaches"
HOMEPAGE = "https://github.com/openstack/os-brick"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI = "\
	git://github.com/openstack/os-brick.git;branch=stable/pike \
	"

PV = "1.15.5+git${SRCPV}"
SRCREV = "4090db76673cadb3b8adfceb106069e03414de49"
S = "${WORKDIR}/git"

inherit setuptools3

FILES_${PN} += "${datadir}/etc/*"

DEPENDS += " \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-babel \
        python-eventlet \
        python-oslo.concurrency \
        python-oslo.log \
        python-oslo.serialization \
        python-oslo.i18n \
        python-oslo.privsep \
        python-oslo.service \
        python-oslo.utils \
        python-requests \
        python-retrying \
        python-six \
        python-os-win \
