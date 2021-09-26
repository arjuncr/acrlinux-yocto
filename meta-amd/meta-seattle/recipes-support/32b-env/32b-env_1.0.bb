SUMMARY = "A simple set of shell scripts used for enabling support for \
dynamic linked 32b application on AMD Seattle."
DESCRIPTION = "The 32b-env package installs the set_32b_env_chroot.sh \
and set_32b_env_qemu.sh shell scripts which enables support for dynamic \
linked 32b application on Seattle until the multilib support \
will be available for armv8 architecture."

SRC_URI = "file://set_32b_env_chroot.sh \
           file://set_32b_env_qemu.sh"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

inherit allarch

do_install () {
    install -d ${D}${bindir}/
    install -m 755 ${WORKDIR}/set_32b_env_chroot.sh ${D}${bindir}/
    install -m 755 ${WORKDIR}/set_32b_env_qemu.sh ${D}${bindir}/
}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_build[noexec] = "1"
