package org.vcell.util;

import org.vcell.util.recording.CLIRecordManager;
import org.vcell.util.recording.RecordManager;
import org.vcell.util.recording.VCellRecordManager;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to hold all singletons and resources needed across VCell.
 * 
 * @since VCell 7.4.0.62
 */
public class VCellUtilityHub {

    private final static Logger logger = LogManager.getLogger(VCellUtilityHub.class);

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
    private static VCellRecordManager logManager;

    // Methods
    /**
     * Starts {@code VCellUtilityHub} using the provided {@code VCellUtilityHub.MODE} as a blueprint for how to construct the underlying singletons and resources.
     * @param mode the mode in which VCell was launched.
     */
    public static void startup(MODE mode){
        if (VCellUtilityHub.instance == null) VCellUtilityHub.instance = new VCellUtilityHub(mode);
        else {
            logger.warn("Restarting VCellUtilityHub...");
            VCellUtilityHub.shutdown();
            VCellUtilityHub.instance = null;
            VCellUtilityHub.startup(mode);
        };
    }

    /**
     * Closes all underlying singletons and resources.
     * 
     * @throws Exception if closing anything produced errors
     */
    public static void shutdown() {
        try {
            logManager.close();
        } catch (IOException e){
            String message = "Log Manager could not close.";
            logger.error(message);
            throw new RuntimeException(message, e);
        }
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
        System.setProperty("java.awt.headless", "true");
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
    public static VCellRecordManager getLogManager(){
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
