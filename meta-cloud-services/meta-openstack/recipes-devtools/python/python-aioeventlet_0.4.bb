DESCRIPTION = "Asyncio event loop scheduling callbacks in eventlet"
HOMEPAGE = "http://aioeventlet.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f7bb094c7232b058c7e9f2e431f389c"

SRC_URI += " \
    file://Makefile-skip-building-docs.patch \
    "

SRC_URI[md5sum] = "678ea30265ae0326bddc767f80efd144"
SRC_URI[sha256sum] = "fe78c2b227ce077b1581e2ae2c071f351111d0878ec1b0216435f6a898df79a6"

inherit setuptools3 pypi

DEPENDS += " \
        python-pip \
        "

RDEPENDS_${PN} += " \
        python-sphinx \
        "

