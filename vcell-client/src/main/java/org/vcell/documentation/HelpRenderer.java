package org.vcell.documentation;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.html.MutableAttributes;

import java.awt.Dimension;
import java.util.regex.Pattern;

/**
 * Turns a {@link HelpPage} into HTML.
 *
 * <p>Images get explicit width/height (scaled down to {@code maxImageWidth}) so the page lays out
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

	/** The page's body as an HTML fragment. */
	public String renderBody(HelpPage page) {
		MutableDataSet options = new MutableDataSet(HelpBook.markdownOptions())
				.set(HtmlRenderer.GENERATE_HEADER_ID, true)
				.set(HtmlRenderer.RENDER_HEADER_ID, true);
		HtmlRenderer renderer = HtmlRenderer.builder(options)
				.attributeProviderFactory(new IndependentAttributeProviderFactory() {
					@Override
					public AttributeProvider apply(LinkResolverContext context) {
						return (node, part, attributes) -> HelpRenderer.this.setAttributes(page, node, part, attributes);
					}
				})
				.build();
		String html = renderer.render(page.document());
		if (this.target != Target.SWING) return html;
		// JEditorPane.scrollToReference only finds <a name>, and it does not know thead/tbody
		html = HEADING_WITH_ID.matcher(html).replaceAll("<h$1><a name=\"$2\"></a>");
		return TABLE_SECTION_TAG.matcher(html).replaceAll("");
	}

	private void setAttributes(HelpPage page, Node node, AttributablePart part, MutableAttributes attributes) {
		if (node instanceof Image image) {
			this.sizeImage(page, image.getUrl().toString(), attributes);
			return;
		}
		if (node instanceof Link link && this.target == Target.WEB) {
			String href = link.getUrl().toString();
			if (HelpBook.isExternal(href)) return;
			attributes.replaceValue("href", HelpRenderer.toHtmlHref(this.withHeadingId(page, href)));
		}
	}

	/** A browser only follows "#id", so turn an Obsidian-style "#Heading%20Text" into the heading's id. */
	private String withHeadingId(HelpPage page, String href) {
		String fragment = HelpBook.fragmentOf(href);
		if (fragment == null) return href;
		String targetPath = HelpBook.resolvePath(page.path(), href);
		HelpPage targetPage = targetPath == null ? null : this.book.getPageByPath(targetPath);
		String headingId = targetPage == null ? null : this.book.resolveHeadingId(targetPage, fragment);
		if (headingId == null) return href;
		return href.substring(0, href.indexOf('#') + 1) + headingId;
	}

	private void sizeImage(HelpPage page, String src, MutableAttributes attributes) {
		String imagePath = HelpBook.resolvePath(page.path(), src);
		if (imagePath == null) return;
		Dimension size = this.book.getImageSize(imagePath);
		if (size == null || size.width <= 0) return;
		int width = Math.min(size.width, this.maxImageWidth);
		int height = (int) Math.round(size.height * (width / (double) size.width));
		attributes.replaceValue("width", Integer.toString(width));
		attributes.replaceValue("height", Integer.toString(height));
	}

	/** "../a/Page.md#x" becomes "../a/Page.html#x"; other hrefs are unchanged. */
	static String toHtmlHref(String href) {
		int hash = href.indexOf('#');
		String target = hash < 0 ? href : href.substring(0, hash);
		String fragment = hash < 0 ? "" : href.substring(hash);
		if (!target.endsWith(".md")) return href;
		return target.substring(0, target.length() - ".md".length()) + ".html" + fragment;
	}
}
