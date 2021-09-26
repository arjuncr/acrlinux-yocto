SUMMARY = "Robust enumerated type support in Python"
DESCRIPTION = "This package provides a module for robust enumerations in Python."
HOMEPAGE = "https://pypi.python.org/pypi/enum"
SECTION = "devel/python"
LICENSE = "GPLv2 | PSFv2"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=4325afd396febcb659c36b49533135d4 \
                    file://LICENSE.PSF;md5=1ad8a43fc3bbfea1585223c99f4c3e6f \
"

SRC_URI[md5sum] = "ce75c7c3c86741175a84456cc5bd531e"
SRC_URI[sha256sum] = "9bdfacf543baf2350df7613eb37f598a802f346985ca0dc1548be6494140fdff"

inherit setuptools3 pypi

DEPENDS += " \
        python-pip \
"

RDEPENDS_${PN} += " \
"
