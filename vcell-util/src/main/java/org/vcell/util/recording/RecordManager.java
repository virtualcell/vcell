package org.vcell.util.recording;

public interface RecordManager extends AutoCloseable {

    /**
     * Default 
     */
    public static RecordManager getInstance(){
        // time for some black magic to create a default method.
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String topClassName = stackTrace[stackTrace.length - 1].getClassName();
        if (topClassName.contains("org.vcell.cli")){
            return CLIRecordManager.getInstance(); // Replace with class!
        } else if (topClassName.contains("cbit.vcell.client")){
            return new VCellRecordManager(); // Replace with more specific class!
        } else {
            throw new RuntimeException("Unknown entry point detected; unable to decide which version of LogManager to provide.");
        }
    }

    public FileRecord requestNewFileLog(String fileName);
}
