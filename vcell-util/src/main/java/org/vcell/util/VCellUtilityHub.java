package org.vcell.util;

import org.vcell.util.recording.CLIRecordManager;
import org.vcell.util.recording.RecordManager;
import org.vcell.util.recording.VCellRecordManager;

/**
 * Class to hold all singletons and resources needed across VCell.
 * 
 * @since VCell 7.4.0.62
 */
public class VCellUtilityHub {

    /*
     * For each way we want to initialize VCell:
     * - Add a option to MODE
     * - Add a startVCell<MODE name> method
     * - Add the new start method to the private constructor
     * - (optional) Add relevant closing calls to shutdown  
     */

    /**
     * Class that contains all the modes VCell can start in.
     */
    public enum MODE {
        CLI,STANDARD
    }


    private static VCellUtilityHub instance;

    // Utility declarations
    private static RecordManager logManager;

    // Methods
    /**
     * Starts {@code VCellUtilityHub} using the provided {@code VCellUtilityHub.MODE} as a blueprint for how to construct the underlying singletons and resources.
     * @param mode the mode in which VCell was launched.
     */
    public static void startup(MODE mode){
        if (instance == null) instance = new VCellUtilityHub(mode);
        else throw new RuntimeException("VCellUtilityHub already started.");
    }

    /**
     * Closes all underlying singletons and resources.
     * 
     * @throws Exception if closing anything produced errors
     */
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
        VCellUtilityHub.logManager = CLIRecordManager.getInstance();
    }

    private void startVCellStandard(){
        VCellUtilityHub.logManager = VCellRecordManager.getInstance();
    }

    // Getters
    /** 
     * Method to get the singleton instance of {@code LogManager} selected for the requested mode of VCell
     * 
     * @return The {@code LogManager} for current execution of VCell
     */
    public static RecordManager getLogManager(){
        if (instance == null) {
            // Attempt to auto-start in standard mode
            try {
                VCellUtilityHub.startup(MODE.STANDARD);
            } catch (Exception e){
                throw new RuntimeException("VCellUtilityHub not started, and unable to be started.", e);
            }
        } 
        return VCellUtilityHub.logManager;
    }
}
