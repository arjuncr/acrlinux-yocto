DESCRIPTION = "Python library to interact with Apache HBase"
HOMEPAGE = "https://pypi.python.org/pypi/happybase/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=41f55ae3d7000e4323e84c468d8b42ee"

SRC_URI[md5sum] = "96cb7ea8163f531907fabd0dddc8b87e"
SRC_URI[sha256sum] = "850b4ee651128588a57e1e152dd1252e5ec39776a5d3d14ee892b8bac0fa9e1a"

inherit pypi

do_install_append() {
	perm_files=$(find "${D}${PYTHON_SITEPACKAGES_DIR}/" -name "top_level.txt")
	perm_files="$perm_files "$(find "${D}${PYTHON_SITEPACKAGES_DIR}/" -name "PKG-INFO")
	for f in $perm_files; do
		chmod 644 "${f}"
	done
}
