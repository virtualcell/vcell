package org.vcell.solver.fenics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Where a FEniCSx results bundle's files are read from: a local directory (a quick run's bundle, or
 * the data server reading its own storage), or the data server over RPC (a cluster run viewed from the
 * desktop). Paths are relative to the bundle root and use '/' ({@code .zattrs},
 * {@code cyto/u/3.0}, {@code mesh/cyto.vtu}).
 */
public interface BundleStore {

	/** @return the file's bytes, or null if it does not exist (an unwritten chunk is normal) */
	byte[] read(String relativePath) throws IOException;

	/** a human-readable name for messages */
	String describe();

	/**
	 * Several files at once, in order, each null if it does not exist. A remote store answers this in
	 * as few round trips as it can; the default reads them one by one.
	 */
	default byte[][] readAll(java.util.List<String> relativePaths) throws IOException {
		byte[][] out = new byte[relativePaths.size()][];
		for (int i = 0; i < out.length; i++) {
			out[i] = read(relativePaths.get(i));
		}
		return out;
	}

	/**
	 * A hint that {@code relativePaths} are about to be read: a caching store fetches the ones it lacks
	 * together ({@link #readAll}). Does nothing by default; a read afterwards is still the source of truth.
	 */
	default void prefetch(java.util.List<String> relativePaths) throws IOException {
	}

	/**
	 * Rejects anything but a plain relative path inside the bundle: no absolute paths, no '..', no
	 * backslashes, no empty segments. The data server applies this before touching its storage.
	 */
	static String checkRelativePath(String relativePath) {
		if (relativePath == null || relativePath.isEmpty() || relativePath.startsWith("/") || relativePath.contains("\\")
				|| relativePath.contains("\0")) {
			throw new IllegalArgumentException("invalid bundle path '" + relativePath + "'");
		}
		for (String segment : relativePath.split("/", -1)) {
			if (segment.isEmpty() || segment.equals(".") || segment.equals("..")) {
				throw new IllegalArgumentException("invalid bundle path '" + relativePath + "'");
			}
		}
		return relativePath;
	}

	/** {@link #cached(BundleStore)}'s default budget, in MB; {@code -Dvcell.fenics.bundleCacheMB} overrides it */
	int DEFAULT_CACHE_MB = 512;

	/**
	 * Caches what never changes once written: meshes, array metadata and written chunks. The manifest
	 * ({@code .zattrs}, which grows while the solver runs) and absent files (an unwritten chunk may land
	 * later) are always re-read. For a remote store this turns scrubbing back and forth into one fetch
	 * per row, and {@link #prefetch} turns a kymograph's or time series' rows into a few batched fetches.
	 * The cache keeps the most recently used files within {@code -Dvcell.fenics.bundleCacheMB}
	 * (default {@value #DEFAULT_CACHE_MB}).
	 */
	static BundleStore cached(BundleStore store) {
		return cached(store, Long.getLong("vcell.fenics.bundleCacheMB", DEFAULT_CACHE_MB) * 1024 * 1024);
	}

	/** {@link #cached(BundleStore)} keeping at most {@code maxBytes} of file contents */
	static BundleStore cached(BundleStore store, long maxBytes) {
		final class Lru extends java.util.LinkedHashMap<String, byte[]> {
			long bytes;

			Lru() {
				super(64, 0.75f, true);
			}

			synchronized byte[] lookup(String path) {
				return get(path);
			}

			synchronized void keep(String path, byte[] content) {
				if (content.length > maxBytes) {
					return;
				}
				byte[] old = put(path, content);
				bytes += content.length - (old != null ? old.length : 0);
				java.util.Iterator<java.util.Map.Entry<String, byte[]>> eldest = entrySet().iterator();
				while (bytes > maxBytes && eldest.hasNext()) {
					byte[] evicted = eldest.next().getValue();
					eldest.remove();
					bytes -= evicted.length;
				}
			}
		}
		Lru cache = new Lru();
		return new BundleStore() {
			@Override
			public byte[] read(String relativePath) throws IOException {
				if (relativePath.endsWith(".zattrs")) {
					return store.read(relativePath);
				}
				byte[] cachedBytes = cache.lookup(relativePath);
				if (cachedBytes != null) {
					return cachedBytes;
				}
				byte[] bytes = store.read(relativePath);
				if (bytes != null) {
					cache.keep(relativePath, bytes);
				}
				return bytes;
			}

			@Override
			public void prefetch(java.util.List<String> relativePaths) throws IOException {
				java.util.List<String> missing = new java.util.ArrayList<>();
				for (String path : new java.util.LinkedHashSet<>(relativePaths)) {
					if (!path.endsWith(".zattrs") && cache.lookup(path) == null) {
						missing.add(path);
					}
				}
				if (missing.isEmpty()) {
					return;
				}
				byte[][] fetched = store.readAll(missing);
				for (int i = 0; i < fetched.length; i++) {
					if (fetched[i] != null) {
						cache.keep(missing.get(i), fetched[i]);
					}
				}
			}

			@Override
			public String describe() {
				return store.describe();
			}
		};
	}

	/** a bundle directory on this machine */
	static BundleStore directory(File root) {
		Path rootPath = root.toPath().toAbsolutePath().normalize();
		return new BundleStore() {
			@Override
			public byte[] read(String relativePath) throws IOException {
				Path file = rootPath.resolve(checkRelativePath(relativePath)).normalize();
				if (!file.startsWith(rootPath)) {
					throw new IllegalArgumentException("bundle path '" + relativePath + "' escapes " + rootPath);
				}
				try {
					return Files.isRegularFile(file) ? Files.readAllBytes(file) : null;
				} catch (NoSuchFileException e) {
					return null; // removed between the check and the read
				}
			}

			@Override
			public String describe() {
				return rootPath.toString();
			}
		};
	}
}
