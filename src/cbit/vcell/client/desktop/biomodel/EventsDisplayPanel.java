package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import cbit.gui.ScopedExpression;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;

public class EventsDisplayPanel extends JPanel {
	private JSplitPane outerSplitPane = null;
	private JScrollPane scrollPane = null;
	private JTable scrollPaneTable = null;
	private EventsSummaryTableModel eventsSummaryTableModel = null;
	private EventPanel eventPanel = null;
	private SimulationContext fieldSimContext = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

	class IvjEventHandler implements PropertyChangeListener, ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == EventsDisplayPanel.this && (evt.getPropertyName().equals("simulationContext"))) {
				getEventSummaryTableModel().setSimulationContext(fieldSimContext);
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) {
				updateEventPanel();
			}

		};
	};

	public EventsDisplayPanel() {
		super();
		initialize(false);
	}

	public EventsDisplayPanel(boolean expanded) {
		super();
		initialize(expanded);
	}

	private void initConnections() {
		this.addPropertyChangeListener(ivjEventHandler);

		// for scrollPaneTable, set tableModel and create default columns
		getScrollPaneTable().setModel(getEventSummaryTableModel());
		getScrollPaneTable().createDefaultColumnsFromModel();
		getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
		
		// cellRenderer for table (name column)
		getScrollPaneTable().setDefaultRenderer(BioEvent.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (value instanceof BioEvent) {
					setText(((BioEvent)value).getName());
				}
				return this;
			}
		});
	}
	
	private void initialize(boolean expanded) {
		try {
			setName("EventsPanel");
			setLayout(new GridBagLayout());
			// setSize(450, 530);

			java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
			constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
			constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJSplitPane1.weightx = 1.0;
			constraintsJSplitPane1.weighty = 1.0;
			constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getOuterSplitPane(expanded), constraintsJSplitPane1);
			initConnections();
			
			getScrollPaneTable().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			getScrollPaneTable().setDefaultRenderer(ScopedExpression.class,new ScopedExpressionTableCellRenderer());
			
			getEventSummaryTableModel().addTableModelListener(
					new javax.swing.event.TableModelListener(){
						public void tableChanged(javax.swing.event.TableModelEvent e){
							try {
								ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
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

	private javax.swing.JScrollPane getJScrollPane1() {
		if (scrollPane == null) {
			try {
				scrollPane = new javax.swing.JScrollPane();
				scrollPane.setName("JScrollPane1");
				scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollPane.setViewportView(getScrollPaneTable());
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return scrollPane;
	}
	
	private javax.swing.JTable getScrollPaneTable() {
		if (scrollPaneTable == null) {
			try {
				scrollPaneTable = new javax.swing.JTable();
				scrollPaneTable.setName("ScrollPaneTable");
				getJScrollPane1().setColumnHeaderView(scrollPaneTable.getTableHeader());
				scrollPaneTable.setBounds(0, 0, 200, 200);
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return scrollPaneTable;
	}
	
	private EventsSummaryTableModel getEventSummaryTableModel() {
		if (eventsSummaryTableModel == null) {
			try {
				eventsSummaryTableModel = new EventsSummaryTableModel(getScrollPaneTable());
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		return eventsSummaryTableModel;
	}

	private EventPanel getEventPanel() {
		if (eventPanel == null) {
			try {
				eventPanel = new EventPanel();
				eventPanel.setName("EventPanel");
			} catch (java.lang.Throwable e) {
				e.printStackTrace(System.out);
			}
		}
		
		return eventPanel;
	}

	protected JSplitPane getOuterSplitPane(boolean expanded) {
		if (outerSplitPane == null) {
			outerSplitPane = new JSplitPane();
			outerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			outerSplitPane.setDividerLocation(300);
			outerSplitPane.setTopComponent(getJScrollPane1());
			if(expanded) {
				outerSplitPane.setBottomComponent(getEventPanel());	// reaction kinetics editor
			} else {
				outerSplitPane.setBottomComponent(null);
			}
		}
		return outerSplitPane;
	}

	public SimulationContext getSimulationContext() {
		return fieldSimContext;
	}

	public void setSimulationContext(SimulationContext simulationContext) {
		SimulationContext oldValue = fieldSimContext;
		fieldSimContext = simulationContext;
		firePropertyChange("simulationContext", oldValue, simulationContext);
	}

	public void setScrollPaneTableCurrentRow(BioEvent selectedEvent) {
		// if 'Event' node is selected, EventSummary table should have no selection & panel below should not be visible.
		if (selectedEvent == null) {
			getScrollPaneTable().clearSelection();
			getEventPanel().setEnabled(false);
			return;
		}
		
		getEventPanel().setEnabled(true);
		outerSplitPane.setDividerLocation(300);
		// 'selectedEvent' is the leaf selection in the SPRR Panel tree, so change the row in table to reflect 'selectedEvent'
		int numRows = getScrollPaneTable().getRowCount();
		for(int i=0; i<numRows; i++) {
			BioEvent bioevent = (BioEvent)getScrollPaneTable().getValueAt(i, EventsSummaryTableModel.COLUMN_EVENT_NAME);
			if(bioevent.equals(selectedEvent)) {
				getScrollPaneTable().changeSelection(i, 0, false, false);
				break;
			}
		}
	}

	private void updateEventPanel() {
		BioEvent oldValue = getEventPanel().getBioEvent();
		BioEvent selectedBioEvent = null;
		int row = getScrollPaneTable().getSelectedRow();
		if (row >= 0) {
			selectedBioEvent = getEventSummaryTableModel().getBioEvent(row);
		}
		firePropertyChange("selectedBioEvent", oldValue, selectedBioEvent);   		
   		getEventPanel().setBioEvent(selectedBioEvent);
   	}
	
}
