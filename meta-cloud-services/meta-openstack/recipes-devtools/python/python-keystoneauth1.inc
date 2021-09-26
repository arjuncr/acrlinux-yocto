DESCRIPTION = "Authentication Library for OpenStack Identity"
HOMEPAGE = "https://pypi.python.org/pypi/keystoneauth1"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=96f840d59b245a1c817fbcb901afc574"

SRC_URI[md5sum] = "6104205044175fa8aac659582fdd0260"
SRC_URI[sha256sum] = "3ae67c6542ed66a37cddcd26a35457c1ff5cd14b20f5490973273cf9eb555a52"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        ${PYTHON_PN}-pbr-native \
        "

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-pbr \
        ${PYTHON_PN}-iso8601 \
        ${PYTHON_PN}-requests \
        ${PYTHON_PN}-six \
        ${PYTHON_PN}-stevedore \
        "
