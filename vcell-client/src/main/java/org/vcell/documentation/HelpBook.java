package org.vcell.documentation;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.HardLineBreak;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.renderer.HeaderIdGenerator;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.Escaping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * The VCell help: a table of contents ({@code toc.md}) and the Markdown pages reachable from it.
 *
 * <p>Source lives in {@code vcell-client/UserDocumentation} and is copied onto the classpath under
 * {@value #CLASSPATH_ROOT} by the build; an author can also open a folder of it directly (see
 * {@link HelpSource.Folder}). Pages are found by following links, starting from the table of
 * contents, so a page that nothing links to is not part of the book (HelpChecks reports one). A
 * page's id is its file name without ".md".
 *
 * <p>Loading never fails on bad content: a file that is missing, too big, oddly named or
 * unparseable is left out and reported in {@link #getProblems()}. Limits on page count and
 * contents depth keep a hostile folder from making loading slow or deep.
 */
public final class HelpBook {
	public static final String CLASSPATH_ROOT = "/vcellHelp/";
	public static final String TOC_PATH = "toc.md";

	private static final Logger LG = LogManager.getLogger(HelpBook.class);
	/** Most pages loaded; the help has about 150. */
	public static final int MAX_PAGES = 1000;
	/** Deepest nesting of the contents; the help uses 4 levels. */
	public static final int MAX_TOC_DEPTH = 10;
	/** Largest image width or height shown, in pixels; a larger one is left out. */
	public static final int MAX_IMAGE_DIMENSION = 10_000;
	/** Most pixels in an image shown, so a small file cannot unpack into gigabytes. */
	public static final long MAX_IMAGE_PIXELS = 40_000_000L;
	/**
	 * Most parsed Markdown elements on one page, and in the whole book. A parsed element takes
	 * about 100 bytes, and text like "*a* *a* ..." makes one from every few characters, so these
	 * bound memory where byte limits cannot. The help has about 500 on its largest page and
	 * 16,000 in all.
	 */
	public static final int MAX_PAGE_NODES = 20_000;
	public static final int MAX_BOOK_NODES = 200_000;

	private static final Pattern EXTERNAL_HREF = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*:.*", Pattern.DOTALL);
	private static final DataHolder MARKDOWN_OPTIONS = new MutableDataSet()
			.set(Parser.EXTENSIONS, List.of(TablesExtension.create()))
			.toImmutable();
	private static final Parser PARSER = Parser.builder(MARKDOWN_OPTIONS).build();

	private static HelpBook classpathBook;

	private final HelpSource source;
	private final String title;
	private final List<HelpTocEntry> tableOfContents;
	private final Map<String, HelpPage> pagesByPath = new LinkedHashMap<>();
	private final Map<String, HelpPage> pagesById = new HashMap<>();
	private final Map<String, List<String>> tocTrailsByPath = new HashMap<>();
	private final Map<String, Dimension> imageSizes = new ConcurrentHashMap<>();
	private final Map<String, String> imageProblems = new ConcurrentHashMap<>();
	private final Map<String, HeadingIndex> headingIndexByPath = new ConcurrentHashMap<>();
	private final List<HelpProblem> problems = new ArrayList<>();
	private int bookNodes = 0;

	/** A page's headings, looked up by id, by text and by letters and digits, each in O(1). */
	private record HeadingIndex(Set<String> ids, Map<String, String> idByLowerText, Map<String, String> idByLetters) {}

	/**
	 * @throws IOException when the table of contents cannot be read or no page can; anything
	 *                     wrong with a page is a {@link #getProblems() problem} instead
	 */
	public HelpBook(HelpSource source) throws IOException {
		this.source = source;
		Document tocDocument = HelpBook.parse(HelpBook.readText(source, TOC_PATH));
		this.title = HelpBook.firstHeadingText(tocDocument, "VCell Help");
		this.tableOfContents = this.readTocList(tocDocument);
		this.loadPages();
		if (this.pagesByPath.isEmpty()) throw new IOException(TOC_PATH + " lists no page that could be read");
	}

	/** The help bundled with the client; loaded once. */
	public static synchronized HelpBook fromClasspath() throws IOException {
		if (HelpBook.classpathBook != null) return HelpBook.classpathBook;
		HelpBook.classpathBook = new HelpBook(HelpSource.BuiltIn.INSTANCE);
		for (HelpProblem problem : HelpBook.classpathBook.problems) LG.warn("help: " + problem);
		return HelpBook.classpathBook;
	}

	/**
	 * The help read straight from a source folder such as {@code vcell-client/UserDocumentation}.
	 *
	 * @throws HelpSource.FolderException when the folder is not help source
	 */
	public static HelpBook fromDirectory(File rootDirectory) throws IOException {
		return new HelpBook(HelpSource.Folder.open(rootDirectory));
	}

	// ---------------------------------------------------------------------------------- queries

	public String getTitle() {
		return this.title;
	}

	public List<HelpTocEntry> getTableOfContents() {
		return this.tableOfContents;
	}

	/** Pages in table-of-contents order, then pages only reachable by links. */
	public Collection<HelpPage> getPages() {
		return Collections.unmodifiableCollection(this.pagesByPath.values());
	}

	/** @return the page with this id, or null */
	public HelpPage findPage(String pageId) {
		return this.pagesById.get(pageId);
	}

	/** @return the page at this path relative to the help root, or null */
	public HelpPage getPageByPath(String path) {
		return this.pagesByPath.get(path);
	}

	/** The first page in the table of contents. */
	public HelpPage getHomePage() {
		return this.pagesByPath.values().iterator().next();
	}

	/** @return URL of a file in the help, by path relative to the help root; null when absent or not allowed */
	public URL getResourceUrl(String path) {
		return this.source.locate(path);
	}

	public HelpSource getSource() {
		return this.source;
	}

	/**
	 * Titles of the contents entries above the first place this page appears, outermost first;
	 * empty for a page that is not in the table of contents or is at its top level.
	 */
	public List<String> getTocTrail(String pagePath) {
		return this.tocTrailsByPath.getOrDefault(pagePath, List.of());
	}

	/** Things wrong found while loading: missing or unreadable pages, duplicate ids, limits reached. */
	public List<HelpProblem> getProblems() {
		return Collections.unmodifiableList(this.problems);
	}

	/** Plain text of a page, for search. */
	public static String plainText(HelpPage page) {
		return HelpBook.textOf(page.document());
	}

	/**
	 * The anchor id of the heading a link's "#fragment" names, or null if it names none.
	 *
	 * <p>Accepts the id the renderer writes ("reaction-rules", as GitHub and VS Code link
	 * headings) and the heading's own text ("Reaction%20Rules", as Obsidian links them), so pages
	 * can be edited in either tool.
	 */
	public String resolveHeadingId(HelpPage page, String fragment) {
		if (fragment == null || fragment.isEmpty() || fragment.length() > 500) return null;
		HeadingIndex index = this.headingIndexByPath.computeIfAbsent(page.path(), path -> HelpBook.headingIndex(page));
		if (index.ids().contains(fragment)) return fragment;
		String decoded = HelpBook.percentDecode(fragment).trim();
		String byText = index.idByLowerText().get(decoded.toLowerCase(Locale.ROOT));
		if (byText != null) return byText;
		// Obsidian drops some punctuation from heading links; compare letters and digits only
		String wanted = HelpBook.lettersAndDigits(decoded);
		return wanted.isEmpty() ? null : index.idByLetters().get(wanted);
	}

	/** The page's headings as the renderer generates their ids; the first heading wins a tie. */
	private static HeadingIndex headingIndex(HelpPage page) {
		HeaderIdGenerator idGenerator = new HeaderIdGenerator(MARKDOWN_OPTIONS);
		idGenerator.generateIds(page.document());
		Set<String> ids = new HashSet<>();
		Map<String, String> idByLowerText = new HashMap<>();
		Map<String, String> idByLetters = new HashMap<>();
		for (Node node : page.document().getDescendants()) {
			if (!(node instanceof Heading heading)) continue;
			String id = heading.getAnchorRefId();
			String text = HelpBook.textOf(heading);
			ids.add(id);
			idByLowerText.putIfAbsent(text.toLowerCase(Locale.ROOT), id);
			String letters = HelpBook.lettersAndDigits(text);
			if (!letters.isEmpty()) idByLetters.putIfAbsent(letters, id);
		}
		return new HeadingIndex(ids, idByLowerText, idByLetters);
	}

	/**
	 * The words a node shows: text and inline code, without markup, links' targets or HTML
	 * comments, one space between words and blocks. (flexmark's TextCollectingVisitor fails an
	 * internal length assertion on some of our pages, so this does not use it.)
	 */
	static String textOf(Node root) {
		StringBuilder text = new StringBuilder();
		for (Node node : root.getDescendants()) {
			if (node instanceof Code code) {
				text.append(code.getText());
			} else if (node instanceof Text && !(node.getParent() instanceof Code)) {
				text.append(Escaping.unescapeString(node.getChars()));
			} else if (node instanceof SoftLineBreak || node instanceof HardLineBreak || node instanceof Block) {
				text.append(' ');
			}
		}
		return text.toString().replaceAll("\\s+", " ").trim();
	}

	private static String percentDecode(String text) {
		try {
			// keep '+' literal: URLDecoder would turn it into a space
			return URLDecoder.decode(text.replace("+", "%2B"), StandardCharsets.UTF_8);
		} catch (IllegalArgumentException e) {
			return text;
		}
	}

	private static String lettersAndDigits(String text) {
		StringBuilder kept = new StringBuilder();
		text.codePoints().filter(Character::isLetterOrDigit).map(Character::toLowerCase).forEach(kept::appendCodePoint);
		return kept.toString();
	}

	/**
	 * Pixel size of an image in the help, or null when it must not be shown: missing, not
	 * allowed, unreadable, or over the size limits ({@link #imageProblem says why}).
	 */
	public Dimension getImageSize(String imagePath) {
		if (this.imageProblem(imagePath) != null) return null;
		return this.imageSizes.get(imagePath);
	}

	/** Why an image cannot be shown, or null when it can. */
	public String imageProblem(String imagePath) {
		if (this.imageSizes.containsKey(imagePath)) return null;
		String known = this.imageProblems.get(imagePath);
		if (known != null) return known;
		String problem = this.findImageProblem(imagePath);
		if (problem != null) this.imageProblems.put(imagePath, problem);
		return problem;
	}

	private String findImageProblem(String imagePath) {
		long bytes = this.source.size(imagePath);
		if (bytes < 0) return "is missing (names are case-sensitive, and links and odd names are not allowed)";
		if (bytes > HelpSource.MAX_IMAGE_BYTES) return "is larger than " + HelpSource.MAX_IMAGE_BYTES / (1024 * 1024) + " MB";
		Dimension size;
		try {
			size = HelpBook.readImageSize(this.source.read(imagePath, HelpSource.MAX_IMAGE_BYTES));
		} catch (IOException | RuntimeException e) {
			return "could not be read: " + e.getMessage();
		}
		if (size == null) return "is not an image VCell can show (use PNG, JPEG or GIF)";
		if (size.width <= 0 || size.height <= 0) return "has no size";
		if (size.width > MAX_IMAGE_DIMENSION || size.height > MAX_IMAGE_DIMENSION
				|| (long) size.width * size.height > MAX_IMAGE_PIXELS) {
			return "is too large to show (" + size.width + " x " + size.height + " pixels)";
		}
		this.imageSizes.put(imagePath, size);
		return null;
	}

	/**
	 * Decode an image for the help window, at about the size it is shown, so that Swing never
	 * decodes an image file itself: its decoder unpacks the whole image (and, for a GIF, the whole
	 * "logical screen" the header claims) however small it is drawn.
	 *
	 * @return the image, or null when it may not be shown or cannot be decoded
	 */
	public BufferedImage readDisplayImage(String imagePath, int width, int height) {
		Dimension size = this.getImageSize(imagePath);
		if (size == null || width <= 0 || height <= 0) return null;
		try (ImageInputStream imageIn = ImageIO.createImageInputStream(new ByteArrayInputStream(this.source.read(imagePath, HelpSource.MAX_IMAGE_BYTES)))) {
			if (imageIn == null) return null;
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageIn);
			if (!readers.hasNext()) return null;
			ImageReader reader = readers.next();
			try {
				reader.setInput(imageIn, true, true);
				ImageReadParam param = reader.getDefaultReadParam();
				int stepX = Math.max(1, reader.getWidth(0) / width);
				int stepY = Math.max(1, reader.getHeight(0) / height);
				param.setSourceSubsampling(stepX, stepY, 0, 0);
				return reader.read(0, param); // the first frame only, at about the shown size
			} finally {
				reader.dispose();
			}
		} catch (IOException | RuntimeException e) {
			LG.warn("help image " + imagePath + " could not be decoded: " + e);
			return null;
		}
	}

	static DataHolder markdownOptions() {
		return MARKDOWN_OPTIONS;
	}

	// ----------------------------------------------------------------------------- href helpers

	/** A plain email address, as a mailto link may name one. */
	private static final Pattern EMAIL_ADDRESS = Pattern.compile("[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]{1,255}");
	/** The only parts a mailto link may carry: some mail programs attach a file named in "attach=". */
	private static final Set<String> MAILTO_FIELDS = Set.of("subject", "body", "cc");

	/** True for "https://...", "mailto:..." and other hrefs with a scheme. */
	public static boolean isExternal(String href) {
		return EXTERNAL_HREF.matcher(href).matches();
	}

	/**
	 * True for an external href the help may open: an http(s) address with a host, or a mailto
	 * address with at most a subject, body and cc. Everything else ("file:", "javascript:",
	 * "smb:", app-specific schemes, mailto with "attach=") is refused, since the help hands it
	 * to the operating system. Only printable ASCII is allowed, so no invisible or right-to-left
	 * characters can disguise where a link goes.
	 */
	public static boolean isAllowedExternalLink(String href) {
		if (href == null || href.length() > 2000) return false;
		for (int i = 0; i < href.length(); i++) {
			char c = href.charAt(i);
			if (c <= ' ' || c >= 0x7F || c == '\\') return false;
		}
		try {
			URI uri = new URI(href);
			String scheme = uri.getScheme();
			if (scheme == null) return false;
			scheme = scheme.toLowerCase(Locale.ROOT);
			if (scheme.equals("mailto")) return HelpBook.isAllowedMailto(uri.getSchemeSpecificPart());
			if (!scheme.equals("http") && !scheme.equals("https")) return false;
			return uri.getHost() != null && !uri.getHost().isEmpty() && uri.getRawUserInfo() == null;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	/** "someone@vcell.org", or that with "?subject=...&body=..." (decoded); nothing else. */
	private static boolean isAllowedMailto(String schemeSpecificPart) {
		if (schemeSpecificPart == null) return false;
		int question = schemeSpecificPart.indexOf('?');
		String addresses = question < 0 ? schemeSpecificPart : schemeSpecificPart.substring(0, question);
		for (String address : addresses.split(",", -1)) {
			if (!EMAIL_ADDRESS.matcher(address).matches()) return false;
		}
		if (question < 0) return true;
		for (String field : schemeSpecificPart.substring(question + 1).split("&", -1)) {
			int equals = field.indexOf('=');
			String name = (equals < 0 ? field : field.substring(0, equals)).toLowerCase(Locale.ROOT);
			if (!MAILTO_FIELDS.contains(name)) return false;
		}
		return true;
	}

	/** The part of an href after '#', or null. */
	public static String fragmentOf(String href) {
		int hash = href.indexOf('#');
		return hash < 0 || hash == href.length() - 1 ? null : href.substring(hash + 1);
	}

	/**
	 * Resolve a relative href against the page that contains it.
	 *
	 * @return the target's path relative to the help root (fragment dropped), the page's own path
	 * for a bare "#fragment", or null for an external href or one that leaves the help root
	 */
	public static String resolvePath(String fromPath, String href) {
		if (href == null || HelpBook.isExternal(href) || href.startsWith("//") || href.contains("\\")) return null;
		int hash = href.indexOf('#');
		String target = hash < 0 ? href : href.substring(0, hash);
		if (target.isEmpty()) return fromPath;
		try {
			URI base = new URI(null, null, "/" + fromPath, null);
			String resolved = base.resolve(new URI(target)).normalize().getPath();
			if (resolved == null || !resolved.startsWith("/") || resolved.startsWith("/..")) return null;
			return resolved.substring(1);
		} catch (URISyntaxException e) {
			return null;
		}
	}

	// -------------------------------------------------------------------------------- loading

	private List<HelpTocEntry> readTocList(Document tocDocument) {
		for (Node node : tocDocument.getChildren()) {
			if (node instanceof BulletList bulletList) return this.readTocItems(bulletList, new ArrayList<>());
		}
		this.problems.add(HelpProblem.of(HelpProblem.Kind.CONTENTS, TOC_PATH, "no list of pages"));
		return List.of();
	}

	private List<HelpTocEntry> readTocItems(BulletList bulletList, List<String> trail) {
		if (trail.size() >= MAX_TOC_DEPTH) {
			this.problems.add(new HelpProblem(HelpProblem.Kind.CONTENTS, TOC_PATH, bulletList.getLineNumber() + 1,
					"the contents nest deeper than " + MAX_TOC_DEPTH + " levels; deeper entries are left out"));
			return List.of();
		}
		List<HelpTocEntry> entries = new ArrayList<>();
		for (Node itemNode : bulletList.getChildren()) {
			if (!(itemNode instanceof ListItem listItem)) continue;
			String entryTitle = "";
			String pagePath = null;
			List<HelpTocEntry> children = List.of();
			for (Node part : listItem.getChildren()) {
				if (part instanceof Paragraph paragraph) {
					entryTitle = HelpBook.textOf(paragraph);
					Link link = HelpBook.firstLink(paragraph);
					if (link == null) continue;
					pagePath = HelpBook.resolvePath(TOC_PATH, link.getUrl().unescape());
					this.checkTocTarget(link, pagePath);
					if (pagePath != null && !this.tocTrailsByPath.containsKey(pagePath)) {
						this.tocTrailsByPath.put(pagePath, List.copyOf(trail));
					}
					continue;
				}
				if (!(part instanceof BulletList nested)) continue;
				trail.add(entryTitle);
				children = this.readTocItems(nested, trail);
				trail.remove(trail.size() - 1);
			}
			entries.add(new HelpTocEntry(entryTitle, pagePath, children));
		}
		return List.copyOf(entries);
	}

	/** Report a contents entry that names no page; a page's own links are checked by HelpChecks. */
	private void checkTocTarget(Link link, String pagePath) {
		int line = link.getLineNumber() + 1;
		String href = link.getUrl().unescape();
		if (pagePath == null || !pagePath.endsWith(".md")) {
			this.problems.add(new HelpProblem(HelpProblem.Kind.CONTENTS, TOC_PATH, line, "entry '" + href + "' is not a page in the help"));
		} else if (!HelpSource.isAllowedPath(pagePath)) {
			this.problems.add(new HelpProblem(HelpProblem.Kind.FILE_NAME, TOC_PATH, line, "entry '" + href + "': page names may only use letters, digits, '_' and '-'"));
		} else if (this.source.locate(pagePath) == null) {
			this.problems.add(new HelpProblem(HelpProblem.Kind.BROKEN_LINK, TOC_PATH, line, "entry '" + href + "' names a page that does not exist (names are case-sensitive; links are not followed)"));
		}
	}

	/** Breadth-first from the table of contents, so pages keep contents order. */
	private void loadPages() {
		Deque<String> queue = new ArrayDeque<>();
		Set<String> queued = new HashSet<>();
		this.enqueueTocPages(this.tableOfContents, queue, queued);
		while (!queue.isEmpty()) {
			String path = queue.removeFirst();
			if (this.pagesByPath.size() >= MAX_PAGES) {
				this.problems.add(HelpProblem.of(HelpProblem.Kind.TOO_LARGE, "", "more than " + MAX_PAGES + " pages; the rest are left out"));
				return;
			}
			HelpPage page = this.loadPage(path);
			if (page == null) continue;
			for (String linkedPath : HelpBook.linkedPagePaths(page)) {
				if (queued.add(linkedPath)) queue.addLast(linkedPath);
			}
		}
	}

	private void enqueueTocPages(List<HelpTocEntry> entries, Deque<String> queue, Set<String> queued) {
		for (HelpTocEntry entry : entries) {
			if (entry.pagePath() != null && queued.add(entry.pagePath())) queue.addLast(entry.pagePath());
			this.enqueueTocPages(entry.children(), queue, queued);
		}
	}

	/** A page, or null when the link to it is broken (reported where the link is: toc.md or HelpChecks). */
	private HelpPage loadPage(String path) {
		if (!path.endsWith(".md") || !HelpSource.isAllowedPath(path) || this.source.locate(path) == null) return null;
		Document document;
		try {
			document = HelpBook.parse(HelpBook.readText(this.source, path));
		} catch (IOException e) {
			this.problems.add(HelpProblem.of(HelpProblem.Kind.UNREADABLE, path, "this page could not be read: " + e.getMessage()));
			return null;
		}
		int nodes = 0;
		for (Iterator<Node> iterator = document.getDescendants().iterator(); iterator.hasNext() && nodes <= MAX_PAGE_NODES; iterator.next()) {
			nodes++;
		}
		if (nodes > MAX_PAGE_NODES) {
			this.problems.add(HelpProblem.of(HelpProblem.Kind.TOO_LARGE, path, "this page has too much markup for one page (over " + MAX_PAGE_NODES + " elements); split it up"));
			return null;
		}
		if (this.bookNodes + nodes > MAX_BOOK_NODES) {
			this.problems.add(HelpProblem.of(HelpProblem.Kind.TOO_LARGE, path, "this page was left out: the help as a whole is over " + MAX_BOOK_NODES + " Markdown elements"));
			return null;
		}
		this.bookNodes += nodes;
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		String id = fileName.substring(0, fileName.length() - ".md".length());
		HelpPage page = new HelpPage(id, path, HelpBook.firstHeadingText(document, id), document);
		HelpPage existing = this.pagesById.putIfAbsent(id, page);
		if (existing != null) {
			this.problems.add(HelpProblem.of(HelpProblem.Kind.DUPLICATE_NAME, path, "this page has the same file name as " + existing.path() + "; page names must be unique"));
		}
		this.pagesByPath.put(path, page);
		return page;
	}

	/** Paths of the .md files a page links to. */
	private static List<String> linkedPagePaths(HelpPage page) {
		List<String> paths = new ArrayList<>();
		for (Node node : page.document().getDescendants()) {
			if (!(node instanceof Link link)) continue;
			String resolved = HelpBook.resolvePath(page.path(), link.getUrl().toString());
			if (resolved == null || resolved.equals(page.path()) || !resolved.endsWith(".md")) continue;
			paths.add(resolved);
		}
		return paths;
	}

	private static Link firstLink(Node parent) {
		for (Node node : parent.getDescendants()) {
			if (node instanceof Link link) return link;
		}
		return null;
	}

	private static String firstHeadingText(Document document, String fallback) {
		for (Node node : document.getDescendants()) {
			if (!(node instanceof Heading heading) || heading.getLevel() != 1) continue;
			return HelpBook.textOf(heading);
		}
		return fallback;
	}

	/** Parse Markdown; a pathological file that breaks the parser is reported as unreadable. */
	private static Document parse(String markdown) throws IOException {
		try {
			return PARSER.parse(markdown);
		} catch (RuntimeException | StackOverflowError e) {
			throw new IOException("its Markdown could not be parsed (" + e.getClass().getSimpleName() + ")", e);
		}
	}

	private static String readText(HelpSource source, String path) throws IOException {
		return new String(source.read(path, HelpSource.MAX_PAGE_BYTES), StandardCharsets.UTF_8);
	}

	/** Width and height from the image header, without decoding the pixels; null for other formats. */
	private static Dimension readImageSize(byte[] imageBytes) throws IOException {
		try (InputStream in = new ByteArrayInputStream(imageBytes); ImageInputStream imageIn = ImageIO.createImageInputStream(in)) {
			if (imageIn == null) return null;
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageIn);
			if (!readers.hasNext()) return null;
			ImageReader reader = readers.next();
			try {
				String format = reader.getFormatName().toLowerCase(Locale.ROOT);
				if (!format.equals("png") && !format.equals("jpeg") && !format.equals("gif")) return null;
				reader.setInput(imageIn);
				return new Dimension(reader.getWidth(0), reader.getHeight(0));
			} finally {
				reader.dispose();
			}
		}
	}
}
