package cbit.vcell.client.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.solver.SimulationModelInfo.ModelCategoryType;

public class  DefaultDataIdentifierFilter implements DataIdentifierFilter{
	private boolean bPostProcessingMode = false;
	public static final String ALL = "All Variables";
	private String VOLUME_FILTER_SET = "Volume Variables";
	private String MEMBRANE_FILTER_SET = "Membrane Variables";
	private String USER_DEFINED_FILTER_SET = "User Functions";
	private String REGION_SIZE_FILTER_SET = "Region Sizes";
	private String[] BUILTIN_FILTER_SET_NAMES = new String[] {
			ALL,
			VOLUME_FILTER_SET,
			MEMBRANE_FILTER_SET,
			USER_DEFINED_FILTER_SET,
			REGION_SIZE_FILTER_SET
	};
	private DataSymbolMetadataResolver dataSymbolMetadataResolver;
	public DefaultDataIdentifierFilter(){
		this(null);
	}
	public DefaultDataIdentifierFilter(DataSymbolMetadataResolver dataSymbolMetadataResolver){
	this.dataSymbolMetadataResolver = dataSymbolMetadataResolver;
//		this.simulationWorkspaceModelInfo = simulationWorkspaceModelInfo;
//		FILTER_SET_NAMES = new String[] {ALL,VOLUME_FILTER_SET,MEMBRANE_FILTER_SET,USER_DEFINED_FILTER_SET, REGION_SIZE_FILTER_SET};
//		if(simulationWorkspaceModelInfo != null && simulationWorkspaceModelInfo.getFilterNames() != null){
//			String[] temp = new String[FILTER_SET_NAMES.length+simulationWorkspaceModelInfo.getFilterNames().length];
//			System.arraycopy(FILTER_SET_NAMES, 0, temp, 0, FILTER_SET_NAMES.length);
//			System.arraycopy(simulationWorkspaceModelInfo.getFilterNames(),0, temp, FILTER_SET_NAMES.length, simulationWorkspaceModelInfo.getFilterNames().length);
//			FILTER_SET_NAMES = temp;
		}

	public ArrayList<DataIdentifier> accept(String filterSetName, List<AnnotatedFunction> functionList, DataIdentifier[] filterTheseDataIdentifiers) {
		//
		// if category matches something from DataSymbolMetadataResolver, then return those
		//
		if(dataSymbolMetadataResolver != null){
			ArrayList<String> allFilterNames = new ArrayList<String>();
			if (this.dataSymbolMetadataResolver!=null){
				ModelCategoryType[] uniqueCategories = dataSymbolMetadataResolver.getUniqueFilterCategories();
				for (ModelCategoryType catType : uniqueCategories){
					allFilterNames.add(catType.getName());
				}
			}
			if (allFilterNames.contains(filterSetName)){
				ArrayList<DataIdentifier> acceptedDataIdentifiers = new ArrayList<DataIdentifier>();
				for (DataIdentifier dataID : filterTheseDataIdentifiers){
					DataSymbolMetadata metadata = dataSymbolMetadataResolver.getDataSymbolMetadata(dataID.getName());
					if (metadata!=null && metadata.filterCategory.getName().equals(filterSetName)){
						acceptedDataIdentifiers.add(dataID);
	}
				}					
				return acceptedDataIdentifiers;
			}
		}			
		ArrayList<DataIdentifier> acceptedDataIdentifiers = new ArrayList<DataIdentifier>();
		for (int i = 0; i < filterTheseDataIdentifiers.length; i++) {
			if (bPostProcessingMode && filterTheseDataIdentifiers[i].getVariableType().equals(VariableType.POSTPROCESSING)){
				acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
				continue;
			}
			if (bPostProcessingMode && !filterTheseDataIdentifiers[i].getVariableType().equals(VariableType.POSTPROCESSING)){
				continue;
			}
			if (!bPostProcessingMode && filterTheseDataIdentifiers[i].getVariableType().equals(VariableType.POSTPROCESSING)){
				continue;
			}
			String varName = filterTheseDataIdentifiers[i].getName();
			boolean bSizeVar = varName.startsWith(MathFunctionDefinitions.Function_regionVolume_current.getFunctionName()) 
					|| varName.startsWith(MathFunctionDefinitions.Function_regionArea_current.getFunctionName())
							|| varName.startsWith(DiffEquMathMapping.PARAMETER_SIZE_FUNCTION_PREFIX);
			
			if (filterSetName.equals(REGION_SIZE_FILTER_SET) && bSizeVar) {
				acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
				continue;
			}
			if (!filterSetName.equals(REGION_SIZE_FILTER_SET) && bSizeVar) {
				continue;
			}
			
			if(filterSetName.equals(ALL)){
				acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
			}else if(filterSetName.equals(VOLUME_FILTER_SET) && filterTheseDataIdentifiers[i].getVariableType().getVariableDomain().equals(VariableDomain.VARIABLEDOMAIN_VOLUME)){
				acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
			}else if(filterSetName.equals(MEMBRANE_FILTER_SET) && filterTheseDataIdentifiers[i].getVariableType().getVariableDomain().equals(VariableDomain.VARIABLEDOMAIN_MEMBRANE)){
				acceptedDataIdentifiers.add(filterTheseDataIdentifiers[i]);
			}else if(filterSetName.equals(USER_DEFINED_FILTER_SET)){
				if(functionList != null){
					for (AnnotatedFunction f : functionList) {
						if(!f.isPredefined() && f.getName().equals(varName)){
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
	}
	public String getDefaultFilterName() {
		return ALL;
	}
	public String[] getFilterSetNames() {
		ArrayList<String> allFilterNames = new ArrayList<String>();
		allFilterNames.addAll(Arrays.asList(BUILTIN_FILTER_SET_NAMES));
		if (this.dataSymbolMetadataResolver!=null){
			ModelCategoryType[] uniqueCategories = dataSymbolMetadataResolver.getUniqueFilterCategories();
			for (ModelCategoryType catType : uniqueCategories){
				allFilterNames.add(catType.getName());
			}
		}
		return allFilterNames.toArray(new String[0]);
	}
	public boolean isAcceptAll(String filterSetName){
		return filterSetName.equals(ALL);
	}
	public boolean isPostProcessingMode() {
		return bPostProcessingMode;
	}
	public void setPostProcessingMode(boolean bPostProcessingMode) {
		this.bPostProcessingMode = bPostProcessingMode;
	}
	public boolean accept(String filterSetName,List<AnnotatedFunction> myFunctionList,DataIdentifier dataidentifier) {
		return accept(filterSetName,myFunctionList, new DataIdentifier[] {dataidentifier}) != null;
	}
};
