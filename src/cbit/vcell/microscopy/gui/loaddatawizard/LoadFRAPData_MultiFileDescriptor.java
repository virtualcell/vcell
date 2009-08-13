package cbit.vcell.microscopy.gui.loaddatawizard;

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
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

public class LoadFRAPData_MultiFileDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "LoadFRAPData_MultipleFiles";
    private LoadFRAPData_MultiFilePanel multiFilePanel = new LoadFRAPData_MultiFilePanel();
    private PropertyChangeSupport propertyChangeSupport;
    private boolean isFileLoaded = false;
    
    public LoadFRAPData_MultiFileDescriptor() {
    	super();
        setPanelComponent(multiFilePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    public String getNextPanelDescriptorID() {
        return LoadFRAPData_SummaryDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return LoadFRAPData_FileTypeDescriptor.IDENTIFIER;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.addPropertyChangeListener(p);
    }
  
    public void removePropertyChangeListener(PropertyChangeListener p) {
        propertyChangeSupport.removePropertyChangeListener(p);
    }
  
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
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
    					//TODO: to check if save is needed before loading 
//    					saveIfNeeded();
    				
    					FRAPStudy newFRAPStudy = null;
//    					SavedFrapModelInfo newSavedFrapModelInfo = null;
    					String newVFRAPFileName = null;
    					
    					ImageDataset imageDataset = ImageDatasetReader.readImageDatasetFromMultiFiles(files, this.getClientTaskStatusSupport(), multiFilePanel.isTimeSeries(), multiFilePanel.getTimeInterval());
    					FRAPData newFrapData = new FRAPData(imageDataset, new String[] { FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()});
//    					frapData.setOriginalGlobalScaleInfo(null);
    					newFRAPStudy = new FRAPStudy();
    					newFRAPStudy.setFrapData(newFrapData);
    					isFileLoaded = true;
    					
    					//for all loaded files
    					newFRAPStudy.setXmlFilename(newVFRAPFileName);
    					hashTable.put(FRAPStudyPanel.NEW_FRAPSTUDY_KEY, newFRAPStudy);
    					
//    					try{
//    						localFrapStudyPanel.setSavedFrapModelInfo(newSavedFrapModelInfo);
//    						localFrapStudyPanel.applySavedParameters(newFRAPStudy);
//    					}catch(Exception e){
//    						throw new RuntimeException(e.getMessage());
//    					}
    					//after loading new file, we need to set frapOptData to null.
    		            
    			}
    		};
    		
    		AsynchClientTask afterLoadingSwingTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				FRAPStudy newFRAPStudy = (FRAPStudy)hashTable.get(FRAPStudyPanel.NEW_FRAPSTUDY_KEY);
    				if(!isFileLoaded)
    				{
    					newFRAPStudy = new FRAPStudy();
    				}
    				firePropertyChange(FRAPStudyPanel.LOADDATA_FRAPSTUDY_CHANGE_PROPERTY, null, newFRAPStudy);
    				
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
}


