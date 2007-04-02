package cbit.vcell.field;

import cbit.util.Extent;
import cbit.util.ISize;
import cbit.util.Origin;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2007 11:01:45 AM)
 * @author: Frank Morgan
 */
public class FieldDataFileOperationResults implements java.io.Serializable{
/**
 * FieldDataStorageResults constructor comment.
 */
	
	public DataIdentifier[] dataIdentifierArr;
	public ISize iSize;
	public Origin origin;
	public Extent extent;
	public double[] times;
	
public FieldDataFileOperationResults() {
	super();
}
}
