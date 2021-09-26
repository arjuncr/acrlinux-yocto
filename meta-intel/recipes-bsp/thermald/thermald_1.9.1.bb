SUMMARY = "Linux thermal daemon"

DESCRIPTION = "Thermal Daemon is a Linux daemon used to prevent the \
overheating of platforms. This daemon monitors temperature and applies \
compensation using available cooling methods."

HOMEPAGE = "https://github.com/01org/thermal_daemon"

DEPENDS = "dbus dbus-glib dbus-glib-native libxml2 glib-2.0 glib-2.0-native"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"
DEPENDS_append_libc-musl = " argp-standalone"
DEPENDS_append_toolchain-clang = " openmp"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea8831610e926e2e469075b52bf08848"

SRC_URI = "git://github.com/intel/thermal_daemon/ \
           file://0001-thd_trip_point-fix-32-bit-build-error-with-musl-v1.2.patch \
           "
SRCREV = "7e23f7cc4611fd7289014c9805749ec75d59bae0"
S = "${WORKDIR}/git"

inherit pkgconfig autotools systemd

FILES_${PN} += "${datadir}/dbus-1/system-services/*.service"

SYSTEMD_SERVICE_${PN} = "thermald.service"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

CONFFILES_${PN} = " \
                   ${sysconfdir}/thermald/thermal-conf.xml \
                   ${sysconfdir}/thermald/thermal-cpu-cdev-order.xml \
                  "

UPSTREAM_CHECK_URI = "https://github.com/01org/thermal_daemon/releases"
