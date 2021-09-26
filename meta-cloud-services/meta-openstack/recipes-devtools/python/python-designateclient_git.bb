DESCRIPTION = "Python bindings to the Designate API"
HOMEPAGE = "https://github.com/openstack/python-designateclient"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
        python-cliff \
        python-jsonschema \
        python-osc-lib \
        python-oslo.utils \
        python-pbr \
        python-keystoneauth1 \
        python-requests \
        python-six \
        python-stevedore \
        python-debtcollector \
        "

SRCNAME = "designateclient"
SRC_URI = "git://github.com/openstack/${BPN}.git;branch=master"

PV = "3.0.0+git${SRCPV}"
SRCREV = "093d8d7170cbf6ef8c7a7c0ff2a4dcd7ecd6361b"
S = "${WORKDIR}/git"

inherit setuptools3
