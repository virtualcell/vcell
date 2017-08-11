package org.vcell.rest.users;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.vcell.rest.VCellApiApplication;

public final class RegistrationFormRestlet extends Restlet {
	public RegistrationFormRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
		Representation entity = request.getEntity();
		Form form = request.getResourceRef().getQueryAsForm();
		
		String userid = form.getFirstValue(VCellApiApplication.NEWUSERID_FORMNAME,"");
		String password1 = form.getFirstValue(VCellApiApplication.NEWPASSWORD1_FORMNAME,"");
		String password2 = form.getFirstValue(VCellApiApplication.NEWPASSWORD2_FORMNAME,"");
		String firstname = form.getFirstValue(VCellApiApplication.NEWFIRSTNAME_FORMNAME,"");
		String lastname  = form.getFirstValue(VCellApiApplication.NEWLASTNAME_FORMNAME,"");
		String email     = form.getFirstValue(VCellApiApplication.NEWEMAIL_FORMNAME,"");
		String institute  = form.getFirstValue(VCellApiApplication.NEWINSTITUTE_FORMNAME,"");
		String country   = form.getFirstValue(VCellApiApplication.NEWCOUNTRY_FORMNAME,"");
		String notifyString = form.getFirstValue(VCellApiApplication.NEWNOTIFY_FORMNAME,"");
		
		String errorMessage = form.getFirstValue(VCellApiApplication.NEWERRORMESSAGE_FORMNAME,"");
		
		String notifyChecked = "unchecked";
		if (notifyString.length()==0 || notifyString.equals("on")){
			notifyChecked = "checked";
		}
		
	   	String html = "<html>\n" +
						"<form name='login' action='"+"/"+VCellApiApplication.NEWUSER+ "' method='post'>\n" +
							// error message
							((errorMessage.length()>0)?("<h2><font color='f00'>"+errorMessage+"</font></h2>"):("")) +
							
							// fields
							"userid <input type='text' name='"+VCellApiApplication.NEWUSERID_FORMNAME+"' value='"+userid+"'/>*<br/>\n" +
							"password <input type='password' name='"+VCellApiApplication.NEWPASSWORD1_FORMNAME+"' value='"+password1+"'/>*&nbsp;&nbsp;&nbsp;\n" +
							"retype password <input type='password' name='"+VCellApiApplication.NEWPASSWORD2_FORMNAME+"' value='"+password2+"'/>*<br/>\n" +
							"first name <input type='text' name='"+VCellApiApplication.NEWFIRSTNAME_FORMNAME+"' value='"+firstname+"'/>*<br/>\n" +
							"last name <input type='text' name='"+VCellApiApplication.NEWLASTNAME_FORMNAME+"' value='"+lastname+"'/>*<br/>\n" +
							"valid email <input type='text' name='"+VCellApiApplication.NEWEMAIL_FORMNAME+"' value='"+email+"'/>*<br/>\n" +
							"institution <input type='text' name='"+VCellApiApplication.NEWINSTITUTE_FORMNAME+"' value='"+institute+"'/><br/>\n" +
							"country <input type='text' name='"+VCellApiApplication.NEWCOUNTRY_FORMNAME+"' value='"+country+"'/><br/>\n" +
							"notify <input type='checkbox' name='"+VCellApiApplication.NEWNOTIFY_FORMNAME+"' value='on' checked='"+notifyChecked+"'/><br/>\n" +
							"<input type='hidden' name='"+VCellApiApplication.NEWFORMPROCESSING_FORMNAME+"' value='yes'/><br/>\n" +
							
							// submit button
							"<input type='submit' value='register'/>\n" +
						"</form>\n" +
						"<h3>* : required</h3>\n" +
				  "</html>\n";
	    	response.setEntity(html, MediaType.TEXT_HTML);
	}
}