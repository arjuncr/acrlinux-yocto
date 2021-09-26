SUMMARY = "Puppet provider for virtual switches."
HOMEPAGE = "https://github.com/openstack/puppet-vswitch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

PV = "3.0.0"
SRCREV = "c374840910c823f7669cf2e1229c7df7192ae880"

SRC_URI = " \
    git://github.com/openstack/puppet-vswitch.git;branch=master \
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

RUBY_INSTALL_GEMS = "puppet-vswitch-${PV}.gem"

do_install_append() {
}
