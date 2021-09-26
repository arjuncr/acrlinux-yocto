DESCRIPTION = "The OpenStack Dashboard."
HOMEPAGE = "https://github.com/openstack/horizon/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

DEPENDS += " \
        python-pip \
        python-pbr \
        "

RDEPENDS_${PN} += " \
        python-pbr \
        python-babel \
        python-django \
        python-pint \
        python-django-babel \
        python-django-compressor \
        python-django-openstack-auth \
        python-django-pyscss \
        python-futurist \
        python-iso8601 \
        python-netaddr \
        python-oslo.concurrency \
        python-oslo.config \
        python-oslo.i18n \
        python-oslo.policy \
        python-oslo.serialization \
        python-oslo.utils \
        python-osprofiler \
        python-pymongo \
        python-pyscss \
        python-cinderclient \
        python-glanceclient \
        python-heatclient \
        python-keystoneclient \
        python-neutronclient \
        python-novaclient \
        python-swiftclient \
        python-pytz \
        python-pyyaml \
        python-semantic-version \
        python-six \
        python-xstatic \
        python-xstatic-angular \
        python-xstatic-angular-bootstrap \
        python-xstatic-angular-fileupload \
        python-xstatic-angular-gettext \
        python-xstatic-angular-lrdragndrop \
        python-xstatic-angular-schema-form \
        python-xstatic-bootstrap-datepicker \
        python-xstatic-bootstrap-scss \
        python-xstatic-bootswatch \
        python-xstatic-d3 \
        python-xstatic-hogan \
        python-xstatic-font-awesome \
        python-xstatic-jasmine \
        python-xstatic-jquery \
        python-xstatic-jquery-migrate \
        python-xstatic-jquery.quicksearch \
        python-xstatic-jquery.tablesorter \
        python-xstatic-jquery-ui \
        python-xstatic-jsencrypt \
        python-xstatic-mdi \
        python-xstatic-objectpath \
        python-xstatic-rickshaw \
        python-xstatic-roboto-fontface \
        python-xstatic-smart-table \
        python-xstatic-spin \
        python-xstatic-term.js \
        python-xstatic-tv4 \
        "

SRCNAME = "horizon"

SRC_URI = "git://github.com/openstack/${SRCNAME}.git;branch=stable/pike \
    file://wsgi-horizon.conf \
    file://fix_bindir_path.patch \
    file://local_settings.py \
    file://horizon-use-full-package-path-to-test-directories.patch \
    "
#    file://openstack-dashboard-apache.conf

SRCREV = "246ff9f81248a00a434e66d18fad70519ba811cc"
PV = "12.0.0+git${SRCPV}"
S = "${WORKDIR}/git"

inherit setuptools3 systemd python3-dir default_configs monitor useradd

USER = "horizon"
GROUP = "horizon"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${GROUP}"
USERADD_PARAM_${PN} = "--system -m -d ${localstatedir}/lib/openstack-dashboard -s /bin/false -g ${GROUP} ${USER}"

# no longer required. kept as reference.
# do_install[dirs] += "${D}/usr/share/bin"

do_install_append() {
    SYSCONF_DIR=${D}${sysconfdir}
    DASHBOARD_CONF_DIR=${SYSCONF_DIR}/openstack-dashboard
    DASHBOARD_SHARE_DIR=${D}${datadir}/openstack-dashboard
    APACHE_CONF_DIR=${D}${sysconfdir}/apache2/conf.d/

    # Fixup to allow running standalone. Currently broken.
    DASHBOARD_DIR=${D}${PYTHON_SITEPACKAGES_DIR}/openstack_dashboard
    #sed -e "s:^LANGUAGE_CODE =.*:LANGUAGE_CODE = 'en-us':g" \
    #    -i ${DASHBOARD_DIR}/settings.py
    #sed -e "s:^# from horizon.utils:from horizon.utils:g" \
    #    ${DASHBOARD_DIR}/local/local_settings.py.example > \
    #    ${DASHBOARD_DIR}/local/local_settings.py
    #sed -e "s:^# SECRET_KEY =:SECRET_KEY =:g" \
    #    -i ${DASHBOARD_DIR}/local/local_settings.py
    install -m 644 ${S}/manage.py ${DASHBOARD_DIR}/manage.py

    sed -i -e 's#%PYTHON_SITEPACKAGES%#${PYTHON_SITEPACKAGES_DIR}#g' \
        ${D}${PYTHON_SITEPACKAGES_DIR}/horizon/test/settings.py

    # no longer required. kept as reference.
    # mv ${D}${datadir}/bin ${DASHBOARD_DIR}/bin

    install -d ${DASHBOARD_CONF_DIR}
    cp run_tests.sh ${DASHBOARD_CONF_DIR}

    # The following allows horizon to be run from apache. This
    # is the preffered way to run horizon.
    install -d ${DASHBOARD_SHARE_DIR}
    cp -a --no-preserve=ownership ${S}/openstack_dashboard  ${DASHBOARD_SHARE_DIR}
    cp ${S}/manage.py  ${DASHBOARD_SHARE_DIR}

    # Copy our version of the local-settings.py file, create a convienence link
    # between /etc/openstack-dashboard and the apache2 files installed above,
    # lastly make a few substitions to match our installation locations.
    SETTINGS_FILE=${DASHBOARD_CONF_DIR}/local_settings.py
    install -D -m 644 ${WORKDIR}/local_settings.py ${SETTINGS_FILE}
    # Link only valid on the target.
    ln -fs ${sysconfdir}/openstack-dashboard/local_settings.py \
        ${DASHBOARD_SHARE_DIR}/openstack_dashboard/local/local_settings.py
    # Be sure to edit the real file in ${D}${sysconfdir}
    sed -e "s:%LOCAL_PATH%:${localstatedir}/lib/openstack-dashboard/static:g" \
        -i ${SETTINGS_FILE}

    # Configure apache to run horizon at http://localhost/horizon
    install -m 755 -d ${APACHE_CONF_DIR}
    APACHE_WSGI_FILE=${APACHE_CONF_DIR}/openstack-dashboard.conf
    install -m 644 ${WORKDIR}/wsgi-horizon.conf ${APACHE_WSGI_FILE}
    sed -e "s#%DATADIR%#${datadir}#g" -i ${APACHE_WSGI_FILE}
    sed -e "s#%USER%#${USER}#g" -i ${APACHE_WSGI_FILE}
    sed -e "s#%GROUP%#${GROUP}#g" -i ${APACHE_WSGI_FILE}
    sed -e "s#%LOCALSTATEDIR%#${localstatedir}#g" -i ${APACHE_WSGI_FILE}

    #HZ_SETTINGS_FILE=${DASHBOARD_SHARE_DIR}/openstack_dashboard/settings.py
    #sed -e "s:^\(WEBROOT\ =\ \).*$:\1'/horizon':" -i ${HZ_SETTINGS_FILE}
    #sed -e "s:^\(STATIC_ROOT\ =\ \).*$:\1'${localstatedir}/lib/openstack-dashboard/static':" \
    #    -i ${HZ_SETTINGS_FILE}

    ln -fs openstack_dashboard/static ${DASHBOARD_SHARE_DIR}/static
}

pkg_postinst_${SRCNAME} () {
    if [ -n "$D" ]; then
        exit 1
    else
        # Regenerate the django static files
        sudo -u horizon /usr/bin/env python ${datadir}/openstack-dashboard/manage.py collectstatic --noinput --clear 
    fi
}

PACKAGES += "${SRCNAME}-tests ${SRCNAME} ${SRCNAME}-apache ${SRCNAME}-standalone"
ALLOW_EMPTY_${SRCNAME}-standalone = "1"

RDEPENDS_${SRCNAME}-tests += " bash"

FILES_${PN} = "${libdir}/*"

FILES_${SRCNAME}-tests = "${sysconfdir}/openstack-dashboard/run_tests.sh"

FILES_${SRCNAME} = "${bindir}/* \
    ${datadir}/* \
    "

FILES_${SRCNAME}-standalone = "${sysconfdir}/init.d/horizon"

FILES_${SRCNAME}-apache = " \
    ${sysconfdir}/apache2 \
    ${sysconfdir}/openstack-dashboard/ \
    ${datadir}/openstack-dashboard/ \
    "

RDEP_ARCH_VAR = ""
RDEP_ARCH_VAR_arm = "nodejs"
RDEP_ARCH_VAR_i686 = "nodejs"
RDEP_ARCH_VAR_x86-64 = "nodejs"
RDEP_ARCH_VAR_ia32 = "nodejs"

RDEPENDS_${PN} += " \
    ${RDEP_ARCH_VAR} \
    "

RDEPENDS_${SRCNAME} = "${PN}"

RDEPENDS_${SRCNAME}-apache = "\
    apache2 \
    mod-wsgi \
    python-lesscpy \
    memcached \
    python-memcached \
    "

MONITOR_SERVICE_PACKAGES = "${SRCNAME}"
MONITOR_SERVICE_${SRCNAME} = "horizon"

CLEANBROKEN = "1"

