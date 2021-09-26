SUMMARY = "Report processor frequency and idle statistics"
DESCRIPTION = "turbostat  reports processor topology, frequency, idle \
power-state statistics, temperature and power on modern X86 processors. \
Either command is forked and statistics are printed upon its completion, \
or statistics are printed periodically."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernelsrc
PROVIDES = "virtual/turbostat"

do_populate_lic[depends] += "virtual/kernel:do_patch"
do_configure[depends] += "virtual/kernel:do_shared_workdir"

B = "${WORKDIR}/${BPN}-${PV}"

EXTRA_OEMAKE = '\
    -C ${S}/tools/power/x86/turbostat \
    O=${B} \
    CROSS_COMPILE=${TARGET_PREFIX} \
    ARCH=${ARCH} \
    CC="${CC}" \
    AR="${AR}" \
'

EXTRA_OEMAKE += "\
    'prefix=${prefix}' \
    'bindir=${bindir}' \
    'sharedir=${datadir}' \
    'sysconfdir=${sysconfdir}' \
    'sharedir=${@os.path.relpath(datadir, prefix)}' \
    'mandir=${@os.path.relpath(mandir, prefix)}' \
    'infodir=${@os.path.relpath(infodir, prefix)}' \
"

do_compile() {
	# Linux kernel build system is expected to do the right thing
	unset CFLAGS
	oe_runmake turbostat
}

do_install() {
	# Linux kernel build system is expected to do the right thing
	unset CFLAGS
	oe_runmake DESTDIR=${D} install
}

python do_package_prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION", True).split("-")[0])
}
