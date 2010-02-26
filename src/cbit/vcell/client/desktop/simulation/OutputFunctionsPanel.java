package cbit.vcell.client.desktop.simulation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;
import cbit.vcell.parser.ASTFuncNode;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.VariableType;

public class OutputFunctionsPanel extends JPanel {
	private JPanel buttons_n_label_Panel = null;
	private javax.swing.JLabel outputFnsLabel = null;
	private JButton addButton = null;
	private JButton deleteButton = null;
	private JScrollPane fnTableScrollPane = null;
	private JSortTable outputFnsScrollPaneTable = null;
	private JLabel functionExprLabel = null;
	private JLabel functionNameLabel = null;
	private TextFieldAutoCompletion functionExpressionTextField = null;
	private JTextField functionNameTextField = null;
	private JPanel functionPanel = null;
	private OutputFunctionsListTableModel outputFnsListTableModel1 = null;
	private OutputFunctionContext outputFunctionContext = null;
	private SimulationWorkspace simulationWorkspace = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	

	class IvjEventHandler implements ActionListener, MouseListener, PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == OutputFunctionsPanel.this.getAddFnButton()) 
				addOutputFunction();
			if (e.getSource() == OutputFunctionsPanel.this.getDeleteFnButton()) 
				deleteOutputFunction();
		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == OutputFunctionsPanel.this && (evt.getPropertyName().equals("outputFunctionContext"))) {
				// disable add/delete function buttons
				if (outputFunctionContext != null && outputFunctionContext.getSimulationOwner() != null) { 
					MathDescription mathDescription = outputFunctionContext.getSimulationOwner().getMathDescription();
					if (mathDescription != null && (mathDescription.isSpatial() || mathDescription.isStoch())) {
						getAddFnButton().setEnabled(false);
						getDeleteFnButton().setEnabled(false);
					}
					getOutputFnsListTableModel1().setOutputFunctionContext(outputFunctionContext);
				}
			}
			if (evt.getSource() == OutputFunctionsPanel.this && (evt.getPropertyName().equals("simulationWorkspace"))) {
				SimulationWorkspace sw_old = (SimulationWorkspace)evt.getOldValue();
				SimulationWorkspace sw_new = (SimulationWorkspace)evt.getNewValue();
				if (sw_old != null) {
					sw_old.removePropertyChangeListener(this);
				} 
				if (sw_new != null) {
					sw_new.addPropertyChangeListener(this);
					if (sw_new.getSimulationOwner() != null) {
						setOutputFunctionContext(sw_new.getSimulationOwner().getOutputFunctionContext());
					} else {
						setOutputFunctionContext(null);
					}
				} else {
					setOutputFunctionContext(null);
				}
			}
			if (evt.getSource() == getSimulationWorkspace() && (evt.getPropertyName().equals("simulationOwner"))) {
				SimulationOwner so_new = (SimulationOwner)evt.getNewValue();
				if (so_new != null) {
					if (so_new != null) {
						setOutputFunctionContext(so_new.getOutputFunctionContext());
					} else {
						setOutputFunctionContext(null);
					}
				} else {
					setOutputFunctionContext(null);
				}
			}
		};

		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == OutputFunctionsPanel.this.getFnScrollPaneTable()) {
				enableDeleteFnButton();
			}
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		};
	};
	
	public OutputFunctionsPanel() {
		super();
		setLayout(new GridBagLayout());
		initialize();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weighty = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
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

	private javax.swing.JLabel getOutputFnsLabel() {
		if (outputFnsLabel == null) {
			try {
				outputFnsLabel = new javax.swing.JLabel();
				outputFnsLabel.setName("OutputFnsLabel");
				outputFnsLabel.setFont(outputFnsLabel.getFont().deriveFont(Font.BOLD));
				outputFnsLabel.setText("  Output Functions: ");
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return outputFnsLabel;
	}

	private javax.swing.JPanel getButtonLabelPanel() {
		if (buttons_n_label_Panel == null) {
			try {
				buttons_n_label_Panel = new javax.swing.JPanel();
				buttons_n_label_Panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
				buttons_n_label_Panel.setAlignmentY(Component.TOP_ALIGNMENT);
				buttons_n_label_Panel.setPreferredSize(new Dimension(750, 40));
				buttons_n_label_Panel.setName("ButtonPanel");
				buttons_n_label_Panel.setLayout(new BoxLayout(buttons_n_label_Panel, BoxLayout.X_AXIS));
				buttons_n_label_Panel.add(getOutputFnsLabel());
				buttons_n_label_Panel.add(Box.createHorizontalGlue());
				buttons_n_label_Panel.add(getAddFnButton());
				buttons_n_label_Panel.add(Box.createRigidArea(new Dimension(5,5)));
				buttons_n_label_Panel.add(getDeleteFnButton());
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return buttons_n_label_Panel;
	}

	private javax.swing.JScrollPane getFnTableScrollPane() {
		if (fnTableScrollPane == null) {
			try {
				fnTableScrollPane = new javax.swing.JScrollPane();
				fnTableScrollPane.setName("JScrollPane1");
				fnTableScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				fnTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				fnTableScrollPane.setPreferredSize(new java.awt.Dimension(750, 100));
				fnTableScrollPane.setMinimumSize(new java.awt.Dimension(100, 100));
				getFnTableScrollPane().setViewportView(getFnScrollPaneTable());
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return fnTableScrollPane;
	}

	private JSortTable getFnScrollPaneTable() {
		if (outputFnsScrollPaneTable == null) {
			try {
				outputFnsScrollPaneTable = new JSortTable();
				outputFnsScrollPaneTable.setName("ScrollPaneTable");
				outputFnsScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
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
				functionExpressionTextField.setPreferredSize(new java.awt.Dimension(200, 30));
				functionExpressionTextField.setMaximumSize(new java.awt.Dimension(200, 30));
				functionExpressionTextField.setMinimumSize(new java.awt.Dimension(200, 30));
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionExpressionTextField;
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
				functionNameLabel.setMinimumSize(new java.awt.Dimension(45, 14));
				functionNameLabel.setMaximumSize(new java.awt.Dimension(45, 14));
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
				functionNameTextField.setSize(new java.awt.Dimension(600, 30));
				functionNameTextField.setPreferredSize(new java.awt.Dimension(600, 30));
				functionNameTextField.setMaximumSize(new java.awt.Dimension(600, 30));
				functionNameTextField.setMinimumSize(new java.awt.Dimension(600, 30));			
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionNameTextField;
	}

	private javax.swing.JPanel getFunctionPanel() {
		if (functionPanel == null) {
			try {
				functionPanel = new javax.swing.JPanel();
				functionPanel.setName("FunctionPanel");
				functionPanel.setLayout(new java.awt.GridBagLayout());
				functionPanel.setBounds(401, 308, 407, 85);

				java.awt.GridBagConstraints constraintsFunctionNameLabel = new java.awt.GridBagConstraints();
				constraintsFunctionNameLabel.gridx = 0; constraintsFunctionNameLabel.gridy = 0;
				constraintsFunctionNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getFunctionPanel().add(getFunctionNameLabel(), constraintsFunctionNameLabel);

				java.awt.GridBagConstraints constraintsFunctionNameTextField = new java.awt.GridBagConstraints();
				constraintsFunctionNameTextField.gridx = 1; constraintsFunctionNameTextField.gridy = 0;
				constraintsFunctionNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsFunctionNameTextField.weightx = 1.0;
				constraintsFunctionNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				getFunctionPanel().add(getFunctionNameTextField(), constraintsFunctionNameTextField);

				java.awt.GridBagConstraints constraintsFunctionExprLabel = new java.awt.GridBagConstraints();
				constraintsFunctionExprLabel.gridx = 0; constraintsFunctionExprLabel.gridy = 1;
				constraintsFunctionExprLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getFunctionPanel().add(getFunctionExprLabel(), constraintsFunctionExprLabel);

				java.awt.GridBagConstraints constraintsFunctionExpressionTextField = new java.awt.GridBagConstraints();
				constraintsFunctionExpressionTextField.gridx = 1; constraintsFunctionExpressionTextField.gridy = 1;
				constraintsFunctionExpressionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsFunctionExpressionTextField.weightx = 1.0;
				constraintsFunctionExpressionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				getFunctionPanel().add(getFunctionExpressionTextField(), constraintsFunctionExpressionTextField);
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return functionPanel;
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
	
	private void initConnections() throws java.lang.Exception {
		getAddFnButton().addActionListener(ivjEventHandler);
		getDeleteFnButton().addActionListener(ivjEventHandler);
		getFnScrollPaneTable().addMouseListener(ivjEventHandler);
		this.addPropertyChangeListener(ivjEventHandler);
		
		getFnTableScrollPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				ScopedExpressionTableCellRenderer.formatTableCellSizes(getFnScrollPaneTable(),null,null);
			}
		});

		// for scrollPaneTable, set tableModel and create default columns
		getFnScrollPaneTable().setModel(getOutputFnsListTableModel1());
		getFnScrollPaneTable().createDefaultColumnsFromModel();
	}

	private void initialize() {
		try {
			setName("OutputFunctionsListPanel");
			setSize(750, 100);
			setLayout(new BorderLayout());
			// setPreferredSize(new Dimension(730, 400));
			add(getButtonLabelPanel(), BorderLayout.NORTH);
			add(getFnTableScrollPane(), BorderLayout.CENTER);
			initConnections();
			getFnScrollPaneTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			getFnScrollPaneTable().setDefaultRenderer(ScopedExpression.class,new ScopedExpressionTableCellRenderer());
			getFnScrollPaneTable().setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(getFnScrollPaneTable(), false));
			
			getOutputFnsListTableModel1().addTableModelListener(
					new javax.swing.event.TableModelListener(){
						public void tableChanged(javax.swing.event.TableModelEvent e){
							try {
								ScopedExpressionTableCellRenderer.formatTableCellSizes(getFnScrollPaneTable(),null,null);
							} catch (Exception e1) {
								e1.printStackTrace(System.out);
							}
						}
					}
				);

		} catch (java.lang.Throwable e) {
			e.printStackTrace(System.out);
		}
	}

	private void addOutputFunction() {
		ArrayList<AnnotatedFunction> outputFunctionList = outputFunctionContext.getOutputFunctionsList();
		String defaultName = null;
		int count = 0;
		while (true) {
			boolean nameUsed = false;
			count++;
			defaultName = "Function" + count;
			for (AnnotatedFunction function : outputFunctionList){
				if (function.getName().equals(defaultName)) {
					nameUsed = true;
				}
			}
			if (!nameUsed) {
				break;
			}
		}

		javax.swing.JPanel FnPanel = getFunctionPanel();
		getFunctionNameTextField().setText(defaultName);
		getFunctionExpressionTextField().setText("0.0");
		Set<String> autoCompList = new HashSet<String>();
		Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
		outputFunctionContext.getEntries(entryMap);
		autoCompList = entryMap.keySet();
		getFunctionExpressionTextField().setAutoCompletionWords(autoCompList);
		getFunctionExpressionTextField().setAutoCompleteSymbolFilter(new AutoCompleteSymbolFilter() {
			public boolean accept(SymbolTableEntry ste) {
				return true;
			}
			public boolean acceptFunction(String funcName) {
				if (funcName.equals(ASTFuncNode.getFunctionNames()[ASTFuncNode.FIELD]) || funcName.equals(ASTFuncNode.getFunctionNames()[ASTFuncNode.GRAD])) {
					return false;
				}
				return true;
			}
		});

		//
		// Show the editor with a default name and default expression for the function
		// If the OK option is chosen, get the new name and expression for the function and create a new
		// function, add it to the list of output functions in simulationOwner 
		// Else, pop-up an error dialog indicating that function cannot be added.
		//
		int ok = JOptionPane.showOptionDialog(this, FnPanel, "Add Function" , 0, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, null);
		if (ok == javax.swing.JOptionPane.OK_OPTION) {
			String funcName = getFunctionNameTextField().getText();
			Expression funcExp = null;
			try {
				funcExp = new Expression(getFunctionExpressionTextField().getText());
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
			}
			AnnotatedFunction newFunction = new AnnotatedFunction(funcName, funcExp, null, VariableType.UNKNOWN, true);
			try {
				outputFunctionContext.addOutputFunction(newFunction);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, "Function '" + newFunction.getName() + "' cannot be added." + e1.getMessage());
			}
		}

		enableDeleteFnButton();
	}

	private void deleteOutputFunction() {
		int selectedRow = getFnScrollPaneTable().getSelectedRow();
		if (selectedRow > -1) {
			AnnotatedFunction function = (AnnotatedFunction)getOutputFnsListTableModel1().getData().get(selectedRow);
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

}
