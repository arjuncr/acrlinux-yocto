HOMEPAGE = "http://saltstack.com/"
SECTION = "admin"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS = "\
           python3-msgpack \
           python3-pyyaml \
           python3-jinja2 \
           python3-markupsafe \
           python3-dateutil \
           python3-pycrypto \
           python3-pytest-salt \
           python3-pyzmq \
           python3-requests \
"

PACKAGECONFIG ??= "zeromq"
PACKAGECONFIG[zeromq] = ",,python3-pyzmq python3-pycrypto,"
PACKAGECONFIG[tcp] = ",,python3-pycrypto"

SRC_URI = "https://files.pythonhosted.org/packages/source/s/${PN}/${PN}-${PV}.tar.gz \
           file://set_python_location_hashbang.patch \
"

SRC_URI[md5sum] = "8084ef6f2a275b627ae364b9d562b4ff"
SRC_URI[sha256sum] = "04fbc64933b375cbbefc9576bcc65167b74d5eec7f58e64d096d67529ea66500"


SYSTEMD_AUTO_ENABLE_${PN}-master = "disable"
SYSTEMD_AUTO_ENABLE_${PN}-minion = "disable"
SYSTEMD_AUTO_ENABLE_${PN}-api = "disable"

S = "${WORKDIR}/${PN}-${PV}"

inherit setuptools3 systemd

# Avoid a QA Warning triggered by the test package including a file
# with a .a extension
INSANE_SKIP_${PN}-tests += "staticdev"

RDEPENDS_${PN} += "${PN}-api \
                   ${PN}-common \
                   ${PN}-master \
                   ${PN}-minion \
                   ${PN}-bash-completion \
"

# Note ${PN}-tests must be before ${PN}-common in the PACKAGES variable
# in order for ${PN}-tests to own the correct FILES.
PACKAGES += "\
           ${PN}-api \
           ${PN}-common \
           ${PN}-master \
           ${PN}-minion \
           ${PN}-cloud \
           ${PN}-syndic \
           ${PN}-ssh \
           ${PN}-bash-completion \
           ${PN}-zsh-completion \
"

do_install_append() {
        install -Dm644 ${S}/pkg/salt-common.logrotate ${D}${sysconfdir}/logrotate.d/${PN}
        install -Dm644 ${S}/pkg/salt.bash ${D}${datadir}/bash-completion/completions/${PN}
        install -Dm644 ${S}/pkg/zsh_completion.zsh ${D}${datadir}/zsh/site-functions/_${PN}

        # default config
        install -Dm644 ${S}/conf/minion ${D}${sysconfdir}/${PN}/minion
        install -Dm644 ${S}/conf/minion ${D}${sysconfdir}/${PN}/master

        # systemd services
        for _svc in salt-master.service salt-syndic.service salt-minion.service salt-api.service; do
            install -Dm644 ${S}/pkg/$_svc "${D}${systemd_system_unitdir}/$_svc"
        done
}

ALLOW_EMPTY_${PN} = "1"
FILES_${PN} = ""
FILES_${PN} += "${systemd_system_unitdir} ${systemd_system_unitdir}/* /etc/salt/master.d /etc/salt/master.d/preseed_key.py"

DESCRIPTION_COMMON = "salt is a powerful remote execution manager that can be used to administer servers in a\
 fast and efficient way. It allows commands to be executed across large groups of servers. This means systems\
 can be easily managed, but data can also be easily gathered. Quick introspection into running systems becomes\
 a reality. Remote execution is usually used to set up a certain state on a remote system. Salt addresses this\
 problem as well, the salt state system uses salt state files to define the state a server needs to be in. \
Between the remote execution system, and state management Salt addresses the backbone of cloud and data center\
 management."

SUMMARY_${PN}-minion = "client package for salt, the distributed remote execution system"
DESCRIPTION_${PN}-minion = "${DESCRIPTION_COMMON} This particular package provides the worker agent for salt."
RDEPENDS_${PN}-minion = "${PN}-common (= ${EXTENDPKGV}) python3-msgpack"
RDEPENDS_${PN}-minion += "${@bb.utils.contains('PACKAGECONFIG', 'zeromq', 'python3-pycrypto python3-pyzmq (>= 13.1.0)', '',d)}"
RDEPENDS_${PN}-minion += "${@bb.utils.contains('PACKAGECONFIG', 'tcp', 'python3-pycrypto', '',d)}"
RRECOMMENDS_${PN}-minion_append_x64 = "dmidecode"
RSUGGESTS_${PN}-minion = "python3-augeas"
CONFFILES_${PN}-minion = "${sysconfdir}/${PN}/minion"
FILES_${PN}-minion = "${bindir}/${PN}-minion ${sysconfdir}/${PN}/minion.d/ ${CONFFILES_${PN}-minion} ${bindir}/${PN}-proxy ${systemd_system_unitdir}/salt-minion.service"

SUMMARY_${PN}-common = "shared libraries that salt requires for all packages"
DESCRIPTION_${PN}-common ="${DESCRIPTION_COMMON} This particular package provides shared libraries that \
salt-master, salt-minion, and salt-syndic require to function."
RDEPENDS_${PN}-common = "python3-dateutil python3-jinja2 python3-pyyaml python3-requests (>= 1.0.0)"
RRECOMMENDS_${PN}-common = "lsb"
RSUGGESTS_${PN}-common = "python3-mako python3-git"
RCONFLICTS_${PN}-common = "python3-mako (< 0.7.0)"
CONFFILES_${PN}-common="${sysconfdir}/logrotate.d/${PN}"
FILES_${PN}-common = "${bindir}/${PN}-call ${PYTHON_SITEPACKAGES_DIR}/* ${CONFFILES_${PN}-common}"

SUMMARY_${PN}-ssh = "remote manager to administer servers via salt"
DESCRIPTION_${PN}-ssh = "${DESCRIPTION_COMMON} This particular package provides the salt ssh controller. It \
is able to run salt modules and states on remote hosts via ssh. No minion or other salt specific software needs\
 to be installed on the remote host."
RDEPENDS_${PN}-ssh = "${PN}-common (= ${EXTENDPKGV}) python3-msgpack"
CONFFILES_${PN}-ssh="${sysconfdir}/${PN}/roster"
FILES_${PN}-ssh = "${bindir}/${PN}-ssh ${CONFFILES_${PN}-ssh}"

SUMMARY_${PN}-api = "generic, modular network access system"
DESCRIPTION_${PN}-api = "a modular interface on top of Salt that can provide a variety of entry points into a \
running Salt system. It can start and manage multiple interfaces allowing a REST API to coexist with XMLRPC or \
even a Websocket API. The Salt API system is used to expose the fundamental aspects of Salt control to external\
 sources. salt-api acts as the bridge between Salt itself and REST, Websockets, etc. Documentation is available\
 on Read the Docs: http://salt-api.readthedocs.org/"
RDEPENDS_${PN}-api = "${PN}-master"
RSUGGESTS_${PN}-api = "python3-cherrypy"
CONFFILES_${PN}-api = "${sysconfdir}/init.d/${PN}-api"
FILES_${PN}-api = "${bindir}/${PN}-api ${CONFFILES_${PN}-api} ${systemd_system_unitdir}/${PN}-api.service"


SUMMARY_${PN}-master = "remote manager to administer servers via salt"
DESCRIPTION_${PN}-master ="${DESCRIPTION_COMMON} This particular package provides the salt controller."
RDEPENDS_${PN}-master = "${PN}-common (= ${EXTENDPKGV}) python3-msgpack python3-distro"
RDEPENDS_${PN}-master += "${@bb.utils.contains('PACKAGECONFIG', 'zeromq', 'python3-pycrypto python3-pyzmq (>= 13.1.0)', '',d)}"
RDEPENDS_${PN}-master += "${@bb.utils.contains('PACKAGECONFIG', 'tcp', 'python3-pycrypto', '',d)}"
CONFFILES_${PN}-master="${sysconfdir}/init.d/${PN}-master  ${sysconfdir}/${PN}/master"
RSUGGESTS_${PN}-master = "python3-git"
FILES_${PN}-master = "${bindir}/${PN} ${bindir}/${PN}-cp ${bindir}/${PN}-key ${bindir}/${PN}-master ${bindir}/${PN}-run ${bindir}/${PN}-unity ${bindir}/spm ${CONFFILES_${PN}-master} ${systemd_system_unitdir}/${PN}-master.service"


SUMMARY_${PN}-syndic = "master-of-masters for salt, the distributed remote execution system"
DESCRIPTION_${PN}-syndic = "${DESCRIPTION_COMMON} This particular package provides the master of masters for \
salt; it enables the management of multiple masters at a time."
RDEPENDS_${PN}-syndic = "${PN}-master (= ${EXTENDPKGV})"
CONFFILES_${PN}-syndic="${sysconfdir}/init.d/${PN}-syndic"
FILES_${PN}-syndic = "${bindir}/${PN}-syndic ${CONFFILES_${PN}-syndic} ${systemd_system_unitdir}/${PN}-syndic.service"

SUMMARY_${PN}-cloud = "public cloud VM management system"
DESCRIPTION_${PN}-cloud = "provision virtual machines on various public clouds via a cleanly controlled profile and mapping system."
RDEPENDS_${PN}-cloud = "${PN}-common (= ${EXTENDPKGV})"
RSUGGESTS_${PN}-cloud = "python3-netaddr python3-botocore"
CONFFILES_${PN}-cloud = "${sysconfdir}/${PN}/cloud"
FILES_${PN}-cloud = "${bindir}/${PN}-cloud ${sysconfdir}/${PN}/cloud.conf.d/ ${sysconfdir}/${PN}/cloud.profiles.d/ ${sysconfdir}/${PN}/cloud.providers.d/ ${CONFFILES_${PN}-cloud}"

SUMMARY_${PN}-tests = "salt stack test suite"
DESCRIPTION_${PN}-tests ="${DESCRIPTION_COMMON} This particular package provides the salt unit test suite."
RDEPENDS_${PN}-tests = "${PN}-common python3-pytest-salt python3-tests python3-image bash"
FILES_${PN}-tests = "${PYTHON_SITEPACKAGES_DIR}/salt-tests/tests/"

FILES_${PN}-bash-completion = "${datadir}/bash-completion"
FILES_${PN}-zsh-completion = "${datadir}/zsh/site-functions"
