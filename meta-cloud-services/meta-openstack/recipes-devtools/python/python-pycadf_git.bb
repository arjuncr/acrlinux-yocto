DESCRIPTION = "CADF Library"
HOMEPAGE = "https://launchpad.net/pycadf"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c46f31914956e4579f9b488e71415ac8"

PV = "2.10.0+git${SRCPV}"
SRCREV = "d113c1564d49d93451d9330955e8a42b7db04149"

SRCNAME = "pycadf"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git"

S = "${WORKDIR}/git"

inherit setuptools3

FILES_${PN} += "${datadir}/etc/${SRCNAME}/*"

DEPENDS += " \
        python-pip \
        python-pbr \
        "
# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
      python-babel \
      python-iso8601 \
      python-netaddr \
      python-posix-ipc \
      python-pytz \
      python-six \
      python-webob \
      python-pbr \
        "
