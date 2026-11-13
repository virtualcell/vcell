package cbit.vcell.client.desktop;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;

import cbit.vcell.client.ModelExplorerWindowManager;
import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.client.desktop.biomodel.BioModelsNetPanel;
import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.ModelExplorerDetailsPanel;
import cbit.vcell.desktop.ModelExplorerTableModel;
import cbit.vcell.desktop.ModelExplorerTree;
import cbit.vcell.desktop.ModelExplorerTree.Folder;
import cbit.vcell.desktop.ModelExplorerTree.Model;
import cbit.vcell.desktop.ModelExplorerTree.Owner;
import cbit.vcell.desktop.ModelExplorerTree.Publication;
import cbit.vcell.desktop.ModelExplorerTree.Section;
import cbit.vcell.desktop.ModelExplorerTreeRenderer;
import cbit.vcell.desktop.SearchOptions;
import cbit.vcell.geometry.GeometryInfo;

/**
 * The Model Explorer: along the top, a search and its filters (as the welcome window's); at the left, a tree of the
 * VCell database's models, geometries and the BioModels Database, in the folders VCell's database tree has always had
 * (see ModelExplorerTree); at the right, the details of what's selected in it: its model, document and publication
 * information. Double-click (or Enter) opens a model; right-click offers what the welcome window's lists do.
 * <br/>
 * Two models of the same kind selected together can be compared; and Compare With > Another Model (here or in the
 * welcome window) asks for the other model to be picked in the tree, with a bar above it saying so.
 */
@SuppressWarnings("serial")
public class ModelExplorerPanel extends JPanel {
	private static final Logger lg = LogManager.getLogger(ModelExplorerPanel.class);
	private static final String TREE = "tree", TABLE = "table", LOGGED_OUT = "loggedOut";
	private static final int EXPAND_ALL_UNDER = 400; // a search expands every folder down to its models when it finds fewer

	private final ModelExplorerWindowManager manager;
	private final DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("Model Explorer"));
	private final JTree tree = new JTree(this.treeModel);
	private final ModelExplorerTreeRenderer renderer = new ModelExplorerTreeRenderer();
	// the table view: the same models, one row each, sortable by any column
	private final ModelExplorerTableModel tableModel = new ModelExplorerTableModel();
	private final javax.swing.JTable table = new javax.swing.JTable(this.tableModel);
	private final ModelExplorerTableModel.Renderer tableRenderer = new ModelExplorerTableModel.Renderer();
	private final CardLayout viewCards = new CardLayout();
	private final JPanel views = new JPanel(this.viewCards); // the tree or the table
	private final JToggleButton treeViewButton = new JToggleButton("Tree");
	private final JToggleButton tableViewButton = new JToggleButton("Table");
	private boolean tableShown = false;
	private final ModelExplorerDetailsPanel details = new ModelExplorerDetailsPanel();
	private final WelcomePanel.SearchField searchField = new WelcomePanel.SearchField("Search models, geometries and the BioModels Database");
	private final Timer searchPause = new Timer(200, e -> this.rebuild());
	private final SearchOptionsPanel searchOptions = new SearchOptionsPanel(this.searchPause::restart);
	private final JToggleButton filtersButton = new JToggleButton();
	private final JPanel optionsHolder = new JPanel(new BorderLayout());
	private final JLabel status = new JLabel(" ");
	private final CardLayout cards = new CardLayout();
	private final JPanel body = new JPanel(this.cards);
	private final DatabaseListener databaseListener = new DatabaseListener() {
		@Override public void databaseDelete(DatabaseEvent event) { ModelExplorerPanel.this.rebuildLater(); }
		@Override public void databaseInsert(DatabaseEvent event) { ModelExplorerPanel.this.rebuildLater(); }
		@Override public void databaseRefresh(DatabaseEvent event) { ModelExplorerPanel.this.rebuildLater(); }
		@Override public void databaseUpdate(DatabaseEvent event) { ModelExplorerPanel.this.rebuildLater(); }
	};
	private DocumentManager documentManager = null;
	private List<BioModelsNetModelInfo> bioModelsDatabase = null; // read once, in the background
	private boolean bioModelsDatabaseRequested = false;
	private SwingWorker<DefaultMutableTreeNode, Void> building = null;
	private boolean rebuildAgain = false; // asked for while building: build once more after
	private VCDocumentInfo pendingTarget = null; // a model to show once the tree is built
	private boolean firstBuild = true;
	// comparing: two models selected, or one being picked to compare a model with (the bar above the tree says which)
	private final JPanel compareBar = new JPanel(new BorderLayout(12, 0));
	private final JLabel compareMessage = new JLabel();
	private final JButton compareButton = new JButton("Compare");
	private final JButton cancelCompareButton = new JButton("Cancel");
	private VCDocumentInfo comparing = null; // the model another is being picked for; null when not picking

	public ModelExplorerPanel(ModelExplorerWindowManager manager) {
		super(new BorderLayout(0, 8));
		this.manager = manager;
		this.setName("ModelExplorerPanel");
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.add(this.createTop(), BorderLayout.NORTH);
		this.add(this.createBody(), BorderLayout.CENTER);
		this.details.setOpener(manager::openModel);
		this.details.setImporter(manager::importModel);
		this.details.setSelector(this::selectModel);
		this.setDocumentManager(null);
	}

	/** The document manager of whoever is logged in (null for no one): the tree lists what it knows, and follows its changes. */
	public void setDocumentManager(DocumentManager documentManager) {
		if (documentManager == this.documentManager) return;
		if (null != this.documentManager) this.documentManager.removeDatabaseListener(this.databaseListener);
		this.documentManager = documentManager;
		if (null != documentManager) documentManager.addDatabaseListener(this.databaseListener);
		this.cards.show(this.body, null == documentManager ? LOGGED_OUT : TREE);
		if (null == documentManager) this.stopComparison();
		this.firstBuild = true;
		this.rebuild();
	}

	/**
	 * Selects a model in the tree (its version, if listed), and shows its details; a search or filters that hide it are
	 * cleared first. Null just shows the window as it was.
	 */
	public void showModel(VCDocumentInfo target) {
		if (null == target) return;
		this.pendingTarget = target;
		if (null == this.building) this.selectPendingTarget();
	}

	// ---- the parts

	// The search, the Filters button that shows its options under it, Refresh, and how many models are listed
	private JComponent createTop() {
		this.searchField.setName("ModelExplorerSearch");
		this.searchField.setToolTipText("Finds models by name, owner or description; BioModels Database models by name or ID");
		this.searchField.putClientProperty("JTextField.variant", "search");
		this.searchPause.setRepeats(false);
		this.searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { ModelExplorerPanel.this.searchPause.restart(); }
			@Override public void removeUpdate(DocumentEvent e) { ModelExplorerPanel.this.searchPause.restart(); }
			@Override public void changedUpdate(DocumentEvent e) { ModelExplorerPanel.this.searchPause.restart(); }
		});
		this.searchField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSearch");
		this.searchField.getActionMap().put("clearSearch", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModelExplorerPanel.this.searchField.setText("");
			}
		});
		JLabel searchLabel = new JLabel("Search");
		searchLabel.setLabelFor(this.searchField);

		this.filtersButton.setName("ModelExplorerFilters");
		this.filtersButton.addActionListener(e -> this.showOptions(this.filtersButton.isSelected()));
		JButton refresh = new JButton("Refresh");
		refresh.setName("ModelExplorerRefresh");
		refresh.setToolTipText("Lists the database's models again, as the server has them now");
		refresh.addActionListener(e -> this.refreshFromServer());
		this.status.setName("ModelExplorerStatus");
		this.status.setForeground(VCellColors.getDimForeground());
		VCellFonts.setCategory(this.status, VCellFonts.Category.CAPTION);

		// Tree or Table: the models in their folders, or one row each, to sort
		this.treeViewButton.setName("ModelExplorerTreeView");
		this.treeViewButton.setToolTipText("The models in their folders");
		this.treeViewButton.setSelected(true);
		this.treeViewButton.addActionListener(e -> this.showView(false));
		this.tableViewButton.setName("ModelExplorerTableView");
		this.tableViewButton.setToolTipText("The models in a table, one row each: click a column's heading to sort by it");
		this.tableViewButton.addActionListener(e -> this.showView(true));
		// not a ButtonGroup: Tab would reach only the one selected; showView keeps one of the two selected
		this.treeViewButton.putClientProperty("JButton.buttonType", "segmented");
		this.treeViewButton.putClientProperty("JButton.segmentPosition", "first");
		this.tableViewButton.putClientProperty("JButton.buttonType", "segmented");
		this.tableViewButton.putClientProperty("JButton.segmentPosition", "last");
		JPanel viewSwitch = new JPanel(new java.awt.GridLayout(1, 2, 0, 0));
		viewSwitch.setOpaque(false);
		viewSwitch.add(this.treeViewButton);
		viewSwitch.add(this.tableViewButton);

		JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 6, 0));
		buttons.setOpaque(false);
		buttons.add(this.status);
		buttons.add(viewSwitch);
		buttons.add(this.filtersButton);
		buttons.add(refresh);
		JPanel searchRow = new JPanel(new BorderLayout(8, 0));
		searchRow.setOpaque(false);
		searchRow.add(searchLabel, BorderLayout.WEST);
		searchRow.add(this.searchField, BorderLayout.CENTER);
		searchRow.add(buttons, BorderLayout.EAST);

		this.optionsHolder.setOpaque(false);
		this.optionsHolder.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")),
				BorderFactory.createEmptyBorder(8, 0, 8, 0)));
		this.searchOptions.setNote("BioModels Database models are found by their names and IDs only.");
		JPanel natural = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 0, 0)); // the options at their own width
		natural.setOpaque(false);
		natural.add(this.searchOptions);
		this.optionsHolder.add(natural, BorderLayout.CENTER);
		this.optionsHolder.setVisible(false);
		this.showFiltersChanged();

		JPanel top = new JPanel(new BorderLayout(0, 8));
		top.setOpaque(false);
		top.add(searchRow, BorderLayout.NORTH);
		top.add(this.optionsHolder, BorderLayout.CENTER);
		top.add(this.createCompareBar(), BorderLayout.SOUTH);
		return top;
	}

	// What's being compared, and the button that compares it; hidden unless comparing
	private JComponent createCompareBar() {
		this.compareBar.setName("ModelExplorerCompareBar");
		this.compareMessage.setName("ModelExplorerCompareMessage");
		this.compareButton.setName("ModelExplorerCompare");
		this.compareButton.addActionListener(e -> this.compareSelected());
		this.cancelCompareButton.setName("ModelExplorerCancelCompare");
		this.cancelCompareButton.setToolTipText("Stops picking a model to compare with (Esc)");
		this.cancelCompareButton.addActionListener(e -> this.stopComparison());
		JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 6, 0));
		buttons.setOpaque(false);
		buttons.add(this.compareButton);
		buttons.add(this.cancelCompareButton);
		this.compareBar.add(this.compareMessage, BorderLayout.CENTER);
		this.compareBar.add(buttons, BorderLayout.EAST);
		this.compareBar.setVisible(false);
		return this.compareBar;
	}

	// The tree at the left, the details at the right; a note in their place while no one is logged in
	private JComponent createBody() {
		this.tree.setName("ModelExplorerTree");
		this.tree.setRootVisible(false);
		this.tree.setShowsRootHandles(true);
		this.tree.setCellRenderer(this.renderer);
		this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION); // two, to compare
		this.tree.getAccessibleContext().setAccessibleName("Models, by folder");
		VCellFonts.followRowHeight(this.tree);
		ToolTipManager.sharedInstance().registerComponent(this.tree);
		this.tree.addTreeSelectionListener(e -> this.showSelected());
		this.tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2 || !SwingUtilities.isLeftMouseButton(e)) return;
				TreePath path = ModelExplorerPanel.this.tree.getPathForLocation(e.getX(), e.getY());
				if (null == path) return;
				ModelExplorerPanel.this.activate(ModelExplorerPanel.objectOf(path));
			}

			@Override public void mousePressed(MouseEvent e) { this.maybeShowMenu(e); }
			@Override public void mouseReleased(MouseEvent e) { this.maybeShowMenu(e); } // the popup trigger is on release on Windows

			private void maybeShowMenu(MouseEvent e) {
				if (!e.isPopupTrigger()) return;
				TreePath path = ModelExplorerPanel.this.tree.getPathForLocation(e.getX(), e.getY());
				if (null == path) return;
				JTree tree = ModelExplorerPanel.this.tree;
				if (tree.isPathSelected(path) && 2 == tree.getSelectionCount()) { // the two selected: compare them
					ModelExplorerPanel.this.showCompareMenu(tree, e.getX(), e.getY());
					return;
				}
				tree.setSelectionPath(path);
				ModelExplorerPanel.this.showMenu(ModelExplorerPanel.objectOf(path), tree, e.getX(), e.getY());
			}
		});
		this.tree.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openModel");
		this.tree.getActionMap().put("openModel", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath path = ModelExplorerPanel.this.focusedPath();
				if (null == path) return;
				if (ModelExplorerPanel.isOpenable(ModelExplorerPanel.objectOf(path))) ModelExplorerPanel.this.activate(ModelExplorerPanel.objectOf(path));
				else if (ModelExplorerPanel.this.tree.isExpanded(path)) ModelExplorerPanel.this.tree.collapsePath(path);
				else ModelExplorerPanel.this.tree.expandPath(path);
			}
		});

		this.tree.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "stopComparison");
		this.tree.getActionMap().put("stopComparison", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModelExplorerPanel.this.stopComparison();
			}
		});

		JScrollPane treeScroll = new JScrollPane(this.tree);
		this.views.add(treeScroll, TREE);
		this.views.add(this.createTable(), TABLE);
		this.views.setMinimumSize(new java.awt.Dimension(220, 100));
		this.details.setMinimumSize(new java.awt.Dimension(260, 100));
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.views, this.details);
		split.setName("ModelExplorerSplit");
		split.setResizeWeight(0.42);
		split.setContinuousLayout(true);
		split.setBorder(BorderFactory.createEmptyBorder());

		JLabel loggedOut = new JLabel("<html><div style='text-align:center'>Log in to explore the VCell database.<br>"
				+ "The welcome window and Account menu have the ways to log in.</div></html>", SwingConstants.CENTER);
		loggedOut.setName("ModelExplorerLoggedOut");
		loggedOut.setForeground(VCellColors.getDimForeground());
		this.body.add(split, TREE);
		this.body.add(loggedOut, LOGGED_OUT);
		return this.body;
	}

	// The table view: a row a model, sortable by any column (click its heading); the same selection, opening, menus
	// and comparing as the tree's
	private JComponent createTable() {
		this.table.setName("ModelExplorerTable");
		this.table.getAccessibleContext().setAccessibleName("Models, one row each");
		this.table.setAutoCreateRowSorter(true);
		for (Class<?> type : new Class<?>[] { Object.class, String.class, java.util.Date.class, Integer.class, Long.class }) {
			this.table.setDefaultRenderer(type, this.tableRenderer);
		}
		this.table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // two, to compare
		this.table.setFillsViewportHeight(true);
		this.table.setShowGrid(false);
		VCellFonts.followRowHeight(this.table);
		this.table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF); // every column its width, scrolling sideways
		int[] widths = { 240, 80, 150, 100, 150, 64, 110, 240, 90 };
		for (int i = 0; i < widths.length; i++) this.table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		// Tab and Shift-Tab leave the table, rather than move from cell to cell: it's for choosing a model, not editing
		this.table.setFocusTraversalKeys(java.awt.KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		this.table.setFocusTraversalKeys(java.awt.KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		this.table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && this.tableShown) this.showSelected();
		});
		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2 || !SwingUtilities.isLeftMouseButton(e)) return;
				int row = ModelExplorerPanel.this.table.rowAtPoint(e.getPoint());
				if (row >= 0) ModelExplorerPanel.this.activate(ModelExplorerPanel.this.tableModel.getRow(ModelExplorerPanel.this.table.convertRowIndexToModel(row)).model());
			}

			@Override public void mousePressed(MouseEvent e) { this.maybeShowMenu(e); }
			@Override public void mouseReleased(MouseEvent e) { this.maybeShowMenu(e); }

			private void maybeShowMenu(MouseEvent e) {
				if (!e.isPopupTrigger()) return;
				javax.swing.JTable table = ModelExplorerPanel.this.table;
				int row = table.rowAtPoint(e.getPoint());
				if (row < 0) return;
				if (table.isRowSelected(row) && 2 == table.getSelectedRowCount()) { // the two selected: compare them
					ModelExplorerPanel.this.showCompareMenu(table, e.getX(), e.getY());
					return;
				}
				table.setRowSelectionInterval(row, row);
				ModelExplorerPanel.this.showMenu(ModelExplorerPanel.this.tableModel.getRow(table.convertRowIndexToModel(row)).model(), table, e.getX(), e.getY());
			}
		});
		for (int condition : new int[] { JComponent.WHEN_FOCUSED, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT }) {
			this.table.getInputMap(condition).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openModel");
			this.table.getInputMap(condition).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "stopComparison");
		}
		this.table.getActionMap().put("openModel", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModelExplorerPanel.this.activate(ModelExplorerPanel.this.focusedObject());
			}
		});
		this.table.getActionMap().put("stopComparison", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModelExplorerPanel.this.stopComparison();
			}
		});
		return new JScrollPane(this.table);
	}

	// Shows the tree or the table, with what was selected in the one selected in the other
	private void showView(boolean table) {
		this.treeViewButton.setSelected(!table);
		this.tableViewButton.setSelected(table);
		if (table == this.tableShown) return;
		List<VCDocumentInfo> selected = this.selectedDocuments();
		this.tableShown = table;
		this.viewCards.show(this.views, table ? TABLE : TREE);
		if (table) {
			this.selectInTable(selected);
		} else {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.treeModel.getRoot();
			List<TreePath> paths = new ArrayList<>();
			for (VCDocumentInfo info : selected) {
				TreePath path = ModelExplorerTree.find(root, info);
				if (null != path) paths.add(path);
			}
			if (1 == paths.size()) this.select(paths.get(0));
			else if (!paths.isEmpty()) {
				for (TreePath path : paths) this.tree.expandPath(path.getParentPath());
				this.tree.setSelectionPaths(paths.toArray(new TreePath[0]));
			}
		}
		this.showSelected();
		(table ? this.table : this.tree).requestFocusInWindow();
	}

	// Selects these models' rows in the table (a version selects its model's row), scrolled to the first
	private void selectInTable(List<VCDocumentInfo> documents) {
		this.table.clearSelection();
		int first = -1;
		for (VCDocumentInfo document : documents) {
			for (int modelRow = 0; modelRow < this.tableModel.getRowCount(); modelRow++) {
				if (!ModelExplorerPanel.rowHolds(this.tableModel.getRow(modelRow), document)) continue;
				int row = this.table.convertRowIndexToView(modelRow);
				if (row < 0) break; // not shown
				this.table.addRowSelectionInterval(row, row);
				if (first < 0) first = row;
				break;
			}
		}
		if (first >= 0) this.table.scrollRectToVisible(this.table.getCellRect(first, 0, true));
	}

	private static boolean rowHolds(ModelExplorerTree.TableRow row, VCDocumentInfo document) {
		if (ModelExplorerPanel.versionKey(row.latest()).equals(ModelExplorerPanel.versionKey(document))) return true;
		for (VCDocumentInfo version : row.model().versions()) {
			if (ModelExplorerPanel.versionKey(version).equals(ModelExplorerPanel.versionKey(document))) return true;
		}
		return false;
	}

	// ---- building the tree

	private void rebuildLater() {
		SwingUtilities.invokeLater(this::rebuild);
	}

	// The tree again, off the event thread, for the search, its options and the database as they are now; keeping what was
	// expanded and selected
	private void rebuild() {
		this.searchPause.stop();
		this.showFiltersChanged();
		if (null != this.building) {
			this.rebuildAgain = true;
			return;
		}
		this.loadBioModelsDatabase();
		DocumentManager documentManager = this.documentManager;
		String searchText = this.searchField.getText();
		SearchOptions options = this.searchOptions.getOptions();
		List<BioModelsNetModelInfo> bioModelsDatabase = this.bioModelsDatabase;
		Set<String> expanded = this.firstBuild ? null : this.expandedKeys();
		String selected = this.keyOf(this.tree.getSelectionPath());
		this.status.setText("Listing...");
		this.building = new SwingWorker<>() {
			@Override
			protected DefaultMutableTreeNode doInBackground() {
				User user = null == documentManager ? null : documentManager.getUser();
				if (null == user) return new DefaultMutableTreeNode("Model Explorer");
				List<VCDocumentInfo> models = new ArrayList<>(List.of(documentManager.getBioModelInfos()));
				models.addAll(List.of(documentManager.getMathModelInfos()));
				List<VCDocumentInfo> geometries = new ArrayList<>(List.of(documentManager.getGeometryInfos()));
				return ModelExplorerTree.build(new ModelExplorerTree.Input(models, geometries, user, searchText, options, bioModelsDatabase, new Date()));
			}

			@Override
			protected void done() {
				ModelExplorerPanel.this.building = null;
				try {
					ModelExplorerPanel.this.showTree(this.get(), expanded, selected, !searchText.isBlank());
				} catch (InterruptedException | ExecutionException e) {
					lg.error("Could not list the models", e);
					ModelExplorerPanel.this.status.setText("Could not list the models");
				}
				if (ModelExplorerPanel.this.rebuildAgain) {
					ModelExplorerPanel.this.rebuildAgain = false;
					ModelExplorerPanel.this.rebuild();
				}
			}
		};
		this.building.execute();
	}

	private void showTree(DefaultMutableTreeNode root, Set<String> expanded, String selected, boolean searching) {
		List<VCDocumentInfo> tableSelected = this.tableShown ? this.selectedDocuments() : List.of();
		this.treeModel.setRoot(root);
		this.tableModel.setRows(ModelExplorerTree.tableRows(root)); // the table's rows, the tree's models
		if (this.tableShown && null == this.pendingTarget) this.selectInTable(tableSelected);
		int models = ModelExplorerTree.modelsUnder(root).size(); // each model once, however many folders list it
		this.status.setText(null == this.documentManager ? " " : String.format("%,d models and geometries", models));
		// at first, the top folders open, and Public Models' own; searching, every folder down to the models, if not too many
		if (null == expanded || searching) {
			for (int i = 0; i < root.getChildCount(); i++) {
				DefaultMutableTreeNode folder = (DefaultMutableTreeNode) root.getChildAt(i);
				this.tree.expandPath(new TreePath(folder.getPath()));
				if (folder.getUserObject() instanceof Folder about && Section.PUBLIC == about.getSection()) {
					for (int j = 0; j < folder.getChildCount(); j++) this.tree.expandPath(new TreePath(((DefaultMutableTreeNode) folder.getChildAt(j)).getPath()));
				}
			}
			if (searching && models < EXPAND_ALL_UNDER) this.expandDownToModels(root);
		}
		if (null != expanded) this.expandKeys(root, expanded);
		this.firstBuild = false;
		if (null != this.pendingTarget) {
			this.selectPendingTarget();
		} else if (null != selected) {
			TreePath path = this.findKey(root, selected);
			if (null != path) this.select(path);
			else this.details.show(null, null);
		} else {
			this.details.show(null, null);
		}
		if (this.tableShown) this.showSelected(); // the table's selection, not the tree's
	}

	private void expandDownToModels(DefaultMutableTreeNode node) {
		Object object = node.getUserObject();
		if (object instanceof Model || object instanceof VCDocumentInfo || object instanceof BioModelsNetModelInfo) return;
		if (object instanceof Folder about && Section.BIOMODELS_DATABASE == about.getSection() && node.getChildCount() > EXPAND_ALL_UNDER) return;
		if (null != node.getParent()) this.tree.expandPath(new TreePath(node.getPath()));
		for (int i = 0; i < node.getChildCount(); i++) this.expandDownToModels((DefaultMutableTreeNode) node.getChildAt(i));
	}

	// The BioModels Database's list, read once, in the background; the tree is built again with it
	private void loadBioModelsDatabase() {
		if (this.bioModelsDatabaseRequested) return;
		this.bioModelsDatabaseRequested = true;
		new SwingWorker<List<BioModelsNetModelInfo>, Void>() {
			@Override
			protected List<BioModelsNetModelInfo> doInBackground() throws Exception {
				return BioModelsNetPanel.readModels();
			}

			@Override
			protected void done() {
				try {
					ModelExplorerPanel.this.bioModelsDatabase = this.get();
				} catch (InterruptedException | ExecutionException e) {
					lg.error("Could not read the BioModels Database's list", e);
					ModelExplorerPanel.this.bioModelsDatabase = List.of();
				}
				ModelExplorerPanel.this.rebuild();
			}
		}.execute();
	}

	// The document manager reads the database's lists again; the tree follows its refresh event
	private void refreshFromServer() {
		DocumentManager documentManager = this.documentManager;
		if (null == documentManager) return;
		this.status.setText("Refreshing...");
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				// reads the lists again (only reads): what Refresh in VCell's database tree did
				if (documentManager instanceof cbit.vcell.clientdb.ClientDocumentManager client) client.initAllDatabaseInfos();
				return null;
			}

			@Override
			protected void done() {
				try {
					this.get();
				} catch (InterruptedException | ExecutionException e) {
					lg.error("Could not refresh the database's lists", e);
				}
				ModelExplorerPanel.this.rebuild();
			}
		}.execute();
	}

	// ---- the search options

	private void showOptions(boolean shown) {
		this.filtersButton.setSelected(shown);
		this.optionsHolder.setVisible(shown);
		this.showFiltersChanged();
		this.revalidate();
	}

	private void showFiltersChanged() {
		SearchOptions options = this.searchOptions.getOptions();
		int changed = options.changedCount();
		this.filtersButton.setToolTipText(SearchOptionsPanel.filtersToolTip(options)); // and which ones
		this.filtersButton.setText("Filters" + (changed > 0 ? " (" + changed + ")" : "") + (this.filtersButton.isSelected() ? " ▴" : " ▾"));
	}

	// ---- selection, opening

	// The selected row the details are of: the one last clicked, of those selected
	private TreePath focusedPath() {
		TreePath lead = this.tree.getLeadSelectionPath();
		return null != lead && this.tree.isPathSelected(lead) ? lead : this.tree.getSelectionPath();
	}

	private static Object objectOf(TreePath path) {
		return null == path ? null : ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
	}

	// What the details are of, in the view shown: the tree's node last clicked, or the table's model
	private Object focusedObject() {
		if (!this.tableShown) return ModelExplorerPanel.objectOf(this.focusedPath());
		int lead = this.table.getSelectionModel().getLeadSelectionIndex();
		int row = lead >= 0 && this.table.isRowSelected(lead) ? lead : this.table.getSelectedRow();
		return row < 0 ? null : this.tableModel.getRow(this.table.convertRowIndexToModel(row)).model();
	}

	// Everything selected in the view shown
	private List<Object> selectedObjects() {
		List<Object> objects = new ArrayList<>();
		if (this.tableShown) {
			for (int row : this.table.getSelectedRows()) objects.add(this.tableModel.getRow(this.table.convertRowIndexToModel(row)).model());
			return objects;
		}
		TreePath[] paths = this.tree.getSelectionPaths();
		if (null != paths) for (TreePath path : paths) objects.add(ModelExplorerPanel.objectOf(path));
		return objects;
	}

	private void showSelected() {
		if (this.tableShown) {
			this.details.show(this.focusedObject(), null);
		} else {
			TreePath path = this.focusedPath();
			DefaultMutableTreeNode node = null == path ? null : (DefaultMutableTreeNode) path.getLastPathComponent();
			this.details.show(null == node ? null : node.getUserObject(), node);
		}
		this.showComparison();
	}

	private static boolean isOpenable(Object object) {
		return null != ModelExplorerTree.documentOf(object) || object instanceof BioModelsNetModelInfo;
	}

	// Double-click or Enter on a model: opens it, or, while picking one to compare with, compares it
	private void activate(Object object) {
		if (!ModelExplorerPanel.isOpenable(object)) return;
		if (null != this.comparing) this.compareIfPicked(object);
		else this.openSelected();
	}

	private void openSelected() {
		Object object = this.focusedObject();
		if (null == object) return;
		if (object instanceof BioModelsNetModelInfo info) {
			if (info.isSupported()) this.manager.importModel(info);
			return;
		}
		VCDocumentInfo info = ModelExplorerTree.documentOf(object);
		if (null != info) this.manager.openModel(info);
	}

	private void showMenu(Object object, Component invoker, int x, int y) {
		VCDocumentInfo info = ModelExplorerTree.documentOf(object);
		if (null != info) {
			this.manager.createModelMenu(info).show(invoker, x, y);
			return;
		}
		if (object instanceof BioModelsNetModelInfo bioModelsDatabaseModel) {
			JPopupMenu menu = new JPopupMenu();
			javax.swing.JMenuItem importItem = new javax.swing.JMenuItem("Import");
			importItem.setEnabled(bioModelsDatabaseModel.isSupported());
			importItem.addActionListener(e -> this.manager.importModel(bioModelsDatabaseModel));
			menu.add(importItem);
			menu.show(invoker, x, y);
		}
	}

	// ---- comparing

	/**
	 * Asks for the model to compare this one with: selects it, marks it in the tree, and shows the bar that says to pick
	 * the other; Compare (or double-click, or Enter) on it compares the two.
	 */
	public void startComparison(VCDocumentInfo model) {
		if (null == model) return;
		this.comparing = model;
		this.renderer.setComparing(model);
		this.tableRenderer.setComparing(model);
		this.tree.repaint();
		this.table.repaint();
		this.showModel(model);
		this.showComparison();
	}

	private void stopComparison() {
		if (null == this.comparing) return;
		this.comparing = null;
		this.renderer.setComparing(null);
		this.tableRenderer.setComparing(null);
		this.tree.repaint();
		this.table.repaint();
		this.showComparison();
	}

	// The models selected, each once (a model and its own latest version are the same one), in the order selected
	private List<VCDocumentInfo> selectedDocuments() {
		List<VCDocumentInfo> documents = new ArrayList<>();
		Set<String> seen = new HashSet<>();
		for (Object object : this.selectedObjects()) {
			VCDocumentInfo info = ModelExplorerTree.documentOf(object);
			if (null != info && seen.add(ModelExplorerPanel.versionKey(info))) documents.add(info);
		}
		return documents;
	}

	// The two to compare, the older saved first (the baseline): the one being compared and the one picked, or the two
	// selected; null if there aren't two
	private VCDocumentInfo[] comparedPair() {
		List<VCDocumentInfo> selected = this.selectedDocuments();
		VCDocumentInfo first, second;
		if (null != this.comparing) {
			VCDocumentInfo picked = ModelExplorerTree.documentOf(this.focusedObject());
			if (null == picked) return null;
			first = this.comparing;
			second = picked;
		} else if (2 == selected.size() && 2 == this.selectedObjects().size()) {
			first = selected.get(0);
			second = selected.get(1);
		} else {
			return null;
		}
		java.util.Date firstSaved = first.getVersion().getDate(), secondSaved = second.getVersion().getDate();
		boolean swap = null != firstSaved && null != secondSaved && secondSaved.before(firstSaved);
		return swap ? new VCDocumentInfo[] { second, first } : new VCDocumentInfo[] { first, second };
	}

	// The bar above the tree: while picking, what's being compared and whether what's selected can be compared with it;
	// with two selected, which two; hidden otherwise
	private void showComparison() {
		boolean picking = null != this.comparing;
		VCDocumentInfo[] pair = this.comparedPair();
		boolean twoSelected = !picking && 2 == this.selectedObjects().size();
		this.compareBar.setVisible(picking || twoSelected);
		this.cancelCompareButton.setVisible(picking);
		if (!picking && !twoSelected) return;

		String problem;
		if (null == pair) problem = picking ? "Select the model to compare it with" : "Select two models to compare";
		else if (picking && ModelExplorerPanel.sameVersion(pair[0], pair[1])) problem = "Select the model to compare it with"; // still on the one being compared: not a mistake yet
		else problem = ModelExplorerTree.whyNotComparable(pair[0], pair[1]);
		this.compareButton.setEnabled(null == problem);
		this.compareButton.setToolTipText(null == problem ? "Shows what changed from the older saved to the newer" : problem);

		StringBuilder message = new StringBuilder("<html>");
		if (picking) {
			message.append("<b>Comparing \u201C").append(ModelExplorerPanel.escape(this.comparing.getVersion().getName())).append("\u201D</b> (")
					.append(ModelExplorerPanel.savedOn(this.comparing)).append(")<br>");
			if (null == problem) {
				VCDocumentInfo picked = ModelExplorerPanel.sameVersion(pair[0], this.comparing) ? pair[1] : pair[0];
				message.append("with \u201C").append(ModelExplorerPanel.escape(picked.getVersion().getName())).append("\u201D (")
						.append(ModelExplorerPanel.savedOn(picked)).append("): Compare, or double-click it");
			} else {
				message.append(ModelExplorerPanel.escape(problem)).append(", then Compare (or double-click it)");
			}
		} else if (null == problem) {
			message.append("<b>Compare \u201C").append(ModelExplorerPanel.escape(pair[0].getVersion().getName())).append("\u201D</b> (")
					.append(ModelExplorerPanel.savedOn(pair[0])).append(")<br><b>with \u201C").append(ModelExplorerPanel.escape(pair[1].getVersion().getName()))
					.append("\u201D</b> (").append(ModelExplorerPanel.savedOn(pair[1])).append(")");
		} else {
			message.append(ModelExplorerPanel.escape(problem));
		}
		this.compareMessage.setText(message.append("</html>").toString());

		// picking stands out: the selection's color around it, softly behind it
		java.awt.Color accent = UIManager.getColor("Component.accentColor");
		if (null == accent) accent = UIManager.getColor("Tree.selectionBackground");
		this.compareBar.setOpaque(picking);
		this.compareBar.setBackground(VCellColors.getSoftSelectionBackground());
		this.compareBar.setBorder(BorderFactory.createCompoundBorder(
				picking ? BorderFactory.createLineBorder(accent, 1, true) : BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.foreground")),
				BorderFactory.createEmptyBorder(6, picking ? 10 : 0, 6, picking ? 6 : 0)));
		this.compareBar.revalidate();
		this.compareBar.repaint();
	}

	private void compareSelected() {
		VCDocumentInfo[] pair = this.comparedPair();
		if (null == pair || null != ModelExplorerTree.whyNotComparable(pair[0], pair[1])) return;
		this.stopComparison();
		this.manager.compare(pair[0], pair[1]);
	}

	// Double-click or Enter on a model while picking: compares it, if it can be
	private void compareIfPicked(Object object) {
		if (null == ModelExplorerTree.documentOf(object)) return;
		this.compareSelected();
	}

	private void showCompareMenu(Component invoker, int x, int y) {
		JPopupMenu menu = new JPopupMenu();
		menu.setName("ModelExplorerCompareMenu");
		javax.swing.JMenuItem compare = new javax.swing.JMenuItem("Compare");
		compare.setName("CompareSelectedMenuItem");
		VCDocumentInfo[] pair = this.comparedPair();
		String problem = null == pair ? "Select two models to compare" : ModelExplorerTree.whyNotComparable(pair[0], pair[1]);
		compare.setEnabled(null == problem);
		compare.setToolTipText(problem);
		compare.addActionListener(e -> this.compareSelected());
		menu.add(compare);
		menu.show(invoker, x, y);
	}

	private static boolean sameVersion(VCDocumentInfo one, VCDocumentInfo other) {
		return one.getVersion().getVersionKey().equals(other.getVersion().getVersionKey());
	}

	private static String savedOn(VCDocumentInfo info) {
		java.util.Date saved = info.getVersion().getDate();
		return null == saved ? "not dated" : "saved " + java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT).format(saved);
	}

	private static String escape(String text) {
		return null == text ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	// A model listed in the details (under a publication): its node in the tree, under the same publication if it's there
	private void selectModel(VCDocumentInfo info) {
		TreePath under = this.tree.getSelectionPath();
		DefaultMutableTreeNode start = null == under ? (DefaultMutableTreeNode) this.treeModel.getRoot() : (DefaultMutableTreeNode) under.getLastPathComponent();
		TreePath path = ModelExplorerTree.find(start, info);
		if (null == path) path = ModelExplorerTree.find((DefaultMutableTreeNode) this.treeModel.getRoot(), info);
		if (null != path) this.select(path);
		if (this.tableShown) this.selectInTable(List.of(info));
	}

	private void selectPendingTarget() {
		VCDocumentInfo target = this.pendingTarget;
		if (null == target) return;
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.treeModel.getRoot();
		if (root.getChildCount() == 0 && null != this.documentManager) return; // not built yet: once it is
		TreePath path = ModelExplorerTree.find(root, target);
		boolean filtered = !this.searchField.getText().isBlank() || this.searchOptions.getOptions().changedCount() > 0;
		if (null == path && filtered) { // hidden by the search: clear it, and look again once built
			this.searchField.setText("");
			this.searchOptions.reset();
			this.rebuild();
			return;
		}
		this.pendingTarget = null;
		if (null != path) this.select(path);
		if (this.tableShown) this.selectInTable(List.of(target));
	}

	private void select(TreePath path) {
		this.tree.expandPath(path.getParentPath());
		this.tree.setSelectionPath(path);
		Rectangle bounds = this.tree.getPathBounds(path);
		if (null != bounds) {
			bounds.x = 0; // its row, from the left: not scrolled sideways to a long name's end
			this.tree.scrollRectToVisible(bounds);
		}
		this.tree.requestFocusInWindow();
	}

	// ---- keeping what's expanded across rebuilds: each node by a key that survives them

	private String keyOf(TreePath path) {
		if (null == path) return null;
		StringBuilder key = new StringBuilder();
		for (Object node : path.getPath()) key.append('/').append(ModelExplorerPanel.keyOf(((DefaultMutableTreeNode) node).getUserObject()));
		return key.toString();
	}

	private static String keyOf(Object object) {
		if (object instanceof Folder folder) return "folder:" + folder.getSection();
		if (object instanceof Owner owner) return "owner:" + owner.name();
		if (object instanceof Publication publication) {
			return "publication:" + (null != publication.info().getPublicationKey() ? publication.info().getPublicationKey() : publication.info().getTitle());
		}
		if (object instanceof Model model) {
			VCDocumentInfo latest = model.latest();
			Object branch = null != latest.getVersion().getBranchID() ? latest.getVersion().getBranchID() : latest.getVersion().getVersionKey();
			return "model:" + latest.getClass().getSimpleName() + ":" + branch;
		}
		if (object instanceof VCDocumentInfo info) return "version:" + ModelExplorerPanel.versionKey(info);
		if (object instanceof BioModelsNetModelInfo info) return "bmdb:" + info.getId();
		return "root";
	}

	private static String versionKey(VCDocumentInfo info) {
		String kind = info instanceof GeometryInfo ? "geometry" : info.getClass().getSimpleName();
		return kind + ":" + info.getVersion().getVersionKey();
	}

	private Set<String> expandedKeys() {
		Set<String> keys = new HashSet<>();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.treeModel.getRoot();
		Enumeration<TreePath> expanded = this.tree.getExpandedDescendants(new TreePath(root.getPath()));
		if (null == expanded) return keys;
		while (expanded.hasMoreElements()) keys.add(this.keyOf(expanded.nextElement()));
		return keys;
	}

	private void expandKeys(DefaultMutableTreeNode node, Set<String> keys) {
		TreePath path = new TreePath(node.getPath());
		if (null != node.getParent() && !keys.contains(this.keyOf(path))) return;
		this.tree.expandPath(path);
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			if (!child.isLeaf()) this.expandKeys(child, keys);
		}
	}

	private TreePath findKey(DefaultMutableTreeNode root, String key) {
		for (Enumeration<javax.swing.tree.TreeNode> nodes = root.preorderEnumeration(); nodes.hasMoreElements(); ) {
			TreePath path = new TreePath(((DefaultMutableTreeNode) nodes.nextElement()).getPath());
			if (key.equals(this.keyOf(path))) return path;
		}
		return null;
	}

	JTree getTree() {
		return this.tree;
	}
}
