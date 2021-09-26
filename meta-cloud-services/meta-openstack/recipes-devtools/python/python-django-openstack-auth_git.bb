DESCRIPTION = "A Django authentication backend for use with the OpenStack Keystone backend."
HOMEPAGE = "http://django_openstack_auth.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCNAME = "django_openstack_auth"

PV = "3.5.0+git${SRCPV}"
SRCREV = "9e108ed426a5a1e5c9dd394b197c27d046754d0c"

SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike \
"

S = "${WORKDIR}/git"

inherit setuptools3 

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-django \
        python-oslo.config \
        python-oslo.policy \
        python-keystoneclient \
        python-keystoneauth1 \
        python-six \
        "
