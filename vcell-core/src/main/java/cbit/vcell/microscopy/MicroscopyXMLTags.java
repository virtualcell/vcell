/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import cbit.vcell.xml.XMLTags;

/**
 * This class contains all the XML tags.
 * @author schaff
 * @version $Revision: 1.0 $
 */
public class MicroscopyXMLTags {

	public static final String FRAPStudyTag = "FrapStudy";
	public static final String UShortImageTag = "UShortImage";
	public static final String FRAPDataTag = "FrapData";
	public static final String TimeStampListTag = "TimeStampList";
	public static final String ROITag = "ROI";
	public static final String ROITypeAttrTag = "ROIType";
	
	public static final String ImageDatasetTag = "ImageDataset";
	public static final String OwnerNameAttrTag = XMLTags.OwnerNameAttrTag;
	public static final String ImageDatasetExternalDataInfoTag = "ImageDatasetExternalDataInfo";
	public static final String FilenameAttrTag = "Filename";
	public static final String ExternalDataIdentifierTag = "ExternalDataIdentifier";
	public static final String ROIExternalDataInfoTag = "ROIExternalDataInfo";
	public static final String OriginalImagePathAttrTag = "OriginalImagePath";
//	public static final String OriginalGlobalScaleInfoTag = "OriginalGlobalScaleInfo";
//    public final static String OriginalGlobalScaleInfoMinTag = "OriginalGlobalScaleInfoMin";
//    public final static String OriginalGlobalScaleInfoMaxTag = "OriginalGlobalScaleInfoMax";
//    public final static String OriginalGlobalScaleInfoScaleTag = "OriginalGlobalScaleInfoScale";
//    public final static String OriginalGlobalScaleInfoOffsetTag = "OriginalGlobalScaleInfoOffset";
	//frapstudy attribute
    public static final String StartingIndexForRecoveryTag = "StartingIndexForRecovery";
    public static final String BestModelIndexTag = "BestModelIndex";
    //frap selected ROIs
    public static final String SelectedROIsTag = "SelectedROIs";
    //frap models
    public static final String FrapModelsTag = "FrapModels";
    public static final String DiffustionWithOneComponentModelTag = "DiffustionWithOneComponentModel";
    public static final String DiffustionWithTwoComponentsModelTag = "DiffustionWithTwoComponentsModel";
    public static final String ReactionDominantOffRateModelTag = "ReactionDominantOffRateModel";
    public static final String DiffustionReactionModelTag = "DiffustionReactionModel";
    public static final String ModelParametersTag = "ModelParameters";
    public static final String ModelDataTag = "ModelData";
    public static final String ModelTimePointsLengthAttTag = "ModelTimePointsLength";
    public static final String ModelTimePointsTag = "ModelTimePoints";
    public static final String ModelIdentifierAttTag = "ModelIdentifier";
    
    //model parameters as attributes
    public static final String BleachWhileMonitoringTauAttrTag = "BleachWhileMonitoringTau";
	public static final String PrimaryRateAttrTag = "PrimaryDiffusionRate";
	public static final String PrimaryFractionAttTag = "PrimaryMobileFraction";
	public static final String SecondRateAttrTag = "SecondaryDiffusionRate";
	public static final String SecondFractionAttTag = "SecondaryMobileFraction";
	public static final String BindingSiteConcentrationAttTag = "BindingSiteConcentration";
	public static final String ReactionOffRateAttTag = "ReactionOffRate";
	public static final String ReactionOnRateAttTag = "ReactionOnRate";
    
	//reference simulation data info.
	public static final String ReferenceDataTag = "ReferenceData";
	public static final String DiffusionReactionDataTag = "DiffusionReactionData";
	
	//batch run 
	public static final String FrapBatchRunTag = "FrapBatchRun";
	public static final String FrapStudyListTag = "FrapStudyList";
	public static final String NumFrapStudyListAttrTag = "NumOfFrapStudies";
	public static final String FrapFileNameTag = "FrapFileName";
	public static final String BatchRunSelectedModelTypeTag = "BatchRunSelectedModelType";
	
	//profie data
	public static final String ListOfProfileData_OneDiffTag = "ListOfProfileData_oneDiffusingComponent";
	public static final String ListOfProfileData_TwoDiffTag = "ListOfProfileData_twoDiffusingComponents";
	public static final String ListOfProfileData_ReacOffRateTag = "ListOfProfileData_reactionOffRate";
	public static final String ProfileDataTag = "ProfileData";
	public static final String ProfieDataElementTag = "ProfileDataElement";
	public static final String profileDataElementParameterNameAttrTag = "ParamName";
	public static final String profileDataElementParameterValueAttrTag = "ParamVal";
	public static final String profileDataElementLikelihoodAttrTag = "Likelihood";
    
	/**
	 * XMLTags default constructor.
	 */
	public MicroscopyXMLTags() {
		super();
	}
}
