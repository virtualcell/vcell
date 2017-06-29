/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.Table;
import cbit.vcell.model.SpeciesContext;
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
	public final Field speciesPattern=new Field("speciesPattern","varchar2(255)","");
	
	private final Field fields[] = {modelRef,speciesRef,structRef,name,/*diffRate,initCond,*/hasOverride,speciesPattern};
	
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
		
		String speciesPattern = rset.getString(table.speciesPattern.getUnqualifiedColName());
		if(rset.wasNull()){
			speciesPattern = null;
		}else{
			speciesPattern = TokenMangler.getSQLRestoredString(speciesPattern);
		}
		SpeciesContext speciesContext = null;
		//try {
			speciesContext = new SpeciesContext(keyValue,nameStr,null,null);
			speciesContext.setSpeciesPatternString(speciesPattern);
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
	String speciesPattern = "NULL";
	if(speciesContext.getSpeciesPattern()!=null){
		speciesPattern = TokenMangler.getSQLEscapedString(speciesContext.getSpeciesPattern().toString());
	}
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(modelKey+",");
	buffer.append(speciesKey+",");
	buffer.append(structureKey+",");
	buffer.append("'"+speciesContext.getName()+"',");
	//buffer.append("'"+speciesContext.getDiffusionRate()+"'" + ",");
	//buffer.append("'"+speciesContext.getInitialValue()+"'" + ",");
	buffer.append("'"+OVERRIDE_TRUE+"',");
	buffer.append("'"+speciesPattern+"'");
	buffer.append(")");

	return buffer.toString();
}
}
