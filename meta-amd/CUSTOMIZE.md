# 4. Customizing images with AMD Features

AMD supports various features and software components that can be
enabled by setting the corresponding configuration variable to a
valid value in the `local.conf`.

Following is a list of components that can be enabled if you want
them to be installed/available on your image, or can be configured:

* **ON-TARGET DEVELOPMENT - SDK for on-target development**

> gcc, make, autotools, autoconf, build-essential etc.

* **ON-TARGET DEBUGGING - tools for on-target debugging**

> gdb, gdbserver, strace, mtrace

* **ON-TARGET PROFILING - tools for on-target profiling**

> lttng, babeltrace, systemtap, powertop, valgrind

* **RT KERNEL - Realtime Kernel support**

> Linux kernel with PREEMPT_RT patch

---
##### Note

Please set the required configuration variables as shown below in the
`local.conf` **before building an image or generating an SDK** (that
can be used to develop apps for these components (if applicable)).

Otherwise they will not be configured, and will not be available on the
target.

---

#### Supported software features

| Software feature      | Configuration variable      | Configuration values | Default value | Supported machines |
|:----------------------|:----------------------------|:---------------------|:--------------|:-------------------|
| ON-TARGET DEVELOPMENT | EXTRA_IMAGE_FEATURES_append | tools-sdk            |               | e3000, rome        |
| ON-TARGET DEBUGGING   | EXTRA_IMAGE_FEATURES_append | tools-debug          |               | e3000, rome        |
| ON-TARGET PROFILING   | EXTRA_IMAGE_FEATURES_append | tools-profile        |               | e3000, rome        |
| RT KERNEL             | RT_KERNEL_AMD               | yes, no              | no            | e3000, rome        |

#### Example configuration in local.conf
```sh
EXTRA_IMAGE_FEATURES_append = " tools-sdk"
EXTRA_IMAGE_FEATURES_append = " tools-debug"
EXTRA_IMAGE_FEATURES_append = " tools-profile"

# Please run 'bitbake -c clean virtual/kernel' everytime before
# configuring the RT_KERNEL_AMD variable
RT_KERNEL_AMD = "yes"
```

---
#### What's next

Continue to "Section 2 - Setting up and starting a build"
([BUILD.md](BUILD.md#23-start-the-build)) and restart the image build
as `bitbake <image-name>`, and deploy the new image to see the
changes take effect.
