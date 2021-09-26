DESCRIPTION = "Library to enforce positional or key-word arguments"
HOMEPAGE = "https://github.com/morganfainberg/positional"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5335066555b14d832335aa4660d6c376"

SRC_URI[md5sum] = "4afcffd8e2ba733fd7a50f137a2ee893"
SRC_URI[sha256sum] = "cf48ea169f6c39486d5efa0ce7126a97bed979a52af6261cf255a41f9a74453a"

inherit pypi

CLEANBROKEN = "1"

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        ${PYTHON_PN}-pbr-native \
        "

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-wrapt \
"
