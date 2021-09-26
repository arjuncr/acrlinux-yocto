
add_releaseinfo[vardepsexclude] += "DATETIME"

add_releaseinfo () {
    echo "image: ${PN}" > ${IMAGE_ROOTFS}/${sysconfdir}/release
    echo "machine: ${MACHINE}" >> ${IMAGE_ROOTFS}/${sysconfdir}/release
    echo "version: ${DATETIME}" >> ${IMAGE_ROOTFS}/${sysconfdir}/release
}

ROOTFS_POSTPROCESS_COMMAND += " add_releaseinfo ; "
