DESCRIPTION = "IrDragNDrop javascript library packaged for setuptools"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Angular-IrDragNDrop"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=9acc2dfc3a0345c175df08104bd22298"

PYPI_PACKAGE = "XStatic-Angular-IrDragNDrop"

SRC_URI[md5sum] = "6ea8809bae94014aef699d58836dba84"
SRC_URI[sha256sum] = "5595f4a98ac8f8468f7e56dc916e10c0fc5f0197567899a75755f99fe8b5bd6d"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

RDEPENDS_${PN} += " \
        "
