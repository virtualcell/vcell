package cbit.vcell.modeldb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;

/** sim-data cleanup must remove a FEniCSx results bundle, which is a directory tree, not a file */
@Tag("Fast")
public class DBBackupAndCleanFenicsTest {

	@TempDir
	File userDir;

	@Test
	public void deletesAFenicsBundleTree() throws Exception {
		File bundle = new File(userDir, "SimID_274514696_0_.fenics");
		Files.createDirectories(new File(bundle, "cyto/u").toPath());
		Files.createDirectories(new File(bundle, "mesh").toPath());
		Files.writeString(new File(bundle, ".zattrs").toPath(), "{}");
		Files.writeString(new File(bundle, "cyto/u/0.0").toPath(), "x");
		Files.writeString(new File(bundle, "mesh/cyto.vtu").toPath(), "<VTKFile/>");
		Assertions.assertTrue(DBBackupAndClean.isFenicsBundle(bundle));

		StringBuffer log = new StringBuffer();
		Assertions.assertTrue(DBBackupAndClean.deleteFileAndLink(bundle, log), log.toString());
		Assertions.assertFalse(bundle.exists(), log.toString());
		Assertions.assertTrue(log.toString().contains("FEniCSx results bundle"));
	}

	@Test
	public void onlyBundleDirectoriesAreTrees() throws Exception {
		File log = new File(userDir, "SimID_274514696_0_.log");
		Files.writeString(log.toPath(), "IDA");
		Assertions.assertFalse(DBBackupAndClean.isFenicsBundle(log));
		File otherDir = new File(userDir, "SimID_274514696_0_.zarr");
		Files.createDirectories(otherDir.toPath());
		Assertions.assertFalse(DBBackupAndClean.isFenicsBundle(otherDir));
	}
}
