addhandler openstack_bbappend_distrocheck
openstack_bbappend_distrocheck[eventmask] = "bb.event.SanityCheck"
python openstack_bbappend_distrocheck() {
    skip_check = e.data.getVar('SKIP_META_OPENSTACK_SANITY_CHECK') == "1"
    if 'openstack' not in e.data.getVar('DISTRO_FEATURES').split() and not skip_check:
        bb.warn("You have included the meta-openstack layer, but \
'openstack' has not been enabled in your DISTRO_FEATURES. Some bbappend files \
and preferred version setting may not take effect. See the meta-openstack README \
for details on enabling openstack support.")
}
