package org.vcell.cli.run.hdf5;

import cbit.vcell.resource.PropertyLoader;
import com.google.common.io.Files;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import org.jlibsedml.DataSet;
import org.junit.jupiter.api.Tag;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;
import org.vcell.util.VCellUtilityHub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

@Tag("Fast")
public class Hdf5WriterTest {

    public static Hdf5DataContainer createExampleData() {

        DataSet t = new DataSet("t","t","t","#null");
        DataSet s0 = new DataSet("s0","s0","s0","#null");
        DataSet s1 = new DataSet("s1", "s1", "s1","#null");

        Hdf5SedmlMetadata plotMetadata = new Hdf5SedmlMetadata();
        plotMetadata._type = "SedPlot2D";
        plotMetadata.sedmlDataSetDataTypes = new ArrayList<>(Arrays.asList("float64", "float64", "float64"));
        plotMetadata.sedmlDataSetIds = new ArrayList<>(Arrays.asList("dataGen_tsk_0_0_s0", "dataGen_tsk_0_0_s1", "time_tsk_0_0"));
        plotMetadata.sedmlDataSetLabels = new ArrayList<>(Arrays.asList("dataGen_tsk_0_0_s0", "dataGen_tsk_0_0_s1", "time_tsk_0_0"));
        plotMetadata.sedmlDataSetNames = new ArrayList<>(Arrays.asList("dataGen_tsk_0_0_s0", "dataGen_tsk_0_0_s1", "time_tsk_0_0"));
        plotMetadata.sedmlDataSetShapes = new ArrayList<>(Arrays.asList("21", "21", "21"));
        plotMetadata.sedmlId = "plot2d_simple";
        plotMetadata.sedmlName = "Application0_simple_plot";
        plotMetadata.uri = "___0_export_NO_scan_test.sedml/plot2d_simple";

        Hdf5SedmlMetadata reportMetadata = new Hdf5SedmlMetadata();
        reportMetadata._type = "SedReport";
        reportMetadata.sedmlDataSetDataTypes = new ArrayList<>(Arrays.asList("float64", "float64", "float64"));
        reportMetadata.sedmlDataSetIds = new ArrayList<>(Arrays.asList("__data_set__plot2d_simpletime_tsk_0_0", "__data_set__plot2d_simpledataGen_tsk_0_0_s0", "__data_set__plot2d_simpledataGen_tsk_0_0_s1"));
        reportMetadata.sedmlDataSetLabels = new ArrayList<>(Arrays.asList("time_tsk_0_0", "dataGen_tsk_0_0_s0", "dataGen_tsk_0_0_s1"));
        reportMetadata.sedmlDataSetNames = new ArrayList<>(Arrays.asList("", "", ""));
        reportMetadata.sedmlDataSetShapes = new ArrayList<>(Arrays.asList("21", "21", "21"));
        reportMetadata.sedmlId = "report_simple";
        reportMetadata.sedmlName = "Application0_simple_report"; // should it say simple_report?
        reportMetadata.uri = "___0_export_NO_scan_test.sedml/report_simple";

        double[] row_S0_0 = new double[] { 5.0, 4.557601554959449, 4.2130612935114025, 3.944733095329601, 3.7357588758928553, 3.573009574805288, 3.446260315908218, 3.3475478903157536, 3.270670576690077, 3.2107984498648108, 3.1641700002133346, 3.127855727468622, 3.0995741465223223, 3.0775484137891307, 3.0603947604876303, 3.0470354805330198, 3.0366312589052136, 3.0285284455398327, 3.022217968554351, 3.017303364885424, 3.0134758559758974};
        double[] row_S0_1 = new double[] { 5.1, 4.557601554959449, 4.2130612935114025, 3.944733095329601, 3.7357588758928553, 3.573009574805288, 3.446260315908218, 3.3475478903157536, 3.270670576690077, 3.2107984498648108, 3.1641700002133346, 3.127855727468622, 3.0995741465223223, 3.0775484137891307, 3.0603947604876303, 3.0470354805330198, 3.0366312589052136, 3.0285284455398327, 3.022217968554351, 3.017303364885424, 3.0134758559758974};
        double[] row_S0_2 = new double[] { 5.2, 4.557601554959449, 4.2130612935114025, 3.944733095329601, 3.7357588758928553, 3.573009574805288, 3.446260315908218, 3.3475478903157536, 3.270670576690077, 3.2107984498648108, 3.1641700002133346, 3.127855727468622, 3.0995741465223223, 3.0775484137891307, 3.0603947604876303, 3.0470354805330198, 3.0366312589052136, 3.0285284455398327, 3.022217968554351, 3.017303364885424, 3.0134758559758974};
        double[] row_S1 = new double[] { 0.0, 0.442398445040551, 0.786938706488598, 1.0552669046703975, 1.2642411241071438, 1.426990425194712, 1.5537396840917823, 1.6524521096842468, 1.7293294233099237, 1.7892015501351888, 1.835829999786665, 1.8721442725313768, 1.900425853477675, 1.922451586210867, 1.9396052395123684, 1.952964519466979, 1.9633687410947849, 1.9714715544601655, 1.9777820314456476, 1.982696635114575, 1.9865241440241013};
        double[] row_t = new double[] { 0.0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6000000000000001, 0.65, 0.7000000000000001, 0.75, 0.8, 0.8500000000000001, 0.9, 0.95, 1.0 };

        Hdf5SedmlResults plotDatasetWrapper = new Hdf5SedmlResults();
        plotDatasetWrapper.datasetMetadata = plotMetadata;
        Hdf5SedmlResultsNonspatial plotDataSourceNonspatial = new Hdf5SedmlResultsNonspatial();
        plotDataSourceNonspatial.scanBounds = new int[0];
        plotDataSourceNonspatial.scanParameterNames = new String[0];
        plotDatasetWrapper.dataSource = plotDataSourceNonspatial;
        Map<DataSet, List<double[]>> plotJob = new LinkedHashMap<>();
        plotJob.put(t, Arrays.asList(row_t));
        plotJob.put(s0, Arrays.asList(row_S0_0));
        plotJob.put(s1, Arrays.asList(row_S1));
        plotDataSourceNonspatial.allJobResults = plotJob;


        Hdf5SedmlResults reportDatasetWrapper = new Hdf5SedmlResults();
        reportDatasetWrapper.datasetMetadata = reportMetadata;
        Hdf5SedmlResultsNonspatial reportDataSourceNonspatial = new Hdf5SedmlResultsNonspatial();
        reportDataSourceNonspatial.scanBounds = new int[] { 2 }; // zero indexed? 
        reportDataSourceNonspatial.scanParameterNames = new String[] { "k1" };
        reportDatasetWrapper.dataSource = reportDataSourceNonspatial;
        Map<DataSet, List<double[]>> reportJob = new LinkedHashMap<>();
        reportJob.put(t, Arrays.asList(row_t, row_t, row_t));
        reportJob.put(s0, Arrays.asList(row_S0_0, row_S0_1, row_S0_2));
        reportJob.put(s1, Arrays.asList(row_S1, row_S1, row_S1));
        reportDataSourceNonspatial.allJobResults = reportJob;

        Hdf5DataContainer hdf5FileWrapper = new Hdf5DataContainer();
        String uri = "___0_export_NO_scan_test.sedml";
        List<Hdf5SedmlResults> wrappers = new ArrayList<>();
        wrappers.add(plotDatasetWrapper);
        wrappers.add(reportDatasetWrapper);
        hdf5FileWrapper.uriToResultsMap.put(uri, wrappers);

        return hdf5FileWrapper;
    }

    @Test
    public void test() throws HDF5Exception, IOException {
        PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
        VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);
        NativeLib.HDF5.load();
        Hdf5DataContainer exampleHdf5FileWrapper = Hdf5WriterTest.createExampleData();
        File dir = Files.createTempDir();
        Hdf5Writer.writeHdf5(exampleHdf5FileWrapper, dir);
    }

}