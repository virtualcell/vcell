package cbit.vcell.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.ExternalDataIdentifier;

import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

/**
 * This class implements a data symbol object which refers to general Field Data images
 * @author vasilescu
 *
 */
public class FieldDataSymbol extends DataSymbol {

	private ExternalDataIdentifier dataSetID;	// ID of the field database
	private String fieldItemName;		// name of a timepoint in the field database (may be different from data symbol name!)
	private String fieldItemType;
	private double fieldItemTime;
	
	public ExternalDataIdentifier getExternalDataIdentifier() {
		return dataSetID;
	}
	public String getFieldItemName() {
		return fieldItemName;
	}
	public String getFieldItemType() {
		return fieldItemType;
	}
	public double getFieldItemTime() {
		return fieldItemTime;
	}
	
/*	
	private String dataSetName;				// dataset name / id
	private FieldFunctionArguments fieldFunctionArguments = null;
	private String expression = null;
	
	public class DataSymbolType {
		public static final int UNKNOWN = 0;
		public static final int GENERIC_SYMBOL = 1;
		public static final int VFRAP_SYMBOL = 2;
	}
	public class VFrapImageSubtype {	// applies only to vFrap field data
		public static final int UNKNOWN = 0;
		public static final int TIMEPOINT = 1;
		public static final int PREBLEACH_AVERAGE = 2;
		public static final int FIRST_POSTBLEACH = 3;
		public static final int ROI = 4;
	}
	
	public FieldFunctionArguments getFieldFunctionArguments() {
		return fieldFunctionArguments;
	}

	public void setFieldFunctionArguments(
			FieldFunctionArguments fieldFunctionArguments) {
		this.fieldFunctionArguments = fieldFunctionArguments;
	}
*/
	public FieldDataSymbol(String dataSymbolName, DataSymbolType dataSymbolType, 
			DataContext dataContext, VCUnitDefinition vcUnitDefinition){
		super(dataSymbolName, dataSymbolType, dataContext, vcUnitDefinition);
		this.dataSetID = dataSetID;
		this.fieldItemName = fieldItemName;
		this.fieldItemType = fieldItemType;
		this.fieldItemTime = fieldItemTime;
	}
	public FieldDataSymbol(String dataSymbolName, 
			DataContext dataContext, VCUnitDefinition vcUnitDefinition,
			ExternalDataIdentifier dataSetID, String fieldItemName, 
			String fieldItemType, double fieldItemTime){
		super(dataSymbolName, DataSymbolType.UNKNOWN, dataContext, vcUnitDefinition);
		this.dataSetID = dataSetID;
		this.fieldItemName = fieldItemName;
		this.fieldItemType = fieldItemType;
		this.fieldItemTime = fieldItemTime;
	}
	public FieldDataSymbol(String dataSymbolName, DataSymbolType dataSymbolType, 
			DataContext dataContext, VCUnitDefinition vcUnitDefinition,
			ExternalDataIdentifier dataSetID, String fieldItemName, 
			String fieldItemType, double fieldItemTime){
		super(dataSymbolName, dataSymbolType, dataContext, vcUnitDefinition);
		this.dataSetID = dataSetID;
		this.fieldItemName = fieldItemName;
		this.fieldItemType = fieldItemType;
		this.fieldItemTime = fieldItemTime;
	}
	public boolean compareEqual(Matchable obj) {
		FieldDataSymbol fieldDataSymbol = null;
		if (!(obj instanceof FieldDataSymbol)){
			return false;
		}else{
			fieldDataSymbol = (FieldDataSymbol)obj;
		}

		if(!super.compareEqual(obj)){
			return false;
		}
		if(!Compare.isEqualOrNull(dataSetID, fieldDataSymbol.dataSetID)){
			return false;
		}
		if(!Compare.isEqualOrNull(fieldItemName, fieldDataSymbol.fieldItemName)){
			return false;
		}
		if(!Compare.isEqualOrNull(fieldItemType, fieldDataSymbol.fieldItemType)){
			return false;
		}
		if(fieldItemTime != fieldDataSymbol.fieldItemTime){
			return false;
		}
		
		return true;
	}
}
