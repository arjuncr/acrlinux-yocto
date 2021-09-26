DESCRIPTION = "API supporting parsing command line arguments and .ini style configuration files."
HOMEPAGE = "https://pypi.python.org/pypi/oslo.config/4.11.1"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c46f31914956e4579f9b488e71415ac8"

PV = "4.11.1+git${SRCPV}"
SRCREV = "fb0738974824af6e1bc7d9fdf32a7c1d3ebf65fb"

SRCNAME = "oslo.config"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

S = "${WORKDIR}/git"

inherit setuptools3 rmargparse

DEPENDS += " \
        python-pbr \
        python-pip \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
    python-pbr \
    python-netaddr \
    python-six \
    python-stevedore \
    python-debtcollector \
    python-oslo.i18n \
    python-rfc3986 \
    python-pyyaml \
    "
	
