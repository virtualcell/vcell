package org.vcell.wizard;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.vcell.util.ClientTaskStatusSupport;

import cbit.vcell.client.task.AsynchClientTask;

/**
 * Copyright � 2008, 2010 Oracle and/or its affiliates. All rights reserved. Use is subject to license terms.
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of Oracle Corporation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
You acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility. 
 */
/**
 * A base descriptor class used to reference a Component panel for the Wizard, as
 * well as provide general rules as to how the panel should behave.
 */
/**
 * Modified by Tracy Li for VCell usage.
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
