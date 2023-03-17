package org.vcell.util.recording;

public class CLIRecordManager extends VCellRecordManager {

    protected static CLIRecordManager instance;

    protected CLIRecordManager(){
        super();
    }

    public static VCellRecordManager getInstance(){
        return CLIRecordManager.instance == null ? instance = new CLIRecordManager() : CLIRecordManager.instance;
    }
}
