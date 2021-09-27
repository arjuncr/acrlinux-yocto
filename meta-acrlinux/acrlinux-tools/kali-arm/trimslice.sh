#!/usr/bin/env bash
#
# Kali Linux ARM build-script for Trimslice
# https://gitlab.com/kalilinux/build-scripts/kali-arm
#
# This is a community script - you will need to generate your own image to use
# More information: https://www.kali.org/docs/arm/trimslice/
#

# Stop on error
set -e

# shellcheck disable=SC2154
# Load general functions
# shellcheck source=/dev/null
source ./common.d/functions.sh

# Hardware model
hw_model=${hw_model:-"trimslice"}
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

status_stage3 'Install packages'
eatmydata apt-get install -y ${packages} || eatmydata apt-get install -y --fix-broken

status_stage3 'Install desktop packages'
eatmydata apt-get install -y ${desktop_pkgs} ${extra} || eatmydata apt-get install -y --fix-broken

status_stage3 'Install kernel and u-boot packages'
eatmydata apt-get install -y linux-image-armmp u-boot-menu

status_stage3 'Install systemd-timesyncd instead of ntp'
eatmydata apt-get install --autoremove -y systemd-timesyncd

status_stage3 'Clean up'
eatmydata apt-get -y --purge autoremove

status_stage3 'Linux console/keyboard configuration'
echo 'console-common console-data/keymap/policy select Select keymap from full list' | debconf-set-selections
echo 'console-common console-data/keymap/full select en-latin1-nodeadkeys' | debconf-set-selections

status_stage3 'Copy all services'
cp -p /bsp/services/all/*.service /etc/systemd/system/
cp -p /bsp/services/rpi/*.service /etc/systemd/system/

status_stage3 'Copy script rpi-resizerootfs'
install -m755 /bsp/scripts/rpi-resizerootfs /usr/sbin/

status_stage3 'Enable rpi-resizerootfs first boot'
systemctl enable rpi-resizerootfs

status_stage3 'Generate SSH host keys on first run'
systemctl enable regenerate_ssh_host_keys

status_stage3 'Allow users to use NetworkManager over ssh'
install -m644 /bsp/polkit/10-NetworkManager.pkla /var/lib/polkit-1/localauthority/50-local.d

status_stage3 'Install ca-certificate'
cd /root
apt download -o APT::Sandbox::User=root ca-certificates 2>/dev/null

status_stage3 'Set a REGDOMAIN'
sed -i -e 's/REGDOM.*/REGDOMAIN=00/g' /etc/default/crda

status_stage3 'Enable login over serial'
echo "T0:23:respawn:/sbin/agetty -L ttyS0 115200 vt100" >> /etc/inittab

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

# systemd doesn't seem to be generating the fstab properly for some people, so
# let's create one. We add the root partition below after creating the image
# as we don't know the UUID until the image has been partitioned
status 'Create /etc/fstab'
cat << EOF > ${work_dir}/etc/fstab
# <file system> <mount point>   <type>  <options>       <dump>  <pass>
proc            /proc           proc    defaults          0       0
LABEL=BOOT  /boot           ext2    defaults          0       2
EOF

# Calculate the space to create the image and create
make_image

# Create the disk and partition it
status "Creating image file ${image_name}.img"
fallocate -l $(echo ${raw_size}Ki | numfmt --from=iec-i --to=si) "${image_dir}/${image_name}.img"
parted -s "${image_dir}/${image_name}.img" mklabel msdos
parted -s "${image_dir}/${image_name}.img" mkpart primary ext2 1MiB ${bootsize}MiB
parted -s -a minimal "${image_dir}/${image_name}.img" mkpart primary $fstype ${bootsize}MiB 100%

# Set the partition variables
loopdevice=$(losetup -f --show "${image_dir}/${image_name}.img")
device=$(kpartx -va ${loopdevice} | sed 's/.*\(loop[0-9]\+\)p.*/\1/g' | head -1)
sleep 5s
device="/dev/mapper/${device}"
bootp=${device}p1
rootp=${device}p2

mkfs.ext2 -L BOOT "${bootp}"
if [[ $fstype == ext4 ]]; then
  features="^64bit,^metadata_csum"
elif [[ $fstype == ext3 ]]; then
  features="^64bit"
fi
mkfs -O "$features" -t "$fstype" -L ROOTFS "${rootp}"

# Create the dirs for the partitions and mount them
status "Create the dirs for the partitions and mount them"
mkdir -p "${base_dir}"/root
mount ${rootp} "${base_dir}"/root
mkdir -p "${base_dir}"/root/boot
mount ${bootp} "${base_dir}"/root/boot

# Create an fstab so that we don't mount / read-only
status "Fix rootfs entry in /etc/fstab"
UUID=$(blkid -s UUID -o value ${rootp})
echo "UUID=$UUID /               $fstype    errors=remount-ro 0       1" >> ${work_dir}/etc/fstab

status "Fix root entry in extlinux.conf"
# Ensure we don't have root=/dev/sda3 in the extlinux.conf which comes from running u-boot-menu in a cross chroot
# We do this down here because we don't know the UUID until after the image is created
sed -i -e "0,/root=.*/s//root=UUID=$(blkid -s UUID -o value ${rootp}) rootfstype=$fstype console=ttyS0,115200 console=tty1 consoleblank=0 rw quiet rootwait/g" ${work_dir}/boot/extlinux/extlinux.conf
# And we remove the "Debian GNU/Linux because we're Kali"
sed -i -e "s/Debian GNU\/Linux/Kali Linux/g" ${work_dir}/boot/extlinux/extlinux.conf

status "Rsyncing rootfs into image file"
rsync -HPavz -q ${work_dir}/ ${base_dir}/root/
sync

# Flush buffers and bytes - this is nicked from the Devuan arm-sdk
blockdev --flushbufs "${loopdevice}"
python -c 'import os; os.fsync(open("'${loopdevice}'", "r+b"))'

# Unmount filesystem
status "Unmount filesystem"
umount -l "${bootp}"
umount -l "${rootp}"

# Check filesystem
status "Check filesystem"
e2fsck -y -f "${bootp}"
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