package org.vcell.documentation;

import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.HtmlBlock;
import com.vladsch.flexmark.ast.HtmlInline;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.ImageRef;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.LinkRef;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Everything that can be wrong with the help source, as problems an author can fix: broken links,
 * anchors and images, pages nothing links to, odd file names, links (symlinks) in the folder,
 * raw HTML, Obsidian-only syntax, and {@link HelpIds} constants with no page. Images no page shows,
 * and image files over {@link #LARGE_IMAGE_BYTES}, are warnings ({@link HelpProblem#isWarning()}):
 * they ship in VCell without changing what the help shows.
 *
 * <p>HelpBookTest runs these on vcell-client/UserDocumentation, and the help window runs them on a
 * folder an author opens, so both report the same things.
 */
public final class HelpChecks {
	/**
	 * Obsidian syntax that standard Markdown, and so the VCell help viewer, shows as literal text.
	 * Escaped characters (\[\[) and inline code are left alone.
	 */
	private record ObsidianSyntax(Pattern pattern, String instead) {}
	private static final List<ObsidianSyntax> OBSIDIAN_ONLY_SYNTAX = List.of(
			new ObsidianSyntax(Pattern.compile("(?<!\\\\)!?\\[\\["), "a [[wikilink]] or ![[embed]]: use [text](page.md) or ![](../../images/name.png)"),
			new ObsidianSyntax(Pattern.compile("(?<![\\\\=])==(?=\\S)[^=\\n]*\\S=="), "an ==highlight==: use **bold**"),
			new ObsidianSyntax(Pattern.compile("(?<!\\\\)%%"), "a %%comment%%: use <!-- comment -->"),
			new ObsidianSyntax(Pattern.compile("(?m)\\s\\^[A-Za-z0-9-]+$"), "a ^block-reference: link to a heading instead"));

	/** An image file bigger than this is a warning: the old help compiler's limit, 500 KB. */
	public static final long LARGE_IMAGE_BYTES = 500_000;

	private HelpChecks() {
	}

	/**
	 * Every problem in the book: errors first (loading problems, then file by file), then
	 * warnings in the same order.
	 */
	public static List<HelpProblem> check(HelpBook book) {
		List<HelpProblem> problems = new ArrayList<>(book.getProblems());
		Set<String> shownImages = new HashSet<>();
		for (HelpPage page : book.getPages()) {
			HelpChecks.checkPage(book, page, shownImages, problems);
		}
		HelpChecks.checkFiles(book, shownImages, problems);
		HelpChecks.checkHelpIds(book, problems);
		List<HelpProblem> ordered = new ArrayList<>(problems.size());
		for (HelpProblem problem : problems) if (!problem.isWarning()) ordered.add(problem);
		for (HelpProblem problem : problems) if (problem.isWarning()) ordered.add(problem);
		return ordered;
	}

	/** Only the errors: what fails HelpBookTest and the web export. */
	public static List<HelpProblem> errors(List<HelpProblem> problems) {
		return problems.stream().filter(problem -> !problem.isWarning()).toList();
	}

	/**
	 * Files under pages/ and images/ that the help would not read, or that nothing links to.
	 *
	 * @param shownImages the images the pages show, as paths from the help root
	 */
	private static void checkFiles(HelpBook book, Set<String> shownImages, List<HelpProblem> problems) {
		List<String> files;
		try {
			files = book.getSource().listFiles();
		} catch (IOException e) {
			problems.add(HelpProblem.of(HelpProblem.Kind.UNREADABLE, "", e.getMessage()));
			return;
		}
		Set<String> pagePaths = new HashSet<>();
		for (HelpPage page : book.getPages()) pagePaths.add(page.path());
		for (String path : files) {
			if (!HelpSource.isAllowedPath(path)) {
				problems.add(HelpProblem.of(HelpProblem.Kind.FILE_NAME, path, "rename it using only letters, digits, '_' and '-' (no spaces), with one extension"));
				continue;
			}
			if (book.getSource().locate(path) == null) {
				problems.add(HelpProblem.of(HelpProblem.Kind.NOT_A_PLAIN_FILE, path, "this file is a link (shortcut) or not a plain file; the help only reads plain files"));
				continue;
			}
			if (path.startsWith("pages/") && path.endsWith(".md") && !pagePaths.contains(path)) {
				problems.add(HelpProblem.of(HelpProblem.Kind.UNLINKED_PAGE, path, "nothing links to this page; add it to toc.md or link it from a page"));
				continue;
			}
			if (path.startsWith("images/") && !shownImages.contains(path)) {
				problems.add(HelpProblem.of(HelpProblem.Kind.UNUSED_IMAGE, path, "no page shows this image, but it still ships in VCell; delete it if it is no longer needed"));
			}
		}
	}

	private static void checkPage(HelpBook book, HelpPage page, Set<String> shownImages, List<HelpProblem> problems) {
		boolean hasTitle = false;
		Set<Integer> htmlLines = new HashSet<>();
		for (Node node : page.document().getDescendants()) {
			int line = node.getLineNumber() + 1;
			if (node instanceof Heading heading && heading.getLevel() == 1) hasTitle = true;
			if (node instanceof HtmlBlock || node instanceof HtmlInline) {
				if (htmlLines.add(line)) problems.add(new HelpProblem(HelpProblem.Kind.HTML, page.path(), line, "HTML is not shown in the help; use Markdown instead"));
			} else if (node instanceof Image image) {
				HelpChecks.checkImage(book, page, line, image.getUrl().unescape(), shownImages, problems);
			} else if (node instanceof ImageRef imageRef) {
				Reference reference = HelpChecks.reference(imageRef.isDefined(), imageRef);
				if (reference != null) HelpChecks.checkImage(book, page, line, reference.getUrl().unescape(), shownImages, problems);
			} else if (node instanceof Link link) {
				HelpChecks.checkLink(book, page, line, link.getUrl().unescape(), problems);
			} else if (node instanceof LinkRef linkRef) {
				Reference reference = HelpChecks.reference(linkRef.isDefined(), linkRef);
				if (reference != null) HelpChecks.checkLink(book, page, line, reference.getUrl().unescape(), problems);
			} else if (node instanceof AutoLink autoLink) {
				String text = autoLink.getText().toString();
				HelpChecks.checkLink(book, page, line, text.startsWith("www.") ? "https://" + text : text, problems);
			}
			if (node instanceof Paragraph || node instanceof Heading) {
				HelpChecks.checkObsidianSyntax(page, node, problems);
			}
		}
		if (!hasTitle) problems.add(new HelpProblem(HelpProblem.Kind.NO_TITLE, page.path(), 1, "this page has no title; start it with '# Title'"));
	}

	private static Reference reference(boolean defined, Node refNode) {
		if (!defined) return null;
		if (refNode instanceof LinkRef linkRef) return linkRef.getReferenceNode(Parser.REFERENCES.get(refNode.getDocument()));
		if (refNode instanceof ImageRef imageRef) return imageRef.getReferenceNode(Parser.REFERENCES.get(refNode.getDocument()));
		return null;
	}

	/**
	 * An image a page shows. A large one is reported once, where it is first shown, so choosing
	 * the warning opens a page that shows it.
	 */
	private static void checkImage(HelpBook book, HelpPage page, int line, String src, Set<String> shownImages, List<HelpProblem> problems) {
		if (HelpBook.isExternal(src)) {
			problems.add(new HelpProblem(HelpProblem.Kind.IMAGE, page.path(), line, "image " + HelpChecks.quoted(src) + " must be a file in the images folder"));
			return;
		}
		String imagePath = HelpBook.resolvePath(page.path(), src);
		if (imagePath == null) {
			problems.add(new HelpProblem(HelpProblem.Kind.IMAGE, page.path(), line, "image " + HelpChecks.quoted(src) + " is outside the help folder"));
			return;
		}
		boolean firstTimeShown = shownImages.add(imagePath);
		String imageProblem = book.imageProblem(imagePath);
		if (imageProblem != null) {
			problems.add(new HelpProblem(HelpProblem.Kind.IMAGE, page.path(), line, "image " + HelpChecks.quoted(src) + " " + imageProblem));
			return;
		}
		if (!firstTimeShown) return;
		long bytes = book.getSource().size(imagePath);
		if (bytes > LARGE_IMAGE_BYTES) {
			problems.add(new HelpProblem(HelpProblem.Kind.LARGE_IMAGE, page.path(), line, "image " + HelpChecks.quoted(src) + " is "
					+ HelpChecks.kilobytes(bytes) + ", and every image ships in VCell; keep images under " + HelpChecks.kilobytes(LARGE_IMAGE_BYTES)
					+ " (crop it, make it smaller, or save it with fewer colours)"));
		}
	}

	/** "1,433 KB" */
	private static String kilobytes(long bytes) {
		return String.format("%,d KB", (bytes + 500) / 1000);
	}

	private static void checkLink(HelpBook book, HelpPage page, int line, String href, List<HelpProblem> problems) {
		if (HelpBook.isExternal(href)) {
			if (!HelpBook.isAllowedExternalLink(href)) {
				problems.add(new HelpProblem(HelpProblem.Kind.LINK_NOT_ALLOWED, page.path(), line, "link " + HelpChecks.quoted(href) + " is not shown; links outside the help must start with https://, http:// or mailto:"));
			}
			return;
		}
		String targetPath = HelpBook.resolvePath(page.path(), href);
		if (targetPath == null || book.getSource().locate(targetPath) == null) {
			problems.add(new HelpProblem(HelpProblem.Kind.BROKEN_LINK, page.path(), line, "broken link " + HelpChecks.quoted(href) + " (names are case-sensitive)"));
			return;
		}
		String fragment = HelpBook.fragmentOf(href);
		if (fragment == null || !targetPath.endsWith(".md")) return;
		HelpPage targetPage = book.getPageByPath(targetPath);
		if (targetPage == null || book.resolveHeadingId(targetPage, fragment) == null) {
			problems.add(new HelpProblem(HelpProblem.Kind.MISSING_HEADING, page.path(), line, "link " + HelpChecks.quoted(href) + " names no heading in " + targetPath));
		}
	}

	private static void checkObsidianSyntax(HelpPage page, Node node, List<HelpProblem> problems) {
		int line = node.getLineNumber() + 1;
		String text = HelpChecks.withoutInlineCode(node);
		if (node.getParent() instanceof BlockQuote quote && quote.getFirstChild() == node && text.startsWith("[!")) {
			problems.add(new HelpProblem(HelpProblem.Kind.OBSIDIAN_SYNTAX, page.path(), line, "a > [!callout]: use a plain > quote or **Note:**"));
		}
		for (ObsidianSyntax syntax : OBSIDIAN_ONLY_SYNTAX) {
			if (syntax.pattern().matcher(text).find()) problems.add(new HelpProblem(HelpProblem.Kind.OBSIDIAN_SYNTAX, page.path(), line, syntax.instead()));
		}
	}

	/**
	 * The block's source with inline code blanked out, so `[[example]]` in code is fine. Uses
	 * the parsed code spans: a regex for them backtracks for minutes on a long run of backticks.
	 */
	private static String withoutInlineCode(Node block) {
		StringBuilder text = new StringBuilder(block.getChars());
		int start = block.getStartOffset();
		for (Node node : block.getDescendants()) {
			if (!(node instanceof Code)) continue;
			for (int offset = node.getStartOffset(); offset < node.getEndOffset(); offset++) {
				int index = offset - start;
				if (index >= 0 && index < text.length() && text.charAt(index) != '\n') text.setCharAt(index, ' ');
			}
		}
		return text.toString();
	}

	/** An href or src as quoted in a message: long ones are cut short. */
	private static String quoted(String text) {
		return "'" + (text.length() <= 120 ? text : text.substring(0, 117) + "...") + "'";
	}

	/** VCell opens these pages by id; the help must keep them. */
	private static void checkHelpIds(HelpBook book, List<HelpProblem> problems) {
		for (Field field : HelpIds.class.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()) || field.getType() != String.class) continue;
			String pageId;
			try {
				pageId = (String) field.get(null);
			} catch (IllegalAccessException e) {
				continue;
			}
			if (book.findPage(pageId) != null) continue;
			problems.add(HelpProblem.of(HelpProblem.Kind.MISSING_PAGE_FOR_VCELL, "", "VCell opens a page named " + pageId + " (HelpIds." + field.getName() + "), and the help has none"));
		}
		if (!book.getHomePage().id().equals(HelpIds.HOME)) {
			problems.add(HelpProblem.of(HelpProblem.Kind.MISSING_PAGE_FOR_VCELL, HelpBook.TOC_PATH, "the first page in the contents must be " + HelpIds.HOME + " (the home page)"));
		}
	}
}
