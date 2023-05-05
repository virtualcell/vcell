package org.vcell.sbml;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
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

    public static SBMLResults fromOdeSolverResultSet(ODESolverResultSet odeSolverResultSet, SBMLSimulationSpec sbmlSimulationSpec) throws ExpressionException {
        List<String> columnNames = new ArrayList<>(Arrays.asList(sbmlSimulationSpec.variables));
        columnNames.add(0, "time");
        List<double[]> values = new ArrayList<>();
        values.add(odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn("t")));
        for (int col=1; col < columnNames.size(); col++) {
            String var = columnNames.get(col);
            int varIndex = odeSolverResultSet.findColumn(var);
            values.add(odeSolverResultSet.extractColumn(varIndex));
        }
        return new SBMLResults(columnNames, values);
    }

    public String toCSV() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.join(",", columnNames)+"\n");
        for (int row=0; row<values.get(0).length; row++){
            for (int col=0; col<columnNames.size(); col++){
                if (col > 0){
                    sb.append(",");
                }
                sb.append(values.get(col)[row]);
            }
            sb.append("\n");
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
                boolean bWithinTol = withinSbmlTestSuiteTolerance(
                        simSpec.absoluteTolerance, simSpec.relativeTolerance, thisColData[row], otherColData[row]);
                if (!bWithinTol){
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean withinSbmlTestSuiteTolerance(double tolAbs, double tolRel, double correctVal, double unknownVal){
        /**
         * | Cij − Uij |  ≤  ( Ta + Tr × | Cij | )
         */
        return Math.abs(correctVal - unknownVal) <= (tolAbs + tolRel * Math.abs(correctVal));
    }
}
