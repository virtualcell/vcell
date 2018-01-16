package org.vcell.rest.users;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.representation.StringRepresentation;

import cbit.vcell.resource.PropertyLoader;

public final class SWVersionRestlet extends Restlet {
	public SWVersionRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
		response.setEntity(new StringRepresentation(PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion)));
	}
}