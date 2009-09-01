package cbit.vcell.microscopy.gui.defineROIwizard;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JPanel;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryPanel;

public class DefineROI_SummaryDescriptor extends WizardPanelDescriptor implements PropertyChangeListener{
	public static final String IDENTIFIER = "DefineROI_Summary";
	private JPanel imgPanel = null;
	private PropertyChangeSupport propertyChangeSupport;
	
    public DefineROI_SummaryDescriptor (JPanel imagePanel) {
        super(IDENTIFIER, new DefineROI_SummaryPanel());
        imgPanel = imagePanel;
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
        propertyChangeSupport = new PropertyChangeSupport(this);
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
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    
    public String getBackPanelDescriptorID() {
        return DefineROI_BackgroundROIDescriptor.IDENTIFIER;
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(FRAPStudyPanel.DEFINEROI_CHANGE_PROPERTY))
		{
			if(evt.getNewValue() != null && evt.getNewValue() instanceof FRAPStudy)
			{
				((DefineROI_SummaryPanel)getPanelComponent()).setLoadInfo((FRAPStudy)evt.getNewValue());
			}
		}
		
	}
	
	public ArrayList<AsynchClientTask> preBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		final String nextROIStr = FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name();
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it goes next to
				((DefineROI_Panel)imgPanel).setCurrentROI(nextROIStr);
			}
		};
		taskArrayList.add(setCurrentROITask);
		return taskArrayList;
    } 
	
	//save the startingIndex before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask verifyLoadedDataTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				String msg = ((DefineROI_SummaryPanel)getPanelComponent()).checkInputValidity();
				if(msg.equals(""))
				{
					int startIndex = ((DefineROI_SummaryPanel)getPanelComponent()).getStartingIndex();
					firePropertyChange(FRAPStudyPanel.DEFINEROI_VERIFY_INFO_PROPERTY, null, new Integer(startIndex));
				}
				else throw new Exception(msg);
			}
		};
		
		taskArrayList.add(verifyLoadedDataTask);
		return taskArrayList;
    }
	
}
