package org.vcell.cli;

public class CLIStandalone {
    public static void main(String[] args) {
        CLIHandler cliHandler = new CLIHandler(args);
        String inputFile = cliHandler.getInputFilePath();
        String outputDir = cliHandler.getOutputDirPath();
        System.out.println("CLI works");
    }
}
