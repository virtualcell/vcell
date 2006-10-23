package cbit.sql;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.document.KeyValue;
import cbit.util.document.SimulationVersion;
import cbit.util.document.Version;
import cbit.util.document.VersionFlag;
import cbit.util.document.Versionable;
import cbit.util.document.VersionableType;
import cbit.vcell.modeldb.BioModelSimContextLinkTable;
import cbit.vcell.modeldb.BioModelSimulationLinkTable;
import cbit.vcell.modeldb.BioModelTable;
import cbit.vcell.modeldb.GeometryTable;
import cbit.vcell.modeldb.ImageTable;
import cbit.vcell.modeldb.MathDescTable;
import cbit.vcell.modeldb.MathModelSimulationLinkTable;
import cbit.vcell.modeldb.MathModelTable;
import cbit.vcell.modeldb.ModelTable;
import cbit.vcell.modeldb.SimContextTable;
import cbit.vcell.modeldb.SimulationTable;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.modeldb.VersionRef;
/**
 * This type was created in VisualAge.
 */
public abstract class VersionTable extends cbit.sql.Table {
	
	public static final String name_ColumnName = "name";
	public final Field name				= new Field(name_ColumnName,			"varchar(255)",	"NOT NULL");
	public static final String ownerRef_ColumnName = "ownerRef";
	public final Field ownerRef 		= new Field(ownerRef_ColumnName,		"integer",		"NOT NULL "+UserTable.REF_TYPE);
	public static final String privacy_ColumnName = "privacy";
	public final Field privacy 			= new Field(privacy_ColumnName,			"integer",		"NOT NULL");
	public static final String versionBranchPointRef_ColumnName = "versionPRef";
	public final Field versionBranchPointRef;
	public static final String versionDate_ColumnName = "versionDate";
	public final Field versionDate		= new Field(versionDate_ColumnName,		"date",			"NOT NULL");
	public static final String versionFlag_ColumnName = "versionFlag";
	public final Field versionFlag		= new Field(versionFlag_ColumnName,		"integer",		"NOT NULL");
	public static final String versionAnnot_ColumnName = "versionAnnot";
	public final Field versionAnnot		= new Field(versionAnnot_ColumnName,	"varchar(4000)",	"");
	public static final String versionBranchID_ColumnName = "versionBranchID";
	public final Field versionBranchID	=new Field(versionBranchID_ColumnName,	"integer",	"NOT NULL");
	//
	// the following field is used only for Simulation objects (and is defined in the constructor)
	//
	public static final String versionParentSimRef_ColumnName = "parentSimRef";
	public final Field versionParentSimRef; // = new Field(versionParentSimRef_ColumnName,"integer",  SimulationTable.REF_TYPE);

	public final Field[] versionFields;

/**
 * VersionTable constructor comment.
 * @param name java.lang.String
 * @param refType java.lang.String
 * @param fields cbit.sql.Field[]
 */
protected VersionTable(String tableName,String vp) {
	this(tableName,vp,false);
}


/**
 * VersionTable constructor comment.
 * @param name java.lang.String
 * @param refType java.lang.String
 * @param fields cbit.sql.Field[]
 */
protected VersionTable(String tableName,String vp,boolean bHasParentSimRef) {
	super(tableName);
	this.versionBranchPointRef = new Field(versionBranchPointRef_ColumnName,"integer",vp);

	if (bHasParentSimRef){
		this.versionParentSimRef = new Field(versionParentSimRef_ColumnName,"integer",  SimulationTable.REF_TYPE);
		versionFields = new Field[] {this.name,this.ownerRef,this.privacy,this.versionBranchPointRef,this.versionDate,this.versionFlag,this.versionAnnot,this.versionBranchID,this.versionParentSimRef};
	}else{
		this.versionParentSimRef = null;
		versionFields = new Field[] {this.name,this.ownerRef,this.privacy,this.versionBranchPointRef,this.versionDate,this.versionFlag,this.versionAnnot,this.versionBranchID};
	}

	for(int i=0;i< versionFields.length;i+= 1){
		addField(versionFields[i]);
	}
	//addField(this.name);
	//addField(this.ownerRef);
	//addField(this.privacy);
	//addField(this.versionBranchPointRef);
	//addField(this.versionDate);
	//addField(this.versionFlag);
	//addField(this.versionAnnot);
	//addField(this.versionBranchID);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String formatDateToOracle(java.util.Date date) {
	//java.util.Date currentTime = new java.util.Date();
	SimpleDateFormat oracleDateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", java.util.Locale.US);
	return "TO_DATE("+"'"+oracleDateFormatter.format(date)+"'"+","+"'"+"DD-MON-YYYY HH24:MI:SS"+"'"+")";
}


/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public static java.util.Vector getChildVersionableTypes(VersionableType vType) {
	//
	// VersionableTypes that vType depends on(children, references to)
	//
	java.util.Vector rt = new java.util.Vector();
	if (VersionableType.VCImage.equals(vType)) {
		//VCImage has no Versionable children
		
	} else if (VersionableType.Geometry.equals(vType)) {
		rt.addElement(new VersionRef(VersionableType.VCImage,GeometryTable.table.imageRef));
			
	} else if (VersionableType.MathDescription.equals(vType)) {
		rt.addElement(new VersionRef(VersionableType.Geometry,MathDescTable.table.geometryRef));
			
	} else if (VersionableType.Model.equals(vType)) {
		//Model has no Versionable children
		
	} else if (VersionableType.SimulationContext.equals(vType)) {
		rt.addElement(new VersionRef(VersionableType.MathDescription,SimContextTable.table.mathRef));
		rt.addElement(new VersionRef(VersionableType.Geometry,SimContextTable.table.geometryRef));
		rt.addElement(new VersionRef(VersionableType.Model,SimContextTable.table.modelRef));
		
	} else if (VersionableType.Simulation.equals(vType)) {
		rt.addElement(new VersionRef(VersionableType.MathDescription,SimulationTable.table.mathRef));
		
	} else if (VersionableType.BioModelMetaData.equals(vType)) {
		rt.addElement(new VersionRef(VersionableType.Model,BioModelTable.table.modelRef));
		rt.addElement(new VersionRef(VersionableType.Simulation, BioModelSimulationLinkTable.table.simRef, BioModelSimulationLinkTable.table.bioModelRef));
		rt.addElement(new VersionRef(VersionableType.SimulationContext, BioModelSimContextLinkTable.table.simContextRef, BioModelSimContextLinkTable.table.bioModelRef));
		
	} else if (VersionableType.MathModelMetaData.equals(vType)) {
		rt.addElement(new VersionRef(VersionableType.MathDescription,MathModelTable.table.mathRef));
		rt.addElement(new VersionRef(VersionableType.Simulation, MathModelSimulationLinkTable.table.simRef, MathModelSimulationLinkTable.table.mathModelRef));
		
	} else {
		throw new IllegalArgumentException("getReferencingVersionableTypes(" + vType + "): Unknown version type");
	}
	return rt;
}


/**
 * Insert the method's description here.
 * Creation date: (2/4/01 9:28:11 AM)
 * @return java.util.Date
 * @param rset java.sql.ResultSet
 * @param field cbit.sql.Field
 */
public static java.util.Date getDate(ResultSet rset, String field) throws java.sql.SQLException {
	java.util.Date date = null;
	//
	// Do this stuff because ResultSet only returns java.sql.Date without time information
	//
	java.sql.Date DBDate = rset.getDate(field);
	if (DBDate==null || rset.wasNull()){
		return null;
	}
	java.sql.Time DBTime = rset.getTime(field);
	if (DBTime==null || rset.wasNull()){
		return null;
	}
	java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
	try{
		date = sdf.parse(DBDate + " " + DBTime);
	}catch(ParseException e){
		throw new RuntimeException("VersionTable.getDate()\n"+e.getClass().getName()+"\n"+e.getMessage());
	}
	return date;
}


/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public static java.util.Vector getReferencingVersionableTypes(VersionableType vType) {
	//
	// VersionableTypes that depend(referenc) on vType
	//
	java.util.Vector rt = new java.util.Vector();
	if (VersionableType.VCImage.equals(vType)) {
		rt.addElement(new VersionRef(vType, ImageTable.table.versionBranchPointRef));
		rt.addElement(new VersionRef(VersionableType.Geometry, GeometryTable.table.imageRef));
		
	} else if (VersionableType.Geometry.equals(vType)) {
		rt.addElement(new VersionRef(vType, GeometryTable.table.versionBranchPointRef));
		rt.addElement(new VersionRef(VersionableType.MathDescription, MathDescTable.table.geometryRef));
		rt.addElement(new VersionRef(VersionableType.SimulationContext, SimContextTable.table.geometryRef));
		
	} else if (VersionableType.MathDescription.equals(vType)) {
		rt.addElement(new VersionRef(vType, MathDescTable.table.versionBranchPointRef));
		rt.addElement(new VersionRef(VersionableType.SimulationContext, SimContextTable.table.mathRef));
		rt.addElement(new VersionRef(VersionableType.MathModelMetaData, MathModelTable.table.mathRef));
		//rt.addElement(new VersionRef(VersionableType.Simulation, SimulationTable.table.mathRef));
		
	} else if (VersionableType.Model.equals(vType)) {
		rt.addElement(new VersionRef(vType, ModelTable.table.versionBranchPointRef));
		rt.addElement(new VersionRef(VersionableType.SimulationContext, SimContextTable.table.mathRef));
		
	} else if (VersionableType.SimulationContext.equals(vType)) {
		rt.addElement(new VersionRef(vType, SimContextTable.table.versionBranchPointRef));
		rt.addElement(new VersionRef(VersionableType.BioModelMetaData, BioModelSimContextLinkTable.table.simContextRef, BioModelSimContextLinkTable.table.bioModelRef));
		
	} else if (VersionableType.Simulation.equals(vType)) {
		rt.addElement(new VersionRef(vType, SimulationTable.table.versionBranchPointRef));
		rt.addElement(new VersionRef(vType, SimulationTable.table.versionParentSimRef));
		rt.addElement(new VersionRef(VersionableType.BioModelMetaData, BioModelSimulationLinkTable.table.simRef, BioModelSimulationLinkTable.table.bioModelRef));
		rt.addElement(new VersionRef(VersionableType.MathModelMetaData, MathModelSimulationLinkTable.table.simRef, MathModelSimulationLinkTable.table.mathModelRef));
		
	} else if (VersionableType.BioModelMetaData.equals(vType)) {
		rt.addElement(new VersionRef(vType, BioModelTable.table.versionBranchPointRef));
		
	} else if (VersionableType.MathModelMetaData.equals(vType)) {
		rt.addElement(new VersionRef(vType, MathModelTable.table.versionBranchPointRef));
		
	} else {
		throw new IllegalArgumentException("getReferencingVersionableTypes(" + vType + "): Unknown version type");
	}
	return rt;
}


/**
 * This method was created in VisualAge.
 * @return cbit.util.Version
 * @param rset ResultSet
 * @param log SessionLog
 */
public static Version getVersion(ResultSet rset, cbit.util.document.GroupAccess groupAccess,SessionLog log) throws SQLException ,DataAccessException{
	KeyValue vBranchPointRef = null;
	java.math.BigDecimal vBranchID = null;
	java.util.Date vDate = null;
	VersionFlag vFlag = null;
	String vAnnot = null;
	//
	java.math.BigDecimal vBranchPointRefDB = rset.getBigDecimal(versionBranchPointRef_ColumnName);
	if (rset.wasNull()) {
		vBranchPointRef = null;
	} else {
		vBranchPointRef = new KeyValue(vBranchPointRefDB);
	}
	java.math.BigDecimal vBranchIDDB = rset.getBigDecimal(versionBranchID_ColumnName);
	if (rset.wasNull()) {
		vBranchID = null;
	} else {
		vBranchID = vBranchIDDB;
	}
	vDate = getDate(rset,versionDate_ColumnName);
	if (vDate==null){
		throw new DataAccessException("could not parse date");
	}
	//
	vFlag = VersionFlag.fromInt(rset.getInt(VersionTable.versionFlag_ColumnName));
	//
	String vAnnotDB = rset.getString(versionAnnot_ColumnName);
	if (rset.wasNull()) {
		vAnnot = null;
	} else {
		vAnnot = cbit.util.TokenMangler.getSQLRestoredString(vAnnotDB);
	}
	boolean bFoundParentSimRefColumn = false;
	KeyValue parentSimRef = null;
	try {
		java.sql.ResultSetMetaData rsetMetaData = rset.getMetaData();
		int numColumns = rsetMetaData.getColumnCount();
		for (int i = 0; i < numColumns; i++){
			if (rsetMetaData.getColumnName(i+1).toUpperCase().endsWith(SimulationTable.table.versionParentSimRef.toString().toUpperCase())){
				bFoundParentSimRefColumn = true;
				break;
			}
		}
		if (bFoundParentSimRefColumn){
			java.math.BigDecimal parentSimRefDB = rset.getBigDecimal(SimulationTable.table.versionParentSimRef.toString());
			if (rset.wasNull()) {
				parentSimRef = null;
			} else {
				parentSimRef = new KeyValue(parentSimRefDB);
			}
		}
	}catch (SQLException e){
		e.printStackTrace(System.out);
		log.print("VersionTable.getVersion(): consuming exception for missing column "+versionParentSimRef_ColumnName+": "+e.getMessage());
	}
	//
	KeyValue key = new KeyValue(rset.getBigDecimal(Table.id_ColumnName));
	String name = rset.getString(VersionTable.name_ColumnName);
	String ownerName = rset.getString(cbit.vcell.modeldb.UserTable.table.userid.toString());
	KeyValue ownerID = new KeyValue(rset.getBigDecimal(VersionTable.ownerRef_ColumnName));
	cbit.util.document.User owner = new cbit.util.document.User(ownerName, ownerID);
	//cbit.vcell.server.AccessInfo privacy = new cbit.vcell.server.AccessInfo(rset.getInt(VersionTable.privacy_ColumnName));
	//
	if (bFoundParentSimRefColumn){
		return new SimulationVersion(key,name,owner,groupAccess,vBranchPointRef,vBranchID, vDate, vFlag, vAnnot, parentSimRef);
	}else{
		return new Version(key,name,owner,groupAccess,vBranchPointRef,vBranchID, vDate, vFlag, vAnnot);
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param versionPRef cbit.sql.KeyValue
 */
protected static String getVersionGroupSQLValue(Version version) {
	StringBuffer buffer = new StringBuffer();
	//
	buffer.append(version.getVersionKey().toString() + ",");
	buffer.append("'" + version.getName() + "',");
	buffer.append(version.getOwner().getID() + ",");
	buffer.append(version.getGroupAccess().getGroupid() + ",");
	//
	if (version.getBranchPointRefKey() != null) {
		buffer.append(version.getBranchPointRefKey().toString() + ",");
	} else {
		buffer.append("NULL" + ",");
	}
	//
	buffer.append(formatDateToOracle(version.getDate()) + ",");
	buffer.append(version.getFlag().getIntValue() + ",");
	if (version.getAnnot() != null) {
		buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(version.getAnnot())+"'");
	} else {
		buffer.append("NULL");
	}
	buffer.append(",");
	//
	if (version.getBranchID() != null) {
		buffer.append(version.getBranchID().toString());
	} else {
		buffer.append("NULL");
	}
	if (version instanceof SimulationVersion){
		SimulationVersion simVersion = (SimulationVersion)version;
		buffer.append(",");
		if (simVersion.getParentSimulationReference() != null) {
			buffer.append(simVersion.getParentSimulationReference().toString());
		} else {
			buffer.append("NULL");
		}
	}
	//
	// note: ommit the final ',' it isn't always needed
	//
	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.VersionTable
 * @param v cbit.sql.Versionable
 */
public static VersionTable getVersionTable(Versionable v) throws IllegalArgumentException {

	VersionableType vType = versionableTypeFromVersionable(v);
	return getVersionTable(vType);
}

/**
* This method was created in VisualAge.
* @return cbit.sql.VersionableType
* @param versionable cbit.sql.Versionable
*/
public static VersionableType versionableTypeFromVersionable(Versionable versionable) {
	if (versionable instanceof cbit.vcell.geometry.Geometry){
		return VersionableType.Geometry;
	}else if (versionable instanceof cbit.vcell.math.MathDescription){
		return VersionableType.MathDescription;
	}else if (versionable instanceof cbit.image.VCImage){
		return VersionableType.VCImage;
	}else if (versionable instanceof cbit.vcell.model.Model){
		return VersionableType.Model;
	}else if (versionable instanceof cbit.vcell.modelapp.SimulationContext){
		return VersionableType.SimulationContext;
	}else if (versionable instanceof cbit.vcell.simulation.Simulation){
		return VersionableType.Simulation;
	}else if (versionable instanceof cbit.vcell.biomodel.BioModelMetaData){
		return VersionableType.BioModelMetaData;
	}else if (versionable instanceof cbit.vcell.mathmodel.MathModelMetaData){
		return VersionableType.MathModelMetaData;
	}
	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.VersionTable
 * @param vType int
 */
public static VersionTable getVersionTable(VersionableType vType) throws IllegalArgumentException {

	if (VersionableType.VCImage.equals(vType)){
		return ImageTable.table;
	}else if (VersionableType.Geometry.equals(vType)){
		return GeometryTable.table;
	}else if (VersionableType.MathDescription.equals(vType)){
		return MathDescTable.table;
	}else if (VersionableType.Model.equals(vType)){
		return ModelTable.table;
	}else if (VersionableType.SimulationContext.equals(vType)){
		return SimContextTable.table;
	}else if (VersionableType.Simulation.equals(vType)){
		return SimulationTable.table;
	}else if (VersionableType.BioModelMetaData.equals(vType)){
		return BioModelTable.table;
	}else if (VersionableType.MathModelMetaData.equals(vType)){
		return MathModelTable.table;
	}else{
		throw new IllegalArgumentException("getVersionTable("+vType+"): Unknown version type");
	}
}
}