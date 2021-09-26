DESCRIPTION = "A tool for discovering and configuring services in your infrastructure"
HOMEPAGE = "https://www.consul.io/"
LICENSE = "MPL-2.0 & BSD & Apache-2.0 & BSD-2-Clause & MIT"
LIC_FILES_CHKSUM = "file://src/github.com/hashicorp/consul/LICENSE;md5=b278a92d2c1509760384428817710378"

GO_IMPORT = "github.com/hashicorp/consul"
SRCREV = "944cc71026c007e7de9467ec3f38f0ad14464fcc"
SRC_URI = "git://${GO_IMPORT}.git \
           file://0001-Migration-Rename-imports-throughout.patch \
           file://consul.service \
          "

S = "${WORKDIR}/git"

RDEPENDS_${PN}-dev_append = " bash"

# Temporarily workaround an issue with GO 1.12.1
# http://www.lyddzz.com/github_/golang/go/issues/30768
# Disable experimental use of '-linkshared'.
GO_LINKSHARED = ""

inherit systemd go

# Besides the hosts specified in goarch, we do not build for mips.
#
COMPATIBLE_HOST_mipsarch = "null"

SYSTEMD_SERVICE_${PN} = "consul.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

do_install_append() {
    install -d ${D}/${systemd_unitdir}/system
    cp ${WORKDIR}/consul.service ${D}/${systemd_unitdir}/system
}

FILES_${PN} += "${systemd_unitdir}/system"

#for i586, executable consul contains textrel
INSANE_SKIP_${PN} += "textrel"
