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
	/** The sorts of problem, each with the few words the help window shows above it. */
	public enum Kind {
		BROKEN_LINK("Broken link"),
		MISSING_HEADING("Link to a missing heading"),
		LINK_NOT_ALLOWED("Link not allowed"),
		IMAGE("Image can't be shown"),
		HTML("HTML not shown"),
		OBSIDIAN_SYNTAX("Obsidian-only syntax"),
		NO_TITLE("Page has no title"),
		UNLINKED_PAGE("Page nothing links to"),
		FILE_NAME("File name not allowed"),
		NOT_A_PLAIN_FILE("Link or special file"),
		DUPLICATE_NAME("Duplicate page name"),
		CONTENTS("Contents problem"),
		TOO_LARGE("Too large"),
		UNREADABLE("Can't be read"),
		MISSING_PAGE_FOR_VCELL("Page VCell needs is missing");

		private final String label;

		Kind(String label) {
			this.label = label;
		}

		public String label() {
			return this.label;
		}
	}

	public static HelpProblem of(Kind kind, String path, String message) {
		return new HelpProblem(kind, path, 0, message);
	}

	/** "pages/Intro/New.md, line 12", "pages/Intro/New.md", or "Help source folder". */
	public String location() {
		if (this.path.isEmpty()) return "Help source folder";
		return this.line > 0 ? this.path + ", line " + this.line : this.path;
	}

	@Override
	public String toString() {
		if (this.path.isEmpty()) return this.message;
		return this.path + (this.line > 0 ? ":" + this.line : "") + ": " + this.message;
	}
}
