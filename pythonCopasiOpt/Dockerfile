FROM python:3.10-slim-bullseye

RUN apt -y update && apt -y upgrade &&  \
    apt install -y curl

RUN mkdir -p /usr/local/app/vcell/installDir && \
    mkdir -p /usr/local/app/vcell/installDir/python/vcell_opt

# don't install poetry in /root or it cannot be run from Singularity as an unprivileged user
ENV PATH="/opt/poetry/bin:$PATH"
ENV POETRY_HOME=/opt/poetry

COPY ./vcell-opt /usr/local/app/vcell/installDir/python/vcell_opt/
COPY ./docker_run.sh /usr/local/app/vcell/installDir/

RUN curl -sSL https://install.python-poetry.org | python3 -  && \
    cd /usr/local/app/vcell/installDir/python/vcell_opt/ && \
    poetry config cache-dir "/poetry/.cache" --local && chmod 755 poetry.toml && poetry install

WORKDIR /usr/local/app/vcell/installDir/python/vcell_opt

ENTRYPOINT ["/usr/local/app/vcell/installDir/docker_run.sh"]
CMD []
