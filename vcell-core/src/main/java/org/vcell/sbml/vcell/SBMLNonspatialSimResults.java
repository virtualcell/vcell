package org.vcell.sbml.vcell;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.ode.ODESolverResultSet;
import org.sbml.jsbml.SBase;

import java.util.Arrays;

public class SBMLNonspatialSimResults {

    private ODESolverResultSet resultSet;
    private SBMLSymbolMapping sbmlMapping;

    private MathSymbolMapping mathMapping;

    public SBMLNonspatialSimResults(ODESolverResultSet resultSet, SBMLSymbolMapping sbmlMapping, MathSymbolMapping mathMapping){
        this.resultSet = resultSet;
        this.sbmlMapping = sbmlMapping;
        this.mathMapping = mathMapping;
    }

    public double[] getDataForSBMLVar(String sbmlId, double outputStartTime, int outputNumberOfPoints)
            throws ExpressionException {
        int column = this.resultSet.findColumn(sbmlId) ;
        double[] data = null;

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
                    if (!(bioSte instanceof StructureMapping.StructureMappingParameter)) continue;
                    StructureMapping.StructureMappingParameter param = (StructureMapping.StructureMappingParameter) bioSte;
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

            if (ste instanceof Kinetics.KineticsParameter
                    && ((Kinetics.KineticsParameter)ste).getRole()==Kinetics.ROLE_LumpedReactionRate
                    && this.mathMapping.getVariable(ste) == null
            ) {
                // if reaction has been transformed to distributed, then retrieve distributed rate and multiply by compartment size.
                // find distributed rate by looking in MathSymbolMapping for a variable which is mapped to the same reaction and has ROLE_ReactionRate
                Kinetics.KineticsParameter lumpedRate = (Kinetics.KineticsParameter) ste;
                Kinetics.KineticsParameter distributedRate = mathMapping.getMappedBiologicalSymbols().stream()
                        .filter(mappedSte -> mappedSte instanceof Kinetics.KineticsParameter
                                && ((Kinetics.KineticsParameter)mappedSte).getRole()==Kinetics.ROLE_ReactionRate
                                && ((Kinetics.KineticsParameter)mappedSte).getKinetics().getReactionStep()==lumpedRate.getKinetics().getReactionStep())
                        .map(mappedSte -> (Kinetics.KineticsParameter)mappedSte)
                        .findFirst().orElseThrow(() -> new RuntimeException("failed to find VCell distributed reaction rate for sbml reaction: " + sbmlId));
                // find the math variable for the distributed rate
                cbit.vcell.math.Variable distributedRateMathVar = this.mathMapping.getVariable(distributedRate);
                // find compartment size by looking in MathSymbolMapping for a StructureMappingParameter which is mapped to the same compartment and has ROLE_Size
                StructureMapping.StructureMappingParameter sizeParam = mathMapping.getMappedBiologicalSymbols().stream()
                        .filter(mappedSte -> mappedSte instanceof StructureMapping.StructureMappingParameter
                                && ((StructureMapping.StructureMappingParameter)mappedSte).getRole()==StructureMapping.ROLE_Size
                                && ((StructureMapping.StructureMappingParameter)mappedSte).getStructure()==lumpedRate.getKinetics().getReactionStep().getStructure())
                        .map(mappedSte -> (StructureMapping.StructureMappingParameter)mappedSte)
                        .findFirst().orElseThrow(() -> new RuntimeException("failed to find VCell compartment size for sbml compartment: " + sbmlId));
                // find the math variable for the compartment size, and if it is of type Constant, get the value
                cbit.vcell.math.Variable sizeMathVar = this.mathMapping.getVariable(sizeParam);
                if (!(sizeMathVar instanceof Constant)) {
                    throw new RuntimeException("expecting compartment size to be a constant");
                }
                double compartmentSize = sizeMathVar.getExpression().evaluateConstant();
                // if the distributed rate is in the result set, then multiply distributed rate by compartment size
                if (this.resultSet.findColumn(distributedRateMathVar.getName()) >= 0) {
                    data = this.resultSet.extractColumn(this.resultSet.findColumn(distributedRateMathVar.getName()));
                    for (int i = 0; i < data.length; i++) {
                        data[i] *= compartmentSize;
                    }
                }else if (distributedRateMathVar instanceof Constant) {
                    // if the distributed rate is a constant, then multiply constant by compartment size
                    data = new double[this.resultSet.getRowCount()];
                    Arrays.fill(data, ((Constant)distributedRateMathVar).getExpression().evaluateConstant() * compartmentSize);
                }else {
                    throw new RuntimeException("failed to find VCell reaction rate for sbml reaction: " + sbmlId);
                }

                return getRequestedDataVector(outputStartTime, outputNumberOfPoints, data);
            }

            cbit.vcell.math.Variable mathVar = this.mathMapping.getVariable(ste);
            if (mathVar == null) {
                throw new RuntimeException("Math mapping couldn't find mathVar with ste: " + ste.getName());
            }

            int varIndex = this.resultSet.findColumn(mathVar.getName());
            if (varIndex < 0) {
                if (mathVar instanceof Constant) {
                    double value = mathVar.getExpression().evaluateConstant();
                    data = new double[this.resultSet.getRowCount()];
                    Arrays.fill(data, value);
                }
            } else {
                data = this.resultSet.extractColumn(varIndex);
            }

            if (data == null) {
                throw new RuntimeException("couldn't find var '" + sbmlId + "' in vcell sim results");
            }
        } else { // found column
            data = this.resultSet.extractColumn(column);
        }

        return getRequestedDataVector(outputStartTime, outputNumberOfPoints, data);
    }

    private static double[] getRequestedDataVector(double outputStartTime, int outputNumberOfPoints, double[] data) {
        if (outputStartTime == 0)
            return data;

        double[] adjData = new double[outputNumberOfPoints + 1];
        for (int i = data.length - outputNumberOfPoints - 1, j = 0; i < data.length; i++, j++) {
            adjData[j] = data[i];
        }
        return adjData;
    }
}
