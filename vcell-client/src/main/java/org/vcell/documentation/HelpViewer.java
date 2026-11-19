package org.vcell.documentation;

import cbit.vcell.client.laf.AppearanceMenus;
import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import org.vcell.util.gui.VCellIcons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.util.OperatingSystemInfo;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GeneralGuiUtils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * <p><b>Help source folder</b> (View menu): an author can show the help from a folder of
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


	private HelpBook book;
	private HelpRenderer renderer;
	private HelpSearch search; // built on first search
	private HelpSource.Folder sourceFolder; // null while showing the built-in help
	private String sourceFolderFingerprint;
	private boolean folderTaskRunning = false;
	/** Bumped by every folder load and by going back to the built-in help; a result from an older one is dropped. */
	private int folderGeneration = 0;

	/** The open pages, one tab each; see {@link HelpTab}. */
	private final javax.swing.JTabbedPane pageTabs = new javax.swing.JTabbedPane(javax.swing.JTabbedPane.TOP, javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
	/** The last tab: never selected (it is disabled); its "+" button opens a new tab before it. */
	private final JPanel newTabPlaceholder = new JPanel();
	private final JTree contentsTree;
	private final JList<HelpSearch.Hit> resultsList = new JList<>(new DefaultListModel<>());
	private final JLabel resultsLabel = new JLabel();
	private final JTextField searchField = new JTextField();
	private final CardLayout navigationCards = new CardLayout();
	private final JPanel navigationPanel = new JPanel(this.navigationCards);
	/** Contents (or search results) and, while a source folder is shown, its Problems. */
	private final javax.swing.JTabbedPane navigationTabs = new javax.swing.JTabbedPane();
	private final JPanel problemsPanel = new JPanel(new BorderLayout());
	// each command once, shared by its toolbar button and menu item so they enable together
	private final Action backAction = HelpViewer.action("Back", this::goBack);
	private final Action forwardAction = HelpViewer.action("Forward", this::goForward);
	private final Action homeAction = HelpViewer.action("Home", this::showTableOfContents);
	private final Action searchAction = HelpViewer.action("Search Help", this::focusSearch);
	private final Action contentsTabAction = HelpViewer.action("Contents", this::showContentsTab);
	private final Action problemsTabAction = HelpViewer.action("Problems", this::showProblems);
	private final Action expandAllAction = HelpViewer.action("Expand All", this::expandAllContents);
	private final Action collapseAllAction = HelpViewer.action("Collapse All", this::collapseAllContents);
	private final Action chooseFolderAction = HelpViewer.action("Show Help from a Source Folder...", this::chooseSourceFolder);
	private final Action useBuiltInAction = HelpViewer.action("Use the Built-in Help", this::useBuiltInHelp);
	private final Action newTabAction = HelpViewer.action("New Tab", this::newTab);
	private final Action closeTabAction = HelpViewer.action("Close Tab", this::closeTab);
	private final Action nextTabAction = HelpViewer.action("Show Next Tab", () -> this.selectTab(1));
	private final Action previousTabAction = HelpViewer.action("Show Previous Tab", () -> this.selectTab(-1));
	private final JButton backButton = new JButton(this.backAction);
	private final JButton forwardButton = new JButton(this.forwardAction);
	private final JButton homeButton = new JButton(this.homeAction);
	private final Timer searchTimer = new Timer(SEARCH_DELAY_MILLIS, e -> this.runSearch());
	private final Timer watchTimer = new Timer(WATCH_INTERVAL_MILLIS, e -> this.checkFolderForChanges());

	private final JPanel sourceBanner = new JPanel(new BorderLayout(8, 0));
	private final JLabel sourceLabel = new JLabel();
	private final JButton bannerBuiltInButton = new JButton("Use Built-in Help");
	private final JList<HelpProblem> problemsList = new JList<>(new DefaultListModel<>());
	private final JLabel problemsLabel = new JLabel();
	private final JCheckBox showErrorsCheckBox = new JCheckBox("Errors", true);
	private final JCheckBox showWarningsCheckBox = new JCheckBox("Warnings", true);
	private List<HelpProblem> allProblems = List.of(); // the folder's, before scope and filters
	private boolean listingProblems = false; // rebuilding the list: its selection is not the reader's

	private boolean selectingProgrammatically = false;

	/** The top-level help window: tracked in the Window menu, independent of any document. */
	private static final class HelpWindow extends LWTopFrame {
		private final HelpViewer viewer;

		private HelpWindow(HelpViewer viewer) {
			super();
			this.viewer = viewer;
			this.setTitle("Virtual Cell Help -- VCell " + VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString());
			this.getContentPane().add(viewer);
			this.setJMenuBar(viewer.createMenuBar(this));
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

	/**
	 * Open the help on its table of contents, with no page open (Help > VCell Help), in a tab of
	 * its own: a tab already showing the contents, else a new one.
	 */
	public static void showHelp() {
		HelpWindow window = HelpViewer.openHelpWindow();
		if (window == null) return;
		window.viewer.showInTab(HelpTab.Location.contents());
		window.setVisible(true);
		window.toFront();
	}

	/**
	 * Open the help at a page, in a tab of its own (a tab already showing the page, else a new
	 * one), reusing the help window if it is already open.
	 *
	 * @param pageId a {@link HelpIds} constant (a page file name without ".md"); an unknown id
	 *               shows the table of contents
	 */
	public static void showHelp(String pageId) {
		HelpWindow window = HelpViewer.openHelpWindow();
		if (window == null) return;
		window.viewer.showInTab(HelpTab.Location.page(window.viewer.findPage(pageId), null));
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
		// Cmd/Ctrl+W closes a tab here (the window closes with its last), so no close-window shortcut
		this.installKeyBindings();
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
		this.backButton.setName("HelpBack");
		this.forwardButton.setName("HelpForward");
		this.backButton.setToolTipText("Back (" + menuKey + "[ or Alt+Left)");
		this.forwardButton.setToolTipText("Forward (" + menuKey + "] or Alt+Right)");
		this.homeButton.setName("HelpHome");
		this.homeButton.setToolTipText("Show the table of contents, with no page open (" + (HelpViewer.isMac() ? "⇧⌘H" : "Ctrl+Shift+H") + ")");
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
		// enabled once a source folder is shown (View menu)
		this.useBuiltInAction.setEnabled(false);
		this.problemsTabAction.setEnabled(false);
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
		// a click in the empty space below the entries deselects, leaving no page open (the table
		// of contents), as a click off a selection does elsewhere; Swing's tree ignores it
		this.contentsTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e) || e.isPopupTrigger()) return;
				JTree tree = HelpViewer.this.contentsTree;
				int lastRow = tree.getRowCount() - 1;
				Rectangle lastRowBounds = lastRow < 0 ? null : tree.getRowBounds(lastRow);
				if (lastRowBounds != null && e.getY() < lastRowBounds.y + lastRowBounds.height) return;
				tree.clearSelection();
			}
		});
		// Escape leaves the open page for the table of contents, as deselecting (Cmd/Ctrl-click) does
		this.contentsTree.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "showContents");
		this.contentsTree.getActionMap().put("showContents", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpViewer.this.showTableOfContents();
			}
		});
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
			if (e.getValueIsAdjusting() || this.listingProblems) return;
			this.openSelectedProblem();
		});
		this.problemsList.setToolTipText("Choose one to see it in full; double-click to go to it");
		// a double-click goes straight to the problem, as the problem tab's Go to button does
		this.problemsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2 || !SwingUtilities.isLeftMouseButton(e)) return;
				HelpTab tab = HelpViewer.this.selectedTab();
				if (tab != null && tab.problem() != null) tab.goToProblem();
			}
		});
		this.problemsLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 0, 6));
		this.problemsLabel.setLabelFor(this.problemsList);
		this.showErrorsCheckBox.setName("HelpShowErrors");
		this.showErrorsCheckBox.setToolTipText("List the errors: what the help shows wrongly, and what fails its checks");
		this.showErrorsCheckBox.addActionListener(e -> this.listProblems());
		this.showWarningsCheckBox.setName("HelpShowWarnings");
		this.showWarningsCheckBox.setToolTipText("List the warnings: worth tidying, but they fail nothing");
		this.showWarningsCheckBox.addActionListener(e -> this.listProblems());
		JPanel filters = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 4, 0));
		filters.add(this.showErrorsCheckBox);
		filters.add(this.showWarningsCheckBox);
		JPanel problemsHeader = new JPanel(new BorderLayout());
		problemsHeader.add(this.problemsLabel, BorderLayout.NORTH);
		problemsHeader.add(filters, BorderLayout.SOUTH);
		this.problemsPanel.setName("HelpProblems");
		this.problemsPanel.add(problemsHeader, BorderLayout.NORTH);
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
		this.problemsTabAction.setEnabled(shown);
		int index = this.navigationTabs.indexOfComponent(this.problemsPanel);
		if (shown && index < 0) {
			this.navigationTabs.addTab("Problems", this.problemsPanel);
			this.navigationTabs.setToolTipTextAt(this.navigationTabs.getTabCount() - 1, "What to fix in the help source folder (errors/warnings)");
			this.listProblems();
		} else if (!shown && index >= 0) {
			this.navigationTabs.removeTabAt(index);
		}
	}

	private JComponent createContentPanel() {
		this.pageTabs.setName("HelpPages");
		this.pageTabs.getAccessibleContext().setAccessibleName("Open help pages");
		JButton plus = HelpViewer.glyphButton(true, "HelpNewTab", "New Tab (" + (HelpViewer.isMac() ? "⌘" : "Ctrl+") + "T)", "New tab");
		plus.addActionListener(e -> this.newTab());
		this.pageTabs.addTab("", this.newTabPlaceholder);
		this.pageTabs.setTabComponentAt(0, plus);
		this.pageTabs.setEnabledAt(0, false);
		this.pageTabs.addChangeListener(e -> {
			// the "+" tab is only ever selected when a tab before it closes: show a page instead
			if (this.pageTabs.getSelectedComponent() == this.newTabPlaceholder) {
				List<HelpTab> tabs = this.tabs();
				if (!tabs.isEmpty()) this.pageTabs.setSelectedComponent(tabs.get(tabs.size() - 1));
				return;
			}
			HelpTab tab = this.selectedTab();
			if (tab != null) this.syncToTab(tab);
		});
		return this.pageTabs;
	}

	/** Cmd on macOS, Ctrl elsewhere. */
	static int menuMask() {
		try {
			return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
		} catch (java.awt.HeadlessException e) {
			return InputEvent.CTRL_DOWN_MASK; // no display (tests): as ShortcutsWizard assumes
		}
	}

	/** Shortcuts that work with or without the window's menu bar (see {@link #createMenuBar}). */
	private void installKeyBindings() {
		int menuMask = HelpViewer.menuMask();
		this.bind("helpBack", this::goBack,
				KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, menuMask),
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK));
		this.bind("helpForward", this::goForward,
				KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, menuMask),
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK));
		this.bind("helpFind", this::focusSearch, KeyStroke.getKeyStroke(KeyEvent.VK_F, menuMask));
		this.bind("helpNewTab", this::newTab, KeyStroke.getKeyStroke(KeyEvent.VK_T, menuMask));
		this.bind("helpCloseTab", this::closeTab, KeyStroke.getKeyStroke(KeyEvent.VK_W, menuMask));
	}

	private static Action action(String name, Runnable command) {
		return new AbstractAction(name) {
			@Override
			public void actionPerformed(ActionEvent e) {
				command.run();
			}
		};
	}

	private void focusSearch() {
		this.searchField.requestFocusInWindow();
		this.searchField.selectAll();
	}

	private void showContentsTab() {
		this.navigationTabs.setSelectedComponent(this.navigationPanel);
	}

	/** Expands row by row: each expansion adds the rows beneath it, which the loop then reaches. */
	private void expandAllContents() {
		for (int row = 0; row < this.contentsTree.getRowCount(); row++) this.contentsTree.expandRow(row);
	}

	private void collapseAllContents() {
		for (int row = this.contentsTree.getRowCount() - 1; row >= 0; row--) this.contentsTree.collapseRow(row);
	}

	// ------------------------------------------------------------------------------ menu bar

	/**
	 * The help window's menu bar: File (tabs), Go, View (with the help source folder's commands, for authors)
	 * and VCell's Window menu, with the theme and font as the Welcome and model windows have them. Its
	 * shortcuts replace the window-wide key bindings for the same keys, so that no key runs a
	 * command twice (the macOS screen menu bar handles a shortcut itself).
	 */
	JMenuBar createMenuBar(Component window) {
		int menuMask = HelpViewer.menuMask();
		JMenu file = HelpViewer.menu("File", KeyEvent.VK_F, "HelpFileMenu");
		file.add(this.menuItem(this.newTabAction, KeyStroke.getKeyStroke(KeyEvent.VK_T, menuMask)));
		file.add(this.menuItem(this.closeTabAction, KeyStroke.getKeyStroke(KeyEvent.VK_W, menuMask)));

		JMenu go = HelpViewer.menu("Go", KeyEvent.VK_G, "HelpGoMenu");
		go.add(this.menuItem(this.backAction, KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, menuMask)));
		go.add(this.menuItem(this.forwardAction, KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, menuMask)));
		go.add(this.menuItem(this.homeAction, KeyStroke.getKeyStroke(KeyEvent.VK_H, menuMask | InputEvent.SHIFT_DOWN_MASK)));
		go.addSeparator();
		go.add(this.menuItem(this.searchAction, KeyStroke.getKeyStroke(KeyEvent.VK_F, menuMask)));
		go.addSeparator();
		go.add(this.menuItem(this.nextTabAction, KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, menuMask | InputEvent.SHIFT_DOWN_MASK)));
		go.add(this.menuItem(this.previousTabAction, KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, menuMask | InputEvent.SHIFT_DOWN_MASK)));

		JMenu view = HelpViewer.menu("View", KeyEvent.VK_V, "HelpViewMenu");
		view.add(this.menuItem(this.contentsTabAction, KeyStroke.getKeyStroke(KeyEvent.VK_1, menuMask)));
		view.add(this.menuItem(this.problemsTabAction, KeyStroke.getKeyStroke(KeyEvent.VK_2, menuMask)));
		view.addSeparator();
		view.add(this.menuItem(this.expandAllAction, null));
		view.add(this.menuItem(this.collapseAllAction, null));
		view.addSeparator();
		view.add(this.menuItem(this.chooseFolderAction, null));
		view.add(this.menuItem(this.useBuiltInAction, null));

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(go);
		menuBar.add(view);
		menuBar.add(LWTopFrame.createWindowMenu(true, AppearanceMenus.create(window)));
		return menuBar;
	}

	private static JMenu menu(String text, int mnemonic, String name) {
		JMenu menu = new JMenu(text);
		menu.setMnemonic(mnemonic);
		menu.setName(name);
		return menu;
	}

	private JMenuItem menuItem(Action action, KeyStroke accelerator) {
		JMenuItem item = new JMenuItem(action);
		if (accelerator == null) return item;
		item.setAccelerator(accelerator);
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(accelerator);
		return item;
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

	/** The help window, made the first time; null (and the reader told) if the help can't load. */
	private static HelpWindow openHelpWindow() {
		HelpWindow window = HelpViewer.openWindow != null ? HelpViewer.openWindow.get() : null;
		if (window != null) return window;
		HelpBook book;
		try {
			book = HelpBook.fromClasspath();
		} catch (IOException e) {
			LG.error("help could not be loaded", e);
			DialogUtils.showErrorDialog(null, "VCell help could not be loaded: " + e.getMessage(), e);
			return null;
		}
		window = new HelpWindow(new HelpViewer(book));
		HelpViewer.openWindow = new WeakReference<>(window);
		window.viewer.restoreSourceFolder();
		return window;
	}

	/** Show a page by id in the selected tab, as a new history entry; an unknown id shows the contents. */
	public void showPage(String pageId) {
		this.tab().open(HelpTab.Location.page(this.findPage(pageId), null), List.of());
	}

	/** The page with this id, or null (and a warning) when there is none: the contents are shown. */
	private HelpPage findPage(String pageId) {
		HelpPage page = this.book.findPage(pageId);
		if (page == null) LG.warn("no help page with id '" + pageId + "'; showing the contents");
		return page;
	}

	/** Show the table of contents in the selected tab, with no page open, as a new history entry. */
	public void showTableOfContents() {
		this.tab().open(HelpTab.Location.contents(), List.of());
	}

	// ---------------------------------------------------------------------------------- tabs

	/** The selected tab; a new one, at the contents, when none is open. */
	private HelpTab tab() {
		HelpTab tab = this.selectedTab();
		return tab != null ? tab : this.addTab(true);
	}

	private HelpTab selectedTab() {
		return this.pageTabs.getSelectedComponent() instanceof HelpTab tab ? tab : null;
	}

	private List<HelpTab> tabs() {
		// by tab, not by child component: while a tab closes, its component is still a child
		List<HelpTab> tabs = new ArrayList<>();
		for (int index = 0; index < this.pageTabs.getTabCount(); index++) {
			if (this.pageTabs.getComponentAt(index) instanceof HelpTab tab) tabs.add(tab);
		}
		return tabs;
	}

	private HelpTab addTab(boolean select) {
		HelpTab tab = new HelpTab(this);
		// before the "+" tab, always the last
		this.pageTabs.insertTab("", null, tab, null, this.pageTabs.indexOfComponent(this.newTabPlaceholder));
		this.pageTabs.setTabComponentAt(this.pageTabs.indexOfComponent(tab), new TabHeader(tab));
		if (select) this.pageTabs.setSelectedComponent(tab);
		this.updateTabActions();
		return tab;
	}

	/**
	 * Show a place in a tab of its own, as opening the help from VCell does: the tab already
	 * there, if one is, so opening the same help twice doesn't pile up tabs; else a new tab.
	 */
	void showInTab(HelpTab.Location location) {
		for (HelpTab tab : this.tabs()) {
			if (!tab.place().equals(location)) continue;
			this.pageTabs.setSelectedComponent(tab);
			return;
		}
		this.addTab(true).open(location, List.of());
	}

	/** A new tab at Home: the table of contents. */
	private void newTab() {
		this.addTab(true).open(HelpTab.Location.contents(), List.of());
	}

	/** Close the selected tab; closing the last one closes the help window. */
	private void closeTab() {
		HelpTab tab = this.selectedTab();
		if (tab != null) this.closeTab(tab);
	}

	private void closeTab(HelpTab tab) {
		this.pageTabs.remove(tab);
		this.updateTabActions();
		if (!this.tabs().isEmpty()) return;
		java.awt.Window window = SwingUtilities.getWindowAncestor(this);
		if (window != null) window.dispatchEvent(new java.awt.event.WindowEvent(window, java.awt.event.WindowEvent.WINDOW_CLOSING));
	}

	/** Select the next (1) or previous (-1) tab, wrapping around (past the "+" tab). */
	private void selectTab(int step) {
		List<HelpTab> tabs = this.tabs();
		if (tabs.size() < 2) return;
		int index = Math.max(0, tabs.indexOf(this.selectedTab()));
		this.pageTabs.setSelectedComponent(tabs.get(Math.floorMod(index + step, tabs.size())));
	}

	private void updateTabActions() {
		int count = this.tabs().size();
		this.nextTabAction.setEnabled(count > 1);
		this.previousTabAction.setEnabled(count > 1);
		this.closeTabAction.setEnabled(count > 0);
	}

	/** A tab showed something new: retitle it, and if it is the selected tab, follow it. */
	void tabShown(HelpTab tab) {
		int index = this.pageTabs.indexOfComponent(tab);
		if (index >= 0) {
			// the header draws the title, shortened; the tab's own title, which screen readers read, is whole
			this.pageTabs.setTitleAt(index, tab.fullTitle());
			this.pageTabs.setToolTipTextAt(index, HelpViewer.plainText(tab.fullTitle()));
			if (this.pageTabs.getTabComponentAt(index) instanceof TabHeader header) header.refresh();
		}
		if (tab == this.selectedTab()) this.syncToTab(tab);
	}

	/** The contents tree, Back/Forward and the problems pane follow the selected tab. */
	private void syncToTab(HelpTab tab) {
		this.selectInContents(tab.currentPage());
		this.backAction.setEnabled(tab.canGoBack());
		this.forwardAction.setEnabled(tab.canGoForward());
		this.listProblems();
	}

	private void goBack() {
		this.tab().goBack();
	}

	private void goForward() {
		this.tab().goForward();
	}

	/**
	 * Follow a link on a tab's page: in that tab, or with Cmd/Ctrl held, in a new tab behind it.
	 */
	void followLink(HelpTab tab, String href, boolean inNewTab) {
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
		// the contents' links are relative to the help root, where toc.md is
		String fromPath = tab.currentPage() != null ? tab.currentPage().path() : HelpBook.TOC_PATH;
		String targetPath = HelpBook.resolvePath(fromPath, href);
		HelpPage target = targetPath == null ? null : this.book.getPageByPath(targetPath);
		if (target == null) {
			LG.warn("help link '" + href + "' on " + fromPath + " does not resolve");
			return;
		}
		String headingId = this.book.resolveHeadingId(target, fragment);
		HelpTab.Location location = HelpTab.Location.page(target, headingId != null ? headingId : fragment);
		(inNewTab ? this.addTab(false) : tab).open(location, List.of());
	}

	/** A tab's heading: its icon and title, and a button that closes it. */
	private final class TabHeader extends JPanel {
		private final HelpTab tab;
		private final JLabel title = new JLabel();

		private TabHeader(HelpTab tab) {
			super();
			this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
			this.tab = tab;
			this.setOpaque(false);
			// the title comes from the help source: never HTML
			this.title.putClientProperty("html.disable", Boolean.TRUE);
			this.title.setAlignmentY(Component.CENTER_ALIGNMENT);
			JButton close = HelpViewer.glyphButton(false, "HelpCloseTab", "Close Tab (" + (HelpViewer.isMac() ? "⌘" : "Ctrl+") + "W)", "Close tab");
			close.addActionListener(e -> HelpViewer.this.closeTab(this.tab));
			this.add(this.title);
			this.add(javax.swing.Box.createHorizontalStrut(6));
			this.add(close);
			this.refresh();
		}

		private void refresh() {
			this.title.setText(this.tab.title());
			this.title.setIcon(this.tab.icon());
		}
	}

	/**
	 * A small square button drawn as a cross (close) or a plus (new tab). A look and feel's own
	 * button is taller than a tab (Aqua's especially), which pushed the tab's title down; this one
	 * is no bigger than its glyph. It takes the keyboard focus like any button, and shows a border
	 * while it has it.
	 */
	private static JButton glyphButton(boolean plus, String name, String toolTip, String accessibleName) {
		int size = 14;
		Icon glyph = new Icon() {
			@Override
			public void paintIcon(Component component, java.awt.Graphics graphics, int x, int y) {
				java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
				g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				Color color = component.isEnabled() ? UIManager.getColor("Label.foreground") : UIManager.getColor("Label.disabledForeground");
				g.setColor(color != null ? color : Color.DARK_GRAY);
				g.setStroke(new java.awt.BasicStroke(1.4f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
				int inset = plus ? 3 : 4;
				int low = inset;
				int high = size - 1 - inset;
				if (plus) {
					int middle = size / 2;
					g.drawLine(x + middle, y + low, x + middle, y + high);
					g.drawLine(x + low, y + middle, x + high, y + middle);
				} else {
					g.drawLine(x + low, y + low, x + high, y + high);
					g.drawLine(x + high, y + low, x + low, y + high);
				}
				g.dispose();
			}

			@Override
			public int getIconWidth() {
				return size;
			}

			@Override
			public int getIconHeight() {
				return size;
			}
		};
		JButton button = new JButton(glyph);
		button.setName(name);
		button.setToolTipText(toolTip);
		button.getAccessibleContext().setAccessibleName(accessibleName);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setMargin(new java.awt.Insets(0, 0, 0, 0));
		Dimension exact = new Dimension(size + 2, size + 2);
		button.setPreferredSize(exact);
		button.setMinimumSize(exact);
		button.setMaximumSize(exact);
		button.setAlignmentY(Component.CENTER_ALIGNMENT);
		javax.swing.border.Border unfocused = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		button.setBorder(unfocused);
		button.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusGained(java.awt.event.FocusEvent e) {
				Color focus = UIManager.getColor("Focus.color");
				button.setBorder(BorderFactory.createLineBorder(focus != null ? focus : VCellColors.getDimForeground()));
			}

			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
				button.setBorder(unfocused);
			}
		});
		return button;
	}

	private void openSelectedContentsEntry() {
		if (this.selectingProgrammatically) return;
		TreePath selection = this.contentsTree.getSelectionPath();
		HelpTab tab = this.tab();
		if (selection == null) {
			// the open entry was deselected (Cmd/Ctrl-click): no page, so show the contents
			if (!tab.showsContents()) this.showTableOfContents();
			return;
		}
		Object userObject = ((DefaultMutableTreeNode) selection.getLastPathComponent()).getUserObject();
		if (!(userObject instanceof HelpTocEntry entry) || entry.pagePath() == null) return;
		HelpPage page = this.book.getPageByPath(entry.pagePath());
		if (page == null || page == tab.currentPage()) return;
		tab.open(HelpTab.Location.page(page, null), List.of());
	}

	/**
	 * Select the first contents entry for the page (pages may appear more than once); none for
	 * the table of contents, or a page the contents don't list.
	 */
	private void selectInContents(HelpPage page) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.contentsTree.getModel().getRoot();
		if (page == null) {
			this.clearContentsSelection();
			return;
		}
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
		this.clearContentsSelection();
	}

	private void clearContentsSelection() {
		this.selectingProgrammatically = true;
		try {
			this.contentsTree.clearSelection();
		} finally {
			this.selectingProgrammatically = false;
		}
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
	 *                  (package-private for HelpViewerTest)
	 */
	void loadSourceFolder(File directory, boolean chosenNow) {
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
		this.useBuiltInAction.setEnabled(true);
		if (this.isShowing()) this.watchTimer.start();
		// open on the problems only for errors; warnings can wait until the author looks
		if (firstTime && !HelpChecks.errors(loaded.problems()).isEmpty()) this.showProblems();
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
		this.useBuiltInAction.setEnabled(false);
		if (this.sourceFolder == null) return;
		this.sourceFolder = null;
		this.sourceFolderFingerprint = null;
		// the built-in help has no problems to show
		for (HelpTab tab : this.tabs()) {
			if (tab.problem() != null) this.pageTabs.remove(tab);
		}
		this.updateTabActions();
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
	 * Show another book (the built-in help or a folder's), keeping each tab's page if the new
	 * book has it.
	 *
	 * @param keepScroll true for a reload of the same folder: stay where the author was reading
	 */
	private void setBook(HelpBook newBook, boolean keepScroll) {
		this.book = newBook;
		this.renderer = new HelpRenderer(newBook, HelpRenderer.Target.SWING, MAX_IMAGE_WIDTH);
		this.search = null;
		// a new model clears the selection, which is not the reader deselecting a page
		this.selectingProgrammatically = true;
		try {
			this.contentsTree.setModel(HelpViewer.buildTreeModel(newBook));
		} finally {
			this.selectingProgrammatically = false;
		}
		// each tab keeps its place and history: the same page if the new book has it, else the contents
		for (HelpTab tab : this.tabs()) tab.reload(keepScroll);
		if (this.tabs().isEmpty()) this.showTableOfContents();
		if (!this.searchField.getText().isBlank()) this.runSearch();
	}

	private void setProblems(List<HelpProblem> problems) {
		this.allProblems = List.copyOf(problems);
		for (HelpTab tab : this.tabs()) tab.problemsChanged(this.allProblems);
		this.listProblems();
	}

	/** Whether the folder still has this problem (one in a tab may since have been fixed). */
	boolean isReported(HelpProblem problem) {
		return this.allProblems.contains(problem);
	}

	/**
	 * List the problems in scope (the selected tab's page's; all of them for the contents; for a
	 * problem tab, those listed when it opened) that the Errors and Warnings boxes let through, with
	 * the problem tab's problem selected. The tab counts the scope's errors and warnings
	 * ("Problems (2/36)") whatever the boxes, so a hidden error is still counted.
	 */
	private void listProblems() {
		HelpTab tab = this.selectedTab();
		String scopePath = tab == null ? null
				: tab.problem() != null ? tab.place().problemScope()
				: tab.currentPage() != null ? tab.currentPage().path() : null;
		List<HelpProblem> inScope = scopePath == null ? this.allProblems
				: this.allProblems.stream().filter(problem -> problem.path().equals(scopePath)).toList();
		long warnings = inScope.stream().filter(HelpProblem::isWarning).count();
		long errors = inScope.size() - warnings;
		List<HelpProblem> shown = inScope.stream()
				.filter(problem -> problem.isWarning() ? this.showWarningsCheckBox.isSelected() : this.showErrorsCheckBox.isSelected())
				.toList();

		HelpProblem selected = tab == null ? null : tab.problem();
		DefaultListModel<HelpProblem> model = (DefaultListModel<HelpProblem>) this.problemsList.getModel();
		this.listingProblems = true;
		try {
			model.clear();
			model.addAll(shown);
			if (selected != null && shown.contains(selected)) this.problemsList.setSelectedValue(selected, true);
			else this.problemsList.clearSelection();
		} finally {
			this.listingProblems = false;
		}

		this.showErrorsCheckBox.setText("Errors (" + errors + ")");
		this.showWarningsCheckBox.setText("Warnings (" + warnings + ")");
		String scope = scopePath == null ? "All pages" : "This page (Home shows all)";
		String status = !inScope.isEmpty() && shown.isEmpty() ? "None of the ticked kinds."
				: inScope.isEmpty() ? (scopePath == null ? "No problems found." : "No problems on this page.") : "";
		// our own words, so safe as HTML
		this.problemsLabel.setText(status.isEmpty() ? scope : "<html>" + scope + "<br>" + status + "</html>");
		int index = this.navigationTabs.indexOfComponent(this.problemsPanel);
		if (index >= 0) this.navigationTabs.setTitleAt(index, "Problems (" + errors + "/" + warnings + ")");
	}

	/**
	 * Swing wraps HTML only where a line may break, and a path like
	 * pages/Introduction/GeneralNavAndWinSetup.md has none, so it would run past the list's edge.
	 * A zero-width space after each slash lets it wrap there, and shows nothing.
	 */
	private static String breakablePaths(String text) {
		return text.replace("/", "/\u200B");
	}

	private void showProblems() {
		this.setProblemsTabShown(true);
		this.navigationTabs.setSelectedComponent(this.problemsPanel);
		if (this.problemsList.getModel().getSize() > 0) this.problemsList.requestFocusInWindow();
	}

	/**
	 * Show the chosen problem in a tab: the tab already showing it; else the selected tab when it
	 * shows a problem (so stepping through the list doesn't open a tab for each); else a new tab.
	 */
	private void openSelectedProblem() {
		HelpProblem problem = this.problemsList.getSelectedValue();
		if (problem == null) return;
		for (HelpTab tab : this.tabs()) {
			if (!problem.equals(tab.problem())) continue;
			this.pageTabs.setSelectedComponent(tab);
			return;
		}
		HelpTab selected = this.selectedTab();
		String scopePath = selected == null ? null
				: selected.problem() != null ? selected.place().problemScope()
				: selected.currentPage() != null ? selected.currentPage().path() : null;
		HelpTab tab = selected != null && selected.problem() != null ? selected : this.addTab(true);
		tab.open(HelpTab.Location.problem(problem, scopePath), List.of());
	}

	/**
	 * Three lines per problem: what sort it is, where it is, then the whole message, wrapped to
	 * the list's width; beside them VCell's issue icon, a red cross for an error and a yellow
	 * exclamation mark for a warning, as its issue tables use.
	 */
	private static final class ProblemRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (!(value instanceof HelpProblem problem)) return this;
			Icon icon = problem.isWarning() ? VCellIcons.issueWarningIcon : VCellIcons.issueErrorIcon;
			this.setIcon(icon);
			this.setIconTextGap(6);
			// the icon beside the first line, not the middle of three
			this.setVerticalAlignment(SwingConstants.TOP);
			this.setVerticalTextPosition(SwingConstants.TOP);
			int textWidth = Math.max(120, list.getWidth() - 20 - icon.getIconWidth() - this.getIconTextGap());
			String dim = VCellColors.toHtmlColor(isSelected ? this.getForeground() : VCellColors.getDimForeground());
			String message = problem.message().isEmpty() ? "" : Character.toUpperCase(problem.message().charAt(0)) + problem.message().substring(1);
			// a table width is in real pixels; Swing scales a CSS width, so text would run past the edge
			this.setText("<html><table width='" + textWidth + "' cellpadding='0' cellspacing='0' border='0'><tr><td>"
					+ "<b>" + HelpViewer.escapeHtml(problem.kind().label()) + "</b>"
					+ "<br><font color='" + dim + "'>" + HelpViewer.breakablePaths(HelpViewer.escapeHtml(problem.location())).replace(", line ", ", line&nbsp;") + "</font>"
					+ "</td></tr></table></html>");
			this.setToolTipText(null);
			this.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
			// the icon says error or warning only to the eye; say it to a screen reader too
			this.getAccessibleContext().setAccessibleName((problem.isWarning() ? "Warning: " : "Error: ") + problem.kind().label()
					+ ", " + problem.location() + ". " + message);
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
	static final class SafeHtmlEditorKit extends HTMLEditorKit {
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

		SafeHtmlEditorKit() {
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
			HelpTab tab = this.selectedTab();
			if (tab != null) tab.clearHighlights();
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
		this.tab().open(HelpTab.Location.page(hit.page(), null), HelpSearch.terms(this.searchField.getText()));
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

	HelpBook book() {
		return this.book;
	}

	HelpRenderer renderer() {
		return this.renderer;
	}

	/**
	 * A document for the page area, styled for the current theme.
	 *
	 * @param bodyHtml HTML from {@link HelpRenderer}, which has checked it
	 * @param images   the only images it may show, already decoded
	 */
	HTMLDocument newDocument(JEditorPane pane, String bodyHtml, java.util.Hashtable<java.net.URL, java.awt.Image> images) throws IOException, BadLocationException {
		HTMLEditorKit editorKit = (HTMLEditorKit) pane.getEditorKit();
		StyleSheet styleSheet = new StyleSheet();
		styleSheet.addStyleSheet(editorKit.getStyleSheet()); // never modify the kit's shared default
		styleSheet.addRule(this.themeCss());
		HTMLDocument document = new HTMLDocument(styleSheet);
		// no document base: the renderer gives images absolute URLs it has checked, and nothing
		// else on the page may load anything
		document.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
		// Swing's ImageView takes images from this cache when the document has one, and then
		// never fetches or decodes anything itself; an image missing from it is not shown
		document.putProperty("imageCache", images);
		editorKit.read(new StringReader("<html><body>" + bodyHtml + "</body></html>"), document, 0);
		return document;
	}

	/** The page's images decoded at their shown size, within the per-page budgets. */
	java.util.Hashtable<java.net.URL, java.awt.Image> decodeImages(HelpRenderer.RenderedPage rendered) {
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

	/** Page styling from the current look and feel, so dark themes get a dark page. */
	private String themeCss() {
		Color background = HelpViewer.uiColor("EditorPane.background", Color.WHITE);
		Color foreground = HelpViewer.uiColor("EditorPane.foreground", Color.BLACK);
		Color link = VCellColors.getReadableForeground(LIGHT_THEME_LINK);
		Color rule = VCellColors.blend(background, foreground, 0.25f);
		Color codeBackground = VCellColors.blend(background, foreground, 0.07f);
		return "body { background-color: " + VCellColors.toHtmlColor(background) + "; color: " + VCellColors.toHtmlColor(foreground)
				+ "; margin: 10px 16px 24px 16px; }"
				+ " p { margin-top: 0px; margin-bottom: 8px; }"
				+ " h1 { font-size: " + HelpViewer.points(VCellFonts.Category.DISPLAY) + "pt; margin-top: 4px; margin-bottom: 10px; }"
				+ " h2 { font-size: " + HelpViewer.points(VCellFonts.Category.TITLE) + "pt; margin-top: 14px; margin-bottom: 6px; }"
				+ " h3 { font-size: " + HelpViewer.points(VCellFonts.Category.SUBTITLE) + "pt; margin-top: 12px; margin-bottom: 4px; }"
				+ " a { color: " + VCellColors.toHtmlColor(link) + "; }"
				+ " ul, ol { margin-top: 0px; margin-bottom: 8px; margin-left-ltr: 22px; }"
				+ " li { margin-bottom: 3px; }"
				+ " ul ul, ol ol, ul ol, ol ul { margin-bottom: 0px; }"
				+ " code, pre { font-family: Monospaced; background-color: " + VCellColors.toHtmlColor(codeBackground) + "; }"
				+ " th, td { border: 1px solid " + VCellColors.toHtmlColor(rule) + "; padding: 3px 6px; }"
				+ " strong { font-weight: bold; } em { font-style: italic; }";
	}

	private static int points(VCellFonts.Category category) {
		return VCellFonts.getFont(category).getSize();
	}

	/** Re-render the open pages when the look and feel (light/dark theme) changes. */
	@Override
	public void updateUI() {
		super.updateUI();
		if (this.pageTabs == null) return; // called by the constructor, before there are tabs
		SwingUtilities.invokeLater(() -> {
			for (HelpTab tab : this.tabs()) tab.rerender();
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
