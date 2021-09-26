DESCRIPTION = "Database schema migration for SQLAlchemy"
HOMEPAGE = "http://code.google.com/p/sqlalchemy-migrate/"
SECTION = "devel/python"
LICENSE = "MIT & Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=32;endline=32;md5=d41d8cd98f00b204e9800998ecf8427e"

inherit pypi

SRC_URI[md5sum] = "86572c92ae84334907f5e3a2cecc92a6"
SRC_URI[sha256sum] = "0bc02e292a040ade5e35a01d3ea744119e1309cdddb704fdb99bac13236614f8"

inherit setuptools3

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-sqlalchemy \
        python-decorator \
        python-six \
        python-sqlparse \
        python-tempita \
        "
	
