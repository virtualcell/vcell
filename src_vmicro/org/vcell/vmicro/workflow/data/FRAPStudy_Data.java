package org.vcell.vmicro.workflow.data;

import java.beans.PropertyChangeSupport;

import org.vcell.optimization.ProfileData;
import org.vcell.vmicro.workflow.scratch.ExternalDataInfo;
import org.vcell.vmicro.workflow.scratch.FRAPData;
import org.vcell.vmicro.workflow.scratch.FRAPModel;
import org.vcell.vmicro.workflow.scratch.FRAPOptData;
import org.vcell.vmicro.workflow.scratch.FRAPOptFunctions;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.opt.SimpleReferenceData;

public class FRAPStudy_Data {
	public String name;
	public String description;
	public String xmlFilename;
	public String originalImageFilePath;
	public FRAPData frapData;
	public Integer startingIndexForRecovery;
	public BioModel bioModel;
	public ExternalDataInfo frapDataExternalDataInfo;
	public ExternalDataInfo roiExternalDataInfo;
	public SimpleReferenceData storedRefData;
	public ProfileData[] profileData_oneDiffComponent;
	public ProfileData[] profileData_twoDiffComponents;
	public ProfileData[] profileData_reactionOffRate;
	public FRAPModel[] models;
	public Integer bestModelIndex;
	public boolean[] selectedROIsForErrCalculation;
	public PropertyChangeSupport propertyChangeSupport;
	public String movieURLString;
	public String movieFileString;
	public FRAPOptData frapOptData;
	public FRAPOptFunctions frapOptFunc;
	public double[][] analysisMSESummaryData;
	public double[][] dimensionReducedExpData;
	public double[] reducedExpTimePoints;
	public boolean bSaveNeeded;

	public FRAPStudy_Data(String name, String description, String xmlFilename,
			String originalImageFilePath, FRAPData frapData,
			Integer startingIndexForRecovery, BioModel bioModel,
			ExternalDataInfo frapDataExternalDataInfo,
			ExternalDataInfo roiExternalDataInfo,
			SimpleReferenceData storedRefData,
			ProfileData[] profileData_oneDiffComponent,
			ProfileData[] profileData_twoDiffComponents,
			ProfileData[] profileData_reactionOffRate, FRAPModel[] models,
			Integer bestModelIndex, boolean[] selectedROIsForErrCalculation,
			PropertyChangeSupport propertyChangeSupport, String movieURLString,
			String movieFileString, FRAPOptData frapOptData,
			FRAPOptFunctions frapOptFunc, double[][] analysisMSESummaryData,
			double[][] dimensionReducedExpData, double[] reducedExpTimePoints,
			boolean bSaveNeeded) {
		this.name = name;
		this.description = description;
		this.xmlFilename = xmlFilename;
		this.originalImageFilePath = originalImageFilePath;
		this.frapData = frapData;
		this.startingIndexForRecovery = startingIndexForRecovery;
		this.bioModel = bioModel;
		this.frapDataExternalDataInfo = frapDataExternalDataInfo;
		this.roiExternalDataInfo = roiExternalDataInfo;
		this.storedRefData = storedRefData;
		this.profileData_oneDiffComponent = profileData_oneDiffComponent;
		this.profileData_twoDiffComponents = profileData_twoDiffComponents;
		this.profileData_reactionOffRate = profileData_reactionOffRate;
		this.models = models;
		this.bestModelIndex = bestModelIndex;
		this.selectedROIsForErrCalculation = selectedROIsForErrCalculation;
		this.propertyChangeSupport = propertyChangeSupport;
		this.movieURLString = movieURLString;
		this.movieFileString = movieFileString;
		this.frapOptData = frapOptData;
		this.frapOptFunc = frapOptFunc;
		this.analysisMSESummaryData = analysisMSESummaryData;
		this.dimensionReducedExpData = dimensionReducedExpData;
		this.reducedExpTimePoints = reducedExpTimePoints;
		this.bSaveNeeded = bSaveNeeded;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setXmlFilename(String xmlFilename) {
		this.xmlFilename = xmlFilename;
	}

	public void setOriginalImageFilePath(String originalImageFilePath) {
		this.originalImageFilePath = originalImageFilePath;
	}

	public void setFrapData(FRAPData frapData) {
		this.frapData = frapData;
	}

	public void setStartingIndexForRecovery(Integer startingIndexForRecovery) {
		this.startingIndexForRecovery = startingIndexForRecovery;
	}

	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
	}

	public void setFrapDataExternalDataInfo(
			ExternalDataInfo frapDataExternalDataInfo) {
		this.frapDataExternalDataInfo = frapDataExternalDataInfo;
	}

	public void setRoiExternalDataInfo(ExternalDataInfo roiExternalDataInfo) {
		this.roiExternalDataInfo = roiExternalDataInfo;
	}

	public void setStoredRefData(SimpleReferenceData storedRefData) {
		this.storedRefData = storedRefData;
	}

	public void setProfileData_oneDiffComponent(
			ProfileData[] profileData_oneDiffComponent) {
		this.profileData_oneDiffComponent = profileData_oneDiffComponent;
	}

	public void setProfileData_twoDiffComponents(
			ProfileData[] profileData_twoDiffComponents) {
		this.profileData_twoDiffComponents = profileData_twoDiffComponents;
	}

	public void setProfileData_reactionOffRate(
			ProfileData[] profileData_reactionOffRate) {
		this.profileData_reactionOffRate = profileData_reactionOffRate;
	}

	public void setModels(FRAPModel[] models) {
		this.models = models;
	}

	public void setBestModelIndex(Integer bestModelIndex) {
		this.bestModelIndex = bestModelIndex;
	}

	public void setSelectedROIsForErrCalculation(
			boolean[] selectedROIsForErrCalculation) {
		this.selectedROIsForErrCalculation = selectedROIsForErrCalculation;
	}

	public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

	public void setMovieURLString(String movieURLString) {
		this.movieURLString = movieURLString;
	}

	public void setMovieFileString(String movieFileString) {
		this.movieFileString = movieFileString;
	}

	public void setFrapOptData(FRAPOptData frapOptData) {
		this.frapOptData = frapOptData;
	}

	public void setFrapOptFunc(FRAPOptFunctions frapOptFunc) {
		this.frapOptFunc = frapOptFunc;
	}

	public void setAnalysisMSESummaryData(double[][] analysisMSESummaryData) {
		this.analysisMSESummaryData = analysisMSESummaryData;
	}

	public void setDimensionReducedExpData(double[][] dimensionReducedExpData) {
		this.dimensionReducedExpData = dimensionReducedExpData;
	}

	public void setReducedExpTimePoints(double[] reducedExpTimePoints) {
		this.reducedExpTimePoints = reducedExpTimePoints;
	}

	public void setbSaveNeeded(boolean bSaveNeeded) {
		this.bSaveNeeded = bSaveNeeded;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getXmlFilename() {
		return xmlFilename;
	}

	public String getOriginalImageFilePath() {
		return originalImageFilePath;
	}

	public FRAPData getFrapData() {
		return frapData;
	}

	public Integer getStartingIndexForRecovery() {
		return startingIndexForRecovery;
	}

	public BioModel getBioModel() {
		return bioModel;
	}

	public ExternalDataInfo getFrapDataExternalDataInfo() {
		return frapDataExternalDataInfo;
	}

	public ExternalDataInfo getRoiExternalDataInfo() {
		return roiExternalDataInfo;
	}

	public SimpleReferenceData getStoredRefData() {
		return storedRefData;
	}

	public ProfileData[] getProfileData_oneDiffComponent() {
		return profileData_oneDiffComponent;
	}

	public ProfileData[] getProfileData_twoDiffComponents() {
		return profileData_twoDiffComponents;
	}

	public ProfileData[] getProfileData_reactionOffRate() {
		return profileData_reactionOffRate;
	}

	public FRAPModel[] getModels() {
		return models;
	}

	public Integer getBestModelIndex() {
		return bestModelIndex;
	}

	public boolean[] getSelectedROIsForErrCalculation() {
		return selectedROIsForErrCalculation;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public String getMovieURLString() {
		return movieURLString;
	}

	public String getMovieFileString() {
		return movieFileString;
	}

	public FRAPOptData getFrapOptData() {
		return frapOptData;
	}

	public FRAPOptFunctions getFrapOptFunc() {
		return frapOptFunc;
	}

	public double[][] getAnalysisMSESummaryData() {
		return analysisMSESummaryData;
	}

	public double[][] getDimensionReducedExpData() {
		return dimensionReducedExpData;
	}

	public double[] getReducedExpTimePoints() {
		return reducedExpTimePoints;
	}

	public boolean isbSaveNeeded() {
		return bSaveNeeded;
	}


}