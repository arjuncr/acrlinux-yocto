LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

COMPATIBLE_MACHINE = "ti-soc"

PACKAGE_ARCH = "${MACHINE_ARCH}"

INHIBIT_DEFAULT_DEPS = "1"

inherit nopackages deploy

PLAT_SFX = ""
PLAT_SFX_j7 = "/j721e"
PLAT_SFX_j7200-evm = "/j7200"
PLAT_SFX_am65xx = "/am65xx"
PLAT_SFX_am64xx = "/am64x"

# Use weak assignment to set defaults to TI_RTOS_METADATA_* variables
TI_RTOS_METADATA_URI ?= "git://git.ti.com/processor-sdk/coresdk_rtos_releases.git"
TI_RTOS_METADATA_PROTOCOL ?= "git"
TI_RTOS_METADATA_SRCREV ?= "2ecbf45af64bc47806623cc5bf7ab493489acaf9"
TI_RTOS_METADATA_BRANCH ?= "master"
TI_RTOS_METADATA_DIR ?= "${PLAT_SFX}"
TI_RTOS_METADATA_FILE ?= "${S}${TI_RTOS_METADATA_DIR}/metadata.inc"
TI_RTOS_METADATA_FILE_am64xx ?= "${S}${TI_RTOS_METADATA_DIR}/mcusdk_metadata.inc"

PV = "1.0.0+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI = "${TI_RTOS_METADATA_URI};branch=${TI_RTOS_METADATA_BRANCH};protocol=${TI_RTOS_METADATA_PROTOCOL}"
SRCREV = "${TI_RTOS_METADATA_SRCREV}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

do_deploy () {
	:
}

do_deploy_k3 () {
	install -d ${DEPLOYDIR}
	install -m 0644 ${TI_RTOS_METADATA_FILE} ${DEPLOYDIR}/
}

addtask deploy before do_build after do_compile
