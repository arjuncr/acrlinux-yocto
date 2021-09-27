#!/usr/bin/env bash
#
# Kali Linux ARM build-script for Chromebook (Samsung - Exynos)
# https://gitlab.com/kalilinux/build-scripts/kali-arm
#
# This is a community script - you will need to generate your own image to use
# More information: https://www.kali.org/docs/arm/samsung-chromebook/
#

# Stop on error
set -e

# Uncomment to activate debug
# debug=true

if [ "$debug" = true ]; then
  exec > >(tee -a -i "${0%.*}.log") 2>&1
  set -x
fi

# Architecture
architecture=${architecture:-"armhf"}
# Generate a random machine name to be used
machine=$(tr -cd 'A-Za-z0-9' < /dev/urandom | head -c16 ; echo)
# Custom hostname variable
hostname=${2:-kali}
# Custom image file name variable - MUST NOT include .img at the end
image_name=${3:-kali-linux-$1-chromebook-exynos}
# Suite to use, valid options are:
# kali-rolling, kali-dev, kali-bleeding-edge, kali-dev-only, kali-experimental, kali-last-snapshot
suite=${suite:-"kali-rolling"}
# Free space rootfs in MiB
free_space="300"
# /boot partition in MiB
bootsize="128"
# Select compression, xz or none
compress="xz"
# Choose filesystem format to format (ext3 or ext4)
fstype="ext3"
# If you have your own preferred mirrors, set them here
mirror=${mirror:-"http://http.kali.org/kali"}
# GitLab URL Kali repository
kaligit="https://gitlab.com/kalilinux"
# GitHub raw URL
githubraw="https://raw.githubusercontent.com"

# Check EUID=0 you can run any binary as root
if [[ $EUID -ne 0 ]]; then
  echo "This script must be run as root or have super user permissions" >&2
  echo "Use: sudo $0 ${1:-2.0} ${2:-kali}" >&2
  exit 1
fi

# Pass version number
if [[ $# -eq 0 ]] ; then
  echo "Please pass version number, e.g. $0 2.0, and (if you want) a hostname, default is kali" >&2
  exit 0
fi

# Check exist bsp directory
if [ ! -e "bsp" ]; then
  echo "Error: missing bsp directory structure" >&2
  echo "Please clone the full repository ${kaligit}/build-scripts/kali-arm" >&2
  exit 255
fi

# Current directory
current_dir="$(pwd)"
# Base directory
base_dir=${current_dir}/exynos-"$1"
# Working directory
work_dir="${base_dir}/kali-${architecture}"

# Check directory build
if [ -e "${base_dir}" ]; then
  echo "${base_dir} directory exists, will not continue" >&2
  exit 1
elif [[ ${current_dir} =~ [[:space:]] ]]; then
  echo "The directory "\"${current_dir}"\" contains whitespace. Not supported." >&2
  exit 1
else
  echo "The base_dir thinks it is: ${base_dir}"
  mkdir -p ${base_dir}
fi

components="main,contrib,non-free"
arm="kali-linux-arm ntpdate"
base="apt-transport-https apt-utils bash-completion console-setup dialog e2fsprogs firmware-samsung ifupdown initramfs-tools inxi iw man-db mlocate netcat-traditional net-tools parted psmisc rfkill screen tmux unrar usbutils vim wget whiptail zerofree"
desktop="kali-desktop-xfce kali-root-login xserver-xorg-video-fbdev xserver-xorg-input-libinput xserver-xorg-input-synaptics xfonts-terminus xinput"
tools="kali-linux-default"
services="apache2 atftpd"
extras="alsa-utils bc bison bluez bluez-firmware kali-linux-core libnss-systemd libssl-dev triggerhappy"

packages="${arm} ${base} ${services}"

kernel_release="R71-11151.B-chromeos-3.8"

# Automatic configuration to use an http proxy, such as apt-cacher-ng
# You can turn off automatic settings by uncommenting apt_cacher=off
# apt_cacher=off
# By default the proxy settings are local, but you can define an external proxy
# proxy_url="http://external.intranet.local"
apt_cacher=${apt_cacher:-"$(lsof -i :3142|cut -d ' ' -f3 | uniq | sed '/^\s*$/d')"}
if [ -n "$proxy_url" ]; then
  export http_proxy=$proxy_url
elif [ "$apt_cacher" = "apt-cacher-ng" ] ; then
  if [ -z "$proxy_url" ]; then
    proxy_url=${proxy_url:-"http://127.0.0.1:3142/"}
    export http_proxy=$proxy_url
  fi
fi

# Detect architecture
if [[ "${architecture}" == "arm64" ]]; then
        qemu_bin="/usr/bin/qemu-aarch64-static"
        lib_arch="aarch64-linux-gnu"
elif [[ "${architecture}" == "armhf" ]]; then
        qemu_bin="/usr/bin/qemu-arm-static"
        lib_arch="arm-linux-gnueabihf"
elif [[ "${architecture}" == "armel" ]]; then
        qemu_bin="/usr/bin/qemu-arm-static"
        lib_arch="arm-linux-gnueabi"
fi

# create the rootfs - not much to modify here, except maybe throw in some more packages if you want
eatmydata debootstrap --foreign --keyring=/usr/share/keyrings/kali-archive-keyring.gpg --include=kali-archive-keyring,eatmydata \
  --components=${components} --arch ${architecture} ${suite} ${work_dir} http://http.kali.org/kali

# systemd-nspawn environment
systemd-nspawn_exec(){
  LANG=C systemd-nspawn -q --bind-ro ${qemu_bin} -M ${machine} -D ${work_dir} "$@"
}

# We need to manually extract eatmydata to use it for the second stage
for archive in ${work_dir}/var/cache/apt/archives/*eatmydata*.deb; do
  dpkg-deb --fsys-tarfile "$archive" > ${work_dir}/eatmydata
  tar -xkf ${work_dir}/eatmydata -C ${work_dir}
  rm -f ${work_dir}/eatmydata
done

# Prepare dpkg to use eatmydata
systemd-nspawn_exec dpkg-divert --divert /usr/bin/dpkg-eatmydata --rename --add /usr/bin/dpkg

cat > ${work_dir}/usr/bin/dpkg << EOF
#!/bin/sh
if [ -e /usr/lib/${lib_arch}/libeatmydata.so ]; then
    [ -n "\${LD_PRELOAD}" ] && LD_PRELOAD="\$LD_PRELOAD:"
    LD_PRELOAD="\$LD_PRELOAD\$so"
fi
for so in /usr/lib/${lib_arch}/libeatmydata.so; do
    [ -n "\$LD_PRELOAD" ] && LD_PRELOAD="\$LD_PRELOAD:"
    LD_PRELOAD="\$LD_PRELOAD\$so"
done
export LD_PRELOAD
exec "\$0-eatmydata" --force-unsafe-io "\$@"
EOF
chmod 0755 ${work_dir}/usr/bin/dpkg

# debootstrap second stage
systemd-nspawn_exec eatmydata /debootstrap/debootstrap --second-stage

cat << EOF > ${work_dir}/etc/apt/sources.list
deb ${mirror} ${suite} ${components//,/ }
#deb-src ${mirror} ${suite} ${components//,/ }
EOF

# Set hostname
echo "${hostname}" > ${work_dir}/etc/hostname

# So X doesn't complain, we add kali to hosts
cat << EOF > ${work_dir}/etc/hosts
127.0.0.1       ${hostname}    localhost
::1             localhost ip6-localhost ip6-loopback
fe00::0         ip6-localnet
ff00::0         ip6-mcastprefix
ff02::1         ip6-allnodes
ff02::2         ip6-allrouters
EOF

# Disable IPv6
cat << EOF > ${work_dir}/etc/modprobe.d/ipv6.conf
# Don't load ipv6 by default
alias net-pf-10 off
EOF

cat << EOF > ${work_dir}/etc/network/interfaces
auto lo
iface lo inet loopback
EOF

# DNS server
echo "nameserver ${nameserver}" > "${work_dir}"/etc/resolv.conf

# Copy directory bsp into build dir
cp -rp bsp ${work_dir}

export MALLOC_CHECK_=0 # workaround for LP: #520465

# Enable the use of http proxy in third-stage in case it is enabled
if [ -n "$proxy_url" ]; then
  echo "Acquire::http { Proxy \"$proxy_url\" };" > ${work_dir}/etc/apt/apt.conf.d/66proxy
fi

# Third stage
cat << EOF >  ${work_dir}/third-stage
#!/usr/bin/env bash
set -e
export DEBIAN_FRONTEND=noninteractive

eatmydata apt-get update

eatmydata apt-get -y install binutils ca-certificates console-common cryptsetup-bin git initramfs-tools less locales nano u-boot-tools

# Create kali user with kali password... but first, we need to manually make some groups because they don't yet exist..
# This mirrors what we have on a pre-installed VM, until the script works properly to allow end users to set up their own... user
# However we leave off floppy, because who a) still uses them, and b) attaches them to an SBC!?
# And since a lot of these have serial devices of some sort, dialout is added as well
# scanner, lpadmin and bluetooth have to be added manually because they don't
# yet exist in /etc/group at this point
groupadd -r -g 118 bluetooth
groupadd -r -g 113 lpadmin
groupadd -r -g 122 scanner
groupadd -g 1000 kali

useradd -m -u 1000 -g 1000 -G sudo,audio,bluetooth,cdrom,dialout,dip,lpadmin,netdev,plugdev,scanner,video,kali -s /bin/bash kali
echo "kali:kali" | chpasswd

aptops="--allow-change-held-packages -o dpkg::options::=--force-confnew -o Acquire::Retries=3"

# This looks weird, but we do it twice because every so often, there's a failure to download from the mirror
# So to workaround it, we attempt to install them twice
eatmydata apt-get install -y \$aptops ${packages} || eatmydata apt-get --yes --fix-broken install
eatmydata apt-get install -y \$aptops ${packages} || eatmydata apt-get --yes --fix-broken install
eatmydata apt-get install -y \$aptops ${desktop} ${extras} ${tools} || eatmydata apt-get --yes --fix-broken install
eatmydata apt-get install -y \$aptops ${desktop} ${extras} ${tools} || eatmydata apt-get --yes --fix-broken install
eatmydata apt-get install -y \$aptops --autoremove systemd-timesyncd || eatmydata apt-get --yes --fix-broken install
eatmydata apt-get dist-upgrade -y \$aptops

eatmydata apt-get -y --allow-change-held-packages --purge autoremove

# Linux console/Keyboard configuration
echo 'console-common console-data/keymap/policy select Select keymap from full list' | debconf-set-selections
echo 'console-common console-data/keymap/full select en-latin1-nodeadkeys' | debconf-set-selections

# Copy all services
cp -p /bsp/services/all/*.service /etc/systemd/system/

# Regenerated the shared-mime-info database on the first boot
# since it fails to do so properly in a chroot
systemctl enable smi-hack

# Generate SSH host keys on first run
systemctl enable regenerate_ssh_host_keys
# Enable sshd
systemctl enable ssh

# Allow users to use NM over ssh
install -m644 /bsp/polkit/10-NetworkManager.pkla /var/lib/polkit-1/localauthority/50-local.d

cd /root
apt download -o APT::Sandbox::User=root ca-certificates 2>/dev/null

# Copy over the default bashrc
cp /etc/skel/.bashrc /root/.bashrc

# Set a REGDOMAIN.  This needs to be done or wireless doesn't work correctly on the RPi 3B+
sed -i -e 's/REGDOM.*/REGDOMAIN=00/g' /etc/default/crda

# Try and make the console a bit nicer
# Set the terminus font for a bit nicer display
sed -i -e 's/FONTFACE=.*/FONTFACE="Terminus"/' /etc/default/console-setup
sed -i -e 's/FONTSIZE=.*/FONTSIZE="6x12"/' /etc/default/console-setup

# Fix startup time from 5 minutes to 15 secs on raise interface wlan0
sed -i 's/^TimeoutStartSec=5min/TimeoutStartSec=15/g' "/usr/lib/systemd/system/networking.service"

rm -f /usr/bin/dpkg
EOF

# Run third stage
chmod 0755 ${work_dir}/third-stage
systemd-nspawn_exec /third-stage

# Clean up eatmydata
systemd-nspawn_exec dpkg-divert --remove --rename /usr/bin/dpkg

# Clean system
systemd-nspawn_exec << 'EOF'
rm -f /0
rm -rf /bsp
fc-cache -frs
rm -rf /tmp/*
rm -rf /etc/*-
rm -rf /hs_err*
rm -rf /userland
rm -rf /opt/vc/src
rm -f /etc/ssh/ssh_host_*
rm -rf /var/lib/dpkg/*-old
rm -rf /var/lib/apt/lists/*
rm -rf /var/cache/apt/*.bin
rm -rf /var/cache/apt/archives/*
rm -rf /var/cache/debconf/*.data-old
for logs in $(find /var/log -type f); do > $logs; done
history -c
EOF

# Disable the use of http proxy in case it is enabled
if [ -n "$proxy_url" ]; then
  unset http_proxy
  rm -rf ${work_dir}/etc/apt/apt.conf.d/66proxy
fi

# Mirror & suite replacement
if [[ ! -z "${4}" || ! -z "${5}" ]]; then
  mirror=${4}
  suite=${5}
fi

# Define sources.list
cat << EOF > ${work_dir}/etc/apt/sources.list
deb ${mirror} ${suite} ${components//,/ }
#deb-src ${mirror} ${suite} ${components//,/ }
EOF

# Pull in the gcc 4.7 cross compiler to build the kernel
# Debian uses a gcc that the chromebook kernel doesn't have support for
cd "${base_dir}"
git clone --depth 1 https://gitlab.com/kalilinux/packages/gcc-arm-linux-gnueabihf-4-7.git gcc-arm-linux-gnueabihf-4.7

# Kernel section.  If you want to use a custom kernel, or configuration, replace
# them in this section
git clone --depth 1 https://chromium.googlesource.com/chromiumos/third_party/kernel -b release-${kernel_release} ${work_dir}/usr/src/kernel
cd ${work_dir}/usr/src/kernel
cp ${current_dir}/kernel-configs/chromebook-3.8.config .config
cp ${current_dir}/kernel-configs/chromebook-3.8.config ../exynos.config
cp ${current_dir}/kernel-configs/chromebook-3.8_wireless-3.4.config exynos_wifi34.config
git rev-parse HEAD > ${work_dir}/usr/src/kernel-at-commit
export ARCH=arm
# Edit the CROSS_COMPILE variable as needed
export CROSS_COMPILE="${base_dir}"/gcc-arm-linux-gnueabihf-4.7/bin/arm-linux-gnueabihf-
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/mac80211.patch
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/0001-exynos-drm-smem-start-len.patch
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/0001-mwifiex-do-not-create-AP-and-P2P-interfaces-upon-dri.patch
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/0001-Commented-out-pr_debug-line.patch
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/0002-Fix-udl_connector-include.patch
make oldconfig || die "Kernel config options added"
make -j $(grep -c processor /proc/cpuinfo)
make dtbs
make modules_install INSTALL_MOD_PATH=${work_dir}
cat << __EOF__ > ${work_dir}/usr/src/kernel/arch/arm/boot/kernel-exynos.its
/dts-v1/;

/ {
    description = "Chrome OS kernel image with one or more FDT blobs";
    images {
        kernel@1{
            description = "kernel";
            data = /incbin/("zImage");
            type = "kernel_noload";
            arch = "arm";
            os = "linux";
            compression = "none";
            load = <0>;
            entry = <0>;
        };
        fdt@1{
            description = "exynos5250-skate.dtb";
            data = /incbin/("dts/exynos5250-skate.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@2{
            description = "exynos5250-smdk5250.dtb";
            data = /incbin/("dts/exynos5250-smdk5250.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@3{
            description = "exynos5250-snow-rev4.dtb";
            data = /incbin/("dts/exynos5250-snow-rev4.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@4{
            description = "exynos5250-snow-rev5.dtb";
            data = /incbin/("dts/exynos5250-snow-rev5.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@5{
            description = "exynos5250-spring.dtb";
            data = /incbin/("dts/exynos5250-spring.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@6{
            description = "exynos5420-peach-kirby.dtb";
            data = /incbin/("dts/exynos5420-peach-kirby.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@7{
            description = "exynos5420-peach-pit-rev3_5.dtb";
            data = /incbin/("dts/exynos5420-peach-pit-rev3_5.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@8{
            description = "exynos5420-peach-pit-rev4.dtb";
            data = /incbin/("dts/exynos5420-peach-pit-rev4.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@9{
            description = "exynos5420-peach-pit.dtb";
            data = /incbin/("dts/exynos5420-peach-pit.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@10{
            description = "exynos5420-smdk5420.dtb";
            data = /incbin/("dts/exynos5420-smdk5420.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@11{
            description = "exynos5420-smdk5420-evt0.dtb";
            data = /incbin/("dts/exynos5420-smdk5420-evt0.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@12{
            description = "exynos5422-peach-pi.dtb";
            data = /incbin/("dts/exynos5422-peach-pi.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
        fdt@13{
            description = "exynos5440-ssdk5440.dtb";
            data = /incbin/("dts/exynos5440-ssdk5440.dtb");
            type = "flat_dt";
            arch = "arm";
            compression = "none";
            hash@1{
                algo = "sha1";
            };
        };
    };
    configurations {
        default = "conf@1";
        conf@1{
            kernel = "kernel@1";
            fdt = "fdt@1";
        };
        conf@2{
            kernel = "kernel@1";
            fdt = "fdt@2";
        };
        conf@3{
            kernel = "kernel@1";
            fdt = "fdt@3";
        };
        conf@4{
            kernel = "kernel@1";
            fdt = "fdt@4";
        };
        conf@5{
            kernel = "kernel@1";
            fdt = "fdt@5";
        };
        conf@6{
            kernel = "kernel@1";
            fdt = "fdt@6";
        };
        conf@7{
            kernel = "kernel@1";
            fdt = "fdt@7";
        };
        conf@8{
            kernel = "kernel@1";
            fdt = "fdt@8";
        };
        conf@9{
            kernel = "kernel@1";
            fdt = "fdt@9";
        };
        conf@10{
            kernel = "kernel@1";
            fdt = "fdt@10";
        };
        conf@11{
            kernel = "kernel@1";
            fdt = "fdt@11";
        };
        conf@12{
            kernel = "kernel@1";
            fdt = "fdt@12";
        };
        conf@13{
            kernel = "kernel@1";
            fdt = "fdt@13";
        };
    };
};
__EOF__
cd ${work_dir}/usr/src/kernel/arch/arm/boot
mkimage -D "-I dts -O dtb -p 2048" -f kernel-exynos.its exynos-kernel

# microSD Card
echo 'noinitrd console=tty1 quiet root=PARTUUID=%U/PARTNROFF=1 rootwait rw lsm.module_locking=0 net.ifnames=0 rootfstype=$fstype' > cmdline

# Pulled from ChromeOS, this is exactly what they do because there's no
# bootloader in the kernel partition on ARM
dd if=/dev/zero of=bootloader.bin bs=512 count=1

vbutil_kernel --arch arm --pack "${base_dir}"/kernel.bin --keyblock /usr/share/vboot/devkeys/kernel.keyblock --signprivate /usr/share/vboot/devkeys/kernel_data_key.vbprivk --version 1 --config cmdline --bootloader bootloader.bin --vmlinuz exynos-kernel

cd ${work_dir}/usr/src/kernel/
make mrproper
cp ../exynos.config .config
cd "${base_dir}"

# Fix up the symlink for building external modules
# kernver is used so we don't need to keep track of what the current compiled
# version is
kernver=$(ls ${work_dir}/lib/modules/)
cd ${work_dir}/lib/modules/${kernver}
rm build
rm source
ln -s /usr/src/kernel build
ln -s /usr/src/kernel source
cd ${current_dir}

# Bit of a hack to hide eMMC partitions from XFCE
cat << EOF > ${work_dir}/etc/udev/rules.d/99-hide-emmc-partitions.rules
KERNEL=="mmcblk0*", ENV{UDISKS_IGNORE}="1"
EOF

# Disable uap0 and p2p0 interfaces in NetworkManager
mkdir -p ${work_dir}/etc/NetworkManager/
printf '\n[keyfile]\nunmanaged-devices=interface-name:p2p0\n' >> ${work_dir}/etc/NetworkManager/NetworkManager.conf

# Touchpad configuration
mkdir -p ${work_dir}/etc/X11/xorg.conf.d
cp ${current_dir}/bsp/xorg/10-synaptics-chromebook.conf ${work_dir}/etc/X11/xorg.conf.d/

# Turn off Accel
cat << EOF > ${work_dir}/etc/X11/xorg.conf.d/20-modesetting.conf
Section "Device"
    Identifier  "Exynos Video"
    Driver      "modesetting"
    Option      "AccelMethod"   "none"
EndSection
EOF

# Mali GPU rules aka mali-rules package in ChromeOS
cat << EOF > ${work_dir}/etc/udev/rules.d/50-mali.rules
KERNEL=="mali0", MODE="0660", GROUP="video"
EOF

# Video rules aka media-rules package in ChromeOS
cat << EOF > ${work_dir}/etc/udev/rules.d/50-media.rules
ATTR{name}=="s5p-mfc-dec", SYMLINK+="video-dec"
ATTR{name}=="s5p-mfc-enc", SYMLINK+="video-enc"
ATTR{name}=="s5p-jpeg-dec", SYMLINK+="jpeg-dec"
ATTR{name}=="exynos-gsc.0*", SYMLINK+="image-proc0"
ATTR{name}=="exynos-gsc.1*", SYMLINK+="image-proc1"
ATTR{name}=="exynos-gsc.2*", SYMLINK+="image-proc2"
ATTR{name}=="exynos-gsc.3*", SYMLINK+="image-proc3"
ATTR{name}=="rk3288-vpu-dec", SYMLINK+="video-dec"
ATTR{name}=="rk3288-vpu-enc", SYMLINK+="video-enc"
ATTR{name}=="go2001-dec", SYMLINK+="video-dec"
ATTR{name}=="go2001-enc", SYMLINK+="video-enc"
ATTR{name}=="mt81xx-vcodec-dec", SYMLINK+="video-dec"
ATTR{name}=="mt81xx-vcodec-enc", SYMLINK+="video-enc"
ATTR{name}=="mt81xx-image-proc", SYMLINK+="image-proc0"
EOF

# This is for Peach - kinda a hack, never really worked properly they say
# Ambient light sensor
cat << EOF > ${work_dir}/lib/udev/light-sensor-set-multiplier.sh
#!/bin/sh

# Copyright (c) 2012 The Chromium OS Authors. All rights reserved
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file

# In iio/devices, find device0 on 3.0.x kernels and iio:device0 on 3.2 kernels
for FILE in /sys/bus/iio/devices/*/in_illuminance0_calibscale; do
  # Set the light sensor calibration value
  echo 5.102040 > \$FILE && break;
done

for FILE in /sys/bus/iio/devices/*/in_illuminance1_calibscale; do
  # Set the IR compensation calibration value
  echo 0.053425 > \$FILE && break;
done

for FILE in /sys/bus/iio/devices/*/range; do
  # Set the light sensor range value (max lux)
  echo 16000 > \$FILE && break;
done

for FILE in /sys/bus/iio/devices/*/continuous; do
  # Change the measurement mode to the continuous mode
  echo als > \$FILE && break;
done
EOF

cat << EOF > ${work_dir}/etc/udev/rules.d/99-light-sensor.rules
# Calibrate the light sensor when the isl29018 driver is installed
ACTION=="add", SUBSYSTEM=="drivers", KERNEL=="isl29018", RUN+="light-sensor-set-multiplier.sh"
EOF

cd ${current_dir}

# Calculate the space to create the image
root_size=$(du -s -B1 ${work_dir} --exclude=${work_dir}/boot | cut -f1)
root_extra=$((${root_size}/1024/1000*5*1024/5))
raw_size=$(($((${free_space}*1024))+${root_extra}+$((${bootsize}*1024))+4096))

# Create the disk and partition it
echo "Creating image file ${image_name}.img"
fallocate -l $(echo ${raw_size}Ki | numfmt --from=iec-i --to=si) "${image_dir}/${image_name}.img"
parted -s "${image_dir}/${image_name}.img" mklabel gpt
cgpt create -z "${image_dir}/${image_name}.img"
cgpt create "${image_dir}/${image_name}.img"

cgpt add -i 1 -t kernel -b 8192 -s 32768 -l kernel -S 1 -T 5 -P 10 "${image_dir}/${image_name}.img"
cgpt add -i 2 -t data -b 40960 -s `expr $(cgpt show "${image_dir}/${image_name}.img" | grep 'Sec GPT table' | awk '{ print \$1 }')  - 40960` -l Root "${image_dir}/${image_name}.img"

loopdevice=`losetup -f --show ${current_dir}/${image_name}.img`
device=`kpartx -va ${loopdevice} | sed 's/.*\(loop[0-9]\+\)p.*/\1/g' | head -1`
sleep 5
device="/dev/mapper/${device}"
bootp=${device}p1
rootp=${device}p2

if [[ $fstype == ext4 ]]; then
  features="-O ^64bit,^metadata_csum"
elif [[ $fstype == ext3 ]]; then
  features="-O ^64bit"
fi
mkfs $features -t $fstype -L ROOTFS ${rootp}

mkdir -p "${base_dir}"/root
mount ${rootp} "${base_dir}"/root

# We do this down here to get rid of the build system's resolv.conf after running through the build
echo "nameserver ${nameserver}" > "${work_dir}"/etc/resolv.conf

# Create an fstab so that we don't mount / read-only
UUID=$(blkid -s UUID -o value ${rootp})
echo "UUID=$UUID /               $fstype    errors=remount-ro 0       1" >> ${work_dir}/etc/fstab

echo "Rsyncing rootfs into image file"
rsync -HPavz -q ${work_dir}/ ${base_dir}/root/

# Unmount partition
sync
umount ${rootp}

dd if="${base_dir}"/kernel.bin of=${bootp}

cgpt repair ${loopdevice}

kpartx -dv ${loopdevice}
losetup -d ${loopdevice}

# Limit CPU function
limit_cpu (){
  rand=$(tr -cd 'A-Za-z0-9' < /dev/urandom | head -c4 ; echo) # Random name group
  cgcreate -g cpu:/cpulimit-${rand} # Name of group cpulimit
  cgset -r cpu.shares=800 cpulimit-${rand} # Max 1024
  cgset -r cpu.cfs_quota_us=80000 cpulimit-${rand} # Max 100000
  # Retry command
  local n=1; local max=5; local delay=2
  while true; do
    cgexec -g cpu:cpulimit-${rand} "$@" && break || {
      if [[ $n -lt $max ]]; then
        ((n++))
        echo -e "\e[31m Command failed. Attempt $n/$max \033[0m"
        sleep $delay;
      else
        echo "The command has failed after $n attempts."
        break
      fi
    }
  done
}

if [ $compress = xz ]; then
  if [ $(arch) == 'x86_64' ]; then
    echo "Compressing ${image_name}.img"
    [ $(nproc) \< 3 ] || cpu_cores=3 # cpu_cores = Number of cores to use
    limit_cpu pixz -p ${cpu_cores:-2} "${image_dir}/${image_name}.img" # -p Nº cpu cores use
    chmod 0644 ${current_dir}/${image_name}.img.xz
  fi
else
  chmod 0644 "${image_dir}/${image_name}.img"
fi

# Clean up all the temporary build stuff and remove the directories
# Comment this out to keep things around if you want to see what may have gone wrong
echo "Removing temporary build files"
rm -rf "${base_dir}"
