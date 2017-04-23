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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.VCellThreadChecker;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.AbstractMathMapping.MathMappingParameter;
import cbit.vcell.mapping.AbstractMathMapping.ObservableConcentrationParameter;
import cbit.vcell.mapping.AbstractMathMapping.ObservableCountParameter;
import cbit.vcell.mapping.AbstractMathMapping.SpeciesConcentrationParameter;
import cbit.vcell.mapping.AbstractMathMapping.SpeciesCountParameter;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.NetworkTransformer.GeneratedSpeciesSymbolTableEntry;
import cbit.vcell.mapping.SimContextTransformer.ModelEntityMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (9/19/2005 1:30:44 PM)
 * @author: Frank Morgan
 */
public class SimulationWorkspaceModelInfo implements SimulationModelInfo {

	private SimulationOwner simulationOwner = null;
	private String simulationName = null;
	private final DataSymbolMetadataResolver dataSymbolMetadataResolver = new InternalDataSymbolMetadataResolver();

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
//public String[] getFilterNames(){
//	if(simulationOwner instanceof SimulationContext){
//		ArrayList<String> filterNames = new ArrayList<String>();
//		for(FilterType filterType:FilterType.values()){
//			filterNames.add(filterType.toString());
//		}
//		return filterNames.toArray(new String[0]);		
//	}
//	return null;
//}
//public boolean hasFilter(String filterName){
//	String[] filterNames = getFilterNames();
//	for (int i = 0; filterNames != null && i < filterNames.length; i++) {
//		if(filterNames[i].equals(filterName)){
//			return true;
//		}
//	}
//	return false;
//}
//public static enum FilterType {Species,Flux};
//public ArrayList<DataIdentifier> filter(DataIdentifier[] filterTheseDataIdentifiers,FilterType filterType) throws Exception{
//	if(simulationOwner instanceof SimulationContext){
//		MathMapping mathMapping = ((SimulationContext)simulationOwner).getMostRecentlyCreatedMathMapping();
//		if (mathMapping==null){
//			mathMapping = ((SimulationContext)simulationOwner).createNewMathMapping();
//		}
//		MathDescription mathDescription = mathMapping.getMathDescription();
//		MathSymbolMapping mathSymbolMapping = mathMapping.getMathSymbolMapping();
//		ArrayList<DataIdentifier> acceptedDataIdentifiers = new ArrayList<DataIdentifier>();
//		for (int i = 0; i < filterTheseDataIdentifiers.length; i++) {
//			Variable variable = mathDescription.getVariable(filterTheseDataIdentifiers[i].getName());
//			SymbolTableEntry[] symbolTableEntries = mathSymbolMapping.getBiologicalSymbol(variable);
//			System.out.println("-----DI="+filterTheseDataIdentifiers[i].getName()+" var="+variable);
//			if(symbolTableEntries != null){
//				for (int j = 0; j < symbolTableEntries.length; j++) {
//					System.out.println("        "+symbolTableEntries[j]);
//					if(filterType.equals(FilterType.Species) && symbolTableEntries[j] instanceof SpeciesContext){
//						acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
//						break;
//					}else if(filterType.equals(FilterType.Flux)  && symbolTableEntries[j] instanceof KineticsParameter){
//						KineticsParameter kineticsParameter = (KineticsParameter)symbolTableEntries[j];
//						if(kineticsParameter.getRole() == Kinetics.ROLE_ReactionRate){
//							acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
//							break;
//						}
//					}
//				}
//			}			
//		}
//		if(acceptedDataIdentifiers.size() > 0){
//			return acceptedDataIdentifiers;
//		}
//		return null;
//	}else{
//		return null;
////		MathDescription mathDescription = simulationOwner.getMathDescription();
////		Variable variable = mathDescription.getVariable(dataName);
////		System.out.println("-----"+variable);
//	}
//}

private class InternalDataSymbolMetadataResolver implements DataSymbolMetadataResolver {

	private HashMap<String, DataSymbolMetadata> savedMetadataMap = null;
	
	private InternalDataSymbolMetadataResolver(){
	}
	
	@Override
	public DataSymbolMetadata getDataSymbolMetadata(SymbolTableEntry ste) {
		//
		// if called before the map is populated, it will return null;
		//
		return getDataSymbolMetadata(ste.getName());
	}

	@Override
	public DataSymbolMetadata getDataSymbolMetadata(String symbolName) {
		//
		// if called before the map is populated, it will return null;
		//
		if (savedMetadataMap!=null){
			DataSymbolMetadata dataSymbolMetadata = savedMetadataMap.get(symbolName);
//			System.out.println("InternalDataSymbolMetadataResolver.getDataSymbolMetadata("+symbolName+") has map, returns "+dataSymbolMetadata);
			return dataSymbolMetadata;
		}else{
//			System.out.println("InternalDataSymbolMetadataResolver.getDataSymbolMetadata("+symbolName+") has no map, return null");
			return null;
		}
	}
	
	@Override
	public ModelCategoryType[] getUniqueFilterCategories() {
		//
		// if called before the map is populated, it will indicate an empty list of FilterCategoryTypes (not yet processed).
		//
		HashSet<ModelCategoryType> filters = new HashSet<ModelCategoryType>();
		if (savedMetadataMap != null){
			for (DataSymbolMetadata dsm : savedMetadataMap.values()){
				if (dsm.filterCategory != null){
					filters.add(dsm.filterCategory);
				}
			}
		}	
		return filters.toArray(new BioModelCategoryType[0]);
	}
	
	private SymbolTableEntry[] getSortedBiologicalSymbols(SymbolTableEntry[] bioSymbols){
		if (bioSymbols.length==0){
			return null;
		}
		if (bioSymbols.length==1){
			return bioSymbols;
		}
		ArrayList<SymbolTableEntry> list = new ArrayList<SymbolTableEntry>();
		list.addAll(Arrays.asList(bioSymbols));
		list.sort(new Comparator<SymbolTableEntry>() {

			@Override
			public int compare(SymbolTableEntry o1, SymbolTableEntry o2) {
				int typeRank1 = rankType(o1);
				int typeRank2 = rankType(o2);
				if (typeRank1 != typeRank2){
					return Integer.compare(typeRank1, typeRank2);
				}
				return o1.getName().compareTo(o2.getName());
			}
		});
		return list.toArray(new SymbolTableEntry[0]);
	}
	
	private int rankType(SymbolTableEntry ste){
		if (ste instanceof GeneratedSpeciesSymbolTableEntry){
			return 5;
		}else if (ste instanceof MathMappingParameter){
			return 0;
		}else{
			return 10;
		}
	}
	
	@Override
	public void populateDataSymbolMetadata() {
		VCellThreadChecker.checkCpuIntensiveInvocation();	// must be explicitly called from a non-swing thread

		if (savedMetadataMap != null){
			if(simulationOwner instanceof SimulationContext){
				addOutputFunctionsToMetaData((SimulationContext)simulationOwner, savedMetadataMap);
			}
			return;
		}
		HashMap<String, DataSymbolMetadata> metadataMap = new HashMap<String,DataSymbolMetadata>();
		
		if(simulationOwner instanceof SimulationContext){
ArrayList<String> mappings = new ArrayList<String>();
			SimulationContext simulationContext = (SimulationContext)simulationOwner;
			MathMapping mathMapping = simulationContext.getMostRecentlyCreatedMathMapping();
			if (mathMapping==null){
				mathMapping = ((SimulationContext)simulationOwner).createNewMathMapping();
			}
			try {
				MathDescription mathDescription = mathMapping.getMathDescription();
				MathSymbolMapping mathSymbolMapping = mathMapping.getMathSymbolMapping();
				Enumeration<Variable> varEnum = mathDescription.getVariables();
				addOutputFunctionsToMetaData(simulationContext, metadataMap);
				boolean isSymbolsNotFound = false;
				while (varEnum.hasMoreElements()){
					String tooltipString = null;
					Variable var = varEnum.nextElement();
String mathInfo = var.getClass().getSimpleName()+"("+var.getName()+")";
					SymbolTableEntry[] bioSymbols1 = mathSymbolMapping.getBiologicalSymbol(var);
					if (bioSymbols1 != null && bioSymbols1.length>0){
						
						SymbolTableEntry[] sortedBioSymbols = getSortedBiologicalSymbols(bioSymbols1);
						SymbolTableEntry bestBioSymbol = sortedBioSymbols[0];
						if(bestBioSymbol instanceof KineticsProxyParameter) {
							bestBioSymbol = ((KineticsProxyParameter) bestBioSymbol).getTarget();
						}
						BioModelCategoryType filterCategory = BioModelCategoryType.Other;
						
						if (bestBioSymbol instanceof SpeciesContext || bestBioSymbol instanceof SpeciesConcentrationParameter || bestBioSymbol instanceof SpeciesCountParameter || bestBioSymbol instanceof GeneratedSpeciesSymbolTableEntry){
							filterCategory = BioModelCategoryType.Species;
							SpeciesContext transformedModelSpeciesContext = null;
							if (bestBioSymbol instanceof SpeciesContext){
								transformedModelSpeciesContext = (SpeciesContext) bestBioSymbol;
							}else if (bestBioSymbol instanceof SpeciesConcentrationParameter){
								transformedModelSpeciesContext = ((SpeciesConcentrationParameter) bestBioSymbol).getSpeciesContext();
							}else if (bestBioSymbol instanceof SpeciesCountParameter){
								transformedModelSpeciesContext = ((SpeciesCountParameter) bestBioSymbol).getSpeciesContext();
							}else if (bestBioSymbol instanceof GeneratedSpeciesSymbolTableEntry){
								transformedModelSpeciesContext = ((GeneratedSpeciesSymbolTableEntry) bestBioSymbol).getGeneratedSpeciesContext();
								filterCategory = BioModelCategoryType.GeneratedSpecies;
							}
							
							//
							// for SpeciesConcentrationParameters corresponding to Generated species, add GeneratedSpeciesSymbolTableEntry
							//
							if (mathMapping.getTransformation() != null){
								for (ModelEntityMapping mem : mathMapping.getTransformation().modelEntityMappings){
									if (mem.newModelObj == transformedModelSpeciesContext){
										if (mem.origModelObj instanceof GeneratedSpeciesSymbolTableEntry){
											filterCategory = BioModelCategoryType.GeneratedSpecies;
										}
									}
								}
							}
							if(transformedModelSpeciesContext.hasSpeciesPattern()) {
								SpeciesPattern sp = transformedModelSpeciesContext.getSpeciesPattern();
								tooltipString = sp.toString();
							}
						}else if (bestBioSymbol instanceof KineticsParameter){
							KineticsParameter kineticsParameter = (KineticsParameter)bestBioSymbol;
							if(kineticsParameter.getRole() == Kinetics.ROLE_ReactionRate){
								filterCategory = BioModelCategoryType.Reactions;
							}
						}else if (bestBioSymbol instanceof RbmObservable || bestBioSymbol instanceof ObservableConcentrationParameter || bestBioSymbol instanceof ObservableCountParameter){
							filterCategory = BioModelCategoryType.Observables;
							RbmObservable observable = null;
							if (bestBioSymbol instanceof RbmObservable){
								observable = (RbmObservable) bestBioSymbol;
							}else if (bestBioSymbol instanceof ObservableConcentrationParameter){
								observable = ((ObservableConcentrationParameter) bestBioSymbol).getObservable();
							}else if (bestBioSymbol instanceof ObservableCountParameter){
								observable = ((ObservableCountParameter) bestBioSymbol).getObservable();
							}
							if(observable != null) {
								for (SpeciesPattern sp : observable.getSpeciesPatternList()){
									if (tooltipString == null){
										tooltipString = sp.toString();
									}else{
										tooltipString += " or "+sp.toString();
									}
								}
							}
						}
						VCUnitDefinition unit = bestBioSymbol.getUnitDefinition();
if (tooltipString==null){
tooltipString = "<html>";
}else{
tooltipString = "<html>"+tooltipString+"<br/>";
}
tooltipString += mathInfo + "<br/>";
for (SymbolTableEntry bioSymbol : sortedBioSymbols){
VCUnitDefinition unitDefinition = bioSymbol.getUnitDefinition();
String unitSymbol = (unitDefinition!=null)?(unitDefinition.getSymbol()):("<null unit>");
String bioInfo = bioSymbol.getClass().getSimpleName()+"("+bioSymbol.getName()+",["+unitSymbol+"])  category="+filterCategory+", unit=["+unitSymbol+"]";
tooltipString += " ==> "+bioInfo + "<br/>";
mappings.add(var.getName()+"   "+mathInfo+"    ==>    "+bioInfo+", tooltipString="+tooltipString);
}
tooltipString+="</html>";
						metadataMap.put(var.getName(),new DataSymbolMetadata(filterCategory, unit, tooltipString));
					}else{
						isSymbolsNotFound = true;
String bioInfo = "Biological Symbol Not Found";
tooltipString = "<html>"+mathInfo+"<br/> ==> "+bioInfo+"</html>";
mappings.add(var.getName()+"   "+mathInfo+"    ==>    "+bioInfo+", tooltipString="+tooltipString);
//						System.out.println("couldn't find biological symbol for var "+var.getName());
					}
				}
				if(isSymbolsNotFound) {
					System.out.println("couldn't find biological symbol(s) for one or more variables.");
				}
				//
				// add reserved symbols for x,y,z,t
				//
				ModelUnitSystem unitSystem = simulationContext.getModel().getUnitSystem();
				metadataMap.put(ReservedVariable.TIME.getName(),new DataSymbolMetadata(null,unitSystem.getTimeUnit(), null));
//				metadataMap.put(ReservedVariable.X.getName(),new DataSymbolMetadata(FilterCategoryType.ReservedXYZT,unitSystem.getLengthUnit()));
//				metadataMap.put(ReservedVariable.Y.getName(),new DataSymbolMetadata(FilterCategoryType.ReservedXYZT,unitSystem.getLengthUnit()));
//				metadataMap.put(ReservedVariable.Z.getName(),new DataSymbolMetadata(FilterCategoryType.ReservedXYZT,unitSystem.getLengthUnit()));
			} catch (MappingException | MathException | MatrixException	| ExpressionException | ModelException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to determine metadata for data symbols: "+e.getMessage(), e);
			}
			savedMetadataMap = metadataMap;
mappings.sort(null);
StringBuffer buffer = new StringBuffer();
buffer.append("SimulationWorkspaceModelInfo.populateDataSymbolMetadata(): \n");
for (String line : mappings){
buffer.append(line+"\n");
}
System.out.println(buffer.toString());
		}else if (simulationOwner instanceof MathModel){
			savedMetadataMap = metadataMap;
		}else{
			throw new RuntimeException("Unexpected SimulationOwner="+simulationOwner.getClass().getName());
		}
	}

}

private static enum BioModelCategoryType implements ModelCategoryType {
	Species(true,true),
	Reactions,
	UserFunctions,
	Observables(true,true),
	GeneratedSpecies,
	ReservedXYZT(true,false),
	Other;
	/**
	 * should this be selected initially on GUI?
	 */
	private final boolean initialSelect;
	/**
	 * should this be enabled so user can select / deselect?
	 */
	private final boolean enabled;
	
	/**
	 * initialSelect is false, enabled is true
	 */
	private BioModelCategoryType( ) {
		initialSelect = false;
		enabled = true;
	}
	/**
	 * @param initialSelect display checked initially?
	 * @param enabled allow user to edit? 
	 */
	private BioModelCategoryType(boolean initialSelect, boolean enabled) {
		this.initialSelect = initialSelect;
		this.enabled = enabled;
	}
	
	@Override
	public String getName(){
		return name();
	}
	
	@Override
	public boolean isInitialSelect() {
		return initialSelect;
	}
	@Override
	public boolean isEnabled() {
		return enabled;
	}
};

@Override
public DataSymbolMetadataResolver getDataSymbolMetadataResolver() {
	return dataSymbolMetadataResolver;
}

private static void addOutputFunctionsToMetaData(SimulationContext simulationContext,HashMap<String, DataSymbolMetadata> metaDataMap){
	if(metaDataMap != null &&
		simulationContext.getOutputFunctionContext() != null &&
		simulationContext.getOutputFunctionContext().getOutputFunctionsList() != null){
			ArrayList<AnnotatedFunction> annotfuncs = simulationContext.getOutputFunctionContext().getOutputFunctionsList();
			for(AnnotatedFunction annotfunc:annotfuncs){
				if(simulationContext.getOutputFunctionContext().getEntry(annotfunc.getName()) != null){
				metaDataMap.put(annotfunc.getName(),
					new DataSymbolMetadata(BioModelCategoryType.UserFunctions,
						simulationContext.getOutputFunctionContext().getEntry(annotfunc.getName()).getUnitDefinition(), null));
				}
			}
		}
}

}
