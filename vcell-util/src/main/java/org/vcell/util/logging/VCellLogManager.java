package org.vcell.util.logging;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class VCellLogManager implements LogManager {
    protected static VCellLogManager instance;
    protected Map<String,Log> logMap; 

    public static LogManager getInstance(){
        return VCellLogManager.instance == null ? instance = new VCellLogManager() : VCellLogManager.instance;
    }

    protected VCellLogManager(){
        this.logMap = new HashMap<>();
    }

    @Override
    public FileLog requestNewFileLog(String fileName){
        if (this.logMap.containsKey(fileName)) 
            return (FileLog)this.logMap.get(fileName);
        return (FileLog)this.addLog(fileName, new FileLog(fileName));
    }

    @Override
    public StdErrLog requestStdErrLog(){
        String stdErrKey = "StdErrStream";
        if (this.logMap.containsKey(stdErrKey)) 
            return (StdErrLog)this.logMap.get(stdErrKey);
        return (StdErrLog)this.addLog(stdErrKey, new StdErrLog());
    }

    @Override
    public StdOutLog requestStdOutLog(){
        String stdOutKey = "StdOutStream";
        if (this.logMap.containsKey(stdOutKey)) 
            return (StdOutLog)this.logMap.get(stdOutKey);
        return (StdOutLog)this.addLog(stdOutKey, new StdOutLog());
    }

    @Override
    public Log4JLog requestLog4JLog(Class<?> clazz){
        if (this.logMap.containsKey(clazz.getName())) 
            return (Log4JLog)this.logMap.get(clazz.getName());
        return (Log4JLog)this.addLog(clazz.getName(), new Log4JLog(clazz));
    }

    // Note: uses AutoClose
    public void close() throws IOException {
        for (Log l : this.logMap.values()) 
            l.close();
        logMap.clear();
    }

    protected Log addLog(String name, Log log){
        this.logMap.put(name, log);
        return log;
    }
}
