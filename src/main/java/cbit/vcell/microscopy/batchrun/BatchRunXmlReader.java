/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun;

import java.util.List;

import org.jdom.Element;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.MicroscopyXMLTags;
import cbit.vcell.opt.Parameter;
import cbit.vcell.xml.XmlParseException;

public class BatchRunXmlReader {
	
	/**
	 * This method returns a Biomodel object from a XML Element.
	 * Creation date: (3/13/2001 12:35:00 PM)
	 * @param param org.jdom.Element
	 * @return cbit.vcell.biomodel.BioModel
	 * @throws XmlParseException
	 */
	public FRAPBatchRunWorkspace getBatchRunWorkspace(Element param) throws XmlParseException
	{
		FRAPBatchRunWorkspace tempBatchRunWorkspace = new FRAPBatchRunWorkspace();
//		Element param = root.getChild(MicroscopyXMLTags.FrapBatchRunTag);
		//selected model index
		Element selectedModexIndexElement = param.getChild(MicroscopyXMLTags.BatchRunSelectedModelTypeTag);
		String selectedModexIndexStr = selectedModexIndexElement.getText();
		int selectedModelIndex = (new Integer(selectedModexIndexStr).intValue());
		if(selectedModexIndexStr != null && selectedModexIndexStr.length() > 0)
		{
			tempBatchRunWorkspace.setSelectedModel(selectedModelIndex);
		}
		//get average model parameters when there is selected model
		Element averageParametersElement = param.getChild(MicroscopyXMLTags.ModelParametersTag);
		Parameter[] params = null;
		if(averageParametersElement != null && averageParametersElement.getAttributes().size()>0)
		{
			if(selectedModelIndex == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
			{
				params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF];
				double primaryDiffRate = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.PrimaryRateAttrTag));
				params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE],
															FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
															FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
															FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(),
		                									primaryDiffRate);
				double primaryFraction = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.PrimaryFractionAttTag));
				params[FRAPModel.INDEX_PRIMARY_FRACTION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
															FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(), 
															FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
															FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(),
															primaryFraction);
				double bwmRate = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag));
				params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE],
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
											                bwmRate); 
			}
			else if(selectedModelIndex ==FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
			{
				params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF];
				double primaryDiffRate = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.PrimaryRateAttrTag));
				params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE], 
											                FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
											                FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
											                FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(), 
											                primaryDiffRate);
				double primaryFraction = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.PrimaryFractionAttTag));
				params[FRAPModel.INDEX_PRIMARY_FRACTION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
											                FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
											                FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
											                FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(), 
											                primaryFraction);
				double bwmRate = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag));
				params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), 
											                bwmRate);
				double secDiffRate = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.SecondRateAttrTag));
				params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE],
															FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(),
															FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(),
															FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getScale(), 
											                secDiffRate);
				double secFraction = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.SecondFractionAttTag));
				params[FRAPModel.INDEX_SECONDARY_FRACTION]= new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION],
															FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(),
															FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(),
															FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getScale(),
											                secFraction);
			}
			else if(selectedModelIndex ==FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
			{
				params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE];
				double bwmRate = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag));
				params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
															FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), 
											                bwmRate);
				double fittingParam = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.BindingSiteConcentrationAttTag));
				params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION],
															FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound(),
															FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound(),
															FRAPModel.REF_BS_CONCENTRATION_OR_A.getScale(), 
															fittingParam);
				double offRate = Double.parseDouble(averageParametersElement.getAttributeValue(MicroscopyXMLTags.ReactionOffRateAttTag));
				params[FRAPModel.INDEX_OFF_RATE]= new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE],
															FRAPModel.REF_REACTION_OFF_RATE.getLowerBound(),
															FRAPModel.REF_REACTION_OFF_RATE.getUpperBound(),
															FRAPModel.REF_REACTION_OFF_RATE.getScale(),
															offRate);
				params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = null;
				params[FRAPModel.INDEX_PRIMARY_FRACTION] = null;
				params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = null;
				params[FRAPModel.INDEX_SECONDARY_FRACTION]= null;
				params[FRAPModel.INDEX_ON_RATE]= null;
			}
			tempBatchRunWorkspace.setAverageParameters(params);
		}
		
		//get FrapStudy file list
		Element frapStudyListElement = param.getChild(MicroscopyXMLTags.FrapStudyListTag);
		String numFrapStudiesStr = frapStudyListElement.getAttributeValue(MicroscopyXMLTags.NumFrapStudyListAttrTag);
	    Integer.parseInt(numFrapStudiesStr);
	    //get each individual FRAPStudy 
	    @SuppressWarnings("unchecked")
	    List<Element> fileNameList = frapStudyListElement.getChildren(MicroscopyXMLTags.FrapFileNameTag);
	    for (int i = 0; i < fileNameList.size(); i++)
	    {
	        String fileName = fileNameList.get(i).getText();
	        //create empty frap study to put in temporay batchrunworkspace
	        FRAPStudy fStudy = new FRAPStudy();
	        fStudy.setXmlFilename(fileName);
	        tempBatchRunWorkspace.addFrapStudy(fStudy);
	    }
	    
	    return tempBatchRunWorkspace;
	}
}
