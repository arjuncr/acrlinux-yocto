SUMMARY = "Configuration for OpenStack Controller node"
PR = "r0"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup

RDEPENDS_${PN} = " postgresql \
    postgresql-client \
    python-psycopg2 \
    rabbitmq-server \
    nova-setup \
    nova-controller \
    keystone-setup \
    glance \
    glance-api \
    glance-registry \
    glance-setup \
    keystone \
    keystone-cronjobs \
    neutron-setup \
    neutron-server \
    neutron-plugin-openvswitch \
    cinder-api \
    cinder-volume \
    cinder-scheduler \
    cinder-backup \
    cinder-setup \
    swift \
    swift-setup \
    ceilometer-setup \
    ceilometer-api \
    ceilometer-collector \
    ceilometer-controller \
    heat-api \
    heat-api-cfn \
    heat-engine \
    heat-setup \
    python-heat-cfntools \
    python-openstackclient \
    horizon \
    horizon-standalone \
    horizon-apache \
    apache2 \
    barbican \
    trove \
    novnc \
    chkconfig \
    qemu \
    fuse \
    ${@bb.utils.contains('CINDER_EXTRA_FEATURES', 'glusterfs', 'glusterfs glusterfs-fuse glusterfs-server', '', d)} \
    ${@bb.utils.contains('CINDER_EXTRA_FEATURES', 'ceph', 'packagegroup-ceph ceph-setup xfsprogs', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'OpenLDAP', 'openldap python-ldap cyrus-sasl nss-pam-ldapd pam-plugin-mkhomedir python-keystone-hybrid-backend', '', d)} \
    ${@bb.utils.contains('OPENSTACK_EXTRA_FEATURES', 'monitoring', 'packagegroup-monitoring-core', '', d)} \
    "

RRECOMMENDS_${PN} = " \
    kernel-module-fuse \
    cloud-init \
    kernel-module-softdog \
    kernel-module-openvswitch \
    "
