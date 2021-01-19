package org.vcell.cli;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import com.google.common.io.Files;

import org.jlibsedml.DataGenerator;
import org.jlibsedml.DataSet;
import org.jlibsedml.Output;
import org.jlibsedml.Report;
import org.jlibsedml.SedML;
import org.jlibsedml.Simulation;
import org.jlibsedml.Task;
import org.jlibsedml.UniformTimeCourse;
import org.jlibsedml.Variable;
import org.jmathml.ASTNode;
import org.sbml.jsbml.JSBML;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.stochtest.TimeSeriesMultitrialData;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class CLIUtils {
    //    private String tempDirPath = null;
    private String extractedOmexPath = null;

    public CLIUtils() {

    }

    public static boolean removeDirs(File f) {
        try {
            deleteRecursively(f);
        } catch (IOException ex) {
            System.err.println("Failed to delete the file: " + f);
            return false;
        }
        return true;
    }

    public static boolean makeDirs(File f) {
        if (f.exists()) {
            boolean isRemoved = removeDirs(f);
            if (!isRemoved)
                return false;
        }
        return f.mkdirs();
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                deleteRecursively(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    public static void saveTimeSeriesMultitrialDataAsCSV(TimeSeriesMultitrialData data, File outDir) {
        File outFile = Paths.get(outDir.toString(), data.datasetName + ".csv").toFile();
        int numberOfRows = data.times.length;
        int numberOfVariables = data.varNames.length;
        // Headers for CSV
        ArrayList<String> headersList = new ArrayList<>();
        headersList.add("times");
        for (String varName : data.varNames) {
            headersList.add(varName);
        }

        // Complete rows for CSV
        ArrayList<ArrayList<Double>> allRows = new ArrayList<>();

        for (int rowCounter = 0; rowCounter < numberOfRows; rowCounter++) {
            ArrayList<Double> row = new ArrayList<>();
            row.add(data.times[rowCounter]);

            for (int varCounter = 0; varCounter < numberOfVariables; varCounter++) {
                row.add(data.data[varCounter][rowCounter][0]);
            }

            allRows.add(row);

        }

        // Writing CSV in string buffer
        StringBuilder headersBuilder = new StringBuilder();

        for (String headerName : headersList) {
            headersBuilder.append(headerName);
            headersBuilder.append(",");
        }


        String headers = headersBuilder.replace(headersBuilder.length() - 1, headersBuilder.length(), "\n").toString();

        StringBuilder allRowsBuilder = new StringBuilder(headers);

        for (ArrayList<Double> rowValues : allRows) {
            StringBuilder rowBuilder = new StringBuilder();
            for (Double val : rowValues) {
                rowBuilder.append(val);
                rowBuilder.append(",");
            }
            allRowsBuilder.append(rowBuilder.replace(rowBuilder.length() - 1, rowBuilder.length(), "\n").toString());
        }

        String csvAsString = allRowsBuilder.toString();

        try {
            PrintWriter out = new PrintWriter(outFile);
            out.print(csvAsString);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static void createCSVFromODEResultSet(ODESolverResultSet resultSet, File f) throws ExpressionException {
        ColumnDescription[] descriptions =  resultSet.getColumnDescriptions();
        StringBuilder sb = new StringBuilder();


        int numberOfColumns = descriptions.length;
        int numberOfRows = resultSet.getRowCount();

        double[][] dataPoints = new double[numberOfColumns][];
        // Write headers
        for (ColumnDescription description : descriptions) {
            sb.append(description.getDisplayName());
            sb.append(",");
        }
        sb.append("\n");


        // Write rows
        for (int i = 0; i<numberOfColumns; i++) {
            dataPoints[i] = resultSet.extractColumn(i);
        }

        for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
            for (int colNum = 0; colNum < numberOfColumns; colNum++) {
                sb.append(dataPoints[colNum][rowNum]);
                sb.append(",");
            }

            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("\n");
        }


        try {
            PrintWriter out = new PrintWriter(f);
            out.print(sb.toString());
            out.flush();
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find path, failed with err: " + e.getMessage());
        }

    }

    public static void removeIntermediarySimFiles(File path) {
        File[] files = path.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".csv")) {
                // Do nothing
                continue;
            } else {
                f.delete();
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public String getTempDir() {
        return Files.createTempDir().getAbsolutePath();
    }
    public static HashMap<String, File> generateReportsAsCSV(SedML sedml, HashMap<String, ODESolverResultSet> resultsHash, File outDir) {
    	// finally, the real work
    	HashMap<String, File> reportsHash = new HashMap<String, File>();
        List<Output> ooo = sedml.getOutputs();
        for(Output oo : ooo) {
        	if (!(oo instanceof Report)) {
        		System.out.println("Ignoring unsupported output "+oo.getId());
        	} else {
        		System.out.println("Generating report "+oo.getId());
        		try {
        	        StringBuilder sb = new StringBuilder();
					List<DataSet> datasets = ((Report)oo).getListOfDataSets();
					for (DataSet dataset : datasets) {
						DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference());
						ArrayList<Variable> vars = new ArrayList<Variable>();
						ArrayList<String> varIDs = new ArrayList<String>();
						vars.addAll(datagen.getListOfVariables());
						int mxlen = 0;
						// get target values
						HashMap values = new HashMap<Variable, double[]>();
						for (Variable var : vars) {
							varIDs.add(var.getId());
							Task task = (Task)sedml.getTaskWithId(var.getReference());
							ODESolverResultSet results = resultsHash.get(task.getId());
							int column = results.findColumn(var.getName());
							double[] data = results.extractColumn(column);
							mxlen = Integer.max(mxlen, data.length);
							values.put(var, data);
						}
						//get math
						String mathMLStr = datagen.getMathAsString();
						Expression expr = new Expression(mathMLStr);
						SymbolTable st = new SimpleSymbolTable(varIDs.toArray(new String[vars.size()]));
						expr.bindExpression(st);
						//compute and write result, padding with NaN if unequal length or errors
						double[] row = new double[vars.size()];
						sb.append(dataset.getLabel()+",");
						for (int i = 0; i < mxlen; i++) {
							for (int j = 0; j < vars.size(); j++) {
								double[] varVals = ((double[])values.get(vars.get(j)));
								if (i < varVals.length) {
									row[j] = varVals[i];
								} else {
									row[j] = Double.NaN;
								}
							}
							double computed = Double.NaN;
							try {
								computed = expr.evaluateVector(row);
							} catch (Exception e) {
								// do nothing, we leave NaN and don't warn/log since it could flood
							}
							sb.append(computed).append(",");
						}
			            sb.deleteCharAt(sb.lastIndexOf(","));
			            sb.append("\n");							
					}
					File f = new File(outDir, oo.getId()+".csv");
		            PrintWriter out = new PrintWriter(f);
		            out.print(sb.toString());
		            out.flush();
		            reportsHash.put(oo.getId(), f);
				} catch (Exception e) {
					e.printStackTrace(System.err);
					reportsHash.put(oo.getId(), null);
				}
        	}
        }
        return reportsHash;
    }

	public static ODESolverResultSet interpolate(ODESolverResultSet odeSolverResultSet, UniformTimeCourse sedmlSim) {
		// TODO Auto-generated method stub
		double outputStart = sedmlSim.getOutputStartTime();
		double outputEnd = sedmlSim.getOutputEndTime();
		int numPoints = sedmlSim.getNumberOfPoints();

		ColumnDescription[] columnDescriptions = odeSolverResultSet.getColumnDescriptions();
		String[] columnNames = new String[columnDescriptions.length];

		for (int i = 0; i < columnDescriptions.length; i++) {
		    columnNames[i] = columnDescriptions[i].getDisplayName();
        }

        RowColumnResultSet rowColumnResultSet = new RowColumnResultSet(columnNames);

		double deltaTime = ((outputEnd - outputStart)/numPoints);
		double[] timepoints = new double[numPoints];

		timepoints[0] = outputStart;
		for(int i = 1; i<numPoints;i++) {
		    timepoints[i] = timepoints[i-1] + deltaTime;
        }

        // TODO: @gmarupilla Complete interpolation here

		// need to construct a new RowColumnResultSet instance
		// use same column descriptions
		// add a numPoints number of rows one by one as double[]
		// each row uses the time index based on the params above and for each column descriptions interpolate the value from the original result set
		return odeSolverResultSet;
	}
}
