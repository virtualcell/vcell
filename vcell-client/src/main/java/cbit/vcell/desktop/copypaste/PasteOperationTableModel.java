package cbit.vcell.desktop.copypaste;

import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.parser.Expression;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class PasteOperationTableModel<T> extends AbstractTableModel {
	private final List<PasteOperationTableModelRow<T>> modelDataStructure;
	private final TableColumnModel tableColumnModel;

	/**
	 * Builds a <code>PasteOperationTableModel</code> from am list of <code>PasteOperationTableModelRow</code>s. Rows are directly processed rather than directly used.
	 * @param rowsToAdd the rows of data to add
	 */
	public PasteOperationTableModel(Collection<PasteOperationTableModelRow<T>> rowsToAdd, final String[] columnNames) {
		super();
		this.modelDataStructure = new ArrayList<>();
		this.tableColumnModel = new DefaultTableColumnModel();
		boolean onlyOneChangeRequested = rowsToAdd.size() == 1;
		String[] confirmedColumnNames = Stream.concat((onlyOneChangeRequested ? Stream.of() : Stream.of("Perform Change")), Arrays.stream(columnNames)).toArray(String[]::new);

		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		for (int columnIndex = 0; columnIndex < confirmedColumnNames.length; columnIndex++){
			this.tableColumnModel.addColumn(new TableColumn(columnIndex));
			TableColumn tableColumn = this.tableColumnModel.getColumn(columnIndex);
			tableColumn.setHeaderValue(confirmedColumnNames[columnIndex]);
			tableColumn.setHeaderRenderer(cellRenderer);
			tableColumn.setCellRenderer(cellRenderer);
			tableColumn.setResizable(true);
		}
		
		if (onlyOneChangeRequested) {
			PasteOperationTableModelRow<T> row = rowsToAdd.iterator().next();
			this.modelDataStructure.add(new PasteOperationTableModelRow<>(row.getCategory(), row.getSpecifier(), row.getCurrentValue(), row.getProposedValue()));
			return;
		}
		for (PasteOperationTableModelRow<T> row : rowsToAdd){
			this.modelDataStructure.add(new PasteOperationTableModelRow<>(row.getCategory(), row.getSpecifier(), row.getCurrentValue(), row.getProposedValue(), true));
		}
	}

	public boolean isMultipleChoice(){
		return this.getRowCount() != 1;
	}


	/**
	 * Returns the number of rows in the model. A
	 * <code>JTable</code> uses this method to determine how many rows it
	 * should display.  This method should be quick, as it
	 * is called frequently during rendering.
	 *
	 * @return the number of rows in the model
	 * @see #getColumnCount
	 */
	@Override
	public int getRowCount() {
		return this.modelDataStructure.size();
	}

	/**
	 * Returns the number of columns in the model. A
	 * <code>JTable</code> uses this method to determine how many columns it
	 * should create and display by default.
	 *
	 * @return the number of columns in the model
	 * @see #getRowCount
	 */
	@Override
	public int getColumnCount() {
		return this.modelDataStructure.get(0).getNumberOfColumns();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return this.isMultipleChoice() && columnIndex == 0 && rowIndex >= 0 && rowIndex < this.getRowCount();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return this.isMultipleChoice() && columnIndex == 0 ? Boolean.class : String.class;
	}

	/**
	 * Returns the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 *
	 * @param rowIndex    the row whose value is to be queried
	 * @param columnIndex the column whose value is to be queried
	 * @return the value Object at the specified cell
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = this.modelDataStructure.get(rowIndex).getValueAt(columnIndex);
		if (value instanceof SpeciesContextSpec.SpeciesContextSpecParameter param) return param.getExpression().infix();
		if (value instanceof Expression expression) return expression.infix();
		return value;
	}

	@Override
	public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
		if (!this.isCellEditable(rowIndex, columnIndex)) return;
		if (!this.getColumnClass(columnIndex).isInstance(newValue)) return;
		this.modelDataStructure.get(rowIndex).setValueAt(columnIndex, newValue);
	}

	public List<PasteOperationTableModelRow<T>> getSelectedRows(){
		return this.modelDataStructure.stream().filter(PasteOperationTableModelRow::isSelected).toList();
	}

	public TableColumnModel getTableColumnModel(){
		return this.tableColumnModel;
	}

	public String getLongestColumnEntry(int columnIndex){
		String currentLongest = "";
		for (int rowIndex = 0; rowIndex < this.getRowCount(); rowIndex++){
			String nextContender = this.getValueAt(rowIndex, columnIndex).toString();
			if (nextContender.length() <= currentLongest.length()) continue;
			currentLongest = nextContender;
		}
		return currentLongest;
	}
}
