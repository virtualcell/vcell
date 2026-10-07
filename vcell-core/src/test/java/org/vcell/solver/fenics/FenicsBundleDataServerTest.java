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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The data server's side of viewing a cluster run's FEniCSx results (docs/plan-fenics.md, V5):
 * {@link DataSetControllerImpl#getFenicsBundleFile} finds {@code SimID_<key>_<job>_.fenics} in the
 * owner's user directory and serves its files, and nothing outside it; and the whole bundle reads the
 * same through it as from disk.
 */
@Tag("Fast")
public class FenicsBundleDataServerTest {

	private static final User OWNER = new User("schaff", new KeyValue("17"));
	private static final VCSimulationDataIdentifier SIM = new VCSimulationDataIdentifier(
			new VCSimulationIdentifier(new KeyValue("274514696"), OWNER), 0);

	@TempDir
	File tmp;

	private File primary;
	private File secondary;

	@BeforeEach
	public void setup() {
		primary = new File(tmp, "primary");
		secondary = new File(tmp, "secondary");
		assertTrue(new File(primary, OWNER.getName()).mkdirs());
		assertTrue(new File(secondary, OWNER.getName()).mkdirs());
	}

	private File installBundle(File root) throws Exception {
		File target = new File(new File(root, OWNER.getName()), "SimID_274514696_0_.fenics");
		Path src = FenicsBundleTest.fixture("membrane_efflux").toPath();
		try (Stream<Path> paths = Files.walk(src)) {
			for (Path p : (Iterable<Path>) paths::iterator) {
				Path dest = target.toPath().resolve(src.relativize(p).toString());
				if (Files.isDirectory(p)) Files.createDirectories(dest); else Files.copy(p, dest);
			}
		}
		return target;
	}

	private DataSetControllerImpl dataServer() throws Exception {
		return new DataSetControllerImpl(null, primary, secondary);
	}

	@Test
	public void servesFilesOfTheBundle() throws Exception {
		File bundle = installBundle(primary);
		DataSetControllerImpl ds = dataServer();
		assertArrayEquals(Files.readAllBytes(new File(bundle, ".zattrs").toPath()), ds.getFenicsBundleFile(SIM, ".zattrs"));
		assertArrayEquals(Files.readAllBytes(new File(bundle, "cytosol_dom/u/2.0").toPath()), ds.getFenicsBundleFile(SIM, "cytosol_dom/u/2.0"));
		assertNull(ds.getFenicsBundleFile(SIM, "cytosol_dom/u/7.0"), "an unwritten chunk is absent, not an error");
		assertNull(ds.getFenicsBundleFile(new VCSimulationDataIdentifier(SIM.getVcSimID(), 1), ".zattrs"), "no bundle for job 1");
	}

	@Test
	public void fallsBackToTheSecondaryUserDirectory() throws Exception {
		installBundle(secondary);
		assertNotNull(dataServer().getFenicsBundleFile(SIM, ".zattrs"));
	}

	@Test
	public void refusesPathsOutsideTheBundle() throws Exception {
		File bundle = installBundle(primary);
		Files.writeString(new File(new File(primary, OWNER.getName()), "secret.txt").toPath(), "not yours");
		DataSetControllerImpl ds = dataServer();
		for (String bad : List.of("../secret.txt", "/etc/passwd", "cytosol_dom/../../secret.txt", "a\\b", "", "./.zattrs", "cytosol_dom//u")) {
			assertThrows(IllegalArgumentException.class, () -> ds.getFenicsBundleFile(SIM, bad), bad);
		}
		// a link planted inside the bundle does not lead out of it
		Files.createSymbolicLink(new File(bundle, "link.txt").toPath(), new File(new File(primary, OWNER.getName()), "secret.txt").toPath());
		assertThrows(DataAccessException.class, () -> ds.getFenicsBundleFile(SIM, "link.txt"));
		// a directory is not a file
		assertThrows(DataAccessException.class, () -> ds.getFenicsBundleFile(SIM, "cytosol_dom"));
	}

	@Test
	public void theRpcDispatchFindsTheMethod() throws Exception {
		// the data server resolves an RPC by method name and argument count, by reflection, on DataServerImpl;
		// the request and the reply both cross JMS as Java-serialized objects
		File bundle = installBundle(primary);
		cbit.vcell.simdata.DataServerImpl server = new cbit.vcell.simdata.DataServerImpl(dataServer(), null);
		cbit.vcell.message.VCRpcRequest request = new cbit.vcell.message.VCRpcRequest(OWNER,
				cbit.vcell.message.VCRpcRequest.RpcServiceType.DATA, "getFenicsBundleFile", new Object[] { OWNER, SIM, ".zattrs" });
		cbit.vcell.message.VCRpcRequest overTheWire = roundTrip(request);
		Object reply = roundTrip(overTheWire.rpc(server));
		assertArrayEquals(Files.readAllBytes(new File(bundle, ".zattrs").toPath()), (byte[]) reply);

		cbit.vcell.message.VCRpcRequest traversal = new cbit.vcell.message.VCRpcRequest(OWNER,
				cbit.vcell.message.VCRpcRequest.RpcServiceType.DATA, "getFenicsBundleFile", new Object[] { OWNER, SIM, "../secret" });
		assertThrows(DataAccessException.class, () -> traversal.rpc(server), "refused paths reach the client as a data-access error");
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

	@Test
	public void theBundleReadsTheSameThroughTheDataServer() throws Exception {
		installBundle(primary);
		DataSetControllerImpl ds = dataServer();
		AtomicInteger calls = new AtomicInteger();
		BundleStore rpc = new BundleStore() {
			@Override
			public byte[] read(String relativePath) throws IOException {
				calls.incrementAndGet();
				try {
					return ds.getFenicsBundleFile(SIM, relativePath);
				} catch (DataAccessException e) {
					throw new IOException(e);
				}
			}

			@Override
			public String describe() {
				return "rpc";
			}
		};
		BundleStore cached = BundleStore.cached(rpc);
		FenicsBundle remote = FenicsBundle.open(cached);
		FenicsBundle local = FenicsBundle.open(FenicsBundleTest.fixture("membrane_efflux"));
		assertEquals(local.getTimes(), remote.getTimes());
		for (int row = 0; row < local.getTimes().size(); row++) {
			assertArrayEquals(local.field("cytosol_dom", "u", row), remote.field("cytosol_dom", "u", row), 0.0);
			assertArrayEquals(local.stats("cytosol_dom", "u", row), remote.stats("cytosol_dom", "u", row), 0.0);
		}
		assertArrayEquals(local.meshBytes("cytosol_dom", 0), remote.meshBytes("cytosol_dom", 0));

		// scrubbing again costs only the manifest: meshes, metadata and written rows are cached
		int before = calls.get();
		FenicsBundle again = remote.refresh();
		again.field("cytosol_dom", "u", 2);
		again.meshBytes("cytosol_dom", 0);
		assertEquals(before + 1, calls.get(), "only .zattrs is re-read");
	}
	@Test
	public void servesSeveralFilesInOneCall() throws Exception {
		File bundle = installBundle(primary);
		DataSetControllerImpl ds = dataServer();
		String[] paths = { "cytosol_dom/u/0.0", "cytosol_dom/u/7.0", ".zattrs", "cytosol_dom/u/2.0" };
		byte[][] files = ds.getFenicsBundleFiles(SIM, paths);
		assertEquals(4, files.length, "a small batch comes back whole");
		assertArrayEquals(Files.readAllBytes(new File(bundle, "cytosol_dom/u/0.0").toPath()), files[0]);
		assertNull(files[1], "an unwritten chunk is absent, not an error");
		assertArrayEquals(Files.readAllBytes(new File(bundle, ".zattrs").toPath()), files[2]);
		assertArrayEquals(Files.readAllBytes(new File(bundle, "cytosol_dom/u/2.0").toPath()), files[3]);
		assertThrows(IllegalArgumentException.class, () -> ds.getFenicsBundleFiles(SIM, new String[] { ".zattrs", "../secret" }));
		assertThrows(DataAccessException.class, () -> ds.getFenicsBundleFiles(SIM,
				new String[DataSetControllerImpl.MAX_FENICS_BUNDLE_BATCH_PATHS + 1]));

		// over the wire, by reflection, as the data server answers it
		cbit.vcell.simdata.DataServerImpl server = new cbit.vcell.simdata.DataServerImpl(ds, null);
		cbit.vcell.message.VCRpcRequest request = roundTrip(new cbit.vcell.message.VCRpcRequest(OWNER,
				cbit.vcell.message.VCRpcRequest.RpcServiceType.DATA, "getFenicsBundleFiles", new Object[] { OWNER, SIM, paths }));
		byte[][] reply = (byte[][]) roundTrip(request.rpc(server));
		for (int i = 0; i < paths.length; i++) {
			assertArrayEquals(files[i], reply[i]);
		}
	}

	@Test
	public void aLargeBatchAnswersAPrefix() throws Exception {
		File bundle = installBundle(primary);
		DataSetControllerImpl ds = dataServer();
		long twoChunks = new File(bundle, "cytosol_dom/u/1.0").length() + new File(bundle, "cytosol_dom/u/2.0").length();
		String[] paths = { "cytosol_dom/u/1.0", "cytosol_dom/u/2.0", "cytosol_dom/u/0.0", "cytosol_dom/u/3.0" };
		byte[][] files = ds.getFenicsBundleFiles(SIM, paths, twoChunks);
		assertEquals(2, files.length, "the reply stops before it would pass the byte budget");
		assertEquals(1, ds.getFenicsBundleFiles(SIM, paths, 1).length, "a reply always carries the first file");
	}

	/** an old data server, a failing one, and one that answers in parts, as the client store sees them */
	@Test
	public void theClientStoreBatchesAndFallsBack() throws Exception {
		installBundle(primary);
		DataSetControllerImpl ds = dataServer();
		AtomicInteger singles = new AtomicInteger();
		AtomicInteger batches = new AtomicInteger();
		String[] mode = { "prefix" };
		DataServerBundleStore store = new DataServerBundleStore(new DataServerBundleStore.Server() {
			@Override
			public byte[] file(String relativePath) throws DataAccessException {
				singles.incrementAndGet();
				return ds.getFenicsBundleFile(SIM, relativePath);
			}

			@Override
			public byte[][] files(String[] relativePaths) throws DataAccessException {
				batches.incrementAndGet();
				switch (mode[0]) {
					case "old": throw new DataAccessException("No such method: getFenicsBundleFiles(cbit.vcell.solver.VCSimulationDataIdentifier,[Ljava.lang.String;)");
					case "failing": throw new DataAccessException("Server is temporarily not responding");
					default: return java.util.Arrays.copyOf(ds.getFenicsBundleFiles(SIM, relativePaths), Math.min(2, relativePaths.length));
				}
			}
		}, "test");
		List<String> paths = List.of("cytosol_dom/u/0.0", "cytosol_dom/u/1.0", "cytosol_dom/u/7.0", "cytosol_dom/u/2.0", ".zattrs");
		byte[][] expected = new byte[paths.size()][];
		for (int i = 0; i < expected.length; i++) {
			expected[i] = ds.getFenicsBundleFile(SIM, paths.get(i));
		}

		byte[][] got = store.readAll(paths);
		for (int i = 0; i < expected.length; i++) {
			assertArrayEquals(expected[i], got[i], paths.get(i));
		}
		assertEquals(3, batches.get(), "answered two at a time: three calls");
		assertEquals(0, singles.get());

		mode[0] = "failing";
		got = store.readAll(paths);
		assertArrayEquals(expected[3], got[3]);
		assertEquals(4, batches.get());
		assertEquals(5, singles.get(), "a failed batch falls back to one file at a time");

		mode[0] = "old";
		store.readAll(paths);
		store.readAll(paths);
		assertEquals(5, batches.get(), "an old data server is asked for a batch once, then never again");
		assertEquals(15, singles.get());
	}

	@Test
	public void theCacheFetchesAVariablesRowsTogetherAndStaysWithinItsBudget() throws Exception {
		File bundle = installBundle(primary);
		DataSetControllerImpl ds = dataServer();
		AtomicInteger singles = new AtomicInteger();
		AtomicInteger batches = new AtomicInteger();
		DataServerBundleStore remote = new DataServerBundleStore(new DataServerBundleStore.Server() {
			@Override
			public byte[] file(String relativePath) throws DataAccessException {
				singles.incrementAndGet();
				return ds.getFenicsBundleFile(SIM, relativePath);
			}

			@Override
			public byte[][] files(String[] relativePaths) throws DataAccessException {
				batches.incrementAndGet();
				return ds.getFenicsBundleFiles(SIM, relativePaths);
			}
		}, "test");
		FenicsBundle local = FenicsBundle.open(FenicsBundleTest.fixture("membrane_efflux"));
		int n = local.getTimes().size();
		int[] rows = new int[n];
		for (int i = 0; i < n; i++) {
			rows[i] = i;
		}

		FenicsBundle viaCache = FenicsBundle.open(BundleStore.cached(remote));
		viaCache.prefetchField("cytosol_dom", "u", rows);
		assertEquals(1, batches.get(), "every row in one call");
		int afterPrefetch = singles.get();
		for (int row = 0; row < n; row++) {
			assertArrayEquals(local.field("cytosol_dom", "u", row), viaCache.field("cytosol_dom", "u", row), 0.0);
		}
		assertEquals(afterPrefetch, singles.get(), "the rows were already there");
		viaCache.prefetchField("cytosol_dom", "u", rows);
		assertEquals(1, batches.get(), "nothing left to fetch");

		// a budget of two chunks keeps only the two most recently used
		long twoChunks = new File(bundle, "cytosol_dom/u/1.0").length() + new File(bundle, "cytosol_dom/u/2.0").length();
		BundleStore small = BundleStore.cached(remote, twoChunks);
		small.prefetch(List.of("cytosol_dom/u/0.0", "cytosol_dom/u/1.0", "cytosol_dom/u/2.0"));
		int before = singles.get();
		small.read("cytosol_dom/u/2.0");
		small.read("cytosol_dom/u/1.0");
		assertEquals(before, singles.get(), "the newest two are kept");
		small.read("cytosol_dom/u/0.0");
		assertEquals(before + 1, singles.get(), "the oldest was evicted");
	}
}
