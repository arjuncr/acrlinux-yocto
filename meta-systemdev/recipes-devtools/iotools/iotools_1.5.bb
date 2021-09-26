SUMMARY = "A suite of tools for reading and writing hardware registers"
HOMEPAGE = "https://code.google.com/p/iotools/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/iotools/iotools-1.5.tar.gz"
SRC_URI += "file://Makefile.patch"

SRC_URI[md5sum] = "64c27488a70866577cf99044aa01e28d"
SRC_URI[sha256sum] = "3186ba296072f644dda881b78d77e3774b79e8cef3e828fffc947d558b08830d"

CFLAGS += ' -D_GNU_SOURCE -DVER_MAJOR=1 -DVER_MINOR=5'

do_compile () {
    oe_runmake STATIC=0
}

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/iotools ${D}${sbindir}/iotools
}

pkg_postinst_ontarget_${PN} () {
	${sbindir}/iotools --make-links
}
