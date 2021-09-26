SUMMARY = "Modern concurrency tools including agents, futures, promises, thread pools, supervisors, and more. Inspired by Erlang, Clojure, Scala, Go, Java, JavaScript, and classic concurrency patterns."
HOMEPAGE = "http://www.concurrent-ruby.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=fde65ae93d18826f70c6fe125aa04297"

SRC_URI = "git://github.com/ruby-concurrency/concurrent-ruby.git;protocol=https;tag=v1.1.6\
	   file://0001-Removed-check-for-concurrent_ruby.jar.patch"

S = "${WORKDIR}/git"

inherit ruby
