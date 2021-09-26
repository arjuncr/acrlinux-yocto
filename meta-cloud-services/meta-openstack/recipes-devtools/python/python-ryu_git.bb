DESCRIPTION = "Ryu component-based software defined networking framework"
HOMEPAGE = "http://osrg.github.io/ryu/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PV = "4.19+git${SRCPV}"
SRCREV = "51a1130f6cdcb029a51b6a75d43ac5e4cdde7072"

SRCNAME = "ryu"
SRC_URI = "git://github.com/osrg/${SRCNAME}.git"

S = "${WORKDIR}/git"

inherit setuptools3

FILES_${PN} += "${datadir}/etc/${SRCNAME}/*"

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-eventlet \
        python-msgpack \
        python-netaddr \
        python-oslo.config \
        python-ovs \
        python-routes \
        python-six \
        python-tinyrpc \
        python-webob \
        "
