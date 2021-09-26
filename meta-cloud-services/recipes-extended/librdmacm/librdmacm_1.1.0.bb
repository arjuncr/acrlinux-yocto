SUMMARY = "Userspace RDMA Connection Manager"
DESCRIPTION = "\
User space RDMA 'socket' protocol and libraries for establising RDMA\
communication.  Includes both Infiniband specific and general RDMA\
communication management libraries for unreliable datagram, reliable\
connected, and multicast data transfers.\
Also includes ACM (communication management assistant) service."

HOMEPAGE = "http://www.openfabrics.org/downloads/rdmacm/"
SECTION = "libs/devel"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=39cc3044d68741f9005da73e9b92db95"

DEPENDS = "virtual/libibverbs"

# Repo is at http://git.openfabrics.org/git?p=~shefty/librdmacm.git
SRC_URI = "https://www.openfabrics.org/downloads/rdmacm/${BPN}-${PV}.tar.gz \
           file://0001-librdmacm-needs-libdl.patch"

SRC_URI[md5sum] = "9459e523002978ef6e7b852e01d8b29e"
SRC_URI[sha256sum] = "8f10848d4810585d6d70b443abc876c1db8df5e9b8b07e095c7e6eaf4ac380c5"

inherit autotools

# Allow plug-in symlinks.
INSANE_SKIP_${PN} += "dev-so"

PACKAGES += "${PN}-utils"
FILES_${PN} = "${libdir}/*.so.* ${libdir}/rsocket/*.so*"
FILES_${PN}-utils = "${bindir}"
FILES_${PN}-staticdev += "${libdir}/rsocket/librspreload.a"
FILES_${PN}-dbg += "${libdir}/rsocket/.debug"

PACKAGECONFIG ??= ""
PACKAGECONFIG[valgrind] = "--with-valgrind,--without-valgrind,valgrind,"

do_install_append() {
        rm -f ${D}${libdir}/librdmacm.la
        rm -f ${D}${libdir}/rsocket/librspreload.la
}

COMPATIBLE_HOST_mipsarch = "none"
COMPATIBLE_HOST_arm = "none"
