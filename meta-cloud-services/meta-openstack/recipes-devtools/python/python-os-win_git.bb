DESCRIPTION = "Windows / Hyper-V library for OpenStack projects."
HOMEPAGE = "https://github.com/openstack/os-win"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI = "\
	git://github.com/openstack/os-win.git;branch=stable/pike \
	"

PV = "2.2.0+git${SRCPV}"
SRCREV = "b507ec4e7cb5eead7a008e4d002bc31b85359908"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-babel \
        python-eventlet \
        python-oslo.concurrency \
        python-oslo.config \
        python-oslo.log \
        python-oslo.utils \
        python-oslo.i18n \
        "
