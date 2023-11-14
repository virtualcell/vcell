package cbit.vcell.resource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cbit.vcell.client.server.ClientServerInfo;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.StackTraceUtils;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.vcell.util.AmplistorUtils;

public class ErrorUtils {

    private final static Logger lg = LogManager.getLogger(ErrorUtils.class);

    private static boolean bDebugMode = false;
    private static UserLoginInfo loginInfo = null;
    private static ClientServerInfo clientServerInfo = null;

    public static void setDebug(boolean isDebug){
        bDebugMode = isDebug;
    }

    public static void setLoginInfo(UserLoginInfo loginInfo){
        ErrorUtils.loginInfo = loginInfo;
    }

    public static void setClientServerInfo(ClientServerInfo clientServerInfo){
        ErrorUtils.clientServerInfo = clientServerInfo;
    }

    public static void sendErrorReport(Throwable exception) throws RuntimeException{
        sendErrorReport(exception, null);
    }

    public static void sendErrorReport(String message) throws RuntimeException{
        sendErrorReport(null, message);
    }

    public static class ErrorReport {
        public String username;
        public String message;
        public String exceptionMessage;
        public String stackTrace;
        public String softwareVersion;
        public String platform;

        public ErrorReport(){
        }

        public ErrorReport(String username, String message, String exceptionMessage, String stackTrace, String softwareVersion,
                           String platform){
            this.username = username;
            this.message = message;
            this.exceptionMessage = exceptionMessage;
            this.stackTrace = stackTrace;
            this.softwareVersion = softwareVersion;
            this.platform = platform;
        }
    }

    public static void sendErrorReport(Throwable exception, String message) throws RuntimeException{
        String softwareVersion = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
        String exceptionMessage = exception != null ? exception.getMessage() : "null";
        String stackTrace = exception != null ? StackTraceUtils.getStackTrace(exception) : "null";
        String platform = "Running under Java " + (System.getProperty("java.version")) +
                ", published by " + (System.getProperty("java.vendor")) + ", on the " + (System.getProperty("os.arch")) + " architecture running version " + (System.getProperty("os.version")) +
                " of the " + (System.getProperty("os.name")) + " operating system";
        String username = null;
        String serverHost = PropertyLoader.getProperty(PropertyLoader.vcellServerHost, null);
        if(clientServerInfo != null && clientServerInfo.getApihost() != null){
            serverHost = clientServerInfo.getApihost();
            if(clientServerInfo.getUsername() != null){
                username = clientServerInfo.getUsername();
            }
        }
        if(serverHost == null){
            throw new RuntimeException("cannot send error report to server, unknown host");
        }
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslsf = new SSLConnectionSocketFactory(builder.build());
        } catch(Exception e){
            lg.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build()) {
            HttpPost httpPost = new HttpPost("https://" + serverHost + "/contactus");
            Gson gson = new Gson();
            ErrorReport errorReport = new ErrorReport(username, message, exceptionMessage, stackTrace, softwareVersion, platform);
            String json = gson.toJson(errorReport);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200){
                lg.info("sent error message to /contactus");
            } else {
                lg.error("failed to send error message to /contactus");
            }
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void sendRemoteLogMessage(UserLoginInfo argUserLoginInfo, final String message){
        final UserLoginInfo userLoginInfo = argUserLoginInfo != null ? argUserLoginInfo : ErrorUtils.loginInfo;
        if(!ErrorUtils.bDebugMode && userLoginInfo != null){
            new Thread(new Runnable() {
                @Override
                public void run(){
                    try {
                        final String formattedMessage = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n" +
                                "vers='" + VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString() + "' java='" + userLoginInfo.getJava_version() + "' os='" + userLoginInfo.getOs_name() + "' osvers='" + userLoginInfo.getOs_version() + "' arch='" + userLoginInfo.getOs_arch() + "'\n" +
                                message;
                        AmplistorUtils.uploadString(AmplistorUtils.DEFAULT_PROXY_AMPLI_VCELL_LOGS_URL + userLoginInfo.getUserName() + "_" + System.currentTimeMillis(), null, formattedMessage);
                    } catch(Exception e){
                        lg.error("Failed to upload message to Amplistor " + AmplistorUtils.DEFAULT_PROXY_AMPLI_VCELL_LOGS_URL + " : " + message, e);
                        //ignore
                    }
                }
            }).start();
        } else {
            System.err.println("Remote log message: " + message);
        }
    }

//	public static void main(String[] args){
//		System.setProperty(PropertyLoader.vcellServerHost,"Jims-MBP-2.fios-router.home:8082");
//		System.setProperty(PropertyLoader.vcellSoftwareVersion,"my software version");
//		ErrorUtils.sendErrorReport("this works");
//	}

}
