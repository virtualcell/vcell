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
	public static final String FRAPDataAnalysisResultsTag = "FrapDataAnalysisResults";
	public static final String RecoveryTauAttrTag = "RecoveryTau";
	public static final String BleachWidthAttrTag = "BleachWidth";
	public static final String BleachWhileMonitoringTauAttrTag = "BleachWhileMonitoringTau";
	public static final String RecoveryDiffusionRateAttrTag = "RecoveryDiffusionRate";
	public static final String MobileFractionAttrTag = "MobileFraction";
	public static final String BleachTypeAttrTag = "BleachType";
	public static final String ImageDatasetTag = "ImageDataset";
	public static final String OwnerNameAttrTag = "OwnerName";
	public static final String ImageDatasetExternalDataInfoTag = "ImageDatasetExternalDataInfo";
	public static final String FilenameAttrTag = "Filename";
	public static final String ExternalDataIdentifierTag = "ExternalDataIdentifier";
	public static final String ROIExternalDataInfoTag = "ROIExternalDataInfo";
	public static final String OriginalImagePathAttrTag = "OriginalImagePath";
	public static final String StartingIndexForRecoveryAttrTag = "StartingIndexForRecovery";
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