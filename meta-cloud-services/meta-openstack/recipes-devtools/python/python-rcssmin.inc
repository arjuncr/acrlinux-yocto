DESCRIPTION = "RCSSmin is a CSS minifier."
HOMEPAGE = "http://opensource.perlig.de/rcssmin/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[md5sum] = "467d56503ce386c0b0e52f69ac143a9a"
SRC_URI[sha256sum] = "ca87b695d3d7864157773a61263e5abb96006e9ff0e021eff90cbe0e1ba18270"

inherit pypi

RDEPENDS_${PN} += " \
        "

CLEANBROKEN = "1"
