package org.vcell.cli.run.plotting;

import cbit.vcell.publish.PDFWriter;

import com.lowagie.text.DocumentException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.sedml.log.BiosimulationLog;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Stores all relevant info to create a 2D plot, and lazily builds a PDF on request
 */
public class Results2DLinePlot implements ResultsLinePlot {

    public static final int AXIS_LABEL_FONT_SIZE = 15;
    public static final int MAX_SERIES_DATA_POINT_LABELS = 10;
    public static final int SERIES_DATA_POINT_LABEL_FONT_SIZE = 8;

    private static final Logger lg = LogManager.getLogger(Results2DLinePlot.class);

    private String plotTitle, xAxisTitle;
    private final Map<SingleAxisSeries, Set<SingleAxisSeries>> dataSetMappings;
    private final Set<String> xLabels, yLabels;
    private int largestSeriesSize;

    public Results2DLinePlot(){
        this("", "");
    }

    public Results2DLinePlot(String plotTitle, String xAxisTitle){
        this.plotTitle = plotTitle;
        this.xAxisTitle = xAxisTitle;
        this.dataSetMappings = new LinkedHashMap<>();
        this.xLabels = new HashSet<>();
        this.yLabels = new HashSet<>();
        this.largestSeriesSize = 0;
    }

    @Override
    public void setTitle(String newTitle) {
        this.plotTitle = newTitle;
    }

    @Override
    public String getTitle() {
        return this.plotTitle;
    }

    public void setXAxisTitle(String newTitle) {
        this.xAxisTitle = newTitle;
    }

    public String getXAxisTitle() {
        return this.xAxisTitle;
    }

    public int getLargestSeriesSize(){
        return this.largestSeriesSize;
    }

    /**
     * Adds an XY pairing to this plot; will not duplicate pairings.
     * @param xData the x-axis of data
     * @param yData the y-axis of data
     * @throws IllegalArgumentException if the length of data on each axis doesn't match
     */
    public void addXYData(SingleAxisSeries xData, SingleAxisSeries yData){
        if (xData == null) throw new IllegalArgumentException("Parameter `xData` can not be null!");
        if (yData == null) throw new IllegalArgumentException("Parameter `yData` can not be null!");
        if (xData.data().size() != yData.data().size()) throw new IllegalArgumentException("Data lengths do not match!");
        if (this.xLabels.contains(xData.label()) && !this.dataSetMappings.containsKey(xData))
            throw new IllegalArgumentException("plot already has data for x-axis with the label`" + xData.label() + "` (but it has different values) ");
        if (this.yLabels.contains(yData.label())) throw new IllegalArgumentException("plot already has data for y-axis `" + yData.label() + "`");
        if (!this.dataSetMappings.containsKey(xData)) this.dataSetMappings.put(xData, new LinkedHashSet<>());
        this.dataSetMappings.get(xData).add(yData);
        this.xLabels.add(xData.label());
        this.yLabels.add(yData.label());
        if (this.largestSeriesSize < xData.data().size()) this.largestSeriesSize = xData.data().size();
    }

    /**
     * Replaces the x-axis data associated with a certain label, with a newly provided set.
     * @param axisLabel the label of the data you want to change
     * @param axisData the data to replace with
     * @throws IllegalArgumentException if changing the length of the data would create an illegal pairing.
     */
    public void changeXAxisData(String axisLabel, double[] axisData){
        if (axisData == null) throw new IllegalArgumentException("Parameter `axisData` may not be null!");
        this.changeXAxisData(axisLabel, Arrays.stream(axisData).boxed().toList());
    }

    /**
     * Replaces the x-axis data associated with a certain label, with a newly provided set.
     * @param axisLabel the label of the data you want to change
     * @param axisData the data to replace with
     * @throws IllegalArgumentException if changing the length of the data would create an illegal pairing.
     */
    public void changeXAxisData(String axisLabel, List<Double> axisData){
        this.changeXAxisData(new SingleAxisSeries(axisLabel, axisData));
    }

    /**
     * Replaces the x-axis data associated with a certain label, with a newly provided set.
     * @param axis the axis to replace with; matches by label.
     * @throws IllegalArgumentException if changing the length of the data would create an illegal pairing.
     */
    public void changeXAxisData(SingleAxisSeries axis){
        SingleAxisSeries axisToChange;
        if (null == (axisToChange = this.getXAxisSeries(axis.label()))) throw new IllegalArgumentException("Axis with label `" +  axis.label() + "` does not exist!");
        if (axis.data().size() != axisToChange.data().size()) throw new IllegalArgumentException("Data lengths do not match!");
        Set<SingleAxisSeries> yAxes = this.dataSetMappings.remove(axisToChange);
        this.dataSetMappings.put(axis, yAxes);
    }

    /**
     * Removes an XY paring to this plot, if it exists
     * <br/>
     * WARNING: Do not provide this method with freshly constructed series; `SingleAxisSeries` hashes by object_id for efficiency.
     * <br/>
     * See {@link SingleAxisSeries} for further details
     * @return true if the mapping formerly existed, else false
     */
    public boolean removeXYData(SingleAxisSeries xSeries, SingleAxisSeries ySeries){
        if (!this.dataSetMappings.containsKey(xSeries) || !this.dataSetMappings.get(xSeries).contains(ySeries)) return false;
        this.dataSetMappings.get(xSeries).remove(ySeries);
        if (this.dataSetMappings.get(xSeries).isEmpty()) this.dataSetMappings.remove(xSeries);
        this.xLabels.remove(xSeries.label());
        this.xLabels.remove(ySeries.label());
        this.largestSeriesSize = this.findLargestSeriesSize();
        return true;
    }

    /**
     * Removes an XY paring to this plot, if it exists
     * <br/>
     * WARNING: this method must use iteration, and is the medium speed of it's counterparts.
     * @param xSeries the x-series to find and remove
     * @param yLabel the label of the y-series to find and remove
     * @return true if the mapping formerly existed, else false
     */
    public boolean removeXYData(SingleAxisSeries xSeries, String yLabel){
        if (yLabel == null) throw new IllegalArgumentException("Parameter `yLabel` can not be null!");
        if (!this.dataSetMappings.containsKey(xSeries)) return false;
        for (SingleAxisSeries ySeries : this.dataSetMappings.get(xSeries)){
            if (!yLabel.equals(ySeries.label())) continue;
            return this.removeXYData(xSeries, ySeries);
        }
        return false;
    }

    /**
     * Removes an XY paring to this plot, if it exists
     * <br/>
     * WARNING: this method can not utilize hashing, and is the slowest of its counterparts!
     * @param xLabel the label of the x-series to find and remove
     * @param yLabel the label of the y-series to find and remove
     * @return true if the mapping formerly existed, else false
     */
    public boolean removeXYData(String xLabel, String yLabel){
        SingleAxisSeries xSeries;
        if (null == (xSeries = this.getXAxisSeries(xLabel))) return false;
        return this.removeXYData(xSeries, yLabel);
    }

    /**
     * Gets the x-axis series with the provided label.
     * @param label the x-axis series to find
     * @return null if no axis series could be found, else the desired series
     */
    public SingleAxisSeries getXAxisSeries(String label){
        for (SingleAxisSeries xSeries : this.dataSetMappings.keySet()){
            if (!xSeries.label().equals(label)) continue;
            return xSeries;
        }
        return null;
    }

    /**
     * Gets the y-axis series with the provided label.
     * @param label the y-axis series to find
     * @return null if no axis series could be found, else the desired series
     */
    public SingleAxisSeries getYAxisSeries(String label){
        for (SingleAxisSeries xSeries : this.dataSetMappings.keySet()){
            for (SingleAxisSeries ySeries : this.dataSetMappings.get(xSeries)){
                if (!ySeries.label().equals(label)) continue;
                return ySeries;
            }
        }
        return null;
    }

    public int getNumXSeries(){
        return this.dataSetMappings.keySet().size();
    }

    public int getNumYSeries(){
        int sum = 0;
        for (SingleAxisSeries xAxis : this.dataSetMappings.keySet()){
            sum += this.getNumYSeries(xAxis);
        }
        return sum;
    }

    public int getNumYSeries(SingleAxisSeries xData){
        if (!this.dataSetMappings.containsKey(xData)) return 0;
        return this.dataSetMappings.get(xData).size();
    }

    public void generatePng(String desiredFileName, File desiredParentDirectory) throws ChartCouldNotBeProducedException {
        JFreeChart chart = this.createChart();
        PageFormat pageFormat = Results2DLinePlot.generateAlternatePageFormat();

        File testfile = new File(desiredParentDirectory, desiredFileName);
        try {
            if (testfile.exists() && !testfile.isFile()) throw new IllegalArgumentException("desired PDF already exists, and is not a regular file");
            if (!testfile.exists() && !testfile.createNewFile()) throw new IllegalArgumentException("Unable to create desired PDF; creation itself failed.");
            BufferedImage bfi = chart.createBufferedImage((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
            ImageIO.write(bfi, "png", testfile);
        } catch (IOException e){
            lg.error("Error while preparing PNG; see exception below");
            throw new ChartCouldNotBeProducedException(e);
        }
    }


    @Override
    public void generatePdf(String desiredFileName, File desiredParentDirectory) throws ChartCouldNotBeProducedException {
        JFreeChart chart = this.createChart();

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
            }
        } catch (DocumentException | IOException e){
            lg.error("Error while preparing PDF; see exception below");
            throw new ChartCouldNotBeProducedException(e);
        }
    }

    private XYDataset generateChartLibraryDataset(){
        XYSeriesCollection dataset2D = new XYSeriesCollection();
        for (SingleAxisSeries xAxis : this.dataSetMappings.keySet()){
            for (SingleAxisSeries yAxis : this.dataSetMappings.get(xAxis)){
                XYSeries xySeries = new XYSeries(yAxis.label(), true, false);
                for (int i = 0; i < yAxis.data().size(); i++){
                    xySeries.add(xAxis.data().get(i), yAxis.data().get(i));
                }
                dataset2D.addSeries(xySeries);
            }
        }
        return dataset2D;
    }

    private JFreeChart createChart(){
        String yAxisLabel = "";
        XYDataset dataset2D = this.generateChartLibraryDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(this.plotTitle, this.xAxisTitle, yAxisLabel, dataset2D);

        // Tweak Chart so it looks better
        chart.setBorderVisible(true);
        chart.getPlot().setBackgroundPaint(Color.white);
        XYPlot chartPlot = chart.getXYPlot();
        chartPlot.getDomainAxis().setLabelFont(new Font(this.xAxisTitle, Font.PLAIN, Results2DLinePlot.AXIS_LABEL_FONT_SIZE));
        chartPlot.getRangeAxis().setLabelFont(new Font(yAxisLabel, Font.PLAIN, Results2DLinePlot.AXIS_LABEL_FONT_SIZE));
        if (this.largestSeriesSize <= Results2DLinePlot.MAX_SERIES_DATA_POINT_LABELS) { // if it's too crowded, having data point labels is bad
            for (int i = 0; i < dataset2D.getSeriesCount(); i++) {
                //DecimalFormat decimalformat1 = new DecimalFormat("##"); // Should we ever need it, this object is used in formating the labels.
                chartPlot.getRenderer().setSeriesItemLabelGenerator(i, new StandardXYItemLabelGenerator("({1}, {2})"));
                chartPlot.getRenderer().setSeriesItemLabelFont(i, new Font(null, Font.PLAIN, Results2DLinePlot.SERIES_DATA_POINT_LABEL_FONT_SIZE));
                chartPlot.getRenderer().setSeriesItemLabelsVisible(i, true);
            }
        }
        return chart;
    }

    private int findLargestSeriesSize(){
        int newMax = 0;
        for (SingleAxisSeries xAxis : this.dataSetMappings.keySet()){
            if (newMax < xAxis.data().size()) newMax = xAxis.data().size();
        }
        return newMax;
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
