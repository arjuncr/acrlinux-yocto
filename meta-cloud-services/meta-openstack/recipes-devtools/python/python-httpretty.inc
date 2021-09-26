#
# Copyright (C) 2014 Wind River Systems, Inc.
#
DESCRIPTION = "HTTP client mock for Python"
HOMEPAGE = "https://pypi.python.org/pypi/httpretty"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=113a91608a1d89039af9393c39bcaa2c"

SRC_URI[md5sum] = "2fc3d0dc986200be95ce8ad3ef56bc04"
SRC_URI[sha256sum] = "66216f26b9d2c52e81808f3e674a6fb65d4bf719721394a1a9be926177e55fbe"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

