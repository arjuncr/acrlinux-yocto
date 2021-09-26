FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI_append_amd = " \
     file://0001-init-install.sh-Don-t-set-quiet-kernel-option-in-ins.patch;striplevel=0;patchdir=${WORKDIR} \
     file://0002-init-install.sh-Add-a-second-prompt-to-install.patch;striplevel=0;patchdir=${WORKDIR} \
"
