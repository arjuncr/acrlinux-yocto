DESCRIPTION = "Middleware for OpenStack Identity API"
HOMEPAGE = "https://launchpad.net/keystonemiddleware"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a4d0e932ffae1c0131528d30d419c55"

SRCNAME = "keystonemiddleware"

SRC_URI = "\
	git://git.openstack.org/openstack/keystonemiddleware;branch=stable/pike \
	"

PV = "4.17.0+git${SRCPV}"
SRCREV = "69aed2693475797c8d0dda2725a790eea05a9419"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
        python-keystoneauth1 \
        python-oslo.config \
        python-oslo.context \
        python-oslo.i18n \
        python-oslo.log \
        python-oslo.serialization \
        python-oslo.utils \
        python-pbr \
        python-positional \
        python-pycadf \
        python-keystoneclient \
        python-requests \
        python-six \
        python-webob \
        "
