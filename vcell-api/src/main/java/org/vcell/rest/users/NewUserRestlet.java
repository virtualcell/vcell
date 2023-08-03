package org.vcell.rest.users;

import java.io.IOException;
import java.sql.SQLException;
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
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.vcell.rest.VCellApiApplication;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import com.google.gson.Gson;

import cbit.vcell.resource.PropertyLoader;

public final class NewUserRestlet extends Restlet {
	private final static Logger lg = LogManager.getLogger(NewUserRestlet.class);
	
	public NewUserRestlet(Context context) {
		super(context);
	}

	private void handleJsonRequest(Request request, Response response) {
		String content = request.getEntityAsText();
		Gson gson = new Gson();
		org.vcell.api.common.UserInfo userinfo = gson.fromJson(content, org.vcell.api.common.UserInfo.class);
		
		if (userinfo.email.length()<4){
			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			response.setEntity("valid email required", MediaType.TEXT_PLAIN);
			return;
		}
		if (userinfo.userid.length()<4 || !userinfo.userid.equals(org.vcell.util.TokenMangler.fixTokenStrict(userinfo.userid))){
			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			response.setEntity("userid must be at least 4 characters and contain only alpha-numeric characters", MediaType.TEXT_PLAIN);
			return;
		}
				
		// form new UnverifiedUserInfo
		UserInfo newUserInfo = new UserInfo();
		newUserInfo.company = userinfo.company;
		newUserInfo.country = userinfo.country;
		newUserInfo.digestedPassword0 = DigestedPassword.createAlreadyDigested(userinfo.digestedPassword0);
		newUserInfo.email = userinfo.email;
		newUserInfo.wholeName = userinfo.wholeName;
		newUserInfo.notify = userinfo.notify;
		newUserInfo.title = userinfo.title;
		newUserInfo.userid = userinfo.userid;
		
		boolean bEmailVerification = false;
		
		if (!bEmailVerification) {
			// add Unverified UserInfo and send email
			VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
			try {
				UserInfo insertedUserInfo = vcellApiApplication.getRestDatabaseService().addUser(newUserInfo);
				org.vcell.api.common.UserInfo inserted = insertedUserInfo.getApiUserInfo();				
				String userInfoJson = gson.toJson(inserted);
				JsonRepresentation userRep = new JsonRepresentation(userInfoJson);
				response.setStatus(Status.SUCCESS_CREATED);
				response.setEntity(userRep);
				return;
				
			} catch (SQLException | DataAccessException e) {
				lg.error(e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("failed to add user "+newUserInfo.userid+": "+e.getMessage(), MediaType.TEXT_PLAIN);
				return;
			} catch (UseridIDExistsException e) {
				lg.error(e);
				response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				response.setEntity("failed to add user "+newUserInfo.userid+": "+e.getMessage(), MediaType.TEXT_PLAIN);
				return;
			}
		} else {
			Date submitDate = new Date();
			long timeExpiresMS = 1000*60*60*1; // one hour
			Date expirationDate = new Date(System.currentTimeMillis()+timeExpiresMS);
			DigestedPassword emailVerifyToken = new DigestedPassword(Long.toString(System.currentTimeMillis()));
			UnverifiedUser unverifiedUser = new UnverifiedUser(newUserInfo,submitDate,expirationDate,emailVerifyToken.getString());
			
			// add Unverified UserInfo and send email
			VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
			vcellApiApplication.getUserService().addUnverifiedUser(unverifiedUser);
			
			try {
				//Send new password to user
				PropertyLoader.loadProperties();
				BeanUtils.sendSMTP(
					PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPHostName),
					new Integer(PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPPort)).intValue(),
					PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPEmailAddress),
					newUserInfo.email,
					"new VCell account verification",
					"You have received this email to verify that a Virtual Cell account has been associated " +
					"with this email address.  To activate this account, please follow this link: " +
					request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.NEWUSER_VERIFY+"?"+VCellApiApplication.EMAILVERIFYTOKEN_FORMNAME+"="+emailVerifyToken.getString()
				);
			} catch (Exception e) {
				lg.error(e.getMessage(), e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("we failed to send a verification email to "+newUserInfo.email, MediaType.TEXT_PLAIN);
				return;
			}
			response.setStatus(Status.SUCCESS_CREATED);
			response.setEntity("we sent you a verification email at "+newUserInfo.email+", please follow the link in that email", MediaType.TEXT_PLAIN);
		}
	}

	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.POST)){
			Representation entity = request.getEntity();
			if (entity.getMediaType().equals(MediaType.APPLICATION_JSON)) {
				handleJsonRequest(request, response);
				return;
			}
			
			String content = request.getEntityAsText();
			lg.info(content);
			Form form = new Form(entity);
			
			String userid = form.getFirstValue(VCellApiApplication.NEWUSERID_FORMNAME,"");
			String password1 = form.getFirstValue(VCellApiApplication.NEWPASSWORD1_FORMNAME,"");
			String password2 = form.getFirstValue(VCellApiApplication.NEWPASSWORD2_FORMNAME,"");
			String email = form.getFirstValue(VCellApiApplication.NEWEMAIL_FORMNAME,"");
			String firstName = form.getFirstValue(VCellApiApplication.NEWFIRSTNAME_FORMNAME,"");
			String lastName = form.getFirstValue(VCellApiApplication.NEWLASTNAME_FORMNAME,"");
			String institute = form.getFirstValue(VCellApiApplication.NEWINSTITUTE_FORMNAME,"");
			String country = form.getFirstValue(VCellApiApplication.NEWCOUNTRY_FORMNAME,"");
			String notify = form.getFirstValue(VCellApiApplication.NEWNOTIFY_FORMNAME,"on");
			String formprocessing = form.getFirstValue(VCellApiApplication.NEWFORMPROCESSING_FORMNAME,null);
			
			Status status = null;
			String errorMessage = "";
			
			// validate
			if (!password1.equals(password2)){
				status = Status.CLIENT_ERROR_FORBIDDEN;
				errorMessage = "passwords dont match";
			}
			int MIN_PASSWORD_LENGTH = 5;
			if (password1.length()<MIN_PASSWORD_LENGTH || password1.contains(" ") || password1.contains("'") || password1.contains("\"") || password1.contains(",")){
				status = Status.CLIENT_ERROR_FORBIDDEN;
				errorMessage = "password must be at least "+MIN_PASSWORD_LENGTH+" characters, and must not contains spaces, commas, or quotes";
			}
			if (email.length()<4){
				status = Status.CLIENT_ERROR_FORBIDDEN;
				errorMessage = "valid email required";
			}
			if (userid.length()<4 || !userid.equals(org.vcell.util.TokenMangler.fixTokenStrict(userid))){
				status = Status.CLIENT_ERROR_FORBIDDEN;
				errorMessage = "userid must be at least 4 characters and contain only alpha-numeric characters";
			}
			
			if (errorMessage.length()>0 && formprocessing!=null){
				Form newform = new Form();
				newform.add(VCellApiApplication.NEWERRORMESSAGE_FORMNAME, errorMessage);
				newform.add(VCellApiApplication.NEWUSERID_FORMNAME, userid);
				newform.add(VCellApiApplication.NEWPASSWORD1_FORMNAME, password1);
				newform.add(VCellApiApplication.NEWPASSWORD2_FORMNAME, password2);
				newform.add(VCellApiApplication.NEWEMAIL_FORMNAME, email);
				newform.add(VCellApiApplication.NEWFIRSTNAME_FORMNAME, firstName);
				newform.add(VCellApiApplication.NEWLASTNAME_FORMNAME, lastName);
				newform.add(VCellApiApplication.NEWINSTITUTE_FORMNAME, institute);
				newform.add(VCellApiApplication.NEWCOUNTRY_FORMNAME, country);
				newform.add(VCellApiApplication.NEWNOTIFY_FORMNAME, notify);
				
				Reference redirectRef;
				try {
					redirectRef = new Reference(request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.REGISTRATIONFORM+"?"+newform.encode());
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}
				response.redirectSeeOther(redirectRef);
				return;
			}
			
			// form new UnverifiedUserInfo
			UserInfo newUserInfo = new UserInfo();
			newUserInfo.company = institute;
			newUserInfo.country = country;
			newUserInfo.digestedPassword0 = new DigestedPassword(password1);
			newUserInfo.email = email;
			newUserInfo.wholeName = firstName+" "+lastName;
			newUserInfo.notify = notify.equals("on");
			newUserInfo.title = " ";
			newUserInfo.userid = userid;
			
			Date submitDate = new Date();
			long timeExpiresMS = 1000*60*60*1; // one hour
			Date expirationDate = new Date(System.currentTimeMillis()+timeExpiresMS);
			DigestedPassword emailVerifyToken = new DigestedPassword(Long.toString(System.currentTimeMillis()));
			UnverifiedUser unverifiedUser = new UnverifiedUser(newUserInfo,submitDate,expirationDate,emailVerifyToken.getString());
			
			// add Unverified UserInfo and send email
			VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
			vcellApiApplication.getUserService().addUnverifiedUser(unverifiedUser);
			
			try {
				//Send new password to user
				PropertyLoader.loadProperties();
				BeanUtils.sendSMTP(
					PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPHostName),
					new Integer(PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPPort)).intValue(),
					PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPEmailAddress),
					newUserInfo.email,
					"new VCell account verification",
					"You have received this email to verify that a Virtual Cell account has been associated " +
					"with this email address.  To activate this account, please follow this link: " +
					request.getResourceRef().getHostIdentifier()+"/"+VCellApiApplication.NEWUSER_VERIFY+"?"+VCellApiApplication.EMAILVERIFYTOKEN_FORMNAME+"="+emailVerifyToken.getString()
				);
			} catch (Exception e) {
				lg.error(e.getMessage(), e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("we failed to send a verification email to "+newUserInfo.email, MediaType.TEXT_PLAIN);
			}
			response.setStatus(Status.SUCCESS_CREATED);
			response.setEntity("we sent you a verification email at "+newUserInfo.email+", please follow the link in that email", MediaType.TEXT_PLAIN);
		}
	}
}