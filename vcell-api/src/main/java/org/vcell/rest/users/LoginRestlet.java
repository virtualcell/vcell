package org.vcell.rest.users;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.json.JsonRepresentation;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.util.document.User;

public final class LoginRestlet extends Restlet {
	public LoginRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
//	    String html = 
//	    	"<html>" +
//	    		"/login ... should have been redirected."+
//	    	"</html>";
//	    response.setEntity(html, MediaType.TEXT_HTML);
		
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(request.getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		String jsonString = "{}";
		if (vcellUser != null){
			jsonString = "{user: \""+vcellUser.getName()+"\"}";
		}
    	response.setEntity(new JsonRepresentation(jsonString));
	}
}