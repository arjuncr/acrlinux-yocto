FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# tinker-board and tinker-board-s. rk3288 covers both
SRC_URI_append_rk3288 = " file://0001-ARM-dts-rockchip-Keep-rk3288-tinker-SD-card-IO-power.patch"

