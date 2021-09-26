DESCRIPTION = "Client library for Barbican API"
HOMEPAGE = "https://github.com/stackforge/python-barbicanclient"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e031cff4528978748f9cc064c6e6fa73"

SRC_URI = "\
	git://github.com/openstack/python-barbicanclient.git \
	"

PV = "4.9.0+git${SRCPV}"
SRCREV = "9c0e02d367b86eb5bdebda4e0ff1434d70db5f61"
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
        python-pbr \
        python-requests \
        python-six \
        python-cliff \
        python-keystoneauth1 \
        python-oslo.i18n \
        python-oslo.serialization \
        python-oslo.utils \
        "
