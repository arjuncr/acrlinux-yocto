DESCRIPTION = "Oslo Middleware library"
HOMEPAGE = "http://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PV = "3.30.1+git${SRCPV}"
SRCREV = "d9ad4bae1e0d6c43a009d393ac94f7ff50116171"

SRCNAME = "oslo.middleware"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

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
        python-pbr \
        python-jinja2 \
        python-oslo.config \
        python-oslo.context \
        python-oslo.i18n \
        python-oslo.utils \
        python-six \
        python-stevedore \
        python-webob \
        python-debtcollector \
        python-statsd \
        "
