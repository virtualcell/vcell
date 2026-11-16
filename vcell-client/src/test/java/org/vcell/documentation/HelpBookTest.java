package org.vcell.documentation;

import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.util.ast.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks the help source in vcell-client/UserDocumentation: every link, anchor and image resolves,
 * every page is reachable, and every {@link HelpIds} constant names a page. A broken reference
 * here would be a dead link or a blank Help window in the client.
 */
@Tag("Fast")
public class HelpBookTest {
	private static final File HELP_ROOT = new File("UserDocumentation");
	/** Images above this make the client jar heavy; DocumentCompiler used the same limit. */
	private static final long PREFERRED_MAX_IMAGE_BYTES = 500_000;
	/** A page's file name is its id, used in code and in web addresses; Obsidian writes spaces as %20. */
	private static final Pattern SAFE_FILE_NAME = Pattern.compile("[A-Za-z0-9_-]+\\.[A-Za-z0-9]+");

	/**
	 * Obsidian syntax that standard Markdown, and so the VCell help viewer, shows as literal text.
	 * Escaped characters (\\[\\[) and inline code are left alone.
	 */
	private record ObsidianSyntax(Pattern pattern, String instead) {}
	private static final List<ObsidianSyntax> OBSIDIAN_ONLY_SYNTAX = List.of(
			new ObsidianSyntax(Pattern.compile("(?<!\\\\)!?\\[\\["), "a [[wikilink]] or ![[embed]]: use [text](page.md) or ![](../../images/name.png)"),
			new ObsidianSyntax(Pattern.compile("(?<![\\\\=])==(?=\\S)[^=\\n]*\\S=="), "an ==highlight==: use **bold**"),
			new ObsidianSyntax(Pattern.compile("(?<!\\\\)%%"), "a %%comment%%: use <!-- comment -->"),
			new ObsidianSyntax(Pattern.compile("(?m)\\s\\^[A-Za-z0-9-]+$"), "a ^block-reference: link to a heading instead"));
	private static final Pattern INLINE_CODE = Pattern.compile("(`+).*?\\1", Pattern.DOTALL);

	private static HelpBook book;

	@BeforeAll
	public static void loadBook() throws IOException {
		HelpBookTest.book = HelpBook.fromDirectory(HELP_ROOT);
	}

	@Test
	public void loadsWithoutProblems() {
		assertEquals(List.of(), HelpBookTest.book.getProblems());
		assertFalse(HelpBookTest.book.getTableOfContents().isEmpty());
	}

	@Test
	public void everyPageFileIsReachableFromTheContents() throws IOException {
		Set<String> reachable = new HashSet<>();
		for (HelpPage page : HelpBookTest.book.getPages()) reachable.add(page.path());
		List<String> orphans = new ArrayList<>();
		try (Stream<Path> files = Files.walk(HELP_ROOT.toPath().resolve("pages"))) {
			files.filter(path -> path.toString().endsWith(".md"))
					.map(path -> HELP_ROOT.toPath().relativize(path).toString().replace(File.separatorChar, '/'))
					.filter(path -> !reachable.contains(path))
					.forEach(orphans::add);
		}
		assertEquals(List.of(), orphans, "pages nothing links to; add them to toc.md or link them from a page");
	}

	@Test
	public void noObsidianOnlySyntax() {
		List<String> found = new ArrayList<>();
		for (HelpPage page : HelpBookTest.book.getPages()) found.addAll(HelpBookTest.obsidianOnlySyntax(page));
		assertEquals(List.of(), found, "Obsidian-only syntax, which VCell's help shows as plain text");
	}

	@Test
	public void detectsObsidianOnlySyntax(@TempDir Path helpRoot) throws IOException {
		Files.writeString(helpRoot.resolve("toc.md"), "# Help\n\n- [Page](pages/Page.md)\n", StandardCharsets.UTF_8);
		Files.createDirectories(helpRoot.resolve("pages"));
		Files.writeString(helpRoot.resolve("pages/Page.md"), String.join("\n",
				"# Page", "",
				"See [[Other Page]] and ![[picture.png]].", "",
				"This is ==important== text. %% a note %%", "",
				"A paragraph to point at. ^my-block", "",
				"> [!note]", "> A callout.", "",
				"Fine: `[[in code]]`, \\[\\[escaped\\]\\], a == b, x^2 and > [plain quote].", ""), StandardCharsets.UTF_8);
		HelpPage page = HelpBook.fromDirectory(helpRoot.toFile()).findPage("Page");
		List<String> found = HelpBookTest.obsidianOnlySyntax(page);
		assertEquals(5, found.size(), String.join("\n", found));
		assertTrue(found.get(0).startsWith("pages/Page.md:3: "), found.get(0));
	}

	@Test
	public void fileNamesAreSafe() throws IOException {
		List<String> unsafe = new ArrayList<>();
		for (String folder : List.of("pages", "images")) {
			try (Stream<Path> files = Files.walk(HELP_ROOT.toPath().resolve(folder))) {
				files.filter(Files::isRegularFile)
						.filter(path -> !SAFE_FILE_NAME.matcher(path.getFileName().toString()).matches())
						.map(path -> HELP_ROOT.toPath().relativize(path).toString())
						.forEach(unsafe::add);
			}
		}
		assertEquals(List.of(), unsafe, "rename these using only letters, digits, '_' and '-' (no spaces)");
	}

	@Test
	public void headingLinksWorkInGitHubAndObsidianForm() {
		HelpPage definitions = HelpBookTest.book.findPage("Definitions");
		assertEquals("reaction-rules", HelpBookTest.book.resolveHeadingId(definitions, "reaction-rules"));
		assertEquals("reaction-rules", HelpBookTest.book.resolveHeadingId(definitions, "Reaction%20Rules"));
		assertEquals("reaction-rules", HelpBookTest.book.resolveHeadingId(definitions, "reaction rules"));
		assertEquals(null, HelpBookTest.book.resolveHeadingId(definitions, "No%20Such%20Heading"));
	}

	@Test
	public void everyPageHasATitle() {
		List<String> untitled = new ArrayList<>();
		for (HelpPage page : HelpBookTest.book.getPages()) {
			boolean hasTitle = false;
			for (Node node : page.document().getDescendants()) {
				if (node instanceof Heading heading && heading.getLevel() == 1) {
					hasTitle = true;
					break;
				}
			}
			if (!hasTitle) untitled.add(page.path());
		}
		assertEquals(List.of(), untitled, "pages without a '# Title' first heading");
	}

	@Test
	public void everyLinkAndImageResolves() {
		List<String> broken = new ArrayList<>();
		for (HelpPage page : HelpBookTest.book.getPages()) {
			for (Node node : page.document().getDescendants()) {
				if (node instanceof Image image) {
					HelpBookTest.checkImage(page, image.getUrl().toString(), broken);
				} else if (node instanceof Link link) {
					HelpBookTest.checkLink(page, link.getUrl().toString(), broken);
				}
			}
		}
		assertEquals(List.of(), broken);
	}

	@Test
	public void helpIdsNamePages() throws IllegalAccessException {
		List<String> missing = new ArrayList<>();
		for (Field field : HelpIds.class.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()) || field.getType() != String.class) continue;
			String pageId = (String) field.get(null);
			if (HelpBookTest.book.findPage(pageId) == null) missing.add(field.getName() + " = " + pageId);
		}
		assertEquals(List.of(), missing, "HelpIds constants without a page");
		assertEquals(HelpIds.HOME, HelpBookTest.book.getHomePage().id(), "the first contents entry is the home page");
	}

	@Test
	public void searchFindsTitleMatchesFirst() {
		HelpSearch search = new HelpSearch(HelpBookTest.book);
		List<HelpSearch.Hit> hits = search.search("field data");
		assertFalse(hits.isEmpty());
		assertTrue(hits.get(0).page().title().toLowerCase().contains("field data"), hits.get(0).page().title());
		assertEquals(List.of(), search.search("   "));
		assertEquals(List.of(), search.search("zzqqxx-no-such-word"));
	}

	@Test
	public void rendersImagesWithSizesAndWebLinksAsHtml() {
		HelpPage page = HelpBookTest.book.findPage("fieldData");
		String swingHtml = new HelpRenderer(HelpBookTest.book, HelpRenderer.Target.SWING, 600).renderBody(page);
		assertTrue(swingHtml.matches("(?s).*<img[^>]* width=\"\\d+\" height=\"\\d+\".*"), swingHtml);
		assertTrue(swingHtml.contains("<h1><a name="), "Swing headings carry <a name> anchors");

		HelpPage definitionsLinker = HelpBookTest.book.findPage("New");
		String webHtml = new HelpRenderer(HelpBookTest.book, HelpRenderer.Target.WEB, 600).renderBody(definitionsLinker);
		assertFalse(webHtml.contains(".md\""), webHtml);
		assertTrue(webHtml.contains(".html\""), webHtml);
	}

	@Test
	public void resolvesRelativeHrefs() {
		assertEquals("pages/Physiology/Molecules.md", HelpBook.resolvePath("pages/Introduction/New.md", "../Physiology/Molecules.md#x"));
		assertEquals("pages/Introduction/New.md", HelpBook.resolvePath("pages/Introduction/New.md", "#section"));
		assertEquals(null, HelpBook.resolvePath("pages/Introduction/New.md", "https://vcell.org"));
		assertEquals(null, HelpBook.resolvePath("pages/New.md", "../../outside.md"));
		assertEquals("../a/B.html#c", HelpRenderer.toHtmlHref("../a/B.md#c"));
	}

	@Test
	public void reportsImagesThatAreUnusedOrLarge() throws IOException {
		Set<String> referenced = new HashSet<>();
		for (HelpPage page : HelpBookTest.book.getPages()) {
			for (Node node : page.document().getDescendants()) {
				if (!(node instanceof Image image)) continue;
				referenced.add(HelpBook.resolvePath(page.path(), image.getUrl().toString()));
			}
		}
		Set<String> unused = new TreeSet<>();
		Set<String> large = new TreeSet<>();
		try (Stream<Path> files = Files.walk(HELP_ROOT.toPath().resolve("images"))) {
			for (Path file : (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
				String path = HELP_ROOT.toPath().relativize(file).toString().replace(File.separatorChar, '/');
				if (!referenced.contains(path)) unused.add(path);
				if (Files.size(file) > PREFERRED_MAX_IMAGE_BYTES) large.add(path + " (" + Files.size(file) + " bytes)");
			}
		}
		// informational, as DocumentCompiler's were: removing images is a content decision
		if (!unused.isEmpty()) System.out.println("Help images no page uses (" + unused.size() + "): " + unused);
		if (!large.isEmpty()) System.out.println("Help images over " + PREFERRED_MAX_IMAGE_BYTES + " bytes: " + large);
	}

	/** One "path:line: what was found" entry per Obsidian-only construct on the page. */
	private static List<String> obsidianOnlySyntax(HelpPage page) {
		List<String> found = new ArrayList<>();
		for (Node node : page.document().getDescendants()) {
			if (!(node instanceof Paragraph) && !(node instanceof Heading)) continue;
			String where = page.path() + ":" + (node.getLineNumber() + 1) + ": ";
			String text = INLINE_CODE.matcher(node.getChars().toString()).replaceAll("");
			if (node.getParent() instanceof BlockQuote quote && quote.getFirstChild() == node && text.startsWith("[!")) {
				found.add(where + "a > [!callout]: use a plain > quote or **Note:**");
			}
			for (ObsidianSyntax syntax : OBSIDIAN_ONLY_SYNTAX) {
				if (syntax.pattern().matcher(text).find()) found.add(where + syntax.instead());
			}
		}
		return found;
	}

	private static void checkImage(HelpPage page, String src, List<String> broken) {
		if (HelpBook.isExternal(src)) {
			broken.add(page.path() + ": image '" + src + "' must be a file in UserDocumentation/images");
			return;
		}
		String imagePath = HelpBook.resolvePath(page.path(), src);
		if (imagePath == null || !HelpBookTest.existsWithExactCase(imagePath)) {
			broken.add(page.path() + ": missing image '" + src + "' (names are case-sensitive in the client jar)");
		}
	}

	private static void checkLink(HelpPage page, String href, List<String> broken) {
		if (HelpBook.isExternal(href)) return;
		String targetPath = HelpBook.resolvePath(page.path(), href);
		if (targetPath == null || !HelpBookTest.existsWithExactCase(targetPath)) {
			broken.add(page.path() + ": broken link '" + href + "'");
			return;
		}
		String fragment = HelpBook.fragmentOf(href);
		if (fragment == null || !targetPath.endsWith(".md")) return;
		HelpPage targetPage = HelpBookTest.book.getPageByPath(targetPath);
		if (targetPage == null || HelpBookTest.book.resolveHeadingId(targetPage, fragment) == null) {
			broken.add(page.path() + ": link '" + href + "' names no heading in " + targetPath);
		}
	}

	/** File.exists is case-insensitive on macOS and Windows; jar entries are not. */
	private static boolean existsWithExactCase(String relativePath) {
		File file = HELP_ROOT;
		for (String segment : relativePath.split("/")) {
			String[] names = file.list();
			if (names == null || !Arrays.asList(names).contains(segment)) return false;
			file = new File(file, segment);
		}
		return file.exists();
	}
}
