DESCRIPTION = "An unladen web framework for building APIs and app backends."
HOMEPAGE = "http://projects.unbit.it/uwsgi/"
SECTION = "net"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33ab1ce13e2312dddfad07f97f66321f"

SRCNAME = "uwsgi"
SRC_URI = "git://github.com/unbit/uwsgi.git;branch=uwsgi-2.0 \
    file://Add-explicit-breaks-to-avoid-implicit-passthrough.patch \
    file://more-Add-explicit-breaks-to-avoid-implicit-passthrough.patch \
"

SRCREV="af44211739136e22471a2897383f34586284bf86"
PV="2.0.14+git${SRCPV}"
S = "${WORKDIR}/git"

inherit setuptools3 pkgconfig

# prevent host contamination and remove local search paths
export UWSGI_REMOVE_INCLUDES = "/usr/include,/usr/local/include"

DEPENDS += " \
        e2fsprogs \
        python3-pip \
        python3-six \
        yajl \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "

CLEANBROKEN = "1"

