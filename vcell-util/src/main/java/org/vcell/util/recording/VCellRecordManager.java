package org.vcell.util.recording;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class VCellRecordManager implements RecordManager {
    protected static VCellRecordManager instance;
    protected Map<String,Record> logMap; 

    public static RecordManager getInstance(){
        return VCellRecordManager.instance == null ? instance = new VCellRecordManager() : VCellRecordManager.instance;
    }

    protected VCellRecordManager(){
        this.logMap = new HashMap<>();
    }

    @Override
    public FileRecord requestNewFileLog(String fileName){
        if (this.logMap.containsKey(fileName)) 
            return (FileRecord)this.logMap.get(fileName);
        return (FileRecord)this.addLog(fileName, new FileRecord(fileName));
    }

    // Note: uses AutoClose
    public void close() throws IOException {
        for (Record l : this.logMap.values()) 
            l.close();
        logMap.clear();
    }

    protected Record addLog(String name, Record log){
        this.logMap.put(name, log);
        return log;
    }
}
