package org.vcell.util.recording;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class VCellRecordManager implements RecordManager {
    protected static VCellRecordManager instance;
    protected Map<String,Record> logMap; 

    public static VCellRecordManager getInstance(){
        return VCellRecordManager.instance == null ? instance = new VCellRecordManager() : VCellRecordManager.instance;
    }

    protected VCellRecordManager(){
        this.logMap = new HashMap<>();
    }

    @Override
    public TextFileRecord requestNewRecord(String filePath){
        if (this.logMap.containsKey(filePath)) 
            return (TextFileRecord)this.logMap.get(filePath);
        // The log hasn't been registered yet; lets do that.

        // Make parent if it doesn't exist
        File parent = (new File(filePath)).getParentFile();
        if (!parent.exists())parent.mkdirs();
        
        return (TextFileRecord)this.addLog(filePath, new TextFileRecord(filePath));
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
