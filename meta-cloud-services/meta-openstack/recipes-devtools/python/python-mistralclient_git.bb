DESCRIPTION = "Python client for Mistral REST API"
HOMEPAGE = "https://github.com/openstack/python-mistralclient"
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

RDEPENDS_${PN} +=" \
        python-cliff \
        python-pbr \
        python-keystoneclient \
        python-pyyaml \
        python-requests \
	"

SRCNAME = "mistralclient"
SRC_URI = "git://github.com/openstack/${BPN}.git;branch=master"

PV = "1.1.0+git${SRCPV}"
SRCREV = "48e2780ee0148efc186c8972ca22e572fa2433c5"
S = "${WORKDIR}/git"

inherit setuptools3
