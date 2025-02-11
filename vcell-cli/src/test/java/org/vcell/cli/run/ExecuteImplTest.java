package org.vcell.cli.run;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.cli.messaging.CLIRecorder;
import org.vcell.util.VCellUtilityHub;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Tag("Fast")
public class ExecuteImplTest {

    @Test
    public void test_singleExecOmex() throws Exception {
        PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
        VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);

        PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
        VCMongoMessage.enabled = false;
        try {

            InputStream omexInputStream = ExecuteImplTest.class.getResourceAsStream("/BioModel1.omex");
            File tempOutputDir = Files.createTempDirectory("ExecuteImplTest_temp").toFile();
            File tempOmexFile = File.createTempFile("BioModel1", ".omex", tempOutputDir);
            java.nio.file.Files.copy(omexInputStream, tempOmexFile.toPath(), REPLACE_EXISTING);

            CLIRecorder cliRecorder = new CLIRecorder(tempOutputDir, true, true);
            boolean bKeepTempFiles = true;
            boolean bExactMatchOnly = false;
            boolean bEncapsulateOutput = true;
            boolean bSmallMeshOverride = false;
            ExecuteImpl.singleMode(
                    tempOmexFile, tempOutputDir, cliRecorder,
                    bKeepTempFiles, bExactMatchOnly, bEncapsulateOutput, bSmallMeshOverride);

            org.apache.commons.io.FileUtils.forceDeleteOnExit(tempOutputDir);
            tempOmexFile.delete();

        } finally {
        }
    }
}
