package cbit.vcell.microscopy.gui.loaddatawizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ListSelectionModel;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;

public class LoadFRAPData_SingleFileDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "LoadFRAPData_SingleFile";
    //    private FRAPStudy localFrapStudy = null;
    private LoadFRAPData_SingleFilePanel singleFilePanel = new LoadFRAPData_SingleFilePanel();
    private PropertyChangeSupport propertyChangeSupport;
    private boolean isFileLoaded = false;
    
    public LoadFRAPData_SingleFileDescriptor() {
    	super();
        setPanelComponent(singleFilePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    //This method is override to make fure that it goes to SummaryPanel
    public String getNextPanelDescriptorID() {
        return LoadFRAPData_SummaryDescriptor.IDENTIFIER;
    }
    //This method is override to make sure that it backs to FileTypePanel
    public String getBackPanelDescriptorID() {
        return LoadFRAPData_FileTypeDescriptor.IDENTIFIER;
    }
    
    //load the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
    	if(singleFilePanel.getFileName().length() > 0)
    	{
//    		final String inFileDescription =
//    			(inFileArr.length == 1
//    				?"file "+inFileArr[0].getAbsolutePath()
//    				:"files from "+inFileArr[0].getParentFile().getAbsolutePath());
    		final String fileStr = singleFilePanel.getFileName();
    		final String LOADING_MESSAGE = "Loading "+fileStr+"...";
    		
			AsynchClientTask updateUIBeforeLoadTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
    		{
    			public void run(Hashtable<String, Object> hashTable) throws Exception
    			{
    				VirtualFrapMainFrame.updateStatus(LOADING_MESSAGE);
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
    					
    					File inFile = new File(fileStr);
    					if(inFile.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)) //.log (vcell log file) 
    					{
							DataIdentifier[] dataIdentifiers =
								FRAPData.getDataIdentiferListFromVCellSimulationData(inFile, 0);
							String[][] rowData = new String[dataIdentifiers.length][1];
							for (int i = 0; i < dataIdentifiers.length; i++) {
								if(dataIdentifiers[i].getVariableType().equals(VariableType.VOLUME)){
									rowData[i][0] = dataIdentifiers[i].getName();
								}
							}
							int[] selectedIndexArr = DialogUtils.showComponentOKCancelTableList(
									LoadFRAPData_SingleFileDescriptor.this.getPanelComponent(),
									"Select Volume Variable",
									new String[] {"Volume Variable Name"},
									rowData, ListSelectionModel.SINGLE_SELECTION);
							if(selectedIndexArr != null && selectedIndexArr.length > 0){
//								localFrapStudyPanel.clearCurrentLoadState();
								FRAPData newFrapData = 
									FRAPData.importFRAPDataFromVCellSimulationData(inFile,
										dataIdentifiers[selectedIndexArr[0]].getName(),
										/*loadFileProgressListener*/this.getClientTaskStatusSupport());
								newFRAPStudy = new FRAPStudy();
								newFRAPStudy.setFrapData(newFrapData);
								isFileLoaded = true;
							}else{
								throw UserCancelException.CANCEL_GENERIC;
							}
    					}else //.lsm or other image formatss
    					{
//    							localFrapStudyPanel.clearCurrentLoadState();
//    							ImageLoadingProgress myImageLoadingProgress =
//    								new ImageLoadingProgress(){
//    									public void setSubProgress(double mbprog) {
//    										loadFileProgressListener.updateProgress(mbprog);
//    									}
//    							};
    	            			ImageDataset imageDataset = ImageDatasetReader.readImageDataset(inFile.getAbsolutePath(), /*myImageLoadingProgress*/this.getClientTaskStatusSupport());
    	            			FRAPData newFrapData = FRAPData.importFRAPDataFromImageDataSet(imageDataset);
    							newFRAPStudy = new FRAPStudy();
    							newFRAPStudy.setFrapData(newFrapData);
    							isFileLoaded = true;
    					}
    					
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
        				VirtualFrapMainFrame.updateStatus("Loaded " + fileStr);
    				}
    				else
    				{
						VirtualFrapMainFrame.updateStatus("Failed loading " + fileStr+".");
    				}
    			}
    		};
    		taskArrayList.add(updateUIBeforeLoadTask);
    		taskArrayList.add(loadTask);
    		taskArrayList.add(afterLoadingSwingTask);
    	}
		
		return taskArrayList;
    } 
    
    public void addPropertyChangeListener(PropertyChangeListener p) {
//        propertyChangeSupport.addPropertyChangeListener(new PropertyChangeListenerProxyVCell(p));
    	propertyChangeSupport.addPropertyChangeListener(p);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener p) {
//    	PropertyChangeListenerProxyVCell.removeProxyListener(propertyChangeSupport, p);
        propertyChangeSupport.removePropertyChangeListener(p);
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}