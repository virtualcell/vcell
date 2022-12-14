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
    public int nModels, nSimulations, nTasks, nOutputs, nReportsCount, nPlots2DCount, nPlots3DCount;
    public boolean hasOverrides, hasScans;

    public SedmlStatistics(){
        this.nModels = -1;
        this.nSimulations = -1;
        this.nTasks = -1;
        this.nOutputs = -1;
        this.nReportsCount = -1;
        this.nPlots2DCount = -1;
        this.nPlots3DCount = -1;
        this.hasOverrides = false;
        this.hasScans = false;
    }

    /**
     * A comma separated list of the contents for logging purposes
     * @returns the relevant info as a comma separated list or an error message
     */
    @Override
    public String toString(){
        if (this.nModels < 0 && this.nModels < 0 && this.nModels < 0 && this.nModels < 0 && 
            this.nModels < 0 && this.nModels < 0 && this.nModels < 0) return "Processing incomplete; no reportable data.";
        return String.format(
            "%d model%s, %d simulation%s, %d task%s, %d output%s, %d report%s, %d 2D plot%s, %d 3D plot%s, %s Math Overrides, %s Paramerter Scans", 
            this.nModels < 0 ? 0 : this.nModels             , this.nModels != 1 ? "s" : "",
            this.nSimulations < 0 ? 0 : this.nSimulations   , this.nSimulations != 1 ? "s" : "",
            this.nTasks < 0 ? 0 : this.nTasks               , this.nTasks != 1 ? "s" : "",
            this.nOutputs < 0 ? 0 : this.nOutputs           , this.nOutputs != 1 ? "s" : "",
            this.nReportsCount < 0 ? 0 : this.nReportsCount , this.nReportsCount != 1 ? "s" : "",
            this.nPlots2DCount < 0 ? 0 : this.nPlots2DCount , this.nPlots2DCount != 1 ? "s" : "",
            this.nPlots3DCount < 0 ? 0 : this.nPlots3DCount , this.nPlots3DCount != 1 ? "s" : "",
            this.hasOverrides? "Has" : "Does not have",
            this.hasScans ? "Has" : "Does not have"
        );
    }
}
