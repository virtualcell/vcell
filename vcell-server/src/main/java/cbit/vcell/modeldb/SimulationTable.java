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

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.vcell.db.DatabaseSyntax;
import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.QueryHashtable;
import cbit.sql.Table;
import cbit.vcell.math.MathDescription;
import cbit.vcell.modeldb.DatabasePolicySQL.JoinOp;
import cbit.vcell.modeldb.DatabasePolicySQL.OuterJoin;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MathOverrides.Element;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SolverTaskDescription;
/**
 * This type was created in VisualAge.
 */
public class SimulationTable extends VersionTable {
	private static final String TABLE_NAME = "vc_simulation";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field mathRef				= new Field("mathRef",			SQLDataType.integer,		"NOT NULL "+MathDescTable.REF_TYPE);
	public final Field mathOverridesOrig	= new Field("mathOverrides",	SQLDataType.clob_text,		"");
	public final Field mathOverridesLarge	= new Field("mathOverridesLRG",	SQLDataType.clob_text,		"");
	public final Field mathOverridesSmall	= new Field("mathOverridesSML",	SQLDataType.varchar2_4000,	"");
	public final Field taskDescription		= new Field("taskDesc",			SQLDataType.varchar2_4000,	"");
	public final Field meshSpecX			= new Field("meshspecx",		SQLDataType.integer,		"");
	public final Field meshSpecY			= new Field("meshspecy",		SQLDataType.integer,		"");
	public final Field meshSpecZ			= new Field("meshspecz",		SQLDataType.integer,		"");
	public final Field dataProcInstr		= new Field("dataProcInstr",	SQLDataType.varchar2_4000,	"");
	
	private final Field fields[] = {mathRef,mathOverridesOrig,mathOverridesLarge,mathOverridesSmall,taskDescription,meshSpecX,meshSpecY,meshSpecZ,dataProcInstr };
	
	public static final SimulationTable table = new SimulationTable();

/**
 * ModelTable constructor comment.
 */
private SimulationTable() {
	super(TABLE_NAME,SimulationTable.REF_TYPE,true);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset,Connection con) throws SQLException,DataAccessException {
	
	KeyValue mathRef = new KeyValue(rset.getBigDecimal(SimulationTable.table.mathRef.toString()));
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid));
	SimulationVersion simulationVersion = (SimulationVersion)version;
	String softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.toString());
	
	return new SimulationInfo(mathRef,simulationVersion,VCellSoftwareVersion.fromString(softwareVersion));
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special,DatabaseSyntax dbSyntax) {
	
	UserTable userTable = UserTable.table;
	SimulationTable vTable = SimulationTable.table;
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	//Field[] f = {userTable.userid,new cbit.sql.StarField(vTable)};
	Field[] f = new Field[] {vTable.id,userTable.userid,swvTable.softwareVersion};
	f = (Field[])BeanUtils.addElements(f,vTable.versionFields);
	f = (Field[])BeanUtils.addElement(f,vTable.mathRef);

	Table[] t = {vTable,userTable,swvTable};

	switch (dbSyntax){
	case ORACLE:{
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() +  // links in the userTable
		           " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
		if (extraConditions != null && extraConditions.trim().length()>0){
			condition += " AND "+extraConditions;
		}
	
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,(OuterJoin)null,condition,special,dbSyntax);
		return sql;
	}
	case POSTGRES:{
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() + " "; // links in the userTable
		          // " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
		if (extraConditions != null && extraConditions.trim().length()>0){
			condition += " AND "+extraConditions;
		}
		OuterJoin outerJoin = new OuterJoin( vTable, swvTable, JoinOp.LEFT_OUTER_JOIN, vTable.id, swvTable.versionableRef);
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,outerJoin,condition,special,dbSyntax);
		return sql;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public Simulation getSimulation(QueryHashtable dbc, ResultSet rset, Connection con, User user, MathDescriptionDbDriver mathDB, DatabaseSyntax dbSyntax) 
										throws SQLException,DataAccessException,PropertyVetoException {

	//
	// get TaskDescription Data into parsable form
	//
	//
	//String taskDescriptionString = new String(solverTaskDescData);
	//
//	System.out.println("taskDescriptionString '"+taskDescriptionString+"'");
	String taskDescriptionString = rset.getString(SimulationTable.table.taskDescription.getUnqualifiedColName());
	taskDescriptionString = TokenMangler.getSQLRestoredString(taskDescriptionString);
	CommentStringTokenizer solverTaskDescTokens = new CommentStringTokenizer(taskDescriptionString);
	
	//
	// get MathOverride Data (language) (MUST BE READ FIRST)
	//
	/*
	byte[] mathOverridesData = null;
	mathOverridesData = rset.getBytes(SimulationTable.table.mathOverrides.toString());
	if (rset.wasNull() || mathOverridesData==null || mathOverridesData.length==0){
		throw new DataAccessException("no data stored for MathOverrides");
	}
	String mathOverridesString = new String(mathOverridesData);
	*/
	//
	CommentStringTokenizer mathOverrideTokens = getMathOverridesTokenizer(rset, dbSyntax);

	String dataProcessingInstructionString = rset.getString(dataProcInstr.getUnqualifiedColName());
	DataProcessingInstructions dpi = null;
	if(!rset.wasNull() && dataProcessingInstructionString != null && dataProcessingInstructionString.length() > 0){
		dataProcessingInstructionString = TokenMangler.getSQLRestoredString(dataProcessingInstructionString);
		dpi = DataProcessingInstructions.fromDbXml(dataProcessingInstructionString);
	}
	//
	// Get Version
	//
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	SimulationVersion simulationVersion = (SimulationVersion)getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid));

	java.math.BigDecimal bigD = rset.getBigDecimal(SimulationTable.table.mathRef.toString());
	KeyValue mathKey = null;
	if (!rset.wasNull()) {
		mathKey = new KeyValue(bigD);
	}else{
		throw new DataAccessException("Error:  MathDescription Reference Cannot be Null for Simulation");
	}
	MathDescription mathDesc = (MathDescription)mathDB.getVersionable(dbc, con,user,VersionableType.MathDescription,mathKey);
	
	Simulation simulation = new Simulation(simulationVersion,mathDesc,mathOverrideTokens,solverTaskDescTokens);
	simulation.setDataProcessingInstructions(dpi);
	
	
	//MeshSpec (Is This Correct?????)
	if (mathDesc != null && mathDesc.getGeometry() != null && mathDesc.getGeometry().getDimension()>0){
		int msX = rset.getInt(SimulationTable.table.meshSpecX.getUnqualifiedColName());
		int msY = rset.getInt(SimulationTable.table.meshSpecY.getUnqualifiedColName());
		int msZ = rset.getInt(SimulationTable.table.meshSpecZ.getUnqualifiedColName());
		MeshSpecification meshSpec = new MeshSpecification(simulation.getMathDescription().getGeometry());
		meshSpec.setSamplingSize(new ISize(msX,msY,msZ));
		simulation.getMeshSpecification().copyFrom(meshSpec);
	}
	
	//
	// setMathDescription is done in calling parent
	//
	//simulation.setMathDescription(mathDesc);

	return simulation;
}
public static CommentStringTokenizer getMathOverridesTokenizer(ResultSet rset, DatabaseSyntax dbSyntax) throws SQLException, DataAccessException {
	String mathOverridesString =
		DbDriver.varchar2_CLOB_get(rset,SimulationTable.table.mathOverridesSmall,SimulationTable.table.mathOverridesLarge, dbSyntax);
	if(mathOverridesString == null || mathOverridesString.length() == 0){
		throw new DataAccessException("no data stored for MathOverrides");
	}
	//
	//	System.out.println("mathOverridesString '"+mathOverridesString+"'");
	if (mathOverridesString.endsWith(";}\n")){
		StringBuffer buffer = new StringBuffer(mathOverridesString.substring(0,mathOverridesString.length()-2));
		buffer.append("\n}\n");
		mathOverridesString = buffer.toString();
	}
	CommentStringTokenizer mathOverrideTokens = new CommentStringTokenizer(mathOverridesString);
	return mathOverrideTokens;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(Simulation simulation,KeyValue mathKey,Version version, DatabaseSyntax dbSyntax) {

	SolverTaskDescription 	solverTD 		= simulation.getSolverTaskDescription();
	String					mathOverrides	= simulation.getMathOverrides().getVCML();
	
	StringBuffer buffer = new StringBuffer();
	switch (dbSyntax){
	case ORACLE:{
		buffer.append("(");
		buffer.append(getVersionGroupSQLValue(version) + ",");
		buffer.append(mathKey+",");
		buffer.append("EMPTY_CLOB()"+","); // MathOverridesOrig keep for compatibility with old VCell
		break;
	}
	case POSTGRES:{
		buffer.append("(");
		buffer.append(getVersionGroupSQLValue(version) + ",");
		buffer.append(mathKey+",");
		buffer.append("null"+","); // MathOverridesOrig keep for compatibility with old VCell
		break;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
	
	if(DbDriver.varchar2_CLOB_is_Varchar2_OK(mathOverrides)){
		buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE+",");
	}else{
		buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null"+",");
	}
	
	buffer.append((solverTD != null?"'"+TokenMangler.getSQLEscapedString(solverTD.getVCML())+"'":"null")+",");
	if (simulation.getMathDescription() != null &&
		simulation.getMathDescription().getGeometry() != null &&
		simulation.getMathDescription().getGeometry().getDimension()>0){
		MeshSpecification	meshSpec = simulation.getMeshSpecification();
		buffer.append(meshSpec.getSamplingSize().getX()+","+meshSpec.getSamplingSize().getY()+","+meshSpec.getSamplingSize().getZ());
	}else{
		buffer.append("null,null,null");
	}
	if (simulation.getDataProcessingInstructions()!=null){
		buffer.append(",'"+TokenMangler.getSQLEscapedString(simulation.getDataProcessingInstructions().getDbXml())+"'");
	}else{
		buffer.append(",null");
	}
	buffer.append(")");
	return buffer.toString();
}




public String getPreparedStatement_SimulationReps(){

	SimulationTable simTable = SimulationTable.table;
	UserTable userTable = UserTable.table;
	
	String subquery = 			
		"select " +
		    simTable.id.getQualifiedColName()+", "+
		    simTable.name.getQualifiedColName()+", "+
		    simTable.versionBranchID.getQualifiedColName()+", "+
		    simTable.ownerRef.getQualifiedColName()+", "+
		    UserTable.table.userid.getQualifiedColName()+", "+
		    simTable.mathRef.getQualifiedColName()+", "+
		    simTable.taskDescription.getQualifiedColName()+", "+
		    simTable.mathOverridesLarge.getUnqualifiedColName()+", "+
		    simTable.mathOverridesSmall.getUnqualifiedColName()+" "+
		
		"from "+simTable.getTableName()+", "+userTable.getTableName()+" "+
		"where "+simTable.ownerRef.getQualifiedColName()+" = "+userTable.id.getQualifiedColName()+" "+
		"and "+simTable.id.getQualifiedColName() + " > ?";

	String orderByClause = "order by "+simTable.versionDate.getQualifiedColName()+" ASC";

	String sql =  
		"select * from "+
		"(" + subquery + " " + orderByClause + ") "+
		"where rownum <= ?";
	
	//System.out.println(sql);
	return sql;
}

public String getPreparedStatement_SimulationRep(){

	SimulationTable simTable = SimulationTable.table;
	UserTable userTable = UserTable.table;
	
	String sql = 			
		"select " +
		    simTable.id.getQualifiedColName()+", "+
		    simTable.name.getQualifiedColName()+", "+
		    simTable.versionBranchID.getQualifiedColName()+", "+
		    simTable.ownerRef.getQualifiedColName()+", "+
		    UserTable.table.userid.getQualifiedColName()+", "+
		    simTable.mathRef.getQualifiedColName()+", "+
		    simTable.taskDescription.getQualifiedColName()+", "+
		    simTable.mathOverridesLarge.getUnqualifiedColName()+", "+
		    simTable.mathOverridesSmall.getUnqualifiedColName()+" "+
		
		"from "+simTable.getTableName()+", "+userTable.getTableName()+" "+
		"where "+simTable.ownerRef.getQualifiedColName()+" = "+userTable.id.getQualifiedColName()+" "+
		"and "+simTable.id.getQualifiedColName() + " = ?";

	//System.out.println(sql);
	return sql;
}


public void setPreparedStatement_SimulationReps(PreparedStatement stmt, KeyValue minSimKeyValue, int numRows) throws SQLException{
	BigDecimal minSimKey = new BigDecimal(0);
	if (minSimKeyValue!=null){
		minSimKey = new BigDecimal(minSimKeyValue.toString());
	}
	stmt.setBigDecimal(1, minSimKey);
	stmt.setInt(2, numRows);
}


public void setPreparedStatement_SimulationRep(PreparedStatement stmt, KeyValue simKeyValue) throws SQLException{
	BigDecimal simKey = new BigDecimal(0);
	if (simKeyValue!=null){
		simKey = new BigDecimal(simKeyValue.toString());
	}
	stmt.setBigDecimal(1, simKey);
}



public SimulationRep getSimulationRep(ResultSet rset, DatabaseSyntax dbSyntax) throws IllegalArgumentException, SQLException, DataAccessException {
	KeyValue scKey = new KeyValue(rset.getBigDecimal(table.id.toString()));
	String name = rset.getString(table.name.toString());
	BigDecimal branchID = rset.getBigDecimal(table.versionBranchID.toString());
	KeyValue ownerRef = new KeyValue(rset.getBigDecimal(table.ownerRef.toString()));
	String ownerName = rset.getString(UserTable.table.userid.toString());
	User owner = new User(ownerName,ownerRef);
	KeyValue mathKey = new KeyValue(rset.getBigDecimal(table.mathRef.toString()));
	String taskDesc = rset.getString(table.taskDescription.toString());
	SolverTaskDescription solverTaskDescription = null;
	try {
		solverTaskDescription = new SolverTaskDescription(new CommentStringTokenizer(taskDesc));
	} catch (DataAccessException e) {
		System.out.println("SimulationTable:getSimulationRep(): failed to parse solver task description, exception=["+e.getMessage()+"]::\n[[["+taskDesc+"]]]\n");
//		e.printStackTrace();
	}

	CommentStringTokenizer mathOverridesTokenizer = getMathOverridesTokenizer(rset,dbSyntax);
	List<Element> mathOverrideElements = MathOverrides.parseOverrideElementsFromVCML(mathOverridesTokenizer);

	return new SimulationRep(scKey,branchID,name,owner,mathKey,solverTaskDescription,mathOverrideElements.toArray(new MathOverrides.Element[0]));
}


}
