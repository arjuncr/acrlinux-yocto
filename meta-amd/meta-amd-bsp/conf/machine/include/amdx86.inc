PREFERRED_PROVIDER_virtual/kernel ?= "${@bb.utils.contains('RT_KERNEL_AMD', 'yes', "linux-yocto-rt", "linux-yocto", d)}"
PREFERRED_VERSION_linux-yocto ?= "5.4%"
PREFERRED_VERSION_linux-yocto-rt ?= "5.4%"

require conf/machine/include/tune-amdx86.inc
require conf/machine/include/amd-common-configurations.inc
include conf/machine/include/amd-customer-configurations.inc

MACHINE_FEATURES_remove = "alsa"
MACHINE_EXTRA_RRECOMMENDS_remove = " rtc-test alsa-utils"

# Disable the graphical console, no graphics support for now
# this allows the serial console to be used for installation
APPEND_remove="console=tty0"

# Drop features we do not need
DISTRO_FEATURES_remove="opengl x11 alsa pulseaudio"

MACHINEOVERRIDES =. "amd:amdx86:"

# Mask out recipes for packages we do not support for
# amdx86 but may be required by others. Use '|' sign
# between the patterns.
BBMASK = "\
.*/meta-amd/common/recipes-support/libtinyxml\
"
