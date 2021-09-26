SUMMARY = "Shadow Password Module"
HOMEPAGE = "https://github.com/apalmblad/ruby-shadow"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=137882914e5269b7268f0fe8e28a3f89"

PV = "2.5.0"

SRC_URI = "git://github.com/apalmblad/ruby-shadow.git"
SRCREV = "d2e822d8a8bda61f0774debbfce363a7347ed893"
S = "${WORKDIR}/git"

inherit ruby

DEPENDS += " \
        ruby \
"

RDEPENDS_${PN} += " \
        ruby \
"

RUBY_INSTALL_GEMS = "ruby-shadow-${PV}.gem"
FILES_${PN}-dbg += "/usr/lib*/ruby/gems/*/gems/ruby-shadow-${PV}/.debug/shadow.so"
