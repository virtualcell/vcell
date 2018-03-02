package org.vcell.rest.health;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.health.HealthService.HealthEvent;

import com.google.gson.Gson;

public final class HealthRestlet extends Restlet {
	public HealthRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.GET)){
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
//				HttpRequest request = (HttpRequest)req;
//				Form form = request.getResourceRef().getQueryAsForm();
//				String beginTimestampString = form.getFirstValue(VCellApiApplication.EVENTS_BEGINTIMESTAMP, true);
//				if (beginTimestampString==null) {
//					throw new RuntimeException("expecting "+VCellApiApplication.EVENTS_BEGINTIMESTAMP+" query parameter");
//				}
//				long beginTimestamp = Long.parseLong(beginTimestampString);
				
				long beginTimestamp = System.currentTimeMillis() - (30*60*1000);
				
				HealthService healthService = application.getHealthService();
				HealthEvent[] events = healthService.query(beginTimestamp, System.currentTimeMillis());
				Gson gson = new Gson();
				String healthEventsJSON = gson.toJson(events);
				response.setStatus(Status.SUCCESS_OK);
				response.setEntity(new JsonRepresentation(healthEventsJSON));
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("failed to retrieve system health", MediaType.TEXT_PLAIN);
			}
		}
	}
}