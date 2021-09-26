DESCRIPTION = "OpenStack integration library between network and compute providers."
HOMEPAGE = "https://github.com/openstack/os-vif"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI = "\
	git://github.com/openstack/os-vif.git;branch=stable/pike \
	"

PV = "1.7.0+git${SRCPV}"
SRCREV = "5184b7fc3b8d1689823eacb859087c8a943f9a09"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-netaddr \
        python-oslo.concurrency \
        python-oslo.config \
        python-oslo.log \
        python-oslo.i18n \
        python-oslo.privsep \
        python-oslo.versionedobjects \
        python-six \
        python-stevedore \
        "
	