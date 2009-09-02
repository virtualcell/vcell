package cbit.vcell.client.desktop.simulation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.vcell.util.gui.DefaultTableCellRendererEnhanced;
import org.vcell.util.gui.JTableFixed;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.client.PopupGenerator;
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
	private JTableFixed obsFnsScrollPaneTable = null;
	private ObservablesListTableModel observablesListTableModel1 = null;
	private ListSelectionModel selectionModel1 = null;
	private DefaultCellEditor cellEditor1 = null;
	private java.awt.Component Component1 = null;
	private SimulationOwner simulationOwner = null;
//	private Function[] fieldObservableFunctionsList = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener, javax.swing.event.TableModelListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ObservablesPanel.this.getAddFnButton()) 
				addObservableFunction(e);
			if (e.getSource() == ObservablesPanel.this.getDeleteFnButton()) 
				deleteObservableFunction(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
//			if (e.getSource() == ObservablesPanel.this.getComponent1()) 
//				connEtoC11(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
//			if (evt.getSource() == ObservablesPanel.this && (evt.getPropertyName().equals("observableFunctionsList"))) 
//				setObservableFunctionsOnTableModel();
//			if (evt.getSource() == ObservablesPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("selectionModel"))) 
//				connPtoP2SetTarget();
//			if (evt.getSource() == ObservablesPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("cellEditor"))) 
//				connEtoM4(evt);
		};
		public void tableChanged(javax.swing.event.TableModelEvent e) {
//			if (e.getSource() == ObservablesPanel.this.getSimulationListTableModel1()) 
//				connEtoC10(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
//			if (e.getSource() == ObservablesPanel.this.getselectionModel1()) 
//				connEtoC9(e);
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
				deleteButton.setEnabled(true);
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
				obsFnsScrollPaneTable = new JTableFixed();
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
	
	private javax.swing.ListSelectionModel getselectionModel1() {
		return selectionModel1;
	}

//	private javax.swing.DefaultCellEditor getcellEditor1() {
//		return ivjcellEditor1;
//	}

	private void initConnections() throws java.lang.Exception {
		getAddFnButton().addActionListener(ivjEventHandler);
		getDeleteFnButton().addActionListener(ivjEventHandler);
		this.addPropertyChangeListener(ivjEventHandler);
		getObservablesListTableModel1().addTableModelListener(ivjEventHandler);
		getFnScrollPaneTable().addPropertyChangeListener(ivjEventHandler);

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

	private void addObservableFunction(ActionEvent e) {
		PopupGenerator.showWarningDialog(this, "Not yet implemented");
	}

	private void deleteObservableFunction(ActionEvent e) {
		PopupGenerator.showWarningDialog(this, "Not yet implemented");
	}

	public void setSimulationOwner(SimulationOwner simulationOwner) {
		this.simulationOwner = simulationOwner;
		getObservablesListTableModel1().setSimulationOwner(simulationOwner);
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
