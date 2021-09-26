DESCRIPTION = "A helper class for handling configuration defaults of packaged apps gracefully."
HOMEPAGE = "http://django-appconf.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f34b9b2f6413fd5f91869fa7e992457"

SRC_URI[md5sum] = "29c87a00f0d098b90f3ac6113ae6e52d"
SRC_URI[sha256sum] = "ba1375fb1024e8e91547504d4392321795c989fde500b96ebc7c93884f786e60"

inherit pypi

#RDEPENDS_${PN} += "${PYTHON_PN}-six \
#	"
