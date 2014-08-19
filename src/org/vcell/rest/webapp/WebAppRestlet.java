package org.vcell.rest.webapp;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.LocalReference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public final class WebAppRestlet extends Restlet {
	public WebAppRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
		Representation webappRep = new ClientResource(LocalReference.createClapReference("/webapp.html")).get();
		response.setEntity(webappRep);
	}
}