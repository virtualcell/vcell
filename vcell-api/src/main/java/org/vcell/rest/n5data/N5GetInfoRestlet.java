package org.vcell.rest.n5data;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.vcell.rest.VCellApiApplication;
import org.vcell.util.document.User;

import java.util.ArrayList;

/*
Handling receiving data

Logging, tracking
 */

public final class N5GetInfoRestlet extends Restlet {
	private final static Logger lg = LogManager.getLogger(N5GetInfoRestlet.class);


	public N5GetInfoRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.GET)){
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
				HttpRequest request = (HttpRequest)req;
				Form form = request.getResourceRef().getQueryAsForm();


				String desiredInfo = form.getFirstValue(VCellApiApplication.N5_INFO_TYPE, true);
				String simID = request.getAttributes().get(VCellApiApplication.N5_SIMID).toString();

				switch (desiredInfo){
					case VCellApiApplication.N5_INFO_SUPPORTED_SPECIES:
//						User user = application.getVCellUser(req.getChallengeResponse(), VCellApiApplication.AuthenticationPolicy.ignoreInvalidCredentials);
//						N5Service n5Service = new N5Service(simID, user);
//						ArrayList<String> listOfSupportedSpecies = n5Service.supportedSpecies();
//						Gson gson = new Gson();
//						response.setEntity(gson.toJson(listOfSupportedSpecies), MediaType.APPLICATION_JSON);
						return;
					default:
						return;
//						lg.info("No information type was specified in getting N5 info");
//						response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//						response.setEntity("failed to specify type of N5 information request for sim ID: " + simID, MediaType.TEXT_PLAIN);
				}
			} catch (Exception e) {
				lg.error(e.getMessage(), e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("failed to get N5 info: "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}
}