package cbit.vcell.desktop.copypaste;

import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.parser.Expression;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class PasteOperationTableModel<T> extends AbstractTableModel {
	List<PasteOperationTableModelRow<T>> modelDataStructure;

	/**
	 * Builds a <code>PasteOperationTableModel</code> from am list of <code>PasteOperationTableModelRow</code>s. Rows are directly processed rather than directly used.
	 * @param rowsToAdd the rows of data to add
	 */
	public PasteOperationTableModel(Collection<PasteOperationTableModelRow<T>> rowsToAdd) {
		super();
		this.modelDataStructure = new ArrayList<>();
		if (rowsToAdd.size() == 1){
			PasteOperationTableModelRow<T> row = rowsToAdd.iterator().next();
			this.modelDataStructure.add(new PasteOperationTableModelRow<>(row.getCategory(), row.getSpecifier(), row.getCurrentValue(), row.getProposedValue()));
		} else {
			for (PasteOperationTableModelRow<T> row : rowsToAdd){
				this.modelDataStructure.add(new PasteOperationTableModelRow<>(row.getCategory(), row.getSpecifier(), row.getCurrentValue(), row.getProposedValue(), new JCheckBox("", true)));
			}
		}
	}

	/**
	 * Builds a <code>PasteOperationTableModel</code> from lists of components. Equal amounts of each list must be provided.
	 * @param entryNames the entries that are being modified by the paste
	 * @param entrySpecifiers the list of aspects of each entry that is being modified
	 * @param currentValues the list of values each entry currently has
	 * @param proposedValues the list of values proposed to replace the current value of each entry
	 */
	public PasteOperationTableModel(List<String> entryNames, List<String> entrySpecifiers, List<T> currentValues, List<T> proposedValues){
		super();
		if (entryNames == null) throw new IllegalArgumentException("entryNames cannot be null");
		if (entrySpecifiers == null) throw new IllegalArgumentException("entrySpecifiers cannot be null");
		if (currentValues == null) throw new IllegalArgumentException("currentValues cannot be null");
		if (proposedValues == null) throw new IllegalArgumentException("proposedValues cannot be null");
		if (
				entryNames.size() != entrySpecifiers.size() ||
				currentValues.size() != proposedValues.size() ||
				entryNames.size() != proposedValues.size()
		) throw new IllegalArgumentException("arguments must have the same number of entries");

		this.modelDataStructure = new ArrayList<>();
		Iterator<String> categoriesIterator = entryNames.iterator();
		Iterator<String> specifierIterator = entrySpecifiers.iterator();
		Iterator<T> currentValuesIterator = currentValues.iterator();
		Iterator<T> proposedValuesIterator = proposedValues.iterator();
		if (entryNames.size() == 1){
			this.modelDataStructure.add(new PasteOperationTableModelRow<>(categoriesIterator.next(), specifierIterator.next(), currentValuesIterator.next(), proposedValuesIterator.next()));
		} else {
			while (categoriesIterator.hasNext()){
				this.modelDataStructure.add(new PasteOperationTableModelRow<>(categoriesIterator.next(), specifierIterator.next(), currentValuesIterator.next(), proposedValuesIterator.next(), new JCheckBox("", true)));
			}
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

	public List<PasteOperationTableModelRow<T>> getSelectedRows(){
		return this.modelDataStructure.stream().filter(PasteOperationTableModelRow::isSelected).toList();
	}
}
