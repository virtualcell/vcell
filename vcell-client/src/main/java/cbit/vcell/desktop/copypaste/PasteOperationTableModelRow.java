package cbit.vcell.desktop.copypaste;

import cbit.vcell.parser.Expression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PasteOperationTableModelRow<T> {
	private static final Logger lg = LogManager.getLogger(PasteOperationTableModelRow.class);

	private Boolean shouldPerformChange; // intentionally using null-ability here as a "N/A" option.
	private String category;
	private String specifier;
	private final T currentValue;
	private final T proposedValue;
	private final List<Object> orderedValues;

	public PasteOperationTableModelRow(String category, String specifier, T currentValue, T proposedValue){
		this.category = category;
		this.specifier = specifier;
		this.currentValue = currentValue;
		this.proposedValue = proposedValue;
		this.shouldPerformChange = null;
		this.orderedValues = new ArrayList<>(List.of(category, specifier, currentValue, proposedValue));
	}
	public PasteOperationTableModelRow(String category, String specifier, T currentValue, T proposedValue, boolean shouldPerformChange){
		this(category, specifier, currentValue, proposedValue);
		this.shouldPerformChange = shouldPerformChange;
		this.orderedValues.add(0, this.shouldPerformChange);
	}

	public int getNumberOfColumns(){
		return null == this.shouldPerformChange ? 4 : 5;
	}

	public Object getValueAt(int columnIndex){
		if (columnIndex < 0) throw new IndexOutOfBoundsException();
		if (columnIndex >= this.orderedValues.size()) throw new IndexOutOfBoundsException();
		return this.orderedValues.get(columnIndex);
	}

	public void setValueAt(int columnIndex, Object value){
		if (columnIndex < 0) throw new IndexOutOfBoundsException();
		if (columnIndex >= this.orderedValues.size()) throw new IndexOutOfBoundsException();
		this.setValue(columnIndex, value);
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
		return null == this.shouldPerformChange || this.shouldPerformChange;
	}

	public String getDisplayString(){
		String current = this.getCurrentValue() instanceof Expression expr ? expr.infix() : this.getCurrentValue().toString();
		String proposed = this.getProposedValue() instanceof Expression expr ? expr.infix() : this.getCurrentValue().toString();
		return String.format("%s.%s (%s -> %s)", this.getCategory(), this.getSpecifier(), current, proposed);
	}

	private void setValue(int columnIndex, Object value){
		int attributeCode = columnIndex + (null == this.shouldPerformChange? 1 : 0);
		this.orderedValues.set(columnIndex, value);
		switch (attributeCode){
			case 0 -> {
				if (!(value instanceof Boolean shouldPerformChangeChange)) return;
				this.shouldPerformChange = shouldPerformChangeChange;
			}
			case 1 -> {
				if (!(value instanceof String categoryChange)) throw new IllegalArgumentException("value was expected to be of type String");
				this.category = categoryChange;
			}
			case 2 -> {
				if (!(value instanceof String specifierChange)) throw new IllegalArgumentException("value was expected to be of type String");
				this.specifier = specifierChange;
			}
			default -> throw new IllegalArgumentException("attribute code " + attributeCode + " is not supported at this time!");
		}
	}

}
