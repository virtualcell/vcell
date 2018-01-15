/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.api.client;

import org.vcell.api.client.VCellApiClient.RpcDestination;

/**
 * Insert the type's description here.
 * Creation date: (5/13/2003 1:41:34 PM)
 * @author: Fei Gao
 */
public class VCellApiRpcRequest implements java.io.Serializable {
	
	public enum RpcServiceType {
		DATA("Data"), 
		DATAEXPORT("Exprt"), 
		DB("Db"), 
		DISPATCH("Dsptch"), 
		TESTING_SERVICE("testing");
		
		final String name;
		
		private RpcServiceType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	
	public final String username;
	public final String userkey;
	public final Object[] args;
	public final RpcDestination rpcDestination;
	public final String methodName;	
	public final Long requestTimestampMS;
/**
 * SimpleTask constructor comment.
 * @param argName java.lang.String
 * @param argEstimatedSizeMB double
 * @param argUserid java.lang.String
 */
public VCellApiRpcRequest(String username, String userkey, RpcDestination rpcDestination, String methodName, Object[] arglist) {
	this.username = username;
	this.userkey = userkey;
	this.rpcDestination = rpcDestination;
	this.methodName = methodName;
	this.args = arglist;
	this.requestTimestampMS = System.currentTimeMillis();
}
public String toString() {
	return "[" + username + "," + rpcDestination + "," + methodName + "]";
}

}
