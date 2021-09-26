DESCRIPTION = "Tools for using Babel with Django"
HOMEPAGE = "https://github.com/python-babel/django-babel"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=5ae97ab65116b8d7890c59de57577b46"

SRCNAME = "django-babel"

PV = "0.5.1+git${SRCPV}"
SRCREV = "88b389381c0e269605311ae07029555b65a86bc5"

SRC_URI = "git://github.com/python-babel/${SRCNAME}.git \
          "

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += " \
        python-pip \
        "

RDEPENDS_${PN} += " \
        python-django \
        python-babel \
        "

