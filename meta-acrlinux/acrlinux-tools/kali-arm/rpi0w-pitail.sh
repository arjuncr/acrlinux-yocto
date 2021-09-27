#!/usr/bin/env bash
#
# Kali Linux ARM build-script for Raspberry Pi Zero W (Pi-Tail)
# https://gitlab.com/kalilinux/build-scripts/kali-arm
#
# This is a supported device - which you can find pre-generated images for
# More information: https://www.kali.org/docs/arm/raspberry-pi-zero-w-pi-tail/
#

# Stop on error
set -e

# shellcheck disable=SC2154
# Load general functions
# shellcheck source=/dev/null
source ./common.d/functions.sh

# Hardware model
hw_model=${hw_model:-"rpi0w-pitail"}
# Architecture
architecture=${architecture:-"armel"}
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
#include network
#add_interface wlan0

# Copy directory bsp into build dir
status "Copy directory bsp into build dir"
cp -rp bsp "${work_dir}"

# Download Pi-Tail files
git clone --depth 1 https://github.com/re4son/Kali-Pi ${work_dir}/opt/Kali-Pi
wget -O ${work_dir}/etc/systemd/system/pi-tail.service https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/pi-tail.service
wget -O ${work_dir}/etc/systemd/system/pi-tailbt.service https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/pi-tailbt.service
wget -O ${work_dir}/etc/systemd/system/pi-tailms.service https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/pi-tailms.service
wget -O ${work_dir}/etc/systemd/system/pi-tailap.service https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/pi-tailap.services
wget -O ${work_dir}/etc/systemd/network/pan0.network https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/pan0.network
wget -O ${work_dir}/etc/systemd/system/bt-agent.service https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/bt-agent.service
wget -O ${work_dir}/etc/systemd/system/bt-network.service https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/bt-network.service
wget -O ${work_dir}/lib/systemd/system/hciuart.service https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/hciuart.service
wget -O ${work_dir}/boot/cmdline.txt https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/cmdline.storage
wget -O ${work_dir}/boot/cmdline.storage https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/cmdline.storage
wget -O ${work_dir}/boot/cmdline.eth https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/cmdline.eth
wget -O ${work_dir}/boot/interfaces https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/interfaces
wget -O ${work_dir}/boot/interfaces.example.wifi https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/interfaces.example.wifi
wget -O ${work_dir}/boot/interfaces.example.wifi-AP https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/interfaces.example.wifi-AP
wget -O ${work_dir}/boot/pi-tailbt.example https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/pi-tailbt.example
wget -O ${work_dir}/boot/wpa_supplicant.conf https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/wpa_supplicant.conf
wget -O ${work_dir}/boot/Pi-Tail.README https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/Pi-Tail.README
wget -O ${work_dir}/boot/Pi-Tail.HOWTO https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/Pi-Tail.HOWTO
wget -O ${work_dir}/boot/config.txt https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/config.txt
wget -O ${work_dir}/etc/udev/rules.d/70-persistent-net.rules https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/70-persistent-net.rules
wget -O ${work_dir}/opt/Kali-Pi/Menus/RAS-AP/dnsmasq-dhcpd.conf https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/dnsmasq-dhcpd.conf
wget -O ${work_dir}/opt/Kali-Pi/Menus/RAS-AP/ras-ap.sh https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/ras-ap.sh
wget -O ${work_dir}/opt/Kali-Pi/Menus/RAS-AP/ras-ap.conf https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/ras-ap.conf
wget -O ${work_dir}/usr/local/bin/mon0up https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/mon0up
wget -O ${work_dir}/usr/local/bin/mon0down https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/pi-tail/mon0down
wget -O ${work_dir}/lib/systemd/system/vncserver@.service https://github.com/Re4son/vncservice/raw/master/vncserver@.service
chmod 0755 ${work_dir}/usr/local/bin/mon0up ${work_dir}/usr/local/bin/mon0down
mkdir -p ${work_dir}/etc/skel/.vnc/
wget -O ${work_dir}/etc/skel/.vnc/xstartup https://raw.githubusercontent.com/Re4son/RPi-Tweaks/master/vncservice/xstartup
chmod 0750 ${work_dir}/etc/skel/.vnc/xstartup


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

status_stage3 'Create kali user'
# Normally this would be done by runonce, however, because this image is special, and needs the kali home directory
# to exist before the first boot, we create it here, and remove the script that does it in the runonce stuff later.
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

status_stage3 'Update apt'
export DEBIAN_FRONTEND=noninteractive
eatmydata apt-get update

status_stage3 'Install core packages'
eatmydata apt-get -y install ${third_stage_pkgs}

status_stage3 'Install packages'
eatmydata apt-get install -y ${packages} || eatmydata apt-get install -y --fix-broken

status_stage3 'Install desktop packages'
eatmydata apt-get install -y ${desktop_pkgs} ${extra} || eatmydata apt-get install -y --fix-broken

status_stage3 'Install PiTail packages'
eatmydata apt-get install -y ${pitail_pkgs} || eatmydata apt-get install -y --fix-broken

status_stage3 'ntp does not always sync the date, but systemd-timesyncd does, so we remove ntp and reinstall it with this'
eatmydata apt-get install -y systemd-timesyncd --autoremove

status_stage3 'Clean up'
eatmydata apt-get -y --purge autoremove

status_stage3 'Linux console/keyboard configuration'
echo 'console-common console-data/keymap/policy select Select keymap from full list' | debconf-set-selections
echo 'console-common console-data/keymap/full select en-latin1-nodeadkeys' | debconf-set-selections

status_stage3 'Copy all services'
cp -p /bsp/services/all/*.service /etc/systemd/system/
cp -p /bsp/services/rpi/*.service /etc/systemd/system/

status_stage3 'Script mode wlan monitor START/STOP'
install -m755 /bsp/scripts/monstart /usr/bin/
install -m755 /bsp/scripts/monstop /usr/bin/

status_stage3 'Install the kernel packages'
echo "deb http://http.re4son-kernel.com/re4son kali-pi main" > /etc/apt/sources.list.d/re4son.list
wget -qO /etc/apt/trusted.gpg.d/kali_pi-archive-keyring.gpg https://re4son-kernel.com/keys/http/kali_pi-archive-keyring.gpg
eatmydata apt-get update
eatmydata apt-get install -y ${re4son_pkgs}

status_stage3 'Copy script rpi-resizerootfs'
install -m755 /bsp/scripts/rpi-resizerootfs /usr/sbin/
install -m755 /bsp/scripts/growpart /usr/local/bin/

status_stage3 'Copy script for handling wpa_supplicant file'
install -m755 /bsp/scripts/copy-user-wpasupplicant.sh /usr/bin/

status_stage3 'Enable rpi-resizerootfs first boot'
systemctl enable rpi-resizerootfs

status_stage3 'Generate SSH host keys on first run'
systemctl enable regenerate_ssh_host_keys

status_stage3 'Enable ssh'
systemctl enable ssh

status_stage3 'Enable copying of user wpa_supplicant.conf file'
systemctl enable copy-user-wpasupplicant

status_stage3 'Enabling ssh by putting ssh or ssh.txt file in /boot'
systemctl enable enable-ssh

status_stage3 'Allow users to use NetworkManager over ssh'
install -m644 /bsp/polkit/10-NetworkManager.pkla /var/lib/polkit-1/localauthority/50-local.d

status_stage3 'Set a REGDOMAIN'
sed -i -e 's/REGDOM.*/REGDOMAIN=00/g' /etc/default/crda

status_stage3 'Enable login over serial'
echo "T0:23:respawn:/sbin/agetty -L ttyAMA0 115200 vt100" >> /etc/inittab

status_stage3 'Whitelist /dev/ttyGS0 so that users can login over the gadget serial device if they enable it'
# https://github.com/offensive-security/kali-arm-build-scripts/issues/151
echo "ttyGS0" >> /etc/securetty

status_stage3 'Turn off kernel dmesg showing up in console since rpi0 only uses console'
echo "#!/bin/sh -e" > /etc/rc.local
echo "#" >> /etc/rc.local
echo "# rc.local" >> /etc/rc.local
echo "#" >> /etc/rc.local
echo "# This script is executed at the end of each multiuser runlevel." >> /etc/rc.local
echo "# Make sure that the script will "exit 0" on success or any other" >> /etc/rc.local
echo "# value on error." >> /etc/rc.local
echo "#" >> /etc/rc.local
echo "# In order to enable or disable this script just change the execution" >> /etc/rc.local
echo "# bits." >> /etc/rc.local
echo "dmesg -D" >> /etc/rc.local
echo "exit 0" >> /etc/rc.local
chmod +x /etc/rc.local

status_stage3 'Copy bashrc for root user'
cp  /etc/skel/.bashrc /root/.bashrc

status_stage3 'Copy xstartup for root and kali users'
cp -r /etc/skel/.vnc /root/
cp -r /etc/skel/.vnc /home/kali/

status_stage3 'Configure darkstat to use wlan0 by default'
sed -i 's/^INTERFACE="-i eth0"/INTERFACE="-i wlan0"/g' "/lib/systemd/system/networking.service"

status_stage3 'Reduce DHCP timeout to speed up boot process'
sed -i -e 's/#timeout 60/timeout 10/g' /etc/dhcp/dhclient.conf

status_stage3 'Boot into cli'
systemctl set-default multi-user.target

status_stage3 'Create swap file'
sudo dd if=/dev/zero of=/swapfile.img bs=1M count=1024
sudo mkswap /swapfile.img
chmod 0600 /swapfile.img

status_stage3 'Enable Pi-Tail services'
systemctl enable pi-tail.service
systemctl enable pi-tailbt.service
systemctl enable pi-tailms.service
systemctl enable pi-tailap.service
systemctl enable systemd-networkd
systemctl enable bt-agent
systemctl enable bt-network
systemctl disable NetworkManager
systemctl disable haveged

status_stage3 'Set vnc password'
echo kalikali | vncpasswd -f > /home/kali/.vnc/passwd
chown -R kali:kali /home/kali/.vnc
chmod 0600 /home/kali/.vnc/passwd

status_stage3 'Try and make the console a bit nicer. Set the terminus font for a bit nicer display'
sed -i -e 's/FONTFACE=.*/FONTFACE="Terminus"/' /etc/default/console-setup
sed -i -e 's/FONTSIZE=.*/FONTSIZE="6x12"/' /etc/default/console-setup

status_stage3 'Fix startup time from 5 minutes to 15 secs on raise interface wlan0'
sed -i 's/^TimeoutStartSec=5min/TimeoutStartSec=15/g' "/usr/lib/systemd/system/networking.service"

status_stage3 'Enable runonce'
install -m755 /bsp/scripts/runonce /usr/sbin/
cp -rf /bsp/runonce.d /etc
# As noted above, remove the creation of the kali user, since we do it above.
rm /etc/runonce.d/00-add-user
systemctl enable runonce

status_stage3 'Clean up dpkg.eatmydata'
rm -f /usr/bin/dpkg
dpkg-divert --remove --rename /usr/bin/dpkg
EOF

# Run third stage
chmod 0755 "${work_dir}"/third-stage
status "Run third stage"
systemd-nspawn_exec /third-stage

## Fix the the infamous “Authentication Required to Create Managed Color Device” in vnc
cat << EOF > ${work_dir}/etc/polkit-1/localauthority/50-local.d/45-allow-colord.pkla
[Allow Colord all Users]
Identity=unix-user:*
Action=org.freedesktop.color-manager.create-device;org.freedesktop.color-manager.create-profile;org.freedesktop.color-manager.delete-device;org.freedesktop.color-manager.delete-profile;org.freedesktop.color-manager.modify-device;org.freedesktop.color-manager.modify-profile
ResultAny=no
ResultInactive=no
ResultActive=yes
EOF

status 'Always put our favourite adapter as wlan1'
cat << EOF > ${work_dir}/etc/udev/rules.d/70-persistent-net.rules
# USB device 0x:0x (ath9k_htc)
SUBSYSTEM=="net", ACTION=="add", DRIVERS=="?*", ATTR{address}=="", ATTR{dev_id}=="0x0", ATTR{type}=="1", KERNEL=="wlan*", NAME="wlan1"
EOF

status 'Create cmdline.txt file'
cat << EOF > ${work_dir}/boot/cmdline.txt
dwc_otg.lpm_enable=0 console=serial0,115200 console=tty1 root=/dev/mmcblk0p2 rootfstype=$fstype elevator=deadline fsck.repair=yes rootwait
EOF

# systemd doesn't seem to be generating the fstab properly for some people, so
# let's create one
status 'Create /etc/fstab'
cat << EOF > ${work_dir}/etc/fstab
# <file system> <mount point>   <type>  <options>       <dump>  <pass>
proc            /proc           proc    defaults          0       0
/dev/mmcblk0p1  /boot           vfat    defaults          0       2
/dev/mmcblk0p2  /               $fstype    defaults,noatime  0       1
/swapfile.img   none            swap    sw                0       0
EOF

# Clean system
include clean_system
trap clean_build ERR SIGTERM SIGINT
cd "${current_dir}/"

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
mkfs.vfat -n BOOT -F 32 "${bootp}"
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

status "Rsyncing rootfs into image file"
rsync -HPavz -q --exclude boot "${work_dir}"/ "${base_dir}"/root/
status "Rsyncing rootfs into image file (/boot)"
rsync -rtx -q "${work_dir}"/boot "${base_dir}"/root
sync

# Flush buffers and bytes - this is nicked from the Devuan arm-sdk
blockdev --flushbufs "${loopdevice}"
python3 -c 'import os; os.fsync(open("'${loopdevice}'", "r+b"))'

# Unmount filesystem
status "Unmount filesystem"
umount -l "${bootp}"
umount -l "${rootp}"

# Check filesystem
status "Check filesystem"
dosfsck -w -r -a -t "$bootp"
e2fsck -y -f "${rootp}"

# Remove loop devices
status "Remove loop devices"
losetup -d "${loopdevice}"

# Compress image compilation
include compress_img

# Clean up all the temporary build stuff and remove the directories
# Comment this out to keep things around if you want to see what may have gone wrong
clean_build
