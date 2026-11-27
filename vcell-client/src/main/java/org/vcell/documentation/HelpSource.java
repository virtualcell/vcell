package org.vcell.documentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Where the help's files come from: the copy built into the client, or a folder an author picked.
 *
 * <p>Every file is asked for by a path relative to the help root, and both sources refuse any
 * path that is not {@linkplain #isAllowedPath plain}: letters, digits, '_' and '-', one extension,
 * no "..", no leading '/', no Windows device names. A folder additionally refuses anything that is
 * a symbolic link, sits outside the folder, or differs in case from the name on disk, so a page
 * can only ever reach the files that are really in the help folder, exactly as the client jar
 * would hold them.
 */
public interface HelpSource {
	/** Largest Markdown file read; the biggest page today is about 14 KB. */
	long MAX_PAGE_BYTES = 256 * 1024;
	/** Largest image shown; the biggest today is about 1.4 MB. */
	long MAX_IMAGE_BYTES = 8 * 1024 * 1024;
	/** Most files looked at when listing a folder; the help has about 600. */
	int MAX_FOLDER_FILES = 5000;
	int MAX_PATH_SEGMENTS = 8;
	int MAX_PATH_LENGTH = 300;

	/** A file name: letters, digits, '_' and '-', with at most one short extension. */
	Pattern ALLOWED_SEGMENT = Pattern.compile("[A-Za-z0-9_-]{1,100}(\\.[A-Za-z0-9]{1,10})?");
	/** Names Windows treats as devices whatever their extension: reading "CON.md" would wait on the console. */
	Set<String> WINDOWS_DEVICE_NAMES = Set.of("CON", "PRN", "AUX", "NUL",
			"COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
			"LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9");

	/** @return URL of the file at this help-relative path, or null when it is absent or not allowed */
	URL locate(String path);

	/** Size in bytes of an allowed file, or -1 when it is absent or not allowed. */
	long size(String path);

	/** Read an allowed file, failing if it is larger than {@code maxBytes}. */
	byte[] read(String path, long maxBytes) throws IOException;

	/** Paths of every entry under pages/ and images/, links and odd names included; empty when not listable. */
	List<String> listFiles() throws IOException;

	/** For people: "the built-in help" or the folder's path. */
	String describe();

	/**
	 * True for a path the help may contain: segments of letters, digits, '_' and '-' joined by
	 * '/', the last with at most one extension.
	 */
	static boolean isAllowedPath(String path) {
		if (path == null || path.isEmpty() || path.length() > MAX_PATH_LENGTH) return false;
		String[] segments = path.split("/", -1);
		if (segments.length > MAX_PATH_SEGMENTS) return false;
		for (String segment : segments) {
			if (!ALLOWED_SEGMENT.matcher(segment).matches()) return false;
			String baseName = segment.contains(".") ? segment.substring(0, segment.indexOf('.')) : segment;
			if (WINDOWS_DEVICE_NAMES.contains(baseName.toUpperCase(Locale.ROOT))) return false;
		}
		return true;
	}

	private static byte[] readAtMost(InputStream in, long maxBytes, String path) throws IOException {
		byte[] bytes = in.readNBytes((int) Math.min(Integer.MAX_VALUE - 8, maxBytes + 1));
		if (bytes.length > maxBytes) throw new IOException(path + " is larger than " + maxBytes + " bytes");
		return bytes;
	}

	// ------------------------------------------------------------------------------- built-in

	/** The help copied into the client jar under {@value HelpBook#CLASSPATH_ROOT}. */
	final class BuiltIn implements HelpSource {
		static final BuiltIn INSTANCE = new BuiltIn();

		private BuiltIn() {
		}

		@Override
		public URL locate(String path) {
			if (!HelpSource.isAllowedPath(path)) return null;
			return HelpSource.class.getResource(HelpBook.CLASSPATH_ROOT + path);
		}

		@Override
		public long size(String path) {
			URL url = this.locate(path);
			if (url == null) return -1;
			try {
				URLConnection connection = url.openConnection();
				connection.setUseCaches(false);
				long length = connection.getContentLengthLong();
				connection.getInputStream().close();
				return length;
			} catch (IOException e) {
				return -1;
			}
		}

		@Override
		public byte[] read(String path, long maxBytes) throws IOException {
			URL url = this.locate(path);
			if (url == null) throw new IOException(path + " is not in the help");
			try (InputStream in = url.openStream()) {
				return HelpSource.readAtMost(in, maxBytes, path);
			}
		}

		@Override
		public List<String> listFiles() {
			return List.of();
		}

		@Override
		public String describe() {
			return "the built-in help";
		}
	}

	// --------------------------------------------------------------------------------- folder

	/** Why a folder cannot be used as help, in words for the person who picked it. */
	final class FolderException extends IOException {
		public FolderException(String message) {
			super(message);
		}
	}

	/** Help source read from a folder on disk, such as a checkout's vcell-client/UserDocumentation. */
	final class Folder implements HelpSource {
		private final Path root;

		private Folder(Path root) {
			this.root = root;
		}

		/**
		 * Check that a folder looks like help source (a toc.md and a pages folder) and open it.
		 *
		 * @throws FolderException with a message for the person who picked it
		 */
		public static Folder open(File directory) throws FolderException {
			if (directory == null) throw new FolderException("No folder was chosen.");
			String given = directory.getPath();
			if (given.startsWith("\\\\") || given.startsWith("//")) {
				throw new FolderException("Help can't be read from a network folder (" + given + "). Copy it to this computer first.");
			}
			Path root;
			try {
				root = directory.toPath().toRealPath();
			} catch (IOException | RuntimeException e) {
				throw new FolderException("The folder " + given + " can't be found.");
			}
			if (root.toString().startsWith("\\\\")) {
				throw new FolderException("Help can't be read from a network folder (" + root + "). Copy it to this computer first.");
			}
			if (!Files.isDirectory(root)) throw new FolderException(given + " is not a folder.");
			Folder folder = new Folder(root);
			if (folder.locate(HelpBook.TOC_PATH) == null) {
				throw new FolderException("This folder has no " + HelpBook.TOC_PATH + ". Choose the help source folder"
						+ " (vcell-client/UserDocumentation in a VCell checkout), the one that holds toc.md, pages and images.");
			}
			if (!Files.isDirectory(root.resolve("pages"), LinkOption.NOFOLLOW_LINKS)) {
				throw new FolderException("This folder has a " + HelpBook.TOC_PATH + " but no pages folder, so it is not VCell help source.");
			}
			return folder;
		}

		public Path getRoot() {
			return this.root;
		}

		/** The file for an allowed path, if it is a plain file really inside the folder under exactly this name. */
		private Path resolve(String path) {
			if (!HelpSource.isAllowedPath(path)) return null;
			Path candidate = this.root.resolve(path);
			try {
				// toRealPath follows links and reports the case on disk; any difference means a
				// symbolic link somewhere on the way, or a name in the wrong case
				if (!candidate.toRealPath().equals(candidate)) return null;
			} catch (IOException | RuntimeException e) {
				return null;
			}
			if (!Files.isRegularFile(candidate, LinkOption.NOFOLLOW_LINKS)) return null;
			return candidate;
		}

		@Override
		public URL locate(String path) {
			Path file = this.resolve(path);
			if (file == null) return null;
			try {
				return file.toUri().toURL();
			} catch (IOException | RuntimeException e) {
				return null;
			}
		}

		@Override
		public long size(String path) {
			Path file = this.resolve(path);
			if (file == null) return -1;
			try {
				return Files.size(file);
			} catch (IOException e) {
				return -1;
			}
		}

		@Override
		public byte[] read(String path, long maxBytes) throws IOException {
			Path file = this.resolve(path);
			if (file == null) throw new IOException(path + " is not a file in the help folder");
			try (InputStream in = Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS)) {
				return HelpSource.readAtMost(in, maxBytes, path);
			}
		}

		@Override
		public List<String> listFiles() throws IOException {
			List<String> paths = new ArrayList<>();
			for (String top : List.of("pages", "images")) {
				Path start = this.root.resolve(top);
				if (!Files.isDirectory(start, LinkOption.NOFOLLOW_LINKS)) continue;
				// walk does not follow links; a link is listed as an entry of its own
				int entriesSeen = 0;
				try (Stream<Path> entries = Files.walk(start, MAX_PATH_SEGMENTS)) {
					for (Path entry : (Iterable<Path>) entries::iterator) {
						if (++entriesSeen > MAX_FOLDER_FILES) {
							throw new FolderException("The help folder has more than " + MAX_FOLDER_FILES + " files and folders in " + top + ".");
						}
						if (Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)) continue;
						String relative = this.root.relativize(entry).toString().replace(File.separatorChar, '/');
						if (relative.contains("/.")) continue; // .DS_Store and other hidden files
						paths.add(relative);
					}
				} catch (java.io.UncheckedIOException e) {
					// a folder that can't be listed: report it, rather than failing the whole check
					throw new FolderException("Part of the " + top + " folder can't be read: " + e.getCause().getMessage());
				}
			}
			return paths;
		}

		@Override
		public String describe() {
			return this.root.toString();
		}
	}
}
