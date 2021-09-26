SUMMARY = "pools for your pikas"
HOMEPAGE = "https://github.com/bninja/pika-pool"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRC_URI[md5sum] = "0a3897e991aa3da948e03660313c1980"
SRC_URI[sha256sum] = "f3985888cc2788cdbd293a68a8b5702a9c955db6f7b8b551aeac91e7f32da397"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pika \
    "
