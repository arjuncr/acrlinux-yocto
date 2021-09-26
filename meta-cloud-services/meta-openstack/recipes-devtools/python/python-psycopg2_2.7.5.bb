DESCRIPTION = "Python-PostgreSQL Database Adapter"
HOMEPAGE = "http://initd.org/psycopg/"
SECTION = "devel/python"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=72bded22a37845c7d9dc3fd39d699a2d"
DEPENDS = "postgresql"

SRC_URI += " \
           file://remove-pg-config.patch \
          "

SRC_URI[md5sum] = "9e7d6f695fc7f8d1c42a7905449246c9"
SRC_URI[sha256sum] = "eccf962d41ca46e6326b97c8fe0a6687b58dfc1a5f6540ed071ff1474cea749e"

inherit distutils3 pypi

DEPENDS += " \
    postgresql \
"
