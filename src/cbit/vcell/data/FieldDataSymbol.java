package cbit.vcell.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.units.VCUnitDefinition;

/**
 * This class implements a data symbol object which refers to general Field Data images
 * @author vasilescu
 *
 */
public class FieldDataSymbol extends DataSymbol {
	
	private final int dataSymbolType;
	private final int vFrapImageSubtype;
	private String dataSetName;			// dataset name / id
	private FieldFunctionArguments fieldFunctionArguments = null;
	private String expression = null;
//	private int imageIndex;				// which image in the dataset we refer to
	
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

	public FieldDataSymbol(String name, String dataSetName, DataContext dataContext, 
			VCUnitDefinition vcUnitDefinition, FieldFunctionArguments fieldFunctionArguments){
		super(name,dataContext,vcUnitDefinition);
		this.dataSetName = dataSetName;
		this.dataSymbolType = DataSymbolType.UNKNOWN;
		this.vFrapImageSubtype = VFrapImageSubtype.UNKNOWN;
		this.fieldFunctionArguments = fieldFunctionArguments;
	}
	public FieldDataSymbol(int dataSymbolType, String name, String dataSetName, DataContext dataContext, 
			VCUnitDefinition vcUnitDefinition, FieldFunctionArguments fieldFunctionArguments){
		super(name,dataContext,vcUnitDefinition);
		this.dataSetName = dataSetName;
		this.dataSymbolType = dataSymbolType;
		this.vFrapImageSubtype = VFrapImageSubtype.UNKNOWN;
		this.fieldFunctionArguments = fieldFunctionArguments;
	}
	public FieldDataSymbol(int dataSymbolType, int vFrapImageSubtype, String name, String dataSetName, DataContext dataContext, 
			VCUnitDefinition vcUnitDefinition, FieldFunctionArguments fieldFunctionArguments){
		super(name,dataContext,vcUnitDefinition);
		this.dataSetName = dataSetName;
		this.dataSymbolType = dataSymbolType;
		this.vFrapImageSubtype = vFrapImageSubtype;
		this.fieldFunctionArguments = fieldFunctionArguments;
	}
	
	public String getDatasetName() {
		return dataSetName;
	}
	public int getDataSymbolType() {
		if((dataSymbolType > DataSymbolType.UNKNOWN) && (dataSymbolType <= DataSymbolType.VFRAP_SYMBOL)) {
			return dataSymbolType;
		} else {
			return DataSymbolType.UNKNOWN;
		}
	}
	public int getDataSymbolSubtype() {
		if((vFrapImageSubtype > VFrapImageSubtype.UNKNOWN) && (vFrapImageSubtype <= VFrapImageSubtype.ROI)) {
			return vFrapImageSubtype;
		} else {
			return VFrapImageSubtype.UNKNOWN;
		}
	}
}
