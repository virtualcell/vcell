package cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.ImageLoadingProgress;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_MultiFilePanel;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

public class MultiFileDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "BATCHRUN_MultipleFiles";
    private LoadFRAPData_MultiFilePanel multiFilePanel = new LoadFRAPData_MultiFilePanel();
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    private boolean isFileLoaded = false;
    
    public MultiFileDescriptor() {
    	super();
        setPanelComponent(multiFilePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
    }
    
    public String getNextPanelDescriptorID() {
        return FileSummaryDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return FileTypeDescriptor.IDENTIFIER;
    }
    
    //load the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		final File[] files = multiFilePanel.getSelectedFiles();
		
    	if(files.length > 0)
    	{
    		final String filePath = files[0].getParent();
    		final String LOADING_MESSAGE = "Loading files from directory "+ filePath +"...";
    		
			AsynchClientTask updateUIBeforeLoadTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				try
    				{
    					VirtualFrapMainFrame.updateStatus(LOADING_MESSAGE);
    				}catch (Exception e){
    					e.printStackTrace(System.out);
    				}
    			}
    		};
			
			
    		AsynchClientTask loadTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
					FRAPStudy newFRAPStudy = null;

					newFRAPStudy = FRAPWorkspace.loadFRAPDataFromMultipleFiles(files, this.getClientTaskStatusSupport(), multiFilePanel.isTimeSeries(), multiFilePanel.getTimeInterval());
					isFileLoaded = true;
					
					//for all loaded files
					hashTable.put(FRAPStudyPanel.NEW_FRAPSTUDY_KEY, newFRAPStudy);
    					
    			}
    		};
    		
    		AsynchClientTask afterLoadingSwingTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				FRAPStudy newFRAPStudy = (FRAPStudy)hashTable.get(FRAPStudyPanel.NEW_FRAPSTUDY_KEY);
    				//setFrapStudy fires property change, so we have to put it in Swing thread.
    				getFrapWorkspace().setWorkingFRAPStudy(newFRAPStudy);
    				
    				VirtualFrapLoader.mf.setMainFrameTitle("");
    				VirtualFrapMainFrame.updateProgress(0);
    				if(isFileLoaded)
    				{
        				VirtualFrapMainFrame.updateStatus("Loaded files from " + filePath);
    				}
    				else
    				{
						VirtualFrapMainFrame.updateStatus("Failed loading files from " + filePath+".");
    				}
    			}
    		};
    		taskArrayList.add(updateUIBeforeLoadTask);
    		taskArrayList.add(loadTask);
    		taskArrayList.add(afterLoadingSwingTask);
    	}
    	else
    	{
    		DialogUtils.showErrorDialog(multiFilePanel, "No file is selected. Please input one or more file names to continue.");
    		throw new RuntimeException("No file is selected. Please input one or more file names to continue.");
    	}
		return taskArrayList;
    } 
    
    //Actions after the panel disappears
    public ArrayList<AsynchClientTask> postNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		WizardPanelDescriptor newDescriptor = getWizardModel().getCurrentPanelDescriptor();
		
		return taskArrayList;
    }
    
    public FRAPBatchRunWorkspace getFrapWorkspace() {
		return batchRunWorkspace;
	}
    
	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
	}
}


