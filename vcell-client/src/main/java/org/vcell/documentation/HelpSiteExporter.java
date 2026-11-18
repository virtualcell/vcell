package org.vcell.documentation;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.ImageRef;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Writes the help as static HTML for the web (index.html, pages/, images/, help.css), as published
 * at vcell.org by .github/workflows/webhelp-deploy.yml.
 *
 * <p>Usage: {@code HelpSiteExporter <help source dir> <output dir>}; the vcell-client "web-help"
 * Maven profile runs it into target/webhelp.
 */
public final class HelpSiteExporter {
	private static final int MAX_IMAGE_WIDTH = 900;
	private static final String STYLESHEET = "help.css";
	private static final String CSS = """
			body { font-family: -apple-system, "Segoe UI", Helvetica, Arial, sans-serif; line-height: 1.5;
			       max-width: 60em; margin: 0 auto; padding: 1em 1.5em 3em; color: #1f2328; background: #fff; }
			nav { font-size: 0.9em; margin-bottom: 1.5em; }
			a { color: #1a5fb4; }
			img { max-width: 100%; height: auto; }
			code, pre { background: #f2f3f5; }
			table { border-collapse: collapse; }
			th, td { border: 1px solid #d0d7de; padding: 0.25em 0.6em; }
			@media (prefers-color-scheme: dark) {
			  body { color: #e6e6e6; background: #1e1f22; }
			  a { color: #8cb4ff; }
			  code, pre { background: #2b2d31; }
			  th, td { border-color: #44474d; }
			}
			""";

	private HelpSiteExporter() {
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java " + HelpSiteExporter.class.getName() + " <help source dir> <output dir>");
			System.exit(1);
		}
		HelpSiteExporter.export(new File(args[0]), new File(args[1]).toPath());
	}

	public static void export(File sourceDirectory, Path outputDirectory) throws IOException {
		HelpBook book = HelpBook.fromDirectory(sourceDirectory);
		List<HelpProblem> problems = HelpChecks.check(book);
		List<HelpProblem> errors = HelpChecks.errors(problems);
		if (!errors.isEmpty()) {
			throw new IOException("help source has problems:\n" + errors.stream().map(HelpProblem::toString).collect(java.util.stream.Collectors.joining("\n")));
		}
		// warnings don't stop the export; list them for whoever reads the build log
		for (HelpProblem problem : problems) System.err.println(problem);
		HelpRenderer renderer = new HelpRenderer(book, HelpRenderer.Target.WEB, MAX_IMAGE_WIDTH);
		Files.createDirectories(outputDirectory);
		Files.writeString(outputDirectory.resolve(STYLESHEET), CSS, StandardCharsets.UTF_8);
		Files.writeString(outputDirectory.resolve("index.html"), HelpSiteExporter.indexHtml(book), StandardCharsets.UTF_8);
		for (HelpPage page : book.getPages()) {
			Path pageFile = outputDirectory.resolve(HelpRenderer.toHtmlHref(page.path()));
			Files.createDirectories(pageFile.getParent());
			String toRoot = "../".repeat((int) page.path().chars().filter(c -> c == '/').count());
			Files.writeString(pageFile, HelpSiteExporter.pageHtml(page.title(), toRoot, renderer.renderBody(page)), StandardCharsets.UTF_8);
		}
		int images = HelpSiteExporter.copyImages(book, outputDirectory);
		System.out.println("wrote " + book.getPages().size() + " help pages and " + images + " images to " + outputDirectory);
	}

	private static String pageHtml(String title, String toRoot, String body) {
		return "<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
				+ "<title>" + HelpSiteExporter.escape(title) + " - VCell Help</title>\n"
				+ "<link rel=\"stylesheet\" href=\"" + toRoot + STYLESHEET + "\">\n</head>\n<body>\n"
				+ "<nav><a href=\"" + toRoot + "index.html\">VCell Help contents</a></nav>\n"
				+ body + "</body>\n</html>\n";
	}

	private static String indexHtml(HelpBook book) {
		StringBuilder list = new StringBuilder();
		HelpSiteExporter.appendEntries(list, book.getTableOfContents());
		return HelpSiteExporter.pageHtml(book.getTitle(), "", "<h1>" + HelpSiteExporter.escape(book.getTitle()) + "</h1>\n" + list)
				.replace("<nav><a href=\"index.html\">VCell Help contents</a></nav>\n", "");
	}

	private static void appendEntries(StringBuilder html, List<HelpTocEntry> entries) {
		if (entries.isEmpty()) return;
		html.append("<ul>\n");
		for (HelpTocEntry entry : entries) {
			html.append("<li>");
			if (entry.pagePath() == null) {
				html.append(HelpSiteExporter.escape(entry.title()));
			} else {
				html.append("<a href=\"").append(HelpRenderer.toHtmlHref(entry.pagePath())).append("\">")
						.append(HelpSiteExporter.escape(entry.title())).append("</a>");
			}
			HelpSiteExporter.appendEntries(html, entry.children());
			html.append("</li>\n");
		}
		html.append("</ul>\n");
	}

	/**
	 * Copy the images the pages show, read through the book's source, so the site holds only
	 * plain files from inside the help folder (no links, nothing unused).
	 */
	private static int copyImages(HelpBook book, Path outputDirectory) throws IOException {
		Set<String> imagePaths = new TreeSet<>();
		for (HelpPage page : book.getPages()) {
			for (Node node : page.document().getDescendants()) {
				String src = null;
				if (node instanceof Image image) {
					src = image.getUrl().unescape();
				} else if (node instanceof ImageRef imageRef && imageRef.isDefined()) {
					Reference reference = imageRef.getReferenceNode(Parser.REFERENCES.get(imageRef.getDocument()));
					if (reference != null) src = reference.getUrl().unescape();
				}
				if (src == null) continue;
				String imagePath = HelpBook.resolvePath(page.path(), src);
				if (imagePath != null && book.getImageSize(imagePath) != null) imagePaths.add(imagePath);
			}
		}
		for (String imagePath : imagePaths) {
			Path target = outputDirectory.resolve(imagePath);
			Files.createDirectories(target.getParent());
			Files.write(target, book.getSource().read(imagePath, HelpSource.MAX_IMAGE_BYTES));
		}
		return imagePaths.size();
	}

	private static String escape(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
