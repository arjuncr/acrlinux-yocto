DESCRIPTION = "Import exceptions from potentially bundled packages in requests."
HOMEPAGE = "https://www.openstack.org/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d273d63619c9aeaf15cdaf76422c4f87"

SRC_URI[md5sum] = "f43b246ccd7d5b618e6f0dc946a6c3f3"
SRC_URI[sha256sum] = "b095cbc77618f066d459a02b137b020c37da9f46d9b057704019c9f77dba3065"

inherit pypi

# conflicting file prevention
do_install_append() {
	rm -f ${D}${libdir}/python*/site-packages/tests/*
}

DEPENDS += " \
        ${PYTHON_PN}-pbr-native \
        "

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-pbr \
        "
