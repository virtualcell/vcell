package org.vcell.cli.run;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ExecuteImplTest {

    @Test
    public void test_singleExecOmex() throws Exception {
        System.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
        NativeLib.HDF5.load();
        NativeLib.combinej.load();

        System.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
        CLIPythonManager.getInstance().instantiatePythonProcess();

        InputStream omexInputStream = ExecuteImplTest.class.getResourceAsStream("/BioModel1.omex");
        File outputDir = Files.createTempDir();
        File targetFile = File.createTempFile("BioModel1", ".omex", outputDir);
        FileUtils.copyInputStreamToFile(omexInputStream, targetFile);

        File omexFile = new File("/Users/schaff/Documents/workspace/vcell/vcell-cli/src/test/resources/BioModel1.omex");
        CLIRecorder cliRecorder = new CLIRecorder(outputDir,true,true);
        boolean bKeepTempFiles = true;
        boolean bExactMatchOnly = false;
        boolean bEncapsulateOutput = true;
        ExecuteImpl.singleExecOmex(omexFile, outputDir, cliRecorder, bKeepTempFiles, bExactMatchOnly, bEncapsulateOutput);

        CLIPythonManager.getInstance().closePythonProcess();
    }

}
