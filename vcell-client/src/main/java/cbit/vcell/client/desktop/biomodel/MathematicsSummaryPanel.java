package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.MathematicsSummary.Group;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.model.ModelUnitSystem;
import org.vcell.util.gui.GuiUtils;

/**
 * The Summary of Mathematics &amp; Units: the model's unit system, the measured physical constants its equations use,
 * and how many parameters and functions it has and where. Each part leads to where it's worked on: the unit system to
 * its tab, the constants to Predefined Expressions, and a group to the Parameters and Functions table showing just it.
 */
public class MathematicsSummaryPanel extends JPanel {
	private static final String[] UNIT_LABELS = { "length", "area", "volume", "time", "volume species substance", "membrane species substance", "lumped reaction substance" };

	private final Supplier<String> unitSystemName;
	private final Consumer<Group> showGroup;
	private final JLabel unitSystemLabel = new JLabel();
	private final JLabel[] unitValues = new JLabel[MathematicsSummaryPanel.UNIT_LABELS.length];
	private final JButton changeUnitsButton = new JButton("Change...");
	private final GroupsTableModel groupsModel = new GroupsTableModel();
	private final JTable groupsTable = new JTable(this.groupsModel);
	private final JButton showGroupButton = new JButton("Show in Parameters and Functions");
	private final JPanel constantsPanel = new JPanel(new GridBagLayout());
	private final JButton showPredefinedButton = new JButton("All Predefined...");

	/**
	 * @param unitSystemName the name the unit system chooser shows for the model's units
	 * @param showUnitSystem opens the Model Unit System tab
	 * @param showGroup opens Parameters and Functions listing just that group
	 * @param showPredefined opens the Predefined Expressions tab
	 */
	public MathematicsSummaryPanel(Supplier<String> unitSystemName, Runnable showUnitSystem, Consumer<Group> showGroup, Runnable showPredefined) {
		super(new BorderLayout());
		this.setName("MathematicsSummaryPanel");
		this.unitSystemName = unitSystemName;
		this.showGroup = showGroup;

		this.changeUnitsButton.setName("SummaryChangeUnitsButton");
		this.changeUnitsButton.setToolTipText("Choose another unit system, in the Model Unit System tab");
		this.changeUnitsButton.addActionListener(e -> showUnitSystem.run());
		this.showGroupButton.setName("SummaryShowGroupButton");
		this.showGroupButton.setToolTipText("List the chosen group's parameters and functions (or double-click it)");
		this.showGroupButton.addActionListener(e -> this.showChosenGroup());
		this.showPredefinedButton.setName("SummaryShowPredefinedButton");
		this.showPredefinedButton.setToolTipText("Every constant and function VCell predefines, in the Predefined Expressions tab");
		this.showPredefinedButton.addActionListener(e -> showPredefined.run());
		this.constantsPanel.setName("SummaryConstantsPanel");
		this.configureTable();

		// the unit system and the physical constants on the left, the parameters and functions beside them
		JPanel left = new JPanel(new GridBagLayout());
		int y = 0;
		left.add(MathematicsSummaryPanel.heading("Unit System"), MathematicsSummaryPanel.row(y++, 0));
		left.add(this.buildUnitSystem(), MathematicsSummaryPanel.row(y++, 0));
		JPanel constantsHeading = new JPanel(new BorderLayout(12, 0));
		constantsHeading.add(MathematicsSummaryPanel.heading("Physical Constants"), BorderLayout.WEST);
		constantsHeading.add(this.showPredefinedButton, BorderLayout.EAST);
		GridBagConstraints constantsHeadingCell = MathematicsSummaryPanel.row(y++, 14);
		constantsHeadingCell.fill = GridBagConstraints.HORIZONTAL;
		left.add(constantsHeading, constantsHeadingCell);
		left.add(this.constantsPanel, MathematicsSummaryPanel.row(y++, 2));
		GridBagConstraints leftFiller = MathematicsSummaryPanel.row(y, 0);
		leftFiller.weighty = 1;
		left.add(new JPanel(), leftFiller);

		JPanel right = new JPanel(new GridBagLayout());
		y = 0;
		right.add(MathematicsSummaryPanel.heading("Parameters and Functions"), MathematicsSummaryPanel.row(y++, 0));
		JScrollPane tableScroll = new JScrollPane(this.groupsTable);
		GridBagConstraints tableCell = MathematicsSummaryPanel.row(y++, 0);
		tableCell.fill = GridBagConstraints.BOTH;
		tableCell.weighty = 1;
		right.add(tableScroll, tableCell);
		right.add(MathematicsSummaryPanel.flow(this.showGroupButton, MathematicsSummaryPanel.hint("or double-click a row")), MathematicsSummaryPanel.row(y++, 4));

		JPanel content = new JPanel(new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		GridBagConstraints leftCell = new GridBagConstraints();
		leftCell.gridx = 0;
		leftCell.gridy = 0;
		leftCell.fill = GridBagConstraints.VERTICAL;
		leftCell.anchor = GridBagConstraints.FIRST_LINE_START;
		leftCell.weighty = 1;
		content.add(left, leftCell);
		GridBagConstraints rightCell = new GridBagConstraints();
		rightCell.gridx = 1;
		rightCell.gridy = 0;
		rightCell.fill = GridBagConstraints.BOTH;
		rightCell.weightx = 1;
		rightCell.weighty = 1;
		rightCell.insets = new Insets(0, 24, 0, 0);
		content.add(right, rightCell);
		JScrollPane scroll = new JScrollPane(content);
		scroll.setBorder(null);
		this.add(scroll, BorderLayout.CENTER);
		this.refresh(null);
	}

	/** Recounts, for this model. */
	public void refresh(BioModel bioModel) {
		ModelUnitSystem units = bioModel == null ? null : bioModel.getModel().getUnitSystem();
		String name = units == null ? null : this.unitSystemName.get();
		this.unitSystemLabel.setText(name == null ? "" : name);
		String[] symbols = units == null ? new String[this.unitValues.length] : MathematicsSummaryPanel.unicodeSymbols(units);
		for (int i = 0; i < this.unitValues.length; i++) this.unitValues[i].setText(symbols[i] == null ? "" : symbols[i]);

		int chosenRow = this.groupsTable.getSelectedRow();
		this.groupsModel.setGroups(MathematicsSummary.of(bioModel));
		if (chosenRow >= 0 && chosenRow < this.groupsModel.groups.size()) this.groupsTable.setRowSelectionInterval(chosenRow, chosenRow);
		int rows = this.groupsModel.getRowCount();
		this.groupsTable.setPreferredScrollableViewportSize(new Dimension(420, Math.max(rows, 1) * this.groupsTable.getRowHeight()));
		this.updateShowGroupButton();

		this.showConstants(MathematicsSummary.measuredConstants(bioModel));
		this.revalidate();
	}

	JTable getGroupsTable() {
		return this.groupsTable;
	}

	JPanel getConstantsPanel() {
		return this.constantsPanel;
	}

	JLabel getUnitSystemLabel() {
		return this.unitSystemLabel;
	}

	// one row per constant: its name (what it is, on hover), symbol, value and unit
	private void showConstants(List<MathematicsSummary.Constant> constants) {
		this.constantsPanel.removeAll();
		for (int i = 0; i < constants.size(); i++) {
			MathematicsSummary.Constant constant = constants.get(i);
			JLabel name = new JLabel(constant.name());
			name.setToolTipText(constant.about());
			JLabel symbol = MathematicsSummaryPanel.hint(constant.symbol());
			JLabel value = new JLabel(MathematicsSummary.formatValue(constant.value()));
			value.setName("SummaryConstant_" + constant.symbol());
			value.setToolTipText(constant.symbol() + " = " + constant.value() + " " + constant.unit());
			JLabel unit = new JLabel(constant.unit());
			this.constantsPanel.add(name, MathematicsSummaryPanel.cell(0, i));
			this.constantsPanel.add(symbol, MathematicsSummaryPanel.cell(1, i));
			GridBagConstraints valueCell = MathematicsSummaryPanel.cell(2, i);
			valueCell.anchor = GridBagConstraints.LINE_END;
			this.constantsPanel.add(value, valueCell);
			this.constantsPanel.add(unit, MathematicsSummaryPanel.cell(3, i));
		}
		this.constantsPanel.revalidate();
		this.constantsPanel.repaint();
	}

	private JPanel buildUnitSystem() {
		JPanel panel = new JPanel(new GridBagLayout());
		this.unitSystemLabel.setName("SummaryUnitSystemName");
		this.unitSystemLabel.setFont(this.unitSystemLabel.getFont().deriveFont(Font.BOLD));
		GridBagConstraints nameCell = MathematicsSummaryPanel.cell(0, 0);
		nameCell.gridwidth = 2;
		panel.add(this.unitSystemLabel, nameCell);
		GridBagConstraints changeCell = MathematicsSummaryPanel.cell(2, 0);
		changeCell.gridwidth = 2;
		panel.add(this.changeUnitsButton, changeCell);
		// two columns: length, area, volume and time, then the substances
		for (int i = 0; i < MathematicsSummaryPanel.UNIT_LABELS.length; i++) {
			JLabel label = MathematicsSummaryPanel.hint(MathematicsSummaryPanel.UNIT_LABELS[i].replace(" species substance", " substance").replace("lumped reaction substance", "lumped substance"));
			label.setToolTipText(MathematicsSummaryPanel.UNIT_LABELS[i]);
			this.unitValues[i] = new JLabel();
			this.unitValues[i].setName("SummaryUnit_" + MathematicsSummaryPanel.UNIT_LABELS[i].replace(' ', '_'));
			label.setLabelFor(this.unitValues[i]);
			int column = i < 4 ? 0 : 2;
			int row = (i < 4 ? i : i - 4) + 1;
			GridBagConstraints labelCell = MathematicsSummaryPanel.cell(column, row);
			if (column > 0) labelCell.insets.left = 24;
			panel.add(label, labelCell);
			panel.add(this.unitValues[i], MathematicsSummaryPanel.cell(column + 1, row));
		}
		return panel;
	}

	private void configureTable() {
		this.groupsTable.setName("SummaryGroupsTable");
		// the body text's size, as VCell's other tables have, and rows to fit it (also when the text size changes)
		VCellFonts.setCategory(this.groupsTable, VCellFonts.Category.BODY);
		VCellFonts.setCategory(this.groupsTable.getTableHeader(), VCellFonts.Category.BODY);
		VCellFonts.followRowHeight(this.groupsTable);
		this.groupsTable.getAccessibleContext().setAccessibleName("Parameters and functions by where they're defined");
		this.groupsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.groupsTable.setFillsViewportHeight(true);
		this.groupsTable.getTableHeader().setReorderingAllowed(false);
		this.groupsTable.getColumnModel().getColumn(0).setPreferredWidth(240);
		for (int column = 1; column < this.groupsModel.getColumnCount(); column++) this.groupsTable.getColumnModel().getColumn(column).setPreferredWidth(80);
		this.groupsTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null); // Tab leaves the table,
		this.groupsTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null); // not just its cells
		this.groupsTable.setDefaultRenderer(Object.class, new TotalsRenderer());
		this.groupsTable.setDefaultRenderer(Integer.class, new TotalsRenderer());
		this.groupsTable.getSelectionModel().addListSelectionListener(e -> this.updateShowGroupButton());
		this.groupsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) MathematicsSummaryPanel.this.showChosenGroup();
			}
		});
		this.groupsTable.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "showGroup");
		this.groupsTable.getActionMap().put("showGroup", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MathematicsSummaryPanel.this.showChosenGroup();
			}
		});
	}

	private Group chosenGroup() {
		int row = this.groupsTable.getSelectedRow();
		return row >= 0 && row < this.groupsModel.groups.size() ? this.groupsModel.groups.get(row) : null; // not the Total row
	}

	private void showChosenGroup() {
		Group group = this.chosenGroup();
		if (group != null) this.showGroup.accept(group);
	}

	private void updateShowGroupButton() {
		this.showGroupButton.setEnabled(this.chosenGroup() != null);
	}

	private static String[] unicodeSymbols(ModelUnitSystem units) {
		return new String[] {
				units.getLengthUnit().getSymbolUnicode(),
				units.getAreaUnit().getSymbolUnicode(),
				units.getVolumeUnit().getSymbolUnicode(),
				units.getTimeUnit().getSymbolUnicode(),
				units.getVolumeSubstanceUnit().getSymbolUnicode(),
				units.getMembraneSubstanceUnit().getSymbolUnicode(),
				units.getLumpedReactionSubstanceUnit().getSymbolUnicode(),
		};
	}

	/** The groups, then a Total row. */
	private static final class GroupsTableModel extends AbstractTableModel {
		private static final String[] COLUMNS = { "Defined In", "Parameters", "Functions", "Total" };
		private List<Group> groups = new ArrayList<>();

		void setGroups(List<Group> groups) {
			this.groups = groups;
			this.fireTableDataChanged();
		}

		@Override public int getRowCount() { return this.groups.isEmpty() ? 0 : this.groups.size() + 1; }
		@Override public int getColumnCount() { return GroupsTableModel.COLUMNS.length; }
		@Override public String getColumnName(int column) { return GroupsTableModel.COLUMNS[column]; }
		@Override public Class<?> getColumnClass(int column) { return column == 0 ? String.class : Integer.class; }

		@Override
		public Object getValueAt(int row, int column) {
			if (row == this.groups.size()) { // the Total row: the headings', which hold the rest
				List<Group> headings = this.groups.stream().filter(Group::heading).toList();
				return switch (column) {
					case 0 -> "Total";
					case 1 -> headings.stream().mapToInt(Group::parameters).sum();
					case 2 -> headings.stream().mapToInt(Group::functions).sum();
					default -> headings.stream().mapToInt(Group::total).sum();
				};
			}
			Group group = this.groups.get(row);
			return switch (column) {
				case 0 -> group.label();
				case 1 -> group.parameters();
				case 2 -> group.functions();
				default -> group.total();
			};
		}
	}

	// numbers to the right; headings and the Total row bold, the groups under a heading indented
	private final class TotalsRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			List<Group> groups = MathematicsSummaryPanel.this.groupsModel.groups;
			boolean total = row == groups.size();
			boolean heading = total || groups.get(row).heading();
			this.setHorizontalAlignment(value instanceof Integer ? RIGHT : LEFT);
			this.setFont(heading ? table.getFont().deriveFont(Font.BOLD) : table.getFont());
			int indent = column == 0 && !heading ? 20 : 4;
			this.setBorder(BorderFactory.createCompoundBorder(this.getBorder(), BorderFactory.createEmptyBorder(0, indent, 0, 4)));
			return this;
		}
	}

	private static JLabel heading(String text) {
		return GuiUtils.createBoldJLabel(text);
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

	private static JPanel flow(JComponent... components) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 0));
		for (JComponent component : components) panel.add(component);
		return panel;
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

	private static GridBagConstraints cell(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(2, x == 0 ? 0 : 12, 2, 0);
		return gbc;
	}
}
