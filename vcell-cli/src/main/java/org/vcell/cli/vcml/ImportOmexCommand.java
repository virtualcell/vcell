package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;
import org.vcell.sedml.ModelFormat;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "import-omex", description = "import a COMBINE archive (.omex) to one or more VCML documents")
public class ImportOmexCommand implements Callable<Integer> {
    @Option(names = { "-i", "--inputFilePath" }, description = "full path to .omex file", required = true)
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" }, description = "full path to output directory", required = true)
    private File outputFilePath;

    public Integer call() {
        try {
            PropertyLoader.loadProperties();

            if (!inputFilePath.exists() || inputFilePath.isFile()){
                throw new RuntimeException("expecting inputFilePath to be an existing file: "+inputFilePath.getAbsolutePath());
            }
            if (!outputFilePath.exists() || outputFilePath.isDirectory()){
                throw new RuntimeException("expecting outputFilePath to be an existing directory: "+inputFilePath.getAbsolutePath());
            }
            VcmlOmexConverter.importOneOmexFile(inputFilePath, outputFilePath);

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
