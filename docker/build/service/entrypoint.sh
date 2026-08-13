#!/bin/bash
#
# Launches one of the five VCell server services from the single vcell-service image.
#
# These five ran from five near-identical Dockerfiles that differed only in a main class, a
# log4j file, a handful of literal -D flags, and 98 more -D flags whose only job was renaming
# container environment variables into system properties. EnvironmentConfigProvider does that
# renaming now, so what is left is small enough to read in one screen -- which is the point:
# a difference between two services should be visible here rather than buried in the diff
# between two 200-line Dockerfiles that nobody reads side by side.
#
#   docker run ghcr.io/virtualcell/vcell-service sched
#
# In Kubernetes the service name is the Deployment's `args`, so `kubectl describe` shows which
# service a pod is, and an unknown name fails immediately and loudly rather than starting
# something unintended.
#
set -euo pipefail

usage() {
	cat >&2 <<-EOF
	usage: $(basename "$0") <api|data|db|sched|submit> [extra JVM args...]

	The service name selects the main class, the log4j configuration and the directory
	layout. Everything else comes from the environment; see EnvironmentConfigProvider.
	EOF
}

service="${1:-}"
if [ $# -gt 0 ]; then
	shift
fi

# Shared by all five. installDir and the log4j manager are properties of the image layout
# rather than of a deployment, which is why they stay here instead of moving to the
# environment with everything else.
common_properties=(
	-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager
	-Dvcell.installDir=/usr/local/app
	-Dvcell.jms.blobMessageUseMongo=true
)

# Heap must leave room for the rest of the JVM. Measured on the api container (2000Mi limit):
# metaspace 83Mi + code cache 65Mi + ~56Mi of thread stacks + ~500Mi of other native
# allocation, so roughly 700Mi is not heap. At MaxRAMPercentage=80 the heap alone may reach
# 1600Mi, and 1600 + 700 does not fit in 2000 -- the container is OOM-killed (exit 137) by the
# kernel while the Java heap is still ~95% free, and because the JVM never throws
# OutOfMemoryError in that situation the two dump flags below can never fire. That is what
# looked like an undiagnosed leak for 183 days.
#
# Revisit if a container memory limit changes: a percentage cannot express "leave 700Mi of
# headroom" on its own.
memory_flags=(
	-XX:MaxRAMPercentage=50
	-XX:G1PeriodicGCInterval=300000
	-XX:NativeMemoryTracking=summary
)

crash_flags=(
	-XX:+ExitOnOutOfMemoryError
	-XX:+HeapDumpOnOutOfMemoryError
	-XX:HeapDumpPath=/dump
)

service_properties=()
app_args=()

case "${service}" in
	api)
		main_class=org.vcell.rest.VCellApiMain
		service_properties=(
			-Dvcell.n5DataDir.internal=/n5DataDir
			-Dvcell.primarySimdatadir.internal=/simdata
			-Dvcell.secondarySimdatadir.internal=/simdata_secondary
		)
		# Unquoted deliberately: an unset protocol drops the argument rather than passing an
		# empty one, matching what the shell-form ENTRYPOINT did.
		# shellcheck disable=SC2206
		app_args=(/usr/local/app/docroot 8080 ${protocol:-})
		# NOTE: api alone did not set -Djava.awt.headless=true. Preserved as-is rather than
		# quietly normalised -- it looks accidental, but this change is a refactor.
		;;
	data)
		main_class=cbit.vcell.message.server.data.SimDataServerMain
		service_properties=(
			-Djava.awt.headless=true
			-Dvcell.python.executable=/usr/local/bin/python
			-Dvcell.primarySimdatadir.internal=/simdata
			-Dvcell.secondarySimdatadir.internal=/simdata_secondary
			-Dvcell.n5DataDir.internal=/n5DataDir
			-Dvcell.export.baseDir.internal=/exportdir/
		)
		# NOTE: data alone still runs at MaxRAMPercentage=80 without periodic GC or native
		# memory tracking -- it was not part of the sizing fix. Preserved; worth revisiting
		# with the same measurement that produced the numbers above.
		memory_flags=(-XX:MaxRAMPercentage=80)
		app_args=("${servertype:-}")
		;;
	db)
		main_class=cbit.vcell.message.server.db.DatabaseServer
		service_properties=(-Djava.awt.headless=true)
		;;
	sched)
		main_class=cbit.vcell.message.server.dispatcher.SimulationDispatcherMain
		service_properties=(
			-Djava.awt.headless=true
			-Dvcell.primarySimdatadir.internal=/simdata
			-Dvcell.htc.logdir.internal=/htclogs
		)
		;;
	submit)
		main_class=cbit.vcell.message.server.batch.sim.HtcSimulationWorker
		service_properties=(
			-Djava.awt.headless=true
			-Dvcell.primarySimdatadir.internal=/simdata
			-Dvcell.secondarySimdatadir.internal=/simdata_secondary
			-Dvcell.htc.logdir.internal=/htclogs
			-Dvcell.simulation.postprocessor=JavaPostprocessor64
			-Dvcell.simulation.preprocessor=JavaPreprocessor64
			-Dvcell.javaSimulation.executable=JavaSimExe64
		)
		;;
	""|-h|--help|help)
		usage
		exit 64
		;;
	*)
		echo "unknown service '${service}'" >&2
		usage
		exit 64
		;;
esac

# VCELL_DEBUG_OPTS is unquoted on purpose: it carries a whole JVM flag when debugging is
# enabled, and must word-split. Debugging is opt-in because a JDWP listener accepts any
# connection with no authentication at all -- whoever reaches the port gets code execution as
# the JVM user. It cannot be attached later either (libjdwp implements only Agent_OnLoad), so
# turning it on costs a restart:
#
#   kubectl -n <ns> set env deployment/<svc> \
#     VCELL_DEBUG_OPTS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000'
#   kubectl -n <ns> set env deployment/<svc> VCELL_DEBUG_OPTS-       # to turn it back off
#
# What needs no restart, and captures the process as it is: jcmd <pid> GC.heap_dump,
# JFR.start settings=profile, Thread.print, VM.native_memory.
# shellcheck disable=SC2086
exec java \
	${VCELL_DEBUG_OPTS:-} \
	"${memory_flags[@]}" \
	"${crash_flags[@]}" \
	"${common_properties[@]}" \
	-Dlog4j.configurationFile="/usr/local/app/vcell-${service}.log4j.xml" \
	"${service_properties[@]}" \
	"$@" \
	-cp "./lib/*" "${main_class}" ${app_args[@]+"${app_args[@]}"}
