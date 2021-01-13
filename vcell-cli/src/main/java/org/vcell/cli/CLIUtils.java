package org.vcell.cli;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import com.google.common.io.Files;
import org.vcell.stochtest.TimeSeriesMultitrialData;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import org.sedml.libsedml.*;

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


    public static void getSpeciesFromSedml() {
        System.loadLibrary("sedmlj");
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

    public static void convertIDAtoCSV(File f) {
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find IDA file, failed with err: " + e.getMessage());
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            System.err.println("Unable to read line, failed with err: " + e.getMessage());
        }
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            try {
                line = buf.readLine();
            } catch (IOException e) {
                System.err.println("Unable to read line, failed with err: " + e.getMessage());
            }
        }
        String fileAsString = sb.toString();
        fileAsString = fileAsString.replace("\t", ",");
        fileAsString = fileAsString.replace(":\n", "\n");
        fileAsString = fileAsString.replace(":", ",");

        f.delete();
        try {
            PrintWriter out = new PrintWriter(f);
            out.print(fileAsString);
            out.flush();
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find path, failed with err: " + e.getMessage());
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
}
