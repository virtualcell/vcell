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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.image.VCImage;
import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.clientdb.ServerRejectedSaveException;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.vcell_4_8.MathMapping_4_8;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathCompareResults.Decision;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.model.Model;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
/**
 * Insert the type's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 * @author: Jim Schaff
 *
 * This class is responsible for the detailed change management, loading, and
 * saving of all primary VCell versionables (BioModel, MathModel, Geometry).
 * Then the entire versionable is sent to the client intact (whether in XML or
 * as a single Serialized object).
 *
 * ??
 * ?? ..... maybe ServerDocumentManager shouldn't cache anything !!!!!
 * ?? ......(XML selects are cheap and Versionables are cached by DbTopLevel)
 * ??
 * ?? This class caches only XML documents which assures immutable storage (Strings)
 * ?? and relies on XmlReader to produce java objects having no duplicate instances
 * ?? (eliminating the need for ReferenceResolverIntrospection.  This also eliminates
 * ?? the need for cloneSerializable for guarenteeing immutable caching.
 *
 * ServerDocumentManager is on the server so can take advantage of the existing
 * dbCacheTable for the Database server. 
 *
 */
public class ServerDocumentManager {
	private DatabaseServerImpl dbServer = null;
	private static Logger lg = LogManager.getLogger(ServerDocumentManager.class);

/**
 * ClientDocumentManager constructor comment.
 */
public ServerDocumentManager(DatabaseServerImpl argDbServerImpl) {
	this.dbServer = argDbServerImpl;
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/01 3:20:55 PM)
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
private void forceDeepDirtyIfForeign(User user,VCImage image) {
	//
	// ensures that saving a public "shared" geometry saves a deep clone as your own.
	//
	// assumes the following structure
	//
	// Image   .... (no children)
	//
	forceDirtyIfForeign(user,image);
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/01 3:20:55 PM)
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
private void forceDeepDirtyIfForeign(User user,BioModel bioModel) {
	//
	// ensures that saving a public "shared" model saves a deep clone as your own.
	//
	// assumes the following structure
	//
	//  BioModel
	//    Model
	//    SimulationContexts
	//      MathDescription
	//        Geometry
	//          Image
	//    Simulations
	//
	try {
		if (bioModel.getVersion()!=null && !bioModel.getVersion().getOwner().equals(user)){
			bioModel.setDescription("cloned from '"+bioModel.getName()+"' owned by user "+bioModel.getVersion().getOwner().getName()+"\n"+bioModel.getDescription());
			bioModel.clearVersion();
		}
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getLocalizedMessage(), e);
	}
	forceDirtyIfForeign(user,bioModel.getModel());
	SimulationContext simulationContexts[] = bioModel.getSimulationContexts();
	for (int i = 0; simulationContexts!=null && i < simulationContexts.length; i++){
		forceDirtyIfForeign(user,simulationContexts[i]);
		MathDescription mathDesc = simulationContexts[i].getMathDescription();
		if (mathDesc!=null){
			forceDirtyIfForeign(user,mathDesc);
			forceDirtyIfForeign(user,mathDesc.getGeometry());
			if (mathDesc.getGeometry().getGeometrySpec().getImage()!=null){
				forceDirtyIfForeign(user,mathDesc.getGeometry().getGeometrySpec().getImage());
			}
		}
	}
	Simulation simulations[] = bioModel.getSimulations();
	for (int i = 0; simulations!=null && i < simulations.length; i++){
		forceDirtyIfForeign(user,simulations[i]);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/01 3:20:55 PM)
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
private void forceDeepDirtyIfForeign(User user,Geometry geometry) {
	//
	// ensures that saving a public "shared" geometry saves a deep clone as your own.
	//
	// assumes the following structure
	//
	// Geometry
	//   Image
	//
	forceDirtyIfForeign(user,geometry);
	if (geometry.getGeometrySpec().getImage()!=null){
		forceDirtyIfForeign(user,geometry.getGeometrySpec().getImage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/01 3:20:55 PM)
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
private void forceDeepDirtyIfForeign(User user, MathModel mathModel) {
	//
	// ensures that saving a public "shared" mathModel saves a deep clone as your own.
	//
	// assumes the following structure
	//
	//  MathModel
	//    MathDescription
	//      Geometry
	//        Image
	//    Simulations
	//
	try {
		if (mathModel.getVersion()!=null && !mathModel.getVersion().getOwner().equals(user)){
			mathModel.setDescription("cloned from '"+mathModel.getName()+"' owned by user "+mathModel.getVersion().getOwner().getName()+"\n"+mathModel.getDescription());
			mathModel.clearVersion();
		}
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getLocalizedMessage(),e);
	}
	MathDescription mathDesc = mathModel.getMathDescription();
	if (mathDesc!=null){
		forceDirtyIfForeign(user,mathDesc);
		forceDirtyIfForeign(user,mathDesc.getGeometry());
		if (mathDesc.getGeometry().getGeometrySpec().getImage()!=null){
			forceDirtyIfForeign(user,mathDesc.getGeometry().getGeometrySpec().getImage());
		}
	}
	Simulation simulations[] = mathModel.getSimulations();
	for (int i = 0; simulations!=null && i < simulations.length; i++){
		forceDirtyIfForeign(user,simulations[i]);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/01 3:20:55 PM)
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
private void forceDeepDirtyIfForeign(User user, Simulation simulation) {
	//
	// ensures that saving a public "shared" mathModel saves a deep clone as your own.
	//
	// assumes the following structure
	//
	//  Simulation
	//    MathDescription
	//      Geometry
	//        Image
	//
	forceDirtyIfForeign(user,simulation);
	MathDescription mathDesc = simulation.getMathDescription();
	if (mathDesc!=null){
		forceDirtyIfForeign(user,mathDesc);
		forceDirtyIfForeign(user,mathDesc.getGeometry());
		if (mathDesc.getGeometry().getGeometrySpec().getImage()!=null){
			forceDirtyIfForeign(user,mathDesc.getGeometry().getGeometrySpec().getImage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/01 3:20:55 PM)
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
private void forceDirtyIfForeign(User user,Versionable versionable) {
	try {
		if (versionable.getVersion()!=null && !versionable.getVersion().getOwner().equals(user)){
			versionable.setDescription("cloned from '"+versionable.getName()+"'"+
										" owned by user "+versionable.getVersion().getOwner().getName()+
										"\n"+versionable.getDescription());
			versionable.clearVersion();
		}
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getLocalizedMessage(),e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
//
// this returns a BioModel that contains multiple instances of objects.
// 
public String getBioModelUnresolved(QueryHashtable dbc, User user, KeyValue bioModelKey) throws DataAccessException, XmlParseException, java.sql.SQLException {
	
	//
	// get meta data associated with BioModel
	//
	BioModelMetaData bioModelMetaData = dbServer.getDBTopLevel().getBioModelMetaData(dbc, user,bioModelKey);

	//
	// get list of appropriate child components
	//
	KeyValue modelKey = bioModelMetaData.getModelKey();
	KeyValue simKeys[] = getKeyArrayFromEnumeration(bioModelMetaData.getSimulationKeys());
	KeyValue scKeys[] = getKeyArrayFromEnumeration(bioModelMetaData.getSimulationContextKeys());

	Model model = dbServer.getDBTopLevel().getModel(dbc, user,modelKey);

	Simulation simArray[] = new Simulation[simKeys.length];
	for (int i=0;i<simKeys.length;i++){
		Simulation sim = dbServer.getDBTopLevel().getSimulation(dbc, user,simKeys[i]);
		//
		// clone Simulations so as to isolate different MathModels
		//
		try {
			simArray[i] = (Simulation)BeanUtils.cloneSerializable(sim);
		}catch (Throwable e){
			lg.error(e.getLocalizedMessage(),e);
			throw new RuntimeException("exception cloning Simulation: "+e.getMessage());
		}
	}
	
	SimulationContext scArray[] = new SimulationContext[scKeys.length];
	for (int i=0;i<scKeys.length;i++){
		SimulationContext sc = dbServer.getDBTopLevel().getSimulationContext(dbc, user,scKeys[i]);
		//
		// clone SimulationContexts so as to isolate different MathModels
		//
		try {
			scArray[i] = (SimulationContext)BeanUtils.cloneSerializable(sc);
			scArray[i].getModel().refreshDependencies();
			scArray[i].refreshDependencies();
			scArray[i].setModel(model);
		}catch (Throwable e){
			lg.error(e.getLocalizedMessage(),e);
			throw new RuntimeException("exception cloning Application: "+e.getMessage());
		}
		if (!scArray[i].getModel().getKey().compareEqual(modelKey)){
//			throw new DataAccessException("simulationContext("+scKeys[i]+").model = "+scArray[i].getModel().getKey()+", BioModel.model = "+modelKey);
			if (lg.isWarnEnabled()) lg.warn("simulationContext("+scKeys[i]+").model = "+scArray[i].getModel().getKey()+", BioModel.model = "+modelKey);
		}
	}

	//
	// create new BioModel according to loaded BioModelMetaData
	//
	BioModel newBioModel = new BioModel(bioModelMetaData.getVersion());
	try {
		// newBioModel.setMIRIAMAnnotation(bioModelMetaData.getMIRIAMAnnotation());
		if (lg.isWarnEnabled()) lg.warn("< < < < NEED TO GET VCMETADATA FROM METADATA TABLE ... METADATA IS EMPTY. > > > >");
		newBioModel.setModel(model);
		newBioModel.setSimulationContexts(scArray);
		//
		// resolve simulation mathDescriptions before adding them to the bioModel
		//
		for (int i=0;i<simArray.length;i++){
			boolean bMathFound = false;
			for (int j=0;j<scArray.length;j++) {
				if (simArray[i].getMathDescription().getVersion().getVersionKey().compareEqual(scArray[j].getMathDescription().getVersion().getVersionKey())){
					simArray[i].setMathDescription(scArray[j].getMathDescription());
					bMathFound = true;
					break;
				}
			}
			if (!bMathFound){
				if (lg.isWarnEnabled()) lg.warn("<<<<WARNING>>>>> ClientDocumentManager.getBioModel(), Simulation "+simArray[i].getName()+" is orphaned, Math("+simArray[i].getMathDescription().getName()+") not found in Applications");
				simArray = (Simulation[])BeanUtils.removeElement(simArray,simArray[i]);
				i--;
			}
		}
		newBioModel.setSimulations(simArray);
	}catch (java.beans.PropertyVetoException e){
		lg.error(e.getLocalizedMessage(),e);
		throw new DataAccessException("PropertyVetoException caught "+e.getMessage());
	}

//
// The BioModel is no longer cloned because the reference to this BioModel is no longer returned to the calling method.
//   the only possible side effect is the "BioModel:refreshDependencies()" method call.
//   this will reconnect internal listeners and other transient fields, and is not going to harm the cache integrity.
//
//	//
//	// clone BioModel (so that children can't be corrupted in the cache)
//	//
//	try {
//		newBioModel = (BioModel)BeanUtils.cloneSerializable(newBioModel);
//	}catch (Exception e){
//		lg.error(e.getLocalizedMessage(),e);
//		throw new DataAccessException("BioModel clone failed: "+e.getMessage());
//	}

	newBioModel.refreshDependencies();
	
	//
	// return new BioModel as an XML document
	//
	return cbit.vcell.xml.XmlHelper.bioModelToXML(newBioModel);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 4:04:16 PM)
 * @return java.lang.String
 * @param vType cbit.sql.VersionableType
 * @param vKey cbit.sql.KeyValue
 */
public String getBioModelXML(QueryHashtable dbc, User user, KeyValue bioModelKey,boolean bRegenerateXML) throws DataAccessException {
	String bioModelXML = null;
	
	try {
		bioModelXML = dbServer.getDBTopLevel().getBioModelXML(user,bioModelKey,true);
		if (bioModelXML != null){
			if(bRegenerateXML){
				try {
					BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML));
					return cbit.vcell.xml.XmlHelper.bioModelToXML(bioModel);
				} catch (XmlParseException e) {
					e.printStackTrace();
					throw new DataAccessException(e.getMessage(),e);
				}
			}else{
				return bioModelXML;
			}
		}
	} catch (java.sql.SQLException e) {
		lg.error(e.getLocalizedMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e){
		//
		// not stored as XML currently, retrieve traditional way.
		//
	}
	

	try {
		bioModelXML = getBioModelUnresolved(dbc, user,bioModelKey);

		dbServer.insertVersionableXML(user,VersionableType.BioModelMetaData,bioModelKey,bioModelXML);

		return bioModelXML;
	} catch (java.sql.SQLException e) {
		lg.error(e.getLocalizedMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (cbit.vcell.xml.XmlParseException e) {
		lg.error(e.getLocalizedMessage(),e);
		throw new DataAccessException (e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:30:03 PM)
 * @return cbit.sql.KeyValue[]
 * @param enum1 java.util.Enumeration
 */
private KeyValue[] getKeyArrayFromEnumeration(Enumeration enum1) {
	Vector temp = new Vector();
	while (enum1.hasMoreElements()){
		temp.addElement(enum1.nextElement());
	}
	KeyValue keyArray[] = new KeyValue[temp.size()];
	temp.copyInto(keyArray);
	return keyArray;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 4:02:44 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */

//
// this returns a MathModel that has duplicate instances of the same objects
//
public MathModel getMathModelUnresolved(QueryHashtable dbc, User user, KeyValue mathModelKey) throws DataAccessException, java.sql.SQLException {
	
	//
	// get meta data associated with MathModel
	//
	MathModelMetaData mathModelMetaData = dbServer.getMathModelMetaData(user,mathModelKey);

	//
	// get list of appropriate child components
	//
	KeyValue mathDescriptionKey = mathModelMetaData.getMathKey();
	KeyValue simKeys[] = getKeyArrayFromEnumeration(mathModelMetaData.getSimulationKeys());

	MathDescription mathDescription = dbServer.getDBTopLevel().getMathDescription(dbc, user,mathDescriptionKey);

	Simulation simArray[] = new Simulation[simKeys.length];
	for (int i=0;i<simKeys.length;i++){
		Simulation sim = dbServer.getDBTopLevel().getSimulation(dbc, user,simKeys[i]);
		//
		// clone Simulations so as to isolate different MathModels
		//
		try {
			simArray[i] = (Simulation)BeanUtils.cloneSerializable(sim);
		}catch (Throwable e){
			throw new RuntimeException("exception cloning Simulation: "+e.getMessage());
		}
		if (!simArray[i].getMathDescription().getKey().compareEqual(mathDescriptionKey)){
//			throw new DataAccessException("simulation("+simKeys[i]+").mathDescription = "+simArray[i].getMathDescription().getKey()+", MathModel.mathDescription = "+mathDescriptionKey);
			if (lg.isWarnEnabled()) lg.warn("ClientDocumentManager.getMathModel(), simulation("+simKeys[i]+").mathDescription = "+simArray[i].getMathDescription().getKey()+", MathModel.mathDescription = "+mathDescriptionKey);
		}
	}
	
	//
	// create new MathModel according to loaded MathModelMetaData
	//
	MathModel newMathModel = new MathModel(mathModelMetaData.getVersion());
	try {
		newMathModel.setMathDescription(mathDescription);
		newMathModel.setSimulations(simArray);
		ArrayList<AnnotatedFunction> outputFunctions = mathModelMetaData.getOutputFunctions();
		if (outputFunctions != null) {
			newMathModel.getOutputFunctionContext().setOutputFunctions(outputFunctions);
		}
	}catch (java.beans.PropertyVetoException e){
		throw new DataAccessException("PropertyVetoException caught "+e.getMessage());
	}

	//
	// clone MathModel (so that children can't be corrupted in the cache)
	//
	try {
		newMathModel = (MathModel)BeanUtils.cloneSerializable(newMathModel);
	}catch (Exception e){
		lg.error(e.getLocalizedMessage(),e);
		throw new DataAccessException("MathModel clone failed: "+e.getMessage());
	}

	newMathModel.refreshDependencies();

	//
	// return new MathModel
	//
	return newMathModel;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 4:04:16 PM)
 * @return java.lang.String
 * @param vType cbit.sql.VersionableType
 * @param vKey cbit.sql.KeyValue
 */
public String getMathModelXML(QueryHashtable dbc, User user, KeyValue mathModelKey,boolean bRegenerateXML) throws DataAccessException {
	String mathModelXML = null;
	
	try {
		mathModelXML = dbServer.getDBTopLevel().getMathModelXML(user,mathModelKey,true);
		if (mathModelXML != null){
			if(bRegenerateXML){
				try {
					MathModel mathModel = XmlHelper.XMLToMathModel(new XMLSource(mathModelXML));
					return cbit.vcell.xml.XmlHelper.mathModelToXML(mathModel);
				} catch (XmlParseException e) {
					e.printStackTrace();
					throw new DataAccessException(e.getMessage(),e);
				}
			}else{
				return mathModelXML;
			}
		}
	} catch (java.sql.SQLException e) {
		lg.error(e.getLocalizedMessage(),e);
		throw new DataAccessException(e.getMessage());
	}catch (ObjectNotFoundException e){
		//
		// not stored as XML currently, retrieve traditional way.
		//
	}
	

	try {
		MathModel mathModel = getMathModelUnresolved(dbc, user,mathModelKey);

		mathModelXML = cbit.vcell.xml.XmlHelper.mathModelToXML(mathModel);
		dbServer.insertVersionableXML(user,VersionableType.MathModelMetaData,mathModelKey,mathModelXML);

		return mathModelXML;
	} catch (java.sql.SQLException e) {
		lg.error(e.getLocalizedMessage(),e);
		throw new DataAccessException(e.getMessage());
	} catch (cbit.vcell.xml.XmlParseException e) {
		lg.error(e.getLocalizedMessage(),e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 */
boolean isChanged(QueryHashtable dbc, User user, cbit.image.VCImage vcImage) throws org.vcell.util.DataAccessException {
	//
	// identify versionable as it was last loaded from the database
	//
	KeyValue key = (vcImage.getVersion()!=null)?(vcImage.getVersion().getVersionKey()):null;

	if (key==null){
		return true;
	}

	Versionable savedVersionable = null;
	//
	// get versionable from database or from cache (should be in cache)
	//	
	try {
		savedVersionable = dbServer.getDBTopLevel().getVCImage(dbc, user,key,true);
	}catch (ObjectNotFoundException e){
		//
		// loaded version has been deleted
		//
		return true;
	}catch (Throwable e){
		//
		// loaded version has been deleted
		//
		lg.error(e.getLocalizedMessage(),e);
		return true;
	}
	return isChanged0(user, vcImage, savedVersionable);

}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
boolean isChanged(QueryHashtable dbc, User user, Geometry geometry) throws DataAccessException {
	//
	// identify versionable as it was last loaded from the database
	//
	KeyValue key = (geometry.getVersion()!=null)?(geometry.getVersion().getVersionKey()):null;

	if (key==null){
		return true;
	}

	Versionable savedVersionable = null;
	//
	// get versionable from database or from cache (should be in cache)
	//	
	try {
		savedVersionable = dbServer.getDBTopLevel().getGeometry(dbc, user,key,true);
	}catch (ObjectNotFoundException e){
		//
		// loaded version has been deleted
		//
		return true;
	}catch (Throwable e){
		//
		// loaded version has been deleted
		//
		lg.error(e.getLocalizedMessage(),e);
		return true;
	}
	
	return isChanged0(user, geometry, savedVersionable);

}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
private boolean isChanged0(User user, org.vcell.util.document.Versionable versionable, Versionable savedVersionable) throws DataAccessException {
	/*
	//
	// identify versionable as it was last loaded from the database
	//
	VersionableType vType = VersionableType.fromVersionable(versionable);
	KeyValue key = (versionable.getVersion()!=null)?(versionable.getVersion().getVersionKey()):null;

	if (key==null){
		return true;
	}
	//
	// get versionable from database or from cache (should be in cache)
	//	
	try {
		// only work for Geometry and VCImage
		if (vType.equals(VersionableType.Geometry)) {
			savedVersionable = dbServer.getDBTopLevel().getGeometry(user,key,true);
		} else if (vType.equals(VersionableType.VCImage)) {
			savedVersionable = dbServer.getDBTopLevel().getVCImage(user,key,true);
		} else {
			return true;
		}
	}catch (ObjectNotFoundException e){
		//
		// loaded version has been deleted
		//
		return true;
	}catch (Throwable e){
		//
		// loaded version has been deleted
		//
		lg.error(e.getLocalizedMessage(),e);
		return true;
	}*/

	//
	// if never saved, then it has changed (from null to something)
	//
	if (savedVersionable == null){
		return true;
	}

	//
	// if comparison fails, then it changed
	//
	if (!savedVersionable.compareEqual(versionable)){
		return true;
	}

	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 5:43:44 PM)
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private VersionInfo removeUserFromGroup0(User user, VersionInfo versionInfo, VersionableType vType, Hashtable vInfoHash, String userToAdd) throws DataAccessException {

	//
	// unpublish from database
	//
	VersionInfo newVersionInfo = dbServer.groupRemoveUser(user,vType,versionInfo.getVersion().getVersionKey(),userToAdd,false);
	
	//
	// replace versionInfo in hashTable
	//
	vInfoHash.remove(versionInfo.getVersion().getVersionKey());
	vInfoHash.put(newVersionInfo.getVersion().getVersionKey(),newVersionInfo);

	//
	// refresh versionInfos of child Geometries and Images (refresh all for now)
	//
	//
	// Removed because of deep cloning of children
	//
	//if (vType.equals(VersionableType.MathModelMetaData) || vType.equals(VersionableType.BioModelMetaData)){
	//	reloadInfos(VersionableType.Geometry,geoInfoHash);
	//	reloadInfos(VersionableType.VCImage,imgInfoHash);
	//}

	return newVersionInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public String saveBioModel(QueryHashtable dbc, User user, String bioModelXML, String newName, String independentSims[]) throws DataAccessException, java.sql.SQLException, java.beans.PropertyVetoException, MappingException, cbit.vcell.xml.XmlParseException {

long start = System.currentTimeMillis();
	//
	// this invokes "update" on the database layer
	//
	BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML));

	forceDeepDirtyIfForeign(user,bioModel);
	boolean isSaveAsNew = true;
	//
	// rename if required
	//
	if (newName!=null){
		try{
			bioModel.setName(newName);
		}catch (java.beans.PropertyVetoException e){
			lg.error(e.getLocalizedMessage(),e);
			throw new DataAccessException("couldn't set new name for BioModel: "+e.getMessage());
		}
	}else{
		isSaveAsNew = false;
	}

	Version oldVersion = bioModel.getVersion();

	BioModel origBioModel = null;
	if (oldVersion!=null){
		try{
			String origBioModelXML = getBioModelXML(dbc, user,oldVersion.getVersionKey(),false);
			origBioModel = XmlHelper.XMLToBioModel(new XMLSource(origBioModelXML));
		}catch(ObjectNotFoundException nfe){
			if(isSaveAsNew){
				User foceClearVersionUser = new User("foceClearVersionUser",new KeyValue("0"));
				forceDeepDirtyIfForeign(foceClearVersionUser, bioModel);
			}else{
				throw new DataAccessException("Stored model has been changed or removed, please use 'Save As..'");
			}
		}
	}

	boolean bSomethingChanged = false;

	//
	// verify that there are no orphaned Simulations (that belonged to Applications that have null mathDescriptions ... incomplete mappings)
	//
	// the workspace is responsible for cleaning up Simulations
	//
	{
		Simulation sims[] = bioModel.getSimulations();
		SimulationContext scs[] = bioModel.getSimulationContexts();
		for (int i=0;sims!=null && i<sims.length;i++){
			boolean bFound = false;
			for (int j = 0; scs!=null && j < scs.length; j++){
				if (scs[j].getMathDescription()==sims[i].getMathDescription()){
					bFound = true;
				}
			}
			if (!bFound){
				throw new RuntimeException("Error: Simulation "+sims[i].getName()+" cannot be saved, no Application exists with same MathDescription");
			}
		}
	}

	//
	// UPDATE AND SUBSTITUTE FROM BOTTOM UP
	//
	//   Image->Geometry
	//   Geometry->SimContext,MathDescription
	//   MathDescription->Simulation,SimulationContext
	//   Model->BioModel
	//   Simulation->BioModel
	//   SimContext->BioModel 
	//   VCMetaData->BioModel
	//
	Simulation simArray[] = bioModel.getSimulations();
	SimulationContext scArray[] = bioModel.getSimulationContexts();

//	Hashtable mathEquivHash = new Hashtable();
	long roundtripTimer = 0;
	long l1 = 0;
	long l2 = 0;
	//
	// for each image (anywhere in document):
	//    save if necessary (only once) and store saved instance in hashTable
	//
	Hashtable<Versionable,Versionable> memoryToDatabaseHash = new Hashtable<Versionable,Versionable>();
	for (int i = 0;scArray!=null && i < scArray.length; i++){
		VCImage memoryImage = scArray[i].getGeometry().getGeometrySpec().getImage();
		if (memoryImage!=null){
			if (!memoryToDatabaseHash.containsKey(memoryImage)){
				//
				// didn't evaluate this image yet.
				//
				memoryToDatabaseHash.put(memoryImage,memoryImage); // defaults to unchanged
				
				if (memoryImage.getKey()!=null && memoryImage.getVersion().getName().equals(memoryImage.getName())){
					//
					// if image had previously been saved, not been forced 'dirty', and name not changed
					// compare with original image to see if "update" is required.
					//
					VCImage databaseImage = null;
					if (origBioModel!=null){
						for (int j = 0; j < origBioModel.getNumSimulationContexts(); j++){
							VCImage origImage = origBioModel.getSimulationContext(j).getGeometry().getGeometrySpec().getImage();
							if (origImage!=null && origImage.getKey().equals(memoryImage.getKey())){
								databaseImage = origImage;
							}
						}
					}
					if (databaseImage==null){
						//
						// saved image not found in origBioModel (too bad), get from database.
						//
						if (lg.isTraceEnabled()) {
							l1 = System.currentTimeMillis();
						}
						databaseImage = dbServer.getDBTopLevel().getVCImage(dbc, user,memoryImage.getKey(),false);
						if (lg.isTraceEnabled()) {
							l2 = System.currentTimeMillis();
							roundtripTimer += l2 - l1;
						}
					}
					if (databaseImage!=null && !databaseImage.compareEqual(memoryImage)){
						KeyValue updatedImageKey = dbServer.getDBTopLevel().updateVersionable(user,memoryImage,false,true);
						if (lg.isTraceEnabled()) {
							l1 = System.currentTimeMillis();
						}
						VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(dbc, user,updatedImageKey,false);
						if (lg.isTraceEnabled()) {
							l2 = System.currentTimeMillis();
							roundtripTimer += l2 - l1;
						}
						memoryToDatabaseHash.put(memoryImage,updatedImage);
						bSomethingChanged = true;
					}
				}else{
					//
					// Image hasn't been saved, has been renamed, or has been forced 'dirty'
					// insert it with a unique name
					//
					int count=0;
					fixNullImageName(memoryImage);
					while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.VCImage,memoryImage.getName(),true)){
						try {
							memoryImage.setName(TokenMangler.getNextRandomToken(memoryImage.getName()));
						}catch (java.beans.PropertyVetoException e){
							lg.error(e.getLocalizedMessage(),e);
						}
						if (count++ > 5){
							throw new DataAccessException("failed to find unique image name '"+memoryImage.getName()+"' is last name tried");
						}
					}
					KeyValue updatedImageKey = dbServer.getDBTopLevel().insertVersionable(user,memoryImage,memoryImage.getName(),false,true);
					if (lg.isTraceEnabled()) {
						l1 = System.currentTimeMillis();
					}
					VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(dbc, user,updatedImageKey,false);
					if (lg.isTraceEnabled()) {
						l2 = System.currentTimeMillis();
						roundtripTimer += l2 - l1;
					}
					memoryToDatabaseHash.put(memoryImage,updatedImage);
					bSomethingChanged = true;
				}
			}
		}
	}
	//
	// for each geometry in document:
	//   substitute saved images into Geometries and 
	//   save Geometries if necessary (only once) and store saved instance in hashtable.
	//
	for (int i = 0;scArray!=null && i < scArray.length; i++){
		Geometry memoryGeometry = scArray[i].getGeometry();
		if (!memoryToDatabaseHash.containsKey(memoryGeometry)){
			//
			// didn't evaluate this geometry yet.
			//
			memoryToDatabaseHash.put(memoryGeometry,memoryGeometry); // defaults to unchanged

			boolean bMustSaveGeometry = false;
			VCImage geometryImage = memoryGeometry.getGeometrySpec().getImage();
			if (geometryImage!=null && memoryToDatabaseHash.get(geometryImage)!=geometryImage){
				//
				// image had changed and was saved, load saved image into geometry and force a save of this geometry.
				//
				memoryGeometry.getGeometrySpec().setImage((VCImage)memoryToDatabaseHash.get(geometryImage));
				geometryImage = (VCImage)memoryToDatabaseHash.get(geometryImage);
				bMustSaveGeometry = true;
			}
			
			if (memoryGeometry.getKey()!=null && memoryGeometry.getVersion().getName().equals(memoryGeometry.getName())){
				if (!bMustSaveGeometry){
					//
					// if geometry had previously been saved, not been forced 'dirty', and name not changed
					// compare with original geometry to see if "update" is required.
					//
					Geometry databaseGeometry = null;
					if (origBioModel!=null){
						for (int j = 0; j < origBioModel.getNumSimulationContexts(); j++){
							Geometry origGeometry = origBioModel.getSimulationContext(j).getGeometry();
							if (origGeometry!=null && origGeometry.getKey().equals(memoryGeometry.getKey())){
								databaseGeometry = origGeometry;
							}
						}
					}
					if (databaseGeometry==null){
						//
						// saved geometry not found in origBioModel (too bad), get from database.
						//
						if (lg.isTraceEnabled()) {
							l1 = System.currentTimeMillis();
						}
						databaseGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,memoryGeometry.getKey(),false);
						if (lg.isTraceEnabled()) {
							l2 = System.currentTimeMillis();
							roundtripTimer += l2 - l1;
						}
					}
					if (databaseGeometry!=null && !databaseGeometry.compareEqual(memoryGeometry)){
						bMustSaveGeometry = true;
					}
					if(!bMustSaveGeometry && memoryGeometry.getDimension() > 0){
						GeometrySurfaceDescription geomSurfDescr = memoryGeometry.getGeometrySurfaceDescription();
						SurfaceClass[] surfClassArr = geomSurfDescr.getSurfaceClasses();
						for (int j = 0; surfClassArr != null && j < surfClassArr.length; j++) {
							if(surfClassArr[j].getKey() == null){
								bMustSaveGeometry = true;
								break;
							}
						}
					}
				}
				if (bMustSaveGeometry){
					KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
					KeyValue updatedGeometryKey = dbServer.getDBTopLevel().updateVersionable(dbc, user,memoryGeometry,updatedImageKey,false,true);
					if (lg.isTraceEnabled()) {
						l1 = System.currentTimeMillis();
					}
					Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,updatedGeometryKey,false);
					if (lg.isTraceEnabled()) {
						l2 = System.currentTimeMillis();
						roundtripTimer += l2 - l1;
					}
					memoryToDatabaseHash.put(memoryGeometry,updatedGeometry);
					bSomethingChanged = true;
				}
			}else{
				//
				// Geometry hasn't been saved, has been renamed, or has been forced 'dirty'
				// insert it with a unique name
				//
				int count=0;
				while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.Geometry,memoryGeometry.getName(),true)){
					try {
						memoryGeometry.setName(TokenMangler.getNextRandomToken(memoryGeometry.getName()));
					}catch (java.beans.PropertyVetoException e){
						lg.error(e.getLocalizedMessage(),e);
					}
					if (count++ > 5){
						throw new DataAccessException("failed to find unique geometry name '"+memoryGeometry.getName()+"' is last name tried");
					}
				}
				KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
				KeyValue updatedGeometryKey = dbServer.getDBTopLevel().insertVersionable(dbc, user,memoryGeometry,updatedImageKey,memoryGeometry.getName(),false,true);
				if (lg.isTraceEnabled()) {
					l1 = System.currentTimeMillis();
				}
				Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,updatedGeometryKey,false);
				if (lg.isTraceEnabled()) {
					l2 = System.currentTimeMillis();
					roundtripTimer += l2 - l1;
				}
				memoryToDatabaseHash.put(memoryGeometry,updatedGeometry);
				bSomethingChanged = true;
			}
		}
	}
	//
	// for each MathDescription in document:
	//   substitute saved geometry's into SimulationContext and 
	//   save SimulationContext if necessary (only once) and store saved instance in hashtable.
	//
	Hashtable<MathDescription,MathCompareResults> mathEquivalencyHash = new Hashtable<MathDescription,MathCompareResults>();
	for (int i = 0;scArray!=null && i < scArray.length; i++){
		MathDescription memoryMathDescription = scArray[i].getMathDescription();
		if (!memoryToDatabaseHash.containsKey(memoryMathDescription)){
			//
			// didn't evaluate this SimulationContext yet.
			//
			memoryToDatabaseHash.put(memoryMathDescription,memoryMathDescription); // defaults to unchanged

			boolean bMustSaveMathDescription = false;
			Geometry scGeometry = memoryMathDescription.getGeometry();
			if (scGeometry!=null && memoryToDatabaseHash.get(scGeometry)!=scGeometry){
				//
				// geometry had changed and was saved, load saved geometry into SimulationContext (and it's MathDescription) and force a save of this SimulationContext.
				//
				memoryMathDescription.setGeometry((Geometry)memoryToDatabaseHash.get(scGeometry));
				bMustSaveMathDescription = true;
			}
			MathDescription databaseMathDescription = null;
			if (memoryMathDescription.getKey()!=null){
				//
				// if MathDescription had previously been saved, not been forced 'dirty', and name not changed
				// compare with original MathDescription to see if "update" is required.
				//
				if (origBioModel!=null){
					for (int j = 0; j < origBioModel.getNumSimulationContexts(); j++){
						MathDescription math = origBioModel.getSimulationContext(j).getMathDescription();
						if (math.getKey().equals(memoryMathDescription.getKey())){
							databaseMathDescription = math;
						}
					}
				}
				if (databaseMathDescription==null){
					//
					// saved mathDescription not found in origBioModel (too bad), get from database.
					//
					if (lg.isTraceEnabled()) {
						l1 = System.currentTimeMillis();
					}
					databaseMathDescription = dbServer.getDBTopLevel().getMathDescription(dbc, user,memoryMathDescription.getKey());
					if (lg.isTraceEnabled()) {
						l2 = System.currentTimeMillis();
						roundtripTimer += l2 - l1;
					}
				}
				if (databaseMathDescription!=null && !databaseMathDescription.compareEqual(memoryMathDescription)){
					bMustSaveMathDescription = true;
				}
			}else{
				bMustSaveMathDescription = true;
			}
			if (bMustSaveMathDescription){
				MathCompareResults mathCompareResults = null;
				if (databaseMathDescription!=null){
					try {
						mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),memoryMathDescription,databaseMathDescription);
						if (mathCompareResults!=null && !mathCompareResults.isEquivalent() &&
							(mathCompareResults.decision.equals(Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES) ||
							 mathCompareResults.decision.equals(Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION))){

							//
							// if there is a different number of variables or cannot find variables by name (even considering change of state variables)
							// then try the VCell 4.8 generated math.
							//
							MathDescription mathDesc_4_8 = new MathMapping_4_8(scArray[i]).getMathDescription();
							mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),mathDesc_4_8, databaseMathDescription);
						}
					}catch (Exception e){
						lg.error(e.getLocalizedMessage(),e);
						mathCompareResults = new MathCompareResults(Decision.MathDifferent_FAILURE_UNKNOWN,"Exception: '"+e.getMessage()+"'");
						if (lg.isTraceEnabled()) {
							lg.trace("FAILED TO COMPARE THE FOLLOWING MATH DESCRIPTIONS");
							try {
								lg.trace("MemoryMathDescription:\n"+((memoryMathDescription!=null)?(memoryMathDescription.getVCML_database()):("null")));
								lg.trace("DatabaseMathDescription:\n"+((databaseMathDescription!=null)?(databaseMathDescription.getVCML_database()):("null")));
							}catch (Exception e2){
								lg.error(e2.getLocalizedMessage(), e2);
							}
						}
					}
				}else{
					mathCompareResults = new MathCompareResults(Decision.MathDifferent_NOT_SAVED);
				}
				//
				// MathDescription hasn't been saved, has been renamed, or has been forced 'dirty'
				// insert it with a any name (doens't have to be unique ... mathDescription is not a top-level versionable).
				//
				KeyValue updatedGeometryKey = memoryMathDescription.getGeometry().getKey();
				KeyValue updatedMathDescriptionKey = null;
				if (memoryMathDescription.getVersion()!=null && memoryMathDescription.getVersion().getName().equals(memoryMathDescription.getName())){
					updatedMathDescriptionKey = dbServer.getDBTopLevel().updateVersionable(user,memoryMathDescription,updatedGeometryKey,false,true);
				}else{
					updatedMathDescriptionKey = dbServer.getDBTopLevel().insertVersionable(user,memoryMathDescription,updatedGeometryKey,memoryMathDescription.getName(),false,true);
				}
					
				if (lg.isTraceEnabled()) {
					l1 = System.currentTimeMillis();
				}
				MathDescription updatedMathDescription = dbServer.getDBTopLevel().getMathDescription(dbc, user,updatedMathDescriptionKey);
				if (lg.isTraceEnabled()) {
					l2 = System.currentTimeMillis();
					roundtripTimer += l2 - l1;
				}
				memoryToDatabaseHash.put(memoryMathDescription,updatedMathDescription);
				mathEquivalencyHash.put(updatedMathDescription,mathCompareResults);
				bSomethingChanged = true;
			}else{
				mathEquivalencyHash.put(memoryMathDescription,new MathCompareResults(Decision.MathEquivalent_SAME_MATHDESC_AS_IN_DB));
			}
		}
	}
	//
	// update physiology
	//
	{
		Model memoryModel = bioModel.getModel();
		memoryToDatabaseHash.put(memoryModel,memoryModel);  // preload with unchanged.
		if (memoryModel.getKey()!=null && memoryModel.getVersion().getName().equals(memoryModel.getName())){
			//
			// if Model had previously been saved, not been forced 'dirty', and name not changed
			// compare with original Model to see if "update" is required.
			//
			Model databaseModel = null;
			if (origBioModel!=null){
				if (origBioModel.getModel().getKey().equals(memoryModel.getKey())){
					databaseModel = origBioModel.getModel();
				}
			}
			if (databaseModel==null){
				//
				// saved model not found in origBioModel (too bad), get from database.
				//
				if (lg.isTraceEnabled()) {
					l1 = System.currentTimeMillis();
				}
				databaseModel = dbServer.getDBTopLevel().getModel(dbc, user,memoryModel.getKey());
				if (lg.isTraceEnabled()) {
					l2 = System.currentTimeMillis();
					roundtripTimer += l2 - l1;
				}
			}
			if (databaseModel!=null && !databaseModel.compareEqual(memoryModel)){
				KeyValue updatedModelKey = dbServer.getDBTopLevel().updateVersionable(user,memoryModel,false,true);
				if (lg.isTraceEnabled()) {
					l1 = System.currentTimeMillis();
				}
				Model updatedModel = dbServer.getDBTopLevel().getModel(dbc, user,updatedModelKey);
				if (lg.isTraceEnabled()) {
					l2 = System.currentTimeMillis();
					roundtripTimer += l2 - l1;
				}
				memoryToDatabaseHash.put(memoryModel,updatedModel);
				bSomethingChanged = true;
			}
		}else{
			//
			// Model hasn't been saved, has been renamed, or has been forced 'dirty'
			// insert it with a any name (doens't have to be unique ... mathDescription is not a top-level versionable).
			//
			KeyValue updatedModelKey = dbServer.getDBTopLevel().insertVersionable(user,memoryModel,memoryModel.getName(),false,true);
			if (lg.isTraceEnabled()) {
				l1 = System.currentTimeMillis();
			}
			Model updatedModel = dbServer.getDBTopLevel().getModel(dbc, user,updatedModelKey);
			if (lg.isTraceEnabled()) {
				l2 = System.currentTimeMillis();
				roundtripTimer += l2 - l1;
			}
			memoryToDatabaseHash.put(memoryModel,updatedModel);
			bSomethingChanged = true;
		}
	}
	//
	// for each SimulationContext in document:
	//   substitute saved geometry's and saved MathDescriptions into SimulationContext and 
	//   save SimulationContext if necessary (only once) and store saved instance in hashtable.
	//
	for (int i = 0;scArray!=null && i < scArray.length; i++){
		SimulationContext memorySimContext = scArray[i];
		if (!memoryToDatabaseHash.containsKey(memorySimContext)){
			//
			// didn't evaluate this SimulationContext yet.
			//
			memoryToDatabaseHash.put(memorySimContext,memorySimContext); // defaults to unchanged

			boolean bMustSaveSimContext = false;
			Geometry scGeometry = memorySimContext.getGeometry();
			if (scGeometry!=null && memoryToDatabaseHash.get(scGeometry)!=scGeometry){
				//
				// geometry had changed and was saved, load saved geometry into SimulationContext (and force a save)
				//
				memorySimContext.setGeometry((Geometry)memoryToDatabaseHash.get(scGeometry));
				bMustSaveSimContext = true;
			}
			MathDescription scMathDescription = memorySimContext.getMathDescription();
			if (scMathDescription!=null && memoryToDatabaseHash.get(scMathDescription)!=scMathDescription){
				//
				// mathDescription had changed and was saved, load saved mathDescription into SimulationContext (and force a save)
				//
				memorySimContext.setMathDescription((MathDescription)memoryToDatabaseHash.get(scMathDescription));
				bMustSaveSimContext = true;
			}
			Model scModel = memorySimContext.getModel();
			if (scModel!=null && memoryToDatabaseHash.get(scModel)!=scModel){
				//
				// model had changed and was saved, load saved model into SimulationContext (and force a save)
				//
				memorySimContext.setModel((Model)memoryToDatabaseHash.get(scModel));
				bMustSaveSimContext = true;
			}
			if (memorySimContext.getKey()!=null && memorySimContext.getVersion().getName().equals(memorySimContext.getName())){
				if (!bMustSaveSimContext){
					//
					// if SimulationContext had previously been saved, not been forced 'dirty', and name not changed
					// compare with original SimulationContext to see if "update" is required.
					//
					SimulationContext databaseSimContext = null;
					if (origBioModel!=null){
						for (int j = 0; j < origBioModel.getNumSimulationContexts(); j++){
							if (origBioModel.getSimulationContext(j).getKey().equals(memorySimContext.getKey())){
								databaseSimContext = origBioModel.getSimulationContext(j);
							}
						}
					}
					if (databaseSimContext==null){
						//
						// saved geometry not found in origBioModel (too bad), get from database.
						//
						if (lg.isTraceEnabled()) {
							l1 = System.currentTimeMillis();
						}
						databaseSimContext = dbServer.getDBTopLevel().getSimulationContext(dbc, user,memorySimContext.getKey());
						if (lg.isTraceEnabled()) {
							l2 = System.currentTimeMillis();
							roundtripTimer += l2 - l1;
						}
					}
					if (databaseSimContext!=null && !databaseSimContext.compareEqual(memorySimContext)){
						bMustSaveSimContext = true;
					}
				}
				if (bMustSaveSimContext){
					KeyValue updatedGeometryKey = memorySimContext.getGeometry().getKey();
					KeyValue updatedMathDescriptionKey = memorySimContext.getMathDescription().getKey();
					Model updatedModel = memorySimContext.getModel();
					KeyValue updatedSimContextKey = dbServer.getDBTopLevel().updateVersionable(user,memorySimContext,updatedMathDescriptionKey,updatedModel,updatedGeometryKey,false,true);
					if (lg.isTraceEnabled()) {
						l1 = System.currentTimeMillis();
					}
					SimulationContext updatedSimContext = dbServer.getDBTopLevel().getSimulationContext(dbc, user,updatedSimContextKey);
					if (lg.isTraceEnabled()) {
						l2 = System.currentTimeMillis();
						roundtripTimer += l2 - l1;
					}
					//
					// make sure mathDescription is a single reference (for this app and all of it's Simulations).
					//
					updatedSimContext.setMathDescription((MathDescription)memorySimContext.getMathDescription());
					
					memoryToDatabaseHash.put(memorySimContext,updatedSimContext);
					bSomethingChanged = true;
				}
			}else{
				//
				// SimulationContext hasn't been saved, has been renamed, or has been forced 'dirty'
				//
				KeyValue updatedGeometryKey = memorySimContext.getGeometry().getKey();
				KeyValue updatedMathDescriptionKey = memorySimContext.getMathDescription().getKey();
				Model updatedModel = memorySimContext.getModel();
				KeyValue updatedSimContextKey = dbServer.getDBTopLevel().insertVersionable(user,memorySimContext,updatedMathDescriptionKey,updatedModel,updatedGeometryKey,memorySimContext.getName(),false,true);
				if (lg.isTraceEnabled()) {
					l1 = System.currentTimeMillis();
				}
				SimulationContext updatedSimContext = dbServer.getDBTopLevel().getSimulationContext(dbc, user,updatedSimContextKey);
				if (lg.isTraceEnabled()) {
					l2 = System.currentTimeMillis();
					roundtripTimer += l2 - l1;
				}
				//
				// make sure mathDescription is a single reference (for this app and all of it's Simulations).
				//
				updatedSimContext.setMathDescription((MathDescription)memorySimContext.getMathDescription());
				memoryToDatabaseHash.put(memorySimContext,updatedSimContext);
				bSomethingChanged = true;
			}
		}
	}
	//
	// for each Simulation in document:
	//   substitute saved MathDescriptions into Simulation and 
	//   save Simulation if necessary (only once) and store saved instance in hashtable.
	//
	for (int i = 0;simArray!=null && i < simArray.length; i++){
		Simulation memorySimulation = simArray[i];
		if (!memoryToDatabaseHash.containsKey(memorySimulation)){
			//
			// didn't evaluate this Simulation yet.
			//
			memoryToDatabaseHash.put(memorySimulation,memorySimulation); // defaults to unchanged

			boolean bMustSaveSimulation = false;
			MathDescription simMathDescription = memorySimulation.getMathDescription();
			if (simMathDescription!=null && memoryToDatabaseHash.get(simMathDescription)!=simMathDescription){
				if (memoryToDatabaseHash.get(simMathDescription)!=null){ // make sure mathDescription hasn't already propagated (newer math won't be in hashtable)
					//
					// mathDescription had changed and was saved, load saved mathDescription into Simulation (and force a save)
					//
					memorySimulation.setMathDescription((MathDescription)memoryToDatabaseHash.get(simMathDescription));
					bMustSaveSimulation = true;
				}
			}
			Simulation databaseSimulation = null;
			//
			// if Simulation had previously been saved
			// compare with original Simulation to see if "update" is required.
			//
			// always get the "original" simulation so that SimulationParentReference can be maintained.
			//
			if (memorySimulation.getKey()!=null){
				if (origBioModel!=null){
					for (int j = 0; j < origBioModel.getNumSimulations(); j++){
						if (origBioModel.getSimulation(j).getKey().equals(memorySimulation.getKey())){
							databaseSimulation = origBioModel.getSimulation(j);
						}
					}
				}
				if (databaseSimulation==null){
					//
					// saved simulation not found in origBioModel (too bad), get from database.
					//
					if (lg.isTraceEnabled()) {
						l1 = System.currentTimeMillis();
					}
					databaseSimulation = dbServer.getDBTopLevel().getSimulation(dbc, user,memorySimulation.getKey());
					if (lg.isTraceEnabled()) {
						l2 = System.currentTimeMillis();
						roundtripTimer += l2 - l1;
					}
				}
				if (databaseSimulation!=null){
					if (!memorySimulation.compareEqual(databaseSimulation)){
						bMustSaveSimulation = true;
					}
				}
				if (!memorySimulation.getVersion().getName().equals(memorySimulation.getName())){
					// name was changed.
					bMustSaveSimulation = true;
				}
			}else{
				// never been saved.
				bMustSaveSimulation = true;
			}

			if (bMustSaveSimulation){
				boolean bMathematicallyEquivalent = false;
				if (databaseSimulation!=null){
					//
					// if to be forced "independent", then set equivalent to false
					//
					boolean bForceIndependent = false;
					for (int j = 0; independentSims!=null && j < independentSims.length; j++){
						if (independentSims[j].equals(memorySimulation.getName())){
							bForceIndependent = true;
						}
					}
					//
					// check for math equivalency first
					//
					MathCompareResults mathCompareResults = mathEquivalencyHash.get(memorySimulation.getMathDescription());
					bMathematicallyEquivalent = !bForceIndependent && Simulation.testEquivalency(memorySimulation,databaseSimulation,mathCompareResults);
					//
					// don't set equivalent if no data exists.
					//
					if (bMathematicallyEquivalent){
						VCSimulationIdentifier vcSimulationIdentifier = databaseSimulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
						SimulationStatusPersistent simStatus = dbServer.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
						if (simStatus==null || !simStatus.getHasData()){
							bMathematicallyEquivalent = false;
						}
					}
				}
				KeyValue updatedMathDescriptionKey = memorySimulation.getMathDescription().getKey();
				KeyValue updatedSimulationKey = null;
				if (memorySimulation.getKey()!=null && memorySimulation.getVersion().getName().equals(memorySimulation.getName())){
					// name not changed, update simulation (but pass in database Simulation to check for parent-equivalence)
					updatedSimulationKey = dbServer.getDBTopLevel().updateVersionable(user,memorySimulation,updatedMathDescriptionKey,false,bMathematicallyEquivalent,true);
				}else{
					// name changed, insert simulation (but pass in database Simulation to check for parent-equivalence)
					updatedSimulationKey = dbServer.getDBTopLevel().insertVersionable(user,memorySimulation,updatedMathDescriptionKey,memorySimulation.getName(),false,bMathematicallyEquivalent,true);
				}						
				if (lg.isTraceEnabled()) {
					l1 = System.currentTimeMillis();
				}
				Simulation updatedSimulation = dbServer.getDBTopLevel().getSimulation(dbc, user,updatedSimulationKey);
				if (lg.isTraceEnabled()) {
					l2 = System.currentTimeMillis();
					roundtripTimer += l2 - l1;
				}
				//
				// make sure mathDescription is a single reference (for an app and all of it's Simulations).
				//
				updatedSimulation.setMathDescription((MathDescription)memorySimulation.getMathDescription());
				
				memoryToDatabaseHash.put(memorySimulation,updatedSimulation);
				bSomethingChanged = true;
			}
		}
	}

	boolean bMustSaveVCMetaData = false;
	if (origBioModel!=null){
		//
		// for the VCMetaData in the document:
		//   save VCMetaData if necessary (only once) and store saved instance in hashtable.
		//
		// The persisted VCMetaData doesn't have any foreign keys 
		// (when annotating a simulation ... we don't point to the simulation, 
		// we use the text-based VCID that is stored in URIBindingList in the XML serialization 
		//
		// Therefore, there are no additional dependencies that we have to update during the 
		// incremental save and force propagation to save the VCMetaData.
		//
		VCMetaData memoryVCMetaData = bioModel.getVCMetaData();
		VCMetaData databaseVCMetaData = origBioModel.getVCMetaData();
		//
		// compare with original VCMetaData to see if "update" is required.
		//
		// always get the "original" simulation so that SimulationParentReference can be maintained.
		//
		if (databaseVCMetaData==null || !databaseVCMetaData.compareEquals(memoryVCMetaData)){
			bMustSaveVCMetaData = true;
			bSomethingChanged = true;
		}
	}
	
	if (bSomethingChanged || origBioModel==null || !bioModel.compareEqual(origBioModel)){
		//
		// create new BioModelMetaData and save to server
		//
		KeyValue modelKey = ((Model)memoryToDatabaseHash.get(bioModel.getModel())).getKey();
		KeyValue scKeys[] = new KeyValue[bioModel.getNumSimulationContexts()];
		for (int i = 0; i < bioModel.getNumSimulationContexts(); i++){
			scKeys[i] = ((SimulationContext)memoryToDatabaseHash.get(bioModel.getSimulationContext(i))).getKey();
		}
		KeyValue simKeys[] = new KeyValue[bioModel.getNumSimulations()];
		for (int i = 0; i < bioModel.getNumSimulations(); i++){
			simKeys[i] = ((Simulation)memoryToDatabaseHash.get(bioModel.getSimulation(i))).getKey();
		}
		// @TODO Add VC_METADATA table ... pointed to by VC_BIOMODEL (metadataref on delete cascade)
		// @TODO Write script to populate VC_METADATA from VC_MIRIAM
		// @TODO save VCMetaData from this BioModel into VC_METADATA .. stick in memoryToDatabaseHash
		//
		BioModelMetaData bioModelMetaData = null;
		String vcMetaDataXML = XmlHelper.vcMetaDataToXML(bioModel.getVCMetaData(), bioModel);
		if (oldVersion==null){
			bioModelMetaData =
				new BioModelMetaData(modelKey, scKeys, simKeys,vcMetaDataXML, bioModel.getName(), bioModel.getDescription());
		}else{
			bioModelMetaData = new BioModelMetaData(oldVersion, modelKey, scKeys, simKeys,vcMetaDataXML);
			if (!bioModel.getDescription().equals(oldVersion.getAnnot())) {
				try {
					bioModelMetaData.setDescription(bioModel.getDescription());
				} catch (java.beans.PropertyVetoException e) {
					lg.error(e.getLocalizedMessage(),e);
				}
			}
		}
//		bioModelMetaData.setMIRIAMAnnotation(bioModel.getMIRIAMAnnotation());
		BioModelMetaData updatedBioModelMetaData = null;
		if (bioModel.getVersion()==null || !bioModel.getVersion().getName().equals(bioModel.getName())){
			KeyValue updatedBioModelKey = dbServer.getDBTopLevel().insertVersionable(user,bioModelMetaData,null/*hack*/,bioModel.getName(),false,true);
			if (lg.isTraceEnabled()) {
				l1 = System.currentTimeMillis();
			}
			updatedBioModelMetaData = dbServer.getDBTopLevel().getBioModelMetaData(dbc, user,updatedBioModelKey);
			if (lg.isTraceEnabled()) {
				l2 = System.currentTimeMillis();
				roundtripTimer += l2 - l1;
			}
		}else{
			KeyValue updatedBioModelKey = dbServer.getDBTopLevel().updateVersionable(user,bioModelMetaData,null/*hack*/,false,true);
			if (lg.isTraceEnabled()) {
				l1 = System.currentTimeMillis();
			}
			updatedBioModelMetaData = dbServer.getDBTopLevel().getBioModelMetaData(dbc, user,updatedBioModelKey);
			if (lg.isTraceEnabled()) {
				l2 = System.currentTimeMillis();
				roundtripTimer += l2 - l1;
			}
		}

		//
		// (THIS IS THE REALLY SCAREY PART...NOT GETTING A FRESH VIEW OF EVERYTING FROM THE DATABASE FOR CREATING THE XML)
		//
		//bioModelXML = getBioModelXML(user,updatedBioModelMetaData.getVersion().getVersionKey());
		BioModel updatedBioModel = new BioModel(updatedBioModelMetaData.getVersion());
		//updatedBioModel.setMIRIAMAnnotation(updatedBioModelMetaData.getMIRIAMAnnotation());
		updatedBioModel.setModel((Model)memoryToDatabaseHash.get(bioModel.getModel()));
		for (int i = 0; i < bioModel.getNumSimulationContexts(); i++){
			updatedBioModel.addSimulationContext((SimulationContext)memoryToDatabaseHash.get(bioModel.getSimulationContext(i)));
		}
		for (int i = 0; i < bioModel.getNumSimulations(); i++){
			updatedBioModel.addSimulation((Simulation)memoryToDatabaseHash.get(bioModel.getSimulation(i)));
		}
		updatedBioModel.setVCMetaData(XmlHelper.xmlToVCMetaData(updatedBioModel.getVCMetaData(), updatedBioModel, vcMetaDataXML));

		//TODO must replace this with proper persistance.
		updatedBioModel.getPathwayModel().merge(bioModel.getPathwayModel());
		updatedBioModel.getRelationshipModel().merge(bioModel.getRelationshipModel());
		
		bioModelXML = cbit.vcell.xml.XmlHelper.bioModelToXML(updatedBioModel);
		dbServer.insertVersionableChildSummary(user,VersionableType.BioModelMetaData,updatedBioModel.getVersion().getVersionKey(),
				updatedBioModel.createBioModelChildSummary().toDatabaseSerialization());
		dbServer.insertVersionableXML(user,VersionableType.BioModelMetaData,updatedBioModel.getVersion().getVersionKey(),bioModelXML);
		if (lg.isTraceEnabled()) {
			lg.trace("Total time: " + ((double)(System.currentTimeMillis() - start)) / 1000 + " roundtrip: " + ((double)roundtripTimer) / 1000);
		}
		return bioModelXML;
	} else {
		if (lg.isTraceEnabled()) {
			lg.trace("Total time: " + ((double)(System.currentTimeMillis() - start)) / 1000 + " roundtrip: " + ((double)roundtripTimer) / 1000);
		}
		//If we got here were were doing 'save' or 'save as new version' but no changes were detected
		boolean bError = !bSomethingChanged && !isSaveAsNew;
		if(bError) {
			throw new ServerRejectedSaveException(origBioModel.getVersion().getVersionKey().toString());
		}

		return bioModelXML;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public String saveGeometry(QueryHashtable dbc, User user,String geometryXML,String newName) throws DataAccessException, cbit.vcell.xml.XmlParseException, java.sql.SQLException {

	Geometry geometry = XmlHelper.XMLToGeometry(new XMLSource(geometryXML));
	
	forceDeepDirtyIfForeign(user,geometry);

	//
	// rename if required
	//
	if (newName!=null){
		try{
			geometry.setName(newName);
		}catch (java.beans.PropertyVetoException e){
			lg.error(e.getLocalizedMessage(),e);
			throw new DataAccessException("couldn't set new name for Geometry: "+e.getMessage());
		}
	}

	//
	// if geometry has an image, save it if necessary
	//
	KeyValue updatedImageKey = null;
	if (geometry.getGeometrySpec().getImage()!=null){
		VCImage image = geometry.getGeometrySpec().getImage();
		if (image.getKey()==null || !image.getVersion().getName().equals(image.getName())){
			//
			// Image hasn't been saved, has been renamed, or has been forced 'dirty'
			// insert it with a unique name
			//
			try{
				fixNullImageName(image);
				int count = 0;
				while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.VCImage,image.getName(),true))
				{
					try {
						image.setName(TokenMangler.getNextRandomToken(image.getName()));
					}catch (java.beans.PropertyVetoException e){
						lg.error(e.getLocalizedMessage(),e);
					}
					if (count++ > 5){
						throw new DataAccessException("failed to find unique image name '" + image.getName()+"' is last name tried");
					}
				}
			}catch(Exception ex){
				lg.error(ex.getLocalizedMessage(),ex);
				throw new DataAccessException(ex.getMessage(),ex);
			}
			updatedImageKey = dbServer.getDBTopLevel().insertVersionable(user,image,image.getName(),false,true);
		}else{
			//
			// Image has been saved previously, get old image from database
			//
			VCImage origImage = dbServer.getDBTopLevel().getVCImage(dbc, user,image.getKey(),false);
			if (origImage==null || !origImage.compareEqual(image)){
				updatedImageKey = dbServer.getDBTopLevel().updateVersionable(user,image,false,true);
			}else{
				updatedImageKey = image.getKey(); // image didn't need to be saved
			}
		}
	}

	//
	// save geometry
	//
	KeyValue geometryKey = null;
	if (geometry.getVersion()!=null && geometry.getName().equals(geometry.getVersion().getName())){
		geometryKey = dbServer.getDBTopLevel().updateVersionable(dbc, user,geometry,updatedImageKey,false,true);
	}else{
		geometryKey = dbServer.getDBTopLevel().insertVersionable(dbc, user,geometry,updatedImageKey,geometry.getName(),false,true);
	}
	
	return dbServer.getGeometryXML(user,geometryKey).toString();
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public String saveMathModel(QueryHashtable dbc, User user, String mathModelXML, String newName, String independentSims[]) throws DataAccessException, java.sql.SQLException, java.beans.PropertyVetoException, cbit.vcell.xml.XmlParseException {
	//
	// this invokes "update" on the database layer
	//
	MathModel mathModel = XmlHelper.XMLToMathModel(new XMLSource(mathModelXML));
	
	forceDeepDirtyIfForeign(user,mathModel);
	boolean isSaveAsNew = true;

	//
	// rename if required
	//
	if (newName!=null){
		try{
			mathModel.setName(newName);
		}catch (java.beans.PropertyVetoException e){
			lg.error(e.getLocalizedMessage(),e);
			throw new DataAccessException("couldn't set new name for MathModel: "+e.getMessage());
		}
	}else{
		isSaveAsNew = false;
	}

	Version oldVersion = mathModel.getVersion();

	MathModel origMathModel = null;
	if (oldVersion!=null){
		try{
			String origMathModelXML = getMathModelXML(dbc, user,oldVersion.getVersionKey(),false);
			origMathModel = XmlHelper.XMLToMathModel(new XMLSource(origMathModelXML));
		}catch(ObjectNotFoundException nfe){
			if(isSaveAsNew){
				User foceClearVersionUser = new User("foceClearVersionUser",new KeyValue("0"));
				forceDeepDirtyIfForeign(foceClearVersionUser, mathModel);
			}else{
				throw new DataAccessException("Stored model has been changed or removed, please use 'Save As..'");
			}
		}
	}

	boolean bSomethingChanged = false;

	//
	// UPDATE AND SUBSTITUTE FROM BOTTOM UP
	//
	//   Image->Geometry
	//   Geometry->MathDescription
	//   MathDescription->Simulation,MathModel
	//   Simulation->MathModel
	//
	Simulation simArray[] = mathModel.getSimulations();
	
	//
	// if this mathModel has an image:
	//   save if necessary (only once) and store saved instance in hashTable
	//
	Hashtable<Versionable,Versionable> memoryToDatabaseHash = new Hashtable<Versionable,Versionable>();
	{
		VCImage memoryImage = mathModel.getMathDescription().getGeometry().getGeometrySpec().getImage();
		if (memoryImage!=null){
			memoryToDatabaseHash.put(memoryImage,memoryImage); // defaults to unchanged
			
			if (memoryImage.getKey()!=null && memoryImage.getVersion().getName().equals(memoryImage.getName())){
				//
				// if image had previously been saved, not been forced 'dirty', and name not changed
				// compare with original image to see if "update" is required.
				//
				VCImage databaseImage = null;
				if (origMathModel!=null){
					VCImage origImage = origMathModel.getMathDescription().getGeometry().getGeometrySpec().getImage();
					if (origImage!=null && origImage.getKey().equals(memoryImage.getKey())){
						databaseImage = origImage;
					}
				}
				if (databaseImage==null){
					//
					// saved image not found in origMathModel (too bad), get from database.
					//
					databaseImage = dbServer.getDBTopLevel().getVCImage(dbc, user,memoryImage.getKey(),false);
				}
				if (databaseImage!=null && !databaseImage.compareEqual(memoryImage)){
					KeyValue updatedImageKey = dbServer.getDBTopLevel().updateVersionable(user,memoryImage,false,true);
					VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(dbc, user,updatedImageKey,false);
					memoryToDatabaseHash.put(memoryImage,updatedImage);
					bSomethingChanged = true;
				}
			}else{
				//
				// Image hasn't been saved, has been renamed, or has been forced 'dirty'
				// insert it with a unique name
				//
				int count=0;
				fixNullImageName(memoryImage);
				while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.VCImage,memoryImage.getName(),true)){
					try {
						memoryImage.setName(TokenMangler.getNextRandomToken(memoryImage.getName()));
					}catch (java.beans.PropertyVetoException e){
						lg.error(e.getLocalizedMessage(),e);
					}
					if (count++ > 5){
						throw new DataAccessException("failed to find unique image name '"+memoryImage.getName()+"' is last name tried");
					}
				}
				KeyValue updatedImageKey = dbServer.getDBTopLevel().insertVersionable(user,memoryImage,memoryImage.getName(),false,true);
				VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(dbc, user,updatedImageKey,false);
				memoryToDatabaseHash.put(memoryImage,updatedImage);
				bSomethingChanged = true;
			}
		}
	}
	//
	// for the Geometry:
	//   substitute saved Image into Geometry and 
	//   save Geometry if necessary (only once) and store saved instance in hashtable.
	//
	{
		Geometry memoryGeometry = mathModel.getMathDescription().getGeometry();
		memoryToDatabaseHash.put(memoryGeometry,memoryGeometry); // defaults to unchanged

		boolean bMustSaveGeometry = false;
		VCImage geometryImage = memoryGeometry.getGeometrySpec().getImage();
		if (geometryImage!=null && memoryToDatabaseHash.get(geometryImage)!=geometryImage){
			//
			// image had changed and was saved, load saved image into geometry and force a save of this geometry.
			//
			memoryGeometry.getGeometrySpec().setImage((VCImage)memoryToDatabaseHash.get(geometryImage));
			geometryImage = (VCImage)memoryToDatabaseHash.get(geometryImage);
			bMustSaveGeometry = true;
		}
			
		if (memoryGeometry.getKey()!=null && memoryGeometry.getVersion().getName().equals(memoryGeometry.getName())){
			if (!bMustSaveGeometry){
				//
				// if geometry had previously been saved, not been forced 'dirty', and name not changed
				// compare with original geometry to see if "update" is required.
				//
				Geometry databaseGeometry = null;
				if (origMathModel!=null){
					Geometry origGeometry = origMathModel.getMathDescription().getGeometry();
					if (origGeometry.getKey().equals(memoryGeometry.getKey())){
						databaseGeometry = origGeometry;
					}
				}
				if (databaseGeometry==null){
					//
					// saved geometry not found in origMathModel (too bad), get from database.
					//
					databaseGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,memoryGeometry.getKey(),false);
				}
				if (databaseGeometry!=null && !databaseGeometry.compareEqual(memoryGeometry)){
					bMustSaveGeometry = true;
				}
			}
			if (bMustSaveGeometry){
				KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
				KeyValue updatedGeometryKey = dbServer.getDBTopLevel().updateVersionable(dbc, user,memoryGeometry,updatedImageKey,false,true);
				Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,updatedGeometryKey,false);
				memoryToDatabaseHash.put(memoryGeometry,updatedGeometry);
				bSomethingChanged = true;
			}
		}else{
			//
			// Geometry hasn't been saved, has been renamed, or has been forced 'dirty'
			// insert it with a unique name
			//
			int count=0;
			while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.Geometry,memoryGeometry.getName(),true)){
				try {
					memoryGeometry.setName(TokenMangler.getNextRandomToken(memoryGeometry.getName()));
				}catch (java.beans.PropertyVetoException e){
					lg.error(e.getLocalizedMessage(),e);
				}
				if (count++ > 5){
					throw new DataAccessException("failed to find unique geometry name '"+memoryGeometry.getName()+"' is last name tried");
				}
			}
			KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
			KeyValue updatedGeometryKey = dbServer.getDBTopLevel().insertVersionable(dbc, user,memoryGeometry,updatedImageKey,memoryGeometry.getName(),false,true);
			Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,updatedGeometryKey,false);
			memoryToDatabaseHash.put(memoryGeometry,updatedGeometry);
			bSomethingChanged = true;
		}
	}
	//
	// for the MathDescription:
	//   substitute saved geometry into MathDescription
	//   save MathDescription if necessary (only once) and store saved instance in hashtable.
	//
	MathCompareResults mathCompareResults = null;
	{
		MathDescription memoryMathDescription = mathModel.getMathDescription();
		memoryToDatabaseHash.put(memoryMathDescription,memoryMathDescription); // defaults to unchanged

		boolean bMustSaveMathDescription = false;
		Geometry scGeometry = memoryMathDescription.getGeometry();
		if (scGeometry!=null && memoryToDatabaseHash.get(scGeometry)!=scGeometry){
			//
			// geometry had changed and was saved, load saved geometry into SimulationContext (and it's MathDescription) and force a save of this SimulationContext.
			//
			memoryMathDescription.setGeometry((Geometry)memoryToDatabaseHash.get(scGeometry));
			bMustSaveMathDescription = true;
		}
		MathDescription databaseMathDescription = null;
		if (memoryMathDescription.getKey()!=null){
			//
			// if MathDescription had previously been saved, not been forced 'dirty', and name not changed
			// compare with original MathDescription to see if "update" is required.
			//
			if (origMathModel!=null){
				MathDescription origMathDescription = origMathModel.getMathDescription();
				if (origMathDescription.getKey().equals(memoryMathDescription.getKey())){
					databaseMathDescription = origMathDescription;
				}
			}
			if (databaseMathDescription==null){
				//
				// saved mathDescription not found in origMathModel (too bad), get from database.
				//
				databaseMathDescription = dbServer.getDBTopLevel().getMathDescription(dbc, user,memoryMathDescription.getKey());
			}
			if (databaseMathDescription!=null){
				if (!memoryMathDescription.compareEqual(databaseMathDescription)){
					bMustSaveMathDescription = true;
				}
			}
		}else{
			bMustSaveMathDescription = true;
		}
		if (bMustSaveMathDescription){
			//
			// MathDescription hasn't been saved, has been renamed, or has been forced 'dirty'
			// insert it with a any name (doens't have to be unique ... mathDescription is not a top-level versionable).
			//
			if (databaseMathDescription!=null){
				try {
					mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),memoryMathDescription,databaseMathDescription);
				}catch (Exception e){
					lg.error(e.getLocalizedMessage(),e);
					mathCompareResults = new MathCompareResults(Decision.MathDifferent_FAILURE_UNKNOWN,"Exception: '"+e.getMessage()+"'");
					if (lg.isTraceEnabled()) {
						lg.trace("FAILED TO COMPARE THE FOLLOWING MATH DESCRIPTIONS");
						try {
							lg.trace("MemoryMathDescription:\n"+((memoryMathDescription!=null)?(memoryMathDescription.getVCML_database()):("null")));
							lg.trace("DatabaseMathDescription:\n"+((databaseMathDescription!=null)?(databaseMathDescription.getVCML_database()):("null")));
						}catch (Exception e2){
							lg.error("couldn't print math descriptions: "+e2.getLocalizedMessage(),e2);
						}
					}
				}
			}else{
				mathCompareResults = new MathCompareResults(Decision.MathDifferent_NOT_SAVED);
			}
			KeyValue updatedGeometryKey = memoryMathDescription.getGeometry().getKey();
			KeyValue updatedMathDescriptionKey = null;
 			if (memoryMathDescription.getVersion()!=null && memoryMathDescription.getVersion().getName().equals(memoryMathDescription.getName())){
				updatedMathDescriptionKey = dbServer.getDBTopLevel().updateVersionable(user,memoryMathDescription,updatedGeometryKey,false,true);
 			}else{
	 			updatedMathDescriptionKey = dbServer.getDBTopLevel().insertVersionable(user,memoryMathDescription,updatedGeometryKey,memoryMathDescription.getName(),false,true);
 			}
			MathDescription updatedMathDescription = dbServer.getDBTopLevel().getMathDescription(dbc, user,updatedMathDescriptionKey);
			memoryToDatabaseHash.put(memoryMathDescription,updatedMathDescription);
			bSomethingChanged = true;
		}else{
			mathCompareResults = new MathCompareResults(Decision.MathEquivalent_SAME_MATHDESC_AS_IN_DB);
		}
	}
	//
	// for each Simulation in document:
	//   substitute saved MathDescriptions into Simulation and 
	//   save Simulation if necessary (only once) and store saved instance in hashtable.
	//
	for (int i = 0;simArray!=null && i < simArray.length; i++){
		Simulation memorySimulation = simArray[i];
		if (!memoryToDatabaseHash.containsKey(memorySimulation)){
			//
			// didn't evaluate this Simulation yet.
			//
			memoryToDatabaseHash.put(memorySimulation,memorySimulation); // defaults to unchanged

			boolean bMustSaveSimulation = false;
			MathDescription simMathDescription = memorySimulation.getMathDescription();
			if (simMathDescription!=null && memoryToDatabaseHash.get(simMathDescription)!=simMathDescription){
				if (memoryToDatabaseHash.get(simMathDescription)!=null){ // make sure mathDescription hasn't already propagated (newer math won't be in hashtable)
					//
					// mathDescription had changed and was saved, load saved mathDescription into SimulationContext (and force a save)
					//
					memorySimulation.setMathDescription((MathDescription)memoryToDatabaseHash.get(simMathDescription));
					bMustSaveSimulation = true;
				}
			}
			Simulation databaseSimulation = null;
			//
			// if Simulation had previously been saved
			// compare with original Simulation to see if "update" is required.
			//
			// always get the "original" simulation so that SimulationParentReference can be maintained.
			//
			if (memorySimulation.getKey()!=null){
				if (origMathModel!=null){
					for (int j = 0; j < origMathModel.getNumSimulations(); j++){
						if (origMathModel.getSimulations(j).getKey().equals(memorySimulation.getKey())){
							databaseSimulation = origMathModel.getSimulations(j);
							break;
						}
					}
				}
				if (databaseSimulation==null){
					//
					// saved simulation not found in origBioModel (too bad), get from database.
					//
					databaseSimulation = dbServer.getDBTopLevel().getSimulation(dbc, user,memorySimulation.getKey());
				}
				if (databaseSimulation!=null && !databaseSimulation.compareEqual(memorySimulation)){
					bMustSaveSimulation = true;
				}
				if (!memorySimulation.getVersion().getName().equals(memorySimulation.getName())){
					// name was changed.
					bMustSaveSimulation = true;
				}
			}else{
				// never been saved.
				bMustSaveSimulation = true;
			}
			if (bMustSaveSimulation){		
				KeyValue updatedMathDescriptionKey = memorySimulation.getMathDescription().getKey();
				KeyValue updatedSimulationKey = null;
				boolean bSimMathematicallyEquivalent = false;
				if (databaseSimulation!=null){
					//
					// if to be forced "independent", then set equivalent to false
					//
					boolean bForceIndependent = false;
					for (int j = 0; independentSims!=null && j < independentSims.length; j++){
						if (independentSims[j].equals(memorySimulation.getName())){
							bForceIndependent = true;
						}
					}
					// check for math equivalency
					try {
						bSimMathematicallyEquivalent = !bForceIndependent && Simulation.testEquivalency(memorySimulation, databaseSimulation, mathCompareResults);
					}catch (Exception e){
						lg.error(e.getLocalizedMessage(),e);
						throw new DataAccessException(e.getMessage());
					}
					//
					// don't set equivalent if no data exists.
					//
					if (bSimMathematicallyEquivalent){
						VCSimulationIdentifier vcSimulationIdentifier = databaseSimulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
						SimulationStatusPersistent simStatus = dbServer.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
						if (simStatus==null || !simStatus.getHasData()){
							bSimMathematicallyEquivalent = false;
						}
					}
				}
				if (memorySimulation.getKey()!=null && memorySimulation.getVersion().getName().equals(memorySimulation.getName())){
					// name not changed, update simulation (but pass in database Simulation to check for parent-equivalence)
					updatedSimulationKey = dbServer.getDBTopLevel().updateVersionable(user,memorySimulation,updatedMathDescriptionKey,false,bSimMathematicallyEquivalent,true);
				}else{
					// name changed, insert simulation (but pass in database Simulation to check for parent-equivalence)
					updatedSimulationKey = dbServer.getDBTopLevel().insertVersionable(user,memorySimulation,updatedMathDescriptionKey,memorySimulation.getName(),false,bSimMathematicallyEquivalent,true);
				}						
				Simulation updatedSimulation = dbServer.getDBTopLevel().getSimulation(dbc, user,updatedSimulationKey);
				memoryToDatabaseHash.put(memorySimulation,updatedSimulation);
				bSomethingChanged = true;
			}
		}
	}

	if (bSomethingChanged || origMathModel==null || !mathModel.compareEqual(origMathModel)){
		//
		// create new MathModelMetaData and save to server
		//
		KeyValue mathDescriptionKey = ((MathDescription)memoryToDatabaseHash.get(mathModel.getMathDescription())).getKey();
		KeyValue simKeys[] = new KeyValue[mathModel.getNumSimulations()];
		for (int i = 0; i < mathModel.getNumSimulations(); i++){
			simKeys[i] = ((Simulation)memoryToDatabaseHash.get(mathModel.getSimulations(i))).getKey();
		}
		MathModelMetaData mathModelMetaData = null;
		if (oldVersion==null){
			mathModelMetaData = new MathModelMetaData(mathDescriptionKey, simKeys, mathModel.getName(), mathModel.getDescription(),mathModel.getOutputFunctionContext().getOutputFunctionsList());
		}else{
			mathModelMetaData = new MathModelMetaData(oldVersion, mathDescriptionKey, simKeys,mathModel.getOutputFunctionContext().getOutputFunctionsList());
			if (!mathModel.getDescription().equals(oldVersion.getAnnot())) {
				try {
					mathModelMetaData.setDescription(mathModel.getDescription());
				} catch (java.beans.PropertyVetoException e) {
					lg.error(e.getLocalizedMessage(),e);
				}
			}
		}

		MathModelMetaData updatedMathModelMetaData = null;
		if (mathModel.getVersion()==null || !mathModel.getVersion().getName().equals(mathModel.getName())){
			KeyValue updatedMathModelKey = dbServer.getDBTopLevel().insertVersionable(user,mathModelMetaData,null /*hack*/,mathModel.getName(),false,true);
			updatedMathModelMetaData = dbServer.getDBTopLevel().getMathModelMetaData(dbc, user,updatedMathModelKey);
		}else{
			KeyValue updatedMathModelKey = dbServer.getDBTopLevel().updateVersionable(user,mathModelMetaData,null /*hack*/,false,true);
			updatedMathModelMetaData = dbServer.getDBTopLevel().getMathModelMetaData(dbc, user,updatedMathModelKey);
		}

		//
		// (THIS IS THE REALLY SCARY PART...NOT GETTING A FRESH VIEW OF EVERYTING FROM THE DATABASE FOR CREATING THE XML)
		//
		//mathModelXML = getMathModelXML(user,updatedMathModelMetaData.getVersion().getVersionKey());
		MathModel updatedMathModel = new MathModel(updatedMathModelMetaData.getVersion());
		updatedMathModel.setMathDescription((MathDescription)memoryToDatabaseHash.get(mathModel.getMathDescription()));
		for (int i = 0; i < mathModel.getNumSimulations(); i++){
			updatedMathModel.addSimulation((Simulation)memoryToDatabaseHash.get(mathModel.getSimulations(i)));
		}
		updatedMathModel.getOutputFunctionContext().setOutputFunctions(mathModel.getOutputFunctionContext().getOutputFunctionsList());
		mathModelXML = cbit.vcell.xml.XmlHelper.mathModelToXML(updatedMathModel);
		dbServer.insertVersionableChildSummary(user,VersionableType.MathModelMetaData,updatedMathModel.getVersion().getVersionKey(),
				updatedMathModel.createMathModelChildSummary().toDatabaseSerialization());
		dbServer.insertVersionableXML(user,VersionableType.MathModelMetaData,updatedMathModel.getVersion().getVersionKey(),mathModelXML);
		return mathModelXML;
	} else {
		//If we got here were were doing 'save' or 'save as new version' but no changes were detected
		boolean bError = !bSomethingChanged && !isSaveAsNew;
		if(bError) {
			throw new ServerRejectedSaveException(origMathModel.getVersion().getVersionKey().toString());
		}

		return mathModelXML;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 * @deprecated for testing purposes only.
 */
public String saveSimulation(QueryHashtable dbc, User user, String simulationXML, boolean bForceIndependent) throws DataAccessException, java.sql.SQLException, java.beans.PropertyVetoException, cbit.vcell.xml.XmlParseException {
	//
	// this invokes "update" on the database layer
	//
	Simulation simulation = XmlHelper.XMLToSim(simulationXML);

	forceDeepDirtyIfForeign(user,simulation);
	
	Version oldVersion = simulation.getVersion();

	Simulation origSimulation = null;
	if (oldVersion!=null){
		try {
			origSimulation = dbServer.getDBTopLevel().getSimulation(dbc, user,oldVersion.getVersionKey());
		}catch (ObjectNotFoundException e){
		}
	}

	boolean bSomethingChanged = false;

	//
	// UPDATE AND SUBSTITUTE FROM BOTTOM UP
	//
	//   Image->Geometry
	//   Geometry->MathDescription
	//   MathDescription->Simulation
	//
	
	//
	// if this simulation has an image:
	//   save if necessary (only once) and store saved instance in hashTable
	//
	Hashtable<Versionable,Versionable> memoryToDatabaseHash = new Hashtable<Versionable,Versionable>();
	{
		VCImage memoryImage = simulation.getMathDescription().getGeometry().getGeometrySpec().getImage();
		if (memoryImage!=null){
			memoryToDatabaseHash.put(memoryImage,memoryImage); // defaults to unchanged
			
			if (memoryImage.getKey()!=null && memoryImage.getVersion().getName().equals(memoryImage.getName())){
				//
				// if image had previously been saved, not been forced 'dirty', and name not changed
				// compare with original image to see if "update" is required.
				//
				VCImage databaseImage = null;
				if (origSimulation!=null){
					VCImage origImage = origSimulation.getMathDescription().getGeometry().getGeometrySpec().getImage();
					if (origImage!=null && origImage.getKey().equals(memoryImage.getKey())){
						databaseImage = origImage;
					}
				}
				if (databaseImage==null){
					//
					// saved image not found in origMathModel (too bad), get from database.
					//
					databaseImage = dbServer.getDBTopLevel().getVCImage(dbc, user,memoryImage.getKey(),false);
				}
				if (databaseImage!=null && !databaseImage.compareEqual(memoryImage)){
					KeyValue updatedImageKey = dbServer.getDBTopLevel().updateVersionable(user,memoryImage,false,true);
					VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(dbc, user,updatedImageKey,false);
					memoryToDatabaseHash.put(memoryImage,updatedImage);
					bSomethingChanged = true;
				}
			}else{
				//
				// Image hasn't been saved, has been renamed, or has been forced 'dirty'
				// insert it with a unique name
				//
				int count=0;
				fixNullImageName(memoryImage);
				while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.VCImage,memoryImage.getName(),true)){
					try {
						memoryImage.setName(TokenMangler.getNextRandomToken(memoryImage.getName()));
					}catch (java.beans.PropertyVetoException e){
						lg.error(e.getLocalizedMessage(),e);
					}
					if (count++ > 5){
						throw new DataAccessException("failed to find unique image name '"+memoryImage.getName()+"' is last name tried");
					}
				}
				KeyValue updatedImageKey = dbServer.getDBTopLevel().insertVersionable(user,memoryImage,memoryImage.getName(),false,true);
				VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(dbc, user,updatedImageKey,false);
				memoryToDatabaseHash.put(memoryImage,updatedImage);
				bSomethingChanged = true;
			}
		}
	}
	//
	// for the Geometry:
	//   substitute saved Image into Geometry and 
	//   save Geometry if necessary (only once) and store saved instance in hashtable.
	//
	{
		Geometry memoryGeometry = simulation.getMathDescription().getGeometry();
		memoryToDatabaseHash.put(memoryGeometry,memoryGeometry); // defaults to unchanged

		boolean bMustSaveGeometry = false;
		VCImage geometryImage = memoryGeometry.getGeometrySpec().getImage();
		if (geometryImage!=null && memoryToDatabaseHash.get(geometryImage)!=geometryImage){
			//
			// image had changed and was saved, load saved image into geometry and force a save of this geometry.
			//
			memoryGeometry.getGeometrySpec().setImage((VCImage)memoryToDatabaseHash.get(geometryImage));
			geometryImage = (VCImage)memoryToDatabaseHash.get(geometryImage);
			bMustSaveGeometry = true;
		}
			
		if (memoryGeometry.getKey()!=null && memoryGeometry.getVersion().getName().equals(memoryGeometry.getName())){
			if (!bMustSaveGeometry){
				//
				// if geometry had previously been saved, not been forced 'dirty', and name not changed
				// compare with original geometry to see if "update" is required.
				//
				Geometry databaseGeometry = null;
				if (origSimulation!=null){
					Geometry origGeometry = origSimulation.getMathDescription().getGeometry();
					if (origGeometry.getKey().equals(memoryGeometry.getKey())){
						databaseGeometry = origGeometry;
					}
				}
				if (databaseGeometry==null){
					//
					// saved geometry not found in origMathModel (too bad), get from database.
					//
					databaseGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,memoryGeometry.getKey(),false);
				}
				if (databaseGeometry!=null && !databaseGeometry.compareEqual(memoryGeometry)){
					bMustSaveGeometry = true;
				}
			}
			if (bMustSaveGeometry){
				KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
				KeyValue updatedGeometryKey = dbServer.getDBTopLevel().updateVersionable(dbc, user,memoryGeometry,updatedImageKey,false,true);
				Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,updatedGeometryKey,false);
				memoryToDatabaseHash.put(memoryGeometry,updatedGeometry);
				bSomethingChanged = true;
			}
		}else{
			//
			// Geometry hasn't been saved, has been renamed, or has been forced 'dirty'
			// insert it with a unique name
			//
			int count=0;
			while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.Geometry,memoryGeometry.getName(),true)){
				try {
					memoryGeometry.setName(TokenMangler.getNextRandomToken(memoryGeometry.getName()));
				}catch (java.beans.PropertyVetoException e){
					lg.error(e.getLocalizedMessage(),e);
				}
				if (count++ > 5){
					throw new DataAccessException("failed to find unique geometry name '"+memoryGeometry.getName()+"' is last name tried");
				}
			}
			KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
			KeyValue updatedGeometryKey = dbServer.getDBTopLevel().insertVersionable(dbc, user,memoryGeometry,updatedImageKey,memoryGeometry.getName(),false,true);
			Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(dbc, user,updatedGeometryKey,false);
			memoryToDatabaseHash.put(memoryGeometry,updatedGeometry);
			bSomethingChanged = true;
		}
	}
	//
	// for the MathDescription:
	//   substitute saved geometry into MathDescription
	//   save MathDescription if necessary (only once) and store saved instance in hashtable.
	//
	MathCompareResults mathCompareResults = null;
	{
		MathDescription memoryMathDescription = simulation.getMathDescription();
		memoryToDatabaseHash.put(memoryMathDescription,memoryMathDescription); // defaults to unchanged

		boolean bMustSaveMathDescription = false;
		Geometry scGeometry = memoryMathDescription.getGeometry();
		if (scGeometry!=null && memoryToDatabaseHash.get(scGeometry)!=scGeometry){
			//
			// geometry had changed and was saved, load saved geometry into SimulationContext (and it's MathDescription) and force a save of this SimulationContext.
			//
			memoryMathDescription.setGeometry((Geometry)memoryToDatabaseHash.get(scGeometry));
			bMustSaveMathDescription = true;
		}
		if (memoryMathDescription.getKey()!=null && memoryMathDescription.getVersion().getName().equals(memoryMathDescription.getName())){
			if (!bMustSaveMathDescription){
				//
				// if MathDescription had previously been saved, not been forced 'dirty', and name not changed
				// compare with original MathDescription to see if "update" is required.
				//
				MathDescription databaseMathDescription = null;
				if (origSimulation!=null){
					MathDescription origMathDescription = origSimulation.getMathDescription();
					if (origMathDescription.getKey().equals(memoryMathDescription.getKey())){
						databaseMathDescription = origMathDescription;
					}
				}
				if (databaseMathDescription==null){
					//
					// saved mathDescription not found in origMathModel (too bad), get from database.
					//
					databaseMathDescription = dbServer.getDBTopLevel().getMathDescription(dbc, user,memoryMathDescription.getKey());
				}
				if (databaseMathDescription!=null){
					mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),memoryMathDescription,databaseMathDescription);
					if (!mathCompareResults.decision.equals(Decision.MathEquivalent_SAME_MATHDESC_AS_IN_DB)){
						bMustSaveMathDescription = true;
					}
				}
			}
			if (bMustSaveMathDescription){
				KeyValue updatedGeometryKey = memoryMathDescription.getGeometry().getKey();
				KeyValue updatedMathDescriptionKey = dbServer.getDBTopLevel().updateVersionable(user,memoryMathDescription,updatedGeometryKey,false,true);
				MathDescription updatedMathDescription = dbServer.getDBTopLevel().getMathDescription(dbc, user,updatedMathDescriptionKey);
				memoryToDatabaseHash.put(memoryMathDescription,updatedMathDescription);
				bSomethingChanged = true;
			}
		}else{
			//
			// MathDescription hasn't been saved, has been renamed, or has been forced 'dirty'
			// insert it with a any name (doens't have to be unique ... mathDescription is not a top-level versionable).
			//
			KeyValue updatedGeometryKey = memoryMathDescription.getGeometry().getKey();
			KeyValue updatedMathDescriptionKey = dbServer.getDBTopLevel().insertVersionable(user,memoryMathDescription,updatedGeometryKey,memoryMathDescription.getName(),false,true);
			MathDescription updatedMathDescription = dbServer.getDBTopLevel().getMathDescription(dbc, user,updatedMathDescriptionKey);
			memoryToDatabaseHash.put(memoryMathDescription,updatedMathDescription);
			bSomethingChanged = true;
		}
	}
	//
	// for each Simulation in document:
	//   substitute saved MathDescriptions into Simulation and 
	//   save Simulation if necessary (only once) and store saved instance in hashtable.
	//
	Simulation memorySimulation = simulation;
	//
	// didn't evaluate this Simulation yet.
	//
	memoryToDatabaseHash.put(memorySimulation,memorySimulation); // defaults to unchanged

	boolean bMustSaveSimulation = false;
	MathDescription simMathDescription = memorySimulation.getMathDescription();
	if (simMathDescription!=null && memoryToDatabaseHash.get(simMathDescription)!=simMathDescription){
		if (memoryToDatabaseHash.get(simMathDescription)!=null){ // make sure mathDescription hasn't already propagated (newer math won't be in hashtable)
			//
			// mathDescription had changed and was saved, load saved mathDescription into SimulationContext (and force a save)
			//
			memorySimulation.setMathDescription((MathDescription)memoryToDatabaseHash.get(simMathDescription));
			bMustSaveSimulation = true;
		}
	}
	Simulation databaseSimulation = null;
	//
	// if Simulation had previously been saved
	// compare with original Simulation to see if "update" is required.
	//
	// always get the "original" simulation so that SimulationParentReference can be maintained.
	//
	if (memorySimulation.getKey()!=null){
		databaseSimulation = origSimulation;
		if (databaseSimulation!=null && !databaseSimulation.compareEqual(memorySimulation)){
			bMustSaveSimulation = true;
		}
		if (!memorySimulation.getVersion().getName().equals(memorySimulation.getName())){
			// name was changed.
			bMustSaveSimulation = true;
		}
	}else{
		// never been saved.
		bMustSaveSimulation = true;
	}
	if (bMustSaveSimulation){
		KeyValue updatedMathDescriptionKey = memorySimulation.getMathDescription().getKey();
		KeyValue updatedSimulationKey = null;
		boolean bMathEquivalent = false;
		if (origSimulation != null){
			bMathEquivalent = !bForceIndependent && Simulation.testEquivalency(memorySimulation,origSimulation,mathCompareResults);
		}
		if (memorySimulation.getKey()!=null && memorySimulation.getVersion().getName().equals(memorySimulation.getName())){
			// name not changed, update simulation (but pass in database Simulation to check for parent-equivalence)
			updatedSimulationKey = dbServer.getDBTopLevel().updateVersionable(user,memorySimulation,updatedMathDescriptionKey,false,bMathEquivalent,true);
		}else{
			// name changed, insert simulation (but pass in database Simulation to check for parent-equivalence)
			updatedSimulationKey = dbServer.getDBTopLevel().insertVersionable(user,memorySimulation,updatedMathDescriptionKey,memorySimulation.getName(),false,bMathEquivalent,true);
		}						
		Simulation updatedSimulation = dbServer.getDBTopLevel().getSimulation(dbc, user,updatedSimulationKey);
		memoryToDatabaseHash.put(memorySimulation,updatedSimulation);
		bSomethingChanged = true;
		simulationXML = XmlHelper.simToXML(updatedSimulation);
		return simulationXML;
	} else {
		return simulationXML;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public String saveVCImage(User user,String imageXML,String newName) throws DataAccessException, cbit.vcell.xml.XmlParseException, java.sql.SQLException {

	VCImage image = XmlHelper.XMLToImage(imageXML);
	
	forceDeepDirtyIfForeign(user,image);

	//
	// rename if required
	//
	if (newName!=null){
		try{
			image.setName(newName);
		}catch (java.beans.PropertyVetoException e){
			lg.error(e.getLocalizedMessage(),e);
			throw new DataAccessException("couldn't set new name for Image: "+e.getMessage());
		}
	}

	//
	// save image
	//
	KeyValue imageKey = null;
	if (image.getVersion()!=null && image.getName().equals(image.getVersion().getName())){
		imageKey = dbServer.getDBTopLevel().updateVersionable(user,image,false,true);
	}else{
		imageKey = dbServer.getDBTopLevel().insertVersionable(user,image,image.getName(),false,true);
	}
	
	return dbServer.getVCImageXML(user,imageKey).toString();
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:26:59 PM)
 * @return cbit.sql.Versionable
 * @param versionable cbit.sql.Versionable
 */
private KeyValue update(User user, VCImage image) throws DataAccessException, java.sql.SQLException {
	//
	// if saved previously and no name change and same user, then update, else insert
	//
	if (image.getVersion()!=null && image.getVersion().getOwner().equals(user) && image.getName().equals(image.getVersion().getName())){
		return dbServer.getDBTopLevel().updateVersionable(user,image,false,true);
	}else{
		return dbServer.getDBTopLevel().insertVersionable(user,image,image.getName(),false,true);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 2:26:59 PM)
 * @return cbit.sql.Versionable
 * @param versionable cbit.sql.Versionable
 */
private KeyValue update(User user, BioModelMetaData bioModelMetaData,BioModelChildSummary bmcs) throws DataAccessException, java.sql.SQLException {
	//
	// update in database and get updated edition
	//
	Versionable updatedVersionable = null;

	//
	// if saved previously and no name change and same user, then update, else insert
	//
	if (bioModelMetaData.getVersion()!=null && bioModelMetaData.getVersion().getOwner().equals(user) && bioModelMetaData.getName().equals(bioModelMetaData.getVersion().getName())){
		return dbServer.getDBTopLevel().updateVersionable(user,bioModelMetaData,bmcs,false,true);
	}else{
		return dbServer.getDBTopLevel().insertVersionable(user,bioModelMetaData,bmcs,bioModelMetaData.getName(),false,true);
	}
}

private static void fixNullImageName(VCImage image) throws PropertyVetoException
{
	if(image.getName() == null || image.getName().length() == 0){
		String newImageName = "image" + BeanUtils.generateDateTimeString();
		image.setName(newImageName);
	}
}
}
