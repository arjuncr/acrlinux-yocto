SUMMARY = "Python higher-order functions and operations on callable objects"
HOMEPAGE = "https://pypi.python.org/pypi/functools32"
SECTION = "devel/python"
LICENSE = "PSFv2"

PV = "3.2.3-2"
SRCREV = "ad90fa86e2f4f494a3aedb0571274f3bbc6d7ab5"

SRCNAME = "functools32"

LIC_FILES_CHKSUM = "file://LICENSE;md5=27cf2345969ed18e6730e90fb0063a10"
SRC_URI = "git://github.com/MiCHiLU/python-${SRCNAME}.git"

S = "${WORKDIR}/git"

inherit distutils3

DISTUTILS_INSTALL_ARGS = "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"

DEPENDS += " \
	python-pbr \
	"

RDEPENDS_${PN} += "python-testtools \
	python-pbr \
	"

