SUMMARY = "Extensions for JSONPath RW"
HOMEPAGE = "https://github.com/sileht/python-jsonpath-rw-ext"
SECTION = "devel/python"
LICENSE = "Apache-2.0"

PV = "0.1.9+git${SRCPV}"
SRCREV = "0a2d032f9743f5c9dd0f29be20a22b3f3388a93d"

SRCNAME = "jsonpath-rw-ext"

LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"
SRC_URI = "git://github.com/sileht/${BPN}.git"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
	python-pbr \
	"

RDEPENDS_${PN} += " \
        python-pbr \
        python-babel \
        python-jsonpath-rw \
	"

