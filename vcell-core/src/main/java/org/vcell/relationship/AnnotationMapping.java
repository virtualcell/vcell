/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.relationship;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.rpc.ServiceException;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BiochemicalReactionImpl;
import org.vcell.pathway.EntityImpl;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.PublicationXref;
import org.vcell.pathway.RelationshipXref;
import org.vcell.pathway.SmallMolecule;
import org.vcell.pathway.TransportImpl;
import org.vcell.pathway.UnificationXref;
import org.vcell.pathway.Xref;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.Identifiable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import uk.ac.ebi.jdbfetch.exceptions.DbfConnException;
import uk.ac.ebi.jdbfetch.exceptions.DbfException;
import uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException;
import uk.ac.ebi.jdbfetch.exceptions.DbfParamsException;
import uk.ac.ebi.www.ws.services.WSDbfetch.InputException;

public class AnnotationMapping {
		
	public String annotation2BioPaxObject(BioModel bioModel, Identifiable identifiable){
		String name = "";
		String type = "";
		ArrayList<String> componentInfo = getComponentInfo(bioModel, identifiable);
		if(componentInfo.size() > 0){
			name = componentInfo.get(0);
			type = componentInfo.get(1);
		}
		String info = type + " : " + name;
		HashMap<String, String> refInfo = getRefInfo(bioModel, identifiable);
		// check whether the object is existed: if so, check the linkage, if not linked -> add linkage
		 //			  otherwise, create object and add link
		 if(type.equals(VCID.CLASS_SPECIES) ){
			 //lookup name by speciesContext
			ArrayList<SpeciesContext> speciesContextArrList = new ArrayList<SpeciesContext>(Arrays.asList(new SpeciesContext[] {bioModel.getModel().getSpeciesContext(name)}));
			if(speciesContextArrList.get(0) == null){
				speciesContextArrList.clear();
				//lookup name by species
				for (int i = 0; i < bioModel.getModel().getSpeciesContexts().length; i++) {
					if(bioModel.getModel().getSpeciesContexts()[i].getSpecies().getCommonName().equals(name)){
						speciesContextArrList.add(bioModel.getModel().getSpeciesContexts()[i]);
					}
				}
			}
			for (int i = 0; i < speciesContextArrList.size(); i++) {
				SpeciesContext speciesContext = speciesContextArrList.get(i);
				if(bioModel.getRelationshipModel().getRelationshipObjects(speciesContext).size() == 0){
					ArrayList<Xref> xRef = getXrefs(bioModel, refInfo);
					ArrayList<String> refName = getNameRef(xRef, name);
					BioPaxObject bpObject = bioModel.getPathwayModel().findFromNameAndType(refName.get(0), EntityImpl.TYPE_PHYSICALENTITY);
					if(bpObject == null){
						bpObject = createPhysicalEntity(xRef, refName, name);
						bioModel.getPathwayModel().add(bpObject);
					}
					if(!isLinked(bioModel, bpObject, speciesContext)) {
						// create linkage
						RelationshipObject newRelationship = new RelationshipObject(speciesContext, bpObject);
						bioModel.getRelationshipModel().addRelationshipObject(newRelationship);	
						return info;
					}
				}				
			}
		 }else if(type.equals(VCID.CLASS_BIOMODEL)){
//				BioPaxObject bpObject = bioModel.getPathwayModel().findFromName(refName.get(0), "Pathway");
//				if(bpObject == null){
//					bpObject = createPathway(componentInfo, refInfo);
//					bioModel.getPathwayModel().add(bpObject);
//					return info;
//				}
		 }else if(type.equals(VCID.CLASS_REACTION_STEP) ){
			 	ReactionStep reactionStep = bioModel.getModel().getReactionStep(name);
			 	ArrayList<Xref> xRef = getXrefs(bioModel, refInfo);
				ArrayList<String> refName = getNameRef(xRef, name);
			 	if(bioModel.getRelationshipModel().getRelationshipObjects(reactionStep).size() == 0){
					BioPaxObject bpObject = bioModel.getPathwayModel().findFromNameAndType(refName.get(0), EntityImpl.TYPE_INTERACTION);
					if(bpObject == null){
						bpObject = createInteraction(reactionStep, xRef, refName);
						bioModel.getPathwayModel().add(bpObject);
					}
					if(!isLinked(bioModel, bpObject, reactionStep)) {
						// create linkage
						RelationshipObject newRelationship = new RelationshipObject(reactionStep, bpObject);
						bioModel.getRelationshipModel().addRelationshipObject(newRelationship);	
						return info;
					}
			 	}
		 }
		 return null;
	}
	
	private boolean isLinked(BioModel bioModel, BioPaxObject bpObject, BioModelEntityObject bmeObject){
		boolean islinked = false;
		for(RelationshipObject rObject : bioModel.getRelationshipModel().getRelationshipObjects(bpObject)){
			if(rObject.getBioModelEntityObject() == bmeObject)
				islinked = true;
		}
		return islinked;
	}
	
	private PhysicalEntity createPhysicalEntity(ArrayList<Xref> xRef, ArrayList<String>  refName, String name){
		PhysicalEntity physicalEntity = null;
		if(isSmallMolecule(xRef)){
			physicalEntity = new SmallMolecule();
		}else{
			physicalEntity = new PhysicalEntity();
		}
		physicalEntity.setName(refName);
		physicalEntity.setID("BIOMODEL_"+ name);
//		physicalEntity.setxRef(xRef);
		for(Xref ref : xRef){
			physicalEntity.getxRef().add(ref);
		}
		return physicalEntity;
	}
	
	private Interaction createInteraction(ReactionStep reactionStep, ArrayList<Xref> xRef, ArrayList<String> name){
		Interaction interaction = null;
		if(reactionStep instanceof SimpleReaction){
			interaction = new BiochemicalReactionImpl();
		}
		else if(reactionStep instanceof FluxReaction){
			interaction = new TransportImpl();
		}
		interaction.setName(name);
		interaction.setID("BIOMODEL_"+reactionStep.getName());
		for(Xref ref : xRef){
			interaction.getxRef().add(ref);
		}
		return interaction;
		
	}
	
//	private Pathway createPathway(ArrayList<String> componentInfo, HashMap<String, String> refInfo){
//		Pathway pathway = new Pathway();
//		ArrayList<String> name = getName(componentInfo);
//		pathway.setName(name);
//		pathway.setxRef(getXrefs(refInfo));
//		pathway.setID(name.get(0));
//		return pathway;
//	}
	
	private boolean isSmallMolecule(ArrayList<Xref> xRef){
		HashSet<String> refDB = new HashSet<String>();
		for(Xref xref : xRef){
			String ref = xref.getDb().toLowerCase();
			if(ref.contains("chebi")) ref = "chebi";
			refDB.add(ref);
		}
		if(refDB.contains("chebi") && !refDB.contains("uniprot"))
			return true;
		return false;
	}
	
	private ArrayList<Xref> getXrefs(BioModel bioModel, HashMap<String, String> refInfo){
		ArrayList<Xref> xRef = new ArrayList<Xref>();
		for(String id : refInfo.keySet()){
			String[] temp = (refInfo.get(id)).split(":");
			String db = temp[0];
			String type = temp[1];
			if(db.toUpperCase().contains("OBO.GO")){
				db = "GENE_ONTOLOGY";
				if(id.contains(":")) {
					id = id.split(":")[1];
				}
			}
			String refId = db + "_" + id;
			Xref xref = (Xref) bioModel.getPathwayModel().findBioPaxObject(refId);
			if(xref == null){// create new xref
			
				if(type.toLowerCase().contains("described")){
					xref = new PublicationXref();
				}else if(type.toLowerCase().contains("homolog") || db.toUpperCase().contains("TAXONOMY")){
					xref = new RelationshipXref();
				}else{
					xref = new UnificationXref();
				}
				xref.setId(id);
				xref.setDb(db.toUpperCase());
				xref.setID(refId);

			}
			xRef.add(xref);
			
		}
		return xRef;
	}
	
	public static ArrayList<String> getNameRef(ArrayList<Xref> xRef, String entryName){
		CachedDataBaseReferenceReader dbReader = CachedDataBaseReferenceReader.getCachedReader();
		ArrayList<String> names = new ArrayList<String>();
		for(Xref xref : xRef){
			try {
//				System.out.println(xref.getDb() + "***" + xref.getId());
				if(xref.getDb().toLowerCase().equals("uniprot")){
//					String name = dbReader.getMoleculeDataBaseReference(xref.getId());
//					if(name != null)names.add(name);
//					if(xref.getId() != null && xref.getDb().toLowerCase() != null) {
//						names.add(xref.getDb().toLowerCase()+ ":" + xref.getId());
//					}
//					System.out.println(xref.getId() + ">>>>>>>"+ name);
				}else if(xref.getDb().toLowerCase().equals("interpro")){
//					String name = dbReader.getMoleculeDataBaseReference("interpro", xref.getId());
//					if(name != null)names.add(name);
//					if(xref.getId() != null && xref.getDb().toLowerCase() != null) {
//						names.add(xref.getDb().toLowerCase()+ ":" + xref.getId());
//					}
//					System.out.println(xref.getId() + ">>>>>>>"+ name);
				}else if(xref.getDb().toLowerCase().equals("obo.chebi")){
					String id = xref.getId().substring(6);
					String name = dbReader.getChEBIName(id);
					if(name != null)names.add(name);
//					System.out.println(xref.getId() + ">>>>>>>"+ name);
				}else if(xref.getDb().toLowerCase().equals("gene_ontology")){
//					String name = dbReader.getGOTerm(xref.getId());
//					if(name != null) {
//						names.add(name);
//					} else 
//					if(xref.getId() != null && xref.getDb().toLowerCase() != null) {
//						names.add(xref.getDb().toLowerCase()+ ":" + xref.getId());
//					}
//					System.out.println(xref.getId() + ">>>>>>>"+ name);
				}
//				else if(xref.getDb().toLowerCase().equals("ec-code")){
//					WSDBFetchServerServiceLocator providerLocator = new WSDBFetchServerServiceLocator();
//					WSDBFetchServer server = providerLocator.getWSDbfetch();
//					String[] supported = server.getSupportedDBs();
//					for (int i = 0; i < supported.length; i++) {
//						System.out.println(supported[i]);
//					}
//					String fetchResultStr = server.fetchBatch("embl", xref.getId(),null,null);
//					System.out.println(xref.getId() + ">>>>>>> lookup"+fetchResultStr);
//				}
				else{
					//System.out.println(xref.getDb());
				}
			} catch (DbfConnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DbfNoEntryFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DbfParamsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DbfException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InputException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(names.size() == 0 && entryName != null) names.add(entryName);
		return names;
	}
	
	private ArrayList<String> getComponentInfo(BioModel bioModel, Identifiable identifiable){
		ArrayList<String> info = new ArrayList<String>();
		VCMetaData vcMetaData = bioModel.getVCMetaData();
		VCID vcid = vcMetaData.getIdentifiableProvider().getVCID(identifiable);
		String modelComponentType = vcid.getClassName();
		String modelComponentName = vcid.getLocalName();
		info.add(modelComponentName);
		info.add(modelComponentType);
		return info;
	}
	
	private HashMap<String, String> getRefInfo(BioModel bioModel, Identifiable identifiable){
		HashMap<String, String> info = new HashMap<String, String>(); 
		VCMetaData vcMetaData = bioModel.getVCMetaData();
		MiriamManager miriamManager = vcMetaData.getMiriamManager();
		TreeMap<Identifiable, Map<MiriamRefGroup, MIRIAMQualifier>> miriamDescrHeir = miriamManager.getMiriamTreeMap();
		Map<MiriamRefGroup, MIRIAMQualifier> refGroupMap = miriamDescrHeir.get(identifiable);
		if (refGroupMap!=null){
			for (MiriamRefGroup refGroup : refGroupMap.keySet()){
				MIRIAMQualifier qualifier = refGroupMap.get(refGroup);
				String[] quaTemp = qualifier.toString().split("/");
				String bioQualifier = quaTemp[quaTemp.length-1];
				bioQualifier = bioQualifier.substring(0, bioQualifier.length()-1);
				for (MiriamResource miriamResource : refGroup.getMiriamRefs()){
					String refSource = miriamResource.getMiriamURN();
					String[] temp = refSource.split(":");
					String sourceInfo = temp[2] + ":" + bioQualifier;
					String refId = miriamResource.getIdentifier();
					info.put(refId, sourceInfo);
//					System.out.println(refId + "*********" + sourceInfo);
				}
			}
		}
		return info;
	}
}
