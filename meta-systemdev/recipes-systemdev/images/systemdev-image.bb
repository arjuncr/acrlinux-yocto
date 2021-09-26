DESCRIPTION = "System development image"
LICENSE = "MIT"

inherit core-image features_check releaseinfo

IMAGE_FEATURES += "package-management \
                   debug-tweaks \
                   tools-testapps \
                   tools-debug \
                   tools-profile \
                   ssh-server-openssh \
                  "

# dpdk recipe specifies list of compatible machines so we can only include
# it in images built for supported machine
# This adds a layer dependency on meta-dpdk
# http://git.yoctoproject.org/cgit/cgit.cgi/meta-dpdk/
IMAGE_INSTALL_append_intel-corei7-64 = " dpdk"

# mesa and weston need the following in local.conf or similar:
# DISTRO_FEATURES_append = " opengl vulkan wayland"
# netperf needs non-commercial license whitelisted:
# LICENSE_FLAGS_WHITELIST_append = " non-commercial"
IMAGE_INSTALL += "pciutils usbutils dmidecode \
                  acpica \
                  alsa-utils \
                  kernel-modules \
                  ethtool \
                  powertop \
                  pm-graph \
                  rt-tests \
                  i2c-tools \
                  fb-test \
                  evtest \
                  screen minicom \
                  hdperf \
                  iperf3 \
                  vim \
                  python3-pyserial \
                  tmux \
                  lmbench \
                  bonnie++ \
                  ltp \
                  bootchart \
                  netperf \
                  git \
                  python3-sqlalchemy \
                  python3-twisted \
                  connman \
                  mesa \
                  libdrm \
                  cpuid \
                  phoronix-test-suite \
                  turbostat \
                  iotools \
                  watts-up \
                  mesa-megadriver \
                  weston \
                  nfs-utils-client \
                  procps \
                  time \
                  cifs-utils \
                  thermald \
                  sysstat \
                  numactl \
                  btrfs-tools \
                  xfsprogs \
                  xfsdump \
                  f2fs-tools \
                  mdadm \
                  psmisc \
                  ndctl \
                  util-linux \
                 "
