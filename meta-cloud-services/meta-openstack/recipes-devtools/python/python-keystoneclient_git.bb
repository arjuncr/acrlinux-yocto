DESCRIPTION = "Client library for OpenStack Identity API"
HOMEPAGE = "https://github.com/openstack/python-keystoneclient"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a4d0e932ffae1c0131528d30d419c55"

SRCNAME = "keystoneclient"

SRC_URI = "\
	git://github.com/openstack/python-keystoneclient.git;branch=stable/pike \
	file://keystone-api-check.sh \
	"

PV = "3.13.0+git${SRCPV}"
SRCREV = "7ff05baa1fa56f152173651f16fc6fd181291292"
S = "${WORKDIR}/git"

inherit setuptools3 monitor

FILES_${PN}-doc += "${datadir}/keystoneclient" 

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
        bash \
        python-pbr \
        python-debtcollector \
        python-keystoneauth1 \
        python-oslo.config \
        python-oslo.i18n \
        python-oslo.serialization \
        python-oslo.utils \
        python-positional \
        python-requests \
        python-six \
        python-stevedore \
        "

do_install_append() {
	cp -r ${S}/examples ${D}${PYTHON_SITEPACKAGES_DIR}/${SRCNAME}
}

PACKAGES =+ " ${SRCNAME}-tests"

FILES_${SRCNAME}-tests = "${PYTHON_SITEPACKAGES_DIR}/${SRCNAME}/examples \
        "
RDEPENDS_${SRCNAME}-tests += " \
	python-httpretty \
        bash \
	"

MONITOR_CHECKS_${PN} += "\
	keystone-api-check.sh \
"
