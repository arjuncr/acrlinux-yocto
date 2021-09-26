DESCRIPTION = "Generic Key Manager interface for OpenStack"
HOMEPAGE = "https://github.com/openstack/castellan"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCNAME = "castellan"

SRC_URI = "\
	git://git.openstack.org/openstack/${SRCNAME}.git;branch=stable/pike \
	"

PV="0.12.1+git${SRCPV}"
SRCREV="124e9296fe79412b510c73b35936a6be91267d18"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-babel \
        python-cryptography \
        python-barbicanclient \
        python-oslo.config \
        python-oslo.context \
        python-oslo.i18n \
        python-oslo.log \
        python-oslo.utils \
        python-keystoneauth1 \
        "
