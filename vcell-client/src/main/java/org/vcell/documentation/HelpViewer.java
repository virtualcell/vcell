package org.vcell.documentation;

import cbit.vcell.client.laf.VCellColors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.util.OperatingSystemInfo;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.gui.ShortcutsWizard;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * The VCell help window: a contents tree and search on the left, the page on the right.
 *
 * <p>Pages are Markdown (see {@link HelpBook}) rendered on demand. Open one with
 * {@link #showHelp(String)} and a {@link HelpIds} constant.
 */
@SuppressWarnings("serial")
public final class HelpViewer extends JPanel {
	private static final Logger LG = LogManager.getLogger(HelpViewer.class);
	private static final int DEFAULT_WIDTH = 1050;
	private static final int DEFAULT_HEIGHT = 760;
	private static final int NAVIGATION_WIDTH = 290;
	private static final int MAX_IMAGE_WIDTH = 720;
	private static final int SEARCH_DELAY_MILLIS = 250;
	private static final String CONTENTS_CARD = "contents";
	private static final String RESULTS_CARD = "results";
	private static final Color LIGHT_THEME_LINK = new Color(0x1A5FB4);

	/** Reused while open; collectable once closed and unreferenced. */
	private static WeakReference<HelpWindow> openWindow = null;

	private record Location(String pagePath, String fragment) {}

	private final HelpBook book;
	private final HelpRenderer renderer;
	private HelpSearch search; // built on first search

	private final JEditorPane contentPane = new JEditorPane();
	private final JScrollPane contentScrollPane = new JScrollPane(this.contentPane);
	private final JTree contentsTree;
	private final JList<HelpSearch.Hit> resultsList = new JList<>(new DefaultListModel<>());
	private final JLabel resultsLabel = new JLabel();
	private final JTextField searchField = new JTextField();
	private final CardLayout navigationCards = new CardLayout();
	private final JPanel navigationPanel = new JPanel(this.navigationCards);
	private final JButton backButton = new JButton("Back");
	private final JButton forwardButton = new JButton("Forward");
	private final JButton homeButton = new JButton("Home");
	private final Timer searchTimer = new Timer(SEARCH_DELAY_MILLIS, e -> this.runSearch());

	private final List<Location> history = new ArrayList<>();
	private int historyIndex = -1;
	private HelpPage currentPage = null;
	private List<String> highlightTerms = List.of();
	private boolean selectingProgrammatically = false;

	/** The top-level help window: tracked in the Window menu, independent of any document. */
	private static final class HelpWindow extends LWTopFrame {
		private final HelpViewer viewer;

		private HelpWindow(HelpViewer viewer) {
			super();
			this.viewer = viewer;
			this.setTitle("Virtual Cell Help -- VCell " + VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString());
			this.getContentPane().add(viewer);
			this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
			this.pack();
			GeneralGuiUtils.centerOnScreen(this);
		}

		@Override
		public String menuDescription() {
			return this.getTitle();
		}
	}

	// ------------------------------------------------------------------------- opening help

	/** Open the help at its first page. */
	public static void showHelp() {
		HelpViewer.showHelp(HelpIds.HOME);
	}

	/**
	 * Open the help at a page, reusing the help window if it is already open.
	 *
	 * @param pageId a {@link HelpIds} constant (a page file name without ".md"); an unknown id
	 *               opens the first page
	 */
	public static void showHelp(String pageId) {
		HelpWindow window = HelpViewer.openWindow != null ? HelpViewer.openWindow.get() : null;
		if (window == null) {
			HelpBook book;
			try {
				book = HelpBook.fromClasspath();
			} catch (IOException e) {
				LG.error("help could not be loaded", e);
				DialogUtils.showErrorDialog(null, "VCell help could not be loaded: " + e.getMessage(), e);
				return;
			}
			window = new HelpWindow(new HelpViewer(book));
			HelpViewer.openWindow = new WeakReference<>(window);
		}
		window.viewer.showPage(pageId);
		window.setVisible(true);
		window.toFront();
	}

	// ------------------------------------------------------------------------------- layout

	public HelpViewer(HelpBook book) {
		super(new BorderLayout());
		this.book = book;
		this.renderer = new HelpRenderer(book, HelpRenderer.Target.SWING, MAX_IMAGE_WIDTH);
		this.contentsTree = new JTree(HelpViewer.buildTreeModel(book));

		this.add(this.createToolBar(), BorderLayout.NORTH);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.createNavigationPanel(), this.createContentPanel());
		splitPane.setDividerLocation(NAVIGATION_WIDTH);
		splitPane.setResizeWeight(0);
		this.add(splitPane, BorderLayout.CENTER);
		this.installKeyBindings();
		ShortcutsWizard.addCloseWindowShortcut(this);
	}

	private JComponent createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		String menuKey = HelpViewer.isMac() ? "⌘" : "Ctrl+";
		this.backButton.setToolTipText("Back (" + menuKey + "[ or Alt+Left)");
		this.backButton.addActionListener(e -> this.goBack());
		this.forwardButton.setToolTipText("Forward (" + menuKey + "] or Alt+Right)");
		this.forwardButton.addActionListener(e -> this.goForward());
		this.homeButton.setToolTipText("Go to the first page of the help");
		this.homeButton.addActionListener(e -> this.openPage(this.book.getHomePage(), null, List.of()));
		toolBar.add(this.backButton);
		toolBar.add(this.forwardButton);
		toolBar.add(this.homeButton);
		toolBar.addSeparator();

		this.searchField.setName("HelpSearch");
		this.searchField.setColumns(28);
		this.searchField.setMaximumSize(new Dimension(360, this.searchField.getPreferredSize().height));
		this.searchField.putClientProperty("JTextField.placeholderText", "Search help (" + menuKey + "F)");
		this.searchField.putClientProperty("JTextField.showClearButton", true);
		this.searchField.getAccessibleContext().setAccessibleName("Search help");
		this.searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { HelpViewer.this.searchTimer.restart(); }
			@Override public void removeUpdate(DocumentEvent e) { HelpViewer.this.searchTimer.restart(); }
			@Override public void changedUpdate(DocumentEvent e) { HelpViewer.this.searchTimer.restart(); }
		});
		this.searchField.addActionListener(e -> this.openFirstResult());
		this.searchField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "focusResults");
		this.searchField.getActionMap().put("focusResults", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpViewer.this.focusResults();
			}
		});
		this.searchField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSearch");
		this.searchField.getActionMap().put("clearSearch", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpViewer.this.searchField.setText("");
			}
		});
		this.searchTimer.setRepeats(false);
		JLabel searchLabel = new JLabel("Search ");
		searchLabel.setLabelFor(this.searchField);
		searchLabel.setToolTipText("Find pages containing all these words (" + menuKey + "F)");
		toolBar.add(searchLabel);
		toolBar.add(this.searchField);
		return toolBar;
	}

	private JComponent createNavigationPanel() {
		this.contentsTree.setName("HelpContents");
		this.contentsTree.setRootVisible(false);
		this.contentsTree.setShowsRootHandles(true);
		this.contentsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.contentsTree.getAccessibleContext().setAccessibleName("Help contents");
		this.contentsTree.addTreeSelectionListener(e -> this.openSelectedContentsEntry());
		this.navigationPanel.add(new JScrollPane(this.contentsTree), CONTENTS_CARD);

		this.resultsList.setName("HelpSearchResults");
		this.resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.resultsList.getAccessibleContext().setAccessibleName("Search results");
		this.resultsList.setCellRenderer(new SearchResultRenderer());
		this.resultsList.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			this.openSelectedResult();
		});
		this.resultsLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		this.resultsLabel.setLabelFor(this.resultsList);
		JPanel resultsPanel = new JPanel(new BorderLayout());
		resultsPanel.add(this.resultsLabel, BorderLayout.NORTH);
		resultsPanel.add(new JScrollPane(this.resultsList), BorderLayout.CENTER);
		this.navigationPanel.add(resultsPanel, RESULTS_CARD);
		this.navigationPanel.setMinimumSize(new Dimension(160, 0));
		return this.navigationPanel;
	}

	private JComponent createContentPanel() {
		this.contentPane.setName("HelpPage");
		this.contentPane.setEditable(false);
		this.contentPane.setEditorKit(new HTMLEditorKit());
		this.contentPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		this.contentPane.getAccessibleContext().setAccessibleName("Help page");
		this.contentPane.addHyperlinkListener(e -> {
			if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) return;
			this.followLink(e.getDescription());
		});
		this.contentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return this.contentScrollPane;
	}

	private void installKeyBindings() {
		int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
		this.bind("helpBack", this::goBack,
				KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, menuMask),
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK));
		this.bind("helpForward", this::goForward,
				KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, menuMask),
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK));
		this.bind("helpFind", () -> {
			this.searchField.requestFocusInWindow();
			this.searchField.selectAll();
		}, KeyStroke.getKeyStroke(KeyEvent.VK_F, menuMask));
	}

	private void bind(String actionKey, Runnable action, KeyStroke... keyStrokes) {
		for (KeyStroke keyStroke : keyStrokes) {
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
		}
		this.getActionMap().put(actionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.run();
			}
		});
	}

	private static DefaultTreeModel buildTreeModel(HelpBook book) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(book.getTitle());
		HelpViewer.addTreeNodes(root, book.getTableOfContents());
		return new DefaultTreeModel(root);
	}

	private static void addTreeNodes(DefaultMutableTreeNode parent, List<HelpTocEntry> entries) {
		for (HelpTocEntry entry : entries) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry);
			parent.add(node);
			HelpViewer.addTreeNodes(node, entry.children());
		}
	}

	// ---------------------------------------------------------------------------- navigation

	/** Show a page by id, as a new history entry; an unknown id shows the home page. */
	public void showPage(String pageId) {
		HelpPage page = this.book.findPage(pageId);
		if (page == null) {
			LG.warn("no help page with id '" + pageId + "'; showing the first page");
			page = this.book.getHomePage();
		}
		this.openPage(page, null, List.of());
	}

	private void openPage(HelpPage page, String fragment, List<String> terms) {
		Location location = new Location(page.path(), fragment);
		boolean sameAsCurrent = this.historyIndex >= 0 && this.history.get(this.historyIndex).equals(location);
		if (!sameAsCurrent) {
			while (this.history.size() > this.historyIndex + 1) this.history.remove(this.history.size() - 1);
			this.history.add(location);
			this.historyIndex = this.history.size() - 1;
		}
		this.highlightTerms = terms;
		this.display(location);
	}

	private void goBack() {
		if (this.historyIndex <= 0) return;
		this.historyIndex--;
		this.highlightTerms = List.of();
		this.display(this.history.get(this.historyIndex));
	}

	private void goForward() {
		if (this.historyIndex >= this.history.size() - 1) return;
		this.historyIndex++;
		this.highlightTerms = List.of();
		this.display(this.history.get(this.historyIndex));
	}

	private void display(Location location) {
		HelpPage page = this.book.getPageByPath(location.pagePath());
		if (page != this.currentPage) {
			this.currentPage = page;
			this.loadDocument(page);
		}
		this.highlightTerms();
		this.selectInContents(page);
		this.backButton.setEnabled(this.historyIndex > 0);
		this.forwardButton.setEnabled(this.historyIndex < this.history.size() - 1);
		String fragment = location.fragment();
		SwingUtilities.invokeLater(() -> this.scrollTo(fragment));
	}

	private void scrollTo(String fragment) {
		if (fragment != null) {
			this.contentPane.scrollToReference(fragment);
			return;
		}
		Highlighter.Highlight[] highlights = this.contentPane.getHighlighter().getHighlights();
		int offset = highlights.length > 0 ? highlights[0].getStartOffset() : 0;
		try {
			Rectangle area = this.contentPane.modelToView2D(offset).getBounds();
			area.y = Math.max(0, area.y - 40); // a little context above a search match
			area.height = this.contentScrollPane.getViewport().getHeight();
			this.contentPane.scrollRectToVisible(offset == 0 ? new Rectangle(0, 0, 1, 1) : area);
		} catch (BadLocationException e) {
			this.contentPane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
		}
	}

	private void followLink(String href) {
		if (href == null || href.isBlank()) return;
		if (HelpBook.isExternal(href)) {
			DialogUtils.browserLauncher(this, href, "Could not open " + href);
			return;
		}
		String fragment = HelpBook.fragmentOf(href);
		String targetPath = HelpBook.resolvePath(this.currentPage.path(), href);
		HelpPage target = targetPath == null ? null : this.book.getPageByPath(targetPath);
		if (target == null) {
			LG.warn("help link '" + href + "' on " + this.currentPage.path() + " does not resolve");
			return;
		}
		this.openPage(target, fragment, List.of());
	}

	private void openSelectedContentsEntry() {
		if (this.selectingProgrammatically) return;
		TreePath selection = this.contentsTree.getSelectionPath();
		if (selection == null) return;
		Object userObject = ((DefaultMutableTreeNode) selection.getLastPathComponent()).getUserObject();
		if (!(userObject instanceof HelpTocEntry entry) || entry.pagePath() == null) return;
		HelpPage page = this.book.getPageByPath(entry.pagePath());
		if (page == null || page == this.currentPage) return;
		this.openPage(page, null, List.of());
	}

	/** Select the first contents entry for the page (pages may appear more than once). */
	private void selectInContents(HelpPage page) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.contentsTree.getModel().getRoot();
		Enumeration<?> nodes = root.preorderEnumeration();
		while (nodes.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
			if (!(node.getUserObject() instanceof HelpTocEntry entry) || !page.path().equals(entry.pagePath())) continue;
			TreePath path = new TreePath(node.getPath());
			this.selectingProgrammatically = true;
			try {
				this.contentsTree.setSelectionPath(path);
				this.contentsTree.scrollPathToVisible(path);
			} finally {
				this.selectingProgrammatically = false;
			}
			return;
		}
		this.contentsTree.clearSelection();
	}

	// -------------------------------------------------------------------------------- search

	private void runSearch() {
		String query = this.searchField.getText();
		DefaultListModel<HelpSearch.Hit> model = (DefaultListModel<HelpSearch.Hit>) this.resultsList.getModel();
		model.clear();
		if (query.isBlank()) {
			this.navigationCards.show(this.navigationPanel, CONTENTS_CARD);
			if (!this.highlightTerms.isEmpty()) {
				this.highlightTerms = List.of();
				this.highlightTerms();
			}
			return;
		}
		if (this.search == null) this.search = new HelpSearch(this.book);
		List<HelpSearch.Hit> hits = this.search.search(query);
		model.addAll(hits);
		this.resultsLabel.setText(hits.isEmpty() ? "No pages match" : hits.size() + (hits.size() == 1 ? " page" : " pages"));
		this.navigationCards.show(this.navigationPanel, RESULTS_CARD);
	}

	private void openFirstResult() {
		this.searchTimer.stop();
		this.runSearch();
		if (this.resultsList.getModel().getSize() == 0) return;
		this.resultsList.setSelectedIndex(0); // opens it
	}

	private void focusResults() {
		if (this.resultsList.getModel().getSize() == 0) return;
		if (this.resultsList.getSelectedIndex() < 0) this.resultsList.setSelectedIndex(0);
		this.resultsList.requestFocusInWindow();
	}

	private void openSelectedResult() {
		HelpSearch.Hit hit = this.resultsList.getSelectedValue();
		if (hit == null) return;
		this.openPage(hit.page(), null, HelpSearch.terms(this.searchField.getText()));
	}

	/** Two lines per result: the page title, then where it sits in the contents. */
	private final class SearchResultRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (!(value instanceof HelpSearch.Hit hit)) return this;
			List<String> trail = HelpViewer.this.book.getTocTrail(hit.page().path());
			String where = String.join(" › ", trail);
			String whereColor = VCellColors.toHtmlColor(isSelected ? this.getForeground() : VCellColors.getDimForeground());
			this.setText("<html><b>" + HelpViewer.escapeHtml(hit.page().title()) + "</b>"
					+ (where.isEmpty() ? "" : "<br><font color='" + whereColor + "'>" + HelpViewer.escapeHtml(where) + "</font>")
					+ "</html>");
			this.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
			return this;
		}
	}

	// ------------------------------------------------------------------------------ rendering

	private void loadDocument(HelpPage page) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			HTMLEditorKit editorKit = (HTMLEditorKit) this.contentPane.getEditorKit();
			StyleSheet styleSheet = new StyleSheet();
			styleSheet.addStyleSheet(editorKit.getStyleSheet()); // never modify the kit's shared default
			styleSheet.addRule(this.themeCss());
			HTMLDocument document = new HTMLDocument(styleSheet);
			document.setBase(this.book.getResourceUrl(page.path()));
			document.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
			String html = "<html><body>" + this.renderer.renderBody(page) + "</body></html>";
			editorKit.read(new StringReader(html), document, 0);
			this.contentPane.setDocument(document);
			this.contentPane.getAccessibleContext().setAccessibleDescription(page.title());
		} catch (IOException | BadLocationException e) {
			LG.error("help page " + page.path() + " could not be shown", e);
			this.contentPane.setText("<html><body><p>This help page could not be shown: "
					+ HelpViewer.escapeHtml(String.valueOf(e.getMessage())) + "</p></body></html>");
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void highlightTerms() {
		Highlighter highlighter = this.contentPane.getHighlighter();
		highlighter.removeAllHighlights();
		if (this.highlightTerms.isEmpty()) return;
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(VCellColors.getMatchHighlightBackground());
		try {
			String text = this.contentPane.getDocument().getText(0, this.contentPane.getDocument().getLength()).toLowerCase(Locale.ROOT);
			for (String term : this.highlightTerms) {
				for (int index = text.indexOf(term); index >= 0; index = text.indexOf(term, index + term.length())) {
					highlighter.addHighlight(index, index + term.length(), painter);
				}
			}
		} catch (BadLocationException e) {
			LG.warn("could not highlight search terms", e);
		}
	}

	/** Page styling from the current look and feel, so dark themes get a dark page. */
	private String themeCss() {
		Color background = HelpViewer.uiColor("EditorPane.background", Color.WHITE);
		Color foreground = HelpViewer.uiColor("EditorPane.foreground", Color.BLACK);
		Color link = VCellColors.getReadableForeground(LIGHT_THEME_LINK);
		Color rule = VCellColors.blend(background, foreground, 0.25f);
		Color codeBackground = VCellColors.blend(background, foreground, 0.07f);
		Font font = UIManager.getFont("Label.font");
		int size = font != null ? font.getSize() : 13;
		return "body { background-color: " + VCellColors.toHtmlColor(background) + "; color: " + VCellColors.toHtmlColor(foreground)
				+ "; margin: 10px 16px 24px 16px; }"
				+ " p { margin-top: 0px; margin-bottom: 8px; }"
				+ " h1 { font-size: " + (size + 9) + "pt; margin-top: 4px; margin-bottom: 10px; }"
				+ " h2 { font-size: " + (size + 4) + "pt; margin-top: 14px; margin-bottom: 6px; }"
				+ " h3 { font-size: " + (size + 2) + "pt; margin-top: 12px; margin-bottom: 4px; }"
				+ " a { color: " + VCellColors.toHtmlColor(link) + "; }"
				+ " ul, ol { margin-top: 0px; margin-bottom: 8px; margin-left-ltr: 22px; }"
				+ " li { margin-bottom: 3px; }"
				+ " code, pre { font-family: Monospaced; background-color: " + VCellColors.toHtmlColor(codeBackground) + "; }"
				+ " th, td { border: 1px solid " + VCellColors.toHtmlColor(rule) + "; padding: 3px 6px; }"
				+ " strong { font-weight: bold; } em { font-style: italic; }";
	}

	/** Re-render the open page when the look and feel (light/dark theme) changes. */
	@Override
	public void updateUI() {
		super.updateUI();
		if (this.currentPage == null) return;
		HelpPage page = this.currentPage;
		SwingUtilities.invokeLater(() -> {
			this.loadDocument(page);
			this.highlightTerms();
		});
	}

	private static Color uiColor(String key, Color fallback) {
		Color color = UIManager.getColor(key);
		return color != null ? color : fallback;
	}

	private static boolean isMac() {
		return OperatingSystemInfo.getInstance().isMac();
	}

	private static String escapeHtml(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
