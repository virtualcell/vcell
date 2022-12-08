FROM python:3.9-slim-buster

RUN apt -y update && apt -y upgrade &&  \
    apt-get install -y curl

RUN mkdir -p /usr/local/app/vcell/installDir && \
    mkdir -p /usr/local/app/vcell/installDir/python/vcell_opt

ENV PATH="/root/.poetry/bin:/root/.local/bin:$PATH"

COPY ./vcell-opt /usr/local/app/vcell/installDir/python/vcell_opt/
COPY ./docker_run.sh /usr/local/app/vcell/installDir/

RUN curl -sSL https://install.python-poetry.org | python3 -  && \
    cd /usr/local/app/vcell/installDir/python/vcell_opt/ && \
    poetry config cache-dir "/poetry/.cache" --local && chmod 755 poetry.toml && poetry install

WORKDIR /usr/local/app/vcell/installDir/python/vcell_opt

ENTRYPOINT ["/usr/local/app/vcell/installDir/docker_run.sh"]
CMD []
