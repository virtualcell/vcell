package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "export-omex", description = "export a VCML document to a COMBINE archive (.omex)")
public class ExportOmexCommand implements Callable<Integer> {
    @Option(names = { "-m", "--outputModelFormat" }, defaultValue = "SBML", description = "expecting SBML or VCML")
    private ModelFormat outputModelFormat = ModelFormat.SBML;

    @Option(names = { "-i", "--inputFilePath" }, description = "full path to .vcml file to export to .omex")
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" })
    private File outputFilePath;

    @Option(names = "--validate")
    boolean bValidateOmex;

    public Integer call() {
        try {
            PropertyLoader.loadProperties();
            boolean bForceLogFiles = true; // TODO find out what this means and simplify

            try {
                VcmlOmexConverter.convertOneFile(inputFilePath, outputFilePath,
                        outputModelFormat, bForceLogFiles, bValidateOmex);
            } catch (IOException | DataAccessException e) {
                e.printStackTrace(System.err);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
