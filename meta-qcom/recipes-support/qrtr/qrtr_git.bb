SUMMARY = "Qualcomm QRTR applications and library"
HOMEPAGE = "https://github.com/andersson/qrtr.git"
SECTION = "devel"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15329706fbfcb5fc5edcc1bc7c139da5"

inherit systemd

SRCREV = "9dc7a88548c27983e06465d3fbba2ba27d4bc050"
SRC_URI = "git://github.com/andersson/${BPN}.git;branch=master;protocol=https"

PV = "0.3+${SRCPV}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "prefix=${prefix} bindir=${bindir} libdir=${libdir} includedir=${includedir} LDFLAGS='${LDFLAGS}'"

do_install () {
    oe_runmake install DESTDIR=${D} prefix=${prefix} servicedir=${systemd_unitdir}/system
}

SYSTEMD_SERVICE_${PN} = "qrtr-ns.service"
