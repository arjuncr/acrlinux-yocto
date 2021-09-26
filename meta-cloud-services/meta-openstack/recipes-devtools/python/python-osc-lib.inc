DESCRIPTION = "OpenStackClient Library"
HOMEPAGE = "http://opensource.perlig.de/rcssmin/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "240d3debc1b6eaadf5e8838f5f2d06fb"
SRC_URI[sha256sum] = "3467a1edf62946f1b67724fa7f9c699b5e31d80b111ed9e4c7aec21633a3e30d"

inherit pypi

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        ${PYTHON_PN}-pbr-native \
        "

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-pbr \
        ${PYTHON_PN}-six \
        ${PYTHON_PN}-babel \
        ${PYTHON_PN}-cliff \
        ${PYTHON_PN}-keystoneauth1 \
        ${PYTHON_PN}-os-client-config \
        ${PYTHON_PN}-oslo.i18n \
        ${PYTHON_PN}-oslo.utils \
        ${PYTHON_PN}-simplejson \
        ${PYTHON_PN}-stevedore \
        "

CLEANBROKEN = "1"
