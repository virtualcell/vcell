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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import cbit.vcell.model.*;
import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.ElectricalStimulusPanel;
import cbit.vcell.math.Equation;
import cbit.vcell.math.MathException;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;

@SuppressWarnings("serial")
public class EventPanel extends DocumentEditorSubPanel {
		// for trigger and delay 
		private JPanel labels_n_textfields_Panel = null;
		private JLabel triggerLabel = null;
//		private TextFieldAutoCompletion triggerTextfield = null;
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

		private static AutoCompleteSymbolFilter autoCompleteFilter = new AutoCompleteSymbolFilter() {
			public boolean accept(SymbolTableEntry ste) {
				return true;
			}
			public boolean acceptFunction(String funcName) {
				return true;
			}
		};
		private Set<String> autoCompleteList = null;
		IvjEventHandler ivjEventHandler = new IvjEventHandler();
		private JButton btnPlotTrigger;
		private JCheckBox persistenceCheckBox;
		private JRadioButton rdbtnTrigTime;
		private JRadioButton rdbtnDelayTime;
		private ButtonGroup buttonGroup = new ButtonGroup();
		private JPanel triggerEvalPanel;
		private JLabel labelTriggerType;
		private JButton buttonEditTrigger;
		
		class IvjEventHandler implements ActionListener, MouseListener, PropertyChangeListener/*, FocusListener*/ {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getSource() == EventPanel.this.getAddEventAssgnButton()) { 
					addEventAssignment();
				}
				if (e.getSource() == EventPanel.this.getDeleteEventAssgnButton()) { 
					deleteEventAssignment();
				}
				if (e.getSource() == getRdbtnDelayTime() || e.getSource() == getRdbtnTrigTime()) {
					updateDelay();
				}
			}

			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				if (evt.getSource() == EventPanel.this && (evt.getPropertyName().equals("bioEvent"))) {
					try{
						if(evt.getOldValue() != null){
							//Make sure uncommitted change is saved
							BioEvent oldBioevent = ((BioEvent)evt.getOldValue());
							setNewDelay(oldBioevent,getDelayTextField().getText(),getSimulationContext(),getRdbtnTrigTime().isSelected());
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
					setSimulationContext((((BioEvent)evt.getNewValue())==null?null:((BioEvent)evt.getNewValue()).getSimulationContext()));

					// disable add/delete function buttons					
					updateEventPanel();					
				}
				if (evt.getSource() == EventPanel.this && (evt.getPropertyName().equals("simulationContext"))) {
//					getTriggerTextField().setAutoCompleteSymbolFilter(getAutoCompleteFilter());
//					getTriggerTextField().setAutoCompletionWords(getAutoCompleteList());
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

//			public void focusGained(FocusEvent e) {
//			}
//			public void focusLost(FocusEvent e) {
//				if (!isEnabled() || getBioEvent() == null) {
//					return;
//				}
//				if (e.isTemporary()) {
//					return;
//				}
////				if (e.getSource() == getTriggerTextField()) {
////					try{
////						if (getBioEvent() == null || getSimulationContext() == null) {
////							return;
////						}
////						getBioEvent().setTriggerExpression(bindTriggerExpression(getTriggerTextField().getText(), getSimulationContext()));
////					} catch (Exception e1) {
////						e1.printStackTrace(System.out);
////					}
////				}
//				if (e.getSource() == getDelayTextField()) {
//					setNewDelay();
//				}
//
//			};
		};
		
		public EventPanel() {
			super();
			initialize();
		}

		private void updateDelay(){
			try{
				setNewDelay(getBioEvent(),getDelayTextField().getText(),getSimulationContext(),rdbtnTrigTime.isSelected());
				updateEventPanel();
			}catch(Exception e2){
				e2.printStackTrace();
				DialogUtils.showErrorDialog(EventPanel.this, "Error updating delay: "+e2.getMessage());
			}
		}
		private javax.swing.JButton getAddEventAssgnButton() {
			if (addButton == null) {
				try {
					addButton = new javax.swing.JButton();
					addButton.setName("AddEventAssgnButton");
					addButton.setText("Add Action");
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
					deleteButton.setText("Delete Action");
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
					eventAssgnLabel.setText("  Event Actions: ");
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
					triggerLabel.setText("Trigger:");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return triggerLabel;
		}

//		private TextFieldAutoCompletion getTriggerTextField() {
//			if (triggerTextfield == null) {
//				try {
//					triggerTextfield = new TextFieldAutoCompletion();
//					triggerTextfield.setName("TriggerTextField");
//					triggerTextfield.addActionListener(new ActionListener() {
//						@Override
//						public void actionPerformed(ActionEvent e) {
//							getTriggerTextField().getInputVerifier().verify(getTriggerTextField());
//						}
//					});
//
//				} catch (java.lang.Throwable e) {
//					e.printStackTrace(System.out);
//				}
//			}
//			return triggerTextfield;
//		}
		
		private JLabel getDelayLabel() {
			if (delayLabel == null) {
				try {
					delayLabel = new javax.swing.JLabel();
					delayLabel.setName("DelayLabel");
					delayLabel.setFont(delayLabel.getFont().deriveFont(Font.BOLD));
					delayLabel.setText("Action Delay (sec):");
				} catch (java.lang.Throwable e) {
					e.printStackTrace(System.out);
				}
			}
			return delayLabel;
		}

//		private InputVerifier delayInputVerifier = new InputVerifier() {
//			@Override
//			public boolean verify(JComponent input) {
//				boolean bValid = true;
//				Exception delayException = null;
//				boolean bTextNotEmpty = getDelayTextField().getText() != null && getDelayTextField().getText().trim().length() > 0;
//				try{
//					if (bTextNotEmpty) {
//						//Use createDelay to check validity
//						createDelay(getBioEvent(),getDelayTextField().getText(),getSimulationContext(),getRdbtnTrigTime().isSelected());
//					}
//				}catch(Exception e){
//					bValid = false;
//					delayException = e;
////					e.printStackTrace();
//				}
//				if (bValid) {
//					getDelayTextField().setBorder(UIManager.getBorder("TextField.border"));
//					getDelayTextField().setToolTipText(null);
//					enableTriggerRadioButtons(bTextNotEmpty);
//				} else {
//					getDelayTextField().setBorder(GuiConstants.ProblematicTextFieldBorder);
//					getDelayTextField().setToolTipText("Error parse 'delay': "+delayException.getMessage());
//					enableTriggerRadioButtons(false);
////					SwingUtilities.invokeLater(new Runnable() { 
////					    public void run() { 
////					    	getDelayTextField().requestFocus();
////					    }
////					});
//				}
//				return bValid;
//			}
//		};
		
//		ActionListener editTimerActionListener = new ActionListener() {
//			private String lastEdit = null;
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if(!delayTextField.isPopupVisible()){
//					
//				}
//			}
//		};
//		Timer editTimer;
				
		private TextFieldAutoCompletion getDelayTextField() {
			if (delayTextField == null) {
				try {
					delayTextField = new TextFieldAutoCompletion();
					delayTextField.setName("DelayTextField");
					KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
//							System.out.println(evt);
							if(evt.getPropertyName().equals("focusOwner") && evt.getNewValue() == delayTextField){
//								System.out.println("-----I got Focus "+delayTextField.hashCode()+" "+(getBioEvent()==null?null:getBioEvent().hashCode()));
							}else if(evt.getPropertyName().equals("focusOwner") && evt.getOldValue() == delayTextField && evt.getNewValue() != delayTextField){
//								System.out.println("-----I lost Focus "+bErrorDialog+" "+delayTextField.hashCode()+" autolist visible="+delayTextField.isPopupVisible()+" "+(getBioEvent()==null?null:getBioEvent().hashCode()));
								if(!delayTextField.isPopupVisible()){
									try{
										setNewDelay(getBioEvent(),getDelayTextField().getText(),getSimulationContext(),getRdbtnTrigTime().isSelected());
									}catch(Exception e){
										e.printStackTrace();
									}
//									System.out.println("set new delay="+(getBioEvent()==null?null:getBioEvent().getDelay()));										
								}
							}
//							System.out.println(EventPanel.this.hashCode()+" "+evt);
						}
					});
					delayTextField.getDocument().addUndoableEditListener(new UndoableEditListener() {
						@Override
						public void undoableEditHappened(UndoableEditEvent e) {
							if(delayTextField.getText() != null && delayTextField.getText().length() > 0){
								enableTriggerRadioButtons(true);
							}else{
								enableTriggerRadioButtons(false);
							}
//							delayInputVerifier.verify(delayTextField);
						}
					});
					
					
//					delayTextField.addMouseListener(new MouseAdapter() {
//						@Override
//						public void mouseExited(MouseEvent e) {
//							super.mouseExited(e);
//							if(!delayTextField.isPopupVisible()){
//								delayInputVerifier.verify(delayTextField);
//							}else{
//								System.out.println("-----MouseExit");
//							}
//						}
//					});
					delayTextField.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							updateDelay();
						}
					});
//					
//					delayTextField.setInputVerifier(delayInputVerifier);

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
					GridBagConstraints gbc_labelTriggerType = new GridBagConstraints();
					gbc_labelTriggerType.anchor = GridBagConstraints.WEST;
					gbc_labelTriggerType.weightx = 1.0;
					gbc_labelTriggerType.insets = new Insets(4, 4, 5, 5);
					gbc_labelTriggerType.gridx = 1;
					gbc_labelTriggerType.gridy = 0;
					labels_n_textfields_Panel.add(getLabelTriggerType(), gbc_labelTriggerType);
					GridBagConstraints gbc_buttonEditTrigger = new GridBagConstraints();
					gbc_buttonEditTrigger.fill = GridBagConstraints.HORIZONTAL;
					gbc_buttonEditTrigger.insets = new Insets(4, 4, 5, 5);
					gbc_buttonEditTrigger.gridx = 2;
					gbc_buttonEditTrigger.gridy = 0;
					labels_n_textfields_Panel.add(getButtonEditTrigger(), gbc_buttonEditTrigger);

//					java.awt.GridBagConstraints constraintsTriggerTextField = new java.awt.GridBagConstraints();
//					constraintsTriggerTextField.gridwidth = 2;
//					constraintsTriggerTextField.gridx = 1; constraintsTriggerTextField.gridy = 0;
//					constraintsTriggerTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
//					constraintsTriggerTextField.weightx = 1.0;
//					constraintsTriggerTextField.insets = new Insets(4, 4, 5, 5);
//					labels_n_textfields_Panel.add(getTriggerTextField(), constraintsTriggerTextField);
					
					GridBagConstraints gbc_btnPlotTrigger = new GridBagConstraints();
					gbc_btnPlotTrigger.anchor = GridBagConstraints.EAST;
					gbc_btnPlotTrigger.fill = GridBagConstraints.VERTICAL;
					gbc_btnPlotTrigger.insets = new Insets(4, 4, 5, 5);
					gbc_btnPlotTrigger.gridx = 3;
					gbc_btnPlotTrigger.gridy = 0;
					labels_n_textfields_Panel.add(getBtnPlotTrigger(), gbc_btnPlotTrigger);
					
					GridBagConstraints gbc_persistence = new GridBagConstraints();
					gbc_persistence.anchor = GridBagConstraints.EAST;
					gbc_persistence.insets = new Insets(4, 4, 5, 5);
					gbc_persistence.gridx = 4;
					gbc_persistence.gridy = 0;
					labels_n_textfields_Panel.add(getPersistenceCheckBox(), gbc_persistence);
					

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
					GridBagConstraints gbc_triggerEvalPanel = new GridBagConstraints();
					gbc_triggerEvalPanel.insets = new Insets(4, 4, 4, 4);
					gbc_triggerEvalPanel.fill = GridBagConstraints.BOTH;
					gbc_triggerEvalPanel.gridwidth = 2;
					gbc_triggerEvalPanel.gridx = 3;
					gbc_triggerEvalPanel.gridy = 1;
					labels_n_textfields_Panel.add(getTriggerEvalPanel(), gbc_triggerEvalPanel);
					
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
					eventAssgnVarNameLabel.setText("Variable to modifiy when action triggered");
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

//					java.awt.GridBagConstraints constraintsEAExprLabel = new java.awt.GridBagConstraints();
//					constraintsEAExprLabel.gridx = 0; constraintsEAExprLabel.gridy = 1;
//					constraintsEAExprLabel.insets = new java.awt.Insets(4, 4, 4, 4);
//					getEventAssignmentPanel().add(getEventAssignExprLabel(), constraintsEAExprLabel);
//
//					java.awt.GridBagConstraints constraintsEAExpressionTextField = new java.awt.GridBagConstraints();
//					constraintsEAExpressionTextField.gridx = 1; constraintsEAExpressionTextField.gridy = 1;
//					constraintsEAExpressionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
//					constraintsEAExpressionTextField.weightx = 1.0;
//					constraintsEAExpressionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
//					getEventAssignmentPanel().add(getEventAssignExpressionTextField(), constraintsEAExpressionTextField);
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
//			getTriggerTextField().addFocusListener(ivjEventHandler);
			
//			getDelayTextField().addFocusListener(ivjEventHandler);

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
			
//			getTriggerTextField().setInputVerifier(new InputVerifier() {
//				
//				@Override
//				public boolean verify(JComponent input) {
//					boolean bValid = true;
//					if (fieldBioEvent != null && getTriggerTextField().isEnabled()) {
//						String text = getTriggerTextField().getText();
//						String errorText = null;
//						if (text == null || text.trim().length() == 0) {
//							bValid = false;
//							errorText = "Trigger expression cannot be empty";
//						}else{
//							Expression expr = null;
//							try{
//								expr = bindTriggerExpression(getTriggerTextField().getText(), getSimulationContext());
//							}catch(Exception e){
//								bValid = false;
//								errorText = e.getMessage();
//							}
////							if(expr != null){
////								try{
////									ElectricalStimulusPanel.getProtocolParameterExprPreview(expr, getSimulationContext(), getSimulationContext().getModel().getTIME());
////									getBtnPlotTrigger().setEnabled(true);
////								}catch(Exception e){
////									getBtnPlotTrigger().setEnabled(false);
////								}
////							}
//						}
//						if (bValid) {
//							getTriggerTextField().setBorder(UIManager.getBorder("TextField.border"));
//							getBtnPlotTrigger().setEnabled(true);
//							getTriggerTextField().setToolTipText(null);
//						} else {
//							getTriggerTextField().setBorder(GuiConstants.ProblematicTextFieldBorder);
//							getBtnPlotTrigger().setEnabled(false);
//							getTriggerTextField().setToolTipText(errorText);
//							SwingUtilities.invokeLater(new Runnable() { 
//							    public void run() { 
//							    	getTriggerTextField().requestFocus();
//							    }
//							});
//						}
//					}
//					return bValid;
//				}
//			});
		}

		private void initialize() {
			try {
				setName("EventPanel");
				setPreferredSize(new Dimension(547, 400));
				setLayout(new BorderLayout());
				add(getLabelsTextFieldsPanel(), BorderLayout.NORTH);
				
				buttonGroup.add(getRdbtnTrigTime());
				buttonGroup.add(getRdbtnDelayTime());
				getRdbtnTrigTime().setSelected(true);
				getRdbtnTrigTime().setEnabled(false);
				getRdbtnDelayTime().setEnabled(false);
				getPersistenceCheckBox().setEnabled(false);
				getPersistenceCheckBox().setSelected(true);
				
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
		
		
		private static void populateVariableComboBoxModel(DefaultComboBoxModel<String> defaultComboBoxModel, SimulationContext simContext) {
			defaultComboBoxModel.removeAllElements();
			ArrayList<String> varNameList = filterEventAssignmentVariables(simContext);
			for (String varName : varNameList) {
				defaultComboBoxModel.addElement(varName);
			}
		}
		public static ArrayList<String> filterEventAssignmentVariables(SimulationContext simContext) {
			// fill comboboxmodel with possible variables from simContext (symboltable entries) list
			Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
			simContext.getEntries(entryMap);
			ArrayList<String> varNameList = new ArrayList<String>();
			for (String varName : entryMap.keySet()) {
				SymbolTableEntry symbolTableEntry = entryMap.get(varName);
				if (/*bExcludeFuncAndReserved && */
					(symbolTableEntry instanceof SymbolTableFunctionEntry || 
					symbolTableEntry instanceof Structure.StructureSize || 
					symbolTableEntry instanceof Model.ReservedSymbol ||
					symbolTableEntry instanceof RbmObservable)) {
					continue;
				}
				if(simContext.getAssignmentRule(symbolTableEntry) != null) {
					continue;	// we don't allow assignment rule variables, they'll become functions in the math
				}
				if(symbolTableEntry instanceof Model.ModelParameter) {	// exclude global parameters that are rate or assignment rule variables
					if(simContext.getRateRule(symbolTableEntry) != null) {
						;		// it's a rate rule variable, we can use it
					} else {
						Model.ModelParameter mp = (Model.ModelParameter)symbolTableEntry;
						Expression exp = mp.getExpression();
						try {
							boolean isConstant = BioEvent.isConstantExpression(simContext, exp);
							if(!isConstant) {
//								System.out.println(mp.getName() + " - NO");		// skip this
								continue;
							}
//							System.out.println(mp.getName() + " - yes: ");
						} catch (ExpressionException e) {
//							System.out.println(mp.getName() + " - NO");			// skip this
							continue;
						}
					}
				}
				varNameList.add(varName);
			}
			Collections.sort(varNameList);
			return varNameList;
		}
		
		private void addEventAssignment() {

			populateVariableComboBoxModel(getVarNameComboBoxModel(),getSimulationContext()/*,true*/);
			
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
			int ok = JOptionPane.showOptionDialog(this, eventAssignmentPanel, "New Action Event" , 0, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, null);
			if (ok == javax.swing.JOptionPane.OK_OPTION) {
				String varName = (String)getEventAssignVarNameComboBox().getSelectedItem();
				EventAssignment newEventAssignment = null;
				try {
					SymbolTableEntry ste = fieldSimContext.getEntry(varName);
					Expression eventAssgnExp = new Expression(getEventAssignExpressionTextField().getText());
					newEventAssignment = fieldBioEvent.new EventAssignment((EditableSymbolTableEntry) ste, eventAssgnExp);
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

		public static AutoCompleteSymbolFilter getAutoCompleteFilter() {
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
			if (getBioEvent() == null) {
				setEnabled(false);
			} else {
				if (getSimulationContext().getGeometry() != null && getSimulationContext().getGeometry().getDimension() > 0 
						|| getSimulationContext().isStoch()) {
					getAddEventAssgnButton().setEnabled(false);
				}
				getDeleteEventAssgnButton().setEnabled(false);
								
				setEnabled(true);
				// we are initializing EventsPanel, hence no focuslistener should not be active on the text fields
//				getTriggerTextField().removeFocusListener(ivjEventHandler);
//				getDelayTextField().removeFocusListener(ivjEventHandler);

				// set 
//				getTriggerTextField().setText(fieldBioEvent.getTriggerExpression().infix());
				
//				try{
//					String expr = fieldBioEvent.getTrigger().getGeneratedExpression().infix();
//					getLabelTriggerType().setText(
//						getBioEvent().getTrigger().getClass().getSimpleName()+
//						" '"+
//						BeanUtils.forceStringSize(expr, 15, " ", false)+
//						(expr.length()>10?"...":"")+"'");
//				}catch(ExpressionException e){
//					e.printStackTrace();
//					getLabelTriggerType().setText(getBioEvent().getTrigger().getClass().getSimpleName());
//				}
				getLabelTriggerType().setText(getBioEvent().getTriggerType().name());
				LocalParameter delayParam = getBioEvent().getParameter(BioEventParameterType.TriggerDelay);
				if (delayParam != null) {
					if (delayParam.getExpression() != null){
						getDelayTextField().setText(delayParam.getExpression().infix());
					}
					enableTriggerRadioButtons(true);
					if(getBioEvent().getUseValuesFromTriggerTime()){
						getRdbtnTrigTime().setSelected(true);
					}else{
						getRdbtnDelayTime().setSelected(true);
					}
//					getUseValuesAtTriggerTimeCheckBox().setSelected(delay.useValuesFromTriggerTime());
				} else {
					getDelayTextField().setText("");
					enableTriggerRadioButtons(false);
				}
				getPersistenceCheckBox().setEnabled(false);
				getPersistenceCheckBox().setSelected(true);
				
				getEventAssignmentsTableModel().setSimulationContext(fieldSimContext);
				getEventAssignmentsTableModel().setBioEvent(fieldBioEvent);
				// for some reason, when events are selected in the table, the delayTextField gains focus (this fires table update whenever
				// another selection is made. To avoid this, adding a request focus for the events table.
//				requestFocusInWindow();
//				addFocusListener(new FocusListener() {					
//					public void focusLost(FocusEvent e) {						
//					}					
//					public void focusGained(FocusEvent e) {
//						removeFocusListener(this);
//						getTriggerTextField().addFocusListener(ivjEventHandler);
//						getDelayTextField().addFocusListener(ivjEventHandler);						
//					}
//				});
			}
			if(getRdbtnTrigTime().isSelected()) {	// change the column title based on selected radiobutton
				getEventTargetsScrollPaneTable().getColumnModel().getColumn(EventAssignmentsTableModel.COLUMN_EVENTASSIGN_EXPRESSION).setHeaderValue("Expression to evaluate at trigger time");
			} else {
				getEventTargetsScrollPaneTable().getColumnModel().getColumn(EventAssignmentsTableModel.COLUMN_EVENTASSIGN_EXPRESSION).setHeaderValue("Expression to evaluate at (trigger+delay) time");
			}
			getEventTargetsScrollPaneTable().getTableHeader().repaint();
		}

		private void enableTriggerRadioButtons(boolean isEnabled){
			getRdbtnDelayTime().setEnabled(isEnabled);
			getRdbtnTrigTime().setEnabled(isEnabled);
		}
		
		private static void setNewDelay(BioEvent bioEvent,String text,SimulationContext simulationContext,boolean bUseValuesFromTriggerTime) throws ExpressionException, PropertyVetoException{
//			try {
				if (bioEvent == null) {
					return;
				}
				bioEvent.setParameterValue(BioEventParameterType.TriggerDelay, new Expression(text));
				bioEvent.setUseValuesFromTriggerTime(bUseValuesFromTriggerTime);
				
//			} catch (ExpressionException e1) {
//				e1.printStackTrace(System.out);
//				//This stuff done because focus switches when dialog is shown
//				try{
//					synchronized (bErrorDialog){
//						bErrorDialog = true;
//						DialogUtils.showErrorDialog(EventPanel.this, e1.getMessage());
//					}
//				}finally{
//					synchronized (bErrorDialog){
//						bErrorDialog = false;
//					}
//				}
//			}
		}

		public static Expression bindTriggerExpression(String expr,SimulationContext simulationContext) throws ExpressionBindingException,ExpressionException{
			Expression triggerExpr = new Expression(expr);
			triggerExpr.bindExpression(simulationContext);
			return triggerExpr;
		}
				
	@Override
	public void setEnabled(boolean enabled) {		
		if (!enabled) {
//			getTriggerTextField().setText(null);
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
						ElectricalStimulusPanel.graphTimeFunction(
								EventPanel.this,
								getBioEvent().generateTriggerExpression(),
//								bindTriggerExpression(getTriggerTextField().getText(), getSimulationContext()),
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
	
	private JCheckBox getPersistenceCheckBox() {
		if(persistenceCheckBox == null) {
			persistenceCheckBox = new JCheckBox("Persistent Trigger");
			persistenceCheckBox.setToolTipText("Trigger condition is checked at trigger time only");
			persistenceCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateEventPanel();
				}
			});

		}
		return persistenceCheckBox;
	}
	
	private JRadioButton getRdbtnTrigTime() {
		if (rdbtnTrigTime == null) {
			rdbtnTrigTime = new JRadioButton("Evaluate at trigger time");
			rdbtnTrigTime.setToolTipText("Action expression is evaluated at trigger time");
		}
		return rdbtnTrigTime;
	}
	private JRadioButton getRdbtnDelayTime() {
		if (rdbtnDelayTime == null) {
			rdbtnDelayTime = new JRadioButton("Evaluate after delay");
			rdbtnDelayTime.setToolTipText("Action expression is evaluated at (trigger + delay) time");
		}
		return rdbtnDelayTime;
	}
	private JPanel getTriggerEvalPanel() {
		if (triggerEvalPanel == null) {
			triggerEvalPanel = new JPanel();
			triggerEvalPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			GridBagLayout gbl_triggerEvalPanel = new GridBagLayout();
			gbl_triggerEvalPanel.columnWidths = new int[]{0, 0};
			gbl_triggerEvalPanel.rowHeights = new int[]{0};
			gbl_triggerEvalPanel.columnWeights = new double[]{0, 0.0};
			gbl_triggerEvalPanel.rowWeights = new double[]{0.0};
			triggerEvalPanel.setLayout(gbl_triggerEvalPanel);
			GridBagConstraints gbc_rdbtnTrigTime = new GridBagConstraints();
			gbc_rdbtnTrigTime.anchor = GridBagConstraints.WEST;
			gbc_rdbtnTrigTime.insets = new Insets(0, 0, 0, 5);
			gbc_rdbtnTrigTime.gridx = 0;
			gbc_rdbtnTrigTime.gridy = 0;
			triggerEvalPanel.add(getRdbtnTrigTime(), gbc_rdbtnTrigTime);
			GridBagConstraints gbc_rdbtnDelayTime = new GridBagConstraints();
			gbc_rdbtnDelayTime.gridx = 1;
			gbc_rdbtnDelayTime.gridy = 0;
			triggerEvalPanel.add(getRdbtnDelayTime(), gbc_rdbtnDelayTime);
		}
		return triggerEvalPanel;
	}
	private JLabel getLabelTriggerType() {
		if (labelTriggerType == null) {
			labelTriggerType = new JLabel("Trigger Type");
		}
		return labelTriggerType;
	}
	private JButton getButtonEditTrigger() {
		if (buttonEditTrigger == null) {
			buttonEditTrigger = new JButton("Edit Trigger...");
			buttonEditTrigger.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					EventsDisplayPanel.createOrEditTrigger(EventPanel.this, getSimulationContext(), getBioEvent());
					updateEventPanel();
				}
			});
		}
		return buttonEditTrigger;
	}
}
