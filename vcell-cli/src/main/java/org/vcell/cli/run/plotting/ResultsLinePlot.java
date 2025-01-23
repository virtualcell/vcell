package org.vcell.cli.run.plotting;

import java.io.File;

/**
 * Describes the basic functionality all LinePlots displaying simulation results should have.
 */
public interface ResultsLinePlot {

    /**
     * Generates a PDF copy of the current plot state, and exports it as PDF at the desired location.
     * @param desiredFileName name of the plot file; needs no file suffix: ".pdf" will be appended.
     * @param desiredParentDirectory directory to place the new pdf into.
     */
    void generatePdf(String desiredFileName, File desiredParentDirectory);

    /**
     * Setter for the title of the plot
     * @param newTitle the desired new title as a String
     */
    void setTitle(String newTitle);

    /**
     * Getter for the title of the plot
     * @return the title as a String
     */
    String getTitle();

}
