DESCRIPTION = "oslo.i18n library"
HOMEPAGE = "http://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PV = "3.17.0+git${SRCPV}"
SRCREV = "f2729cd36f8694a6ec53a0e700599ddf4427440d"

SRCNAME = "oslo.i18n"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

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
        ${PYTHON_PN}-babel \
        ${PYTHON_PN}-pbr \
        ${PYTHON_PN}-six \
        "
