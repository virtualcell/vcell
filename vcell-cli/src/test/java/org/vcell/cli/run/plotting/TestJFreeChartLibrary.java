package org.vcell.cli.run.plotting;

import cbit.vcell.publish.PDFWriter;

import com.lowagie.text.DocumentException;

import org.jfree.chart.*;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.vcell.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Tag("Fast")
public class TestJFreeChartLibrary {

    private static List<XYDataItem> testData = List.of(
        new XYDataItem(0.0, 0.0),
        new XYDataItem(0.5, 0.25),
        new XYDataItem(1.0, 1.0),
        new XYDataItem(1.5, 2.25),
        new XYDataItem(2.0, 4.0),
        new XYDataItem(2.5, 6.25),
        new XYDataItem(3.0, 9.0),
        new XYDataItem(3.5, 12.25),
        new XYDataItem(4.0, 16.0),
        new XYDataItem(4.5, 20.25),
        new XYDataItem(5.0, 25.0),
        new XYDataItem(5.5, 30.25)
    );

    @Test
    public void testDirectChartCreation() throws IOException, DocumentException {
        PageFormat pageFormat = TestJFreeChartLibrary.generateAlternatePageFormat();

        // Build Chart
        XYSeries xySeries = new XYSeries("Ka+", true, false); // The "key" field is what to name the curve itself, when shown in a Legend
        for (XYDataItem valuePair: TestJFreeChartLibrary.testData){
            xySeries.add(valuePair);
        }
        XYDataset dataset2D = new XYSeriesCollection(xySeries);
        this.generateChart(dataset2D, pageFormat, "Time");
    }

    @Test
    public void testDirectChartCreationFromCSV() throws IOException, DocumentException {
        PageFormat pageFormat = TestJFreeChartLibrary.generateAlternatePageFormat();
        Pair<String, XYDataset> datasetPair = TestJFreeChartLibrary.csvToXYDataset();
        this.generateChart(datasetPair.two, pageFormat, datasetPair.one);
    }

    private void generateChart(XYDataset dataset2D, PageFormat pageFormat, String xAxisLabel) throws IOException, DocumentException {
        String yAxisLabel = "";
        JFreeChart chart = ChartFactory.createXYLineChart("Test Sim Results", xAxisLabel, yAxisLabel, dataset2D);

        // Tweak Chart so it looks better
        chart.setBorderVisible(true);
        chart.getPlot().setBackgroundPaint(Color.white);
        XYPlot chartPlot = chart.getXYPlot();
        chartPlot.getDomainAxis().setLabelFont(new Font(xAxisLabel, Font.PLAIN, Results2DLinePlot.AXIS_LABEL_FONT_SIZE));
        chartPlot.getRangeAxis().setLabelFont(new Font(yAxisLabel, Font.PLAIN, Results2DLinePlot.AXIS_LABEL_FONT_SIZE));
        if (dataset2D.getItemCount(0) <= Results2DLinePlot.MAX_SERIES_DATA_POINT_LABELS) { // if it's too crowded, having data point labels is bad
            for (int i = 0; i < dataset2D.getSeriesCount(); i++){
                //DecimalFormat decimalformat1 = new DecimalFormat("##");
                chartPlot.getRenderer().setSeriesItemLabelGenerator(i, new StandardXYItemLabelGenerator("({1}, {2})"));
                chartPlot.getRenderer().setSeriesItemLabelFont(i, new Font(null, Font.PLAIN, Results2DLinePlot.SERIES_DATA_POINT_LABEL_FONT_SIZE));
                chartPlot.getRenderer().setSeriesItemLabelsVisible(i, true);
            }
        }

        // Prepare for export
        PDFWriter pdfWriter = new PDFWriter();

        File testfile = File.createTempFile("VCell::TestJFreeChartLibrary::testDirectChartCreation::", ".pdf");
        if (!testfile.exists()) throw new IllegalArgumentException("Unable to create the testfile, somehow.");
        try (FileOutputStream fos = new FileOutputStream(testfile)){
            BufferedImage bfi = chart.createBufferedImage((int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());
            pdfWriter.writePlotImageDocument("Test Document", fos, pageFormat, bfi);
        }
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

    private static Pair<String, XYDataset> csvToXYDataset() throws IOException {
        String CSV_DATA_FILE_LOCAL_PATH = "plot2d_SimpleSimulation.csv";
        InputStream inputStream = TestJFreeChartLibrary.class.getResourceAsStream(CSV_DATA_FILE_LOCAL_PATH);
        if (inputStream == null)
            throw new FileNotFoundException(String.format("can not find `%s`; maybe it moved?", CSV_DATA_FILE_LOCAL_PATH));
        XYSeriesCollection dataset2D = new XYSeriesCollection();
        List<String> linesOfData;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){
            linesOfData = br.lines().toList();
        }
        String[] indepDataParts = linesOfData.get(linesOfData.size() - 1).split(",");
        List<Double> xSeriesData = new ArrayList<>();
        for (int partNum = 3; partNum < indepDataParts.length; partNum++){ // Skip first three; no need for them
            xSeriesData.add(Double.parseDouble(indepDataParts[partNum]));
        }

        for (int lineNum = 0; lineNum < linesOfData.size() - 1; lineNum++){ // Skip last line; that's our independent-var data
            XYSeries series = getXySeries(linesOfData, lineNum, xSeriesData);
            dataset2D.addSeries(series);
        }

        return new Pair<>(indepDataParts[2], dataset2D);
    }

    private static XYSeries getXySeries(List<String> linesOfData, int lineNum, List<Double> xSeriesData) {
        String[] parts = linesOfData.get(lineNum).split(",");
        String ySeriesName = parts[2];
        XYSeries series = new XYSeries(ySeriesName, true);
        if (parts.length - 3 != xSeriesData.size()) throw new RuntimeException("Mismatched data length!");
        for (int partNum = 3; partNum < parts.length; partNum++){ // Skip first two; third we've already grabbed
            double xData = xSeriesData.get(partNum - 3), yData = Double.parseDouble(parts[partNum]);
            series.add(xData, yData);
        }
        return series;
    }
}
