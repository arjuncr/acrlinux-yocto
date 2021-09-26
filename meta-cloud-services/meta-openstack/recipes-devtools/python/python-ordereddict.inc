DESCRIPTION = "A drop-in substitute for Py2.7's new collections.OrderedDict that works in Python 2.4-2.6."
HOMEPAGE = "https://pypi.python.org/pypi/ordereddict"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f0267460024072a9bbf135ee87a41b8"

SRC_URI[md5sum] = "a0ed854ee442051b249bfad0f638bbec"
SRC_URI[sha256sum] = "1c35b4ac206cef2d24816c89f89cf289dd3d38cf7c449bb3fab7bf6d43f01b1f"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pip \
    "
