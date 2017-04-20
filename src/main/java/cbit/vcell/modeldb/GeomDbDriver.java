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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.Extent;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.image.BrowseImage;
import cbit.image.GifParsingException;
import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageCompressed;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.StarField;
import cbit.sql.Table;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.Filament;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
public class GeomDbDriver extends DbDriver {
	private final UserTable userTable = 					UserTable.table;
	private final GeometryTable geomTable = 				GeometryTable.table;
	private final ExtentTable extentTable = 				ExtentTable.table;
	private final SubVolumeTable subVolumeTable = 			SubVolumeTable.table;
	private final ImageTable imageTable = 					ImageTable.table;
	private final ImageDataTable imageDataTable = 			ImageDataTable.table;
	private final BrowseImageDataTable browseImageDataTable = BrowseImageDataTable.table;
	private final ImageRegionTable imageRegionTable = 		ImageRegionTable.table;
	private final FilamentTable filamentTable =				FilamentTable.table;
	private final CurveTable curveTable =					CurveTable.table;
	private final GeometricRegionTable geoRegionTable =		GeometricRegionTable.table;
	private final SurfaceDescriptionTable geoSurfaceTable =	SurfaceDescriptionTable.table;

/**
 * LocalDBManager constructor comment.
 */
public GeomDbDriver(SessionLog sessionLog) {
	super(sessionLog);
}


/**
 * only the owner can delete a Geometry
 */
private void deleteGeometrySQL(Connection con, User user,KeyValue geomKey) throws SQLException,DataAccessException,DependencyException {

	failOnExternalRefs(con, MathDescTable.table.geometryRef, VersionTable.getVersionTable(VersionableType.MathDescription), geomKey,GeometryTable.table);
	failOnExternalRefs(con, SimContextTable.table.geometryRef, VersionTable.getVersionTable(VersionableType.SimulationContext), geomKey,GeometryTable.table);
		
	String sql;
	
	KeyValue imageRefKey = getImageRefKeyFromGeometry(con, geomKey);
	KeyValue extentKey = getExtentRefKeyFromGeometry(con, geomKey);
	
	//Delete Geometry, all APPROPRIATE children go ON DELETE CASCADE

	sql = DatabasePolicySQL.enforceOwnershipDelete(user,geomTable,GeometryTable.table.id + " = " + geomKey);
	
//System.out.println(sql);

	updateCleanSQL(con,sql);

	//Do this last because sizetable is parent of geometry
	//Check if there is an image for this Geometry, if so Do Not delete SizeTable entry because ImageTable is using it
	//Don't have to check if other Geometries are using because Geometries never share sizetable
	if (imageRefKey == null) {
		sql = "DELETE FROM " + extentTable.getTableName() + " WHERE " + extentTable.id + " = " + extentKey;
		//System.out.println(sql);
		updateCleanSQL(con,sql);
	}
}


/**
 * only the owner can delete a Model
 */
private void deleteVCImageSQL(Connection con, User user, KeyValue imageKey) throws SQLException, DataAccessException,DependencyException {
	String sql;

	//Check if any geometries use this image, if so Do Not delete image and return error
	failOnExternalRefs(con, GeometryTable.table.imageRef, VersionTable.getVersionTable(VersionableType.Geometry), imageKey,ImageTable.table);

	
	KeyValue extentKey = getExtentRefKeyFromImage(con, imageKey);

	//Delete ImageTable entry, all children go ON DELETE CASCADE
	sql = DatabasePolicySQL.enforceOwnershipDelete(user,imageTable,imageTable.id + " = " + imageKey);
//System.out.println(sql);
	updateCleanSQL(con,sql);
	//
	
	//Delete SizeTable entry because we know there can't be any more refs to it.  Do Last because it is parent of image
	sql = "DELETE FROM " + extentTable.getTableName() + " WHERE " + extentTable.id + " = " + extentKey;
	//System.out.println(sql);
	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
public void deleteVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
				throws DependencyException, ObjectNotFoundException,
						SQLException,DataAccessException,PermissionException {
					
	deleteVersionableInit(con, user, vType, vKey);

	if (vType.equals(VersionableType.VCImage)){
		deleteVCImageSQL(con, user, vKey);
	}else if (vType.equals(VersionableType.Geometry)){
		deleteGeometrySQL(con, user, vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * getModel method comment.
 */
private KeyValue getExtentRefKeyFromGeometry(Connection con, KeyValue geomKey) throws SQLException, DataAccessException, ObjectNotFoundException {
	if (geomKey == null){
		throw new IllegalArgumentException("Improper parameters for getSizeKeyFromGeometry");
	}
	//log.print("GeomDbDriver.getSizeKeyFromGeometry(id="+geomKey+")");
	String sql;
	sql = 	" SELECT "+geomTable.extentRef+
			" FROM " + geomTable.getTableName()+
			" WHERE " + geomTable.id + " = " + geomKey;

//System.out.println(sql);
	
	//Connection con = conFact.getConnection();
	
	Statement stmt = con.createStatement();
 	try {		
		ResultSet rset = stmt.executeQuery(sql);
	
//showMetaData(rset);
	
		if (rset.next()) {
			return new KeyValue(rset.getBigDecimal(geomTable.extentRef.toString()));
		}else{
			throw new ObjectNotFoundException("getSizeKeyFromGeometry for Image id="+geomKey+" not found");
		}
 	}finally {
		stmt.close(); // Release resources include resultset
 	}
}


/**
 * getModel method comment.
 */
private KeyValue getExtentRefKeyFromImage(Connection con, KeyValue imageKey) throws SQLException, DataAccessException, ObjectNotFoundException {
	if (imageKey == null){
		throw new IllegalArgumentException("Improper parameters for getSizeKeyFromImage");
	}
	//log.print("GeomDbDriver.getSizeRefKeyFromImage(id="+imageKey+")");
	String sql;
	sql = 	" SELECT "+imageTable.extentRef+
			" FROM " + imageTable.getTableName()+
			" WHERE " + imageTable.id + " = " + imageKey;

//System.out.println(sql);
	
	//Connection con = conFact.getConnection();
	
	Statement stmt = con.createStatement();
 	try {		
		ResultSet rset = stmt.executeQuery(sql);
	
//showMetaData(rset);
	
		if (rset.next()) {
			return new KeyValue(rset.getBigDecimal(imageTable.extentRef.toString()));
		}else{
			throw new ObjectNotFoundException("getSizeKeyFromImage for Image id="+imageKey+" not found");
		}
 	}finally {
		stmt.close(); // Release resources include resultset
 	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/29/00 2:10:42 PM)
 * @param con java.sql.Connection
 * @param geom cbit.vcell.geometry.Geometry
 */
private void getFilaments(Connection con, Geometry geom) throws SQLException, DataAccessException {
	String sql = null;
	sql = 	" SELECT " + 	filamentTable.filamentName.getQualifiedColName() + "," +
							curveTable.curveData.getQualifiedColName() +
			" FROM " + 	filamentTable.getTableName() + ","+
						curveTable.getTableName() +
			" WHERE " + filamentTable.geometryRef.getQualifiedColName() + " = " + geom.getVersion().getVersionKey() +
			" AND " + curveTable.filamentRef.getQualifiedColName() + " = " + filamentTable.id.getQualifiedColName();
//System.out.println(sql);
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while(rset.next()){
			String filColName = filamentTable.filamentName.toString();
			String filamentType = rset.getString(filColName);
			String curveColName = curveTable.curveData.toString();
			//Curve curve = CurveTable.decodeCurve(new String(rset.getBytes(curveColName)));
			String curveString = (String) DbDriver.getLOB(rset,curveColName);
			Curve curve = CurveTable.decodeCurve(curveString);
			//
			geom.getGeometrySpec().getFilamentGroup().addCurve(filamentType,curve);
		}
	}catch(Exception e){
		throw new DataAccessException(e.toString());
	} finally {
		stmt.close(); // Release resources include resultset
	}
}


/**
 * getModel method comment.
 */
private Geometry getGeometry(QueryHashtable dbc, Connection con, User user, KeyValue geomKey, boolean bCheckPermission) throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null || geomKey == null){
		throw new IllegalArgumentException("Improper parameters for getGeometry");
	}
	//log.print("GeomDbDriver.getGeometry(user="+user+", id="+geomKey+")");
	String sql;
	Field[] f = {new StarField(geomTable),userTable.userid,extentTable.extentX,extentTable.extentY,extentTable.extentZ};
	Table[] t = {geomTable,userTable,extentTable};
	String condition = geomTable.id.getQualifiedColName() + " = " + geomKey +
					" AND " + userTable.id.getQualifiedColName() + " = " + geomTable.ownerRef.getQualifiedColName() +
					" AND " + extentTable.id.getQualifiedColName() + " = " + geomTable.extentRef.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null,bCheckPermission);
//System.out.println(sql);
	
  	Geometry geom = null;
	//Connection con = conFact.getConnection();
	Statement stmt = con.createStatement();
  	try{
		ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()) {
			//This geometry privacy flag gives this user permission to access
			geom = getGeometry(dbc, con, user,rset);
		}else{
			//Due to change years ago, Geometry is no longer a TopLevel object so
			//see if at least 1 geometry parents (Mathmodels and/or BioModels) are shared with this user
			if(bCheckPermission){
				rset.close();
				String parentSQL = GeometryTable.getParentsPermissionSQL(geomKey, user);
				rset = stmt.executeQuery(parentSQL);
				if (rset.next()) {
					// At least 1 parent of the geometry exists that's shared to this user so give them the geometry
					rset.close();
					//Get the geometry without checking the geometry permission
					sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null,false);
					rset = stmt.executeQuery(sql);
					if(rset.next()){
						geom = getGeometry(dbc, con, user,rset);
					}
				}
			}
			if(geom == null){
				throw new ObjectNotFoundException("Geometry id="+geomKey+" not found for user '"+user+"'");
			}
		}
  	}finally{
		stmt.close(); // Release resources include resultset
  	}
	
	return geom;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Model
 * @param rset java.sql.ResultSet
 */
private Geometry getGeometry(QueryHashtable dbc, Connection con, User user, ResultSet rset) throws SQLException, DataAccessException {
	//
	// get Image reference
	//
	java.math.BigDecimal bigD = rset.getBigDecimal(geomTable.imageRef.toString()/*,0*/);
	KeyValue imageRef = null;
	if (!rset.wasNull()){
		imageRef = new KeyValue(bigD);
	}
	//
	// get geometry object
	//
	Geometry tempGeometry = null;
	try {
		tempGeometry = geomTable.getGeometry(rset,con,log);
	}catch (PropertyVetoException e){
		throw new DataAccessException(e.getMessage());
	}
	Geometry geom = null;
	//
	// get image for this geometry
	//
	VCImage vcImage = null;
	if(imageRef != null){
		// permission checking for the image is disabled because it is a child of this geometry
		vcImage = getVCImage(dbc, con, user,imageRef,false);
	}
	if (vcImage!=null){
		geom = new Geometry(tempGeometry,vcImage);
	}else{
		geom = tempGeometry;
	}
	//
	// get SubVolumes for this geometry
	//
	SubVolume subVolumes[] = getSubVolumesFromGeometry(dbc, con, geom.getVersion().getVersionKey());
	if (subVolumes!=null){
		try {
			geom.getGeometrySpec().setSubVolumes(subVolumes);
		}catch (java.beans.PropertyVetoException e){
			log.exception(e);
			throw new DataAccessException(e.getMessage());
		}
	}
	//
	// set GeometrySurfaceDescription for this geometry
	//
	if (geom.getDimension()>0){
		getSurfaceDescription(con,geom);
	}
	//
	// set Filaments for this geometry
	//
	if (geom.getDimension()>0){
		getFilaments(con,geom);
	}
	
	//
	// get SurfaceClasses for this geometry
	//
	SurfaceClass[] surfaceClasses = getSurfaceClassesFromGeometry(dbc, con, geom.getVersion().getVersionKey());
	if (surfaceClasses!=null){
		try {
			geom.getGeometrySurfaceDescription().setSurfaceClasses(surfaceClasses);
		}catch (java.beans.PropertyVetoException e){
			log.exception(e);
			throw new DataAccessException(e.getMessage());
		}
	}

	return geom;
}


/**
 * getModel method comment.
 */
private KeyValue getImageRefKeyFromGeometry(Connection con, KeyValue geomKey) throws SQLException, DataAccessException, ObjectNotFoundException {
	if (geomKey == null){
		throw new IllegalArgumentException("Improper parameters for getImageRefKeyFromGeometry");
	}
	//log.print("GeomDbDriver.getImageRefKeyFromGeometry(id="+geomKey+")");
	String sql;
	sql = 	" SELECT "+geomTable.imageRef+
			" FROM " + geomTable.getTableName()+
			" WHERE " + geomTable.id + " = " + geomKey;

//System.out.println(sql);
	
	//Connection con = conFact.getConnection();
	
	Statement stmt = con.createStatement();
 	try {		
	ResultSet rset = stmt.executeQuery(sql);
	
//showMetaData(rset);
	
		if (rset.next()) {
			java.math.BigDecimal iR = rset.getBigDecimal(geomTable.imageRef.toString());
			if(rset.wasNull()){
				return null;
			}
			return new KeyValue(iR);
		}else{
			throw new ObjectNotFoundException("getSizeKeyFromGeometry for Image id="+geomKey+" not found");
		}
 	}finally {
		stmt.close(); // Release resources include resultset
 	}
}


/**
 * This method was created in VisualAge.
 * @param vcImage cbit.image.VCImage
 */
private void getImageRegionsForVCImage(QueryHashtable dbc, Connection con, VCImage vcImage) throws SQLException, DataAccessException {
	String sql;
	sql = " SELECT *" +
	//
	" FROM " + imageRegionTable.getTableName() +
	//
	" WHERE " + imageRegionTable.imageRef + " = " + vcImage.getVersion().getVersionKey() +
	" ORDER BY " + imageRegionTable.id;
	//
	//System.out.println(sql);
	//
	//Connection con = conFact.getConnection();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//
		// get vcPixelClass objects and give them to vcImage
		//
		Vector<VCPixelClass> vcpcVector = new Vector<VCPixelClass>();
		while (rset.next()) {
			KeyValue vcpcKey = new KeyValue(rset.getBigDecimal(imageRegionTable.id.toString()));
			//
			// try to get PixelClass from object cache
			//
			VCPixelClass vcpc = (VCPixelClass) dbc.get(vcpcKey);
			if (vcpc == null) {
				String vcpcName = rset.getString(imageRegionTable.regionName.toString());
				int vcpcPixVal = new Integer(rset.getInt(imageRegionTable.pixelValue.toString())).intValue();
				vcpc = new VCPixelClass(vcpcKey, vcpcName, vcpcPixVal);
				//
				// put the ImageRegion in the object cache
				//
				dbc.put(vcpcKey,vcpc);
			}
			vcpcVector.addElement(vcpc);
		}
		VCPixelClass vcPixelClasses[] = (VCPixelClass[])BeanUtils.getArray(vcpcVector,VCPixelClass.class);
		vcImage.setPixelClasses(vcPixelClasses);
		if(vcImage instanceof VCImageCompressed){
			//Fix out of memory error when a BioModel has many apps with large geometries
			((VCImageCompressed)vcImage).nullifyUncompressedPixels();
		}
		
	} catch (PropertyVetoException e) {
		throw new DataAccessException(e.getMessage());
	} finally {
		stmt.close(); // Release resources include resultset
	}
}


/**
 * This method was created in VisualAge.
 * @param vcImage cbit.image.VCImage
 */
private VCPixelClass getPixelClass(QueryHashtable dbc, Connection con, KeyValue imageRegionKey) throws SQLException, DataAccessException {

	//
	// check object cache first
	//
	VCPixelClass vcPixelClass = (VCPixelClass) dbc.get(imageRegionKey);
	if (vcPixelClass!=null){
		return vcPixelClass;
	}
	
	String sql;
	sql =	" SELECT *" +
			" FROM  " + imageRegionTable.getTableName() +
			" WHERE " + imageRegionTable.id + " = " + imageRegionKey;
	//
	//System.out.println(sql);
	//
	//Connection con = conFact.getConnection();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//
		// get vcImageRegion object and stick in object cache
		//
		if (rset.next()) {
			KeyValue vcpcKey = new KeyValue(rset.getBigDecimal(imageRegionTable.id.toString()));
			String rName = rset.getString(imageRegionTable.regionName.toString()).trim();
			int pixValue = rset.getInt(imageRegionTable.pixelValue.toString());
			vcPixelClass = new VCPixelClass(vcpcKey,rName,pixValue);
			dbc.put(vcpcKey,vcPixelClass);
		}else{
			throw new ObjectNotFoundException("ImageRegion("+imageRegionKey+") not found");
		}
		return vcPixelClass;
		
	} finally {
		stmt.close(); // Release resources include resultset
	}
}

/**
 * getModel method comment.
 */
private SubVolume getSubVolume(QueryHashtable dbc, Connection con, ResultSet rset) throws SQLException, DataAccessException, ExpressionException {

	//showMetaData(rset);

	//
	// if already in cache, then return that instance instead of a new one
	//
	KeyValue svKey = new KeyValue(rset.getBigDecimal(subVolumeTable.id.toString()));
	SubVolume subVolume = (SubVolume) dbc.get(svKey);
	if (subVolume != null) {
		return subVolume;
	}

	java.math.BigDecimal imageRegionKeyBigDecimal = rset.getBigDecimal(subVolumeTable.imageRegionRef.toString());
	KeyValue imageRegionKey = null;
	if (!rset.wasNull()){
		imageRegionKey = new KeyValue(imageRegionKeyBigDecimal);
	}
	
	if (imageRegionKey == null) {
		subVolume = subVolumeTable.getAnalyticOrCompartmentSubVolume(svKey, rset, log);
	} else {
		VCPixelClass vcPixelClass = getPixelClass(dbc, con, imageRegionKey);
		subVolume = subVolumeTable.getImageSubVolume(svKey, rset, log, vcPixelClass);
	}
	//
	// put newly read SumVolume into object cache
	//
	dbc.put(svKey,subVolume);
	return subVolume;
}

private SurfaceClass getSurfaceClass(QueryHashtable dbc, Connection con, ResultSet rset) throws SQLException, DataAccessException, ExpressionException {

	//showMetaData(rset);

	//
	// if already in cache, then return that instance instead of a new one
	//
	KeyValue surfaceClassKey = new KeyValue(rset.getBigDecimal(SurfaceClassTable.table.id.toString()));
	SurfaceClass surfaceClass = (SurfaceClass) dbc.get(surfaceClassKey);
	if (surfaceClass != null) {
		return surfaceClass;
	}

	String surfaceClassName = rset.getString(SurfaceClassTable.table.name.toString());
	BigDecimal subVolumeRef1 = rset.getBigDecimal(SurfaceClassTable.table.subVolumeRef1.toString());
	BigDecimal subVolumeRef2 = rset.getBigDecimal(SurfaceClassTable.table.subVolumeRef2.toString());
	KeyValue subVolumeref1Key = null;
	KeyValue subVolumeref2Key = null;
	HashSet<SubVolume> subVolumeSet = new HashSet<SubVolume>();
	if(subVolumeRef1 != null){
		subVolumeref1Key = new KeyValue(subVolumeRef1);
		SubVolume subvolume = (SubVolume)dbc.get(subVolumeref1Key);
		if(subvolume != null){
			subVolumeSet.add(subvolume);
		}else{
			throw new DataAccessException("Subvolume key="+subVolumeref1Key+" not found in cache");
		}
	}
	if(subVolumeRef2 != null){
		subVolumeref2Key = new KeyValue(subVolumeRef2);
		SubVolume subvolume = (SubVolume)dbc.get(subVolumeref2Key);
		if(subvolume != null){
			subVolumeSet.add(subvolume);
		}else{
			throw new DataAccessException("Subvolume key="+subVolumeref1Key+" not found in cache");
		}
	}

	SurfaceClass surfaceclass = new SurfaceClass(subVolumeSet, surfaceClassKey, surfaceClassName);
	//
	// put newly read SumVolume into object cache
	//
	dbc.put(surfaceClassKey,surfaceclass);
	return surfaceclass;
}


/**
 * getModel method comment.
 */
private SubVolume[] getSubVolumesFromGeometry(QueryHashtable dbc, Connection con, KeyValue geomKey) throws SQLException, DataAccessException {
	//log.print("GeomDbDriver.getSubVolumesFromGeometry(geometryKey="+geomKey+")");
	String sql;
	
	sql = 	" SELECT * " +
			" FROM " + subVolumeTable.getTableName() +
			" WHERE " + subVolumeTable.geometryRef + " = " + geomKey + 
			" ORDER BY " + subVolumeTable.ordinal;

//System.out.println(sql);
	
	Statement stmt = con.createStatement();
	Vector<SubVolume> subVolumeList = new Vector<SubVolume>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
	
//showMetaData(rset);
		SubVolume subVolume = null;
		while (rset.next()) {
			
			subVolume = getSubVolume(dbc, con,rset);

			subVolumeList.addElement(subVolume);
			
		}
		if (subVolumeList.size()>0){
			SubVolume subVolumes[] = new SubVolume[subVolumeList.size()];
			subVolumeList.copyInto(subVolumes);
			return subVolumes;
		}else{
			return null;
		}
	}catch (ExpressionException e){
		throw new DataAccessException(e.getMessage());
	}finally{
		stmt.close(); // Release resources include resultset		
	}
}

private SurfaceClass[] getSurfaceClassesFromGeometry(QueryHashtable dbc, Connection con, KeyValue geomKey) throws SQLException, DataAccessException {
	//log.print("GeomDbDriver.getSubVolumesFromGeometry(geometryKey="+geomKey+")");
	String sql;
	
	sql = 	" SELECT * " +
			" FROM " + SurfaceClassTable.table.getTableName() +
			" WHERE " + SurfaceClassTable.table.geometryRef + " = " + geomKey;

//System.out.println(sql);
	
	Statement stmt = con.createStatement();
	Vector<SurfaceClass> surfaceClassV = new Vector<SurfaceClass>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
	
//showMetaData(rset);
		SurfaceClass surfaceClass = null;
		while (rset.next()) {
			
			surfaceClass = getSurfaceClass(dbc, con,rset);

			surfaceClassV.addElement(surfaceClass);
			
		}
		if (surfaceClassV.size()>0){
			return surfaceClassV.toArray(new SurfaceClass[0]);
		}else{
			return null;
		}
	}catch (ExpressionException e){
		throw new DataAccessException(e.getMessage());
	}finally{
		stmt.close(); // Release resources include resultset		
	}
}

/**
 * Insert the method's description here.
 * Creation date: (7/29/00 2:10:42 PM)
 * @param con java.sql.Connection
 * @param geom cbit.vcell.geometry.Geometry
 */
private void getSurfaceDescription(Connection con, Geometry geom) throws SQLException, DataAccessException {
//System.out.println(sql);
	Statement stmt = con.createStatement();
	try {
		String sql = null;
		//
		// read sampleSize and filterFrequency from GeometrySurfaceTable.
		//
		sql = 	" SELECT " + geoSurfaceTable.getTableName()+".* "+
				" FROM " + 	geoSurfaceTable.getTableName() +
				" WHERE " + geoSurfaceTable.geometryRef.getQualifiedColName() + " = " + geom.getVersion().getVersionKey();
System.out.println(sql);
		ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()){
			geoSurfaceTable.populateGeometrySurfaceDescription(rset,geom.getGeometrySurfaceDescription(),log);
			rset.close();
		}else{
			log.alert("surface description not found for geometry "+geom.getVersion().toString());
			rset.close();
			return;
		}
		
		//
		// read volume regions from GeometricRegionTable
		//
		sql = 	" SELECT " + geoRegionTable.name.getQualifiedColName() + ", " +
							geoRegionTable.size.getQualifiedColName() + ", " +
							geoRegionTable.sizeUnit.getQualifiedColName() + ", " +
							geoRegionTable.subVolumeRef.getQualifiedColName() + ", " +
							geoRegionTable.regionID.getQualifiedColName() +
				" FROM " + 	geoRegionTable.getTableName() +
				" WHERE " + geoRegionTable.geometryRef.getQualifiedColName() + " = " + geom.getVersion().getVersionKey() +
				" AND " + geoRegionTable.type + " = " + GeometricRegionTable.TYPE_VOLUME;
System.out.println(sql);
		rset = stmt.executeQuery(sql);
		Vector<GeometricRegion> regionList = new Vector<GeometricRegion>();
		while (rset.next()){
			VolumeGeometricRegion volumeRegion = geoRegionTable.getVolumeRegion(rset,geom,log);
			regionList.add(volumeRegion);
		}
		VolumeGeometricRegion volumeRegions[] = (VolumeGeometricRegion[])BeanUtils.getArray(regionList,VolumeGeometricRegion.class);
		
		//
		// read surface regions from GeometricRegionTable
		//
		sql = 	" SELECT " + "surfTable." + geoRegionTable.name.getUnqualifiedColName() + ", " +
							"surfTable." + geoRegionTable.size.getUnqualifiedColName() + ", " +
							"surfTable." + geoRegionTable.sizeUnit.getUnqualifiedColName() + ", " +
							"vol1Table.name as " + GeometricRegionTable.VOLUME1_NAME_COLUMN + ", " +
							"vol2Table.name as " + GeometricRegionTable.VOLUME2_NAME_COLUMN + " " +
				" FROM " + 	geoRegionTable.getTableName() + " surfTable, " + 
							geoRegionTable.getTableName() + " vol1Table, "+
							geoRegionTable.getTableName() + " vol2Table "+
				" WHERE surfTable." + geoRegionTable.geometryRef.getUnqualifiedColName() + " = " + geom.getVersion().getVersionKey() +
				" AND vol1Table.id = surfTable." + geoRegionTable.volRegion1.getUnqualifiedColName() +
				" AND vol2Table.id = surfTable." + geoRegionTable.volRegion2.getUnqualifiedColName() +
				" AND surfTable." + geoRegionTable.type.getUnqualifiedColName() + " = " + GeometricRegionTable.TYPE_SURFACE;
System.out.println(sql);
		rset = stmt.executeQuery(sql);
		while (rset.next()){
			SurfaceGeometricRegion surfaceRegion = geoRegionTable.getSurfaceRegion(rset, volumeRegions, geom.getUnitSystem(), log);
			regionList.add(surfaceRegion);
		}

		//
		// set regions onto the geometrySurfaceDescription
		//
		GeometricRegion regions[] = (GeometricRegion[])BeanUtils.getArray(regionList,GeometricRegion.class);
		geom.getGeometrySurfaceDescription().setGeometricRegions(regions);
		
		
	}catch(Exception e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.toString());
	} finally {
		stmt.close(); // Release resources include resultset
	}

}


/**
 * getModel method comment.
 */
private VCImage getVCImage(QueryHashtable dbc, Connection con, User user, KeyValue imageKey, boolean bCheckPermission) 
			throws SQLException, DataAccessException, ObjectNotFoundException {

	//log.print("GeomDbDriver.getImage(user="+user+", id="+imageKey+")");
	String sql;
	Field[] f = {new StarField(imageTable),userTable.userid,imageDataTable.data,new StarField(extentTable)};
	Table[] t = {imageTable,userTable,imageDataTable,extentTable};
	String condition =	imageTable.id.getQualifiedColName() + " = " + imageKey +
					" AND " + imageTable.ownerRef.getQualifiedColName() + " = " + userTable.id.getQualifiedColName() +
					" AND " + imageTable.id.getQualifiedColName() + " = " + imageDataTable.imageRef.getQualifiedColName()+
					" AND " + imageTable.extentRef.getQualifiedColName() + " = " + extentTable.id.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null,bCheckPermission);
//System.out.println(sql);
	
	//Connection con = conFact.getConnection();
	
	VCImage vcImage = null;
	Statement stmt = con.createStatement();
 	try {		
	ResultSet rset = stmt.executeQuery(sql);
	
		if (rset.next()) {
			vcImage = imageTable.getImage(rset,con,log,imageDataTable);
			getImageRegionsForVCImage(dbc, con, vcImage);
		}else{
			throw new ObjectNotFoundException("Image id="+imageKey+" not found for user '"+user+"'");
		}
 	}finally {
		stmt.close(); // Release resources include resultset
 	}
	
	return vcImage;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public Versionable getVersionable(QueryHashtable dbc, Connection con, User user, VersionableType vType, KeyValue vKey, boolean bCheckPermission) 
					throws ObjectNotFoundException, SQLException, DataAccessException {
						
	Versionable versionable = (Versionable) dbc.get(vKey);
	if (versionable != null) {
		return versionable;
	} else {
		if (vType.equals(VersionableType.VCImage)){
			versionable = getVCImage(dbc, con, user, vKey,bCheckPermission);
		}else if (vType.equals(VersionableType.Geometry)){
			versionable = getGeometry(dbc, con, user, vKey,bCheckPermission);
		}else{
			throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
		}
		dbc.put(versionable.getVersion().getVersionKey(),versionable);
	}
	return versionable;
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertBrowseImageDataSQL(Connection con, KeyValue key, KeyValue imageKey, VCImage image) 
				throws SQLException, DataAccessException,ImageException,GifParsingException{

	String sql;
	sql = "INSERT INTO " + browseImageDataTable.getTableName() + " " + 
		browseImageDataTable.getSQLColumnList() + " VALUES " + 
		browseImageDataTable.getSQLValueList(key, imageKey);
	//	System.out.println(sql);

	byte[] gifEncodedImage = null;
	try{
		if(image != null){
			if(image.getNumZ() > 1){
				byte[] sliceBytes = new byte[image.getNumX()*image.getNumY()];
				System.arraycopy(image.getPixels(), 0, sliceBytes, 0, sliceBytes.length);
				gifEncodedImage =
					BrowseImage.makeBrowseGIFImage(
						new VCImageUncompressed(null, sliceBytes, new Extent(1, 1, 1), image.getNumX(),image.getNumY(), 1)).getGifEncodedData();
			}else{
				gifEncodedImage = BrowseImage.makeBrowseGIFImage(image).getGifEncodedData();				
			}
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	if(gifEncodedImage == null){
		gifEncodedImage =
			BrowseImage.makeBrowseGIFImage(
				new VCImageUncompressed(null, new byte[BrowseImage.BROWSE_XSIZE*BrowseImage.BROWSE_YSIZE], new Extent(1, 1, 1), BrowseImage.BROWSE_XSIZE, BrowseImage.BROWSE_YSIZE, 1)).getGifEncodedData();
		
	}
	updateCleanSQL(con,sql);
	updateCleanLOB(	con,browseImageDataTable.id.toString(),key,
					browseImageDataTable.tableName,
					browseImageDataTable.data.getUnqualifiedColName(),
					gifEncodedImage);
	/*
	PreparedStatement pps;
	pps = con.prepareStatement(sql);
	try {
		pps.setBytes(1, BrowseImage.makeBrowseGIFImage(image).getGifEncodedData());
		pps.executeUpdate();
	} finally {
		pps.close();
	}
	*/
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertExtentSQL(Connection con, KeyValue key, double extentX, double extentY, double extentZ) throws SQLException  {
	String sql;
	sql = "INSERT INTO " + extentTable.getTableName() + " " + 
		extentTable.getSQLColumnList() + " VALUES " + 
		extentTable.getSQLValueList(key, extentX, extentY, extentZ);
	//	System.out.println(sql);

	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertFilamentsSQL(Connection con, Geometry geom, KeyValue geomKey) throws SQLException, DataAccessException {
	String sql;
	Filament[] filaments = geom.getGeometrySpec().getFilamentGroup().getFilaments();
	for (int i = 0; i < filaments.length; i++) { //Iterate through Filaments
		KeyValue newFilamentKey = getNewKey(con);
		//
		//Insert Filament
		//
		Filament currentFilament = filaments[i];
		sql = 	"INSERT INTO " + filamentTable.getTableName() + " " + filamentTable.getSQLColumnList() + 
				" VALUES " + filamentTable.getSQLValueList(newFilamentKey, currentFilament.getName(), geomKey);
		updateCleanSQL(con, sql);
		//
		//Insert all Curves for this filament
		//
		Curve[] curves = currentFilament.getCurves();
		for (int j = 0; j < curves.length; j += 1) {
			KeyValue newCurveKey = getNewKey(con);
			sql = 	"INSERT INTO " + curveTable.getTableName() + " " + curveTable.getSQLColumnList() + 
					" VALUES " + curveTable.getSQLValueList(newCurveKey,newFilamentKey);
			//
			updateCleanSQL(con,sql);
			updateCleanLOB(	con,curveTable.id.toString(),newCurveKey,
					curveTable.tableName,
					curveTable.curveData.getUnqualifiedColName(),
					CurveTable.encodeCurve(curves[j]));
			/*
			PreparedStatement pps = con.prepareStatement(sql);
			try {
				pps.setBytes(1, CurveTable.encodeCurve(curves[j]).getBytes());
				pps.executeUpdate();
			} finally {
				pps.close();
			}
			*/
		}
	}
}


/**
 * This method was created in VisualAge.
 */
private void insertGeometry(InsertHashtable hash, QueryHashtable dbc, Connection con, User user, Geometry geom, KeyValue updatedImageKey, Version newVersion, boolean bVersionChildren) 
					throws ImageException, SQLException, DataAccessException,RecordChangedException {
	//log.print("GeomDbDriver.insertGeometry(" + geom + ")");

// everybody needs to be in synch 'cause children may be manipulated...
	geom.refreshDependencies();
	
	KeyValue extentKey = null;
	GeometrySpec geometrySpec = geom.getGeometrySpec();
	if (geometrySpec.getImage() != null) {
		////try {
			//imageVersionKey = hash.getDatabaseKey(geometrySpec.getImage());
			//if (imageVersionKey==null){
				//if(geometrySpec.getImage().getVersion()!=null && geometrySpec.getImage().getVersion().getVersionKey() != null){
					//imageVersionKey = updateVersionable(hash, con, user, geometrySpec.getImage(), bVersionChildren);
				//}else{
					//String imageName = geometrySpec.getImage().getName(); // + "_image";
					//while (isNameUsed(con,VersionableType.VCImage,user,imageName)){
						//imageName = cbit.util.TokenMangler.getNextRandomToken(imageName);
					//}
					//imageVersionKey = insertVersionable(hash, con, user, geometrySpec.getImage(),imageName ,bVersionChildren);
				//}
				//try{
					//geometrySpec.setImage(getVCImage(con,user,imageVersionKey));
				//}catch(PropertyVetoException e){
					//e.printStackTrace();
					//throw new DataAccessException(e.getMessage());
				//}
			//}
		////} catch (RecordChangedException rce) {
		////	throw rce;
		////}
		extentKey = getExtentRefKeyFromImage(con, updatedImageKey);
	}
	//
	if (extentKey == null) {
		extentKey = getNewKey(con);
		insertExtentSQL(con, extentKey, geometrySpec.getExtent().getX(), geometrySpec.getExtent().getY(), geometrySpec.getExtent().getZ());
	}
	//
	insertGeometrySQL(con, geom, updatedImageKey, extentKey, newVersion,user);
	hash.put(geom,newVersion.getVersionKey());
	if (updatedImageKey != null && !updatedImageKey.equals(geom.getGeometrySpec().getImage().getKey())){
		VCImage resavedImage = getVCImage(dbc, con, user, updatedImageKey, true);
		try {
			geom.getGeometrySpec().setImage(resavedImage);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new DataAccessException(e.getMessage());
		}
	}
	
	insertSubVolumesSQL(hash, con, geom, newVersion.getVersionKey());
	insertSurfaceClassesSQL(hash, con, geom, newVersion.getVersionKey());

	if (geom.getDimension()>0){
		insertGeometrySurfaceDescriptionSQL(hash, con, geom, newVersion.getVersionKey());
		
		insertFilamentsSQL(con,geom,newVersion.getVersionKey());
	}
}

private void insertSurfaceClassesSQL(InsertHashtable hash, Connection con, Geometry geom,KeyValue geomKey) throws SQLException, cbit.image.ImageException, DataAccessException, ObjectNotFoundException {
	if (geom.getGeometrySurfaceDescription() == null ||
			geom.getGeometrySurfaceDescription().getSurfaceClasses() == null) {
		return;
	}
	SurfaceClass surfaceClasses[] = geom.getGeometrySurfaceDescription().getSurfaceClasses();
	for (int i=0;i<surfaceClasses.length;i++){
		SurfaceClass surfaceClass = surfaceClasses[i];
		if (hash.getDatabaseKey(surfaceClass)==null){
			KeyValue newSurfaceClassKey = getNewKey(con);
			String sql = "INSERT INTO " + SurfaceClassTable.table.getTableName() + " " + SurfaceClassTable.table.getSQLColumnList() + 
				" VALUES " + SurfaceClassTable.table.getSQLValueList(hash,newSurfaceClassKey, geom, surfaceClass,geomKey);
	//System.out.println(sql);
			updateCleanSQL(con,sql);
			hash.put(surfaceClass,newSurfaceClassKey);
		}
	}
}

/**
 * This method was created in VisualAge.
 */
private void insertGeometrySQL(Connection con, Geometry geom, KeyValue imageKey, KeyValue sizeKey,Version version,User user) 
							throws SQLException,DataAccessException {
	String sql;
	Object[] o = {imageKey,geom,sizeKey};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,geomTable,o,version);
//System.out.println(sql);
	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
 //default access for this method, to allow retrofitting surfaces to geometries.
 void insertGeometrySurfaceDescriptionSQL(InsertHashtable hash, Connection con, Geometry geom,KeyValue geomKey) throws SQLException, cbit.image.ImageException, DataAccessException, ObjectNotFoundException {
	String sql;
	GeometrySurfaceDescription geoSurfaceDescription = geom.getGeometrySurfaceDescription();
	//
	// store GeometrySurfaceDescription (sampleSize and filterFrequency for now)
	//
	KeyValue newGeomSurfDescKey = getNewKey(con);
	sql = "INSERT INTO " + geoSurfaceTable.getTableName() + " " + geoSurfaceTable.getSQLColumnList() +
		" VALUES " + geoSurfaceTable.getSQLValueList(newGeomSurfDescKey,geoSurfaceDescription,geomKey);
//System.out.println(sql);
	updateCleanSQL(con,sql);

	//
	// store GeometricRegions
	//
	GeometricRegion regions[] = geoSurfaceDescription.getGeometricRegions();
	//if (regions==null){
		//try {
			//geoSurfaceDescription.updateAll();
			//regions = geoSurfaceDescription.getGeometricRegions();
		//}catch (Exception e){
			//log.exception(e);
			//throw new DataAccessException("failed to create regions: "+e.getMessage());
		//}
	//}
	if (regions==null){
		System.out.println("Geometry "+geom.getName()+"("+geom.getVersion()+") doesn't have region information");
		throw new DataAccessException("geometry '"+geom.getName()+" didn't have region information");
	}
	//
	// first, store volume regions ... (surfaces must reference them)
	//
	for (int i=0;i<regions.length;i++){
		if (regions[i] instanceof VolumeGeometricRegion){
			VolumeGeometricRegion volumeRegion = (VolumeGeometricRegion)regions[i];
			if (hash.getDatabaseKey(volumeRegion)==null){
				KeyValue newVolumeRegionKey = getNewKey(con);
				KeyValue subvolumeKey = hash.getDatabaseKey(volumeRegion.getSubVolume());
				sql = "INSERT INTO " + geoRegionTable.getTableName() + " " + geoRegionTable.getSQLColumnList() + 
					" VALUES " + geoRegionTable.getSQLValueList(newVolumeRegionKey,volumeRegion,subvolumeKey,geomKey);
		//System.out.println(sql);
				updateCleanSQL(con,sql);
				hash.put(volumeRegion,newVolumeRegionKey);
			}
		}
	}
	//
	// second, store surface regions ... (refering to the volumeRegions)
	//
	for (int i=0;i<regions.length;i++){
		if (regions[i] instanceof SurfaceGeometricRegion){
			SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)regions[i];
			if (hash.getDatabaseKey(surfaceRegion)==null){
				KeyValue newSurfaceRegionKey = getNewKey(con);
				KeyValue volumeRegion1Key = hash.getDatabaseKey((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[0]);
				KeyValue volumeRegion2Key = hash.getDatabaseKey((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[1]);
				sql = "INSERT INTO " + geoRegionTable.getTableName() + " " + geoRegionTable.getSQLColumnList() + 
					" VALUES " + geoRegionTable.getSQLValueList(newSurfaceRegionKey,surfaceRegion,volumeRegion1Key,volumeRegion2Key,geomKey);
		//System.out.println(sql);
				updateCleanSQL(con,sql);
				hash.put(surfaceRegion,newSurfaceRegionKey);
			}
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertImageDataSQL(Connection con, KeyValue key, KeyValue imageKey, VCImage image) throws SQLException, DataAccessException,ImageException {
	String sql;
	sql = "INSERT INTO " + imageDataTable.getTableName() + " " + imageDataTable.getSQLColumnList() + " VALUES " + 
		imageDataTable.getSQLValueList(key, imageKey);
//System.out.println(sql);
	updateCleanSQL(con,sql);
	updateCleanLOB(	con,imageDataTable.id.toString(),key,
			imageDataTable.tableName,
			imageDataTable.data.getUnqualifiedColName(),
			image.getPixelsCompressed());
	/*
	PreparedStatement pps;
	pps = con.prepareStatement(sql);
	try {
		pps.setBytes(1, image.getPixelsCompressed());
		pps.executeUpdate();
	} finally {
		pps.close();
	}
	*/
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertImageSQL(Connection con, VCImage image, KeyValue keySizeRef,Version version,User user) 
					throws SQLException, cbit.image.ImageException,DataAccessException {

	String sql;
	Object[] o = {image, keySizeRef};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,imageTable,o,version);
//System.out.println(sql);

	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertPixelClassSQL(Connection con, KeyValue key, KeyValue imageKey, VCPixelClass pc) throws SQLException, cbit.image.ImageException {
	String sql;
	sql = "INSERT INTO " + imageRegionTable.getTableName() + " " +
		imageRegionTable.getSQLColumnList() + " VALUES " +
		imageRegionTable.getSQLValueList(key, imageKey, pc);
//System.out.println(sql);

	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertSubVolumesSQL(InsertHashtable hash, Connection con, Geometry geom,KeyValue geomKey) throws SQLException, cbit.image.ImageException, DataAccessException, ObjectNotFoundException {
	String sql;
	SubVolume subVolumes[] = geom.getGeometrySpec().getSubVolumes();
	int ordinal = 0;
	for (int i=0;i<subVolumes.length;i++){
		SubVolume sv = (SubVolume) subVolumes[i];
		if (hash.getDatabaseKey(sv)==null){
			KeyValue newSVKey = getNewKey(con);
			sql = "INSERT INTO " + subVolumeTable.getTableName() + " " + subVolumeTable.getSQLColumnList() + 
				" VALUES " + subVolumeTable.getSQLValueList(hash,newSVKey, geom, sv,geomKey, ordinal);
	//System.out.println(sql);
			updateCleanSQL(con,sql);
			hash.put(sv,newSVKey);
		}
		ordinal++;
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
private void insertVCImage(InsertHashtable hash, Connection con, User user,VCImage image,Version newVersion)
				throws ImageException, DataAccessException, SQLException {

	//Connection con = conFact.getConnection();
	//KeyValue imageKey = getNewKey(con);
	KeyValue keySizeRef = getNewKey(con);
	KeyValue imageDataKey = getNewKey(con);
	KeyValue browseImageKey = getNewKey(con);
	insertExtentSQL(con, keySizeRef, image.getExtent().getX(), image.getExtent().getY(), image.getExtent().getZ());
	insertImageSQL(con, image, keySizeRef, newVersion,user);
	insertImageDataSQL(con, imageDataKey, newVersion.getVersionKey()/*imageKey*/, image);
	try{
		insertBrowseImageDataSQL(con, browseImageKey, newVersion.getVersionKey()/*imageKey*/, image);
	}catch(cbit.image.GifParsingException e){
		log.exception(e);
		throw new DataAccessException("Error Parsing BrowseImage",e);
	}
	VCPixelClass vcPixelClasses[] = image.getPixelClasses();
	for (int i = 0; i < vcPixelClasses.length; i++){
		KeyValue keyPixelClass = getNewKey(con);
		insertPixelClassSQL(con, keyPixelClass, newVersion.getVersionKey()/*imageKey*/, vcPixelClasses[i]);
		hash.put(vcPixelClasses[i],keyPixelClass);
	}
	hash.put(image,newVersion.getVersionKey());
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, VCImage vcImage, String name, boolean bVersion) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	Version newVersion = insertVersionableInit(hash, con, user, vcImage, name, vcImage.getDescription(), bVersion);
	try {
		insertVCImage(hash, con, user, vcImage, newVersion);
		return newVersion.getVersionKey();
	} catch (ImageException e) {
		log.exception(e);
		throw new DataAccessException("ImageException: " + e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, QueryHashtable dbc, Connection con, User user, Geometry geometry, KeyValue updatedImageKey, String name, boolean bVersion) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	Version newVersion = insertVersionableInit(hash, con, user, geometry, name, geometry.getDescription(), bVersion);
	try {
		insertGeometry(hash, dbc, con, user, geometry, updatedImageKey, newVersion,bVersion);
		return newVersion.getVersionKey();
	} catch (ImageException e) {
		log.exception(e);
		throw new DataAccessException("ImageException: " + e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash, Connection con, User user, VCImage vcImage, boolean bVersion) 
			throws DataAccessException, SQLException, RecordChangedException{
				
	Version newVersion = null;
	try {
		newVersion = updateVersionableInit(hash, con, user, vcImage, bVersion);
		insertVCImage(hash, con, user, vcImage, newVersion);
	} catch (ImageException e) {
		log.exception(e);
		throw new DataAccessException("ImageException: " + e.getMessage());
	}
	return newVersion.getVersionKey();
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash, QueryHashtable dbc, Connection con, User user, Geometry geometry, KeyValue updatedImageKey, boolean bVersion) 
			throws DataAccessException, SQLException, RecordChangedException{
				
	Version newVersion = null;
	try {
		newVersion = updateVersionableInit(hash, con, user, geometry, bVersion);
		insertGeometry(hash, dbc, con, user, geometry, updatedImageKey, newVersion, bVersion);
	} catch (ImageException e) {
		log.exception(e);
		throw new DataAccessException("ImageException: " + e.getMessage());
	}
	return newVersion.getVersionKey();
}
}
