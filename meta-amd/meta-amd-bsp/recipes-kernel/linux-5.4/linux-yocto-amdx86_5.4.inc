FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto-${LINUX_VERSION}-amdx86:"

SRC_URI_append_amdx86 += "file://amdx86-user-features.scc \
    file://amdx86-user-patches.scc \
    file://amdx86.cfg \
    file://amdx86-user-config.cfg \
    file://amdx86-extra-config.cfg \
    file://amd-xgbe.cfg \
    file://amd-ccp.cfg \
    file://kvm.cfg \
    file://afalg.cfg \
    file://disable-graphics.cfg \
    file://amdx86-standard-only.cfg \
"

do_validate_branches_append() {
    # Droping configs related to sound generating spurious warnings
    sed -i '/kconf hardware snd_hda_intel.cfg/d' ${WORKDIR}/${KMETA}/features/sound/snd_hda_intel.scc

    # Droping configs related to graphics generating spurious warnings
    sed -i '/CONFIG_DRM/d' ${WORKDIR}/${KMETA}/bsp/common-pc/common-pc-gfx.cfg
    sed -i '/kconf hardware i915.cfg/d' ${WORKDIR}/${KMETA}/features/i915/i915.scc

    # Droping configs related to vga/framebuffer-console generating spurious warnings
    sed -i '/CONFIG_FB/d' ${WORKDIR}/${KMETA}/bsp/common-pc/common-pc-gfx.cfg
    sed -i '/CONFIG_FRAMEBUFFER_CONSOLE/d' ${WORKDIR}/${KMETA}/bsp/common-pc/common-pc-gfx.cfg
    sed -i '/CONFIG_FB/d' ${WORKDIR}/${KMETA}/cfg/efi-ext.cfg
    sed -i '/CONFIG_FRAMEBUFFER_CONSOLE/d' ${WORKDIR}/${KMETA}/cfg/efi-ext.cfg

    # Drop configs limiting the max number of cpus - we use our own setting in amdx86-user-config.cfg
    sed -i '/CONFIG_NR_CPUS/d' ${WORKDIR}/${KMETA}/cfg/smp.cfg
    sed -i '/CONFIG_NR_CPUS/d' ${WORKDIR}/${KMETA}/cfg/smp_64.cfg
}
