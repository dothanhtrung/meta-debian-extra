SUMMARY = "cluster resource manager"
DESCRIPTION = "\
 At its core, Pacemaker is a distributed finite state machine capable of \
 co-ordinating the startup and recovery of inter-related services across \
 a set of machines. \
 Pacemaker understands many different resource types (OCF, SYSV, systemd) \
 and can accurately model the relationships between them (colocation, \
 ordering). \
 It can even use technology such as Docker to automatically isolate the \
 resources managed by the cluster."
HOMEPAGE = "http://www.clusterlabs.org/"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "jessie-backports-master"

PR = "r0"
PV = "1.1.15"
inherit debian-package

LICENSE = "GPL-2.0+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=6adca3b36477cc77e04376f9a40df32c \
	file://COPYING.LIB;md5=243b725d71bb5df4a1e5920b344b86ad"

# Do not execute target program while cross compile
# and do not build help
SRC_URI += "\
	file://pacemaker-do-not-execute-target-program-while-cross-.patch \
	file://pacemaker-do-not-build-help.patch"

EXTRA_OECONF += "--disable-fatal-warnings --libexecdir=${libdir}"

inherit autotools-brokensep pkgconfig useradd

DEPENDS += "util-linux glib-2.0 libxslt corosync libqb libesmtp systemd dbus"

# Base on debian/pacemaker-common.postinst
USERADD_PACKAGES = "${PN}-common"
GROUPADD_PARAM_${PN}-common = "-r haclient"
USERADD_PARAM_${PN}-common = "-r -g haclient --home /var/lib/pacemaker \
                              --no-create-home hacluster \
                              "
pkg_postinst_${PN}-common() {
	AUTHKEY=$D/etc/pacemaker/authkey
	if ! [ -e "$AUTHKEY" ]; then
		( umask 037 && dd if=/dev/urandom of="$AUTHKEY" bs=4096 count=1 )
	chgrp haclient "$AUTHKEY"
	fi
}

do_install_append() {
	# Base on debian/rules
	rm -rf ${D}${datadir}/${BPN}/tests \
	       ${D}${libdir}/python* \
	       ${D}${docdir}/${BPN}/COPYING \
	       ${D}${docdir}/${BPN}/COPYING.LIB \
	       ${D}${libdir}/service_crm.so \
	       ${D}${libdir}/lcrso \
	       ${D}${systemd_system_unitdir}/crm_mon.service \
	       ${D}${libdir}/${BPN}/lrmd_test \
	       ${D}${libdir}/*.a
	install -D -m 644 ${S}/mcp/pacemaker.sysconfig \
	      ${D}${sysconfdir}/default/pacemaker
}

PACKAGES =+ "\
	libcib-dev libcib libcrmcluster-dev libcrmcluster libcrmcommon-dev \
	libcrmcommon libcrmservice-dev libcrmservice liblrmd-dev liblrmd \
	libpe-rules libpe-status libpengine-dev libpengine libstonithd-dev \
	libstonithd libtransitioner ${PN}-cli-utils ${PN}-common ${PN}-remote \
	${PN}-resource-agents"

RDEPENDS_${PN}-common += "adduser corosync"
RDEPENDS_${PN} += "${PN}-common ${PN}-resource-agents"
RDEPENDS_${PN}-cli-utils += "init-system-helpers"
RDEPENDS_${PN}-remote += "${PN}-common ${PN}-resource-agents"
RDEPENDS_${PN}-resource-agents += "resource-agents"
FILES_libcib-dev = "\
	${includedir}/${BPN}/crm/cib* \
	${libdir}/libcib.so \
	${libdir}/pkgconfig/pacemaker-cib.pc \
	"
FILES_libcib = "\
	${libdir}/libcib${SOLIBS} \
	"
FILES_libcrmcluster-dev = "\
	${includedir}/${BPN}/crm/cluster.h \
	${libdir}/libcrmcluster.so \
	${libdir}/pkgconfig/pacemaker-cluster.pc \
	"
FILES_libcrmcluster = "\
	${libdir}/libcrmcluster${SOLIBS} \
	"
FILES_libcrmcommon-dev = "\
	${includedir}/${BPN}/crm/attrd.h \
	${includedir}/${BPN}/crm/common \
	${includedir}/${BPN}/crm/compatibility.h \
	${includedir}/${BPN}/crm/crm.h \
	${includedir}/${BPN}/crm/error.h \
	${includedir}/${BPN}/crm/msg_xml.h \
	${includedir}/${BPN}/crm/transition.h \
	${includedir}/${BPN}/crm_config.h \
	${libdir}/libcrmcommon.so \
	${libdir}/libtransitioner.so \
	${libdir}/pkgconfig/pacemaker.pc \
	"
FILES_libcrmcommon = "\
	${libdir}/libcrmcommon${SOLIBS} \
	"
FILES_libcrmservice-dev = "\
	${includedir}/${BPN}/crm/services.h \
	${libdir}/libcrmservice.so \
	${libdir}/pkgconfig/pacemaker-service.pc \
	"
FILES_libcrmservice = "\
	${libdir}/libcrmservice${SOLIBS} \
	"
FILES_liblrmd-dev = "\
	${includedir}/${BPN}/crm/lrmd.h \
	${libdir}/liblrmd.so \
	${libdir}/pkgconfig/pacemaker-lrmd.pc \
	"
FILES_liblrmd = "\
	${libdir}/liblrmd${SOLIBS} \
	"
FILES_libpe-rules = "\
	${libdir}/libpe_rules${SOLIBS} \
	"
FILES_libpe-status = "\
	${libdir}/libpe_status${SOLIBS} \
	"
FILES_libpengine-dev = "\
	${includedir}/${BPN}/crm/pengine \
	${libdir}/libpe*.so \
	${libdir}/pkgconfig/pacemaker-pengine.pc \
	"
FILES_libpengine = "\
	${libdir}/libpengine${SOLIBS} \
	"
FILES_libstonithd-dev = "\
	${includedir}/${BPN}/crm/stonith-ng.h \
	${libdir}/libstonithd.so \
	${libdir}/pkgconfig/pacemaker-fencing.pc \
	"
FILES_libstonithd = "\
	${libdir}/libstonithd${SOLIBS} \
	"
FILES_libtransitioner = "\
	${libdir}/libtransitioner${SOLIBS} \
	"
FILES_${PN}-cli-utils = "\
	${sbindir}/attrd_updater \
	${sbindir}/cibadmin \
	${sbindir}/crm_diff \
	${sbindir}/crm_error \
	${sbindir}/crm_failcount \
	${sbindir}/crm_master \
	${sbindir}/crm_mon \
	${sbindir}/crm_report \
	${sbindir}/crm_resource \
	${sbindir}/crm_shadow \
	${sbindir}/crm_simulate \
	${sbindir}/crm_standby \
	${sbindir}/crm_ticket \
	${sbindir}/crm_verify \
	${sbindir}/crmadmin \
	${sbindir}/iso8601 \
	${sbindir}/stonith_admin \
	"
FILES_${PN}-common = "\
	${sysconfdir}/default/pacemaker \
	${sysconfdir}/logrotate.d/pacemaker \
	${datadir}/${BPN}/* \
	${datadir}/snmp \
	"
FILES_${PN}-remote = "\
	${sysconfdir}/init.d/pacemaker_remote \
	${systemd_system_unitdir}/pacemaker_remote.service \
	${sbindir}/pacemaker_remoted \
	"
FILES_${PN}-resource-agents = "\
	${libdir}/ocf/resource.d/.isolation \
	${libdir}/ocf/resource.d/${BPN}/ClusterMon \
	${libdir}/ocf/resource.d/${BPN}/Dummy \
	${libdir}/ocf/resource.d/${BPN}/HealthCPU \
	${libdir}/ocf/resource.d/${BPN}/HealthSMART \
	${libdir}/ocf/resource.d/${BPN}/Stateful \
	${libdir}/ocf/resource.d/${BPN}/SysInfo \
	${libdir}/ocf/resource.d/${BPN}/SystemHealth \
	${libdir}/ocf/resource.d/${BPN}/ping* \
	"
FILES_${PN} += "\
	${libdir}/ocf/resource.d/${BPN}/o2cb \
	${libdir}/ocf/resource.d/${BPN}/controld \
	${libdir}/ocf/resource.d/${BPN}/remote \
	${systemd_system_unitdir}/pacemaker.service \
	"
FILES_${PN}-dbg += "\
	${libdir}/lcrso/.debug \
	"
