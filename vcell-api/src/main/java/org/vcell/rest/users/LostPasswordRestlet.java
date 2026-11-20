package org.vcell.rest.users;

import java.io.IOException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.vcell.util.BeanUtils;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.resource.PropertyLoader;

public final class LostPasswordRestlet extends Restlet {
	private final static Logger lg = LogManager.getLogger(LostPasswordRestlet.class);

	public LostPasswordRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.POST)){
			// Retired: it reset any named account's password without authentication and emailed the new one in clear text.
			lg.info("refused retired LostPasswordRestlet request");
			response.setStatus(Status.CLIENT_ERROR_GONE);
			response.setEntity("Legacy password reset is no longer available; sign in to VCell and use account recovery to claim your username.", MediaType.TEXT_PLAIN);
		}
	}
}