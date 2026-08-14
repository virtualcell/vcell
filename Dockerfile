#  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #
#   MAKE SURE TO RUN: `mvn clean install dependency:copy-dependencies -DskipTests=true` BEFOREHAND!
#  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #
FROM eclipse-temurin:17-jammy AS jre-build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

# Define base image and copy in jlink created minimal Java 17 environment
FROM ubuntu:jammy
RUN apt-get update && apt-get install -y locales && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

# now we have Java 17 and Python 3.9
ARG SIMULATOR_VERSION="7.5.0.11"
ARG MAX_JAVA_MEM=0
# Make sure you don't sap all of docker's memory when you set this.
ENV ENV_SIMULATOR_VERSION=$SIMULATOR_VERSION
ENV MAX_JAVA_MEM_MB=$MAX_JAVA_MEM

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
    base_image="ubuntu:22.04" \
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

ENV DEBIAN_FRONTEND=noninteractive
# Keep the index refresh in the same layer as every install. Split across two RUNs,
# an install resolves against whatever index the earlier layer cached, and once Ubuntu
# supersedes a package the version that index names is gone from the pool. (Note this
# is a hazard for cached builds; the release job that prompted the change builds with
# --no-cache, where a 404 instead means a transient archive.ubuntu.com mirror lag.)
RUN apt-get update \
    && apt-get install -y software-properties-common \
    && apt-get install -y --no-install-recommends curl python3.10 python3-pip build-essential dnsutils \
        apt-utils libfreetype6 fontconfig fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /usr/local/app/vcell/lib && \
    mkdir -p /usr/local/app/vcell/simulation && \
    mkdir -p /usr/local/app/vcell/installDir && \
    mkdir -p /usr/local/app/vcell/installDir/python/vcell_cli_utils && \
    mkdir -p /usr/local/app/vcell/installDir/bionetgen

RUN python3 -m pip install poetry &&  poetry config cache-dir "/poetry/.cache"
ENV PATH="/root/.poetry/bin:/root/.local/bin:$PATH"

# Copy JAR files
COPY ./vcell-cli/target/vcell-cli-0.0.1-SNAPSHOT.jar \
     ./vcell-cli/target/maven-jars/*.jar \
     /usr/local/app/vcell/lib/

# Install required python-packages
COPY ./vcell-cli-utils/ /usr/local/app/vcell/installDir/python/vcell_cli_utils/
# --only main: pytest and mypy are dev dependencies and have no business in a runtime image.
# They were being installed, and at different versions in each virtualenv (mypy 0.991
# alongside 1.13.0), which is a fair sign nothing was reading them.
#
# The artifact cache is pruned in the SAME layer as the install: doing it in a later RUN
# would leave the 256MB sitting in this layer and shrink nothing. virtualenvs/ is a sibling
# directory and is untouched.
RUN cd /usr/local/app/vcell/installDir/python/vcell_cli_utils/ && \
    poetry config cache-dir "/poetry/.cache" --local && \
    chmod 755 poetry.toml && \
    poetry install --only main && \
    rm -rf /poetry/.cache/artifacts /poetry/.cache/cache

# Add linux local solvers only
ADD ./localsolvers /usr/local/app/vcell/installDir/localsolvers
ADD ./nativelibs /usr/local/app/vcell/installDir/nativelibs
COPY ./docker_run.sh /usr/local/app/vcell/installDir/
COPY ./bionetgen/BNG2.pl ./bionetgen/*.txt ./bionetgen/VERSION /usr/local/app/vcell/installDir/bionetgen/
COPY ./bionetgen/Perl2 /usr/local/app/vcell/installDir/bionetgen/Perl2
COPY ./biosimulations_log4j2.xml /usr/local/app/vcell/installDir/

# Declare supported environment variables
ENV ALGORITHM_SUBSTITUTION_POLICY=SIMILAR_VARIABLES
EXPOSE 1433


# Entrypoint
ENTRYPOINT ["/usr/local/app/vcell/installDir/docker_run.sh"]
CMD []
