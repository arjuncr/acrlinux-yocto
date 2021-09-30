LICENSE = "GPL-3.0-with-GCC-exception & GPLv3 & GPLv2 & LGPLv3 & LGPLv2.1 & LGPLv2"

EAT_BFD_LICENSE = "GPL-3.0"
EAT_GCC_LICENSE = "GPL-3.0-with-GCC-exception & GPLv3"
EAT_GDB_LICENSE = "GPL-3.0"
EAT_LIBC_LICENSE = "GPLv2 & LGPLv2.1"
EAT_RLE_LICENSE = "GPL-3.0-with-GCC-exception"

LICENSE_${PN} = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-dev = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-doc = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-dbg = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-pic = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-utils = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-mtrace = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-gconv = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-extra-nss = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-thread-db = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-pcprofile = "${EAT_LIBC_LICENSE}"
LICENSE_${PN}-staticdev = "${EAT_LIBC_LICENSE}"
LICENSE_catchsegv = "${EAT_LIBC_LICENSE}"
LICENSE_glibc-extra-nss = "${EAT_LIBC_LICENSE}"
LICENSE_glibc-thread-db = "${EAT_LIBC_LICENSE}"

LICENSE_libmemusage = "${EAT_LIBC_LICENSE}"
LICENSE_libsegfault = "${EAT_LIBC_LICENSE}"
LICENSE_libsotruss = "${EAT_LIBC_LICENSE}"
LICENSE_sln = "${EAT_LIBC_LICENSE}"
LICENSE_nscd = "${EAT_LIBC_LICENSE}"
LICENSE_ldd = "${EAT_LIBC_LICENSE}"

LICENSE_libasan-dev = "${EAT_GCC_LICENSE}"
LICENSE_libasan = "${EAT_GCC_LICENSE}"
LICENSE_libasan-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libatomic-dev = "${EAT_GCC_LICENSE}"
LICENSE_libatomic = "${EAT_GCC_LICENSE}"
LICENSE_libatomic-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libg2c-dev = "${EAT_GCC_LICENSE}"
LICENSE_libg2c = "${EAT_GCC_LICENSE}"
LICENSE_libgcc-dev = "${EAT_GCC_LICENSE}"
LICENSE_libgcc = "${EAT_GCC_LICENSE}"
LICENSE_libgfortran-dbg = "${EAT_GCC_LICENSE}"
LICENSE_libgfortran-dev = "${EAT_GCC_LICENSE}"
LICENSE_libgfortran = "${EAT_GCC_LICENSE}"
LICENSE_libgfortran-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libgomp-dev = "${EAT_GCC_LICENSE}"
LICENSE_libgomp = "${EAT_GCC_LICENSE}"
LICENSE_libgomp-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libitm-dev = "${EAT_GCC_LICENSE}"
LICENSE_libitm = "${EAT_GCC_LICENSE}"
LICENSE_libitm-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_liblsan-dev = "${EAT_GCC_LICENSE}"
LICENSE_liblsan = "${EAT_GCC_LICENSE}"
LICENSE_liblsan-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libmudflap-dev = "${EAT_GCC_LICENSE}"
LICENSE_libmudflap = "${EAT_GCC_LICENSE}"
LICENSE_libmudflap-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libquadmath-dev = "${EAT_GCC_LICENSE}"
LICENSE_libquadmath = "${EAT_GCC_LICENSE}"
LICENSE_libquadmath-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libssp-dev = "${EAT_GCC_LICENSE}"
LICENSE_libssp = "${EAT_GCC_LICENSE}"
LICENSE_libssp-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libstdc++-dbg = "${EAT_GCC_LICENSE}"
LICENSE_libstdc++-dev = "${EAT_GCC_LICENSE}"
LICENSE_libstdc++ = "${EAT_GCC_LICENSE}"
LICENSE_libstdc++-precompile-dev = "${EAT_GCC_LICENSE}"
LICENSE_libstdc++-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libtsan-dev = "${EAT_GCC_LICENSE}"
LICENSE_libtsan = "${EAT_GCC_LICENSE}"
LICENSE_libtsan-staticdev = "${EAT_GCC_LICENSE}"
LICENSE_libubsan-dev = "${EAT_GCC_LICENSE}"
LICENSE_libubsan = "${EAT_GCC_LICENSE}"
LICENSE_libubsan-staticdev = "${EAT_GCC_LICENSE}"

LICENSE_linux-libc-headers-dev = "GPLv2"
LICENSE_linux-libc-headers = "GPLv2"

LICENSE_gdbserver = "GPLv2 & GPLv3 & LGPLv2 & LGPLv3"