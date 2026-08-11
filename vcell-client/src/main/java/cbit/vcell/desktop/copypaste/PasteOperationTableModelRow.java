package cbit.vcell.desktop.copypaste;

import cbit.vcell.parser.Expression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PasteOperationTableModelRow<T> {
	private static final Logger lg = LogManager.getLogger(PasteOperationTableModelRow.class);

	private JCheckBox selectionCheckboxReference;
	private final String category;
	private final String specifier;
	private final T currentValue;
	private final T proposedValue;
	private final List<Object> orderedValues;

	public PasteOperationTableModelRow(String category, String specifier, T currentValue, T proposedValue){
		this.category = category;
		this.specifier = specifier;
		this.currentValue = currentValue;
		this.proposedValue = proposedValue;
		this.selectionCheckboxReference = null;
		this.orderedValues = new ArrayList<>(List.of(category, specifier, currentValue, proposedValue));
	}
	public PasteOperationTableModelRow(String category, String specifier, T currentValue, T proposedValue, JCheckBox selectionCheckboxReference){
		this(category, specifier, currentValue, proposedValue);
		if (null == selectionCheckboxReference) lg.warn("null was directly provided to selectionCheckboxReference");
		else this.selectionCheckboxReference = selectionCheckboxReference;
		this.orderedValues.add(0, selectionCheckboxReference);
	}

	public int getNumberOfColumns(){
		return null == this.selectionCheckboxReference ? 4 : 5;
	}

	public Object getValueAt(int column){
		if (column < 0) throw new IndexOutOfBoundsException();
		if (column >= this.orderedValues.size()) throw new IndexOutOfBoundsException();
		return this.orderedValues.get(column);
	}

	public String getCategory(){
		return this.category;
	}

	public String getSpecifier(){
		return this.specifier;
	}

	public T getCurrentValue(){
		return this.currentValue;
	}

	public T getProposedValue(){
		return this.proposedValue;
	}

	public boolean isSelected(){
		return null == this.selectionCheckboxReference || this.selectionCheckboxReference.isSelected();
	}

	public String getDisplayString(){
		String current = this.getCurrentValue() instanceof Expression expr ? expr.infix() : this.getCurrentValue().toString();
		String proposed = this.getProposedValue() instanceof Expression expr ? expr.infix() : this.getCurrentValue().toString();
		return String.format("%s.%s (%s -> %s)", this.getCategory(), this.getSpecifier(), current, proposed);
	}

}
