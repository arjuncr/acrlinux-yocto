DESCRIPTION = "JQuery.TableSorter JavaScript library packaged for setuptools3"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-JQuery.TableSorter"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=a9ef3319547ce4563718db4b4657fb94"

PYPI_PACKAGE = "XStatic-JQuery.TableSorter"

SRC_URI[md5sum] = "fc05a6731b6ac3f6489b893f96a5d29a"
SRC_URI[sha256sum] = "3ba24aecd9a3dc71a79dd4096fa5a8a041c3a7b892c61d05e6e46de0605070f0"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
