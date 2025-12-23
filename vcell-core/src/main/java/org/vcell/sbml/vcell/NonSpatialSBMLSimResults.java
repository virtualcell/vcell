package org.vcell.sbml.vcell;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.ode.ODESolverResultSet;
import org.jlibsedml.components.simulation.UniformTimeCourse;
import org.sbml.jsbml.SBase;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class NonSpatialSBMLSimResults {

    private final ODESolverResultSet resultSet;
    private final SBMLSymbolMapping sbmlMapping;

    private final MathSymbolMapping mathMapping;

    private final Map<String, LazySBMLNonSpatialDataAccessor> lazyAccessorMapping;

    public NonSpatialSBMLSimResults(ODESolverResultSet resultSet, SBMLSymbolMapping sbmlMapping, MathSymbolMapping mathMapping){
        this.resultSet = resultSet;
        this.sbmlMapping = sbmlMapping;
        this.mathMapping = mathMapping;
        this.lazyAccessorMapping = new HashMap<>();
    }

    public int getMaxDataFlatLength(){
        return this.resultSet.getRowCount();
    }

    public LazySBMLNonSpatialDataAccessor getSBMLDataAccessor(String sbmlId, UniformTimeCourse utcSim, int desiredLength){
        String key = NonSpatialSBMLSimResults.createUniqueKey(sbmlId, utcSim);
        if (this.lazyAccessorMapping.containsKey(key))return this.lazyAccessorMapping.get(key);
        LazySBMLNonSpatialDataAccessor newAccessor = new LazySBMLNonSpatialDataAccessor(this.generateCallable(sbmlId, utcSim, desiredLength), desiredLength);
        this.lazyAccessorMapping.put(key, newAccessor);
        return newAccessor;
    }

    private Callable<SBMLDataRecord> generateCallable(String sbmlId, UniformTimeCourse utcSim, int desiredLength){
        return new Callable<>() {
            /**
             * Access upon request the data desired from the appropriate
             *
             * @return computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            public SBMLDataRecord call() throws Exception {
                return NonSpatialSBMLSimResults.this.getSBMLVarData(sbmlId, utcSim, desiredLength);
            }
        };
    }

    // We want to do lazy data fetching
    private SBMLDataRecord getSBMLVarData(String sbmlId, UniformTimeCourse utcSim, int desiredLength)
            throws ExpressionException {
        int column = this.resultSet.findColumn(sbmlId) ;
        //double[] data = null;
        double[] processedData = new double[desiredLength];

        if (column < 0) {
            SBase sBase = this.sbmlMapping.getMappedSBase(sbmlId);
            if (sBase == null) {
                throw new RuntimeException("failed to find VCell symbol for sbml id: "+sbmlId);
            }
            SymbolTableEntry ste = this.sbmlMapping.getSte(sBase, SymbolContext.RUNTIME);
            if (ste == null) {
                ste = this.sbmlMapping.getSte(this.sbmlMapping.getMappedSBase(sbmlId), SymbolContext.INITIAL);
            }

            if (ste instanceof Structure.StructureSize) {
                StructureMapping.StructureMappingParameter sizeParam = null;
                for (SymbolTableEntry bioSte : this.mathMapping.getMappedBiologicalSymbols()) {
                    if (!(bioSte instanceof StructureMapping.StructureMappingParameter param)) continue;
                    if (param.getRole() == StructureMapping.ROLE_Size &&
                            param.getStructure().getStructureSize() == ste) {
                        sizeParam = param;
                        break;
                    }
                }
                if (sizeParam != null){
                    ste = sizeParam;
                }else {
                    throw new RuntimeException("failed to find VCell structure size parameter for sbml compartment size: " + sbmlId);
                }
            }

            if (ste instanceof Kinetics.KineticsParameter lumpedRate && lumpedRate.getRole() == Kinetics.ROLE_LumpedReactionRate
                    && this.mathMapping.getVariable(ste) == null
            ) {
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
                if (this.resultSet.findColumn(distributedRateMathVar.getName()) >= 0) {
                    double[] data = this.resultSet.extractColumn(this.resultSet.findColumn(distributedRateMathVar.getName()));
                    double[] adjustedData = NonSpatialSBMLSimResults.getRequestedDataVector(data.length - processedData.length, processedData.length, data);
                    System.arraycopy(adjustedData, 0, processedData, 0, adjustedData.length);
                    for (int i = 0; i < processedData.length; i++) {
                        processedData[i] *= compartmentSize;
                    }
                } else if (distributedRateMathVar instanceof Constant constDRMV) {
                    // if the distributed rate is a constant, then multiply constant by compartment size
                    double[] data = new double[processedData.length];
                    Arrays.fill(data, constDRMV.getExpression().evaluateConstant() * compartmentSize);
                    System.arraycopy(data, 0, processedData, 0, data.length);
                } else {
                    throw new RuntimeException("failed to find VCell reaction rate for sbml reaction: " + sbmlId);
                }

                double[] vector = new double[desiredLength];
                Arrays.fill(vector, Double.NaN);
                System.arraycopy(processedData, 0, vector, 0, processedData.length);
                return new SBMLDataRecord(vector, List.of(vector.length), null);
            }

            cbit.vcell.math.Variable mathVar = this.mathMapping.getVariable(ste);
            if (mathVar == null) {
                throw new RuntimeException("Math mapping couldn't find mathVar with ste: " + ste.getName());
            }
            double[] data = null;
            int varIndex = this.resultSet.findColumn(mathVar.getName());

            if (varIndex >= 0){
                data = this.resultSet.extractColumn(varIndex);
            } else if (mathVar instanceof Constant){
                double value = mathVar.getExpression().evaluateConstant();
                data = new double[this.resultSet.getRowCount()];
                Arrays.fill(data, value);
            }

            if (data == null) throw new RuntimeException("couldn't find var '" + sbmlId + "' in vcell sim results");
            double[] formattedData = NonSpatialSBMLSimResults.getRequestedDataVector(data.length - processedData.length, processedData.length, data);
            System.arraycopy(formattedData, 0, processedData, 0, formattedData.length);
        } else { // found column
            double[] data = this.resultSet.extractColumn(column);
            if ("t".equals(sbmlId)) for (int i = 0; i < data.length; i++) data[i] += utcSim.getInitialTime();
            double[] formattedData = NonSpatialSBMLSimResults.getRequestedDataVector(data.length - processedData.length, processedData.length, data);
            System.arraycopy(formattedData, 0, processedData, 0, formattedData.length);
        }

        //double[] vector = NonSpatialSBMLSimResults.getRequestedDataVector(utcSim.getOutputStartTime() - utcSim.getInitialTime(), utcSim.getNumberOfSteps() + 1, processedData);
        double[] vector = new double[desiredLength];
        Arrays.fill(vector, Double.NaN);
        System.arraycopy(processedData, 0, vector, 0, processedData.length);

        return new SBMLDataRecord(vector, List.of(vector.length), null);
    }

    /**
     * note that because output start time isn't directly used in math calculations,
     * we can use the value as a truthy value instead.
     *
     * @param outputStartTime used to determine if a non-zero start time is used
     * @param outputNumberOfPoints size of the output array
     * @param data the source data
     * @return an adjusted data array
     */
    private static double[] getRequestedDataVector(double outputStartTime, int outputNumberOfPoints, double[] data) {
        if (outputStartTime == 0) return data;

        double[] adjData = new double[outputNumberOfPoints];
        int statingPosition = Math.max(0, data.length - outputNumberOfPoints - 1);
        if (outputNumberOfPoints > data.length) Arrays.fill(adjData, Double.NaN);
        System.arraycopy(data, statingPosition, adjData, 0, Math.min(outputNumberOfPoints, data.length));
        return adjData;
    }

    private static String createUniqueKey(String sbmlId, UniformTimeCourse utcSim){
        return String.format("%s@%s", sbmlId, utcSim.toString());
    }
}
