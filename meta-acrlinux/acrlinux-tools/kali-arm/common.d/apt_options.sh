#!/usr/bin/env bash

log "apt options" green

cat > "${work_dir:=}"/etc/apt/apt.conf.d/apt_opts <<EOF
DPkg::Options "--force-confnew";
APT::Get::allow-change-held-packages "true";
Acquire::Retries "3";
EOF

if [[ "${variant:=}" == *lite* ]]; then
  cat > "${work_dir:=}"/etc/apt/apt.conf.d/99_norecommends <<EOM
APT::Install-Recommends "false";
APT::AutoRemove::RecommendsImportant "false";
APT::AutoRemove::SuggestsImportant "false";
EOM
fi

# Enable the use of http proxy in third-stage in case it is enabled.
if [ -n "${proxy_url:=}" ]; then
  log "enabling proxy" green
  echo "Acquire::http { Proxy \"$proxy_url\" };" > "${work_dir:=}"/etc/apt/apt.conf.d/66proxy
fi
