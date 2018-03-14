package org.vcell.rest.health;

import org.apache.commons.lang3.ArrayUtils;
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
							"("+VCellApiApplication.HEALTH_CHECK_LOGIN+"|"+VCellApiApplication.HEALTH_CHECK_SIM+"|"+VCellApiApplication.HEALTH_CHECK_ALL+")"
							+ "[&"+VCellApiApplication.HEALTH_CHECK_ALL_START_TIMESTAMP+"=<start_ms>]"
							+ "[&"+VCellApiApplication.HEALTH_CHECK_ALL_END_TIMESTAMP+"=<end_ms>]"
							+ "[&"+VCellApiApplication.HEALTH_CHECK_STATUS_TIMESTAMP+"=<status_ms>]");
				}
				
				// defaults to current status
				long status_timestamp = System.currentTimeMillis();
				String statusTimestampString = form.getFirstValue(VCellApiApplication.HEALTH_CHECK_STATUS_TIMESTAMP, true);
				if (statusTimestampString!=null) {
					status_timestamp = Long.parseLong(statusTimestampString);
				}

				// defaults to returning logs (all option) starting two hours ago
				long start_timestamp = System.currentTimeMillis() - (2*60*60*1000);
				String startTimestampString = form.getFirstValue(VCellApiApplication.HEALTH_CHECK_ALL_START_TIMESTAMP, true);
				if (startTimestampString!=null) {
					start_timestamp = Long.parseLong(startTimestampString);
				}

				// defaults to returning logs (all option) up to present time
				long end_timestamp = System.currentTimeMillis();
				String endTimestampString = form.getFirstValue(VCellApiApplication.HEALTH_CHECK_ALL_END_TIMESTAMP, true);
				if (endTimestampString!=null) {
					end_timestamp = Long.parseLong(endTimestampString);
				}

				HealthService healthService = application.getHealthService();

				switch (requestType) {
					case all:{
						HealthEvent[] events = healthService.query(start_timestamp, end_timestamp);
						ArrayUtils.reverse(events);
						Gson gson = new Gson();
						String healthEventsJSON = gson.toJson(events);
						response.setStatus(Status.SUCCESS_OK);
						response.setEntity(new JsonRepresentation(healthEventsJSON));
						break;
					}
					case login:{
						NagiosStatus nagiosStatus = healthService.getLoginStatus(status_timestamp);
						Gson gson = new Gson();
						String statusJSON = gson.toJson(nagiosStatus);
						response.setStatus(Status.SUCCESS_OK);
						response.setEntity(new JsonRepresentation(statusJSON));
						break;
					}
					case sim:{
						NagiosStatus nagiosStatus = healthService.getRunsimStatus(status_timestamp);
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