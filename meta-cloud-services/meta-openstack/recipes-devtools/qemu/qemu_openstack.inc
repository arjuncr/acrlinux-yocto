PACKAGECONFIG[spice] = "--enable-spice,--disable-spice,spice,"
PACKAGECONFIG[libseccomp] = "--enable-seccomp,--disable-seccomp,libseccomp,libseccomp"

PACKAGECONFIG ?= "fdt virtfs libcap-ng"
PACKAGECONFIG_x86 ?= "fdt spice virtfs libcap-ng"
PACKAGECONFIG_x86-64 ?= "fdt spice virtfs libcap-ng"

PACKAGECONFIG_class-native = "fdt"
PACKAGECONFIG_class-nativesdk = "fdt"
