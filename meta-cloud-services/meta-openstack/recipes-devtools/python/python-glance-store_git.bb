DESCRIPTION = "Glance stores library"
HOMEPAGE = "https://github.com/openstack/glance_store"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI = "\
	git://git.openstack.org/openstack/glance_store.git;branch=stable/pike \
	"

PV="0.22.0+git${SRCPV}"
SRCREV="49c915f498fc8d91c98fcf4e07ceecdcf167fc5a"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        bash \
        python-oslo.config \
        python-oslo.i18n \
        python-oslo.serialization \
        python-oslo.utils \
        python-oslo.concurrency \
        python-stevedore \
        python-enum34 \
        python-eventlet \
        python-six \
        python-jsonschema \
        python-keystoneauth1 \
        python-keystoneclient \
        python-requests \
        "
