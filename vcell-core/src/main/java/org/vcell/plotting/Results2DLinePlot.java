package org.vcell.plotting;

import java.io.File;

/**
 * Stores all relevant info to create a 2D plot, and lazily builds a PDF on request
 */
public class Results2DLinePlot implements ResultsLinePlot {
    private String plotTitle;
    private double[] xData, yData;
    private String xLabel, yLabel;

    public Results2DLinePlot(){
        this.plotTitle = "";
        this.xData = new double[0];
        this.yData = new double[0];
        this.xLabel = "";
        this.yLabel = "";
    }

    @Override
    public void setTitle(String newTitle) {
        this.plotTitle = newTitle;
    }

    @Override
    public String getTitle() {
        return this.plotTitle;
    }

    public void setXData(double[] newXData){
        this.xData = newXData;
    }

    public double[] getXData(){
        return this.xData;
    }

    public void setYData(double[] newYData){
        this.yData = newYData;
    }

    public double[] getYData(){
        return this.yData;
    }

    public void setXLabel(String newXLabel){
        this.xLabel = newXLabel;
    }

    public String getXLabel(){
        return this.xLabel;
    }

    public void setYLabel(String newYLabel){
        this.yLabel = newYLabel;
    }

    public String getYLabel(){
        return this.yLabel;
    }

    @Override
    public void generatePdf(String desiredFileName, File desiredParentDirectory) {

    }
}
