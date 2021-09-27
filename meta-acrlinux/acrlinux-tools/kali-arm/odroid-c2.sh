#!/usr/bin/env bash
#
# Kali Linux ARM build-script for ODROID-C2 (64-bit)
# https://gitlab.com/kalilinux/build-scripts/kali-arm
#
# This is a supported device - which you can find pre-generated images for
# More information: https://www.kali.org/docs/arm/odroid-c2/
#

# Stop on error
set -e

# shellcheck disable=SC2154
# Load general functions
# shellcheck source=/dev/null
source ./common.d/functions.sh

# Hardware model
hw_model=${hw_model:-"odroid-c2"}
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
cp -p /bsp/services/odroid-c2/*.service /etc/systemd/system/

# For some reason the latest modesetting driver (part of xorg server) seems to cause a lot of jerkiness
status_stage3 'Using the fbdev driver is not ideal but it is far less frustrating to work with'
mkdir -p /etc/X11/xorg.conf.d
cp -p /bsp/xorg/20-meson.conf /etc/X11/xorg.conf.d/

status_stage3 'Install the kernel packages'
eatmydata apt-get install -y dkms linux-image-arm64 u-boot-menu

# We will replace this later, via sed, to point to the correct root partition (hopefully?)
status_stage3 'Run u-boot-update to generate the extlinux.conf file'
u-boot-update

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


# 1366x768 is sort of broken on the ODROID-C2, not sure where the issue is, but
# we can work around it by setting the resolution to 1360x768
# This requires 2 files, a script and then something for lightdm to use
# I do not have anything set up for the console though, so that's still broken for now
mkdir -p ${work_dir}/usr/local/bin
cat << EOF > ${work_dir}/usr/local/bin/xrandrscript.sh
#!/usr/bin/env bash

resolution=$(xdpyinfo | awk '/dimensions:/ { print $2; exit }')

if [[ "$resolution" == "1366x768" ]]; then
    xrandr --newmode "1360x768_60.00"   84.75  1360 1432 1568 1776  768 771 781 798 -hsync +vsync
    xrandr --addmode HDMI-1 1360x768_60.00
    xrandr --output HDMI-1 --mode  1360x768_60.00
fi
EOF
chmod 0755 ${work_dir}/usr/local/bin/xrandrscript.sh

mkdir -p ${work_dir}/usr/share/lightdm/lightdm.conf.d/
cat << EOF > ${work_dir}/usr/share/lightdm/lightdm.conf.d/60-xrandrscript.conf
[SeatDefaults]
display-setup-script=/usr/local/bin/xrandrscript.sh
session-setup-script=/usr/local/bin/xrandrscript.sh
EOF

cd "${current_dir}/"

# Calculate the space to create the image and create
make_image

# Create the disk partitions
status "Create the disk partitions"
parted -s "${image_dir}/${image_name}.img" mklabel msdos
parted -s -a minimal "${image_dir}/${image_name}.img" mkpart primary $fstype 32MiB 100%

# Set the partition variables
loopdevice=$(losetup --show -fP "${image_dir}/${image_name}.img")
rootp="${loopdevice}p1"

# Create file systems
status "Formatting partitions"
if [[ "$fstype" == "ext4" ]]; then
  features="^64bit,^metadata_csum"
elif [[ "$fstype" == "ext3" ]]; then
  features="^64bit"
fi
mkfs -O "$features" -t "$fstype" -L ROOTFS "${rootp}"

# Create the dirs for the partitions and mount them
status "Create the dirs for the partitions and mount them"
mkdir -p "${base_dir}"/root/
mount "${rootp}" "${base_dir}"/root

# We do this here because we don't want to hardcode the UUID for the partition during creation
# systemd doesn't seem to be generating the fstab properly for some people, so let's create one
status "/etc/fstab"
cat <<EOF > "${work_dir}"/etc/fstab
# <file system> <mount point>   <type>  <options>       <dump>  <pass>
proc            /proc           proc    defaults          0       0
UUID=$(blkid -s UUID -o value ${rootp})  /               $fstype    defaults,noatime  0       1
EOF

status "Edit the extlinux.conf file to set root uuid and proper name"
# Ensure we don't have root=/dev/sda3 in the extlinux.conf which comes from running u-boot-menu in a cross chroot
# We do this down here because we don't know the UUID until after the image is created
sed -i -e "0,/root=.*/s//root=UUID=$(blkid -s UUID -o value ${rootp}) rootfstype=$fstype console=tty1 consoleblank=0 ro rootwait/g" ${work_dir}/boot/extlinux/extlinux.conf
# And we remove the "GNU/Linux because we don't use it
sed -i -e "s|.*GNU/Linux Rolling|menu label Kali Linux|g" ${work_dir}/boot/extlinux/extlinux.conf

status "Set the default options in /etc/default/u-boot"
echo 'U_BOOT_MENU_LABEL="Kali Linux"' >> ${work_dir}/etc/default/u-boot
echo 'U_BOOT_PARAMETERS="console=tty1 consoleblank=0 ro rootwait"' >> ${work_dir}/etc/default/u-boot

status "Rsyncing rootfs into image file"
rsync -HPavz -q "${work_dir}"/ "${base_dir}"/root/
sync

# We are gonna use as much open source as we can here, hopefully we end up with a nice
# mainline u-boot and signed bootloader - unfortunately, due to the way this is packaged up
# we have to clone two different u-boot repositories - the one from HardKernel which
# has the bootloader binary blobs we need, and the denx mainline u-boot repository
# Let the fun begin

# Unset these because we're building on the host
unset ARCH
unset CROSS_COMPILE

status "Bootloader"
mkdir -p ${base_dir}/bootloader
cd ${base_dir}/bootloader
git clone --depth 1 https://github.com/afaerber/meson-tools --depth 1
git clone --depth 1 https://github.com/u-boot/u-boot.git
git clone --depth 1 https://github.com/hardkernel/u-boot -b odroidc2-v2015.01 u-boot-hk

# First things first, let's build the meson-tools, of which, we only really need amlbootsig
cd ${base_dir}/bootloader/meson-tools/
make
# Now we need to build fip_create
cd ${base_dir}/bootloader/u-boot-hk/tools/fip_create
HOSTCC=cc HOSTLD=ld make

cd ${base_dir}/bootloader/u-boot/
make ARCH=arm CROSS_COMPILE=aarch64-linux-gnu- odroid-c2_defconfig
make ARCH=arm CROSS_COMPILE=aarch64-linux-gnu-

# Now the real fun... keeping track of file locations isn't fun, and i should probably move them to
# one single directory, but since we're not keeping these things around afterwards, it's fine to
# leave them where they are
# See:
# https://forum.odroid.com/viewtopic.php?t=26833
# https://github.com/nxmyoz/c2-overlay/blob/master/Readme.md
# for the inspirations for it.  Specifically Adrian's posts got us closest

# This is funky, but in the end, it should do the right thing
cd ${base_dir}/bootloader/
# Create the fip.bin
./u-boot-hk/tools/fip_create/fip_create --bl30 ./u-boot-hk/fip/gxb/bl30.bin \
--bl301 ./u-boot-hk/fip/gxb/bl301.bin --bl31 ./u-boot-hk/fip/gxb/bl31.bin \
--bl33 u-boot/u-boot.bin fip.bin

# Create the stage2 bootloader thingie?
cat ./u-boot-hk/fip/gxb/bl2.package fip.bin > boot_new.bin
# Now sign it, and call it u-boot.bin
./meson-tools/amlbootsig boot_new.bin u-boot.bin
# Now strip a portion of it off, and put it in the sd_fuse directory
dd if=u-boot.bin of=./u-boot-hk/sd_fuse/u-boot.bin bs=512 skip=96
# Finally, write it to the loopdevice so we have our bootloader on the card
cd ./u-boot-hk/sd_fuse
./sd_fusing.sh ${loopdevice}
sync

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
