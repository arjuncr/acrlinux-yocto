DESCRIPTION = "Translation library for Python"
HOMEPAGE = "https://github.com/tuvistavie/python-i18n"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.md;md5=7624ac071dec291ca8ef74e62e536a7a"

PYPI_PACKAGE = "python-i18n"

SRC_URI[md5sum] = "08b8c247add9f90806c4742a685fdb88"
SRC_URI[sha256sum] = "e1d832ff7d1d39ce593793cc709b2344aa8fdf321cf707081335f146160d9f20"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        ${PYTHON_PN}-pyyaml \
        "
