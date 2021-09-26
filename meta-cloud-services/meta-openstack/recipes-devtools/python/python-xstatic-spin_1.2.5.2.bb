DESCRIPTION = "Spin JavaScript library packaged for setuptools3"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Spin"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=322c2399a1682aaec2f4e5fff4be5726"

PYPI_PACKAGE = "XStatic-Spin"

SRC_URI[md5sum] = "df83f80fd0b11545b64497112996e49e"
SRC_URI[sha256sum] = "7f46ef0e45e047019ba6eda22c432fb96f681b97bbe7f1749aa9209e07727192"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
