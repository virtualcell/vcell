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

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.ArrayList;

import org.jdom.Element;

import cbit.util.xml.XmlUtil;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.MicroscopyXMLTags;
import cbit.vcell.opt.Parameter;
import cbit.vcell.xml.XmlParseException;

public class BatchRunXmlProducer 
{
	public static void writeXMLFile(FRAPBatchRunWorkspace batchRunWorkspace,File outputFile) throws Exception
	{
		Element root = getXML(batchRunWorkspace);
		java.io.FileOutputStream fileOutStream = new java.io.FileOutputStream(outputFile);
		BufferedOutputStream bufferedStream = new BufferedOutputStream(fileOutStream);
		XmlUtil.writeXmlToStream(root, true, bufferedStream);
		fileOutStream.flush();
		fileOutStream.close();
	}

	/**
	 * Method mangle.
	 * @param str String
	 * @return String
	 */
	private static String mangle(String str){
		return cbit.vcell.xml.XmlBase.mangle(str);
	}

	/**
	 * This method returns a XML representation 
	 * for a frap batch run including 
	 */
	private static Element getXML(FRAPBatchRunWorkspace param) throws XmlParseException, cbit.vcell.parser.ExpressionException {

		Element batchRunNode = new Element(MicroscopyXMLTags.FrapBatchRunTag);
		//Get selected model index
		Element selectedModelIdxElem = new Element(MicroscopyXMLTags.BatchRunSelectedModelTypeTag);
		selectedModelIdxElem.setText(mangle(param.getSelectedModel()+""));
		batchRunNode.addContent(selectedModelIdxElem);
		
		//Get average model parameters
		Element parametersNode = new Element(MicroscopyXMLTags.ModelParametersTag);
		Parameter[] avgParams = param.getAverageParameters(); 
		if(avgParams != null)
		{
			if(param.getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT && avgParams.length == FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
			{
				parametersNode.setAttribute(MicroscopyXMLTags.PrimaryRateAttrTag, avgParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() + "");
				parametersNode.setAttribute(MicroscopyXMLTags.PrimaryFractionAttTag, avgParams[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() + "");
				parametersNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, avgParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + "");
			}
			else if(param.getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS && avgParams.length == FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
			{
				parametersNode.setAttribute(MicroscopyXMLTags.PrimaryRateAttrTag, avgParams[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() + "");
				parametersNode.setAttribute(MicroscopyXMLTags.PrimaryFractionAttTag, avgParams[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() + "");
				parametersNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, avgParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + "");
				parametersNode.setAttribute(MicroscopyXMLTags.SecondRateAttrTag, avgParams[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess() + "");
				parametersNode.setAttribute(MicroscopyXMLTags.SecondFractionAttTag, avgParams[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess() + "");
			}
			else if(param.getSelectedModel() == FRAPModel.IDX_MODEL_REACTION_OFF_RATE && avgParams.length == FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE)
			{
				parametersNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, avgParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + "");
				parametersNode.setAttribute(MicroscopyXMLTags.BindingSiteConcentrationAttTag, avgParams[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess() + "");
				parametersNode.setAttribute(MicroscopyXMLTags.ReactionOffRateAttTag, avgParams[FRAPModel.INDEX_OFF_RATE].getInitialGuess() + "");
			}
		}
		batchRunNode.addContent(parametersNode);
		
		//Get frap study file lists
		if(param.getFrapStudies() != null && param.getFrapStudies().size() > 0)
		{
			Element frapStudyListNode = new Element(MicroscopyXMLTags.FrapStudyListTag);
			frapStudyListNode.setAttribute(MicroscopyXMLTags.NumFrapStudyListAttrTag,Integer.toString(param.getFrapStudies().size()));
			ArrayList<FRAPStudy> frapList = param.getFrapStudies();
			for(int i = 0; i < frapList.size(); i++)
			{
				if(frapList.get(i).getXmlFilename() != null)
				{
					Element fileNameNode = new Element(MicroscopyXMLTags.FrapFileNameTag);
					fileNameNode.addContent(frapList.get(i).getXmlFilename());
					frapStudyListNode.addContent(fileNameNode);
				}
			}
			
			batchRunNode.addContent(frapStudyListNode);
		}
		
		return batchRunNode;
	}
}
