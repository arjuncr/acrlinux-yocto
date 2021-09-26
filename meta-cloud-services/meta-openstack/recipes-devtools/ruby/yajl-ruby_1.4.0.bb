SUMMARY = "A streaming JSON parsing and encoding library for Ruby (C bindings to yajl)"
HOMEPAGE = "http://rdoc.info/projects/brianmario/yajl-ruby"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7dbd3a9f471247a243db5b62fe091587"

SRC_URI = "git://github.com/brianmario/yajl-ruby.git;protocol=https;tag=1.4.0"

S = "${WORKDIR}/git"

inherit ruby
