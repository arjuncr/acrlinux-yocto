DESCRIPTION = "bootswatch javascript library packaged for setuptools (easy_install) / pip."
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-bootswatch"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PYPI_PACKAGE = "XStatic-bootswatch"

SRC_URI[md5sum] = "422f09e076ff1741b76a9f7554ceca32"
SRC_URI[sha256sum] = "93ee5863c1ec07212fe12ae137a10708b4102720391d460f061dd3f441ba3b6e"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

RDEPENDS_${PN} += " \
        "
