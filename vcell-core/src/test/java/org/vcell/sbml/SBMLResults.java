package org.vcell.sbml;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.SourceSymbolMapping;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.ode.ODESolverResultSet;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.vcell.sbml.vcell.SBMLSymbolMapping;
import org.vcell.sbml.vcell.SymbolContext;
import org.vcell.util.Compare;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SBMLResults {
    public final List<String> columnNames;
    public final List<double[]> values;

    /**
     * time,S1,S2
     * 0,0.00015,0
     * 0.1,0.0001357256127053939,1.427438729460607e-005
     * 0.2,0.0001228096129616973,2.719038703830272e-005
     */

    private SBMLResults(List<String> columnNames, List<double[]> values) {
        this.columnNames = columnNames;
        this.values = values;
    }

    public static SBMLResults fromCSV(String csvText) throws IOException, CsvException {
        StringReader reader = new StringReader(csvText);

        CSVReader csvReader = new CSVReaderBuilder(reader).build();
        List<String[]> allData = csvReader.readAll();

        List<String> columnNames = Arrays.asList(allData.get(0));
        int numColumns = columnNames.size();
        int numDataRows = allData.size()-1;

        List<double[]> values = new ArrayList<>();
        for (int i=0; i<numColumns; i++){
            values.add(new double[numDataRows]);
        }

        // print Data
        for (int row=0; row < numDataRows; row++) {
            for (int col=0; col < numColumns; col++) {
                values.get(col)[row] = Double.parseDouble(allData.get(row + 1)[col]);
            }
        }
        return new SBMLResults(columnNames, values);
    }

    public static SBMLResults fromOdeSolverResultSet(
            ODESolverResultSet odeSolverResultSet,
            SBMLSimulationSpec sbmlSimulationSpec,
            SimulationContext simulationContext,
            SBMLSymbolMapping sbmlSymbolMapping
    ) throws ExpressionException {
        List<String> columnNames = new ArrayList<>(Arrays.asList(sbmlSimulationSpec.variables));
        columnNames.add(0, "time");
        List<double[]> values = new ArrayList<>();
        values.add(odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn("t")));
        for (int col=1; col < columnNames.size(); col++) {
            String var = columnNames.get(col);
            int varIndex = odeSolverResultSet.findColumn(var);
            if (varIndex<0){
                SymbolTableEntry ste = sbmlSymbolMapping.getSte(sbmlSymbolMapping.getMappedSBase(var), SymbolContext.RUNTIME);
                if (ste==null){
                    ste = sbmlSymbolMapping.getSte(sbmlSymbolMapping.getMappedSBase(var), SymbolContext.INITIAL);
                }
                MathDescription mathDescription = simulationContext.getMathDescription();
                SourceSymbolMapping sourceSymbolMapping = mathDescription.getSourceSymbolMapping();
                Variable mathVar = sourceSymbolMapping.getVariable(ste);
                if (mathVar!=null) {
                    varIndex = odeSolverResultSet.findColumn(mathVar.getName());
                }
                if (varIndex<0) {
                    throw new RuntimeException("couldn't find var '" + var + "' in vcell sim results");
                }
            }
            double[] rawSolution = odeSolverResultSet.extractColumn(varIndex);
            if (Arrays.asList(sbmlSimulationSpec.concentrationVars).contains(var)){
                values.add(rawSolution);
            }else if (Arrays.asList(sbmlSimulationSpec.amountVars).contains(var)) {
                //
                // vcell solutions for species (for ODEs) are in rawSolution
                // since amounts are needed, must multiply by corresponding structure size
                //
                MathDescription mathDescription = simulationContext.getMathDescription();
                Variable mathVar = mathDescription.getVariable(var);
                SourceSymbolMapping sourceSymbolMapping = mathDescription.getSourceSymbolMapping();
                SymbolTableEntry[] vcellBioSymbols = sourceSymbolMapping.getBiologicalSymbol(mathVar);
                if (vcellBioSymbols==null || !(vcellBioSymbols[0] instanceof SpeciesContext)) {
                    throw new RuntimeException("amount required, failed to find SpeciesContext for var '" + var + "'");
                }
                SpeciesContext speciesContext = (SpeciesContext) vcellBioSymbols[0];
                Structure structure = speciesContext.getStructure();
                StructureMapping.StructureMappingParameter sizeParameter =
                        simulationContext.getGeometryContext().getStructureMapping(structure).getSizeParameter();
                Variable structSizeVar = sourceSymbolMapping.getVariable(sizeParameter);
                Expression sizeExp = structSizeVar.getExpression().flatten();
                if (!sizeExp.isNumeric()){
                    throw new RuntimeException("failed to find Structure size for '"+structure.getName()+"' needed for variable '"+var+"'");
                }
                double structureSize = sizeExp.evaluateConstant();
                double[] amounts = Arrays.stream(rawSolution).map(c -> c * structureSize).toArray();
                values.add(amounts);
            }else{
                values.add(rawSolution);
            }
        }
        return new SBMLResults(columnNames, values);
    }

    public String toCSV() {
        List<String> lines = toCsvLines(false);
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<lines.size(); i++){
            sb.append(lines.get(i)).append("\n");
        }
        return sb.toString();
    }

    private List<String> toCsvLines(boolean bSkipTime) {
        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", columnNames.subList(bSkipTime ? 1 : 0, columnNames.size())));
        for (int row=0; row<values.get(0).length; row++){
            StringBuffer sb = new StringBuffer();
            int startCol = bSkipTime ? 1 : 0;
            for (int col=startCol; col<columnNames.size(); col++){
                if (col > startCol){
                    sb.append(",");
                }
                sb.append(values.get(col)[row]);
            }
            lines.add(sb.toString());
        }
        return lines;
    }

    public static String toCSV(SBMLResults results1, SBMLResults results2) {
        List<String> lines1 = results1.toCsvLines(false);
        List<String> lines2 = results2.toCsvLines(true);
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<lines1.size(); i++){
            sb.append(lines1.get(i))
                    .append(",")
                    .append(lines2.get(i))
                    .append("\n");
        }
        return sb.toString();
    }

    public static boolean compareEquivalent(SBMLResults correctResults, SBMLResults computedResults, SBMLSimulationSpec simSpec) {
        if (!Compare.isEqual(
                computedResults.columnNames.toArray(new String[0]),
                computedResults.columnNames.toArray(new String[0]))){
            return false;
        }
        for (int col=0; col < correctResults.columnNames.size(); col++){
            double[] thisColData = correctResults.values.get(col);
            double[] otherColData = computedResults.values.get(col);
            for (int row=0; row < thisColData.length; row++){
                double err = error(
                        simSpec.absoluteTolerance, simSpec.relativeTolerance, thisColData[row], otherColData[row]);
                if (err > 1.0){
                    return false;
                }
            }
        }
        return true;
    }

    public static double error(double tolAbs, double tolRel, double correctVal, double unknownVal){
        /**
         * | Cij − Uij |  ≤  ( Ta + Tr × | Cij | )
         */
        return Math.abs(correctVal - unknownVal) / (tolAbs + tolRel * Math.abs(correctVal));
    }
}
