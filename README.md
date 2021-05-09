yocto-acrlinux


#To build   

TEMPLATECONF=meta-acrlinux/conf/ . oe-init-build-env   

bitbake core-image-minimal    
