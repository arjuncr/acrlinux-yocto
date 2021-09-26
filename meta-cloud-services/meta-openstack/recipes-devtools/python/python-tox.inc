DESCRIPTION = "virtualenv-based automation of test activities"
HOMEPAGE = "http://tox.testrun.org"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f24505dfbdd8c22c61de6383f2e74bf9"

inherit pypi

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-virtualenv \
        ${PYTHON_PN}-py \
        ${PYTHON_PN}-pytest \
        "

do_install_append() {

    install -d ${D}/${sysconfdir}/${SRCNAME}
    cp ${S}/tox.ini    ${D}/${sysconfdir}/${SRCNAME}
    cp ${S}/setup.py   ${D}/${sysconfdir}/${SRCNAME}
    cp ${S}/README.md ${D}/${sysconfdir}/${SRCNAME}

    ln -s ${PYTHON_SITEPACKAGES_DIR}/tox ${D}/${sysconfdir}/${SRCNAME}/tox
}

FILES_${PN} += "${sysconfdir}/${SRCNAME}/*"
