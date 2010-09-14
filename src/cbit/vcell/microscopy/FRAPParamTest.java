package cbit.vcell.microscopy;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import loci.formats.FormatException;
import cbit.image.ImageException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.opt.Parameter;
import cbit.vcell.resource.ResourceUtil;
/**
 * 
 */

public class FRAPParamTest
{
	private static final double stepIncrease = 1.001; //increase 0.01 based on last step value
	private static final double stepDecrease = 0.999; //decrease 0.1 based on last step value
	private static final String SUB_DIRECTORY = "paramTest\\test2_mobileFrac0.1%\\";
	private static final int maxIteration = 100; 
	private ArrayList<ProfileDataElement> profileData = new ArrayList<ProfileDataElement>();
		
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
		} catch (ImageException e) {
			e.printStackTrace(System.out);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		} catch (FormatException e) {
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
			newFRAPStudy = FRAPWorkspace.loadFRAPDataFromVcellLogFile(inFile, varName, bleachedMaskVarName, new Double(50000), null);
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
			newFRAPStudy.setFrapOptData(new FRAPOptData(newFRAPStudy, FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), newFRAPStudy.getStoredRefData()));
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
				//try diffusion with one diffusing compoent model
				Parameter[] bestParameters = frapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters();//######
				//get frap opt class
				FRAPOptData optData = frapStudy.getFrapOptData();
                ProfileData[] profileData = optData.evaluateParameters(bestParameters, new ClientTaskStatusSupport() {
					
						public void setProgress(int progress) {
							// TODO Auto-generated method stub
							
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
					});

				//output profile likelihood
                for(int i=0; i<profileData.length; i++)
                {
                	ProfileDataElement profileDataElement = profileData[i].getProfileDataElements().get(0);
					outputProfileLikelihood(profileData[i].getProfileDataElements(), profileDataElement.getParamName());
                }
			}catch(Exception e)
			{
				e.printStackTrace(System.out);
				System.exit(1);
			}
		}
	}
	
	private void outputProfileLikelihood(ArrayList<ProfileDataElement> arg_profileData, String fixedParamName) 
	{
		try{
			System.out.println("Writing profile likelihood...");
			//output results
			String outFileName = getLocalWorkspace().getDefaultWorkspaceDirectory() + SUB_DIRECTORY + fixedParamName +"_profileLikelihood" +".txt"; 
			File outFile = new File(outFileName);
			FileWriter fstream = new FileWriter(outFile);
	        BufferedWriter out = new BufferedWriter(fstream);
	        //output profile
	        for(int i=0; i < arg_profileData.size(); i++)
	        {
	        	out.newLine();
	        	String rowStr = Math.log10(arg_profileData.get(i).getParameterValue()) + "\t" + arg_profileData.get(i).getLikelihood();
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



