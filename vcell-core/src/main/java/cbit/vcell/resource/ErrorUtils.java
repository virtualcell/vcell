package cbit.vcell.resource;

import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.util.AmplistorUtils;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        public String exceptionType;
        public String exceptionMessage;
        public String stackTrace;
        public String softwareVersion;
        public String targetedServerID;
        public String javaPlatform;
        public String machinePlatform;

        public ErrorReport(){}

        public ErrorReport(String username, String message, String exceptionType, String exceptionMessage, String stackTrace, String softwareVersion, String targetedServerID,
                           String javaPlatform, String machinePlatform){
            this.username = username;
            this.message = message;
            this.exceptionType = exceptionType;
            this.exceptionMessage = exceptionMessage;
            this.stackTrace = stackTrace;
            this.softwareVersion = softwareVersion;
            this.targetedServerID = targetedServerID;
            this.javaPlatform = javaPlatform;
            this.machinePlatform = machinePlatform;
        }

        public String formatErrorMessageForEmailBody(){
            StringBuilder sb = new StringBuilder();
            sb.append("An issue has been encountered by \"").append(this.username)
                    .append("\" on VCell ").append(this.softwareVersion)
                    .append(" deployed for: ").append(this.targetedServerID).append("\n");
            sb.append("\tIssue occurred on ").append(this.machinePlatform).append(" using ").append(this.javaPlatform).append("\n");
            sb.append("\tException Type: ").append(this.exceptionType).append("\n");
            sb.append("\tException Message:\n\t\t").append(this.exceptionMessage.replace("\r\n", "\n").replace("\n", "\n\t\t")).append("\n");
            sb.append("\tStack trace:\n\t\t").append(this.exceptionMessage.replace("\r\n", "\n").replace("\n", "\n\t\t")).append("\n");
            return sb.toString();
        }
    }

    public static void sendErrorReport(Throwable exception, String message) throws RuntimeException {
        boolean canGetApiHost = ErrorUtils.clientServerInfo != null && ErrorUtils.clientServerInfo.getApihost() != null;
        String serverHost = canGetApiHost ? ErrorUtils.clientServerInfo.getApihost() : PropertyLoader.getProperty(PropertyLoader.vcellServerHost, null);
        if(serverHost == null) throw new RuntimeException("cannot send error report to server, unknown host");
        String serverPrefixV0 = PropertyLoader.getProperty(PropertyLoader.vcellServerPrefixV0, null);

        String username = ErrorUtils.clientServerInfo != null && ErrorUtils.clientServerInfo.getUsername() != null ? ErrorUtils.clientServerInfo.getUsername() : "<Anonymous User>";
        SSLConnectionSocketFactory ssLConnectionFactory;
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            ssLConnectionFactory = new SSLConnectionSocketFactory(builder.build());
        } catch(Exception e){
            lg.error(e);
            throw new RuntimeException("Problem encountered attempting to build SSL Connection for Error Report", e);
        }
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(ssLConnectionFactory).build()) {
            HttpPost httpPost = new HttpPost("https://" + serverHost + serverPrefixV0 + "/contactus");
            Gson gson = new Gson();
            ErrorReport errorReport = ErrorUtils.generateErrorReport(exception, message, username);
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

     static ErrorReport generateErrorReport(Throwable exception, String message, String username){
        String softwareVersion = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
        String exceptionType = exception != null ? exception.getClass().getSimpleName() : "<none>";
        String exceptionMessage = exception != null ? exception.getMessage() : "N/A";
        String stackTrace = exception != null ? StackTraceUtils.getStackTrace(exception) : "N/A";
        String javaPlatform = "Java " + System.getProperty("java.version") + " " + "(via '" + System.getProperty("java.vendor") + exceptionType + "') ";
        String machinePlatform = System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")[" + System.getProperty("os.arch") + "]";
        String serverID = PropertyLoader.getProperty(PropertyLoader.vcellServerIDProperty, "<UNKNOWN>");

        return new ErrorReport(username, message, exceptionType, exceptionMessage, stackTrace, softwareVersion, javaPlatform, machinePlatform, serverID);
    }

//	public static void main(String[] args){
//		System.setProperty(PropertyLoader.vcellServerHost,"Jims-MBP-2.fios-router.home:8082");
//		System.setProperty(PropertyLoader.vcellSoftwareVersion,"my software version");
//		ErrorUtils.sendErrorReport("this works");
//	}

}
