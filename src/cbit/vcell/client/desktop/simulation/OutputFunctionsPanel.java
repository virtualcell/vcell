package cbit.vcell.client.desktop.simulation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.AnnotatedFunction.FunctionCategory;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.VariableType;

@SuppressWarnings("serial")
public class OutputFunctionsPanel extends JPanel {
	public static final String PROPERTY_SELECTED_OUTPUT_FUNCTION = "selectedOutputFunction";
	private JPanel buttons_n_label_Panel = null;
	private JButton addButton = null;
	private JButton deleteButton = null;
	private JSortTable outputFnsScrollPaneTable = null;
	private JLabel functionExprLabel = null;
	private JLabel functionNameLabel = null;
	private TextFieldAutoCompletion functionExpressionTextField = null;
	private JTextField functionNameTextField = null;
	private JPanel functionPanel = null;
	private OutputFunctionsListTableModel outputFnsListTableModel1 = null;
	private OutputFunctionContext outputFunctionContext = null;
	private SimulationWorkspace simulationWorkspace = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JComboBox subdomainComboBox = null;
	private JLabel subdomainLabel = null;

	private class IvjEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				if (e.getSource() == OutputFunctionsPanel.this.getAddFnButton()) 
					addOutputFunction();
				if (e.getSource() == OutputFunctionsPanel.this.getDeleteFnButton()) 
					deleteOutputFunction();	
				if (e.getSource() == getSubdomainComboBox()) {
					getFunctionExpressionTextField().setAutoCompleteSymbolFilter(outputFunctionContext.getAutoCompleteSymbolFilter(getNewFunctionDomain()));
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
				DialogUtils.showErrorDialog(OutputFunctionsPanel.this, ex.getMessage(), ex);
			}
		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == OutputFunctionsPanel.this && (evt.getPropertyName().equals("outputFunctionContext"))) {
				if (outputFunctionContext != null && outputFunctionContext.getSimulationOwner() != null) { 
					getOutputFnsListTableModel1().setOutputFunctionContext(outputFunctionContext);
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
					if (sw_new.getSimulationOwner() != null) {
						setOutputFunctionContext(sw_new.getSimulationOwner().getOutputFunctionContext());
					} else {
						setOutputFunctionContext(null);
					}
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
				functionPanel.setLayout(new java.awt.GridBagLayout());

				int gridy = 0;
				
				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; gbc.gridy = gridy;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				functionPanel.add(getSubdomainLabel(), gbc);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 1; gbc.gridy = gridy;
				gbc.anchor = GridBagConstraints.LINE_START;
				gbc.weightx = 1.0;
				gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				functionPanel.add(getSubdomainComboBox(), gbc);
				
				gridy ++;
				java.awt.GridBagConstraints constraintsFunctionNameLabel = new java.awt.GridBagConstraints();
				constraintsFunctionNameLabel.gridx = 0; constraintsFunctionNameLabel.gridy = gridy;
				constraintsFunctionNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				constraintsFunctionNameLabel.anchor = GridBagConstraints.LINE_END;
				functionPanel.add(getFunctionNameLabel(), constraintsFunctionNameLabel);

				java.awt.GridBagConstraints constraintsFunctionNameTextField = new java.awt.GridBagConstraints();
				constraintsFunctionNameTextField.gridx = 1; constraintsFunctionNameTextField.gridy = gridy;
				constraintsFunctionNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsFunctionNameTextField.weightx = 1.0;
				constraintsFunctionNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				functionPanel.add(getFunctionNameTextField(), constraintsFunctionNameTextField);

				gridy ++;
				java.awt.GridBagConstraints constraintsFunctionExprLabel = new java.awt.GridBagConstraints();
				constraintsFunctionExprLabel.gridx = 0; constraintsFunctionExprLabel.gridy = gridy;
				constraintsFunctionExprLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				constraintsFunctionExprLabel.anchor = GridBagConstraints.LINE_END;
				functionPanel.add(getFunctionExprLabel(), constraintsFunctionExprLabel);

				java.awt.GridBagConstraints constraintsFunctionExpressionTextField = new java.awt.GridBagConstraints();
				constraintsFunctionExpressionTextField.gridx = 1; constraintsFunctionExpressionTextField.gridy = gridy;
				constraintsFunctionExpressionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsFunctionExpressionTextField.weightx = 1.0;
				constraintsFunctionExpressionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				functionPanel.add(getFunctionExpressionTextField(), constraintsFunctionExpressionTextField);
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionPanel;
	}

	private JLabel getSubdomainLabel() {
		if (subdomainLabel == null) {
			subdomainLabel  = new JLabel("Subdomain");
		}
		return subdomainLabel;
	}

	private OutputFunctionsListTableModel getOutputFnsListTableModel1() {
		if (outputFnsListTableModel1 == null) {
			try {
				outputFnsListTableModel1 = new OutputFunctionsListTableModel(outputFnsScrollPaneTable);
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return outputFnsListTableModel1;
	}
	
	private void initConnections() {
		getAddFnButton().addActionListener(ivjEventHandler);
		getDeleteFnButton().addActionListener(ivjEventHandler);
		getFnScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
		this.addPropertyChangeListener(ivjEventHandler);
		getSubdomainComboBox().addActionListener(ivjEventHandler);

		// for scrollPaneTable, set tableModel and create default columns
		getFnScrollPaneTable().setModel(getOutputFnsListTableModel1());
		getFnScrollPaneTable().createDefaultColumnsFromModel();
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

	private void addOutputFunction() {
		AsynchClientTask task1 = new AsynchClientTask("refresh math description", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				simulationWorkspace.getSimulationOwner().refreshMathDescription();
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
		
				boolean bSpatial = simulationWorkspace.getSimulationOwner().getGeometry().getDimension() > 0;
				getSubdomainComboBox().setVisible(bSpatial);
				getSubdomainLabel().setVisible(bSpatial);
				
				if (bSpatial) {
					DefaultComboBoxModel aModel = new DefaultComboBoxModel();
					GeometryClass[] geometryClasses = simulationWorkspace.getSimulationOwner().getGeometry().getGeometryClasses();
					for (GeometryClass gc : geometryClasses){
						aModel.addElement(gc);
					}
					getSubdomainComboBox().setModel(aModel);
					getSubdomainComboBox().setSelectedIndex(0);
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
				int ok = DialogUtils.showComponentOKCancelDialog(OutputFunctionsPanel.this, getAddFunctionPanel(), "Add Function");
				if (ok == javax.swing.JOptionPane.OK_OPTION) {
					String funcName = getFunctionNameTextField().getText();
					Expression funcExp = null;
					try {
						funcExp = new Expression(getFunctionExpressionTextField().getText());
					} catch (ExpressionException e) {
						e.printStackTrace(System.out);
					}
					Domain domain = null;
					VariableType newFunctionVariableType = null;
					if (bSpatial) {
						GeometryClass geoClass = (GeometryClass)getSubdomainComboBox().getSelectedItem();
						domain = new Domain(geoClass);
						if (getSubdomainComboBox().getSelectedItem() instanceof SubVolume) {
							newFunctionVariableType = VariableType.VOLUME;
						} else {
							newFunctionVariableType = VariableType.MEMBRANE;
						}
					} else {
						newFunctionVariableType = VariableType.NONSPATIAL;
					}
					AnnotatedFunction newFunction = new AnnotatedFunction(funcName, funcExp, domain, null, newFunctionVariableType, FunctionCategory.OUTPUTFUNCTION);
					try {
						VariableType vt = outputFunctionContext.computeFunctionTypeWRTExpression(newFunction, funcExp);
						if (!vt.compareEqual(newFunctionVariableType)) {
							newFunction = new AnnotatedFunction(funcName, funcExp, domain, null, vt, FunctionCategory.OUTPUTFUNCTION);
						}
						outputFunctionContext.addOutputFunction(newFunction);
						outputFnsListTableModel1.selectOutputFunction(newFunction);
					} catch (Exception e1) {
						e1.printStackTrace(System.out);
						DialogUtils.showErrorDialog(OutputFunctionsPanel.this, "Function '" + newFunction.getName() + "' cannot be added. " + e1.getMessage(), e1);
					}
				}		
				enableDeleteFnButton();
			}
		};
		ClientTaskDispatcher.dispatch(OutputFunctionsPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });
	}

	private void deleteOutputFunction() {
		int selectedRow = getFnScrollPaneTable().getSelectedRow();
		if (selectedRow > -1) {
			AnnotatedFunction function = getOutputFnsListTableModel1().getValueAt(selectedRow);
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

	public void select(AnnotatedFunction selection) {
		if (selection == null) {
			getFnScrollPaneTable().clearSelection();
			return;
		}
		int numRows = getFnScrollPaneTable().getRowCount();
		for(int i=0; i<numRows; i++) {
			AnnotatedFunction valueAt = getOutputFnsListTableModel1().getValueAt(i);
			if (selection == valueAt) {
				getFnScrollPaneTable().changeSelection(i, 0, false, false);
				return;
			}
		}
	}
	
	public void tableSelectionChanged() {
		AnnotatedFunction af = getOutputFnsListTableModel1().getSelectedOutputFunction();
		firePropertyChange(PROPERTY_SELECTED_OUTPUT_FUNCTION, null, af);		
	}
}
