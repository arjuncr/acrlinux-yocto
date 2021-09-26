SUMMARY = "Hard Drive Performance Benchmark"
HOMEPAGE = "http://hdperf.sourceforge.net/"
SECTION = "benchmarks"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://hdperf.c;beginline=0;endline=21;md5=a4486bc71611cfb35c024ac5b88b7038"
DEPENDS = "virtual/libc"

SRC_URI = "https://downloads.sourceforge.net/project/${BPN}/${BPN}/${PV}/${BP}.tar.gz"

SRC_URI[md5sum] = "b42930b968e124d9b2c909681f863c82"
SRC_URI[sha256sum] = "367135fbab6a83545cd1a9e08c8543cd503b165d83e4aca94095fd4e30e99555"

S = "${WORKDIR}/hdperf"

# The tarball ships a binary.... sigh.
do_configure() {
	rm ${S}/hdperf
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 hdperf ${D}${bindir}
}
