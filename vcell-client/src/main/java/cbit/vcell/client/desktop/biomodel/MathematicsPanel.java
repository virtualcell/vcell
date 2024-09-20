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
    private final IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private SimulationContext simulationContext = null;
    private MultiPurposeTextPanel ivjVCMLPanel = null;
    private final CardLayout cardLayout = new CardLayout();
    protected transient ActionListener actionListener = null;

    private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, PropertyChangeListener {

        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == MathematicsPanel.this.getRefreshMathButton())
                MathematicsPanel.this.refreshMath();
            if (e.getSource() == MathematicsPanel.this.getCreateMathModelButton())
                MathematicsPanel.this.createMathModel(e);
        }

        public void itemStateChanged(java.awt.event.ItemEvent e) {
            if (e.getSource() == MathematicsPanel.this.getViewEqunsRadioButton() || e.getSource() == MathematicsPanel.this.getViewVCMDLRadioButton()) {
                MathematicsPanel.this.viewMath_ItemStateChanged(e);
            }
        }

        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getSource() == MathematicsPanel.this.simulationContext && (evt.getPropertyName().equals("mathDescription")))
                MathematicsPanel.this.refreshVCML();
        }
    }

    public MathematicsPanel() {
        this.initialize();
    }

    private void initialize() {
        this.setName("ViewMathPanel");
        this.setLayout(new java.awt.BorderLayout());
        this.add(this.getButtonsPanel(), BorderLayout.NORTH);
        this.add(this.getMainPanel(), BorderLayout.CENTER);

        this.ivjbuttonGroup = new javax.swing.ButtonGroup();
        this.ivjbuttonGroup.add(this.getViewEqunsRadioButton());
        this.ivjbuttonGroup.add(this.getViewVCMDLRadioButton());
        this.getViewEqunsRadioButton().addItemListener(this.ivjEventHandler);
        this.getViewVCMDLRadioButton().addItemListener(this.ivjEventHandler);
        this.getRefreshMathButton().addActionListener(this.ivjEventHandler);
        this.getRefreshMathButton().setPreferredSize(this.getCreateMathModelButton().getPreferredSize());
        this.getCreateMathModelButton().addActionListener(this.ivjEventHandler);

//		cardLayout.show(getMainPanel(), getVCMLPanel().getName());
    }

    private javax.swing.JPanel getButtonsPanel() {
        if (this.ivjButtonsPanel == null) {
            this.ivjButtonsPanel = new javax.swing.JPanel();
            this.ivjButtonsPanel.setName("ButtonsPanel");
            this.ivjButtonsPanel.setLayout(new BoxLayout(this.ivjButtonsPanel, BoxLayout.X_AXIS));
            JLabel label = new JLabel("Choose View:");
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            this.ivjButtonsPanel.add(label);
            this.ivjButtonsPanel.add(this.getViewEqunsRadioButton());
            this.ivjButtonsPanel.add(this.getViewVCMDLRadioButton());
            this.ivjButtonsPanel.add(Box.createHorizontalGlue());
            this.ivjButtonsPanel.add(this.getRefreshMathButton());
            this.ivjButtonsPanel.add(Box.createRigidArea(new Dimension(3, 5)));
            this.ivjButtonsPanel.add(this.getCreateMathModelButton());
        }
        return this.ivjButtonsPanel;
    }

    /**
     * Return the CreateMathModelButton property value.
     *
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCreateMathModelButton() {
        if (this.ivjCreateMathModelButton == null) {
            this.ivjCreateMathModelButton = new javax.swing.JButton("Create Math Model");
            this.ivjCreateMathModelButton.setName("CreateMathModelButton");
        }
        return this.ivjCreateMathModelButton;
    }

    private javax.swing.JButton getRefreshMathButton() {
        if (this.ivjRefreshMathButton == null) {
            this.ivjRefreshMathButton = new javax.swing.JButton("Refresh Math");
            this.ivjRefreshMathButton.setName("RefreshMathButton");
        }
        return this.ivjRefreshMathButton;
    }

    private javax.swing.JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new javax.swing.JPanel();
            this.mainPanel.setName("MathViewerPanel");
            this.mainPanel.setLayout(this.cardLayout);
            this.mainPanel.add(this.getMathDescPanel(), this.getMathDescPanel().getName());
            this.mainPanel.add(this.getVCMLPanel(), this.getVCMLPanel().getName());
        }
        return this.mainPanel;
    }

    private MultiPurposeTextPanel getVCMLPanel() {
        if (this.ivjVCMLPanel == null) {
            this.ivjVCMLPanel = new MultiPurposeTextPanel(false);
            this.ivjVCMLPanel.setName("VCMLPanel");
            this.ivjVCMLPanel.setKeywords(MathDescEditor.getkeywords());
        }
        return this.ivjVCMLPanel;
    }

    private MathDescPanel getMathDescPanel() {
        if (this.mathDescPanel == null) {
            this.mathDescPanel = new MathDescPanel();
            this.mathDescPanel.setName("MathDescPanel");
        }
        return this.mathDescPanel;
    }

    private javax.swing.JRadioButton getViewEqunsRadioButton() {
        if (this.ivjViewEqunsRadioButton == null) {
            this.ivjViewEqunsRadioButton = new javax.swing.JRadioButton("Math Equations");
            this.ivjViewEqunsRadioButton.setName("ViewEqunsRadioButton");
            this.ivjViewEqunsRadioButton.setSelected(true);
            this.ivjViewEqunsRadioButton.setActionCommand("MathDescPanel1");
        }
        return this.ivjViewEqunsRadioButton;
    }

    private javax.swing.JRadioButton getViewVCMDLRadioButton() {
        if (this.ivjViewVCMDLRadioButton == null) {
            this.ivjViewVCMDLRadioButton = new javax.swing.JRadioButton("Math Description Language");
            this.ivjViewVCMDLRadioButton.setName("ViewVCMDLRadioButton");
            this.ivjViewVCMDLRadioButton.setActionCommand("VCMLPanel");
        }
        return this.ivjViewVCMDLRadioButton;
    }

    private void refreshMath() {
        ClientTaskDispatcher.dispatchColl(MathematicsPanel.this, new Hashtable<String, Object>(), ClientRequestManager.updateMath(this, this.simulationContext, true, NetworkGenerationRequirements.ComputeFullStandardTimeout), false);
    }

    private void createMathModel(final ActionEvent e) {
        // relays an action event with this as the source
        Collection<AsynchClientTask> tasks = ClientRequestManager.updateMath(this, this.simulationContext, true, NetworkGenerationRequirements.ComputeFullStandardTimeout);
        tasks.add(new AsynchClientTaskFunction(ht -> this.refireActionPerformed(e), "creating math model", AsynchClientTask.TASKTYPE_SWING_BLOCKING));
        ClientTaskDispatcher.dispatchColl(this, new Hashtable<String, Object>(), tasks, false);
    }

    public synchronized void addActionListener(ActionListener l) {
        this.actionListener = AWTEventMulticaster.add(this.actionListener, l);
    }

    public synchronized void removeActionListener(ActionListener l) {
        this.actionListener = AWTEventMulticaster.remove(this.actionListener, l);
    }

    protected void fireActionPerformed(ActionEvent e) {
        if (this.actionListener != null) {
            this.actionListener.actionPerformed(e);
        }
    }

    private void refireActionPerformed(ActionEvent e) {
        // relays an action event with this as the source
        this.fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
    }

    public SimulationContext getSimulationContext() {
        return this.simulationContext;
    }

    public void setSimulationContext(SimulationContext newValue) {
        if (this.simulationContext != null) {
            this.simulationContext.removePropertyChangeListener(this.ivjEventHandler);
        }
        this.simulationContext = newValue;
        if (this.simulationContext != null) {
            newValue.addPropertyChangeListener(this.ivjEventHandler);
        }
        if (this.simulationContext.getApplicationType().equals(Application.RULE_BASED_STOCHASTIC)) {
            this.getViewEqunsRadioButton().setEnabled(false);
            if (this.getViewEqunsRadioButton().isSelected()) {
                this.getViewEqunsRadioButton().setSelected(false);
                this.getViewVCMDLRadioButton().setSelected(true);
                this.cardLayout.show(this.getMainPanel(), this.getVCMLPanel().getName());
            }
        } else {
            this.getViewEqunsRadioButton().setEnabled(true);
        }
        this.getMathDescPanel().setMathDescription(newValue.getMathDescription());
        this.refreshVCML();
    }

    private void viewMath_ItemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            if (itemEvent.getSource() == this.getViewEqunsRadioButton()) {
                this.cardLayout.show(this.getMainPanel(), this.getMathDescPanel().getName());
            } else if (itemEvent.getSource() == this.getViewVCMDLRadioButton()) {
                this.cardLayout.show(this.getMainPanel(), this.getVCMLPanel().getName());
            }
        }
    }

    private void refreshVCML() {
        if (this.simulationContext != null && this.simulationContext.getMathDescription() != null) {
            try {
                this.getVCMLPanel().setText(this.simulationContext.getMathDescription().getVCML_database());
                this.getVCMLPanel().setCaretPosition(0);
                this.getMathDescPanel().setMathDescription(this.simulationContext.getMathDescription());
            } catch (Exception e) {
                e.printStackTrace(System.out);
                this.getVCMLPanel().setText("error displaying math language: " + e.getMessage());
            }
        } else {
            this.getVCMLPanel().setText("");
        }
    }
}
