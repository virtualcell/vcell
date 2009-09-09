package cbit.vcell.client.desktop.simulation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.vcell.util.gui.DefaultTableCellRendererEnhanced;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.JTableFixed;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.math.Function;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ScopedExpression;

public class ObservablesPanel extends JPanel {
	private JPanel buttons_n_label_Panel = null;
	private javax.swing.JLabel observablesLabel = null;
	private JButton addButton = null;
	private JButton deleteButton = null;
	private JScrollPane fnTableScrollPane = null;
	private JSortTable obsFnsScrollPaneTable = null;
	private ObservablesListTableModel observablesListTableModel1 = null;
	private SimulationOwner simulationOwner = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	

	class IvjEventHandler implements ActionListener, MouseListener{
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ObservablesPanel.this.getAddFnButton()) 
				addObservableFunction();
			if (e.getSource() == ObservablesPanel.this.getDeleteFnButton()) 
				deleteObservableFunction();
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == ObservablesPanel.this.getFnScrollPaneTable()) {
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
	
	public ObservablesPanel() {
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

	private javax.swing.JLabel getObservablesLabel() {
		if (observablesLabel == null) {
			try {
				observablesLabel = new javax.swing.JLabel();
				observablesLabel.setName("ObservablesLabel");
				observablesLabel.setFont(observablesLabel.getFont().deriveFont(Font.BOLD));
				observablesLabel.setText("  Observable Functions: ");
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return observablesLabel;
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
				buttons_n_label_Panel.add(getObservablesLabel());
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

	private JTableFixed getFnScrollPaneTable() {
		if (obsFnsScrollPaneTable == null) {
			try {
				obsFnsScrollPaneTable = new JSortTable();
				obsFnsScrollPaneTable.setName("ScrollPaneTable");
				getFnTableScrollPane().setColumnHeaderView(obsFnsScrollPaneTable.getTableHeader());
				obsFnsScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
				obsFnsScrollPaneTable.setBounds(0, 0, 200, 200);
				obsFnsScrollPaneTable.setPreferredScrollableViewportSize(new java.awt.Dimension(450, 100));
				obsFnsScrollPaneTable.setAutoCreateColumnsFromModel(false);
				obsFnsScrollPaneTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				obsFnsScrollPaneTable.setDefaultRenderer(Object.class, new DefaultTableCellRendererEnhanced());
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return obsFnsScrollPaneTable;
	}

	private ObservablesListTableModel getObservablesListTableModel1() {
		if (observablesListTableModel1 == null) {
			try {
				observablesListTableModel1 = new ObservablesListTableModel(obsFnsScrollPaneTable);
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return observablesListTableModel1;
	}
	
	private void initConnections() throws java.lang.Exception {
		getAddFnButton().addActionListener(ivjEventHandler);
		getDeleteFnButton().addActionListener(ivjEventHandler);
		getFnScrollPaneTable().addMouseListener(ivjEventHandler);

		// for scrollPaneTable, set tableModel and create default columns
		getFnScrollPaneTable().setModel(getObservablesListTableModel1());
		getFnScrollPaneTable().createDefaultColumnsFromModel();
	}

	private void initialize() {
		try {
			setName("ObservableFunctionsListPanel");
			setSize(750, 100);
			setLayout(new BorderLayout());
			// setPreferredSize(new Dimension(730, 400));
			add(getButtonLabelPanel(), BorderLayout.NORTH);
			add(getFnTableScrollPane(), BorderLayout.CENTER);
			initConnections();
			getFnScrollPaneTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			getFnScrollPaneTable().setDefaultRenderer(ScopedExpression.class,new ScopedExpressionTableCellRenderer());
			getFnScrollPaneTable().setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(getFnScrollPaneTable(), false));
		} catch (java.lang.Throwable e) {
			e.printStackTrace(System.out);
		}
	}

	private void addObservableFunction() {
		ArrayList<Function> obsFunctionList = simulationOwner.getObservableFunctionsList();
		String defaultName = null;
		int count = 0;
		while (true) {
			boolean nameUsed = false;
			count++;
			defaultName = "Function" + count;
			for (Function function : obsFunctionList){
				if (function.getName().equals(defaultName)) {
					nameUsed = true;
				}
			}
			if (!nameUsed) {
				break;
			}
		}

		Function newFunction = new Function(defaultName, new Expression(0.0));
		try {
			simulationOwner.addObservableFunction(newFunction);
			getObservablesListTableModel1().fireTableDataChanged();
		} catch (PropertyVetoException e1) {
			e1.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, "Function '" + newFunction.getName() + "' already exists." + e1.getMessage());
		}
		enableDeleteFnButton();
	}

	private void deleteObservableFunction() {
		ArrayList<Function> obsFunctionList = simulationOwner.getObservableFunctionsList();
		int selectedRow = getFnScrollPaneTable().getSelectedRow();
		Function function = obsFunctionList.get(selectedRow);
		if (selectedRow > -1) {
			try {
				simulationOwner.removeObservableFunction(function);
				getObservablesListTableModel1().fireTableDataChanged();
			} catch (PropertyVetoException e1) {
				e1.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, " Deletion of function '" + function.getName() + "' failed." + e1.getMessage());
			}
		}
		enableDeleteFnButton();
	}

	public void setSimulationOwner(SimulationOwner simulationOwner) {
		this.simulationOwner = simulationOwner;
		getObservablesListTableModel1().setSimulationOwner(simulationOwner);
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
			ObservablesPanel anObservablesPanel = new ObservablesPanel();
			frame.setContentPane(anObservablesPanel);
			frame.setSize(anObservablesPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
			
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}

}
