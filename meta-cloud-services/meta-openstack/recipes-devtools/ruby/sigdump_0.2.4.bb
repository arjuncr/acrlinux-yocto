SUMMARY = "Use signal to show stacktrace of a Ruby process without restarting it"
HOMEPAGE = "https://github.com/frsyuki/sigdump"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=07d6218b18fb6826f04fd32b4918f085"

SRC_URI = "git://github.com/frsyuki/sigdump.git;protocol=https;tag=v0.2.4"

S = "${WORKDIR}/git"

inherit ruby
