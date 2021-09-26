DESCRIPTION = "Unified SDK for OpenStack"
HOMEPAGE = "https://github.com/openstack/python-openstacksdk"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI = " \
        git://github.com/openstack/python-openstacksdk.git;branch=master \
        "

PV = "0.9.19+git${SRCPV}"
SRCREV = "bd60aa4a21676b8901691298eb0786fc231f7bff"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "\
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-jsonpatch \
        python-six \
        python-stevedore \
        python-os-client-config \
        python-keystoneauth1 \
        python-deprecation \
        "
