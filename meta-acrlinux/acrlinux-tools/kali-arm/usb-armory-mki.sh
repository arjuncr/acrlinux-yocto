#!/usr/bin/env bash
#
# Kali Linux ARM build-script for USB Armory MKI (32-bit)
# https://gitlab.com/kalilinux/build-scripts/kali-arm
#
# This is a community script - you will need to generate your own image to use
# More information: https://www.kali.org/docs/arm/usb-armory-mkii/
#

# Stop on error
set -e

# shellcheck disable=SC2154
# Load general functions
# shellcheck source=/dev/null
source ./common.d/functions.sh

# Hardware model
hw_model=${hw_model:-"usbarmory-mki"}
# Architecture
architecture=${architecture:-"armhf"}
# Variant name for image and dir build
variant=${variant:-"${architecture}"}
# Desktop manager (xfce, gnome, i3, kde, lxde, mate, e17 or none)
desktop=${desktop:-"xfce"}

# Load common variables
include variables
# Checks script environment
include check
# Packages build list
include packages
# Execute initial debootstrap
debootstrap_exec http://http.kali.org/kali
# Enable eatmydata in compilation
include eatmydata
# debootstrap second stage
systemd-nspawn_exec eatmydata /debootstrap/debootstrap --second-stage
# Define sources.list
include sources.list
# APT options
include apt_options
# So X doesn't complain, we add kali to hosts
include hosts
# Set hostname
set_hostname "${hostname}"
# Network configs
include network
add_interface eth0

# Copy directory bsp into build dir
status "Copy directory bsp into build dir"
cp -rp bsp "${work_dir}"

# Third stage
cat <<EOF > "${work_dir}"/third-stage
#!/usr/bin/env bash
set -e
status_3i=0
status_3t=\$(grep '^status_stage3 ' \$0 | wc -l)

status_stage3() {
  status_3i=\$((status_3i+1))
  echo  " [i] Stage 3 (\${status_3i}/\${status_3t}): \$1"
}

status_stage3 'Update apt'
export DEBIAN_FRONTEND=noninteractive
eatmydata apt-get update

status_stage3 'Install core packages'
eatmydata apt-get -y install ${third_stage_pkgs}

status_stage3 'Enable dhcp server'
eatmydata apt-get install -y ${packages} || eatmydata apt-get install -y --fix-broken

status_stage3 'Install desktop packages'
eatmydata apt-get install -y ${desktop_pkgs} ${extra} || eatmydata apt-get install -y --fix-broken

status_stage3 'ntp doesn't always sync the date, but systemd's timesyncd does, so we remove ntp and reinstall it with this'
eatmydata apt-get install -y systemd-timesyncd --autoremove

status_stage3 'Install dhcp and vnc servers'
eatmydata apt-get install -y isc-dhcp-server tightvncserver || eatmydata apt-get install -y --fix-broken

status_stage3 'Clean up'
eatmydata apt-get -y --purge autoremove

status_stage3 'Linux console/keyboard configuration'
echo 'console-common console-data/keymap/policy select Select keymap from full list' | debconf-set-selections
echo 'console-common console-data/keymap/full select en-latin1-nodeadkeys' | debconf-set-selections

status_stage3 'Copy all services'
cp -p /bsp/services/all/*.service /etc/systemd/system/

status_stage3 'Copy script rpi-resizerootfs'
install -m755 /bsp/scripts/rpi-resizerootfs /usr/sbin/
install -m755 /bsp/scripts/growpart /usr/local/bin/

status_stage3 'Enable rpi-resizerootfs first boot'
systemctl enable rpi-resizerootfs

status_stage3 'Generate SSH host keys on first run'
systemctl enable regenerate_ssh_host_keys

status_stage3 'Enable ssh'
systemctl enable ssh

status_stage3 'Allow users to use NetworkManager over ssh'
install -m644 /bsp/polkit/10-NetworkManager.pkla /var/lib/polkit-1/localauthority/50-local.d

status_stage3 'Remove /etc/modules*'
rm /etc/modules
rm /etc/modules-load.d/modules.conf


status_stage3 'Add our /etc/modules-load.d/'
cat << __EOF__ > /etc/modules-load.d/modules.conf
ledtrig_heartbeat
ci_hdrc_imx
g_ether
#g_mass_storage
#g_multi
__EOF__

status_stage3 'Add our /etc/modprobe.d/'
cat << __EOF__ > /etc/modprobe.d/usbarmory.conf
options g_ether use_eem=0 dev_addr=1a:55:89:a2:69:41 host_addr=1a:55:89:a2:69:42
# To use either of the following, you should create the file /disk.img via dd
# "dd if=/dev/zero of=/disk.img bs=1M count=2048" would create a 2GB disk.img file
#options g_mass_storage file=disk.img
#options g_multi use_eem=0 dev_addr=1a:55:89:a2:69:41 host_addr=1a:55:89:a2:69:42 file=disk.img
__EOF__

status_stage3 'Add our /etc/network/interfaces.d/usb0'
cat << __EOF__ > /etc/network/interfaces.d/usb0
allow-hotplug usb0
iface usb0 inet static
address 10.0.0.1
netmask 255.255.255.0
gateway 10.0.0.2
__EOF__


status_stage3 'Add our /etc/dhcp/dhcpd.conf'
# Debian reads the config from inside /etc/dhcp
cp /etc/dhcp/dhcpd.conf /etc/dhcp/dhcpd.conf.old
cat << __EOF__ > /etc/dhcp/dhcpd.conf
# Sample configuration file for ISC dhcpd for Debian
# Original file /etc/dhcp/dhcpd.conf.old

ddns-update-style none;

default-lease-time 600;
max-lease-time 7200;

log-facility local7;

subnet 10.0.0.0 netmask 255.255.255.0 {
  range 10.0.0.2 10.0.0.2;
  default-lease-time 600;
  max-lease-time 7200;
}
__EOF__

status_stage3 'Only listen on usb0'
sed -i -e 's/INTERFACES.*/INTERFACES="usb0"/g' /etc/default/isc-dhcp-server

status_stage3 'Enable dhcp server'
update-rc.d isc-dhcp-server enable

status_stage3 'Set a REGDOMAIN'
sed -i -e 's/REGDOM.*/REGDOMAIN=00/g' /etc/default/crda

status_stage3 'Try and make the console a bit nicer. Set the terminus font for a bit nicer display'
sed -i -e 's/FONTFACE=.*/FONTFACE="Terminus"/' /etc/default/console-setup
sed -i -e 's/FONTSIZE=.*/FONTSIZE="6x12"/' /etc/default/console-setup

status_stage3 'Fix startup time from 5 minutes to 15 secs on raise interface wlan0'
sed -i 's/^TimeoutStartSec=5min/TimeoutStartSec=15/g' "/usr/lib/systemd/system/networking.service"

status_stage3 'Enable runonce'
install -m755 /bsp/scripts/runonce /usr/sbin/
cp -rf /bsp/runonce.d /etc
systemctl enable runonce

status_stage3 'Clean up dpkg.eatmydata'
rm -f /usr/bin/dpkg
dpkg-divert --remove --rename /usr/bin/dpkg
EOF

# Run third stage
chmod 0755 "${work_dir}"/third-stage
status "Run third stage"
systemd-nspawn_exec /third-stage

# Clean system
include clean_system
trap clean_build ERR SIGTERM SIGINT

# Kernel section. If you want to use a custom kernel, or configuration, replace
# them in this section
status "Kernel stuff"
git clone -b linux-5.4.y --depth 1 git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git ${work_dir}/usr/src/kernel
cd ${work_dir}/usr/src/kernel
git rev-parse HEAD > ${work_dir}/usr/src/kernel-at-commit
touch .scmversion
export ARCH=arm
export CROSS_COMPILE=arm-linux-gnueabihf-
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/kali-wifi-injection-5.4.patch
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/0001-wireless-carl9170-Enable-sniffer-mode-promisc-flag-t.patch
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-host.dts -O arch/arm/boot/dts/imx53-usbarmory-host.dts
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-gpio.dts -O arch/arm/boot/dts/imx53-usbarmory-gpio.dts
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-spi.dts -O arch/arm/boot/dts/imx53-usbarmory-spi.dts
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-i2c.dts -O arch/arm/boot/dts/imx53-usbarmory-i2c.dts
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-scc2.dts -O arch/arm/boot/dts/imx53-usbarmory-scc2.dts
cp ${current_dir}/kernel-configs/usbarmory-5.4.config ${work_dir}/usr/src/kernel/.config
cp ${current_dir}/kernel-configs/usbarmory-5.4.config ${work_dir}/usr/src/usbarmory-5.4.config
make olddefconfig
make LOADADDR=0x70008000 -j $(grep -c processor /proc/cpuinfo) uImage modules imx53-usbarmory-gpio.dtb imx53-usbarmory-i2c.dtb imx53-usbarmory-spi.dtb imx53-usbarmory.dtb imx53-usbarmory-host.dtb imx53-usbarmory-scc2.dtb
make modules_install INSTALL_MOD_PATH=${work_dir}
cp arch/arm/boot/zImage ${work_dir}/boot/
cp arch/arm/boot/dts/imx53-usbarmory*.dtb ${work_dir}/boot/
make mrproper
# Since these aren't integrated into the kernel yet, mrproper removes them
cp ${current_dir}/kernel-configs/usbarmory-5.4.config ${work_dir}/usr/src/kernel/.config
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-host.dts -O arch/arm/boot/dts/imx53-usbarmory-host.dts
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-gpio.dts -O arch/arm/boot/dts/imx53-usbarmory-gpio.dts
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-spi.dts -O arch/arm/boot/dts/imx53-usbarmory-spi.dts
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-i2c.dts -O arch/arm/boot/dts/imx53-usbarmory-i2c.dts
wget $githubraw/inversepath/usbarmory/master/software/kernel_conf/mark-one/imx53-usbarmory-scc2.dts -O arch/arm/boot/dts/imx53-usbarmory-scc2.dts

# Fix up the symlink for building external modules
# kernver is used so we don't need to keep track of what the current compiled
# version is
status "building external modules"
kernver=$(ls ${work_dir}/lib/modules/)
cd ${work_dir}/lib/modules/${kernver}
rm build
rm source
ln -s /usr/src/kernel build
ln -s /usr/src/kernel source

cd "${current_dir}/"

# Calculate the space to create the image and create
make_image

# Create the disk partitions
status "Create the disk partitions"
parted -s "${image_dir}/${image_name}.img" mklabel msdos
parted -s -a minimal "${image_dir}/${image_name}.img" mkpart primary ext2 5MiB 100%

# Set the partition variables
loopdevice=$(losetup --show -fP "${image_dir}/${image_name}.img")
rootp="${loopdevice}p1"

# Create file systems
status "Formatting partitions"
mkfs.ext2 ${rootp}

# Create the dirs for the partitions and mount them
status "Create the dirs for the partitions and mount them"
mkdir -p "${base_dir}"/root
mount ${rootp} "${base_dir}"/root

# Create an fstab so that we don't mount / read-only
status "/etc/fstab"
UUID=$(blkid -s UUID -o value ${rootp})
echo "UUID=$UUID /               $fstype    errors=remount-ro 0       1" >> ${work_dir}/etc/fstab

status "Rsyncing rootfs into image file"
rsync -HPavz -q "${work_dir}"/ "${base_dir}"/root/
sync

status "u-Boot"
cd "${base_dir}"
wget ftp://ftp.denx.de/pub/u-boot/u-boot-2020.10.tar.bz2
tar xvf u-boot-2020.10.tar.bz2 && cd u-boot-2020.10
make distclean
make usbarmory_config
make ARCH=arm
dd if=u-boot.imx of=${loopdevice} bs=512 seek=2 conv=fsync

cd "${current_dir}/"

# Flush buffers and bytes - this is nicked from the Devuan arm-sdk
blockdev --flushbufs "${loopdevice}"
python3 -c 'import os; os.fsync(open("'${loopdevice}'", "r+b"))'

# Unmount filesystem
status "Unmount filesystem"
umount -l "${rootp}"

# Check filesystem
status "Check filesystem"
e2fsck -y -f "${rootp}"

# Remove loop devices
status "Remove loop devices"
losetup -d "${loopdevice}"

# Compress image compilation
include compress_img

# Clean up all the temporary build stuff and remove the directories
# Comment this out to keep things around if you want to see what may have gone wrong
clean_build
