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
    private Integer nModels, nSimulations, nTasks, nOutputs, nReportsCount, nPlots2DCount, nPlots3DCount;
    public boolean hasOverrides, hasScans;

    public SedmlStatistics(){
        // -1 indicates the value has not been initalized. 
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

    public int getNumModels(){
        return this.nModels == null ? 0 : this.nModels;
    }

    public int getNumSimultions(){
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

    public int getPlots3Dcount(){
        return this.nPlots3DCount == null ? 0 : this.nPlots3DCount;
    }

    public boolean getHasOverrides(){
        return this.hasOverrides;
    }

    public boolean getHasScans(){
        return this.hasScans;
    }

    public void setNumModels(int nModels){
        if (this.nModels == null) this.nModels = 0;
        this.nModels = nModels;
    }

    public void setNumSimultions(int nSimulations){
        if (this.nSimulations == null) this.nSimulations = 0;
        this.nSimulations = nSimulations;
    }

    public void setNumTasks(int nTasks){
        if (this.nTasks == null) this.nTasks = 0;
        this.nTasks = nTasks;
    }

    public void setNumOutputs(int nOutputs){
        if (this.nOutputs == null) this.nOutputs = 0;
        this.nOutputs = nOutputs;
    }

    public void setReportsCount(int nReportsCount){
        if (this.nReportsCount == null) this.nReportsCount = 0;
        this.nReportsCount = nReportsCount;
    }

    public void setPlots2DCount(int nPlots2DCount){
        if (this.nPlots2DCount == null) this.nPlots2DCount = 0;
        this.nPlots2DCount = nPlots2DCount;
    } 

    public void setPlots3Dcount(int nPlots3DCount){
        if (this.nPlots3DCount == null) this.nPlots3DCount = 0;
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
        if (this.nModels == null && this.nSimulations == null && this.nTasks == null && this.nModels == null && 
            this.nModels == null && this.nModels == null && this.nModels == null) return "Processing incomplete; no reportable data.";
        return String.format(
            "%d model%s, %d simulation%s, %d task%s, %d output%s, %d report%s, %d 2D plot%s, %d 3D plot%s, %s Math Overrides, %s Paramerter Scans", 
            this.getNumModels(), this.nModels != 1 ? "s" : "",
            this.getNumSimultions(), this.nSimulations != 1 ? "s" : "",
            this.getNumTasks(), this.nTasks != 1 ? "s" : "",
            this.getNumOutputs(), this.nOutputs != 1 ? "s" : "",
            this.getReportsCount(), this.nReportsCount != 1 ? "s" : "",
            this.getPlots2DCount(), this.nPlots2DCount != 1 ? "s" : "",
            this.getPlots3Dcount(), this.nPlots3DCount != 1 ? "s" : "",
            this.getHasOverrides() ? "Has" : "Does not have",
            this.getHasScans() ? "Has" : "Does not have"
        );
    }
}
