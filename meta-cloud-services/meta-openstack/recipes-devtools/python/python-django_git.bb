DESCRIPTION = "A high-level Python Web framework"
HOMEPAGE = "http://www.djangoproject.com/"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

SRCNAME = "Django"

PV = "1.8.6"
SRCREV = "80b7e9d09f2d23209b591288f9b2cf3eb3d927c8"

SRC_URI = " \
    git://github.com/django/django.git;branch=stable/1.8.x \
    "

S = "${WORKDIR}/git"

inherit setuptools3

FILES_${PN} += "${datadir}/django/*"

