package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
import cbit.vcell.model.*;
import cbit.vcell.server.*;
/**
 * This type was created in VisualAge.
 */
public class StructTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_struct";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field strName		= new Field("strName",		"varchar(255)",	"NOT NULL");
	public final Field structType	= new Field("structType",	"varchar(10)",	"NOT NULL");
	public final Field parentRef 	= new Field("parentRef",	"integer",		StructTable.REF_TYPE);
	public final Field cellTypeRef 	= new Field("cellTypeRef",	"integer",  	CellTypeTable.REF_TYPE);
	public final Field memVoltName	= new Field("memVoltName",	"varchar(64)",	"");

	private final Field fields[] = {strName,structType,parentRef,cellTypeRef,memVoltName};
	
	private static final String STRUCTTYPE_FEATURE = "feature";
	private static final String STRUCTTYPE_MEMBRANE = "membrane";
	private static final String STRUCTTYPE_CONTOUR = "contour";

	public static final StructTable table = new StructTable();

/**
 * ModelTable constructor comment.
 */
private StructTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, Structure structure, KeyValue parentKey, KeyValue cellTypeKey) throws DataAccessException {

	int defaultCharge = 0;

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+structure.getName()+"',");
	buffer.append("'"+getStructType(structure)+"',");
	buffer.append(parentKey+",");  // structRef
	buffer.append(cellTypeKey+",");  // cellTypeRef
	if (structure instanceof Membrane){
		buffer.append("'"+((Membrane)structure).getMembraneVoltage().getName()+"'"+")");
	}else{
		buffer.append("null"+")");
	}

	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param structure cbit.vcell.model.Structure
 */
private String getStructType(Structure structure) throws DataAccessException {
	if (structure instanceof Feature){
		return STRUCTTYPE_FEATURE;
	}else if (structure instanceof Membrane){
		return STRUCTTYPE_MEMBRANE;
//	}else if (structure instanceof Contour){
//		return STRUCTTYPE_CONTOUR;
	}else{
		throw new DataAccessException("unsupported structure type "+structure);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 */
public Structure getStructure(java.sql.ResultSet rset, SessionLog log, KeyValue structKey) throws java.sql.SQLException, DataAccessException {
	
	//KeyValue key = new KeyValue(rset.getBigDecimal(StructTable.id.toString(),0));
	String name = rset.getString(StructTable.table.strName.toString());

	String structType = rset.getString(StructTable.table.structType.toString());

	String membraneVoltageNameString = rset.getString(StructTable.table.memVoltName.toString());
	if (rset.wasNull()){
		membraneVoltageNameString=null;
	}

	Structure structure = null;
	if (structType.equals(StructTable.STRUCTTYPE_FEATURE)){
		try {
			structure = new Feature(structKey, name);
		}catch (java.beans.PropertyVetoException e){
		}
	}else if (structType.equals(StructTable.STRUCTTYPE_MEMBRANE)){
		try {
			structure = new Membrane(structKey, name);
			if (membraneVoltageNameString!=null){
				((Membrane)structure).getMembraneVoltage().setName(membraneVoltageNameString);
			}
		}catch (java.beans.PropertyVetoException e){
		}
	}

	return structure;
}
}