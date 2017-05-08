/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.ClientTaskDispatcher.BlockingTimer;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.Function;
import cbit.vcell.math.InconsistentDomainException;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.ReservedMathSymbolEntries;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.AnnotatedFunction.FunctionCategory;

@SuppressWarnings("serial")
public class OutputFunctionsPanel extends DocumentEditorSubPanel {
	private JPanel buttons_n_label_Panel = null;
	private JButton addButton = null;
	private JButton deleteButton = null;
	private JSortTable outputFnsScrollPaneTable = null;
	private JLabel functionExprLabel = null;
	private JLabel functionNameLabel = null;
	private TextFieldAutoCompletion functionExpressionTextField = null;
	private JTextField functionNameTextField = null;
	private JPanel functionPanel = null;
	private final CardLayout cardLayout = new CardLayout();
	private JPanel funcNameAndExprPanel = null; 
	private JPanel geometryClassPanel = null;
	private OutputFunctionsListTableModel outputFnsListTableModel = null;
	private OutputFunctionContext outputFunctionContext = null;
	private SimulationWorkspace simulationWorkspace = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JComboBox subdomainComboBox = null;
	private JLabel subdomainLabel = null;
	private JButton previousButton = null;
	private JButton finishButton = null;
	private JButton nextButton = null;
	private JButton cancelFnNameExprPanelButton = null;
	private JButton cancelGeomClassPanelButton = null;
	private JDialog addFunctionDialog;

	private BlockingTimer outputContextAddFnBtnTimer;
	private BlockingTimer outputContextDelFnBtnTimer;
	private class IvjEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				if (e.getSource() == OutputFunctionsPanel.this.getAddFnButton()){
					if((outputContextAddFnBtnTimer = ClientTaskDispatcher.getBlockingTimer(OutputFunctionsPanel.this, null, null, outputContextAddFnBtnTimer, new ActionListener() {@Override public void actionPerformed(ActionEvent e2){ivjEventHandler.actionPerformed(e);}},"OutputFunctionsPanel add new Function...")) != null){
						return;
					}
					addOutputFunction();
				}
				if (e.getSource() == OutputFunctionsPanel.this.getDeleteFnButton()){
					if((outputContextDelFnBtnTimer = ClientTaskDispatcher.getBlockingTimer(OutputFunctionsPanel.this, null, null, outputContextDelFnBtnTimer, new ActionListener() {@Override public void actionPerformed(ActionEvent e2){ivjEventHandler.actionPerformed(e);}},"OutputFunctionsPanel delete Function '"+getSelectedFunction()+"'")) != null){
						return;
					}
					deleteOutputFunction();	
				}
				if (e.getSource() == previousButton) {
					cardLayout.show(functionPanel, funcNameAndExprPanel.getName());
				} else if (e.getSource() == finishButton) {
					try {
						addFunction();
					} catch (Exception e1) {
						e1.printStackTrace(System.out);
						DialogUtils.showErrorDialog(OutputFunctionsPanel.this, "Function '" + getFunctionNameTextField().getText() + "' cannot be added.\n\t" + e1.getMessage(), e1);
						return;
					}
					getAddFunctionDialog().dispose();
				} else if (e.getSource() == cancelFnNameExprPanelButton || e.getSource() == cancelGeomClassPanelButton) {
					getAddFunctionDialog().dispose();
				} else if (e.getSource() == nextButton) {
					if (outputFunctionContext.getSimulationOwner().getGeometry().getDimension() > 0) {
						try {
							nextButtonClicked();
						} catch (Exception ex) {
							ex.printStackTrace(System.out);
							DialogUtils.showErrorDialog(OutputFunctionsPanel.this, ex.getMessage());
							return;
						}
					} else {
						try {
							addFunction();
						} catch (Exception e1) {
							e1.printStackTrace(System.out);
							DialogUtils.showErrorDialog(OutputFunctionsPanel.this, "Function '" + getFunctionNameTextField().getText() + "' cannot be added.\n\t" + e1.getMessage(), e1);
							return;
						}
						getAddFunctionDialog().dispose();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
				DialogUtils.showErrorDialog(OutputFunctionsPanel.this, ex.getMessage(), ex);
			}
		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == OutputFunctionsPanel.this && (evt.getPropertyName().equals("outputFunctionContext"))) {
				if (outputFunctionContext != null && outputFunctionContext.getSimulationOwner() != null) {
					getFunctionExpressionTextField().setAutoCompleteSymbolFilter(outputFunctionContext.getAutoCompleteSymbolFilter());
					outputFnsListTableModel.setOutputFunctionContext(outputFunctionContext);
				}
			}
			if (evt.getSource() == OutputFunctionsPanel.this && (evt.getPropertyName().equals("simulationWorkspace"))) {
				SimulationWorkspace sw_old = (SimulationWorkspace)evt.getOldValue();
				SimulationWorkspace sw_new = (SimulationWorkspace)evt.getNewValue();
				if (sw_old != null) {
					sw_old.removePropertyChangeListener(this);
					sw_old.getSimulationOwner().removePropertyChangeListener(this);
				} 
				if (sw_new != null) {
					sw_new.addPropertyChangeListener(this);
					sw_new.getSimulationOwner().addPropertyChangeListener(this);			
					setOutputFunctionContext(sw_new.getSimulationOwner().getOutputFunctionContext());
				} else {
					setOutputFunctionContext(null);
				}
			}
			if (getSimulationWorkspace() != null && (evt.getPropertyName().equals("geometry"))) {
			}
			if (evt.getSource() == getSimulationWorkspace() && (evt.getPropertyName().equals("simulationOwner"))) {
				SimulationOwner so_new = (SimulationOwner)evt.getNewValue();
				if (so_new != null) {
					setOutputFunctionContext(so_new.getOutputFunctionContext());
				} else {
					setOutputFunctionContext(null);
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == OutputFunctionsPanel.this.getFnScrollPaneTable().getSelectionModel()) {
				int row = getFnScrollPaneTable().getSelectedRow();
				if (row < 0) {
					return;
				}
				enableDeleteFnButton();
				tableSelectionChanged();
			}
		}
	};
	
	public OutputFunctionsPanel() {
		super();
		initialize();
	}

	private javax.swing.JButton getAddFnButton() {
		if (addButton == null) {
			try {
				addButton = new javax.swing.JButton();
				addButton.setName("AddFnButton");
				addButton.setText("Add Function");
				addButton.setEnabled(true);
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return addButton;
	}

	private javax.swing.JButton getDeleteFnButton() {
		if (deleteButton == null) {
			try {
				deleteButton = new javax.swing.JButton();
				deleteButton.setName("DeleteFnButton");
				deleteButton.setText("Delete Function");
				deleteButton.setEnabled(false);
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return deleteButton;
	}

	private javax.swing.JButton getNextButton() {
		if (nextButton == null) {
			try {
				nextButton = new javax.swing.JButton();
				nextButton.setName("NextButton");
				nextButton.setText("Next >>");
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return nextButton;
	}
	
	private javax.swing.JPanel getButtonPanel() {
		if (buttons_n_label_Panel == null) {
			try {
				buttons_n_label_Panel = new javax.swing.JPanel();
				buttons_n_label_Panel.setName("ButtonPanel");
				buttons_n_label_Panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
				buttons_n_label_Panel.add(getAddFnButton());
				buttons_n_label_Panel.add(getDeleteFnButton());
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return buttons_n_label_Panel;
	}

	private JSortTable getFnScrollPaneTable() {
		if (outputFnsScrollPaneTable == null) {
			try {
				outputFnsScrollPaneTable = new JSortTable();
				outputFnsScrollPaneTable.setName("ScrollPaneTable");
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return outputFnsScrollPaneTable;
	}

	private TextFieldAutoCompletion getFunctionExpressionTextField() {
		if (functionExpressionTextField == null) {
			try {
				functionExpressionTextField = new TextFieldAutoCompletion();
				functionExpressionTextField.setName("FunctionExpressionTextField");
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionExpressionTextField;
	}

	private JComboBox getSubdomainComboBox() {
		if (subdomainComboBox == null) {
			try {
				subdomainComboBox = new JComboBox();
				subdomainComboBox.setRenderer(new DefaultListCellRenderer() {

					@Override
					public Component getListCellRendererComponent(JList list,
							Object value, int index, boolean isSelected,
							boolean cellHasFocus) {
						super.getListCellRendererComponent(list, value, index, isSelected,
								cellHasFocus);
						if (value instanceof GeometryClass) {
							setText(((GeometryClass)value).getName());
						} else if (value instanceof VariableType) {
							setText(((VariableType) value).getTypeName());
						}
						return this;
					}
					
				});
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return subdomainComboBox;
	}

	/**
	 * Return the FunctionExprLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getFunctionExprLabel() {
		if (functionExprLabel == null) {
			try {
				functionExprLabel = new javax.swing.JLabel();
				functionExprLabel.setName("FunctionExprLabel");
				functionExprLabel.setText("Function Expression");
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionExprLabel;
	}


	/**
	 * Return the FunctionNameLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getFunctionNameLabel() {
		if (functionNameLabel == null) {
			try {
				functionNameLabel = new javax.swing.JLabel();
				functionNameLabel.setName("FunctionNameLabel");
				functionNameLabel.setText("Function Name");
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionNameLabel;
	}


	/**
	 * Return the JTextField1 property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getFunctionNameTextField() {
		if (functionNameTextField == null) {
			try {
				functionNameTextField = new javax.swing.JTextField();
				functionNameTextField.setName("FunctionNameTextField");
				functionNameTextField.setColumns(40);
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionNameTextField;
	}

	private javax.swing.JPanel getAddFunctionPanel() {
		if (functionPanel == null) {
			try {
				functionPanel = new javax.swing.JPanel();
				functionPanel.setName("FunctionPanel");
				functionPanel.setLayout(cardLayout);

				// first page of card layout : function name and expression page
				funcNameAndExprPanel = new JPanel();
				funcNameAndExprPanel.setName("Function and Name Expression");
				funcNameAndExprPanel.setName("FuncNameAndExprPanel");
				funcNameAndExprPanel.setLayout(new java.awt.GridBagLayout());
				int gridy = 0;
				
				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gridy ++;
				java.awt.GridBagConstraints constraintsFunctionNameLabel = new java.awt.GridBagConstraints();
				constraintsFunctionNameLabel.gridx = 0; constraintsFunctionNameLabel.gridy = gridy;
				constraintsFunctionNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				constraintsFunctionNameLabel.anchor = GridBagConstraints.LINE_END;
				funcNameAndExprPanel.add(getFunctionNameLabel(), constraintsFunctionNameLabel);

				java.awt.GridBagConstraints constraintsFunctionNameTextField = new java.awt.GridBagConstraints();
				constraintsFunctionNameTextField.gridx = 1; constraintsFunctionNameTextField.gridy = gridy;
				constraintsFunctionNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsFunctionNameTextField.weightx = 1.0;
				constraintsFunctionNameTextField.gridwidth = 2;
				constraintsFunctionNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				funcNameAndExprPanel.add(getFunctionNameTextField(), constraintsFunctionNameTextField);

				gridy ++;
				java.awt.GridBagConstraints constraintsFunctionExprLabel = new java.awt.GridBagConstraints();
				constraintsFunctionExprLabel.gridx = 0; constraintsFunctionExprLabel.gridy = gridy;
				constraintsFunctionExprLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				constraintsFunctionExprLabel.anchor = GridBagConstraints.LINE_END;
				funcNameAndExprPanel.add(getFunctionExprLabel(), constraintsFunctionExprLabel);

				java.awt.GridBagConstraints constraintsFunctionExpressionTextField = new java.awt.GridBagConstraints();
				constraintsFunctionExpressionTextField.gridx = 1; constraintsFunctionExpressionTextField.gridy = gridy;
				constraintsFunctionExpressionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsFunctionExpressionTextField.gridwidth = 2;
				constraintsFunctionExpressionTextField.weightx = 1.0;
				constraintsFunctionExpressionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				funcNameAndExprPanel.add(getFunctionExpressionTextField(), constraintsFunctionExpressionTextField);

				// 'next' button for funcNameAndExprPanel
				gridy++;
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 1; gbc.gridy = gridy;
				gbc.weightx = 1.0;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				funcNameAndExprPanel.add(getNextButton(), gbc);

				// cancel button for funcNameAndExprPanel
				cancelFnNameExprPanelButton = new JButton("Cancel");
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 2; gbc.gridy = gridy;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				funcNameAndExprPanel.add(cancelFnNameExprPanelButton, gbc);

				// second page of the card layout : domain/geometryClass selection page.
				geometryClassPanel = new JPanel();
				geometryClassPanel.setName("Function Domain");
				geometryClassPanel.setName("GeometryClassPanel");
				geometryClassPanel.setLayout(new java.awt.GridBagLayout());
				gridy = 0;
				
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; gbc.gridy = gridy;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				geometryClassPanel.add(getSubdomainLabel(), gbc);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 1; gbc.gridy = gridy;
				gbc.anchor = GridBagConstraints.LINE_START;
				gbc.weightx = 1.0;
				gbc.gridwidth = 3;
				gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				geometryClassPanel.add(getSubdomainComboBox(), gbc);

				// 'previous' button for GeometryClassPanel
				gridy++;
				previousButton = new JButton("<< Previous");
				previousButton.addActionListener(ivjEventHandler);
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 1; gbc.gridy = gridy;
				gbc.weightx = 1.0;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				geometryClassPanel.add(previousButton, gbc);

				// 'finish' button for GeometryClassPanel
				finishButton = new JButton("Finish");
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 2; gbc.gridy = gridy;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				geometryClassPanel.add(finishButton, gbc);

				// 'cancel' button for GeometryClassPanel
				cancelGeomClassPanelButton = new JButton("Cancel");
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 3; gbc.gridy = gridy;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				geometryClassPanel.add(cancelGeomClassPanelButton, gbc);

				functionPanel.add(funcNameAndExprPanel, funcNameAndExprPanel.getName());
				functionPanel.add(geometryClassPanel, geometryClassPanel.getName());

				finishButton.addActionListener(ivjEventHandler);
				getNextButton().addActionListener(ivjEventHandler);
				cancelFnNameExprPanelButton.addActionListener(ivjEventHandler);
				cancelGeomClassPanelButton.addActionListener(ivjEventHandler);

			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionPanel;
	}
	
	

	private JLabel getSubdomainLabel() {
		if (subdomainLabel == null) {
			subdomainLabel  = new JLabel("Defined In:");
		}
		return subdomainLabel;
	}
	
	private void initConnections() {
		getAddFnButton().addActionListener(ivjEventHandler);
		getDeleteFnButton().addActionListener(ivjEventHandler);
		getFnScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
		this.addPropertyChangeListener(ivjEventHandler);
		getSubdomainComboBox().addActionListener(ivjEventHandler);

		// for scrollPaneTable, set tableModel and create default columns
		outputFnsListTableModel = new OutputFunctionsListTableModel(outputFnsScrollPaneTable);
		getFnScrollPaneTable().setModel(outputFnsListTableModel);
		getFnScrollPaneTable().createDefaultColumnsFromModel();
		getFnScrollPaneTable().setDefaultRenderer(String.class, new DefaultScrollTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (column == OutputFunctionsListTableModel.COLUMN_OUTPUTFN_NAME) {
					setIcon(VCellIcons.getOutputFunctionIcon());
				} else {
					setIcon(null);
				}
				return this;
			}
		});
	}

	private void initialize() {
		try {
			setName("OutputFunctionsListPanel");
			setSize(750, 100);
			setLayout(new BorderLayout());
			add(getButtonPanel(), BorderLayout.NORTH);
			add(getFnScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
			initConnections();
			getFnScrollPaneTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable e) {
			e.printStackTrace(System.out);
		}
	}

	
	private ArrayList<Object> getPossibleGeometryClassesAndVariableTypes(Expression expr) throws ExpressionException, InconsistentDomainException {
		SimulationOwner simulationOwner = getSimulationWorkspace().getSimulationOwner();
		MathDescription mathDescription = simulationOwner.getMathDescription();
		boolean bSpatial = simulationOwner.getGeometry().getDimension() > 0;
		if (!bSpatial) {
			return null;
		}
		// making sure that output function is not direct function of constant.
		expr.bindExpression(outputFunctionContext);
		
		// here use math description as symbol table because we allow 
		// new expression itself to be function of constant.
		expr = MathUtilities.substituteFunctions(expr, outputFunctionContext).flatten();
		String[] symbols = expr.getSymbols();
		// using bit operation to determine whether geometry classes for symbols in expression are vol, membrane or both. 01 => vol; 10 => membrane; 11 => both 
		int gatherFlag = 0;		 
		Set<GeometryClass> geomClassSet = new HashSet<GeometryClass>();
		ArrayList<Object> objectsList = new ArrayList<Object>();
		boolean bHasVariable = false;
		VariableType[] varTypes = null;
		if (symbols != null && symbols.length > 0) {
			// making sure that new expression is defined in the same domain
			varTypes = new VariableType[symbols.length];
			for (int i = 0; i < symbols.length; i++) {
				if (ReservedMathSymbolEntries.getReservedVariableEntry(symbols[i]) != null) {
					varTypes[i] = VariableType.VOLUME;
				} else {
					Variable var = mathDescription.getVariable(symbols[i]);
					if (var == null){
						var = mathDescription.getPostProcessingBlock().getDataGenerator(symbols[i]);
					}
					varTypes[i] = VariableType.getVariableType(var);
					bHasVariable = true;
					if (var.getDomain() != null) {
						GeometryClass varGeoClass = simulationOwner.getGeometry().getGeometryClass(var.getDomain().getName());
						geomClassSet.add(varGeoClass);
						if (varGeoClass instanceof SubVolume) {
							gatherFlag |= 1; 
						} else if (varGeoClass instanceof SurfaceClass){
							gatherFlag |= 2;
						}
					}
					if (varTypes[i].equals(VariableType.POSTPROCESSING)){
						gatherFlag |= 4;
					}
				}
			}
		}
		if (gatherFlag > 4) {
			throw new RuntimeException("cannot mix post processing variables with membrane or volume variables");
		}
		int numGeomClasses = geomClassSet.size();
		if (numGeomClasses == 0) {
			if (bHasVariable) {
				// if there are no variables (like built in function, vcRegionArea), check with flattened expression to find out the variable type of the new expression
				Function flattenedFunction = new Function(getFunctionNameTextField().getText(), expr, null);
				flattenedFunction.bind(outputFunctionContext);
				VariableType newVarType = SimulationSymbolTable.getFunctionVariableType(flattenedFunction, simulationOwner.getMathDescription(), symbols, varTypes, bSpatial);
				objectsList.add(newVarType);
			} else {
				objectsList.add(VariableType.VOLUME);
				objectsList.add(VariableType.MEMBRANE);
			}
		} else if (numGeomClasses == 1) {
			objectsList.add(geomClassSet.iterator().next());
			if (gatherFlag == 1) {
				objectsList.add(VariableType.MEMBRANE);
			}
		} else if (gatherFlag == 1) {		//  all volumes
			if (numGeomClasses == 2) {
				// all subvolumes, if there are only 2, check for adjacency.
				GeometryClass[] geomClassesArray = geomClassSet.toArray(new GeometryClass[0]);
				SurfaceClass sc = simulationOwner.getGeometry().getGeometrySurfaceDescription().getSurfaceClass((SubVolume)geomClassesArray[0], (SubVolume)geomClassesArray[1]);
				if (sc != null) {
					objectsList.add(sc);
				}
			} 
			objectsList.add(VariableType.VOLUME);
		} else if (gatherFlag == 2) {		// all membranes 
			objectsList.add(VariableType.MEMBRANE);
		} else if (gatherFlag == 3) {		// mixed - both vols and membranes
			// add only membranes? 
			objectsList.add(VariableType.MEMBRANE);
		}
		return objectsList;
	}
	
	private void nextButtonClicked() throws ExpressionException, InconsistentDomainException {
		boolean bSpatial = simulationWorkspace.getSimulationOwner().getGeometry().getDimension() > 0;
		
		if (bSpatial) {
			DefaultComboBoxModel aModel = new DefaultComboBoxModel();
			ArrayList<Object> objectsList = null;
			
			String exprStr = getFunctionExpressionTextField().getText();
			if (exprStr == null) {
				throw new ExpressionException("No expression provided for output function.");
			}
			Expression expr = new Expression(exprStr);

			objectsList = getPossibleGeometryClassesAndVariableTypes(expr);

			for (Object ob : objectsList) {
				aModel.addElement(ob);
			}
			getSubdomainComboBox().setModel(aModel);
			getSubdomainComboBox().setSelectedIndex(0);
		}

		cardLayout.show(functionPanel, geometryClassPanel.getName());
	}
	
	private void addOutputFunction() {
		if (simulationWorkspace == null) {
			return;
		}
		AsynchClientTask task1 = new AsynchClientTask("refresh math description", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				MathMappingCallback mathMappingCallback = new MathMappingCallbackTaskAdapter(getClientTaskStatusSupport());
				if (simulationWorkspace.getSimulationOwner() instanceof SimulationContext){
					SimulationContext simulationContext = (SimulationContext)simulationWorkspace.getSimulationOwner();
					simulationContext.refreshMathDescription(mathMappingCallback,NetworkGenerationRequirements.AllowTruncatedStandardTimeout);
				} // else, for mathModels, nothing to refresh.
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("show dialog", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ArrayList<AnnotatedFunction> outputFunctionList = outputFunctionContext.getOutputFunctionsList();
				String defaultName = null;
				int count = 0;
				while (true) {
					boolean nameUsed = false;
					count++;
					defaultName = "func" + count;
					for (AnnotatedFunction function : outputFunctionList){
						if (function.getName().equals(defaultName)) {
							nameUsed = true;
						}
					}
					if (!nameUsed) {
						break;
					}
				}
		
				final boolean bSpatial = simulationWorkspace.getSimulationOwner().getGeometry().getDimension() > 0;
				// for non-spatial application, set 'Next' to 'Finish'.
				if (!bSpatial) {
					getNextButton().setText("Finish");
				} else {
					getNextButton().setText("Next >>");
				}

				getFunctionNameTextField().setText(defaultName);
				getFunctionExpressionTextField().setText("0.0");
				Set<String> autoCompList = new HashSet<String>();
				Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
				outputFunctionContext.getEntries(entryMap);
				autoCompList = entryMap.keySet();
				getFunctionExpressionTextField().setAutoCompletionWords(autoCompList);
				getFunctionExpressionTextField().setSymbolTable(outputFunctionContext);
				//
				// Show the editor with a default name and default expression for the function
				// If the OK option is chosen, get the new name and expression for the function and create a new
				// function, add it to the list of output functions in simulationOwner 
				// Else, pop-up an error dialog indicating that function cannot be added.
				//
				cardLayout.show(getAddFunctionPanel(), funcNameAndExprPanel.getName());
				DialogUtils.showModalJDialogOnTop(getAddFunctionDialog(), OutputFunctionsPanel.this);
			}
		};
		ClientTaskDispatcher.dispatch(OutputFunctionsPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });
	}

	private void addFunction() throws Exception {
		String funcName = getFunctionNameTextField().getText();
		Expression funcExp = null;
		funcExp = new Expression(getFunctionExpressionTextField().getText());
		Domain domain = null;
		VariableType newFunctionVariableType = null;
		boolean bSpatial = simulationWorkspace.getSimulationOwner().getGeometry().getDimension() > 0;
		if (bSpatial) {
			Object selectedItem = getSubdomainComboBox().getSelectedItem();
			if (selectedItem instanceof GeometryClass) {
				GeometryClass geoClass = (GeometryClass)selectedItem;
				domain = new Domain(geoClass);
				if (selectedItem instanceof SubVolume) {
					newFunctionVariableType = VariableType.VOLUME;
				} else if (selectedItem instanceof SurfaceClass) {
					newFunctionVariableType = VariableType.MEMBRANE;
				}
			} else if (selectedItem instanceof VariableType) {
				newFunctionVariableType = (VariableType) selectedItem;
			} else {
				newFunctionVariableType = VariableType.UNKNOWN;
			}
		} else {
			newFunctionVariableType = VariableType.NONSPATIAL;
		}
		AnnotatedFunction tempFunction = new AnnotatedFunction(funcName, funcExp, domain, null, newFunctionVariableType, FunctionCategory.OUTPUTFUNCTION);
	
		VariableType vt = outputFunctionContext.computeFunctionTypeWRTExpression(tempFunction, funcExp);

		FunctionCategory category = FunctionCategory.OUTPUTFUNCTION;
		if (vt.equals(VariableType.POSTPROCESSING)){
			category = FunctionCategory.POSTPROCESSFUNCTION;
		}
		
		AnnotatedFunction newFunction = new AnnotatedFunction(funcName, funcExp, domain, null, vt, category);
		
		outputFunctionContext.addOutputFunction(newFunction);
		setSelectedObjects(new Object[] {newFunction});
		enableDeleteFnButton();
	}		
	private AnnotatedFunction getSelectedFunction(){
		int selectedRow = getFnScrollPaneTable().getSelectedRow();
		if (selectedRow > -1) {
			return outputFnsListTableModel.getValueAt(selectedRow);
		}
		return null;
	}
	private void deleteOutputFunction() {
		if (getSelectedFunction() != null) {
			AnnotatedFunction function = getSelectedFunction();
			String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting Output Function", "You are going to delete the Output Function '" + function.getName() + "'. Continue?");
			if (confirm.equals(UserMessage.OPTION_CANCEL)) {
				return;
			}
			try {
				outputFunctionContext.removeOutputFunction(function);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, " Deletion of function '" + function.getName() + "' failed." + e1.getMessage());
			}
		}
		enableDeleteFnButton();
	}

	private void setOutputFunctionContext(OutputFunctionContext argOutputFnContext) {
		OutputFunctionContext oldValue = this.outputFunctionContext; 
		this.outputFunctionContext = argOutputFnContext;
		firePropertyChange("outputFunctionContext", oldValue, argOutputFnContext);
	}

	private void enableDeleteFnButton() {
		if (getFnScrollPaneTable().getSelectedRow() > -1) {
			getDeleteFnButton().setEnabled(true);
		} else {
			getDeleteFnButton().setEnabled(false);
		}
	}

	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			OutputFunctionsPanel anOutputFnsPanel = new OutputFunctionsPanel();
			frame.setContentPane(anOutputFnsPanel);
			frame.setSize(anOutputFnsPanel.getSize());
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

	public SimulationWorkspace getSimulationWorkspace() {
		return simulationWorkspace;
	}

	public void setSimulationWorkspace(SimulationWorkspace argSimulationWorkspace) {
		SimulationWorkspace oldValue = this.simulationWorkspace; 
		this.simulationWorkspace = argSimulationWorkspace;
		firePropertyChange("simulationWorkspace", oldValue, argSimulationWorkspace);
	}
	
	private Domain getNewFunctionDomain() {
		return new Domain((GeometryClass)(getSubdomainComboBox().getSelectedItem()));
	}
	
	public void tableSelectionChanged() {
		setSelectedObjectsFromTable(getFnScrollPaneTable(), outputFnsListTableModel);		
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, getFnScrollPaneTable(), outputFnsListTableModel);
	}

	@Override
	public void setIssueManager(IssueManager newValue) {
		super.setIssueManager(newValue);
		outputFnsListTableModel.setIssueManager(newValue);
	}

	private JDialog getAddFunctionDialog() {
		if (addFunctionDialog == null) {
			addFunctionDialog = new JDialog(JOptionPane.getFrameForComponent(OutputFunctionsPanel.this));
			addFunctionDialog.setResizable(true);
			addFunctionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			addFunctionDialog.setTitle("Add Output Function");
			addFunctionDialog.setModal(true);
			addFunctionDialog.getContentPane().add(getAddFunctionPanel());
			addFunctionDialog.pack();
			BeanUtils.centerOnComponent(addFunctionDialog, this);
		}
		return addFunctionDialog;
	}	
}
