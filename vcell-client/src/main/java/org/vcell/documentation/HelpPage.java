package org.vcell.documentation;

import com.vladsch.flexmark.util.ast.Document;

/**
 * One page of VCell help, parsed from a Markdown file.
 *
 * @param id       the file name without ".md"; unique across the book, and what code passes to
 *                 {@link HelpViewer#showHelp(String)} (see {@link HelpIds})
 * @param path     location relative to the help root, e.g. "pages/Introduction/New.md"
 * @param title    text of the page's first level-1 heading
 * @param document the parsed Markdown
 */
public record HelpPage(String id, String path, String title, Document document) {
	@Override
	public String toString() {
		return this.title;
	}
}
