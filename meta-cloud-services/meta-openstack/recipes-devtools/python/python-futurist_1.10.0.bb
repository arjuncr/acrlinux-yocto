DESCRIPTION = "Useful additions to futures, from the future"
HOMEPAGE = "https://pypi.python.org/pypi/futurist"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "d7f98e9a5cb09fe9706e4afcbeab1552"
SRC_URI[sha256sum] = "139d223503d47275636285ae98e7b470085b2b3b6fb2fc9a0d04a76de4b3d30e"

inherit setuptools3 pypi

DEPENDS += " \
        python-pip \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-six \
        python-monotonic \
        python-futures \
        python-contextlib2 \
        python-prettytable \
        "

