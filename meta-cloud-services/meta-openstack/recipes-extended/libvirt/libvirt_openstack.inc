PACKAGECONFIG ?= "qemu lxc test remote macvtap libvirtd udev yajl \
	 	 python ebtables \
		 ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)} \
		"

inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN}_append = " ;--system libvirt"

do_install_append() {
	sed -e "s:^#unix_sock_group =:unix_sock_group =:g" -i ${D}/etc/libvirt/libvirtd.conf
	sed -e "s:^#unix_sock_rw_perms =:unix_sock_rw_perms =:g" -i ${D}/etc/libvirt/libvirtd.conf
}
