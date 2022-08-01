
FROM ubuntu:20.04

ARG SIMULATOR_VERSION="7.4.0.51"
ENV ENV_SIMULATOR_VERSION=$SIMULATOR_VERSION

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
    base_image="ubuntu:18.04" \
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

RUN apt-get -y update
RUN apt-get install -y --no-install-recommends curl openjdk-8-jre dnsutils
RUN apt-get install -y python3.9 python3-pip python3.9-venv

RUN update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.8 20 && update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.9 40

RUN mkdir -p /usr/local/app/vcell/lib && \
    mkdir -p /usr/local/app/vcell/simulation && \
    mkdir -p /usr/local/app/vcell/installDir && \
    mkdir -p /usr/local/app/vcell/installDir/python/vcell_cli_utils

# Install Poetry dependency
#RUN curl -sSL https://raw.githubusercontent.com/python-poetry/poetry/master/get-poetry.py | python3 - && \
#    echo export PATH="$HOME/.poetry/bin:$PATH" >> /etc/bash.bashrc
RUN python3 -m pip install poetry &&  poetry config cache-dir "/poetry/.cache"

ENV PATH="/root/.poetry/bin:/root/.local/bin:$PATH"

# Copy JAR files
COPY ./vcell-client/target/vcell-client-0.0.1-SNAPSHOT.jar \
     ./vcell-client/target/maven-jars/*.jar \
 	 ./vcell-core/target/vcell-core-0.0.1-SNAPSHOT.jar \
     ./vcell-core/target/maven-jars/*.jar \
 	 ./vcell-server/target/vcell-server-0.0.1-SNAPSHOT.jar \
     ./vcell-server/target/maven-jars/*.jar \
 	 ./vcell-vmicro/target/vcell-vmicro-0.0.1-SNAPSHOT.jar \
     ./vcell-vmicro/target/maven-jars/*.jar \
 	 ./vcell-oracle/target/vcell-oracle-0.0.1-SNAPSHOT.jar \
     ./vcell-oracle/target/maven-jars/*.jar \
     ./vcell-admin/target/vcell-admin-0.0.1-SNAPSHOT.jar \
     ./vcell-admin/target/vcell-admin-0.0.1-SNAPSHOT-tests.jar \
     ./vcell-admin/target/maven-jars/*.jar \
     ./vcell-cli/target/vcell-cli-0.0.1-SNAPSHOT.jar \
     ./vcell-cli/target/maven-jars/*.jar \
     ./non-maven-java-libs/com/oracle/ojdbc6/11.2.0.4/ojdbc6-11.2.0.4.jar \
     ./non-maven-java-libs/com/oracle/ucp/11.2.0.4/ucp-11.2.0.4.jar \
     ./non-maven-java-libs/org/sbml/libcombine/libCombineLinux64/0.2.7/libCombineLinux64-0.2.7.jar \
     /usr/local/app/vcell/lib/

# Install required python-packages
COPY ./vcell-cli-utils/ /usr/local/app/vcell/installDir/python/vcell_cli_utils/
RUN cd /usr/local/app/vcell/installDir/python/vcell_cli_utils/ && \
     poetry config cache-dir "/poetry/.cache" --local && chmod 755 poetry.toml && poetry install

# Add linux local solvers only
ADD ./localsolvers /usr/local/app/vcell/installDir/localsolvers
ADD ./nativelibs /usr/local/app/vcell/installDir/nativelibs
COPY ./docker_run.sh /usr/local/app/vcell/installDir/
COPY ./biosimulations_log4j2.xml /usr/local/app/vcell/installDir/

# Declare supported environment variables
ENV ALGORITHM_SUBSTITUTION_POLICY=SIMILAR_VARIABLES

# Entrypoint
ENTRYPOINT ["/usr/local/app/vcell/installDir/docker_run.sh"]
CMD []
