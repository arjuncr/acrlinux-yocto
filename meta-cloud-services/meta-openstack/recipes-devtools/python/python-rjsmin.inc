DESCRIPTION = "rJSmin is a javascript minifier written in python."
HOMEPAGE = "http://opensource.perlig.de/rjsmin/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[md5sum] = "9f67e133c88df5497d3da847603da9bf"
SRC_URI[sha256sum] = "dd9591aa73500b08b7db24367f8d32c6470021f39d5ab4e50c7c02e4401386f1"

inherit pypi

RDEPENDS_${PN} += " \
        "

CLEANBROKEN = "1"
