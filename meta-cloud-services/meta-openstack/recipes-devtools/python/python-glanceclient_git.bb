DESCRIPTION = "Client library for Glance built on the OpenStack Images API"
HOMEPAGE = "https://github.com/openstack/python-glanceclient"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"
SRC_URI = "git://github.com/openstack/python-glanceclient.git \
           file://glance-api-check.sh \
        "

PV = "2.17.0+git${SRCPV}"
SRCREV = "40c19aa44361e13ac997d325d357d3e4748fa063"
S = "${WORKDIR}/git"

DEPENDS += " \
        gmp \
        python-pip \
        python-pbr \
        "

inherit setuptools3 monitor rmargparse

FILES_${PN} += "${datadir}/${SRCNAME}"

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
    python-pbr-native \
    "

RDEPENDS_${PN} += " \
   gmp \
   bash \
   python-pbr \
   python-babel \
   python-prettytable \
   python-keystoneauth1 \
   python-requests \
   python-warlock \
   python-six \
   python-oslo.utils \
   python-oslo.i18n \
   python-wrapt \
   python-pyopenssl \
   "

MONITOR_CHECKS_${PN} += "\
	glance-api-check.sh \
"
