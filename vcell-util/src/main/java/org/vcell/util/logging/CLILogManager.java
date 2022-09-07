package org.vcell.util.logging;

public class CLILogManager extends VCellLogManager {

    protected static CLILogManager instance;

    public static LogManager getInstance(){
        return CLILogManager.instance == null ? instance = new CLILogManager() : CLILogManager.instance;
    }
}
