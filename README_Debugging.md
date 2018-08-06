
Service configuration info including **java class** [detailed instructions](docker/README_serviceInfo.md)

## Real location of required properties

[script that creates server environment variables file](docker/swarm/serverconfig-uch.sh) 
 
client local run  alpha  
-Dvcell.primarySimdatadir.internal=\\cfs05\vcell\users  
-Dvcell.secondarySimdatadir.internal=\\cfs05\vcell\users  
-Dvcell.server.dbPassword=  
-Dvcell.server.dbUserid=vcell  
-Dvcell.server.dbDriverName=oracle.jdbc.driver.OracleDriver  
-Dvcell.server.dbConnectURL=jdbc:oracle:thin:@vcell-db.cam.uchc.edu:1521:vcelldborcl  
-Dvcell.server.id=alpha\_7.0.0\_51  
-Dvcell.mongodb.database=TEST  
-Dvcell.mongodb.host.internal=vcellapi-beta.cam.uchc.edu  
-Dvcell.mongodb.port.internal=27019  
-Dvcell.installDir=C:\Users\frm\VCellTrunkGitWorkspace2\vcell  
-Dvcell.softwareVersion="frm\_VCell\_7.0"  
-Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.4-jar-with-dependencies.jar  
-Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.4-jar-with-dependencies.jar  

