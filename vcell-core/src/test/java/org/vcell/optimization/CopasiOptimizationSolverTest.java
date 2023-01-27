package org.vcell.optimization;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.math.MathException;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.optimization.jtd.Vcellopt;
import org.vcell.optimization.jtd.VcelloptStatus;
import org.vcell.test.Fast;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Category(Fast.class)
public class CopasiOptimizationSolverTest {

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

        optProblemFile.delete();
        optProblemFile1.delete();
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

        CopasiOptimizationSolver copasiOptimizationSolver = new CopasiOptimizationSolver();
        OptimizationResultSet optimizationResultSet = copasiOptimizationSolver.solveLocalPython(parameterEstimationTask);

        Assert.assertNotNull(optimizationResultSet);
    }

//    @Test
//    public void testRemoteOptimization() throws ExpressionException, IOException, XmlParseException {
//        String filename = "Biomodel_simple_parest.vcml";
//        BioModel bioModel = getBioModelFromResource(filename);
//        ParameterEstimationTask parameterEstimationTask = (ParameterEstimationTask) bioModel.getSimulationContext(0).getAnalysisTasks(0);
//
//        CopasiOptimizationSolver copasiOptimizationSolver = new CopasiOptimizationSolver();
//        CopasiOptSolverCallbacks optSolverCallbacks = new CopasiOptSolverCallbacks();
//         ClientTaskStatusSupport clientTaskStatusSupport = new TestClientTaskStatusSupport();
//        ClientServerInfo clientServerInfo = null;
//        OptimizationResultSet optimizationResultSet = copasiOptimizationSolver.solveRemoteApi(
//                parameterEstimationTask, optSolverCallbacks, clientTaskStatusSupport, clientServerInfo);
//
//        Assert.assertNotNull(optimizationResultSet);
//    }

    private static class TestClientTaskStatusSupport implements ClientTaskStatusSupport {
        @Override public void setMessage(String message) {
            System.out.println(message);
        }
        @Override public void setProgress(int progress) {
            System.out.println("Progress : "+progress);
        }
        @Override public int getProgress() {
            return 0;
        }
        @Override public boolean isInterrupted() {
            return false;
        }
        @Override public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {}
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
