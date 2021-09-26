DESCRIPTION = "Firmware Image for Juno to be copied to the Configuration \
microSD card"

LICENSE = "BSD-3-Clause"
SECTION = "firmware"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

DEPENDS = "virtual/trusted-firmware-a virtual/kernel"

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "juno"

LINARO_RELEASE = "19.06"

SRC_URI = "http://releases.linaro.org/members/arm/platforms/${LINARO_RELEASE}/juno-latest-oe-uboot.zip;subdir=${UNPACK_DIR} \
    file://images-r0.txt \
    file://images-r1.txt \
    file://images-r2.txt \
    file://uEnv.txt \
"
SRC_URI[md5sum] = "01b662b81fa409d55ff298238ad24003"
SRC_URI[sha256sum] = "b8a3909bb3bc4350a8771b863193a3e33b358e2a727624a77c9ecf13516cec82"

UNPACK_DIR = "juno-firmware-${LINARO_RELEASE}"

inherit deploy nopackages

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# The ${D} is used as a temporary directory and we don't generate any
# packages for this recipe.
do_install() {
    cp -a ${WORKDIR}/${UNPACK_DIR} ${D}
    cp -f ${RECIPE_SYSROOT}/firmware/bl1-juno.bin \
        ${D}/${UNPACK_DIR}/SOFTWARE/bl1.bin

    cp -f ${RECIPE_SYSROOT}/firmware/fip-juno.bin \
        ${D}/${UNPACK_DIR}/SOFTWARE/fip.bin

    # u-boot environment file
    cp -f ${WORKDIR}/uEnv.txt ${D}/${UNPACK_DIR}/SOFTWARE/

    # Juno images list file
    cp -f ${WORKDIR}/images-r0.txt ${D}/${UNPACK_DIR}/SITE1/HBI0262B/images.txt
    cp -f ${WORKDIR}/images-r1.txt ${D}/${UNPACK_DIR}/SITE1/HBI0262C/images.txt
    cp -f ${WORKDIR}/images-r2.txt ${D}/${UNPACK_DIR}/SITE1/HBI0262D/images.txt
}

do_deploy() {
    # To avoid dependency loop between firmware-image-juno:do_install
    # and virtual/kernel:do_deploy when INITRAMFS_IMAGE_BUNDLE = "1",
    # we need to handle the kernel binaries copying in the do_deploy
    # task.
    for f in ${KERNEL_DEVICETREE}; do
        install -m 755 -c ${DEPLOY_DIR_IMAGE}/$(basename $f) \
            ${D}/${UNPACK_DIR}/SOFTWARE/.
    done

    if [ "${INITRAMFS_IMAGE_BUNDLE}" -eq 1 ]; then
        cp -L -f ${DEPLOY_DIR_IMAGE}/Image-initramfs-juno.bin \
            ${D}/${UNPACK_DIR}/SOFTWARE/Image
    else
        cp -L -f ${DEPLOY_DIR_IMAGE}/Image ${D}/${UNPACK_DIR}/SOFTWARE/
    fi

    # Compress the files
    tar -C ${D}/${UNPACK_DIR} -zcvf ${WORKDIR}/${PN}.tar.gz ./

    # Deploy the compressed archive to the deploy folder
    install -D -p -m0644 ${WORKDIR}/${PN}.tar.gz ${DEPLOYDIR}/${PN}.tar.gz
}
do_deploy[depends] += "virtual/kernel:do_deploy"
addtask deploy after do_install
