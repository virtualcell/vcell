package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.StringTokenizer;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.*;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class StructureMappingTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_structmapping";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field subVolumeRef			= new Field("subVolumeRef",	"integer",	"NOT NULL "+SubVolumeTable.REF_TYPE);
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

	private final Field fields[] = {subVolumeRef,structRef,simContextRef,bResolved,surfToVolExp,volFractExp,
					boundaryTypeXm,boundaryTypeXp,boundaryTypeYm,boundaryTypeYp,boundaryTypeZm,boundaryTypeZp,
					bCalculateVoltage,specificCap,initialVoltage, sizeExp, volPerUnitAreaExp, volPerUnitVolExp, 
					areaPerUnitAreaExp, areaPerUnitVolExp};
	
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
public String getSQLValueList(InsertHashtable hash, KeyValue Key, KeyValue simContextKey, StructureMapping structureMapping, cbit.vcell.geometry.SubVolume subVolume, boolean isResolved) throws DataAccessException {

	KeyValue subVolumeKey = hash.getDatabaseKey(subVolume);
	if (subVolumeKey==null){
		subVolumeKey = subVolume.getKey();
		if (subVolumeKey==null){
			throw new DataAccessException("no key for subvolume "+subVolume);
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
	buffer.append(subVolumeKey + ",");
	buffer.append(structureKey + ",");
	buffer.append(simContextKey + ",");
	buffer.append((isResolved ? 1 : 0) + ",");
	if (structureMapping instanceof FeatureMapping) {
		FeatureMapping fm = (FeatureMapping)structureMapping;
		buffer.append("null" + ",");
		buffer.append("null" + ",");
		buffer.append("'"+fm.getBoundaryConditionTypeXm().toString() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeXp().toString() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeYm().toString() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeYp().toString() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeZm().toString() + "',");
		buffer.append("'"+fm.getBoundaryConditionTypeZp().toString() + "',");
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
	buffer.append(")");
	return buffer.toString();
}
}