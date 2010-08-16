package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.QueryHashtable;
import cbit.sql.Table;
import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContextInfo;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.Model;
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

	public final Field mathRef 		= new Field("mathRef",		"integer",	MathDescTable.REF_TYPE);
	public final Field modelRef 		= new Field("modelRef",		"integer",	"NOT NULL "+ModelTable.REF_TYPE);
	public final Field geometryRef 	= new Field("geometryRef",	"integer",	"NOT NULL "+GeometryTable.REF_TYPE);
	public final Field charSize		= new Field("charSize",		"NUMBER",	"");

	public final Field appComponentsLarge	= new Field("appComponentsLRG",	"CLOB",				"");
	public final Field appComponentsSmall	= new Field("appComponentsSML",	"VARCHAR2(4000)",	"");

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
	
	return new SimulationContextInfo(mathRef,geomRef,modelRef,version);
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special) {
	UserTable userTable = UserTable.table;
	SimContextTable vTable = SimContextTable.table;
	String sql;
	
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable)};
	Table[] t = {vTable,userTable};

	String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName()+" ";  // links in the userTable
	if (extraConditions != null && extraConditions.trim().length()>0){
		condition += " AND "+extraConditions;
	}

	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special);
	return sql;
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
	
	SimulationContext simContext = new SimulationContext(model,geom,mathDesc,version, mathDesc.isNonSpatialStoch());
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
public static String getXMLStringForDatabase(SimulationContext simContext) {
	Element appComponentsElement = new Element(XMLTags.ApplicationComponents);
	
	// first fill in bioevents from simContext
	BioEvent[] bioEvents = simContext.getBioEvents();
	if (bioEvents != null && bioEvents.length > 0) {
		try {
			Element bioEventElement = ((new Xmlproducer(false)).getXML(bioEvents));
			appComponentsElement.addContent(bioEventElement);
		} catch (XmlParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error generating XML for bioevents : " + e.getMessage());
		}
	}
	
	// fill in other application components when ready (rate rules, etc. etc.?)
	
	String appComponentsXMLStr = null; 
	if (appComponentsElement.getContent() != null) {
		appComponentsXMLStr = XmlUtil.xmlToString(appComponentsElement);
	}
	return appComponentsXMLStr;
}
		
/**
 * getAppComponentsElement : retireves the <AppComponents> element from the database when requested.
 * @param con
 * @param simContextRef
 * @return
 * @throws SQLException
 * @throws DataAccessException
 */
private Element getAppComponentsElement(Connection con, KeyValue simContextRef) throws SQLException, DataAccessException {
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
	String appComponentsXMLStr = DbDriver.varchar2_CLOB_get(rset, SimContextTable.table.appComponentsSmall, SimContextTable.table.appComponentsLarge);
	rset.close();
	if(appComponentsXMLStr == null){
		return null;
	}
	
	if(stmt != null){stmt.close();}
	Element appComponentsElement = XmlUtil.stringToXML(appComponentsXMLStr, null).getRootElement();
	return appComponentsElement;
}

/**
 * getBioEvents : retrieves the <BioEvents> element from <AppComponents> from database and via XMLReader, constructs the 
 * BioEvents list for the simContext. 
 * @param con
 * @param simContext
 * @return
 * @throws SQLException
 * @throws DataAccessException
 */
public BioEvent[] getBioEvents(Connection con, SimulationContext simContext) throws SQLException, DataAccessException {
	BioEvent[] bioEvents = null;
	
	try {
		Element appComponentsElement = getAppComponentsElement(con, simContext.getVersion().getVersionKey());
		if (appComponentsElement != null) {
			Element bioEventsElement = appComponentsElement.getChild(XMLTags.BioEventsTag);
			if (bioEventsElement != null) {
				bioEvents = (new XmlReader(false)).getBioEvents(simContext, bioEventsElement);
			}
		}
	} catch (XmlParseException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException("Error retrieving bioevents : " + e.getMessage());
	}
	return bioEvents;
}

}