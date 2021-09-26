require ${@bb.utils.contains('DISTRO_FEATURES', 'openstack', '${BPN}_openstack.inc', '', d)}
