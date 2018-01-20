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
			throw new RuntimeException("not yet implementd in new VCellapi RPC");
//			Representation entity = request.getEntity();
//			Form form = new Form(entity);
//			
//			String userid = form.getFirstValue(VCellApiApplication.NEWUSERID_FORMNAME,"");
//			String password1 = form.getFirstValue(VCellApiApplication.NEWPASSWORD1_FORMNAME,"");
//			String password2 = form.getFirstValue(VCellApiApplication.NEWPASSWORD2_FORMNAME,"");
//			String email = form.getFirstValue(VCellApiApplication.NEWEMAIL_FORMNAME,"");
//			String firstName = form.getFirstValue(VCellApiApplication.NEWFIRSTNAME_FORMNAME,"");
//			String lastName = form.getFirstValue(VCellApiApplication.NEWLASTNAME_FORMNAME,"");
//			String institute = form.getFirstValue(VCellApiApplication.NEWINSTITUTE_FORMNAME,"");
//			String country = form.getFirstValue(VCellApiApplication.NEWCOUNTRY_FORMNAME,"");
//			String notify = form.getFirstValue(VCellApiApplication.NEWNOTIFY_FORMNAME,"on");
//			String formprocessing = form.getFirstValue(VCellApiApplication.NEWFORMPROCESSING_FORMNAME,null);
//			
//			Status status = null;
//			String errorMessage = "";
//			
//			// validate
//			if (!password1.equals(password2)){
//				status = Status.CLIENT_ERROR_FORBIDDEN;
//				errorMessage = "passwords dont match";
//			}
//			int MIN_PASSWORD_LENGTH = 5;
//			if (password1.length()<MIN_PASSWORD_LENGTH || password1.contains(" ") || password1.contains("'") || password1.contains("\"") || password1.contains(",")){
//				status = Status.CLIENT_ERROR_FORBIDDEN;
//				errorMessage = "password must be at least "+MIN_PASSWORD_LENGTH+" characters, and must not contains spaces, commas, or quotes";
//			}
//			if (email.length()<4){
//				status = Status.CLIENT_ERROR_FORBIDDEN;
//				errorMessage = "valid email required";
//			}
//			if (userid.length()<4 || !userid.equals(org.vcell.util.TokenMangler.fixTokenStrict(userid))){
//				status = Status.CLIENT_ERROR_FORBIDDEN;
//				errorMessage = "userid must be at least 4 characters and contain only alpha-numeric characters";
//			}
//			
//			if (errorMessage.length()>0 && formprocessing!=null){
//				Form newform = new Form();
//				newform.add(VCellApiApplication.NEWERRORMESSAGE_FORMNAME, errorMessage);
//				newform.add(VCellApiApplication.NEWUSERID_FORMNAME, userid);
//				newform.add(VCellApiApplication.NEWPASSWORD1_FORMNAME, password1);
//				newform.add(VCellApiApplication.NEWPASSWORD2_FORMNAME, password2);
//				newform.add(VCellApiApplication.NEWEMAIL_FORMNAME, email);
//				newform.add(VCellApiApplication.NEWFIRSTNAME_FORMNAME, firstName);
//				newform.add(VCellApiApplication.NEWLASTNAME_FORMNAME, lastName);
//				newform.add(VCellApiApplication.NEWINSTITUTE_FORMNAME, institute);
//				newform.add(VCellApiApplication.NEWCOUNTRY_FORMNAME, country);
//				newform.add(VCellApiApplication.NEWNOTIFY_FORMNAME, notify);
//				
//				Reference redirectRef;
//				try {
//					redirectRef = new Reference(request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.REGISTRATIONFORM+"?"+newform.encode());
//				} catch (IOException e) {
//					throw new RuntimeException(e.getMessage());
//				}
//				response.redirectSeeOther(redirectRef);
//				return;
//			}
//			
//			// form new UnverifiedUserInfo
//			UserInfo newUserInfo = new UserInfo();
//			newUserInfo.company = institute;
//			newUserInfo.country = country;
//			newUserInfo.digestedPassword0 = new DigestedPassword(password1);
//			newUserInfo.email = email;
//			newUserInfo.wholeName = firstName+" "+lastName;
//			newUserInfo.notify = notify.equals("on");
//			newUserInfo.title = " ";
//			newUserInfo.userid = userid;
//			
//			Date submitDate = new Date();
//			long timeExpiresMS = 1000*60*60*1; // one hour
//			Date expirationDate = new Date(System.currentTimeMillis()+timeExpiresMS);
//			DigestedPassword emailVerifyToken = new DigestedPassword(Long.toString(System.currentTimeMillis()));
//			UnverifiedUser unverifiedUser = new UnverifiedUser(newUserInfo,submitDate,expirationDate,emailVerifyToken.getString());
//			
//			// add Unverified UserInfo and send email
//			VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
//			vcellApiApplication.getUserVerifier().addUnverifiedUser(unverifiedUser);
//			
//			try {
//				//Send new password to user
//				PropertyLoader.loadProperties();
//				BeanUtils.sendSMTP(
//					PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPHostName),
//					new Integer(PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPPort)).intValue(),
//					PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPEmailAddress),
//					newUserInfo.email,
//					"new VCell account verification",
//					"You have received this email to verify that a Virtual Cell account has been associated " +
//					"with this email address.  To activate this account, please follow this link: " +
//					request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.NEWUSER_VERIFY+"?"+VCellApiApplication.EMAILVERIFYTOKEN_FORMNAME+"="+emailVerifyToken.getString()
//				);
//			} catch (Exception e) {
//				e.printStackTrace();
//				response.setStatus(Status.SERVER_ERROR_INTERNAL);
//				response.setEntity("we failed to send a verification email to "+newUserInfo.email, MediaType.TEXT_PLAIN);
//			}
//			response.setStatus(Status.SUCCESS_CREATED);
//			response.setEntity("we sent you a verification email at "+newUserInfo.email+", please follow the link in that email", MediaType.TEXT_PLAIN);
		}
	}
}