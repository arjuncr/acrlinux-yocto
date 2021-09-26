DESCRIPTION = "Simple library for parsing OpenStack microversion headers."
HOMEPAGE = "https://github.com/openstack/microversion-parse"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = "\
	git://github.com/openstack/microversion-parse.git;branch=master \
	"

PV = "0.1.4+git${SRCPV}"
SRCREV = "1f6eac7c3df048679663919f75c2bcecd3e183a2"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        "

RDEPENDS_${PN} += " \
        "
	