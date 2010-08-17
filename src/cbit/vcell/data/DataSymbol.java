package cbit.vcell.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

/**
 * Creation date: (5/25/2010 11:12:01 AM)
 * @author dan
 * @version $Revision: 1.0 $
 */

public abstract class DataSymbol implements SymbolTableEntry {
	private String name = null;					// name of data symbol
	private final DataContext dataSymbols;		// list of data symbols where we belong
	private VCUnitDefinition vcUnitDefinition = null;
	private transient PropertyChangeSupport propertyChangeSupport = null;
	
	protected DataSymbol(String name, DataContext dataSymbols, VCUnitDefinition vcUnitDefinition) {
		this.name = name;
		this.dataSymbols = dataSymbols;
		this.vcUnitDefinition = vcUnitDefinition;
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String newName){
		String oldName = this.name;
		this.name = newName;
		firePropertyChange("name", oldName, newName);
	}
	
	public final double getConstantValue() throws ExpressionException {
		throw new ExpressionException("cannot get constant value from a data symbol");
	}

	public Expression getExpression() throws ExpressionException {
		return null;
	}

	public final int getIndex() {
		return -1;
	}

	public final NameScope getNameScope() {
		return dataSymbols.getNameScope();
	}

	public final VCUnitDefinition getUnitDefinition() {
		return vcUnitDefinition;
	}

	public final boolean isConstant() throws ExpressionException {
		return false;
	}
	

	private PropertyChangeSupport getPropertyChangeSupport(){
		if (propertyChangeSupport==null){
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}
	
	private void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}
}
