SUMMARY = "Facter gathers basic facts about nodes (systems)"
HOMEPAGE = "http://puppetlabs.com/facter"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ce69a88023d6f6ab282865ddef9f1e41"

SRC_URI = " \
    http://downloads.puppetlabs.com/facter/facter-${PV}.tar.gz \
    file://add_facter_gemspec.patch \
"
SRC_URI[md5sum] = "58b6b609f19d1c146c600c4dc6e7fa39"
SRC_URI[sha256sum] = "47ccbfb8a69e4d48c3c88e47ac1ae754fcc583d4090fa9d838461b3ede7b07cb"

inherit ruby

DEPENDS += " \
        ruby \
"

RUBY_INSTALL_GEMS = "facter-${PV}.gem"
