package org.vcell.util.logging;

import java.io.IOException;
import java.util.HashSet;

public class VCellLogManager implements LogManager {
    protected static VCellLogManager instance;
    protected HashSet<Log> logSet; 

    public static LogManager getInstance(){
        return VCellLogManager.instance == null ? instance = new VCellLogManager() : VCellLogManager.instance;
    }

    protected VCellLogManager(){
        this.logSet = new HashSet<>();
    }

    @Override
    public FileLog requestNewFileLog(String fileName){
        return (FileLog)this.addLog(new FileLog(fileName));
    }

    @Override
    public StdErrLog requestStdErrLog(){
        return (StdErrLog)this.addLog(new StdErrLog());
    }

    @Override
    public StdOutLog requestStdOutLog(){
        return (StdOutLog)this.addLog(new StdOutLog());
    }

    @Override
    public Log4JLog requestLog4JLog(Class<?> clazz){
        return (Log4JLog)this.addLog(new Log4JLog(clazz));
    }

    // Note: uses AutoClose
    public void close() throws IOException {
        for (Log l : this.logSet) l.close();
        logSet.clear();
    }

    protected Log addLog(Log log){
        this.logSet.add(log);
        return log;
    }
}
