package org.vcell.cli.run.plotting;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.vcell.cli.commands.execution.BiosimulationsCommand;
import org.vcell.util.Pair;
import org.vcell.util.VCellUtilityHub;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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

    private static Pair<List<Double>, List<Double>> parabolicListData = new Pair<>(
            List.of(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0),
            List.of(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0)
    );

    private static Pair<List<Double>, List<Double>> linearListData = new Pair<>(
            List.of(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0),
            List.of(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0)
    );

    @Test
    public void testConstructors(){
        Results2DLinePlot[] testInstances = {
                new Results2DLinePlot(),
                new Results2DLinePlot("Title", "Label")
        };

        for (Results2DLinePlot instance : testInstances){
            Assertions.assertTrue("".equals(instance.getTitle()) || "Title".equals(instance.getTitle()));
            Assertions.assertTrue("".equals(instance.getXAxisTitle()) || "Label".equals(instance.getXAxisTitle()));
        }
    }

    @Test
    public void testSettingAndGetting(){
        Results2DLinePlot[] testInstances = {
                new Results2DLinePlot(),
                new Results2DLinePlot("Title", "Label")
        };
        for (Results2DLinePlot plot : testInstances){
            String alternateTitle = "AltTitle", alternateAxisTitle = "AltAxisTitle";
            plot.setTitle(alternateTitle);
            Assertions.assertEquals(alternateTitle, plot.getTitle());
            plot.setXAxisTitle(alternateAxisTitle);
            Assertions.assertEquals(alternateAxisTitle, plot.getXAxisTitle());

            Assertions.assertThrows(IllegalArgumentException.class, () -> plot.addXYData(null, new SingleAxisSeries("Linear", linearListData.two)));
            Assertions.assertThrows(IllegalArgumentException.class, () -> plot.addXYData(new SingleAxisSeries("Numbers", linearListData.one), null));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> plot.addXYData(new SingleAxisSeries(null, linearListData.one), new SingleAxisSeries("Linear", linearListData.two)));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> plot.addXYData(new SingleAxisSeries("Numbers", null), new SingleAxisSeries("Linear", linearListData.two)));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> plot.addXYData(new SingleAxisSeries("Numbers", linearListData.one), new SingleAxisSeries(null, linearListData.two)));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> plot.addXYData(new SingleAxisSeries("Numbers", linearListData.one), new SingleAxisSeries("Linear", null)));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> plot.addXYData(new SingleAxisSeries("Numbers", linearListData.one), new SingleAxisSeries("Linear", new ArrayList<>(List.of(0.2, 1.2)))));


            SingleAxisSeries xNumbers = new SingleAxisSeries("Numbers", linearListData.one);
            plot.addXYData(xNumbers, new SingleAxisSeries("Linear", linearListData.two));
            plot.addXYData(xNumbers, new SingleAxisSeries("Quadratic", parabolicListData.two));
            plot.addXYData(new SingleAxisSeries("ToRemoveX", List.of(0.0, 1.0)), new SingleAxisSeries("ToRemoveY1", List.of(0.1, 1.1)));
            plot.addXYData(new SingleAxisSeries("ToRemoveX", List.of(0.0, 1.0)), new SingleAxisSeries("ToRemoveY2", List.of(0.2, 1.2)));
            plot.addXYData(new SingleAxisSeries("ToRemoveX", List.of(0.0, 1.0)), new SingleAxisSeries("ToRemoveY3", List.of(0.3, 1.3)));
            Assertions.assertEquals(plot.getXAxisSeries("ToRemoveX"), new SingleAxisSeries("ToRemoveX", List.of(0.0, 1.0)));
            Assertions.assertEquals(plot.getYAxisSeries("ToRemoveY2"), new SingleAxisSeries("ToRemoveY2", List.of(0.2, 1.2)));

            Assertions.assertEquals(3, plot.getNumYSeries(new SingleAxisSeries("ToRemoveX", List.of(0.0, 1.0))));
            Assertions.assertTrue(plot.removeXYData("ToRemoveX", "ToRemoveY3"));
            Assertions.assertFalse(plot.removeXYData("ToRemoveX", "ToRemoveY3"));
            Assertions.assertEquals(2, plot.getNumYSeries(new SingleAxisSeries("ToRemoveX", List.of(0.0, 1.0))));
            Assertions.assertTrue(plot.removeXYData("ToRemoveX", "ToRemoveY2"));
            Assertions.assertEquals(2, plot.getNumXSeries());
            Assertions.assertTrue(plot.removeXYData("ToRemoveX", "ToRemoveY1"));
            Assertions.assertEquals(1, plot.getNumXSeries());
            Assertions.assertEquals(0, plot.getNumYSeries(new SingleAxisSeries("ToRemoveX", List.of(0.0, 1.0))));
            Assertions.assertNull(plot.getXAxisSeries("ToRemoveX"));
            Assertions.assertEquals(2, plot.getNumYSeries(xNumbers));
            Assertions.assertEquals(2, plot.getNumYSeries());


            Assertions.assertEquals(parabolicListData.one.size(), plot.getLargestSeriesSize());
            Assertions.assertThrows(IllegalArgumentException.class, ()->plot.changeXAxisData(null, linearListData.one));
            Assertions.assertThrows(IllegalArgumentException.class, ()->plot.changeXAxisData("Numbers", (double[]) null));
            Assertions.assertThrows(IllegalArgumentException.class, ()->plot.changeXAxisData("Nummmmbers", linearListData.one));
            Assertions.assertThrows(IllegalArgumentException.class, ()->plot.changeXAxisData("Numbers", new double[0]));
            Assertions.assertThrows(IllegalArgumentException.class, ()->plot.changeXAxisData(new SingleAxisSeries("Nummmmbers", linearListData.one)));
            Assertions.assertThrows(IllegalArgumentException.class, ()->plot.changeXAxisData(new SingleAxisSeries("Numbers", List.of())));
            plot.changeXAxisData(new SingleAxisSeries("Numbers", parabolicListData.one));
            Assertions.assertThrows(IllegalArgumentException.class, ()-> plot.changeXAxisData("Numbers", parabolicData.one));
            plot.changeXAxisData("Numbers", parabolicListData.one.stream().mapToDouble(Double::doubleValue).toArray());
            plot.changeXAxisData("Numbers", parabolicListData.one);
        }

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

    @Test
    public void pngExecutionLevelTest() throws IOException {
        String INPUT_FILE_LOCAL_PATH = "MultiplePlotsTest.omex";
        Path inputFilePath = Files.createTempFile("VCellInputOmex", ".omex");
        Path outputFile = Files.createTempDirectory("VCellCliOut");
        try (InputStream inputFileStream = TestResults2DLinePlot.class.getResourceAsStream(INPUT_FILE_LOCAL_PATH)){
            if (inputFileStream == null)
                throw new FileNotFoundException(String.format("can not find `%s`; maybe it moved?", INPUT_FILE_LOCAL_PATH));
            Files.copy(inputFileStream, inputFilePath, StandardCopyOption.REPLACE_EXISTING);
        }

        /////////////////////////////////////////
        File installRoot = new File("..");
        PropertyLoader.setProperty(PropertyLoader.installationRoot, installRoot.getAbsolutePath());
        VCMongoMessage.enabled = false;
        VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);
        int result = BiosimulationsCommand.executeVCellBiosimulationsMode(inputFilePath.toFile(), outputFile.toFile());
        if (result != 0) throw new RuntimeException("VCell Execution failed!");
        /////////////////////////////////////////

        File generatedPlot0 = new File(outputFile.toFile(), "BIOMD0000000912_sim.sedml/plot_0.png");
        File generatedPlot1 = new File(outputFile.toFile(), "BIOMD0000000912_sim.sedml/plot_1.png");
        BufferedImage generatedImage0 = ImageIO.read(generatedPlot0);
        if (generatedImage0 == null) throw new RuntimeException("Plot_0 PNG was not found; check paths?");
        BufferedImage generatedImage1 = ImageIO.read(generatedPlot1);
        if (generatedImage1 == null) throw new RuntimeException("Plot_1 PNG was not found; check paths?");


        String PLOT_0_PATH = "plot_0.png";
        String PLOT_1_PATH = "plot_1.png";
        InputStream standardImageStream0 = TestResults2DLinePlot.class.getResourceAsStream(PLOT_0_PATH);
        if (standardImageStream0 == null)
            throw new FileNotFoundException(String.format("can not find `%s`; maybe it moved?", PLOT_0_PATH));
        BufferedImage standardImage0 = ImageIO.read(standardImageStream0);
        InputStream standardImageStream1 = TestResults2DLinePlot.class.getResourceAsStream(PLOT_1_PATH);
        if (standardImageStream1 == null)
            throw new FileNotFoundException(String.format("can not find `%s`; maybe it moved?", PLOT_1_PATH));
        BufferedImage standardImage1 = ImageIO.read(standardImageStream1);

        Assertions.assertEquals(standardImage0.getWidth(), generatedImage0.getWidth());
        Assertions.assertEquals(standardImage0.getHeight(), generatedImage0.getHeight());
        Assertions.assertEquals(standardImage1.getWidth(), generatedImage1.getWidth());
        Assertions.assertEquals(standardImage1.getHeight(), generatedImage1.getHeight());


        for (int wPix = 0; wPix < generatedImage0.getWidth(); wPix++){
            for (int hPix = 0; hPix < generatedImage0.getHeight(); hPix++){
                Assertions.assertEquals(generatedImage0.getRGB(wPix, hPix), standardImage0.getRGB(wPix, hPix));
            }
        }

        for (int wPix = 0; wPix < generatedImage1.getWidth(); wPix++){
            for (int hPix = 0; hPix < generatedImage1.getHeight(); hPix++){
                Assertions.assertEquals(generatedImage1.getRGB(wPix, hPix), standardImage1.getRGB(wPix, hPix));
            }
        }
    }
}
