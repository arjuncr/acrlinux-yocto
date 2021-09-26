SUMMARY = "Python client for Apache Kafka"
HOMEPAGE = "https://github.com/mumrah/kafka-python"
SECTION = "devel/python"
LICENSE = "Apache-2.0"

PV = "1.4.7+git${SRCPV}"
SRCREV = "0552b04326c73be29f209c12920ef4cbaceb9818"

SRCNAME = "kafka-python"

LIC_FILES_CHKSUM = "file://LICENSE;md5=22efebb1e053dcc946f4b9d17f3cbbea"
SRC_URI = "git://github.com/mumrah/${SRCNAME}.git"

S = "${WORKDIR}/git"

DEPENDS += " \
	${PYTHON_PN}-pbr \
	"

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-six \
	"

