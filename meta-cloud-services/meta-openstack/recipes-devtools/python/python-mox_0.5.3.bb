DESCRIPTION = "Mock object framework"
HOMEPAGE = "https://github.com/dreamhost/cliff"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "6de7371e7e8bd9e2dad3fef2646f4a43"
SRC_URI[sha256sum] = "424ee725ee12652802b4e86571f816059b0d392401ceae70bf6487d65602cba9"

inherit distutils3 pypi

DISTUTILS_INSTALL_ARGS = "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"
