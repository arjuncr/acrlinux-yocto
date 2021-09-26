DESCRIPTION = "Library for validation of digital signatures."
HOMEPAGE = "https://github.com/openstack/cursive"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI = "\
	git://git.openstack.org/openstack/cursive.git;branch=master \
	"

PV="0.2.0+git${SRCPV}"
SRCREV="ad25a4016c56eeceb85764c7ac4501def2b5445a"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-lxml \
        python-cryptography \
        python-netifaces \
        python-six \
        python-oslo.serialization \
        python-oslo.utils \
        python-oslo.i18n \
        python-oslo.log \
        python-castellan \
        "
