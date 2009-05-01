package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.*;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
public class SpeciesContextModelTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_modelsc";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field modelRef 	= new Field("modelRef",	"integer",	"NOT NULL "+ModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field speciesRef	= new Field("speciesRef",	"integer",		"NOT NULL "+SpeciesTable.REF_TYPE);
	public final Field structRef	= new Field("structRef",	"integer",		"NOT NULL "+StructTable.REF_TYPE);
	public final Field name			= new Field("name",			"varchar(255)",	"NOT NULL");
	//public final Field diffRate		= new Field("diffRate",		"varchar(255)",	"NOT NULL");
	//public final Field initCond		= new Field("initCond",		"varchar(255)",	"NOT NULL");
	public final Field hasOverride	= new Field("hasOverride",	"varchar2(1)",	"NOT NULL");// 'T' or 'F'

	private final Field fields[] = {modelRef,speciesRef,structRef,name,/*diffRate,initCond,*/hasOverride};
	
	public static final SpeciesContextModelTable table = new SpeciesContextModelTable();
	//
	private static final String OVERRIDE_TRUE = "T";
	private static final String OVERRIDE_FALSE = "F";

/**
 * ModelTable constructor comment.
 */
private SpeciesContextModelTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 */
public SpeciesContext getSpeciesContext(java.sql.ResultSet rset, SessionLog log, KeyValue keyValue) throws java.sql.SQLException, DataAccessException {

	//try {
		String nameStr = rset.getString(name.toString());
		//Expression initValueExp = new Expression(rset.getString(initCond.toString()));
		//Expression diffValueExp = new Expression(rset.getString(diffRate.toString()));
		boolean bHasOverride = (rset.getString(hasOverride.toString()).equals(OVERRIDE_TRUE)?true:false);
		
		SpeciesContext speciesContext = null;
		//try {
			speciesContext = new SpeciesContext(keyValue,nameStr,null,null,bHasOverride);
			//speciesContext.setInitialValue(initValueExp.evaluateConstant());
			//speciesContext.setDiffusionRate(diffValueExp.evaluateConstant());
			return speciesContext;
		//}catch (java.beans.PropertyVetoException e){
			//log.exception(e);
			//throw new DataAccessException("PropertyVetoException unexpected: "+e.getMessage());
		//}
	//}catch (cbit.vcell.parser.ExpressionException e){
		//log.exception(e);
		//throw new DataAccessException("ExpressionException while reading SpeciesContext: "+e.getMessage());
	//}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(InsertHashtable hash, KeyValue key, SpeciesContext speciesContext, KeyValue modelKey) throws DataAccessException {

	int defaultCharge = 0;
	KeyValue speciesKey = hash.getDatabaseKey(speciesContext.getSpecies());
	KeyValue structureKey = hash.getDatabaseKey(speciesContext.getStructure());
	
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(modelKey+",");
	buffer.append(speciesKey+",");
	buffer.append(structureKey+",");
	buffer.append("'"+speciesContext.getName()+"',");
	//buffer.append("'"+speciesContext.getDiffusionRate()+"'" + ",");
	//buffer.append("'"+speciesContext.getInitialValue()+"'" + ",");
	buffer.append((speciesContext.getHasOverride() ? "'"+OVERRIDE_TRUE+"'" : "'"+OVERRIDE_FALSE+"'"));
	buffer.append(")");

	return buffer.toString();
}
}