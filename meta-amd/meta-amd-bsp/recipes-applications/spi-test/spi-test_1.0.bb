DESCRIPTION = "Sample application for AMD SPI driver"
SECTION = "applications"
LICENSE = "BSD"
DEPENDS = "readline"
LIC_FILES_CHKSUM = "file://spirom-test.c;endline=29;md5=8e7a9706367d146e5073510a6e176dc2"

SRC_URI = "file://spirom-test.c \
           file://spirom.h \
          "

SRC_URI_append_amdx86 = "file://0001-Modified-the-spi-driver-test-application-to-support-.patch"

S = "${WORKDIR}"

TARGET_CC_ARCH += "${LDFLAGS}"

do_compile() {
	${CC} spirom-test.c -o spirom-test -lreadline
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 spirom-test ${D}${bindir}
}
