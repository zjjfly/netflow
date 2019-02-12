#!/usr/bin/env bash

root=$(
	cd $(dirname $(readlink $0 || echo $0))/..
	pwd
)

sbtver=1.1.0
sbtjar=sbt-launch.jar
sbtsum=4a75a972d1a9254ff59033c845e20b27

function download {
	echo "downloading ${sbtjar}" 1>&2
	https://piccolo.link/sbt-1.1.0.zip
	wget -O ${sbtjar} "https://piccolo.link/sbt-${sbtver}.zip"
	unzip sbt-${sbtver}.zip .
	mkdir -p target/ && mv ./sbt/bin/${sbtjar} target/${sbtjar}
}

function sbtjar_md5 {
	openssl md5 < target/${sbtjar} | cut -f2 -d'=' | awk '{print $1}'
}

if [ ! -f "target/${sbtjar}" ]; then
	download
fi

test -f "target/${sbtjar}" || exit 1

jarmd5=$(sbtjar_md5)
if [ "${jarmd5}" != "${sbtsum}" ]; then
	echo "Bad MD5 checksum on ${sbtjar}!" 1>&2
	echo "Moving current sbt-launch.jar to sbt-launch.jar.old!" 1>&2
	mv "target/${sbtjar}" "target/${sbtjar}.old"
	download

	jarmd5=$(sbtjar_md5)
	if [ "${jarmd5}" != "${sbtsum}" ]; then
		echo "Bad MD5 checksum *AGAIN*!" 1>&2
		exit 1
	fi
fi

if [ $# -eq 0 ]; then
	echo "no sbt command given"
	exit 1
fi

case $1 in
	import)
		echo "Import-mode"
		SBT_OPTS="-Xms128M -Xmx4G"
		SBT_CMD="~console"
		;;
	prod*)
		echo "Prod-Mode"
		SBT_OPTS="-Xms128M -Xmx1G -Drun.mode=production"
		SBT_CMD="~run"
		;;
	*)
		echo "Dev-Mode"
		SBT_OPTS="-Xms128M -Xmx1G"
		SBT_CMD=$@
		;;
esac


java -ea -server ${JAVA_OPTS}						\
	-XX:+AggressiveOpts						\
	-XX:+OptimizeStringConcat					\
	-XX:+UseConcMarkSweepGC						\
	-XX:+CMSParallelRemarkEnabled					\
	-XX:+CMSClassUnloadingEnabled					\
	-Dio.netty.leakDetectionLevel=advanced				\
	-Dlogback.configurationFile=src/main/resources/logback.xml	\
	-Dconfig.file=src/main/resources/application.conf		\
	-jar target/${sbtjar} ${SBT_CMD}

