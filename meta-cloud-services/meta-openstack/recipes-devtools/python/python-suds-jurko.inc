DESCRIPTION = "Lightweight SOAP client (Jurko's fork)"
HOMEPAGE = "https://bitbucket.org/jurko/suds"
SECTION = "devel/python"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=847e96bce86d8774f491a92924343a29"

PYPI_PACKAGE_EXT = "tar.bz2"

SRC_URI[md5sum] = "1309e9bc2454aa3434041f0a24ae4e11"
SRC_URI[sha256sum] = "29edb72fd21e3044093d86f33c66cf847c5aaab26d64cb90e69e528ef014e57f"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

RDEPENDS_${PN} += " \
        "

