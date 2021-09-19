**acrlinux**    


**Setting up your acrlinux project**  

**Prerequisite**     

Ubuntu 20.04  
sudo apt-get install -y git build-essential libsdl1.2-dev texinfo gawk chrpath diffstat

**Download the source**  

git clone https://github.com/arjuncr/acrlinux-yocto.git      
              OR     
git clone https://github.com/arjuncr/acrlinux-yocto.git -b acrlinux-active  

cd acrlinux-yocto   

**Build**   

**To build x86_64**   

```
. acrlinux-env

bitbake acrlinux-minimal    
```  

```
Loading cache: 100% |                                                                                  | ETA:  --:--:--
Loaded 0 entries from dependency cache.
Parsing recipes: 100% |#################################################################################| Time: 0:00:19
Parsing of 813 .bb files complete (0 cached, 813 parsed). 1437 targets, 40 skipped, 0 masked, 0 errors.
NOTE: Resolving any missing task queue dependencies

Build Configuration:
BB_VERSION           = "1.49.2"
BUILD_SYS            = "x86_64-linux"
NATIVELSBSTRING      = "ubuntu-20.04"
TARGET_SYS           = "x86_64-acrlinux-linux"
MACHINE              = "acrlinux_qemu_x86-64"
DISTRO               = "acrlinux"
DISTRO_VERSION       = "1.0.0.0"
TUNE_FEATURES        = "m64 core2"
TARGET_FPU           = ""
meta
meta-poky
meta-acrlinux        = "master:153964ca4079907bd6597db12ac569d5003db417"

```

## Run with qemu  
runqemu acrlinux_qemu_x86-64 nographic       
   
console login:     

user name : root     
password: no need of password      

```
tarting udev
[    3.105817] udevd[93]: starting version 3.2.10
[    3.166900] udevd[94]: starting eudev-3.2.10
[    3.188983] udevadm (95) used greatest stack depth: 13808 bytes left
[    4.378804] EXT4-fs (vda): re-mounted. Opts: (null)
[    4.379151] ext4 filesystem being remounted at / supports timestamps until 2038 (0x7fffffff)
depmod: can't change directory to 'lib/modules/5.10.25-yocto-standard': No such file or directory
INIT: Entering runlevel: 5
Configuring network interfaces... ip: RTNETLINK answers: File exists
Starting syslogd/klogd: done

Acrlinux disto 1.0.0 acrlinux_qemu_x86-64 /dev/ttyS0

acrlinux_qemu_x86-64 login: root
root@acrlinux_qemu_x86-64:~#

```
