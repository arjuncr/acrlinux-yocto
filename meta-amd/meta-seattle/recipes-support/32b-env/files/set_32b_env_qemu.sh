#!/bin/sh

help()
{
    echo " The scope of this script is to setup an environment for 32b legacy applications."
    echo " This is a replacement for MULTILIB mechanism which is not available yet for aarch64."
    echo " qemu-arm(user mode) is used to set access to 32b libraries instead of the host's libs"
    echo "                                                                                      "
    echo " Prerequisites:"
    echo "    - user must build a 32b amrv7 rootfs (e.q. build a image for qemuarm)"
    echo "    - user must copy 32b armv7 rootfs under 64b rootfs (e.q. /mnt/rootfs32)"
    echo "    - user must copy 32b application under 32b rootfs (e.q. /mnt/rootfs32/myapp)"
    echo "    - user must configure properly the smart package manager in order to access"
    echo "                <qemu-arm> and <kernel-module-binfmt-misc> packages otherwise it is"
    echo "                expected that those packages are already installed"
    echo ""
    echo " Run setup script to prepare the environment for 32b applications( -l param is optional )"
    echo "    > set_32b_env_qemu -r /mnt/rootfs32 -l /usr/local/extlib:/usr/local/mylib"
    echo ""
    echo " After that user should be able to run any 32b application as usual!"
    echo "    >./myapp"
    break
}

usage()
{
    echo "Setup environment to run 32b dynamically linked applications using qemu."
    echo "Prerequisites:"
    echo "   It is expected that qemu and kernel-module-binfmt-misc packages are installed"
    echo "   It is expected that 32bit rootfs is already installed!"
    echo ""
    echo "set_32b_env_qemu -r <path to 32b rootfs> -l <ext_libs1:ext_libs2:...>"
    echo " -r : [mandatory] path to 32b rootfs"
    echo " -l : [optional]  list of paths for nonstandard lib folders, paths must be relative to 32b rootfs"
    echo " -h : [optional]  display help"
    echo ""
    echo "Example: set_32b_env_qemu -r ./rootfs32b -l /usr/local/mylib:/usr/local/mylib2"
    break
}

if [[ $# -eq 0 ]] ; then
    echo "[ERR]: Missing script parameters!"
    echo ""
    usage
fi

while getopts "hr:l:" OPTION;
do
        case $OPTION in

                r)
                        ROOTFS32b_PATH="$OPTARG"
                        ;;

                l)
                        LIBS_PATH="$OPTARG"
                        ;;

                h)
                        usage
                        ;;

                ?)
                        help
                        ;;

        esac
done

ABS_PATH=`cd "${ROOTFS32b_PATH}"; pwd`
ROOTFS32b_PATH=${ABS_PATH}

if [[ ! -d ${ROOTFS32b_PATH} ]] ; then
    usage
fi

echo "Please wait..."
echo ""
QEMU_EXISTS=`which qemu-arm`
if [[ "${QEMU_EXISTS}" == "" ]] ; then
    echo "Please make sure smart package manager is configured !"
    echo "Otherwise make sure <qemu> and <kernel-module-binfmt-misc> are installed!"
    smart --quiet update
    smart --quiet install qemu
    smart --quiet install kernel-module-binfmt-misc
fi

echo "."
mount -t binfmt_misc none /proc/sys/fs/binfmt_misc
if [[ "$?" != "0" ]] ; then
    echo "Failed to install binfmt_misc or the setup was already prepared for 32b"
    echo ""
    echo "Please, make sure the module is available into system"
    echo "How to install the binfmt_misc package:"
    echo "         smart install binfmt_misc"
    break
fi

BINFMT_INSTALLED=`cat /proc/sys/fs/binfmt_misc/status`
if [[ "${BINFMT_INSTALLED}" != "enabled" ]] ; then
    echo "binfmt_misc not enabled!!"
    break
fi

echo ".."
# configure qemu to run 32b armv7 applications
echo ":arm:M::\x7fELF\x01\x01\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x02\x00\x28\x00:\xff\xff\xff\xff\xff\xff\xff\x00\xff\xff\xff\xff\xff\xff\xff\xff\xfe\xff\xff\xff:/usr/bin/qemu-arm:" > /proc/sys/fs/binfmt_misc/register

export QEMU_LD_PREFIX=${ROOTFS32b_PATH}
export QEMU_SET_ENV="LD_LIBRARY_PATH=${LIBS_PATH}"

echo "Done!"
echo "Environment was set to run 32b applications!"
