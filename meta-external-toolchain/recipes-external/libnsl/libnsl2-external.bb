SUMMARY = "Library containing NIS functions using TI-RPC (IPv6 enabled)"
DESCRIPTION = "This library contains the public client interface for NIS(YP) and NIS+ \
               it was part of glibc and now is standalone packages. it also supports IPv6. \
               This recipe should work for extracting either the glibc or standalone libnsl \
               from the external toolchain."
HOMEPAGE = "https://github.com/thkukuk/libnsl"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"
SECTION = "libs"

inherit external-toolchain

FILES_${PN} = "${libdir}/libnsl*.so.* ${libdir}/libnsl-*.so"
FILES_${PN}-dev = "${libdir}/libnsl.so ${includedir}/rpcsvc/nis*.h ${includedir}/rpcsvc/yp*.h"
FILES_${PN}-staticdev = "${libdir}/libnsl.a"

libc_rdep = "${@'${PREFERRED_PROVIDER_virtual/libc}' if d.getVar('PREFERRED_PROVIDER_virtual/libc') else '${TCLIBC}'}"
RDEPENDS_${PN} += "${libc_rdep}"

# Default to avoid parsing issue
PREFERRED_PROVIDER_libtirpc ?= "libtirpc"
RDEPENDS_${PN} += "${PREFERRED_PROVIDER_libtirpc}"

do_install_extra () {
    # Depending on whether this comes from the standalone libnsl2 or glibc, the
    # soname may vary, hence covering both 1 and 2, and it may be installed in
    # base_libdir instead of libdir, but the FILES configuration may result in its
    # location changing, breaking the libnsl.so symlink, so recreate it here.
    cd ${D}${libdir}/ || exit 1
    rm -f libnsl.so
    ln -s libnsl.so.[12] libnsl.so
}
