SUMMARY = "Glusterfs distributed filesystem"
DESCRIPTION = "\
GlusterFS is an open source, distributed file system capable of scaling \
to several petabytes (actually, 72 brontobytes!) and handling thousands \
of clients.  GlusterFS clusters together storage building blocks over \
Infiniband RDMA or TCP/IP interconnect, aggregating disk and memory \
resources and managing data in a single global namespace."
HOMEPAGE = "http://www.gluster.org/"
SECTION = "console/network"

SRC_URI += "file://glusterd.init \
            file://glusterd.service \
            file://glusterfs-configure-automake-with-subdir-objects-for.patch \
            file://xlator-host-contamination-5.4.patch \
            file://glusterfs-disable-default-startup-scripts.patch \
            file://glusterfs-api-remove-unnecessary-library-options.patch \
            file://python-work-around-host-and-target-python.patch \
            file://glusterfs-change-default-nfs-port-number.patch \
            file://libglusterfs-Don-t-link-against-libfl.patch \
            file://glusterd-change-port-range.patch \
            file://configure.ac-allow-PYTHON-values-to-be-passed-via-en.patch \
           "

LICENSE = "(LGPLv3+ | GPLv2) & GPLv3+ & LGPLv3+ & GPLv2+ & LGPLv2+ & LGPLv2.1+ & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING-GPLV2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING-LGPLV3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://contrib/fuse-util/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools pkgconfig distutils3-base update-rc.d systemd
inherit python3-dir

DEPENDS += "bison-native flex-native python3-native fuse libaio libtirpc libxml2 ncurses \
            openssl python3 readline zlib liburcu util-linux glib-2.0 acl"

# rpc now comes from libtirpc instead of glibc
#
CFLAGS += "-I${STAGING_INCDIR}/tirpc -Wno-error=implicit-function-declaration"
LDFLAGS += " -ltirpc"

EXTRA_OECONF = "ac_cv_file__etc_debian_version=no \
                ac_cv_file__etc_SuSE_release=no \
                ac_cv_file__etc_redhat_release=no \
				ac_cv_file__etc_centos_release=no \
                --with-mountutildir=${sbindir} \
                --disable-fusermount \
                TARGET_PYTHON=${bindir}/python3 \
                PYTHON=${PYTHON} \
                BUILD_PYTHON_INC=${STAGING_INCDIR}/python${PYTHON_BASEVERSION} \
                BUILD_PYTHON_LIB=python${PYTHON_BASEVERSION} \
                BUILD_PYTHON_SITE_PACKAGES=${PYTHON_SITEPACKAGES_DIR} \
               "

PACKAGECONFIG ??= "georeplication libibverbs"
PACKAGECONFIG_remove_mipsarch = "libibverbs"
PACKAGECONFIG_remove_arm = "libibverbs"

PACKAGECONFIG[debug] = "--enable-debug,--disable-debug,,"
PACKAGECONFIG[georeplication] = "--enable-georeplication,--disable-georeplication,,rsync"
PACKAGECONFIG[libibverbs] = "--enable-ibverbs,--disable-ibverbs,virtual/libibverbs librdmacm,"
PACKAGECONFIG[ocf] = "--with-ocf,--without-ocf,,"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind,"

do_install_append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/glusterd.init \
        ${D}${sysconfdir}/init.d/glusterd

    if ! ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','true','false',d)}; then
        # Mount looks for mount.* plug-ins in /sbin; fix it with a symlink.
        mkdir -p ${D}${base_sbindir}
        (cd ${D}${base_sbindir}; ln -s ..${sbindir}/mount.glusterfs .)
    fi

    # These are plug-ins, so they don't need libtool configs.
    find ${D}${libdir}/glusterfs/${PV} -name '*.la' -exec rm -f '{}' ';'

    # The RPM spec file creates these directories.
    install -d ${D}${sysconfdir}/default/volatiles
    cat > ${D}${sysconfdir}/default/volatiles/99_glusterfs << EOF
d root root 0755 ${localstatedir}/log/glusterfs none
d root root 0755 ${localstatedir}/run/gluster none
EOF

    # Install systemd service files
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/glusterd.service ${D}${systemd_system_unitdir}
    sed -i -e 's#@SBINDIR@#${sbindir}#g' \
        -e 's#@base_bindir@#${base_bindir}#g' \
        -e 's#@localstatedir@#${localstatedir}#g' \
        ${D}${systemd_system_unitdir}/glusterd.service

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        cat > ${D}${sysconfdir}/tmpfiles.d/glusterfs.conf << EOF
d ${localstatedir}/log/glusterfs 0755 root root -
d ${localstatedir}/run/gluster 0755 root root -
EOF
    fi

    # Remove /var/run and /var/log as they are created on startup
    rm -rf ${D}${localstatedir}/run
    rm -rf ${D}${localstatedir}/log
}

INITSCRIPT_PACKAGES = "glusterfs-server"
INITSCRIPT_NAME = "glusterd"
INITSCRIPT_PARAMS = "start 20 5 3 2 . stop 80 0 1 6 ."

SYSTEMD_PACKAGES = "glusterfs-server"
SYSTEMD_SERVICE_${PN} = "glusterd.service"
SYSTEMD_AUTO_ENABLE = "disable"

# Allow plug-in symlinks.
INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN}-rdma += "dev-so"
INSANE_SKIP_${PN}-fuse += "dev-so"
INSANE_SKIP_${PN}-server += "dev-so"

PACKAGES += "${PN}-rdma ${PN}-geo-replication ${PN}-fuse ${PN}-server \
             ${PN}-api ${PN}-ocf"

RDEPENDS_${PN} += "python3-core python3-json python3-ctypes bash"
RDEPENDS_${PN}-ocf += "bash"
RDEPENDS_${PN}-server += "bash"
RDEPENDS_${PN}-geo-replication += "python3-core bash"

FILES_${PN}-dbg += "${libdir}/glusterfs/${PV}/*/.debug \
                    ${libdir}/glusterfs/${PV}/*/*/.debug \
                    ${libdir}/glusterfs/${PV}/*/*/*/.debug \
                    ${libexecdir}/glusterfs/.debug"

FILES_${PN} = "${libdir}/glusterfs/${PV}/auth \
               ${libdir}/glusterfs/${PV}/rpc-transport/socket* \
               ${libdir}/glusterfs/${PV}/xlator/cluster \
               ${libdir}/glusterfs/${PV}/xlator/debug \
               ${libdir}/glusterfs/${PV}/xlator/encryption \
               ${libdir}/glusterfs/${PV}/xlator/features \
               ${libdir}/glusterfs/${PV}/xlator/performance \
               ${libdir}/glusterfs/${PV}/xlator/protocol/client* \
               ${libdir}/glusterfs/${PV}/xlator/system \
               ${libdir}/glusterfs/${PV}/xlator/testing \
			   ${libdir}/glusterfs/${PV}/xlator/playground \
               ${libdir}/glusterfs/${PV}/xlator/meta.so \
               ${libdir}/glusterfs/ganesha \
               ${libdir}/glusterfs/glusterfs/glusterfind \
               ${libdir}/glusterfs/glusterfs/gfind_missing_files \
               ${libdir}/glusterfs/glusterfs/peer* \
               ${libdir}/glusterfs/glusterfs/*.sh \
               ${libdir}/libglusterfs.so.* \
               ${libdir}/libgfrpc.so.* \
               ${libdir}/libgfchangelog.so.* \
               ${libdir}/libgfdb.so.* \
               ${libdir}/libgfxdr.so.* \
               ${libdir}/python*/site-packages \
			   ${libexecdir}/glusterfs/gfevents \
               ${libexecdir}/glusterfs/events \
               ${libexecdir}/glusterfs/glusterfind \
               ${libexecdir}/glusterfs/gfind_missing_files \
               ${libexecdir}/glusterfs/mount-shared-storage.sh \
               ${libexecdir}/glusterfs/peer_mountbroker.py \
               ${libexecdir}/glusterfs/peer_eventsapi.py \
               ${libexecdir}/glusterfs/peer_mountbroker \
               ${libexecdir}/glusterfs/peer_gsec_create \
               ${libexecdir}/glusterfs/gverify.sh \
               ${libexecdir}/glusterfs/peer_add_secret_pub \
               ${localstatedir} \
               ${sysconfdir}/default/volatiles \
               ${sbindir}/glusterfs \
               ${sbindir}/glusterfsd \
               ${sbindir}/gfind_missing_files \
               ${sbindir}/glfsheal \
               ${sbindir}/gluster-eventsapi \
               ${sbindir}/gluster-mountbroker \
               ${sbindir}/glustereventsd \
               ${sbindir}/gf_attach \
               ${sbindir}/*.py \
			   ${sbindir}/gluster-setgfid2path \
               ${bindir}/ \
               ${sysconfdir}/glusterfs \
               ${sysconfdir}/ganesha \
               ${datadir}/glusterfs/* \
	      "

FILES_${PN}-rdma = "${libdir}/glusterfs/${PV}/rpc-transport/rdma*"

FILES_${PN}-geo-replication = "${libexecdir}/glusterfs/gsyncd \
                               ${libexecdir}/glusterfs/python/syncdaemon \
                               ${libexecdir}/glusterfs/peer_georep-sshkey.py \
                               ${libexecdir}/glusterfs/set_geo_rep_pem_keys.sh \
                               ${sbindir}/gluster-georep-sshkey"

FILES_${PN}-fuse = "${bindir}/fusermount-glusterfs \
                    ${libdir}/glusterfs/${PV}/xlator/mount \
                    ${sbindir}/mount.glusterfs \
                    ${base_sbindir}/mount.glusterfs"

FILES_${PN}-server = "${libdir}/glusterfs/${PV}/xlator/mgmt \
                      ${libdir}/glusterfs/${PV}/xlator/nfs \
                      ${libdir}/glusterfs/${PV}/xlator/protocol/server* \
                      ${libdir}/glusterfs/${PV}/xlator/storage \
                      ${sbindir}/gluster \
                      ${sbindir}/glusterd \
                      ${sysconfdir}/glusterfs/glusterd.vol \
                      ${sysconfdir}/glusterfs/glusterfs-logrotate \
                      ${sysconfdir}/init.d/glusterd \
                      ${sysconfdir}/tmpfiles.d \
                      ${systemd_system_unitdir}"

# Note: Debian package renaming produces libgfapi[0-9]+-*.*.rpm.
FILES_${PN}-api = "${libdir}/libgfapi.so.*"

FILES_${PN}-ocf = "${prefix}/lib/ocf"

pkg_postinst_${PN}() {
    if [ -z "$D" ] && [ -e /etc/init.d/populate-volatile.sh ]; then
        /etc/init.d/populate-volatile.sh update
    fi
}

CVE_PRODUCT = "glusterfs gluster_storage"
