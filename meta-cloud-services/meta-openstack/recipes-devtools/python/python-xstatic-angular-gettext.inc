DESCRIPTION = "Angular-Gettext javascript library packaged for setuptools (easy_install) / pip."
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Angular-Gettext"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-Angular-Gettext"

SRC_URI[md5sum] = "de55242cf5dc5fa712ac7e4a53aeb12b"
SRC_URI[sha256sum] = "18db14dccaae5d64a9b66d08d0134b6c154fc1fb1e216964bba8e403e03c870e"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

RDEPENDS_${PN} += " \
        "
