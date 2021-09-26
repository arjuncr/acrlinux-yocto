DESCRIPTION = "OpenStack Client Configuation Library"
HOMEPAGE = "https://pypi.python.org/pypi/os-client-config"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PV = "1.28.0"
SRCREV = "261c05f0057d556a8910457f1e22ca4d81801081"

SRCNAME = "os-client-config"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

S = "${WORKDIR}/git"

DEPENDS += " \
        ${PYTHON_PN}-pbr \
        "
# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        ${PYTHON_PN}-pbr-native \
        "

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-pbr \
        ${PYTHON_PN}-pyyaml \
        ${PYTHON_PN}-appdirs \
        ${PYTHON_PN}-keystoneauth1 \
        ${PYTHON_PN}-requestsexceptions \
        "
