DESCRIPTION = "Coordination library for distributed systems."
HOMEPAGE = "https://pypi.python.org/pypi/tooz"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "8b39e07002f21c15d2a463b63d8a2952"
SRC_URI[sha256sum] = "c1b17935207e9c4809feff91b679883928aa5bd3fce75e09b4945c261b513e60"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# RDEPENDS_default:
RDEPENDS_${PN} += " \
        python-pbr \
        python-stevedore \
        python-six \
        python-voluptuous \
        python-msgpack \
        python-fasteners \
        python-tenacity \
        python-futures \
        python-futurist \
        python-oslo.utils \
        python-oslo.serialization \
        "
