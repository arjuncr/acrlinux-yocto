require linux-yocto-common_5.4.inc

KBRANCH_amd ?= "v5.4/standard/base"
SRCREV_machine_amd ?= "cfcdd63145c0d741e57ee3e3e58f794229c6c09c"

SRC_URI_append_amdgpu += "file://amdgpu-patches.scc \
			  file://misc-patches.scc"

include linux-yocto-${MACHINE}_5.4.inc
