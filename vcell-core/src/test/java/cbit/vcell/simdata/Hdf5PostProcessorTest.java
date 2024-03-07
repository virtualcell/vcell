package cbit.vcell.simdata;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Tag("Fast")
public class Hdf5PostProcessorTest {

    static File tempHdf5File;

    @BeforeAll
    public static void setUp() throws IOException {
        tempHdf5File = File.createTempFile("SimID_946368938_0_", ".hdf5");
        Resources.asByteSource(Resources.getResource("cbit/vcell/simdata/SimID_946368938_0_.hdf5"))
                .copyTo(Files.asByteSink(tempHdf5File));
    }

    @AfterAll
    public static void tearDown() {
        tempHdf5File.delete();
    }

    @Test
    public void testRead() {
        Hdf5PostProcessor.PostProcessing postProcessing = new Hdf5PostProcessor.PostProcessing(tempHdf5File.toPath());
        postProcessing.read();

        var expected_variable_infos = new Hdf5PostProcessor.VariableInfo[]{
                new Hdf5PostProcessor.VariableInfo(0, "C_cyt_average", "uM", 0, "C_cyt"),
                new Hdf5PostProcessor.VariableInfo(1, "C_cyt_total", "molecules", 0, "C_cyt"),
                new Hdf5PostProcessor.VariableInfo(2, "C_cyt_min", "uM", 0, "C_cyt"),
                new Hdf5PostProcessor.VariableInfo(3, "C_cyt_max", "uM", 0, "C_cyt"),
                new Hdf5PostProcessor.VariableInfo(4, "Ran_cyt_average", "uM", 1, "Ran_cyt"),
                new Hdf5PostProcessor.VariableInfo(5, "Ran_cyt_total", "molecules", 1, "Ran_cyt"),
                new Hdf5PostProcessor.VariableInfo(6, "Ran_cyt_min", "uM", 1, "Ran_cyt"),
                new Hdf5PostProcessor.VariableInfo(7, "Ran_cyt_max", "uM", 1, "Ran_cyt"),
                new Hdf5PostProcessor.VariableInfo(8, "RanC_cyt_average", "uM", 2, "RanC_cyt"),
                new Hdf5PostProcessor.VariableInfo(9, "RanC_cyt_total", "molecules", 2, "RanC_cyt"),
                new Hdf5PostProcessor.VariableInfo(10, "RanC_cyt_min", "uM", 2, "RanC_cyt"),
                new Hdf5PostProcessor.VariableInfo(11, "RanC_cyt_max", "uM", 2, "RanC_cyt"),
                new Hdf5PostProcessor.VariableInfo(12, "RanC_nuc_average", "uM", 3, "RanC_nuc"),
                new Hdf5PostProcessor.VariableInfo(13, "RanC_nuc_total", "molecules", 3, "RanC_nuc"),
                new Hdf5PostProcessor.VariableInfo(14, "RanC_nuc_min", "uM", 3, "RanC_nuc"),
                new Hdf5PostProcessor.VariableInfo(15, "RanC_nuc_max", "uM", 3, "RanC_nuc")
        };
        for (int i = 0; i < expected_variable_infos.length; i++) {
            var expected = expected_variable_infos[i];
            var actual = postProcessing.getVariables().get(i);
            assert expected.channel_index() == actual.channel_index();
            assert expected.stat_var_name().equals(actual.stat_var_name());
            assert expected.unit().equals(actual.unit());
            assert expected.var_index() == actual.var_index();
            assert expected.var_name().equals(actual.var_name());
        }

        var stats_table_from_vcell_plot =
                """
                        t	C_cyt_average	C_cyt_total	C_cyt_min	C_cyt_max	Ran_cyt_average	Ran_cyt_total	Ran_cyt_min	Ran_cyt_max	RanC_cyt_average	RanC_cyt_total	RanC_cyt_min	RanC_cyt_max	RanC_nuc_average	RanC_nuc_total	RanC_nuc_min	RanC_nuc_max
                        0.0	0.0	0.0	0.0	2.2250738585072014E-308	0.0	0.0	0.0	2.2250738585072014E-308	0.0	0.0	0.0	2.2250738585072014E-308	4.500000000000469E-4	995.2639514331112	4.5E-4	4.5E-4
                        0.25	1.7242715861760507E-6	15.424394116394046	0.0	1.6578610937269188E-5	1.7242715861760507E-6	15.424394116394046	0.0	1.6578610937269188E-5	1.207791769681895E-5	108.04247089303067	0.0	1.386595389472678E-4	3.941755233129602E-4	871.7970864237174	2.618557008421516E-4	4.4784349464175205E-4
                        0.5	5.558151571952027E-6	49.720195525909354	0.0	3.688810690038264E-5	5.558151571952027E-6	49.720195525909354	0.0	3.688810690038264E-5	1.8059551583630324E-5	161.55090846739807	0.0	1.5070593618817915E-4	3.5447559498153353E-4	783.992847439835	2.1160422746379327E-4	4.325308047580796E-4
                        0.75	1.047028801407123E-5	93.66149169073127	0.0	5.838639163921412E-5	1.047028801407123E-5	93.66149169073127	0.0	5.838639163921412E-5	2.1237498474550513E-5	189.9790898046723	0.0	1.518858395444779E-4	3.217543607511082E-4	711.6233699377424	1.8374881412946774E-4	4.084354763369491E-4
                        1.0	1.5889249586954565E-5	142.1365693245928	0.0	7.973304853048764E-5	1.5889249586954565E-5	142.1365693245928	0.0	7.973304853048764E-5	2.277124484748409E-5	203.69914917373157	0.0	1.4872052622450286E-4	2.9363336670624725E-4	649.4282329348212	1.6516455078631075E-4	3.8171626169331387E-4
                        """;

        // parse the table into an array of rows of type double, skipping the first column and the first row
        double[][] stats_table = stats_table_from_vcell_plot.lines()
                .skip(1)
                .map(row -> row.split("\t"))
                .map(row -> {
                    var row_doubles = new double[row.length - 1];
                    for (int i = 1; i < row.length; i++) {
                        row_doubles[i - 1] = Double.parseDouble(row[i]);
                    }
                    return row_doubles;
                })
                .toArray(double[][]::new);

        for (int channel_index = 0; channel_index < postProcessing.getVariables().size(); channel_index++) {
            var variable = postProcessing.getVariables().get(channel_index);
            double[] stats_from_postProcessing = postProcessing.readVariableData(variable);
            for (int time_index = 0; time_index < stats_table.length; time_index++) {
                assert stats_table[time_index][channel_index] == stats_from_postProcessing[time_index];
            }
        }
        var fluorescence = postProcessing.imageMetadata.get(0);
        Assertions.assertEquals("fluor", fluorescence.name);
        Assertions.assertArrayEquals(new int[]{1, 71, 71}, fluorescence.shape);
        Assertions.assertArrayEquals(new double[]{74.24, 74.24, 26.0}, fluorescence.extent, 1e-5);
        Assertions.assertArrayEquals(new double[]{0.0, 0.0, 0.0}, fluorescence.origin);
        Assertions.assertEquals("/PostProcessing/fluor/", fluorescence.groupPath);

        var fluorescence_data_0 = postProcessing.readImageData(fluorescence, 0);
        Assertions.assertEquals(1, fluorescence_data_0.length);
        Assertions.assertEquals(71, fluorescence_data_0[0].length);
        Assertions.assertEquals(71, fluorescence_data_0[0][0].length);
        Assertions.assertEquals(0.0, min(fluorescence_data_0));
        Assertions.assertEquals( 0.0, max(fluorescence_data_0));

        var fluorescence_data_4 = postProcessing.readImageData(fluorescence, 4);
        Assertions.assertEquals(1, fluorescence_data_4.length);
        Assertions.assertEquals(71, fluorescence_data_4[0].length);
        Assertions.assertEquals(71, fluorescence_data_4[0][0].length);
        Assertions.assertEquals(0.0, min(fluorescence_data_4));
        Assertions.assertEquals( 0.7147863306841433, max(fluorescence_data_4), 1e-5);
    }

    private static double min(double[][][] fluorescence_data_0) {
        return Arrays.stream(fluorescence_data_0).flatMap(Arrays::stream).flatMapToDouble(Arrays::stream).min().getAsDouble();
    }
    private static double max(double[][][] fluorescence_data_0) {
        return Arrays.stream(fluorescence_data_0).flatMap(Arrays::stream).flatMapToDouble(Arrays::stream).max().getAsDouble();
    }
}
