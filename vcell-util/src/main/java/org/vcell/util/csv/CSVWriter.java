package org.vcell.util.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {
    private final char DELIMITER;
    private final char ROW_SEPARATOR;
    private final BufferedWriter writer;
    public CSVWriter(File outputCSVFile) throws IOException {
        this(outputCSVFile, false);
    }

    public CSVWriter(File outputCSVFile, boolean append) throws IOException {
        this(outputCSVFile, append, ',', '\n');
    }

    public CSVWriter(File outputCSVFile, boolean append, char delimiter, char rowSeparator) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(outputCSVFile, append));
        this.DELIMITER = delimiter;
        this.ROW_SEPARATOR = rowSeparator;
    }

    public void appendToLine(int lineNumber, String value){

    }
}
