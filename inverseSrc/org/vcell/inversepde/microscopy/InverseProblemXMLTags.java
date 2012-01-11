/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.microscopy;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This class contains all the XML tags.
 * @author schaff
 * @version $Revision: 1.0 $
 */
public class InverseProblemXMLTags {

	public static final String FRAPStudyTag = "FrapStudy";
	public static final String InverseProblemTag = "InverseProblem";
	public static final String UShortImageTag = "UShortImage";
	public static final String FRAPDataTag = "FrapData";
	public static final String TimeStampListTag = "TimeStampList";
	public static final String ROIImageTag = "ROIImage";
	public static final String ROIImageNameAttrTag = "ROIImageName";
	public static final String ROITag = "ROI";
	public static final String ROITypeAttrTag = "ROIType";
	public static final String ROINameAttrTag = "ROIName";
	public static final String ROIPixelValueAttrTag = "ROIPixelValue";
	public static final String FRAPModelParametersTag = "FrapModelParameters";
	public static final String RecoveryTauAttrTag = "RecoveryTau";
	public static final String BleachWidthAttrTag = "BleachWidth";
	public static final String BleachWhileMonitoringTauAttrTag = "BleachWhileMonitoringTau";
	public static final String RecoveryDiffusionRateAttrTag = "RecoveryDiffusionRate";
	public static final String MobileFractionAttrTag = "MobileFraction";
	public static final String SecondRateAttrTag = "SecondaryDiffusionRate";
	public static final String SecondFractionAttTag = "SecondaryMobileFraction";
	public static final String BleachTypeAttrTag = "BleachType";
	public static final String ImageDatasetTag = "ImageDataset";
	public static final String OwnerNameAttrTag = "OwnerName";
	public static final String ImageDatasetExternalDataInfoTag = "ImageDatasetExternalDataInfo";
	public static final String FilenameAttrTag = "Filename";
	public static final String ExternalDataIdentifierTag = "ExternalDataIdentifier";
	public static final String ROIExternalDataInfoTag = "ROIExternalDataInfo";
	public static final String RefExternalDataInfoTag = "RefExternalDataInfo";
	public static final String OriginalImagePathAttrTag = "OriginalImagePath";
	public static final String StartingIndexForRecoveryAttrTag = "StartingIndexForRecovery";
	public static final String FitExpressionAttrTag = "FitExpression";

	
	public static final String TimeSeriesImageData_Tag = "TimeSeriesImageData";
	public static final String ZStackImageData_Tag = "ZStackImageData";
	public static final String PSFImageData_Tag = "PSFImageData";
	public static final String BasesFieldDataEDITag = "BasesFieldDataEDI";
	public static final String PsfFieldDataEDITag = "PsfFieldDataEDI";
	public static final String ImageROIFieldDataEDITag = "ImageROIFieldDataEDI";
	public static final String ExactSolutionEDITag = "ExactSolutionEDI";
	public static final String VolumeControlPointIndicesTag = "VolumeControlPointIndices";
	public static final String MembraneControlPointIndicesTag = "MembraneControlPointIndices";
	public static final String MatlabDataFileTag = "MatlabDataFile";
	public static final String RefVolumeSimMathModelTag = "RefVolumeSimMathModel";
	public static final String RefMembraneSimMathModelTag = "RefMembraneSimMathModel";
	public static final String RefFluxSimMathModelTag = "RefFluxSimMathModel";
	public static final String NonlinearModelTag = "NonlinearModel";

	public static final String ListOfParametersTag = "ListOfParameters";
	public static final String ParameterTag = "Parameter";
	public static final String ParameterLowerBoundAttrTag = "LowerBound";
	public static final String ParameterUpperBoundAttrTag = "UpperBound";
	public static final String ParameterInitialGuessAttrTag = "InitialGuess";
	public static final String ParameterScaleAttrTag = "Scale";

	
    public static final String OriginalGlobalScaleInfoTag = "OriginalGlobalScaleInfo";
    public final static String OriginalGlobalScaleInfoMinTag = "OriginalGlobalScaleInfoMin";
    public final static String OriginalGlobalScaleInfoMaxTag = "OriginalGlobalScaleInfoMax";
    public final static String OriginalGlobalScaleInfoScaleTag = "OriginalGlobalScaleInfoScale";
    public final static String OriginalGlobalScaleInfoOffsetTag = "OriginalGlobalScaleInfoOffset";

    public final static String ListOfVolumeBasesTag = "ListOfVolumeBases";
    public final static String VolumeBasisTag = "VolumeBasis";
    public final static String ListOfMembraneBasesTag = "ListOfMembraneBases";
    public final static String MembraneBasisTag = "MembraneBasis";
    public final static String BasisIndexAttrTag = "BasisIndex";
    public final static String BasisControlPointMeshIndexAttrTag = "BasisControlPointMeshIndex";
    public final static String BasisIsWithinBleachAttrTag = "IsWithinBleach";
    public final static String BasisImageValueAttrTag = "BasisImageValue";
    public final static String BasisSubvolumeNameAttrTag = "SubVolumeName";
    public final static String AdjacentInsideVolumeBasisNameAttrTag = "AdjacentInsideVolumeBasisName";
    public final static String InsideSubvolumeNameAttrTag = "InsideSubvolumeName";
    public final static String OutsideSubvolumeNameAttrTag = "OutsideSubvolumeName";
	public static final String LinearResponseModelTag = "LinearResponseModel";
	public static final String BasisFunctionsTag = "BasisFunctions";
    
    
/**
 * XMLTags default constructor.
 */
public InverseProblemXMLTags() {
	super();
}
}