package org.vcell.solver.fenics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.DeflaterOutputStream;

import org.vcell.util.DataAccessException;

import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;

/**
 * A real {@link DataSetControllerImpl} seen through {@link DataServerBundleStore}, as a desktop sees the data server,
 * counting RPCs and the bytes of each reply (Java-serialized and deflated, as the api relays it) and optionally
 * sleeping a fixed latency per RPC. {@code level} is the data server's release: 13 (single files only), 14 (batched
 * files), 15 (samples) or 17 (samples and located reads). For the benchmark, not for CI.
 */
public final class MeasuredDataServer implements DataServerBundleStore.Server {

	public final AtomicInteger rpcs = new AtomicInteger();
	public final AtomicLong bytes = new AtomicLong();
	private final DataSetControllerImpl ds;
	private final VCSimulationDataIdentifier sim;
	private final int level;
	private final long latencyMs;

	public MeasuredDataServer(DataSetControllerImpl ds, VCSimulationDataIdentifier sim, int level, long latencyMs) {
		this.ds = ds;
		this.sim = sim;
		this.level = level;
		this.latencyMs = latencyMs;
	}

	/** the desktop's store over this server: {@link BundleStore#cached} around {@link DataServerBundleStore} */
	public BundleStore store() {
		return BundleStore.cached(new DataServerBundleStore(this, "measured " + sim.getID()));
	}

	private <T> T reply(T object) throws DataAccessException {
		rpcs.incrementAndGet();
		try {
			ByteArrayOutputStream raw = new ByteArrayOutputStream();
			try (ObjectOutputStream out = new ObjectOutputStream(new DeflaterOutputStream(raw))) {
				out.writeObject(object);
			}
			bytes.addAndGet(raw.size());
			if (latencyMs > 0) {
				Thread.sleep(latencyMs);
			}
		} catch (IOException | InterruptedException e) {
			throw new DataAccessException(e.getMessage());
		}
		return object;
	}

	private DataAccessException noSuchMethod(String name) {
		rpcs.incrementAndGet();
		try {
			Thread.sleep(latencyMs);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return new DataAccessException("No such method: " + name + "(...)");
	}

	@Override
	public byte[] file(String relativePath) throws DataAccessException {
		return reply(ds.getFenicsBundleFile(sim, relativePath));
	}

	@Override
	public byte[][] files(String[] relativePaths) throws DataAccessException {
		if (level < 14) {
			throw noSuchMethod("getFenicsBundleFiles");
		}
		return reply(ds.getFenicsBundleFiles(sim, relativePaths));
	}

	@Override
	public FenicsSamples samples(String[] arrayPaths, int[] indices, int[] rows) throws DataAccessException {
		if (level < 15) {
			throw noSuchMethod("getFenicsBundleSamples");
		}
		return reply(ds.getFenicsBundleSamples(sim, arrayPaths, indices, rows));
	}

	@Override
	public FenicsLocatedSamples located(String meshPath, String coordsPath, String[] arrayPaths, double[] points, boolean snap,
			int[] rows) throws DataAccessException {
		if (level < 17) {
			throw noSuchMethod("getFenicsBundleLocatedSamples");
		}
		return reply(ds.getFenicsBundleLocatedSamples(sim, meshPath, coordsPath, arrayPaths, points, snap, rows));
	}
}
