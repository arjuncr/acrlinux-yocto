DESCRIPTION = "Authentication service for OpenStack"
HOMEPAGE = "http://www.openstack.org"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCNAME = "keystone"

SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike \
           file://keystone-init \
           file://keystone-init.service \
           file://keystone.conf \
           file://identity.sh \
           file://convert_keystone_backend.py \
           file://wsgi-keystone.conf \
           file://admin-openrc \
           "

# TBD: update or drop
# file://keystone-search-in-etc-directory-for-config-files.patch 
# file://keystone-remove-git-commands-in-tests.patch 
# file://keystone-explicitly-import-localcontext-from-oslo.me.patch

SRCREV = "d07677aba54362a4a3aa2d165b155105ffe30d73"
PV = "12.0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit setuptools3 identity hosts default_configs monitor useradd systemd

SERVICE_TOKEN = "password"
TOKEN_FORMAT ?= "PKI"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system -m -s /bin/false keystone"

LDAP_DN ?= "dc=my-domain,dc=com"

SERVICECREATE_PACKAGES = "${SRCNAME}-setup"
KEYSTONE_HOST="${CONTROLLER_IP}"

# USERCREATE_PARAM and SERVICECREATE_PARAM contain the list of parameters to be
# set.  If the flag for a parameter in the list is not set here, the default
# value will be given to that parameter. Parameters not in the list will be set
# to empty.

USERCREATE_PARAM_${SRCNAME}-setup = "name pass tenant role email"
python () {
    flags = {'name':'${ADMIN_USER}',\
             'pass':'${ADMIN_PASSWORD}',\
             'tenant':'${ADMIN_TENANT}',\
             'role':'${ADMIN_ROLE}',\
             'email':'${ADMIN_USER_EMAIL}',\
            }
    d.setVarFlags("USERCREATE_PARAM_%s-setup" % d.getVar('SRCNAME',True), flags)
}

SERVICECREATE_PARAM_${SRCNAME}-setup = "name type description region publicurl adminurl internalurl"
python () {
    flags = {'type':'identity',\
             'description':'OpenStack Identity',\
             'publicurl':"'http://${KEYSTONE_HOST}:8081/keystone/main/v2.0'",\
             'adminurl':"'http://${KEYSTONE_HOST}:8081/keystone/admin/v2.0'",\
             'internalurl':"'http://${KEYSTONE_HOST}:8081/keystone/main/v2.0'"}
    d.setVarFlags("SERVICECREATE_PARAM_%s-setup" % d.getVar('SRCNAME',True), flags)
}

do_install_append() {

    KEYSTONE_CONF_DIR=${D}${sysconfdir}/keystone
    KEYSTONE_PACKAGE_DIR=${D}${PYTHON_SITEPACKAGES_DIR}/keystone
    APACHE_CONF_DIR=${D}${sysconfdir}/apache2/conf.d/

    # Create directories
    install -m 755 -d ${KEYSTONE_CONF_DIR}
    install -m 755 -d ${APACHE_CONF_DIR}
    install -d ${D}${localstatedir}/log/${SRCNAME}

    # Setup the systemd service file
    install -d ${D}${systemd_unitdir}/system/
    KS_INIT_SERVICE_FILE=${D}${systemd_unitdir}/system/keystone-init.service
    install -m 644 ${WORKDIR}/keystone-init.service ${KS_INIT_SERVICE_FILE}
    sed -e "s:%SYSCONFIGDIR%:${sysconfdir}:g" -i ${KS_INIT_SERVICE_FILE}

    # Setup the keystone initialization script
    KS_INIT_FILE=${KEYSTONE_CONF_DIR}/keystone-init
    install -m 755 ${WORKDIR}/keystone-init ${KS_INIT_FILE}
    sed -e "s:%DB_USER%:${DB_USER}:g" -i ${KS_INIT_FILE}
    sed -e "s:%KEYSTONE_USER%:keystone:g" -i ${KS_INIT_FILE}
    sed -e "s:%KEYSTONE_GROUP%:keystone:g" -i ${KS_INIT_FILE}
    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${KS_INIT_FILE}
    sed -e "s:%ADMIN_USER%:${ADMIN_USER}:g" -i ${KS_INIT_FILE}
    sed -e "s:%ADMIN_PASSWORD%:${ADMIN_PASSWORD}:g" -i ${KS_INIT_FILE}
    sed -e "s:%ADMIN_ROLE%:${ADMIN_ROLE}:g" -i ${KS_INIT_FILE}

    # Setup the admin-openrc file
    KS_OPENRC_FILE=${KEYSTONE_CONF_DIR}/admin-openrc
    install -m 600 ${WORKDIR}/admin-openrc ${KS_OPENRC_FILE}
    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${KS_OPENRC_FILE}
    sed -e "s:%ADMIN_USER%:${ADMIN_USER}:g" -i ${KS_OPENRC_FILE}
    sed -e "s:%ADMIN_PASSWORD%:${ADMIN_PASSWORD}:g" -i ${KS_OPENRC_FILE}

    # Install various configuration files. We have to select suitable
    # permissions as packages such as Apache require read access.
    #
    # Apache needs to read the keystone.conf
    install -m 644 ${WORKDIR}/keystone.conf ${KEYSTONE_CONF_DIR}/
    # Apache needs to read the wsgi-keystone.conf
    install -m 644 ${WORKDIR}/wsgi-keystone.conf \
        ${APACHE_CONF_DIR}/keystone.conf
    install -m 755 ${WORKDIR}/identity.sh ${KEYSTONE_CONF_DIR}/
    install -m 600 ${S}${sysconfdir}/logging.conf.sample \
        ${KEYSTONE_CONF_DIR}/logging.conf
    install -m 600 ${S}${sysconfdir}/keystone.conf.sample \
        ${KEYSTONE_CONF_DIR}/keystone.conf.sample
    install -m 644 ${S}${sysconfdir}/keystone-paste.ini \
        ${KEYSTONE_CONF_DIR}/keystone-paste.ini

    # Copy examples from upstream
    cp -r ${S}/examples ${KEYSTONE_PACKAGE_DIR}

    # Edit the configuration to allow it to work out of the box
    KEYSTONE_CONF_FILE=${KEYSTONE_CONF_DIR}/keystone.conf
    sed "/# admin_endpoint = .*/a \
        public_endpoint = http://%CONTROLLER_IP%:5000/ " \
        -i ${KEYSTONE_CONF_FILE}

    sed "/# admin_endpoint = .*/a \
        admin_endpoint = http://%CONTROLLER_IP%:35357/ " \
        -i ${KEYSTONE_CONF_FILE}
    
    sed -e "s:%SERVICE_TOKEN%:${SERVICE_TOKEN}:g" -i ${KEYSTONE_CONF_FILE}
    sed -e "s:%DB_USER%:${DB_USER}:g" -i ${KEYSTONE_CONF_FILE}
    sed -e "s:%DB_PASSWORD%:${DB_PASSWORD}:g" -i ${KEYSTONE_CONF_FILE}
    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${KEYSTONE_CONF_FILE}
    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${KEYSTONE_CONF_FILE}
    sed -e "s:%TOKEN_FORMAT%:${TOKEN_FORMAT}:g" -i ${KEYSTONE_CONF_FILE}
    
    install -d ${KEYSTONE_PACKAGE_DIR}/tests/tmp
    if [ -e "${KEYSTONE_PACKAGE_DIR}/tests/test_overrides.conf" ];then
        sed -e "s:%KEYSTONE_PACKAGE_DIR%:${PYTHON_SITEPACKAGES_DIR}/keystone:g" \
            -i ${KEYSTONE_PACKAGE_DIR}/tests/test_overrides.conf
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'OpenLDAP', 'true', 'false', d)};
    then
        sed -i -e '/^\[identity\]/a \
driver = keystone.identity.backends.hybrid_identity.Identity \
\
[assignment]\
driver = keystone.assignment.backends.hybrid_assignment.Assignment\
' ${D}${sysconfdir}/keystone/keystone.conf

        sed -i -e '/^\[ldap\]/a \
url = ldap://localhost \
user = cn=Manager,${LDAP_DN} \
password = secret \
suffix = ${LDAP_DN} \
use_dumb_member = True \
\
user_tree_dn = ou=Users,${LDAP_DN} \
user_attribute_ignore = enabled,email,tenants,default_project_id \
user_id_attribute = uid \
user_name_attribute = uid \
user_mail_attribute = email \
user_pass_attribute = keystonePassword \
\
tenant_tree_dn = ou=Groups,${LDAP_DN} \
tenant_desc_attribute = description \
tenant_domain_id_attribute = businessCategory \
tenant_attribute_ignore = enabled \
tenant_objectclass = groupOfNames \
tenant_id_attribute = cn \
tenant_member_attribute = member \
tenant_name_attribute = ou \
\
role_attribute_ignore = enabled \
role_objectclass = groupOfNames \
role_member_attribute = member \
role_id_attribute = cn \
role_name_attribute = ou \
role_tree_dn = ou=Roles,${LDAP_DN} \
' ${KEYSTONE_CONF_FILE}

        install -m 0755 ${WORKDIR}/convert_keystone_backend.py \
            ${D}${sysconfdir}/keystone/convert_keystone_backend.py
    fi
}

# By default tokens are expired after 1 day so by default we can set
# this token flush cronjob to run every 2 days
KEYSTONE_TOKEN_FLUSH_TIME ??= "0 0 */2 * *"

pkg_postinst_${SRCNAME}-cronjobs () {
    if [ -z "$D" ]; then
	# By default keystone expired tokens are not automatic removed out of the
	# database.  So we create a cronjob for cleaning these expired tokens.
	echo "${KEYSTONE_TOKEN_FLUSH_TIME} root /usr/bin/keystone-manage token_flush" >> /etc/crontab
    fi
}

PACKAGES += " ${SRCNAME}-tests ${SRCNAME} ${SRCNAME}-setup ${SRCNAME}-cronjobs"

SYSTEMD_PACKAGES += "${SRCNAME}-setup"
SYSTEMD_SERVICE_${SRCNAME}-setup = "keystone-init.service"

FILES_${SRCNAME}-setup = " \
    ${systemd_unitdir}/system \
    "

ALLOW_EMPTY_${SRCNAME}-cronjobs = "1"

FILES_${PN} = "${libdir}/* \
    "

FILES_${SRCNAME}-tests = "${sysconfdir}/${SRCNAME}/run_tests.sh"

FILES_${SRCNAME} = "${bindir}/* \
    ${sysconfdir}/${SRCNAME}/* \
    ${localstatedir}/* \
    ${datadir}/openstack-dashboard/openstack_dashboard/api/keystone-httpd.py \
    ${sysconfdir}/apache2/conf.d/keystone.conf \
    "

DEPENDS += " \
        python-pip \
        python-pbr \
        "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
        python-pbr-native \
        "

RDEPENDS_${PN} += " \
        python-babel \
        python-pbr \
        python-webob \
        python-pastedeploy \
        python-paste \
        python-routes \
        python-cryptography \
        python-six \
        python-sqlalchemy \
        python-sqlalchemy-migrate \
        python-stevedore \
        python-passlib \
        python-keystoneclient \
        python-keystonemiddleware \
        python-bcrypt \
        python-scrypt \
        python-oslo.cache \
        python-oslo.concurrency \
        python-oslo.config \
        python-oslo.context \
        python-oslo.messaging \
        python-oslo.db \
        python-oslo.i18n \
        python-oslo.log \
        python-oslo.middleware \
        python-oslo.policy \
        python-oslo.serialization \
        python-oslo.utils \
        python-oauthlib \
        python-pysaml2 \
        python-dogpile.cache \
        python-jsonschema \
        python-pycadf \
        python-msgpack \
        python-osprofiler \
        python-pytz \
        "

RDEPENDS_${SRCNAME}-tests += " bash"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'OpenLDAP', 'OpenLDAP', '', d)}"
PACKAGECONFIG[OpenLDAP] = ",,,python-ldap python-keystone-hybrid-backend"

# TODO:
#    if DISTRO_FEATURE contains "tempest" then add *-tests to the main RDEPENDS

RDEPENDS_${SRCNAME} = " \
    ${PN} \
    postgresql \
    postgresql-client \
    python-psycopg2 \
    apache2 \
    "

RDEPENDS_${SRCNAME}-setup = "postgresql sudo ${SRCNAME}"
RDEPENDS_${SRCNAME}-cronjobs = "cronie ${SRCNAME}"

MONITOR_SERVICE_PACKAGES = "${SRCNAME}"
MONITOR_SERVICE_${SRCNAME} = "keystone"
