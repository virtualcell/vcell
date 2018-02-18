package org.vcell.rest.users;

import java.io.IOException;
import java.util.Date;

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

public final class LostPasswordRestlet extends Restlet {
	public LostPasswordRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.POST)){
			System.out.println("in LostPasswordRestlet.handle()");
			String userid = request.getEntityAsText();
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
				vcellApiApplication.getRestDatabaseService().sendLostPassword(userid);
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("we failed to send a verification email for account '"+userid+"': "+e.getMessage(), MediaType.TEXT_PLAIN);
				return;
			}

			response.setStatus(Status.SUCCESS_ACCEPTED);
			response.setEntity("we sent you a lost password email, please follow the link in that email", MediaType.TEXT_PLAIN);
		}
	}
}