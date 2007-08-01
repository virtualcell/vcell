
Here is how VCell client should be executed:

main class to run:
cbit.vcell.client.test.VCellClientTest

program arguments:
-local userVCellLogin userPassword

virtual machine options - add correct path to 'vcell.properties' file; 
use vcell.properties.sample file as a template if needed

-Xms200M -Xmx500M 
-Djava.rmi.server.ignoreStubClasses=true
-Dvcell.propertyfile="C:\pass\to\vcell.properties"