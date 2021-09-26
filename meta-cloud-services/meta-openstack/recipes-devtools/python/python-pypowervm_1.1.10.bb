DESCRIPTION = "Python binding for the PowerVM REST API"
HOMEPAGE = "https://pypi.python.org/pypi/pypowervm"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "54e4dfaa569350254b0a0f4c19686ded"
SRC_URI[sha256sum] = "ab27d4efb59105555b6c9b1fe7792fd895ad9ca893058c39bba4106d6a0ad986"

inherit setuptools3 pypi

RDEPENDS_${PN} += " \
        python-lxml \
        python-oslo.concurrency \
        python-oslo.context \
        python-oslo.i18n \
        python-oslo.log \
        python-oslo.utils \
        python-pbr \
        python-pyasn1-modules \
        python-pyasn1 \
        python-pytz \
        python-requests \
        python-six \
        python-futures \
        python-taskflow \
        python-networkx \
        "
