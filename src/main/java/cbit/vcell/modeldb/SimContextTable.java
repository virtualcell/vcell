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
import java.sql.Statement;

import org.jdom.Element;
import org.vcell.db.DatabaseSyntax;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.QueryHashtable;
import cbit.sql.Table;
import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.SimulationContextParameter;
import cbit.vcell.mapping.SimulationContextInfo;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.Model;
import cbit.vcell.modeldb.DatabasePolicySQL.JoinOp;
import cbit.vcell.modeldb.DatabasePolicySQL.OuterJoin;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.xml.Xmlproducer;
/**
 * This type was created in VisualAge.
 */
public class SimContextTable extends cbit.vcell.modeldb.VersionTable {
	private static final String TABLE_NAME = "vc_simcontext";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field mathRef 				= new Field("mathRef",		SQLDataType.integer,			MathDescTable.REF_TYPE);
	public final Field modelRef 			= new Field("modelRef",		SQLDataType.integer,			"NOT NULL "+ModelTable.REF_TYPE);
	public final Field geometryRef 			= new Field("geometryRef",	SQLDataType.integer,			"NOT NULL "+GeometryTable.REF_TYPE);
	public final Field charSize				= new Field("charSize",		SQLDataType.number_as_real,		"");

	public final Field appComponentsLarge	= new Field("appComponentsLRG",	SQLDataType.clob_text,		"");
	public final Field appComponentsSmall	= new Field("appComponentsSML",	SQLDataType.varchar2_4000,	"");

	private final Field fields[] = {mathRef,modelRef,geometryRef,charSize, appComponentsLarge, appComponentsSmall};
	
	public static final SimContextTable table = new SimContextTable();

/**
 * ModelTable constructor comment.
 */
private SimContextTable() {
	super(TABLE_NAME,SimContextTable.REF_TYPE);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset,Connection con,SessionLog log) throws SQLException,DataAccessException {

	KeyValue mathRef = null;
	java.math.BigDecimal mathRefValue = rset.getBigDecimal(SimContextTable.table.mathRef.toString());
	if (!rset.wasNull()){
		mathRef = new KeyValue(mathRefValue);
	}
	
	KeyValue geomRef = new KeyValue(rset.getBigDecimal(SimContextTable.table.geometryRef.toString()));

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(SimContextTable.table.modelRef.toString()));

	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	String softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.toString());
	
	return new SimulationContextInfo(mathRef,geomRef,modelRef,version,VCellSoftwareVersion.fromString(softwareVersion));
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special,DatabaseSyntax dbSyntax) {
	UserTable userTable = UserTable.table;
	SimContextTable vTable = SimContextTable.table;
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable),swvTable.softwareVersion};
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
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() +  " ";// links in the userTable
		         //  " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
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
 * @return cbit.vcell.mapping.SimulationContext
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 * @deprecated shouldn't do recursive query
 */
public SimulationContext getSimContext(QueryHashtable dbc, Connection con,User user,ResultSet rset,SessionLog log,
										GeomDbDriver geomDB,ModelDbDriver modelDB,MathDescriptionDbDriver mathDB) 
							throws SQLException,DataAccessException, java.beans.PropertyVetoException {
			
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	KeyValue geomKey = new KeyValue(rset.getBigDecimal(SimContextTable.table.geometryRef.toString()));
	Geometry geom = (Geometry)geomDB.getVersionable(dbc, con,user, VersionableType.Geometry,geomKey,false);
	KeyValue modelKey = new KeyValue(rset.getBigDecimal(SimContextTable.table.modelRef.toString()));
	Model model  = (Model)modelDB.getVersionable(dbc, con,user, VersionableType.Model,modelKey);

	//
	// read characteristic size (may be null)
	//
	Double characteristicSize = null;
	BigDecimal size = rset.getBigDecimal(charSize.toString());
	if (!rset.wasNull() && size!=null){
		characteristicSize = new Double(size.doubleValue());
	}

	//
	// get mathKey (may be null)
	//
	MathDescription mathDesc = null;
	BigDecimal mathKeyValue = rset.getBigDecimal(SimContextTable.table.mathRef.toString());
	if (!rset.wasNull()){
		KeyValue mathKey = new KeyValue(mathKeyValue);
		mathDesc  = (MathDescription)mathDB.getVersionable(dbc, con,user, VersionableType.MathDescription,mathKey);
	}
	boolean bStochastic = mathDesc.isNonSpatialStoch() || mathDesc.isSpatialStoch() || mathDesc.isSpatialHybrid();
	boolean bRuleBased = mathDesc.isRuleBased();
	SimulationContext simContext = new SimulationContext(model,geom,mathDesc,version, bStochastic, bRuleBased);
	if (characteristicSize!=null){
		simContext.setCharacteristicSize(characteristicSize);
	}
	return simContext;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(SimulationContext simContext,KeyValue mathDescKey,KeyValue modelKey,KeyValue geomKey, String serialAppComp, Version version) {
			
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version)+",");
	buffer.append(mathDescKey + ",");
	buffer.append(modelKey+",");
	buffer.append(geomKey+",");
	buffer.append(simContext.getCharacteristicSize()+",");
	
	if (serialAppComp==null){
		buffer.append("null,null");
	}else if (DbDriver.varchar2_CLOB_is_Varchar2_OK(serialAppComp)){
		buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE);
	}else{
		buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null");
	}

	buffer.append(")");
	return buffer.toString();
}

/**
 * getXMLStringForDatabase : this returns the XML string for the container element <AppComponents> for application-related protocols 
 * and other extra specifications. For now, BioEvents falls under this category, so the BioEvents element (list of bioevents)
 * is obtained from the simContext (via the XMLProducer) and added as content to <AppComponents> element. The <AppComponents>
 * element is converted to XML string which is the return value of this method. This string is stored in the database in the
 * SimContextTable. Instead of creating new fields for each possible application component, it is convenient to store them 
 * all under a blanket XML element <AppComponents>.
 * @param simContext
 * @return
 */
public static String getAppComponentsForDatabase(SimulationContext simContext) {
	Element appComponentsElement = new Element(XMLTags.ApplicationComponents);

	// Add element for application flags : bStoch, bUseConcentration, bRandomizeInitCondition, and any other new flags that might be introduced.
	
	// for now, create the element only if application is stochastic. Can change it later.
	if (simContext.isStoch()) {
//		appRelatedFlagsElement.setAttribute(XMLTags.StochAttrTag, "true");
//		if(simContext.isUsingConcentration()) {
//			appRelatedFlagsElement.setAttribute(XMLTags.ConcentrationAttrTag, "true");
//		} else {
//			appRelatedFlagsElement.setAttribute(XMLTags.ConcentrationAttrTag, "false");
//		}
		// add 'randomizeInitCondition' flag only if simContext is non-spatial
		if (simContext.getGeometry().getDimension() == 0) {
			Element appRelatedFlagsElement = new Element(XMLTags.ApplicationSpecificFlagsTag);
			if(simContext.isRandomizeInitCondition()) {
				appRelatedFlagsElement.setAttribute(XMLTags.RandomizeInitConditionTag, "true");
			} else {
				appRelatedFlagsElement.setAttribute(XMLTags.RandomizeInitConditionTag, "false");
			}
			appComponentsElement.addContent(appRelatedFlagsElement);			
		}
	}
	if(simContext.isInsufficientIterations()) {
		appComponentsElement.setAttribute(XMLTags.InsufficientIterationsTag, "true");
	} else {
		appComponentsElement.setAttribute(XMLTags.InsufficientIterationsTag, "false");
	}
	if(simContext.isInsufficientMaxMolecules()) {
		appComponentsElement.setAttribute(XMLTags.InsufficientMaxMoleculesTag, "true");
	} else {
		appComponentsElement.setAttribute(XMLTags.InsufficientMaxMoleculesTag, "false");
	}
		
	Xmlproducer xmlProducer = new Xmlproducer(false);
	
	NetworkConstraints constraints = simContext.getNetworkConstraints();
	if(constraints != null) {
		appComponentsElement.addContent(xmlProducer.getXML(constraints));
	}
	
	// first fill in bioevents from simContext
	BioEvent[] bioEvents = simContext.getBioEvents();
	if (bioEvents != null && bioEvents.length > 0) {
		try {
			Element bioEventsElement = xmlProducer.getXML(bioEvents);
			appComponentsElement.addContent(bioEventsElement);
		} catch (XmlParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error generating XML for bioevents : " + e.getMessage());
		}
	}
	
	SimulationContextParameter[] appParams = simContext.getSimulationContextParameters();
	if (appParams!=null && appParams.length>0){
		try {
			Element appParamsElement = xmlProducer.getXML(appParams);
			appComponentsElement.addContent( appParamsElement);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error generating XML for application parameters : " + e.getMessage());
		}
	}
	
	SpatialObject[] spatialObjects = simContext.getSpatialObjects();
	if (spatialObjects!=null && spatialObjects.length>0){
		try {
			Element spatialObjectsElement = xmlProducer.getXML(spatialObjects);
			appComponentsElement.addContent( spatialObjectsElement);
		} catch (XmlParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error generating XML for spatialObjects : " + e.getMessage());
		}
	}
	
	SpatialProcess[] spatialProcesses = simContext.getSpatialProcesses();
	if (spatialProcesses!=null && spatialProcesses.length>0){
		try {
			Element spatialProcessesElement = xmlProducer.getXML(spatialProcesses);
			appComponentsElement.addContent( spatialProcessesElement);
		} catch (XmlParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error generating XML for spatialProcesses : " + e.getMessage());
		}
	}
	
	
	// microscope measurements
	Element element = xmlProducer.getXML(simContext.getMicroscopeMeasurement());
	appComponentsElement.addContent(element);

	// rate rules
	RateRule[] rateRules = simContext.getRateRules();
	if (rateRules != null && rateRules.length > 0) {
		try {
			Element rateRulesElement = xmlProducer.getXML(rateRules);
			appComponentsElement.addContent(rateRulesElement);
		} catch (XmlParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error generating XML for bioevents : " + e.getMessage());
		}
	}
	
	// ReactionRuleSpecs
	ReactionRuleSpec[] reactionRuleSpecs = simContext.getReactionContext().getReactionRuleSpecs();
	if (reactionRuleSpecs != null && reactionRuleSpecs.length > 0){
		Element reactionRuleSpecsElement = xmlProducer.getXML(reactionRuleSpecs);
		appComponentsElement.addContent(reactionRuleSpecsElement);
	}

	String appComponentsXMLStr = null; 
	if (appComponentsElement.getContent() != null) {
		appComponentsXMLStr = XmlUtil.xmlToString(appComponentsElement);
	}
	return appComponentsXMLStr;
}
		
/**
 * getAppComponentsElement : retrieves the <AppComponents> element from the database when requested.
 * @param con
 * @param simContextRef
 * @return
 * @throws SQLException
 * @throws DataAccessException
 */
private Element getAppComponentsElement(Connection con, KeyValue simContextRef, DatabaseSyntax dbSyntax) throws SQLException, DataAccessException {
	Statement stmt = null;
	stmt = con.createStatement();
	ResultSet rset =
		stmt.executeQuery(
			"SELECT * FROM "+SimContextTable.table.getTableName() +
			" WHERE "+
			SimContextTable.table.id.getUnqualifiedColName()+" = "+simContextRef.toString());
	
	if(!rset.next()){
		return null;
	}
	String appComponentsXMLStr = DbDriver.varchar2_CLOB_get(rset, SimContextTable.table.appComponentsSmall, SimContextTable.table.appComponentsLarge, dbSyntax);
	rset.close();
	if(appComponentsXMLStr == null){
		return null;
	}
	
	if(stmt != null){stmt.close();}
	Element appComponentsElement = XmlUtil.stringToXML(appComponentsXMLStr, null).getRootElement();
	return appComponentsElement;
}

/**
 * readAppComponents : reads the additional simContext components like bioevents/application related flags (for stochastic, at the moment), if present, and sets them on simContext.
 * @param con
 * @param simContext
 * @return
 * @throws SQLException
 * @throws DataAccessException
 * @throws PropertyVetoException 
 */
public void readAppComponents(Connection con, SimulationContext simContext, DatabaseSyntax dbSyntax) throws SQLException, DataAccessException, PropertyVetoException {
	
	try {
		Element appComponentsElement = getAppComponentsElement(con, simContext.getVersion().getVersionKey(), dbSyntax);
		if (appComponentsElement != null) {
			Element appRelatedFlags = appComponentsElement.getChild(XMLTags.ApplicationSpecificFlagsTag);
			if (appRelatedFlags != null) {
				// for now, only reading the 'randomizeInitCondition' attribute, since 'isStoch' and 'isUsingconcentration' are read in by other means; so not messing with those fields of simContext.
				boolean bRandomizeInitCondition = false;
				if ((appRelatedFlags.getAttributeValue(XMLTags.RandomizeInitConditionTag)!= null) && (appRelatedFlags.getAttributeValue(XMLTags.RandomizeInitConditionTag).equals("true"))) {
					bRandomizeInitCondition = true;
				}
				simContext.setRandomizeInitConditions(bRandomizeInitCondition);
			}
			if((appComponentsElement.getAttributeValue(XMLTags.InsufficientIterationsTag)!= null) && (appComponentsElement.getAttributeValue(XMLTags.InsufficientIterationsTag).equals("true"))) {
				simContext.setInsufficientIterations(true);
			} else {
				simContext.setInsufficientIterations(false);
			}
			if((appComponentsElement.getAttributeValue(XMLTags.InsufficientMaxMoleculesTag)!= null) && (appComponentsElement.getAttributeValue(XMLTags.InsufficientMaxMoleculesTag).equals("true"))) {
				simContext.setInsufficientMaxMolecules(true);
			} else {
				simContext.setInsufficientMaxMolecules(false);
			}

			XmlReader xmlReader = new XmlReader(false);
			
			NetworkConstraints nc = null;
			Element ncElement = appComponentsElement.getChild(XMLTags.RbmNetworkConstraintsTag);
			if(ncElement != null) {
				nc = xmlReader.getAppNetworkConstraints(ncElement, simContext.getModel());	// one network constraint element
			}
			simContext.setNetworkConstraints(nc);
			
			// get spatial objects
			Element spatialObjectsElement = appComponentsElement.getChild(XMLTags.SpatialObjectsTag);
			if (spatialObjectsElement != null) {
				SpatialObject[] spatialObjects = xmlReader.getSpatialObjects(simContext, spatialObjectsElement);
				simContext.setSpatialObjects(spatialObjects);
			}
			// get application parameters
			Element appParamsElement = appComponentsElement.getChild(XMLTags.ApplicationParametersTag);
			if (appParamsElement != null) {
				SimulationContextParameter[] appParams = xmlReader.getSimulationContextParams(appParamsElement, simContext);
				simContext.setSimulationContextParameters(appParams);
			}
			// get bioEvents
			Element bioEventsElement = appComponentsElement.getChild(XMLTags.BioEventsTag);
			if (bioEventsElement != null) {
				BioEvent[] bioEvents = xmlReader.getBioEvents(simContext, bioEventsElement);
				simContext.setBioEvents(bioEvents);
			}
			// get spatial processes
			Element spatialProcessesElement = appComponentsElement.getChild(XMLTags.SpatialProcessesTag);
			if (spatialProcessesElement != null) {
				SpatialProcess[] spatialProcesses = xmlReader.getSpatialProcesses(simContext, spatialProcessesElement);
				simContext.setSpatialProcesses(spatialProcesses);
			}
			// get microscope measurements
			Element element = appComponentsElement.getChild(XMLTags.MicroscopeMeasurement);
			if (element != null) {
				xmlReader.getMicroscopeMeasurement(element, simContext);
			}
			// get rate rules
			Element rateRulesElement = appComponentsElement.getChild(XMLTags.RateRulesTag);
			if (rateRulesElement != null) {
				RateRule[] rateRules = xmlReader.getRateRules(simContext, rateRulesElement);
				simContext.setRateRules(rateRules);
			}
			// get reaction rule specs
			Element reactionRuleSpecsElement = appComponentsElement.getChild(XMLTags.ReactionRuleSpecsTag);
			if (reactionRuleSpecsElement != null) {
				ReactionRuleSpec[] reactionRuleSpecs = xmlReader.getReactionRuleSpecs(simContext, reactionRuleSpecsElement);
				simContext.getReactionContext().setReactionRuleSpecs(reactionRuleSpecs);
			}
		}
	} catch (XmlParseException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException("Error retrieving bioevents : " + e.getMessage());
	}
}


public String getPreparedStatement_SimContextReps(){

	SimContextTable scTable = SimContextTable.table;
	UserTable userTable = UserTable.table;
	
	String subquery = 			
		"select " +
		    scTable.id.getQualifiedColName()+", "+
		    scTable.name.getQualifiedColName()+", "+
		    scTable.versionBranchID.getQualifiedColName()+", "+
		    scTable.ownerRef.getQualifiedColName()+", "+
		    UserTable.table.userid.getQualifiedColName()+", "+
		    scTable.mathRef.getQualifiedColName()+" "+
		
		"from "+scTable.getTableName()+", "+userTable.getTableName()+" "+
		"where "+scTable.ownerRef.getQualifiedColName()+" = "+userTable.id.getQualifiedColName()+" "+
		"and "+scTable.id.getQualifiedColName() + " > ?";

	String orderByClause = "order by "+scTable.versionDate.getQualifiedColName()+" ASC";

	String sql =  
		"select * from "+
		"(" + subquery + " " + orderByClause + ") "+
		"where rownum <= ?";
	
	//System.out.println(sql);
	return sql;
}

public String getPreparedStatement_SimContextRep(){

	SimContextTable scTable = SimContextTable.table;
	UserTable userTable = UserTable.table;
	
	String sql = 			
		"select " +
		    scTable.id.getQualifiedColName()+", "+
		    scTable.name.getQualifiedColName()+", "+
		    scTable.versionBranchID.getQualifiedColName()+", "+
		    scTable.ownerRef.getQualifiedColName()+", "+
		    UserTable.table.userid.getQualifiedColName()+", "+
		    scTable.mathRef.getQualifiedColName()+" "+
		
		"from "+scTable.getTableName()+", "+userTable.getTableName()+" "+
		"where "+scTable.ownerRef.getQualifiedColName()+" = "+userTable.id.getQualifiedColName()+" "+
		"and "+scTable.id.getQualifiedColName() + " = ?";

	//System.out.println(sql);
	return sql;
}


public void setPreparedStatement_SimContextReps(PreparedStatement stmt, KeyValue minSimContextKeyValue, int numRows) throws SQLException{
	BigDecimal minSimContextKey = new BigDecimal(0);
	if (minSimContextKeyValue!=null){
		minSimContextKey = new BigDecimal(minSimContextKeyValue.toString());
	}
	stmt.setBigDecimal(1, minSimContextKey);
	stmt.setInt(2, numRows);
}


public void setPreparedStatement_SimContextRep(PreparedStatement stmt, KeyValue simContextKeyValue) throws SQLException{
	BigDecimal simContextKey = new BigDecimal(0);
	if (simContextKeyValue!=null){
		simContextKey = new BigDecimal(simContextKeyValue.toString());
	}
	stmt.setBigDecimal(1, simContextKey);
}



public SimContextRep getSimContextRep(ResultSet rset) throws IllegalArgumentException, SQLException {
	KeyValue scKey = new KeyValue(rset.getBigDecimal(table.id.toString()));
	String name = rset.getString(table.name.toString());
	BigDecimal branchID = rset.getBigDecimal(table.versionBranchID.toString());
	KeyValue ownerRef = new KeyValue(rset.getBigDecimal(table.ownerRef.toString()));
	String ownerName = rset.getString(UserTable.table.userid.toString());
	User owner = new User(ownerName,ownerRef);
	KeyValue mathKey = new KeyValue(rset.getBigDecimal(table.mathRef.toString()));
	
	return new SimContextRep(scKey,branchID,name,owner,mathKey);
}


}
