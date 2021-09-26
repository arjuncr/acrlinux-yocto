DESCRIPTION = "Services for discovering, registering and retrieving virtual machine images"
HOMEPAGE = "http://glance.openstack.org/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCNAME = "glance"

SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike \
           file://glance.init \
           file://glance-api.service \
           file://glance-registry.service \
           file://glance-init.service \
           file://glance-init \
           "

SRCREV = "06af2eb5abe0332f7035a7d7c2fbfd19fbc4dae7"
PV = "15.0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit setuptools3 identity default_configs hosts monitor useradd systemd

USER = "glance"
GROUP = "glance"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${GROUP}"
USERADD_PARAM_${PN} = "--system -m -d ${localstatedir}/lib/glance -s /bin/false -g ${GROUP} ${USER}"

GLANCE_DEFAULT_STORE ?= "file"
GLANCE_KNOWN_STORES ?= "glance.store.rbd.Store,\
 glance.store.swift.Store,\
 glance.store.cinder.Store,\
 glance.store.filesystem.Store,\
 glance.store.http.Store"


SERVICECREATE_PACKAGES = "${SRCNAME}-setup"
KEYSTONE_HOST="${CONTROLLER_IP}"

# USERCREATE_PARAM and SERVICECREATE_PARAM contain the list of parameters to be set.
# If the flag for a parameter in the list is not set here, the default value will be given to that parameter.
# Parameters not in the list will be set to empty.

USERCREATE_PARAM_${SRCNAME}-setup = "name pass tenant role email"
SERVICECREATE_PARAM_${SRCNAME}-setup = "name type description region publicurl adminurl internalurl"
python () {
    flags = {'type':'image',\
             'description':'OpenStack Image Service',\
             'publicurl':"'http://${KEYSTONE_HOST}:5000'",\
             'adminurl':"'http://${KEYSTONE_HOST}:35357'",\
             'internalurl':"'http://${KEYSTONE_HOST}:35357'"}

    d.setVarFlags("SERVICECREATE_PARAM_%s-setup" % d.getVar('SRCNAME',True), flags)
}
SERVICECREATE_PACKAGES[vardeps] += "KEYSTONE_HOST"

do_install_prepend() {
    sed 's:%PYTHON_SITEPACKAGES_DIR%:${PYTHON_SITEPACKAGES_DIR}:g' -i ${S}/${SRCNAME}/tests/functional/__init__.py
    sed 's:%PYTHON_SITEPACKAGES_DIR%:${PYTHON_SITEPACKAGES_DIR}:g' -i ${S}/${SRCNAME}/tests/unit/base.py
    sed 's:%PYTHON_SITEPACKAGES_DIR%:${PYTHON_SITEPACKAGES_DIR}:g' -i ${S}/${SRCNAME}/tests/utils.py
}

do_install_append() {
    SRC_SYSCONFDIR=${S}${sysconfdir}
    GLANCE_CONF_DIR=${D}${sysconfdir}/glance

    install -o root -g ${GROUP} -m 750 -d ${GLANCE_CONF_DIR}

    # Start with pristine copies from upstream
    install -m 644 -o root -g ${GROUP} ${SRC_SYSCONFDIR}/glance-registry.conf ${GLANCE_CONF_DIR}/
    install -m 644 -o root -g ${GROUP} ${SRC_SYSCONFDIR}/glance-api.conf ${GLANCE_CONF_DIR}/
    install -m 644 -o root -g ${GROUP} ${SRC_SYSCONFDIR}/glance-cache.conf ${GLANCE_CONF_DIR}/
    install -m 644 -o root -g ${GROUP} ${SRC_SYSCONFDIR}/glance-registry-paste.ini ${GLANCE_CONF_DIR}/
    install -m 644 -o root -g ${GROUP} ${SRC_SYSCONFDIR}/glance-api-paste.ini ${GLANCE_CONF_DIR}/
    install -m 644 -o root -g ${GROUP} ${SRC_SYSCONFDIR}/policy.json ${GLANCE_CONF_DIR}/
    install -m 644 -o root -g ${GROUP} ${SRC_SYSCONFDIR}/schema-image.json ${GLANCE_CONF_DIR}/

    for file in api registry cache
    do
        sed -e "/^#connection = .*/aconnection = postgresql+psycopg2://${DB_USER}:${DB_PASSWORD}@localhost/glance" \
            -i ${GLANCE_CONF_DIR}/glance-$file.conf
        sed -e "/^#filesystem_store_datadir = .*/afilesystem_store_datadir = ${localstatedir}/lib/${SRCNAME}/images/" \
            -i ${GLANCE_CONF_DIR}/glance-$file.conf
	sed -e "/^#service_token_roles_required = .*/aservice_token_roles_required = true" \
	    -i ${GLANCE_CONF_DIR}/glance-$file.conf
    done

    CONF_FILE=${GLANCE_CONF_DIR}/glance-api.conf
    sed -e '/^#stores = .*/astores = ${GLANCE_KNOWN_STORES}' -i ${CONF_FILE}
    sed -e '/^#default_store = .*/adefault_store = ${GLANCE_DEFAULT_STORE}' -i ${CONF_FILE}
    #sed -e 's:^swift_store_auth_address =.*:swift_store_auth_address = http\://127.0.0.1\:8081/keystone/main/:g' -i ${CONF_FILE}
    #sed -e 's:^swift_store_user =.*:swift_store_user = %SERVICE_TENANT_NAME%\:${SRCNAME}:g' -i ${CONF_FILE}
    #sed -e 's:^swift_store_key =.*:swift_store_key = %SERVICE_PASSWORD%:g' -i ${CONF_FILE}
    sed -e '/^#swift_store_create_container_on_put = .*/aswift_store_create_container_on_put = True' -i ${CONF_FILE}

    # As documented in https://docs.openstack.org/glance/pike/install/install-debian.html
    for file in api registry
    do
        CONF_FILE=${GLANCE_CONF_DIR}/glance-$file.conf
	keystone_authtoken="#\n# Setup at install by python-glance_git.bb\n#"
	keystone_authtoken="$keystone_authtoken\nauth_uri = http://${CONTROLLER_IP}:5000"
	keystone_authtoken="$keystone_authtoken\nauth_url = http://${CONTROLLER_IP}:35357"
	keystone_authtoken="$keystone_authtoken\nmemcached_servers = ${CONTROLLER_IP}:11211"
	keystone_authtoken="$keystone_authtoken\nauth_type = password"
	keystone_authtoken="$keystone_authtoken\nproject_domain_name = Default"
	keystone_authtoken="$keystone_authtoken\nuser_domain_name = Default"
	keystone_authtoken="$keystone_authtoken\nproject_name = service"
	keystone_authtoken="$keystone_authtoken\nusername = ${USER}"
	keystone_authtoken="$keystone_authtoken\npassword = ${ADMIN_PASSWORD}"
	sed -e "/^\[keystone_authtoken\].*/a$keystone_authtoken" -i ${CONF_FILE}
	sed -e "/^#flavor = .*/aflavor = keystone" -i ${CONF_FILE}
    done

    # Install and setup systemd service files
    install -d ${D}${systemd_system_unitdir}/
    for service in glance-api.service glance-registry.service glance-init.service
    do
        SERVICE_FILE=${D}${systemd_system_unitdir}/$service
        install -m 644 ${WORKDIR}/$service ${D}${systemd_system_unitdir}/
        sed -e "s:%SYSCONFDIR%:${sysconfdir}:g" -i ${SERVICE_FILE}
        sed -e "s:%LOCALSTATEDIR%:${localstatedir}:g" -i ${SERVICE_FILE}
        sed -e "s:%USER%:${USER}:g" -i ${SERVICE_FILE}
        sed -e "s:%GROUP%:${GROUP}:g" -i ${SERVICE_FILE}
    done

    # Setup the glance initialization script
    INIT_FILE=${GLANCE_CONF_DIR}/glance-init
    install -o root -g ${USER} -m 750 ${WORKDIR}/glance-init ${INIT_FILE}
    sed -e "s:%DB_USER%:${DB_USER}:g" -i ${INIT_FILE}
    sed -e "s:%GLANCE_USER%:${USER}:g" -i ${INIT_FILE}
    sed -e "s:%GLANCE_GROUP%:${GROUP}:g" -i ${INIT_FILE}
    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_USER%:${ADMIN_USER}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_PASSWORD%:${ADMIN_PASSWORD}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_ROLE%:${ADMIN_ROLE}:g" -i ${INIT_FILE}
    sed -e "s:%SYSCONFDIR%:${sysconfdir}:g" -i ${INIT_FILE}
}

PACKAGES += " ${SRCNAME} ${SRCNAME}-setup ${SRCNAME}-api ${SRCNAME}-registry"

FILES_${PN} = " \
    ${libdir}/* \
    ${datadir}/etc/${SRCNAME}* \
    "

FILES_${SRCNAME} = "${bindir}/* \
    ${sysconfdir}/${SRCNAME}/* \
    ${localstatedir}/* \
    "

FILES_${SRCNAME}-setup = " \
    ${systemd_unitdir}/system/glance-init.service \
    "

FILES_${SRCNAME}-api = " \
    ${bindir}/glance-api \
    ${systemd_unitdir}/system/glance-api.service \
    "

FILES_${SRCNAME}-registry = "\
    ${bindir}/glance-registry \
    ${systemd_unitdir}/system/glance-registry.service \
    "

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        coreutils \
        python-pbr \
        python-sqlalchemy \
        python-eventlet \
        python-pastedeploy \
        python-routes \
        python-webob \
        python-sqlalchemy-migrate \
        python-sqlparse \
        python-alembic \
        python-httplib2 \
        python-oslo.config \
        python-oslo.concurrency \
        python-oslo.context \
        python-oslo.utils \
        python-stevedore \
        python-futurist \
        python-taskflow \
        python-keystoneauth1 \
        python-keystonemiddleware \
        python-wsme \
        python-prettytable \
        python-paste \
        python-jsonschema \
        python-keystoneclient \
        python-pyopenssl \
        python-six \
        python-oslo.db \
        python-oslo.i18n \
        python-oslo.log \
        python-oslo.messaging \
        python-oslo.middleware \
        python-oslo.policy \
        python-retrying \
        python-osprofiler \
        python-glance-store \
        python-debtcollector \
        python-cryptography \
        python-cursive \
        python-iso8601 \
        python-monotonic \
        "

RDEPENDS_${SRCNAME} = " \
        ${PN} \
        postgresql \
        postgresql-client \
        python-psycopg2 \
        "

RDEPENDS_${SRCNAME}-api = "${SRCNAME}"
RDEPENDS_${SRCNAME}-registry = "${SRCNAME}"
RDEPENDS_${SRCNAME}-setup = "postgresql keystone-setup sudo ${SRCNAME}"
RDEPENDS_${SRCNAME}-tests = "python-psutil qpid-python bash"

SYSTEMD_PACKAGES = "${SRCNAME}-api ${SRCNAME}-registry ${SRCNAME}-setup"
SYSTEMD_SERVICE_${SRCNAME}-api = "glance-api.service"
SYSTEMD_SERVICE_${SRCNAME}-registry = "glance-registry.service"
SYSTEMD_SERVICE_${SRCNAME}-setup = "glance-init.service"

MONITOR_SERVICE_PACKAGES = "${SRCNAME}"
MONITOR_SERVICE_${SRCNAME} = "glance"
