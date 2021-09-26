SUMMARY = "Pure python memcached client"
DESCRIPTION = "\
  This software is a 100% Python interface to the memcached memory cache daemon. \
  It is the client side software which allows storing values in one or more, possibly remote, \
  memcached servers. Search google for memcached for more information."
HOMEPAGE = "https://pypi.python.org/pypi/python-memcached/"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PSF.LICENSE;md5=7dd786e8594f1e787da94a946557b40e"

PV = "1.57+git${SRCPV}"
SRCREV = "664bd3e23fe500fbde4c70636e2d24c8fd2f35af"

SRC_URI = "git://github.com/linsomniac/python-memcached.git"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-six \
    "

