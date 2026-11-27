package org.vcell.documentation;

import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.VCellIcons;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * One tab of the help window: a page, the table of contents, or a problem in the help source,
 * with its own Back/Forward history. {@link HelpViewer} owns what the tabs share (the book, the
 * contents tree, search and the problems pane) and keeps them in step with the selected tab.
 */
final class HelpTab extends JPanel {
	private static final Logger LG = LogManager.getLogger(HelpTab.class);
	private static final String PAGE_CARD = "page";
	private static final String PROBLEM_CARD = "problem";
	private static final int MAX_TITLE_LENGTH = 28;

	/**
	 * A place in a tab's history: a page (and heading), the table of contents (no page), or a
	 * problem's details.
	 *
	 * @param pagePath     the page, relative to the help root; null for the contents or a problem
	 * @param fragment     the heading to scroll to, or null
	 * @param problem      the problem shown, or null for a page or the contents
	 * @param problemScope with a problem: the page whose problems the problems pane lists while it
	 *                     is shown (null for all of them), so choosing one doesn't change the list
	 */
	record Location(String pagePath, String fragment, HelpProblem problem, String problemScope) {
		static Location contents() {
			return new Location(null, null, null, null);
		}

		static Location page(HelpPage page, String fragment) {
			return page == null ? Location.contents() : new Location(page.path(), fragment, null, null);
		}

		static Location problem(HelpProblem problem, String scope) {
			return new Location(null, null, problem, scope);
		}
	}

	private final HelpViewer viewer;
	private final CardLayout cards = new CardLayout();
	final JEditorPane pagePane = new JEditorPane();
	private final JScrollPane pageScrollPane = new JScrollPane(this.pagePane);
	private final ProblemView problemView = new ProblemView();

	private final List<Location> history = new ArrayList<>();
	private int historyIndex = -1;
	private HelpPage currentPage = null; // null for the contents, a problem, or before anything is shown
	private boolean contentsShown = false;
	private List<String> highlightTerms = List.of();

	HelpTab(HelpViewer viewer) {
		this.viewer = viewer;
		this.setLayout(this.cards);
		this.setName("HelpTab");
		this.pagePane.setName("HelpPage");
		this.pagePane.setEditable(false);
		this.pagePane.setEditorKit(new HelpViewer.SafeHtmlEditorKit());
		this.pagePane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		this.pagePane.getAccessibleContext().setAccessibleName("Help page");
		this.pagePane.addHyperlinkListener(e -> {
			if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) return;
			// Cmd-click (Ctrl-click elsewhere) opens the link in a new tab, as in a browser
			InputEvent input = e.getInputEvent();
			boolean newTab = input != null && (input.getModifiersEx() & HelpViewer.menuMask()) != 0;
			this.viewer.followLink(this, e.getDescription(), newTab);
		});
		this.pageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.add(this.pageScrollPane, PAGE_CARD);
		this.add(this.problemView, PROBLEM_CARD);
	}

	// ---------------------------------------------------------------------------- navigation

	/** Show a place as a new history entry (unless it is already the current one). */
	void open(Location location, List<String> terms) {
		boolean sameAsCurrent = this.historyIndex >= 0 && this.history.get(this.historyIndex).equals(location);
		if (!sameAsCurrent) {
			while (this.history.size() > this.historyIndex + 1) this.history.remove(this.history.size() - 1);
			this.history.add(location);
			this.historyIndex = this.history.size() - 1;
		}
		this.highlightTerms = terms;
		this.display(this.history.get(this.historyIndex), false);
	}

	void goBack() {
		if (!this.canGoBack()) return;
		this.historyIndex--;
		this.highlightTerms = List.of();
		this.display(this.history.get(this.historyIndex), false);
	}

	void goForward() {
		if (!this.canGoForward()) return;
		this.historyIndex++;
		this.highlightTerms = List.of();
		this.display(this.history.get(this.historyIndex), false);
	}

	boolean canGoBack() {
		return this.historyIndex > 0;
	}

	boolean canGoForward() {
		return this.historyIndex < this.history.size() - 1;
	}

	/** Where the tab is now; the contents before anything is shown. */
	Location place() {
		return this.historyIndex < 0 ? Location.contents() : this.history.get(this.historyIndex);
	}

	/** The page shown, or null for the contents or a problem. */
	HelpPage currentPage() {
		return this.currentPage;
	}

	boolean showsContents() {
		return this.contentsShown;
	}

	/** The problem shown, or null. */
	HelpProblem problem() {
		return this.place().problem();
	}

	/** The words of the search that opened the page, highlighted in it; none once the reader moves on. */
	void clearHighlights() {
		if (this.highlightTerms.isEmpty()) return;
		this.highlightTerms = List.of();
		this.highlightTerms();
	}

	/**
	 * Show the place again after the book changed (a folder reloaded, or the help switched):
	 * the same page if the new book has it, else the contents.
	 *
	 * @param keepScroll stay where the reader was, for a reload of the same folder
	 */
	void reload(boolean keepScroll) {
		int scrollPosition = this.pageScrollPane.getVerticalScrollBar().getValue();
		this.currentPage = null;
		this.contentsShown = false;
		this.display(this.place(), true);
		if (keepScroll && this.problem() == null) {
			SwingUtilities.invokeLater(() -> SwingUtilities.invokeLater(() -> this.pageScrollPane.getVerticalScrollBar().setValue(scrollPosition)));
		}
	}

	/** Render the page again, for a new look and feel (light/dark theme). */
	void rerender() {
		if (this.problem() != null) return;
		if (this.contentsShown) {
			this.loadContents();
			return;
		}
		if (this.currentPage == null) return;
		this.loadDocument(this.currentPage);
		this.highlightTerms();
	}

	/** Whether the problem shown is still among the folder's, after a reload. */
	void problemsChanged(List<HelpProblem> problems) {
		HelpProblem problem = this.problem();
		if (problem != null) this.problemView.show(problem, problems.contains(problem));
	}

	private void display(Location location, boolean reloading) {
		if (location.problem() != null) {
			this.currentPage = null;
			this.contentsShown = false;
			this.problemView.show(location.problem(), this.viewer.isReported(location.problem()));
			this.cards.show(this, PROBLEM_CARD);
			this.viewer.tabShown(this);
			return;
		}
		// a page that has gone (the folder changed) shows the contents
		HelpPage page = location.pagePath() == null ? null : this.viewer.book().getPageByPath(location.pagePath());
		if (page == null && !this.contentsShown) {
			this.currentPage = null;
			this.contentsShown = true;
			this.loadContents();
		} else if (page != null && (page != this.currentPage || this.contentsShown)) {
			this.currentPage = page;
			this.contentsShown = false;
			this.loadDocument(page);
		}
		this.cards.show(this, PAGE_CARD);
		this.highlightTerms();
		this.viewer.tabShown(this);
		if (reloading) return;
		String fragment = location.fragment();
		SwingUtilities.invokeLater(() -> this.scrollTo(fragment));
	}

	// -------------------------------------------------------------------------- tab heading

	/** The tab's title: the page's, "Contents", or the sort of problem; cut short if long. */
	String title() {
		String full = this.fullTitle();
		return full.length() <= MAX_TITLE_LENGTH ? full : full.substring(0, MAX_TITLE_LENGTH - 1) + "…";
	}

	String fullTitle() {
		HelpProblem problem = this.problem();
		if (problem != null) return problem.kind().label();
		if (this.currentPage != null) return this.currentPage.title();
		return "Contents";
	}

	/** VCell's issue icon for a problem; none for a page. */
	Icon icon() {
		HelpProblem problem = this.problem();
		if (problem == null) return null;
		return problem.isWarning() ? VCellIcons.issueWarningIcon : VCellIcons.issueErrorIcon;
	}

	// ----------------------------------------------------------------------------- rendering

	private void loadContents() {
		try {
			this.pagePane.setDocument(this.viewer.newDocument(this.pagePane, HelpBook.TOC_PATH, this.viewer.renderer().renderContents(), new java.util.Hashtable<>()));
			this.pagePane.getAccessibleContext().setAccessibleDescription("Help contents");
		} catch (IOException | BadLocationException e) {
			LG.error("help contents could not be shown", e);
			this.pagePane.setText("<html><body><p>The help contents could not be shown.</p></body></html>");
		}
	}

	private void loadDocument(HelpPage page) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			HelpRenderer.RenderedPage rendered = this.viewer.renderer().renderPage(page);
			this.pagePane.setDocument(this.viewer.newDocument(this.pagePane, page.path(), rendered.html(), this.viewer.decodeImages(rendered)));
			this.pagePane.getAccessibleContext().setAccessibleDescription(page.title());
		} catch (IOException | BadLocationException e) {
			LG.error("help page " + page.path() + " could not be shown", e);
			this.pagePane.setText("<html><body><p>This help page could not be shown.</p></body></html>");
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void highlightTerms() {
		Highlighter highlighter = this.pagePane.getHighlighter();
		highlighter.removeAllHighlights();
		if (this.highlightTerms.isEmpty()) return;
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(VCellColors.getMatchHighlightBackground());
		try {
			String text = this.pagePane.getDocument().getText(0, this.pagePane.getDocument().getLength()).toLowerCase(Locale.ROOT);
			for (String term : this.highlightTerms) {
				for (int index = text.indexOf(term); index >= 0; index = text.indexOf(term, index + term.length())) {
					highlighter.addHighlight(index, index + term.length(), painter);
				}
			}
		} catch (BadLocationException e) {
			LG.warn("could not highlight search terms", e);
		}
	}

	private void scrollTo(String fragment) {
		if (fragment != null) {
			this.pagePane.scrollToReference(fragment);
			return;
		}
		Highlighter.Highlight[] highlights = this.pagePane.getHighlighter().getHighlights();
		int offset = highlights.length > 0 ? highlights[0].getStartOffset() : 0;
		try {
			// null until the pane is laid out (help opened before its window is shown): stay at the top
			java.awt.geom.Rectangle2D view = this.pagePane.modelToView2D(offset);
			if (view == null) {
				this.pagePane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
				return;
			}
			Rectangle area = view.getBounds();
			area.y = Math.max(0, area.y - 40); // a little context above a search match
			area.height = this.pageScrollPane.getViewport().getHeight();
			this.pagePane.scrollRectToVisible(offset == 0 ? new Rectangle(0, 0, 1, 1) : area);
		} catch (BadLocationException e) {
			this.pagePane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
		}
	}

	// ------------------------------------------------------------------------- problem view

	/**
	 * Show the problem's page, at the heading above its line (Swing can't scroll to a source
	 * line); a contents problem shows the contents. Does nothing for a problem on no page.
	 */
	void goToProblem() {
		Location target = this.problemTarget(this.problem());
		if (target != null) this.open(target, List.of());
	}

	/** Where "Go to" takes a problem, or null when it is on no page (an unused image, say). */
	Location problemTarget(HelpProblem problem) {
		if (problem == null) return null;
		if (problem.path().equals(HelpBook.TOC_PATH)) return Location.contents();
		HelpPage page = this.viewer.book().getPageByPath(problem.path());
		if (page == null) return null;
		String fragment = null;
		for (Node node : page.document().getDescendants()) {
			if (!(node instanceof Heading heading) || heading.getLineNumber() + 1 > problem.line()) continue;
			String id = this.viewer.book().resolveHeadingId(page, heading.getText().toString());
			if (id != null) fragment = id;
		}
		return Location.page(page, fragment);
	}

	/**
	 * A problem in full: its sort, whether it is an error or a warning, where it is, the whole
	 * message, and a button to go to it. Everything is plain text: the message can quote what the
	 * help source contains, so none of it may reach Swing as HTML.
	 */
	private final class ProblemView extends JPanel {
		private final JLabel heading = HelpTab.plainLabel();
		private final JLabel severity = HelpTab.plainLabel();
		private final JTextArea where = HelpTab.textArea("Where the problem is");
		private final JTextArea message = HelpTab.textArea("The problem");
		private final JLabel status = HelpTab.plainLabel();
		private final JButton goToButton = new JButton("Go to Problem");

		private ProblemView() {
			super(new BorderLayout());
			this.setName("HelpProblemView");
			this.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
			VCellFonts.setCategory(this.heading, VCellFonts.Category.TITLE);
			this.heading.setIconTextGap(8);
			this.goToButton.setName("HelpGoToProblem");
			this.goToButton.addActionListener(e -> HelpTab.this.goToProblem());
			this.status.setForeground(VCellColors.getWarningForeground());

			JPanel column = new JPanel();
			column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
			for (Component part : List.of(this.heading, Box.createVerticalStrut(4), this.severity, Box.createVerticalStrut(12),
					HelpTab.caption("Where"), this.where, Box.createVerticalStrut(10), HelpTab.caption("Problem"), this.message,
					Box.createVerticalStrut(10), this.status, Box.createVerticalStrut(12), this.goToButton)) {
				((javax.swing.JComponent) part).setAlignmentX(Component.LEFT_ALIGNMENT);
				column.add(part);
			}
			this.add(column, BorderLayout.NORTH);
		}

		private void show(HelpProblem problem, boolean stillReported) {
			this.heading.setIcon(problem.isWarning() ? VCellIcons.issueWarningIcon : VCellIcons.issueErrorIcon);
			this.heading.setText(problem.kind().label());
			this.severity.setText(problem.isWarning() ? "Warning: worth tidying; it fails nothing."
					: "Error: the help shows this wrongly, and it fails the help's checks.");
			this.where.setText(problem.location());
			String text = problem.message();
			this.message.setText(text.isEmpty() ? "" : Character.toUpperCase(text.charAt(0)) + text.substring(1));
			this.status.setText(stillReported ? "" : "No longer reported: fixed, or the file changed since.");
			this.status.setVisible(!stillReported);
			boolean canGo = HelpTab.this.problemTarget(problem) != null;
			this.goToButton.setEnabled(canGo);
			this.goToButton.setToolTipText(canGo ? "Open the page, at the section with the problem" : "This problem isn't on a page");
		}
	}

	/** A label that never renders its text as HTML. */
	private static JLabel plainLabel() {
		JLabel label = new JLabel();
		label.putClientProperty("html.disable", Boolean.TRUE);
		return label;
	}

	private static JLabel caption(String text) {
		JLabel label = HelpTab.plainLabel();
		label.setText(text);
		label.setForeground(VCellColors.getDimForeground());
		return label;
	}

	/** Wrapped, selectable plain text, so a long path or message can be read and copied. */
	private static JTextArea textArea(String accessibleName) {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setOpaque(false);
		area.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		area.setFont(UIManager.getFont("Label.font"));
		area.getAccessibleContext().setAccessibleName(accessibleName);
		return area;
	}
}
