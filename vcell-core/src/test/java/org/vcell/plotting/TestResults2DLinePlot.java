package org.vcell.plotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.vcell.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Tag("Fast")
public class TestResults2DLinePlot {
    private static List<XYDataItem> paraData = List.of(
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

    private static Pair<double[], double[]> parabolicData = new Pair<>(
            Stream.of(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5).mapToDouble(Double::valueOf).toArray(),
            Stream.of(0.0, 0.25, 1.0, 2.25, 4.0, 6.25, 9.0, 12.25, 16.0, 20.25, 25.0, 30.25).mapToDouble(Double::valueOf).toArray()
    );

    @Test
    public void testConstructors(){
        double[] xValuesPrim = {0.0, 2.0, 4.0};
        Double[] xValuesWrap = {0.0, 2.0, 4.0};
        Results2DLinePlot[] testInstances = {
                new Results2DLinePlot(),
                new Results2DLinePlot("Title"),
                new Results2DLinePlot("Label", xValuesWrap),
                new Results2DLinePlot("Label", xValuesPrim),
                new Results2DLinePlot("Title", "Label", xValuesWrap),
                new Results2DLinePlot("Title", "Label", xValuesPrim),
        };

        for (Results2DLinePlot instance : testInstances){
            Assertions.assertTrue("".equals(instance.getTitle()) || "Title".equals(instance.getTitle()));
            Assertions.assertTrue("".equals(instance.getXLabel()) || "Label".equals(instance.getXLabel()));
            Assertions.assertTrue(instance.getXDataValues().length == 0 || instance.getXDataValues().length == xValuesPrim.length);
            if (instance.getXDataValues().length == 3){
                Assertions.assertArrayEquals(xValuesPrim, instance.getXDataValues());
            }
        }
    }

    @Test
    public void testSettingAndGetting(){
        double[] xValuesPrim = {0.0, 2.0, 4.0};
        Double[] xValuesWrap = {0.0, 2.0, 4.0};
        Results2DLinePlot[] testInstances = {
                new Results2DLinePlot(),
                new Results2DLinePlot("Title"),
                new Results2DLinePlot("Label", xValuesWrap),
                new Results2DLinePlot("Label", xValuesPrim),
                new Results2DLinePlot("Title", "Label", xValuesWrap),
                new Results2DLinePlot("Title", "Label", xValuesPrim),
        };
    }

    @Test
    public void pngRoundTripTest() throws IOException {
        File dupe = File.createTempFile("VCellPNG::", ".png");
        XYSeries series = new XYSeries("key");
        for (int i = 0; i < TestResults2DLinePlot.parabolicData.one.length; i++){
            series.add(TestResults2DLinePlot.parabolicData.one[i], TestResults2DLinePlot.parabolicData.two[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart("Test", "X-Axis","Y-Axis", dataset);
        BufferedImage originalImage = chart.createBufferedImage(1000,1000);
        ImageIO.write(originalImage, "png", dupe);
        BufferedImage roundTrippedImage = ImageIO.read(dupe);
        Assertions.assertEquals(originalImage.getWidth(), roundTrippedImage.getWidth());
        Assertions.assertEquals(originalImage.getHeight(), roundTrippedImage.getHeight());
        for (int wPix = 0; wPix < originalImage.getWidth(); wPix++){
            for (int hPix = 0; hPix < originalImage.getHeight(); hPix++){
                Assertions.assertEquals(originalImage.getRGB(wPix, hPix), roundTrippedImage.getRGB(wPix, hPix));
            }
        }
    }

    @Test
    public void pngLibraryLevelTest() throws IOException {
        String STANDARD_IMAGE_LOCAL_PATH = "Parabolic.png";
        InputStream standardImageStream = TestResults2DLinePlot.class.getResourceAsStream(STANDARD_IMAGE_LOCAL_PATH);
        if (standardImageStream == null)
            throw new FileNotFoundException(String.format("can not find `%s`; maybe it moved?", STANDARD_IMAGE_LOCAL_PATH));
        BufferedImage standardImage = ImageIO.read(standardImageStream);
        XYSeries series = new XYSeries("key");
        for (int i = 0; i < TestResults2DLinePlot.parabolicData.one.length; i++){
            series.add(TestResults2DLinePlot.parabolicData.one[i], TestResults2DLinePlot.parabolicData.two[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart("Test", "X-Axis","Y-Axis", dataset);
        BufferedImage currentImage = chart.createBufferedImage(1000,1000);
        Assertions.assertEquals(currentImage.getWidth(), standardImage.getWidth());
        Assertions.assertEquals(currentImage.getHeight(), standardImage.getHeight());
        for (int wPix = 0; wPix < currentImage.getWidth(); wPix++){
            for (int hPix = 0; hPix < currentImage.getHeight(); hPix++){
                Assertions.assertEquals(currentImage.getRGB(wPix, hPix), standardImage.getRGB(wPix, hPix));
            }
        }
    }
}
