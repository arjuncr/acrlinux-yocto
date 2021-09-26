IMAGE_FEATURES += "tools-sdk \
                   dev-pkgs \
                  "
# We find in packagegroup-core-sdk.bb that only some of the gcc libraries
# are recommended to be installed as part of the sdk package group, which
# "tools-sdk" from above resolves to. We would like to be able to compile
# applications with libssp (stack smashing protection) and thus add these
# libraries manually to the list.
IMAGE_INSTALL += "bc \
                  python3-pip \
                  swig \
                  libssp \
                  weston-examples \
                  yasm \
                 "
