//
// very basic rmi check that expected RMI service name is present on specified host and port. 
//
// usage: jjs -Dhost=vcell-rmi-beta.cam.uchc.edu -Dport=40106 -Dservice=VCellBootstrapServer checkRmiListening.js
//   returns 0 if rmi registry responds for that service on that port
//   returns -1 if cannot connect or service not advertized
//
host = java.lang.System.getProperty('host');
port = java.lang.System.getProperty('port');
service = java.lang.System.getProperty('service');
bootstrapClass = java.lang.System.getProperty('bootstrapClass');

if (host==null || port==null || service==null || bootstrapClass==null){
	print("usage: jjs -Dhost=vcell-rmi-beta.cam.uchc.edu -Dport=40105 -Dservice=VCellBootstrapServer -DbootstrapClass=cbit.vcell.server.VCellBootstrap checkRmiListening.js");
	print("   returns 0 if rmi registry responds for specified service on host and port and attempts to return bootstrapClass");
	print("   returns -1 if cannot connect or service not advertized");
	print("   returns -2 for other errors");
	java.lang.System.exit(-2);
}

print("checking host = "+host+", port="+port+", service="+service+" for class="+bootstrapClass);

try {
	java.rmi.registry.LocateRegistry.getRegistry(host,port).lookup(service);
}catch(err){
	if (err.message.contains("java.lang.ClassNotFoundException") && err.message.contains(bootstrapClass)){
		print("it worked");
		java.lang.System.exit(0);
	}else{
		print("it didn't work");
		print("exception message: "+err.getClass().getName()+": "+err.message);
		java.lang.System.exit(-1);
	}
}	
