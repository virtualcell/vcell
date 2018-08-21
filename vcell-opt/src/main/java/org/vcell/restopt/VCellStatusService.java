package org.vcell.restopt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.service.StatusService;

public class VCellStatusService extends StatusService {
	private static Logger lg = LogManager.getLogger(VCellStatusService.class);
	public VCellStatusService(){
		super(true);
	}
	
	@Override
	public Representation getRepresentation(Status status, Request request, Response response){
		if (status.isError()){
			String errorString = "status = (" + status.getDescription() + "), request = (" + request + "), response = (" + response + ")";
			lg.error(errorString);
			return new StringRepresentation(errorString);
		}
		return null;
	}

}
