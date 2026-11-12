package org.vcell.util.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Arranges a table's columns: which show, and in what order. Each column is a row here, ticked to show it, with
 * buttons to move it up (left) or down (right); Reset brings back the table's default arrangement. Dragging a column's
 * header in the table is the same as moving it here. Columns are known by their place in the table's model, so the
 * arrangement survives the table making its columns anew.
 */
@SuppressWarnings("serial")
public class TableColumnsPanel extends JPanel {
	private final JTable table;
	private final Map<Integer, TableColumn> columns = new HashMap<>(); // by model index
	private final List<Integer> defaultOrder;
	private final List<Integer> order = new ArrayList<>(); // every column, shown or not, left to right
	private final Set<Integer> hidden = new HashSet<>();
	private final JPanel rows = new JPanel(new GridBagLayout());
	private boolean arranging = false; // while this panel rearranges the table, its own column events are ignored

	/**
	 * @param defaultOrder the model indices of the columns, left to right, as the table first shows them (and Reset
	 *        brings back); columns it leaves out come after, in the model's order
	 */
	public TableColumnsPanel(JTable table, List<Integer> defaultOrder) {
		super(new BorderLayout(0, 6));
		this.table = table;
		this.setName("TableColumnsPanel");
		this.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
		this.captureColumns();
		List<Integer> full = new ArrayList<>();
		for (Integer modelIndex : defaultOrder) {
			if (this.columns.containsKey(modelIndex) && !full.contains(modelIndex)) full.add(modelIndex);
		}
		for (int modelIndex = 0; modelIndex < table.getModel().getColumnCount(); modelIndex++) {
			if (!full.contains(modelIndex)) full.add(modelIndex);
		}
		this.defaultOrder = full;
		this.order.addAll(full);

		JLabel title = GuiUtils.createBoldJLabel("Columns");
		JButton reset = new JButton("Reset");
		reset.setName("TableColumnsReset");
		reset.setToolTipText("Show every column, in the table's own order");
		reset.addActionListener(e -> this.reset());
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
		bottom.add(reset);
		this.add(title, BorderLayout.NORTH);
		this.add(this.rows, BorderLayout.CENTER);
		this.add(bottom, BorderLayout.SOUTH);

		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			@Override public void columnMoved(TableColumnModelEvent e) {
				if (e.getFromIndex() != e.getToIndex()) TableColumnsPanel.this.followTable();
			}
			@Override public void columnAdded(TableColumnModelEvent e) {}
			@Override public void columnRemoved(TableColumnModelEvent e) {}
			@Override public void columnMarginChanged(ChangeEvent e) {}
			@Override public void columnSelectionChanged(ListSelectionEvent e) {}
		});
		table.getModel().addTableModelListener(e -> {
			if (e.getFirstRow() != TableModelEvent.HEADER_ROW) return;
			// the table makes its columns anew (listeners hear of it newest first, so after this one): arrange them then
			javax.swing.SwingUtilities.invokeLater(() -> {
				this.captureColumns();
				this.apply();
			});
		});
		this.apply();
	}

	/** The model indices of the shown columns, left to right. */
	public List<Integer> getShownOrder() {
		List<Integer> shown = new ArrayList<>();
		for (Integer modelIndex : this.order) {
			if (!this.hidden.contains(modelIndex)) shown.add(modelIndex);
		}
		return shown;
	}

	/** Shows or hides a column (one always stays shown). */
	public void setShown(int modelIndex, boolean shown) {
		if (!shown && this.getShownOrder().size() <= 1) return;
		if (shown) this.hidden.remove(modelIndex);
		else this.hidden.add(modelIndex);
		this.apply();
	}

	/** Moves a column one place left (-1) or right (+1), among all of them. */
	public void move(int modelIndex, int by) {
		int from = this.order.indexOf(modelIndex);
		int to = from + by;
		if (from < 0 || to < 0 || to >= this.order.size()) return;
		this.order.remove(from);
		this.order.add(to, modelIndex);
		this.apply();
	}

	/** Every column shown, in the default order. */
	public void reset() {
		this.order.clear();
		this.order.addAll(this.defaultOrder);
		this.hidden.clear();
		this.apply();
	}

	private void captureColumns() {
		TableColumnModel model = this.table.getColumnModel();
		for (int i = 0; i < model.getColumnCount(); i++) {
			TableColumn column = model.getColumn(i);
			this.columns.put(column.getModelIndex(), column);
		}
	}

	// the table's columns as this panel has them: the shown ones, in order
	private void apply() {
		this.arranging = true;
		try {
			TableColumnModel model = this.table.getColumnModel();
			while (model.getColumnCount() > 0) model.removeColumn(model.getColumn(0));
			for (Integer modelIndex : this.getShownOrder()) {
				TableColumn column = this.columns.get(modelIndex);
				if (column != null) model.addColumn(column);
			}
		} finally {
			this.arranging = false;
		}
		this.buildRows();
		this.table.getTableHeader().repaint();
	}

	// a header dragged in the table: the shown columns take its order, the hidden ones keep their places among them
	private void followTable() {
		if (this.arranging) return;
		TableColumnModel model = this.table.getColumnModel();
		List<Integer> shown = new ArrayList<>();
		for (int i = 0; i < model.getColumnCount(); i++) shown.add(model.getColumn(i).getModelIndex());
		// each hidden column stays just after the shown column it followed (or first, if none did)
		List<Integer> merged = new ArrayList<>(shown);
		Integer anchor = null;
		List<Integer> placedAtFront = new ArrayList<>();
		Map<Integer, List<Integer>> after = new HashMap<>();
		for (Integer modelIndex : this.order) {
			if (!this.hidden.contains(modelIndex)) {
				anchor = modelIndex;
			} else if (anchor == null) {
				placedAtFront.add(modelIndex);
			} else {
				after.computeIfAbsent(anchor, k -> new ArrayList<>()).add(modelIndex);
			}
		}
		for (Map.Entry<Integer, List<Integer>> entry : after.entrySet()) {
			int at = merged.indexOf(entry.getKey());
			if (at < 0) merged.addAll(entry.getValue());
			else merged.addAll(at + 1, entry.getValue());
		}
		merged.addAll(0, placedAtFront);
		this.order.clear();
		this.order.addAll(merged);
		this.buildRows();
	}

	private void buildRows() {
		this.rows.removeAll();
		boolean lastShown = this.getShownOrder().size() <= 1;
		for (int i = 0; i < this.order.size(); i++) {
			int modelIndex = this.order.get(i);
			String name = this.table.getModel().getColumnName(modelIndex);
			boolean shown = !this.hidden.contains(modelIndex);
			JCheckBox check = new JCheckBox(name, shown);
			check.setName("TableColumnShown_" + modelIndex);
			check.setToolTipText(shown ? "Hide the " + name + " column" : "Show the " + name + " column");
			check.setEnabled(!(shown && lastShown)); // one column always shows
			check.addActionListener(e -> this.setShown(modelIndex, check.isSelected()));
			JButton up = TableColumnsPanel.arrow("▲", "Move " + name + " left", "TableColumnUp_" + modelIndex, i > 0, () -> this.move(modelIndex, -1));
			JButton down = TableColumnsPanel.arrow("▼", "Move " + name + " right", "TableColumnDown_" + modelIndex, i < this.order.size() - 1, () -> this.move(modelIndex, 1));
			GridBagConstraints checkCell = new GridBagConstraints();
			checkCell.gridx = 0;
			checkCell.gridy = i;
			checkCell.weightx = 1;
			checkCell.anchor = GridBagConstraints.LINE_START;
			this.rows.add(check, checkCell);
			GridBagConstraints upCell = new GridBagConstraints();
			upCell.gridx = 1;
			upCell.gridy = i;
			upCell.insets = new Insets(0, 4, 0, 0);
			this.rows.add(up, upCell);
			GridBagConstraints downCell = new GridBagConstraints();
			downCell.gridx = 2;
			downCell.gridy = i;
			this.rows.add(down, downCell);
		}
		GridBagConstraints filler = new GridBagConstraints();
		filler.gridy = this.order.size();
		filler.weighty = 1;
		this.rows.add(new JPanel(), filler);
		this.rows.revalidate();
		this.rows.repaint();
	}

	private static JButton arrow(String text, String tip, String name, boolean enabled, Runnable action) {
		JButton button = new JButton(text);
		button.setName(name);
		button.setToolTipText(tip);
		button.getAccessibleContext().setAccessibleName(tip);
		button.putClientProperty("JButton.buttonType", "square"); // small on macOS
		button.setMargin(new Insets(0, 2, 0, 2));
		button.setEnabled(enabled);
		button.addActionListener(e -> action.run());
		return button;
	}
}
