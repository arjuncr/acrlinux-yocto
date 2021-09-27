#!/usr/bin/env bash
#
# Kali Linux ARM build-script for Beaglebone Black (32-bit)
# https://gitlab.com/kalilinux/build-scripts/kali-arm
#
# This is a community script - you will need to generate your own image to use
# More information: https://www.kali.org/docs/arm/beaglebone-black/
#

# Stop on error
set -e

# shellcheck disable=SC2154
# Load general functions
# shellcheck source=/dev/null
source ./common.d/functions.sh

# Hardware model
hw_model=${hw_model:-"beaglebone-black"}
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

status_stage3 'ntp doesn't always sync the date, but systemd's timesyncd does, so we remove ntp and reinstall it with this'
eatmydata apt-get install -y systemd-timesyncd --autoremove

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
sed -i -e 's/REGDOM.*/REGDOMAIN=00/g' /etc/default/crda || true

status_stage3 'Enable login over serial'
echo "T0:23:respawn:/sbin/agetty -L ttyO0 115200 vt100" >> /etc/inittab

status_stage3 'Enable ttyO0 in udev links config'
cat << _EOF_ >> /etc/udev/links.conf
M   ttyO0 c 5 1
_EOF_

status_stage3 'Enable root login on serial'
cat << _EOF_ >> /etc/securetty
ttyO0
_EOF_

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

status 'Kernel compile'
git clone https://github.com/beagleboard/linux -b 4.14 --depth 1 ${work_dir}/usr/src/kernel
cd ${work_dir}/usr/src/kernel
git rev-parse HEAD > ${work_dir}/usr/src/kernel-at-commit
export ARCH=arm
# Edit the CROSS_COMPILE variable as needed
export CROSS_COMPILE=arm-linux-gnueabihf-
touch .scmversion
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/kali-wifi-injection-4.14.patch
patch -p1 --no-backup-if-mismatch < ${current_dir}/patches/0001-wireless-carl9170-Enable-sniffer-mode-promisc-flag-t.patch
make bb.org_defconfig
make -j $(grep -c processor /proc/cpuinfo)
cp arch/arm/boot/zImage ${work_dir}/boot/zImage
mkdir -p ${work_dir}/boot/dtbs
cp arch/arm/boot/dts/*.dtb ${work_dir}/boot/dtbs/
make INSTALL_MOD_PATH=${work_dir} modules_install
#make INSTALL_MOD_PATH=${work_dir} firmware_install
make mrproper
make bb.org_defconfig
cd "${base_dir}"

status 'Create uEnv.txt file'
cat << EOF > ${work_dir}/boot/uEnv.txt
#u-boot eMMC specific overrides; Angstrom Distribution (BeagleBone Black) 2013-06-20
kernel_file=zImage
initrd_file=uInitrd

loadzimage=load mmc \${mmcdev}:\${mmcpart} \${loadaddr} \${kernel_file}
loadinitrd=load mmc \${mmcdev}:\${mmcpart} 0x81000000 \${initrd_file}; setenv initrd_size \${filesize}
loadfdt=load mmc \${mmcdev}:\${mmcpart} \${fdtaddr} /dtbs/\${fdtfile}
#

console=ttyO0,115200n8
mmcroot=/dev/mmcblk0p2 rw net.ifnames=0
mmcrootfstype=$fstype rootwait fixrtc

##To disable HDMI/eMMC..
#optargs=capemgr.disable_partno=BB-BONELT-HDMI,BB-BONELT-HDMIN,BB-BONE-EMMC-2G

##3.1MP Camera Cape
#optargs=capemgr.disable_partno=BB-BONE-EMMC-2G

mmcargs=setenv bootargs console=\${console} root=\${mmcroot} rootfstype=\${mmcrootfstype} \${optargs}

#zImage:
uenvcmd=run loadzimage; run loadfdt; run mmcargs; bootz \${loadaddr} - \${fdtaddr}

#zImage + uInitrd: where uInitrd has to be generated on the running system
#boot_fdt=run loadzimage; run loadinitrd; run loadfdt
#uenvcmd=run boot_fdt; run mmcargs; bootz \${loadaddr} 0x81000000:\${initrd_size} \${fdtaddr}
EOF

status "Setting up modules.conf"
# rm the symlink if it exists, and the original files if they exist
rm ${work_dir}/etc/modules
rm ${work_dir}/etc/modules-load.d/modules.conf
cat << EOF > ${work_dir}/etc/modules-load.d/modules.conf
g_ether
EOF

# systemd doesn't seem to be generating the fstab properly for some people, so
# let's create one. Further down we add the root partition, but we don't
# know the UUID til after the image file is created
status 'Fix fstab'
cat << EOF > ${work_dir}/etc/fstab
# <file system> <mount point>   <type>  <options>       <dump>  <pass>
proc            /proc           proc    defaults          0       0
/dev/mmcblk0p1  /boot           vfat    defaults          0       2
EOF

status 'Create xorg config'
mkdir -p ${work_dir}/etc/X11/
cat << EOF > ${work_dir}/etc/X11/xorg.conf
Section "Monitor"
  Identifier    "Builtin Default Monitor"
EndSection

Section "Device"
  Identifier    "Builtin Default fbdev Device 0"
  Driver        "fbdev"
  Option        "SWCursor"  "true"
EndSection

Section "Screen"
  Identifier    "Builtin Default fbdev Screen 0"
  Device        "Builtin Default fbdev Device 0"
  Monitor       "Builtin Default Monitor"
  DefaultDepth  16
  # Comment out the above and uncomment the below if using a
  # bbb-view or bbb-exp
  #DefaultDepth 24
EndSection

Section "ServerLayout"
  Identifier    "Builtin Default Layout"
  Screen        "Builtin Default fbdev Screen 0"
EndSection
EOF

# Fix up the symlink for building external modules
# kernver is used so we don't need to keep track of what the current compiled
# version is
status 'Fix kernel symlinks'
kernver=$(ls ${work_dir}/lib/modules/)
cd ${work_dir}/lib/modules/${kernver}
rm build
rm source
ln -s /usr/src/kernel build
ln -s /usr/src/kernel source
cd "${base_dir}"

# Unused currently, but this script is a part of using the usb as an ethernet
# device
status 'Download g-ether script'
wget -c https://raw.github.com/RobertCNelson/tools/master/scripts/beaglebone-black-g-ether-load.sh -O ${work_dir}/root/beaglebone-black-g-ether-load.sh
chmod 0755 ${work_dir}/root/beaglebone-black-g-ether-load.sh

# Calculate the space to create the image and create
make_image

# Create the disk partitions
status "Create the disk partitions"
parted -s "${image_dir}/${image_name}.img" mklabel msdos
parted -s "${image_dir}/${image_name}.img" mkpart primary fat32 1MiB "${bootsize}"MiB
parted -s -a minimal "${image_dir}/${image_name}.img" mkpart primary "$fstype" "${bootsize}"MiB 100%

# Set the partition variables
loopdevice=$(losetup --show -fP "${image_dir}/${image_name}.img")
bootp="${loopdevice}p1"
rootp="${loopdevice}p2"

# Create file systems
status "Formatting partitions"
mkfs.vfat -n BOOT -F 16 "${bootp}"
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
mkdir -p "${base_dir}"/root/boot
mount "${bootp}" "${base_dir}"/root/boot

# Create an fstab so that we don't mount / read-only
status "/etc/fstab"
UUID=$(blkid -s UUID -o value ${rootp})
echo "UUID=$UUID /               $fstype    errors=remount-ro 0       1" >> ${work_dir}/etc/fstab

status "Rsyncing rootfs into image file"
rsync -HPavz -q --exclude boot "${work_dir}"/ "${base_dir}"/root/
status "Rsyncing rootfs into image file (/boot)"
rsync -rtx -q "${work_dir}"/boot "${base_dir}"/root
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
losetup -d "${loopdevice}"

# Compress image compilation
include compress_img

# Clean up all the temporary build stuff and remove the directories
# Comment this out to keep things around if you want to see what may have gone wrong
clean_build
