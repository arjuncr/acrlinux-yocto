DESCRIPTION = "Validating URI References per RFC 3986"
HOMEPAGE = "https://rfc3986.rtfd.org"
SECTION = "devel/python"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=03731a0e7dbcb30cecdcec77cc93ec29"

SRC_URI[md5sum] = "5b6d009806a94d418c82d48219a256de"
SRC_URI[sha256sum] = "0344d0bd428126ce554e7ca2b61787b6a28d2bbd19fc70ed2dd85efe31176405"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
