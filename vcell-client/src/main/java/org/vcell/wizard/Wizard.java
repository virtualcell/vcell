package org.vcell.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.client.logicalwindow.LWTitledDialog;
import org.vcell.util.BeanUtils;
import org.vcell.util.GeneralGuiUtils;
import org.vcell.util.gui.DialogUtils;
/**
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved. Use is subject to license terms.
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of Oracle Corporation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
You acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility. 
 */
/**
 * This class implements a basic wizard dialog, where the users can
 * insert one or more Components to act as panels. These panels can be navigated
 * through arbitrarily using the 'Next' or 'Back' buttons, or the dialog itself
 * can be closed using the 'Cancel' button. Note that even though the dialog
 * uses a CardLayout manager, the order of the panels is not fixed. Each panel
 * determines at runtime what its next and previous panel will be.
 */
/**
 * Modified by Tracy Li for VCell usage.
 */
public class Wizard extends WindowAdapter implements PropertyChangeListener {

    /**
     * The following three variables indicate wether the dialog is close by clicking
     * "Finish" or "Cancel" button, or closed by error. 
     */    
    public static final int FINISH_RETURN_CODE = 0;
    public static final int CANCEL_RETURN_CODE = 1;
    public static final int ERROR_RETURN_CODE = 2;
     
    /**
     * The String-based action command for the 'Finish' button.
     */
    public static final String FINISH_BUTTON_ACTION_COMMAND = "FinishButtonActionCommand";
    /**
     * The String-based action command for the 'Next' button.
     */    
    public static final String NEXT_BUTTON_ACTION_COMMAND = "NextButtonActionCommand";
    /**
     * The String-based action command for the 'Back' button.
     */    
    public static final String BACK_BUTTON_ACTION_COMMAND = "BackButtonActionCommand";
    /**
     * The String-based action command for the 'Cancel' button.
     */    
    public static final String CANCEL_BUTTON_ACTION_COMMAND = "CancelButtonActionCommand";
    /**
     * Create a finish descriptor 
     */
    public static final FinishDescriptor FINISH = new FinishDescriptor();
    
    // The following text are used for the buttons. Loaded from a property resource file.    
    
    static String BACK_TEXT = "Back";
    static String NEXT_TEXT = "Next";
    static String FINISH_TEXT = "Finish";
    static String CANCEL_TEXT = "Cancel";
    
    private WizardModel wizardModel;
    private WizardController wizardController;
    private JDialog wizardDialog;
        
    private JPanel cardPanel;
    private CardLayout cardLayout;            
    private JButton backButton;
    private JButton nextButton;
    private JButton cancelButton;
    
    private int returnCode;
    /**
     * Default constructor. This method creates a new WizardModel object and passes it
     * into the constructor.
     */    
//    public Wizard() {
//        this((JFrame)null);
//    }
    
    /**
     * This method accepts a JFrame object as the javax.swing.JDialog's parent.
     */
    public Wizard(Frame owner) {
        wizardModel = new WizardModel();
//      wizardDialog = new JDialog(owner);         
        LWContainerHandle lwParent = LWNamespace.findLWOwner(owner);
        wizardDialog = new LWTitledDialog(lwParent, "Wizard") ;
        initComponents();
    }
    /**
     * Returns an instance of the JDialog that this class created. 
     */    
    public JDialog getDialog() {
        return wizardDialog;
    }
    
    /**
     * Returns the owner of the generated Dialog.
     */    
    public Component getOwner() {
        return wizardDialog.getOwner();
    }
    
    /**
     * Sets the title of the generated javax.swing.JDialog.
     */    
    public void setTitle(String s) {
        wizardDialog.setTitle(s);
    }
    
    /**
     * Returns the current title of the generated dialog.
     */    
    public String getTitle() {
        return wizardDialog.getTitle();
    }
    
    /**
     * Sets the modality of the generated javax.swing.JDialog.
     */    
    public void setModal(boolean b) {
        wizardDialog.setModal(b);
    }
    
    /**
     * Returns the modality of the dialog.
     */    
    public boolean isModal() {
        return wizardDialog.isModal();
    }
    
    /**
     * To display a modal wizard dialog and blocks until the dialog
     * has completed.
     */    
    public int showModalDialog(Dimension dim) {
        
        wizardDialog.setModal(true);
        if(dim == null)
        {
        	wizardDialog.pack();
        }
        else
        {
        	wizardDialog.setSize(dim);
        }
        GeneralGuiUtils.centerOnComponent(wizardDialog, wizardDialog.getParent());
        wizardDialog.setVisible(true);
        
        return returnCode;
    }
    
    /**
     * Returns the current model of the wizard dialog.
     */    
    public WizardModel getModel() {
        return wizardModel;
    }
    
    /**
     * Add a Component as a panel for the wizard dialog by registering its
     * WizardPanelDescriptor. Each panel is identified by a unique identifier (a String).
     */    
    public void registerWizardPanel(String id, WizardPanelDescriptor descriptor) {
        
        //  Add the incoming panel to our JPanel display that is managed by
        //  the CardLayout layout manager.
        cardPanel.add(descriptor.getPanelComponent(), id);
        
        //  Set a callback to the current wizard.
        descriptor.setWizard(this);
        
        //  Place a reference to it in the model. 
        wizardModel.registerPanel(id, descriptor);
        
    }  
    
    /**
     * Clear all the panels in the wizard 
     */  
    public void clearAllPanels()
    {
    	if(cardPanel != null)
    	{
    		cardPanel.removeAll();
    	}
    	if(wizardModel != null)
    	{
    		wizardModel.clearAllPanels();
    	}
    }
    
    
    /**
     * Displays the panel identified by the identifier passed in. This is the same 
     * identifier used when registering the panel.
     */    
    public void setCurrentPanel(String id) {

        if (id == null)
        {
            close(ERROR_RETURN_CODE);
            return;
        }
        try{
        	wizardModel.setCurrentPanel(id);
        	wizardModel.getCurrentPanelDescriptor().aboutToDisplayPanel();
        }catch(WizardPanelNotFoundException e)
        {
        	e.printStackTrace(System.out);
        	DialogUtils.showErrorDialog(wizardDialog, "Wizard panel "+ id +" not found. \n" + e.getMessage());
        }
		
		//show panel
		cardLayout.show(cardPanel, id);
    }
    
    /**
     * This is used to listen for property change events from the model and update the
     * dialog's UI as necessary.
     */    
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (evt.getPropertyName().equals(WizardModel.CURRENT_PANEL_DESCRIPTOR_PROPERTY)) {
            wizardController.resetButtonsToPanelRules(); 
        } else if (evt.getPropertyName().equals(WizardModel.NEXT_FINISH_BUTTON_TEXT_PROPERTY)) {            
            nextButton.setText(evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(WizardModel.BACK_BUTTON_TEXT_PROPERTY)) {            
            backButton.setText(evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(WizardModel.CANCEL_BUTTON_TEXT_PROPERTY)) {            
            cancelButton.setText(evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(WizardModel.NEXT_FINISH_BUTTON_ENABLED_PROPERTY)) {            
            nextButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(WizardModel.BACK_BUTTON_ENABLED_PROPERTY)) {            
            backButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(WizardModel.CANCEL_BUTTON_ENABLED_PROPERTY)) {            
            cancelButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
        } 
    }
    
    /**
     * Return code by the dialog.
     */    
    public int getReturnCode() {
        return returnCode;
    }
    
   /**
     * Return if the back button is enabled.
     */  
    public boolean getBackButtonEnabled() {
        return wizardModel.getBackButtonEnabled();
    }

   /**
     * Set the enabled status of the back button.
     */ 
    public void setBackButtonEnabled(boolean newValue) {
        wizardModel.setBackButtonEnabled(newValue);
    }

   /**
     * Return if the next button is enabled.
     */ 
    public boolean getNextFinishButtonEnabled() {
        return wizardModel.getNextFinishButtonEnabled();
    }

   /**
     * Set the enabled status of the next button.
     */ 
    public void setNextFinishButtonEnabled(boolean newValue) {
        wizardModel.setNextFinishButtonEnabled(newValue);
    }
 
   /**
     * Return if the cancel button is enabled.
     */ 
    public boolean getCancelButtonEnabled() {
        return wizardModel.getCancelButtonEnabled();
    }

    /**
     * Set the enabled status of the cancel button.
     */ 
    public void setCancelButtonEnabled(boolean newValue) {
        wizardModel.setCancelButtonEnabled(newValue);
    }
    
    /**
     * Closes the dialog and sets the return code to the integer parameter.
     */    
    void close(int code) {
    	//the return code indicates from which action the dialog is closed.
    	//code which concerning return code can be written below.
        returnCode = code;
        wizardDialog.setVisible(false);
    }
    
    /**
     * This method initializes the components for the wizard dialog: it creates a JDialog
     * as a CardLayout panel , as well as three buttons at the bottom.
     */
    
    private void initComponents() {

        wizardModel.addPropertyChangeListener(this);       
        wizardController = new WizardController(this);       

        wizardDialog.getContentPane().setLayout(new BorderLayout());
        wizardDialog.addWindowListener(this);
                
        //  Create the outer wizard panel, which is responsible for three buttons:
        //  Next, Back, and Cancel. It is also responsible a JPanel above them that
        //  uses a CardLayout layout manager to display multiple panels in the 
        //  same spot.
        
        JPanel buttonPanel = new JPanel();
        JSeparator separator = new JSeparator();
        Box buttonBox = new Box(BoxLayout.X_AXIS);

        cardPanel = new JPanel();
        cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));       

        cardLayout = new CardLayout(); 
        cardPanel.setLayout(cardLayout);
        
        backButton = new JButton();
        nextButton = new JButton();
        cancelButton = new JButton();
        
        backButton.setActionCommand(BACK_BUTTON_ACTION_COMMAND);
        nextButton.setActionCommand(NEXT_BUTTON_ACTION_COMMAND);
        cancelButton.setActionCommand(CANCEL_BUTTON_ACTION_COMMAND);

        backButton.addActionListener(wizardController);
        nextButton.addActionListener(wizardController);
        cancelButton.addActionListener(wizardController);
        
        //  Create the buttons with a separator above them, then put them
        //  on the east side of the panel with a small amount of space between
        //  the back and the next button, and a larger amount of space between
        //  the next button and the cancel button.
        
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(separator, BorderLayout.NORTH);

        buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));       
        buttonBox.add(backButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(nextButton);
        buttonBox.add(Box.createHorizontalStrut(30));
        buttonBox.add(cancelButton);
        
        buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);
        
        wizardDialog.getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);
        wizardDialog.getContentPane().add(cardPanel, java.awt.BorderLayout.CENTER);

    }
    
   /**
     * If the user presses the close box on the dialog's window, treat it
     * as a cancel.
     */ 
    
    public void windowClosing(WindowEvent e) {
        returnCode = CANCEL_RETURN_CODE;
    }
}
