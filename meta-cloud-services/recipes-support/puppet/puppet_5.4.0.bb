SUMMARY = "Open source Puppet is a configuration management system"
HOMEPAGE = "https://puppetlabs.com/puppet/puppet-open-source"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c9045ec00cc0d6b6e0e09ee811da4a0"

SRC_URI = " \
    https://downloads.puppetlabs.com/puppet/puppet-${PV}.tar.gz \
    file://add_puppet_gemspec.patch \
    file://puppet.conf \
    file://puppet.init \
    file://puppet.service \
"
SRC_URI[md5sum] = "e26702fbfb464121d8d60e639ea254d9"
SRC_URI[sha256sum] = "8db3a89c9ced01b43c57f89e42d099a763d02f38bcea5d6c73e1245556932bb2"

inherit ruby update-rc.d systemd

DEPENDS += " \
        ruby \
        facter \
"

RDEPENDS_${PN} += " \
        ruby \
        facter \
        ruby-shadow \
        bash \
"

RUBY_INSTALL_GEMS = "puppet-${PV}.gem"

INITSCRIPT_NAME = "${BPN}"
INITSCRIPT_PARAMS = "start 02 5 3 2 . stop 20 0 1 6 ."

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${BPN}.service"

do_install_append() {
    install -d ${D}${sysconfdir}/puppet
    install -d ${D}${sysconfdir}/puppet/manifests
    install -d ${D}${sysconfdir}/puppet/modules

    install -m 655 ${S}/conf/auth.conf ${D}${sysconfdir}/puppet/
    install -m 655 ${S}/conf/fileserver.conf ${D}${sysconfdir}/puppet/
    install -m 655 ${S}/conf/environment.conf ${D}${sysconfdir}/puppet/
    install -m 655 ${WORKDIR}/puppet.conf ${D}${sysconfdir}/puppet/

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/puppet.service ${D}${systemd_unitdir}/system

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/puppet.init ${D}${sysconfdir}/init.d/puppet
}
