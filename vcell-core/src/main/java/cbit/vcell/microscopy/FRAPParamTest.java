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

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.util.BeanUtils;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.opt.Parameter;
import cbit.vcell.resource.ResourceUtil;
/**
 * 
 */

public class FRAPParamTest
{
	private static final String SUB_DIRECTORY = "paramTest\\";
		
	private LocalWorkspace localWorkspace = null;
	private FRAPStudy frapStudy = null;
	
	public FRAPStudy getFrapStudy() {
		return frapStudy;
	}
	public void setFrapStudy(FRAPStudy frapStudy) {
		this.frapStudy = frapStudy;
	}
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}
	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
	}

	public void loadImageFile(String fileName)
	{
    	System.out.println("Loading "+fileName+" ...");
    		
		FRAPStudy newFRAPStudy = null;
		File inFile = new File(fileName);
		try {
			newFRAPStudy = FRAPWorkspace.loadFRAPDataFromImageFile(inFile, null);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println("File " + fileName +" has been loaded.");
		setFrapStudy(newFRAPStudy);
	}
	
	public void loadLogFile(String fileName, String varName, String bleachedMaskVarName)
	{
		System.out.println("Loading "+fileName+"...");
		
		FRAPStudy newFRAPStudy = null;
		File inFile = new File(fileName);
		try {
			newFRAPStudy = FRAPWorkspace.loadFRAPDataFromVcellLogFile(inFile, varName, bleachedMaskVarName, new Double(50000), true, null);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println("File " + fileName +" has been loaded.");
		setFrapStudy(newFRAPStudy);
	}
	
	public void loadXMLFile(String fileName)
	{
		System.out.println("Loading " + fileName + " ...");
		
		FRAPStudy newFRAPStudy = null;
		String xmlString;
		try {
			xmlString = XmlUtil.getXMLString(fileName);
			MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
			newFRAPStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(),null);
			newFRAPStudy.setXmlFilename(fileName);
			//create optData
			newFRAPStudy.setFrapOptData(new FRAPOptData(newFRAPStudy, FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF, getLocalWorkspace(), newFRAPStudy.getStoredRefData()));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} 
		System.out.println("File " + fileName +" has been loaded.");
		setFrapStudy(newFRAPStudy);
	}
	
	public String checkFrapStudyValidity()
	{
		if(frapStudy == null || frapStudy.getFrapData() == null || frapStudy.getFrapData().getRois() == null)
		{
			return "FrapStudy/FrapData/BasicRois is/are null.";
		}
		FRAPData fData = frapStudy.getFrapData();
		ROI cellROI = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		ROI bleachedROI = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		ROI backgroundROI = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
		if(cellROI.getNonzeroPixelsCount()<1)
		{
			return "No Cell ROI.";
		}
		if(bleachedROI.getNonzeroPixelsCount()<1)
		{
			return "No Bleached ROI.";
		}
		if(backgroundROI.getNonzeroPixelsCount()<1)
		{
			return "No Background ROI.";
		}
		//check ROI void/discontinuous location
		Point internalVoidLocation = ROI.findInternalVoid(cellROI);
		if(internalVoidLocation != null){
			return "CELL ROI has unfilled internal void area.";
		}
		Point[] distinctCellAreaLocations = ROI.checkContinuity(cellROI);
		if(distinctCellAreaLocations != null){
			return "CELL ROI has at least 2 discontinuous areas.";				
		}
		
		return "";
	}
	
	public void runProfileLikelihood()
	{ 
		String errorMsg = checkFrapStudyValidity();
		if(!errorMsg.equals(""))
		{
			System.out.println("Application terminated due to " + errorMsg);
			System.exit(1);
		}
		else
		{
			try{
				ClientTaskStatusSupport ctss =  new ClientTaskStatusSupport() {
						
						public void setProgress(int progress) {
							System.out.println(progress);
						}
						
						public void setMessage(String message) {
							System.out.println(message);
						}
						
						public boolean isInterrupted() {
							// TODO Auto-generated method stub
							return false;
						}
						
						public int getProgress() {
							// TODO Auto-generated method stub
							return 0;
						}

						public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
							throw new RuntimeException("not yet implemented");							
						}
					};
				//get startign index
				if(frapStudy.getStartingIndexForRecovery() == null)
				{
					int index = FRAPDataAnalysis.calculateRecoveryIndex(frapStudy.getFrapData());
					frapStudy.setStartingIndexForRecovery(index);
				}
				//get dependent rois
				if(frapStudy.getFrapData().getRois().length < 4)
				{
					frapStudy.refreshDependentROIs();
				}
				//get selected ROIs
				if(frapStudy.getSelectedROIsForErrorCalculation() == null)
				{
					boolean[] selectedROIs = new boolean[FRAPData.VFRAP_ROI_ENUM.values().length];
					int counter = 0;
					for(FRAPData.VFRAP_ROI_ENUM roiEnum : FRAPData.VFRAP_ROI_ENUM.values())
					{
						if(roiEnum.name().equals(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()) || roiEnum.name().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()))
						{
							counter++;
							continue;
						}
						if(frapStudy.getFrapData().getRoi(roiEnum.name()).getNonzeroPixelsCount() > 0)
						{
							selectedROIs[counter] = true;
							counter++;
						}
					}
					frapStudy.setSelectedROIsForErrorCalculation(selectedROIs);
				}
				//get frap opt data
				if(frapStudy.getFrapOptData() == null)
				{
					if(!FRAPWorkspace.areExternalDataOK(getLocalWorkspace(),frapStudy.getFrapDataExternalDataInfo(), frapStudy.getRoiExternalDataInfo()))
					{
						//if external files are missing/currupt or ROIs are changed, create keys and save them
						frapStudy.setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
						frapStudy.setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
						frapStudy.saveROIsAsExternalData(localWorkspace, frapStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),frapStudy.getStartingIndexForRecovery());
						frapStudy.saveImageDatasetAsExternalData(localWorkspace, frapStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),frapStudy.getStartingIndexForRecovery());
					}
					
					//run ref sim
					frapStudy.setFrapOptData(new FRAPOptData(frapStudy, FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF, localWorkspace, ctss));
				}
				
				FRAPOptData optData = frapStudy.getFrapOptData();
				//create frapModels
				if(frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]== null )
				{
					frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] = new FRAPModel(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT], null, null, null);
					if(frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() ==null )
					{
						frapStudy.getFrapOptData().setNumEstimatedParams(FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF);
						Parameter[] initialParams = FRAPModel.getInitialParameters(frapStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT],frapStudy.getStartingIndexForRecovery());
						Parameter[] bestParameters = frapStudy.getFrapOptData().getBestParamters(initialParams, frapStudy.getSelectedROIsForErrorCalculation());
						frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].setModelParameters(bestParameters);
					}
				}
				
				if(frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]== null)
				{
					frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] = new FRAPModel(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS], null, null, null);
					if(frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() ==null )
					{
						frapStudy.getFrapOptData().setNumEstimatedParams(FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF);
						Parameter[] initialParams = FRAPModel.getInitialParameters(frapStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS],frapStudy.getStartingIndexForRecovery());
						Parameter[] bestParameters = frapStudy.getFrapOptData().getBestParamters(initialParams, frapStudy.getSelectedROIsForErrorCalculation());
						frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].setModelParameters(bestParameters);
					}
				}
				
				
				//try diffusion with one diffusing component model
				System.out.println("Evaluating parameters in diffusion with one diffusing compoent model...");
				Parameter[] bestParameters = frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters();
				ProfileData[] profileData = optData.evaluateParameters(bestParameters, ctss);

				//output profile likelihood 
				File outputDir_oneComponent = new File(getLocalWorkspace().getDefaultWorkspaceDirectory() + SUB_DIRECTORY + "OneComponent_SAVED_AT_" + BeanUtils.generateDateTimeString() + System.getProperty("file.separator"));
				if(!outputDir_oneComponent.exists())
				{
					outputDir_oneComponent.mkdirs();
				}
                for(int i=0; i<profileData.length; i++)
                {
                	ProfileDataElement profileDataElement = profileData[i].getProfileDataElements().get(0);
					outputProfileLikelihood(profileData[i].getProfileDataElements(), profileDataElement.getParamName(), outputDir_oneComponent);
                }
                
                //try diffusion with two diffusing components model
				System.out.println("Evaluating parameters in diffusion with two diffusing compoents model...");
				bestParameters = frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters();
				profileData = optData.evaluateParameters(bestParameters, ctss);

				//output profile likelihood
				File outputDir_twoComponents = new File(getLocalWorkspace().getDefaultWorkspaceDirectory() + SUB_DIRECTORY + "TwoComponents_SAVED_AT_" + BeanUtils.generateDateTimeString() + System.getProperty("file.separator"));
				if(!outputDir_twoComponents.exists())
				{
					outputDir_twoComponents.mkdirs();
				}
                for(int i=0; i<profileData.length; i++)
                {
                	ProfileDataElement profileDataElement = profileData[i].getProfileDataElements().get(0);
					outputProfileLikelihood(profileData[i].getProfileDataElements(), profileDataElement.getParamName(), outputDir_twoComponents);
                }
			}catch(Exception e)
			{
				e.printStackTrace(System.out);
				System.exit(1);
			}
		}
	}
	
	private void outputProfileLikelihood(ArrayList<ProfileDataElement> arg_profileData, String fixedParamName, File outputDir) 
	{
		try{
			System.out.println("Writing profile likelihood...");
			//output results
			String outFileName = outputDir.getAbsolutePath() + "\\" + fixedParamName +"_profileLikelihood" +".txt"; 
			File outFile = new File(outFileName);
			FileWriter fstream = new FileWriter(outFile);
	        BufferedWriter out = new BufferedWriter(fstream);
	        //output profile
	        for(int i=0; i < arg_profileData.size(); i++)
	        {
	        	out.newLine();
	        	String rowStr = arg_profileData.get(i).getParameterValue() + "\t" + arg_profileData.get(i).getLikelihood();
	        	Parameter[] params = arg_profileData.get(i).getBestParameters();
	        	rowStr = rowStr + "\t";
	        	for(int j=0; j < params.length; j++)
	        	{
	        		rowStr = rowStr + "\t" + params[j].getInitialGuess();
	        	}
	        	out.write(rowStr);
	        }
		    out.close();
		    System.out.println("Output is done. Restults saved to " + outFileName);
		}catch(IOException e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	public void outputData(String outFileName, double[] timePoints, double[][] data) throws IOException
	{
		File outFile = new File(outFileName);
		FileWriter fstream = new FileWriter(outFile);
        BufferedWriter out = new BufferedWriter(fstream);
        
		for(int i = 0; i < timePoints.length; i++)
        {
        	String dataStr = "";
        	for(int j=0; j < FRAPData.VFRAP_ROI_ENUM.values().length; j++) 
        	{
        		if(FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED))
    			{
    				if(frapStudy.getSelectedROIsForErrorCalculation()[j])
    				{
    					dataStr = dataStr + data[j][i] + "\t";
    				}
    				else
    				{
    					dataStr = dataStr + "N/A" + "\t";
    				}
    			}
    			else if(FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1) ||
    					FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2) ||
    					FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3) ||
    					FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4) ||
    					FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5) ||
    					FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6) ||
    					FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7) ||
    					FRAPData.VFRAP_ROI_ENUM.values()[j].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8))
    			{
    				if(frapStudy.getSelectedROIsForErrorCalculation()[j])
    				{
    					dataStr = dataStr + data[j][i] + "\t";
    				}
    				else
    				{
    					dataStr = dataStr + "N/A" + "\t";
    				}
    			}
        	}
			out.write(timePoints[i] + "\t" + dataStr);
			out.newLine();
        }
		out.close();
	}
	
	public static void main(String[] args) {
		try{
			if(args.length != 2 && args.length != 4 && args.length != 5 && args.length != 7)
			{
				System.out.println("Wrong Command!");
				System.out.println("Usage: -i imageFileName(/.lsm .tif) [-D workingDirectory]");
				System.out.println("Usage: -l vcellLogFileName(/.log) [-D workingDirectory] -v varName bleachedMaskVarName");
				System.out.println("Usage: -x xmlFileName(/.vfrap) [-D workingDirectory]");
				System.exit(1);
			}
			else
			{
				File workingDirectory = null;
				if((args.length == 4 || args.length == 7) && args[2].equals("-D") && args[3].length() > 0)
				{
					workingDirectory = new File(args[3]);
				}
				else
				{
					workingDirectory = ResourceUtil.getUserHomeDir();
				}
				FRAPParamTest test = new FRAPParamTest();
				test.setLocalWorkspace(new LocalWorkspace(workingDirectory));
				
				//if no path, we try to get the file from workingDirectory\VirtualMicroscopy
				String fileStr = args[1];
				if(fileStr.indexOf(System.getProperty("file.separator"))<0)
				{
					fileStr = test.getLocalWorkspace().getDefaultWorkspaceDirectory() + fileStr;
				}
				//load file (frapStudy will be set if loaded successfully)
				if(args[0].equals("-i"))
				{
					test.loadImageFile(fileStr);
				}
				else if( args[0].equals("-l"))
				{
					if(args.length == 5 && args[2].equals("-v") && args[3].length() > 0 && args[4].length() > 0)
					{
						test.loadLogFile(fileStr, args[3], args[4]);
					}
					else if (args.length == 7 && args[4].equals("-v") && args[5].length() > 0 && args[6].length() > 0)
					{
						test.loadLogFile(fileStr, args[5], args[6]);
					}	
					else
					{
						System.out.println("Wrong Command!");
						System.out.println("Usage: -l vcellLogFileName(/.log) [-D workingDirectory] -v varName [cellMaskVarName, bleachedMaskVarName, backgroundMaskVarName]");
						System.exit(1);
					}
				}
				else if(args[0].equals("-x"))
				{
					test.loadXMLFile(fileStr);
				}
				
				//do parameter scan
				test.runProfileLikelihood();
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

}



