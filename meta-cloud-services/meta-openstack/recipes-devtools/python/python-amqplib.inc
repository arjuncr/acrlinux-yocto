DESCRIPTION = "Python client for the Advanced Message Queuing Procotol (AMQP)"
HOMEPAGE = "http://code.google.com/p/py-amqplib/"
SECTION = "devel/python"
LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1702a92c723f09e3fab3583b165a8d90"

PR = "r1"
SRCNAME = "amqplib"

SRC_URI = "https://github.com/barryp/py-amqplib/archive/${PV}.tar.gz"

SRC_URI[md5sum] = "ff405a7b57aa98cdf07d02bd8090fa29"
SRC_URI[sha256sum] = "9461d73f75ca704e6b656738b5dad42a1948e61dfbc8233077d84d00f74c3936"

S = "${WORKDIR}/py-amqplib-${PV}"

