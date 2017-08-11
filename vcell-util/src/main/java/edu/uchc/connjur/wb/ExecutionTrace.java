// $Id: ExecutionTrace.java,v 1.8 2012/07/31 14:27:34 gerard Exp $
package edu.uchc.connjur.wb;

import java.lang.reflect.Field;

/**
 * utility for logging/debugging/monitoring WorkflowBuilder execution
 * @author gerard
 *
 */
public class ExecutionTrace {

    /**
     * uniquely identify by classname, object id, and thread executing in
     * @param o
     * @return String with identifying info
     */
    public synchronized static String ident(Object o) {
        long tid = Thread.currentThread().getId();
        int objectId = System.identityHashCode(o);

        String name = (o != null) ? justClassName(o) : "null";
        StringBuilder sb = new StringBuilder();
        
        sb.append(name);
        sb.append(" id ");
        sb.append(Integer.toHexString(objectId));
        sb.append(", thread ");
        sb.append(tid);

        return sb.toString();
    }

    /**
     * just class name
     * @param o object or null reference
     * @return name with no package or "null"
     */
    public static String justClassName(Object o) {
        if (o != null) {
            return justClassName(o.getClass());
        }
        return "null";
    }

    /**
     * get bare name. Adapted from
     * http://www.rgagnon.com/javadetails/java-0389.html, Creative Commons license
     * @param o
     * @return classname without package
     */
    public static String justClassName(Class<?> clzz) {
        String fullname = clzz.getName();
        int firstChar = fullname.lastIndexOf('.') + 1;
        if (firstChar > 0) {
            return fullname.substring(firstChar);
        }
        return fullname;
    }

    public static String describe(Field f) {
        StringBuilder sb = new StringBuilder();
        sb.append(justClassName(f.getType()));
        sb.append(' ');
        sb.append(justClassName(f.getDeclaringClass()));
        sb.append('.');
        sb.append(f.getName());
        return sb.toString();
    }

    /**
     * return caller method name
     * @return calling method name
     */
    public static String currentMethod() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        //0 is getStackTrace
        //1 is currentMethod
        return st[2].getMethodName();
    }
    /**
     * prevent object creation
     */
    private ExecutionTrace() {}

    /**
     * if throwable caused by another throwable, return original throwable (recursively)
     * @param t
     * @return t or causing throwable
     */
    public static Throwable getRootThrowable(Throwable t) {
        if (t != null) {
            Throwable cause = t.getCause();
            while (cause != null) {
                t = cause;
                cause = t.getCause();
            }
        }
        return t;
    }
}
/*
 * $Log: ExecutionTrace.java,v $
 * Revision 1.8  2012/07/31 14:27:34  gerard
 * doc fix
 *
 * Revision 1.7  2012/06/04 14:35:19  gerard
 * support null in justclassname
 *
 * Revision 1.6  2011/11/15 22:55:39  gerard
 * split dataflow into separate workflow and reconstruction classes
 *
 * Revision 1.5  2011/03/29 14:37:11  gerard
 * improve error management, runtime processing
 *
 * Revision 1.4  2011/03/26 16:30:36  gerard
 * replace RuntimeExceptions with WorkflowBuilderProcessingExceptions, implement delete Workflows
 *
 * Revision 1.3  2011/03/24 19:45:26  gerard
 * implement Strand copy / paste
 *
 * Revision 1.2  2011/03/11 14:07:48  gerard
 * Improve processing context;standardize format and CVS tag
 *
 * Revision 1.1  2011/02/25 18:51:49  gerard
 * revamp execution processing
 *
 */
