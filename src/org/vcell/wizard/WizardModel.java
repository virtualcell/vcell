package org.vcell.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

/**
 * The model for the Wizard component, which tracks the text, and enabled state
 * of each of the buttons, as well as the current panel that is displayed. 
 */


public class WizardModel {

    //Identification string for the current panel.
    public static final String CURRENT_PANEL_DESCRIPTOR_PROPERTY = "currentPanelDescriptorProperty";
    
    //Property identification String for the Back button's text
    public static final String BACK_BUTTON_TEXT_PROPERTY = "backButtonTextProperty";
    
    //Property identification String for the Back button's enabled state
    public static final String BACK_BUTTON_ENABLED_PROPERTY = "backButtonEnabledProperty";

    //Property identification String for the Next button's text
    public static final String NEXT_FINISH_BUTTON_TEXT_PROPERTY = "nextButtonTextProperty";
    
    //Property identification String for the Next button's enabled state
    public static final String NEXT_FINISH_BUTTON_ENABLED_PROPERTY = "nextButtonEnabledProperty";
    
    //Property identification String for the Cancel button's text
    public static final String CANCEL_BUTTON_TEXT_PROPERTY = "cancelButtonTextProperty";
    
    //Property identification String for the Cancel button's enabled state
    public static final String CANCEL_BUTTON_ENABLED_PROPERTY = "cancelButtonEnabledProperty";
    
    private WizardPanelDescriptor currentPanelDescriptor;
    
    private HashMap<String, WizardPanelDescriptor> panelHashmap;//stores wizardDescriptors
    private HashMap<String, String> buttonTextHashmap;
    private HashMap<String, Boolean> buttonEnabledHashmap;
    
    private PropertyChangeSupport propertyChangeSupport;
    
    public WizardModel() {
        
        panelHashmap = new HashMap<String, WizardPanelDescriptor>();
        
        buttonTextHashmap = new HashMap<String, String>();
        buttonEnabledHashmap = new HashMap<String, Boolean>();
        
        propertyChangeSupport = new PropertyChangeSupport(this);

    }
    
    /**
     * Returns the currently displayed WizardPanelDescriptor.
     */    
    public WizardPanelDescriptor getCurrentPanelDescriptor() {
        return currentPanelDescriptor;
    }
    
    /**
     * Registers the WizardPanelDescriptor in the model using the Object-identifier specified.
     */    
    public void registerPanel(String id, WizardPanelDescriptor descriptor) {
        //  Place a reference to it in a hashtable so we can access it later
        //  when it is about to be displayed.
        panelHashmap.put(id, descriptor);
    }  
    
    /**
     * Sets the current panel to the Object that is passed in.
     */    
    public boolean setCurrentPanel(String id) throws WizardPanelNotFoundException {

        //  First, get the hashtable reference to the panel that should
        //  be displayed.
        WizardPanelDescriptor nextPanelDescriptor =
            (WizardPanelDescriptor)panelHashmap.get(id);
        
        //  If we couldn't find the panel that should be displayed, return
        //  false.
        
        if (nextPanelDescriptor == null)
        {
            throw new WizardPanelNotFoundException();   
        }
        WizardPanelDescriptor oldPanel = currentPanelDescriptor;
        currentPanelDescriptor = nextPanelDescriptor;
        
        if (oldPanel != currentPanelDescriptor)
            firePropertyChange(CURRENT_PANEL_DESCRIPTOR_PROPERTY, oldPanel, currentPanelDescriptor);
        
        return true;
    }

    String getBackButtonText() {
        return buttonTextHashmap.get(BACK_BUTTON_TEXT_PROPERTY);
    }
    
    void setBackButtonText(String newText) {
        
        String oldText = getBackButtonText();        
        if (!newText.equals(oldText)) {
            buttonTextHashmap.put(BACK_BUTTON_TEXT_PROPERTY, newText);
            firePropertyChange(BACK_BUTTON_TEXT_PROPERTY, oldText, newText);
        }
    }

    String getNextFinishButtonText() {
        return buttonTextHashmap.get(NEXT_FINISH_BUTTON_TEXT_PROPERTY);
    }
    
    void setNextFinishButtonText(String newText) {
        
        String oldText = getNextFinishButtonText();        
        if (!newText.equals(oldText)) {
            buttonTextHashmap.put(NEXT_FINISH_BUTTON_TEXT_PROPERTY, newText);
            firePropertyChange(NEXT_FINISH_BUTTON_TEXT_PROPERTY, oldText, newText);
        }
    }

    String getCancelButtonText() {
        return buttonTextHashmap.get(CANCEL_BUTTON_TEXT_PROPERTY);
    }
    
    void setCancelButtonText(String newText) {
        
        String oldText = getCancelButtonText();        
        if (!newText.equals(oldText)) {
            buttonTextHashmap.put(CANCEL_BUTTON_TEXT_PROPERTY, newText);
            firePropertyChange(CANCEL_BUTTON_TEXT_PROPERTY, oldText, newText);
        }
    } 
    
    Boolean getBackButtonEnabled() {
        return (Boolean)buttonEnabledHashmap.get(BACK_BUTTON_ENABLED_PROPERTY);
    }
    
    void setBackButtonEnabled(Boolean newValue) {
        Boolean oldValue = getBackButtonEnabled();        
        if (newValue != oldValue) {
            buttonEnabledHashmap.put(BACK_BUTTON_ENABLED_PROPERTY, newValue);
            firePropertyChange(BACK_BUTTON_ENABLED_PROPERTY, oldValue, newValue);
        }
    }

    Boolean getNextFinishButtonEnabled() {
        return (Boolean)buttonEnabledHashmap.get(NEXT_FINISH_BUTTON_ENABLED_PROPERTY);
    }
    
    void setNextFinishButtonEnabled(Boolean newValue) {
        
        Boolean oldValue = getNextFinishButtonEnabled();        
        if (newValue != oldValue) {
            buttonEnabledHashmap.put(NEXT_FINISH_BUTTON_ENABLED_PROPERTY, newValue);
            firePropertyChange(NEXT_FINISH_BUTTON_ENABLED_PROPERTY, oldValue, newValue);
        }
    }
    
    Boolean getCancelButtonEnabled() {
        return (Boolean)buttonEnabledHashmap.get(CANCEL_BUTTON_ENABLED_PROPERTY);
    }
    
    void setCancelButtonEnabled(Boolean newValue) {
        
        Boolean oldValue = getCancelButtonEnabled();        
        if (newValue != oldValue) {
            buttonEnabledHashmap.put(CANCEL_BUTTON_ENABLED_PROPERTY, newValue);
            firePropertyChange(CANCEL_BUTTON_ENABLED_PROPERTY, oldValue, newValue);
        }
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
    
}
