package cbit.vcell.resource;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.vcell.util.AmplistorUtils;

public class ErrorUtils {
	
	
	private static boolean bDebugMode = false;
	private static UserLoginInfo loginInfo = null;
	public static void setDebug(boolean isDebug) {
		bDebugMode = isDebug;
	}

	public static void setLoginInfo(UserLoginInfo loginInfo) {
		ErrorUtils.loginInfo = loginInfo;
	}


	public static void sendErrorReport(Throwable exception) throws RuntimeException {
		sendErrorReport(exception,null);
	}

	/**
	 * send error report
	 * @param exception
	 * @param supplement extra information to add, may be null
	 * @throws RuntimeException
	 */
	public static void sendErrorReport(Throwable exception, String supplement) throws RuntimeException {
		if (exception == null) {
			throw new RuntimeException("Send Error Report, exception is null");
		}
		String smtpHost = PropertyLoader.getProperty(PropertyLoader.vcellSMTPHostName, null);
		if (smtpHost == null) {
			return;
		}
		String smtpPort = PropertyLoader.getProperty(PropertyLoader.vcellSMTPPort, null);
		if (smtpPort == null) {
			return;
		}
		String from = "VCell";
		String to = PropertyLoader.getProperty(PropertyLoader.vcellSMTPEmailAddress, null);
		if (to == null) {
			return;
		}
		String subject = "VCell Error Report from " + PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
		String content = BeanUtils.getStackTrace(exception)+"\n";
		String platform = "Running under Java major version: ONE point "+ ResourceUtil.getJavaVersion().toString()+".  Specifically: Java "+(System.getProperty("java.version"))+
			", published by "+(System.getProperty("java.vendor"))+", on the "+ (System.getProperty("os.arch"))+" architecture running version "+(System.getProperty("os.version"))+
			" of the "+(System.getProperty("os.name"))+" operating system";
		content = content + platform;
		if (supplement != null) {
			content += BeanUtils.PLAINTEXT_EMAIL_NEWLINE + supplement;
		}
	
		try {
			BeanUtils.sendSMTP(smtpHost, Integer.parseInt(smtpPort), from, to, subject, content);
		} catch (AddressException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * send message to Virtual Cell server, if not in debug mode
	 * @param userLoginInfo; if null, user previously set info if available
	 * @param message
	 */
	public static void sendRemoteLogMessage(UserLoginInfo argUserLoginInfo,final String message){
		final UserLoginInfo userLoginInfo = argUserLoginInfo != null ? argUserLoginInfo : ErrorUtils.loginInfo;
		if (!ErrorUtils.bDebugMode && userLoginInfo != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						final String formattedMessage = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+"\n"+
							"vers='"+VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString()+"' java='"+userLoginInfo.getJava_version()+"' os='"+userLoginInfo.getOs_name()+"' osvers='"+userLoginInfo.getOs_version()+"' arch='"+userLoginInfo.getOs_arch()+"'\n"+
							message;
						AmplistorUtils.uploadString(AmplistorUtils.DEFAULT_PROXY_AMPLI_VCELL_LOGS_URL+userLoginInfo.getUserName()+"_"+System.currentTimeMillis(), null, formattedMessage);
					}catch(Exception e){
						e.printStackTrace();
						System.err.println("Failed to upload message to Amplistor "+AmplistorUtils.DEFAULT_PROXY_AMPLI_VCELL_LOGS_URL+" : "+message);
						//ignore
					}
				}
			}).start();
		}
		else {
			System.err.println("Remote log message: " + message);
		}
	}

}
