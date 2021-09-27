#!/usr/bin/env bash
# shellcheck disable=SC2154

log "rpi userland" green

git clone https://github.com/raspberrypi/userland.git "${base_dir}"/userland

cd "${base_dir}"/userland && mkdir -p build/
pushd "${base_dir}"/userland/build || exit

case ${architecture} in
  arm64)
    cmake -DCMAKE_SYSTEM_NAME=Linux -DCMAKE_BUILD_TYPE=release -DCMAKE_TOOLCHAIN_FILE="makefiles/cmake/toolchains/aarch64-linux-gnu.cmake" \
      -DALL_APPS=OFF -DCMAKE_SYSTEM_PROCESSOR="arm64" -DARM64=ON ../
    ;;
  armel)
    cmake -DCMAKE_SYSTEM_NAME=Linux -DCMAKE_BUILD_TYPE=release -DCMAKE_TOOLCHAIN_FILE="makefiles/cmake/toolchains/arm-linux-gnueabihf.cmake" \
      -DALL_APPS=OFF -DCMAKE_SYSTEM_PROCESSOR="arm" -DARM64=OFF ../
    ;;
  armhf)
    cmake -DCMAKE_SYSTEM_NAME=Linux -DCMAKE_BUILD_TYPE=release -DCMAKE_TOOLCHAIN_FILE="makefiles/cmake/toolchains/arm-linux-gnueabihf.cmake" \
      -DALL_APPS=OFF -DCMAKE_SYSTEM_PROCESSOR="arm" -DARM64=OFF ../
    ;;
esac

make -j"$(nproc)" 2>/dev/null
mkdir -p "${work_dir}"/opt/vc
mv {bin,lib,inc} "${work_dir}"/opt/vc

cd "${current_dir}" || exit

install -m644 "${current_dir}"/bsp/configs/raspi-userland.conf "${work_dir}"/etc/ld.so.conf.d/
install -m755 "${current_dir}"/bsp/configs/vc.sh "${work_dir}"/etc/profile.d/
install -m644 "${current_dir}"/bsp/udev/99-vchiq-permissions.rules "${work_dir}"/etc/udev/rules.d/
