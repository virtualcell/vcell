package cbit.vcell.microscopy;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
	//used for saving model parameters-------------------------------
	public static final String FRAPModelParametersTag = "FrapModelParameters";
	public static final String FRAPInitialParametersTag = "FrapInitialParameters";//for initial parameters
	public static final String RecoveryDiffusionRateAttrTag = "RecoveryDiffusionRate";
	public static final String MobileFractionAttrTag = "MobileFraction";
	public static final String BleachWhileMonitoringTauAttrTag = "BleachWhileMonitoringTau";
	public static final String StartingIndexForRecoveryAttrTag = "StartingIndexForRecovery";
	public static final String FRAPPureDiffusionParametersTag = "FrapPureDiffusionParameters";//for pure diffusion parameters
	public static final String PrimaryRateAttrTag = "PrimaryDiffusionRate";
	public static final String PrimaryFractionAttTag = "PrimaryMobileFraction";
	public static final String SecondRateAttrTag = "SecondaryDiffusionRate";
	public static final String SecondFractionAttTag = "SecondaryMobileFraction";
	//monitor bleash rate has to be store in pure diffusion parameter too, it is defined above
	public static final String isSecondDiffAppliedAttTag = "IsSecondDiffusionApplied";
	public static final String FRAPReactionDiffusionParametersTag = "FrapReactionDiffusionParameters";
	public static final String FreeDiffusionRateAttTag = "FreeDiffusionRate";
	public static final String FreeMobileFractionAttTag = "FreeMobileFraction";
	//monitor bleash rate has to be store in reaction diffusion parameter too , it is defined above
	public static final String ComplexDiffusionRateAttTag = "ComplexDiffusionRate";
	public static final String ComplexMobileFractionAttTag = "ComplexMobileFraction";
	public static final String BindingSiteConcentrationAttTag = "BindingSiteConcentration";
	public static final String ReactionOffRateAttTag = "ReactionOffRate";
	public static final String ReactionOnRateAttTag = "ReactionOnRate";
	
	public static final String RecoveryTauAttrTag = "RecoveryTau";
	public static final String BleachWidthAttrTag = "BleachWidth";
	public static final String BleachTypeAttrTag = "BleachType";
	//----------------------------------------------------
	public static final String ImageDatasetTag = "ImageDataset";
	public static final String OwnerNameAttrTag = "OwnerName";
	public static final String ImageDatasetExternalDataInfoTag = "ImageDatasetExternalDataInfo";
	public static final String FilenameAttrTag = "Filename";
	public static final String ExternalDataIdentifierTag = "ExternalDataIdentifier";
	public static final String ROIExternalDataInfoTag = "ROIExternalDataInfo";
	public static final String RefExternalDataInfoTag = "RefExternalDataInfo";
	public static final String OriginalImagePathAttrTag = "OriginalImagePath";
	
	public static final String FitExpressionAttrTag = "FitExpression";
	
    public static final String OriginalGlobalScaleInfoTag = "OriginalGlobalScaleInfo";
    public final static String OriginalGlobalScaleInfoMinTag = "OriginalGlobalScaleInfoMin";
    public final static String OriginalGlobalScaleInfoMaxTag = "OriginalGlobalScaleInfoMax";
    public final static String OriginalGlobalScaleInfoScaleTag = "OriginalGlobalScaleInfoScale";
    public final static String OriginalGlobalScaleInfoOffsetTag = "OriginalGlobalScaleInfoOffset";
    
/**
 * XMLTags default constructor.
 */
public MicroscopyXMLTags() {
	super();
}
}