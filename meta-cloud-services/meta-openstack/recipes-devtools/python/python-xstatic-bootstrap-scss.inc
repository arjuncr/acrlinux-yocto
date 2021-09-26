DESCRIPTION = "%DESCRIPTION%"
HOMEPAGE = "%URL%"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-Bootstrap-SCSS"

SRC_URI[md5sum] = "cc0d26647d10e1de84ba6649d3264f77"
SRC_URI[sha256sum] = "4d388df037293163bf3242e0b60c79d06e1f743af8b0be8473f92676340e5acd"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
