SUMMARY = "Puppet Labs Standard Library module"
HOMEPAGE = "https://github.com/puppetlabs/puppetlabs-stdlib"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38a048b9d82e713d4e1b2573e370a756"

PV = "4.10.0"
SRCREV = "0b4822be3d2242e83c28ab7fed6c5817adc322d5"

SRC_URI = " \
    git://github.com/puppetlabs/puppetlabs-stdlib.git;branch=master \
    file://Add-gemspec.patch \
"

inherit ruby

S="${WORKDIR}/git"

DEPENDS += " \
        ruby \
        facter \
"

RDEPENDS_${PN} += " \
        ruby \
        facter \
        puppet \
"

RUBY_INSTALL_GEMS = "puppetlabs-stdlib-${PV}.gem"
