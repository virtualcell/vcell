package org.vcell.util;

import org.vcell.util.logging.CLILogManager;
import org.vcell.util.logging.LogManager;
import org.vcell.util.logging.VCellLogManager;

public class VCellUtilityHub {

    /*
     * For each way we want to initialize VCell:
     * - Add a option to MODE
     * - Add a startVCell<MODE name> method
     * - Add the new start method to the private constructor
     * - (optional) Add relevant closing calls to shutdown  
     */


    public enum MODE {
        CLI,STANDARD
    }


    private static VCellUtilityHub instance;

    // Utility declarations
    private static LogManager logManager;

    // Methods
    public static void startup(MODE mode){
        if (instance == null) instance = new VCellUtilityHub(mode);
        else throw new RuntimeException("VCellUtilityHub already started.");
    }

    public static void shutdown() throws Exception {
        logManager.close();
    }

    private VCellUtilityHub(MODE m){
        switch(m){
            case CLI:
                this.startVCellCLI();
                break;
            case STANDARD:
                this.startVCellStandard();
                break;
            default:
                throw new RuntimeException("No mode selected");         
        }
    }

    // Starters

    private void startVCellCLI(){
        VCellUtilityHub.logManager = CLILogManager.getInstance();
    }

    private void startVCellStandard(){
        VCellUtilityHub.logManager = VCellLogManager.getInstance();
    }

    // Getters

    public static LogManager getLogManager(){
        if (instance == null) throw new RuntimeException("VCellUtilityHub not started.");
        return VCellUtilityHub.logManager;
    }

}
