**acrlinux**    


**Setting up your acrlinux project**  

**Prerequisite**     

Ubuntu 20.04  
sudo apt-get install -y git build-essential libsdl1.2-dev texinfo gawk chrpath diffstat

**Download the source**  

git clone https://github.com/arjuncr/acrlinux-yocto.git      

cd acrlinux-yocto   

**Build**   

**To build x86_64**   

```
. acrlinux-env

bitbake acrlinux-minimal    

Parsing recipes: 100% |#################################################################################| Time: 0:00:49
Parsing of 2468 .bb files complete (0 cached, 2468 parsed). 3643 targets, 116 skipped, 0 masked, 0 errors.
NOTE: Resolving any missing task queue dependencies

Build Configuration:
BB_VERSION           = "1.46.0"
BUILD_SYS            = "x86_64-linux"
NATIVELSBSTRING      = "ubuntu-20.04"
TARGET_SYS           = "x86_64-acrlinux-linux"
MACHINE              = "acrlinux-x86-64"
DISTRO               = "acrlinux"
DISTRO_VERSION       = "1.0.0"
TUNE_FEATURES        = "m64 core2"
TARGET_FPU           = ""
meta
meta-poky
meta-yocto-bsp
meta-acrlinux
meta-oe
meta-multimedia
meta-networking
meta-python
meta-perl
meta-gnome
meta-xfce
meta-webserver
meta-filesystems
meta-initramfs       = "master:43293b255fb5c69f4168605fa3e4b98862d71e21"

Initialising tasks: 100% |##############################################################################| Time: 0:00:01
Sstate summary: Wanted 1304 Found 0 Missed 1304 Current 0 (0% match, 0% complete)
```

## Run with virtual box   
find the iso file here:   
build/acrlinux_build/deploy/images/acrlinux-x86-64/acrlinux-distro.iso  
   
console login:     

user name : root     
password: no need of password      

  ![image](https://user-images.githubusercontent.com/29924920/134802663-ea504ffc-fb68-413b-bdbc-6db65e9cf499.png)

