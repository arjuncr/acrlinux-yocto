SUMMARY = "Oslo Policy library"
DESCRIPTION = "The Oslo Policy library provides support for RBAC policy enforcement across all OpenStack services."
HOMEPAGE = "https://github.com/openstack/oslo.policy"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

PV = "1.25.1+git${SRCPV}"
SRCREV = "cb9ab34cd40a7cd9eff0e40ccc2df4ee88edae4e"

SRCNAME = "oslo.policy"
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
        python-oslo.config \
        python-oslo.i18n \
        python-oslo.serialization \
        python-oslo.utils \
        python-six \
        python-pyyaml \
        python-requests \
"
