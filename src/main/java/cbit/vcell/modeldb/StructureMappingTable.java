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
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.Table;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.StructureMapping;
/**
 * This type was created in VisualAge.
 */
public class StructureMappingTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_structmapping";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field subVolumeRef			= new Field("subVolumeRef",	"integer",	SubVolumeTable.REF_TYPE);
	public final Field structRef			= new Field("structRef",	"integer",	"NOT NULL "+StructTable.REF_TYPE);
	public final Field simContextRef		= new Field("simContextRef","integer",	"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field bResolved 			= new Field("bResolved",	"integer",	"NOT NULL");
	public final Field surfToVolExp			= new Field("surfToVolExp",	"varchar(1024)",	"");
	public final Field volFractExp			= new Field("volFractExp",	"varchar(1024)",	"");
	public final Field boundaryTypeXm 		= new Field("boundaryTypeXm", "varchar(10)", "");
	public final Field boundaryTypeXp 		= new Field("boundaryTypeXp", "varchar(10)", "");
	public final Field boundaryTypeYm		= new Field("boundaryTypeYm", "varchar(10)", "");
	public final Field boundaryTypeYp 		= new Field("boundaryTypeYp", "varchar(10)", "");
	public final Field boundaryTypeZm 		= new Field("boundaryTypeZm", "varchar(10)", "");
	public final Field boundaryTypeZp 		= new Field("boundaryTypeZp", "varchar(10)", "");
	public final Field bCalculateVoltage 	= new Field("bCalculateV",	"integer", "");
	public final Field specificCap     		= new Field("specificCap",	"number", "");
	public final Field initialVoltage  		= new Field("initialV",		"varchar(1024)", "");
	public final Field sizeExp				= new Field("sizeExp",	"varchar(1024)",	""); //added Dec 23, 2006
	public final Field volPerUnitAreaExp	= new Field("volPerUnitAreaExp",	"varchar(1024)",	"");
	public final Field volPerUnitVolExp		= new Field("volPerUnitVolExp",	"varchar(1024)",	"");
	public final Field areaPerUnitAreaExp	= new Field("areaPerUnitAreaExp",	"varchar(1024)",	"");
	public final Field areaPerUnitVolExp	= new Field("areaPerUnitVolExp",	"varchar(1024)",	"");
	public final Field surfaceClassRef		= new Field("surfaceClassRef",	"integer",	SurfaceClassTable.REF_TYPE);

	private final Field fields[] = {subVolumeRef,structRef,simContextRef,bResolved,surfToVolExp,volFractExp,
					boundaryTypeXm,boundaryTypeXp,boundaryTypeYm,boundaryTypeYp,boundaryTypeZm,boundaryTypeZp,
					bCalculateVoltage,specificCap,initialVoltage, sizeExp, volPerUnitAreaExp, volPerUnitVolExp, 
					areaPerUnitAreaExp, areaPerUnitVolExp,surfaceClassRef};
	
	public static final StructureMappingTable table = new StructureMappingTable();

/**
 * ModelTable constructor comment.
 */
private StructureMappingTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(InsertHashtable hash, KeyValue Key, KeyValue simContextKey, StructureMapping structureMapping) throws DataAccessException {

	GeometryClass geometryClass = structureMapping.getGeometryClass();
	KeyValue geometryClassKey = (geometryClass==null?null:hash.getDatabaseKey(geometryClass));

	if (geometryClass != null && geometryClassKey==null){
		geometryClassKey = geometryClass.getKey();
		if (geometryClassKey==null){
			throw new DataAccessException("no key for GeometryClass '"+geometryClass.getName()+"' "+geometryClass.getClass().getName());
		}
	}
	KeyValue structureKey = hash.getDatabaseKey(structureMapping.getStructure());
	if (structureKey==null){
		structureKey = structureMapping.getStructure().getKey();
		if (structureKey==null){
			throw new DataAccessException("no key for structure "+structureMapping.getStructure());
		}
	}

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Key + ",");
	buffer.append((geometryClass instanceof SubVolume?geometryClassKey:null) + ",");
	buffer.append(structureKey + ",");
	buffer.append(simContextKey + ",");
	buffer.append((/*isResolved*/false ? 1 : 0) + ",");
	if (structureMapping instanceof FeatureMapping) {
		FeatureMapping fm = (FeatureMapping)structureMapping;
		buffer.append("null" + ",");
		buffer.append("null" + ",");
		buffer.append("'"+fm.getBoundaryConditionTypeXm().boundaryTypeStringValue() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeXp().boundaryTypeStringValue() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeYm().boundaryTypeStringValue() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeYp().boundaryTypeStringValue() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeZm().boundaryTypeStringValue() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeZp().boundaryTypeStringValue() + "',");
		buffer.append("null" + ",");
		buffer.append("null" + ",");
		buffer.append("null" + ",");
	} else if (structureMapping instanceof MembraneMapping) {
		MembraneMapping mm = (MembraneMapping) structureMapping;
		//surface volume ratios and volume fractions can be null in stochastic applications and other old models
		//amended Sept. 17th, 2007
		if(mm.getSurfaceToVolumeParameter().getExpression() != null)
		{
			buffer.append("'"+TokenMangler.getSQLEscapedString(mm.getSurfaceToVolumeParameter().getExpression().infix()) + "',");
		}
		else
		{
			buffer.append("null" + ",");
		}
		if(mm.getVolumeFractionParameter().getExpression() != null)
		{
			buffer.append("'"+TokenMangler.getSQLEscapedString(mm.getVolumeFractionParameter().getExpression().infix()) + "',");
		}
		else
		{
			buffer.append("null" + ",");
		}
		buffer.append("null" + ",");
		buffer.append("null" + ",");
		buffer.append("null" + ",");
		buffer.append("null" + ",");
		buffer.append("null" + ",");
		buffer.append("null" + ",");
		buffer.append((mm.getCalculateVoltage() ? 1 : 0)+",");
		try {
			buffer.append(mm.getSpecificCapacitanceParameter().getExpression().evaluateConstant()+",");
		}catch (cbit.vcell.parser.ExpressionException e){
			e.printStackTrace(System.out);
			throw new DataAccessException("specific capacitance for "+mm.getMembrane().getName()+" not constant: ("+e.getMessage()+")");
		}
		buffer.append("'"+TokenMangler.getSQLEscapedString(mm.getInitialVoltageParameter().getExpression().infix())+"',");
	}
	if(structureMapping.getSizeParameter().getExpression() != null)
		buffer.append("'"+TokenMangler.getSQLEscapedString(structureMapping.getSizeParameter().getExpression().infix())+ "',");
	else
		buffer.append("'',");
	
	if (structureMapping instanceof FeatureMapping) {
		FeatureMapping fm = (FeatureMapping)structureMapping;
		if(fm.getVolumePerUnitAreaParameter().getExpression() != null) {
			buffer.append("'"+TokenMangler.getSQLEscapedString(fm.getVolumePerUnitAreaParameter().getExpression().infix()) + "',");
		} else {
			buffer.append("null" + ",");
		}
		if(fm.getVolumePerUnitVolumeParameter().getExpression() != null) {
			buffer.append("'"+TokenMangler.getSQLEscapedString(fm.getVolumePerUnitVolumeParameter().getExpression().infix()) + "',");
		} else {
			buffer.append("null" + ",");
		}
		// if structureMapping is a featureMapping, 'areaPerUnitArea' and 'areaPerUnitVol' params are null, so fill those in here
		buffer.append("null,null");
	} else if (structureMapping instanceof MembraneMapping) {
		// if structureMapping is a featureMapping, 'volPerUnitArea' and 'volPerUnitVol' params are null, so fill those in here; then memMapping params
		buffer.append("null,null,");
		MembraneMapping mm = (MembraneMapping)structureMapping;
		if(mm.getAreaPerUnitAreaParameter().getExpression() != null) {
			buffer.append("'"+TokenMangler.getSQLEscapedString(mm.getAreaPerUnitAreaParameter().getExpression().infix()) + "',");
		} else {
			buffer.append("null" + ",");
		}
		if(mm.getAreaPerUnitVolumeParameter().getExpression() != null) {
			buffer.append("'"+TokenMangler.getSQLEscapedString(mm.getAreaPerUnitVolumeParameter().getExpression().infix()) + "'");
		} else {
			buffer.append("null");
		}
	}
	buffer.append(","+(geometryClass instanceof SurfaceClass?geometryClassKey:null));

	buffer.append(")");
	return buffer.toString();
}
}
