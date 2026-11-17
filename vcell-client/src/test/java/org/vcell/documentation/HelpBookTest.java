package org.vcell.documentation;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks the help source in vcell-client/UserDocumentation with {@link HelpChecks}: every link,
 * anchor and image resolves, every page is reachable, and every {@link HelpIds} constant names a
 * page. A broken reference here would be a dead link or a blank Help window in the client.
 * {@link HelpSecurityTest} covers hostile content.
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
	public void helpSourceHasNoProblems() {
		List<HelpProblem> problems = HelpChecks.check(HelpBookTest.book);
		assertEquals("", problems.stream().map(HelpProblem::toString).collect(Collectors.joining("\n")));
		assertFalse(HelpBookTest.book.getTableOfContents().isEmpty());
		assertEquals(HelpIds.HOME, HelpBookTest.book.getHomePage().id(), "the first contents entry is the home page");
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
		List<String> found = HelpChecks.check(HelpBook.fromDirectory(helpRoot.toFile())).stream()
				.filter(problem -> problem.path().equals("pages/Page.md"))
				.map(HelpProblem::toString)
				.toList();
		assertEquals(5, found.size(), String.join("\n", found));
		assertTrue(found.get(0).startsWith("pages/Page.md:3: "), found.get(0));
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
		assertEquals(null, HelpBook.resolvePath("pages/New.md", "//host/share/x.md"));
		assertEquals(null, HelpBook.resolvePath("pages/New.md", "..\\..\\outside.md"));
		assertEquals("../a/B.html#c", HelpRenderer.toHtmlHref("../a/B.md#c"));
	}

	@Test
	public void reportsImagesThatAreUnusedOrLarge() throws IOException {
		Set<String> referenced = new HashSet<>();
		for (HelpPage page : HelpBookTest.book.getPages()) {
			for (Node node : page.document().getDescendants()) {
				if (!(node instanceof Image image)) continue;
				referenced.add(HelpBook.resolvePath(page.path(), image.getUrl().unescape()));
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
}
