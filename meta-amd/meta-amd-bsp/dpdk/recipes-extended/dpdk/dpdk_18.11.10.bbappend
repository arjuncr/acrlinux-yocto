DEPENDS += "openssl"

# takes n or y
BUILD_SHARED = "n"
do_configure_prepend () {
	# enable the AMD CCP driver
	sed -e "s#CONFIG_RTE_LIBRTE_PMD_CCP=n#CONFIG_RTE_LIBRTE_PMD_CCP=y#" -i ${S}/config/common_base
	sed -e "s#CONFIG_RTE_LIBRTE_PMD_CCP_CPU_AUTH=n#CONFIG_RTE_LIBRTE_PMD_CCP_CPU_AUTH=y#" -i ${S}/config/common_base

	# shared libs are a more convenient way for development but then the user
	# has to load the PMD explicitly with the -d flag so be careful
	sed -e "s#CONFIG_RTE_BUILD_SHARED_LIB=n#CONFIG_RTE_BUILD_SHARED_LIB=${BUILD_SHARED}#" -i ${S}/config/common_base
}

COMPATIBLE_MACHINE_amdx86 = "amdx86"
DPDK_TARGET_MACHINE_amdx86 = "znver1"
TUNE_FEATURES += "m64"
