DESCRIPTION = "Python lesscpy: Python LESS Compiler"
HOMEPAGE = "https://pypi.python.org/pypi/lesscpy"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a01aae31681ac25b4c46f98d627b9b5d"

DEPENDS += "${PYTHON_PN}-ply"
RDEPENDS_${PN} += "${PYTHON_PN}-ply"

SRC_URI[md5sum] = "fedbad1f4eb19c3af37f09560156246d"
SRC_URI[sha256sum] = "ccad1ad2a89ced1875c5ca4e16e5478b791d31fb187fe29f1541859318ed9055"

inherit pypi

DISTUTILS_INSTALL_ARGS = "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"
