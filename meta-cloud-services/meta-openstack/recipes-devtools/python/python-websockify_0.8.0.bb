DESCRIPTION = "WebSockets support for any application/server"
HOMEPAGE = "https://github.com/kanaka/websockify"
SECTION = "devel/python"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a3b5f97c9d64189899b91b3728bfd774"

SRC_URI[md5sum] = "8fa547ca4de84a96aa3472d55fbcdd59"
SRC_URI[sha256sum] = "547d3d98c5081f2dc2872a2e4a3aef33e0ee5141d5f6209204aab2f4a41548d2"

inherit setuptools3 pypi

RDEPENDS_${PN} += "gmp"

FILES_${PN} += "${datadir}/*"
