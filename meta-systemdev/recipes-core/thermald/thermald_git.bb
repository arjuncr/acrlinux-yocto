SUMMARY = "Linux Thermal Daemon"
DESCRIPTION = "Linux user mode daemon to system developers, reducing time to market with controlled thermal management using P-states, T-states, and the Intel power clamp driver."
HOMEPAGE = "https://01.org/linux-thermal-daemon"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea8831610e926e2e469075b52bf08848"

SRC_URI = "git://github.com/01org/thermal_daemon.git"
SRCREV = "d39c157d7685d55b3067245459921a74c9450050"
S = "${WORKDIR}/git"

inherit autotools pkgconfig systemd
SYSTEMD_SERVICE_${PN} = "thermald.service"

DEPENDS = "dbus-glib dbus-glib-native libxml2"

EXTRA_OECONF = "--with-systemdsystemunitdir=${systemd_unitdir}/system"

FILES_${PN} += "${datadir}/dbus-1/system-services/*"
