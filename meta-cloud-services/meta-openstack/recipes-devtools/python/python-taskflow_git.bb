DESCRIPTION = "A library to complete workflows/tasks in HA manner"
HOMEPAGE = "https://wiki.openstack.org/wiki/TaskFlow"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4476c4be31402271e101d9a4a3430d52"

SRC_URI = "\
	git://git.openstack.org/openstack/taskflow.git;branch=stable/pike \
	"

PV="2.14.1+git${SRCPV}"
SRCREV="ed867c4fd17e4102a133c313a13af37baccf14a4"
S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-six \
        python-enum34 \
        python-futurist \
        python-fasteners \
        python-networkx \
        python-contextlib2 \
        python-stevedore \
        python-futures \
        python-jsonschema \
        python-automaton \
        python-oslo.utils \
        python-oslo.serialization \
        python-tenacity \
        python-cachetools \
        python-debtcollector \
        "
	