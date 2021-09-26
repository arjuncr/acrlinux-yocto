SUMMARY = 'A framework to implement robust multiprocess servers like Unicorn'
HOMEPAGE = 'https://rubygems.org/gems/serverengine'

LICENSE = 'Apache-2.0'
LIC_FILES_CHKSUM = 'file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57'

SRC_URI = 'git://github.com/treasure-data/serverengine.git;protocol=https;tag=v2.2.1'

S = '${WORKDIR}/git'

RDEPENDS_${PN} = "sigdump"

inherit ruby
