#
# Copyright (C) 2014 Wind River Systems, Inc.
#
DESCRIPTION = "Simple cross-platform colored terminal text in Python"
HOMEPAGE = "http://pypi.python.org/pypi/colorama"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b4936429a56a652b84c5c01280dcaa26"

SRC_URI[md5sum] = "f927529cd1735f6f50ee2c61628e9c1f"
SRC_URI[sha256sum] = "05eed71e2e327246ad6b38c540c4a3117230b19679b875190486ddd2d721422d"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

