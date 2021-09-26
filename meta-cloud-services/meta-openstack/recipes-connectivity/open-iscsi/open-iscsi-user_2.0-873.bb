DESCRIPTION = "Open-iSCSI project is a high performance, transport independent, multi-platform implementation of RFC3720."
HOMEPAGE = "http://www.open-iscsi.org/"
LICENSE = "GPLv2"
PR = "r1"

inherit systemd

LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

SRC_URI = "http://www.open-iscsi.org/bits/open-iscsi-${PV}.tar.gz   \
           file://0001-fix-build-error-of-cross-build.patch         \
           file://open-iscsi                                        \
           file://initiatorname.iscsi                               \
           "


S = "${WORKDIR}/open-iscsi-${PV}"
TARGET_CC_ARCH += "${LDFLAGS}"
EXTRA_OEMAKE += "CONFIGURE_ARGS='--host=${HOST_SYS}'"

do_compile () {
        oe_runmake user
}

do_install () {
        oe_runmake DESTDIR="${D}" install_user
        cp -f "${WORKDIR}/open-iscsi" "${D}/etc/init.d/"
        install -m 0644 ${WORKDIR}/initiatorname.iscsi ${D}/etc/iscsi/initiatorname.iscsi
}


SRC_URI[md5sum] = "8b8316d7c9469149a6cc6234478347f7"
SRC_URI[sha256sum] = "7dd9f2f97da417560349a8da44ea4fcfe98bfd5ef284240a2cc4ff8e88ac7cd9"

# systemd support
PACKAGES =+ "${PN}-systemd"
SRC_URI_append = "  file://iscsi-initiator                                   \
                    file://iscsi-initiator.service                           \
                    file://iscsi-initiator-targets.service                   \
                 "
RDEPENDS_${PN} += "bash"
RDEPENDS_${PN}-systemd += "${PN}"
FILES_${PN}-systemd +=  "   ${base_libdir}/systemd                  \
                            ${sysconfdir}/default/iscsi-initiator   \
                        "
SYSTEMD_PACKAGES = "${PN}-systemd"
SYSTEMD_SERVICE_${PN}-systemd = "iscsi-initiator.service iscsi-initiator-targets.service"

do_install_append () {
        install -d ${D}${sysconfdir}/default/
        install -m 0644 ${WORKDIR}/iscsi-initiator ${D}${sysconfdir}/default/
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/iscsi-initiator.service ${D}${systemd_unitdir}/system/
        install -m 0644 ${WORKDIR}/iscsi-initiator-targets.service ${D}${systemd_unitdir}/system/
}
