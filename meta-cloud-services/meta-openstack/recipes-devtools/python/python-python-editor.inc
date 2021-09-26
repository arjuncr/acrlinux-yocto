DESCRIPTION = "Programmatically open an editor, capture the result"
HOMEPAGE = "https://github.com/fmoo/python-editor"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

PV = "0.4+git${SRCPV}"
SRCREV = "d6fa2a6bb3106a1ba00fe40f9af62e4ddc539e1e"

SRCNAME = "python-editor"
SRC_URI = "git://github.com/fmoo/${SRCNAME}.git"

S = "${WORKDIR}/git"

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

RDEPENDS_${PN} += " \
        "
