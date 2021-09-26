SUMMARY = "Python2's stdlib csv module replacement with unicode support"
HOMEPAGE = "https://github.com/jdunck/python-unicodecsv"
SECTION = "devel/python"
LICENSE = "BSD"

PV = "0.14.1+git${SRCPV}"
SRCREV = "4563e33ce322f5e2dea41e76cb33dc0e008ad341"

SRCNAME = "unicodecsv"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e71cdeaa2d2d59b225b8dfb9363fa590"
SRC_URI = "git://github.com/jdunck/${BPN}.git"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
	python-pbr \
	"

RDEPENDS_${PN} += " \
	"

