SUMMARY = "simple callback-based HTTP request/response parser"
HOMEPAGE = "https://rubygems.org/gems/http_parser.rb"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=157efc3766c6d07d3d857ebbab43351a"

SRC_URI = "git://github.com/tmm1/http_parser.rb.git;protocol=https;tag=v0.6.0"

S = "${WORKDIR}/git"

# Bitbake doesn't allow the underscore in file name, hence the dash
SRCNAME = "http_parser.rb"

DEPENDS = "git"

inherit ruby

# Download the submodules
do_configure_prepend() {
	cd ${WORKDIR}/git
	git submodule update --init --recursive
}
