package org.vcell.documentation;

import java.util.List;

/**
 * One line of the help table of contents ({@code toc.md}).
 *
 * @param title    the text shown in the contents tree (may differ from the page's own title)
 * @param pagePath the page it opens, relative to the help root; null for a plain grouping entry
 * @param children nested entries
 */
public record HelpTocEntry(String title, String pagePath, List<HelpTocEntry> children) {
	@Override
	public String toString() {
		return this.title;
	}
}
