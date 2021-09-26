FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

#We are including procps so there is no need to include the process
#utilities provided by it
SRC_URI += "file://procps-overlap.cfg"

#We are using time from time package that provides more statistics than
#time from busybox
SRC_URI += "file://time.cfg"

#We are including psmisc package that provides fuser so we can disable it
#from busybox
SRC_URI += "file://fuser.cfg"
