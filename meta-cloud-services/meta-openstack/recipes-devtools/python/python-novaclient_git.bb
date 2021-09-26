DESCRIPTION = "Client library for OpenStack Compute API"
HOMEPAGE = "https://github.com/openstack/python-novaclient"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7cdb54622cacc9bc9b2883091e6dd669"

SRC_URI = "\
	git://github.com/openstack/python-novaclient.git;branch=stable/pike \
	file://nova-api-check.sh \
	"

PV = "9.1.1+git${SRCPV}"
SRCREV = "204eff6dc7b234d2807a6caa24a2fc58ebee520b"
S = "${WORKDIR}/git"

inherit setuptools3 monitor rmargparse

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
        python-keystoneauth1 \
        python-iso8601 \
        python-oslo.i18n \
        python-oslo.serialization \
        python-oslo.utils \
        python-prettytable \
        python-simplejson \
        python-six \
        python-babel \
        bash \
        "

PACKAGECONFIG ?= "bash-completion"
PACKAGECONFIG[bash-completion] = ",,bash-completion,bash-completion ${BPN}-bash-completion"

do_install_append() {
	install -d ${D}/${sysconfdir}/bash_completion.d
	install -m 664 ${S}/tools/nova.bash_completion ${D}/${sysconfdir}/bash_completion.d

	if [ -e "${D}/${PYTHON_SITEPACKAGES_DIR}/novaclient/tests/v1_1/test_servers.py" ]; then
		sed -e "s:%PYTHON_SITEPACKAGES_DIR%:${PYTHON_SITEPACKAGES_DIR}:g" \
		    -i ${D}/${PYTHON_SITEPACKAGES_DIR}/novaclient/tests/v1_1/test_servers.py
	fi
}

PACKAGES =+ "${BPN}-bash-completion"
FILES_${BPN}-bash-completion = "${sysconfdir}/bash_completion.d/*"

MONITOR_CHECKS_${PN} += "\
	nova-api-check.sh \
"
