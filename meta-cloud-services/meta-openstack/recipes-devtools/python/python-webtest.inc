DESCRIPTION = "Helper to test WSGI applications"
HOMEPAGE = "https://pypi.python.org/pypi/WebTest/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=e3e00192f000e33de99fd5a3cb32a8fc"

PYPI_PACKAGE = "WebTest"

RDEPENDS_${PN} += "${PYTHON_PN}-beautifulsoup4"

SRC_URI[md5sum] = "dd0385c725b85ac1e8079f38d2acd7b1"
SRC_URI[sha256sum] = "41348efe4323a647a239c31cde84e5e440d726ca4f449859264e538d39037fd0"
