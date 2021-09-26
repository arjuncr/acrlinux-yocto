FILESEXTRAPATHS_prepend := "${THISDIR}/postgresql:"

SRC_URI += " \
    file://postgresql-init \
    file://postgresql-init.service \
    "

inherit identity hosts

SYSTEMD_AUTO_ENABLE_${PN} = "enable"

# default
DB_DATADIR ?= "/var/lib/postgres/data"

do_install_append() {
    D_DEST_DIR=${D}${sysconfdir}/postgresql

    install -d ${D_DEST_DIR}
    install -m 0755 ${WORKDIR}/postgresql-init ${D_DEST_DIR}/postgresql-init

    sed -e "s:%DB_DATADIR%:${DB_DATADIR}:g" -i ${D_DEST_DIR}/postgresql-init

    sed -e "s:%DB_USER%:${DB_USER}:g" -i ${D_DEST_DIR}/postgresql-init
    sed -e "s:%DB_PASSWORD%:${DB_PASSWORD}:g" -i ${D_DEST_DIR}/postgresql-init

    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${D_DEST_DIR}/postgresql-init
    sed -e "s:%CONTROLLER_HOST%:${CONTROLLER_HOST}:g" -i ${D_DEST_DIR}/postgresql-init

    sed -e "s:%COMPUTE_IP%:${COMPUTE_IP}:g" -i ${D_DEST_DIR}/postgresql-init
    sed -e "s:%COMPUTE_HOST%:${COMPUTE_HOST}:g" -i ${D_DEST_DIR}/postgresql-init

    install -d ${D}${systemd_unitdir}/system/
    PG_INIT_SERVICE_FILE=${D}${systemd_unitdir}/system/postgresql-init.service
    install -m 644 ${WORKDIR}/postgresql-init.service ${PG_INIT_SERVICE_FILE}
    sed -e "s:%SYSCONFIGDIR%:${sysconfdir}:g" -i ${PG_INIT_SERVICE_FILE}

    # Update PGDATA throughout
    files="${D}${localstatedir}/lib/${BPN}/.profile"
    files="$files ${D}${systemd_unitdir}/system/postgresql.service"
    files="$files ${D}${bindir}/${BPN}-setup"
    files="$files ${D}${sysconfdir}/init.d/${BPN}-server"
    for f in $files
    do
        sed -e "s:\(PGDATA=\).*$:\1${DB_DATADIR}:g" -i $f
    done

    # Ensure DB is initialize before we attempt to start the service
    FILE=${D}${systemd_unitdir}/system/postgresql.service
    sed -e '/ExecStart=.*/i ExecStartPre=-${sysconfdir}/postgresql/postgresql-init initdb' -i $FILE
    sed -e '/ExecStartPre=.*/i PermissionsStartOnly=true' -i $FILE
}

PACKAGES += " ${PN}-setup"

SYSTEMD_PACKAGES += "${PN}-setup"
SYSTEMD_SERVICE_${PN}-setup = "postgresql-init.service"

FILES_${PN}-setup = " \
    ${systemd_unitdir}/system \
"
