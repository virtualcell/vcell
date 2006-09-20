package cbit.vcell.modeldb;

import cbit.util.*;
import cbit.vcell.xml.XmlHelper;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.mathmodel.*;
import cbit.vcell.math.*;
import cbit.vcell.geometry.*;
import cbit.vcell.server.*;
import cbit.vcell.model.*;
import cbit.vcell.mapping.*;
import cbit.vcell.solver.*;
import cbit.image.VCImage;
import cbit.sql.*;
import java.util.*;
import cbit.vcell.biomodel.*;
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

/**
 * ClientDocumentManager constructor comment.
 */
ServerDocumentManager(DatabaseServerImpl argDbServerImpl) {
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
		e.printStackTrace(System.out);
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
		e.printStackTrace(System.out);
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
		e.printStackTrace(System.out);
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
public BioModel getBioModelUnresolved(User user, KeyValue bioModelKey) throws DataAccessException, java.sql.SQLException {
	
	//
	// get meta data associated with BioModel
	//
	BioModelMetaData bioModelMetaData = dbServer.getDBTopLevel().getBioModelMetaData(user,bioModelKey);

	//
	// get list of appropriate child components
	//
	KeyValue modelKey = bioModelMetaData.getModelKey();
	KeyValue simKeys[] = getKeyArrayFromEnumeration(bioModelMetaData.getSimulationKeys());
	KeyValue scKeys[] = getKeyArrayFromEnumeration(bioModelMetaData.getSimulationContextKeys());

	Model model = dbServer.getDBTopLevel().getModel(user,modelKey);

	Simulation simArray[] = new Simulation[simKeys.length];
	for (int i=0;i<simKeys.length;i++){
		Simulation sim = dbServer.getDBTopLevel().getSimulation(user,simKeys[i]);
		//
		// clone Simulations so as to isolate different MathModels
		//
		try {
			simArray[i] = (Simulation)BeanUtils.cloneSerializable(sim);
		}catch (Throwable e){
			throw new RuntimeException("exception cloning Simulation: "+e.getMessage());
		}
	}
	
	SimulationContext scArray[] = new SimulationContext[scKeys.length];
	for (int i=0;i<scKeys.length;i++){
		SimulationContext sc = dbServer.getDBTopLevel().getSimulationContext(user,scKeys[i]);
		//
		// clone SimulationContexts so as to isolate different MathModels
		//
		try {
			scArray[i] = (SimulationContext)BeanUtils.cloneSerializable(sc);
		}catch (Throwable e){
			throw new RuntimeException("exception cloning Application: "+e.getMessage());
		}
		if (!scArray[i].getModel().getKey().compareEqual(modelKey)){
//			throw new DataAccessException("simulationContext("+scKeys[i]+").model = "+scArray[i].getModel().getKey()+", BioModel.model = "+modelKey);
			System.out.println("simulationContext("+scKeys[i]+").model = "+scArray[i].getModel().getKey()+", BioModel.model = "+modelKey);
		}
	}

	//
	// create new BioModel according to loaded BioModelMetaData
	//
	BioModel newBioModel = new BioModel(bioModelMetaData.getVersion());
	try {
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
				System.out.println("<<<<WARNING>>>>> ClientDocumentManager.getBioModel(), Simulation "+simArray[i].getName()+" is orphaned, Math("+simArray[i].getMathDescription().getName()+") not found in Applications");
				simArray = (Simulation[])BeanUtils.removeElement(simArray,simArray[i]);
				i--;
			}
		}
		newBioModel.setSimulations(simArray);
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("PropertyVetoException caught "+e.getMessage());
	}

	//
	// clone BioModel (so that children can't be corrupted in the cache)
	//
	try {
		newBioModel = (BioModel)BeanUtils.cloneSerializable(newBioModel);
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new DataAccessException("BioModel clone failed: "+e.getMessage());
	}

	newBioModel.refreshDependencies();
	
	//
	// return new BioModel
	//
	return newBioModel;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 4:04:16 PM)
 * @return java.lang.String
 * @param vType cbit.sql.VersionableType
 * @param vKey cbit.sql.KeyValue
 */
public String getBioModelXML(User user, KeyValue bioModelKey) throws DataAccessException {
	String bioModelXML = null;
	
	try {
		bioModelXML = dbServer.getDBTopLevel().getBioModelXML(user,bioModelKey,true);
		if (bioModelXML != null){
			return bioModelXML;
		}
	} catch (java.sql.SQLException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	} catch (ObjectNotFoundException e){
		//
		// not stored as XML currently, retrieve traditional way.
		//
	}
	

	try {
		BioModel bm = getBioModelUnresolved(user,bioModelKey);

		bioModelXML = cbit.vcell.xml.XmlHelper.bioModelToXML(bm);
		dbServer.insertVersionableXML(user,VersionableType.BioModelMetaData,bioModelKey,bioModelXML);

		return bioModelXML;
	} catch (java.sql.SQLException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	} catch (cbit.util.xml.XmlParseException e) {
		e.printStackTrace(System.out);
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
public MathModel getMathModelUnresolved(User user, KeyValue mathModelKey) throws DataAccessException, java.sql.SQLException {
	
	//
	// get meta data associated with MathModel
	//
	MathModelMetaData mathModelMetaData = dbServer.getMathModelMetaData(user,mathModelKey);

	//
	// get list of appropriate child components
	//
	KeyValue mathDescriptionKey = mathModelMetaData.getMathKey();
	KeyValue simKeys[] = getKeyArrayFromEnumeration(mathModelMetaData.getSimulationKeys());

	MathDescription mathDescription = dbServer.getDBTopLevel().getMathDescription(user,mathDescriptionKey);

	Simulation simArray[] = new Simulation[simKeys.length];
	for (int i=0;i<simKeys.length;i++){
		Simulation sim = dbServer.getDBTopLevel().getSimulation(user,simKeys[i]);
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
			System.out.println("ClientDocumentManager.getMathModel(), simulation("+simKeys[i]+").mathDescription = "+simArray[i].getMathDescription().getKey()+", MathModel.mathDescription = "+mathDescriptionKey);
		}
	}
	
	//
	// create new MathModel according to loaded MathModelMetaData
	//
	MathModel newMathModel = new MathModel(mathModelMetaData.getVersion());
	try {
		newMathModel.setMathDescription(mathDescription);
		newMathModel.setSimulations(simArray);
	}catch (java.beans.PropertyVetoException e){
		throw new DataAccessException("PropertyVetoException caught "+e.getMessage());
	}

	//
	// clone MathModel (so that children can't be corrupted in the cache)
	//
	try {
		newMathModel = (MathModel)BeanUtils.cloneSerializable(newMathModel);
	}catch (Exception e){
		e.printStackTrace(System.out);
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
public String getMathModelXML(User user, KeyValue mathModelKey) throws DataAccessException {
	String mathModelXML = null;
	
	try {
		mathModelXML = dbServer.getDBTopLevel().getMathModelXML(user,mathModelKey,true);
		if (mathModelXML != null){
			return mathModelXML;
		}
	} catch (java.sql.SQLException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}catch (ObjectNotFoundException e){
		//
		// not stored as XML currently, retrieve traditional way.
		//
	}
	

	try {
		MathModel mathModel = getMathModelUnresolved(user,mathModelKey);

		mathModelXML = cbit.vcell.xml.XmlHelper.mathModelToXML(mathModel);
		dbServer.insertVersionableXML(user,VersionableType.MathModelMetaData,mathModelKey,mathModelXML);

		return mathModelXML;
	} catch (java.sql.SQLException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	} catch (cbit.util.xml.XmlParseException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/01 4:58:40 PM)
 */
boolean isChanged(User user, cbit.image.VCImage vcImage) throws cbit.util.DataAccessException {
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
		savedVersionable = dbServer.getDBTopLevel().getVCImage(user,key,true);
	}catch (ObjectNotFoundException e){
		//
		// loaded version has been deleted
		//
		return true;
	}catch (Throwable e){
		//
		// loaded version has been deleted
		//
		e.printStackTrace(System.out);
		return true;
	}
	return isChanged0(user, vcImage, savedVersionable);

}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
boolean isChanged(User user, Geometry geometry) throws DataAccessException {
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
		savedVersionable = dbServer.getDBTopLevel().getGeometry(user,key,true);
	}catch (ObjectNotFoundException e){
		//
		// loaded version has been deleted
		//
		return true;
	}catch (Throwable e){
		//
		// loaded version has been deleted
		//
		e.printStackTrace(System.out);
		return true;
	}
	
	return isChanged0(user, geometry, savedVersionable);

}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
private boolean isChanged0(User user, cbit.util.Versionable versionable, Versionable savedVersionable) throws DataAccessException {
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
		e.printStackTrace(System.out);
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
public String saveBioModel(User user, String bioModelXML, String newName, String independentSims[]) throws DataAccessException, java.sql.SQLException, java.beans.PropertyVetoException, MappingException, cbit.util.xml.XmlParseException {

long start = System.currentTimeMillis();
	//
	// this invokes "update" on the database layer
	//
	BioModel bioModel = XmlHelper.XMLToBioModel(bioModelXML);

	forceDeepDirtyIfForeign(user,bioModel);
	
	//
	// rename if required
	//
	if (newName!=null){
		try{
			bioModel.setName(newName);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new DataAccessException("couldn't set new name for BioModel: "+e.getMessage());
		}
	}

	Version oldVersion = bioModel.getVersion();

	BioModel origBioModel = null;
	if (oldVersion!=null){
		String origBioModelXML = getBioModelXML(user,oldVersion.getVersionKey());
		origBioModel = XmlHelper.XMLToBioModel(origBioModelXML);
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
	Hashtable memoryToDatabaseHash = new Hashtable();
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
							VCImage origImage = origBioModel.getSimulationContexts(j).getGeometry().getGeometrySpec().getImage();
							if (origImage!=null && origImage.getKey().equals(memoryImage.getKey())){
								databaseImage = origImage;
							}
						}
					}
					if (databaseImage==null){
						//
						// saved image not found in origBioModel (too bad), get from database.
						//
l1 = System.currentTimeMillis();
						databaseImage = dbServer.getDBTopLevel().getVCImage(user,memoryImage.getKey(),false);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
					}
					if (databaseImage!=null && !databaseImage.compareEqual(memoryImage)){
						KeyValue updatedImageKey = dbServer.getDBTopLevel().updateVersionable(user,memoryImage,false,true);
l1 = System.currentTimeMillis();
						VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(user,updatedImageKey,false);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
						memoryToDatabaseHash.put(memoryImage,updatedImage);
						bSomethingChanged = true;
					}
				}else{
					//
					// Image hasn't been saved, has been renamed, or has been forced 'dirty'
					// insert it with a unique name
					//
					int count=0;
					while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.VCImage,memoryImage.getName(),true)){
						try {
							memoryImage.setName(TokenMangler.getNextRandomToken(memoryImage.getName()));
						}catch (java.beans.PropertyVetoException e){
							e.printStackTrace(System.out);
						}
						if (count++ > 5){
							throw new DataAccessException("failed to find unique image name '"+memoryImage.getName()+"' is last name tried");
						}
					}
					KeyValue updatedImageKey = dbServer.getDBTopLevel().insertVersionable(user,memoryImage,memoryImage.getName(),false,true);
l1 = System.currentTimeMillis();
					VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(user,updatedImageKey,false);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
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
							Geometry origGeometry = origBioModel.getSimulationContexts(j).getGeometry();
							if (origGeometry!=null && origGeometry.getKey().equals(memoryGeometry.getKey())){
								databaseGeometry = origGeometry;
							}
						}
					}
					if (databaseGeometry==null){
						//
						// saved geometry not found in origBioModel (too bad), get from database.
						//
l1 = System.currentTimeMillis();
						databaseGeometry = dbServer.getDBTopLevel().getGeometry(user,memoryGeometry.getKey(),false);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
					}
					if (databaseGeometry!=null && !databaseGeometry.compareEqual(memoryGeometry)){
						bMustSaveGeometry = true;
					}
				}
				if (bMustSaveGeometry){
					KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
					KeyValue updatedGeometryKey = dbServer.getDBTopLevel().updateVersionable(user,memoryGeometry,updatedImageKey,false,true);
l1 = System.currentTimeMillis();
					Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(user,updatedGeometryKey,false);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
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
						e.printStackTrace(System.out);
					}
					if (count++ > 5){
						throw new DataAccessException("failed to find unique geometry name '"+memoryGeometry.getName()+"' is last name tried");
					}
				}
				KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
				KeyValue updatedGeometryKey = dbServer.getDBTopLevel().insertVersionable(user,memoryGeometry,updatedImageKey,memoryGeometry.getName(),false,true);
l1 = System.currentTimeMillis();
				Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(user,updatedGeometryKey,false);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
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
	Hashtable mathEquivalencyHash = new Hashtable();
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
						MathDescription math = origBioModel.getSimulationContexts(j).getMathDescription();
						if (math.getKey().equals(memoryMathDescription.getKey())){
							databaseMathDescription = math;
						}
					}
				}
				if (databaseMathDescription==null){
					//
					// saved mathDescription not found in origBioModel (too bad), get from database.
					//
l1 = System.currentTimeMillis();
					databaseMathDescription = dbServer.getDBTopLevel().getMathDescription(user,memoryMathDescription.getKey());
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
				}
				if (databaseMathDescription!=null && !databaseMathDescription.compareEqual(memoryMathDescription)){
					bMustSaveMathDescription = true;
				}
			}else{
				bMustSaveMathDescription = true;
			}
			if (bMustSaveMathDescription){
				String mathEquivalency = null;
				if (databaseMathDescription!=null){
					StringBuffer reasonBuffer = new StringBuffer();
					try {
						mathEquivalency = MathDescription.testEquivalency(memoryMathDescription,databaseMathDescription,reasonBuffer);
					}catch (Exception e){
						e.printStackTrace(System.out);
						mathEquivalency = MathDescription.MATH_DIFFERENT;
						System.out.println("FAILED TO COMPARE THE FOLLOWING MATH DESCRIPTIONS");
						try {
							System.out.println("MemoryMathDescription:\n"+((memoryMathDescription!=null)?(memoryMathDescription.getVCML_database()):("null")));
							System.out.println("DatabaseMathDescription:\n"+((databaseMathDescription!=null)?(databaseMathDescription.getVCML_database()):("null")));
						}catch (Exception e2){
							System.out.println("couldn't print math descriptions");
						}
					}
				}else{
					mathEquivalency = MathDescription.MATH_DIFFERENT;
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
					
l1 = System.currentTimeMillis();
				MathDescription updatedMathDescription = dbServer.getDBTopLevel().getMathDescription(user,updatedMathDescriptionKey);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
				memoryToDatabaseHash.put(memoryMathDescription,updatedMathDescription);
				mathEquivalencyHash.put(updatedMathDescription,mathEquivalency);
				bSomethingChanged = true;
			}else{
				mathEquivalencyHash.put(memoryMathDescription,MathDescription.MATH_SAME);
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
l1 = System.currentTimeMillis();
				databaseModel = dbServer.getDBTopLevel().getModel(user,memoryModel.getKey());
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
			}
			if (databaseModel!=null && !databaseModel.compareEqual(memoryModel)){
				KeyValue updatedModelKey = dbServer.getDBTopLevel().updateVersionable(user,memoryModel,false,true);
l1 = System.currentTimeMillis();
				Model updatedModel = dbServer.getDBTopLevel().getModel(user,updatedModelKey);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
				memoryToDatabaseHash.put(memoryModel,updatedModel);
				bSomethingChanged = true;
			}
		}else{
			//
			// Model hasn't been saved, has been renamed, or has been forced 'dirty'
			// insert it with a any name (doens't have to be unique ... mathDescription is not a top-level versionable).
			//
			KeyValue updatedModelKey = dbServer.getDBTopLevel().insertVersionable(user,memoryModel,memoryModel.getName(),false,true);
l1 = System.currentTimeMillis();
			Model updatedModel = dbServer.getDBTopLevel().getModel(user,updatedModelKey);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
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
							if (origBioModel.getSimulationContexts(j).getKey().equals(memorySimContext.getKey())){
								databaseSimContext = origBioModel.getSimulationContexts(j);
							}
						}
					}
					if (databaseSimContext==null){
						//
						// saved geometry not found in origBioModel (too bad), get from database.
						//
l1 = System.currentTimeMillis();
						databaseSimContext = dbServer.getDBTopLevel().getSimulationContext(user,memorySimContext.getKey());
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
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
l1 = System.currentTimeMillis();
					SimulationContext updatedSimContext = dbServer.getDBTopLevel().getSimulationContext(user,updatedSimContextKey);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
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
l1 = System.currentTimeMillis();
				SimulationContext updatedSimContext = dbServer.getDBTopLevel().getSimulationContext(user,updatedSimContextKey);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
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
						if (origBioModel.getSimulations(j).getKey().equals(memorySimulation.getKey())){
							databaseSimulation = origBioModel.getSimulations(j);
						}
					}
				}
				if (databaseSimulation==null){
					//
					// saved simulation not found in origBioModel (too bad), get from database.
					//
l1 = System.currentTimeMillis();
					databaseSimulation = dbServer.getDBTopLevel().getSimulation(user,memorySimulation.getKey());
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
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
					String mathEquivalency = (String)mathEquivalencyHash.get(memorySimulation.getMathDescription());
					bMathematicallyEquivalent = !bForceIndependent && Simulation.testEquivalency(memorySimulation,databaseSimulation,mathEquivalency);
					//
					// don't set equivalent if no data exists.
					//
					if (bMathematicallyEquivalent){
						VCSimulationIdentifier vcSimulationIdentifier = databaseSimulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
						SimulationStatus simStatus = dbServer.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
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
l1 = System.currentTimeMillis();
				Simulation updatedSimulation = dbServer.getDBTopLevel().getSimulation(user,updatedSimulationKey);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
				//
				// make sure mathDescription is a single reference (for an app and all of it's Simulations).
				//
				updatedSimulation.setMathDescription((MathDescription)memorySimulation.getMathDescription());
				
				memoryToDatabaseHash.put(memorySimulation,updatedSimulation);
				bSomethingChanged = true;
			}
		}
	}

	if (bSomethingChanged || origBioModel==null || !bioModel.compareEqual(origBioModel)){
		//
		// create new BioModelMetaData and save to server
		//
		KeyValue modelKey = ((Model)memoryToDatabaseHash.get(bioModel.getModel())).getKey();
		KeyValue scKeys[] = new KeyValue[bioModel.getNumSimulationContexts()];
		for (int i = 0; i < bioModel.getNumSimulationContexts(); i++){
			scKeys[i] = ((SimulationContext)memoryToDatabaseHash.get(bioModel.getSimulationContexts(i))).getKey();
		}
		KeyValue simKeys[] = new KeyValue[bioModel.getNumSimulations()];
		for (int i = 0; i < bioModel.getNumSimulations(); i++){
			simKeys[i] = ((Simulation)memoryToDatabaseHash.get(bioModel.getSimulations(i))).getKey();
		}
		BioModelMetaData bioModelMetaData = null;
		if (oldVersion==null){
			bioModelMetaData = new BioModelMetaData(modelKey, scKeys, simKeys, bioModel.getName(), bioModel.getDescription());
		}else{
			bioModelMetaData = new BioModelMetaData(oldVersion, modelKey, scKeys, simKeys);
			if (!bioModel.getDescription().equals(oldVersion.getAnnot())) {
				try {
					bioModelMetaData.setDescription(bioModel.getDescription());
				} catch (java.beans.PropertyVetoException e) {
					e.printStackTrace(System.out);
				}
			}
		}

		BioModelMetaData updatedBioModelMetaData = null;
		if (bioModel.getVersion()==null || !bioModel.getVersion().getName().equals(bioModel.getName())){
			KeyValue updatedBioModelKey = dbServer.getDBTopLevel().insertVersionable(user,bioModelMetaData,null/*hack*/,bioModel.getName(),false,true);
l1 = System.currentTimeMillis();
			updatedBioModelMetaData = dbServer.getDBTopLevel().getBioModelMetaData(user,updatedBioModelKey);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
		}else{
			KeyValue updatedBioModelKey = dbServer.getDBTopLevel().updateVersionable(user,bioModelMetaData,null/*hack*/,false,true);
l1 = System.currentTimeMillis();
			updatedBioModelMetaData = dbServer.getDBTopLevel().getBioModelMetaData(user,updatedBioModelKey);
l2 = System.currentTimeMillis();
roundtripTimer += l2 - l1;
		}

		//
		// (THIS IS THE REALLY SCAREY PART...NOT GETTING A FRESH VIEW OF EVERYTING FROM THE DATABASE FOR CREATING THE XML)
		//
		//bioModelXML = getBioModelXML(user,updatedBioModelMetaData.getVersion().getVersionKey());
		BioModel updatedBioModel = new BioModel(updatedBioModelMetaData.getVersion());
		updatedBioModel.setModel((Model)memoryToDatabaseHash.get(bioModel.getModel()));
		for (int i = 0; i < bioModel.getNumSimulationContexts(); i++){
			updatedBioModel.addSimulationContext((SimulationContext)memoryToDatabaseHash.get(bioModel.getSimulationContexts(i)));
		}
		for (int i = 0; i < bioModel.getNumSimulations(); i++){
			updatedBioModel.addSimulation((Simulation)memoryToDatabaseHash.get(bioModel.getSimulations(i)));
		}
		bioModelXML = cbit.vcell.xml.XmlHelper.bioModelToXML(updatedBioModel);
		dbServer.insertVersionableChildSummary(user,VersionableType.BioModelMetaData,updatedBioModel.getVersion().getVersionKey(),
												BioModelChildSummary.fromDatabaseBioModel(updatedBioModel).toDatabaseSerialization());
		dbServer.insertVersionableXML(user,VersionableType.BioModelMetaData,updatedBioModel.getVersion().getVersionKey(),bioModelXML);
System.out.println("------------------------------> Total time: " + ((double)(System.currentTimeMillis() - start)) / 1000);
System.out.println("------------------------------> Time spent on roundtrip: " + ((double)roundtripTimer) / 1000);
		return bioModelXML;
	} else {
System.out.println("------------------------------> Total time: " + ((double)(System.currentTimeMillis() - start)) / 1000);
System.out.println("------------------------------> Time spent on roundtrip: " + ((double)roundtripTimer) / 1000);
		return bioModelXML;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public String saveGeometry(User user,String geometryXML,String newName) throws DataAccessException, cbit.util.xml.XmlParseException, java.sql.SQLException {

	Geometry geometry = XmlHelper.XMLToGeometry(geometryXML);
	
	forceDeepDirtyIfForeign(user,geometry);

	//
	// rename if required
	//
	if (newName!=null){
		try{
			geometry.setName(newName);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
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
			while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.VCImage,image.getName(),true)){
				try {
					image.setName(TokenMangler.getNextRandomToken(image.getName()));
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
				}
			}
			updatedImageKey = dbServer.getDBTopLevel().insertVersionable(user,image,image.getName(),false,true);
		}else{
			//
			// Image has been saved previously, get old image from database
			//
			VCImage origImage = dbServer.getDBTopLevel().getVCImage(user,image.getKey(),false);
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
		geometryKey = dbServer.getDBTopLevel().updateVersionable(user,geometry,updatedImageKey,false,true);
	}else{
		geometryKey = dbServer.getDBTopLevel().insertVersionable(user,geometry,updatedImageKey,geometry.getName(),false,true);
	}
	
	return dbServer.getGeometryXML(user,geometryKey).toString();
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 */
public String saveMathModel(User user, String mathModelXML, String newName, String independentSims[]) throws DataAccessException, java.sql.SQLException, java.beans.PropertyVetoException, cbit.util.xml.XmlParseException {
	//
	// this invokes "update" on the database layer
	//
	MathModel mathModel = XmlHelper.XMLToMathModel(mathModelXML);

	forceDeepDirtyIfForeign(user,mathModel);
	
	//
	// rename if required
	//
	if (newName!=null){
		try{
			mathModel.setName(newName);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new DataAccessException("couldn't set new name for MathModel: "+e.getMessage());
		}
	}

	Version oldVersion = mathModel.getVersion();

	MathModel origMathModel = null;
	if (oldVersion!=null){
		String origMathModelXML = getMathModelXML(user,oldVersion.getVersionKey());
		origMathModel = XmlHelper.XMLToMathModel(origMathModelXML);
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
	Hashtable memoryToDatabaseHash = new Hashtable();
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
					databaseImage = dbServer.getDBTopLevel().getVCImage(user,memoryImage.getKey(),false);
				}
				if (databaseImage!=null && !databaseImage.compareEqual(memoryImage)){
					KeyValue updatedImageKey = dbServer.getDBTopLevel().updateVersionable(user,memoryImage,false,true);
					VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(user,updatedImageKey,false);
					memoryToDatabaseHash.put(memoryImage,updatedImage);
					bSomethingChanged = true;
				}
			}else{
				//
				// Image hasn't been saved, has been renamed, or has been forced 'dirty'
				// insert it with a unique name
				//
				int count=0;
				while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.VCImage,memoryImage.getName(),true)){
					try {
						memoryImage.setName(TokenMangler.getNextRandomToken(memoryImage.getName()));
					}catch (java.beans.PropertyVetoException e){
						e.printStackTrace(System.out);
					}
					if (count++ > 5){
						throw new DataAccessException("failed to find unique image name '"+memoryImage.getName()+"' is last name tried");
					}
				}
				KeyValue updatedImageKey = dbServer.getDBTopLevel().insertVersionable(user,memoryImage,memoryImage.getName(),false,true);
				VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(user,updatedImageKey,false);
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
					databaseGeometry = dbServer.getDBTopLevel().getGeometry(user,memoryGeometry.getKey(),false);
				}
				if (databaseGeometry!=null && !databaseGeometry.compareEqual(memoryGeometry)){
					bMustSaveGeometry = true;
				}
			}
			if (bMustSaveGeometry){
				KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
				KeyValue updatedGeometryKey = dbServer.getDBTopLevel().updateVersionable(user,memoryGeometry,updatedImageKey,false,true);
				Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(user,updatedGeometryKey,false);
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
					e.printStackTrace(System.out);
				}
				if (count++ > 5){
					throw new DataAccessException("failed to find unique geometry name '"+memoryGeometry.getName()+"' is last name tried");
				}
			}
			KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
			KeyValue updatedGeometryKey = dbServer.getDBTopLevel().insertVersionable(user,memoryGeometry,updatedImageKey,memoryGeometry.getName(),false,true);
			Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(user,updatedGeometryKey,false);
			memoryToDatabaseHash.put(memoryGeometry,updatedGeometry);
			bSomethingChanged = true;
		}
	}
	//
	// for the MathDescription:
	//   substitute saved geometry into MathDescription
	//   save MathDescription if necessary (only once) and store saved instance in hashtable.
	//
	String mathEquivalency = null;
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
				databaseMathDescription = dbServer.getDBTopLevel().getMathDescription(user,memoryMathDescription.getKey());
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
				StringBuffer reasonBuffer = new StringBuffer();
				try {
					mathEquivalency = MathDescription.testEquivalency(memoryMathDescription,databaseMathDescription,reasonBuffer);
				}catch (Exception e){
					e.printStackTrace(System.out);
					mathEquivalency = MathDescription.MATH_DIFFERENT;
					System.out.println("FAILED TO COMPARE THE FOLLOWING MATH DESCRIPTIONS");
					try {
						System.out.println("MemoryMathDescription:\n"+((memoryMathDescription!=null)?(memoryMathDescription.getVCML_database()):("null")));
						System.out.println("DatabaseMathDescription:\n"+((databaseMathDescription!=null)?(databaseMathDescription.getVCML_database()):("null")));
					}catch (Exception e2){
						System.out.println("couldn't print math descriptions");
					}
				}
			}else{
				mathEquivalency = MathDescription.MATH_DIFFERENT;
			}
			KeyValue updatedGeometryKey = memoryMathDescription.getGeometry().getKey();
			KeyValue updatedMathDescriptionKey = null;
 			if (memoryMathDescription.getVersion()!=null && memoryMathDescription.getVersion().getName().equals(memoryMathDescription.getName())){
				updatedMathDescriptionKey = dbServer.getDBTopLevel().updateVersionable(user,memoryMathDescription,updatedGeometryKey,false,true);
 			}else{
	 			updatedMathDescriptionKey = dbServer.getDBTopLevel().insertVersionable(user,memoryMathDescription,updatedGeometryKey,memoryMathDescription.getName(),false,true);
 			}
			MathDescription updatedMathDescription = dbServer.getDBTopLevel().getMathDescription(user,updatedMathDescriptionKey);
			memoryToDatabaseHash.put(memoryMathDescription,updatedMathDescription);
			bSomethingChanged = true;
		}else{
			mathEquivalency = MathDescription.MATH_SAME;
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
					databaseSimulation = dbServer.getDBTopLevel().getSimulation(user,memorySimulation.getKey());
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
						bSimMathematicallyEquivalent = !bForceIndependent && Simulation.testEquivalency(memorySimulation, databaseSimulation, mathEquivalency);
					}catch (Exception e){
						e.printStackTrace(System.out);
						throw new DataAccessException(e.getMessage());
					}
					//
					// don't set equivalent if no data exists.
					//
					if (bSimMathematicallyEquivalent){
						VCSimulationIdentifier vcSimulationIdentifier = databaseSimulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
						SimulationStatus simStatus = dbServer.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
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
				Simulation updatedSimulation = dbServer.getDBTopLevel().getSimulation(user,updatedSimulationKey);
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
			mathModelMetaData = new MathModelMetaData(mathDescriptionKey, simKeys, mathModel.getName(), mathModel.getDescription());
		}else{
			mathModelMetaData = new MathModelMetaData(oldVersion, mathDescriptionKey, simKeys);
			if (!mathModel.getDescription().equals(oldVersion.getAnnot())) {
				try {
					mathModelMetaData.setDescription(mathModel.getDescription());
				} catch (java.beans.PropertyVetoException e) {
					e.printStackTrace(System.out);
				}
			}
		}

		MathModelMetaData updatedMathModelMetaData = null;
		if (mathModel.getVersion()==null || !mathModel.getVersion().getName().equals(mathModel.getName())){
			KeyValue updatedMathModelKey = dbServer.getDBTopLevel().insertVersionable(user,mathModelMetaData,null /*hack*/,mathModel.getName(),false,true);
			updatedMathModelMetaData = dbServer.getDBTopLevel().getMathModelMetaData(user,updatedMathModelKey);
		}else{
			KeyValue updatedMathModelKey = dbServer.getDBTopLevel().updateVersionable(user,mathModelMetaData,null /*hack*/,false,true);
			updatedMathModelMetaData = dbServer.getDBTopLevel().getMathModelMetaData(user,updatedMathModelKey);
		}

		//
		// (THIS IS THE REALLY SCAREY PART...NOT GETTING A FRESH VIEW OF EVERYTING FROM THE DATABASE FOR CREATING THE XML)
		//
		//mathModelXML = getMathModelXML(user,updatedMathModelMetaData.getVersion().getVersionKey());
		MathModel updatedMathModel = new MathModel(updatedMathModelMetaData.getVersion());
		updatedMathModel.setMathDescription((MathDescription)memoryToDatabaseHash.get(mathModel.getMathDescription()));
		for (int i = 0; i < mathModel.getNumSimulations(); i++){
			updatedMathModel.addSimulation((Simulation)memoryToDatabaseHash.get(mathModel.getSimulations(i)));
		}
		mathModelXML = cbit.vcell.xml.XmlHelper.mathModelToXML(updatedMathModel);
		dbServer.insertVersionableChildSummary(user,VersionableType.MathModelMetaData,updatedMathModel.getVersion().getVersionKey(),
												MathModelChildSummary.fromDatabaseMathModel(updatedMathModel).toDatabaseSerialization());
		dbServer.insertVersionableXML(user,VersionableType.MathModelMetaData,updatedMathModel.getVersion().getVersionKey(),mathModelXML);
		return mathModelXML;
	} else {
		return mathModelXML;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/28/00 12:08:30 AM)
 * @deprecated for testing purposes only.
 */
public String saveSimulation(User user, String simulationXML, boolean bForceIndependent) throws DataAccessException, java.sql.SQLException, java.beans.PropertyVetoException, cbit.util.xml.XmlParseException {
	//
	// this invokes "update" on the database layer
	//
	Simulation simulation = XmlHelper.XMLToSim(simulationXML);

	forceDeepDirtyIfForeign(user,simulation);
	
	Version oldVersion = simulation.getVersion();

	Simulation origSimulation = null;
	if (oldVersion!=null){
		try {
			origSimulation = dbServer.getDBTopLevel().getSimulation(user,oldVersion.getVersionKey());
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
	Hashtable memoryToDatabaseHash = new Hashtable();
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
					databaseImage = dbServer.getDBTopLevel().getVCImage(user,memoryImage.getKey(),false);
				}
				if (databaseImage!=null && !databaseImage.compareEqual(memoryImage)){
					KeyValue updatedImageKey = dbServer.getDBTopLevel().updateVersionable(user,memoryImage,false,true);
					VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(user,updatedImageKey,false);
					memoryToDatabaseHash.put(memoryImage,updatedImage);
					bSomethingChanged = true;
				}
			}else{
				//
				// Image hasn't been saved, has been renamed, or has been forced 'dirty'
				// insert it with a unique name
				//
				int count=0;
				while (dbServer.getDBTopLevel().isNameUsed(user,VersionableType.VCImage,memoryImage.getName(),true)){
					try {
						memoryImage.setName(TokenMangler.getNextRandomToken(memoryImage.getName()));
					}catch (java.beans.PropertyVetoException e){
						e.printStackTrace(System.out);
					}
					if (count++ > 5){
						throw new DataAccessException("failed to find unique image name '"+memoryImage.getName()+"' is last name tried");
					}
				}
				KeyValue updatedImageKey = dbServer.getDBTopLevel().insertVersionable(user,memoryImage,memoryImage.getName(),false,true);
				VCImage updatedImage = dbServer.getDBTopLevel().getVCImage(user,updatedImageKey,false);
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
					databaseGeometry = dbServer.getDBTopLevel().getGeometry(user,memoryGeometry.getKey(),false);
				}
				if (databaseGeometry!=null && !databaseGeometry.compareEqual(memoryGeometry)){
					bMustSaveGeometry = true;
				}
			}
			if (bMustSaveGeometry){
				KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
				KeyValue updatedGeometryKey = dbServer.getDBTopLevel().updateVersionable(user,memoryGeometry,updatedImageKey,false,true);
				Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(user,updatedGeometryKey,false);
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
					e.printStackTrace(System.out);
				}
				if (count++ > 5){
					throw new DataAccessException("failed to find unique geometry name '"+memoryGeometry.getName()+"' is last name tried");
				}
			}
			KeyValue updatedImageKey = (geometryImage!=null)?(geometryImage.getKey()):(null);
			KeyValue updatedGeometryKey = dbServer.getDBTopLevel().insertVersionable(user,memoryGeometry,updatedImageKey,memoryGeometry.getName(),false,true);
			Geometry updatedGeometry = dbServer.getDBTopLevel().getGeometry(user,updatedGeometryKey,false);
			memoryToDatabaseHash.put(memoryGeometry,updatedGeometry);
			bSomethingChanged = true;
		}
	}
	//
	// for the MathDescription:
	//   substitute saved geometry into MathDescription
	//   save MathDescription if necessary (only once) and store saved instance in hashtable.
	//
	String mathEquivalency = null;
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
					databaseMathDescription = dbServer.getDBTopLevel().getMathDescription(user,memoryMathDescription.getKey());
				}
				if (databaseMathDescription!=null){
					StringBuffer reasonBuffer = new StringBuffer();
					mathEquivalency = MathDescription.testEquivalency(memoryMathDescription,databaseMathDescription,reasonBuffer);
					if (!mathEquivalency.equals(MathDescription.MATH_SAME)){
						bMustSaveMathDescription = true;
					}
				}
			}
			if (bMustSaveMathDescription){
				KeyValue updatedGeometryKey = memoryMathDescription.getGeometry().getKey();
				KeyValue updatedMathDescriptionKey = dbServer.getDBTopLevel().updateVersionable(user,memoryMathDescription,updatedGeometryKey,false,true);
				MathDescription updatedMathDescription = dbServer.getDBTopLevel().getMathDescription(user,updatedMathDescriptionKey);
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
			MathDescription updatedMathDescription = dbServer.getDBTopLevel().getMathDescription(user,updatedMathDescriptionKey);
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
			bMathEquivalent = !bForceIndependent && Simulation.testEquivalency(memorySimulation,origSimulation,mathEquivalency);
		}
		if (memorySimulation.getKey()!=null && memorySimulation.getVersion().getName().equals(memorySimulation.getName())){
			// name not changed, update simulation (but pass in database Simulation to check for parent-equivalence)
			updatedSimulationKey = dbServer.getDBTopLevel().updateVersionable(user,memorySimulation,updatedMathDescriptionKey,false,bMathEquivalent,true);
		}else{
			// name changed, insert simulation (but pass in database Simulation to check for parent-equivalence)
			updatedSimulationKey = dbServer.getDBTopLevel().insertVersionable(user,memorySimulation,updatedMathDescriptionKey,memorySimulation.getName(),false,bMathEquivalent,true);
		}						
		Simulation updatedSimulation = dbServer.getDBTopLevel().getSimulation(user,updatedSimulationKey);
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
public String saveVCImage(User user,String imageXML,String newName) throws DataAccessException, cbit.util.xml.XmlParseException, java.sql.SQLException {

	VCImage image = XmlHelper.XMLToImage(imageXML);
	
	forceDeepDirtyIfForeign(user,image);

	//
	// rename if required
	//
	if (newName!=null){
		try{
			image.setName(newName);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
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
}