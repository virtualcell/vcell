package org.vcell.util.logging;

public class CLILogManager extends VCellLogManager {

    protected static CLILogManager instance;

    protected CLILogManager(){
        super();
    }

    public static LogManager getInstance(){
        return CLILogManager.instance == null ? instance = new CLILogManager() : CLILogManager.instance;
    }
}
