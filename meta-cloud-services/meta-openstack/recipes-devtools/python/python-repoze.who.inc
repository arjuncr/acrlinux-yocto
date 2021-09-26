DESCRIPTION = "An identification and authentication framework for WSGI"
HOMEPAGE = "http://repoze.org/"
SECTION = "devel/python"
LICENSE = "BSD-Modification"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2c33cdbc6bc9ae6e5d64152fdb754292"

SRC_URI[md5sum] = "c677ac4d4a32b6f6880a8f9cf20adffb"
SRC_URI[sha256sum] = "322629ec7c779dce4700e8f3ba531002b724d2429178c1cbb0dc169255dc9a8b"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-zopeinterface \
    "
