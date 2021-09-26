SRC_URI = "https://bits.gluster.org/pub/gluster/glusterfs/src/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "18967c357204d4cbdd9c1731508862c6"
SRC_URI[sha256sum] = "32ac75c883cdf18e081893ce5210b2331f1ee9ba25e3f3f56136d9878b194dc7"

require glusterfs.inc
