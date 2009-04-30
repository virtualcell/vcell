package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.document.KeyValue;

import cbit.sql.*;
import cbit.vcell.mapping.*;
import cbit.vcell.model.VCMODL;
/**
 * This type was created in VisualAge.
 */
public class SpeciesContextSpecTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_speciescontextspec";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] scspec_table_constraints =
		new String[] {
		"initcndcnstr CHECK (not (initCondExp is null and initCondCountExp is null)  and  not (initCondExp is not null and initCondCountExp is not null))"};

	public final Field specContextRef= new Field("specContextRef","integer",	"NOT NULL "+SpeciesContextModelTable.REF_TYPE);
	public final Field simContextRef	= new Field("simContextRef","integer",	"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field bEnableDif	= new Field("bEnableDif",	"integer",	"NOT NULL");
	public final Field bForceConst	= new Field("bForceConst",	"integer",	"NOT NULL");
	public final Field bForceIndep	= new Field("bForceIndep",	"integer",	"NOT NULL");
	public final Field initCondExp	= new Field("initCondExp",	"varchar(1024)","");
	public final Field diffRateExp	= new Field("diffRateExp",	"varchar(255)",	"NOT NULL");
	public final Field boundaryXmExp	= new Field("boundaryXmExp","varchar(255)",	"");
	public final Field boundaryXpExp	= new Field("boundaryXpExp","varchar(255)",	"");
	public final Field boundaryYmExp	= new Field("boundaryYmExp","varchar(255)",	"");
	public final Field boundaryYpExp	= new Field("boundaryYpExp","varchar(255)",	"");
	public final Field boundaryZmExp	= new Field("boundaryZmExp","varchar(255)",	"");
	public final Field boundaryZpExp	= new Field("boundaryZpExp","varchar(255)",	"");
	public final Field initCondCountExp	= new Field("initCondCountExp",	"varchar(1024)","");
	public final Field velocityXExp	= new Field("velocityXExp",	"varchar(255)",	"");
	public final Field velocityYExp	= new Field("velocityYExp",	"varchar(255)",	"");
	public final Field velocityZExp	= new Field("velocityZExp",	"varchar(255)",	"");
	
	private final Field fields[] = {specContextRef,simContextRef,bEnableDif,bForceConst,bForceIndep,initCondExp,diffRateExp,
											boundaryXmExp,boundaryXpExp,boundaryYmExp,boundaryYpExp,boundaryZmExp,boundaryZpExp,initCondCountExp,
											velocityXExp, velocityYExp, velocityZExp};
	
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

	buffer.append((speciesContextSpec.isEnableDiffusing() ? 1 : 0) + ",");
	buffer.append((speciesContextSpec.isConstant() ? 1 : 0) + ",");
	buffer.append((bForceIndependent ? 1 : 0) + ",");

	if(speciesContextSpec.getInitialConditionParameter().getRole() == SpeciesContextSpec.ROLE_InitialConcentration){
		buffer.append("'" + speciesContextSpec.getInitialConditionParameter().getExpression().infix() + "'" + ",");		
	}else{
		buffer.append("NULL" + ",");				
	}
	buffer.append("'" + speciesContextSpec.getDiffusionParameter().getExpression().infix() + "'" + ",");

	if (speciesContextSpec.getBoundaryXmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + speciesContextSpec.getBoundaryXmParameter().getExpression().infix() + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryXpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + speciesContextSpec.getBoundaryXpParameter().getExpression().infix() + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryYmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + speciesContextSpec.getBoundaryYmParameter().getExpression().infix() + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryYpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + speciesContextSpec.getBoundaryYpParameter().getExpression().infix() + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryZmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + speciesContextSpec.getBoundaryZmParameter().getExpression().infix() + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryZpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + speciesContextSpec.getBoundaryZpParameter().getExpression().infix() + "'" + ",");
	}

	if(speciesContextSpec.getInitialConditionParameter().getRole() == SpeciesContextSpec.ROLE_InitialCount){
		buffer.append("'" + speciesContextSpec.getInitialConditionParameter().getExpression().infix() + "'" + ",");		
	}else{
		buffer.append(" NULL " + ",");
	}
	
	// for velocity x,y,z terms
	if (speciesContextSpec.getVelocityXParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + speciesContextSpec.getVelocityXParameter().getExpression().infix() + "'" + ",");
	}
	if (speciesContextSpec.getVelocityYParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + speciesContextSpec.getVelocityYParameter().getExpression().infix() + "'" + ",");
	}
	if (speciesContextSpec.getVelocityZParameter().getExpression() == null){
		buffer.append(" NULL " + ")");
	}else{
		buffer.append("'" + speciesContextSpec.getVelocityZParameter().getExpression().infix() + "'" + ")");
	}

	return buffer.toString();
}
}
