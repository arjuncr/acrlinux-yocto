SUMMARY = 'Simple evented I/O for Ruby (but please check out Celluloid::IO instead)'
HOMEPAGE = 'http://coolio.github.com'

LICENSE = 'MIT'
LIC_FILES_CHKSUM = 'file://LICENSE;md5=a5e7701a63eb0a961f98cd10475129b9'

SRC_URI = 'git://github.com/tarcieri/cool.io.git;protocol=https;tag=v1.6.0'

S = '${WORKDIR}/git'

inherit ruby

