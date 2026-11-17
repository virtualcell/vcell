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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
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
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

/**
 * The VCell help window: a contents tree and search on the left, the page on the right.
 *
 * <p>Pages are Markdown (see {@link HelpBook}) rendered on demand. Open one with
 * {@link #showHelp(String)} and a {@link HelpIds} constant.
 *
 * <p><b>Help source folder</b> (Advanced menu): an author can show the help from a folder of
 * help source instead of the built-in copy, to see edits as VCell shows them. The folder is
 * checked when chosen ({@link HelpSource.Folder#open}), reloaded when its files change, and
 * checked with {@link HelpChecks}; a banner names the folder for as long as it is in use, so its
 * pages can't pass for VCell's own. The folder's content is untrusted: it is read and rendered
 * under the same limits as everything else (see {@link HelpSource} and {@link HelpRenderer}).
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
	private static final String CONTENTS_TAB = "Contents";
	private static final String SOURCE_FOLDER_PREFERENCE = "sourceFolder";
	private static final int WATCH_INTERVAL_MILLIS = 1500;
	private static final long LOAD_TIMEOUT_SECONDS = 20;
	/**
	 * Most distinct images decoded for one page, and most pixels in all of them: the decoded images
	 * live in memory while the page is shown. The help's busiest page has 16 images.
	 */
	private static final int MAX_PAGE_IMAGES = 50;
	private static final long MAX_PAGE_IMAGE_PIXELS = 16_000_000L;
	/**
	 * Folder loading and change checks run here, off the event thread. A task that overruns its
	 * timeout is abandoned, not waited for, so it cannot hold up the next one.
	 */
	private static final ExecutorService LOADER = Executors.newCachedThreadPool(runnable -> {
		Thread thread = new Thread(runnable, "help-folder-loader");
		thread.setDaemon(true);
		return thread;
	});
	private static final Color LIGHT_THEME_LINK = new Color(0x1A5FB4);

	/** Reused while open; collectable once closed and unreferenced. */
	private static WeakReference<HelpWindow> openWindow = null;

	private record Location(String pagePath, String fragment) {}

	private HelpBook book;
	private HelpRenderer renderer;
	private HelpSearch search; // built on first search
	private HelpSource.Folder sourceFolder; // null while showing the built-in help
	private String sourceFolderFingerprint;
	private boolean folderTaskRunning = false;
	/** Bumped by every folder load and by going back to the built-in help; a result from an older one is dropped. */
	private int folderGeneration = 0;

	private final JEditorPane contentPane = new JEditorPane();
	private final JScrollPane contentScrollPane = new JScrollPane(this.contentPane);
	private final JTree contentsTree;
	private final JList<HelpSearch.Hit> resultsList = new JList<>(new DefaultListModel<>());
	private final JLabel resultsLabel = new JLabel();
	private final JTextField searchField = new JTextField();
	private final CardLayout navigationCards = new CardLayout();
	private final JPanel navigationPanel = new JPanel(this.navigationCards);
	/** Contents (or search results) and, while a source folder is shown, its Problems. */
	private final javax.swing.JTabbedPane navigationTabs = new javax.swing.JTabbedPane();
	private final JPanel problemsPanel = new JPanel(new BorderLayout());
	private final JButton backButton = new JButton("Back");
	private final JButton forwardButton = new JButton("Forward");
	private final JButton homeButton = new JButton("Home");
	private final JButton advancedButton = new JButton("Advanced");
	private final JMenuItem useBuiltInMenuItem = new JMenuItem("Use the Built-in Help");
	private final Timer searchTimer = new Timer(SEARCH_DELAY_MILLIS, e -> this.runSearch());
	private final Timer watchTimer = new Timer(WATCH_INTERVAL_MILLIS, e -> this.checkFolderForChanges());

	private final JPanel sourceBanner = new JPanel(new BorderLayout(8, 0));
	private final JLabel sourceLabel = new JLabel();
	private final JButton bannerBuiltInButton = new JButton("Use Built-in Help");
	private final JList<HelpProblem> problemsList = new JList<>(new DefaultListModel<>());
	private final JLabel problemsLabel = new JLabel();

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
			window.viewer.restoreSourceFolder();
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

		JPanel top = new JPanel(new BorderLayout());
		top.add(this.createToolBar(), BorderLayout.NORTH);
		top.add(this.createSourceBanner(), BorderLayout.SOUTH);
		this.add(top, BorderLayout.NORTH);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.createNavigationPanel(), this.createContentPanel());
		splitPane.setDividerLocation(NAVIGATION_WIDTH);
		splitPane.setResizeWeight(0);
		this.add(splitPane, BorderLayout.CENTER);
		this.installKeyBindings();
		ShortcutsWizard.addCloseWindowShortcut(this);
		// watch a source folder only while the help is on screen
		this.watchTimer.setRepeats(true);
		this.addHierarchyListener(e -> {
			if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == 0) return;
			if (this.isShowing() && this.sourceFolder != null) {
				this.watchTimer.start();
			} else {
				this.watchTimer.stop();
			}
		});
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
		toolBar.add(javax.swing.Box.createHorizontalGlue());

		JPopupMenu advancedMenu = new JPopupMenu();
		JMenuItem chooseFolderItem = new JMenuItem("Show Help from a Source Folder...");
		chooseFolderItem.addActionListener(e -> this.chooseSourceFolder());
		this.useBuiltInMenuItem.addActionListener(e -> this.useBuiltInHelp());
		this.useBuiltInMenuItem.setEnabled(false);
		advancedMenu.add(chooseFolderItem);
		advancedMenu.add(this.useBuiltInMenuItem);
		this.advancedButton.setName("HelpAdvanced");
		this.advancedButton.setToolTipText("For help authors: show the help from a folder of help source");
		this.advancedButton.addActionListener(e -> advancedMenu.show(this.advancedButton, 0, this.advancedButton.getHeight()));
		toolBar.add(this.advancedButton);
		return toolBar;
	}

	/** Shown while the help comes from a source folder: which folder, its problems, and the way back. */
	private JComponent createSourceBanner() {
		this.sourceBanner.setName("HelpSourceBanner");
		this.sourceBanner.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 1, 0, VCellColors.getWarningForeground()),
				BorderFactory.createEmptyBorder(4, 8, 4, 8)));
		this.sourceLabel.setName("HelpSourceFolder");
		this.bannerBuiltInButton.setName("HelpUseBuiltIn");
		this.bannerBuiltInButton.setToolTipText("Stop showing the source folder and go back to the help built into VCell");
		this.bannerBuiltInButton.addActionListener(e -> this.useBuiltInHelp());
		JPanel buttons = new JPanel();
		buttons.setOpaque(false);
		buttons.add(this.bannerBuiltInButton);
		this.sourceBanner.add(this.sourceLabel, BorderLayout.CENTER);
		this.sourceBanner.add(buttons, BorderLayout.EAST);
		this.sourceBanner.setVisible(false);
		return this.sourceBanner;
	}

	private JComponent createNavigationPanel() {
		this.contentsTree.setName("HelpContents");
		this.contentsTree.setCellRenderer(new PlainTextTreeCellRenderer());
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

		this.problemsList.setName("HelpProblemList");
		this.problemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.problemsList.getAccessibleContext().setAccessibleName("Problems in the help source");
		this.problemsList.setCellRenderer(new ProblemRenderer());
		this.problemsList.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			this.openSelectedProblem();
		});
		this.problemsLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		this.problemsLabel.setLabelFor(this.problemsList);
		this.problemsPanel.setName("HelpProblems");
		this.problemsPanel.add(this.problemsLabel, BorderLayout.NORTH);
		JScrollPane problemsScrollPane = new JScrollPane(this.problemsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.problemsPanel.add(problemsScrollPane, BorderLayout.CENTER);
		// problems wrap to the list's width, so row heights change with it; the list caches them
		this.problemsList.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				HelpViewer.this.problemsList.setFixedCellHeight(1);
				HelpViewer.this.problemsList.setFixedCellHeight(-1);
			}
		});

		this.navigationTabs.setName("HelpNavigation");
		this.navigationTabs.addTab(CONTENTS_TAB, this.navigationPanel);
		this.navigationTabs.setToolTipTextAt(0, "The help's contents, or the pages matching a search");
		this.navigationTabs.setMinimumSize(new Dimension(160, 0));
		return this.navigationTabs;
	}

	/** The Problems tab is there only while a source folder is shown. */
	private void setProblemsTabShown(boolean shown) {
		int index = this.navigationTabs.indexOfComponent(this.problemsPanel);
		if (shown && index < 0) {
			this.navigationTabs.addTab("Problems", this.problemsPanel);
			this.navigationTabs.setToolTipTextAt(this.navigationTabs.getTabCount() - 1, "What to fix in the help source folder");
		} else if (!shown && index >= 0) {
			this.navigationTabs.removeTabAt(index);
		}
	}

	private JComponent createContentPanel() {
		this.contentPane.setName("HelpPage");
		this.contentPane.setEditable(false);
		this.contentPane.setEditorKit(new SafeHtmlEditorKit());
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
			// the renderer writes only allowed links; check again before the OS gets the address
			if (!HelpBook.isAllowedExternalLink(href)) {
				LG.warn("help link '" + href + "' was not opened: only http, https and mailto links are");
				return;
			}
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
		String headingId = this.book.resolveHeadingId(target, fragment);
		this.openPage(target, headingId != null ? headingId : fragment, List.of());
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

	// -------------------------------------------------------------------- help source folder

	/** A folder's help, loaded off the event thread, with its problems and change fingerprint. */
	private record FolderLoad(HelpSource.Folder folder, HelpBook book, List<HelpProblem> problems, String fingerprint) {
		static FolderLoad load(File directory) throws IOException {
			HelpSource.Folder folder = HelpSource.Folder.open(directory);
			String fingerprint = HelpViewer.fingerprint(folder);
			HelpBook book = new HelpBook(folder);
			return new FolderLoad(folder, book, HelpChecks.check(book), fingerprint);
		}
	}

	private static Preferences preferences() {
		return Preferences.userNodeForPackage(HelpViewer.class);
	}

	/**
	 * Show the folder used last time, if any; the built-in help stays up if it can't be. The
	 * setting is cleared first and saved again only once the folder is on screen, so a folder
	 * that breaks the help (or VCell) is not opened again next time.
	 */
	private void restoreSourceFolder() {
		String saved;
		try {
			saved = HelpViewer.preferences().get(SOURCE_FOLDER_PREFERENCE, null);
			if (saved != null) {
				HelpViewer.preferences().remove(SOURCE_FOLDER_PREFERENCE);
				HelpViewer.preferences().flush();
			}
		} catch (RuntimeException | java.util.prefs.BackingStoreException e) {
			return;
		}
		if (saved == null || saved.isBlank()) return;
		this.loadSourceFolder(new File(saved), false);
	}

	private void chooseSourceFolder() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle("Choose a Help Source Folder");
		chooser.setApproveButtonText("Show This Help");
		if (this.sourceFolder != null) chooser.setCurrentDirectory(this.sourceFolder.getRoot().toFile());
		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
		this.loadSourceFolder(chooser.getSelectedFile(), true);
	}

	/**
	 * Load a folder off the event thread and show it.
	 *
	 * @param chosenNow true when the author just picked it (a failure is a dialog); false for the
	 *                  saved folder at start-up or a reload after a change (a failure is shown
	 *                  in the banner, and the help already on screen stays)
	 */
	private void loadSourceFolder(File directory, boolean chosenNow) {
		// a folder just chosen replaces any load in progress; a reload waits its turn
		if (this.folderTaskRunning && !chosenNow) return;
		this.folderTaskRunning = true;
		int generation = ++this.folderGeneration;
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		CompletableFuture.supplyAsync(() -> {
					try {
						return FolderLoad.load(directory);
					} catch (IOException e) {
						throw new java.util.concurrent.CompletionException(e);
					}
				}, LOADER)
				.orTimeout(LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.whenComplete((loaded, error) -> SwingUtilities.invokeLater(() -> {
					if (generation != this.folderGeneration) return; // superseded, or back to the built-in help
					this.folderTaskRunning = false;
					this.setCursor(Cursor.getDefaultCursor());
					if (error == null) {
						this.showFolder(loaded);
						return;
					}
					this.folderLoadFailed(directory, HelpViewer.describeFailure(error), chosenNow);
				}));
	}

	private static String describeFailure(Throwable error) {
		Throwable cause = error instanceof java.util.concurrent.CompletionException && error.getCause() != null ? error.getCause() : error;
		if (cause instanceof HelpSource.FolderException) return cause.getMessage();
		if (cause instanceof TimeoutException) return "Reading the folder took longer than " + LOAD_TIMEOUT_SECONDS + " seconds.";
		if (cause instanceof IOException) return "The help in this folder could not be read: " + cause.getMessage();
		LG.error("help source folder could not be loaded", cause);
		return "The help in this folder could not be read (" + cause.getClass().getSimpleName() + ").";
	}

	private void folderLoadFailed(File directory, String message, boolean chosenNow) {
		if (chosenNow) {
			// the message can quote the folder's path, which can contain anything; VCell's dialogs
			// render a message containing "<html>" as HTML (images, objects and all)
			DialogUtils.showWarningDialog(this, HelpViewer.plainText(message));
			return;
		}
		if (this.sourceFolder != null) {
			// a reload failed: keep showing the last good version, and say so
			this.sourceLabel.setText("<html>Showing help from <b>" + HelpViewer.escapeHtml(this.sourceFolder.describe())
					+ "</b> as last read. Reloading failed: " + HelpViewer.escapeHtml(message) + "</html>");
			return;
		}
		LG.warn("help source folder " + directory + " was not shown: " + message);
		this.sourceLabel.setText("<html>The help source folder <b>" + HelpViewer.escapeHtml(directory.getPath())
				+ "</b> could not be shown, so this is the built-in help. " + HelpViewer.escapeHtml(message) + "</html>");
		this.setProblemsTabShown(false);
		this.sourceBanner.setVisible(true);
	}

	private void showFolder(FolderLoad loaded) {
		boolean firstTime = this.sourceFolder == null || !this.sourceFolder.getRoot().equals(loaded.folder().getRoot());
		this.sourceFolder = loaded.folder();
		this.sourceFolderFingerprint = loaded.fingerprint();
		try {
			HelpViewer.preferences().put(SOURCE_FOLDER_PREFERENCE, loaded.folder().getRoot().toString());
		} catch (RuntimeException e) {
			LG.warn("could not remember the help source folder", e);
		}
		this.setBook(loaded.book(), !firstTime);
		this.setProblemsTabShown(true);
		this.setProblems(loaded.problems());
		this.sourceLabel.setText("<html>Showing help from the source folder <b>" + HelpViewer.escapeHtml(loaded.folder().describe())
				+ "</b>, not the help built into VCell. It reloads when files change.</html>");
		this.sourceLabel.setToolTipText(loaded.folder().describe());
		this.sourceBanner.setVisible(true);
		this.useBuiltInMenuItem.setEnabled(true);
		if (this.isShowing()) this.watchTimer.start();
		if (firstTime && !loaded.problems().isEmpty()) this.showProblems();
	}

	private void useBuiltInHelp() {
		this.watchTimer.stop();
		this.folderGeneration++; // drop the result of any load still running
		this.folderTaskRunning = false;
		this.setCursor(Cursor.getDefaultCursor());
		try {
			HelpViewer.preferences().remove(SOURCE_FOLDER_PREFERENCE);
		} catch (RuntimeException e) {
			LG.warn("could not forget the help source folder", e);
		}
		this.sourceBanner.setVisible(false);
		this.useBuiltInMenuItem.setEnabled(false);
		if (this.sourceFolder == null) return;
		this.sourceFolder = null;
		this.sourceFolderFingerprint = null;
		this.setProblems(List.of());
		this.setProblemsTabShown(false);
		try {
			this.setBook(HelpBook.fromClasspath(), false);
		} catch (IOException e) {
			LG.error("built-in help could not be loaded", e);
		}
	}

	/** Called by the watch timer: reload the folder when any of its files changed. */
	private void checkFolderForChanges() {
		HelpSource.Folder folder = this.sourceFolder;
		if (folder == null || this.folderTaskRunning) return;
		this.folderTaskRunning = true;
		int generation = this.folderGeneration;
		CompletableFuture.supplyAsync(() -> HelpViewer.fingerprint(folder), LOADER)
				.orTimeout(LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.whenComplete((fingerprint, error) -> SwingUtilities.invokeLater(() -> {
					if (generation != this.folderGeneration) return;
					this.folderTaskRunning = false;
					if (error != null || folder != this.sourceFolder || fingerprint.equals(this.sourceFolderFingerprint)) return;
					this.loadSourceFolder(folder.getRoot().toFile(), false);
				}));
	}

	/**
	 * Names, sizes and modification times of toc.md and everything under pages and images,
	 * without following links; any edit, addition, removal or rename changes it.
	 */
	private static String fingerprint(HelpSource.Folder folder) {
		Path root = folder.getRoot();
		StringBuilder fingerprint = new StringBuilder();
		int[] count = {0};
		for (String top : List.of(HelpBook.TOC_PATH, "pages", "images")) {
			Path start = root.resolve(top);
			if (!Files.exists(start, LinkOption.NOFOLLOW_LINKS)) continue;
			try (Stream<Path> entries = Files.walk(start, HelpSource.MAX_PATH_SEGMENTS)) {
				entries.limit(HelpSource.MAX_FOLDER_FILES + 1L).forEach(entry -> {
					count[0]++;
					try {
						BasicFileAttributes attributes = Files.readAttributes(entry, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
						fingerprint.append(root.relativize(entry)).append('|').append(attributes.size())
								.append('|').append(attributes.lastModifiedTime().toMillis()).append('\n');
					} catch (IOException e) {
						fingerprint.append(entry.getFileName()).append("|?\n");
					}
				});
			} catch (IOException | RuntimeException e) {
				fingerprint.append(top).append("|unreadable\n");
			}
		}
		return count[0] + ":" + Integer.toHexString(fingerprint.toString().hashCode()) + ":" + fingerprint.length();
	}

	/**
	 * Show another book (the built-in help or a folder's), keeping the page that is open if the
	 * new book has it.
	 *
	 * @param keepScroll true for a reload of the same folder: stay where the author was reading
	 */
	private void setBook(HelpBook newBook, boolean keepScroll) {
		String openPath = this.currentPage == null ? null : this.currentPage.path();
		int scrollPosition = this.contentScrollPane.getVerticalScrollBar().getValue();
		this.book = newBook;
		this.renderer = new HelpRenderer(newBook, HelpRenderer.Target.SWING, MAX_IMAGE_WIDTH);
		this.search = null;
		this.contentsTree.setModel(HelpViewer.buildTreeModel(newBook));
		this.history.clear();
		this.historyIndex = -1;
		this.currentPage = null;
		HelpPage page = openPath == null ? null : newBook.getPageByPath(openPath);
		this.openPage(page != null ? page : newBook.getHomePage(), null, List.of());
		if (!this.searchField.getText().isBlank()) this.runSearch();
		if (keepScroll && page != null) {
			SwingUtilities.invokeLater(() -> SwingUtilities.invokeLater(() -> this.contentScrollPane.getVerticalScrollBar().setValue(scrollPosition)));
		}
	}

	private void setProblems(List<HelpProblem> problems) {
		DefaultListModel<HelpProblem> model = (DefaultListModel<HelpProblem>) this.problemsList.getModel();
		model.clear();
		model.addAll(problems);
		this.problemsLabel.setText(problems.isEmpty() ? "No problems found" : "Choose one to open its page");
		int index = this.navigationTabs.indexOfComponent(this.problemsPanel);
		if (index >= 0) this.navigationTabs.setTitleAt(index, problems.isEmpty() ? "Problems" : "Problems (" + problems.size() + ")");
	}

	private void showProblems() {
		this.setProblemsTabShown(true);
		this.navigationTabs.setSelectedComponent(this.problemsPanel);
		if (this.problemsList.getModel().getSize() > 0) this.problemsList.requestFocusInWindow();
	}

	private void openSelectedProblem() {
		HelpProblem problem = this.problemsList.getSelectedValue();
		if (problem == null) return;
		HelpPage page = this.book.getPageByPath(problem.path());
		if (page == null || page == this.currentPage) return;
		this.openPage(page, null, List.of());
	}

	/**
	 * Three lines per problem: what sort it is, where it is, then the whole message, wrapped to
	 * the list's width.
	 */
	private static final class ProblemRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (!(value instanceof HelpProblem problem)) return this;
			int textWidth = Math.max(120, list.getWidth() - 20);
			String dim = VCellColors.toHtmlColor(isSelected ? this.getForeground() : VCellColors.getDimForeground());
			String message = problem.message().isEmpty() ? "" : Character.toUpperCase(problem.message().charAt(0)) + problem.message().substring(1);
			// a table width is in real pixels; Swing scales a CSS width, so text would run past the edge
			this.setText("<html><table width='" + textWidth + "' cellpadding='0' cellspacing='0' border='0'><tr><td>"
					+ "<b>" + HelpViewer.escapeHtml(problem.kind().label()) + "</b>"
					+ "<br><font color='" + dim + "'>" + HelpViewer.escapeHtml(problem.location()).replace(", line ", ", line&nbsp;") + "</font>"
					+ "<br>" + HelpViewer.escapeHtml(message) + "</td></tr></table></html>");
			this.setToolTipText(null);
			this.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
			return this;
		}
	}

	/**
	 * Contents titles come from the help source and are shown as typed. A Swing label renders text
	 * that starts with "<html>" as HTML, objects and all, so a title like
	 * {@code `<html><object ...>`} would otherwise run in the tree.
	 */
	static final class PlainTextTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
		PlainTextTreeCellRenderer() {
			this.putClientProperty("html.disable", Boolean.TRUE);
		}

		@Override
		public void updateUI() {
			super.updateUI();
			this.putClientProperty("html.disable", Boolean.TRUE);
		}
	}

	/**
	 * HTMLEditorKit that never builds components from the page: Swing's default makes Java
	 * objects for {@code <object>} tags and live controls for forms. The renderer never writes
	 * these tags; this keeps a slip there harmless.
	 */
	private static final class SafeHtmlEditorKit extends HTMLEditorKit {
		private static final Set<HTML.Tag> BLOCKED_TAGS = Set.of(HTML.Tag.OBJECT, HTML.Tag.APPLET, HTML.Tag.INPUT,
				HTML.Tag.SELECT, HTML.Tag.TEXTAREA, HTML.Tag.FRAME, HTML.Tag.FRAMESET, HTML.Tag.ISINDEX);
		private final ViewFactory viewFactory = new HTMLFactory() {
			@Override
			public View create(Element element) {
				Object name = element.getAttributes().getAttribute(StyleConstants.NameAttribute);
				if (name instanceof HTML.Tag tag && BLOCKED_TAGS.contains(tag)) {
					return new ComponentView(element) {
						@Override
						protected Component createComponent() {
							return null;
						}
					};
				}
				return super.create(element);
			}
		};

		private SafeHtmlEditorKit() {
			this.setAutoFormSubmission(false);
		}

		@Override
		public ViewFactory getViewFactory() {
			return this.viewFactory;
		}
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
		this.navigationTabs.setSelectedComponent(this.navigationPanel);
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
			// no document base: the renderer gives images absolute URLs it has checked, and nothing
			// else on the page may load anything
			document.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
			HelpRenderer.RenderedPage rendered = this.renderer.renderPage(page);
			// Swing's ImageView takes images from this cache when the document has one, and then
			// never fetches or decodes anything itself; an image missing from it is not shown
			document.putProperty("imageCache", this.decodeImages(rendered));
			String html = "<html><body>" + rendered.html() + "</body></html>";
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

	/** The page's images decoded at their shown size, within the per-page budgets. */
	private java.util.Hashtable<java.net.URL, java.awt.Image> decodeImages(HelpRenderer.RenderedPage rendered) {
		java.util.Hashtable<java.net.URL, java.awt.Image> cache = new java.util.Hashtable<>();
		long pixels = 0;
		for (java.util.Map.Entry<String, HelpRenderer.ShownImage> entry : rendered.images().entrySet()) {
			HelpRenderer.ShownImage shown = entry.getValue();
			pixels += (long) shown.width() * shown.height();
			if (cache.size() >= MAX_PAGE_IMAGES || pixels > MAX_PAGE_IMAGE_PIXELS) {
				LG.warn("help page has more images than are shown; left out " + shown.path());
				continue;
			}
			java.awt.image.BufferedImage image = this.book.readDisplayImage(shown.path(), shown.width(), shown.height());
			if (image == null) continue;
			try {
				cache.put(java.net.URI.create(entry.getKey()).toURL(), image);
			} catch (IllegalArgumentException | java.net.MalformedURLException e) {
				LG.warn("help image " + shown.path() + " has an unusable address", e);
			}
		}
		return cache;
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

	/** Text that no Swing component will take for HTML: angle brackets become look-alikes. */
	static String plainText(String text) {
		return text.replace('<', '\u2039').replace('>', '\u203A');
	}

	private static String escapeHtml(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
