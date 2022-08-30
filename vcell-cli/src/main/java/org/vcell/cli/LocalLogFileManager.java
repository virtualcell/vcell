package org.vcell.cli;

import java.io.File;
import java.util.List;


public interface LocalLogFileManager {

    public List<LocalLogFileName> getAllLocalLogFileName();

    public File getLogFile(LocalLogFileName logName);

    public String getLogFileContents(LocalLogFileName logName);

    public void clearLogFile(LocalLogFileName logName);

    public void clearAllLogFiles();

    public boolean finalizeAndExportLogFiles();

}
