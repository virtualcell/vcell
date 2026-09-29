package org.vcell.client.viz;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import cbit.vcell.resource.PropertyLoader;

/**
 * The field viewer's server over the test fixtures, for the browser tests in {@code webapp-viewer/test/}: it
 * serves the viewer page from a {@code webapp-viewer} directory and every fixture run through the same
 * {@link FieldViewerServer} the desktop client starts.
 * <p>
 * Runs until killed. Prints one line, {@code FIXTURE {"port":…,"datasets":{…}}}, once it is listening: the
 * datasets by role, each as the {@code sim} and {@code job} a viewer URL names.
 *
 * <pre>
 * java -cp &lt;vcell-client test classpath&gt; org.vcell.client.viz.FieldViewerFixtureServer &lt;webapp-viewer dir&gt;
 * </pre>
 */
public final class FieldViewerFixtureServer {

	private FieldViewerFixtureServer() {
	}

	public static void main(String[] args) throws Exception {
		File webapp = new File(args.length > 0 ? args[0] : "webapp-viewer").getAbsoluteFile();
		if (!new File(webapp, "index.html").isFile()) {
			throw new IllegalArgumentException("no index.html in " + webapp + "; pass the webapp-viewer directory");
		}
		PropertyLoader.setProperty(PropertyLoader.fieldViewerStaticDir, webapp.getPath());
		PropertyLoader.setProperty(PropertyLoader.fieldViewerPort, "0"); // any free port

		// finite volume: 2D and 3D runs, and MembraneFrap3D (membrane variables only), read as the desktop reads a local run
		Path fvRoot = Files.createTempDirectory("FieldViewerFixtureServer_");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try (java.util.stream.Stream<Path> walk = Files.walk(fvRoot)) {
				walk.sorted(java.util.Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} catch (Exception e) {
				// a temporary copy; nothing to report on the way out
			}
		}));
		FieldViewerServerFvTest.registerFvFixtures(fvRoot);
		// FEniCSx: a 2D disk, a 2D disk moving along x (ALE), a 3D sphere in a box with its membrane, and a 2D
		// disk in a box with its membrane (a closed curve of line cells)
		FieldViewerServer.registerBundle("987654321", 0, bundle("membrane_efflux.fenics"), "fenics::2d disk");
		FieldViewerServer.registerBundle("777", 0, bundle("moving_translate.fenics"), "fenics::moving disk");
		FieldViewerServer.registerBundle("555", 0, bundle("receptor_3d.fenics"), "fenics::receptor 3d");
		FieldViewerServer.registerBundle("556", 0, bundle("receptor_2d.fenics"), "fenics::receptor 2d");
		// MovingBoundary: a disk moving along x, through the VTU seam
		FakeMovingBoundaryRun.register();
		// Chombo: a disk (2D) and a ball (3D) on one static embedded-boundary mesh, through the VTU seam
		FakeChomboRun.register();

		int port = FieldViewerServer.start();
		if (port < 0) {
			throw new IllegalStateException("the field viewer server did not start");
		}
		System.out.println("FIXTURE {\"port\":" + port + ",\"datasets\":{"
				+ "\"fv2d\":{\"sim\":\"" + FieldViewerServerFvTest.SIM_2D + "\",\"job\":0},"
				+ "\"fv3d\":{\"sim\":\"" + FieldViewerServerFvTest.SIM_3D + "\",\"job\":0},"
				+ "\"fvMembrane3d\":{\"sim\":\"" + FieldViewerServerFvTest.SIM_MEMBRANE_3D + "\",\"job\":0},"
				+ "\"fenics2d\":{\"sim\":\"987654321\",\"job\":0},"
				+ "\"fenicsMoving\":{\"sim\":\"777\",\"job\":0},"
				+ "\"fenics3d\":{\"sim\":\"555\",\"job\":0},"
				+ "\"fenics2dMembrane\":{\"sim\":\"556\",\"job\":0},"
				+ "\"movingBoundary\":{\"sim\":\"" + FakeMovingBoundaryRun.SIM + "\",\"job\":0},"
				+ "\"chombo2d\":{\"sim\":\"" + FakeChomboRun.SIM_2D + "\",\"job\":0},"
				+ "\"chombo3d\":{\"sim\":\"" + FakeChomboRun.SIM_3D + "\",\"job\":0}}}");
		System.out.flush();
		Thread.currentThread().join(); // the server's threads are daemons; stay up until killed
	}

	private static File bundle(String name) throws Exception {
		return new File(FieldViewerFixtureServer.class.getResource(name + "/.zattrs").toURI()).getParentFile();
	}
}
