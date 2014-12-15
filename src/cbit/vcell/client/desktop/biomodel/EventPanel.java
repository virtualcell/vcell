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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.Delay;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.gui.ElectricalStimulusPanel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;

import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Color;

@SuppressWarnings("serial")
public class EventPanel extends DocumentEditorSubPanel {
		// for trigger and delay 
		private JPanel labels_n_textfields_Panel = null;
		private JLabel triggerLabel = null;
		private TextFieldAutoCompletion triggerTextfield = null;
		private JLabel delayLabel = null;
		private TextFieldAutoCompletion delayTextField = null;
		// for event assignment - label, add/delete event assignment var
		private JPanel buttons_n_label_Panel = null;
		private javax.swing.JLabel eventAssgnLabel = null;
		private JButton addButton = null;
		private JButton deleteButton = null;
		// for table to display event assignment variables and expressions and units
		private JSortTable eventTargetsScrollPaneTable = null;
		private EventAssignmentsTableModel eventAssgnListTableModel1 = null;
		// for adding an event assignment - var and expression
		private JLabel eventAssignExprLabel = null;
		private JLabel eventAssgnVarNameLabel = null;
		private TextFieldAutoCompletion eventAssgnExpressionTextField = null;
		private JComboBox<String> eventAssignvarNameComboBox = null;
		private DefaultComboBoxModel<String> varNameComboBoxModel = null;
		private JPanel eventAssignmentPanel = null;
		
//		private JCheckBox useValuesAtTriggerTimeCheckBox = null;
		
		// general
		private SimulationContext fieldSimContext = null;
		private BioEvent fieldBioEvent = null;

		private AutoCompleteSymbolFilter autoCompleteFilter = null;
		private Set<String> autoCompleteList = null;
		IvjEventHandler ivjEventHandler = new IvjEventHandler();
		private JButton btnPlotTrigger;
		private JRadioButton rdbtnTrigTime;
		private JRadioButton rdbtnDelayTime;
		private ButtonGroup buttonGroup = new ButtonGroup();
		private JPanel panel_1;
		
		class IvjEventHandler implements ActionListener, MouseListener, PropertyChangeListener, FocusListener {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getSource() == EventPanel.this.getAddEventAssgnButton()) { 
					addEventAssignment();
				}
				if (e.getSource() == EventPanel.this.getDeleteEventAssgnButton()) { 
					deleteEventAssignment();
				}
				if (e.getSource() == getRdbtnDelayTime() || e.getSource() == getRdbtnTrigTime()) {
					setNewDelay();
				}
			}

			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				if (evt.getSource() == EventPanel.this && (evt.getPropertyName().equals("bioEvent"))) {
					// disable add/delete function buttons					
					updateEventPanel();					
				}
				if (evt.getSource() == EventPanel.this && (evt.getPropertyName().equals("simulationContext"))) {
					getTriggerTextField().setAutoCompleteSymbolFilter(getAutoCompleteFilter());
					getTriggerTextField().setAutoCompletionWords(getAutoCompleteList());
					getDelayTextField().setAutoCompleteSymbolFilter(getAutoCompleteFilter());
					getDelayTextField().setAutoCompletionWords(getAutoCompleteList());					
				}
			};

			public void mouseClicked(MouseEvent e) {
				if (e.getSource() == EventPanel.this.getEventTargetsScrollPaneTable()) {
					enableDeleteEventAssgnButton();
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}

			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				if (!isEnabled() || fieldBioEvent == null) {
					return;
				}
				if (e.isTemporary()) {
					return;
				}
				if (e.getSource() == getTriggerTextField()) {
					try{
						setNewTrigger(getBioEvent(),getSimulationContext(),getTriggerTextField().getText());
					} catch (Exception e1) {
						e1.printStackTrace(System.out);
						DialogUtils.showErrorDialog(EventPanel.this, e1.getMessage());
					}
				}
				if (e.getSource() == getDelayTextField()) {
					setNewDelay();
				}

			};
		};
		
		public EventPanel() {
			super();
			initialize();
		}

		private javax.swing.JButton getAddEventAssgnButton() {
			if (addButton == null) {
				try {
					addButton = new javax.swing.JButton();
					addButton.setName("AddEventAssgnButton");
					addButton.setText("Add Assignment");
					addButton.setEnabled(true);
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return addButton;
		}

		private javax.swing.JButton getDeleteEventAssgnButton() {
			if (deleteButton == null) {
				try {
					deleteButton = new javax.swing.JButton();
					deleteButton.setName("DeleteEventAssgnButton");
					deleteButton.setText("Delete Assignment");
					deleteButton.setEnabled(false);
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return deleteButton;
		}

		private javax.swing.JLabel getEventAssgnLabel() {
			if (eventAssgnLabel == null) {
				try {
					eventAssgnLabel = new javax.swing.JLabel();
					eventAssgnLabel.setName("EventAssgnLabel");
					eventAssgnLabel.setFont(eventAssgnLabel.getFont().deriveFont(Font.BOLD));
					eventAssgnLabel.setText("  Event Assignments: ");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return eventAssgnLabel;
		}

		private JLabel getTriggerLabel() {
			if (triggerLabel == null) {
				try {
					triggerLabel = new javax.swing.JLabel();
					triggerLabel.setName("TriggerLabel");
					triggerLabel.setFont(triggerLabel.getFont().deriveFont(Font.BOLD));
					triggerLabel.setText("Trigger Expression:");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return triggerLabel;
		}

		private TextFieldAutoCompletion getTriggerTextField() {
			if (triggerTextfield == null) {
				try {
					triggerTextfield = new TextFieldAutoCompletion();
					triggerTextfield.setName("TriggerTextField");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return triggerTextfield;
		}

//		private JCheckBox getUseValuesAtTriggerTimeCheckBox() {
//			if (useValuesAtTriggerTimeCheckBox == null) {
//				try {
//					useValuesAtTriggerTimeCheckBox = new JCheckBox("Use Values at Trigger Time");
//					useValuesAtTriggerTimeCheckBox.setSelected(true);
//				} catch (java.lang.Throwable e) {
//					e.printStackTrace(System.out);
//				}
//			}
//			return useValuesAtTriggerTimeCheckBox;
//		}

		
		private JLabel getDelayLabel() {
			if (delayLabel == null) {
				try {
					delayLabel = new javax.swing.JLabel();
					delayLabel.setName("DelayLabel");
					delayLabel.setFont(delayLabel.getFont().deriveFont(Font.BOLD));
					delayLabel.setText("Assignment Delay (s):");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return delayLabel;
		}

		private TextFieldAutoCompletion getDelayTextField() {
			if (delayTextField == null) {
				try {
					delayTextField = new TextFieldAutoCompletion();
					delayTextField.setName("DelayTextField");
					delayTextField.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseExited(MouseEvent e) {
							super.mouseExited(e);
							setNewDelay();
						}
					});
					delayTextField.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							setNewDelay();
						}
					});
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return delayTextField;
		}

		private JPanel getLabelsTextFieldsPanel() {
			if (labels_n_textfields_Panel == null) {
				try {
					labels_n_textfields_Panel = new javax.swing.JPanel();
					labels_n_textfields_Panel.setName("labelsTextfieldsPanel");
					GridBagLayout gbl_labels_n_textfields_Panel = new GridBagLayout();
					gbl_labels_n_textfields_Panel.rowWeights = new double[]{1.0, 0.0};
					gbl_labels_n_textfields_Panel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
					labels_n_textfields_Panel.setLayout(gbl_labels_n_textfields_Panel);

					java.awt.GridBagConstraints constraintsTriggerLabel = new java.awt.GridBagConstraints();
					constraintsTriggerLabel.anchor = GridBagConstraints.EAST;
					constraintsTriggerLabel.gridx = 0; constraintsTriggerLabel.gridy = 0;
					constraintsTriggerLabel.insets = new Insets(4, 4, 5, 5);
					labels_n_textfields_Panel.add(getTriggerLabel(), constraintsTriggerLabel);

					java.awt.GridBagConstraints constraintsTriggerTextField = new java.awt.GridBagConstraints();
					constraintsTriggerTextField.gridwidth = 2;
					constraintsTriggerTextField.gridx = 1; constraintsTriggerTextField.gridy = 0;
					constraintsTriggerTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
					constraintsTriggerTextField.weightx = 1.0;
					constraintsTriggerTextField.insets = new Insets(4, 4, 5, 5);
					labels_n_textfields_Panel.add(getTriggerTextField(), constraintsTriggerTextField);
					GridBagConstraints gbc_btnPlotTrigger = new GridBagConstraints();
					gbc_btnPlotTrigger.anchor = GridBagConstraints.EAST;
					gbc_btnPlotTrigger.fill = GridBagConstraints.VERTICAL;
					gbc_btnPlotTrigger.insets = new Insets(4, 4, 5, 4);
					gbc_btnPlotTrigger.gridx = 3;
					gbc_btnPlotTrigger.gridy = 0;
					labels_n_textfields_Panel.add(getBtnPlotTrigger(), gbc_btnPlotTrigger);

					java.awt.GridBagConstraints constraintsDelayLabel = new java.awt.GridBagConstraints();
					constraintsDelayLabel.anchor = GridBagConstraints.EAST;
					constraintsDelayLabel.gridx = 0; constraintsDelayLabel.gridy = 1;
					constraintsDelayLabel.insets = new Insets(4, 4, 4, 5);
					labels_n_textfields_Panel.add(getDelayLabel(), constraintsDelayLabel);

					java.awt.GridBagConstraints constraintsDelayTextField = new java.awt.GridBagConstraints();
					constraintsDelayTextField.gridwidth = 2;
					constraintsDelayTextField.gridx = 1; constraintsDelayTextField.gridy = 1;
					constraintsDelayTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
					constraintsDelayTextField.weightx = 1.0;
					constraintsDelayTextField.insets = new Insets(4, 4, 4, 5);
					labels_n_textfields_Panel.add(getDelayTextField(), constraintsDelayTextField);
					GridBagConstraints gbc_panel_1 = new GridBagConstraints();
					gbc_panel_1.insets = new Insets(4, 4, 4, 4);
					gbc_panel_1.fill = GridBagConstraints.BOTH;
					gbc_panel_1.gridwidth = 2;
					gbc_panel_1.gridx = 3;
					gbc_panel_1.gridy = 1;
					labels_n_textfields_Panel.add(getPanel_1(), gbc_panel_1);
					
//					java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
//					gbc.gridx = 5; 
//					gbc.gridy = 1;
//					gbc.insets = new java.awt.Insets(4, 4, 4, 4);
//					labels_n_textfields_Panel.add(getUseValuesAtTriggerTimeCheckBox(), gbc);
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return labels_n_textfields_Panel;
		}

		private javax.swing.JPanel getButtonLabelPanel() {
			if (buttons_n_label_Panel == null) {
				try {
					buttons_n_label_Panel = new javax.swing.JPanel();
					buttons_n_label_Panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
					buttons_n_label_Panel.setAlignmentY(Component.TOP_ALIGNMENT);
//					buttons_n_label_Panel.setPreferredSize(new Dimension(750, 40));
					buttons_n_label_Panel.setName("ButtonPanel");
					buttons_n_label_Panel.setLayout(new BoxLayout(buttons_n_label_Panel, BoxLayout.X_AXIS));
					buttons_n_label_Panel.add(getEventAssgnLabel());
					buttons_n_label_Panel.add(Box.createHorizontalGlue());
					buttons_n_label_Panel.add(getAddEventAssgnButton());
					buttons_n_label_Panel.add(Box.createRigidArea(new Dimension(5,5)));
					buttons_n_label_Panel.add(getDeleteEventAssgnButton());
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return buttons_n_label_Panel;
		}

		private JSortTable getEventTargetsScrollPaneTable() {
			if (eventTargetsScrollPaneTable == null) {
				try {
					eventTargetsScrollPaneTable = new JSortTable();
					eventTargetsScrollPaneTable.setName("ScrollPaneTable");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return eventTargetsScrollPaneTable;
		}

		private TextFieldAutoCompletion getEventAssignExpressionTextField() {
			if (eventAssgnExpressionTextField == null) {
				try {
					eventAssgnExpressionTextField = new TextFieldAutoCompletion();
					eventAssgnExpressionTextField.setName("EventAssignExpressionTextField");
//					eventAssgnExpressionTextField.setPreferredSize(new java.awt.Dimension(200, 30));
//					eventAssgnExpressionTextField.setMaximumSize(new java.awt.Dimension(200, 30));
//					eventAssgnExpressionTextField.setMinimumSize(new java.awt.Dimension(200, 30));
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return eventAssgnExpressionTextField;
		}

		private JLabel getEventAssignExprLabel() {
			if (eventAssignExprLabel == null) {
				try {
					eventAssignExprLabel = new JLabel();
					eventAssignExprLabel.setName("EventAssignExprLabel");
					eventAssignExprLabel.setText("Event Assignment Expression");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return eventAssignExprLabel;
		}

		private JLabel getEventAssignVarNameLabel() {
			if (eventAssgnVarNameLabel == null) {
				try {
					eventAssgnVarNameLabel = new javax.swing.JLabel();
					eventAssgnVarNameLabel.setName("EventAssignVarNameLabel");
					eventAssgnVarNameLabel.setText("Event Assignment Variable Name");
//					eventAssgnVarNameLabel.setMinimumSize(new java.awt.Dimension(45, 14));
//					eventAssgnVarNameLabel.setMaximumSize(new java.awt.Dimension(45, 14));
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return eventAssgnVarNameLabel;
		}

		private JComboBox<String> getEventAssignVarNameComboBox() {
			if (eventAssignvarNameComboBox == null) {
				try {
					eventAssignvarNameComboBox = new JComboBox<String>();
					eventAssignvarNameComboBox.setName("VariableNameComboBox");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return eventAssignvarNameComboBox;
		}

		private DefaultComboBoxModel<String> getVarNameComboBoxModel() {
			if (varNameComboBoxModel == null) {
				try {
					varNameComboBoxModel = new DefaultComboBoxModel<String>();
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}

			return varNameComboBoxModel;
		}


		private javax.swing.JPanel getEventAssignmentPanel() {
			if (eventAssignmentPanel == null) {
				try {
					eventAssignmentPanel = new javax.swing.JPanel();
					eventAssignmentPanel.setName("EventAssignmentPanel");
					eventAssignmentPanel.setLayout(new java.awt.GridBagLayout());
					eventAssignmentPanel.setBounds(401, 308, 407, 85);

					java.awt.GridBagConstraints constraintsEAVarNameLabel = new java.awt.GridBagConstraints();
					constraintsEAVarNameLabel.gridx = 0; constraintsEAVarNameLabel.gridy = 0;
					constraintsEAVarNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
					getEventAssignmentPanel().add(getEventAssignVarNameLabel(), constraintsEAVarNameLabel);

					java.awt.GridBagConstraints constraintsEAVarNameTextField = new java.awt.GridBagConstraints();
					constraintsEAVarNameTextField.gridx = 1; constraintsEAVarNameTextField.gridy = 0;
					constraintsEAVarNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
					constraintsEAVarNameTextField.weightx = 1.0;
					constraintsEAVarNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
					getEventAssignmentPanel().add(getEventAssignVarNameComboBox(), constraintsEAVarNameTextField);

					java.awt.GridBagConstraints constraintsEAExprLabel = new java.awt.GridBagConstraints();
					constraintsEAExprLabel.gridx = 0; constraintsEAExprLabel.gridy = 1;
					constraintsEAExprLabel.insets = new java.awt.Insets(4, 4, 4, 4);
					getEventAssignmentPanel().add(getEventAssignExprLabel(), constraintsEAExprLabel);

					java.awt.GridBagConstraints constraintsEAExpressionTextField = new java.awt.GridBagConstraints();
					constraintsEAExpressionTextField.gridx = 1; constraintsEAExpressionTextField.gridy = 1;
					constraintsEAExpressionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
					constraintsEAExpressionTextField.weightx = 1.0;
					constraintsEAExpressionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
					getEventAssignmentPanel().add(getEventAssignExpressionTextField(), constraintsEAExpressionTextField);
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return eventAssignmentPanel;
		}

		private EventAssignmentsTableModel getEventAssignmentsTableModel() {
			if (eventAssgnListTableModel1 == null) {
				try {
					eventAssgnListTableModel1 = new EventAssignmentsTableModel(eventTargetsScrollPaneTable);
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return eventAssgnListTableModel1;
		}
		
		private void initConnections() throws java.lang.Exception {
			getAddEventAssgnButton().addActionListener(ivjEventHandler);
			getDeleteEventAssgnButton().addActionListener(ivjEventHandler);
			getEventTargetsScrollPaneTable().addMouseListener(ivjEventHandler);
			getTriggerTextField().addFocusListener(ivjEventHandler);
			getDelayTextField().addFocusListener(ivjEventHandler);
			
			getRdbtnDelayTime().addActionListener(ivjEventHandler);
			getRdbtnTrigTime().addActionListener(ivjEventHandler);
			
			this.addPropertyChangeListener(ivjEventHandler);
			
			// for scrollPaneTable, set tableModel and create default columns
			getEventTargetsScrollPaneTable().setModel(getEventAssignmentsTableModel());
			getEventTargetsScrollPaneTable().createDefaultColumnsFromModel();
			
			// cellRenderer for table (name column)
			getEventTargetsScrollPaneTable().setDefaultRenderer(EventAssignment.class, new DefaultScrollTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
				{
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (value instanceof EventAssignment) {
						setText(((EventAssignment)value).getTarget().getName());
					}
					return this;
				}
			});	
			
			getTriggerTextField().setInputVerifier(new InputVerifier() {
				
				@Override
				public boolean shouldYieldFocus(JComponent input) {
					boolean bValid = true;
					if (fieldBioEvent != null && getTriggerTextField().isEnabled()) {
						String text = getTriggerTextField().getText();
						if (text == null || text.trim().length() == 0) {
							bValid = false;
							DialogUtils.showErrorDialog(EventPanel.this, "Invalid expression for Trigger!");
						}
						if (bValid) {
							getTriggerTextField().setBorder(UIManager.getBorder("TextField.border"));
						} else {
							getTriggerTextField().setBorder(GuiConstants.ProblematicTextFieldBorder);
							SwingUtilities.invokeLater(new Runnable() { 
							    public void run() { 
							    	getTriggerTextField().requestFocus();
							    }
							});
						}
					}
					return bValid;
				}

				@Override
				public boolean verify(JComponent input) {
					return false;
				}
			});
		}

		private void initialize() {
			try {
				setName("EventPanel");
				setPreferredSize(new Dimension(547, 400));
				setLayout(new BorderLayout());
				add(getLabelsTextFieldsPanel(), BorderLayout.NORTH);
				
				buttonGroup.add(rdbtnTrigTime);
				buttonGroup.add(rdbtnDelayTime);
				rdbtnTrigTime.setSelected(true);
				rdbtnTrigTime.setEnabled(false);
				rdbtnDelayTime.setEnabled(false);
				
				JPanel panel = new JPanel();
				panel.setName("assignmentPanel");
				panel.setLayout(new BorderLayout());
				panel.add(getButtonLabelPanel(), BorderLayout.NORTH);
				panel.add(getEventTargetsScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
				add(panel, BorderLayout.CENTER);
				initConnections();
				getEventTargetsScrollPaneTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}

		public static void populateVariableComboBoxModel(DefaultComboBoxModel<String> defaultComboBoxModel,SimulationContext simContext,boolean bExcludeFuncAndReserved){
			// fill comboboxmodel with possible variables from simContext (symboltable entries) list
			defaultComboBoxModel.removeAllElements();
			Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
			simContext.getEntries(entryMap);
			ArrayList<String> varNameList = new ArrayList<String>();
			for (String varName : entryMap.keySet()) {
				SymbolTableEntry symbolTableEntry = entryMap.get(varName);
				if (bExcludeFuncAndReserved && 
					(symbolTableEntry instanceof SymbolTableFunctionEntry || 
					symbolTableEntry instanceof Model.ReservedSymbol)) {
					continue;
				}
				varNameList.add(varName);
			}
			Collections.sort(varNameList);
			for (String varName : varNameList) {
				defaultComboBoxModel.addElement(varName);
			}

		}
		private void addEventAssignment() {

			populateVariableComboBoxModel(getVarNameComboBoxModel(),getSimulationContext(),true);
			
			JPanel eventAssignmentPanel = getEventAssignmentPanel();
			getEventAssignVarNameComboBox().setModel(getVarNameComboBoxModel());
			getEventAssignExpressionTextField().setText("0.0");
			Set<String> autoCompList = getAutoCompleteList();
			getEventAssignExpressionTextField().setAutoCompletionWords(autoCompList);
			getEventAssignExpressionTextField().setAutoCompleteSymbolFilter(getAutoCompleteFilter());

			//
			// If the OK option is chosen, get the var name and expression and create a new
			// event assignment, add it to the list of event assignments in bioEvent
			// Else, pop-up an error dialog indicating that event assignment cannot be added.
			//
			int ok = JOptionPane.showOptionDialog(this, eventAssignmentPanel, "Add Event Assignment" , 0, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, null);
			if (ok == javax.swing.JOptionPane.OK_OPTION) {
				String varName = (String)getEventAssignVarNameComboBox().getSelectedItem();
				EventAssignment newEventAssignment = null;
				try {
					SymbolTableEntry ste = fieldSimContext.getEntry(varName);
					Expression eventAssgnExp = new Expression(getEventAssignExpressionTextField().getText());
					newEventAssignment = fieldBioEvent.new EventAssignment(ste, eventAssgnExp);
					fieldBioEvent.addEventAssignment(newEventAssignment);
				} catch (ExpressionException e) {
					e.printStackTrace(System.out);
				} catch (PropertyVetoException e1) {
					e1.printStackTrace(System.out);
					String targetName = (newEventAssignment!=null)?(newEventAssignment.getTarget().getName()):("null");
					DialogUtils.showErrorDialog(this, "Event Assignment '" + targetName + "' cannot be added." + e1.getMessage());
				}
			}

			enableDeleteEventAssgnButton();
		}

		private AutoCompleteSymbolFilter getAutoCompleteFilter() {
			if (autoCompleteFilter == null) {
				autoCompleteFilter =  new AutoCompleteSymbolFilter() {
						public boolean accept(SymbolTableEntry ste) {
							return true;
						}
						public boolean acceptFunction(String funcName) {
							return true;
						}
					};
				}
			return autoCompleteFilter;
		}

		private Set<String> getAutoCompleteList() {
			if (autoCompleteList == null && fieldSimContext != null) {
				autoCompleteList = new HashSet<String>();
				Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
				fieldSimContext.getEntries(entryMap);
				autoCompleteList = entryMap.keySet();
			}
			return autoCompleteList ;
		}

		private void deleteEventAssignment() {
			int selectedRow = getEventTargetsScrollPaneTable().getSelectedRow();
			if (selectedRow > -1) {
				EventAssignment eventAssgn = getEventAssignmentsTableModel().getValueAt(selectedRow);
				try {
					fieldBioEvent.removeEventAssignment(eventAssgn);
				} catch (PropertyVetoException e1) {
					e1.printStackTrace(System.out);
					DialogUtils.showErrorDialog(this, " Deletion of Event Assignment '" + eventAssgn.getTarget().getName() + "' failed." + e1.getMessage());
				}
			}
			enableDeleteEventAssgnButton();
		}

		public void setBioEvent(final BioEvent argBioEvent) {
		/**
		 * This set method is a little unconventional. With the old (conventional set method) when a different selection is made in
		 *  the tree (event node) after a value is changed in currently selected event, the setBioEvent method gets called 
		 *  before the events summary table is updated with new value from current selection. Now, this method makes sure the eventPanel 
		 *  has focus before firing propertychange for bioevent. If it doesn't, eventPanel tries to gain focus, removes its focusListener 
		 *  & then sets bioevent and fires propertychange. This is the only way the tree and table selections work in coordination
		 *  when values have been changed in an individually selected event. 
		 */
			BioEvent oldValue = this.fieldBioEvent; 
//			if (hasFocus()) {
				fieldBioEvent = argBioEvent;
				firePropertyChange("bioEvent", oldValue, argBioEvent);
//			} else {
//				requestFocusInWindow();
//				addFocusListener(new FocusListener() {					
//					public void focusLost(FocusEvent e) {						
//					}					
//					public void focusGained(FocusEvent e) {
//						removeFocusListener(this);
//						fieldBioEvent = argBioEvent;
//						firePropertyChange("bioEvent", oldValue, argBioEvent);
//					}
//				});				
//			}			
			
		}

		public BioEvent getBioEvent() {
			return fieldBioEvent;
		}

		public SimulationContext getSimulationContext() {
			return fieldSimContext;
		}
		
		private void setSimulationContext(SimulationContext argSimContext) {
			SimulationContext oldValue = fieldSimContext;
			fieldSimContext = argSimContext;
			firePropertyChange("simulationContext", oldValue, argSimContext);
		}
		
		private void enableDeleteEventAssgnButton() {
			if (getEventTargetsScrollPaneTable().getSelectedRow() > -1) {
				getDeleteEventAssgnButton().setEnabled(true);
			} else {
				getDeleteEventAssgnButton().setEnabled(false);
			}
		}
		
		private void updateEventPanel() {			
			if (fieldBioEvent == null) {
				setEnabled(false);
			} else {
				setSimulationContext(fieldBioEvent.getSimulationContext());
				if (fieldSimContext.getGeometry() != null && fieldSimContext.getGeometry().getDimension() > 0 
						|| fieldSimContext.isStoch()) {
					getAddEventAssgnButton().setEnabled(false);
				}
				getDeleteEventAssgnButton().setEnabled(false);
								
				setEnabled(true);
				// we are initializing EventsPanel, hence no focuslistener should not be active on the text fields
				getTriggerTextField().removeFocusListener(ivjEventHandler);
				getDelayTextField().removeFocusListener(ivjEventHandler);

				// set 
				getTriggerTextField().setText(fieldBioEvent.getTriggerExpression().infix());
				Delay delay = fieldBioEvent.getDelay();
				if (delay != null) {
					getDelayTextField().setText(delay.getDurationExpression().infix());
					getRdbtnDelayTime().setEnabled(true);
					getRdbtnTrigTime().setEnabled(true);
					if(delay.useValuesFromTriggerTime()){
						getRdbtnTrigTime().setSelected(true);
					}else{
						getRdbtnDelayTime().setSelected(true);
					}
//					getUseValuesAtTriggerTimeCheckBox().setSelected(delay.useValuesFromTriggerTime());
				} else {
					getDelayTextField().setText("");
					getRdbtnDelayTime().setEnabled(false);
					getRdbtnTrigTime().setEnabled(false);
				}
				getEventAssignmentsTableModel().setSimulationContext(fieldSimContext);
				getEventAssignmentsTableModel().setBioEvent(fieldBioEvent);
				// for some reason, when events are selected in the table, the delayTextField gains focus (this fires table update whenever
				// another selection is made. To avoid this, adding a request focus for the events table.
				requestFocusInWindow();
				addFocusListener(new FocusListener() {					
					public void focusLost(FocusEvent e) {						
					}					
					public void focusGained(FocusEvent e) {
						removeFocusListener(this);
						getTriggerTextField().addFocusListener(ivjEventHandler);
						getDelayTextField().addFocusListener(ivjEventHandler);						
					}
				});
			}
		}

		private void setNewDelay() {
			try {
				if (fieldBioEvent == null) {
					return;
				}
				String text = getDelayTextField().getText();
				Delay delay = null;
				if (text != null && text.trim().length() > 0) {
					Expression durationExpression = new Expression(text);
					durationExpression.bindExpression(fieldSimContext);
					delay = fieldBioEvent.new Delay(getRdbtnTrigTime().isSelected(), durationExpression);
				}
				fieldBioEvent.setDelay(delay);
				updateEventPanel();
			} catch (ExpressionException e1) {
				e1.printStackTrace(System.out);
				DialogUtils.showErrorDialog(EventPanel.this, e1.getMessage());
			}
		}

		public static void setNewTrigger(BioEvent bioEvent,SimulationContext simulationContext,String expr) throws ExpressionBindingException,ExpressionException{
			if (bioEvent == null) {
				return;
			}
			Expression triggerExpr = new Expression(expr);
			triggerExpr.bindExpression(simulationContext);
			bioEvent.setTriggerExpression(triggerExpr);
		}
		
	@Override
	public void setEnabled(boolean enabled) {		
		if (!enabled) {
			getTriggerTextField().setText(null);
			getDelayTextField().setText(null);
			getEventAssignmentsTableModel().setBioEvent(null);
		}
		super.setEnabled(enabled);
		BeanUtils.enableComponents(this, enabled);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		BioEvent bioEvent = null;
		if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof BioEvent) {
			bioEvent = (BioEvent) selectedObjects[0];
		}
		setBioEvent(bioEvent);			
	}
	private JButton getBtnPlotTrigger() {
		if (btnPlotTrigger == null) {
			btnPlotTrigger = new JButton("Plot Trigger...");
			btnPlotTrigger.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						setNewTrigger(getBioEvent(),getSimulationContext(),getTriggerTextField().getText());
						ElectricalStimulusPanel.graphTimeFunction(
								EventPanel.this,
								getBioEvent().getTriggerExpression(),
								getSimulationContext(),
								getSimulationContext().getModel().getTIME(),
								"Trigger Expression");
					} catch (Exception e1) {
						e1.printStackTrace(System.out);
						DialogUtils.showErrorDialog(EventPanel.this, "Error: Only simple expressions of time can be plotted here");
					}
				}
			});
		}
		return btnPlotTrigger;
	}
	private JRadioButton getRdbtnTrigTime() {
		if (rdbtnTrigTime == null) {
			rdbtnTrigTime = new JRadioButton("evaluate at trigger time");
		}
		return rdbtnTrigTime;
	}
	private JRadioButton getRdbtnDelayTime() {
		if (rdbtnDelayTime == null) {
			rdbtnDelayTime = new JRadioButton("evaluate after delay");
		}
		return rdbtnDelayTime;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
			GridBagLayout gbl_panel_1 = new GridBagLayout();
			gbl_panel_1.columnWidths = new int[]{0, 0};
			gbl_panel_1.rowHeights = new int[]{0};
			gbl_panel_1.columnWeights = new double[]{0, 0.0};
			gbl_panel_1.rowWeights = new double[]{0.0};
			panel_1.setLayout(gbl_panel_1);
			GridBagConstraints gbc_rdbtnTrigTime = new GridBagConstraints();
			gbc_rdbtnTrigTime.anchor = GridBagConstraints.WEST;
			gbc_rdbtnTrigTime.insets = new Insets(0, 0, 0, 5);
			gbc_rdbtnTrigTime.gridx = 0;
			gbc_rdbtnTrigTime.gridy = 0;
			panel_1.add(getRdbtnTrigTime(), gbc_rdbtnTrigTime);
			GridBagConstraints gbc_rdbtnDelayTime = new GridBagConstraints();
			gbc_rdbtnDelayTime.gridx = 1;
			gbc_rdbtnDelayTime.gridy = 0;
			panel_1.add(getRdbtnDelayTime(), gbc_rdbtnDelayTime);
		}
		return panel_1;
	}
}
