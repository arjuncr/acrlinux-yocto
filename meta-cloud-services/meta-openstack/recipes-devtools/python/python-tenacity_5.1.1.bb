DESCRIPTION = "Retry code until it succeeeds"
HOMEPAGE = "https://github.com/jd/tenacity"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

inherit pypi

SRC_URI[md5sum] = "c960e3f0c66207c85bbae72a8232278b"
SRC_URI[sha256sum] = "a4eb168dbf55ed2cae27e7c6b2bd48ab54dabaf294177d998330cf59f294c112"

inherit setuptools3

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
 	 python-setuptools3-scm-native \
        "

RDEPENDS_${PN} += " \
        python-six \
        python-futures \
        python-monotonic \
        "
