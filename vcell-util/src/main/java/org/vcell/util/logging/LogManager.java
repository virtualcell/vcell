package org.vcell.util.logging;

public interface LogManager extends AutoCloseable {

    public static LogManager getInstance(){
        // time for some black magic to create a default method.
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String topClassName = stackTrace[stackTrace.length - 1].getClassName();
        if (topClassName.contains("org.vcell.cli")){
            return CLILogManager.getInstance(); // Replace with class!
        } else if (topClassName.contains("cbit.vcell.client")){
            return new VCellLogManager(); // Replace with more specific class!
        } else {
            throw new RuntimeException("Unknown entry point detected; unable to decide which version of LogManager to provide.");
        }
    }

    public FileLog requestNewFileLog(String fileName);
    public StdErrLog requestStdErrLog();
    public StdOutLog requestStdOutLog();
    public Log4JLog requestLog4JLog();
}
