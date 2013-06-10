package org.vcell.rest;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.service.StatusService;

public class VCellStatusService extends StatusService {
	
	public VCellStatusService(){
		super(true);
	}
	
	@Override
	public Representation getRepresentation(Status status, Request request, Response response){
		if (status.isError()){
			Thread.dumpStack();
			String errorString = "status = (" + status.getDescription() + "), request = (" + request + "), response = (" + response + ")";
			System.err.println(errorString);
			return new StringRepresentation(errorString);
		}
		return null;
	}

}
