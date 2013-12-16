/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;

import java.util.ArrayList;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.solver.SimulationOwner;

/**
 * Insert the type's description here.
 * Creation date: (9/19/2005 1:30:44 PM)
 * @author: Frank Morgan
 */
public class SimulationWorkspaceModelInfo implements SimulationModelInfo {

	private SimulationOwner simulationOwner = null;
	private String simulationName = null;

/**
 * SimulationWorkspaceModelInfo constructor comment.
 */
public SimulationWorkspaceModelInfo(SimulationOwner simOwner,String argSimulationName) {
	super();
	simulationOwner = simOwner;
	simulationName = argSimulationName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 11:04:29 AM)
 * @return java.lang.String
 */
public java.lang.String getContextName() {
	String results = null;
	if(simulationOwner instanceof MathModel){
		MathModel mathModel = (MathModel)simulationOwner;
		results = mathModel.getName();
	}else if(simulationOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simulationOwner;
		results = simContext.getBioModel().getName()+"::"+simContext.getName();
	}
	
	return results;
}

/**
 * Insert the method's description here.
 * Creation date: (9/19/2005 1:30:44 PM)
 * @return java.lang.String
 * @param subVolumeIdIn int
 * @param subVolumeIdOut int
 */
public String getMembraneName(int subVolumeIdIn, int subVolumeIdOut, boolean bFromGeometry) {
	String results = null;
	if(simulationOwner instanceof MathModel){
		MathModel mathModel = (MathModel)simulationOwner;
		final GeometrySpec geometrySpec = mathModel.getMathDescription().getGeometry().getGeometrySpec();
		if(geometrySpec.getSubVolume(subVolumeIdIn) != null &&
			geometrySpec.getSubVolume(subVolumeIdOut) != null){
			SubVolume svIn = geometrySpec.getSubVolume(subVolumeIdIn);
			SubVolume svOut = geometrySpec.getSubVolume(subVolumeIdOut);
			SurfaceClass membrane = mathModel.getMathDescription().getGeometry().getGeometrySurfaceDescription().getSurfaceClass(svIn, svOut);
			results = membrane.getName();
		}
	}else if(simulationOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simulationOwner;
		SubVolume svIn = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeIdIn);
		SubVolume svOut = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeIdOut);
		if (bFromGeometry) {
			SurfaceClass membrane = simContext.getMathDescription().getGeometry().getGeometrySurfaceDescription().getSurfaceClass(svIn, svOut);
			results = membrane.getName();
		} else {
			if(svIn != null && svOut != null){
				GeometryClass[] geometryClasses = simContext.getGeometry().getGeometryClasses();
				for (int i = 0; i < geometryClasses.length; i++) {
					if (geometryClasses[i] instanceof SurfaceClass){
						SurfaceClass surface = (SurfaceClass)geometryClasses[i];
						if (surface.isAdjacentTo(svIn) && surface.isAdjacentTo(svOut)){
							StructureMapping[] structureMappings = simContext.getGeometryContext().getStructureMappings(surface);
							if (structureMappings!=null && structureMappings.length>0){
								results = surface.getName()+"(";
								for (int j = 0; j < structureMappings.length; j++) {
									results += structureMappings[j].getStructure().getName()+" ";
								}
								results += ")";
								return results;
							}
						}
					}
				}
			}
		}
	}
	
	return results;
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2005 11:18:44 AM)
 * @return java.lang.String
 */
public java.lang.String getSimulationName() {
	return simulationName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2005 1:30:44 PM)
 * @return java.lang.String
 * @param subVolumeID int
 */
public String getVolumeNamePhysiology(int subVolumeID) {
	String results = "";
	if(simulationOwner instanceof MathModel){
		MathModel mathModel = (MathModel)simulationOwner;
		if(mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID) != null){
			results = mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID).getName();
		}
	}else if(simulationOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simulationOwner;
		SubVolume sv = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeID);
		if(sv != null){
			Structure[] structures = simContext.getGeometryContext().getStructuresFromGeometryClass(sv);
			if (structures!=null && structures.length>0){
				for (Structure structure : structures){
					results += structure.getName()+" ";
				}
			}
		}
	}
	
	return results;
}
public String getVolumeNameGeometry(int subVolumeID) {
	String results = null;
	if(simulationOwner instanceof MathModel){
		MathModel mathModel = (MathModel)simulationOwner;
		if(mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID) != null){
			results = mathModel.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(subVolumeID).getName();
		}
	}else if(simulationOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simulationOwner;
		if(simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeID) != null){
			results = simContext.getGeometry().getGeometrySpec().getSubVolume(subVolumeID).getName();
		}
	}
	
	return results;
}
public String[] getFilterNames(){
	if(simulationOwner instanceof SimulationContext){
		ArrayList<String> filterNames = new ArrayList<String>();
		for(FilterType filterType:FilterType.values()){
			filterNames.add(filterType.toString());
		}
		return filterNames.toArray(new String[0]);		
	}
	return null;
}
public boolean hasFilter(String filterName){
	String[] filterNames = getFilterNames();
	for (int i = 0; filterNames != null && i < filterNames.length; i++) {
		if(filterNames[i].equals(filterName)){
			return true;
		}
	}
	return false;
}
public static enum FilterType {Species,Flux};
public ArrayList<DataIdentifier> filter(DataIdentifier[] filterTheseDataIdentifiers,FilterType filterType) throws Exception{
	if(simulationOwner instanceof SimulationContext){
		MathMapping mathMapping = ((SimulationContext)simulationOwner).createNewMathMapping();
		MathDescription mathDescription = mathMapping.getMathDescription();
		MathSymbolMapping mathSymbolMapping = mathMapping.getMathSymbolMapping();
		ArrayList<DataIdentifier> acceptedDataIdentifiers = new ArrayList<DataIdentifier>();
		for (int i = 0; i < filterTheseDataIdentifiers.length; i++) {
			Variable variable = mathDescription.getVariable(filterTheseDataIdentifiers[i].getName());
			SymbolTableEntry[] symbolTableEntries = mathSymbolMapping.getBiologicalSymbol(variable);
			System.out.println("-----DI="+filterTheseDataIdentifiers[i].getName()+" var="+variable);
			if(symbolTableEntries != null){
				for (int j = 0; j < symbolTableEntries.length; j++) {
					System.out.println("        "+symbolTableEntries[j]);
					if(filterType.equals(FilterType.Species) && symbolTableEntries[j] instanceof SpeciesContext){
						acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
						break;
					}else if(filterType.equals(FilterType.Flux)  && symbolTableEntries[j] instanceof KineticsParameter){
						KineticsParameter kineticsParameter = (KineticsParameter)symbolTableEntries[j];
						if(kineticsParameter.getRole() == Kinetics.ROLE_ReactionRate){
							acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
							break;
						}
					}
				}
			}			
		}
		if(acceptedDataIdentifiers.size() > 0){
			return acceptedDataIdentifiers;
		}
		return null;
	}else{
		return null;
//		MathDescription mathDescription = simulationOwner.getMathDescription();
//		Variable variable = mathDescription.getVariable(dataName);
//		System.out.println("-----"+variable);
	}
}
}
