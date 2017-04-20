/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.vcell.util.BeanUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.SimpleReaction;
/**
 * Insert the type's description here.
 * Creation date: (7/24/2002 2:30:19 PM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class SimpleReactionPanel extends javax.swing.JPanel {
	private SimpleReaction fieldSimpleReaction = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimpleReaction ivjTornOffSimpleReaction = null;
	private KineticsTypeTemplatePanel ivjKineticsTypeTemplatePanel = null;
	private ReactionCanvas ivjReactionCanvas = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjSimpleReactionNameTextField = null;
	private javax.swing.JLabel ivjStoichiometryLabel = null;
	private javax.swing.JScrollPane ivjReactionScrollPane = null;
	private ReactionElectricalPropertiesPanel ivjReactionElectricalPropertiesPanel1 = null;

	private boolean ivjConnPtoP2Aligning = false;
	private JTextArea annotationTextArea = null;
	private boolean bSubset = false;

	private class IvjEventHandler implements java.beans.PropertyChangeListener, FocusListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimpleReactionPanel.this && (evt.getPropertyName().equals("simpleReaction"))) {
				connPtoP1SetTarget();
				refreshAnnotationTextField();
				refreshNameTextField();
			}
			if (evt.getSource() == SimpleReactionPanel.this.getTornOffSimpleReaction() && (evt.getPropertyName().equals("name"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == SimpleReactionPanel.this.getSimpleReactionNameTextField() && (evt.getPropertyName().equals("text"))) 
				connPtoP2SetSource();
		}
		public void focusGained(FocusEvent e) {
			
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				getSimpleReaction().getModel().getVcMetaData().setFreeTextAnnotation(getSimpleReaction(), annotationTextArea.getText());
			} else if (e.getSource() == ivjSimpleReactionNameTextField) {
				try {
					getSimpleReaction().setName(ivjSimpleReactionNameTextField.getText());
				} catch (PropertyVetoException e1) {
					PopupGenerator.showErrorDialog(SimpleReactionPanel.this, "Error changing name:\n"+e1.getMessage());
				}
			}
		}		
	};

/**
 * SimpleReactionPanel constructor comment.
 */
	public SimpleReactionPanel() {
		this(false, true);
	}

	public SimpleReactionPanel(boolean pSubset, boolean editable) {
		super();
		bSubset = pSubset;
		initialize();		// only draw a subset of the panel
	}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 10:09:40 PM)
 */
public void cleanupOnClose() {
	getKineticsTypeTemplatePanel().cleanupOnClose();
}

/**
 * connPtoP1SetSource:  (SimpleReactionPanel.simpleReaction <--> TornOffSimpleReaction.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getTornOffSimpleReaction() != null)) {
				this.setSimpleReaction(getTornOffSimpleReaction());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (SimpleReactionPanel.simpleReaction <--> TornOffSimpleReaction.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setTornOffSimpleReaction(this.getSimpleReaction());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (TornOffSimpleReaction.name <--> SimpleReactionNameLabel.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getTornOffSimpleReaction() != null)) {
				getTornOffSimpleReaction().setName(getSimpleReactionNameTextField().getText());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (TornOffSimpleReaction.name <--> SimpleReactionNameLabel.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if (!bSubset && (getTornOffSimpleReaction() != null)) {
				getSimpleReactionNameTextField().setText(getTornOffSimpleReaction().getName());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public boolean getElectricalPropertiesVisible(SimpleReaction sr) {
	if (sr==null){
		return true;
	}else if (sr.getStructure() instanceof Membrane){
		return true;
	}
	return false;
}

/**
 * Return the KineticsTypeTemplatePanel property value.
 * @return cbit.vcell.model.gui.KineticsTypeTemplatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private KineticsTypeTemplatePanel getKineticsTypeTemplatePanel() {
	if (ivjKineticsTypeTemplatePanel == null) {
		try {
			ivjKineticsTypeTemplatePanel = new KineticsTypeTemplatePanel();
			ivjKineticsTypeTemplatePanel.setName("KineticsTypeTemplatePanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKineticsTypeTemplatePanel;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(ivjNameLabel.getFont().deriveFont(Font.BOLD));
//			ivjNameLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjNameLabel.setText("Name");
			ivjNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}

/**
 * Return the ReactionCanvas property value.
 * @return cbit.vcell.model.gui.ReactionCanvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionCanvas getReactionCanvas() {
	if (ivjReactionCanvas == null) {
		try {
			ivjReactionCanvas = new ReactionCanvas();
			ivjReactionCanvas.setName("ReactionCanvas");
//			ivjReactionCanvas.setBounds(0, 0, 359, 117);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionCanvas;
}

/**
 * Return the ReactionElectricalPropertiesPanel1 property value.
 * @return cbit.vcell.model.gui.ReactionElectricalPropertiesPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionElectricalPropertiesPanel getReactionElectricalPropertiesPanel1() {
	if (ivjReactionElectricalPropertiesPanel1 == null) {
		try {
			ivjReactionElectricalPropertiesPanel1 = new ReactionElectricalPropertiesPanel();
			ivjReactionElectricalPropertiesPanel1.setName("ReactionElectricalPropertiesPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionElectricalPropertiesPanel1;
}


/**
 * Return the ReactionScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getReactionScrollPane() {
	if (ivjReactionScrollPane == null) {
		try {
			ivjReactionScrollPane = new javax.swing.JScrollPane();
			ivjReactionScrollPane.setName("ReactionScrollPane");
			getReactionScrollPane().setViewportView(getReactionCanvas());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionScrollPane;
}

/**
 * Gets the simpleReaction property (cbit.vcell.model.SimpleReaction) value.
 * @return The simpleReaction property value.
 * @see #setSimpleReaction
 */
public SimpleReaction getSimpleReaction() {
	return fieldSimpleReaction;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSimpleReactionNameTextField() {
	if (ivjSimpleReactionNameTextField == null) {
		try {
			ivjSimpleReactionNameTextField = new javax.swing.JTextField();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimpleReactionNameTextField;
}

/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStoichiometryLabel() {
	if (ivjStoichiometryLabel == null) {
		try {
			ivjStoichiometryLabel = new javax.swing.JLabel();
			ivjStoichiometryLabel.setName("StoichiometryLabel");
			ivjStoichiometryLabel.setFont(ivjStoichiometryLabel.getFont().deriveFont(Font.BOLD));
			ivjStoichiometryLabel.setText("Stoichiometry");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStoichiometryLabel;
}

/**
 * Return the TornOffSimpleReaction property value.
 * @return cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimpleReaction getTornOffSimpleReaction() {
	// user code begin {1}
	// user code end
	return ivjTornOffSimpleReaction;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	
	if (annotationTextArea != null) {
		annotationTextArea.addFocusListener(ivjEventHandler);
	}
	getSimpleReactionNameTextField().addFocusListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("SimpleReactionPanel");
		setLayout(new java.awt.GridBagLayout());

		int gridy = 0;
		if(!bSubset) {	// draw full table
			// stoichiometry
			java.awt.GridBagConstraints constraintsStoichiometryLabel = new java.awt.GridBagConstraints();
			constraintsStoichiometryLabel.gridx = 0; constraintsStoichiometryLabel.gridy = gridy;
			constraintsStoichiometryLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsStoichiometryLabel.insets = new java.awt.Insets(4, 10, 4, 0);
			add(getStoichiometryLabel(), constraintsStoichiometryLabel);
	
			java.awt.GridBagConstraints constraintsReactionScrollPane = new java.awt.GridBagConstraints();
			constraintsReactionScrollPane.gridx = 1; constraintsReactionScrollPane.gridy = gridy;
			constraintsReactionScrollPane.gridwidth = 2;
			constraintsReactionScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReactionScrollPane.weightx = 1.0;
			constraintsReactionScrollPane.weighty = 0.5;
			constraintsReactionScrollPane.insets = new java.awt.Insets(4, 4, 4, 10);
			add(getReactionScrollPane(), constraintsReactionScrollPane);
			gridy ++;
	
			// Name
			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = gridy;
			constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsNameLabel.insets = new java.awt.Insets(4, 10, 4, 4);
			add(getNameLabel(), constraintsNameLabel);
	
			java.awt.GridBagConstraints constraintsSimpleReactionNameLabel = new java.awt.GridBagConstraints();
			constraintsSimpleReactionNameLabel.gridx = 1; constraintsSimpleReactionNameLabel.gridy = gridy;
			constraintsSimpleReactionNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSimpleReactionNameLabel.weightx = 1.0;
			constraintsSimpleReactionNameLabel.insets = new java.awt.Insets(4, 5, 4, 5);
			add(getSimpleReactionNameTextField(), constraintsSimpleReactionNameLabel);
			gridy ++;
				
			java.awt.GridBagConstraints constraintsReactionElectricalPropertiesPanel1 = new java.awt.GridBagConstraints();
			constraintsReactionElectricalPropertiesPanel1.gridx = 0; constraintsReactionElectricalPropertiesPanel1.gridy = gridy;
			constraintsReactionElectricalPropertiesPanel1.gridwidth = 3;
			constraintsReactionElectricalPropertiesPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReactionElectricalPropertiesPanel1.weightx = 1.0;
			constraintsReactionElectricalPropertiesPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getReactionElectricalPropertiesPanel1(), constraintsReactionElectricalPropertiesPanel1);
			gridy ++;
			
			// Annotation
			GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = gridy;
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.NORTHEAST;
			JLabel label = new JLabel("Annotation");
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			add(label, gbc);
			
			annotationTextArea = new javax.swing.JTextArea();
			annotationTextArea.setLineWrap(true);
			annotationTextArea.setWrapStyleWord(true);
			javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 1; gbc.gridy = gridy;
			gbc.gridwidth = 2;
			gbc.weightx = 1.0;
			gbc.weighty = 0.1;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.insets = new java.awt.Insets(5, 4, 5, 10);
			add(jsp, gbc);
			gridy ++;
		}
		
		gridy ++;		
		// Kinetic Parameters
		java.awt.GridBagConstraints constraintsKineticsTypeTemplatePanel = new java.awt.GridBagConstraints();
		constraintsKineticsTypeTemplatePanel.gridx = 0; constraintsKineticsTypeTemplatePanel.gridy = gridy;
		constraintsKineticsTypeTemplatePanel.gridwidth = 3;
		constraintsKineticsTypeTemplatePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsKineticsTypeTemplatePanel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsKineticsTypeTemplatePanel.weightx = 1.0;
		constraintsKineticsTypeTemplatePanel.weighty = 1.0;
		constraintsKineticsTypeTemplatePanel.insets = new java.awt.Insets(5, 10, 5, 10);
		add(getKineticsTypeTemplatePanel(), constraintsKineticsTypeTemplatePanel);
		if (bSubset) {
			BeanUtils.enableComponents(getKineticsTypeTemplatePanel(), false);
		}
		gridy ++;

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleReactionPanel aSimpleReactionPanel;
		aSimpleReactionPanel = new SimpleReactionPanel();
		frame.setContentPane(aSimpleReactionPanel);
		frame.setSize(aSimpleReactionPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

/**
 * Comment
 */
public void setChargeCarrierValence_Exception(java.lang.Throwable e) {
	if (e instanceof NumberFormatException){
		javax.swing.JOptionPane.showMessageDialog(this, "Number format error '"+e.getMessage()+"'", "Error changing charge valence", javax.swing.JOptionPane.ERROR_MESSAGE);
	}else{
		javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error changing charge valence", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
}

/**
 * Sets the simpleReaction property (cbit.vcell.model.SimpleReaction) value.
 * @param simpleReaction The new value for the property.
 * @see #getSimpleReaction
 */
public void setSimpleReaction(SimpleReaction simpleReaction) {
	SimpleReaction oldValue = fieldSimpleReaction;
	fieldSimpleReaction = simpleReaction;
	firePropertyChange("simpleReaction", oldValue, simpleReaction);
}


/**
 * Set the TornOffSimpleReaction to a new value.
 * @param newValue cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTornOffSimpleReaction(SimpleReaction newValue) {
	if (ivjTornOffSimpleReaction != newValue) {
		try {
			SimpleReaction oldValue = getTornOffSimpleReaction();
			/* Stop listening for events from the current object */
			if (ivjTornOffSimpleReaction != null) {
				ivjTornOffSimpleReaction.removePropertyChangeListener(ivjEventHandler);
			}
			ivjTornOffSimpleReaction = newValue;

			/* Listen for events from the new object */
			if (ivjTornOffSimpleReaction != null) {
				ivjTornOffSimpleReaction.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();			
			
			getReactionCanvas().setReactionStep(ivjTornOffSimpleReaction);
			if (ivjTornOffSimpleReaction != null) {
				getSimpleReactionNameTextField().setText(getTornOffSimpleReaction().getName());
			}			
			getReactionElectricalPropertiesPanel1().setKinetics(ivjTornOffSimpleReaction.getKinetics());
			boolean electricalPropertiesVisible = ivjTornOffSimpleReaction != null && ivjTornOffSimpleReaction.getStructure() instanceof Membrane;			
			getReactionElectricalPropertiesPanel1().setVisible(electricalPropertiesVisible);
			
			getKineticsTypeTemplatePanel().setReactionStep(getSimpleReaction());
			
			firePropertyChange("simpleReaction", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * This method initializes jToggleButton	
 * 	
 * @return javax.swing.JButton	
 */
private void refreshAnnotationTextField() {
	if(bSubset) {
		return;
	}
	annotationTextArea.setText(getSimpleReaction().getModel().getVcMetaData().getFreeTextAnnotation(getSimpleReaction()));
	annotationTextArea.setCaretPosition(0);
}


private void refreshNameTextField() {
	if(bSubset) {
		return;
	}
	getSimpleReactionNameTextField().setText(getSimpleReaction().getName());	
}

}
