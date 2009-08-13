package cbit.vcell.microscopy.gui.loaddatawizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class LoadFRAPData_SummaryDescriptor extends WizardPanelDescriptor implements PropertyChangeListener{
    
    public static final String IDENTIFIER = "LoadFRAPData_Summary";
    private PropertyChangeSupport propertyChangeSupport;
    
    public LoadFRAPData_SummaryDescriptor() {
        super(IDENTIFIER, new LoadFRAPData_SummaryPanel());
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    //this method is override to make sure the next step is going to finish.
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
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
    
	public void propertyChange(PropertyChangeEvent evt) {
		
		if(evt.getPropertyName().equals(FRAPStudyPanel.LOADDATA_FRAPSTUDY_CHANGE_PROPERTY))
		{
			if(evt.getNewValue() != null && evt.getNewValue() instanceof FRAPStudy)
			{
				((LoadFRAPData_SummaryPanel)getPanelComponent()).setLoadInfo((FRAPStudy)evt.getNewValue());
			}
		}
		
	}
	//save the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask verifyLoadedDataTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				String msg = ((LoadFRAPData_SummaryPanel)getPanelComponent()).checkInputValidity();
				if(msg.equals(""))
				{
					DataVerifyInfo dataVerifyInfo= ((LoadFRAPData_SummaryPanel)getPanelComponent()).saveDataInfo();
					if(dataVerifyInfo != null)
					{
						firePropertyChange(FRAPStudyPanel.LOADDATA_VERIFY_INFO_PROPERTY, null, dataVerifyInfo);
					}
				}
				else throw new Exception(msg);
			}
		};
		
		taskArrayList.add(verifyLoadedDataTask);
		return taskArrayList;
    }
}