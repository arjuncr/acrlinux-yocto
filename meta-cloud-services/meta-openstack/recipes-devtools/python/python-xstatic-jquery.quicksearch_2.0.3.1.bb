DESCRIPTION = "JQuery.quicksearch JavaScript library packaged for setuptools3 "
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-JQuery.quicksearch"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=503c3857973c52f673691b910068e2d0"

PYPI_PACKAGE = "XStatic-JQuery.quicksearch"

SRC_URI[md5sum] = "0dc4bd1882cf35dc7b19a236ba09b89d"
SRC_URI[sha256sum] = "1271571b420417add56c274fd935e81bfc79e0d54a03559d6ba5ef369f358477"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
