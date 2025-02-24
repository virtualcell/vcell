package org.vcell.sbml.vcell;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import org.jlibsedml.UniformTimeCourse;
import org.sbml.jsbml.SBase;
import org.vcell.sbml.vcell.lazy.LazySBMLSpatialDataAccessor;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpatialSBMLSimResults {

    //private ODESolverResultSet resultSet;
    private final OutputContext DEFAULT_OUTPUT_CONTEXT = new OutputContext(new AnnotatedFunction[] {});
    private final DataSetControllerImpl dataSetController;
    private final VCSimulationIdentifier simId;
    private final int jobIndex;
    private final SBMLSymbolMapping sbmlMapping;
    private final MathSymbolMapping mathMapping;
    private final Map<String, LazySBMLSpatialDataAccessor> lazyAccessorMapping;

    public SpatialSBMLSimResults(SimulationJob vcellSimJob, File userDir, SBMLSymbolMapping sbmlMapping, MathSymbolMapping mathMapping){
        try {
            this.dataSetController = SpatialSBMLSimResults.initializeDataSetController(userDir);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found: `" + userDir.getAbsolutePath() + "`", e);
        }
        this.simId = SpatialSBMLSimResults.initializeSimulationIdentifier(vcellSimJob, userDir);
        this.jobIndex = vcellSimJob.getJobIndex();
        this.sbmlMapping = sbmlMapping;
        this.mathMapping = mathMapping;
        this.lazyAccessorMapping = new HashMap<>();
    }

    public int getMaxDataFlatLength() throws MathException, IOException, DataAccessException {
        var id = new VCSimulationDataIdentifier(this.simId, this.jobIndex);
        CartesianMesh mesh = this.dataSetController.getMesh(id);
        return mesh.getSizeZ() * mesh.getSizeY() * mesh.getSizeX() * this.dataSetController.getDataSetTimes(id).length;
    }

    public LazySBMLSpatialDataAccessor getSBMLDataAccessor(String sbmlId, UniformTimeCourse utcSim) throws MathException, IOException, DataAccessException {
        String key = SpatialSBMLSimResults.createUniqueKey(sbmlId, utcSim);
        if (this.lazyAccessorMapping.containsKey(key))return this.lazyAccessorMapping.get(key);
        LazySBMLSpatialDataAccessor newAccessor = new LazySBMLSpatialDataAccessor(this.generateCallable(sbmlId, utcSim), this.getMaxDataFlatLength());
        this.lazyAccessorMapping.put(key, newAccessor);
        return newAccessor;
    }

    private Callable<SBMLDataRecord> generateCallable(String vcellVarId, UniformTimeCourse utcSim){
        return new Callable<>() {
            /**
             * Access upon request the data desired from the appropriate
             *
             * @return computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            public SBMLDataRecord call() throws Exception {
                return SpatialSBMLSimResults.this.getDataForSBMLVar(vcellVarId, utcSim);
            }
        };
    }

    public SBMLDataRecord getDataForSBMLVar(String sbmlId, UniformTimeCourse utcSim)
            throws ExpressionException, DataAccessException, MathException, IOException {
        VCDataIdentifier vcDId = new VCSimulationDataIdentifier(this.simId, this.jobIndex);
        double[] times = this.getDesiredTimes(vcDId, utcSim);

        DataOperation dataOperation = new DataOperation.DataProcessingOutputInfoOP(vcDId,true, this.DEFAULT_OUTPUT_CONTEXT);
        DataOperationResults.DataProcessingOutputInfo results = (DataOperationResults.DataProcessingOutputInfo) this.dataSetController.doDataOperation(dataOperation);
        Set<String> varNamesSet = new HashSet<>(Arrays.asList(results.getVariableNames()));

        SBase sBase = this.sbmlMapping.getMappedSBase(sbmlId);
        if (sBase == null) throw new DataAccessException("Cannot find SBase for " + sbmlId);

        CartesianMesh mesh = this.dataSetController.getMesh(vcDId);

        SymbolTableEntry runtimeSte = this.sbmlMapping.getSte(sBase, SymbolContext.RUNTIME);
        SymbolTableEntry ste = (runtimeSte != null) ? runtimeSte : this.sbmlMapping.getSte(this.sbmlMapping.getMappedSBase(sbmlId), SymbolContext.INITIAL);
        if (ste instanceof Structure.StructureSize) ste = this.getStructureSizeSymbolTableEntry(ste, sbmlId);

        cbit.vcell.math.Variable mathVar = this.mathMapping.getVariable(ste);
        if (mathVar != null) return this.processSpatialData(mathVar, vcDId, mesh, varNamesSet, times);
        if (ste instanceof Kinetics.KineticsParameter lumpedRate && Kinetics.ROLE_LumpedReactionRate == lumpedRate.getRole())
            return this.processLumpedRate(sbmlId, vcDId, lumpedRate, times, mesh, varNamesSet);
        throw new RuntimeException("Math mapping couldn't find mathVar with ste: " + ste.getName());
    }

    private SBMLDataRecord processSpatialData(cbit.vcell.math.Variable mathVar, VCDataIdentifier vcDId, CartesianMesh mesh, Set<String> varNamesSet, double[] times) throws IOException, DataAccessException, ExpressionException {
        DataIdentifier[] dataIdentifiers = this.dataSetController.getDataIdentifiers(this.DEFAULT_OUTPUT_CONTEXT, vcDId);
        Set<String> identifierSet = Arrays.stream(dataIdentifiers).map(DataIdentifier::getName).collect(Collectors.toSet());
        identifierSet.addAll(varNamesSet);
        int meshStep = mesh.getSizeZ() * mesh.getSizeY() * mesh.getSizeX();
        double[] data;
        if (identifierSet.contains(mathVar.getName())) {
            data = new double[meshStep * times.length];
            for (int i = 0; i < times.length; i++) {
                SimDataBlock sdb = this.dataSetController.getSimDataBlock(this.DEFAULT_OUTPUT_CONTEXT, vcDId, mathVar.getName(), times[i]);
                double[] subData = sdb.getData();
                System.arraycopy(subData, 0, data, i * meshStep, subData.length);
            }
        } else if (mathVar instanceof Constant constMathVar){
            double value = constMathVar.getExpression().evaluateConstant();
            data = new double[meshStep * times.length];
            Arrays.fill(data, value);
        } else {
            throw new RuntimeException("Math mapping couldn't find mathVar with name: " + mathVar.getName());
        }

        return new SBMLDataRecord(data, List.of(times.length, mesh.getSizeZ(), mesh.getSizeY(), mesh.getSizeX()), times);
    }

    private SBMLDataRecord processLumpedRate(String sbmlId, VCDataIdentifier vcDId, Kinetics.KineticsParameter lumpedRate, double[] times, CartesianMesh mesh, Set<String> varNamesSet) throws ExpressionException, DataAccessException {
        // if reaction has been transformed to distributed, then retrieve distributed rate and multiply by compartment size.
        // find distributed rate by looking in MathSymbolMapping for a variable which is mapped to the same reaction and has ROLE_ReactionRate
        Stream<Kinetics.KineticsParameter> kineticsParameterStream = this.mathMapping.getMappedBiologicalSymbols().stream()
                .filter(Kinetics.KineticsParameter.class::isInstance).map(Kinetics.KineticsParameter.class::cast);
        Kinetics.KineticsParameter distributedRate =  kineticsParameterStream
                .filter((dRates) -> dRates.getRole()==Kinetics.ROLE_ReactionRate)
                .filter((dRates) -> dRates.getKinetics().getReactionStep()==lumpedRate.getKinetics().getReactionStep())
                .findFirst().orElseThrow(() -> new RuntimeException("failed to find VCell distributed reaction rate for sbml reaction: " + sbmlId));
        // find the math variable for the distributed rate
        cbit.vcell.math.Variable distributedRateMathVar = this.mathMapping.getVariable(distributedRate);
        // find compartment size by looking in MathSymbolMapping for a StructureMappingParameter which is mapped to the same compartment and has ROLE_Size
        Stream<StructureMapping.StructureMappingParameter> structureMappingParameterStream = this.mathMapping.getMappedBiologicalSymbols().stream()
                .filter(StructureMapping.StructureMappingParameter.class::isInstance).map(StructureMapping.StructureMappingParameter.class::cast);
        StructureMapping.StructureMappingParameter sizeParam = structureMappingParameterStream
                .filter((smp) -> smp.getRole() == StructureMapping.ROLE_Size)
                .filter((smp) -> smp.getStructure() == lumpedRate.getKinetics().getReactionStep().getStructure())
                .findFirst().orElseThrow(() -> new RuntimeException("failed to find VCell compartment size for sbml compartment: " + sbmlId));

        // find the math variable for the compartment size, and if it is of type Constant, get the value
        cbit.vcell.math.Variable sizeMathVar = this.mathMapping.getVariable(sizeParam);
        if (!(sizeMathVar instanceof Constant)) {
            throw new RuntimeException("expecting compartment size to be a constant");
        }
        double compartmentSize = sizeMathVar.getExpression().evaluateConstant();

        // if the distributed rate is in the result set, then multiply distributed rate by compartment size
        double[] data;
        int meshStep = mesh.getSizeZ() * mesh.getSizeY() * mesh.getSizeX();
        if (varNamesSet.contains(distributedRateMathVar.getName())) {
            data = new double[meshStep * times.length];
            for (int i = 0; i < times.length; i++) {
                SimDataBlock sdb = this.dataSetController.getSimDataBlock(this.DEFAULT_OUTPUT_CONTEXT, vcDId, distributedRateMathVar.getName(), times[i]);
                double[] subData = sdb.getData();
                for (int j = 0; j < subData.length; j++) {
                    data[i * meshStep + j] = subData[j] * compartmentSize;
                }
            }
        } else if (distributedRateMathVar instanceof Constant constDRMV) {
            // if the distributed rate is a constant, then multiply constant by compartment size
            data = new double[meshStep * times.length];
            Arrays.fill(data, constDRMV.getExpression().evaluateConstant() * compartmentSize);
        } else {
            throw new RuntimeException("failed to find VCell reaction rate for sbml reaction: " + sbmlId);
        }

        return new SBMLDataRecord(data, List.of(times.length, mesh.getSizeZ(), mesh.getSizeY(), mesh.getSizeX()), times);
    }

    private SymbolTableEntry getStructureSizeSymbolTableEntry(SymbolTableEntry ste, String sbmlId){
        for (SymbolTableEntry bioSte : this.mathMapping.getMappedBiologicalSymbols()) {
            if (!(bioSte instanceof StructureMapping.StructureMappingParameter param)) continue;
            if (param.getRole() != StructureMapping.ROLE_Size || param.getStructure().getStructureSize() != ste) continue;
            return param;
        }
        throw new RuntimeException("failed to find VCell structure size parameter for sbml compartment size: " + sbmlId);
    }

    private double[] getDesiredTimes(VCDataIdentifier vcDId, UniformTimeCourse utcSim) throws DataAccessException {
        double adjustedStartTime = utcSim.getOutputStartTime() - utcSim.getInitialTime();
        double[] finalTimes = new double[utcSim.getNumberOfSteps() + 1];
        double[] preTimes = this.dataSetController.getDataSetTimes(vcDId);
        int startIndex = -1;
        for (int i = 0; i < preTimes.length; i++) {
            if (preTimes[i] != adjustedStartTime) continue;
            startIndex = i;
            break;
        }
        if (startIndex < 0) throw new IllegalArgumentException("Time `" + adjustedStartTime + "` not found");
        double[] subArray = Arrays.copyOfRange(preTimes, startIndex, preTimes.length);
        double indexStep = (1.0 * subArray.length - 1) / (utcSim.getNumberOfSteps());
        if (indexStep % 1 != 0)
            throw new RuntimeException("Found incompatible sampling with respect to requested output points: `" + subArray.length + "` vs `" + utcSim.getNumberOfSteps() + 1 + "`");
        for (int i = 0, j = 0; i < utcSim.getNumberOfSteps() + 1; i++, j += (int)indexStep) {
            finalTimes[i] = subArray[j] + utcSim.getInitialTime();
        }
        return finalTimes;
    }

    private static DataSetControllerImpl initializeDataSetController(File userDir) throws FileNotFoundException {
        Cachetable cachetable = new Cachetable(2000,1000000L);
        return new DataSetControllerImpl(cachetable, userDir.getParentFile(), null);
    }

    private static VCSimulationIdentifier initializeSimulationIdentifier(SimulationJob vcellSimJob, File userDir){
        User user = new User(userDir.getName(), null);
        cbit.vcell.solver.Simulation vcellSim = vcellSimJob.getSimulation();
        return new VCSimulationIdentifier(vcellSim.getKey(), user);
    }

    private static String createUniqueKey(String sbmlId, UniformTimeCourse utcSim){
        return String.format("%s@%s", sbmlId, utcSim.toString());
    }
}
