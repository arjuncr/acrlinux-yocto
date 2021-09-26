DESCRIPTION = "Oslo utils"
HOMEPAGE = "https://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRCNAME = "oslo.utils"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

PV = "3.28.0+git${SRCPV}"
SRCREV = "8b3965b9bbe1e31a4939f2f69c5239d6d5c7f72c"
S = "${WORKDIR}/git"

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        ${PYTHON_PN}-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        ${PYTHON_PN}-pbr-native \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        ${PYTHON_PN}-pbr \
        ${PYTHON_PN}-six \
        ${PYTHON_PN}-iso8601 \
        ${PYTHON_PN}-oslo.i18n \
        ${PYTHON_PN}-pytz \
        ${PYTHON_PN}-netaddr \
        ${PYTHON_PN}-netifaces \
        ${PYTHON_PN}-debtcollector \
        ${PYTHON_PN}-pyparsing \
        "
