DESCRIPTION = "Backport of the concurrent.futures package from Python 3.2"
HOMEPAGE = "https://github.com/agronholm/pythonfutures"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dd6708d05936d3f6c4e20ed14c87b5e3"

PV = "3.3.0+git${SRCPV}"
SRCREV = "ec78e222ece60721fc92a6c650df3116cb15d24e"

SRCNAME = "futures"
SRC_URI = "git://github.com/agronholm/python${SRCNAME}.git"

S = "${WORKDIR}/git"

inherit setuptools3

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
