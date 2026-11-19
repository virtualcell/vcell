package org.vcell.documentation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

/**
 * Checks the help source in vcell-client/UserDocumentation with {@link HelpChecks}: every link,
 * anchor and image resolves, every page is reachable, and every {@link HelpIds} constant names a
 * page. A broken reference here would be a dead link or a blank Help window in the client.
 * {@link HelpSecurityTest} covers hostile content.
 */
@Tag("Fast")
public class HelpBookTest {
	private static final File HELP_ROOT = new File("UserDocumentation");

	private static HelpBook book;

	@BeforeAll
	public static void loadBook() throws IOException {
		HelpBookTest.book = HelpBook.fromDirectory(HELP_ROOT);
	}

	@Test
	public void helpSourceHasNoErrors() {
		List<HelpProblem> problems = HelpChecks.check(HelpBookTest.book);
		// warnings (unused or large images) are worth tidying but fail nothing; list them in the log
		problems.stream().filter(HelpProblem::isWarning).forEach(System.out::println);
		Assertions.assertEquals("", HelpChecks.errors(problems).stream().map(HelpProblem::toString).collect(Collectors.joining("\n")));
		Assertions.assertFalse(HelpBookTest.book.getTableOfContents().isEmpty());
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
		Assertions.assertEquals(5, found.size(), String.join("\n", found));
		Assertions.assertTrue(found.get(0).startsWith("pages/Page.md:3: "), found.get(0));
	}

	@Test
	public void headingLinksWorkInGitHubAndObsidianForm() {
		HelpPage definitions = HelpBookTest.book.findPage("Definitions");
		Assertions.assertEquals("reaction-rules", HelpBookTest.book.resolveHeadingId(definitions, "reaction-rules"));
		Assertions.assertEquals("reaction-rules", HelpBookTest.book.resolveHeadingId(definitions, "Reaction%20Rules"));
		Assertions.assertEquals("reaction-rules", HelpBookTest.book.resolveHeadingId(definitions, "reaction rules"));
		Assertions.assertEquals(null, HelpBookTest.book.resolveHeadingId(definitions, "No%20Such%20Heading"));
	}

	@Test
	public void searchFindsTitleMatchesFirst() {
		HelpSearch search = new HelpSearch(HelpBookTest.book);
		List<HelpSearch.Hit> hits = search.search("field data");
		Assertions.assertFalse(hits.isEmpty());
		Assertions.assertTrue(hits.get(0).page().title().toLowerCase().contains("field data"), hits.get(0).page().title());
		Assertions.assertEquals(List.of(), search.search("   "));
		Assertions.assertEquals(List.of(), search.search("zzqqxx-no-such-word"));
	}

	@Test
	public void rendersImagesWithSizesAndWebLinksAsHtml() {
		HelpPage page = HelpBookTest.book.findPage("fieldData");
		String swingHtml = new HelpRenderer(HelpBookTest.book, HelpRenderer.Target.SWING, 600).renderBody(page);
		Assertions.assertTrue(swingHtml.matches("(?s).*<img[^>]* width=\"\\d+\" height=\"\\d+\".*"), swingHtml);
		Assertions.assertTrue(swingHtml.contains("<h1><a name="), "Swing headings carry <a name> anchors");

		HelpPage definitionsLinker = HelpBookTest.book.findPage("New");
		String webHtml = new HelpRenderer(HelpBookTest.book, HelpRenderer.Target.WEB, 600).renderBody(definitionsLinker);
		Assertions.assertFalse(webHtml.contains(".md\""), webHtml);
		Assertions.assertTrue(webHtml.contains(".html\""), webHtml);
	}

	@Test
	public void resolvesRelativeHrefs() {
		Assertions.assertEquals("pages/Physiology/Molecules.md", HelpBook.resolvePath("pages/Introduction/New.md", "../Physiology/Molecules.md#x"));
		Assertions.assertEquals("pages/Introduction/New.md", HelpBook.resolvePath("pages/Introduction/New.md", "#section"));
		Assertions.assertEquals(null, HelpBook.resolvePath("pages/Introduction/New.md", "https://vcell.org"));
		Assertions.assertEquals(null, HelpBook.resolvePath("pages/New.md", "../../outside.md"));
		Assertions.assertEquals(null, HelpBook.resolvePath("pages/New.md", "//host/share/x.md"));
		Assertions.assertEquals(null, HelpBook.resolvePath("pages/New.md", "..\\..\\outside.md"));
		Assertions.assertEquals("../a/B.html#c", HelpRenderer.toHtmlHref("../a/B.md#c"));
	}

	@Test
	public void warnsAboutImagesThatAreUnusedOrLarge(@TempDir Path helpRoot) throws IOException {
		Files.writeString(helpRoot.resolve("toc.md"), "# Help\n\n- [Page](pages/Page.md)\n", StandardCharsets.UTF_8);
		Files.createDirectories(helpRoot.resolve("pages"));
		Files.createDirectories(helpRoot.resolve("images"));
		Files.writeString(helpRoot.resolve("pages/Page.md"), String.join("\n",
				"# Page", "",
				"![](../images/Small.png)", "",
				"![](../images/Large.png)", "",
				"![](../images/Large.png) again", ""), StandardCharsets.UTF_8);
		HelpBookTest.writePng(helpRoot.resolve("images/Small.png"), 20, 20);
		HelpBookTest.writePng(helpRoot.resolve("images/Unused.png"), 20, 20);
		HelpBookTest.writePng(helpRoot.resolve("images/Large.png"), 600, 600); // random pixels: over 1 MB
		Assertions.assertTrue(Files.size(helpRoot.resolve("images/Large.png")) > HelpChecks.LARGE_IMAGE_BYTES);

		// this small help has none of the pages VCell opens by name, so look at the warnings only
		List<HelpProblem> problems = HelpChecks.check(HelpBook.fromDirectory(helpRoot.toFile()));
		Assertions.assertTrue(problems.stream().noneMatch(problem -> problem.kind() == HelpProblem.Kind.IMAGE), problems.toString());
		List<String> found = problems.stream().filter(HelpProblem::isWarning).map(HelpProblem::toString).toList();
		// a large image is reported once, where it is first shown, so choosing it opens that page
		Assertions.assertEquals(2, found.size(), String.join("\n", found));
		Assertions.assertTrue(found.get(0).startsWith("pages/Page.md:5: warning: image '../images/Large.png' is 1,"), found.get(0));
		Assertions.assertTrue(found.get(1).startsWith("images/Unused.png: warning: no page shows this image"), found.get(1));
		// errors come first, so the warnings are the end of the list
		Assertions.assertTrue(problems.get(problems.size() - 1).isWarning() && !problems.get(0).isWarning());
	}

	/** A PNG of random pixels, which PNG can't compress, so its size follows its dimensions. */
	static void writePng(Path file, int width, int height) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Random random = new Random(1);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) image.setRGB(x, y, random.nextInt(0x1000000));
		}
		ImageIO.write(image, "png", file.toFile());
	}
}
