package org.vcell.optimization;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.opt.*;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sbml.jsbml.SBMLException;
import org.vcell.optimization.jtd.*;
import org.vcell.optimization.jtd.CopasiOptimizationMethod;
import org.vcell.optimization.jtd.CopasiOptimizationParameter;
import org.vcell.optimization.jtd.ParameterDescription;
import org.vcell.sbml.vcell.MathModel_SBMLExporter;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CopasiUtils {
    private final static Logger lg = LogManager.getLogger(CopasiUtils.class);
    public static final Path currentWorkingDir = Paths.get("").toAbsolutePath();
    private static final String pythonExeName = OperatingSystemInfo.getInstance().isWindows() ? "python" : "python3";


    public static OptProblem paramTaskToOptProblem(ParameterEstimationTask parameterEstimationTask) throws IOException, ExpressionException, SBMLException, XMLStreamException, MathException, MappingException {
        parameterEstimationTask.refreshMappings();
        OptimizationSpec optimizationSpec = parameterEstimationTask.getModelOptimizationMapping().getOptimizationSpec();

        SimulationContext simulationContext = parameterEstimationTask.getSimulationContext();
        SimulationContext.MathMappingCallback callback = new SimulationContext.MathMappingCallback() {
            @Override
            public void setProgressFraction(float fractionDone) {
                Thread.dumpStack();
                System.out.println("---> stdout mathMapping: progress = " + (fractionDone * 100.0) + "% done");
            }

            @Override
            public void setMessage(String message) {
                Thread.dumpStack();
                System.out.println("---> stdout mathMapping: message = " + message);
            }

            @Override
            public boolean isInterrupted() {
                return false;
            }
        };
        simulationContext.refreshMathDescription(callback, SimulationContext.NetworkGenerationRequirements.ComputeFullStandardTimeout);
        MathModel vcellMathModel = new MathModel(null);
        vcellMathModel.setMathDescription(simulationContext.getMathDescription());
        //get math model string
        String sbmlString = MathModel_SBMLExporter.getSBMLString(vcellMathModel, 2, 4);

        OptProblem optProblem = new OptProblem();
        optProblem.setMathModelSbmlContents(sbmlString);
        optProblem.setNumberOfOptimizationRuns(parameterEstimationTask.getOptimizationSolverSpec().getNumOfRuns());
        List<ParameterDescription> parameterDescriptions = new ArrayList<>();
        for (Parameter p : optimizationSpec.getParameters()) {
            org.vcell.optimization.jtd.ParameterDescription pdesc = new ParameterDescription();
            pdesc.setName(p.getName());
            pdesc.setScale(p.getScale());
            pdesc.setMinValue(p.getLowerBound());
            pdesc.setMaxValue(p.getUpperBound());
            pdesc.setInitialValue(p.getInitialGuess());
            parameterDescriptions.add(pdesc);
        }
        optProblem.setParameterDescriptionList(parameterDescriptions);

        SimpleReferenceData refData = (SimpleReferenceData) parameterEstimationTask.getModelOptimizationSpec().getReferenceData();
        // check if t is at the first column

        int timeIndex = refData.findColumn(ReservedVariable.TIME.getName());
        if (timeIndex != 0) {
            throw new RuntimeException("t must be the first column");
        }
        List<List<Double>> dataset = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < refData.getNumDataRows(); rowIndex++) {
            List<Double> dataRow = new ArrayList<>();
            double[] array = refData.getDataByRow(rowIndex);
            for (double d : array) {
                dataRow.add(d);
            }
            dataset.add(dataRow);
        }
        optProblem.setDataSet(dataset);

        ReferenceVariable timeRefVar = new ReferenceVariable();
        timeRefVar.setVarName(ReservedVariable.TIME.getName());
        timeRefVar.setReferenceVariableType(ReferenceVariableReferenceVariableType.INDEPENDENT);
        List<ReferenceVariable> referenceVariables = new ArrayList<>();
        referenceVariables.add(timeRefVar);
        // add all other dependent variables, recall that the dependent variables start from 2nd column onward in reference data
        for (int i = 1; i < refData.getNumDataColumns(); i++) {
            ReferenceDataMappingSpec rdms = parameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpec(refData.getColumnNames()[i]);
            ReferenceVariable refVar = new ReferenceVariable();
            refVar.setVarName(rdms.getModelObject().getName());
            refVar.setReferenceVariableType(ReferenceVariableReferenceVariableType.DEPENDENT);
            referenceVariables.add(refVar);
        }
        optProblem.setReferenceVariable(referenceVariables);

        cbit.vcell.opt.CopasiOptimizationMethod vcellCopasiOptimizationMethod = parameterEstimationTask.getOptimizationSolverSpec().getCopasiOptimizationMethod();
        CopasiOptimizationMethodOptimizationMethodType optMethodType = vcellOptMethodToJTD(vcellCopasiOptimizationMethod.getType());
        CopasiOptimizationMethod jtdOptMethod = new CopasiOptimizationMethod();
        jtdOptMethod.setOptimizationMethodType(optMethodType);

        List<CopasiOptimizationParameter> copasiOptimizationParameters = new ArrayList<>();
        for (cbit.vcell.opt.CopasiOptimizationParameter optParam : vcellCopasiOptimizationMethod.getParameters()) {
            CopasiOptimizationParameter optParamJTD = copasiOptParamFromVCell(optParam);
            copasiOptimizationParameters.add(optParamJTD);
        }
        jtdOptMethod.setOptimizationParameter(copasiOptimizationParameters);
        optProblem.setCopasiOptimizationMethod(jtdOptMethod);

        return optProblem;
    }

    private static CopasiOptimizationParameter copasiOptParamFromVCell(cbit.vcell.opt.CopasiOptimizationParameter optParam) {
        CopasiOptimizationParameter p = new CopasiOptimizationParameter();
        p.setValue(optParam.getValue());
        CopasiOptimizationParameterParamType optParmType = null;
        CopasiOptimizationParameterDataType optDataType = null;
        switch (optParam.getType()) {
            case Cooling_Factor:
                optParmType = CopasiOptimizationParameterParamType.COOLING_FACTOR;
                optDataType = CopasiOptimizationParameterDataType.DOUBLE;
                break;
            case IterationLimit:
                optParmType = CopasiOptimizationParameterParamType.ITERATION_LIMIT;
                optDataType = CopasiOptimizationParameterDataType.INT;
                break;
            case Number_of_Generations:
                optParmType = CopasiOptimizationParameterParamType.NUMBER_OF_GENERATIONS;
                optDataType = CopasiOptimizationParameterDataType.INT;
                break;
            case Number_of_Iterations:
                optParmType = CopasiOptimizationParameterParamType.NUMBER_OF_ITERATIONS;
                optDataType = CopasiOptimizationParameterDataType.INT;
                break;
            case Pf:
                optParmType = CopasiOptimizationParameterParamType.PF;
                optDataType = CopasiOptimizationParameterDataType.DOUBLE;
                break;
            case Population_Size:
                optParmType = CopasiOptimizationParameterParamType.POPULATION_SIZE;
                optDataType = CopasiOptimizationParameterDataType.INT;
                break;
            case Random_Number_Generator:
                optParmType = CopasiOptimizationParameterParamType.RANDOM_NUMBER_GENERATOR;
                optDataType = CopasiOptimizationParameterDataType.INT;
                break;
            case Rho:
                optParmType = CopasiOptimizationParameterParamType.RHO;
                optDataType = CopasiOptimizationParameterDataType.DOUBLE;
                break;
            case Scale:
                optParmType = CopasiOptimizationParameterParamType.SCALE;
                optDataType = CopasiOptimizationParameterDataType.DOUBLE;
                break;
            case Seed:
                optParmType = CopasiOptimizationParameterParamType.SEED;
                optDataType = CopasiOptimizationParameterDataType.INT;
                break;
            case Start_Temperature:
                optParmType = CopasiOptimizationParameterParamType.START_TEMPERATURE;
                optDataType = CopasiOptimizationParameterDataType.DOUBLE;
                break;
            case Std_Deviation:
                optParmType = CopasiOptimizationParameterParamType.STD_DEVIATION;
                optDataType = CopasiOptimizationParameterDataType.DOUBLE;
                break;
            case Swarm_Size:
                optParmType = CopasiOptimizationParameterParamType.SWARM_SIZE;
                optDataType = CopasiOptimizationParameterDataType.INT;
                break;
            case Tolerance:
                optParmType = CopasiOptimizationParameterParamType.TOLERANCE;
                optDataType = CopasiOptimizationParameterDataType.DOUBLE;
                break;
            default:
                throw new RuntimeException("unsupported parameter type :" + optParam.getType().name() + " in COPASI optimization solver");
        }
        p.setParamType(optParmType);
        p.setDataType(optDataType);
        return p;
    }

    public static void writeOptProblem(File optProblemFile, OptProblem optProblem) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new FileWriter(optProblemFile), optProblem);
    }

    public static Vcellopt readOptRun(File optRunFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Vcellopt vcellopt = objectMapper.readValue(optRunFile, Vcellopt.class);
        return vcellopt;
    }

    public static OptProblem readOptProblem(File optProblemFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        OptProblem optProblem = objectMapper.readValue(optProblemFile, OptProblem.class);
        return optProblem;
    }

    public static void writeOptRunJson(File optRunFile, Vcellopt vcellopt) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new FileWriter(optRunFile), vcellopt);
    }

    private static CopasiOptimizationMethodOptimizationMethodType vcellOptMethodToJTD(cbit.vcell.opt.CopasiOptimizationMethod.CopasiOptimizationMethodType vcellOptimizationMethodType) {
        switch (vcellOptimizationMethodType) {
            case EvolutionaryProgram:
                return CopasiOptimizationMethodOptimizationMethodType.EVOLUTIONARY_PROGRAM;
            case GeneticAlgorithm:
                return CopasiOptimizationMethodOptimizationMethodType.GENETIC_ALGORITHM;
            case GeneticAlgorithmSR:
                return CopasiOptimizationMethodOptimizationMethodType.GENETIC_ALGORITHM_SR;
            case HookeJeeves:
                return CopasiOptimizationMethodOptimizationMethodType.HOOKE_JEEVES;
            case LevenbergMarquardt:
                return CopasiOptimizationMethodOptimizationMethodType.LEVENBERG_MARQUARDT;
            case NelderMead:
                return CopasiOptimizationMethodOptimizationMethodType.NELDER_MEAD;
            case ParticleSwarm:
                return CopasiOptimizationMethodOptimizationMethodType.PARTICLE_SWARM;
            case Praxis:
                return CopasiOptimizationMethodOptimizationMethodType.PRAXIS;
            case RandomSearch:
                return CopasiOptimizationMethodOptimizationMethodType.RANDOM_SEARCH;
            case SimulatedAnnealing:
                return CopasiOptimizationMethodOptimizationMethodType.SIMULATED_ANNEALING;
            case SRES:
                return CopasiOptimizationMethodOptimizationMethodType.SRES;
            case SteepestDescent:
                return CopasiOptimizationMethodOptimizationMethodType.STEEPEST_DESCENT;
            case TruncatedNewton:
                return CopasiOptimizationMethodOptimizationMethodType.TRUNCATED_NEWTON;
            default:
                throw new RuntimeException("unexpected optimization type " + vcellOptimizationMethodType);
        }
    }


    public static OptimizationResultSet toOptResults(Vcellopt optRun, ParameterEstimationTask parameterEstimationTask, ParameterEstimationTaskSimulatorIDA parestSimulator) throws Exception {
        OptResultSet optResultSet = optRun.getOptResultSet();
        int numFittedParameters = optResultSet.getOptParameterValues().size();
        String[] paramNames = new String[numFittedParameters];
        double[] paramValues = new double[numFittedParameters];
        int pIndex=0;
        for (Map.Entry<String, Double> entry : optResultSet.getOptParameterValues().entrySet()){
            paramNames[pIndex] = entry.getKey();
            paramValues[pIndex] = entry.getValue();
            pIndex++;
        }

        OptimizationStatus status = new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION, optRun.getStatusMessage());
        OptSolverResultSet.OptRunResultSet optRunResultSet = new OptSolverResultSet.OptRunResultSet(paramValues,optResultSet.getObjectiveFunction(),optResultSet.getNumFunctionEvaluations(),status);
        OptSolverResultSet copasiOptSolverResultSet = new OptSolverResultSet(paramNames, optRunResultSet);
        RowColumnResultSet copasiRcResultSet = parestSimulator.getRowColumnRestultSetByBestEstimations(parameterEstimationTask, paramNames, paramValues);
        OptimizationResultSet copasiOptimizationResultSet = new OptimizationResultSet(copasiOptSolverResultSet, copasiRcResultSet);
        return copasiOptimizationResultSet;
    }

    public static Vcellopt runCopasiParameterEstimation(OptProblem optProblem) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
//        File tempDir = Files.createTempDir();
        File tempDir = Files.createTempDir();

        // write optProblem
        File optProblemFile = new File(tempDir, "optProblem.json");
        writeOptProblem(optProblemFile, optProblem);

        File resultsFile = new File(tempDir, "optResults.json");

        // call copasi via vcell_opt package to run parameter estimation
        callCopasiPython(optProblemFile.toPath(), resultsFile.toPath());

        // read VCellopt results
        Vcellopt results = objectMapper.readValue(resultsFile, Vcellopt.class);
        return results;
    }

    public static OptimizationResultSet optRunToOptimizationResultSet(ParameterEstimationTask parameterEstimationTask, Vcellopt optRun) throws Exception {
        OptResultSet optResultSet = optRun.getOptResultSet();
        int numFittedParameters = optResultSet.getOptParameterValues().size();
        String[] paramNames = new String[numFittedParameters];
        double[] paramValues = new double[numFittedParameters];
        int pIndex = 0;
        for (Map.Entry<String, Double> entry : optResultSet.getOptParameterValues().entrySet()) {
            paramNames[pIndex] = entry.getKey();
            paramValues[pIndex] = entry.getValue();
            pIndex++;
        }

        ParameterEstimationTaskSimulatorIDA parestSimulator = new ParameterEstimationTaskSimulatorIDA();
        OptimizationStatus status = new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION, optRun.getStatusMessage());
        OptSolverResultSet.OptRunResultSet optRunResultSet = new OptSolverResultSet.OptRunResultSet(paramValues, optResultSet.getObjectiveFunction(), optResultSet.getNumFunctionEvaluations(), status);
        OptSolverResultSet copasiOptSolverResultSet = new OptSolverResultSet(paramNames, optRunResultSet);
        RowColumnResultSet copasiRcResultSet = parestSimulator.getRowColumnRestultSetByBestEstimations(parameterEstimationTask, paramNames, paramValues);
        OptimizationResultSet copasiOptimizationResultSet = new OptimizationResultSet(copasiOptSolverResultSet, copasiRcResultSet);
        return copasiOptimizationResultSet;
    }

    public static void callCopasiPython(Path optProblemFile, Path resultsFile) throws InterruptedException, IOException {
        //final String pythonExe = pythonExeName;
        File installDir = PropertyLoader.getRequiredDirectory(PropertyLoader.installationRoot);
        File optDir = Paths.get(installDir.getAbsolutePath(),"pythonProject", "vcell-opt").toAbsolutePath().toFile();
//        final String pythonExe = "/Users/schaff/Library/Caches/pypoetry/virtualenvs/vcell-opt-XIpjcTyI-py3.9/bin/python";
        ProcessBuilder pb = new ProcessBuilder(new String[]{"poetry","run","python", "-m", "vcell_opt.optService", String.valueOf(optProblemFile.toAbsolutePath()), String.valueOf(resultsFile.toAbsolutePath())});
        pb.directory(optDir);
        System.out.println(pb.command());
        runAndPrintProcessStreams(pb, "", "");
    }

    private static void runAndPrintProcessStreams(ProcessBuilder pb, String outString, String errString) throws InterruptedException, IOException {
        // Process printing code goes here
        File of = File.createTempFile("temp-", ".out", currentWorkingDir.toFile());
        File ef = File.createTempFile("temp-", ".err", currentWorkingDir.toFile());
        pb.redirectError(ef);
        pb.redirectOutput(of);
        Process process = pb.start();
        process.waitFor();
        StringBuilder sberr = new StringBuilder();
        StringBuilder sbout = new StringBuilder();
        List<String> lines = com.google.common.io.Files.readLines(ef, StandardCharsets.UTF_8);
        lines.forEach(line -> sberr.append(line).append("\n"));
        String es = sberr.toString();
        lines = Files.readLines(of, StandardCharsets.UTF_8);
        lines.forEach(line -> sbout.append(line).append("\n"));
        String os = sbout.toString();
        of.delete();
        ef.delete();
        if (process.exitValue() != 0) {
            lg.error(errString);
            // don't print here, send the error down to caller who is responsible for dealing with it
            throw new RuntimeException(es);
        } else {
            if (!outString.equals("")) lg.info(outString);
            if (!os.equals("")) lg.info(os);
        }
    }


}
