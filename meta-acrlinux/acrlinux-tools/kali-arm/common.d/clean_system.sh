#!/usr/bin/env bash
# shellcheck disable=SC2154

log "clean system" green

# Clean system
systemd-nspawn_exec <<'EOF'
rm -f /0
rm -rf /bsp
command fc-cache && fc-cache -frs
rm -rf /tmp/*
rm -rf /etc/*-
rm -rf /hs_err*
rm -rf /userland
rm -rf /opt/vc/src
rm -rf /third-stage
rm -f /etc/ssh/ssh_host_*
rm -rf /var/lib/dpkg/*-old
rm -rf /var/lib/apt/lists/*
rm -rf /var/cache/apt/*.bin
rm -rf /var/cache/debconf/*-old
rm -rf /var/cache/apt/archives/*
rm -rf /etc/apt/apt.conf.d/apt_opts
rm -rf /etc/apt/apt.conf.d/99_norecommends
for logs in $(find /var/log -type f); do > $logs; done
history -c
EOF

# Choose a locale
set_locale "$locale"

# Disable the use of http proxy in case it is enabled
disable_proxy

# Mirror & suite replacement
restore_mirror

# Reload sources.list
#include sources.list

# Newer systemd requires that /etc/machine-id exists but is empty
rm -f "${work_dir}"/etc/machine-id || true
touch "${work_dir}"/etc/machine-id
rm -f "${work_dir}"/var/lib/dbus/machine-id || true

# Define DNS server after last running systemd-nspawn.
echo "nameserver ${nameserver}" >"${work_dir}"/etc/resolv.conf
