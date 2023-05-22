package org.vcell.cli.run;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.SourceSymbolMapping;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.ode.ODESolverResultSet;
import org.jlibsedml.Variable;
import org.sbml.jsbml.SBase;
import org.vcell.sbml.vcell.SBMLSymbolMapping;
import org.vcell.sbml.vcell.SymbolContext;

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

        if (column < 0){
            SBase sBase = this.sbmlMapping.getMappedSBase(sbmlId);
            if (sBase == null){
                throw new RuntimeException();
            }
            SymbolTableEntry ste = this.sbmlMapping.getSte(sBase, SymbolContext.RUNTIME);
            if (ste==null){
                ste = this.sbmlMapping.getSte(this.sbmlMapping.getMappedSBase(sbmlId), SymbolContext.INITIAL);
            }

            if (ste instanceof Structure.StructureSize){
                for (SymbolTableEntry bioSte : this.mathMapping.getMappedBiologicalSymbols()){
                    if (!(bioSte instanceof StructureMapping.StructureMappingParameter)) continue;
                    StructureMapping.StructureMappingParameter param = (StructureMapping.StructureMappingParameter) bioSte;
                    if (param.getRole() == StructureMapping.ROLE_Size &&
                            param.getStructure().getStructureSize() == ste){
                        ste = param;
                    }

                }
            }

            cbit.vcell.math.Variable mathVar = this.mathMapping.getVariable(ste);
            if (mathVar == null) throw new RuntimeException("Math mapping couldn't find mathVar with ste: " + ste.getName());
            
            int varIndex = this.resultSet.findColumn(mathVar.getName());
            if (varIndex<0) {
                if (mathVar instanceof Constant){
                    double value = mathVar.getExpression().evaluateConstant();
                    data = new double[this.resultSet.getRowCount()];
                    Arrays.fill(data, value);
                } else {
                    throw new RuntimeException("couldn't find var '" + sbmlId + "' in vcell sim results");
                }
            }
        }

        if (data == null)
            data = this.resultSet.extractColumn(column);
        if (outputStartTime == 0)
            return data;

        double[] adjData = new double[outputNumberOfPoints + 1];
        for (int i = data.length - outputNumberOfPoints - 1, j = 0; i < data.length; i++, j++) {
            adjData[j] = data[i];
        }
        return adjData;
    }
}
