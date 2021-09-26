DESCRIPTION = "WSGI object-dispatching web framework"
HOMEPAGE = "https://pypi.python.org/pypi/pecan/"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d846877d24bbb3d7a00a985c90378e8c"

SRC_URI[md5sum] = "8f006ed860f7fbdf0c0bf5ad0cf2f3b4"
SRC_URI[sha256sum] = "b5461add4e3f35a7ee377b3d7f72ff13e93f40f3823b3208ab978b29bde936ff"

inherit pypi

RDEPENDS_${PN} = "${PYTHON_PN}-mako \
                  ${PYTHON_PN}-webtest \
                  ${PYTHON_PN}-six \
                  ${PYTHON_PN}-logutils \
"
