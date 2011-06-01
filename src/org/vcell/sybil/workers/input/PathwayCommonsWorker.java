/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.workers.input;

/*   KeywordSearchWorker  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Perform Pathway Commons keyword search in the background
 */

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.state.SystemWorker;

public class PathwayCommonsWorker extends SystemWorker {
	
	public static interface Client {
		public PathwayCommonsRequest request();
		public void setResponse(PathwayCommonsResponse responseNew);
	}
	
	protected Client client;
	protected PathwayCommonsRequest request;
	
	public PathwayCommonsWorker(Client client) { 
		this.client = client; 
		this.request = client.request();
	}
	
	public Object doConstruct() {
		return request.response();
	}
	public PathwayCommonsResponse getResponse() { return (PathwayCommonsResponse) result; }
	public void doFinished() {
		client.setResponse(getResponse());
	}

	public boolean isDeployed() {
		return client != null && request != null;
	}

	@Override
	public String getNonSwingTaskName() {
		return "querying PathwayCommons";
	}

	@Override
	public String getSwingTaskName() {
		// TODO Auto-generated method stub
		return "displaying results";
	}

}
