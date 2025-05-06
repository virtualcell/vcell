package org.vcell.rest.users;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import cbit.vcell.resource.ErrorUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.*;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.vcell.rest.VCellApiApplication;
import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.resource.PropertyLoader;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public final class ContactUsRestlet extends Restlet {
	private final static Logger lg = LogManager.getLogger(ContactUsRestlet.class);

	public ContactUsRestlet(Context context) {
		super(context);
	}

	private void sendErrorReportEmail(ErrorUtils.ErrorReport errorReport){
		String smtpHost = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPHostName);
		String smtpPort = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPPort);
		String from = "vcellserver";
		String vcellSupportEmail = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPEmailAddress);

		try {
			BeanUtils.sendSMTP(smtpHost, Integer.parseInt(smtpPort), from, vcellSupportEmail, "VCell Support sent through Contact-Us", errorReport.formatErrorMessageForEmailBody());
		} catch (MessagingException e) {
			String username = errorReport.username == null ? "Unknown" : errorReport.username;
			String message = errorReport.message == null ? "Unknown" : errorReport.message;
            this.getLogger().log(Level.SEVERE,"failed to send user (" + username + ") error message " + message, e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.POST)){
			try {
				String errorReportJson = request.getEntityAsText();
				Gson gson = new Gson();
				ErrorUtils.ErrorReport errorReport = gson.fromJson(errorReportJson, ErrorUtils.ErrorReport.class);

				if (errorReport == null) {
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					response.setEntity("bad request", MediaType.TEXT_PLAIN);
					return;		// nothing useful to do
				}

                this.sendErrorReportEmail(errorReport);
			} catch (Exception e) {
				lg.error(e.getMessage(), e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("We failed to report an error to VCell support", MediaType.TEXT_PLAIN);
				return;
			}

			response.setStatus(Status.SUCCESS_ACCEPTED);
			response.setEntity("We successfully reported an error to vCell support", MediaType.TEXT_PLAIN);
		}
	}
}