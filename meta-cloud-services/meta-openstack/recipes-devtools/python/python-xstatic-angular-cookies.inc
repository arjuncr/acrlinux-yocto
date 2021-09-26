DESCRIPTION = "Angular-Cookies JavaScript library packaged for setuptools"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Angular-Cookies"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=70856f1d03d62a3e0c0cb1b7f8c7fd00"

PYPI_PACKAGE = "XStatic-Angular-Cookies"

SRC_URI[md5sum] = "a67066fac5dfe9774ab163d44e5a896a"
SRC_URI[sha256sum] = "c8fc1a52549c601809fc9f25144e4fd346820412b6430e89256e7ec71fce0b4c"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
