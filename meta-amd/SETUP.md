# 1. Setting up the build system

Building images for AMD machines requires setting up the Yocto Project
Build System. Please follow the guidelines on
[Yocto Project Overview and Concepts Manual](https://www.yoctoproject.org/docs/3.1.4/overview-manual/overview-manual.html)
and [Yocto Project Quick Build Guide](https://www.yoctoproject.org/docs/3.1.4/brief-yoctoprojectqs/brief-yoctoprojectqs.html)
if you are not familiar with the Yocto Project and it's Build System.

Running the following commands will setup the build system and will
enable us to build recipes & images for any of the supported AMD machines (i.e `e3000` or `rome`).

### 1.1 Prerequisites

Install the build system's dependencies:
```sh
sudo apt install -y gawk wget git-core diffstat unzip texinfo \
     gcc-multilib build-essential chrpath socat cpio python3 \
     python3-pip python3-pexpect xz-utils debianutils iputils-ping \
     python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev pylint3 \
     xterm
```

### 1.2 Download the build system and the meta-data layers

Select the Yocto Project branch:
```sh
YOCTO_BRANCH="dunfell"
```

Clone the git repositories: 
```sh
git clone --single-branch --branch "${YOCTO_BRANCH}" \
    "git://git.yoctoproject.org/poky" "poky-amd-${YOCTO_BRANCH}"
cd poky-amd-${YOCTO_BRANCH}
git clone --single-branch --branch "${YOCTO_BRANCH}" \
    "git://git.openembedded.org/meta-openembedded"
git clone --single-branch --branch "${YOCTO_BRANCH}" \
    "git://git.yoctoproject.org/meta-dpdk"
git clone --single-branch --branch "${YOCTO_BRANCH}" \
    "git://git.yoctoproject.org/meta-amd"
```

Checkout commit hashes:
```sh
git checkout --quiet tags/yocto-3.1.4
cd meta-openembedded
git checkout --quiet f2d02cb71eaff8eb285a1997b30be52486c160ae
cd ../meta-dpdk
git checkout --quiet 9465b6d27fc9520e18d05cc50dbed9d84e111953
cd ../meta-amd
git checkout --quiet tags/dunfell-rome-ga-202103
cd ..
```

---
#### What's next

Continue to "Section 2 - Setting up and starting a build"
([BUILD.md](BUILD.md)) for instructions on how to setup and start a
build for a particular AMD machine.
