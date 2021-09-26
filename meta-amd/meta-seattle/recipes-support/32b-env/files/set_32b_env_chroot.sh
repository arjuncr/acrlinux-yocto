#!/bin/sh

help()
{
    echo " The scope of this script is to setup an environment for 32b legacy applications."
    echo " This is a replacement for MULTILIB mechanism which is not available yet for aarch64."
    echo " chroot is used to setup an isolated environment for 32b applications"
    echo "                                                                                      "
    echo " Prerequisites:"
    echo "    - user must build a 32b amrv7 rootfs (e.q. build a image for qemuarm)"
    echo "    - user must copy 32b armv7 rootfs under 64b rootfs (e.q. /mnt/rootfs32)"
    echo "    - user must copy 32b application under 32b rootfs (e.q. /mnt/rootfs32/myapp)"
    echo ""
    echo " Run setup script to prepare the environment for 32b applications"
    echo "    > set_32b_env_chroot -r /mnt/rootfs32"
    echo ""
    echo " Run 32b applications"
    echo "    @> ./myapp/myexec"
    echo ""
    echo " In order to return to default root just type <exit>"
    echo "    @> exit"
    exit
}

usage()
{
    echo "set_32b_env_chroot OPTIONS"
    echo "OPTIONS:"
    echo ""
    echo "     -r <32b_rootfs> : path to a 32b rootfs."
    echo "     -h              : display help"
    echo ""
    exit
}

if [[ $# -eq 0 ]] ; then
    echo "[ERR]: Missing script parameters!"
    echo ""
    usage
fi

while getopts "hr:" OPTION;
do
        case $OPTION in

                r)
                        ROOTFS32b_PATH="$OPTARG"
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

if [[ ! -d ${ROOTFS32b_PATH}/dev ]] ; then
    mkdir ${ROOTFS32b_PATH}/dev
fi

if [[ ! -d ${ROOTFS32b_PATH}/proc ]] ; then
    mkdir ${ROOTFS32b_PATH}/proc
fi

if [[ ! -d ${ROOTFS32b_PATH}/sys ]] ; then
    mkdir ${ROOTFS32b_PATH}/sys
fi

if [[ ! -d ${ROOTFS32b_PATH}/etc ]] ; then
    mkdir ${ROOTFS32b_PATH}/etc
fi

umount ${ROOTFS32b_PATH}/dev/pts 2>/dev/null
umount ${ROOTFS32b_PATH}/dev 2>/dev/null
mount --bind /dev ${ROOTFS32b_PATH}/dev
if [[ "$?" != "0" ]] ; then
    echo "Failed to mount /dev folder"
    exit
fi

umount ${ROOTFS32b_PATH}/proc 2>/dev/null
mount --bind /proc ${ROOTFS32b_PATH}/proc
if [[ "$?" != "0" ]] ; then
    echo "Failed to mount /proc folder"
    exit
fi

umount ${ROOTFS32b_PATH}/sys 2>/dev/null
mount --bind /sys ${ROOTFS32b_PATH}/sys
if [[ "$?" != "0" ]] ; then
    echo "Failed to mount /sys folder"
    exit
fi

mount --bind /dev/pts ${ROOTFS32b_PATH}/dev/pts
if [[ "$?" != "0" ]] ; then
    echo "Failed to mount /dev/pts folder"
    exit
fi

cp /etc/resolv.conf ${ROOTFS32b_PATH}/etc/resolv.conf
if [[ "$?" != "0" ]] ; then
    echo "Failed to copy resolv.conf file"
    exit
fi

PATH=/bin:/sbin:/usr/bin:/usr/sbin

echo "New root will be ${ROOTFS32b_PATH}/   type \"exit\" to return to /"
chroot ${ROOTFS32b_PATH}/ /bin/sh
if [[ "$?" != "0" ]] ; then
    echo "Failed to start chroot!"
fi
