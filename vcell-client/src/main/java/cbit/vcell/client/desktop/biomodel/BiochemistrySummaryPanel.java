package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.gui.GuiUtils;

import cbit.gui.graph.GraphModel;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.BiochemistrySummary.Row;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.PathwayGraphModel;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.graph.ReactionCartoon;
import cbit.vcell.graph.ReactionCartoonFull;
import cbit.vcell.graph.ReactionCartoonMolecule;
import cbit.vcell.graph.structures.AllStructureSuite;

/**
 * The Summary of Biochemistry: on the left, view-only copies of its Reaction Diagram and Pathway Diagram; on the
 * right, what its physiology and pathway hold. A diagram opens itself when clicked, and a count its tab.
 * <p><b>Currently unused</b> (since the Biochemistry node was removed, Physiology being top-level): nothing opens the
 * Biochemistry Summary. Kept for its parts, which may be reused elsewhere.</p>
 */
@SuppressWarnings("serial")
public class BiochemistrySummaryPanel extends DocumentEditorSubPanel {
	private final Supplier<GraphModel> livePathwayDiagram;
	private final JPanel diagrams = new JPanel(new GridLayout(2, 1, 0, 12));
	private final RowsTableModel rowsModel = new RowsTableModel();
	private final JTable rowsTable = new JTable(this.rowsModel);
	private final JButton showButton = new JButton("Show");
	private BioModel bioModel = null;
	private DiagramPreview reactionPreview = null;
	private DiagramPreview pathwayPreview = null;

	/** @param livePathwayDiagram the Pathway Diagram's own graph model, whose layout the copy follows (it isn't saved) */
	public BiochemistrySummaryPanel(Supplier<GraphModel> livePathwayDiagram) {
		super();
		this.setName("BiochemistrySummaryPanel");
		this.livePathwayDiagram = livePathwayDiagram;
		this.setLayout(new BorderLayout());
		this.configureTable();

		JPanel right = new JPanel(new GridBagLayout());
		int y = 0;
		right.add(GuiUtils.createBoldJLabel("Contents"), BiochemistrySummaryPanel.row(y++, 0));
		GridBagConstraints tableCell = BiochemistrySummaryPanel.row(y++, 0);
		tableCell.fill = GridBagConstraints.BOTH;
		tableCell.weighty = 1;
		right.add(new JScrollPane(this.rowsTable), tableCell);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 0));
		buttons.add(this.showButton);
		buttons.add(BiochemistrySummaryPanel.hint("or double-click a row"));
		right.add(buttons, BiochemistrySummaryPanel.row(y++, 4));

		JPanel content = new JPanel(new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		GridBagConstraints leftCell = new GridBagConstraints();
		leftCell.gridx = 0;
		leftCell.gridy = 0;
		leftCell.fill = GridBagConstraints.BOTH;
		leftCell.weightx = 0.6;
		leftCell.weighty = 1;
		content.add(this.diagrams, leftCell);
		GridBagConstraints rightCell = new GridBagConstraints();
		rightCell.gridx = 1;
		rightCell.gridy = 0;
		rightCell.fill = GridBagConstraints.BOTH;
		rightCell.weightx = 0.4;
		rightCell.weighty = 1;
		rightCell.insets = new Insets(0, 24, 0, 0);
		content.add(right, rightCell);
		this.add(content, BorderLayout.CENTER);

		// brought up to date whenever it's shown: the diagrams and counts follow the model while it's edited elsewhere
		this.addHierarchyListener(e -> {
			if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && this.isShowing()) this.refresh();
		});
	}

	public void setBioModel(BioModel bioModel) {
		if (this.bioModel == bioModel) return;
		this.bioModel = bioModel;
		this.diagrams.removeAll();
		this.reactionPreview = null;
		this.pathwayPreview = null;
		if (bioModel != null) {
			// as the Reaction Diagram first shows a model: grouped by molecule when it has reaction rules
			boolean rules = !bioModel.getModel().getRbmModelContainer().getReactionRuleList().isEmpty();
			ReactionCartoon reactions = rules ? new ReactionCartoonMolecule() : new ReactionCartoonFull();
			reactions.setStructureSuite(new AllStructureSuite(() -> bioModel.getModel()));
			reactions.setModel(bioModel.getModel());
			this.reactionPreview = new DiagramPreview("ReactionDiagramPreview", "Reaction Diagram", reactions,
					() -> this.open(DocumentEditorTreeFolderClass.MODEL_NODE), "No structures yet", () -> false);
			PathwayGraphModel pathway = new PathwayGraphModel();
			pathway.setBioModel(bioModel);
			pathway.setPathwayModel(bioModel.getPathwayModel());
			this.pathwayPreview = new DiagramPreview("PathwayDiagramPreview", "Pathway Diagram", pathway,
					() -> this.open(DocumentEditorTreeFolderClass.PATHWAY_NODE),
					"No pathway imported yet: find one with Tools \u203a Pathway Tools \u203a Pathway Lookup",
					() -> bioModel.getPathwayModel().getBiopaxObjects().isEmpty());
			this.diagrams.add(this.reactionPreview);
			this.diagrams.add(this.pathwayPreview);
		}
		this.diagrams.revalidate();
		this.refresh();
	}

	/** Recounts and redraws, from the model as it is now. */
	public void refresh() {
		int chosenRow = this.rowsTable.getSelectedRow();
		this.rowsModel.setRows(BiochemistrySummary.of(this.bioModel));
		if (chosenRow >= 0 && chosenRow < this.rowsModel.getRowCount()) this.rowsTable.setRowSelectionInterval(chosenRow, chosenRow);
		this.updateShowButton();
		if (this.reactionPreview != null) this.reactionPreview.refresh(null); // its positions are the model's own
		if (this.pathwayPreview != null) this.pathwayPreview.refresh(this.livePathwayDiagram.get());
	}

	JTable getRowsTable() {
		return this.rowsTable;
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		// a summary: nothing in it is selected
	}

	// the tab where it's worked on, and its node in the tree
	private void open(DocumentEditorTreeFolderClass folderClass) {
		this.setActiveView(new ActiveView(null, folderClass, null));
	}

	private void configureTable() {
		this.rowsTable.setName("BiochemistrySummaryTable");
		VCellFonts.setCategory(this.rowsTable, VCellFonts.Category.BODY);
		VCellFonts.setCategory(this.rowsTable.getTableHeader(), VCellFonts.Category.BODY);
		VCellFonts.followRowHeight(this.rowsTable);
		this.rowsTable.getAccessibleContext().setAccessibleName("What the physiology and pathway hold");
		this.rowsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.rowsTable.setFillsViewportHeight(true);
		this.rowsTable.getTableHeader().setReorderingAllowed(false);
		this.rowsTable.getColumnModel().getColumn(0).setPreferredWidth(260);
		this.rowsTable.getColumnModel().getColumn(1).setPreferredWidth(70);
		this.rowsTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null); // Tab leaves the table,
		this.rowsTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null); // not just its cells
		this.rowsTable.setDefaultRenderer(Object.class, new RowRenderer());
		this.rowsTable.setDefaultRenderer(Integer.class, new RowRenderer());
		this.rowsTable.getSelectionModel().addListSelectionListener(e -> this.updateShowButton());
		this.rowsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) BiochemistrySummaryPanel.this.showChosen();
			}
		});
		this.rowsTable.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "showRow");
		this.rowsTable.getActionMap().put("showRow", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BiochemistrySummaryPanel.this.showChosen();
			}
		});
		this.showButton.setName("BiochemistrySummaryShowButton");
		this.showButton.setToolTipText("Open the chosen row's tab (or double-click it)");
		this.showButton.addActionListener(e -> this.showChosen());
	}

	private Row chosenRow() {
		int row = this.rowsTable.getSelectedRow();
		return row >= 0 && row < this.rowsModel.rows.size() ? this.rowsModel.rows.get(row) : null;
	}

	private void showChosen() {
		Row row = this.chosenRow();
		if (row != null && row.opens() != null) this.open(row.opens());
	}

	private void updateShowButton() {
		Row row = this.chosenRow();
		this.showButton.setEnabled(row != null && row.opens() != null);
	}

	private static final class RowsTableModel extends AbstractTableModel {
		private List<Row> rows = new ArrayList<>();

		void setRows(List<Row> rows) {
			this.rows = rows;
			this.fireTableDataChanged();
		}

		@Override public int getRowCount() { return this.rows.size(); }
		@Override public int getColumnCount() { return 2; }
		@Override public String getColumnName(int column) { return column == 0 ? "In the Model" : "Count"; }
		@Override public Class<?> getColumnClass(int column) { return column == 0 ? String.class : Integer.class; }

		@Override
		public Object getValueAt(int row, int column) {
			Row r = this.rows.get(row);
			return column == 0 ? r.label() : r.count();
		}
	}

	// counts to the right; headings bold, the rows under them indented
	private final class RowRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			boolean heading = BiochemistrySummaryPanel.this.rowsModel.rows.get(row).heading();
			this.setHorizontalAlignment(value instanceof Integer ? RIGHT : LEFT);
			this.setFont(heading ? table.getFont().deriveFont(Font.BOLD) : table.getFont());
			int indent = column == 0 && !heading ? 20 : 4;
			this.setBorder(BorderFactory.createCompoundBorder(this.getBorder(), BorderFactory.createEmptyBorder(0, indent, 0, 4)));
			return this;
		}
	}

	private static JLabel hint(String text) {
		return new JLabel(text) {
			@Override
			public void updateUI() { // the theme's secondary text color, again whenever the theme changes
				super.updateUI();
				this.setForeground(UIManager.getColor("Label.disabledForeground"));
			}
		};
	}

	private static GridBagConstraints row(int y, int top) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 1;
		gbc.insets = new Insets(top, 0, 4, 0);
		return gbc;
	}
}
