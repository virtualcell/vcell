package org.vcell.cli.run;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.SourceSymbolMapping;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.ode.ODESolverResultSet;
import org.jlibsedml.Variable;
import org.vcell.sbml.vcell.SBMLSymbolMapping;
import org.vcell.sbml.vcell.SymbolContext;

public class SBMLNonspatialSimResults {

    private ODESolverResultSet resultSet;
    private SBMLSymbolMapping sbmlMapping;

    private SourceSymbolMapping mathMapping;

    public SBMLNonspatialSimResults(ODESolverResultSet resultSet, SBMLSymbolMapping sbmlMapping, SourceSymbolMapping mathMapping){
        this.resultSet = resultSet;
        this.sbmlMapping = sbmlMapping;
        this.mathMapping = mathMapping;
    }

    public double[] getDataForSBMLVar(String sbmlId, double outputStartTime, int outputNumberOfPoints)
            throws ExpressionException {
        int column = this.resultSet.findColumn(sbmlId) ;

        if (column < 0){

            SymbolTableEntry ste = this.sbmlMapping.getSte(this.sbmlMapping.getMappedSBase(sbmlId), SymbolContext.RUNTIME);
            if (ste==null){
                ste = this.sbmlMapping.getSte(this.sbmlMapping.getMappedSBase(sbmlId), SymbolContext.INITIAL);
            }

            cbit.vcell.math.Variable mathVar = this.mathMapping.getVariable(ste);
            if (mathVar == null) throw new RuntimeException("Math mapping couldn't find mathVar with ste: " + ste.getName());


            int varIndex = this.resultSet.findColumn(mathVar.getName());
            if (varIndex<0) {
                throw new RuntimeException("couldn't find var '" + sbmlId + "' in vcell sim results");
            }
        }

        double[] data = this.resultSet.extractColumn(column);
        if (outputStartTime == 0)
            return data;

        double[] adjData = new double[outputNumberOfPoints + 1];
        for (int i = data.length - outputNumberOfPoints - 1, j = 0; i < data.length; i++, j++) {
            adjData[j] = data[i];
        }
        return adjData;
    }
}
