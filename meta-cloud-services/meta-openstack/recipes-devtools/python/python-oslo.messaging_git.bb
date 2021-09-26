DESCRIPTION = "Oslo Messaging API"
HOMEPAGE = "https://launchpad.net/oslo"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c46f31914956e4579f9b488e71415ac8"

SRCNAME = "oslo.messaging"
SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike"

PV = "5.30.1+git${SRCPV}"
SRCREV = "a07d852b237d229a0f4dd55fd83379c0581e44e9"
S = "${WORKDIR}/git"

inherit setuptools3

# DEPENDS_default: python-pip

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        bash \
        python-pbr \
        python-cachetools \
        python-futurist \
        python-oslo.log \
        python-oslo.utils \
        python-oslo.serialization \
        python-oslo.middleware \
        python-oslo.service \
        python-oslo.i18n \
        python-stevedore \
        python-debtcollector \
        python-monotonic \
        python-six \
        python-webob \
        python-pyyaml \
        python-amqp \
        python-kombu \
        python-pika \
        python-pika-pool \
        python-futures \
        python-tenacity \
        "
