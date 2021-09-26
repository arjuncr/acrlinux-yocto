DESCRIPTION = "Fixtures, reusable state for writing clean tests and more"
HOMEPAGE = "https://pypi.python.org/pypi/fixtures/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=239e2f4698b85aad5ed39bae5d2ef226"

SRC_URI[md5sum] = "cd6345b497a62fad739efee66346c2e0"
SRC_URI[sha256sum] = "fcf0d60234f1544da717a9738325812de1f42c2fa085e2d9252d8fff5712b2ef"

inherit distutils3 pypi

DISTUTILS_INSTALL_ARGS = "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"

DEPENDS += " \
	python-pbr \
	"

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += "python-testtools \
	python-pbr \
	"
