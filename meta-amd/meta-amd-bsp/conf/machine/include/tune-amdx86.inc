DEFAULTTUNE ?= "dbfp5"

require conf/machine/include/x86/arch-x86.inc
require conf/machine/include/x86-base.inc
# AMD DB-FP5 64bit (amdx86)
TUNEVALID[dbfp5] = "Enable AMD DB-FP5 (64 bit) specific processor optimizations"
TUNECONFLICTS[dbfp5] = "m32 mx32"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "dbfp5", " -march=znver1", "", d)}"

# Extra tune selections
AVAILTUNES += "dbfp5"
TUNE_FEATURES_tune-dbfp5 = "m64 dbfp5"
BASE_LIB_tune-dbfp5 = "lib64"
TUNE_PKGARCH_tune-dbfp5 = "dbfp5"
PACKAGE_EXTRA_ARCHS_tune-dbfp5 = "${TUNE_PKGARCH_tune-dbfp5}"
