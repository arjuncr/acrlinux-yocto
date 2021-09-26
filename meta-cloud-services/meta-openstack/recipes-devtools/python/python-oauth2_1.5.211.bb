DESCRIPTION = "library for OAuth"
HOMEPAGE = "https://pypi.python.org/pypi/oauth2"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=15c871e114b5cb956dacad28f4db57c7"

SRC_URI[md5sum] = "987ad7365a70e2286bd1cebb344debbc"
SRC_URI[sha256sum] = "82a38f674da1fa496c0fc4df714cbb058540bed72a30c50a2e344b0d984c4d21"

inherit setuptools3 pypi

RDEPENDS_${PN} += "python-prettytable \
            python-cmd2 \
            python-pyparsing \
            python-mccabe \
            python-pep8 \
            python-pyflakes"


do_install_append() {
	perm_files=$(find "${D}${PYTHON_SITEPACKAGES_DIR}/" -name "PKG-INFO")
	for f in $perm_files; do
		chmod 644 "${f}"
	done
}
