package org.vcell.documentation;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.util.ast.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
	public void everyPageHasATitle() {
		List<String> untitled = new ArrayList<>();
		for (HelpPage page : HelpBookTest.book.getPages()) {
			if (page.title().equals(page.id())) untitled.add(page.path());
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
		if (targetPage == null || !HelpBook.headingIds(targetPage).contains(fragment)) {
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
