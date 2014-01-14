package org.vcell.rest.users;

import java.io.IOException;
import java.sql.SQLException;
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
import org.vcell.rest.VCellApiApplication;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;

public final class EmailTokenVerifyRestlet extends Restlet {
	public EmailTokenVerifyRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.GET)){
			Form form = request.getResourceRef().getQueryAsForm();
			String emailverify_token = form.getFirstValue(VCellApiApplication.EMAILVERIFYTOKEN_FORMNAME);
			VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
			UnverifiedUser unverifiedUser = vcellApiApplication.getUserVerifier().getUnverifiedUser(emailverify_token);
			if (unverifiedUser!=null){
				if (unverifiedUser.verificationTimeoutDate.after(new Date())){
					// add user to database
					// 
					try {
						vcellApiApplication.getRestDatabaseService().addUser(unverifiedUser.submittedUserInfo);
					} catch (SQLException e1) {
						e1.printStackTrace();
						throw new RuntimeException(e1.getMessage(),e1);
					} catch (DataAccessException e1) {
						e1.printStackTrace();
						throw new RuntimeException(e1.getMessage(),e1);
					} catch (UseridIDExistsException e1) {
						e1.printStackTrace();
						throw new RuntimeException(e1.getMessage(),e1);
					}

					//
					// make default redirect after login (/biomodel).
					//
					Reference successRedirectRef = new Reference(request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.BIOMODEL);
					//
					// redirect to login page for user to log in
					//
					Form newform = new Form();
					newform.add(VCellApiApplication.REDIRECTURL_FORMNAME, successRedirectRef.toUrl().toString());
					newform.add(VCellApiApplication.IDENTIFIER_FORMNAME, unverifiedUser.submittedUserInfo.userid);
					newform.add(VCellApiApplication.SECRET_FORMNAME, "");

					Reference redirectRef;
					try {
						redirectRef = new Reference(request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.LOGINFORM+"?"+newform.encode());
					} catch (IOException e) {
						throw new RuntimeException(e.getMessage());
					}
					response.redirectSeeOther(redirectRef);
					return;
				}else{
					response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					response.setEntity("email verification expired, please register again at "+request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.REGISTRATIONFORM, MediaType.TEXT_PLAIN);
				}
			}else{
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				response.setEntity("email verification not found, please register again at "+request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.REGISTRATIONFORM, MediaType.TEXT_PLAIN);
			}
		}
	}
}