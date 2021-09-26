DESCRIPTION = "oslo.db library"
HOMEPAGE = "http://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PV = "4.25.0+git${SRCPV}"
SRCREV = "71607d59ec5c02d7beb5109c500aa9b6a0d9ee2c"

SRCNAME = "oslo.db"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

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
       python-six \
       python-alembic \
       python-oslo.config \
       python-oslo.i18n \
       python-oslo.utils \
       python-sqlalchemy \
       python-sqlalchemy-migrate \
       python-stevedore \
       python-pbr \
       python-debtcollector \
       "
