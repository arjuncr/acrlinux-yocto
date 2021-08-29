**yocto-acrlinux**  

steps to clone:     
git clone https://github.com/arjuncr/acrlinux-yocto.git      
              OR     
git clone https://github.com/arjuncr/acrlinux-yocto.git -b acrlinux-active

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
