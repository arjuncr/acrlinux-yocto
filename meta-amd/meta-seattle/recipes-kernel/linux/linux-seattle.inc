DESCRIPTION = "Linux Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

require recipes-kernel/linux/linux-yocto.inc
KERNEL_EXTRA_ARGS += "dtbs"

S = "${WORKDIR}/git"

DEPENDS += "libgcc"

MACHINE_KERNEL_PR_append = "b+gitr${SRCPV}"
PR = "${MACHINE_KERNEL_PR}"

COMPATIBLE_MACHINE = "seattle*"

do_deploy_append() {                                                                                         
        install -m 0644 ${B}/arch/arm64/boot/dts/amd/amd-overdrive.dtb ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.dtb
}                                                                                                            

