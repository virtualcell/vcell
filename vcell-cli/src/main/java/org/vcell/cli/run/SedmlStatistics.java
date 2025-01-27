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
    private final String sedmlName;
    private Integer nModels, nSimulations, nUTCSims, nOneStepSims, nSteadyStateSims, nAnalysisSims, nTasks, nOutputs, nReportsCount, nPlots2DCount, nPlots3DCount;
    public boolean hasOverrides, hasScans;

    public SedmlStatistics(String sedmlName){
        // -1 indicates the value has not been initialized.
        this.sedmlName = sedmlName;
        this.nModels = null;
        this.nSimulations = null;
        this.nUTCSims = null;
        this.nOneStepSims = null;
        this.nSteadyStateSims = null;
        this.nAnalysisSims = null;
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

    public int getNumUniformTimeCourseSimulations(){
        return this.nUTCSims == null ? 0 : this.nUTCSims;
    }

    public int getNumOneStepSimulations(){
        return this.nOneStepSims == null ? 0 : this.nOneStepSims;
    }

    public int getNumSteadyStateSimulations(){
        return this.nSteadyStateSims == null ? 0 : this.nSteadyStateSims;
    }

    public int getNumAnalysisSimulations(){
        return this.nAnalysisSims == null ? 0 : this.nAnalysisSims;
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

    public void setNumUniformTimeCourseSimulations(int nUTCSims){
        this.nUTCSims = nUTCSims;
    }

    public void setNumOneStepSimulations(int nOneStepSims){
        this.nOneStepSims = nOneStepSims;
    }

    public void setNumSteadyStateSimulations(int nSteadyStateSims){
        this.nSteadyStateSims = nSteadyStateSims;
    }

    public void setNumAnalysisSimulations(int nAnalysisSims){
        this.nAnalysisSims = nAnalysisSims;
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
        String simulationTypeFormatString = "\t\t> %d %s simulation%s\n";
        String utcSimTypeString = this.getNumUniformTimeCourseSimulations() == 0 ? "" : (String.format(simulationTypeFormatString, this.getNumUniformTimeCourseSimulations(), "Uniform Time Course", this.getNumUniformTimeCourseSimulations() != 1 ? "s" : ""));
        String osSimTypeString = this.getNumOneStepSimulations() == 0 ? "" : (String.format(simulationTypeFormatString, this.getNumOneStepSimulations(), "One Step", this.getNumOneStepSimulations() != 1 ? "s" : ""));
        String ssSimTypeString = this.getNumSteadyStateSimulations() == 0 ? "" : (String.format(simulationTypeFormatString, this.getNumSteadyStateSimulations(), "Steady State", this.getNumSteadyStateSimulations() != 1 ? "s" : ""));
        String anSimTypeString = this.getNumAnalysisSimulations() == 0 ? "" : (String.format(simulationTypeFormatString, this.getNumAnalysisSimulations(), "Analysis", this.getNumAnalysisSimulations() != 1 ? "s" : ""));
        String outputTypeFormatString = "\t\t> %d %s%s\n";
        String reportTypeString = this.getReportsCount() == 0 ? "" : (String.format(outputTypeFormatString, this.getReportsCount(), "Report", this.getReportsCount() != 1 ? "s" : ""));
        String plot2DTypeString = this.getPlots2DCount() == 0 ? "" : (String.format(outputTypeFormatString, this.getPlots2DCount(), "2D Plot", this.getPlots2DCount() != 1 ? "s" : ""));
        String plot3DTypeString = this.getPlots3DCount() == 0 ? "" : (String.format(outputTypeFormatString, this.getPlots3DCount(), "3D Plot", this.getPlots3DCount() != 1 ? "s" : ""));
        return String.format(
            "\t> %d model%s\n\t> %d simulation%s\n%s%s%s%s\t> %d task%s\n\t> %d output%s\n%s%s%s\t> %s Math Overrides\n\t> %s Parameter Scans",
            this.getNumModels(), this.getNumModels() != 1 ? "s" : "",
            this.getNumSimulations(), this.getNumSimulations() != 1 ? "s" : "",
            utcSimTypeString,
            osSimTypeString,
            ssSimTypeString,
            anSimTypeString,
            this.getNumTasks(), this.getNumTasks() != 1 ? "s" : "",
            this.getNumOutputs(), this.getNumOutputs() != 1 ? "s" : "",
            reportTypeString,
            plot2DTypeString,
            plot3DTypeString,
            this.getHasOverrides() ? "Has" : "Does not have",
            this.getHasScans() ? "Has" : "Does not have"
        );
    }
}
