DESCRIPTION = "Better dates and times for Python"
HOMEPAGE = "https://github.com/crsmithdev/arrow"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=458d41a4064e4dc109666cfd941a29e4"

SRC_URI[md5sum] = "554f18b0e23182ee0e8cb2a882d2f607"
SRC_URI[sha256sum] = "10257c5daba1a88db34afa284823382f4963feca7733b9107956bed041aff24f"

inherit pypi

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-dateutil \
        ${PYTHON_PN}-backports-functools-lru-cache \
        "
