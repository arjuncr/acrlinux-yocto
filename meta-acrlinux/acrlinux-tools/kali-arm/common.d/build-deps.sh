#!/usr/bin/env bash
#
# Kali Linux ARM build-script
# https://gitlab.com/kalilinux/build-scripts/kali-arm
#

# Stop on error
set -e

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

apt-wait() {
  while fuser /var/{lib/{dpkg,apt/lists},cache/apt/archives}/lock >/dev/null 2>&1; do
    sleep 5
  done

  if [ "$1" == "update" ]; then
    apt-get update
  elif [ "$1" == "install_deps" ]; then
    echo -e "\n[i] Installing $deps..."
    apt-get install -y -qq $deps
  elif [ "$1" == "remove" ]; then
    echo -e "\n[i] Removing $@..."
    apt-get -y --purge "$@"
  fi
}


# Function create script to clean system packages
clean_system() {
  mkdir -p ./local/
  clean_script=${backup_packages/list-pkgs/remove-pkgs}.sh
  echo -e "\n[i] Cleaning up (${clean_script})..."
  cat << EOF > ${clean_script}
#!/usr/bin/env bash

set -e

if [[ \$EUID -ne 0 ]]; then
  echo "[-] This script must be run as root" >&2
  exit 1
fi

clean_system () {
  dpkg --clear-selections
  dpkg --set-selections < ${backup_packages}
  apt-get -y dselect-upgrade
  apt-get -y remove --purge \$(dpkg -l | grep "^rc" | awk '{print \$2}')
  ${del_arch_i386}
}

clear
echo "Use this script under your responsibility"
read -p "Are you sure you want to remove the packages from the build? [y/n]: " yn
case \$yn in
  [Yy]* ) clean_system;;
  [Nn]* ) exit;;
      * ) echo "Please enter Y or N!";;
esac
EOF
  chmod 0755 ${clean_script}

  #rm -f "${backup_packages}"
}

trap clean_system ERR SIGTERM SIGINT

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

# Check permissions script
if [[ $EUID -ne 0 ]]; then
  echo "[-] This script must be run as root" >&2
  exit 1
fi

# Check compatible systems
if ! which dpkg > /dev/null; then
   echo "[-] Script only compatible with Debian-based systems" >&2
   exit 1
fi

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

# List of installed packages file
backup_packages=./local/build-deps-list-pkgs-$(date +"%Y-%m-%d-%H-%M")
mkdir -p ./local/

# Create a current list of installed packages
dpkg --get-selections > ${backup_packages}

compilers="crossbuild-essential-arm64 crossbuild-essential-armhf crossbuild-essential-armel gcc-arm-none-eabi"
libpython2_dev="libexpat1-dev libpython2.7 libpython2.7-dev libpython2.7-minimal libpython2.7-stdlib"
dependencies="gnupg flex bison gperf build-essential zip curl libncurses5-dev zlib1g-dev \
parted kpartx debootstrap pixz qemu-user-static abootimg cgpt vboot-kernel-utils vboot-utils \
u-boot-tools bc lzma lzop automake autoconf m4 rsync schedtool git dosfstools e2fsprogs \
device-tree-compiler libssl-dev systemd-container libgmp3-dev gawk qpdf make libfl-dev swig \
${libpython2_dev} dbus python3-dev cgroup-tools lsof jetring eatmydata cmake pkg-config less"

deps="${dependencies} ${compilers}"

# Update list deb packages
apt-wait update
# Install dependencies
apt-wait install_deps

# Check minimum version debootstrap
debootstrap_ver=$(debootstrap --version | grep -o '[0-9.]\+' | head -1)
if dpkg --compare-versions "$debootstrap_ver" lt "1.0.105"; then
  echo "[-] Currently your version of debootstrap ($debootstrap_ver) does not support the script" >&2
  echo "[-] The minimum version of debootstrap is 1.0.105" >&2
  exit 1
fi

# Install kali-archive-keyring
if [ ! -f /usr/share/keyrings/kali-archive-keyring.gpg ]; then
  echo -e "\n[i] Installing kali-archive-keyring..."
  temp_key="$(mktemp -d)"
  git clone https://gitlab.com/kalilinux/packages/kali-archive-keyring.git $temp_key
  cd $temp_key/
  make
  make install
  cd $OLDPWD/
  rm -rf $temp_key
fi

echo -e "\n[i] Waiting for other software manager to finish..."

# Install packages i386
if [ $(arch) == 'x86_64' ]; then
  echo -e "\n[i] Detected x64"
  if [ -z $(dpkg --print-foreign-architectures | grep i386) ]; then
    dpkg --add-architecture i386
    apt-wait update
    deps="-o APT::Immediate-Configure=0 libstdc++6:i386 libc6:i386 libgcc1:i386 zlib1g:i386 libncurses5:i386"
    apt-wait install_deps
    del_arch_i386="dpkg --remove-architecture i386"
  elif [[ $(dpkg --print-foreign-architectures | grep i386) == 'i386' ]]; then
    deps="-o APT::Immediate-Configure=0 libstdc++6:i386 libc6:i386 libgcc1:i386 zlib1g:i386 libncurses5:i386"
    apt-wait install_deps
  fi
elif [ $(arch) == 'i386' ]; then
  deps="libstdc++6 libc6 libgcc1 zlib1g libncurses5"
  apt-wait install_deps
fi

# Create the script to clean the system
clean_system
