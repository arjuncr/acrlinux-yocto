FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto:"

SRC_URI += "file://enable-veth.cfg \
            file://enable-iscsi-tcp.cfg \
            file://enable-nbd.cfg \
            file://enable-rtlink.cfg \
            file://nf.scc \
            file://nfs.scc \
	"
