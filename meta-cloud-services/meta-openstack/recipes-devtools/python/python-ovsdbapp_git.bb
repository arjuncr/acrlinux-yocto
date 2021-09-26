DESCRIPTION = "OVSDB application library"
HOMEPAGE = "https://github.com/openstack/ovsdbapp"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCNAME = "ovsdbapp"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

PV = "0.4.1+git${SRCPV}"
SRCREV = "742754bce3c9453f8c7186455a92e4f6d6b18ace"
S = "${WORKDIR}/git"

inherit setuptools3

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
        python-fixtures \
        python-ovs \
        python-pbr \
        "
