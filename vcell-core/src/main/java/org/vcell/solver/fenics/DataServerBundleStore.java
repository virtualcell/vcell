package org.vcell.solver.fenics;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.simdata.VCDataManager;

/**
 * A FEniCSx results bundle read from the data server, for a simulation that ran on the cluster: one
 * file per {@link cbit.vcell.server.DataSetController#getFenicsBundleFile} call, or several per
 * {@link cbit.vcell.server.DataSetController#getFenicsBundleFiles} call ({@link #readAll}). Wrap it in
 * {@link BundleStore#cached} so each immutable file crosses the wire once.
 * <p>
 * A data server older than the batched call answers it with "No such method"; this store then reads
 * file by file from there on, as before.
 */
public final class DataServerBundleStore implements BundleStore {

	private static final Logger lg = LogManager.getLogger(DataServerBundleStore.class);

	/** paths asked for per batched call; the server may answer fewer (it caps the bytes per reply) */
	static final int MAX_PATHS_PER_CALL = 64;

	/** the server's interface, so a test can stand in for it */
	interface Server {
		byte[] file(String relativePath) throws DataAccessException;

		byte[][] files(String[] relativePaths) throws DataAccessException;

		/** a data server that predates sampled reads answers like this */
		default FenicsSamples samples(String[] arrayPaths, int[] indices, int[] rows) throws DataAccessException {
			throw new DataAccessException("No such method: getFenicsBundleSamples");
		}
	}

	private final Server server;
	private final String id;
	private volatile boolean batchUnsupported = false;
	private volatile boolean samplesUnsupported = false;

	public DataServerBundleStore(VCDataManager dataManager, VCDataIdentifier vcdID) {
		this(new Server() {
			@Override
			public byte[] file(String relativePath) throws DataAccessException {
				return dataManager.getFenicsBundleFile(vcdID, relativePath);
			}

			@Override
			public byte[][] files(String[] relativePaths) throws DataAccessException {
				return dataManager.getFenicsBundleFiles(vcdID, relativePaths);
			}

			@Override
			public FenicsSamples samples(String[] arrayPaths, int[] indices, int[] rows) throws DataAccessException {
				return dataManager.getFenicsBundleSamples(vcdID, arrayPaths, indices, rows);
			}
		}, vcdID.getID());
	}

	DataServerBundleStore(Server server, String id) {
		this.server = server;
		this.id = id;
	}

	@Override
	public byte[] read(String relativePath) throws IOException {
		try {
			return server.file(BundleStore.checkRelativePath(relativePath));
		} catch (DataAccessException e) {
			throw new IOException("reading " + relativePath + " of " + describe() + ": " + e.getMessage(), e);
		}
	}

	/**
	 * In batched calls of up to {@value #MAX_PATHS_PER_CALL} paths, each answering a prefix of what it
	 * was asked (the server stops once a reply is large enough); file by file if the server does not
	 * know the batched call, or a batched call fails.
	 */
	@Override
	public byte[][] readAll(List<String> relativePaths) throws IOException {
		byte[][] out = new byte[relativePaths.size()][];
		int next = 0;
		while (next < out.length && !batchUnsupported) {
			String[] ask = new String[Math.min(MAX_PATHS_PER_CALL, out.length - next)];
			for (int i = 0; i < ask.length; i++) {
				ask[i] = BundleStore.checkRelativePath(relativePaths.get(next + i));
			}
			byte[][] got;
			try {
				got = server.files(ask);
			} catch (Exception e) {
				String message = String.valueOf(e.getMessage());
				if (message.contains("No such method")) {
					lg.info("the data server does not batch FEniCSx bundle reads; reading file by file");
					batchUnsupported = true;
				} else {
					lg.warn("batched read of " + ask.length + " files of " + describe() + " failed; reading them one by one: "
							+ message, e);
				}
				break;
			}
			if (got == null || got.length == 0 || got.length > ask.length) {
				lg.warn("batched read of " + describe() + " answered " + (got == null ? "null" : got.length + " files")
						+ " for " + ask.length + "; reading the rest one by one");
				break;
			}
			System.arraycopy(got, 0, out, next, got.length);
			next += got.length;
		}
		for (; next < out.length; next++) {
			out[next] = read(relativePaths.get(next));
		}
		return out;
	}

	/**
	 * Sampled reads ({@link cbit.vcell.server.DataSetController#getFenicsBundleSamples}), in calls of at most
	 * {@link FenicsBundle#MAX_SAMPLE_ROWS} rows, each answering a prefix of its rows. Null -- read whole rows
	 * instead -- when the data server predates the call (remembered: it is not asked again), when a call fails,
	 * or when the request is beyond what one call may carry.
	 */
	@Override
	public FenicsSamples sampleRows(String[] arrayPaths, int[] rows, int[] indices) throws IOException {
		if (samplesUnsupported || arrayPaths.length > FenicsBundle.MAX_SAMPLE_ARRAYS || indices.length > FenicsBundle.MAX_SAMPLE_INDICES
				|| (long) arrayPaths.length * Math.max(1, indices.length) > FenicsBundle.MAX_SAMPLE_VALUES) {
			return null;
		}
		for (String path : arrayPaths) {
			BundleStore.checkRelativePath(path);
		}
		double[][][] values = new double[arrayPaths.length][rows.length][];
		boolean[][] written = new boolean[arrayPaths.length][rows.length];
		int next = 0;
		while (next < rows.length) {
			int[] ask = java.util.Arrays.copyOfRange(rows, next, Math.min(rows.length, next + FenicsBundle.MAX_SAMPLE_ROWS));
			FenicsSamples got;
			try {
				got = server.samples(arrayPaths, indices, ask);
			} catch (Exception e) {
				String message = String.valueOf(e.getMessage());
				if (message.contains("No such method")) {
					lg.info("the data server does not sample FEniCSx bundles; reading whole rows");
					samplesUnsupported = true;
				} else {
					lg.warn("sampled read of " + describe() + " failed; reading whole rows: " + message, e);
				}
				return null;
			}
			if (got == null || got.rows == null || got.rows.length == 0 || got.rows.length > ask.length
					|| got.values.length != arrayPaths.length) {
				lg.warn("sampled read of " + describe() + " answered no usable rows; reading whole rows");
				return null;
			}
			for (int r = 0; r < got.rows.length; r++) {
				if (got.rows[r] != ask[r]) {
					lg.warn("sampled read of " + describe() + " answered rows out of order; reading whole rows");
					return null;
				}
				for (int a = 0; a < arrayPaths.length; a++) {
					if (got.values[a][r].length != indices.length) {
						lg.warn("sampled read of " + describe() + " answered the wrong number of values; reading whole rows");
						return null;
					}
					values[a][next + r] = got.values[a][r];
					written[a][next + r] = got.written[a][r];
				}
			}
			next += got.rows.length;
		}
		return new FenicsSamples(rows.clone(), values, written);
	}

	@Override
	public String describe() {
		return "FEniCSx results of " + id + " (data server)";
	}
}
