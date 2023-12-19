package org.vcell.vis.vtk;

import cbit.vcell.resource.PropertyLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.vis.io.CartesianMeshFileReader;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.mapping.vcell.CartesianMeshMapping;
import org.vcell.vis.mapping.vcell.CartesianMeshVtkFileWriter;
import org.vcell.vis.vcell.CartesianMesh;
import org.vcell.vis.vismesh.thrift.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Tag("Fast")
public class VCellDataTest {

	private static String previousPythonDir = null;

	@BeforeAll
	public static void setUp() {
		previousPythonDir = PropertyLoader.getProperty(PropertyLoader.vtkPythonDir, null);
		PropertyLoader.setProperty(PropertyLoader.vtkPythonDir, "../pythonVtk");
	}

	@AfterAll
	public static void tearDown() {
		if (previousPythonDir!=null) {
			PropertyLoader.setProperty(PropertyLoader.vtkPythonDir, previousPythonDir);
		}
	}

	@Test
	public void test_3D() throws Exception {
		KeyValue simKey = new KeyValue("956955326");

		File tempdir = Files.createTempDirectory("VCellDataTest_").toFile();
		File meshFile = copyResourceToFile(tempdir, "SimID_" + simKey + "_0_.mesh");
		File meshMetricsFile = copyResourceToFile(tempdir, "SimID_" + simKey + "_0_.meshmetrics");
		File subdomainFile = copyResourceToFile(tempdir, "SimID_" + simKey + "_0_.subdomains");
		File logFile = copyResourceToFile(tempdir, "SimID_" + simKey + "_0_.log");
		File zipFile = copyResourceToFile(tempdir, "SimID_" + simKey + "_0_00.zip");
		File postprocessingFile = copyResourceToFile(tempdir, "SimID_" + simKey + "_0_.hdf5");
		File[] generatedFiles = null;
		try {

			VCellSimFiles vcellFiles = new VCellSimFiles(simKey, 0, meshFile, meshMetricsFile, subdomainFile, logFile, postprocessingFile);
			vcellFiles.addDataFileEntry(zipFile, new File("SimID_" + simKey + "_0_0000.sim"), 0.0);

			// process each domain separately (only have to process the mesh once for each one)
			CartesianMeshVtkFileWriter cartesianMeshVtkFileWriter = new CartesianMeshVtkFileWriter();
			File destinationDirectory = tempdir;
			generatedFiles = cartesianMeshVtkFileWriter.writeEmptyMeshFiles(vcellFiles, destinationDirectory, null);
			System.out.println("generatedFiles: " + Arrays.asList(generatedFiles));

			CartesianMeshFileReader reader = new CartesianMeshFileReader();
			CartesianMesh mesh = reader.readFromFiles(vcellFiles);
			CartesianMeshMapping cartesianMeshMapping = new CartesianMeshMapping();

			String domainName = mesh.meshRegionInfo.getVolumeDomainNames().get(0);

			System.out.println("making visMesh for domain " + domainName + " of [" + mesh.meshRegionInfo.getVolumeDomainNames() + "]");
			boolean bVolume = true;
			VisMesh visMesh = cartesianMeshMapping.fromMeshData(mesh, domainName, bVolume);
			System.out.println("visMesh: " + visMesh);
		} finally {
			meshFile.delete();
			meshMetricsFile.delete();
			subdomainFile.delete();
			logFile.delete();
			zipFile.delete();
			postprocessingFile.delete();
			if (generatedFiles!=null) {
				Arrays.stream(generatedFiles).forEach(File::delete);
			}
			tempdir.delete();
		}
	}

	private File copyResourceToFile(File tempdir, String fileName) throws IOException {
		File meshFile = new File(tempdir, fileName);
		try (InputStream instream = VCellDataTest.class.getResourceAsStream("/simdata/MembraneFrap3D/"+ fileName)) {
			Files.copy(instream, meshFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		return meshFile;
	}

	@Test
	public void test_chombo_construction() {
		try {
			VisMesh visMesh = new VisMesh(3,new Vect3D(0,0,0),new Vect3D(3,1,1));
			VisPoint[] points0 = new VisPoint[] {
					new VisPoint(0, 0, 0),
					new VisPoint(1, 0, 0),
					new VisPoint(0, 1, 0),
					new VisPoint(0, 0, 1)
			};

			visMesh.addToPoints(points0[0]);
			visMesh.addToPoints(points0[1]);
			visMesh.addToPoints(points0[2]);
			visMesh.addToPoints(points0[3]);

			List<PolyhedronFace> faces0 = Arrays.asList(
					new PolyhedronFace(Arrays.asList( 0, 1, 3 )),
					new PolyhedronFace(Arrays.asList( 0, 3, 2 )),
					new PolyhedronFace(Arrays.asList( 0, 2, 1 )),
					new PolyhedronFace(Arrays.asList( 1, 3, 2 ))
			);

			int level = 0;
			int boxNumber = 0;
			int boxIndex = 0;
			int fraction = 0;

			VisIrregularPolyhedron genPolyhedra0 = new VisIrregularPolyhedron(faces0);
			genPolyhedra0.setChomboVolumeIndex(new ChomboVolumeIndex(level, boxNumber, boxIndex, fraction));

			visMesh.addToIrregularPolyhedra(genPolyhedra0);

		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
}
