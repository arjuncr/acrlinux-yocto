SUMMARY = "Program for interfacing with the Watts Up? Power Meter"
HOMEPAGE = "https://github.com/pyrovski/watts-up"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://readme.txt;md5=7ac3c2fa0c997e3447dcdb3f8f853539"

SRC_URI = "git://github.com/pyrovski/watts-up.git"
SRCREV = "9bee5066cc5bf6e33a381f6879c7bbc3ce8ef3ce"
S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${bindir}
	install -m 0555 wattsup ${D}${bindir}
}
