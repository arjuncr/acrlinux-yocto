DESCRIPTION = "OpenStack library containing standardized trait strings."
HOMEPAGE = "https://github.com/openstack/os-traits"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI = "\
	git://github.com/openstack/os-traits.git;branch=stable/pike \
	"

PV = "0.3.3+git${SRCPV}"
SRCREV = "3e8b4a77aaf2d8b64f89ba3b479113d0b44bbe2f"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-six \
	"
