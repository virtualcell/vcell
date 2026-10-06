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
	}

	private final Server server;
	private final String id;
	private volatile boolean batchUnsupported = false;

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

	@Override
	public String describe() {
		return "FEniCSx results of " + id + " (data server)";
	}
}
