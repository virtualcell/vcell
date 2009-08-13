package org.vcell.wizard;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskStatusSupport;


/**
 * A base descriptor class used to reference a Component panel for the Wizard, as
 * well as provide general rules as to how the panel should behave.
 */
public class WizardPanelDescriptor {
    
    private static final String DEFAULT_PANEL_IDENTIFIER = "defaultPanelIdentifier";
    
    private Wizard wizard;
    private Component targetPanel;
    private String panelIdentifier;
    private String nextDescriptorID = null;
    private String backDescriptorID = null;    
    private ClientTaskStatusSupport taskStatusSupport = null;
    private boolean isTaskKnown = false;
    private boolean isProgressPopupShown = false;
	
	/**
     * Default constructor. 
     */    
    public WizardPanelDescriptor() {
        panelIdentifier = DEFAULT_PANEL_IDENTIFIER;
        targetPanel = new JPanel();
    }
    
    /**
     * Constructor which accepts a identifier and a reference to
     * the Component panel.
     */    
    public WizardPanelDescriptor(String id, Component panel) {
        panelIdentifier = id;
        targetPanel = panel;
    }
   
    /**
     * Get the component panel in a wizard page.
     */    
    public final Component getPanelComponent() {
        return targetPanel;
    }
    
    /**
     * Sets the component panel.
     */    
    public final void setPanelComponent(Component panel) {
        targetPanel = panel;
    }
    
    /**
     * Get the identifier for this panel descriptor.
     */    
    public final String getPanelDescriptorIdentifier() {
        return panelIdentifier;
    }

    /**
     * Sets the identifier for this panel. The identifier must be unique
     * from all the other identifiers in the panel.
     */    
    public final void setPanelDescriptorIdentifier(String id) {
        panelIdentifier = id;
    }
    
    final void setWizard(Wizard w) {
        wizard = w;
    }
    
    /**
     * Returns a reference to the Wizard component.
     */    
    public final Wizard getWizard() {
        return wizard;
    }   

    /**
     * Returns a reference to the current WizardModel for this Wizard component.
     */    
    public WizardModel getWizardModel() {
        return wizard.getModel();
    }
    
    /**
     * The following four methods provide a way to solve dynamic wizard link. 
     * Override these class to provide the Object-based identifier of the panel.
     */    
    public String getNextPanelDescriptorID() {
        return nextDescriptorID;
    }
     
    public String getBackPanelDescriptorID() {
        return backDescriptorID;
    }
    
    public void setNextPanelDescriptorID(String descriptor)
    {
    	nextDescriptorID = descriptor;
    }
    
    public void setBackPanelDescriptorID(String descriptor)
    {
    	backDescriptorID = descriptor;
    }
    
    /**Override the following methods to provide functionality that will be performed just before
     * or after the current panel is disappeared. Any time when cancel/next/back button is clicked, 
     * the action taken is dispatching of a series of ordered Asynchronous client tasks.
     */
    public ArrayList<AsynchClientTask> preCancelProcess()//actions before panel disappears when cancel is clicked
    {
    	return new ArrayList<AsynchClientTask>();
    }
    public ArrayList<AsynchClientTask> postCancelProcess()//actions after panel disappears when cancel is clicked
    {
    	return new ArrayList<AsynchClientTask>();
    }
    public ArrayList<AsynchClientTask> preNextProcess()//actions before panel disappears when next/finish is clicked 
    {
    	return new ArrayList<AsynchClientTask>();
    }
    public ArrayList<AsynchClientTask> postNextProcess()//actions after panel disappears when next/finish is clicked
    {
    	return new ArrayList<AsynchClientTask>();
    }
    public ArrayList<AsynchClientTask> preBackProcess()//actions before panel disappears when back is clicked
    {
    	return new ArrayList<AsynchClientTask>();
    }
    public ArrayList<AsynchClientTask> postBackProcess()//actions after panel disappears when back is clicked
    {
    	return new ArrayList<AsynchClientTask>();
    }
    
    //the following two methods indicate whether the progresses is known or not.
    public boolean isTaskProgressKnown()
    {
    	return isTaskKnown;
    }
    public void setTaskProgressKnown(boolean known)
    {
    	isTaskKnown = known; 
    }
    
    //the following two methods are used to get progress.
    public ClientTaskStatusSupport getTaskStatusSupport() {
		return taskStatusSupport;
	}
	public void setTaskStatusSupport(ClientTaskStatusSupport taskStatusSupport) {
		this.taskStatusSupport = taskStatusSupport;
	}
	
	//the following two methods are used to determine if a progress Pop-up is needed
	public boolean isProgressPopupShown() {
		return isProgressPopupShown;
	}
	public void setProgressPopupShown(boolean isProgressPopupShown) {
		this.isProgressPopupShown = isProgressPopupShown;
	}

	//This method is called before displaying a new panel. No button action is required
	public void aboutToDisplayPanel() {
		//override in child class 
	}

}
