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
[  OK  ] Started D-Bus System Message Bus.
[  OK  ] Started Getty on tty1.
[  OK  ] Started Serial Getty on ttyS0.
[  OK  ] Started Serial Getty on ttyS1.
[  OK  ] Reached target Login Prompts.
         Starting User Login Management...
[  OK  ] Listening on Load/Save RF …itch Status /dev/rfkill Watch.
[  OK  ] Started User Login Management.
[  OK  ] Reached target Multi-User System.
         Starting Update UTMP about System Runlevel Changes...
[  OK  ] Finished Update UTMP about System Runlevel Changes.

Acrlinux disto 1.0.0 acrlinuxqemux86-64 ttyS0

acrlinuxqemux86-64 login: root
root@acrlinuxqemux86-64:~#

```
