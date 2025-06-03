package org.vcell.sbml.vcell;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.jlibsedml.UniformTimeCourse;
import org.sbml.jsbml.SBase;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;
import org.vcell.util.DataAccessException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class NonSpatialSBMLSimResults extends SBMLSimResults {
    private final ODESolverResultSet resultSet;
    private final double[] originalTimes;
    private final Map<Double, Integer> timeIndexMapping;

    public NonSpatialSBMLSimResults(ODESolverResultSet resultSet, UniformTimeCourse utcSim, SBMLSymbolMapping sbmlMapping, MathSymbolMapping mathMapping, boolean resultsNeedInterpolation) throws ExpressionException {
        super(sbmlMapping, mathMapping);
        this.resultSet = resultsNeedInterpolation ? NonSpatialSBMLSimResults.interpolate(resultSet, utcSim) : resultSet;
        this.originalTimes = this.getRawDataAtAllTimes("t");
        this.timeIndexMapping = new HashMap<>();
        for (int i = 0; i < this.originalTimes.length; i++) {
            this.timeIndexMapping.put(this.originalTimes[i], i);
        }
    }

    @Override
    public double[] getOriginalTimes() {
        return this.originalTimes;
    }

    @Override
    public int getMaxDataFlatLength() {
        return this.resultSet.getRowCount();
    }

    @Override
    public List<Integer> getSpatialDimensions() {
        return List.of(1);
    }

    @Override
    public LazySBMLDataAccessor getSBMLDataAccessor(String sbmlId, UniformTimeCourse utcSim) {
        String key = SpatialSBMLSimResults.createUniqueKey(sbmlId, utcSim);
        if (this.lazyAccessorMapping.containsKey(key))return this.lazyAccessorMapping.get(key);
        LazySBMLNonSpatialDataAccessor newAccessor = new LazySBMLNonSpatialDataAccessor(this.generateCallable(sbmlId, utcSim), this.getMaxDataFlatLength(), Arrays.stream(this.getDesiredTimes(utcSim)).boxed().toList());
        this.lazyAccessorMapping.put(key, newAccessor);
        return newAccessor;
    }

    @Override
    protected Callable<SBMLDataRecord> generateCallable(String vcellVarId, UniformTimeCourse utcSim) {
        return new Callable<>() {
            /**
             * Access upon request the data desired from the appropriate
             *
             * @return computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            public SBMLDataRecord call() throws Exception {
                return NonSpatialSBMLSimResults.this.getDataForSBMLVar(vcellVarId, utcSim);
            }
        };
    }

    @Override
    public SBMLDataRecord getDataForSBMLVar(String sbmlId, UniformTimeCourse utcSim) throws ExpressionException, IOException, DataAccessException {
        int column = this.resultSet.findColumn(sbmlId);
        return column < 0 ? this.trackDownDataToBuildRecord(sbmlId, utcSim): this.buildDataRecord(column, utcSim);
    }

    private SBMLDataRecord buildDataRecord(int columnId, UniformTimeCourse utcSim) throws ExpressionException {
        double[] times = this.getDesiredTimes(utcSim);
        double[] rawData = this.resultSet.extractColumn(columnId);
        double[] data = Arrays.stream(times).map((x)->rawData[NonSpatialSBMLSimResults.this.timeIndexMapping.get(x)]).toArray();
        return new SBMLDataRecord(data, List.of(data.length), times);
    }

    private SBMLDataRecord trackDownDataToBuildRecord(String sbmlId, UniformTimeCourse utcSim) throws IOException, DataAccessException, ExpressionException {
        SBase sBase = this.sbmlMapping.getMappedSBase(sbmlId);
        if (sBase == null) throw new RuntimeException("failed to find VCell symbol for sbml id: "+sbmlId);

        SymbolTableEntry ste = this.sbmlMapping.getSte(sBase, SymbolContext.RUNTIME);
        if (ste == null) ste = this.sbmlMapping.getSte(this.sbmlMapping.getMappedSBase(sbmlId), SymbolContext.INITIAL);

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
            if (sizeParam == null)
                throw new RuntimeException("failed to find VCell structure size parameter for sbml compartment size: " + sbmlId);
            ste = sizeParam;
        }

        Set<String> identifiers = Arrays.stream(this.resultSet.getColumnDescriptions()).map(ColumnDescription::getName).collect(Collectors.toSet());
        return this.getDataForSBMLVar(this.mathMapping.getVariable(ste), ste, identifiers, List.of(1), this.getDesiredTimes(utcSim), utcSim);
    }


    @Override
    protected double[] getRawDataAtAllTimes(String varName) throws ExpressionException {
        int columnIndex = this.resultSet.findColumn(varName);
        if (columnIndex == -1) throw new RuntimeException("Unable to find `" + varName + "`!");
        return this.resultSet.extractColumn(columnIndex);
    }

    private static ODESolverResultSet interpolate(ODESolverResultSet odeSolverResultSet, UniformTimeCourse sedmlSim) throws ExpressionException {
        double outputStart = sedmlSim.getOutputStartTime();
        double outputEnd = sedmlSim.getOutputEndTime();

        int numPoints = sedmlSim.getNumberOfSteps() + 1;


        ColumnDescription[] columnDescriptions = odeSolverResultSet.getColumnDescriptions();

        // need to construct a new RowColumnResultSet instance
        ODESolverResultSet finalResultSet = new ODESolverResultSet();

        // use same column descriptions
        for (ColumnDescription cd : columnDescriptions) {
            finalResultSet.addDataColumn(cd);
        }

        double deltaTime = ((outputEnd - outputStart) / (numPoints - 1));
        double[] timepoints = new double[numPoints];

        timepoints[0] = outputStart;
        for (int i = 1; i < numPoints; i++) {
            timepoints[i] = timepoints[i - 1] + deltaTime;
        }

        double[] originalTimepoints = odeSolverResultSet.extractColumn(0);


        double[][] columnValues = new double[columnDescriptions.length][];
        columnValues[0] = timepoints;
        for (int i = 1; i < columnDescriptions.length; i++) {
            // each row uses the time index based on the params above and for each column descriptions interpolate the value from the original result set
            columnValues[i] = interpLinear(originalTimepoints, odeSolverResultSet.extractColumn(i), timepoints);
        }


        double[][] rowValues = new double[numPoints][columnDescriptions.length];

        for (int rowCount = 0; rowCount < numPoints; rowCount++) {
            for (int colCount = 0; colCount < columnDescriptions.length; colCount++) {
                rowValues[rowCount][colCount] = columnValues[colCount][rowCount];
            }
        }


        // add a numPoints number of rows one by one as double[]
        for (int rowCount = 0; rowCount < numPoints; rowCount++) {
            finalResultSet.addRow(rowValues[rowCount]);
        }

        return finalResultSet;
    }

    private static double[] interpLinear(double[] x, double[] y, double[] xi) throws IllegalArgumentException {

        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        double[] dx = new double[x.length - 1];
        double[] dy = new double[x.length - 1];
        double[] slope = new double[x.length - 1];
        double[] intercept = new double[x.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < x.length - 1; i++) {
            dx[i] = x[i + 1] - x[i];
            if (dx[i] == 0) {
                throw new IllegalArgumentException("X must be montotonic. A duplicate " + "x-value was found");
            }
            if (dx[i] < 0) {
                throw new IllegalArgumentException("X must be sorted");
            }
            dy[i] = y[i + 1] - y[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = y[i] - x[i] * slope[i];
        }

        // Perform the interpolation here
        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > x[x.length - 1]) || (xi[i] < x[0])) {
                yi[i] = Double.NaN;
            } else {
                int loc = Arrays.binarySearch(x, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                } else {
                    yi[i] = y[loc];
                }
            }
        }

        return yi;
    }
}
