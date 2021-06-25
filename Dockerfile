FROM maven:3.5-jdk-8-alpine as build
COPY . /app/
WORKDIR /app
RUN mvn clean install --quiet dependency:copy-dependencies


# Base OS
FROM ubuntu:latest

ARG SIMULATOR_VERSION="7.4.0.10"

# metadata
LABEL \
    org.opencontainers.image.title="VCell" \
    org.opencontainers.image.version="${SIMULATOR_VERSION}" \
    org.opencontainers.image.description="Open-source software package for modeling cell biological systems that is built on a central database and disseminated as a standalone application." \
    org.opencontainers.image.url="http://vcell.org/" \
    org.opencontainers.image.documentation="https://vcell.org/support" \
    org.opencontainers.image.source="https://github.com/biosimulators/Biosimulators_VCell" \
    org.opencontainers.image.authors="BioSimulators Team <info@biosimulators.org>" \
    org.opencontainers.image.vendor="BioSimulators Team" \
    org.opencontainers.image.licenses="MIT" \
    \
    base_image="ubuntu:latest" \
    version="${SIMULATOR_VERSION}" \
    software="Virtual Cell" \
    software.version="${SIMULATOR_VERSION}" \
    about.summary="Open-source software package for modeling cell biological systems that is built on a central database and disseminated as a standalone application." \
    about.home="http://vcell.org/" \
    about.documentation="https://vcell.org/support" \
    about.license_file="https://github.com/virtualcell/vcell/blob/master/license.txt" \
    about.license="SPDX:MIT" \
    about.tags="rule-based modeling,kinetic modeling,dynamical simulation,systems biology,BNGL,SED-ML,COMBINE,OMEX" \
    maintainer="BioSimulators Team <info@biosimulators.org>"

RUN apt-get -y update && \
    apt-get install -y --no-install-recommends curl openjdk-8-jre dnsutils && \
    apt-get install -y python3 && \
    apt install -y python3-pip && \
    mkdir -p /usr/local/app/vcell/lib && \
    mkdir -p /usr/local/app/vcell/simulation && \
    mkdir -p /usr/local/app/vcell/installDir && \
    mkdir -p /usr/local/app/vcell/installDir/python/



# Add linux local solvers only
ADD ./localsolvers /usr/local/app/vcell/installDir/localsolvers
ADD ./nativelibs /usr/local/app/vcell/installDir/nativelibs
COPY ./docker_run.sh /usr/local/app/vcell/installDir/

# Declare supported environment variables
ENV ALGORITHM_SUBSTITUTION_POLICY=SIMILAR_VARIABLES

# Install required python-packages
COPY ./vcell-cli-utils/* /usr/local/app/vcell/installDir/python
RUN pip3 install -r /usr/local/app/vcell/installDir/python/requirements.txt
RUN pip3 install /usr/local/app/vcell/installDir/python

# Copy JAR files
COPY --from=build /app/vcell-client/target/vcell-client-0.0.1-SNAPSHOT.jar \
     /app/vcell-client/target/maven-jars/*.jar \
 	 /app/vcell-core/target/vcell-core-0.0.1-SNAPSHOT.jar \
     /app/vcell-core/target/maven-jars/*.jar \
 	 /app/vcell-server/target/vcell-server-0.0.1-SNAPSHOT.jar \
     /app/vcell-server/target/maven-jars/*.jar \
 	 /app/vcell-vmicro/target/vcell-vmicro-0.0.1-SNAPSHOT.jar \
     /app/vcell-vmicro/target/maven-jars/*.jar \
 	 /app/vcell-oracle/target/vcell-oracle-0.0.1-SNAPSHOT.jar \
     /app/vcell-oracle/target/maven-jars/*.jar \
     /app/vcell-admin/target/vcell-admin-0.0.1-SNAPSHOT.jar \
     /app/vcell-admin/target/vcell-admin-0.0.1-SNAPSHOT-tests.jar \
     /app/vcell-admin/target/maven-jars/*.jar \
     /app/vcell-cli/target/vcell-cli-0.0.1-SNAPSHOT.jar \
     /app/vcell-cli/target/maven-jars/*.jar \
     /app/non-maven-java-libs/com/oracle/ojdbc6/11.2.0.4/ojdbc6-11.2.0.4.jar \
     /app/non-maven-java-libs/com/oracle/ucp/11.2.0.4/ucp-11.2.0.4.jar \
     /app/non-maven-java-libs/org/sbml/libcombine/libCombineLinux64/0.2.7/libCombineLinux64-0.2.7.jar \
     /usr/local/app/vcell/lib/

# Entrypoint
ENTRYPOINT ["/usr/local/app/vcell/installDir/docker_run.sh"]
CMD []
