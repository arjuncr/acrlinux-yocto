DESCRIPTION = "OpenStack Command-line Client"
HOMEPAGE = "https://github.com/openstack/python-openstackclient"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI = " \
        git://github.com/openstack/python-openstackclient.git;branch=stable/pike \
        "

PV = "3.12.0+git${SRCPV}"
SRCREV = "ff4abb7d19829efa0209cb67faf01011d9c841c5"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "\
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-six \
        python-babel \
        python-cliff \
        python-keystoneauth1 \
        python-openstacksdk \
        python-osc-lib \
        python-oslo.i18n \
        python-oslo.utils \
        python-glanceclient \
        python-keystoneclient \
        python-novaclient \
        python-cinderclient \
        "
