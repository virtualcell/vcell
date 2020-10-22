package cbit.vcell.client.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
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

import org.vcell.service.VCellServiceHelper;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.gui.ColorIcon;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.StatusIcon;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SessionManager;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.solver.VCSimulationIdentifier;

@SuppressWarnings("serial")
public class ViewJobsPanel extends DocumentEditorSubPanel {

	private EventHandler eventHandler = new EventHandler();
	private DocumentWindowManager dwm = null;
	
	private EditorScrollTable table = null;
	private SimulationJobsTableModel model = null;

	private JTextField textFieldSearch = null;
	private JLabel countLabel = new JLabel("");
	private JLabel simulationInfoLabel = new JLabel("");
	
	private JTextField textFieldJobsLimit = null;
	int maxRows = 200;

	private JButton refreshAllButton = null;
	private JButton showQuotaButton = null;
	private JButton stopJobButton = null;
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
	
	private JCheckBox orphanedButton = null;

	private JPanel submitDatePanel = null;
	private ButtonGroup timeGroup = null;
	private JRadioButton anyTimeRadio = null;
	private JRadioButton pastDayRadio = null;
	private JRadioButton pastMonthRadio = null;
	private JRadioButton pastYearRadio = null;
	
	private JPanel propertiesPanel = null;
	private JTextPane details = null;
	
	private Icon waitingIcon = new StatusIcon(11,10, SchedulerStatus.WAITING);
	private Icon queuedIcon = new StatusIcon(11,10, SchedulerStatus.QUEUED);
	private Icon dispatchedIcon = new StatusIcon(11,10, SchedulerStatus.DISPATCHED);
	private Icon runningIcon = new StatusIcon(11,10, SchedulerStatus.RUNNING);
	private StatusIcon completedIcon = new StatusIcon(11, 10, SchedulerStatus.COMPLETED);
	private Icon failedIcon = new StatusIcon(11, 10, SchedulerStatus.FAILED);
	private Icon stoppedIcon = new StatusIcon(11,10, SchedulerStatus.STOPPED);
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
	public JCheckBox getOrphanedButton() {
		if(orphanedButton == null) {
			orphanedButton = new JCheckBox("Hide orphaned");
			orphanedButton.setToolTipText("Hide the Jobs whose Model have since been deleted.");
			orphanedButton.addActionListener(eventHandler);
		}
		return orphanedButton;
	}

	private JRadioButton getAnyTimeRadio() {
		if(anyTimeRadio == null) {
			anyTimeRadio = new JRadioButton("Any Time");
			anyTimeRadio.addActionListener(eventHandler);
		}
		return anyTimeRadio;
	}
	private JRadioButton getPastDayRadio() {
		if(pastDayRadio == null) {
			pastDayRadio = new JRadioButton("Past 24 hours");
			pastDayRadio.addActionListener(eventHandler);
		}
		return pastDayRadio;
	}
	private JRadioButton getPastMonthRadio() {
		if(pastMonthRadio == null) {
			pastMonthRadio = new JRadioButton("Past month");
			pastMonthRadio.addActionListener(eventHandler);
		}
		return pastMonthRadio;
	}
	private JRadioButton getPastYearRadio() {
		if(pastYearRadio == null) {
			pastYearRadio = new JRadioButton("Past year");
			pastYearRadio.addActionListener(eventHandler);
		}
		return pastYearRadio;
	}
	private ButtonGroup getTimeGroup() {
		if(timeGroup == null) {
			timeGroup = new ButtonGroup();
			timeGroup.add(getAnyTimeRadio());
			timeGroup.add(getPastDayRadio());
			timeGroup.add(getPastMonthRadio());
			timeGroup.add(getPastYearRadio());
		}
		return timeGroup;
	}
	
	private class EventHandler implements ActionListener, ListSelectionListener, TableModelListener, DocumentListener {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(e.getSource() == getRefreshAllButton()) {
				String text = textFieldJobsLimit.getText();
				int rows = 0;
				boolean isInt = true;
				try {
					rows = Integer.parseInt(text);
				} catch(NumberFormatException ex) {
					isInt = false;
				}
				if(!isInt || rows<=0) {
					DialogUtils.showErrorDialog(ViewJobsPanel.this, "Number of results must be a positive integer.");
					return;
				}
				maxRows = rows;
				refreshInterface();
			} else if(e.getSource() == getStopJobButton()) {
				stopJob();
			} else if(e.getSource() == getOrphanedButton()) {
				// nothing to do here
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
			if(!e.getValueIsAdjusting()) {
				String strSimInfo = "";
				String strDetails = "";
				final String htmlStart = "<html><font size =\"-1\">";
				final String htmlEnd = "</font></font></html>";
				int row = table.getSelectedRow();
				if(table.getRowCount() == 0) {
					strSimInfo += "The table is empty";
				} else {
					SimpleJobStatus sjs = model.getValueAt(row);
					String strHost = "";
					if(sjs != null) {
						strSimInfo = "Simulation " + model.getSimulationId(sjs);
						strSimInfo += ", Job " + sjs.jobStatus.getJobIndex() + " of " + model.getJobsCount(sjs);
						strHost = sjs.jobStatus.getComputeHost();
					}
					
					if(strHost != null) {
						strDetails += "Host: " + strHost + "<br>";
					}
					String message = (String) model.getValueAt(row, SimulationJobsTableModel.iColMessage);
					if(message != null) {
						strDetails += "Message: ";
						strDetails += FormatMessage(message);
					}
					
				}
				getStopJobButton().setEnabled(model.isStoppable(row));
				details.setText(htmlStart + strDetails + htmlEnd);
				simulationInfoLabel.setText(strSimInfo);
			}
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
	
	
	private class RunStopTaskQuery extends AsynchClientTask {
		private static final String message = "Stopping Task ...";
		public RunStopTaskQuery() {
			super(message, TASKTYPE_NONSWING_BLOCKING);
		}
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			ClientRequestManager crm = (ClientRequestManager)dwm.getRequestManager();
			ClientDocumentManager cdm = (ClientDocumentManager)crm.getDocumentManager();
			SessionManager sm = cdm.getSessionManager();
			ClientServerManager csm = (ClientServerManager)sm;
			SimulationController sc = csm.getSimulationController();
			
			int row = table.getSelectedRow();
			SimpleJobStatus sjs = model.getValueAt(row);
			VCSimulationIdentifier vcSimulationIdentifier = sjs.jobStatus.getVCSimulationIdentifier();
			getRefreshAllButton().setEnabled(false);
			getStopJobButton().setEnabled(false);

			try {
				sc.stopSimulation(vcSimulationIdentifier);
			} catch (RemoteProxyException e1) {
				e1.printStackTrace();
			}
			System.out.println("Finished Stopping Task");
		}
	}
	private class RunRefreshQuery extends AsynchClientTask {
		private static final String message = "Running Query ...";
		public RunRefreshQuery() {
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
//			ssqs.userid = "nasrin";
			
			ssqs.maxRows = maxRows;
			ssqs.completed = getCompletedButton().isSelected();
			ssqs.dispatched = getDispatchedButton().isSelected();
			ssqs.failed = getFailedButton().isSelected();
			ssqs.queued = getQueuedButton().isSelected();
			ssqs.running = getRunningButton().isSelected();
			ssqs.stopped = getStoppedButton().isSelected();
			ssqs.waiting = getWaitingButton().isSelected();
			
			Calendar cal = new GregorianCalendar();
			long now = cal.getTimeInMillis();
			long day = 24 * 60 * 60 * 1000;
			// if the "Any Time" button is selected, do nothing
			if(getPastDayRadio().isSelected()) {
				ssqs.submitLowMS = now - day;
				ssqs.submitHighMS = now;
//				Date date = new Date(ssqs.submitLowMS);
//				System.out.println(date.toString());
			} else if(getPastMonthRadio().isSelected()) {
				long month = day * 30;
				ssqs.submitLowMS = now - month;
				ssqs.submitHighMS = now;
			} else if(getPastYearRadio().isSelected()) {
				long year = day * 365;
				ssqs.submitLowMS = now - year;
				ssqs.submitHighMS = now;
			}
			
			getRefreshAllButton().setEnabled(false);
			getStopJobButton().setEnabled(false);
			SimpleJobStatus[] sjs = null;
			try {
				sjs = sc.getSimpleJobStatus(ssqs);
			} catch (DataAccessException | RemoteProxyException e) {
				e.printStackTrace();
			}
			hashTable.put("SimpleJobStatus", sjs);
			getRefreshAllButton().setEnabled(true);
			System.out.println("Finish Querry Run");
		}
	}
	private class DisplayRefreshResults extends AsynchClientTask {
		private static final String message = "Displaying Results ...";
		public DisplayRefreshResults() {
			super(message, TASKTYPE_SWING_BLOCKING);
		}
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			SimpleJobStatus[] sjs = (SimpleJobStatus[])hashTable.get("SimpleJobStatus");
			if(sjs == null) {
				System.out.println("SimpleJobStatus is null");
				return;
			}
			model.setData(sjs);
			table.getColumnModel().getColumn(SimulationJobsTableModel.iColMessage).setPreferredWidth(40);
		}
	}
	private class SeparatedJPanel extends JPanel {
		
		@Override
		protected void paintComponent(Graphics g) {
			Color oldColor = g.getColor();
			int h = this.getHeight();
			super.paintComponent(g);
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, 1, h-5);
			g.setColor(oldColor);
		}
	}

	public static String FormatMessage(String  text) {
		// colorize keywords
//		for(String entry : commentKeywords) {
//			String replacement = "<br>" + "<b><font color=\"#005500\">" + entry + "</font></b>";
//			text = text.replaceAll(entry, replacement);
//		}
//		for(String entry : otherKeywords) {
//			String replacement = "<font color=\"#770000\">" + entry + "</font>";
//			text = text.replaceAll(entry, replacement);
//		}
//		if(text.startsWith("<br>")) {
//			text = text.replaceFirst("<br>", "");
//		}
		text = text.replace("\\", "/");
		return text;
	}
	private JPanel getPropertiesPanel() {	// ----------- right panel (of top panel) -------------
		if (propertiesPanel == null) {
			try {
				propertiesPanel = new JPanel();
				Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
				TitledBorder titleRight = BorderFactory.createTitledBorder(loweredEtchedBorder, " Properties and Actions ");
				titleRight.setTitleJustification(TitledBorder.LEFT);
				titleRight.setTitlePosition(TitledBorder.TOP);
				propertiesPanel.setBorder(titleRight);
				
				propertiesPanel.setLayout(new GridBagLayout());
				int gridy = 0;
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = gridy;
				gbc.weightx = 0;
				gbc.weighty = 0;
				gbc.anchor = GridBagConstraints.WEST;
				gbc.insets = new Insets(10,4,4,4);
				propertiesPanel.add(new JLabel("Details:"), gbc);

				details = new JTextPane();
				details.setContentType("text/html");
				details.setEditable(false);
				JScrollPane scrl = new JScrollPane(details);
				
				gbc = new GridBagConstraints();
				gbc.gridx = 1;
				gbc.gridy = gridy;
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				gbc.gridwidth = 4;
				gbc.gridheight = 3;							// 3 rows high
				gbc.anchor = GridBagConstraints.EAST;
				gbc.fill = java.awt.GridBagConstraints.BOTH;
				gbc.insets = new Insets(4,4,4,4);
				propertiesPanel.add(scrl, gbc);
				
				gridy = 3;									// last row
				gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = gridy;
				gbc.weightx = 0;
				gbc.weighty = 0;
				gbc.gridwidth = 2;
				gbc.anchor = GridBagConstraints.WEST;
				gbc.insets = new Insets(4,4,4,4);
				propertiesPanel.add(simulationInfoLabel, gbc);
				
				gbc = new GridBagConstraints();
				gbc.weightx = 1.0;
				gbc.gridx = 2;
				gbc.gridy = gridy;
				gbc.anchor = GridBagConstraints.EAST;
				gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(4,4,4,4);
				propertiesPanel.add(new JLabel(""), gbc);	// fake horizontal

				gbc = new GridBagConstraints();
				gbc.gridx = 4;								// last cell
				gbc.gridy = gridy;
				gbc.weightx = 0;
				gbc.weighty = 0;
				gbc.anchor = GridBagConstraints.EAST;
				gbc.insets = new Insets(4,4,4,4);
				propertiesPanel.add(getStopJobButton(), gbc);
				
//				GridBagConstraints gbc = new GridBagConstraints();
//				gbc.weighty = 1.0;
//				gbc.gridx = 0;
//				gbc.gridy = gridy;
//				gbc.anchor = GridBagConstraints.WEST;
//				gbc.fill = java.awt.GridBagConstraints.VERTICAL;
//				propertiesPanel.add(new JLabel("-"), gbc);			// fake vertical
//
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return propertiesPanel;
	}
	
	private javax.swing.JPanel getSubmitDatePanel() {
		if (submitDatePanel == null) {
			try {
				Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
				TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " Time interval selector ");
				titleTop.setTitleJustification(TitledBorder.LEFT);
				titleTop.setTitlePosition(TitledBorder.TOP);

				submitDatePanel = new javax.swing.JPanel();
				submitDatePanel.setName("QuerySubmitDatePanel");
				submitDatePanel.setBorder(titleTop);
				
				submitDatePanel.setLayout(new GridBagLayout());
				int gridy = 0;
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = gridy;
				gbc.weightx = 1;
				gbc.weighty = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(1, 2, 0, 0);	//  top, left, bottom, right 
				submitDatePanel.add(getAnyTimeRadio(), gbc);

				gridy++;
				gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = gridy;
				gbc.weightx = 1;
				gbc.weighty = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(0, 2, 0, 0);
				submitDatePanel.add(getPastDayRadio(), gbc);

				gridy++;
				gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = gridy;
				gbc.weightx = 1;
				gbc.weighty = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(0, 2, 0, 0);
				submitDatePanel.add(getPastMonthRadio(), gbc);

				gridy++;
				gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = gridy;
				gbc.weightx = 1;
				gbc.weighty = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(0, 2, 1, 0);
				submitDatePanel.add(getPastYearRadio(), gbc);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return submitDatePanel;
	}
	
	public ViewJobsPanel(DocumentWindowManager dwm) {
		super();
		this.dwm = dwm;
		initialize();
	}
	
	private void initialize() {
		try {
			setName("ViewSimulationJobsPanel");
			
			getTimeGroup();		// initialize the time button group and its components
			
			// ----------------------------------------------------------------------------------
			JPanel top = new JPanel();		// filters, buttons
			JPanel bottom = new JPanel();	// table with results
			
			Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
			TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " User Options ");
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
			gbc.insets = new Insets(2, 2, 2, 3);	//  top, left, bottom, right 
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
			TitledBorder titleLeft = BorderFactory.createTitledBorder(loweredEtchedBorder, " Status Filter ");
			titleLeft.setTitleJustification(TitledBorder.LEFT);
			titleLeft.setTitlePosition(TitledBorder.TOP);
			left.setBorder(titleLeft);

			JPanel center = new SeparatedJPanel();
//			Dimension dim = new Dimension(200, 100);
//			center.setMinimumSize(dim);

			top.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 2, 1, 3);
			top.add(left, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(3, 8, 2, 3);
			top.add(getOrphanedButton(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 8, 2, 3);
			top.add(getRefreshAllButton(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.gridheight = 2;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(5, 2, 2, 3);
			top.add(center, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridheight = 2;
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(2, 2, 2, 3);
			top.add(getPropertiesPanel(), gbc);
			
			// ------------------------------------ left panel (of top panel) -------------
			left.setLayout(new GridBagLayout());
			int gridy = 0;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 2, 0, 0);
			left.add(getWaitingButton(), gbc);

			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 0, 3);
			JLabel label = new JLabel(waitingIcon);
			label.setText(" ");
			left.add(label, gbc);

			gbc.gridx = 2;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 15, 0, 0);
			left.add(getQueuedButton(), gbc);
			
			gbc.gridx = 3;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 0, 10);
			label = new JLabel(queuedIcon);
			label.setText(" ");
			left.add(label, gbc);
//-------------------------------------
			gridy++;
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 2, 0, 0);
			left.add(getDispatchedButton(), gbc);
			
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 0, 3);
			label = new JLabel(dispatchedIcon);
			label.setText(" ");
			left.add(label, gbc);

			gbc.gridx = 2;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 15, 0, 0);
			left.add(getRunningButton(), gbc);
			
			gbc.gridx = 3;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 0, 10);
			label = new JLabel(runningIcon);
			label.setText(" ");
			left.add(label, gbc);
//-------------------------------------
			gridy++;
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 2, 0, 0);
			left.add(getCompletedButton(), gbc);
			
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 0, 3);
			label = new JLabel(completedIcon);
			label.setText(" ");
			left.add(label, gbc);

			gbc.gridx = 2;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 15, 0, 0);
			left.add(getFailedButton(), gbc);
			
			gbc.gridx = 3;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 0, 10);
			label = new JLabel(failedIcon);
			label.setText(" ");
			left.add(label, gbc);
			
//-------------------------------------
			gridy++;
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 2, 0, 0);
			left.add(getStoppedButton(), gbc);
			
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 0, 3);
			label = new JLabel(stoppedIcon);
			label.setText(" ");
			left.add(label, gbc);

			// ---------------------------------------- center panel (of top panel) ------------
			center.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.insets = new Insets(4,15,4,4);
			center.add(new JLabel("Max # of results "), gbc);

			textFieldJobsLimit = new JTextField();
			textFieldJobsLimit.addActionListener(eventHandler);
			//textFieldJobsLimit.getDocument().addDocumentListener(eventHandler);
			textFieldJobsLimit.setText(maxRows+"");
			Dimension d = textFieldJobsLimit.getPreferredSize();
			d.width = 60;
			textFieldJobsLimit.setPreferredSize(d);
			textFieldJobsLimit.setMaximumSize(d);
			textFieldJobsLimit.setMinimumSize(d);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1; 
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.insets = new Insets(4, 0, 4, 4);
			center.add(textFieldJobsLimit, gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,11,2,4);
			center.add(getSubmitDatePanel(), gbc);
			
			getCompletedButton().setSelected(false);
			getDispatchedButton().setSelected(true);
			getFailedButton().setSelected(true);
			getQueuedButton().setSelected(true);
			getRunningButton().setSelected(true);
			getStoppedButton().setSelected(true);
			getWaitingButton().setSelected(true);
			
			getOrphanedButton().setSelected(false);
			getPastMonthRadio().setSelected(true);
			
			// ----------------------------------------- bottom panel (the table) -------------------
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
			gridy = 0;
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

//			gbc = new GridBagConstraints();
//			gbc.gridx = 4;
//			gbc.gridy = gridy;
//			gbc.fill = GridBagConstraints.HORIZONTAL;
//			gbc.insets = new Insets(4, 4, 4, 10);
//			bottom.add(getShowQuotaButton(), gbc);

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
							Date date = sjs.jobStatus.getSubmitDate();
							String str = "";
							if(date != null) {
								str = df.format(date);
							}
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
			table.getColumnModel().getColumn(SimulationJobsTableModel.iColSubmitDate).setCellRenderer(dateTimeCellRenderer);
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
		tasksArray[0] = new RunRefreshQuery();
		tasksArray[1] = new DisplayRefreshResults();
		ClientTaskDispatcher.dispatch(this, hash, tasksArray, false, true, new ProgressDialogListener() {
			@Override
			public void cancelButton_actionPerformed(EventObject newEvent) {
				try {
					getRefreshAllButton().setEnabled(true);
					getStopJobButton().setEnabled(model.isStoppable(table.getSelectedRow()));
					System.out.println("...user cancelled.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private void stopJob() {
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		AsynchClientTask[] tasksArray = new AsynchClientTask[3];
		tasksArray[0] = new RunStopTaskQuery();
		tasksArray[1] = new RunRefreshQuery();
		tasksArray[2] = new DisplayRefreshResults();
		ClientTaskDispatcher.dispatch(this, hash, tasksArray, false, true, new ProgressDialogListener() {
			@Override
			public void cancelButton_actionPerformed(EventObject newEvent) {
				try {
					getRefreshAllButton().setEnabled(true);
					getStopJobButton().setEnabled(model.isStoppable(table.getSelectedRow()));
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
			refreshAllButton.addActionListener(eventHandler);		}
		return refreshAllButton;
	}
	private JButton getShowQuotaButton() {
		if (showQuotaButton == null) {
			showQuotaButton = new javax.swing.JButton("Show Quota");
			showQuotaButton.setName("ShowQuotaButton");
			showQuotaButton.addActionListener(eventHandler);
		}
		return showQuotaButton;
	}
	private JButton getStopJobButton() {
		if (stopJobButton == null) {
			stopJobButton = new javax.swing.JButton("Stop Simulation");
			stopJobButton.setName("StopJobButton");
			stopJobButton.addActionListener(eventHandler);
		}
		return stopJobButton;
	}
	
	private void searchTable() {
		String searchText = textFieldSearch.getText();
		model.setSearchText(searchText);
	}
	
	private void handleException(java.lang.Throwable exception) {
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		// TODO Auto-generated method stub
		
	}

	
	/*
	 * The following code was used in ServerManageConsole, see it in a version prior to git# 7e91dd9
	 * Class (and other helper classes, interfaces) was deleted by Jim years ago, probably unusable 
	 */
	private VCMessagingService vcMessagingService = null;
	private VCMessageSession vcMessaging_clientTopicProducerSession = null;
	@Deprecated
	public void broadcastMessage() {
		
		vcMessaging_clientTopicProducerSession = vcMessagingService.createProducerSession();
		
		try {
			int n = javax.swing.JOptionPane.showConfirmDialog(this, "You are going to send message to " /*+ getBroadcastMessageToTextField().getText()*/ + ". Continue?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
			if (n == javax.swing.JOptionPane.NO_OPTION) {
				return;
			}	
			
			VCMessage msg = vcMessaging_clientTopicProducerSession.createObjectMessage(new BigString(/*getBroadcastMessageTextArea().getText()*/ "Some long text"));
			String username = /*getBroadcastMessageToTextField().getText()*/ "getBroadcastMessageToTextField";

			if (username.equalsIgnoreCase(VCMessagingConstants.USERNAME_PROPERTY_VALUE_ALL)) {
				username = VCMessagingConstants.USERNAME_PROPERTY_VALUE_ALL;
			}
				
			msg.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_BROADCASTMESSAGE_VALUE);
			msg.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, username);
			
			vcMessaging_clientTopicProducerSession.sendTopicMessage(VCellTopic.ClientStatusTopic, msg);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
/*
	public static void main(java.lang.String[] args) {
		try {		
			if (args.length == 2 && args[0].equals("-password")) {
//				ServerManageConsole.commandLineAdminPassword = args[1];
			}
			PropertyLoader.loadProperties();
			
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

			VCMessagingService vcMessagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class);
//			vcMessagingService.setDelegate(new ServerMessagingDelegate());
//
//			SessionLog log = new StdoutSessionLog("Console");
//
//			ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(log);
//			KeyFactory keyFactory = conFactory.getKeyFactory();
//			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory,keyFactory,log);
//			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory,log);
//
//			ServerManageConsole aServerManageConsole = new ServerManageConsole(vcMessagingService, adminDbTopLevel, databaseServerImpl, log);
//			aServerManageConsole.reconnect();
//
//			aServerManageConsole.addWindowListener(new java.awt.event.WindowAdapter() {
//				public void windowClosing(java.awt.event.WindowEvent e) {
//					System.exit(0);
//				};
//			});
//			java.awt.Insets insets = aServerManageConsole.getInsets();
//			aServerManageConsole.setSize(aServerManageConsole.getWidth() + insets.left + insets.right, aServerManageConsole.getHeight() + insets.top + insets.bottom);
//			aServerManageConsole.setLocation(200, 200);		
//			aServerManageConsole.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JFrame");
			exception.printStackTrace(System.out);
		}
	}
*/
}
