package org.vcell.rest.users;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.vcell.rest.VCellApiApplication;

public final class LoginFormRestlet extends Restlet {
	public LoginFormRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
		org.restlet.data.Reference reference = request.getReferrerRef();
		if (reference == null){
			reference = new Reference(request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.BIOMODEL);
		}
	   	String html = "<html>" +
					"<form name='login' action='"+"/"+VCellApiApplication.LOGIN+ "' method='post'>" +
							"VCell userid <input type='text' name='"+VCellApiApplication.IDENTIFIER_FORMNAME+"' value=''/><br/>" +
							"VCell password <input type='password' name='"+VCellApiApplication.SECRET_FORMNAME+"' value=''/><br/>" +
							"<input type='hidden' name='"+VCellApiApplication.REDIRECTURL_FORMNAME+"' value='"+reference+"'>" +
							"<input type='submit' value='sign in'/>" +
					"</form>" +
				  "</html>";
	    	response.setEntity(html, MediaType.TEXT_HTML);
	}
}