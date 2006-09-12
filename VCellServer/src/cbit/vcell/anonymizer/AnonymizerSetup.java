package cbit.vcell.anonymizer;
import java.rmi.*;
import java.rmi.activation.*;
import java.util.Properties;

import cbit.gui.PropertyLoader;
import cbit.vcell.server.VCellBootstrap;

public class AnonymizerSetup {

// This class registers information about the ActivatableImplementation
// class with rmid and the rmiregistry
//
public static void main(String[] args) throws Exception {
	try {			
		String host = null;
		try {
			host = java.net.InetAddress.getLocalHost().getHostName();
		}catch (java.net.UnknownHostException e){
			// do nothing, "localhost" is ok
		}

		PropertyLoader.loadAnonymizerProperties();		
		String specifiedHost = PropertyLoader.getRequiredProperty(PropertyLoader.vcellAnonymizerBootstrapLocalHost);
		String specifiedRmiPort = PropertyLoader.getRequiredProperty(PropertyLoader.vcellAnonymizerBootstrapLocalPort);

		if (!specifiedHost.equals("localhost")) {
			host = specifiedHost;
		}
		int rmiPort = Integer.parseInt(specifiedRmiPort);

	    System.out.println("_____________PLEASE NOTE__________________________________");
	    System.out.println("1. make sure you are running RMIRegistry on " + host + " at port " + rmiPort);
	    System.out.println("2. make sure you are also running RMI activation daemon");
	    System.out.println("__________________________________________________________");
		
		System.setSecurityManager(new RMISecurityManager());

	    // Because of the 1.2 security model, a security policy should 
	    // be specified for the ActivationGroup VM. The first argument
	    // to the Properties put method, inherited from Hashtable, is 
	    // the key and the second is the value 
	    // 
	    Properties props = System.getProperties();

	    ActivationGroupDesc.CommandEnvironment ace = null;
	    ActivationGroupDesc exampleGroup = new ActivationGroupDesc(props, ace);

	    // Once the ActivationGroupDesc has been created, register it 
	    // with the activation system to obtain its ID
	    //
	    ActivationGroupID agi = ActivationGroup.getSystem().registerGroup(exampleGroup);

	    // The "location" String specifies a URL from where the class   
	    // definition will come when this object is requested (activated).
	    // Don't forget the trailing slash at the end of the URL 
	    // or your classes won't be found.
	    //       
	    String location = System.getProperty("java.rmi.server.codebase");

	    // Create the rest of the parameters that will be passed to
	    // the ActivationDesc constructor
	    //
	    MarshalledObject data = null;

	    // The location argument to the ActivationDesc constructor will be used 
	    // to uniquely identify this class; it's location is relative to the  
	    // URL-formatted String, location.
	    //
	    ActivationDesc desc = new ActivationDesc(agi, "cbit.vcell.anonymizer.AnonymizerBootstrap", location, data);

	    // Register with rmid
	    //
	    VCellBootstrap vcellBootstrap = (VCellBootstrap) Activatable.register(desc);
	    System.out.println("Registered AnonymizerBootstrap with RMI activation Daemon");

	    // Bind the stub to a name in the registry running on 1099
	    //			
	    Naming.rebind("//" + host + ":" + rmiPort + "/VCellBootstrapServer", vcellBootstrap);
	    System.out.println("Bound AnonymizerBootstrap with RMIRegistry at " + "//" + host + ":" + rmiPort + "/VCellBootstrapServer");
	} catch (Exception ex) {
		ex.printStackTrace();
	} finally {
	    System.exit(0);
	}
}
}