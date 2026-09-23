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
}
