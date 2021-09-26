DESCRIPTION = "mox3: mock object framework for Python"
HOMEPAGE = "https://pypi.python.org/pypi/mox3"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "c930d8479996541b04447a67e96e4a62"
SRC_URI[sha256sum] = "8094868e70f814b4a526ecc592365437bc93a7dd60341321344987b3e8dba49d"

inherit setuptools3 pypi

DEPENDS += "\
    python-pbr \
    "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
    python-pbr-native \
    "

RDEPENDS_${PN} += " \
    python-pbr \
    python-fixtures \
"
