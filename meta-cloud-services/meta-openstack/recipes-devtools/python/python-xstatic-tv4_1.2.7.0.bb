DESCRIPTION = "Tiny Validator packaged for setuptools3 (easy_install) / pip."
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-tv4"
SECTION = "devel/python"
LICENSE = "PD & MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=68f6d0037519374aba3cc3d13bb66260"

PYPI_PACKAGE = "XStatic-tv4"

SRC_URI[md5sum] = "921148dff35fb41431d5f122da570248"
SRC_URI[sha256sum] = "9b4c57244e914126cdda5d8bc24698189d73800203c85b1fc945a08e25c7c713"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
