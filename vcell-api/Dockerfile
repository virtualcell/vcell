FROM java
MAINTAINER Jim Schaff <schaff@uchc.edu>

ENTRYPOINT ["/usr/bin/java", \
 "-cp", "/usr/share/vcell-api/lib/*", \
 "-Dvcell.server.id=TEST", \
 "-Dvcell.server.dbConnectURL=jdbc:oracle:thin:@VCELL-DB.cam.uchc.edu:1521/vcelldborcl.cam.uchc.edu", \
 "-Dvcell.server.dbDriverName=oracle.jdbc.driver.OracleDriver", \
 "-Dvcell.server.dbUserid=vcell", \
 "-Dvcell.server.dbPassword=cbittech", \
 "-Dvcell.mongodb.host=mongo.cam.uchc.edu", \
 "-Dvcell.mongodb.port=27017", \
 "-Dvcell.mongodb.database=TEST", \
 "-Dvcell.mongodb.loggingCollection=logging", \
 "-Dvcell.mongodb.threadSleepMS=10000", \
 "-Dvcell.python.executable=/root/miniconda/bin/python", \
 "-Dvcell.installDir=/usr/share/vcell-api", \
 "org.vcell.rest.VCellApiMain", \
 "/usr/share/vcell-api/keystore_macbook.jks", \
 "password", \
 "/usr/share/vcell-api/docroot", \
 "-", \
 "8081" \
 ]

ADD docroot /usr/share/vcell-api/docroot
ADD keystore_macbook.jks /usr/share/vcell-api/keystore_macbook.jks
ADD target/maven-jars /usr/share/vcell-api/lib
ADD target/vcell-api-0.0.1-SNAPSHOT.jar /usr/share/vcell-api/lib/vcell-api-0.0.1-SNAPSHOT.jar

RUN mkdir /usr/share/vcell-api/localsolvers && mkdir /usr/share/vcell-api/localsolvers/linux64
