DESCRIPTION = "OpenStack test framework and test fixtures. \
The oslotest package can be cross-tested against its consuming projects to ensure \
that no changes to the library break the tests in those other projects."
HOMEPAGE = "https://pypi.python.org/pypi/oslotest"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

PV = "2.17.0+git${SRCPV}"
SRCREV = "aea2b5cfd6442195f7ee479e21664631825af924"

SRCNAME = "oslotest"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "\
    python-pbr \
    "

RDEPENDS_${PN} = "python-fixtures \
                  python-subunit \
                  python-six \
                  python-testrepository \
                  python-testtools \
                  python-mock \
                  python-mox3 \
                  python-os-client-config \
                  python-debtcollector \
                  bash \
"
	