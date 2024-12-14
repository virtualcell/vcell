package org.vcell.cli.testsupport;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OmexTestingDatabase {

    // read a newline-delimited json file into a list of OmexTextCase objects
    public static List<OmexTestCase> readOmexTestCasesFromNdjson(String path) throws IOException {
        List<OmexTestCase> testCases = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            String line = reader.readLine();
            while (line != null) {
                // use jackson objectmapper to read an OmexTestCase object from a json string
                OmexTestCase testCase = objectMapper.readValue(line, OmexTestCase.class);
                testCases.add(testCase);
            }
        }
        return testCases;
    }

    // read a csv file into a list of OmexTextCase objects
    public static List<OmexTestCase> readOmexTestCasesFromCsv(String path) throws IOException{
        List<OmexTestCase> testCases = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            String line = reader.readLine(); // read header
            line = reader.readLine(); // read first line
            while (line != null) {
                line = line + " "; // add a space to the end of the line to handle empty fields at the end
                line = line.replace(",,",", ,"); // replace empty fields with ", ,"
                String[] fields = line.split(",");
                testCases.add(new OmexTestCase(fields));
                line = reader.readLine();
            }
        }
        return testCases;
    }

    public static void main(String[] args) {
        try {
            List<OmexTestCase> testCases = readOmexTestCasesFromCsv("/Users/jimschaff/Documents/workspace/vcell/vcell-cli/src/main/resources/known_problems.csv");
            ObjectMapper objectMapper = new ObjectMapper();
            for (OmexTestCase testCase : testCases) {
                System.out.println(Arrays.stream(testCase.toCsvLine()).collect(Collectors.joining(",")));
                System.out.println(testCase);
                System.out.println(objectMapper.writeValueAsString(testCase));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
