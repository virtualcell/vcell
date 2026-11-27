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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sampled reads of a FEniCSx results bundle: the data server reads and decodes the chunks and sends only the
 * asked-for values ({@link FenicsBundle#gather}, {@link DataSetControllerImpl#getFenicsBundleSamples}); the
 * desktop's store asks for them, falls back to whole rows when it cannot ({@link DataServerBundleStore#sampleRows}),
 * and caches what it got ({@link BundleStore#cached}).
 */
@Tag("Fast")
public class FenicsBundleSamplingTest {

	private static final User OWNER = new User("schaff", new KeyValue("17"));
	private static final VCSimulationDataIdentifier SIM = new VCSimulationDataIdentifier(
			new VCSimulationIdentifier(new KeyValue("274514696"), OWNER), 0);

	@TempDir
	File tmp;

	private File primary;

	@BeforeEach
	public void setup() {
		primary = new File(tmp, "primary");
		assertTrue(new File(primary, OWNER.getName()).mkdirs());
	}

	private File installBundle(String fixture) throws Exception {
		File target = new File(new File(primary, OWNER.getName()), "SimID_274514696_0_.fenics");
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

	private static int[] range(int n) {
		int[] r = new int[n];
		for (int i = 0; i < n; i++) {
			r[i] = i;
		}
		return r;
	}

	/** every few indices of a row, strictly increasing */
	private static int[] someIndices(int n, int stride) {
		return java.util.stream.IntStream.iterate(1, i -> i < n, i -> i + stride).toArray();
	}

	/** gather equals the whole rows' values at the indices, for every fixture, array kind and segment */
	@Test
	public void gatherIsTheWholeRowsAtTheIndices() throws Exception {
		for (String fixture : new String[] { "membrane_efflux", "coupled_3d_small", "moving_remesh", "moving_translate" }) {
			FenicsBundle bundle = FenicsBundle.open(FenicsBundleTest.fixture(fixture));
			BundleStore disk = BundleStore.directory(FenicsBundleTest.fixture(fixture));
			for (FenicsBundle.Variable v : bundle.getVariables()) {
				int start = 0;
				for (FenicsBundle.Segment segment : bundle.getSegments()) {
					int[] global = Arrays.copyOfRange(range(bundle.getTimes().size()), start, start + segment.count());
					int[] local = range(segment.count());
					int n = bundle.field(v.domain(), v.name(), global[0]).length;
					int[] idx = someIndices(n, 7);
					// two arrays of one domain in one call: the variable and itself under its segment prefix
					String path = segment.prefix() + v.path();
					FenicsSamples s = FenicsBundle.gather(disk, new String[] { path, path }, idx, local, FenicsBundle.MAX_SAMPLE_VALUES);
					assertArrayEquals(local, s.rows);
					for (int r = 0; r < local.length; r++) {
						double[] whole = bundle.field(v.domain(), v.name(), global[r]);
						for (int i = 0; i < idx.length; i++) {
							assertEquals(Double.doubleToRawLongBits(whole[idx[i]]), Double.doubleToRawLongBits(s.values[0][r][i]),
									fixture + " " + path + " row " + r);
							assertEquals(Double.doubleToRawLongBits(whole[idx[i]]), Double.doubleToRawLongBits(s.values[1][r][i]));
						}
						assertTrue(s.written[0][r]);
					}
					// the statistics array: indices 0..3 of a (T, 4) row
					FenicsSamples stats = FenicsBundle.gather(disk, new String[] { segment.prefix() + v.stats() }, new int[] { 0, 3 }, local, 100);
					for (int r = 0; r < local.length; r++) {
						double[] row = bundle.stats(v.domain(), v.name(), global[r]);
						assertEquals(row[0], stats.values[0][r][0], 0.0);
						assertEquals(row[3], stats.values[0][r][1], 0.0);
					}
					// ALE point positions: a (T, N, 3) row is N·3 values
					if ("ale".equals(segment.motion())) {
						double[] xyz = bundle.coords(v.domain(), global[0]);
						FenicsSamples c = FenicsBundle.gather(disk, new String[] { segment.prefix() + v.domain() + "/_coords" },
								new int[] { 3, 4, 5 }, new int[] { 0 }, 100);
						assertArrayEquals(Arrays.copyOfRange(xyz, 3, 6), c.values[0][0], 0.0);
					}
					start += segment.count();
				}
			}
		}
	}

	@Test
	public void gatherRefusesWhatItCannotAnswer() throws Exception {
		BundleStore disk = BundleStore.directory(FenicsBundleTest.fixture("coupled_3d_small"));
		String cyto = "cyto_dom/s_cyto", ext = "ext_dom/s_ext";
		int n = FenicsBundle.open(FenicsBundleTest.fixture("coupled_3d_small")).field("cyto_dom", "s_cyto", 0).length;
		long max = FenicsBundle.MAX_SAMPLE_VALUES;
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[] { cyto, ext }, new int[] { 0 }, new int[] { 0 }, max),
				"two domains' arrays (different row lengths) in one call");
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[] { cyto }, new int[] { 3, 2 }, new int[] { 0 }, max));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[] { cyto }, new int[] { 3, 3 }, new int[] { 0 }, max));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[] { cyto }, new int[] { n }, new int[] { 0 }, max));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[] { cyto }, new int[] { -1 }, new int[] { 0 }, max));
		assertThrows(IndexOutOfBoundsException.class, () -> FenicsBundle.gather(disk, new String[] { cyto }, new int[] { 0 }, new int[] { 99 }, max));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[] { "../x" }, new int[] { 0 }, new int[] { 0 }, max));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[17], new int[] { 0 }, new int[] { 0 }, max));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[] { cyto }, new int[] { 0 },
				new int[FenicsBundle.MAX_SAMPLE_ROWS + 1], max));
		assertThrows(IllegalArgumentException.class, () -> FenicsBundle.gather(disk, new String[] { cyto, cyto }, new int[] { 0, 1 }, new int[] { 0 }, 3),
				"not even one row (2 arrays × 2 indices) fits a budget of 3");
	}

	@Test
	public void theDataServerSamplesTheBundleOnDiskAndAnswersAPrefix() throws Exception {
		File bundle = installBundle("membrane_efflux");
		DataSetControllerImpl ds = dataServer();
		FenicsBundle local = FenicsBundle.open(bundle);
		int[] idx = { 0, 5, 9 };
		FenicsSamples all = ds.getFenicsBundleSamples(SIM, new String[] { "cytosol_dom/u" }, idx, new int[] { 2, 0, 1 });
		assertArrayEquals(new int[] { 2, 0, 1 }, all.rows, "rows in the order asked");
		for (int r = 0; r < 3; r++) {
			double[] whole = local.field("cytosol_dom", "u", all.rows[r]);
			assertArrayEquals(new double[] { whole[0], whole[5], whole[9] }, all.values[0][r], 0.0);
		}
		// a budget of five values: 2 arrays × 3 indices = 6 per row does not fit, 1 array × 3 = 3 does once
		FenicsSamples prefix = ds.getFenicsBundleSamples(SIM, new String[] { "cytosol_dom/u" }, idx, new int[] { 0, 1, 2 }, 5);
		assertArrayEquals(new int[] { 0 }, prefix.rows, "the longest prefix of rows that fits");
		assertThrows(IllegalArgumentException.class, () -> ds.getFenicsBundleSamples(SIM, new String[] { "cytosol_dom/u", "cytosol_dom/u" }, idx, new int[] { 0 }, 5),
				"not even one row fits (DataServerImpl turns this into a data-access error for the RPC)");
		// refused paths and a missing bundle surface as data-access errors, as for whole files
		assertThrows(Exception.class, () -> ds.getFenicsBundleSamples(SIM, new String[] { "../secret" }, idx, new int[] { 0 }));
		assertThrows(DataAccessException.class, () -> ds.getFenicsBundleSamples(new VCSimulationDataIdentifier(SIM.getVcSimID(), 1),
				new String[] { "cytosol_dom/u" }, idx, new int[] { 0 }));

		// an unwritten chunk (a running solver): not written, and the fill value, as a whole read gives
		Files.delete(new File(bundle, "cytosol_dom/u/1.0").toPath());
		FenicsSamples gap = ds.getFenicsBundleSamples(SIM, new String[] { "cytosol_dom/u" }, idx, new int[] { 0, 1 });
		assertTrue(gap.written[0][0]);
		assertFalse(gap.written[0][1]);
		double[] filled = FenicsBundle.open(bundle).field("cytosol_dom", "u", 1);
		assertEquals(Double.doubleToRawLongBits(filled[5]), Double.doubleToRawLongBits(gap.values[0][1][1]));
	}

	@Test
	public void theRpcDispatchFindsTheMethod() throws Exception {
		installBundle("membrane_efflux");
		cbit.vcell.simdata.DataServerImpl server = new cbit.vcell.simdata.DataServerImpl(dataServer(), null);
		cbit.vcell.message.VCRpcRequest request = roundTrip(new cbit.vcell.message.VCRpcRequest(OWNER,
				cbit.vcell.message.VCRpcRequest.RpcServiceType.DATA, "getFenicsBundleSamples",
				new Object[] { OWNER, SIM, new String[] { "cytosol_dom/u" }, new int[] { 1, 2 }, new int[] { 0, 2 } }));
		FenicsSamples reply = (FenicsSamples) roundTrip(request.rpc(server));
		FenicsBundle local = FenicsBundle.open(FenicsBundleTest.fixture("membrane_efflux"));
		assertArrayEquals(new int[] { 0, 2 }, reply.rows);
		assertEquals(local.field("cytosol_dom", "u", 2)[2], reply.values[0][1][1], 0.0);
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

	/** a data server that samples, one that answers in parts, one that predates the call, and one that fails */
	@Test
	public void theClientStoreSamplesSplitsAndFallsBack() throws Exception {
		installBundle("membrane_efflux");
		DataSetControllerImpl ds = dataServer();
		AtomicInteger sampleCalls = new AtomicInteger();
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
			public FenicsSamples samples(String[] arrayPaths, int[] indices, int[] rows) throws DataAccessException {
				sampleCalls.incrementAndGet();
				switch (mode[0]) {
					case "old": throw new DataAccessException("No such method: getFenicsBundleSamples(org.vcell.util.document.User,...)");
					case "failing": throw new DataAccessException("Server is temporarily not responding");
					default: return ds.getFenicsBundleSamples(SIM, arrayPaths, indices, rows, arrayPaths.length * indices.length); // one row per reply
				}
			}
		}, "test");
		FenicsBundle local = FenicsBundle.open(FenicsBundleTest.fixture("membrane_efflux"));
		int[] idx = { 2, 3 };
		FenicsSamples s = store.sampleRows(new String[] { "cytosol_dom/u" }, new int[] { 0, 1, 2 }, idx);
		assertEquals(3, sampleCalls.get(), "answered a row at a time: three calls");
		for (int r = 0; r < 3; r++) {
			assertEquals(local.field("cytosol_dom", "u", r)[3], s.values[0][r][1], 0.0);
		}

		mode[0] = "failing";
		assertNull(store.sampleRows(new String[] { "cytosol_dom/u" }, new int[] { 0 }, idx), "a failed call: read whole rows");
		mode[0] = "prefix";
		assertNotNull(store.sampleRows(new String[] { "cytosol_dom/u" }, new int[] { 0 }, idx), "a failure is not remembered");

		mode[0] = "old";
		int before = sampleCalls.get();
		assertNull(store.sampleRows(new String[] { "cytosol_dom/u" }, new int[] { 0 }, idx));
		assertNull(store.sampleRows(new String[] { "cytosol_dom/u" }, new int[] { 0 }, idx));
		assertEquals(before + 1, sampleCalls.get(), "an old data server is asked once, then never again");
		// ...and the bundle then reads whole rows, through the batched and single-file paths, with the same values
		FenicsBundle viaStore = FenicsBundle.open(BundleStore.cached(store));
		assertNull(viaStore.sampleFields("cytosol_dom", new String[] { "u" }, new int[] { 0, 1 }, idx));
		viaStore.prefetchField("cytosol_dom", "u", new int[] { 0, 1, 2 });
		assertArrayEquals(local.field("cytosol_dom", "u", 2), viaStore.field("cytosol_dom", "u", 2), 0.0);

		// a request beyond one call's caps is not sent: whole rows instead
		assertNull(new DataServerBundleStore((DataServerBundleStore.Server) null, "x").sampleRows(new String[17], new int[] { 0 }, idx));
	}

	/** a data server that predates even the batched call: samples, then batches, then single files */
	@Test
	public void theWholeFallbackChain() throws Exception {
		installBundle("membrane_efflux");
		DataSetControllerImpl ds = dataServer();
		AtomicInteger singles = new AtomicInteger();
		AtomicInteger batches = new AtomicInteger();
		DataServerBundleStore ancient = new DataServerBundleStore(new DataServerBundleStore.Server() {
			@Override
			public byte[] file(String relativePath) throws DataAccessException {
				singles.incrementAndGet();
				return ds.getFenicsBundleFile(SIM, relativePath);
			}

			@Override
			public byte[][] files(String[] relativePaths) throws DataAccessException {
				batches.incrementAndGet();
				throw new DataAccessException("No such method: getFenicsBundleFiles");
			}
			// samples(): the default, "No such method"
		}, "ancient");
		FenicsBundle viaStore = FenicsBundle.open(BundleStore.cached(ancient));
		assertNull(viaStore.sampleFields("cytosol_dom", new String[] { "u" }, new int[] { 0, 1, 2 }, new int[] { 1 }));
		viaStore.prefetchField("cytosol_dom", "u", new int[] { 0, 1, 2 });
		FenicsBundle local = FenicsBundle.open(FenicsBundleTest.fixture("membrane_efflux"));
		for (int r = 0; r < 3; r++) {
			assertArrayEquals(local.field("cytosol_dom", "u", r), viaStore.field("cytosol_dom", "u", r), 0.0);
		}
		assertEquals(1, batches.get(), "asked for a batch once");
		assertTrue(singles.get() >= 3, "then file by file");
	}

	/** the desktop's cache answers a repeated sampled read without asking again; an unwritten row is asked again */
	@Test
	public void sampledRowsAreCachedByTheirExactIndexSet() throws Exception {
		File bundle = installBundle("membrane_efflux");
		DataSetControllerImpl ds = dataServer();
		AtomicInteger sampleCalls = new AtomicInteger();
		BundleStore cached = BundleStore.cached(new DataServerBundleStore(new DataServerBundleStore.Server() {
			@Override
			public byte[] file(String relativePath) throws DataAccessException {
				return ds.getFenicsBundleFile(SIM, relativePath);
			}

			@Override
			public byte[][] files(String[] relativePaths) throws DataAccessException {
				return ds.getFenicsBundleFiles(SIM, relativePaths);
			}

			@Override
			public FenicsSamples samples(String[] arrayPaths, int[] indices, int[] rows) throws DataAccessException {
				sampleCalls.incrementAndGet();
				return ds.getFenicsBundleSamples(SIM, arrayPaths, indices, rows);
			}
		}, "test"));
		String[] u = { "cytosol_dom/u" };
		Files.delete(new File(bundle, "cytosol_dom/u/2.0").toPath()); // row 2 not written yet
		FenicsSamples first = cached.sampleRows(u, new int[] { 0, 1, 2 }, new int[] { 1, 4 });
		assertEquals(1, sampleCalls.get());
		FenicsSamples again = cached.sampleRows(u, new int[] { 0, 1 }, new int[] { 1, 4 });
		assertEquals(1, sampleCalls.get(), "written rows at the same indices: from the cache");
		assertArrayEquals(first.values[0][1], again.values[0][1], 0.0);
		cached.sampleRows(u, new int[] { 0, 2 }, new int[] { 1, 4 });
		assertEquals(2, sampleCalls.get(), "the unwritten row is asked for again");
		cached.sampleRows(u, new int[] { 0 }, new int[] { 1, 5 });
		assertEquals(3, sampleCalls.get(), "another index set is another sample");
	}
}
