package cbit.vcell.microscopy.gui.loaddatawizard;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.wizard.WizardPanelDescriptor;

import cbit.image.gui.SourceDataInfo;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.solver.DataProcessingOutput;

public class LoadFRAPData_PostProcessingDataDescriptor extends WizardPanelDescriptor {
    public static final String IDENTIFIER = "LoadFRAPData_PostProcessingDataDescriptor";
    private LoadFRAPData_PostProcessingDataPanel postProcessingDataPanel = new LoadFRAPData_PostProcessingDataPanel();
    private FRAPSingleWorkspace frapWorkspace = null;
    private boolean isFileLoaded = false;
    private DocumentWindowManager documentWindowManager;
    
	public LoadFRAPData_PostProcessingDataDescriptor() {
    	super();
        setPanelComponent(postProcessingDataPanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setProgressPopupShown(true); 
        setTaskProgressKnown(true);
    }

    //This method is override to make sure that it backs to FileTypePanel
    public String getBackPanelDescriptorID() {
        return LoadFRAPData_FileTypeDescriptor.IDENTIFIER;
    }
    //This method is override to make fure that it goes to SummaryPanel
    public String getNextPanelDescriptorID() {
        return LoadFRAPData_SummaryDescriptor.IDENTIFIER;
    }
	public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
	}

	public void setDocumentWindowManager(DocumentWindowManager documentWindowManager){
		this.documentWindowManager = documentWindowManager;
	}
	 public ArrayList<AsynchClientTask> preNextProcess(){
		final String LOADING_MESSAGE = "Loading variable data "+postProcessingDataPanel.getSelectedVariableName()+"...";
		AsynchClientTask updateUIBeforeLoadTask = new AsynchClientTask("Updating status message...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			public void run(Hashtable<String, Object> hashTable) throws Exception{
				 if(postProcessingDataPanel.getSelectedVariableName() == null || postProcessingDataPanel.getDataProcessingOutput() == null){
					 throw new RuntimeException("Post Processing Data variable not selected");
				 }
				VirtualFrapMainFrame.updateStatus(LOADING_MESSAGE);
			}
		};
		
		AsynchClientTask loadTask = new AsynchClientTask(LOADING_MESSAGE, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			public void run(Hashtable<String, Object> hashTable) throws Exception{
				String selectedVariableName = postProcessingDataPanel.getSelectedVariableName();
				DataProcessingOutput dataProcessingOutput = postProcessingDataPanel.getDataProcessingOutput();
//				Vector<SourceDataInfo> sourceDataInfoV = dataProcessingOutput.getDataGenerators().get(selectedVariableName);
				FRAPStudy newFRAPStudy = FRAPWorkspace.loadFRAPDataFromDataProcessingOutput(dataProcessingOutput,selectedVariableName, 65535.0,this.getClientTaskStatusSupport());
				isFileLoaded = true;
				hashTable.put(FRAPStudyPanel.NEW_FRAPSTUDY_KEY, newFRAPStudy);
			}
		};
		
		AsynchClientTask afterLoadingSwingTask = new AsynchClientTask("Setting FrapWorkspace FrapStudy...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			public void run(Hashtable<String, Object> hashTable) throws Exception{
				FRAPStudy newFRAPStudy = (FRAPStudy)hashTable.get(FRAPStudyPanel.NEW_FRAPSTUDY_KEY);
				//setFrapStudy fires property change, so we have to put it in Swing thread.
				getFrapWorkspace().setFrapStudy(newFRAPStudy, true);
				
				VirtualFrapLoader.mf.setMainFrameTitle("");
				VirtualFrapMainFrame.updateProgress(0);
				if(isFileLoaded){
					VirtualFrapMainFrame.updateStatus("Loaded " + postProcessingDataPanel.getSelectedVariableName());
				}
				else{
					VirtualFrapMainFrame.updateStatus("Failed loading " + postProcessingDataPanel.getSelectedVariableName()+".");
				}
			}
		};

    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	tasks.add(updateUIBeforeLoadTask);
    	tasks.add(loadTask);
    	tasks.add(afterLoadingSwingTask);
    	return tasks;
	 }

	@Override
	public void aboutToDisplayPanel() {
		postProcessingDataPanel.setDocumentWindowManager(documentWindowManager);
		postProcessingDataPanel.setWizard(getWizard());
	}
    
}
