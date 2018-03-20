package cbit.vcell.client.desktop;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.vcell.util.DataAccessException;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.server.SessionManager;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationController;

@SuppressWarnings("serial")
public class ViewJobsPanel extends DocumentEditorSubPanel {

	private EventHandler eventHandler = new EventHandler();
	private DocumentWindowManager dwm = null;
	
	private EditorScrollTable table = null;
	private SimulationJobsTableModel model = null;

	private JTextField textFieldSearch = null;
	private JLabel countLabel = new JLabel("");

	private JButton refreshAllButton;
	
	private class EventHandler implements ActionListener, ListSelectionListener, TableModelListener, DocumentListener {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(e.getSource() == getRefreshAllButton()) {
				refreshInterface();
			}
		}
		@Override
		public void tableChanged(TableModelEvent arg0) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(table.getModel().getRowCount() == 0) {
						System.out.println("table is empty");
					} else {
						table.setRowSelectionInterval(0,0);
					}
					countLabel.setText("Jobs: " + table.getModel().getRowCount());
				}
			});
		}
		@Override
		public void valueChanged(ListSelectionEvent e) {
		}
		
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			searchTable();
		}
		@Override
		public void insertUpdate(DocumentEvent arg0) {
			searchTable();
		}
		@Override
		public void removeUpdate(DocumentEvent arg0) {
			searchTable();
		}
	}
	
	private class RunQuery extends AsynchClientTask {
		private static final String message = "Running Query ...";
		public RunQuery() {
			super(message, TASKTYPE_NONSWING_BLOCKING);
		}
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			ClientRequestManager crm = (ClientRequestManager)dwm.getRequestManager();
			ClientDocumentManager cdm = (ClientDocumentManager)crm.getDocumentManager();
			SessionManager sm = cdm.getSessionManager();
			ClientServerManager csm = (ClientServerManager)sm;
			SimulationController sc = csm.getSimulationController();
			
			SimpleJobStatusQuerySpec ssqs = new SimpleJobStatusQuerySpec();
			ssqs.userid = cdm.getUser().getName();
			ssqs.maxRows = 0;
			
			SimpleJobStatus[] sjs = null;
			try {
				sjs = sc.getSimpleJobStatus(ssqs);
			} catch (DataAccessException | RemoteProxyException e) {
				e.printStackTrace();
			}
			hashTable.put("SimpleJobStatus", sjs);
			System.out.println("Finish Querry Run");
		}
	}
	private class DisplayResults extends AsynchClientTask {
		private static final String message = "Displaying Results ...";
		public DisplayResults() {
			super(message, TASKTYPE_SWING_BLOCKING);
		}
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			SimpleJobStatus[] sjs = (SimpleJobStatus[])hashTable.get("SimpleJobStatus");
			if(sjs == null) {
				System.out.println("SimpleJobStatus is null");
			}
			model.setData(sjs);
			table.getColumnModel().getColumn(SimulationJobsTableModel.iColMessage).setPreferredWidth(40);
		}
	}

	
	public ViewJobsPanel(DocumentWindowManager dwm) {
		super();
		this.dwm = dwm;
		initialize();
	}
	
	private void handleException(java.lang.Throwable exception) {
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}
	private void initialize() {
		try {
			setName("ViewSimulationJobsPanel");
			
			getRefreshAllButton().addActionListener(eventHandler);
			
			// ----------------------------------------------------------------------------------
			JPanel top = new JPanel();		// filters, buttons
			JPanel bottom = new JPanel();	// table with results
			
			Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
			TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " Query Filters ");
			titleTop.setTitleJustification(TitledBorder.LEFT);
			titleTop.setTitlePosition(TitledBorder.TOP);
			TitledBorder titleBottom = BorderFactory.createTitledBorder(loweredEtchedBorder, " Query Results ");
			titleBottom.setTitleJustification(TitledBorder.LEFT);
			titleBottom.setTitlePosition(TitledBorder.TOP);

			top.setBorder(titleTop);
			bottom.setBorder(titleBottom);

			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
			add(top, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(5, 2, 2, 3);
			add(bottom, gbc);
			// --------------------------------------- top panel (filters, button) --------------
			JPanel left = new JPanel();		// filters
			JPanel right = new JPanel();	// buttons
			
			top.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 2, 3);
			top.add(left, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(5, 2, 2, 3);
			top.add(right, gbc);
			
			right.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 2, 3);
			right.add(getRefreshAllButton(), gbc);

//			left.setLayout(new GridBagLayout());
//			gbc = new GridBagConstraints();
//			gbc.gridx = 0;
//			gbc.gridy = 0;
//			gbc.weightx = 0;
//			gbc.weighty = 0;
//			gbc.fill = GridBagConstraints.HORIZONTAL;
//			gbc.insets = new Insets(5, 2, 2, 3);
//			left.add(combobox, gbc);
			
			// ----------------------------------------- bottom panel (table) -------------------
			table = new EditorScrollTable();
			model = new SimulationJobsTableModel(table, this);
			table.setModel(model);
			table.getSelectionModel().addListSelectionListener(eventHandler);
			table.getModel().addTableModelListener(eventHandler);

//			DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//			rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

			table.addMouseMotionListener(new MouseMotionAdapter() {	// add toolTipText for each table cell
			    public void mouseMoved(MouseEvent e) { 	
			            Point p = e.getPoint(); 
			            int row = table.rowAtPoint(p);
			            int column = table.columnAtPoint(p);
			            table.setToolTipText(String.valueOf(table.getValueAt(row,column)));
			    } 
			});

			bottom.setLayout(new GridBagLayout());
			int gridy = 0;
			gbc = new java.awt.GridBagConstraints();		
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = 8;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4, 4, 4, 4);
//			table.setPreferredScrollableViewportSize(new Dimension(700,350));	// apparently useless
			bottom.add(table.getEnclosingScrollPane(), gbc);

			gridy ++;	
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.insets = new Insets(4,4,4,4);
			bottom.add(new JLabel("Search "), gbc);

			textFieldSearch = new JTextField(70);
			textFieldSearch.addActionListener(eventHandler);
			textFieldSearch.getDocument().addDocumentListener(eventHandler);
			textFieldSearch.putClientProperty("JTextField.variant", "search");
			
			gbc = new java.awt.GridBagConstraints();
			gbc.weightx = 1.0;
			gbc.gridx = 1; 
			gbc.gridy = gridy;
			gbc.gridwidth = 3;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 0, 4, 4);
			bottom.add(textFieldSearch, gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 4;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 10);
			bottom.add(countLabel, gbc);

			// renderer for the status icon; the tooltip gives the text
			DefaultScrollTableCellRenderer statusCellRenderer = new DefaultScrollTableCellRenderer() {
				Icon icon = null;
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, 
						boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					setText((String)value);
					if (table.getModel() instanceof VCellSortTableModel<?>) {
						Object selectedObject = null;
						if (table.getModel() == model) {
							selectedObject = model.getValueAt(row);
						}
						if(selectedObject != null && selectedObject instanceof SimpleJobStatus) {
							SimpleJobStatus js = (SimpleJobStatus)selectedObject;
							icon = null;
							if(js.stateInfo != null) {
								setText(js.stateInfo.getShortDesc());
							}
						}
					}
					return this;
				}
//				@Override
//				public void paintComponent(Graphics g) {
//					super.paintComponent(g);
//					if(icon != null) {
////						icon.paintSelf();
//					}
//				}
			};

			table.getColumnModel().getColumn(SimulationJobsTableModel.iColStatus).setCellRenderer(statusCellRenderer);
//			table.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColDepiction).setMinWidth(400);
//			
//			table.getColumnModel().getColumn(GeneratedReactionTableModel.iColDefinition).setPreferredWidth(30);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			
			refreshInterface();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	private void refreshInterface() {
		
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		AsynchClientTask[] tasksArray = new AsynchClientTask[2];
		tasksArray[0] = new RunQuery();
		tasksArray[1] = new DisplayResults();
		ClientTaskDispatcher.dispatch(this, hash, tasksArray, false, true, new ProgressDialogListener() {
			@Override
			public void cancelButton_actionPerformed(EventObject newEvent) {
				try {
					System.out.println("...user cancelled.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JButton getRefreshAllButton() {
		if (refreshAllButton == null) {
			refreshAllButton = new javax.swing.JButton("Refresh");
			refreshAllButton.setName("RefreshAllButton");
		}
		return refreshAllButton;
	}
	
	private void searchTable() {
		String searchText = textFieldSearch.getText();
		model.setSearchText(searchText);
	}
	
	/*
	 hasData
	 * 
	waiting		queued	
	dispatched		running	
	completed		failed	
	stopped
	*/

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		// TODO Auto-generated method stub
		
	}

	
	

}
