DESCRIPTION = "oslo.i18n library"
HOMEPAGE = "http://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "20bbee510b9714075b7f48f7c3968d7e2bd21a8a"

SRCNAME = "oslo.i18n"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=master"

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

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        python-babel \
        python-pbr \
        python-six \
        "
