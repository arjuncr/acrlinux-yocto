SUMMARY = "Extra packages that improve the usability of compute/control nodes"
PR = "r0"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup

RDEPENDS_${PN} = " \
    vim \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xterm', '', d)} \
    "

IMAGE_FEATURES += "package-management"


