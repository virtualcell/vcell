package org.vcell.documentation;

import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.ImageRef;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.LinkRef;
import com.vladsch.flexmark.ast.MailLink;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Dimension;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Turns a {@link HelpPage} into HTML that contains only what the help allows.
 *
 * <p>Help can come from a folder an author picked, so its content is treated as untrusted:
 * <ul>
 *   <li>Raw HTML in the Markdown is dropped, comments included. Swing's HTML support builds Java
 *       objects from {@code <object>} tags, submits {@code <form>}s and loads remote
 *       stylesheets and images, so none of it may reach the page.</li>
 *   <li>Links and images are rendered here, not by flexmark: an image only from a file inside the
 *       help that passes {@link HelpBook#imageProblem}, an external link only for http(s) and
 *       mailto. Anything else shows as its text.</li>
 *   <li>The finished HTML is checked against {@link HelpHtmlPolicy}; a page that fails is
 *       replaced by a short notice, so a gap in the rules above shows nothing unsafe.</li>
 * </ul>
 * Images get explicit width/height (scaled down to {@code maxImageWidth}) so the page lays out
 * before they load. Links to other pages keep their ".md" target for the in-app viewer, which
 * resolves them itself; the web export rewrites them to ".html".
 */
public final class HelpRenderer {
	public enum Target {
		/** Swing's JEditorPane: an HTML 3.2-era renderer that only scrolls to {@code <a name>}. */
		SWING,
		/** Static HTML files for a browser. */
		WEB
	}

	private static final Logger LG = LogManager.getLogger(HelpRenderer.class);
	private static final Pattern HEADING_WITH_ID = Pattern.compile("<h([1-6]) id=\"([^\"]*)\">");
	private static final Pattern TABLE_SECTION_TAG = Pattern.compile("</?(thead|tbody)>");

	private final HelpBook book;
	private final Target target;
	private final int maxImageWidth;

	public HelpRenderer(HelpBook book, Target target, int maxImageWidth) {
		this.book = book;
		this.target = target;
		this.maxImageWidth = maxImageWidth;
	}

	/** An image the page shows: its file in the help and the size it is drawn at. */
	public record ShownImage(String path, int width, int height) {}

	/** A rendered page: its HTML, and each image src in it with the file and size it stands for. */
	public record RenderedPage(String html, Map<String, ShownImage> images) {}

	/** The page's body as an HTML fragment that passes {@link HelpHtmlPolicy}. */
	public String renderBody(HelpPage page) {
		return this.renderPage(page).html();
	}

	/** The page's body, and the images it shows, for a viewer that loads the images itself. */
	public RenderedPage renderPage(HelpPage page) {
		Map<String, ShownImage> images = new LinkedHashMap<>();
		String html;
		try {
			html = this.render(page, images);
		} catch (RuntimeException | StackOverflowError e) {
			LG.warn("help page " + page.path() + " could not be rendered", e);
			return new RenderedPage(HelpRenderer.notice(page, "it could not be rendered"), Map.of());
		}
		String violation = HelpHtmlPolicy.findViolation(html, images::containsKey);
		if (violation != null) {
			LG.warn("help page " + page.path() + " was not shown: " + violation);
			return new RenderedPage(HelpRenderer.notice(page, "it contains something the help does not allow (" + violation + ")"), Map.of());
		}
		return new RenderedPage(html, Map.copyOf(images));
	}

	/**
	 * The table of contents as a page of its own: the book's title, then its entries as nested
	 * lists, each linking to its page (a grouping entry, or one whose page is missing, is plain
	 * text). The in-app viewer resolves the links from the help root; the web copy links to the
	 * ".html" files. Titles come from toc.md, so they are escaped, and the whole is checked against
	 * {@link HelpHtmlPolicy} as a page is.
	 */
	public String renderContents() {
		StringBuilder html = new StringBuilder("<h1>").append(HelpRenderer.escape(this.book.getTitle())).append("</h1>\n");
		this.appendContentsEntries(html, this.book.getTableOfContents());
		String violation = HelpHtmlPolicy.findViolation(html.toString(), source -> false);
		if (violation == null) return html.toString();
		LG.warn("help contents were not shown: " + violation);
		return "<h1>Contents</h1>\n<p>The contents can't be shown: they contain something the help does not allow ("
				+ HelpRenderer.escape(violation) + ").</p>\n";
	}

	private void appendContentsEntries(StringBuilder html, List<HelpTocEntry> entries) {
		if (entries.isEmpty()) return;
		html.append("<ul>\n");
		for (HelpTocEntry entry : entries) {
			html.append("<li>");
			String title = HelpRenderer.escape(entry.title());
			if (entry.pagePath() == null || this.book.getPageByPath(entry.pagePath()) == null) {
				html.append(title);
			} else {
				String href = this.target == Target.WEB ? HelpRenderer.toHtmlHref(entry.pagePath()) : entry.pagePath();
				html.append("<a href=\"").append(HelpRenderer.escape(href)).append("\">").append(title).append("</a>");
			}
			this.appendContentsEntries(html, entry.children());
			html.append("</li>\n");
		}
		html.append("</ul>\n");
	}

	private String render(HelpPage page, Map<String, ShownImage> images) {
		MutableDataSet options = new MutableDataSet(HelpBook.markdownOptions())
				.set(HtmlRenderer.GENERATE_HEADER_ID, true)
				.set(HtmlRenderer.RENDER_HEADER_ID, true)
				.set(HtmlRenderer.SUPPRESS_HTML, true)
				.set(HtmlRenderer.SUPPRESS_HTML_BLOCKS, true)
				.set(HtmlRenderer.SUPPRESS_HTML_COMMENT_BLOCKS, true)
				.set(HtmlRenderer.SUPPRESS_INLINE_HTML, true)
				.set(HtmlRenderer.SUPPRESS_INLINE_HTML_COMMENTS, true);
		HtmlRenderer renderer = HtmlRenderer.builder(options)
				.nodeRendererFactory(dataHolder -> new LinkAndImageRenderer(page, images))
				.build();
		String html = renderer.render(page.document());
		if (this.target != Target.SWING) return html;
		// JEditorPane.scrollToReference only finds <a name>, and it does not know thead/tbody
		html = HEADING_WITH_ID.matcher(html).replaceAll("<h$1><a name=\"$2\"></a>");
		return TABLE_SECTION_TAG.matcher(html).replaceAll("");
	}

	private static String notice(HelpPage page, String reason) {
		return "<h1>" + HelpRenderer.escape(page.title()) + "</h1>\n<p>This help page can't be shown: "
				+ HelpRenderer.escape(reason) + ".</p>\n";
	}

	/** Renders every kind of link and image, so none reaches the page unchecked. */
	private final class LinkAndImageRenderer implements NodeRenderer {
		private final HelpPage page;
		private final Map<String, ShownImage> images;

		private LinkAndImageRenderer(HelpPage page, Map<String, ShownImage> images) {
			this.page = page;
			this.images = images;
		}

		@Override
		public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
			return Set.of(
					new NodeRenderingHandler<>(Link.class, (node, context, html) ->
							this.link(node.getUrl().unescape(), node.getTitle().isNull() ? null : node.getTitle().unescape(), node, context, html)),
					new NodeRenderingHandler<>(LinkRef.class, (node, context, html) -> {
						Reference reference = node.isDefined() ? node.getReferenceNode(Parser.REFERENCES.get(node.getDocument())) : null;
						if (reference == null) {
							html.text(node.getChars().unescape());
							return;
						}
						this.link(reference.getUrl().unescape(), reference.getTitle().isNull() ? null : reference.getTitle().unescape(), node, context, html);
					}),
					new NodeRenderingHandler<>(AutoLink.class, (node, context, html) -> {
						String text = node.getText().toString();
						String href = text.startsWith("www.") ? "https://" + text : text;
						this.link(href, null, null, context, html, text);
					}),
					new NodeRenderingHandler<>(MailLink.class, (node, context, html) -> {
						String address = node.getText().unescape();
						this.link("mailto:" + address, null, null, context, html, address);
					}),
					new NodeRenderingHandler<>(Image.class, (node, context, html) ->
							this.image(node.getUrl().unescape(), node, html)),
					new NodeRenderingHandler<>(ImageRef.class, (node, context, html) -> {
						Reference reference = node.isDefined() ? node.getReferenceNode(Parser.REFERENCES.get(node.getDocument())) : null;
						if (reference == null) {
							html.text(node.getChars().unescape());
							return;
						}
						this.image(reference.getUrl().unescape(), node, html);
					}));
		}

		private void link(String href, String title, Node children, NodeRendererContext context, HtmlWriter html) {
			this.link(href, title, children, context, html, null);
		}

		/** An {@code <a>} for an allowed target, otherwise just the link's text. */
		private void link(String href, String title, Node children, NodeRendererContext context, HtmlWriter html, String plainText) {
			String allowedHref = HelpRenderer.this.allowedHref(this.page, href);
			if (allowedHref != null) {
				html.attr("href", allowedHref);
				if (title != null && !title.isBlank()) html.attr("title", title);
				html.withAttr().tag("a");
			}
			if (children != null) {
				context.renderChildren(children);
			} else {
				html.text(plainText);
			}
			if (allowedHref != null) html.tag("/a");
		}

		/** An {@code <img>} for an image file in the help that may be shown, otherwise its alt text. */
		private void image(String src, Node node, HtmlWriter html) {
			String altText = HelpBook.textOf(node);
			String imagePath = HelpBook.resolvePath(this.page.path(), src);
			Dimension size = imagePath == null ? null : HelpRenderer.this.book.getImageSize(imagePath);
			String imageSource = size == null ? null : HelpRenderer.this.imageSource(this.page, imagePath);
			if (imageSource == null) {
				if (!altText.isEmpty()) html.text("[" + altText + "]");
				return;
			}
			int width = Math.min(size.width, HelpRenderer.this.maxImageWidth);
			int height = Math.max(1, (int) Math.round(size.height * (width / (double) size.width)));
			this.images.putIfAbsent(imageSource, new ShownImage(imagePath, width, height));
			html.attr("src", imageSource)
					.attr("alt", altText)
					.attr("width", Integer.toString(width))
					.attr("height", Integer.toString(height))
					.withAttr()
					.tagVoid("img");
		}
	}

	/**
	 * The href to write for a link, or null to show its text only: an allowed external address
	 * as is, a link to a page or heading in the help as ".md" (Swing) or ".html" (web).
	 */
	private String allowedHref(HelpPage page, String href) {
		if (href == null || href.isEmpty()) return null;
		if (HelpBook.isExternal(href)) return HelpBook.isAllowedExternalLink(href) ? href : null;
		String targetPath = HelpBook.resolvePath(page.path(), href);
		if (targetPath == null || !HelpSource.isAllowedPath(targetPath)) return null;
		HelpPage targetPage = this.book.getPageByPath(targetPath);
		if (targetPage == null && this.book.getResourceUrl(targetPath) == null) return null; // broken: show the text
		String relative = HelpRenderer.relativePath(page.path(), targetPath);
		String fragment = HelpBook.fragmentOf(href);
		if (fragment != null && targetPage != null) {
			String headingId = this.book.resolveHeadingId(targetPage, fragment);
			if (headingId != null) fragment = headingId;
		}
		String hash = fragment == null ? "" : "#" + fragment;
		if (targetPath.equals(page.path()) && href.startsWith("#")) return hash.isEmpty() ? null : hash;
		if (this.target == Target.WEB) return HelpRenderer.toHtmlHref(relative + hash);
		return relative + hash;
	}

	/** Swing loads images by absolute URL (no document base); the web copy uses a relative path. */
	private String imageSource(HelpPage page, String imagePath) {
		if (this.target == Target.WEB) return HelpRenderer.relativePath(page.path(), imagePath);
		URL url = this.book.getResourceUrl(imagePath);
		return url == null ? null : url.toExternalForm();
	}

	/** Path of {@code toPath} relative to the folder of {@code fromPath}; both relative to the help root. */
	static String relativePath(String fromPath, String toPath) {
		int depth = (int) fromPath.chars().filter(c -> c == '/').count();
		return "../".repeat(depth) + toPath;
	}

	/** "../a/Page.md#x" becomes "../a/Page.html#x"; other hrefs are unchanged. */
	static String toHtmlHref(String href) {
		int hash = href.indexOf('#');
		String target = hash < 0 ? href : href.substring(0, hash);
		String fragment = hash < 0 ? "" : href.substring(hash);
		if (!target.endsWith(".md")) return href;
		return target.substring(0, target.length() - ".md".length()) + ".html" + fragment;
	}

	static String escape(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
}
