/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cbit.gui.MultiPurposeTextPanel;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.AsynchClientTaskFunction;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.gui.MathDescEditor;
import cbit.vcell.math.gui.MathDescPanel;

@SuppressWarnings("serial")
public class MathematicsPanel extends JPanel {
	private JPanel mainPanel;
	private JPanel ivjButtonsPanel;
	private JRadioButton ivjViewEqunsRadioButton = null;
	private JRadioButton ivjViewVCMDLRadioButton = null;
	private JButton ivjCreateMathModelButton = null;
	private JButton ivjRefreshMathButton = null;
	private ButtonGroup ivjbuttonGroup = null;
	private MathDescPanel mathDescPanel = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationContext simulationContext = null;
	private MultiPurposeTextPanel ivjVCMLPanel = null;
	private CardLayout cardLayout = new CardLayout();
	protected transient ActionListener actionListener = null;
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, PropertyChangeListener {
			
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getRefreshMathButton())
				refreshMath();
			if (e.getSource() == getCreateMathModelButton()) 
				createMathModel(e);
		}
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == getViewEqunsRadioButton() || e.getSource() == getViewVCMDLRadioButton()) { 
				viewMath_ItemStateChanged(e);
			}
		};
		
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == simulationContext && (evt.getPropertyName().equals("mathDescription"))) 
				refreshVCML();
		}
	}
		
	public MathematicsPanel() {
		initialize();		
	}
	private void initialize() {
		setName("ViewMathPanel");
		setLayout(new java.awt.BorderLayout());
		add(getButtonsPanel(), BorderLayout.NORTH);
		add(getMainPanel(), BorderLayout.CENTER);	
		
		ivjbuttonGroup = new javax.swing.ButtonGroup();
		ivjbuttonGroup.add(getViewEqunsRadioButton());
		ivjbuttonGroup.add(getViewVCMDLRadioButton());
		getViewEqunsRadioButton().addItemListener(ivjEventHandler);
		getViewVCMDLRadioButton().addItemListener(ivjEventHandler);
		getRefreshMathButton().addActionListener(ivjEventHandler);
		getRefreshMathButton().setPreferredSize(getCreateMathModelButton().getPreferredSize());
		getCreateMathModelButton().addActionListener(ivjEventHandler);
		
//		cardLayout.show(getMainPanel(), getVCMLPanel().getName());
	}
	
	private javax.swing.JPanel getButtonsPanel() {
		if (ivjButtonsPanel == null) {
			ivjButtonsPanel = new javax.swing.JPanel();
			ivjButtonsPanel.setName("ButtonsPanel");
			ivjButtonsPanel.setLayout(new BoxLayout(ivjButtonsPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel("Choose View:");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			ivjButtonsPanel.add(label);
			ivjButtonsPanel.add(getViewEqunsRadioButton());
			ivjButtonsPanel.add(getViewVCMDLRadioButton());
			ivjButtonsPanel.add(Box.createHorizontalGlue());
			ivjButtonsPanel.add(getRefreshMathButton());
			ivjButtonsPanel.add(Box.createRigidArea(new Dimension(3,5)));
			ivjButtonsPanel.add(getCreateMathModelButton());
		}
		return ivjButtonsPanel;
	}
	
	/**
	 * Return the CreateMathModelButton property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getCreateMathModelButton() {
		if (ivjCreateMathModelButton == null) {
			ivjCreateMathModelButton = new javax.swing.JButton("Create Math Model");
			ivjCreateMathModelButton.setName("CreateMathModelButton");
		}
		return ivjCreateMathModelButton;
	}
	
	private javax.swing.JButton getRefreshMathButton() {
		if (ivjRefreshMathButton == null) {
			ivjRefreshMathButton = new javax.swing.JButton("Refresh Math");
			ivjRefreshMathButton.setName("RefreshMathButton");
		}
		return ivjRefreshMathButton;
	}
	
	private javax.swing.JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setName("MathViewerPanel");
			mainPanel.setLayout(cardLayout);
			mainPanel.add(getMathDescPanel(), getMathDescPanel().getName());
			mainPanel.add(getVCMLPanel(), getVCMLPanel().getName());
		}
		return mainPanel;
	}
	
	private MultiPurposeTextPanel getVCMLPanel() {
		if (ivjVCMLPanel == null) {
			ivjVCMLPanel = new MultiPurposeTextPanel(false);
			ivjVCMLPanel.setName("VCMLPanel");
			ivjVCMLPanel.setKeywords(MathDescEditor.getkeywords());
		}
		return ivjVCMLPanel;
	}
	
	private MathDescPanel getMathDescPanel() {
		if (mathDescPanel == null) {
			mathDescPanel = new MathDescPanel();
			mathDescPanel.setName("MathDescPanel");
		}
		return mathDescPanel;
	}
	private javax.swing.JRadioButton getViewEqunsRadioButton() {
		if (ivjViewEqunsRadioButton == null) {
			ivjViewEqunsRadioButton = new javax.swing.JRadioButton("Math Equations");
			ivjViewEqunsRadioButton.setName("ViewEqunsRadioButton");
			ivjViewEqunsRadioButton.setSelected(true);
			ivjViewEqunsRadioButton.setActionCommand("MathDescPanel1");
		}
		return ivjViewEqunsRadioButton;
	}
	
	private javax.swing.JRadioButton getViewVCMDLRadioButton() {
		if (ivjViewVCMDLRadioButton == null) {
			ivjViewVCMDLRadioButton = new javax.swing.JRadioButton("Math Description Language");
			ivjViewVCMDLRadioButton.setName("ViewVCMDLRadioButton");
			ivjViewVCMDLRadioButton.setActionCommand("VCMLPanel");
		}
		return ivjViewVCMDLRadioButton;
	}
	
	private void refreshMath() {
		ClientTaskDispatcher.dispatchColl(MathematicsPanel.this, new Hashtable<String, Object>(), ClientRequestManager.updateMath(this, simulationContext, true,NetworkGenerationRequirements.ComputeFullStandardTimeout), false);
	}
	
	private void createMathModel(final ActionEvent e) {
		// relays an action event with this as the source
		Collection<AsynchClientTask> tasks = ClientRequestManager.updateMath(this, simulationContext, true,NetworkGenerationRequirements.ComputeFullStandardTimeout);
		tasks.add(new AsynchClientTaskFunction(ht -> refireActionPerformed(e),"creating math model", AsynchClientTask.TASKTYPE_SWING_BLOCKING) ); 
		ClientTaskDispatcher.dispatchColl(this, new Hashtable<String, Object>(), tasks, false);
	}
	
	public synchronized void addActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}
	
	public synchronized void removeActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

	protected void fireActionPerformed(ActionEvent e) {
		if (actionListener != null) {
			actionListener.actionPerformed(e);
		}         
	}
	
	private void refireActionPerformed(ActionEvent e) {
		// relays an action event with this as the source
		fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
	}
	
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}
	
	public void setSimulationContext(SimulationContext newValue) {
		if (simulationContext != null) {
			simulationContext.removePropertyChangeListener(ivjEventHandler);
		}
		simulationContext = newValue;
		if (simulationContext != null) {
			newValue.addPropertyChangeListener(ivjEventHandler);
		}
		if(simulationContext.getApplicationType().equals(Application.RULE_BASED_STOCHASTIC)) {
			getViewEqunsRadioButton().setEnabled(false);
			if(getViewEqunsRadioButton().isSelected()) {
				getViewEqunsRadioButton().setSelected(false);
				getViewVCMDLRadioButton().setSelected(true);
				cardLayout.show(getMainPanel(), getVCMLPanel().getName());
			}
		} else {
			getViewEqunsRadioButton().setEnabled(true);
		}
		getMathDescPanel().setMathDescription(newValue.getMathDescription());
		refreshVCML();
	}
	
	private void viewMath_ItemStateChanged(ItemEvent itemEvent) {
		if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
			if (itemEvent.getSource() == getViewEqunsRadioButton()) {
				cardLayout.show(getMainPanel(), getMathDescPanel().getName());
				return;
			}else if (itemEvent.getSource() == getViewVCMDLRadioButton()) {
				cardLayout.show(getMainPanel(), getVCMLPanel().getName());
				return;			
			}
		}
	}
	
	private void refreshVCML() {
		if (simulationContext != null && simulationContext.getMathDescription() != null){
			try {
				getVCMLPanel().setText(simulationContext.getMathDescription().getVCML_database());
				getVCMLPanel().setCaretPosition(0);
				getMathDescPanel().setMathDescription(simulationContext.getMathDescription());
			}catch (Exception e){
				e.printStackTrace(System.out);
				getVCMLPanel().setText("error displaying math language: "+e.getMessage());
			}
		}else{
			getVCMLPanel().setText("");
		}
	}
}
