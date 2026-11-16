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
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * The VCell help: a table of contents ({@code toc.md}) and the Markdown pages reachable from it.
 *
 * <p>Source lives in {@code vcell-client/UserDocumentation} and is copied onto the classpath under
 * {@value #CLASSPATH_ROOT} by the build. Pages are found by following links, starting from the
 * table of contents, so a page that nothing links to is not part of the book
 * (HelpBookTest fails on one). A page's id is its file name without ".md".
 */
public final class HelpBook {
	public static final String CLASSPATH_ROOT = "/vcellHelp/";
	public static final String TOC_PATH = "toc.md";

	private static final Logger LG = LogManager.getLogger(HelpBook.class);
	private static final Pattern EXTERNAL_HREF = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*:.*");
	private static final DataHolder MARKDOWN_OPTIONS = new MutableDataSet()
			.set(Parser.EXTENSIONS, List.of(TablesExtension.create()))
			.toImmutable();
	private static final Parser PARSER = Parser.builder(MARKDOWN_OPTIONS).build();

	private static HelpBook classpathBook;

	private final Function<String, URL> resourceLocator;
	private final String title;
	private final List<HelpTocEntry> tableOfContents;
	private final Map<String, HelpPage> pagesByPath = new LinkedHashMap<>();
	private final Map<String, HelpPage> pagesById = new HashMap<>();
	private final Map<String, List<String>> tocTrailsByPath = new HashMap<>();
	private final Map<String, Dimension> imageSizes = new ConcurrentHashMap<>();
	private final Map<String, Map<String, String>> headingTextsByPath = new ConcurrentHashMap<>();
	private final List<String> problems = new ArrayList<>();

	/**
	 * @param resourceLocator maps a path relative to the help root to its URL, or null when absent
	 */
	private HelpBook(Function<String, URL> resourceLocator) throws IOException {
		this.resourceLocator = resourceLocator;
		URL tocUrl = resourceLocator.apply(TOC_PATH);
		if (tocUrl == null) throw new IOException("help table of contents '" + TOC_PATH + "' not found");
		Document tocDocument = HelpBook.parse(HelpBook.readText(tocUrl));
		this.title = HelpBook.firstHeadingText(tocDocument, "VCell Help");
		this.tableOfContents = this.readTocList(tocDocument);
		this.loadPages();
	}

	/** The help bundled with the client; loaded once. */
	public static synchronized HelpBook fromClasspath() throws IOException {
		if (HelpBook.classpathBook != null) return HelpBook.classpathBook;
		HelpBook.classpathBook = new HelpBook(path -> HelpBook.class.getResource(CLASSPATH_ROOT + path));
		for (String problem : HelpBook.classpathBook.problems) LG.warn("help: " + problem);
		return HelpBook.classpathBook;
	}

	/** The help read straight from a source directory such as {@code vcell-client/UserDocumentation}. */
	public static HelpBook fromDirectory(File rootDirectory) throws IOException {
		return new HelpBook(path -> {
			File file = new File(rootDirectory, path);
			if (!file.isFile()) return null;
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				return null;
			}
		});
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

	/** @return URL of a file in the help, by path relative to the help root; null when absent */
	public URL getResourceUrl(String path) {
		return this.resourceLocator.apply(path);
	}

	/**
	 * Titles of the contents entries above the first place this page appears, outermost first;
	 * empty for a page that is not in the table of contents or is at its top level.
	 */
	public List<String> getTocTrail(String pagePath) {
		return this.tocTrailsByPath.getOrDefault(pagePath, List.of());
	}

	/** Things wrong with the source found while loading: missing pages, duplicate ids. */
	public List<String> getProblems() {
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
		if (fragment == null || fragment.isEmpty()) return null;
		Map<String, String> headingTextsById = this.headingTextsByPath.computeIfAbsent(page.path(), path -> HelpBook.headingTexts(page));
		if (headingTextsById.containsKey(fragment)) return fragment;
		String decoded = HelpBook.percentDecode(fragment).trim();
		for (Map.Entry<String, String> heading : headingTextsById.entrySet()) {
			if (heading.getValue().equalsIgnoreCase(decoded)) return heading.getKey();
		}
		// Obsidian drops some punctuation from heading links; compare letters and digits only
		String wanted = HelpBook.lettersAndDigits(decoded);
		if (wanted.isEmpty()) return null;
		for (Map.Entry<String, String> heading : headingTextsById.entrySet()) {
			if (HelpBook.lettersAndDigits(heading.getValue()).equals(wanted)) return heading.getKey();
		}
		return null;
	}

	/** Heading anchor id to heading text, in page order, as the renderer generates the ids. */
	private static Map<String, String> headingTexts(HelpPage page) {
		HeaderIdGenerator idGenerator = new HeaderIdGenerator(MARKDOWN_OPTIONS);
		idGenerator.generateIds(page.document());
		Map<String, String> texts = new LinkedHashMap<>();
		for (Node node : page.document().getDescendants()) {
			if (!(node instanceof Heading heading)) continue;
			texts.putIfAbsent(heading.getAnchorRefId(), HelpBook.textOf(heading));
		}
		return texts;
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

	/** Pixel size of an image in the help, or null when it is missing or unreadable. */
	public Dimension getImageSize(String imagePath) {
		Dimension cached = this.imageSizes.get(imagePath);
		if (cached != null) return cached;
		URL imageUrl = this.resourceLocator.apply(imagePath);
		if (imageUrl == null) return null;
		Dimension size = HelpBook.readImageSize(imageUrl);
		if (size != null) this.imageSizes.put(imagePath, size);
		return size;
	}

	static DataHolder markdownOptions() {
		return MARKDOWN_OPTIONS;
	}

	// ----------------------------------------------------------------------------- href helpers

	/** True for "https://...", "mailto:..." and other hrefs with a scheme. */
	public static boolean isExternal(String href) {
		return EXTERNAL_HREF.matcher(href).matches();
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
		if (HelpBook.isExternal(href)) return null;
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
		this.problems.add(TOC_PATH + ": no list of pages");
		return List.of();
	}

	private List<HelpTocEntry> readTocItems(BulletList bulletList, List<String> trail) {
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
					pagePath = HelpBook.resolvePath(TOC_PATH, link.getUrl().toString());
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

	/** Breadth-first from the table of contents, so pages keep contents order. */
	private void loadPages() {
		Deque<String> queue = new ArrayDeque<>();
		Set<String> queued = new HashSet<>();
		this.enqueueTocPages(this.tableOfContents, queue, queued);
		while (!queue.isEmpty()) {
			String path = queue.removeFirst();
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

	private HelpPage loadPage(String path) {
		if (!path.endsWith(".md")) {
			this.problems.add("'" + path + "' is linked as a page but is not a .md file");
			return null;
		}
		URL url = this.resourceLocator.apply(path);
		if (url == null) {
			this.problems.add("page '" + path + "' does not exist");
			return null;
		}
		Document document;
		try {
			document = HelpBook.parse(HelpBook.readText(url));
		} catch (IOException e) {
			this.problems.add("page '" + path + "' could not be read: " + e.getMessage());
			return null;
		}
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		String id = fileName.substring(0, fileName.length() - ".md".length());
		HelpPage page = new HelpPage(id, path, HelpBook.firstHeadingText(document, id), document);
		HelpPage existing = this.pagesById.putIfAbsent(id, page);
		if (existing != null) {
			this.problems.add("pages '" + existing.path() + "' and '" + path + "' share the id '" + id + "'");
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

	private static Document parse(String markdown) {
		return PARSER.parse(markdown);
	}

	private static String readText(URL url) throws IOException {
		try (InputStream in = url.openStream()) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private static Dimension readImageSize(URL imageUrl) {
		try (InputStream in = imageUrl.openStream(); ImageInputStream imageIn = ImageIO.createImageInputStream(in)) {
			if (imageIn == null) return null;
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageIn);
			if (!readers.hasNext()) return null;
			ImageReader reader = readers.next();
			try {
				reader.setInput(imageIn);
				return new Dimension(reader.getWidth(0), reader.getHeight(0));
			} finally {
				reader.dispose();
			}
		} catch (IOException e) {
			LG.warn("help: cannot read image size of " + imageUrl, e);
			return null;
		}
	}
}
