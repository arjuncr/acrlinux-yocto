SUMMARY = "Client library for accessing the Plus, Moderator, and many other Google APIs."
AUTHOR = "Google Inc."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94023d14f6b58272fd885e4e3f2f08b3"

inherit setuptools3 pypi

SRC_URI[md5sum] = "3059dce9c0308852e177c7d99d3f9ac2"
SRC_URI[sha256sum] = "ec72991f95201996a4edcea44a079cae0292798086beaadb054d91921632fe1b"

RDEPENDS_${PN} += " \
        python-httplib2 \
        python-oauth2client \
        python-six \
        python-uritemplate \
        "
