DESCRIPTION = "Angular Fileupload packaged for setuptools (easy_install) / pip."
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Angular-FileUpload"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=e634b82c14383ecefd736caa40ed2222"

PYPI_PACKAGE = "XStatic-Angular-FileUpload"

SRC_URI[md5sum] = "1cf48c0204783da2f71efe79039a8468"
SRC_URI[sha256sum] = "68e66efc4f2ed81438553a54646d5cc67487b05764c0003ff25ae5beb8dae21f"

inherit pypi

# DEPENDS_default: python-pip

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "

# RDEPENDS_default: 
RDEPENDS_${PN} += " \
        "
