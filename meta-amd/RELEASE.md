# Release notes

This is the release notes document for the AMD machine ROME. This document
contains information about the Yocto layers' git repos, their branches
and commit hashes, software versions, and known/fixed issues/limitations.

## Bitbake layers
| Layer             | Git Repo                                     | Branch  | Commit Hash/Tag                          |
|:------------------|:---------------------------------------------|:--------|:-----------------------------------------|
| poky              | git://git.yoctoproject.org/poky              | dunfell | tags/yocto-3.1.4                         |
| meta-openembedded | git://git.openembedded.org/meta-openembedded | dunfell | f2d02cb71eaff8eb285a1997b30be52486c160ae |
| meta-dpdk         | git://git.yoctoproject.org/meta-dpdk         | dunfell | 9465b6d27fc9520e18d05cc50dbed9d84e111953 |
| meta-amd          | git://git.yoctoproject.org/meta-amd          | dunfell | tags/dunfell-rome-ga-202103                                     |

## Software versions
| Software        | Version  |
|:----------------|:---------|
| Yocto Poky base | 3.1.4    |
| grub            | 2.02     |
| linux-yocto     | 5.4.69   |
| linux-yocto-rt  | 5.4.69   |
| gcc             | 9.3.0    |
| util-linux      | 2.35.1   |
| lttng           | 2.11     |
| babeltrace      | 1.5.8    |
| connman         | 1.37     |
| gdb             | 9.1      |
| dpdk            | 18.11.10 |
| strongswan      | 5.8.4    |

## Fixed issues
| __ROME Fixed Issues__ |
|:----------------------|
| None                  |

## Known issues
| __ROME Known Issues/Limitations__                                                           |
|:--------------------------------------------------------------------------------------------|
| Network is detected from only one NIC card when two NIC cards are connected on the platform |
