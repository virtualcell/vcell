package org.vcell.solver.fenics;

import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.vtk.VtuGridParser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Points located in a moving (ALE) bundle mesh on the data server ({@link FenicsBundle#locate},
 * {@link DataSetControllerImpl#getFenicsBundleLocatedSamples}): the cells, vertex positions and values must be
 * exactly what the desktop finds from the row's whole positions and values; the reply is bounded; the desktop's
 * store splits, falls back and remembers an old data server ({@link DataServerBundleStore#locateRows}).
 */
@Tag("Fast")
public class FenicsBundleLocateTest {

	private static final User OWNER = new User("schaff", new KeyValue("17"));
	private static final VCSimulationDataIdentifier SIM = new VCSimulationDataIdentifier(
			new VCSimulationIdentifier(new KeyValue("274641196"), OWNER), 0);
	private static final String[] VARS = { "cell/RanC_cyt", "cell/C_cyt" };

	@TempDir
	File tmp;

	private File primary;

	@BeforeEach
	public void setup() {
		primary = new File(tmp, "primary");
		assertTrue(new File(primary, OWNER.getName()).mkdirs());
	}

	private File installBundle(String fixture) throws Exception {
		File target = new File(new File(primary, OWNER.getName()), "SimID_274641196_0_.fenics");
		Path src = FenicsBundleTest.fixture(fixture).toPath();
		try (Stream<Path> paths = Files.walk(src)) {
			for (Path p : (Iterable<Path>) paths::iterator) {
				Path dest = target.toPath().resolve(src.relativize(p).toString());
				if (Files.isDirectory(p)) Files.createDirectories(dest); else Files.copy(p, dest);
			}
		}
		return target;
	}

	private DataSetControllerImpl dataServer() throws Exception {
		return new DataSetControllerImpl(null, primary, null);
	}

	/** points inside, outside and near the moving domain (it translates in x through 1..9 x 1..9) */
	private static double[] points() {
		java.util.List<Double> p = new java.util.ArrayList<>();
		for (int i = 0; i <= 40; i++) {
			p.add(0.5 + 9.3 * i / 40);
			p.add(5.01 + 0.07 * (i % 5));
			p.add(0.0);
		}
		return p.stream().mapToDouble(Double::doubleValue).toArray();
	}

	private static long bits(double d) {
		return Double.doubleToRawLongBits(d);
	}

	/**
	 * The desktop's own steps on a whole row: the row's mesh, each point located (and snapped), the located cells'
	 * vertices; asserts the server's answer for that row says exactly the same, to the bit.
	 */
	private static void assertAsTheDesktopFindsIt(FenicsBundle bundle, String domain, String[] vars, int row, double[] points, boolean snap,
			FenicsLocatedSamples got, int r) throws Exception {
		VtuGridParser.VtuGrid mesh = VtuGridParser.parse(bundle.meshBytes(domain, row));
		VtuGridParser.VtuGrid grid = new VtuGridParser.VtuGrid(bundle.coords(domain, row), mesh.cells, mesh.cellTypes, mesh.cellFaces);
		TreeSet<Integer> needed = new TreeSet<>();
		for (int p = 0; p < points.length / 3; p++) {
			double x = points[3 * p], y = points[3 * p + 1], z = points[3 * p + 2];
			int c = VtuGridParser.locate(grid, x, y, z);
			double[] near = null;
			if (c < 0 && snap) {
				near = VtuGridParser.nearestOnMesh(grid, x, y, z);
				if (near != null) {
					c = (int) near[3];
				}
			}
			assertEquals(c, got.cells[r][p], "row " + row + " point " + p + ": the cell");
			assertArrayEquals(c >= 0 ? mesh.cells[c] : null, got.cellVertices[r][p], "row " + row + " point " + p + ": the cell's vertices");
			assertEquals(c >= 0 ? mesh.cellTypes[c] : -1, got.cellTypes[r][p], "row " + row + " point " + p + ": the cell's type");
			if (near != null) {
				for (int k = 0; k < 3; k++) {
					assertEquals(bits(near[k]), bits(got.snapped[r][3 * p + k]), "row " + row + " point " + p + ": where it snapped");
				}
			} else if (got.snapped[r] != null) {
				assertTrue(Double.isNaN(got.snapped[r][3 * p]), "row " + row + " point " + p + ": not snapped");
			}
			if (c >= 0) {
				for (int v : mesh.cells[c]) {
					needed.add(v);
				}
			}
		}
		int[] vertices = needed.stream().mapToInt(Integer::intValue).toArray();
		assertArrayEquals(vertices, got.vertices[r], "row " + row + ": the located cells' vertices");
		for (int i = 0; i < vertices.length; i++) {
			for (int k = 0; k < 3; k++) {
				assertEquals(bits(grid.points[3 * vertices[i] + k]), bits(got.coords[r][3 * i + k]), "row " + row + ": vertex position");
			}
		}
		for (int a = 0; a < vars.length; a++) {
			double[] whole = bundle.field(domain, vars[a], row);
			for (int i = 0; i < vertices.length; i++) {
				assertEquals(bits(whole[vertices[i]]), bits(got.values[a][r][i]), "row " + row + " " + vars[a] + ": value");
			}
		}
	}

	@Test
	public void locateIsWhatTheDesktopFindsInEveryRowAndSegment() throws Exception {
		double[] points = points();
		for (String fixture : new String[] { "moving_translate", "moving_remesh" }) {
			FenicsBundle bundle = FenicsBundle.open(FenicsBundleTest.fixture(fixture));
			BundleStore disk = BundleStore.directory(FenicsBundleTest.fixture(fixture));
			int start = 0;
			for (FenicsBundle.Segment segment : bundle.getSegments()) {
				int[] local = java.util.stream.IntStream.range(0, segment.count()).toArray();
				String[] paths = { segment.prefix() + VARS[0], segment.prefix() + VARS[1] };
				for (boolean snap : new boolean[] { false, true }) {
					FenicsLocatedSamples got = FenicsBundle.locate(disk, segment.prefix() + "mesh/cell.vtu", segment.prefix() + "cell/_coords",
							paths, points, snap, local, FenicsBundle.MAX_SAMPLE_VALUES, FenicsBundle.MAX_LOCATE_WORK);
					assertArrayEquals(local, got.rows);
					boolean someInside = false, someOutside = false, someSnapped = false;
					for (int r = 0; r < local.length; r++) {
						assertTrue(got.coordsWritten[r] && got.written[0][r] && got.written[1][r]);
						assertAsTheDesktopFindsIt(bundle, "cell", new String[] { "RanC_cyt", "C_cyt" }, start + r, points, snap, got, r);
						for (int c : got.cells[r]) {
							someInside |= c >= 0;
							someOutside |= c < 0;
						}
						someSnapped |= got.snapped[r] != null;
					}
					assertTrue(someInside, fixture + ": some points lie in the mesh");
					assertTrue(snap || someOutside, fixture + ": some miss it (a snap moves the near ones onto it)");
					assertEquals(snap, someSnapped, fixture + ": snapped points say where they went");
				}
				// the client's view of it: output rows of one segment, through FenicsBundle.locateFields
				int[] rows = java.util.stream.IntStream.range(start, start + segment.count()).toArray();
				FenicsBundle viaStore = FenicsBundle.open(new BundleStore() {
					@Override
					public byte[] read(String relativePath) throws java.io.IOException {
						return disk.read(relativePath);
					}

					@Override
					public FenicsLocatedSamples locateRows(String meshPath, String coordsPath, String[] arrayPaths, double[] pts, boolean snap,
							int[] localRows) throws java.io.IOException {
						return FenicsBundle.locate(disk, meshPath, coordsPath, arrayPaths, pts, snap, localRows, FenicsBundle.MAX_SAMPLE_VALUES,
								FenicsBundle.MAX_LOCATE_WORK);
					}

					@Override
					public String describe() {
						return "located";
					}
				});
				FenicsLocatedSamples got = viaStore.locateFields("cell", new String[] { "Ran_cyt" }, rows, points, false);
				for (int r = 0; r < rows.length; r++) {
					assertAsTheDesktopFindsIt(bundle, "cell", new String[] { "Ran_cyt" }, rows[r], points, false, got, r);
				}
				start += segment.count();
			}
			assertNull(FenicsBundle.open(FenicsBundleTest.fixture(fixture)).locateFields("cell", new String[] { "Ran_cyt" }, new int[] { 0 },
					points, false), "a store that cannot locate answers null: read whole rows");
		}
	}

	@Test
	public void locateRefusesWhatItCannotAnswerAndAnswersAPrefix() throws Exception {
		BundleStore disk = BundleStore.directory(FenicsBundleTest.fixture("moving_translate"));
		double[] points = points();
		int[] rows = { 0, 1, 2, 3 };
		String mesh = "mesh/cell.vtu", coords = "cell/_coords";
		long max = FenicsBundle.MAX_SAMPLE_VALUES, dec = FenicsBundle.MAX_LOCATE_WORK;
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.locate(disk, mesh, coords, VARS, new double[] { 1, 2 }, false, rows, max, dec),
				"points are triples");
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.locate(disk, mesh, coords, VARS,
				new double[3 * (FenicsBundle.MAX_LOCATE_POINTS + 1)], false, rows, max, dec), "too many points");
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.locate(disk, mesh, coords, new String[17], points, false, rows, max, dec),
				"too many arrays");
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.locate(disk, mesh, coords, VARS, points, false, new int[1001], max, dec),
				"too many rows");
		assertThrows(IndexOutOfBoundsException.class, () -> FenicsBundle.locate(disk, mesh, coords, VARS, points, false, new int[] { 11 }, max, dec));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.locate(disk, mesh, coords, new String[] { "stats/cell/C_cyt" }, points, false,
				rows, max, dec), "an array that is not one value per mesh point");
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.locate(disk, mesh, VARS[0], VARS, points, false, rows, max, dec),
				"positions that are not x,y,z per mesh point");
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.locate(disk, "../x.vtu", coords, VARS, points, false, rows, max, dec));
		assertThrows(java.io.FileNotFoundException.class, () -> FenicsBundle.locate(disk, "mesh/none.vtu", coords, VARS, points, false, rows, max, dec));

		FenicsLocatedSamples all = FenicsBundle.locate(disk, mesh, coords, VARS, points, false, rows, max, dec);
		int nPoints = points.length / 3;
		long cellInts = java.util.Arrays.stream(all.cellVertices[0]).mapToLong(v -> v == null ? 0 : v.length).sum();
		long row0 = 2L * nPoints + cellInts + 5L * all.vertices[0].length; // cells, types, their vertices; x,y,z and 2 arrays
		FenicsLocatedSamples one = FenicsBundle.locate(disk, mesh, coords, VARS, points, false, rows, row0, dec);
		assertArrayEquals(new int[] { 0 }, one.rows, "a reply budget of one row's values: the first row");
		assertArrayEquals(all.vertices[0], one.vertices[0]);
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.locate(disk, mesh, coords, VARS, points, false, rows, row0 - 1, dec),
				"not even one row fits");
		FenicsLocatedSamples worked = FenicsBundle.locate(disk, mesh, coords, VARS, points, false, rows, max, 2L * (5 * 442 + 786));
		assertArrayEquals(new int[] { 0, 1 }, worked.rows, "a work budget of two rows: x,y,z and two arrays decoded, 786 cells sorted");
		assertArrayEquals(new int[] { 0 }, FenicsBundle.locate(disk, mesh, coords, VARS, points, false, rows, max, 1).rows,
				"the first row is always decoded");
		assertEquals(0, FenicsBundle.locate(disk, mesh, coords, new String[0], points, false, rows, max, dec).values.length,
				"no arrays: just where the points are (a function of x, y, z, t)");
	}

	@Test
	public void theDataServerLocatesOnDiskAndUnwrittenRowsAreMarked() throws Exception {
		File bundle = installBundle("moving_translate");
		DataSetControllerImpl ds = dataServer();
		double[] points = points();
		FenicsLocatedSamples got = ds.getFenicsBundleLocatedSamples(SIM, "mesh/cell.vtu", "cell/_coords", VARS, points, false, new int[] { 3, 0 });
		assertArrayEquals(new int[] { 3, 0 }, got.rows, "rows in the order asked");
		FenicsBundle local = FenicsBundle.open(bundle);
		assertAsTheDesktopFindsIt(local, "cell", new String[] { "RanC_cyt", "C_cyt" }, 3, points, false, got, 0);
		assertAsTheDesktopFindsIt(local, "cell", new String[] { "RanC_cyt", "C_cyt" }, 0, points, false, got, 1);
		assertThrows(Exception.class, () -> ds.getFenicsBundleLocatedSamples(SIM, "mesh/cell.vtu", "../cell/_coords", VARS, points, false, new int[] { 0 }));
		assertThrows(DataAccessException.class, () -> ds.getFenicsBundleLocatedSamples(new VCSimulationDataIdentifier(SIM.getVcSimID(), 1),
				"mesh/cell.vtu", "cell/_coords", VARS, points, false, new int[] { 0 }), "no such bundle");

		// a running solver: a row's values not written yet -> not written, its fill value, as a whole read gives
		Files.delete(new File(bundle, "cell/C_cyt/2.0").toPath());
		FenicsLocatedSamples gap = ds.getFenicsBundleLocatedSamples(SIM, "mesh/cell.vtu", "cell/_coords", VARS, points, false, new int[] { 2 });
		assertTrue(gap.written[0][0]);
		assertFalse(gap.written[1][0]);
		double[] filled = FenicsBundle.open(bundle).field("cell", "C_cyt", 2);
		assertEquals(bits(filled[gap.vertices[0][0]]), bits(gap.values[1][0][0]));
		// ...and its positions not written yet: no point lies anywhere, as in a whole read's all-NaN mesh
		Files.delete(new File(bundle, "cell/_coords/2.0.0").toPath());
		FenicsLocatedSamples nowhere = ds.getFenicsBundleLocatedSamples(SIM, "mesh/cell.vtu", "cell/_coords", VARS, points, false, new int[] { 2 });
		assertFalse(nowhere.coordsWritten[0]);
		assertTrue(java.util.Arrays.stream(nowhere.cells[0]).allMatch(c -> c < 0));
		assertEquals(0, nowhere.vertices[0].length);
	}

	@Test
	public void theRpcDispatchFindsTheMethod() throws Exception {
		installBundle("moving_translate");
		cbit.vcell.simdata.DataServerImpl server = new cbit.vcell.simdata.DataServerImpl(dataServer(), null);
		double[] points = points();
		cbit.vcell.message.VCRpcRequest request = roundTrip(new cbit.vcell.message.VCRpcRequest(OWNER,
				cbit.vcell.message.VCRpcRequest.RpcServiceType.DATA, "getFenicsBundleLocatedSamples",
				new Object[] { OWNER, SIM, "mesh/cell.vtu", "cell/_coords", VARS, points, true, new int[] { 0, 4 } }));
		FenicsLocatedSamples reply = (FenicsLocatedSamples) roundTrip(request.rpc(server));
		assertArrayEquals(new int[] { 0, 4 }, reply.rows);
		FenicsBundle local = FenicsBundle.open(FenicsBundleTest.fixture("moving_translate"));
		assertAsTheDesktopFindsIt(local, "cell", new String[] { "RanC_cyt", "C_cyt" }, 4, points, true, reply, 1);
	}

	@SuppressWarnings("unchecked")
	private static <T> T roundTrip(T object) throws Exception {
		java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
		try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(bytes)) {
			out.writeObject(object);
		}
		try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
			return (T) in.readObject();
		}
	}

	/** a data server that locates, one that answers in parts, one that predates the call, and one that fails */
	@Test
	public void theClientStoreLocatesSplitsAndFallsBack() throws Exception {
		installBundle("moving_translate");
		DataSetControllerImpl ds = dataServer();
		AtomicInteger calls = new AtomicInteger();
		String[] mode = { "prefix" };
		DataServerBundleStore store = new DataServerBundleStore(new DataServerBundleStore.Server() {
			@Override
			public byte[] file(String relativePath) throws DataAccessException {
				return ds.getFenicsBundleFile(SIM, relativePath);
			}

			@Override
			public byte[][] files(String[] relativePaths) throws DataAccessException {
				return ds.getFenicsBundleFiles(SIM, relativePaths);
			}

			@Override
			public FenicsLocatedSamples located(String meshPath, String coordsPath, String[] arrayPaths, double[] points, boolean snap,
					int[] rows) throws DataAccessException {
				calls.incrementAndGet();
				switch (mode[0]) {
					case "old": throw new DataAccessException("No such method: getFenicsBundleLocatedSamples(org.vcell.util.document.User,...)");
					case "failing": throw new DataAccessException("Server is temporarily not responding");
					default: return ds.getFenicsBundleLocatedSamples(SIM, meshPath, coordsPath, arrayPaths, points, snap, rows,
							FenicsBundle.MAX_SAMPLE_VALUES, 1); // one row per reply
				}
			}
		}, "test");
		double[] points = points();
		FenicsBundle local = FenicsBundle.open(FenicsBundleTest.fixture("moving_translate"));
		FenicsBundle viaStore = FenicsBundle.open(BundleStore.cached(store));
		FenicsLocatedSamples got = viaStore.locateFields("cell", new String[] { "RanC_cyt", "C_cyt" }, new int[] { 0, 1, 2 }, points, false);
		assertEquals(3, calls.get(), "answered a row at a time: three calls");
		for (int r = 0; r < 3; r++) {
			assertAsTheDesktopFindsIt(local, "cell", new String[] { "RanC_cyt", "C_cyt" }, r, points, false, got, r);
		}

		mode[0] = "failing";
		assertNull(viaStore.locateFields("cell", new String[] { "C_cyt" }, new int[] { 0 }, points, false), "a failed call: read whole rows");
		mode[0] = "prefix";
		assertNotNull(viaStore.locateFields("cell", new String[] { "C_cyt" }, new int[] { 0 }, points, false), "a failure is not remembered");

		mode[0] = "old";
		int before = calls.get();
		assertNull(viaStore.locateFields("cell", new String[] { "C_cyt" }, new int[] { 0 }, points, false));
		assertNull(viaStore.locateFields("cell", new String[] { "C_cyt" }, new int[] { 0 }, points, false));
		assertEquals(before + 1, calls.get(), "an old data server is asked once, then never again");

		// a request beyond one call's caps is not sent
		assertNull(new DataServerBundleStore((DataServerBundleStore.Server) null, "x").locateRows("m", "c", new String[17], points, false,
				new int[] { 0 }));
		assertNull(new DataServerBundleStore((DataServerBundleStore.Server) null, "x").locateRows("m", "c", VARS,
				new double[3 * (FenicsBundle.MAX_LOCATE_POINTS + 1)], false, new int[] { 0 }));
		// a fixed segment is not located
		FenicsBundle fixed = FenicsBundle.open(FenicsBundleTest.fixture("membrane_efflux"));
		assertThrows(IllegalArgumentException.class, () -> fixed.locateFields("cytosol_dom", new String[] { "u" }, new int[] { 0 }, points, false));
	}
}
