package org.vcell.documentation;

/**
 * Something wrong with the help source, as an author would fix it.
 *
 * @param kind    what sort of problem, for a short heading
 * @param path    the file, relative to the help root ("pages/Introduction/New.md"); "" for the
 *                folder as a whole
 * @param line    1-based line in that file, or 0 when the problem is not on one line
 * @param message what is wrong and, where it helps, what to do instead
 */
public record HelpProblem(Kind kind, String path, int line, String message) {
	/**
	 * An error is something the help shows wrongly or not at all; it fails HelpBookTest and the web
	 * export. A warning is worth tidying (the help still shows correctly), so it fails nothing.
	 */
	public enum Severity {
		ERROR,
		WARNING
	}

	/** The sorts of problem, each with the few words the help window shows above it. */
	public enum Kind {
		BROKEN_LINK("Broken link", Severity.ERROR),
		MISSING_HEADING("Link to a missing heading", Severity.ERROR),
		LINK_NOT_ALLOWED("Link not allowed", Severity.ERROR),
		IMAGE("Image can't be shown", Severity.ERROR),
		HTML("HTML not shown", Severity.ERROR),
		OBSIDIAN_SYNTAX("Obsidian-only syntax", Severity.ERROR),
		NO_TITLE("Page has no title", Severity.ERROR),
		UNLINKED_PAGE("Page nothing links to", Severity.ERROR),
		FILE_NAME("File name not allowed", Severity.ERROR),
		NOT_A_PLAIN_FILE("Link or special file", Severity.ERROR),
		DUPLICATE_NAME("Duplicate page name", Severity.ERROR),
		CONTENTS("Contents problem", Severity.ERROR),
		TOO_LARGE("Too large", Severity.ERROR),
		UNREADABLE("Can't be read", Severity.ERROR),
		MISSING_PAGE_FOR_VCELL("Page VCell needs is missing", Severity.ERROR),
		UNUSED_IMAGE("Image no page shows", Severity.WARNING),
		LARGE_IMAGE("Large image file", Severity.WARNING);

		private final String label;
		private final Severity severity;

		Kind(String label, Severity severity) {
			this.label = label;
			this.severity = severity;
		}

		public String label() {
			return this.label;
		}

		public Severity severity() {
			return this.severity;
		}
	}

	public static HelpProblem of(Kind kind, String path, String message) {
		return new HelpProblem(kind, path, 0, message);
	}

	public boolean isWarning() {
		return this.kind.severity() == Severity.WARNING;
	}

	/** "pages/Intro/New.md, line 12", "pages/Intro/New.md", or "Help source folder". */
	public String location() {
		if (this.path.isEmpty()) return "Help source folder";
		return this.line > 0 ? this.path + ", line " + this.line : this.path;
	}

	@Override
	public String toString() {
		String severity = this.isWarning() ? "warning: " : "";
		if (this.path.isEmpty()) return severity + this.message;
		return this.path + (this.line > 0 ? ":" + this.line : "") + ": " + severity + this.message;
	}
}
