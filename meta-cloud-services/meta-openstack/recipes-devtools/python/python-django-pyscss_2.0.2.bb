DESCRIPTION = "Makes it easier to use PySCSS in Django."
HOMEPAGE = "https://github.com/fusionbox/django-pyscss"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=07339dad721a3ae7e420b8b751a15c70"

SRC_URI[md5sum] = "f8dbcc4d314c8e220aa311ec6561b06d"
SRC_URI[sha256sum] = "0f4844f8fd3f69f4d428a616fdcf2b650a24862dd81443ae3fba14980c7b0615"

inherit setuptools3 pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        python-django \
        python-pyscss \        
        python-pathlib \
        "
