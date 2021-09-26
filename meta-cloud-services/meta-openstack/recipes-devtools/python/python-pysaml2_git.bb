DESCRIPTION = "Python implementation of SAML Version 2 to be used in a WSGI environment"
HOMEPAGE = "https://github.com/rohe/pysaml2"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e53b335c47f863b1e324a1c9f2e8e3f3"

PV = "3.0.2+git${SRCPV}"
SRCREV = "248c629aa570b16fdc79c5a5eb2b3c4c0ee52916"

SRCNAME = "pysaml2"
SRC_URI = "git://github.com/rohe/${SRCNAME}.git"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pip \
        "

RDEPENDS_${PN} += " \
	python-zopeinterface \
	python-repoze.who \
        "
