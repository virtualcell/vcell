package cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFileChooser;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.batchrun.gui.VirtualFrapBatchRunFrame;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.gui.PDEPlotControlPanel.DataIdentifierFilter;

public class FileSaveDescriptor extends WizardPanelDescriptor {

    public static final String IDENTIFIER = "BATCHRUN_SAVE_FILE";
    //    private FRAPStudy localFrapStudy = null;
    private FileSavePanel saveFilePanel = new FileSavePanel();
    private boolean isFileLoaded = false;
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    private LocalWorkspace localWorkspace = null;
   
	public FileSaveDescriptor() {
    	super();
        setPanelComponent(saveFilePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
    }
    //This method is override to make fure that it goes to SummaryPanel
    public String getNextPanelDescriptorID() {
    	return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    //This method is override to make sure that it backs to FileTypePanel
    public String getBackPanelDescriptorID() {
        return ROISummaryDescriptor.IDENTIFIER;
    }
    
    //load the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
    	if(saveFilePanel.getFileName().length() > 0)
    	{
    		final String fileStr = saveFilePanel.getFileName();
    		final String SAVING_MESSAGE = "Saving "+fileStr+"...";
    		
    		AsynchClientTask beforeSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				if(getBatchRunWorkspace().getWorkingFrapStudy() == null || getBatchRunWorkspace().getWorkingFrapStudy().getFrapData() == null)
    				{
    					throw new Exception("No FRAP Data exists to save");
    				}else{
    					File outputFile = null;
    					String saveFileName = saveFilePanel.getFileName();
    		    		if(saveFileName == null)
    		    		{
    		    			throw new Exception("Please give a proper file name to save!");
    		    		}
    		    		else
    		    		{
		    				File tempOutputFile = new File(saveFileName);
				    		if(!VirtualFrapLoader.filter_vfrap.accept(tempOutputFile)){
		    					if(tempOutputFile.getName().indexOf(".") == -1){
		    						tempOutputFile = new File(tempOutputFile.getParentFile(),tempOutputFile.getName()+"."+VirtualFrapLoader.VFRAP_EXTENSION);
		    					}else{
		    						throw new Exception("Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);//return?
		    					}
		    				}
				    		if(tempOutputFile.exists())
				    		{
				    			String overwriteChoice = DialogUtils.showWarningDialog(saveFilePanel, "OverWrite file\n"+ tempOutputFile.getAbsolutePath(),
				    						new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL);
				    			if(overwriteChoice.equals(UserMessage.OPTION_CANCEL)){
				    				throw UserCancelException.CANCEL_GENERIC;//----? Should use "return"?
//    				    				return;
				    			}
				    			else
				    			{
					    			//Remove overwritten vfrap document external and simulation files
					    			try{
					    				MicroscopyXmlReader.ExternalDataAndSimulationInfo externalDataAndSimulationInfo =
					    					MicroscopyXmlReader.getExternalDataAndSimulationInfo(tempOutputFile);
					    				FRAPStudy.removeExternalDataAndSimulationFiles(
					    						externalDataAndSimulationInfo.simulationKey,
					    						(externalDataAndSimulationInfo.frapDataExtDataInfo != null
					    							?externalDataAndSimulationInfo.frapDataExtDataInfo.getExternalDataIdentifier():null),
					    						(externalDataAndSimulationInfo.roiExtDataInfo != null
					    							?externalDataAndSimulationInfo.roiExtDataInfo.getExternalDataIdentifier():null),
					    						getLocalWorkspace());
					    			}catch(Exception e){
					    				System.out.println(
					    					"Error deleting externalData and simulation files for overwritten vfrap document "+
					    					tempOutputFile.getAbsolutePath()+"  "+e.getMessage());
					    				e.printStackTrace();
					    			}
				    			}
				    		}
				    		outputFile = tempOutputFile;
    		    		}
    		    		
    		    		
    		    		if(outputFile != null)
    		    		{
//    		    			VirtualFrapMainFrame.updateStatus("Saving file " + outputFile.getAbsolutePath()+" ...");
    		    			hashTable.put(FRAPStudyPanel.SAVE_FILE_NAME_KEY, outputFile);
    		    		}
    				}
    			}
    		};
    		
    		AsynchClientTask saveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
    				saveProcedure(outFile, false, this.getClientTaskStatusSupport());
    			}
    		};
    		
    		AsynchClientTask afterSaveTask = new AsynchClientTask("Saving file ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				File outFile = (File)hashTable.get(FRAPStudyPanel.SAVE_FILE_NAME_KEY);
//    				VirtualFrapMainFrame.updateStatus("File " + outFile.getAbsolutePath()+" has been saved.");
//    		        VirtualFrapLoader.mf.setMainFrameTitle(outFile.getName());
//    		        VirtualFrapMainFrame.updateProgress(0);
    			}
    		};


    		
    		taskArrayList.add(beforeSaveTask);
    		taskArrayList.add(saveTask);
    		taskArrayList.add(afterSaveTask);
    	}
		
		return taskArrayList;
    } 
    
    private void saveProcedure(File xmlFrapFile, boolean bSaveAs, ClientTaskStatusSupport progressListener) throws Exception
	{
		
		//save
		MicroscopyXmlproducer.writeXMLFile(getBatchRunWorkspace().getWorkingFrapStudy(), xmlFrapFile, true,progressListener,VirtualFrapMainFrame.SAVE_COMPRESSED);
		getBatchRunWorkspace().getWorkingFrapStudy().setXmlFilename(xmlFrapFile.getAbsolutePath());
		getBatchRunWorkspace().getWorkingFrapStudy().setSaveNeeded(false);
	}
    
    public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}
    
	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
	}
	
	 
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}
	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
	}
}