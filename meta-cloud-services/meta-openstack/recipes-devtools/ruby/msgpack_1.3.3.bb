SUMMARY = "MessagePack implementation for Ruby"
HOMEPAGE = "http://msgpack.org/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = "git://github.com/msgpack/msgpack-ruby.git;protocol=https;tag=v${PV}"

S = "${WORKDIR}/git"

inherit ruby

