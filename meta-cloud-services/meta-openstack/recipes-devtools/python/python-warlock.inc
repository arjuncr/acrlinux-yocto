DESCRIPTION = "Build self-validating python objects using JSON schemas"
HOMEPAGE = "http://github.com/bcwaldon/warlock"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "python-jsonschema python-jsonpatch"

SRC_URI[md5sum] = "746aba989d97762948e5fca6601f283f"
SRC_URI[sha256sum] = "a093c4d04b42b7907f69086e476a766b7639dca50d95edc83aef6aeab9db2090"

inherit pypi

RDEPENDS_${PN} += "${PYTHON_PN}-jsonschema \
                   ${PYTHON_PN}-jsonpatch  \
                  "

