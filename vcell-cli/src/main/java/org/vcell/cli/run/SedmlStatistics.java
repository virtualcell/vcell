package org.vcell.cli.run;


/**
 * Class containing all the important info of a sedml execution:
 * - nModels :: the number of models
 * - nSimulations :: the number of simulations
 * - nTasks :: the number of tasks
 * - nOutputs :: the number of outputs
 * - nReportsCount :: the number of reports
 * - nPlots2DCount :: the number of 2D plots
 * - nPlots3DCount :: the number of 3D plots
 * - hasOverrides :: if there are math overrides
 * - hasScans :: if the sedml has parameter scans
 */
public class SedmlStatistics {
    private String sedmlName;
    private Integer nModels, nSimulations, nTasks, nOutputs, nReportsCount, nPlots2DCount, nPlots3DCount;
    public boolean hasOverrides, hasScans;

    public SedmlStatistics(String sedmlName){
        // -1 indicates the value has not been initialized.
        this.sedmlName = sedmlName;
        this.nModels = null;
        this.nSimulations = null;
        this.nTasks = null;
        this.nOutputs = null;
        this.nReportsCount = null;
        this.nPlots2DCount = null;
        this.nPlots3DCount = null;
        this.hasOverrides = false;
        this.hasScans = false;
    }

    public String getSedmlName(){
        return this.sedmlName;
    }

    public int getNumModels(){
        return this.nModels == null ? 0 : this.nModels;
    }

    public int getNumSimulations(){
        return this.nSimulations == null ? 0 : this.nSimulations;
    }

    public int getNumTasks(){
        return this.nTasks == null ? 0 : this.nTasks;
    }

    public int getNumOutputs(){
        return this.nOutputs == null ? 0 : this.nOutputs;
    }

    public int getReportsCount(){
        return this.nReportsCount == null ? 0 : this.nReportsCount;
    }

    public int getPlots2DCount(){
        return this.nPlots2DCount == null ? 0 : this.nPlots2DCount;
    } 

    public int getPlots3DCount(){
        return this.nPlots3DCount == null ? 0 : this.nPlots3DCount;
    }

    public boolean getHasOverrides(){
        return this.hasOverrides;
    }

    public boolean getHasScans(){
        return this.hasScans;
    }

    public void setNumModels(int nModels){
        this.nModels = nModels;
    }

    public void setNumSimulations(int nSimulations){
        this.nSimulations = nSimulations;
    }

    public void setNumTasks(int nTasks){
        this.nTasks = nTasks;
    }

    public void setNumOutputs(int nOutputs){
        this.nOutputs = nOutputs;
        // If we have outputs, we should de-null the sub-categories
        if (this.nReportsCount == null) this.setReportsCount(0);
        if (this.nPlots2DCount == null) this.setPlots2DCount(0);
        if (this.nPlots3DCount == null) this.setPlots3DCount(0);
    }

    public void setReportsCount(int nReportsCount){
        this.nReportsCount = nReportsCount;
    }

    public void setPlots2DCount(int nPlots2DCount){
        this.nPlots2DCount = nPlots2DCount;
    } 

    public void setPlots3DCount(int nPlots3DCount){
        this.nPlots3DCount = nPlots3DCount;
    }

    public void setHasOverrides(boolean hasOverrides){
        this.hasOverrides = hasOverrides;
    }

    public void setHasScans(boolean hasScans){
        this.hasScans = hasScans;
    }

    /**
     * A comma separated list of the contents for logging purposes
     * @returns the relevant info as a comma separated list or an error message
     */
    public String toFormattedString(){
        if (this.nModels == null && this.nSimulations == null && this.nTasks == null && this.nReportsCount == null
                && this.nPlots2DCount == null && this.nPlots3DCount == null) return "Processing incomplete; no reportable data.";
        return String.format(
            "\t> %d model%s\n\t> %d simulation%s\n\t> %d task%s\n\t> %d output%s\n\t\t>> %d report%s\n\t\t>> %d 2D plot%s\n\t\t>> %d 3D plot%s\n\t> %s Math Overrides\n\t> %s Parameter Scans",
            this.getNumModels(), this.nModels != 1 ? "s" : "",
            this.getNumSimulations(), this.nSimulations != 1 ? "s" : "",
            this.getNumTasks(), this.nTasks != 1 ? "s" : "",
            this.getNumOutputs(), this.nOutputs != 1 ? "s" : "",
            this.getReportsCount(), this.nReportsCount != 1 ? "s" : "",
            this.getPlots2DCount(), this.nPlots2DCount != 1 ? "s" : "",
            this.getPlots3DCount(), this.nPlots3DCount != 1 ? "s" : "",
            this.getHasOverrides() ? "Has" : "Does not have",
            this.getHasScans() ? "Has" : "Does not have"
        );
    }
}
