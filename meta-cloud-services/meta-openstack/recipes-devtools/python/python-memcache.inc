DESCRIPTION = "A comprehensive, fast, pure Python memcached client"
HOMEPAGE = "https://github.com/Pinterest/pymemcache"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=e8538d10fb74087ea2dc61033b6ebf9f"

PYPI_PACKAGE = "pymemcache"

SRC_URI[md5sum] = "215510250997423a2a57da061b1bd592"
SRC_URI[sha256sum] = "05fd71f0337384024cc3d1340d35fd0d46307cf711eac9365b0eb166812bb121"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
