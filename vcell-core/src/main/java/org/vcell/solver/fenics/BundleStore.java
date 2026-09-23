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

	/**
	 * Caches what never changes once written: meshes, array metadata and written chunks. The manifest
	 * ({@code .zattrs}, which grows while the solver runs) and absent files (an unwritten chunk may land
	 * later) are always re-read. For a remote store this turns scrubbing back and forth into one fetch
	 * per row.
	 */
	static BundleStore cached(BundleStore store) {
		java.util.Map<String, byte[]> cache = new java.util.concurrent.ConcurrentHashMap<>();
		return new BundleStore() {
			@Override
			public byte[] read(String relativePath) throws IOException {
				if (relativePath.endsWith(".zattrs")) {
					return store.read(relativePath);
				}
				byte[] cachedBytes = cache.get(relativePath);
				if (cachedBytes != null) {
					return cachedBytes;
				}
				byte[] bytes = store.read(relativePath);
				if (bytes != null) {
					cache.put(relativePath, bytes);
				}
				return bytes;
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
