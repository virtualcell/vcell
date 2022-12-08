package org.vcell.optimization;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathException;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.VCellConfiguration;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.optimization.jtd.Vcellopt;
import org.vcell.optimization.jtd.VcelloptStatus;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CopasiOptimizationSolverTest {

    private static final String pythonExeName = OperatingSystemInfo.getInstance().isWindows() ? "python" : "python3";

    @BeforeClass
    public static void before(){
        System.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
    }

    @Test
    public void testOptProblemRoundTrip() throws ExpressionException, IOException, XmlParseException, MathException, MappingException, XMLStreamException {
        String filename = "optproblem.vcml";
        BioModel bioModel = getBioModelFromResource(filename);
        ParameterEstimationTask parameterEstimationTask = (ParameterEstimationTask) bioModel.getSimulationContext(0).getAnalysisTasks(0);

        OptProblem optProblem = CopasiUtils.paramTaskToOptProblem(parameterEstimationTask);

        File optProblemFile = new File("optproblem.json");
        CopasiUtils.writeOptProblem(optProblemFile, optProblem);

        OptProblem optProblem1 = CopasiUtils.readOptProblem(optProblemFile);
        File optProblemFile1 = new File("optproblem1.json");
        CopasiUtils.writeOptProblem(optProblemFile1, optProblem);

        boolean filesEqual = Files.equal(optProblemFile, optProblemFile1);
        Assert.assertTrue("round trip files should be equal", filesEqual);
    }

    @Test
    public void testDirectOptimization() throws ExpressionException, IOException, XmlParseException, MathException, MappingException, XMLStreamException, InterruptedException {
        String filename = "Biomodel_simple_parest.vcml";
        BioModel bioModel = getBioModelFromResource(filename);
        ParameterEstimationTask parameterEstimationTask = (ParameterEstimationTask) bioModel.getSimulationContext(0).getAnalysisTasks(0);
        parameterEstimationTask.refreshMappings();

        OptProblem optProblem = CopasiUtils.paramTaskToOptProblem(parameterEstimationTask);

        Vcellopt results = CopasiUtils.runCopasiParameterEstimation(optProblem);

        Assert.assertEquals("expected status to be complete", results.getStatus(), VcelloptStatus.COMPLETE);
        for (Map.Entry<String, Double> fitted_param : results.getOptResultSet().getOptParameterValues().entrySet()){
            System.out.println(fitted_param.getKey()+" -> "+fitted_param.getValue());
        }
    }

    @Test
    public void testLocalOptimization() throws ExpressionException, IOException, XmlParseException, MathException, MappingException, XMLStreamException, InterruptedException {
        String filename = "Biomodel_simple_parest.vcml";
        BioModel bioModel = getBioModelFromResource(filename);
        ParameterEstimationTask parameterEstimationTask = (ParameterEstimationTask) bioModel.getSimulationContext(0).getAnalysisTasks(0);

        ParameterEstimationTaskSimulatorIDA bestFitSimulator = new ParameterEstimationTaskSimulatorIDA();
        CopasiOptimizationSolver copasiOptimizationSolver = new CopasiOptimizationSolver();
        CopasiOptSolverCallbacks copasiOptCallbacks = new CopasiOptSolverCallbacks();
        SimulationContext.MathMappingCallback mathMappingCallback = new SimulationContext.MathMappingCallback() {
            @Override public void setProgressFraction(float fractionDone) {}
            @Override public void setMessage(String message) {}
            @Override public boolean isInterrupted() {
                return false;
            }
        };

        OptimizationResultSet optimizationResultSet = copasiOptimizationSolver.solveLocalPython(
                bestFitSimulator, parameterEstimationTask, copasiOptCallbacks, mathMappingCallback);

        Assert.assertNotNull(optimizationResultSet);
    }

//    @Test
//    public void testRemoteOptimization() throws ExpressionException, IOException, XmlParseException {
//        String filename = "Biomodel_simple_parest.vcml";
//        BioModel bioModel = getBioModelFromResource(filename);
//        ParameterEstimationTask parameterEstimationTask = (ParameterEstimationTask) bioModel.getSimulationContext(0).getAnalysisTasks(0);
//
//        CopasiOptimizationSolver copasiOptimizationSolver = new CopasiOptimizationSolver();
//        ParameterEstimationTaskSimulatorIDA taskSimulatorIDA = new ParameterEstimationTaskSimulatorIDA();
//        CopasiOptSolverCallbacks optSolverCallbacks = new CopasiOptSolverCallbacks();
//        SimulationContext.MathMappingCallback mathMappingCallback = new TestMathMappingCallback();
//        ClientTaskStatusSupport clientTaskStatusSupport = new TestClientTaskStatusSupport();
//        OptimizationResultSet optimizationResultSet = copasiOptimizationSolver.solveRemoteApi(
//                taskSimulatorIDA, parameterEstimationTask, optSolverCallbacks, mathMappingCallback, clientTaskStatusSupport);
//    }

    private static class TestMathMappingCallback implements SimulationContext.MathMappingCallback {
        @Override
        public void setMessage(String message) {
            System.out.println(message);
        }

        @Override
        public void setProgressFraction(float fractionDone) {
            System.out.println("fraction done "+fractionDone);
        }

        @Override
        public boolean isInterrupted() {
            return false;
        }
    }

    private static class TestClientTaskStatusSupport implements ClientTaskStatusSupport {
        @Override
        public void setMessage(String message) {
            System.out.println(message);
        }

        @Override
        public void setProgress(int progress) {
            System.out.println("Progress : "+progress);
        }

        @Override
        public int getProgress() {
            return 0;
        }

        @Override
        public boolean isInterrupted() {
            return false;
        }

        @Override
        public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
        }
    }

    private static BioModel getBioModelFromResource(String fileName) throws IOException, XmlParseException {
        InputStream inputStream = CopasiOptimizationSolverTest.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            String vcml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return XmlHelper.XMLToBioModel(new XMLSource(vcml));
        }
    }


}
