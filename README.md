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
MACHINE              = "acrlinux-x86-64"
DISTRO               = "acrlinux"
DISTRO_VERSION       = "1.0.0.0"
TUNE_FEATURES        = "m64 core2"
TARGET_FPU           = ""
meta
meta-poky
meta-acrlinux        = "master:153964ca4079907bd6597db12ac569d5003db417"

```

## Run with virual box   
take the iso file from  
acrlinux_build/deploy/images/acrlinux-x86-64/acrlinux-minimal-acrlinux-x86-64-<date-time>.iso
   
console login:     

user name : root     
password: no need of password      

  ![image](https://user-images.githubusercontent.com/29924920/134802663-ea504ffc-fb68-413b-bdbc-6db65e9cf499.png)

