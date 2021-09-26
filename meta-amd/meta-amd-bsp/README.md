# meta-amd/meta-amd-bsp

This layer contains metadata that is appropriate for all the supported
AMD-based platforms.

Settings in this layer should use appropriate variable suffixes
to ensure they only apply to expected boards.

## Machines

The supported AMD machines/platforms are:

* rome  - AMD EPYC™ 7002 Series (a.k.a. ROME)
* e3000 - AMD EPYC™ Embedded 3000 Series (a.k.a. E3000)

Please see the README file contained in the root meta-amd directory
for general information and usage details.

## Dependencies

Depending on the machine, this layer may depend on:

[bitbake](https://github.com/openembedded/bitbake) layer,
[oe-core](https://github.com/openembedded/openembedded-core) layer,
[meta-oe](https://github.com/openembedded/meta-openembedded) layer,
[meta-python](https://github.com/openembedded/meta-openembedded/meta-python) layer,
[meta-networking](https://github.com/openembedded/meta-openembedded/meta-networking) layer
