DESCRIPTION = "basic functions for parsing mime-type names and matching "
HOMEPAGE = "https://pypi.python.org/pypi/python-mimeparse/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README;md5=07e6feb820fbca7eb99538badb3cd8e2"

PYPI_PACKAGE = "python-mimeparse"

SRC_URI[md5sum] = "1d2816a16f17dcfe0c613da611fe7e13"
SRC_URI[sha256sum] = "3c69a21e37e77f754e6fc09ebda70acd92c90d8a58f29a41cc0248351378ddc3"

inherit distutils3 pypi

DISTUTILS_INSTALL_ARGS = "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"
