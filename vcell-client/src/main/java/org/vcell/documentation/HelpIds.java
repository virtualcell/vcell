package org.vcell.documentation;

/**
 * Help pages that code opens directly with {@link HelpViewer#showHelp(String)}.
 *
 * <p>A page's id is its Markdown file name without ".md" (in vcell-client/UserDocumentation/pages).
 * HelpBookTest fails if a constant here names a page that does not exist, so renaming a page
 * cannot silently break a Help button.
 */
public final class HelpIds {
	/** First page of the contents; what Help &gt; VCell Help opens. */
	public static final String HOME = "GeneralOverview";
	public static final String ANALYTIC_GEOMETRY = "geom_Analytic";
	public static final String VIRTUAL_FRAP = "VFrapIntroduction";

	private HelpIds() {
	}
}
