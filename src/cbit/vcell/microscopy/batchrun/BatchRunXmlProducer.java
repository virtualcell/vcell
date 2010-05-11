package cbit.vcell.microscopy.batchrun;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;

import org.jdom.Element;
import org.vcell.util.document.ExternalDataIdentifier;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.MicroscopyXMLTags;
import cbit.vcell.microscopy.FRAPData.VFRAP_ROI_ENUM;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.Xmlproducer;

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
		}
		batchRunNode.addContent(parametersNode);
		//Get selected ROIs
		if(param.getSelectedROIsForErrorCalculation() != null && param.getSelectedROIsForErrorCalculation().length >0)
		{
			Element selectedROIsNode = new Element(MicroscopyXMLTags.SelectedROIsTag);
			boolean[] selectedROIs = param.getSelectedROIsForErrorCalculation();
			String rowText = "";
			for (int i = 0; i < selectedROIs.length; i++){
	            if (i > 0){
	                  rowText += " ";
	            }
	            rowText += selectedROIs[i];
		    }
			selectedROIsNode.addContent(rowText);
			batchRunNode.addContent(selectedROIsNode);
		}
		//Get frap study file lists
		if(param.getFrapStudyList() != null && param.getFrapStudyList().size() > 0)
		{
			Element frapStudyListNode = new Element(MicroscopyXMLTags.FrapStudyListTag);
			frapStudyListNode.setAttribute(MicroscopyXMLTags.NumFrapStudyListAttrTag,Integer.toString(param.getFrapStudyList().size()));
			ArrayList<FRAPStudy> frapList = param.getFrapStudyList();
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
