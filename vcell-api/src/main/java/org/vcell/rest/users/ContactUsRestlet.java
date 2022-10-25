package org.vcell.rest.users;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.vcell.rest.VCellApiApplication;
import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.resource.PropertyLoader;

public final class ContactUsRestlet extends Restlet {
	private String message = null;
	public ContactUsRestlet(Context context) {
		super(context);
		ConcurrentMap<String, Object> attributes = context.getAttributes();
		message = (String)attributes.get("message");
	}

	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.POST)){
			System.out.println("in ContactUsRestlet.handle()");
			String userid = request.getEntityAsText();
			if (message == null || message.isEmpty()) {
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				response.setEntity("message missing or empty", MediaType.TEXT_PLAIN);
				return;		// nothing useful to do
			}
			if (userid==null || userid.length()==0) {
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				response.setEntity("expecting username in body", MediaType.TEXT_PLAIN);
				return;
			}
			// first line of defense against sql insertion attacks.
			if (!TokenMangler.fixTokenStrict(userid).equals(userid)) {
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				response.setEntity("unexpected characters in userid '"+userid+"'", MediaType.TEXT_PLAIN);
				return;
			}
			
			try {
				VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
				vcellApiApplication.getRestDatabaseService().contactUs(userid, message);
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("We failed to report an error to VCell support", MediaType.TEXT_PLAIN);
				return;
			}

			response.setStatus(Status.SUCCESS_ACCEPTED);
			response.setEntity("We successfully reported an error to vCell support", MediaType.TEXT_PLAIN);
		}
	}
}