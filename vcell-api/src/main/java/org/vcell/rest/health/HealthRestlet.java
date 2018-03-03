package org.vcell.rest.health;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.ext.json.JsonRepresentation;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.health.HealthService.HealthEvent;
import org.vcell.rest.health.HealthService.NagiosStatus;

import com.google.gson.Gson;

public final class HealthRestlet extends Restlet {
	
	enum RequestType {
		login,
		sim,
		all
	};
	
	
	public HealthRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.GET)){
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
				HttpRequest request = (HttpRequest)req;
				Form form = request.getResourceRef().getQueryAsForm();
				String requestTypeString = form.getFirstValue(VCellApiApplication.HEALTH_CHECK, true);
				RequestType requestType = null;
				if (requestTypeString!=null) {
					if (requestTypeString.equalsIgnoreCase(VCellApiApplication.HEALTH_CHECK_LOGIN)) {
						requestType = RequestType.login;
					}else if (requestTypeString.equalsIgnoreCase(VCellApiApplication.HEALTH_CHECK_SIM)) {
						requestType = RequestType.sim;
					}else if (requestTypeString.equalsIgnoreCase(VCellApiApplication.HEALTH_CHECK_ALL)) {
						requestType = RequestType.all;
					}
				}
				if (requestType==null){
					throw new RuntimeException("expecting /"+VCellApiApplication.HEALTH+"?"+VCellApiApplication.HEALTH_CHECK+"="+
							"("+VCellApiApplication.HEALTH_CHECK_LOGIN+"|"+VCellApiApplication.HEALTH_CHECK_SIM+"|"+VCellApiApplication.HEALTH_CHECK_ALL+")");
				}
				
				HealthService healthService = application.getHealthService();

				switch (requestType) {
					case all:{
						long beginTimestamp = System.currentTimeMillis() - (30*60*1000);
						HealthEvent[] events = healthService.query(beginTimestamp, System.currentTimeMillis());
						Gson gson = new Gson();
						String healthEventsJSON = gson.toJson(events);
						response.setStatus(Status.SUCCESS_OK);
						response.setEntity(new JsonRepresentation(healthEventsJSON));
						break;
					}
					case login:{
						NagiosStatus nagiosStatus = healthService.getLoginStatus();
						Gson gson = new Gson();
						String statusJSON = gson.toJson(nagiosStatus);
						response.setStatus(Status.SUCCESS_OK);
						response.setEntity(new JsonRepresentation(statusJSON));
						break;
					}
					case sim:{
						NagiosStatus nagiosStatus = healthService.getRunsimStatus();
						Gson gson = new Gson();
						String statusJSON = gson.toJson(nagiosStatus);
						response.setStatus(Status.SUCCESS_OK);
						response.setEntity(new JsonRepresentation(statusJSON));
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("failed to retrieve system health: "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}
}