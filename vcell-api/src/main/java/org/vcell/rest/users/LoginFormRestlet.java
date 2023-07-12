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
				"<style>\n" +
				".content {\n" +
				"  margin: auto;\n" +
				"  width: 640px; \n" +
				"  padding: 50px;\n" +
				"  font-size: large;\n" +
				"  font-family: 'Lexend Deca', sans-serif; \n" +
				"  color: #2E475D;  \n" +
				"}\n" +
				"</style>\n" +
				"<body><div class=\"content\">\n" +
					"<h3>Sign in to VCell</h3>" +
					"<form name='login' action='"+"/"+VCellApiApplication.LOGIN+ "' method='post'>" +
							"userid<br/> <input type='text' name='"+VCellApiApplication.IDENTIFIER_FORMNAME+"' value=''/><br/> <br/>" +
							"password<br/> <input type='password' name='"+VCellApiApplication.SECRET_FORMNAME+"' value=''/><br/>" +
							"<input type='hidden' name='"+VCellApiApplication.REDIRECTURL_FORMNAME+"' value='"+reference+"'>" +
							"<br/> <br/><input type='submit' value='sign in'/>" +
					"</form>" +
				"</div></body>" +
				"</html>";
	    	response.setEntity(html, MediaType.TEXT_HTML);
	}
}