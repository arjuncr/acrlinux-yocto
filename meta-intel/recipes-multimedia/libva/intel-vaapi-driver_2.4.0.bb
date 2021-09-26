SUMMARY = "VA driver for Intel G45 & HD Graphics family"
DESCRIPTION = "intel-vaapi-driver is the VA-API implementation \
for Intel G45 chipsets and Intel HD Graphics for Intel Core \
processor family."

HOMEPAGE = "https://github.com/intel/intel-vaapi-driver"
BUGTRACKER = "https://github.com/intel/intel-vaapi-driver/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2e48940f94acb0af582e5ef03537800f"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

DEPENDS = "libva libdrm"

SRC_URI = "https://github.com/intel/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "731dd9aaf9f5ef1b9c906ce82ef0220b"
SRC_URI[sha256sum] = "71e2ddd985af6b221389db1018c4e8ca27a7f939fb51dcdf49d0efcb5ff3d089"

UPSTREAM_CHECK_URI = "https://github.com/intel/intel-vaapi-driver/releases"

inherit meson pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "x11", "x11", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "opengl wayland", "wayland", "", d)}"
PACKAGECONFIG[x11] = "-Dwith_x11=yes, -Dwith_x11=no"
PACKAGECONFIG[wayland] = "-Dwith_wayland=yes, -Dwith_wayland=no, wayland wayland-native virtual/egl"

FILES_${PN} += "${libdir}/dri/*.so"
FILES_${PN}-dev += "${libdir}/dri/*.la"
FILES_${PN}-dbg += "${libdir}/dri/.debug"
