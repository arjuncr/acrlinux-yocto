SUMMARY = "A high-performance memory object caching system"
DESCRIPTION = "\
 memcached optimizes specific high-load serving applications that are designed \
 to take advantage of its versatile no-locking memory access system. Clients \
 are available in several different programming languages, to suit the needs \
 of the specific application. Traditionally this has been used in mod_perl \
 apps to avoid storing large chunks of data in Apache memory, and to share \
 this burden across several machines."
SECTION = "web"
HOMEPAGE = "http://memcached.org/"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://COPYING;md5=7e5ded7363d335e1bb18013ca08046ff"

inherit autotools systemd

DEPENDS += "libevent"
RDEPENDS_${PN} += "\
    bash \
    perl \
    perl-module-posix \
    perl-module-autoloader \
    perl-module-tie-hash \
    "

SRC_URI = "http://www.memcached.org/files/${BP}.tar.gz \
           file://memcached-add-hugetlbfs-check.patch \
           file://memcached-config.txt \
           "

SRC_URI[md5sum] = "263819baf411388b3f72700a3212d4e2"
SRC_URI[sha256sum] = "258cc3ddb7613685465acfd0215f827220a3bbdd167fd2c080632105b2d2f3ce"

# set the same COMPATIBLE_HOST as libhugetlbfs
COMPATIBLE_HOST = '(i.86|x86_64|powerpc|powerpc64|arm).*-linux'

python __anonymous () {
    endianness = d.getVar('SITEINFO_ENDIANNESS', True)
    if endianness == 'le':
        d.setVar('EXTRA_OECONF', "ac_cv_c_endian=little")
    else:
        d.setVar('EXTRA_OECONF', "ac_cv_c_endian=big")
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[hugetlbfs] = "--enable-hugetlbfs, --disable-hugetlbfs, libhugetlbfs"

inherit update-rc.d

INITSCRIPT_NAME = "memcached"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_PACKAGES = "memcached"
SYSTEMD_SERVICE_${PN} = "memcached.service"

do_install_append() {
    install -D -m 755 ${S}/scripts/memcached-init ${D}${sysconfdir}/init.d/memcached
    
    mkdir -p ${D}/usr/share/memcached/scripts
    install -m 755 ${S}/scripts/memcached-tool ${D}/usr/share/memcached/scripts
    install -m 755 ${S}/scripts/start-memcached ${D}/usr/share/memcached/scripts

    install -d ${D}/${sysconfdir}/default
    install -m 600 ${WORKDIR}/memcached-config.txt ${D}/${sysconfdir}/default/memcached

    install -d ${D}/${systemd_system_unitdir}
    install -m 644 ${S}/scripts/memcached.service ${D}/${systemd_system_unitdir}/.
    sed -e "s@^EnvironmentFile=.*@EnvironmentFile=${sysconfdir}/default/memcached@" \
        -i ${D}/${systemd_system_unitdir}/memcached.service
}
