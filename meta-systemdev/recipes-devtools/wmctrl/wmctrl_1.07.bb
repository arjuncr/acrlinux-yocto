DESCRIPTION = "A UNIX/Linux command line tool to interact with an EWMH/NetWM compatible X Window Manager"
HOMEPAGE = "https://sites.google.com/site/tstyblo/wmctrl"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "https://sites.google.com/site/tstyblo/wmctrl/wmctrl-1.07.tar.gz"

inherit autotools pkgconfig
DEPENDS = "glib-2.0 libxmu"

SRC_URI[md5sum] = "1fe3c7a2caa6071e071ba34f587e1555"
SRC_URI[sha256sum] = "d78a1efdb62f18674298ad039c5cbdb1edb6e8e149bb3a8e3a01a4750aa3cca9"
