package org.vcell.plotting;

import cbit.vcell.publish.ITextWriter;
import cbit.vcell.publish.PDFWriter;

import com.lowagie.text.DocumentException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.vcell.util.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Stores all relevant info to create a 2D plot, and lazily builds a PDF on request
 */
public class Results2DLinePlot implements ResultsLinePlot {

    public static final int AXIS_LABEL_FONT_SIZE = 15;
    public static final int MAX_SERIES_DATA_POINT_LABELS = 10;
    public static final int SERIES_DATA_POINT_LABEL_FONT_SIZE = 8;

    private static final Logger lg = LogManager.getLogger(Results2DLinePlot.class);

    private String plotTitle;
    private Pair<String, double[]> xData;
    private final Map<String, double[]> yDataSets;

    public Results2DLinePlot(){
        this("");
    }

    public Results2DLinePlot(String plotTitle){
        this(plotTitle, "", new double[0]);
    }

    public Results2DLinePlot(String xLabel, Double[] xDataValues){
        this("", xLabel, xDataValues);
    }

    public Results2DLinePlot(String xLabel, double[] xDataValues){
        this("", xLabel, xDataValues);
    }

    public Results2DLinePlot(String plotTitle, String xLabel, Double[] xDataValues){
        this(plotTitle, xLabel, Stream.of(xDataValues).mapToDouble(Double::doubleValue).toArray());
    }

    public Results2DLinePlot(String plotTitle, String xLabel, double[] xDataValues){
        this.plotTitle = plotTitle;
        this.xData = new Pair<>(xLabel, xDataValues);
        this.yDataSets = new HashMap<>();
    }

    @Override
    public void setTitle(String newTitle) {
        this.plotTitle = newTitle;
    }

    @Override
    public String getTitle() {
        return this.plotTitle;
    }

    public void setXLabel(String newXLabel){
        this.xData = new Pair<>(newXLabel, this.xData.two);
    }

    public void setXData(Double[] newXData){
        this.setXData(Stream.of(newXData).mapToDouble(Double::doubleValue).toArray());
    }

    public void setXData(double[] newXData){
        this.setXData(this.xData.one, newXData);
    }

    public void setXData(String newXLabel, double[] newXData){
        if (this.xData.two.length != newXData.length){
            lg.warn("Changing xData to different length! YData will be purged!");
            this.yDataSets.clear();
        }
        this.xData = new Pair<>(newXLabel, newXData);
    }

    public String getXLabel(){
        return this.xData.one;
    }

    public double[] getXDataValues(){
        return this.xData.two;
    }

    public Pair<String, double[]> getXData(){
        return this.xData;
    }

    public void setYData(String yLabel, double[] newYData){
        if (this.xData.two.length != newYData.length){
            lg.error("Error adding dataset `{}`; see exception below", yLabel);
            String exceptionMessage = String.format("Can not accept yDataSet: size (%d) does not map with domain size (%d)", newYData.length, this.xData.two.length);
            throw new IllegalArgumentException(exceptionMessage);
        }
        this.yDataSets.put(yLabel, newYData);
    }

    public int getNumYDataSets(){
        return this.yDataSets.size();
    }

    public double[] getYData(String yLabel){
        if (this.yDataSets.containsKey(yLabel)) return this.yDataSets.get(yLabel);
        throw new IllegalArgumentException(String.format("`%s` is not a known dataset in this object", yLabel));
    }

    public Set<String> getYDataSetLabels(){
        return this.yDataSets.keySet();
    }


    @Override
    public void generatePdf(String desiredFileName, File desiredParentDirectory) throws ChartCouldNotBeProducedException {
        String yAxisLabel = "";
        XYDataset dataset2D = this.generateChartLibraryDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("Test Sim Results", this.xData.one, yAxisLabel, dataset2D);

        // Tweak Chart so it looks better
        chart.setBorderVisible(true);
        chart.getPlot().setBackgroundPaint(Color.white);
        XYPlot chartPlot = chart.getXYPlot();
        chartPlot.getDomainAxis().setLabelFont(new Font(this.xData.one, Font.PLAIN, Results2DLinePlot.AXIS_LABEL_FONT_SIZE));
        chartPlot.getRangeAxis().setLabelFont(new Font(yAxisLabel, Font.PLAIN, Results2DLinePlot.AXIS_LABEL_FONT_SIZE));
        if (this.xData.two.length <= Results2DLinePlot.MAX_SERIES_DATA_POINT_LABELS) { // if it's too crowded, having data point labels is bad
            for (int i = 0; i < dataset2D.getSeriesCount(); i++) {
                //DecimalFormat decimalformat1 = new DecimalFormat("##"); // Should we ever need it, this object is used in formating the labels.
                chartPlot.getRenderer().setSeriesItemLabelGenerator(i, new StandardXYItemLabelGenerator("({1}, {2})"));
                chartPlot.getRenderer().setSeriesItemLabelFont(i, new Font(null, Font.PLAIN, Results2DLinePlot.SERIES_DATA_POINT_LABEL_FONT_SIZE));
                chartPlot.getRenderer().setSeriesItemLabelsVisible(i, true);
            }
        }

        // Prepare for export
        PDFWriter pdfWriter = new PDFWriter();
        PageFormat pageFormat = Results2DLinePlot.generateAlternatePageFormat();

        File testfile = new File(desiredParentDirectory, desiredFileName);
        try {
            if (testfile.exists() && !testfile.isFile()) throw new IllegalArgumentException("desired PDF already exists, and is not a regular file");
            if (!testfile.exists() && !testfile.createNewFile()) throw new IllegalArgumentException("Unable to create desired PDF; creation itself failed.");
            try (FileOutputStream fos = new FileOutputStream(testfile)) {
                BufferedImage bfi = chart.createBufferedImage((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
                pdfWriter.writePlotImageDocument("Test Document", fos, pageFormat, bfi);
            } catch (DocumentException e) {
                lg.error("Error while building PDF; see exception below");
                throw new ChartCouldNotBeProducedException(e);
            }
        } catch (IOException e){
            lg.error("Error while preparing PDF; see exception below");
            throw new ChartCouldNotBeProducedException(e);
        }
    }

    private XYDataset generateChartLibraryDataset(){
        XYSeriesCollection dataset2D = new XYSeriesCollection();
        for (String yLabel : this.yDataSets.keySet()){
            double[] yDataValues = this.yDataSets.get(yLabel);
            XYSeries series = new XYSeries(yLabel, true, false);
            for (int i = 0; i < this.xData.two.length; i++){ // our methods have guaranteed the sizes match!
                series.add(this.xData.two[i], yDataValues[i]);
            }
            dataset2D.addSeries(series);
        }
        return dataset2D;
    }

    private static PageFormat generateAlternatePageFormat(){
        java.awt.print.PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
        Paper alternatePaper = new Paper(); // We want to try and increase the margins
        double altOriginX = alternatePaper.getImageableX() / 2, altOriginY = alternatePaper.getImageableY() / 2;
        double altWidth = alternatePaper.getWidth() - 2 * altOriginX, altHeight = alternatePaper.getHeight() - 2 * altOriginY;
        alternatePaper.setImageableArea(altOriginX, altOriginY, altWidth, altHeight);
        pageFormat.setPaper(alternatePaper);
        pageFormat.setOrientation(PageFormat.LANDSCAPE);
        return pageFormat;
    }
}
