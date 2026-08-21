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
        public String exceptionMessage;
        public String stackTrace;
        public String softwareVersion;
        public String platform;

        /**
         * Absent or 1 for the original report, where the client log arrived inside the
         * exception message; 2 for the structured form, where each part has its own field.
         * The server renders both, so a client of either vintage is understood.
         */
        public Integer reportVersion;
        /** Version 2 only: the model the user was working on, previously mixed into the log. */
        public String modelInfo;
        /** Version 2 only: the client log, carried once and nowhere else. */
        public String clientLog;
        /** Version 2 only: the recorded user events, previously appended to the log. */
        public String userEvents;

        public ErrorReport(){
        }

        /**
         * A version 2 report: the exception, the log, the model and the recorded events are
         * separate fields, so nothing has to be recovered from inside anything else.
         */
        public static ErrorReport version2(String username, String userMessage, String exceptionMessage,
                                           String stackTrace, String softwareVersion, String platform,
                                           String modelInfo, String clientLog, String userEvents){
            ErrorReport report = new ErrorReport(username, userMessage, exceptionMessage, stackTrace,
                    softwareVersion, platform);
            report.reportVersion = 2;
            report.modelInfo = modelInfo;
            report.clientLog = clientLog;
            report.userEvents = userEvents;
            return report;
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

        /**
         * Render this report as the plain-text body of a support email.
         *
         * <p>The report reaches vcell_support as an email. Serialised as JSON it arrives as
         * a single line thousands of characters long, with every newline written as a
         * literal backslash-n and every apostrophe as a unicode escape -- readable by a
         * parser, not by a person.
         *
         * <p>Sections run shortest first, so that what a reader sees -- or what a mail
         * client shows in a preview, typically the first couple of thousand characters --
         * is the identifying detail rather than the opening lines of a very long client
         * log. The log, which can run to hundreds of kilobytes, always comes last.
         */
        public String toEmailText(){
            StringBuilder sb = new StringBuilder();
            sb.append("VCell error report").append(NL);
            sb.append("==================").append(NL).append(NL);
            appendField(sb, "User", username);
            appendField(sb, "Version", softwareVersion);
            appendField(sb, "Platform", platform);
            appendSection(sb, "User message", message);
            if (isVersion2()){
                appendVersion2Body(sb);
            } else {
                appendLegacyBody(sb);
            }
            return sb.toString();
        }

        private boolean isVersion2(){
            return reportVersion != null && reportVersion >= 2;
        }

        /** Every part arrived in its own field, so simply lay them out, log last. */
        private void appendVersion2Body(StringBuilder sb){
            appendSection(sb, "Exception", exceptionMessage);
            appendSection(sb, "Stack trace", stackTrace);
            appendSection(sb, "Model", modelInfo);
            appendSection(sb, "Recorded user events", userEvents);
            appendSection(sb, "Client log", clientLog);
        }

        /**
         * A version 1 report from an older client. The client log arrives twice: once as
         * exceptionMessage, and again as the message of the exception that opens the stack
         * trace, because the report was raised as new RuntimeException(log). On a real report
         * that was 94% of the body, and the second copy buried the exception chain behind
         * tens of thousands of characters of routine logging. Carry it once, at the end.
         */
        private void appendLegacyBody(StringBuilder sb){
            String log = normalizeNewlines(nullToEmpty(exceptionMessage)).trim();
            String trace = normalizeNewlines(nullToEmpty(stackTrace)).trim();
            if (log.length() > MIN_DEDUPE_LENGTH && trace.contains(log)){
                trace = trace.replace(log, LOG_MOVED_MARKER);
            }
            appendSection(sb, "Exception chain and stack trace", trace);
            appendSection(sb, "Client log", log);
        }

        /** Short enough that a coincidental match would not matter; long enough to be the log. */
        private static final int MIN_DEDUPE_LENGTH = 200;
        private static final String LOG_MOVED_MARKER = "(client log -- reproduced in full below)";

        private static String nullToEmpty(String s){
            return s == null || "null".equals(s.trim()) ? "" : s;
        }

        private static final String NL = "\n";
        private static final int RULE_WIDTH = 62;

        private static void appendField(StringBuilder sb, String label, String value){
            String shown = isBlank(value) ? "(not reported)" : oneLine(value);
            sb.append(pad(label + ":", 10)).append(' ').append(shown).append(NL);
        }

        private static void appendSection(StringBuilder sb, String title, String value){
            sb.append(NL).append("--- ").append(title).append(' ');
            for (int i = title.length() + 5; i < RULE_WIDTH; i++){
                sb.append('-');
            }
            sb.append(NL).append(NL);
            sb.append(isBlank(value) ? "(none)" : normalizeNewlines(value).trim());
            sb.append(NL);
        }

        private static String pad(String s, int width){
            StringBuilder sb = new StringBuilder(s);
            while (sb.length() < width){
                sb.append(' ');
            }
            return sb.toString();
        }

        private static boolean isBlank(String s){
            return s == null || s.trim().isEmpty() || "null".equals(s.trim());
        }

        /** Newlines arrive as CRLF from some platforms; normalise so the body renders evenly. */
        private static String normalizeNewlines(String s){
            return s.replace("\r\n", "\n").replace('\r', '\n');
        }

        /** Keep a header field on one line however the value was assembled. */
        private static String oneLine(String s){
            return normalizeNewlines(s).replace('\n', ' ').replaceAll(" +", " ").trim();
        }
    }

    /**
     * Send a version 2 report: the exception, the client log, the model and the recorded user
     * events travel in separate fields.
     *
     * <p>The original form had the caller wrap everything in
     * {@code new RuntimeException(log, cause)} before sending, which put the log in the
     * exception's message and therefore into both {@code exceptionMessage} and the head of
     * {@code stackTrace}. That duplicate was 94% of a real support email, and it pushed the
     * frames that identify the fault past fifty thousand characters of routine logging.
     *
     * @param exception the exception as thrown, not wrapped
     */
    public static void sendErrorReport(Throwable exception, String userMessage, String modelInfo,
                                       String clientLog, String userEvents) throws RuntimeException{
        String exceptionMessage = exception != null ? describe(exception) : null;
        String stackTrace = exception != null ? StackTraceUtils.getStackTrace(exception) : null;
        ErrorReport report = ErrorReport.version2(currentUsername(), userMessage, exceptionMessage, stackTrace,
                PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion), currentPlatform(),
                modelInfo, clientLog, userEvents);
        postErrorReport(report);
    }

    /** Type and message, so the section reads as an exception rather than a bare sentence. */
    private static String describe(Throwable exception){
        String message = exception.getMessage();
        return exception.getClass().getName() + (message == null ? "" : ": " + message);
    }

    private static String currentPlatform(){
        return "Running under Java " + (System.getProperty("java.version")) +
                ", published by " + (System.getProperty("java.vendor")) + ", on the " + (System.getProperty("os.arch")) +
                " architecture running version " + (System.getProperty("os.version")) +
                " of the " + (System.getProperty("os.name")) + " operating system";
    }

    private static String currentUsername(){
        if(clientServerInfo != null && clientServerInfo.getUsername() != null){
            return clientServerInfo.getUsername();
        }
        return null;
    }

    private static void postErrorReport(ErrorReport errorReport) throws RuntimeException{
        String serverHost = PropertyLoader.getProperty(PropertyLoader.vcellServerHost, null);
        String serverPrefixV0 = PropertyLoader.getProperty(PropertyLoader.vcellServerPrefixV0, null);
        if(clientServerInfo != null && clientServerInfo.getApihost() != null){
            serverHost = clientServerInfo.getApihost();
        }
        if(serverHost == null){
            throw new RuntimeException("cannot send error report to server, unknown host");
        }
        SSLConnectionSocketFactory sslsf;
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslsf = new SSLConnectionSocketFactory(builder.build());
        } catch(Exception e){
            lg.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build()) {
            HttpPost httpPost = new HttpPost("https://" + serverHost + serverPrefixV0 + "/contactus");
            String json = new Gson().toJson(errorReport);
            httpPost.setEntity(new StringEntity(json));
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

    public static void sendErrorReport(Throwable exception, String message) throws RuntimeException{
        String softwareVersion = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
        String exceptionMessage = exception != null ? exception.getMessage() : "null";
        String stackTrace = exception != null ? StackTraceUtils.getStackTrace(exception) : "null";
        String platform = "Running under Java " + (System.getProperty("java.version")) +
                ", published by " + (System.getProperty("java.vendor")) + ", on the " + (System.getProperty("os.arch")) + " architecture running version " + (System.getProperty("os.version")) +
                " of the " + (System.getProperty("os.name")) + " operating system";
        String username = null;
        String serverHost = PropertyLoader.getProperty(PropertyLoader.vcellServerHost, null);
        String serverPrefixV0 = PropertyLoader.getProperty(PropertyLoader.vcellServerPrefixV0, null);
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
            HttpPost httpPost = new HttpPost("https://" + serverHost + serverPrefixV0 + "/contactus");
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
