FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_amd = " \
	    file://gpt_disklabel.cfg \
           "
