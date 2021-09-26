DESCRIPTION = "System development image with XFCE"
LICENSE = "MIT"

require systemdev-image.bb

IMAGE_FEATURES += "splash"

IMAGE_INSTALL += "packagegroup-xfce-base \
                  packagegroup-core-x11-xserver \
                  packagegroup-core-x11-utils \
                  dbus \
                  pointercal \
                  liberation-fonts \
                  kernelshark \
                  gstreamer1.0 \
                  gstreamer1.0-meta-base \
                  gstreamer1.0-plugins-base-meta \
                  gstreamer1.0-plugins-good-meta \
                  gstreamer1.0-plugins-bad-meta \
                  gstreamer1.0-plugins-ugly-meta \
                  gstreamer1.0-libav \
                  gstreamer-vaapi-1.0 \
                  libva-intel-driver \
                  libsdl2 \
                  wmctrl \
                 "

SYSTEMD_DEFAULT_TARGET = "graphical.target"

REQUIRED_DISTRO_FEATURES = "x11"
