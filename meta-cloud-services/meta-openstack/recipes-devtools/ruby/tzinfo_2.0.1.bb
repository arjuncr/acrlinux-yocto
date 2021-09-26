SUMMARY = "TZInfo - Ruby Timezone Library"
HOMEPAGE = "https://tzinfo.github.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c326926e773a4e99e89820f5d8a0966f"

SRC_URI = "git://github.com/tzinfo/tzinfo.git;protocol=https;tag=v2.0.1"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "concurrent-ruby"

inherit ruby
