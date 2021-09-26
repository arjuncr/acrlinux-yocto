do_compile_append_rock2-square () {
	# copy to default search path
	if [ "${SPL_BINARY}" = "u-boot-spl-dtb.bin" ]; then
		cp ${B}/spl/${SPL_BINARY} ${B}
	fi
}

ATF_DEPENDS ??= ""

EXTRA_OEMAKE_append_rk3399 = " BL31=${DEPLOY_DIR_IMAGE}/bl31.elf"
ATF_DEPENDS_rk3399 = "virtual/atf:do_deploy"

do_compile[depends] += "${ATF_DEPENDS}"

