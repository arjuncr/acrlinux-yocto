DESCRIPTION = "Python client for containers service"
HOMEPAGE = "https://github.com/openstack/python-magnumclient"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} +=" \
        python-pbr \
        python-babel \
        python-oslo.config \
        python-oslo.i18n \
        python-oslo.serialization \
        python-oslo.utils \
        python-iso8601 \
        python-requests \
        python-keystoneclient \
        python-pyyaml \
        python-stevedore \
        python-six \
	"

SRCNAME = "magnumclient"
SRC_URI = "git://github.com/openstack/${BPN}.git;branch=master"

PV = "1.0.0.0b1+git${SRCPV}"
SRCREV = "fb1ff6777eb96a5b7ba38156bf8354cda9b88ad4"
S = "${WORKDIR}/git"

inherit setuptools3 rmargparse
