package org.vcell.documentation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

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
		if (!book.getProblems().isEmpty()) {
			throw new IOException("help source has problems: " + book.getProblems());
		}
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
		HelpSiteExporter.copyDirectory(sourceDirectory.toPath().resolve("images"), outputDirectory.resolve("images"));
		System.out.println("wrote " + book.getPages().size() + " help pages to " + outputDirectory);
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

	private static void copyDirectory(Path from, Path to) throws IOException {
		try (Stream<Path> files = Files.walk(from)) {
			for (Path source : (Iterable<Path>) files::iterator) {
				Path target = to.resolve(from.relativize(source).toString());
				if (Files.isDirectory(source)) {
					Files.createDirectories(target);
					continue;
				}
				Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	private static String escape(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
