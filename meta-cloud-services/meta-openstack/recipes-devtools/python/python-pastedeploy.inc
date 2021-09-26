DESCRIPTION = "Load, configure, and compose WSGI applications and servers"
HOMEPAGE = "http://pythonpaste.org/deploy/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=d6c3089319205d7e6129f0213b7e949f"

PYPI_PACKAGE = "PasteDeploy"

SRC_URI[md5sum] = "6193afccc0982abedb9b7711d0d6adb0"
SRC_URI[sha256sum] = "d423fb9d51fdcf853aa4ff43ac7ec469b643ea19590f67488122d6d0d772350a"

inherit pypi

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

RDEPENDS_${PN} += "${PYTHON_PN}-paste"
