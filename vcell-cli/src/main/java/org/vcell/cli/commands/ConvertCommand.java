package org.vcell.cli.commands;

import org.vcell.cli.vcml.ConvertImplementation;
import org.vcell.cli.vcml.ModelFormat;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;

import java.util.concurrent.Callable;

@Command(name = "convert", description = "convert from VCML to COMBINE archive (.omex)")
public class ConvertCommand implements Callable<Integer> {
    @Option(names = { "-m", "--outputModelFormat" }, defaultValue = "SBML", description = "expecting SBML or VCML")
    private ModelFormat outputModelFormat = ModelFormat.SBML;

    @Option(names = { "-i", "--inputFilePath" })
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" })
    private File outputFilePath;

    @Option(names = "--hasDataOnly")
    boolean bHasDataOnly;

    @Option(names = "--makeLogsOnly")
    boolean bMakeLogsOnly;

    @Option(names = "--nonSpatialOnly")
    boolean bNonSpatialOnly;

    @Option(names = "--forceLogFiles")
    boolean bForceLogFiles;

    @Option(names = "--validate")
    boolean bValidateOmex;

    public Integer call(){
        ConvertImplementation ci = new ConvertImplementation(inputFilePath, outputFilePath, bHasDataOnly, 
            bMakeLogsOnly, bNonSpatialOnly, bForceLogFiles, bValidateOmex);
        return ci.call();
    }
}
