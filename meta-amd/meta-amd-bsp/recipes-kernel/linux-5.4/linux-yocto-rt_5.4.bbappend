require linux-yocto-common_5.4.inc

KBRANCH_amd ?= "v5.4/standard/preempt-rt/base"
SRCREV_machine_amd ?= "3a5f7e9a874f0a6e9ad599b4fc6c491db231dd6f"

include linux-yocto-${MACHINE}_5.4.inc
