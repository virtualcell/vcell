echo off

set CLASSPATH=.\vcell-client\target\maven-jars\*;.\vcell-client\target\*
set MAIN_CLASS=cbit.vcell.client.test.VCellClientTest

set props=-Dvcell.installDir=%~dp0
set props=%props% -Dvcell.softwareVersion=standalone_VCell_7.0
set props=%props% -Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.9-jar-with-dependencies.jar
set props=%props% -Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.9-jar-with-dependencies.jar

echo java %props% -cp %CLASSPATH% %MAIN_CLASS% vcellapi.cam.uchc.edu:443

java %props% -cp %CLASSPATH% %MAIN_CLASS% vcellapi.cam.uchc.edu:443