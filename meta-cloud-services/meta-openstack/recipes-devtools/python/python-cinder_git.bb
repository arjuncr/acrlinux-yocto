DESCRIPTION = "OpenStack Block storage service"
HOMEPAGE = "https://launchpad.net/cinder"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCNAME = "cinder"

SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike \
    file://cinder-init \
    file://cinder-init.service \
    file://cinder-api.service \
    file://cinder-backup.service \
    file://cinder-scheduler.service \
    file://cinder-volume.service \
    file://cinder.conf \
    file://cinder-volume \
    file://nfs_setup.sh \
    file://glusterfs_setup.sh \
    file://lvm_iscsi_setup.sh \
    file://add-cinder-volume-types.sh \
    "

SRCREV = "4fb3a702ba8c3de24c41a6f706597bfa81e60435"
PV = "11.1.0+git${SRCPV}"
S = "${WORKDIR}/git"

inherit setuptools3 systemd useradd identity default_configs hosts monitor

USER = "cinder"
GROUP = "cinder"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${GROUP}"
USERADD_PARAM_${PN} = "--system -m -d ${localstatedir}/lib/cinder -s /bin/false -g ${GROUP} ${USER}"

CINDER_BACKUP_BACKEND_DRIVER ?= "cinder.backup.drivers.swift"

KEYSTONE_HOST="${CONTROLLER_IP}"

CINDER_LVM_VOLUME_BACKING_FILE_SIZE ?= "2G"
CINDER_NFS_VOLUME_SERVERS_DEFAULT = "controller:/etc/cinder/nfs_volumes"
CINDER_NFS_VOLUME_SERVERS ?= "${CINDER_NFS_VOLUME_SERVERS_DEFAULT}"
CINDER_GLUSTERFS_VOLUME_SERVERS_DEFAULT = "controller:/glusterfs_volumes"
CINDER_GLUSTERFS_VOLUME_SERVERS ?= "${CINDER_GLUSTERFS_VOLUME_SERVERS_DEFAULT}"

do_install_append() {
    TEMPLATE_CONF_DIR=${S}${sysconfdir}/${SRCNAME}
    CINDER_CONF_DIR=${D}${sysconfdir}/${SRCNAME}
    
    #Instead of substituting api-paste.ini from the WORKDIR,
    #move it over to the image's directory and do the substitution there
    install -d ${CINDER_CONF_DIR}
    install -o ${USER} -m 664 ${WORKDIR}/cinder.conf ${CINDER_CONF_DIR}/
    install -o ${USER} -m 664 ${TEMPLATE_CONF_DIR}/api-paste.ini ${CINDER_CONF_DIR}/
    install -o ${USER} -m 664 ${S}/etc/cinder/policy.json ${CINDER_CONF_DIR}/

    install -d ${CINDER_CONF_DIR}/drivers
    install -m 600 ${WORKDIR}/nfs_setup.sh ${CINDER_CONF_DIR}/drivers/
    install -m 600 ${WORKDIR}/glusterfs_setup.sh ${CINDER_CONF_DIR}/drivers/
    install -m 600 ${WORKDIR}/lvm_iscsi_setup.sh ${CINDER_CONF_DIR}/drivers/
    install -m 700 ${WORKDIR}/add-cinder-volume-types.sh ${CINDER_CONF_DIR}/

    install -d ${D}${localstatedir}/log/${SRCNAME}

    # Setup the neutron initialization script
    INIT_FILE=${CINDER_CONF_DIR}/cinder-init
    install -g ${USER} -m 750 ${WORKDIR}/cinder-init ${INIT_FILE}
    sed -e "s:%DB_USER%:${DB_USER}:g" -i ${INIT_FILE}
    sed -e "s:%CINDER_USER%:${USER}:g" -i ${INIT_FILE}
    sed -e "s:%CINDER_GROUP%:${GROUP}:g" -i ${INIT_FILE}
    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_USER%:${ADMIN_USER}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_PASSWORD%:${ADMIN_PASSWORD}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_ROLE%:${ADMIN_ROLE}:g" -i ${INIT_FILE}
    sed -e "s:%SYSCONFDIR%:${sysconfdir}:g" -i ${INIT_FILE}
    sed -e "s:%ROOT_HOME%:${ROOT_HOME}:g" -i ${INIT_FILE}

    # install systemd service files
    install -d ${D}${systemd_system_unitdir}/
    for j in cinder-init cinder-api cinder-backup cinder-volume cinder-scheduler; do
        SERVICE_FILE=${D}${systemd_system_unitdir}/$j.service
	install -m 644 ${WORKDIR}/$j.service ${SERVICE_FILE}
        sed -e "s:%USER%:${USER}:g" -i ${SERVICE_FILE}
	sed -e "s:%GROUP%:${GROUP}:g" -i ${SERVICE_FILE}
        sed -e "s:%LOCALSTATEDIR%:${localstatedir}:g" -i ${SERVICE_FILE}
        sed -e "s:%SYSCONFDIR%:${sysconfdir}:g" -i ${SERVICE_FILE}			
    done

    #
    # Per https://docs.openstack.org/cinder/pike/install/cinder-controller-install-ubuntu.html
    #
    CONF_FILE="${CINDER_CONF_DIR}/cinder.conf"
    sed -e "/^\[database\]/aconnection = postgresql+psycopg2://${DB_USER}:${DB_PASSWORD}@${CONTROLLER_IP}/cinder" \
           -i ${CONF_FILE}
    sed -e "/#transport_url =/atransport_url = rabbit://openstack:${ADMIN_PASSWORD}@${CONTROLLER_IP}" -i ${CONF_FILE}
    sed -e "/#auth_strategy =/aauth_strategy = keystone" -i ${CONF_FILE}

    str="auth_uri = http://${CONTROLLER_IP}:5000"
    str="$str\nauth_url = http://${CONTROLLER_IP}:35357"
    str="$str\nmemcached_servers = ${CONTROLLER_IP}:11211"
    str="$str\nauth_type = password"
    str="$str\nproject_domain_name = Default"
    str="$str\nuser_domain_name = Default"
    str="$str\nproject_name = service"
    str="$str\nusername = ${USER}"
    str="$str\npassword = ${ADMIN_PASSWORD}"
    sed -e "/^\[keystone_authtoken\].*/a$str" -i ${CONF_FILE}

    sed -e "/#my_ip =/amy_ip = ${MY_IP}" -i ${CONF_FILE}
    sed -e "/#lock_path =/alock_path = ${localstatedir}/lib/cinder/tmp" -i ${CONF_FILE}

    sed -e "/#enabled_backends =/aenabled_backends = nfsdriver" -i ${CONF_FILE}
    str="[nfsdriver]"
    str="$str\nvolume_group=nfs-group-1"
    str="$str\nvolume_driver=cinder.volume.drivers.nfs.NfsDriver"
    str="$str\nvolume_backend_name=Generic_NFS"
    sed -e "s/\(^\[backend\].*\)/$str\n\1/" -i ${CONF_FILE}

    # test setup
    cp -r tools ${CINDER_CONF_DIR}

    #Create cinder volume group backing file
    sed 's/%CINDER_LVM_VOLUME_BACKING_FILE_SIZE%/${CINDER_LVM_VOLUME_BACKING_FILE_SIZE}/g' -i ${D}/etc/cinder/drivers/lvm_iscsi_setup.sh
    mkdir -p ${D}/etc/tgt/conf.d/
    echo "include /etc/cinder/data/volumes/*" > ${D}/etc/tgt/conf.d/python-cinder.conf

    # Create Cinder nfs_share config file with default nfs server
    echo "${CINDER_NFS_VOLUME_SERVERS}" > ${D}/etc/cinder/nfs_shares
    sed 's/\s\+/\n/g' -i ${D}/etc/cinder/nfs_shares
    [ "x${CINDER_NFS_VOLUME_SERVERS}" = "x${CINDER_NFS_VOLUME_SERVERS_DEFAULT}" ] && is_default="1" || is_default="0"
    sed -e "s:%IS_DEFAULT%:${is_default}:g" -i ${D}/etc/cinder/drivers/nfs_setup.sh

    # Create Cinder glusterfs_share config file with default glusterfs server
    echo "${CINDER_GLUSTERFS_VOLUME_SERVERS}" > ${D}/etc/cinder/glusterfs_shares
    sed 's/\s\+/\n/g' -i ${D}/etc/cinder/glusterfs_shares
    [ "x${CINDER_GLUSTERFS_VOLUME_SERVERS}" = "x${CINDER_GLUSTERFS_VOLUME_SERVERS_DEFAULT}" ] && is_default="1" || is_default="0"
    sed -e "s:%IS_DEFAULT%:${is_default}:g" -i ${D}/etc/cinder/drivers/glusterfs_setup.sh
}

#pkg_postinst_${SRCNAME}-setup () {
#    if [ -z "$D" ]; then
#	 if [ ! -d /var/log/cinder ]; then
#	    mkdir /var/log/cinder
#	 fi
#
#	 sudo -u postgres createdb cinder
#	 cinder-manage db sync
#
#	 # Create Cinder nfs_share config file with default nfs server
#	 if [ ! -f /etc/cinder/nfs_shares ]; then
#	     /bin/bash /etc/cinder/drivers/nfs_setup.sh
#	 fi
#
#	 # Create Cinder glusterfs_share config file with default glusterfs server
#	 if [ ! -f /etc/cinder/glusterfs_shares ] && [ -f /usr/sbin/glusterfsd ]; then
#	     /bin/bash /etc/cinder/drivers/glusterfs_setup.sh
#	 fi
#    fi
#}

PACKAGES += "${SRCNAME}-tests ${SRCNAME} ${SRCNAME}-setup ${SRCNAME}-api ${SRCNAME}-volume ${SRCNAME}-scheduler ${SRCNAME}-backup"
ALLOW_EMPTY_${SRCNAME}-setup = "1"
ALLOW_EMPTY_${SRCNAME}-backup = "1"
ALLOW_EMPTY_${SRCNAME}-scheduler = "1"
ALLOW_EMPTY_${SRCNAME}-volume = "1"
ALLOW_EMPTY_${SRCNAME}-api = "1"

RDEPENDS_${SRCNAME}-tests += " bash python"

FILES_${PN} = "${libdir}/* /etc/tgt"

FILES_${SRCNAME}-tests = "${sysconfdir}/${SRCNAME}/tools"

FILES_${SRCNAME}-api = " \
    ${bindir}/cinder-api \
"

FILES_${SRCNAME}-volume = " \
    ${bindir}/cinder-volume \
"

FILES_${SRCNAME}-scheduler = " \
    ${bindir}/cinder-scheduler \
"

FILES_${SRCNAME}-backup = " \
    ${bindir}/cinder-backup \
"

FILES_${SRCNAME} = "${bindir}/* \
    ${sysconfdir}/${SRCNAME}/* \
    ${localstatedir}/* \
    ${sysconfdir}/${SRCNAME}/drivers/* \
    "

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        lvm2 \
        python-pbr \
        python-babel \
        python-decorator \
        python-eventlet \
        python-greenlet \
        python-httplib2 \
        python-iso8601 \
        python-ipaddress \
        python-keystoneauth1 \
        python-keystonemiddleware \
        python-lxml \
        python-oauth2client \
        python-oslo.config \
        python-oslo.concurrency \
        python-oslo.context \
        python-oslo.db \
        python-oslo.log \
        python-oslo.messaging \
        python-oslo.middleware \
        python-oslo.policy \
        python-oslo.privsep \
        python-oslo.reports \
        python-oslo.rootwrap \
        python-oslo.serialization \
        python-oslo.service \
        python-oslo.utils \
        python-oslo.versionedobjects \
        python-osprofiler \
        python-paramiko \
        python-paste \
        python-pastedeploy \
        python-psutil \
        python-pyparsing \
        python-barbicanclient \
        python-glanceclient \
        python-keystoneclient \
        python-novaclient \
        python-swiftclient \
        python-pytz \
        python-requests \
        python-retrying \
        python-routes \
        python-taskflow \
        python-rtslib-fb \
        python-simplejson \
        python-six \
        python-sqlalchemy \
        python-sqlalchemy-migrate \
        python-stevedore \
        python-suds-jurko \
        python-webob \
        python-oslo.i18n \
        python-oslo.vmware \
        python-os-brick \
        python-os-win \
        python-tooz \
        python-google-api-python-client \
        python-castellan \
        python-cryptography \
        "

RDEPENDS_${SRCNAME} = " \
    ${PN} \
    postgresql \
    postgresql-client \
    python-psycopg2 \
    tgt"

RDEPENDS_${SRCNAME}-api = "${SRCNAME}"
RDEPENDS_${SRCNAME}-volume = "${SRCNAME}"
RDEPENDS_${SRCNAME}-scheduler = "${SRCNAME}"
RDEPENDS_${SRCNAME}-setup = "postgresql sudo ${SRCNAME} bash"

SYSTEMD_PACKAGES = " \
    ${SRCNAME}-setup \
    ${SRCNAME}-api \
    ${SRCNAME}-volume \
    ${SRCNAME}-scheduler \
    ${SRCNAME}-backup \
"

SYSTEMD_SERVICE_${SRCNAME}-setup = "cinder-init.service"
SYSTEMD_SERVICE_${SRCNAME}-api = "cinder-api.service"
SYSTEMD_SERVICE_${SRCNAME}-volume = "cinder-volume.service"
SYSTEMD_SERVICE_${SRCNAME}-scheduler = "cinder-scheduler.service"
SYSTEMD_SERVICE_${SRCNAME}-backup = "cinder-backup.service"

# Disable until they are configured (via -setup)
SYSTEMD_AUTO_ENABLE_${SRCNAME}-api = "disable"
SYSTEMD_AUTO_ENABLE_${SRCNAME}-volume = "disable"
SYSTEMD_AUTO_ENABLE_${SRCNAME}-scheduler = "disable"
SYSTEMD_AUTO_ENABLE_${SRCNAME}-backup = "disable"

MONITOR_SERVICE_PACKAGES = "${SRCNAME}"
MONITOR_SERVICE_${SRCNAME} = "cinder"
