package cbit.vcell.field;

import org.vcell.util.document.KeyValue;

import cbit.util.Extent;
import cbit.util.ISize;
import cbit.util.Origin;
import cbit.vcell.simdata.DataIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2007 11:01:45 AM)
 * @author: Frank Morgan
 */
public class FieldDataFileOperationResults implements java.io.Serializable{
/**
 * FieldDataStorageResults constructor comment.
 */
	public static class FieldDataReferenceInfo{
		public static final String FIELDDATATYPENAME = "FieldData";
		public String referenceSourceType;
		public String referenceSourceName;
		public String applicationName;
		public String simulationName;
		public String refSourceVersionDate;
		public String[] funcNames;
		public KeyValue refSourceVersionKey;
	};

	
	public DataIdentifier[] dataIdentifierArr;
	public ISize iSize;
	public Origin origin;
	public Extent extent;
	public double[] times;
	public FieldDataFileOperationResults.FieldDataReferenceInfo[] dependantFunctionInfo;
	
public FieldDataFileOperationResults() {
	super();
}
}
