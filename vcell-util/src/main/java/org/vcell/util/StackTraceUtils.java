package org.vcell.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtils {
    public static String getStackTrace(Throwable throwable) {
        StringWriter out = new StringWriter();
        PrintWriter pw = new PrintWriter(out);
        Throwable t = throwable;
        while (t != null) {
            t.printStackTrace(pw);
            t = t.getCause( );
        }
        pw.close();
        return out.getBuffer().toString();
    }

    /**
     * @return String for current stack trace
     */
    public static String getStackTrace() {
        StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        //0 and 1 are "getStackTrace" -- start with callers invocation
        for (int i = 2; i < stackTraceArray.length; i++) {
            sb.append(stackTraceArray[i]);
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * recursive assemble exception message
     * @param throwable exception / error to recursively get the message from
     * @return {@link Throwable#getMessage()}, recursively
     */
    public static String getMessageRecursive(Throwable throwable) {
        StringBuilder rval = new StringBuilder(throwable.getMessage());
        Throwable cause = throwable.getCause();
        while (cause != null) {
            rval.append(" caused by ").append(cause.getMessage());
            cause = cause.getCause();
        }

        return rval.toString();
    }
}
