SUMMARY = "TZInfo::Data - Timezone Data for TZInfo"
HOMEPAGE = "https://tzinfo.github.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c326926e773a4e99e89820f5d8a0966f"

SRC_URI = "git://github.com/tzinfo/tzinfo-data.git;protocol=https;tag=v1.2019.3"

S = "${WORKDIR}/git"

inherit ruby
