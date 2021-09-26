SUMMARY = "Configuration for OpenStack Compute node"
PR = "r0"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup

RDEPENDS_${PN} = " \
    cloud-init \
    postgresql \
    qemu \
    libvirt \
    libvirt-libvirtd \
    libvirt-virsh \
    nova-compute \
    ceilometer-compute \
    python-novaclient \
    neutron-plugin-openvswitch \
    openvswitch-switch \
    troveclient \
    ${@bb.utils.contains('CINDER_EXTRA_FEATURES', 'open-iscsi-user', 'open-iscsi-user', '', d)} \
    ${@bb.utils.contains('CINDER_EXTRA_FEATURES', 'iscsi-initiator-utils', 'iscsi-initiator-utils', '', d)} \
    nfs-utils-client \
    fuse \
    ${@bb.utils.contains('CINDER_EXTRA_FEATURES', 'glusterfs', 'glusterfs glusterfs-fuse', '', d)} \
    ${@bb.utils.contains('CINDER_EXTRA_FEATURES', 'ceph', 'packagegroup-ceph xfsprogs', '', d)} \
    ${@bb.utils.contains('OPENSTACK_EXTRA_FEATURES', 'monitoring', 'packagegroup-monitoring-agent', '', d)} \
    "

RRECOMMENDS_${PN} = " \
    kernel-module-kvm \
    kernel-module-kvm-intel \
    kernel-module-kvm-amd \
    kernel-module-nbd \
    kernel-module-libiscsi-tcp \
    kernel-module-iscsi-boot-sysfs \
    kernel-module-iscsi-tcp \
    kernel-module-libiscsi \
    kernel-module-fuse \
    kernel-module-softdog \
    kernel-module-veth \
    kernel-module-openvswitch \
    "
