DESCRIPTION = "POSIX IPC primitives (semaphores, shared memory and message queues) for Python"
HOMEPAGE = "http://semanchuk.com/philip/posix_ipc/"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc089fb2f37e90799a54c19a20c9880f"

PYPI_PACKAGE = "posix_ipc"

# Archived version so we need to overwrite what the pypi class will derive
PYPI_SRC_URI = "https://pypi.python.org/packages/source/p/${PYPI_PACKAGE}/${PYPI_PACKAGE}-${PV}.tar.gz"

SRC_URI[md5sum] = "c4fd0065244ccb2f59cb8ceedf287cf6"
SRC_URI[sha256sum] = "ff6c9077633fc62a491d6997c43b094d885bb45a7ca1f36c9a0d647c54b74b14"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
