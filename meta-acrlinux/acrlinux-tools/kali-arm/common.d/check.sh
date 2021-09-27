#!/usr/bin/env bash

# Check EUID=0 you can run any binary as root.
if [[ $EUID -ne 0 ]]; then
  log "This script must be run as root or have super user permissions" red
  log "Use: $(tput sgr0)sudo $0" green
  exit 1
fi

# Check exist bsp directory.
if [ ! -e "bsp" ]; then
  log "Error: missing bsp directory structure" red
  log "Please clone the full repository ${kaligit}/build-scripts/kali-arm" green
  exit 255
fi

# Check directory build
if [ -e "${base_dir}" ]; then
  log "${base_dir} directory exists, will not continue" red
  exit 1
elif [[ ${current_dir} =~ [[:space:]] ]]; then
  log "The directory "\"${current_dir}"\" contains whitespace. Not supported." red
  exit 1
else
  print_config
  mkdir -p ${base_dir}
fi

# Detect architecture
case ${architecture} in
  arm64)
    qemu_bin="/usr/bin/qemu-aarch64-static"
    lib_arch="aarch64-linux-gnu"
    ;;
  armhf)
    qemu_bin="/usr/bin/qemu-arm-static"
    lib_arch="arm-linux-gnueabihf"
    ;;
  armel)
    qemu_bin="/usr/bin/qemu-arm-static"
    lib_arch="arm-linux-gnueabi"
    ;;
esac

# Check systemd-nspawn version
nspawn_ver=$(systemd-nspawn --version | awk '{if(NR==1) print $2}')
if [[ $nspawn_ver -ge 245 ]]; then
  extra_args="--hostname=$hostname -q -P"
elif [[ $nspawn_ver -ge 241 ]]; then
  extra_args="--hostname=$hostname -q"
else
  extra_args="-q"
fi

# Check CPU cores to use
if [ "$cpu_cores" = "0" ]; then
  num_cores=$(nproc --all)
elif [[ "$cpu_cores" =~ ^[0-9]{1,2}$ ]]; then
  if [ "$cpu_cores" -le $(nproc --all) ]; then
    num_cores="$cpu_cores"
  else
    num_cores=$(nproc --all)
  fi
elif [[ "$cpu_cores" =~ ^[-0-9]{1,2}$ ]]; then
  num_cores=$(nproc --ignore="${cpu_cores/-/}")
else
  num_cores="1"
fi

# Automatic configuration to use an http proxy, such as apt-cacher-ng.
apt_cacher=${apt_cacher:-"$(lsof -i :3142 | cut -d ' ' -f3 | uniq | sed '/^\s*$/d')"}
if [ -n "$proxy_url" ]; then
  export http_proxy=$proxy_url
elif [ "$apt_cacher" = "apt-cacher-ng" ]; then
  if [ -z "$proxy_url" ]; then
    proxy_url=${proxy_url:-"http://127.0.0.1:3142/"}
    export http_proxy=$proxy_url
  fi
fi
