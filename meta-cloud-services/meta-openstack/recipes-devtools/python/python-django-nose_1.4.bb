#
# Copyright (C) 2014 Wind River Systems, Inc.
#
DESCRIPTION = "Django test runner using nose"
HOMEPAGE = "https://github.com/django-nose/django-nose"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f88f52f66738ec7259424ce46e855c2"

SRC_URI[md5sum] = "2713d95286ea49860458a312d2efe653"
SRC_URI[sha256sum] = "26cef3c6f62df2eee955a25195de6f793881317c0f5fd1a1c6f9e22f351a9313"

inherit setuptools3 pypi

DEPENDS += " \
        python-pip \
        "

RDEPENDS_${PN} += " \
        python-django \
        python-nose \
        "

