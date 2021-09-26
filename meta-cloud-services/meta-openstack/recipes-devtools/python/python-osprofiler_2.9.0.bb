DESCRIPTION = "OpenStack Profiler Library"
HOMEPAGE = "http://www.openstack.org/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19cbd64715b51267a47bf3750cc6a8a5"

SRC_URI[md5sum] = "75f190b00ac248b3d547b67a480801db"
SRC_URI[sha256sum] = "6915cbf8ac390dc3e125340b0ff368dd53f5889e3d053f9122dd83ba0dd366f7"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        python-pbr \
        python-six \
        python-oslo.messaging \
        python-oslo.log \
        python-oslo.utils \
        python-webob \
        python-requests \
        python-netaddr \
        python-oslo.concurrency \
        "
