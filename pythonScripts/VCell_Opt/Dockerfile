FROM continuumio/anaconda
MAINTAINER Jim Schaff <schaff@uchc.edu>
RUN conda install -y -c fbergmann python-copasi && \
 conda install -y -c sbmlteam python-libsbml && \
 conda install -y -c conda-forge thrift && \
 conda install -y amqp

ENTRYPOINT ["/opt/conda/bin/python", "/usr/share/vcell-opt/optServiceMessaging.py", "4444"]

ADD vcellopt /usr/share/vcell-opt/vcellopt
ADD optService.py /usr/share/vcell-opt/optService.py
