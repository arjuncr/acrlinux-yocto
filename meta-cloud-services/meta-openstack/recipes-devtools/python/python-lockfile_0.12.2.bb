DESCRIPTION = "Platform-independent file locking module"
HOMEPAGE = "http://code.google.com/p/pylockfile/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2340dffbbfea534b58f1349984eeef72"

SRC_URI[md5sum] = "a6a1a82957a23afdf44cfdd039b65ff9"
SRC_URI[sha256sum] = "6aed02de03cba24efabcd600b30540140634fc06cfa603822d508d5361e9f799"

inherit distutils3 pypi

DEPENDS += "\
    python-pbr \
    "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
    python-pbr-native \
    "

RDEPENDS_${PN} += "\
    python-pbr \
    "
