package org.vcell.rest.common;

public class BioModelRep {
	
	public final String bmKey;
	public final SimRefRep[] sims;
	public final SimContextRefRep[] simContexts;
	
	public BioModelRep(String bmKey, SimRefRep[] sims, SimContextRefRep[] simContexts){
		this.bmKey = bmKey;
		this.sims = sims;
		this.simContexts = simContexts;
	}
}

