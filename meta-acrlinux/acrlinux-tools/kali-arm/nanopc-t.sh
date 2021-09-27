#!/usr/bin/env bash
#
# Kali Linux ARM build-script for NanoPC-T3/T4 (64-bit)
# https://gitlab.com/kalilinux/build-scripts/kali-arm
#
# This is a supported device - which you can find pre-generated images for
# More information: https://www.kali.org/docs/arm/nanopc-t3/
#

# Stop on error
set -e

# shellcheck disable=SC2154
# Load general functions
# shellcheck source=/dev/null
source ./common.d/functions.sh

# Hardware model
hw_model=${hw_model:-"nanopc-t"}
# Architecture
architecture=${architecture:-"arm64"}
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
#add_interface wlan0

# Copy directory bsp into build dir
status "Copy directory bsp into build dir"
cp -rp bsp "${work_dir}"

# Disable RESUME (suspend/resume is currently broken anyway!) which speeds up boot massively
mkdir -p ${work_dir}/etc/initramfs-tools/conf.d/
cat << EOF > ${work_dir}/etc/initramfs-tools/conf.d/resume
RESUME=none
EOF

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

status_stage3 'Install packages'
eatmydata apt-get install -y ${packages} || eatmydata apt-get install -y --fix-broken

status_stage3 'Install desktop packages'
eatmydata apt-get install -y ${desktop_pkgs} ${extra} || eatmydata apt-get install -y --fix-broken

status_stage3 'ntp doesn't always sync the date, but systemd's timesyncd does, so we remove ntp and reinstall it with this'
eatmydata apt-get install -y systemd-timesyncd --autoremove

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

status_stage3 'Set a REGDOMAIN'
sed -i -e 's/REGDOM.*/REGDOMAIN=00/g' /etc/default/crda

status_stage3 'Enable login over serial'
echo "T0:23:respawn:/sbin/agetty -L ttyAMA0 115200 vt100" >> /etc/inittab

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
status "Kernel section"
git clone --depth 1 https://github.com/friendlyarm/linux -b nanopi2-v4.4.y ${work_dir}/usr/src/kernel
cd ${work_dir}/usr/src/kernel/
git rev-parse HEAD > ${work_dir}/usr/src/kernel-at-commit
touch .scmversion
export ARCH=arm64
#export CROSS_COMPILE="${base_dir}"/gcc-arm-linux-gnueabihf-4.7/bin/arm-linux-gnueabihf-
export CROSS_COMPILE=aarch64-linux-gnu-
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/kali-wifi-injection-4.4.patch
make nanopi3_linux_defconfig
make -j $(grep -c processor /proc/cpuinfo)
make modules_install INSTALL_MOD_PATH=${work_dir}
cp arch/arm64/boot/Image ${work_dir}/boot
cp arch/arm64/boot/dts/nexell/*.dtb ${work_dir}/boot/
make mrproper
make nanopi3_linux_defconfig
cd "${current_dir}/"

# Copy over the firmware for the nanopi3 wifi
# At some point, nexmon could work for the device, but the support would need to
# be added to nexmon
status "WiFi firmware"
mkdir -p ${work_dir}/lib/firmware/ap6212/
wget https://raw.githubusercontent.com/friendlyarm/android_vendor_broadcom_nanopi2/nanopi2-lollipop-mr1/proprietary/nvram_ap6212.txt -O ${work_dir}/lib/firmware/ap6212/nvram.txt
wget https://raw.githubusercontent.com/friendlyarm/android_vendor_broadcom_nanopi2/nanopi2-lollipop-mr1/proprietary/nvram_ap6212a.txt -O ${work_dir}/lib/firmware/ap6212/nvram_ap6212.txt
wget https://raw.githubusercontent.com/friendlyarm/android_vendor_broadcom_nanopi2/nanopi2-lollipop-mr1/proprietary/fw_bcm43438a0.bin -O ${work_dir}/lib/firmware/ap6212/fw_bcm43438a0.bin
wget https://raw.githubusercontent.com/friendlyarm/android_vendor_broadcom_nanopi2/nanopi2-lollipop-mr1/proprietary/fw_bcm43438a1.bin -O ${work_dir}/lib/firmware/ap6212/fw_bcm43438a1.bin
wget https://raw.githubusercontent.com/friendlyarm/android_vendor_broadcom_nanopi2/nanopi2-lollipop-mr1/proprietary/fw_bcm43438a0_apsta.bin -O ${work_dir}/lib/firmware/ap6212/fw_bcm43438a0_apsta.bin
wget https://raw.githubusercontent.com/friendlyarm/android_vendor_broadcom_nanopi2/nanopi2-lollipop-mr1/proprietary/bcm43438a0.hcd -O ${work_dir}/lib/firmware/ap6212/bcm43438a0.hcd
wget https://raw.githubusercontent.com/friendlyarm/android_vendor_broadcom_nanopi2/nanopi2-lollipop-mr1/proprietary/bcm43438a1.hcd -O ${work_dir}/lib/firmware/ap6212/bcm43438a1.hcd
wget https://raw.githubusercontent.com/friendlyarm/android_vendor_broadcom_nanopi2/nanopi2-lollipop-mr1/proprietary/config_ap6212.txt -O ${work_dir}/lib/firmware/ap6212/config.txt
cd "${current_dir}/"

# Fix up the symlink for building external modules
# kernver is used so we don't need to keep track of what the current compiled
# version is
status "building external modules"
kernver=$(ls ${work_dir}/lib/modules/)
cd ${work_dir}/lib/modules/${kernver}/
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
parted -s "${image_dir}/${image_name}.img" mkpart primary ext3 4MiB "${bootsize}"MiB
parted -s -a minimal "${image_dir}/${image_name}.img" mkpart primary "$fstype" "${bootsize}"MiB 100%

# Set the partition variables
loopdevice=$(losetup --show -fP "${image_dir}/${image_name}.img")
bootp="${loopdevice}p1"
rootp="${loopdevice}p2"

# Create file systems
status "Formatting partitions"
if [[ "$fstype" == "ext4" ]]; then
  features="^64bit,^metadata_csum"
elif [[ "$fstype" == "ext3" ]]; then
  features="^64bit"
fi
mkfs -O "$features" -t "$fstype" -L BOOT "${bootp}"
mkfs -O "$features" -t "$fstype" -L ROOTFS "${rootp}"

# Create the dirs for the partitions and mount them
status "Create the dirs for the partitions and mount them"
mkdir -p "${base_dir}"/root/
mount "${rootp}" "${base_dir}"/root
mkdir -p "${base_dir}"/root/boot
mount "${bootp}" "${base_dir}"/root/boot

# Create an fstab so that we don't mount / read-only
status "/etc/fstab"
UUID=$(blkid -s UUID -o value ${rootp})
echo "UUID=$UUID /               $fstype    errors=remount-ro 0       1" >> ${work_dir}/etc/fstab

status "Rsyncing rootfs into image file"
rsync -HPavz -q "${work_dir}"/ "${base_dir}"/root/
sync

# Samsung bootloaders must be signed
# These are the same steps that are done by
# https://github.com/friendlyarm/sd-fuse_nanopi2/blob/master/fusing.sh
status "Samsung bootloaders"
mkdir -p "${base_dir}"/bootloader/
cd "${base_dir}"/bootloader/
wget 'https://github.com/friendlyarm/sd-fuse_s5p6818/blob/master/prebuilt/bl1-mmcboot.bin?raw=true' -O "${base_dir}"/bootloader/bl1-mmcboot.bin
wget 'https://github.com/friendlyarm/sd-fuse_s5p6818/blob/master/prebuilt/fip-loader.img?raw=true' -O "${base_dir}"/bootloader/fip-loader.img
wget 'https://github.com/friendlyarm/sd-fuse_s5p6818/blob/master/prebuilt/fip-secure.img?raw=true' -O "${base_dir}"/bootloader/fip-secure.img
wget 'https://github.com/friendlyarm/sd-fuse_s5p6818/blob/master/prebuilt/fip-nonsecure.img?raw=true' -O "${base_dir}"/bootloader/fip-nonsecure.img
wget 'https://github.com/friendlyarm/sd-fuse_s5p6818/blob/master/tools/fw_printenv?raw=true' -O "${base_dir}"/bootloader/fw_printenv
chmod 0755 "${base_dir}"/bootloader/fw_printenv
ln -s "${base_dir}"/bootloader/fw_printenv "${base_dir}"/bootloader/fw_setenv

dd if="${base_dir}"/bootloader/bl1-mmcboot.bin of=${loopdevice} bs=512 seek=1
dd if="${base_dir}"/bootloader/fip-loader.img of=${loopdevice} bs=512 seek=129
dd if="${base_dir}"/bootloader/fip-secure.img of=${loopdevice} bs=512 seek=769
dd if="${base_dir}"/bootloader/fip-nonsecure.img of=${loopdevice} bs=512 seek=3841

cat << EOF > "${base_dir}"/bootloader/env.conf
# U-Boot environment for Debian, Ubuntu
#
# Copyright (C) Guangzhou FriendlyARM Computer Tech. Co., Ltd
# (http://www.friendlyarm.com)
#

bootargs	console=ttySAC0,115200n8 root=/dev/mmcblk0p2 rootfstype=$fstype rootwait rw consoleblank=0 net.ifnames=0
bootdelay	1
EOF

./fw_setenv ${loopdevice} -s env.conf
sync

# It should be possible to build your own u-boot, as part of this, if you
# prefer, it will only generate the fip-nonsecure.img however
#git clone https://github.com/friendlyarm/u-boot -b nanopi2-v2016.01
#cd u-boot
#make CROSS_COMPILE=aarch64-linux-gnu- s5p6818_nanopi3_defconfig
#make CROSS_COMPILE=aarch64-linux-gnu-
#dd if=fip-nonsecure.img of=$loopdevice bs=512 seek=3841

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
kpartx -dv "${loopdevice}" 
losetup -d "${loopdevice}"

# Compress image compilation
include compress_img

# Clean up all the temporary build stuff and remove the directories
# Comment this out to keep things around if you want to see what may have gone wrong
clean_build
