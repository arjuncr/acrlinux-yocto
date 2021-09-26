DESCRIPTION = "Client library for OpenStack Object Storage API"
HOMEPAGE = "https://github.com/openstack/python-swiftclient"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PR = "r0"
SRCNAME = "swiftclient"

SRC_URI = "git://github.com/openstack/python-swiftclient.git;branch=master"

PV = "3.8.1+git${SRCPV}"
SRCREV = "72b90fed4c62cf75497a7d66714f5fb0aa91f18b"
S = "${WORKDIR}/git"

inherit setuptools3 python3-dir

do_install_append() {
    cp -r tests ${D}/${PYTHON_SITEPACKAGES_DIR}/${SRCNAME}/
}

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
        python-simplejson \
        python-pbr \
        "
