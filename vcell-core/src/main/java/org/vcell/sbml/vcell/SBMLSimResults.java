package org.vcell.sbml.vcell;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathException;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Kinetics;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.UniformTimeCourse;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public abstract class SBMLSimResults {
    private static final Logger lg = LogManager.getLogger(SBMLSimResults.class);

    protected final OutputContext DEFAULT_OUTPUT_CONTEXT = new OutputContext(new AnnotatedFunction[] {});
    protected final SBMLSymbolMapping sbmlMapping;
    protected final MathSymbolMapping mathMapping;
    protected final Map<String, LazySBMLDataAccessor> lazyAccessorMapping;

    protected SBMLSimResults(SBMLSymbolMapping sbmlMapping, MathSymbolMapping mathMapping){
        this.sbmlMapping = sbmlMapping;
        this.mathMapping = mathMapping;
        this.lazyAccessorMapping = new HashMap<>();
    }

    public abstract double[] getOriginalTimes();

    public abstract int getMaxDataFlatLength() throws MathException, IOException, DataAccessException;

    public abstract List<Integer> getSpatialDimensions();

    public abstract LazySBMLDataAccessor getSBMLDataAccessor(String sbmlId, UniformTimeCourse utcSim) throws MathException, IOException, DataAccessException;

    protected abstract Callable<SBMLDataRecord> generateCallable(String vcellVarId, UniformTimeCourse utcSim);

    public abstract SBMLDataRecord getDataForSBMLVar(String sbmlId, UniformTimeCourse utcSim) throws IOException, DataAccessException, ExpressionException;

    protected abstract double[] getRawDataAtAllTimes(String varName) throws ExpressionException, DataAccessException;

    protected SBMLDataRecord getDataForSBMLVar(Variable mathVar, SymbolTableEntry ste, Set<String> varNamesSet, List<Integer> spatialDimensions, double[] times, UniformTimeCourse utcSim) throws IOException, DataAccessException, ExpressionException {
        if (mathVar != null)
            return this.processData(mathVar, varNamesSet, spatialDimensions, times, utcSim.getInitialTime());
        if (ste instanceof Kinetics.KineticsParameter lumpedRate
                && Kinetics.ROLE_LumpedReactionRate == lumpedRate.getRole())
            return this.processLumpedRate(lumpedRate, varNamesSet, spatialDimensions, times, utcSim.getInitialTime());

        throw new RuntimeException("Math mapping couldn't find mathVar with ste: " + ste.getName());
    }

    private SBMLDataRecord processData(cbit.vcell.math.Variable mathVar, Set<String> identifierSet, List<Integer> spatialDimensions, double[] times, double initialTimeAdjustment) throws IOException, DataAccessException, ExpressionException {
        double[] data;
        List<Integer> sDims = spatialDimensions == null? new LinkedList<>() : spatialDimensions;
        int numOfElementsAtEachTime = sDims.stream().reduce(1, (x, y)-> x * y);
        if (numOfElementsAtEachTime < 1){
            lg.warn("Negative or 0 number of elements per step-size detected");
            numOfElementsAtEachTime = 1;
        }
        /// TODO: Evaluate how to adjust non-spatial time data!
        if (identifierSet.contains(mathVar.getName())) {
            data = this.loadData(mathVar.getName(), times, numOfElementsAtEachTime, 1);
        } else if (mathVar instanceof Constant constMathVar){
            double value = constMathVar.getExpression().evaluateConstant();
            data = new double[numOfElementsAtEachTime * times.length];
            Arrays.fill(data, value);
        } else {
            throw new RuntimeException("Math mapping couldn't find mathVar with name: " + mathVar.getName());
        }

        List<Integer> dataDimensions = new ArrayList<>(List.of(times.length));
        for (int dim : sDims) if (dim != 1 || dataDimensions.size() > 1) dataDimensions.add(dim);
        return new SBMLDataRecord(data, dataDimensions, initialTimeAdjustment == 0.0 ? times : Arrays.stream(times).map((x)-> x + initialTimeAdjustment).toArray());
    }

    private SBMLDataRecord processLumpedRate(Kinetics.KineticsParameter lumpedRate, Set<String> varNamesSet, List<Integer> spatialDimensions, double[] times, double initialTimeAdjustment) throws ExpressionException, DataAccessException {
        // if reaction has been transformed to distributed, then retrieve distributed rate and multiply by compartment size.
        // find distributed rate by looking in MathSymbolMapping for a variable which is mapped to the same reaction and has ROLE_ReactionRate
        Stream<Kinetics.KineticsParameter> kineticsParameterStream = this.mathMapping.getMappedBiologicalSymbols().stream()
                .filter(Kinetics.KineticsParameter.class::isInstance).map(Kinetics.KineticsParameter.class::cast);
        Kinetics.KineticsParameter distributedRate =  kineticsParameterStream
                .filter((dRates) -> dRates.getRole()==Kinetics.ROLE_ReactionRate)
                .filter((dRates) -> dRates.getKinetics().getReactionStep()==lumpedRate.getKinetics().getReactionStep())
                .findFirst().orElseThrow(() -> new RuntimeException("failed to find VCell distributed reaction rate for sbml reaction: "));
        // find the math variable for the distributed rate
        cbit.vcell.math.Variable distributedRateMathVar = this.mathMapping.getVariable(distributedRate);
        // find compartment size by looking in MathSymbolMapping for a StructureMappingParameter which is mapped to the same compartment and has ROLE_Size
        Stream<StructureMapping.StructureMappingParameter> structureMappingParameterStream = this.mathMapping.getMappedBiologicalSymbols().stream()
                .filter(StructureMapping.StructureMappingParameter.class::isInstance).map(StructureMapping.StructureMappingParameter.class::cast);
        StructureMapping.StructureMappingParameter sizeParam = structureMappingParameterStream
                .filter((smp) -> smp.getRole() == StructureMapping.ROLE_Size)
                .filter((smp) -> smp.getStructure() == lumpedRate.getKinetics().getReactionStep().getStructure())
                .findFirst().orElseThrow(() -> new RuntimeException("failed to find VCell compartment size for sbml compartment: "));

        // find the math variable for the compartment size, and if it is of type Constant, get the value
        cbit.vcell.math.Variable sizeMathVar = this.mathMapping.getVariable(sizeParam);
        if (!(sizeMathVar instanceof Constant)) {
            throw new RuntimeException("expecting compartment size to be a constant");
        }
        double compartmentSize = sizeMathVar.getExpression().evaluateConstant();
        List<Integer> sDims = spatialDimensions == null? new LinkedList<>() : spatialDimensions;
        int numOfElementsAtEachTime = sDims.stream().reduce(1, (x, y)-> x * y);
        if (numOfElementsAtEachTime < 1){
            lg.warn("Negative or 0 number of elements per step-size detected");
            numOfElementsAtEachTime = 1;
        }

        // if the distributed rate is in the result set, then multiply distributed rate by compartment size
        double[] data;
        if (varNamesSet.contains(distributedRateMathVar.getName())) {
            data = this.loadData(distributedRateMathVar.getName(), times, numOfElementsAtEachTime, compartmentSize);

        } else if (distributedRateMathVar instanceof Constant constDRMV) {
            // if the distributed rate is a constant, then multiply constant by compartment size
            data = new double[numOfElementsAtEachTime * times.length];
            Arrays.fill(data, constDRMV.getExpression().evaluateConstant() * compartmentSize);
        } else {
            throw new RuntimeException("failed to find VCell reaction rate for sbml reaction: ");
        }
        List<Integer> dataDimensions = new ArrayList<>(List.of(times.length));
        for (int dim : sDims) if (dim != 1 || dataDimensions.size() > 1) dataDimensions.add(dim);

        return new SBMLDataRecord(data, dataDimensions, initialTimeAdjustment == 1.0 ? times : Arrays.stream(times).map((x)-> x + initialTimeAdjustment).toArray());
    }

    protected SymbolTableEntry getStructureSizeSymbolTableEntry(SymbolTableEntry ste, String sbmlId){
        for (SymbolTableEntry bioSte : this.mathMapping.getMappedBiologicalSymbols()) {
            if (!(bioSte instanceof StructureMapping.StructureMappingParameter param)) continue;
            if (param.getRole() != StructureMapping.ROLE_Size || param.getStructure().getStructureSize() != ste) continue;
            return param;
        }
        throw new RuntimeException("failed to find VCell structure size parameter for sbml compartment size: " + sbmlId);
    }

    protected double[] getDesiredTimes(UniformTimeCourse utcSim) {
        double adjustedStartTime = utcSim.getOutputStartTime() - utcSim.getInitialTime();
        double[] finalTimes = new double[utcSim.getNumberOfSteps() + 1];
        double[] preTimes = this.getOriginalTimes();
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
            //finalTimes[i] = subArray[j] + utcSim.getInitialTime();
            finalTimes[i] = subArray[j]; // We will account for initial time at the end!
        }
        return finalTimes;
    }

    protected static DataSetControllerImpl initializeDataSetController(File userDir) throws FileNotFoundException {
        Cachetable cachetable = new Cachetable(2000,1000000L);
        return new DataSetControllerImpl(cachetable, userDir.getParentFile(), null);
    }

    protected static String createUniqueKey(String sbmlId, UniformTimeCourse utcSim){
        return String.format("%s@%s", sbmlId, utcSim.toString());
    }

    private double[] loadData(String varName, double[] times, int numberOfElementsPerTimestep, double scalingFactor) throws DataAccessException, ExpressionException {
//        double[] data = new double[numberOfElementsPerTimestep * times.length];
        double[] preprocessedData = this.getRawDataAtAllTimes(varName);
        return scalingFactor == 1.0 ? preprocessedData: Arrays.stream(preprocessedData).map((x)-> x * scalingFactor).toArray();

//        for (int i = 0; i < times.length; i++) {
//            double[] preprocessedData = this.getRawDataAtAllTimes(varName);
//            double[] subData = scalingFactor == 1.0 ? preprocessedData: Arrays.stream(preprocessedData).map((x)-> x * scalingFactor).toArray();
//            System.arraycopy(subData, 0, data, i * numberOfElementsPerTimestep, subData.length);
//        }
//        return data;
    }
}
