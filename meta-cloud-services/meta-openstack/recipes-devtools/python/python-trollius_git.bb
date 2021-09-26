DESCRIPTION = "Port of the Tulip project (asyncio module, PEP 3156) on Python 2"
HOMEPAGE = "https://github.com/haypo/trollius"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f7bb094c7232b058c7e9f2e431f389c"

PV = "2.0+git${SRCPV}"
SRCREV = "5e9854d7b7bed6eb6e182808379342355e2bfca4"

SRCNAME = "trollius"
SRC_URI = "git://github.com/haypo/${SRCNAME}.git;branch=trollius"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pip \
        "

RDEPENDS_${PN} += " \
        "
