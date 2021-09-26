DESCRIPTION = "Plugin for setuptools that enables git integration"
HOMEPAGE = "https://github.com/wichert/setuptools-git"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3775480a712fc46a69647678acb234cb"

SRC_URI[md5sum] = "40b2ef7687a384ea144503c2e5bc67e2"
SRC_URI[sha256sum] = "ff64136da01aabba76ae88b050e7197918d8b2139ccbf6144e14d472b9c40445"

inherit pypi

# conflicting file prevention
do_install_append() {
	rm -f ${D}${libdir}/python*/site-packages/site.py*
}
BBCLASSEXTEND = "native"
