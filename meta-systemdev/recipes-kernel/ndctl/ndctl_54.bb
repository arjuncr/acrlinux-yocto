SUMMARY = "Utility library for managing the libnvdimm"
DESCRIPTION = "Utility library for managing the libnvdimm \
(non-volatile memory device) sub-system in the Linux kernel."

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=e66651809cac5da60c8b80e9e4e79e08"

inherit pkgconfig autotools bash-completion

DEPENDS += "kmod udev json-c util-linux"

SRC_URI = "https://github.com/pmem/ndctl/archive/v54.tar.gz"
SRC_URI[md5sum] = "0bec56dc136b3b393165e057408ae710"
SRC_URI[sha256sum] = "25cb373b7e531c3061c0e070c2ebc087941f5f11729a3b6d4522f84a4803bc39"

EXTRA_OECONF += "--enable-logging --enable-debug --enable-local --disable-docs"

B = "${S}"

do_configure_prepend() {
	cd ${S}
	./autogen.sh
}
