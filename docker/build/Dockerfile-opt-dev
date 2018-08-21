FROM schaff/python-copasi-jre:latest

RUN mkdir -p /usr/local/app/lib
WORKDIR /usr/local/app

COPY ./vcell-server/target/vcell-server-0.0.1-SNAPSHOT.jar \
     ./vcell-server/target/maven-jars/*.jar \
     ./vcell-opt/target/vcell-opt-0.0.1-SNAPSHOT.jar \
     ./vcell-opt/target/maven-jars/*.jar \
     ./lib/

COPY ./pythonScripts ./pythonScripts
COPY ./docker/build/vcell-opt.log4j.xml .

ENV softwareVersion=SOFTWARE-VERSION-NOT-SET \
	serverid=SITE \
    mongodb_host_internal=mongodb \
    mongodb_port_internal=27017 \
    mongodb_database=test

EXPOSE 8080
EXPOSE 8000

ENTRYPOINT java \
    -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n \
    -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -Xms64M \
	-Dvcell.softwareVersion="${softwareVersion}" \
	-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager \
	-Dlog4j.configurationFile=/usr/local/app/vcell-opt.log4j.xml \
	-Dvcell.server.id="${serverid}" \
	-Dvcell.python.executable=/usr/bin/python \
	-Dvcell.installDir=/usr/local/app \
	-Dvcell.mongodb.host.internal=${mongodb_host_internal} \
	-Dvcell.mongodb.port.internal=${mongodb_port_internal} \
	-Dvcell.mongodb.database=${mongodb_database} \
	-cp "./lib/*" org.vcell.restopt.VCellOptMain 8080
