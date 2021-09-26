DESCRIPTION = "Nova is a cloud computing fabric controller"
HOMEPAGE = "https://launchpad.net/nova"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

DEPENDS = " \
    sudo \
    libvirt \
"

SRCNAME = "nova"

FILESEXTRAPATHS_append := "${THISDIR}/${PN}"

SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike \
           file://neutron-api-set-default-binding-vnic_type.patch \
           "

SRC_URI += " \
            file://nova-compute.service \
            file://nova-init.service \
            file://nova-init \
            file://nova.conf \
            file://openrc \
            file://nova-console.service \
            file://nova-consoleauth.service \
            file://nova-xvpvncproxy.service \
            file://nova-novncproxy.service \
            file://nova-conductor.service \
            file://nova-network.service \
            file://nova-api.service \
            file://nova-scheduler.service \
            file://nova-spicehtml5proxy.service \
           "
SRCREV = "b535f0808526c8eba37f15e83cede536e4e06029"
PV = "16.0.4+git${SRCPV}"

S = "${WORKDIR}/git"

inherit systemd setuptools3 identity hosts useradd default_configs monitor

LIBVIRT_IMAGES_TYPE ?= "default"

USER = "nova"
GROUP = "nova"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${GROUP}"
USERADD_PARAM_${PN} = "--system -m -d ${localstatedir}/lib/nova -s /bin/false -g ${GROUP} ${USER}"

# Need to create the user?
PLACEMENT_USER = "placement"

SERVICECREATE_PACKAGES = "${SRCNAME}-setup ${SRCNAME}-ec2"
KEYSTONE_HOST="${CONTROLLER_IP}"

# USERCREATE_PARAM and SERVICECREATE_PARAM contain the list of parameters to be set.
# If the flag for a parameter in the list is not set here, the default value will be given to that parameter.
# Parameters not in the list will be set to empty.

USERCREATE_PARAM_${SRCNAME}-setup = "name pass tenant role email"
SERVICECREATE_PARAM_${SRCNAME}-setup = "name type description region publicurl adminurl internalurl"
python () {
    flags = {'type':'compute',\
             'description':'OpenStack Compute Service',\
             'publicurl':"'http://${KEYSTONE_HOST}:8774/v2/\$(tenant_id)s'",\
             'adminurl':"'http://${KEYSTONE_HOST}:8774/v2/\$(tenant_id)s'",\
             'internalurl':"'http://${KEYSTONE_HOST}:8774/v2/\$(tenant_id)s'"}
    d.setVarFlags("SERVICECREATE_PARAM_%s-setup" % d.getVar('SRCNAME',True), flags)
}

# ec2 is provided by nova-api
SERVICECREATE_PARAM_${SRCNAME}-ec2 = "name type description region publicurl adminurl internalurl"
python () {
    flags = {'name':'ec2',\
             'type':'ec2',\
             'description':'OpenStack EC2 Service',\
             'publicurl':"'http://${KEYSTONE_HOST}:8773/services/Cloud'",\
             'adminurl':"'http://${KEYSTONE_HOST}:8773/services/Admin'",\
             'internalurl':"'http://${KEYSTONE_HOST}:8773/services/Cloud'"}
    d.setVarFlags("SERVICECREATE_PARAM_%s-ec2" % d.getVar('SRCNAME',True), flags)
}

do_install_append() {
    if [ ! -f "${WORKDIR}/nova.conf" ]; then
        return
    fi

    TEMPLATE_CONF_DIR=${S}${sysconfdir}/${SRCNAME}
    NOVA_CONF_DIR=${D}/${sysconfdir}/nova

    install -d ${NOVA_CONF_DIR}

    # install systemd service files
    install -d ${D}${systemd_system_unitdir}/
    for j in nova-api nova-compute nova-init nova-network nova-console nova-consoleauth \
             nova-xvpvncproxy nova-novncproxy nova-conductor nova-scheduler \
	     nova-spicehtml5proxy
    do
        SERVICE_FILE=${D}${systemd_system_unitdir}/$j.service
        install -m 644 ${WORKDIR}/$j.service ${SERVICE_FILE}
	sed -e "s#%LOCALSTATEDIR%#${localstatedir}#g" -i ${SERVICE_FILE}
	sed -e "s#%SYSCONFDIR%#${sysconfdir}#g" -i ${SERVICE_FILE}
    done

    # Setup the neutron initialization script
    INIT_FILE=${NOVA_CONF_DIR}/nova-init
    install -g ${USER} -m 750 ${WORKDIR}/nova-init ${INIT_FILE}
    sed -e "s:%DB_USER%:${DB_USER}:g" -i ${INIT_FILE}
    sed -e "s:%NOVA_USER%:${USER}:g" -i ${INIT_FILE}
    sed -e "s:%NOVA_GROUP%:${GROUP}:g" -i ${INIT_FILE}
    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_USER%:${ADMIN_USER}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_PASSWORD%:${ADMIN_PASSWORD}:g" -i ${INIT_FILE}
    sed -e "s:%ADMIN_ROLE%:${ADMIN_ROLE}:g" -i ${INIT_FILE}
    sed -e "s:%SYSCONFDIR%:${sysconfdir}:g" -i ${INIT_FILE}
    sed -e "s:%ROOT_HOME%:${ROOT_HOME}:g" -i ${INIT_FILE}
    sed -e "s:%PLACEMENT_USER%:${PLACEMENT_USER}:g" -i ${INIT_FILE}

    # Deploy filters to /etc/nova/rootwrap.d
    install -m 755 -d ${NOVA_CONF_DIR}/rootwrap.d
    install -m 600 ${S}/etc/nova/rootwrap.d/*.filters ${NOVA_CONF_DIR}/rootwrap.d
    chown -R root:root ${NOVA_CONF_DIR}/rootwrap.d
    chmod 644 ${NOVA_CONF_DIR}/rootwrap.d

    # Set up rootwrap.conf, pointing to /etc/nova/rootwrap.d
    install -m 644 ${S}/etc/nova/rootwrap.conf ${NOVA_CONF_DIR}/
    sed -e "s:^filters_path=.*$:filters_path=${sysconfdir}/nova/rootwrap.d:" \
        -i ${NOVA_CONF_DIR}/rootwrap.conf
    chown root:root $NOVA_CONF_DIR/rootwrap.conf

    # Set up the rootwrap sudoers for nova
    install -d -m 750 ${D}${sysconfdir}/sudoers.d
    touch ${D}${sysconfdir}/sudoers.d/nova-rootwrap
    chmod 0440 ${D}${sysconfdir}/sudoers.d/nova-rootwrap
    chown root:root ${D}${sysconfdir}/sudoers.d/nova-rootwrap
    # root user setup
    echo "root ALL=(root) NOPASSWD: ${bindir}/nova-rootwrap" > \
        ${D}${sysconfdir}/sudoers.d/nova-rootwrap
    # nova user setup
    echo "nova ALL=(root) NOPASSWD: ${bindir}/nova-rootwrap ${sysconfdir}/nova/rootwrap.conf *" >> \
         ${D}${sysconfdir}/sudoers.d/nova-rootwrap

    # Copy the configuration file
    install -o nova -m 664 ${WORKDIR}/nova.conf               ${NOVA_CONF_DIR}/nova.conf
    install -o nova -m 664 ${TEMPLATE_CONF_DIR}/api-paste.ini ${NOVA_CONF_DIR}
    install -o nova -m 664 ${WORKDIR}/openrc                  ${NOVA_CONF_DIR}

    # openrc substitutions
    sed -e "s:%OS_PASSWORD%:${ADMIN_PASSWORD}:g" -i ${NOVA_CONF_DIR}/openrc
    sed -e "s:%SERVICE_TOKEN%:${SERVICE_TOKEN}:g" -i ${NOVA_CONF_DIR}/openrc
    sed -e "s:%CONTROLLER_IP%:${CONTROLLER_IP}:g" -i ${NOVA_CONF_DIR}/openrc
    sed -e "s:%CONTROLLER_HOST%:${CONTROLLER_HOST}:g" -i ${NOVA_CONF_DIR}/openrc

    install -o nova -d ${NOVA_CONF_DIR}/instances

    #
    # Per https://docs.openstack.org/nova/pike/install/controller-install-ubuntu.html
    #
    CONF_FILE="${NOVA_CONF_DIR}/nova.conf"
    sed -e "/^\[api_database\]/aconnection = postgresql+psycopg2://${DB_USER}:${DB_PASSWORD}@${CONTROLLER_IP}/nova-api" \
           -i ${CONF_FILE}
    sed -e "/^\[database\]/aconnection = postgresql+psycopg2://${DB_USER}:${DB_PASSWORD}@${CONTROLLER_IP}/nova" \
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
    sed -e "/#use_neutron =/ause_neutron = true" -i ${CONF_FILE}
    sed -e "/#firewall_driver =/afirewall_driver = nova.virt.firewall.NoopFirewallDriver" -i ${CONF_FILE}

    sed -e "/^\[vnc\].*/aenabled = true" -i ${CONF_FILE}
    sed -e "/#vncserver_listen =/avncserver_listen = ${MY_IP}" -i ${CONF_FILE}
    sed -e "/#vncserver_proxyclient_address =/avncserver_proxyclient_address = ${MY_IP}" -i ${CONF_FILE}

    sed -e "/#api_servers =/aapi_servers = ${CONTROLLER_IP}:9292" -i ${CONF_FILE}
    sed -e "/#lock_path =/alock_path = /var/lib/nova/tmp" -i ${CONF_FILE}

    # Configure cinder
    sed -e "/^\[cinder\].*/aos_region_name = RegionOne" -i ${CONF_FILE}

    str="os_region_name = RegionOne"
    str="$str\nproject_domain_name = Default"
    str="$str\nproject_name = service"
    str="$str\nauth_type = password"
    str="$str\nuser_domain_name = Default"
    str="$str\nauth_url = http://${CONTROLLER_IP}:35357"
    str="$str\nusername = ${PLACEMENT_USER}"
    str="$str\npassword = ${ADMIN_PASSWORD}"
    sed -e "/^\[placement\].*/a$str" -i ${CONF_FILE}

    # Install bash completions, docs and plugins
    install -d ${D}/${sysconfdir}/bash_completion.d
    install -m 664 ${S}/tools/nova-manage.bash_completion ${D}/${sysconfdir}/bash_completion.d

    cp -r "${S}/doc" "${D}/${PYTHON_SITEPACKAGES_DIR}/nova"
    cp -r "${S}/plugins" "${D}/${PYTHON_SITEPACKAGES_DIR}/nova"
}

PACKAGES += " ${SRCNAME}-tests"
PACKAGES += " ${SRCNAME}-setup"
PACKAGES += " ${SRCNAME}-common"
PACKAGES += " ${SRCNAME}-compute"
PACKAGES += " ${SRCNAME}-controller"
PACKAGES += " ${SRCNAME}-console"
PACKAGES += " ${SRCNAME}-novncproxy"
PACKAGES += " ${SRCNAME}-spicehtml5proxy"
PACKAGES += " ${SRCNAME}-network"
PACKAGES += " ${SRCNAME}-scheduler"
PACKAGES += " ${SRCNAME}-conductor"
PACKAGES += " ${SRCNAME}-api"
PACKAGES += " ${SRCNAME}-ec2"

PACKAGECONFIG ?= "bash-completion"
PACKAGECONFIG[bash-completion] = ",,bash-completion,bash-completion python-nova-bash-completion"

PACKAGES =+ "${BPN}-bash-completion"
FILES_${BPN}-bash-completion = "${sysconfdir}/bash_completion.d/*"


ALLOW_EMPTY_${SRCNAME}-setup = "1"
ALLOW_EMPTY_${SRCNAME}-ec2 = "1"
ALLOW_EMPTY_${SRCNAME}-api = "1"
ALLOW_EMPTY_${SRCNAME}-compute = "1"
ALLOW_EMPTY_${SRCNAME}-controller = "1"
ALLOW_EMPTY_${SRCNAME}-console = "1"
ALLOW_EMPTY_${SRCNAME}-conductor = "1"
ALLOW_EMPTY_${SRCNAME}-network = "1"
ALLOW_EMPTY_${SRCNAME}-novncproxy = "1"
ALLOW_EMPTY_${SRCNAME}-scheduler = "1"
ALLOW_EMPTY_${SRCNAME}-spicehtml5proxy = "1"



FILES_${PN} = "${libdir}/*"

# MAA FILES_${SRCNAME}-tests = "${sysconfdir}/${SRCNAME}/run_tests.sh"
FILES_${SRCNAME}-tests = ""

FILES_${SRCNAME}-common = "${bindir}/nova-manage \
                           ${bindir}/nova-rootwrap \
                           ${sysconfdir}/${SRCNAME}/* \
                           ${sysconfdir}/sudoers.d"

FILES_${SRCNAME}-compute = "${bindir}/nova-compute \
                            ${sysconfdir}/init.d/nova-compute"

FILES_${SRCNAME}-controller = "${bindir}/* \
 			       ${sysconfdir}/init.d/nova-all "

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
		   libvirt \
		   python-pbr \
		   python-sqlalchemy \
		   python-decorator \
		   python-eventlet \
		   python-jinja2 \
		   python-keystonemiddleware \
		   python-lxml \
		   python-routes \
		   python-cryptography \
		   python-webob \
		   python-greenlet \
		   python-pastedeploy \
		   python-paste \
		   python-prettytable \
		   python-sqlalchemy-migrate \
		   python-netaddr \
		   python-netifaces \
		   python-paramiko \
		   python-babel \
		   python-iso8601 \
		   python-jsonschema \
		   python-cinderclient \
		   python-keystoneauth1 \
		   python-neutronclient \
		   python-glanceclient \
		   python-requests \
		   python-six \
		   python-stevedore \
		   python-setuptools3 \
		   python-websockify \
		   python-oslo.cache \
		   python-oslo.concurrency \
		   python-oslo.config \
		   python-oslo.context \
		   python-oslo.log \
		   python-oslo.reports \
		   python-oslo.serialization \
		   python-oslo.utils \
		   python-oslo.db \
		   python-oslo.rootwrap \
		   python-oslo.messaging \
		   python-oslo.policy \
		   python-oslo.privsep \
		   python-oslo.i18n \
		   python-oslo.service \
		   python-rfc3986 \
		   python-oslo.middleware \
		   python-psutil \
		   python-oslo.versionedobjects \
		   python-os-brick \
		   python-os-traits \
		   python-os-vif \
		   python-os-win \
		   python-castellan \
		   python-microversion-parse \
		   python-os-xenapi \
		   python-tooz \
		   python-cursive \
		   python-pypowervm \
   "

RDEPENDS_${SRCNAME}-common = "${PN} openssl openssl-misc libxml2 libxslt \
                              iptables curl dnsmasq sudo procps"

RDEPENDS_${SRCNAME}-controller = "${PN} ${SRCNAME}-common \
				  ${SRCNAME}-ec2 \
				  ${SRCNAME}-console \
				  ${SRCNAME}-novncproxy \
				  ${SRCNAME}-spicehtml5proxy \
				  ${SRCNAME}-network \
				  ${SRCNAME}-scheduler \
				  ${SRCNAME}-conductor \
                                  ${SRCNAME}-api \
				  postgresql postgresql-client python-psycopg2"

RDEPENDS_${SRCNAME}-compute = "${PN} ${SRCNAME}-common python-oslo.messaging \
			       qemu libvirt libvirt-libvirtd libvirt-python libvirt-virsh"
RDEPENDS_${SRCNAME}-setup = "postgresql sudo ${SRCNAME}-common"
RDEPENDS_${SRCNAME}-ec2 = "postgresql sudo ${SRCNAME}-common"

RDEPENDS_${SRCNAME}-tests = " \
                            python-coverage \
                            bash \
                            "


SYSTEMD_PACKAGES = " \
    ${SRCNAME}-setup \
    ${SRCNAME}-compute \
    ${SRCNAME}-console \
    ${SRCNAME}-novncproxy \
    ${SRCNAME}-spicehtml5proxy \
    ${SRCNAME}-network \
    ${SRCNAME}-scheduler \
    ${SRCNAME}-conductor \
    ${SRCNAME}-api \
    "

SYSTEMD_SERVICE_${SRCNAME}-setup = "nova-init.service"
SYSTEMD_SERVICE_${SRCNAME}-compute = "nova-compute.service"
SYSTEMD_SERVICE_${SRCNAME}-console = "nova-console.service nova-consoleauth.service nova-xvpvncproxy.service"
SYSTEMD_SERVICE_${SRCNAME}-novncproxy = "nova-novncproxy.service"
SYSTEMD_SERVICE_${SRCNAME}-spicehtml5proxy = "nova-spicehtml5proxy.service"
SYSTEMD_SERVICE_${SRCNAME}-network = "nova-network.service"
SYSTEMD_SERVICE_${SRCNAME}-scheduler = "nova-scheduler.service"
SYSTEMD_SERVICE_${SRCNAME}-conductor = "nova-conductor.service"
SYSTEMD_SERVICE_${SRCNAME}-api = "nova-api.service"

# Disable services on first boot to avoid having them run when not configured
SYSTEMD_AUTO_ENABLE_${SRCNAME}-api = "disable"
SYSTEMD_AUTO_ENABLE_${SRCNAME}-consoleauth = "disable"
SYSTEMD_AUTO_ENABLE_${SRCNAME}-scheduler = "disable"
SYSTEMD_AUTO_ENABLE_${SRCNAME}-conductor = "disable"
SYSTEMD_AUTO_ENABLE_${SRCNAME}-novncproxy = "disable"

MONITOR_SERVICE_PACKAGES = "${SRCNAME}"
MONITOR_SERVICE_${SRCNAME} = "nova-api nova-conductor nova-console nova-scheduler"
