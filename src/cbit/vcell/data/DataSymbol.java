package cbit.vcell.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

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

public abstract class DataSymbol implements SymbolTableEntry,Matchable {
	private String dataSymbolName = null;					// name of data symbol
	private DataSymbolType dataSymbolType;
	private VCUnitDefinition vcUnitDefinition = null;

	private transient PropertyChangeSupport propertyChangeSupport = null;
	private final DataContext dataSymbols;		// list of data symbols where we belong
	
	protected DataSymbol(String dataSymbolName, DataSymbolType dataSymbolType, DataContext dataSymbols, VCUnitDefinition vcUnitDefinition) {
		this.dataSymbolName = dataSymbolName;
		this.dataSymbolType = dataSymbolType;
		this.dataSymbols = dataSymbols;
		this.vcUnitDefinition = vcUnitDefinition;
	}	
	public boolean compareEqual(Matchable obj) {
		DataSymbol dataSymbol = null;
		if (!(obj instanceof DataSymbol)){
			return false;
		}else{
			dataSymbol = (DataSymbol)obj;
		}
		
		if(!Compare.isEqualOrNull(dataSymbolName, dataSymbol.dataSymbolName)){
			return false;
		}
		if(dataSymbolType != dataSymbol.dataSymbolType){
			return false;
		}
		if(!Compare.isEqualOrNull(vcUnitDefinition, dataSymbol.vcUnitDefinition)){
			return false;
		}
		return true;
	}
	public String getName() {
		return dataSymbolName;
	}
	public void setName(String newName){
		String oldName = this.dataSymbolName;
		this.dataSymbolName = newName;
		firePropertyChange("name", oldName, newName);
	}
	public DataSymbolType getDataSymbolType() {
		return dataSymbolType;
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
	
	public enum DataSymbolType {
	    UNKNOWN					("Unknown",						"UNKNOWN"),
	    GENERIC_SYMBOL			("Generic",						"GENERIC"),
	    VFRAP_TIMEPOINT			("VFRAP time point", 			"VFRAP_TIMEPOINT"),
	    VFRAP_PREBLEACH_AVG		("VFRAP prebleach average",		"VFRAP_PREBLEACH_AVERAGE"),
	    VFRAP_FIRST_POSTBLEACH	("VFRAP first postbleach",		"VFRAP_FIRST_POSTBLEACH"),
	    VFRAP_ROI				("VFRAP ROI",					"VFRAP_ROI");

	    private final String displayName;
	    private final String databaseName; // DON'T Change
	    
	    DataSymbolType(String displayName, String databaseName) {
	        this.displayName = displayName;
	        this.databaseName = databaseName;
	    }
	    public String getDisplayName() {
	    	return displayName;
	    }
	    public String getDatabaseName() {
	    	return databaseName;
	    }
	    public static DataSymbolType fromDisplayName(String displayName) {
	        for (DataSymbolType dsType : DataSymbolType.values()){
	        	if (dsType.getDisplayName().equals(displayName)){
	        		return dsType;
	        	}
	        }
	        return null;
	    }
	    public static DataSymbolType fromDatabaseName(String databaseName) {
	        for (DataSymbolType dsType : DataSymbolType.values()){
	        	if (dsType.getDatabaseName().equals(databaseName)){
	        		return dsType;
	        	}
	        }
	        return null;
	    }
	}
}
