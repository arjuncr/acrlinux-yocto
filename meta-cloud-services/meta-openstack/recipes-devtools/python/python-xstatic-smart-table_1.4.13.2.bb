DESCRIPTION = "Angular JavaScript library packaged for setuptools3"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-smart-table"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=e057224b3cd6c78a00683d4f37a924fd"

PYPI_PACKAGE = "XStatic-smart-table"

SRC_URI[md5sum] = "21640f39d064ea292de9e8e7b33f9595"
SRC_URI[sha256sum] = "f2fa5ed37c2953253de79c61c346fa6c3c4f38731295d2240552c14298db6b0a"

inherit setuptools3 pypi

DEPENDS += " \
        python-pip \
        "

RDEPENDS_${PN} += " \
        "
