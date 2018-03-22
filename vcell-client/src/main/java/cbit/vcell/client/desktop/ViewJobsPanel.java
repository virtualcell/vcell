package cbit.vcell.client.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
import org.vcell.util.gui.ColorIcon;
import org.vcell.util.gui.ColorIconEx;
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
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;

@SuppressWarnings("serial")
public class ViewJobsPanel extends DocumentEditorSubPanel {

	private EventHandler eventHandler = new EventHandler();
	private DocumentWindowManager dwm = null;
	
	private EditorScrollTable table = null;
	private SimulationJobsTableModel model = null;

	private JTextField textFieldSearch = null;
	private JLabel countLabel = new JLabel("");

	private JButton refreshAllButton;
	/*
	 hasData
	*/
	private JCheckBox waitingButton = null;
	private JCheckBox queuedButton = null;
	private JCheckBox dispatchedButton = null;
	private JCheckBox runningButton = null;
	private JCheckBox completedButton = null;
	private JCheckBox failedButton = null;
	private JCheckBox stoppedButton = null;
	
	private Icon waitingIcon = new ColorIcon(11,11, Color.lightGray, true);
	private Icon queuedIcon = new ColorIcon(11,11, Color.white, true);
	private Icon dispatchedIcon = new ColorIcon(11,11, Color.magenta, true);
	private Icon runningIcon = new ColorIcon(11,11, Color.blue, true);
	private Icon completedIcon = new ColorIcon(11,11, Color.green, true);
	private Icon failedIcon = new ColorIcon(11,11, Color.red, true);
	private Icon stoppedIcon = new ColorIcon(11,11, Color.yellow, true);
//	Icon icon = new ColorIconEx(10,10,c1,c2);
	
	private Icon dataYesIcon = new ColorIcon(7,7, Color.green.brighter(), true);
	private Icon dataNoIcon = new ColorIcon(7,7, Color.lightGray, true);


	private JCheckBox getWaitingButton() {
		if(waitingButton == null) {
			waitingButton = new JCheckBox("Waiting");
			waitingButton.addActionListener(eventHandler);
		}
		return waitingButton;
	}
	private JCheckBox getQueuedButton() {
		if(queuedButton == null) {
			queuedButton = new JCheckBox("Queued");
			queuedButton.addActionListener(eventHandler);
		}
		return queuedButton;
	}
	private JCheckBox getDispatchedButton() {
		if(dispatchedButton == null) {
			dispatchedButton = new JCheckBox("Dispatched");
			dispatchedButton.addActionListener(eventHandler);
		}
		return dispatchedButton;
	}
	private JCheckBox getRunningButton() {
		if(runningButton == null) {
			runningButton = new JCheckBox("Running");
			runningButton.addActionListener(eventHandler);
		}
		return runningButton;
	}
	private JCheckBox getCompletedButton() {
		if(completedButton == null) {
			completedButton = new JCheckBox("Completed");
			completedButton.addActionListener(eventHandler);
		}
		return completedButton;
	}
	private JCheckBox getFailedButton() {
		if(failedButton == null) {
			failedButton = new JCheckBox("Failed");
			failedButton.addActionListener(eventHandler);
		}
		return failedButton;
	}
	private JCheckBox getStoppedButton() {
		if(stoppedButton == null) {
			stoppedButton = new JCheckBox("Stopped");
			stoppedButton.addActionListener(eventHandler);
		}
		return stoppedButton;
	}


	
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
			ssqs.maxRows = 200;
			ssqs.completed = getCompletedButton().isSelected();
			ssqs.dispatched = getDispatchedButton().isSelected();
			ssqs.failed = getFailedButton().isSelected();
			ssqs.queued = getQueuedButton().isSelected();
			ssqs.running = getRunningButton().isSelected();
			ssqs.stopped = getStoppedButton().isSelected();
			ssqs.waiting = getWaitingButton().isSelected();
			
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
			JPanel center = new JPanel();
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
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(5, 2, 2, 3);
			top.add(center, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(5, 2, 2, 3);
			top.add(right, gbc);
			
			/*
			 hasData
			 * 
			waiting		queued	
			dispatched		running	
			completed		failed	
			stopped
			*/

			left.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 2, 0);
			left.add(getWaitingButton(), gbc);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 0, 2, 3);
			JLabel label = new JLabel(waitingIcon);
			left.add(label, gbc);

			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 15, 2, 0);
			left.add(getQueuedButton(), gbc);
			
			gbc.gridx = 3;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 0, 2, 3);
			label = new JLabel(queuedIcon);
			left.add(label, gbc);
//-------------------------------------
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 2, 0);
			left.add(getDispatchedButton(), gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 0, 2, 3);
			label = new JLabel(dispatchedIcon);
			left.add(label, gbc);

			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 15, 2, 0);
			left.add(getRunningButton(), gbc);
			
			gbc.gridx = 3;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 0, 2, 3);
			label = new JLabel(runningIcon);
			left.add(label, gbc);
//-------------------------------------
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 2, 0);
			left.add(getCompletedButton(), gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 0, 2, 3);
			label = new JLabel(completedIcon);
			left.add(label, gbc);

			gbc.gridx = 2;
			gbc.gridy = 2;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 15, 2, 3);
			left.add(getFailedButton(), gbc);
			
			gbc.gridx = 3;
			gbc.gridy = 2;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 0, 2, 3);
			label = new JLabel(failedIcon);
			left.add(label, gbc);
//-------------------------------------
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 2, 0);
			left.add(getStoppedButton(), gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 3;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 0, 2, 3);
			label = new JLabel(stoppedIcon);
			left.add(label, gbc);


			right.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 2, 3);
			right.add(getRefreshAllButton(), gbc);
			
			getCompletedButton().setSelected(false);
			getDispatchedButton().setSelected(true);
			getFailedButton().setSelected(true);
			getQueuedButton().setSelected(true);
			getRunningButton().setSelected(true);
			getStoppedButton().setSelected(true);
			getWaitingButton().setSelected(true);

			
			// ----------------------------------------- bottom panel (table) -------------------
			table = new EditorScrollTable();
			model = new SimulationJobsTableModel(table, this);
			table.setModel(model);
			table.getSelectionModel().addListSelectionListener(eventHandler);
			table.getModel().addTableModelListener(eventHandler);
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
							SchedulerStatus ss1 = js.jobStatus.getSchedulerStatus();
							switch (ss1) {
							case WAITING:
								setIcon(waitingIcon);
								break;
							case QUEUED:
								setIcon(queuedIcon);
								break;
							case DISPATCHED:
								setIcon(dispatchedIcon);
								break;
							case RUNNING:
								setIcon(runningIcon);
								break;
							case COMPLETED:
								setIcon(completedIcon);
								break;
							case STOPPED:
								setIcon(stoppedIcon);
								break;
							case FAILED:
								setIcon(failedIcon);
								break;
							default:
								setIcon(failedIcon);
								break;
							}
							//setText("");
							setToolTipText(ss1.getDescription());
							setHorizontalTextPosition(SwingConstants.RIGHT);
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
//			statusCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
			DefaultScrollTableCellRenderer dateTimeCellRenderer = new DefaultScrollTableCellRenderer() {
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
							SimpleJobStatus sjs = (SimpleJobStatus)selectedObject;
							DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:MM:SS");
							Date date = sjs.jobStatus.getStartDate();
							String str = df.format(date);
							setToolTipText(str);
						}
					}
					return this;
				}
			};
			DefaultScrollTableCellRenderer hasDataCellRenderer = new DefaultScrollTableCellRenderer() {
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
							SimpleJobStatus sjs = (SimpleJobStatus)selectedObject;
							if(sjs.jobStatus.hasData()) {
								setIcon(dataYesIcon);
							} else {
								setIcon(dataNoIcon);
							}
						}
					}
					return this;
				}
			};
			table.getColumnModel().getColumn(SimulationJobsTableModel.iColStatus).setCellRenderer(statusCellRenderer);
			table.getColumnModel().getColumn(SimulationJobsTableModel.iColStartDate).setCellRenderer(dateTimeCellRenderer);
			table.getColumnModel().getColumn(SimulationJobsTableModel.iColHasData).setCellRenderer(hasDataCellRenderer);
//			table.getColumnModel().getColumn(SimulationJobsTableModel.iColDepiction).setMinWidth(400);
//			table.getColumnModel().getColumn(SimulationJobsTableModel.iColStatus).setPreferredWidth(30);
//			table.getColumnModel().getColumn(SimulationJobsTableModel.iColStatus).setMaxWidth(30);

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
	

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		// TODO Auto-generated method stub
		
	}

	
	

}
