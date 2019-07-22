package org.vcell.rest.events;

import org.apache.http.client.methods.HttpPost;
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
import org.vcell.api.common.events.EventWrapper;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.rpc.RpcService;
import org.vcell.util.document.User;

import com.google.gson.Gson;

public final class EventsRestlet extends Restlet {
	public EventsRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request req, Response response) {
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
			if (req.getMethod().equals(Method.GET)){
				User vcellUser = application.getVCellUser(req.getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
				HttpRequest request = (HttpRequest)req;
				Form form = request.getResourceRef().getQueryAsForm();
				String beginTimestampString = form.getFirstValue(VCellApiApplication.EVENTS_BEGINTIMESTAMP, true);
				if (beginTimestampString==null) {
					throw new RuntimeException("expecting "+VCellApiApplication.EVENTS_BEGINTIMESTAMP+" query parameter");
				}
				long beginTimestamp = Long.parseLong(beginTimestampString);
				
				RestEventService rpcService = application.getEventsService();
				EventWrapper[] eventWrappers = rpcService.query(vcellUser.getName(), beginTimestamp);
				Gson gson = new Gson();
				String eventWrappersJSON = gson.toJson(eventWrappers);
				response.setStatus(Status.SUCCESS_OK, "event query succeeded");
				response.setEntity(new JsonRepresentation(eventWrappersJSON));
			}else if (req.getMethod().equals(Method.POST)) {
				HttpRequest request = (HttpRequest)req;
				Form form = request.getResourceRef().getQueryAsForm();
				String action = form.getFirstValue("action", true);
				if(action == null) {
					throw new RuntimeException("expecting 'action' query parameter");
				}
				if(action.equals("clearEvent")) {
					RestEventService rpcService = application.getEventsService();
					rpcService.clearEvent(form.getFirstValue("eventid", true));
				}else {
					throw new Exception("Unknown action command "+action);
				}
			}
			} catch (Exception e) {
				getLogger().severe("internal error retrieving events: "+e.getMessage());
				e.printStackTrace();
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("internal error retrieving events: "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}
