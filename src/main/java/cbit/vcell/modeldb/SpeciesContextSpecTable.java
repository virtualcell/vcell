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

import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.mapping.SpeciesContextSpec;
/**
 * This type was created in VisualAge.
 */
public class SpeciesContextSpecTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_speciescontextspec";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] scspec_table_constraints =
		new String[] {
		"initcndcnstr CHECK (not (initCondExp is null and initCondCountExp is null)  and  not (initCondExp is not null and initCondCountExp is not null))"};

	public final Field specContextRef	= new Field("specContextRef",SQLDataType.integer,		"NOT NULL "+SpeciesContextModelTable.REF_TYPE);
	public final Field simContextRef	= new Field("simContextRef",SQLDataType.integer,		"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field bEnableDif		= new Field("bEnableDif",	SQLDataType.integer,		"NOT NULL");
	public final Field bForceConst		= new Field("bForceConst",	SQLDataType.integer,		"NOT NULL");
	public final Field bForceIndep		= new Field("bForceIndep",	SQLDataType.integer,		"NOT NULL");
	public final Field initCondExp		= new Field("initCondExp",	SQLDataType.varchar_2048,	"");
	public final Field diffRateExp		= new Field("diffRateExp",	SQLDataType.varchar_1024, 	"NOT NULL");
	public final Field boundaryXmExp	= new Field("boundaryXmExp",SQLDataType.varchar_255,	"");
	public final Field boundaryXpExp	= new Field("boundaryXpExp",SQLDataType.varchar_255,	"");
	public final Field boundaryYmExp	= new Field("boundaryYmExp",SQLDataType.varchar_255,	"");
	public final Field boundaryYpExp	= new Field("boundaryYpExp",SQLDataType.varchar_255,	"");
	public final Field boundaryZmExp	= new Field("boundaryZmExp",SQLDataType.varchar_255,	"");
	public final Field boundaryZpExp	= new Field("boundaryZpExp",SQLDataType.varchar_255,	"");
	public final Field initCondCountExp	= new Field("initCondCountExp",	SQLDataType.varchar_1024,"");
	public final Field velocityXExp		= new Field("velocityXExp",	SQLDataType.varchar_1024,	"");
	public final Field velocityYExp		= new Field("velocityYExp",	SQLDataType.varchar_1024,	"");
	public final Field velocityZExp		= new Field("velocityZExp",	SQLDataType.varchar_1024,	"");
	public final Field bWellMixed		= new Field("bWellMixed",	SQLDataType.integer,		"");
	public final Field bForceContinuous	= new Field("bForceContinuous",	SQLDataType.integer,	"");
	
	private final Field fields[] = {specContextRef,simContextRef,bEnableDif,bForceConst,bForceIndep,initCondExp,diffRateExp,
											boundaryXmExp,boundaryXpExp,boundaryYmExp,boundaryYpExp,boundaryZmExp,boundaryZpExp,initCondCountExp,
											velocityXExp, velocityYExp, velocityZExp, bWellMixed, bForceContinuous};
	
	public static final SpeciesContextSpecTable table = new SpeciesContextSpecTable();
/**
 * ModelTable constructor comment.
 */
private SpeciesContextSpecTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue Key, KeyValue simContextKey, SpeciesContextSpec speciesContextSpec, KeyValue scKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Key + ",");
	buffer.append(scKey + ",");
	buffer.append(simContextKey + ",");
	
	boolean bForceIndependent = false;

	boolean bEnableDiffusing = true; // speciesContextSpec.isEnableDiffusing()
	buffer.append((bEnableDiffusing ? 1 : 0) + ",");
	buffer.append((speciesContextSpec.isConstant() ? 1 : 0) + ",");
	buffer.append((bForceIndependent ? 1 : 0) + ",");

	if(speciesContextSpec.getInitialConditionParameter().getRole() == SpeciesContextSpec.ROLE_InitialConcentration){
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getInitialConditionParameter().getExpression().infix()) + "'" + ",");		
	}else{
		buffer.append("NULL" + ",");				
	}
	buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getDiffusionParameter().getExpression().infix()) + "'" + ",");

	if (speciesContextSpec.getBoundaryXmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryXmParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryXpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryXpParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryYmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryYmParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryYpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryYpParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryZmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryZmParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryZpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryZpParameter().getExpression().infix()) + "'" + ",");
	}

	if(speciesContextSpec.getInitialConditionParameter().getRole() == SpeciesContextSpec.ROLE_InitialCount){
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getInitialConditionParameter().getExpression().infix()) + "'" + ",");		
	}else{
		buffer.append(" NULL " + ",");
	}
	
	// for velocity x,y,z terms
	if (speciesContextSpec.getVelocityXParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getVelocityXParameter().getExpression().infix()) + "'" + ",");
	}
	if (speciesContextSpec.getVelocityYParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getVelocityYParameter().getExpression().infix()) + "'" + ",");
	}
	if (speciesContextSpec.getVelocityZParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getVelocityZParameter().getExpression().infix()) + "'" + ",");
	}
	if (speciesContextSpec.isWellMixed() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append((speciesContextSpec.isWellMixed() ? 1 : 0) + ",");
	}
	if (speciesContextSpec.isForceContinuous() == null){
		buffer.append(" NULL " + ")");
	}else{
		buffer.append((speciesContextSpec.isForceContinuous() ? 1 : 0) + ")");
	}

	return buffer.toString();
}
}
