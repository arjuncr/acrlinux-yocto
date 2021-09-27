#!/usr/bin/env bash

# Define sources.list
log "Define sources.list" green
cat <<EOF > "${work_dir}"/etc/apt/sources.list
deb ${mirror} ${suite} ${components//,/ }
#deb-src ${mirror} ${suite} ${components//,/ }
EOF
