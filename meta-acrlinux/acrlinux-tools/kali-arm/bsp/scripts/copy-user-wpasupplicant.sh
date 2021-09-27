#!/bin/bash -e

# If the file doesn't exist, exit 0 so that we don't report an error and systemd is happy.
if [ ! -f /boot/wpa_supplicant.conf ]; then
  exit 0;
fi

if [ -f /boot/wpa_supplicant.conf ]; then
  ssid=$(awk -F = '{if($0 ~ /ssid/) print $2}' /boot/wpa_supplicant.conf | tr -d '"')
  psk=$(awk -F = '{if($0 ~ /#psk/) print $2}' /boot/wpa_supplicant.conf | tr -d '"')
  wifi_dev="wlan0"
  if [ -n "$ssid" ] && [ -n "$psk" ] && [ ! "${#psk}" -lt "8" ]; then
    if [ -x "$(command -v nmcli)" ]; then
      nmcli con add con-name "$ssid" type wifi ifname "$wifi_dev" \
        ssid "$ssid" -- wifi-sec.key-mgmt wpa-psk wifi-sec.psk "$psk" \
        ipv4.method auto
    else
      install -m600 /boot/wpa_supplicant.conf /etc/wpa_supplicant
    fi
    mv /boot/wpa_supplicant.conf{,.bak}
  fi
fi
