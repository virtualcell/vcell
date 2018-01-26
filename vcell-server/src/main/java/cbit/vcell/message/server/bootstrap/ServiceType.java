package cbit.vcell.message.server.bootstrap;

import cbit.vcell.message.VCRpcRequest.RpcServiceType;

public enum ServiceType { 
	DB ("Db"),	
	DATA ("Data"),
	DATAEXPORT ("Exprt"),
	DISPATCH ("Dsptch"),
	PBSCOMPUTE ("PbsC"),	// submit everything to PBS
	LOCALCOMPUTE ("LclC"),   // local pde and ode
	SERVERMANAGER ("ServerManager"),
	API ("Api"), 
	RMI ("Rmi"), 
	MASTER ("Master");
	
	private final String typeName;
	ServiceType(String tn) {
		typeName = tn;
	}
	
	public String getName() {
		return typeName;
	}

	@Override
	public String toString() {
		return typeName;
	}
	
	public static ServiceType fromName(String name) {
		for (ServiceType st : ServiceType.values()) {
			if (st.getName().equals(name)) {
				return st;
			}
		}			
		throw new RuntimeException(name + " is not a legitiamte service type");
	}
}