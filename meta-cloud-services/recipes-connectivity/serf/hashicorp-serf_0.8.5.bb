SUMMARY = "Decentralized Cluster Membership, Failure Detection, and Orchestration." 
DESCRIPTION = "Serf is a decentralized solution for service discovery and \
orchestration that is lightweight, highly available, and fault tolerant.\
\
Serf runs on Linux, Mac OS X, and Windows. An efficient and lightweight gossip \
protocol is used to communicate with other nodes. Serf can detect node failures \
and notify the rest of the cluster. An event system is built on top of Serf, \
letting you use Serf's gossip protocol to propagate events such as deploys, \
configuration changes, etc. Serf is completely masterless with no single point \
of failure."
HOMEPAGE = "https://www.serf.io/"
SECTION = "network"

LICENSE = "Apache-2.0 & BSD-3-Clause & MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=b278a92d2c1509760384428817710378"

require serf-vendor-licenses.inc

GO_IMPORT = "github.com/hashicorp/serf"
SRC_URI = "git://${GO_IMPORT}.git;protocol=https"

PV = "0.8.5+git${SRCPV}"
SRCREV = "1d3fdf93bbe5002c5023da50402368a817488691"

S = "${WORKDIR}/git"

inherit go

RDEPENDS_${PN}-dev += "bash make"

# Apache serf in oe-core is a completely different beast
RCONFLICTS_${PN} = "serf"
