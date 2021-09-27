#!/usr/bin/env bash

log "eatmydata" green

# We need to manually extract eatmydata to use it for the second stage.
for archive in "${work_dir}"/var/cache/apt/archives/*eatmydata*.deb; do
  dpkg-deb --fsys-tarfile "$archive" >"${work_dir}"/eatmydata
  tar -xkf "${work_dir}"/eatmydata -C "${work_dir}"
  rm -f "${work_dir}"/eatmydata
done

# Prepare dpkg to use eatmydata
systemd-nspawn_exec dpkg-divert --divert /usr/bin/dpkg-eatmydata --rename --add /usr/bin/dpkg

cat >"${work_dir}"/usr/bin/dpkg <<EOF
#!/bin/sh
if [ -e /usr/lib/${lib_arch}/libeatmydata.so ]; then
    [ -n "\${LD_PRELOAD}" ] && LD_PRELOAD="\$LD_PRELOAD:"
    LD_PRELOAD="\$LD_PRELOAD\$so"
fi
for so in /usr/lib/${lib_arch}/libeatmydata.so; do
    [ -n "\$LD_PRELOAD" ] && LD_PRELOAD="\$LD_PRELOAD:"
    LD_PRELOAD="\$LD_PRELOAD\$so"
done
export LD_PRELOAD
exec "\$0-eatmydata" --force-unsafe-io "\$@"
EOF
chmod 0755 "${work_dir}"/usr/bin/dpkg
