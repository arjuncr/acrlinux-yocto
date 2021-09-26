DESCRIPTION = "CLI and python client library for OpenStack Ceilometer"
HOMEPAGE = "https://launchpad.net/ceilometer"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

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
        python-iso8601 \
        python-keystoneauth1 \
        python-oslo.i18n \
        python-oslo.serialization \
        python-oslo.utils \
        python-prettytable \
        python-requests \
        python-six \
        python-stevedore \
        bash \
        "

SRC_URI = "\
	git://github.com/openstack/python-ceilometerclient.git;branch=stable/pike \
	"

PV = "2.9.1"
SRCREV = "4ee321feeef39fa2a297ad8e58e931b8a03d8f14"
S = "${WORKDIR}/git"

inherit setuptools3 rmargparse


PACKAGECONFIG ?= "bash-completion"
PACKAGECONFIG[bash-completion] = ",,bash-completion,bash-completion ${BPN}-bash-completion"

do_install_append() {
	install -d ${D}/${sysconfdir}/bash_completion.d
	install -m 664 ${S}/tools/ceilometer.bash_completion ${D}/${sysconfdir}/bash_completion.d
}

PACKAGES =+ "${BPN}-bash-completion"
FILES_${BPN}-bash-completion = "${sysconfdir}/bash_completion.d/*"
