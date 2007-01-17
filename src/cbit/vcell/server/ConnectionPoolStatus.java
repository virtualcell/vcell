package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class ConnectionPoolStatus implements Serializable {
	private ComputeHost[] potentialHosts = null;
	private ComputeHost[] activeHosts = null;
	private ComputeHost nextHost = null;
/**
 * ConnectionPoolStatus constructor comment.
 */
public ConnectionPoolStatus(ComputeHost[] argPotentialComputeHosts, ComputeHost[] argActiveComputeHosts, ComputeHost argNextComputeHost) {
	super();
	//
	// copy by value the potentialHosts string array
	//
	if (argPotentialComputeHosts!=null && argPotentialComputeHosts.length>0){
		this.potentialHosts = new ComputeHost[argPotentialComputeHosts.length];
		System.arraycopy(argPotentialComputeHosts,0,this.potentialHosts,0,argPotentialComputeHosts.length);
	}
	//
	// copy by value the activeHosts string array
	//
	if (argActiveComputeHosts!=null && argActiveComputeHosts.length>0){
		this.activeHosts = new ComputeHost[argActiveComputeHosts.length];
		System.arraycopy(argActiveComputeHosts,0,this.activeHosts,0,argActiveComputeHosts.length);
	}
	
	this.nextHost = argNextComputeHost;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public ComputeHost[] getActiveHosts() {
	return activeHosts;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public ComputeHost getNextHost() {
	return nextHost;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public ComputeHost[] getPotentialHosts() {
	return potentialHosts;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	
	buffer.append("potential hosts = ");
	if (potentialHosts!=null){
		for (int i=0;i<potentialHosts.length;i++){
			buffer.append(" "+potentialHosts[i]);
		}
	}
	buffer.append("\n");

	buffer.append("active hosts = ");
	if (activeHosts!=null){
		for (int i=0;i<activeHosts.length;i++){
			buffer.append(" "+activeHosts[i]);
		}
	}
	buffer.append("\n");

	buffer.append("next host = ");
	if (nextHost!=null){
		buffer.append(" "+nextHost);
	}
	buffer.append("\n");

	return buffer.toString();
}
}
