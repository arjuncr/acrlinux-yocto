#!/usr/bin/env bash

log "rpi firmware" green

# Create cmdline.txt file
cat <<EOF > "${work_dir}"/boot/cmdline.txt
dwc_otg.fiq_fix_enable=2 console=ttyAMA0,115200 kgdboc=ttyAMA0,115200 console=tty1 root=/dev/mmcblk0p2 rootfstype=$fstype elevator=deadline fsck.repair=yes rootwait net.ifnames=0
EOF

# Copy a default config, with everything commented out so people find it when
# they go to add something when they are following instructions on a website.
cp ./bsp/firmware/rpi/config.txt "${work_dir}"/boot/config.txt

# To boot 64bit, these lines *have* to be in config.txt
if [[ "${architecture}" == "arm64" ]]; then
  # Remove repeat conditional filters [all] in config.txt
  sed -i "59,66d" "${work_dir}"/boot/config.txt
  cat <<EOF >>"${work_dir}"/boot/config.txt

[pi2]
# Pi2 is 64bit only on v1.2+
# 64bit kernel for Raspberry Pi 2 is called kernel8 (armv8a)
kernel=kernel8-alt.img
[pi3]
# 64bit kernel for Raspberry Pi 3 is called kernel8 (armv8a)
kernel=kernel8-alt.img
[pi4]
# Enable DRM VC4 V3D driver on top of the dispmanx display stack
#dtoverlay=vc4-fkms-v3d
#max_framebuffers=2
# 64bit kernel for Raspberry Pi 4 is called kernel8l (armv8a)
kernel=kernel8l-alt.img
[all]
#dtoverlay=vc4-fkms-v3d
# Tell firmware to go 64bit mode.
arm_64bit=1
EOF
fi
