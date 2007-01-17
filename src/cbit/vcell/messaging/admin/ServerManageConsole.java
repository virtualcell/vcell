package cbit.vcell.messaging.admin;
import java.text.SimpleDateFormat;
import cbit.vcell.server.ServerInfo;
import cbit.sql.KeyValue;
import cbit.vcell.server.VCellServer;
import cbit.vcell.messaging.server.RpcDbServerProxy;
import cbit.util.BigString;
import cbit.vcell.server.User;
import java.io.IOException;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.util.*;
import javax.jms.*;
import javax.swing.*;
import cbit.vcell.messaging.*;
import cbit.vcell.messaging.db.*;
import cbit.vcell.messaging.server.RpcSimServerProxy;
import javax.swing.border.EtchedBorder;

/**
 * Insert the type's description here.
 * Creation date: (8/15/2003 4:19:19 PM)
 * @author: Fei Gao
 */
public class ServerManageConsole extends JFrame implements ControlTopicListener {
	private cbit.vcell.server.VCellBootstrap vcellBootstrap = null;
	private VCellServer vcellServer = null;
	private cbit.vcell.server.SessionLog log = null;
	private JmsFileReceiver fileChannelReceiver = null;
	private int msgCounter = -1;
	private int bootstrapCounter = -1;
	private GatherInfoDialog gatherDialog = null;
	private List serverList = Collections.synchronizedList(new LinkedList());
	private List userList = Collections.synchronizedList(new LinkedList());
	private List serviceList = Collections.synchronizedList(new LinkedList());
	private JPanel ivjJFrameContentPane = null;
	private javax.swing.Timer gatherTimer = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.messaging.VCellTopicConnection topicConn = null;
	private cbit.vcell.messaging.VCellQueueConnection queueConn = null;
	private cbit.vcell.messaging.VCellTopicSession topicSession = null;
	private cbit.vcell.messaging.JmsConnectionFactory jmsConnFactory = null;
	private JLabel ivjJLabel1 = null;
	private JTabbedPane ivjTabbedPane = null;
	private JPanel ivjServerStatusPage = null;
	private JPanel ivjServerStatusPanel = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjServerStatusTable = null;
	private JScrollPane ivjServerStatusTableScrollPane = null;
	private JPanel ivjServiceStatusPage = null;
	private JScrollPane ivjJScrollPane1 = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjServiceStatusTable = null;
	private JPanel ivjJPanel5 = null;
	private JButton ivjServerStatusOpenButton = null;
	private JPanel ivjJPanel6 = null;
	private JButton ivjStartServiceButton = null;
	private JButton ivjStopServiceButton = null;
	private JPanel ivjJPanel2 = null;
	private JPanel ivjJPanel3 = null;
	private JPanel ivjJPanel7 = null;
	private JPanel ivjJPanel8 = null;
	private JScrollPane ivjJScrollPane3 = null;
	private JSplitPane ivjJSplitPane1 = null;
	private JPanel ivjQueryPage = null;
	private JPanel ivjJPanel10 = null;
	private JPanel ivjJPanel15 = null;
	private JPanel ivjJPanel4 = null;
	private JPanel ivjJPanel9 = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjQueryResultTable = null;
	private JPanel ivjQueryStatusPanel = null;
	private JCheckBox ivjQueryCompletedCheck = null;
	private JCheckBox ivjQueryFailedCheck = null;
	private JCheckBox ivjQueryRunningCheck = null;
	private JCheckBox ivjQueryWaitingCheck = null;
	private JTextField ivjQueryHostField = null;
	private JTextField ivjQuerySimField = null;
	private JTextField ivjQueryUserField = null;
	private JCheckBox ivjQueryAllStatusCheck = null;
	private JButton ivjQueryGoButton = null;
	private JButton ivjQueryResetButton = null;
	private cbit.vcell.server.AdminDatabaseServer adminDbServer = null;
	private List statusChecks = new ArrayList();
	private JCheckBox ivjQueryStartDateCheck = null;
	private JCheckBox ivjQuerySubmitDateCheck = null;
	private JLabel ivjNumResultsLabel = null;
	private JCheckBox ivjQueryEndDateCheck = null;
	private JScrollPane ivjJScrollPane2 = null;
	private JButton ivjOpenServerLogButton = null;
	private JButton ivjOpenServiceLogButton = null;
	private DatePanel ivjQueryEndFromDate = null;
	private DatePanel ivjQueryEndToDate = null;
	private DatePanel ivjQueryStartFromDate = null;
	private DatePanel ivjQuerySubmitToDate = null;
	private DatePanel ivjQueryStartToDate = null;
	private JPanel ivjQuerySubmitDatePanel = null;
	private JPanel ivjQueryEndDatePanel = null;
	private JPanel ivjQueryStartDatePanel = null;
	private DatePanel ivjQuerySubmitFromDate = null;
	private JLabel ivjJLabel2 = null;
	private JLabel ivjNumServiceLabel = null;
	private JLabel ivjNumServerLabel = null;
	private JButton ivjStartBootstrapButton = null;
	private JButton ivjStopBootstrapButton = null;
	private JLabel ivjJLabel3 = null;
	private JLabel ivjJLabel4 = null;
	private JLabel ivjJLabel5 = null;
	private JCheckBox ivjQueryQueuedCheck = null;
	private JCheckBox ivjQueryStoppedCheck = null;
	private JCheckBox ivjQueryDispatchedCheck = null;
	private JLabel ivjFilterServerEqualLabel = null;
	private JButton ivjFilterServerGoButton = null;
	private JComboBox ivjFilterServerNameCombo = null;
	private JRadioButton ivjFilterServerRadioButton = null;
	private JComboBox ivjFilterServerValueCombo = null;
	private JButton ivjFilterServiceGoButton = null;
	private JComboBox ivjFilterServiceNameCombo = null;
	private JRadioButton ivjFilterServiceRadioButton = null;
	private JComboBox ivjFilterServiceValueCombo = null;
	private JLabel ivjFilterServiceEqualLabel = null;
	private JRadioButton ivjFilterServerShowAllRadioButton = null;
	private JRadioButton ivjFilterServiceShowAllRadioButton = null;
	private java.awt.FlowLayout ivjJPanel10FlowLayout = null;
	private java.awt.FlowLayout ivjJPanel2FlowLayout = null;
	private BoxLayout ivjJPanel3BoxLayout = null;
	private java.awt.FlowLayout ivjJPanel4FlowLayout = null;
	private java.awt.FlowLayout ivjJPanel9FlowLayout = null;
	private java.awt.GridLayout ivjQueryEndDatePanelGridLayout = null;
	private java.awt.FlowLayout ivjQueryEndFromDateFlowLayout = null;
	private java.awt.FlowLayout ivjQueryEndToDateFlowLayout = null;
	private java.awt.GridLayout ivjQueryStartDatePanelGridLayout = null;
	private java.awt.FlowLayout ivjQueryStartFromDateFlowLayout = null;
	private java.awt.FlowLayout ivjQueryStartToDateFlowLayout = null;
	private java.awt.GridLayout ivjQueryStatusPanelGridLayout = null;
	private java.awt.GridLayout ivjQuerySubmitDatePanelGridLayout = null;
	private java.awt.FlowLayout ivjQuerySubmitFromDateFlowLayout = null;
	private java.awt.FlowLayout ivjQuerySubmitToDateFlowLayout = null;
	private java.awt.FlowLayout ivjServerStatusPanelFlowLayout = null;
	private HashMap dbProxyHash = null;
	private HashMap simProxyHash = null;
	private JButton ivjExitButton = null;
	private JPanel ivjJPanel1 = null;
	private JButton ivjRefreshButton = null;
	private JPanel ivjJPanel11 = null;
	private java.awt.FlowLayout ivjJPanel11FlowLayout = null;
	private JButton ivjRemoveFromListButton = null;
	private JLabel ivjJLabel51 = null;
	private JPanel ivjJPanel101 = null;
	private java.awt.FlowLayout ivjJPanel101FlowLayout = null;
	private JTextField ivjQueryServerIDField = null;
	private JLabel ivjJLabel6 = null;
	private JPanel ivjJPanel12 = null;
	private java.awt.FlowLayout ivjJPanel12FlowLayout = null;
	private JPanel ivjUserConnectionPage = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjUserConnectionTable = null;
	private JScrollPane ivjJScrollPane4 = null;
	private UserConnectionTableModel ivjUserConnectionTableModel1 = null;
	private JLabel ivjNumUserConnectionLabel = null;
	private JPanel ivjBroadcastPanel = null;
	private JPanel ivjJPanel13 = null;
	private JPanel ivjJPanel14 = null;
	private JButton ivjMessageResetButton = null;
	private JButton ivjSendMessageButton = null;
	private JScrollPane ivjJScrollPane5 = null;
	private JTextArea ivjBroadcastMessageTextArea = null;
	private JTextField ivjBroadcastMessageToTextField = null;
	private JLabel ivjJLabel7 = null;
	private JPanel ivjJPanel16 = null;
	private JPanel ivjJPanel17 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.MouseListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ServerManageConsole.this.getServerStatusOpenButton()) 
				connEtoC12(e);
			if (e.getSource() == ServerManageConsole.this.getStopServiceButton()) 
				connEtoC15(e);
			if (e.getSource() == ServerManageConsole.this.getStartServiceButton()) 
				connEtoC16(e);
			if (e.getSource() == ServerManageConsole.this.getStopBootstrapButton()) 
				connEtoC1(e);
			if (e.getSource() == ServerManageConsole.this.getStartBootstrapButton()) 
				connEtoC3(e);
			if (e.getSource() == ServerManageConsole.this.getQueryGoButton()) 
				connEtoC25(e);
			if (e.getSource() == ServerManageConsole.this.getQueryResetButton()) 
				connEtoC26(e);
			if (e.getSource() == ServerManageConsole.this.getOpenServiceLogButton()) 
				connEtoC30(e);
			if (e.getSource() == ServerManageConsole.this.getOpenServerLogButton()) 
				connEtoC10(e);
			if (e.getSource() == ServerManageConsole.this.getFilterServiceGoButton()) 
				connEtoC33(e);
			if (e.getSource() == ServerManageConsole.this.getFilterServerGoButton()) 
				connEtoC37(e);
			if (e.getSource() == ServerManageConsole.this.getRefreshButton()) 
				connEtoC6();
			if (e.getSource() == ServerManageConsole.this.getExitButton()) 
				connEtoC4(e);
			if (e.getSource() == ServerManageConsole.this.getRemoveFromListButton()) 
				connEtoC7(e);
			if (e.getSource() == ServerManageConsole.this.getSendMessageButton()) 
				connEtoC8(e);
			if (e.getSource() == ServerManageConsole.this.getMessageResetButton()) 
				connEtoC9();
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == ServerManageConsole.this.getQueryWaitingCheck()) 
				connEtoC11(e);
			if (e.getSource() == ServerManageConsole.this.getQueryQueuedCheck()) 
				connEtoC13(e);
			if (e.getSource() == ServerManageConsole.this.getQueryFailedCheck()) 
				connEtoC21(e);
			if (e.getSource() == ServerManageConsole.this.getQueryRunningCheck()) 
				connEtoC23(e);
			if (e.getSource() == ServerManageConsole.this.getQueryStoppedCheck()) 
				connEtoC24(e);
			if (e.getSource() == ServerManageConsole.this.getQueryCompletedCheck()) 
				connEtoC22(e);
			if (e.getSource() == ServerManageConsole.this.getQueryAllStatusCheck()) 
				connEtoC5(e);
			if (e.getSource() == ServerManageConsole.this.getQuerySubmitDateCheck()) 
				connEtoC27(e);
			if (e.getSource() == ServerManageConsole.this.getQueryStartDateCheck()) 
				connEtoC28(e);
			if (e.getSource() == ServerManageConsole.this.getQueryEndDateCheck()) 
				connEtoC29(e);
			if (e.getSource() == ServerManageConsole.this.getFilterServiceRadioButton()) 
				connEtoC32(e);
			if (e.getSource() == ServerManageConsole.this.getFilterServiceShowAllRadioButton()) 
				connEtoC34(e);
			if (e.getSource() == ServerManageConsole.this.getQueryDispatchedCheck()) 
				connEtoC20(e);
			if (e.getSource() == ServerManageConsole.this.getFilterServerShowAllRadioButton()) 
				connEtoC35(e);
			if (e.getSource() == ServerManageConsole.this.getFilterServerRadioButton()) 
				connEtoC36(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == ServerManageConsole.this.getServerStatusTable()) 
				connEtoC14(e);
			if (e.getSource() == ServerManageConsole.this.getServiceStatusTable()) 
				connEtoC17(e);
			if (e.getSource() == ServerManageConsole.this.getQueryResultTable()) 
				connEtoC31(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == ServerManageConsole.this.getTabbedPane()) 
				connEtoC18();
		};
	};

/**
 * ServerManageConsole constructor comment.
 */
public ServerManageConsole() throws java.io.IOException, java.io.FileNotFoundException, org.jdom.JDOMException, javax.jms.JMSException {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 1:15:33 PM)
 */
public void applyChanges(VCServerInfo serverInfo, LinkedList changes, String archiveDir) {
	try {	
		Message msg = topicSession.createObjectMessage(changes);
			
		msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_CHANGECONFIG_VALUE);
		msg.setStringProperty(ManageConstants.ARCHIVE_DIR_PROPERTY, archiveDir);
		msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, serverInfo.getHostName());
		msg.setStringProperty(ManageConstants.SERVER_TYPE_PROPERTY, serverInfo.getServerType());
		msg.setStringProperty(ManageConstants.SERVER_NAME_PROPERTY, serverInfo.getServerName());
		
		log.print("sending change message [" + JmsUtils.toString(msg) + "]");		
		topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);

		int index = serverList.indexOf(serverInfo);
		VCServerInfo serverInfo0 = (VCServerInfo)serverList.get(index);
		serverInfo0.doChange(changes, archiveDir);

		if (serverInfo.isServerManager()) {
			bootstrapCounter = -1;
			onArrivingServerManager(serverInfo0);
		} else {
			updateServiceTable(changes);
		}
	} catch (Exception ex) {
		log.exception(ex);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/2004 1:17:47 PM)
 */
private void clearServerStatusTab() {
	getServerStatusTable().clearSelection();
	getStopBootstrapButton().setEnabled(false);
	getStartBootstrapButton().setEnabled(false);
	getOpenServerLogButton().setEnabled(false);
	getServerStatusOpenButton().setEnabled(false);
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/2004 1:36:54 PM)
 */
private void clearServiceStatusTab() {
	getServiceStatusTable().clearSelection();
	getOpenServiceLogButton().setEnabled(false);
	getStopServiceButton().setEnabled(false);
	getStartServiceButton().setEnabled(false);
}


/**
 * connEtoC1:  (StopServerButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.stopServerButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stopBootstrapButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC10:  (OpenServerLogButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.openServerLogButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.openServerLogButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (QueryWaitingCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryWaitingCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryWaitingCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (ServerStatusOpenButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.serverStatusOpenButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.serverStatusOpenButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (QueryDispatchedCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryDispatchedCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryQueuedCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC14:  (ServerStatusTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> ServerManageConsole.serverStatusTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.serverStatusTable_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (StopServiceButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.stopServiceButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stopServiceButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC16:  (StartServiceButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.startServiceButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.startServiceButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC17:  (ServiceStatusTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> ServerManageConsole.serviceStatusTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.serviceStatusTable_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC18:  (TabbedPane.change. --> ServerManageConsole.tabbedPane_ChangeEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18() {
	try {
		// user code begin {1}
		// user code end
		this.tabbedPane_ChangeEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ServerManageConsole.initialize() --> ServerManageConsole.serverManageConsole_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.serverManageConsole_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC20:  (QueryDispatchedCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryDispatchedCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryDispatchedCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC21:  (QueryFailedCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryFailedCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryFailedCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC22:  (QueryCompletedCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryCompletedCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC22(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryCompletedCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC23:  (QueryRunningCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryRunningCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC23(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryRunningCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC24:  (QueryAbortedCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryAbortedCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC24(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryStoppedCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC25:  (QueryGoButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.queryGoButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC25(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryGoButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC26:  (QueryResetButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.queryResetButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC26(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryResetButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC27:  (QuerySubmitDateCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.querySubmitDateCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC27(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.querySubmitDateCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC28:  (QueryStartDateCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryStartDateCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC28(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryStartDateCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC29:  (QueryEndDateCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryEndDateSubmit_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC29(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryEndDateSubmit_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (StartServerButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.startServerButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.startBootstrapButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC30:  (OpenLogButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.openLogButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC30(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.openLogButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC31:  (QueryResultTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> ServerManageConsole.queryResultTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC31(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryResultTable_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC32:  (FilterRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.filterRadioButton_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC32(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.filterServiceRadioButton_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC33:  (FilterGoButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.filterGoButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC33(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.filterServiceGoButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC34:  (FilterShowAllRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.filterShowAllRadioButton_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC34(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.filterServiceShowAllRadioButton_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC35:  (FilterServerShowAllRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.filterServerShowAllRadioButton_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC35(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.filterServerShowAllRadioButton_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC36:  (FilterServerRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.filterServerRadioButton_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC36(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.filterServerRadioButton_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC37:  (FilterServerGoButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.filterServiceGoButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC37(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.filterServerGoButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (ExitButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.exitButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.exitButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (QueryAllStatusCheck.item.itemStateChanged(java.awt.event.ItemEvent) --> ServerManageConsole.queryAllStatusCheck_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.queryAllStatusCheck_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (RefreshButton.action. --> ServerManageConsole.refreshButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6() {
	try {
		// user code begin {1}
		// user code end
		this.refreshButton_ActionPerformed(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (RemoveFromListButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.removeFromListButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.removeFromListButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (SendMessageButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerManageConsole.sendMessageButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sendMessageButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (MessageResetButton.action. --> ServerManageConsole.messageResetButton_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9() {
	try {
		// user code begin {1}
		// user code end
		this.messageResetButton_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (UserConnectionTableModel1.this <--> UserConnectionTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getUserConnectionTable().setModel(getUserConnectionTableModel1());
		getUserConnectionTable().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void detailMenuItem_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	openServer();
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:19:57 AM)
 * @return cbit.vcell.messaging.admin.GatherInfoDialog
 */
private void displayGatherInfoDialog() {
	//
	// initialize and start timer
	//
	gatherTimer = new javax.swing.Timer(200,new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			gatherDialog.setValue(msgCounter);
		}
	});
	gatherTimer.setInitialDelay(500);
	gatherTimer.start();

	gatherDialog = new GatherInfoDialog(this, bootstrapCounter == -1 ? 40 : bootstrapCounter + 1);		
	gatherDialog.setValue(0);
	gatherDialog.setLocationRelativeTo(this);
	gatherDialog.setVisible(true);
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:19:57 AM)
 * @return cbit.vcell.messaging.admin.GatherInfoDialog
 */
private void displayOpenLogProgressDialog(final long expectedLogLength) {
	//
	// initialize and start timer
	//
	gatherTimer = new javax.swing.Timer(200,new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			gatherDialog.setValue(fileChannelReceiver.getCurrentFile() == null ? 0 : (int)(100 * fileChannelReceiver.getCurrentFile().length()/expectedLogLength));
		}
	});
	gatherTimer.setInitialDelay(500);
	gatherTimer.start();

	gatherDialog = new GatherInfoDialog(this, 100);
	gatherDialog.setValue(0);
	gatherDialog.setLocationRelativeTo(this);
	gatherDialog.setVisible(true);
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:19:57 AM)
 * @return cbit.vcell.messaging.admin.GatherInfoDialog
 */
private void disposeGatherInfoDialog() {
	if (gatherDialog == null) {
		return;
	}
	gatherTimer.stop();
	gatherTimer = null;
	
	gatherDialog.dispose();
	gatherDialog = null;
}


/**
 * Comment
 */
public void exitButton_ActionPerformed(ActionEvent actionEvent) {
	dispose();
	System.exit(0);
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (2/17/2004 1:05:24 PM)
 */
private void filterServer() {
	if (getFilterServerShowAllRadioButton().isSelected()) {
		showServer(serverList);
	} else {
		String name = (String)getFilterServerNameCombo().getSelectedItem();
		String value = (String)getFilterServerValueCombo().getSelectedItem();
		List newList = new ArrayList();
		
		if (name.equals("Alive")) {
			boolean realValue = value.equals("true") ? true : false;
			Iterator iter = serverList.iterator();
			while (iter.hasNext()) {
				VCServerInfo serverInfo = (VCServerInfo)iter.next();
				if (serverInfo.isAlive() == realValue) {
					newList.add(serverInfo);
				}
			}
			showServer(newList);
		}
	}
}


/**
 * Comment
 */
public void filterServerGoButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	filterServer();
	return;
}


/**
 * Comment
 */
public void filterServerRadioButton_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
		getFilterServerEqualLabel().setEnabled(false);
		getFilterServerNameCombo().setEnabled(false);
		getFilterServerValueCombo().setEnabled(false);
		getFilterServerGoButton().setEnabled(false);
	} else if (itemEvent.getStateChange() == ItemEvent.SELECTED) {		
		getFilterServerEqualLabel().setEnabled(true);
		getFilterServerNameCombo().setEnabled(true);
		getFilterServerValueCombo().setEnabled(true);
		getFilterServerGoButton().setEnabled(true);
	}
	
	return;
}


/**
 * Comment
 */
public void filterServerShowAllRadioButton_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
		filterServer();
	} else {
	}
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (2/17/2004 1:05:24 PM)
 */
private void filterService() {
	if (getFilterServiceShowAllRadioButton().isSelected()) {
		showService(serviceList);
	} else {
		String name = (String)getFilterServiceNameCombo().getSelectedItem();
		String value = (String)getFilterServiceValueCombo().getSelectedItem();
		List newList = new ArrayList();
		
		if (name.equals("Alive")) {
			boolean realValue = value.equals("true") ? true : false;
			Iterator iter = serviceList.iterator();
			while (iter.hasNext()) {
				VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
				if (serviceInfo.isAlive() == realValue) {
					newList.add(serviceInfo);
				}
			}
			showService(newList);
		}
	}
}


/**
 * Comment
 */
public void filterServiceGoButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	filterService();
	return;
}


/**
 * Comment
 */
public void filterServiceRadioButton_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
		getFilterServiceEqualLabel().setEnabled(false);
		getFilterServiceNameCombo().setEnabled(false);
		getFilterServiceValueCombo().setEnabled(false);
		getFilterServiceGoButton().setEnabled(false);
	} else if (itemEvent.getStateChange() == ItemEvent.SELECTED) {		
		getFilterServiceEqualLabel().setEnabled(true);
		getFilterServiceNameCombo().setEnabled(true);
		getFilterServiceValueCombo().setEnabled(true);
		getFilterServiceGoButton().setEnabled(true);
	}
	
	return;
}


/**
 * Comment
 */
public void filterServiceShowAllRadioButton_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
		filterService();
	} else {
	}
	return;
}


/**
 * Return the BroadcastMessageTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getBroadcastMessageTextArea() {
	if (ivjBroadcastMessageTextArea == null) {
		try {
			ivjBroadcastMessageTextArea = new javax.swing.JTextArea();
			ivjBroadcastMessageTextArea.setName("BroadcastMessageTextArea");
			ivjBroadcastMessageTextArea.setLineWrap(true);
			ivjBroadcastMessageTextArea.setWrapStyleWord(true);
			ivjBroadcastMessageTextArea.setText("");
			ivjBroadcastMessageTextArea.setFont(new java.awt.Font("Arial", 1, 12));
			ivjBroadcastMessageTextArea.setBounds(0, 0, 376, 68);
			ivjBroadcastMessageTextArea.setMargin(new java.awt.Insets(5, 5, 5, 5));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBroadcastMessageTextArea;
}

/**
 * Return the BroadcastMessageToTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getBroadcastMessageToTextField() {
	if (ivjBroadcastMessageToTextField == null) {
		try {
			ivjBroadcastMessageToTextField = new javax.swing.JTextField();
			ivjBroadcastMessageToTextField.setName("BroadcastMessageToTextField");
			ivjBroadcastMessageToTextField.setFont(new java.awt.Font("Arial", 1, 12));
			ivjBroadcastMessageToTextField.setText("All");
			ivjBroadcastMessageToTextField.setMargin(new java.awt.Insets(0, 5, 0, 0));
			ivjBroadcastMessageToTextField.setColumns(50);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBroadcastMessageToTextField;
}

/**
 * Return the BroadcastPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getBroadcastPanel() {
	if (ivjBroadcastPanel == null) {
		try {
			ivjBroadcastPanel = new javax.swing.JPanel();
			ivjBroadcastPanel.setName("BroadcastPanel");
			ivjBroadcastPanel.setPreferredSize(new java.awt.Dimension(495, 500));
			ivjBroadcastPanel.setLayout(new java.awt.BorderLayout());
			getBroadcastPanel().add(getJPanel13(), "Center");
			getBroadcastPanel().add(getJPanel14(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBroadcastPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (4/5/2006 10:59:42 AM)
 * @return int
 */
public Date getConnectionTimeFromRMIbootstrapLogfile(User user) throws Exception {
	return new Date();
	
	//java.io.File file = new java.io.File("\\\\ms3\\vcell\\" 
		//+ cbit.vcell.server.PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.vcellServerIDProperty) 
		//+ "\\RMIbootstrap.log");
	//if (!file.exists()) {		
		//throw new java.io.FileNotFoundException("RMI bootstrap log file doesn't exist: " + file.getAbsolutePath());
	//}

	//String cmd = "grep \"LocalVCellBootstrap.getVCellConnection(" + user.getName() + ",\" " + file.getAbsolutePath();
	//cbit.util.Executable exe = new cbit.util.Executable(cmd);
	//exe.start();
		
	//String output = exe.getStdoutString();
	//java.util.StringTokenizer st = new java.util.StringTokenizer(output, "\n");

	//String line = null;
	//while (st.hasMoreTokens()) {
		//line = st.nextToken();
	//}
	
	////  now line is the last line
	//st = new java.util.StringTokenizer(line, " ");
	//st.nextToken();
	//st.nextToken();
	//String time = st.nextToken();
	//for (int i = 0; i < 5; i ++) {
		//if (st.hasMoreTokens()) {
			//time += " " + st.nextToken();
		//}
	//}
	//SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",java.util.Locale.US);

	//Date date =	formatter.parse(time);
	//return date;
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:44:01 PM)
 * @return cbit.vcell.messaging.server.RpcDbServerProxy
 */
private cbit.vcell.messaging.server.RpcDbServerProxy getDbProxy(User user) throws JMSException, cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	if (dbProxyHash == null) {
		dbProxyHash = new HashMap();
	}

	RpcDbServerProxy dbProxy = (RpcDbServerProxy)dbProxyHash.get(user);

	if (dbProxy == null) {
		JmsClientMessaging jmsClientMessaging = new JmsClientMessaging(queueConn, log);		
		dbProxy = new cbit.vcell.messaging.server.RpcDbServerProxy(user, jmsClientMessaging, log);
		dbProxyHash.put(user, dbProxy);
	}
	
	return dbProxy;
}


/**
 * Return the ExitButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getExitButton() {
	if (ivjExitButton == null) {
		try {
			ivjExitButton = new javax.swing.JButton();
			ivjExitButton.setName("ExitButton");
			ivjExitButton.setText("Exit");
			ivjExitButton.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExitButton;
}

/**
 * Return the FilterServerEqualLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFilterServerEqualLabel() {
	if (ivjFilterServerEqualLabel == null) {
		try {
			ivjFilterServerEqualLabel = new javax.swing.JLabel();
			ivjFilterServerEqualLabel.setName("FilterServerEqualLabel");
			ivjFilterServerEqualLabel.setFont(new java.awt.Font("Arial", 1, 14));
			ivjFilterServerEqualLabel.setText("=");
			ivjFilterServerEqualLabel.setEnabled(false);
			ivjFilterServerEqualLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServerEqualLabel;
}


/**
 * Return the FilterServerGoButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getFilterServerGoButton() {
	if (ivjFilterServerGoButton == null) {
		try {
			ivjFilterServerGoButton = new javax.swing.JButton();
			ivjFilterServerGoButton.setName("FilterServerGoButton");
			ivjFilterServerGoButton.setText("Go");
			ivjFilterServerGoButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServerGoButton;
}


/**
 * Return the FilterServerNameCombo property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFilterServerNameCombo() {
	if (ivjFilterServerNameCombo == null) {
		try {
			ivjFilterServerNameCombo = new javax.swing.JComboBox();
			ivjFilterServerNameCombo.setName("FilterServerNameCombo");
			ivjFilterServerNameCombo.setPreferredSize(new java.awt.Dimension(131, 23));
			ivjFilterServerNameCombo.setSelectedIndex(-1);
			ivjFilterServerNameCombo.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServerNameCombo;
}


/**
 * Return the FilterServerRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getFilterServerRadioButton() {
	if (ivjFilterServerRadioButton == null) {
		try {
			ivjFilterServerRadioButton = new javax.swing.JRadioButton();
			ivjFilterServerRadioButton.setName("FilterServerRadioButton");
			ivjFilterServerRadioButton.setText("Filter: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServerRadioButton;
}


/**
 * Return the FilterShowAllServerRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getFilterServerShowAllRadioButton() {
	if (ivjFilterServerShowAllRadioButton == null) {
		try {
			ivjFilterServerShowAllRadioButton = new javax.swing.JRadioButton();
			ivjFilterServerShowAllRadioButton.setName("FilterServerShowAllRadioButton");
			ivjFilterServerShowAllRadioButton.setSelected(true);
			ivjFilterServerShowAllRadioButton.setText("Show All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServerShowAllRadioButton;
}

/**
 * Return the FilterServerValueCombo property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFilterServerValueCombo() {
	if (ivjFilterServerValueCombo == null) {
		try {
			ivjFilterServerValueCombo = new javax.swing.JComboBox();
			ivjFilterServerValueCombo.setName("FilterServerValueCombo");
			ivjFilterServerValueCombo.setPreferredSize(new java.awt.Dimension(131, 23));
			ivjFilterServerValueCombo.setEnabled(false);
			ivjFilterServerValueCombo.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServerValueCombo;
}


/**
 * Return the JLabel21 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFilterServiceEqualLabel() {
	if (ivjFilterServiceEqualLabel == null) {
		try {
			ivjFilterServiceEqualLabel = new javax.swing.JLabel();
			ivjFilterServiceEqualLabel.setName("FilterServiceEqualLabel");
			ivjFilterServiceEqualLabel.setFont(new java.awt.Font("Arial", 1, 14));
			ivjFilterServiceEqualLabel.setText("=");
			ivjFilterServiceEqualLabel.setEnabled(false);
			ivjFilterServiceEqualLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServiceEqualLabel;
}

/**
 * Return the FilterGoButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getFilterServiceGoButton() {
	if (ivjFilterServiceGoButton == null) {
		try {
			ivjFilterServiceGoButton = new javax.swing.JButton();
			ivjFilterServiceGoButton.setName("FilterServiceGoButton");
			ivjFilterServiceGoButton.setText("Go");
			ivjFilterServiceGoButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServiceGoButton;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFilterServiceNameCombo() {
	if (ivjFilterServiceNameCombo == null) {
		try {
			ivjFilterServiceNameCombo = new javax.swing.JComboBox();
			ivjFilterServiceNameCombo.setName("FilterServiceNameCombo");
			ivjFilterServiceNameCombo.setPreferredSize(new java.awt.Dimension(131, 23));
			ivjFilterServiceNameCombo.setSelectedIndex(-1);
			ivjFilterServiceNameCombo.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServiceNameCombo;
}

/**
 * Return the FilterRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getFilterServiceRadioButton() {
	if (ivjFilterServiceRadioButton == null) {
		try {
			ivjFilterServiceRadioButton = new javax.swing.JRadioButton();
			ivjFilterServiceRadioButton.setName("FilterServiceRadioButton");
			ivjFilterServiceRadioButton.setText("Filter: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServiceRadioButton;
}

/**
 * Return the FilterShowAllRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getFilterServiceShowAllRadioButton() {
	if (ivjFilterServiceShowAllRadioButton == null) {
		try {
			ivjFilterServiceShowAllRadioButton = new javax.swing.JRadioButton();
			ivjFilterServiceShowAllRadioButton.setName("FilterServiceShowAllRadioButton");
			ivjFilterServiceShowAllRadioButton.setSelected(true);
			ivjFilterServiceShowAllRadioButton.setText("Show All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServiceShowAllRadioButton;
}

/**
 * Return the JComboBox2 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFilterServiceValueCombo() {
	if (ivjFilterServiceValueCombo == null) {
		try {
			ivjFilterServiceValueCombo = new javax.swing.JComboBox();
			ivjFilterServiceValueCombo.setName("FilterServiceValueCombo");
			ivjFilterServiceValueCombo.setPreferredSize(new java.awt.Dimension(131, 23));
			ivjFilterServiceValueCombo.setEnabled(false);
			ivjFilterServiceValueCombo.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilterServiceValueCombo;
}

/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JFrame getJFrame() {
	return this;
}

/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			getJFrameContentPane().add(getTabbedPane(), "Center");
			getJFrameContentPane().add(getJPanel1(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Total Servers:");
			ivjJLabel1.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Total Services:");
			ivjJLabel2.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}

/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Simulaiton ID");
			ivjJLabel3.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel3.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel3.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("Compute Host");
			ivjJLabel4.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel4.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel4.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}

/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("User ID");
			ivjJLabel5.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel5.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel5.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}

/**
 * Return the JLabel51 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel51() {
	if (ivjJLabel51 == null) {
		try {
			ivjJLabel51 = new javax.swing.JLabel();
			ivjJLabel51.setName("JLabel51");
			ivjJLabel51.setText("Server ID");
			ivjJLabel51.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel51.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel51.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel51.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel51;
}


/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel6() {
	if (ivjJLabel6 == null) {
		try {
			ivjJLabel6 = new javax.swing.JLabel();
			ivjJLabel6.setName("JLabel6");
			ivjJLabel6.setText("Total Active Users:");
			ivjJLabel6.setForeground(java.awt.Color.red);
			ivjJLabel6.setRequestFocusEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel6;
}

/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel7() {
	if (ivjJLabel7 == null) {
		try {
			ivjJLabel7 = new javax.swing.JLabel();
			ivjJLabel7.setName("JLabel7");
			ivjJLabel7.setText("To      ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel7;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
			ivjJPanel1.setLayout(new java.awt.GridLayout());
			getJPanel1().add(getJPanel11(), getJPanel11().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel10 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel10() {
	if (ivjJPanel10 == null) {
		try {
			ivjJPanel10 = new javax.swing.JPanel();
			ivjJPanel10.setName("JPanel10");
			ivjJPanel10.setLayout(getJPanel10FlowLayout());
			getJPanel10().add(getJLabel5(), getJLabel5().getName());
			getJPanel10().add(getQueryUserField(), getQueryUserField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel10;
}

/**
 * Return the JPanel101 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel101() {
	if (ivjJPanel101 == null) {
		try {
			ivjJPanel101 = new javax.swing.JPanel();
			ivjJPanel101.setName("JPanel101");
			ivjJPanel101.setLayout(getJPanel101FlowLayout());
			getJPanel101().add(getJLabel51(), getJLabel51().getName());
			getJPanel101().add(getQueryServerIDField(), getQueryServerIDField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel101;
}


/**
 * Return the JPanel101FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel101FlowLayout() {
	java.awt.FlowLayout ivjJPanel101FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel101FlowLayout = new java.awt.FlowLayout();
		ivjJPanel101FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel101FlowLayout;
}


/**
 * Return the JPanel10FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel10FlowLayout() {
	java.awt.FlowLayout ivjJPanel10FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel10FlowLayout = new java.awt.FlowLayout();
		ivjJPanel10FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel10FlowLayout;
}


/**
 * Return the JPanel11 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel11() {
	if (ivjJPanel11 == null) {
		try {
			ivjJPanel11 = new javax.swing.JPanel();
			ivjJPanel11.setName("JPanel11");
			ivjJPanel11.setLayout(getJPanel11FlowLayout());
			ivjJPanel11.add(getExitButton());
			getJPanel11().add(getRefreshButton(), getRefreshButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel11;
}

/**
 * Return the JPanel11FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel11FlowLayout() {
	java.awt.FlowLayout ivjJPanel11FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel11FlowLayout = new java.awt.FlowLayout();
		ivjJPanel11FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel11FlowLayout;
}


/**
 * Return the JPanel12 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel12() {
	if (ivjJPanel12 == null) {
		try {
			ivjJPanel12 = new javax.swing.JPanel();
			ivjJPanel12.setName("JPanel12");
			ivjJPanel12.setLayout(getJPanel12FlowLayout());
			getJPanel12().add(getJLabel6(), getJLabel6().getName());
			getJPanel12().add(getNumUserConnectionLabel(), getNumUserConnectionLabel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel12;
}

/**
 * Return the JPanel12FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel12FlowLayout() {
	java.awt.FlowLayout ivjJPanel12FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel12FlowLayout = new java.awt.FlowLayout();
		ivjJPanel12FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel12FlowLayout;
}


/**
 * Return the JPanel13 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel13() {
	if (ivjJPanel13 == null) {
		try {
			ivjJPanel13 = new javax.swing.JPanel();
			ivjJPanel13.setName("JPanel13");
			ivjJPanel13.setLayout(new java.awt.BorderLayout());
			getJPanel13().add(getJPanel16(), "Center");
			getJPanel13().add(getJPanel17(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel13;
}

/**
 * Return the JPanel14 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel14() {
	if (ivjJPanel14 == null) {
		try {
			ivjJPanel14 = new javax.swing.JPanel();
			ivjJPanel14.setName("JPanel14");
			ivjJPanel14.setPreferredSize(new java.awt.Dimension(610, 200));
			ivjJPanel14.setLayout(new java.awt.FlowLayout());
			getJPanel14().add(getJScrollPane5(), getJScrollPane5().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel14;
}

/**
 * Return the JPanel15 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel15() {
	if (ivjJPanel15 == null) {
		try {
			ivjJPanel15 = new javax.swing.JPanel();
			ivjJPanel15.setName("JPanel15");
			ivjJPanel15.setLayout(new java.awt.FlowLayout());
			getJPanel15().add(getQueryGoButton(), getQueryGoButton().getName());
			getJPanel15().add(getQueryResetButton(), getQueryResetButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel15;
}

/**
 * Return the JPanel16 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel16() {
	if (ivjJPanel16 == null) {
		try {
			ivjJPanel16 = new javax.swing.JPanel();
			ivjJPanel16.setName("JPanel16");
			ivjJPanel16.setLayout(new java.awt.FlowLayout());
			getJPanel16().add(getSendMessageButton(), getSendMessageButton().getName());
			getJPanel16().add(getMessageResetButton(), getMessageResetButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel16;
}


/**
 * Return the JPanel17 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel17() {
	if (ivjJPanel17 == null) {
		try {
			ivjJPanel17 = new javax.swing.JPanel();
			ivjJPanel17.setName("JPanel17");
			ivjJPanel17.setLayout(new java.awt.FlowLayout());
			getJPanel17().add(getJLabel7(), getJLabel7().getName());
			getJPanel17().add(getBroadcastMessageToTextField(), getBroadcastMessageToTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel17;
}


/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(getJPanel2FlowLayout());
			getJPanel2().add(getJLabel2(), getJLabel2().getName());
			ivjJPanel2.add(getNumServiceLabel());
			ivjJPanel2.add(getFilterServiceShowAllRadioButton());
			ivjJPanel2.add(getFilterServiceRadioButton());
			ivjJPanel2.add(getFilterServiceNameCombo());
			ivjJPanel2.add(getFilterServiceEqualLabel());
			ivjJPanel2.add(getFilterServiceValueCombo());
			ivjJPanel2.add(getFilterServiceGoButton());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JPanel2FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel2FlowLayout() {
	java.awt.FlowLayout ivjJPanel2FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel2FlowLayout = new java.awt.FlowLayout();
		ivjJPanel2FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel2FlowLayout;
}


/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(getJPanel3BoxLayout());
			ivjJPanel3.setBounds(0, 0, 160, 120);
			getJPanel3().add(getJPanel4(), getJPanel4().getName());
			getJPanel3().add(getJPanel9(), getJPanel9().getName());
			getJPanel3().add(getJPanel101(), getJPanel101().getName());
			getJPanel3().add(getJPanel10(), getJPanel10().getName());
			getJPanel3().add(getQueryStatusPanel(), getQueryStatusPanel().getName());
			getJPanel3().add(getQuerySubmitDatePanel(), getQuerySubmitDatePanel().getName());
			getJPanel3().add(getQueryStartDatePanel(), getQueryStartDatePanel().getName());
			getJPanel3().add(getQueryEndDatePanel(), getQueryEndDatePanel().getName());
			getJPanel3().add(getJPanel15(), getJPanel15().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}

/**
 * Return the JPanel3BoxLayout property value.
 * @return javax.swing.BoxLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.BoxLayout getJPanel3BoxLayout() {
	javax.swing.BoxLayout ivjJPanel3BoxLayout = null;
	try {
		/* Create part */
		ivjJPanel3BoxLayout = new javax.swing.BoxLayout(getJPanel3(), javax.swing.BoxLayout.Y_AXIS);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel3BoxLayout;
}


/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(getJPanel4FlowLayout());
			getJPanel4().add(getJLabel3(), getJLabel3().getName());
			getJPanel4().add(getQuerySimField(), getQuerySimField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}

/**
 * Return the JPanel4FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel4FlowLayout() {
	java.awt.FlowLayout ivjJPanel4FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel4FlowLayout = new java.awt.FlowLayout();
		ivjJPanel4FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel4FlowLayout;
}


/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setLayout(new java.awt.FlowLayout());
			getJPanel5().add(getServerStatusOpenButton(), getServerStatusOpenButton().getName());
			getJPanel5().add(getOpenServerLogButton(), getOpenServerLogButton().getName());
			getJPanel5().add(getStartBootstrapButton(), getStartBootstrapButton().getName());
			getJPanel5().add(getStopBootstrapButton(), getStopBootstrapButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel5;
}

/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel6() {
	if (ivjJPanel6 == null) {
		try {
			ivjJPanel6 = new javax.swing.JPanel();
			ivjJPanel6.setName("JPanel6");
			ivjJPanel6.setLayout(new java.awt.FlowLayout());
			getJPanel6().add(getOpenServiceLogButton(), getOpenServiceLogButton().getName());
			getJPanel6().add(getStartServiceButton(), getStartServiceButton().getName());
			getJPanel6().add(getStopServiceButton(), getStopServiceButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel6;
}

/**
 * Return the JPanel7 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel7() {
	if (ivjJPanel7 == null) {
		try {
			ivjJPanel7 = new javax.swing.JPanel();
			ivjJPanel7.setName("JPanel7");
			ivjJPanel7.setLayout(new java.awt.BorderLayout());
			getJPanel7().add(getJPanel8(), "North");
			getJPanel7().add(getJScrollPane2(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel7;
}

/**
 * Return the JPanel8 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel8() {
	if (ivjJPanel8 == null) {
		try {
			ivjJPanel8 = new javax.swing.JPanel();
			ivjJPanel8.setName("JPanel8");
			ivjJPanel8.setLayout(new java.awt.BorderLayout());
			getJPanel8().add(getNumResultsLabel(), "West");
			getJPanel8().add(getRemoveFromListButton(), "East");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel8;
}

/**
 * Return the JPanel9 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel9() {
	if (ivjJPanel9 == null) {
		try {
			ivjJPanel9 = new javax.swing.JPanel();
			ivjJPanel9.setName("JPanel9");
			ivjJPanel9.setLayout(getJPanel9FlowLayout());
			getJPanel9().add(getJLabel4(), getJLabel4().getName());
			getJPanel9().add(getQueryHostField(), getQueryHostField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel9;
}

/**
 * Return the JPanel9FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel9FlowLayout() {
	java.awt.FlowLayout ivjJPanel9FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel9FlowLayout = new java.awt.FlowLayout();
		ivjJPanel9FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel9FlowLayout;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPane1().setViewportView(getServiceStatusTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane2.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPane2().setViewportView(getQueryResultTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}

/**
 * Return the JScrollPane3 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane3() {
	if (ivjJScrollPane3 == null) {
		try {
			ivjJScrollPane3 = new javax.swing.JScrollPane();
			ivjJScrollPane3.setName("JScrollPane3");
			getJScrollPane3().setViewportView(getJPanel3());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane3;
}


/**
 * Return the JScrollPane4 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane4() {
	if (ivjJScrollPane4 == null) {
		try {
			ivjJScrollPane4 = new javax.swing.JScrollPane();
			ivjJScrollPane4.setName("JScrollPane4");
			ivjJScrollPane4.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane4.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane4().setViewportView(getUserConnectionTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane4;
}


/**
 * Return the JScrollPane5 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane5() {
	if (ivjJScrollPane5 == null) {
		try {
			ivjJScrollPane5 = new javax.swing.JScrollPane();
			ivjJScrollPane5.setName("JScrollPane5");
			ivjJScrollPane5.setPreferredSize(new java.awt.Dimension(700, 200));
			getJScrollPane5().setViewportView(getBroadcastMessageTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane5;
}

/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setDividerSize(2);
			ivjJSplitPane1.setLastDividerLocation(1);
			ivjJSplitPane1.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
			ivjJSplitPane1.setDividerLocation(220);
			getJSplitPane1().add(getJScrollPane3(), "left");
			getJSplitPane1().add(getJPanel7(), "right");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}

/**
 * Insert the method's description here.
 * Creation date: (4/5/2006 10:07:25 AM)
 * @return java.lang.String
 */
public String getLocalVCellBootstrapUrl() {
	String rmiHost = "ms3";
	int rmiPort = cbit.vcell.server.PropertyLoader.getIntProperty(cbit.vcell.server.PropertyLoader.rmiPortRegistry, 1099);
	return "//" + rmiHost + ":" + rmiPort + "/VCellBootstrapServer";
}


/**
 * Return the MessageResetButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getMessageResetButton() {
	if (ivjMessageResetButton == null) {
		try {
			ivjMessageResetButton = new javax.swing.JButton();
			ivjMessageResetButton.setName("MessageResetButton");
			ivjMessageResetButton.setText("Reset");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMessageResetButton;
}


/**
 * Return the NumResultsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNumResultsLabel() {
	if (ivjNumResultsLabel == null) {
		try {
			ivjNumResultsLabel = new javax.swing.JLabel();
			ivjNumResultsLabel.setName("NumResultsLabel");
			ivjNumResultsLabel.setText("  Query Results");
			ivjNumResultsLabel.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumResultsLabel;
}

/**
 * Return the statusLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNumServerLabel() {
	if (ivjNumServerLabel == null) {
		try {
			ivjNumServerLabel = new javax.swing.JLabel();
			ivjNumServerLabel.setName("NumServerLabel");
			ivjNumServerLabel.setText("0");
			ivjNumServerLabel.setMaximumSize(new java.awt.Dimension(100, 14));
			ivjNumServerLabel.setForeground(java.awt.Color.red);
			ivjNumServerLabel.setPreferredSize(new java.awt.Dimension(100, 14));
			ivjNumServerLabel.setMinimumSize(new java.awt.Dimension(100, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumServerLabel;
}

/**
 * Return the NumServiceLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNumServiceLabel() {
	if (ivjNumServiceLabel == null) {
		try {
			ivjNumServiceLabel = new javax.swing.JLabel();
			ivjNumServiceLabel.setName("NumServiceLabel");
			ivjNumServiceLabel.setPreferredSize(new java.awt.Dimension(100, 14));
			ivjNumServiceLabel.setText("0");
			ivjNumServiceLabel.setMaximumSize(new java.awt.Dimension(100, 14));
			ivjNumServiceLabel.setForeground(java.awt.Color.red);
			ivjNumServiceLabel.setMinimumSize(new java.awt.Dimension(100, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumServiceLabel;
}

/**
 * Return the TotalUserConnectionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNumUserConnectionLabel() {
	if (ivjNumUserConnectionLabel == null) {
		try {
			ivjNumUserConnectionLabel = new javax.swing.JLabel();
			ivjNumUserConnectionLabel.setName("NumUserConnectionLabel");
			ivjNumUserConnectionLabel.setText("0");
			ivjNumUserConnectionLabel.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumUserConnectionLabel;
}

/**
 * Return the OpenServerLogButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOpenServerLogButton() {
	if (ivjOpenServerLogButton == null) {
		try {
			ivjOpenServerLogButton = new javax.swing.JButton();
			ivjOpenServerLogButton.setName("OpenServerLogButton");
			ivjOpenServerLogButton.setText("Open Log");
			ivjOpenServerLogButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpenServerLogButton;
}

/**
 * Return the OpenServiceButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOpenServiceLogButton() {
	if (ivjOpenServiceLogButton == null) {
		try {
			ivjOpenServiceLogButton = new javax.swing.JButton();
			ivjOpenServiceLogButton.setName("OpenServiceLogButton");
			ivjOpenServiceLogButton.setText("Open Log");
			ivjOpenServiceLogButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpenServiceLogButton;
}

/**
 * Return the QueryAllCheck property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryAllStatusCheck() {
	if (ivjQueryAllStatusCheck == null) {
		try {
			ivjQueryAllStatusCheck = new javax.swing.JCheckBox();
			ivjQueryAllStatusCheck.setName("QueryAllStatusCheck");
			ivjQueryAllStatusCheck.setSelected(true);
			ivjQueryAllStatusCheck.setFont(new java.awt.Font("Arial", 1, 12));
			ivjQueryAllStatusCheck.setText("All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryAllStatusCheck;
}

/**
 * Return the JCheckBox7 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryCompletedCheck() {
	if (ivjQueryCompletedCheck == null) {
		try {
			ivjQueryCompletedCheck = new javax.swing.JCheckBox();
			ivjQueryCompletedCheck.setName("QueryCompletedCheck");
			ivjQueryCompletedCheck.setSelected(true);
			ivjQueryCompletedCheck.setText("Completed");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryCompletedCheck;
}

/**
 * Return the QueryDispatchedCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryDispatchedCheck() {
	if (ivjQueryDispatchedCheck == null) {
		try {
			ivjQueryDispatchedCheck = new javax.swing.JCheckBox();
			ivjQueryDispatchedCheck.setName("QueryDispatchedCheck");
			ivjQueryDispatchedCheck.setSelected(true);
			ivjQueryDispatchedCheck.setText("Dispatched");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryDispatchedCheck;
}

/**
 * Return the QueryEndDateCheck property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryEndDateCheck() {
	if (ivjQueryEndDateCheck == null) {
		try {
			ivjQueryEndDateCheck = new javax.swing.JCheckBox();
			ivjQueryEndDateCheck.setName("QueryEndDateCheck");
			ivjQueryEndDateCheck.setText("End Between");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryEndDateCheck;
}

/**
 * Return the QueryEndDatePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getQueryEndDatePanel() {
	if (ivjQueryEndDatePanel == null) {
		try {
			ivjQueryEndDatePanel = new javax.swing.JPanel();
			ivjQueryEndDatePanel.setName("QueryEndDatePanel");
			ivjQueryEndDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
			ivjQueryEndDatePanel.setLayout(getQueryEndDatePanelGridLayout());
			getQueryEndDatePanel().add(getQueryEndDateCheck(), getQueryEndDateCheck().getName());
			getQueryEndDatePanel().add(getQueryEndFromDate(), getQueryEndFromDate().getName());
			getQueryEndDatePanel().add(getQueryEndToDate(), getQueryEndToDate().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryEndDatePanel;
}

/**
 * Return the QueryEndDatePanelGridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getQueryEndDatePanelGridLayout() {
	java.awt.GridLayout ivjQueryEndDatePanelGridLayout = null;
	try {
		/* Create part */
		ivjQueryEndDatePanelGridLayout = new java.awt.GridLayout();
		ivjQueryEndDatePanelGridLayout.setRows(3);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQueryEndDatePanelGridLayout;
}


/**
 * Return the DatePanel3 property value.
 * @return cbit.vcell.messaging.admin.DatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DatePanel getQueryEndFromDate() {
	if (ivjQueryEndFromDate == null) {
		try {
			ivjQueryEndFromDate = new cbit.vcell.messaging.admin.DatePanel();
			ivjQueryEndFromDate.setName("QueryEndFromDate");
			ivjQueryEndFromDate.setLayout(getQueryEndFromDateFlowLayout());
			ivjQueryEndFromDate.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryEndFromDate;
}

/**
 * Return the QueryEndFromDateFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getQueryEndFromDateFlowLayout() {
	java.awt.FlowLayout ivjQueryEndFromDateFlowLayout = null;
	try {
		/* Create part */
		ivjQueryEndFromDateFlowLayout = new java.awt.FlowLayout();
		ivjQueryEndFromDateFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
		ivjQueryEndFromDateFlowLayout.setVgap(0);
		ivjQueryEndFromDateFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQueryEndFromDateFlowLayout;
}


/**
 * Return the QueryEndToDate property value.
 * @return cbit.vcell.messaging.admin.DatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DatePanel getQueryEndToDate() {
	if (ivjQueryEndToDate == null) {
		try {
			ivjQueryEndToDate = new cbit.vcell.messaging.admin.DatePanel();
			ivjQueryEndToDate.setName("QueryEndToDate");
			ivjQueryEndToDate.setLayout(getQueryEndToDateFlowLayout());
			ivjQueryEndToDate.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryEndToDate;
}

/**
 * Return the QueryEndToDateFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getQueryEndToDateFlowLayout() {
	java.awt.FlowLayout ivjQueryEndToDateFlowLayout = null;
	try {
		/* Create part */
		ivjQueryEndToDateFlowLayout = new java.awt.FlowLayout();
		ivjQueryEndToDateFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
		ivjQueryEndToDateFlowLayout.setVgap(0);
		ivjQueryEndToDateFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQueryEndToDateFlowLayout;
}

/**
 * Return the JCheckBox4 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryFailedCheck() {
	if (ivjQueryFailedCheck == null) {
		try {
			ivjQueryFailedCheck = new javax.swing.JCheckBox();
			ivjQueryFailedCheck.setName("QueryFailedCheck");
			ivjQueryFailedCheck.setSelected(true);
			ivjQueryFailedCheck.setText("Failed");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryFailedCheck;
}

/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getQueryGoButton() {
	if (ivjQueryGoButton == null) {
		try {
			ivjQueryGoButton = new javax.swing.JButton();
			ivjQueryGoButton.setName("QueryGoButton");
			ivjQueryGoButton.setText("Go!");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryGoButton;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getQueryHostField() {
	if (ivjQueryHostField == null) {
		try {
			ivjQueryHostField = new javax.swing.JTextField();
			ivjQueryHostField.setName("QueryHostField");
			ivjQueryHostField.setColumns(13);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryHostField;
}

/**
 * Return the QueryPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getQueryPage() {
	if (ivjQueryPage == null) {
		try {
			ivjQueryPage = new javax.swing.JPanel();
			ivjQueryPage.setName("QueryPage");
			ivjQueryPage.setLayout(new java.awt.BorderLayout());
			getQueryPage().add(getJSplitPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryPage;
}


/**
 * Return the JCheckBox2 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryQueuedCheck() {
	if (ivjQueryQueuedCheck == null) {
		try {
			ivjQueryQueuedCheck = new javax.swing.JCheckBox();
			ivjQueryQueuedCheck.setName("QueryQueuedCheck");
			ivjQueryQueuedCheck.setSelected(true);
			ivjQueryQueuedCheck.setText("Queued");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryQueuedCheck;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getQueryResetButton() {
	if (ivjQueryResetButton == null) {
		try {
			ivjQueryResetButton = new javax.swing.JButton();
			ivjQueryResetButton.setName("QueryResetButton");
			ivjQueryResetButton.setText("Reset");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryResetButton;
}

/**
 * Return the QueryResultTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getQueryResultTable() {
	if (ivjQueryResultTable == null) {
		try {
			ivjQueryResultTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjQueryResultTable.setName("QueryResultTable");
			getJScrollPane2().setColumnHeaderView(ivjQueryResultTable.getTableHeader());
			getJScrollPane2().getViewport().setBackingStoreEnabled(true);
			ivjQueryResultTable.setModel(new cbit.vcell.messaging.admin.JobTableModel());
			ivjQueryResultTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryResultTable;
}


/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryRunningCheck() {
	if (ivjQueryRunningCheck == null) {
		try {
			ivjQueryRunningCheck = new javax.swing.JCheckBox();
			ivjQueryRunningCheck.setName("QueryRunningCheck");
			ivjQueryRunningCheck.setSelected(true);
			ivjQueryRunningCheck.setText("Running");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryRunningCheck;
}

/**
 * Return the QueryServerIDField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getQueryServerIDField() {
	if (ivjQueryServerIDField == null) {
		try {
			ivjQueryServerIDField = new javax.swing.JTextField();
			ivjQueryServerIDField.setName("QueryServerIDField");
			ivjQueryServerIDField.setColumns(13);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryServerIDField;
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getQuerySimField() {
	if (ivjQuerySimField == null) {
		try {
			ivjQuerySimField = new javax.swing.JTextField();
			ivjQuerySimField.setName("QuerySimField");
			ivjQuerySimField.setColumns(13);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQuerySimField;
}

/**
 * Return the QueryStartDateCheck property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryStartDateCheck() {
	if (ivjQueryStartDateCheck == null) {
		try {
			ivjQueryStartDateCheck = new javax.swing.JCheckBox();
			ivjQueryStartDateCheck.setName("QueryStartDateCheck");
			ivjQueryStartDateCheck.setText("Start Between");
			ivjQueryStartDateCheck.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryStartDateCheck;
}

/**
 * Return the QueryStartDatePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getQueryStartDatePanel() {
	if (ivjQueryStartDatePanel == null) {
		try {
			ivjQueryStartDatePanel = new javax.swing.JPanel();
			ivjQueryStartDatePanel.setName("QueryStartDatePanel");
			ivjQueryStartDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
			ivjQueryStartDatePanel.setLayout(getQueryStartDatePanelGridLayout());
			getQueryStartDatePanel().add(getQueryStartDateCheck(), getQueryStartDateCheck().getName());
			getQueryStartDatePanel().add(getQueryStartFromDate(), getQueryStartFromDate().getName());
			getQueryStartDatePanel().add(getQueryStartToDate(), getQueryStartToDate().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryStartDatePanel;
}

/**
 * Return the QueryStartDatePanelGridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getQueryStartDatePanelGridLayout() {
	java.awt.GridLayout ivjQueryStartDatePanelGridLayout = null;
	try {
		/* Create part */
		ivjQueryStartDatePanelGridLayout = new java.awt.GridLayout();
		ivjQueryStartDatePanelGridLayout.setRows(3);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQueryStartDatePanelGridLayout;
}


/**
 * Return the DatePanel2 property value.
 * @return cbit.vcell.messaging.admin.DatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DatePanel getQueryStartFromDate() {
	if (ivjQueryStartFromDate == null) {
		try {
			ivjQueryStartFromDate = new cbit.vcell.messaging.admin.DatePanel();
			ivjQueryStartFromDate.setName("QueryStartFromDate");
			ivjQueryStartFromDate.setLayout(getQueryStartFromDateFlowLayout());
			ivjQueryStartFromDate.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryStartFromDate;
}

/**
 * Return the QueryStartFromDateFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getQueryStartFromDateFlowLayout() {
	java.awt.FlowLayout ivjQueryStartFromDateFlowLayout = null;
	try {
		/* Create part */
		ivjQueryStartFromDateFlowLayout = new java.awt.FlowLayout();
		ivjQueryStartFromDateFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
		ivjQueryStartFromDateFlowLayout.setVgap(0);
		ivjQueryStartFromDateFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQueryStartFromDateFlowLayout;
}


/**
 * Return the DatePanel1 property value.
 * @return cbit.vcell.messaging.admin.DatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DatePanel getQueryStartToDate() {
	if (ivjQueryStartToDate == null) {
		try {
			ivjQueryStartToDate = new cbit.vcell.messaging.admin.DatePanel();
			ivjQueryStartToDate.setName("QueryStartToDate");
			ivjQueryStartToDate.setLayout(getQueryStartToDateFlowLayout());
			ivjQueryStartToDate.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryStartToDate;
}

/**
 * Return the QueryStartToDateFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getQueryStartToDateFlowLayout() {
	java.awt.FlowLayout ivjQueryStartToDateFlowLayout = null;
	try {
		/* Create part */
		ivjQueryStartToDateFlowLayout = new java.awt.FlowLayout();
		ivjQueryStartToDateFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
		ivjQueryStartToDateFlowLayout.setVgap(0);
		ivjQueryStartToDateFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQueryStartToDateFlowLayout;
}


/**
 * Return the QueryStatusPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getQueryStatusPanel() {
	if (ivjQueryStatusPanel == null) {
		try {
			ivjQueryStatusPanel = new javax.swing.JPanel();
			ivjQueryStatusPanel.setName("QueryStatusPanel");
			ivjQueryStatusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Status"));
			ivjQueryStatusPanel.setLayout(getQueryStatusPanelGridLayout());
			getQueryStatusPanel().add(getQueryWaitingCheck(), getQueryWaitingCheck().getName());
			getQueryStatusPanel().add(getQueryQueuedCheck(), getQueryQueuedCheck().getName());
			getQueryStatusPanel().add(getQueryDispatchedCheck(), getQueryDispatchedCheck().getName());
			getQueryStatusPanel().add(getQueryRunningCheck(), getQueryRunningCheck().getName());
			getQueryStatusPanel().add(getQueryCompletedCheck(), getQueryCompletedCheck().getName());
			getQueryStatusPanel().add(getQueryFailedCheck(), getQueryFailedCheck().getName());
			getQueryStatusPanel().add(getQueryStoppedCheck(), getQueryStoppedCheck().getName());
			getQueryStatusPanel().add(getQueryAllStatusCheck(), getQueryAllStatusCheck().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryStatusPanel;
}

/**
 * Return the QueryStatusPanelGridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getQueryStatusPanelGridLayout() {
	java.awt.GridLayout ivjQueryStatusPanelGridLayout = null;
	try {
		/* Create part */
		ivjQueryStatusPanelGridLayout = new java.awt.GridLayout(0, 2);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQueryStatusPanelGridLayout;
}

/**
 * Return the JCheckBox6 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryStoppedCheck() {
	if (ivjQueryStoppedCheck == null) {
		try {
			ivjQueryStoppedCheck = new javax.swing.JCheckBox();
			ivjQueryStoppedCheck.setName("QueryStoppedCheck");
			ivjQueryStoppedCheck.setSelected(true);
			ivjQueryStoppedCheck.setText("Stopped");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryStoppedCheck;
}

/**
 * Return the QuerySubmitDateCheck property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQuerySubmitDateCheck() {
	if (ivjQuerySubmitDateCheck == null) {
		try {
			ivjQuerySubmitDateCheck = new javax.swing.JCheckBox();
			ivjQuerySubmitDateCheck.setName("QuerySubmitDateCheck");
			ivjQuerySubmitDateCheck.setSelected(true);
			ivjQuerySubmitDateCheck.setText("Submit Between");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQuerySubmitDateCheck;
}

/**
 * Return the QuerySubmitDatePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getQuerySubmitDatePanel() {
	if (ivjQuerySubmitDatePanel == null) {
		try {
			ivjQuerySubmitDatePanel = new javax.swing.JPanel();
			ivjQuerySubmitDatePanel.setName("QuerySubmitDatePanel");
			ivjQuerySubmitDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
			ivjQuerySubmitDatePanel.setLayout(getQuerySubmitDatePanelGridLayout());
			getQuerySubmitDatePanel().add(getQuerySubmitDateCheck(), getQuerySubmitDateCheck().getName());
			getQuerySubmitDatePanel().add(getQuerySubmitFromDate(), getQuerySubmitFromDate().getName());
			getQuerySubmitDatePanel().add(getQuerySubmitToDate(), getQuerySubmitToDate().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQuerySubmitDatePanel;
}

/**
 * Return the QuerySubmitDatePanelGridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getQuerySubmitDatePanelGridLayout() {
	java.awt.GridLayout ivjQuerySubmitDatePanelGridLayout = null;
	try {
		/* Create part */
		ivjQuerySubmitDatePanelGridLayout = new java.awt.GridLayout();
		ivjQuerySubmitDatePanelGridLayout.setRows(3);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQuerySubmitDatePanelGridLayout;
}


/**
 * Return the DatePanel1 property value.
 * @return cbit.vcell.messaging.admin.DatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DatePanel getQuerySubmitFromDate() {
	if (ivjQuerySubmitFromDate == null) {
		try {
			ivjQuerySubmitFromDate = new cbit.vcell.messaging.admin.DatePanel();
			ivjQuerySubmitFromDate.setName("QuerySubmitFromDate");
			ivjQuerySubmitFromDate.setLayout(getQuerySubmitFromDateFlowLayout());
			ivjQuerySubmitFromDate.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQuerySubmitFromDate;
}

/**
 * Return the QuerySubmitFromDateFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getQuerySubmitFromDateFlowLayout() {
	java.awt.FlowLayout ivjQuerySubmitFromDateFlowLayout = null;
	try {
		/* Create part */
		ivjQuerySubmitFromDateFlowLayout = new java.awt.FlowLayout();
		ivjQuerySubmitFromDateFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
		ivjQuerySubmitFromDateFlowLayout.setVgap(0);
		ivjQuerySubmitFromDateFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQuerySubmitFromDateFlowLayout;
}


/**
 * Return the QuerySubmitToDate property value.
 * @return cbit.vcell.messaging.admin.DatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DatePanel getQuerySubmitToDate() {
	if (ivjQuerySubmitToDate == null) {
		try {
			ivjQuerySubmitToDate = new cbit.vcell.messaging.admin.DatePanel();
			ivjQuerySubmitToDate.setName("QuerySubmitToDate");
			ivjQuerySubmitToDate.setLayout(getQuerySubmitToDateFlowLayout());
			ivjQuerySubmitToDate.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQuerySubmitToDate;
}

/**
 * Return the QuerySubmitToDateFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getQuerySubmitToDateFlowLayout() {
	java.awt.FlowLayout ivjQuerySubmitToDateFlowLayout = null;
	try {
		/* Create part */
		ivjQuerySubmitToDateFlowLayout = new java.awt.FlowLayout();
		ivjQuerySubmitToDateFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
		ivjQuerySubmitToDateFlowLayout.setVgap(0);
		ivjQuerySubmitToDateFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjQuerySubmitToDateFlowLayout;
}


/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getQueryUserField() {
	if (ivjQueryUserField == null) {
		try {
			ivjQueryUserField = new javax.swing.JTextField();
			ivjQueryUserField.setName("QueryUserField");
			ivjQueryUserField.setColumns(13);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryUserField;
}

/**
 * Return the JCheckBox3 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getQueryWaitingCheck() {
	if (ivjQueryWaitingCheck == null) {
		try {
			ivjQueryWaitingCheck = new javax.swing.JCheckBox();
			ivjQueryWaitingCheck.setName("QueryWaitingCheck");
			ivjQueryWaitingCheck.setSelected(true);
			ivjQueryWaitingCheck.setText("Waiting");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueryWaitingCheck;
}

/**
 * Return the RefreshButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRefreshButton() {
	if (ivjRefreshButton == null) {
		try {
			ivjRefreshButton = new javax.swing.JButton();
			ivjRefreshButton.setName("RefreshButton");
			ivjRefreshButton.setText("Refresh");
			ivjRefreshButton.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshButton;
}

/**
 * Return the RemoveFromListButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRemoveFromListButton() {
	if (ivjRemoveFromListButton == null) {
		try {
			ivjRemoveFromListButton = new javax.swing.JButton();
			ivjRemoveFromListButton.setName("RemoveFromListButton");
			ivjRemoveFromListButton.setText("Remove From List");
			ivjRemoveFromListButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveFromListButton;
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 1:20:48 PM)
 * @return cbit.vcell.messaging.admin.SimpleJobStatus
 */
public SimpleJobStatus getReturnedSimulationJobStatus(int selectedRow) {	
	return (SimpleJobStatus)((JobTableModel)getQueryResultTable().getModel()).getValueAt(selectedRow);
}


/**
 * Return the SendMessageButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSendMessageButton() {
	if (ivjSendMessageButton == null) {
		try {
			ivjSendMessageButton = new javax.swing.JButton();
			ivjSendMessageButton.setName("SendMessageButton");
			ivjSendMessageButton.setText("Send");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSendMessageButton;
}


/**
 * Return the ServerStatusOpenButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getServerStatusOpenButton() {
	if (ivjServerStatusOpenButton == null) {
		try {
			ivjServerStatusOpenButton = new javax.swing.JButton();
			ivjServerStatusOpenButton.setName("ServerStatusOpenButton");
			ivjServerStatusOpenButton.setText("Open Details");
			ivjServerStatusOpenButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerStatusOpenButton;
}

/**
 * Return the ServerTab property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getServerStatusPage() {
	if (ivjServerStatusPage == null) {
		try {
			ivjServerStatusPage = new javax.swing.JPanel();
			ivjServerStatusPage.setName("ServerStatusPage");
			ivjServerStatusPage.setLayout(new java.awt.BorderLayout());
			getServerStatusPage().add(getServerStatusPanel(), "North");
			getServerStatusPage().add(getJPanel5(), "South");
			getServerStatusPage().add(getServerStatusTableScrollPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerStatusPage;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getServerStatusPanel() {
	if (ivjServerStatusPanel == null) {
		try {
			ivjServerStatusPanel = new javax.swing.JPanel();
			ivjServerStatusPanel.setName("ServerStatusPanel");
			ivjServerStatusPanel.setLayout(getServerStatusPanelFlowLayout());
			getServerStatusPanel().add(getJLabel1(), getJLabel1().getName());
			getServerStatusPanel().add(getNumServerLabel(), getNumServerLabel().getName());
			getServerStatusPanel().add(getFilterServerShowAllRadioButton(), getFilterServerShowAllRadioButton().getName());
			getServerStatusPanel().add(getFilterServerRadioButton(), getFilterServerRadioButton().getName());
			getServerStatusPanel().add(getFilterServerNameCombo(), getFilterServerNameCombo().getName());
			getServerStatusPanel().add(getFilterServerEqualLabel(), getFilterServerEqualLabel().getName());
			getServerStatusPanel().add(getFilterServerValueCombo(), getFilterServerValueCombo().getName());
			getServerStatusPanel().add(getFilterServerGoButton(), getFilterServerGoButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerStatusPanel;
}

/**
 * Return the ServerStatusPanelFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getServerStatusPanelFlowLayout() {
	java.awt.FlowLayout ivjServerStatusPanelFlowLayout = null;
	try {
		/* Create part */
		ivjServerStatusPanelFlowLayout = new java.awt.FlowLayout();
		ivjServerStatusPanelFlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjServerStatusPanelFlowLayout;
}


/**
 * Return the ServerStatusTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getServerStatusTable() {
	if (ivjServerStatusTable == null) {
		try {
			ivjServerStatusTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjServerStatusTable.setName("ServerStatusTable");
			getServerStatusTableScrollPane().setColumnHeaderView(ivjServerStatusTable.getTableHeader());
			getServerStatusTableScrollPane().getViewport().setBackingStoreEnabled(true);
			ivjServerStatusTable.setModel(new cbit.vcell.messaging.admin.ServerStatusTableModel());
			ivjServerStatusTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerStatusTable;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getServerStatusTableScrollPane() {
	if (ivjServerStatusTableScrollPane == null) {
		try {
			ivjServerStatusTableScrollPane = new javax.swing.JScrollPane();
			ivjServerStatusTableScrollPane.setName("ServerStatusTableScrollPane");
			ivjServerStatusTableScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjServerStatusTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getServerStatusTableScrollPane().setViewportView(getServerStatusTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerStatusTableScrollPane;
}

/**
 * Return the ServicePage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getServiceStatusPage() {
	if (ivjServiceStatusPage == null) {
		try {
			ivjServiceStatusPage = new javax.swing.JPanel();
			ivjServiceStatusPage.setName("ServiceStatusPage");
			ivjServiceStatusPage.setLayout(new java.awt.BorderLayout());
			getServiceStatusPage().add(getJPanel6(), "South");
			getServiceStatusPage().add(getJPanel2(), "North");
			getServiceStatusPage().add(getJScrollPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServiceStatusPage;
}

/**
 * Return the ServiceStatusTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getServiceStatusTable() {
	if (ivjServiceStatusTable == null) {
		try {
			ivjServiceStatusTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjServiceStatusTable.setName("ServiceStatusTable");
			getJScrollPane1().setColumnHeaderView(ivjServiceStatusTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjServiceStatusTable.setModel(new cbit.vcell.messaging.admin.ServiceStatusTableModel());
			ivjServiceStatusTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServiceStatusTable;
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:44:01 PM)
 * @return cbit.vcell.messaging.server.RpcsimServerProxy
 */
private RpcSimServerProxy getSimProxy(User user) throws JMSException, cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	if (simProxyHash == null) {
		simProxyHash = new HashMap();
	}

	RpcSimServerProxy simProxy = (RpcSimServerProxy)simProxyHash.get(user);

	if (simProxy == null) {
		JmsClientMessaging jmsClientMessaging = new JmsClientMessaging(queueConn, log);		
		simProxy = new cbit.vcell.messaging.server.RpcSimServerProxy(user, jmsClientMessaging, log);
		simProxyHash.put(user, simProxy);
	}
	
	return simProxy;
}


/**
 * Return the StartServerButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStartBootstrapButton() {
	if (ivjStartBootstrapButton == null) {
		try {
			ivjStartBootstrapButton = new javax.swing.JButton();
			ivjStartBootstrapButton.setName("StartBootstrapButton");
			ivjStartBootstrapButton.setText("Start Bootstrap");
			ivjStartBootstrapButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartBootstrapButton;
}

/**
 * Return the StartServiceButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStartServiceButton() {
	if (ivjStartServiceButton == null) {
		try {
			ivjStartServiceButton = new javax.swing.JButton();
			ivjStartServiceButton.setName("StartServiceButton");
			ivjStartServiceButton.setText("Start Service");
			ivjStartServiceButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartServiceButton;
}

/**
 * Return the StopServerButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStopBootstrapButton() {
	if (ivjStopBootstrapButton == null) {
		try {
			ivjStopBootstrapButton = new javax.swing.JButton();
			ivjStopBootstrapButton.setName("StopBootstrapButton");
			ivjStopBootstrapButton.setText("Stop Bootstrap");
			ivjStopBootstrapButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopBootstrapButton;
}

/**
 * Return the StopServiceButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStopServiceButton() {
	if (ivjStopServiceButton == null) {
		try {
			ivjStopServiceButton = new javax.swing.JButton();
			ivjStopServiceButton.setName("StopServiceButton");
			ivjStopServiceButton.setText("Stop Service");
			ivjStopServiceButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopServiceButton;
}

/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getTabbedPane() {
	if (ivjTabbedPane == null) {
		try {
			ivjTabbedPane = new javax.swing.JTabbedPane();
			ivjTabbedPane.setName("TabbedPane");
			ivjTabbedPane.insertTab("Servers", null, getServerStatusPage(), null, 0);
			ivjTabbedPane.insertTab("Services", null, getServiceStatusPage(), null, 1);
			ivjTabbedPane.insertTab("Active Users", null, getUserConnectionPage(), null, 2);
			ivjTabbedPane.insertTab("Query", null, getQueryPage(), null, 3);
			ivjTabbedPane.insertTab("Broadcast Message", null, getBroadcastPanel(), null, 4);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTabbedPane;
}

/**
 * Return the UserConnectionPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getUserConnectionPage() {
	if (ivjUserConnectionPage == null) {
		try {
			ivjUserConnectionPage = new javax.swing.JPanel();
			ivjUserConnectionPage.setName("UserConnectionPage");
			ivjUserConnectionPage.setLayout(new java.awt.BorderLayout());
			getUserConnectionPage().add(getJPanel12(), "North");
			getUserConnectionPage().add(getJScrollPane4(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUserConnectionPage;
}

/**
 * Return the UserConnectionTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getUserConnectionTable() {
	if (ivjUserConnectionTable == null) {
		try {
			ivjUserConnectionTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjUserConnectionTable.setName("UserConnectionTable");
			getJScrollPane4().setColumnHeaderView(ivjUserConnectionTable.getTableHeader());
			getJScrollPane4().getViewport().setBackingStoreEnabled(true);
			ivjUserConnectionTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUserConnectionTable;
}


/**
 * Return the UserConnectionTableModel1 property value.
 * @return cbit.vcell.messaging.admin.UserConnectionTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private UserConnectionTableModel getUserConnectionTableModel1() {
	if (ivjUserConnectionTableModel1 == null) {
		try {
			ivjUserConnectionTableModel1 = new cbit.vcell.messaging.admin.UserConnectionTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUserConnectionTableModel1;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getServerStatusOpenButton().addActionListener(ivjEventHandler);
	getStopServiceButton().addActionListener(ivjEventHandler);
	getStartServiceButton().addActionListener(ivjEventHandler);
	getTabbedPane().addChangeListener(ivjEventHandler);
	getStopBootstrapButton().addActionListener(ivjEventHandler);
	getStartBootstrapButton().addActionListener(ivjEventHandler);
	getQueryWaitingCheck().addItemListener(ivjEventHandler);
	getQueryQueuedCheck().addItemListener(ivjEventHandler);
	getQueryFailedCheck().addItemListener(ivjEventHandler);
	getQueryRunningCheck().addItemListener(ivjEventHandler);
	getQueryStoppedCheck().addItemListener(ivjEventHandler);
	getQueryCompletedCheck().addItemListener(ivjEventHandler);
	getQueryAllStatusCheck().addItemListener(ivjEventHandler);
	getQueryGoButton().addActionListener(ivjEventHandler);
	getQueryResetButton().addActionListener(ivjEventHandler);
	getServerStatusTable().addMouseListener(ivjEventHandler);
	getServiceStatusTable().addMouseListener(ivjEventHandler);
	getOpenServiceLogButton().addActionListener(ivjEventHandler);
	getOpenServerLogButton().addActionListener(ivjEventHandler);
	getQueryResultTable().addMouseListener(ivjEventHandler);
	getQuerySubmitDateCheck().addItemListener(ivjEventHandler);
	getQueryStartDateCheck().addItemListener(ivjEventHandler);
	getQueryEndDateCheck().addItemListener(ivjEventHandler);
	getFilterServiceRadioButton().addItemListener(ivjEventHandler);
	getFilterServiceGoButton().addActionListener(ivjEventHandler);
	getFilterServiceShowAllRadioButton().addItemListener(ivjEventHandler);
	getQueryDispatchedCheck().addItemListener(ivjEventHandler);
	getFilterServerShowAllRadioButton().addItemListener(ivjEventHandler);
	getFilterServerRadioButton().addItemListener(ivjEventHandler);
	getFilterServerGoButton().addActionListener(ivjEventHandler);
	getRefreshButton().addActionListener(ivjEventHandler);
	getExitButton().addActionListener(ivjEventHandler);
	getRemoveFromListButton().addActionListener(ivjEventHandler);
	getSendMessageButton().addActionListener(ivjEventHandler);
	getMessageResetButton().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initFilter() {
	ButtonGroup bg = new ButtonGroup();
	bg.add(getFilterServiceShowAllRadioButton());
	bg.add(getFilterServiceRadioButton());
	
	getFilterServiceNameCombo().addItem("Alive");
	getFilterServiceValueCombo().addItem("true");
	getFilterServiceValueCombo().addItem("false");
	
	bg = new ButtonGroup();
	bg.add(getFilterServerShowAllRadioButton());
	bg.add(getFilterServerRadioButton());
	
	getFilterServerNameCombo().addItem("Alive");
	getFilterServerValueCombo().addItem("true");
	getFilterServerValueCombo().addItem("false");
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ServerManageConsole");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Virtual Cell Management Console");
		setSize(1085, 662);
		setContentPane(getJFrameContentPane());
		initConnections();
		connEtoC2();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {		
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		ServerManageConsole aServerManageConsole = new ServerManageConsole();
		aServerManageConsole.prepare();		

		aServerManageConsole.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = aServerManageConsole.getInsets();
		aServerManageConsole.setSize(aServerManageConsole.getWidth() + insets.left + insets.right, aServerManageConsole.getHeight() + insets.top + insets.bottom);
		aServerManageConsole.setLocation(200, 200);		
		aServerManageConsole.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
public void messageResetButton_ActionEvents() {
	getBroadcastMessageTextArea().setText("The Virtual Cell is going to reboot in 5 minutes due to. Please save your work and logout." 
		+ " We are sorry for any inconvenience." 
		+ " If you have any questions, please contact the Virtual Cell at VCell_Support@uchc.edu.");
	getBroadcastMessageToTextField().setText("All");
}


/**
 * Insert the method's description here.
 * Creation date: (9/10/2003 2:27:25 PM)
 * @param service cbit.vcell.messaging.admin.VCService
 */
private synchronized void onArrivingServer(VCServerInfo arrivingServerInfo) {		
	int index = serverList.indexOf(arrivingServerInfo);
	if (index >= 0) {
		arrivingServerInfo.setAlive(true);
		serverList.set(index, arrivingServerInfo);		
	} else {
		serverList.add(arrivingServerInfo);		
	}
	filterServer();

	msgCounter ++;
	if (arrivingServerInfo.isBootstrap()) {
		List tempList = arrivingServerInfo.getServiceList();
		Iterator iter = tempList.iterator();
		while (iter.hasNext()) {
			VCServiceInfo serviceInfoInServerInfo =(VCServiceInfo)iter.next();
			index = serviceList.indexOf(serviceInfoInServerInfo);
			if (index < 0) {
				serviceInfoInServerInfo.setAlive(false);
				serviceList.add(serviceInfoInServerInfo);
			} else {
				// change the configuration in VCellServerInfo if service arrives first
				VCServiceInfo realServiceInfo = (VCServiceInfo)serviceList.get(index);
				serviceInfoInServerInfo.setAlive(true);
				serviceInfoInServerInfo.setBootTime(realServiceInfo.getBootTime());
				serviceInfoInServerInfo.setPerformance(realServiceInfo.getPerformance());				
			}
		}
		filterService();
	} else {
		onArrivingServerManager(arrivingServerInfo);
	}

	if (bootstrapCounter == -1 || bootstrapCounter == msgCounter - 1) {
		notifyAll();					
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (9/10/2003 2:27:25 PM)
 * @param service cbit.vcell.messaging.admin.VCService
 */
private synchronized void onArrivingServerManager(VCServerInfo serverInfo) {
	if (!serverInfo.isServerManager()) {
		return;
	}
	
	if (bootstrapCounter == -1) {
		List tempList = new ArrayList();
		tempList.addAll(serverList);
		serverList.clear();
		serverList.add(serverInfo);
		bootstrapCounter = 0;
		
		Iterator iter = serverInfo.getServiceList().iterator();
		while (iter.hasNext()) {
			VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
			VCServerInfo daemonInfo = null;
			
			if (serviceInfo.getServiceType().equals(ManageConstants.SERVER_TYPE_BOOTSTRAP)) {
				daemonInfo = new VCServerInfo(serviceInfo.getHostName(), ManageConstants.SERVER_TYPE_BOOTSTRAP, serviceInfo.getName());
				if (serviceInfo.isAutoStart()) {
					bootstrapCounter ++;
				}
			} else {
				daemonInfo = new VCServerInfo(serviceInfo.getHostName(), ManageConstants.SERVER_TYPE_RMISERVICE, serviceInfo.getName());
				daemonInfo.setAlive(true);
			}
			int index = tempList.indexOf(daemonInfo);
			if (index >= 0 ) {
				serverList.add(tempList.get(index));
			} else {
				serverList.add(daemonInfo);
			}
		}
		filterServer();
		tempList.clear();
		tempList = null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/10/2003 2:27:25 PM)
 * @param service cbit.vcell.messaging.admin.VCellService
 */
private synchronized void onArrivingService(VCServiceInfo arrivingServiceInfo) {
	int index = serviceList.indexOf(arrivingServiceInfo);
	arrivingServiceInfo.setAlive(true);
	
	if (index < 0) {
		serviceList.add(arrivingServiceInfo);
	} else {
		serviceList.set(index, arrivingServiceInfo);	

		Iterator iter = serverList.iterator();
		while (iter.hasNext()) {
			VCServerInfo serverInfo = (VCServerInfo)iter.next();
			if (serverInfo.isAlive()) {
				int anotherindex = serverInfo.getServiceList().indexOf(arrivingServiceInfo);				
				if (anotherindex >= 0) {
					// change the configuration in VCellServerInfo if server arrives first
					VCServiceInfo serviceInfoInServerInfo = serverInfo.getServiceInfoAt(anotherindex);
					serviceInfoInServerInfo.setAlive(true);
					serviceInfoInServerInfo.setPerformance(arrivingServiceInfo.getPerformance());
					serviceInfoInServerInfo.setBootTime(arrivingServiceInfo.getBootTime());
					break;
				}
			}
		}				
	}

	filterService();
}


/**
 * onMessage method comment.
 */
public synchronized void onControlTopicMessage(Message message) {
	class TempThread1 extends Thread {
		VCServerInfo serverInfo = null;
	
		TempThread1(VCServerInfo serverInfo0) {
			serverInfo = serverInfo0;
		}

		public void run() {
			onArrivingServer(serverInfo);
		}
	}

	class TempThread2 extends Thread {
		VCServiceInfo serviceInfo = null;		
		
		TempThread2(VCServiceInfo serviceInfo0) {
			serviceInfo = serviceInfo0;
		}

		public void run() {
			onArrivingService(serviceInfo);
		}
	}
		
	try {
		log.print("onMessage [" + JmsUtils.toString(message) + "]");	
		String msgType = (String)JmsUtils.parseProperty(message, ManageConstants.MESSAGE_TYPE_PROPERTY, String.class);
		
		if (msgType == null) {
			return;
		}
		
		if (msgType.equals(ManageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE) && message instanceof ObjectMessage) {			
			Object obj = ((ObjectMessage)message).getObject();			
			if (obj instanceof VCServerInfo) {
				SwingUtilities.invokeLater(new TempThread1((VCServerInfo)obj));
			} else if (obj instanceof VCServiceInfo) {
				VCServiceInfo serviceInfo = (VCServiceInfo)obj;
				SwingUtilities.invokeLater(new TempThread2(serviceInfo));
			}			
		}		
	} catch (Exception ex) {
		log.exception(ex);
	}	
}


/**
 * Comment
 */
public void openLogButton_ActionPerformed(ActionEvent actionEvent) {
	openServiceLog();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 8:47:34 AM)
 */
private void openLogDialog(Message request, String title) {
	try {
		class OpenLogWorker extends swingthreads.SwingWorker {
			Message reqMsg = null; 
			String thisTitle = null;
			
			public OpenLogWorker(Message request, String title0) {
				reqMsg = request;
				thisTitle = title0;
			}

			public void finished() {
				try {
					Message reply = (Message)get();
					if (reply == null) {
						javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Opening log failed!", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
					} else {
						log.print("open log: got reply " + JmsUtils.toString(reply));
						String status = (String)JmsUtils.parseProperty(reply, ManageConstants.STATUS_OPENLOG_PROPERTY, String.class);
						String filename = (String)JmsUtils.parseProperty(reply, ManageConstants.FILE_NAME_PROPERTY, String.class);
						
						if (status.equals(ManageConstants.STATUS_OPENLOG_NOTEXIST)) {
							javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Opening log failed: the system can't find the file -" + filename, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
						} else if (status.equals(ManageConstants.STATUS_OPENLOG_EXIST)) {
							long length = ((Long)JmsUtils.parseProperty(reply, ManageConstants.FILE_LENGTH_PROPERTY, long.class)).longValue();
							
							Thread aThread = new Thread(new Runnable() {
								public void run() {
									openLogFile_waitForComplete();
									javax.swing.SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											disposeGatherInfoDialog();
										}
									});
								}
							});
							aThread.setName("WaitLogThread");
							aThread.start();
							
							displayOpenLogProgressDialog(length);

							java.io.File file = fileChannelReceiver.getCurrentFile();				
							if (file == null || !file.getName().equalsIgnoreCase(filename) || !fileChannelReceiver.isComplete()) {
								javax.swing.JOptionPane.showMessageDialog(getJFrameContentPane(), "Opening log failed - " + fileChannelReceiver.getChannelMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
								return;
							} else {				
								LogDialog dialog = new LogDialog(getJFrame());
								dialog.setTitle("Log for " + thisTitle);
								dialog.setLocationRelativeTo(getJFrame());
								dialog.setFile(file);
								dialog.show();

								file.delete();
							}
						}
					}
				} catch (Exception ex) {
				}
			}
			
			public Object construct() {
				try {
					return topicSession.request(ServerManageConsole.this, JmsUtils.getTopicDaemonControl(), reqMsg, 30 * MessageConstants.SECOND);					
				} catch (JMSException ex) {
				}

				return null;
			}
		}
		
		OpenLogWorker worker = new OpenLogWorker(request, title);
		worker.start();				
		
	} catch (Exception ex) {
		log.exception(ex);
		javax.swing.JOptionPane.showMessageDialog(this, "Opening log failed: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/23/2003 10:19:32 AM)
 */
private void openLogFile_waitForComplete() {
	long currentTime = System.currentTimeMillis();
	log.print("Receiving....");
	while (!fileChannelReceiver.isDone() && System.currentTimeMillis() - currentTime < 5 * ManageConstants.MINUTE) {
		try {
			Thread.sleep(1000); // sleep for some time to wait for the logfile
		} catch (InterruptedException ex) {
		}		
	}
	log.print("Receiving....Done!");
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 8:47:34 AM)
 */
private void openServer() {	
	int selectedRow = getServerStatusTable().getSelectedRow();
	VCServerInfo vcServerInfo = (VCServerInfo)((ManageTableModel)getServerStatusTable().getModel()).getValueAt(selectedRow);

	ServerDetailDialog dialog  = new ServerDetailDialog(this);
	dialog.setServer(vcServerInfo);
	dialog.setLocationRelativeTo(this);
	dialog.setVisible(true);
	//dialog = null;
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 8:47:34 AM)
 */
private void openServerLog() {	
	try {
		fileChannelReceiver.startOver();

		int srow = getServerStatusTable().getSelectedRow();
		VCServerInfo serverInfo = (VCServerInfo)((ServerStatusTableModel)getServerStatusTable().getModel()).getValueAt(srow);
		
		Message msg = topicSession.createMessage();
		msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_OPENSERVERLOG_VALUE);
		msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, serverInfo.getHostName());
		msg.setStringProperty(ManageConstants.SERVER_TYPE_PROPERTY, serverInfo.getServerType());		
		msg.setStringProperty(ManageConstants.SERVER_NAME_PROPERTY, serverInfo.getServerName());
		
		log.print("sending open server log message [" + JmsUtils.toString(msg) + "]");		
		openLogDialog(msg, serverInfo.getServerName());
		
	} catch (Exception ex) {
		log.exception(ex);
	}
}


/**
 * Comment
 */
public void openServerLogButton_ActionPerformed(ActionEvent actionEvent) {
	openServerLog();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 8:47:34 AM)
 */
private void openServiceLog() {
	int srow = getServiceStatusTable().getSelectedRow();
	VCServiceInfo serviceInfo = (VCServiceInfo)((ServiceStatusTableModel)getServiceStatusTable().getModel()).getValueAt(srow);
	openServiceLog(serviceInfo);
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 8:47:34 AM)
 */
private void openServiceLog(VCServiceInfo serviceInfo) {
	try {
		fileChannelReceiver.startOver();
		
		Message msg = topicSession.createMessage();
		msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_OPENSERVICELOG_VALUE);
		msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, serviceInfo.getHostName());
		msg.setStringProperty(ManageConstants.SERVICETYPE_PROPERTY, serviceInfo.getServiceType());
		msg.setStringProperty(ManageConstants.SERVICENAME_PROPERTY, serviceInfo.getServiceName());
		
		log.print("sending open service log message [" + JmsUtils.toString(msg) + "]");	
		openLogDialog(msg, serviceInfo.getServiceName());		
	} catch (Exception ex) {
		log.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 11:08:09 PM)
 * @param simulation cbit.vcell.solver.Simulation
 */
private void pingAll() {
	try {
		Message msg = topicSession.createMessage();
			
		msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE);
		//msg.setStringProperty(ManageConstants.SERVER_NAME_PROPERTY, ManageConstants.ANYSERVER);
		
		log.print("sending ping message [" + JmsUtils.toString(msg) + "]");
		
		topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);

		synchronized (this) {
			try {
				wait(15 * ManageConstants.SECOND);
			} catch (InterruptedException ex) {
				log.exception(ex);
			}
		}
	} catch (Exception ex) {
		log.exception(ex);
	}
}


///**
 //* Comment
 //*/
public void prepare() {	
	try {
		log = new cbit.vcell.server.StdoutSessionLog("Console");
		cbit.vcell.server.PropertyLoader.loadProperties();
		setTitle("Virtual Cell Management Console -- " + VCellServerID.getSystemServerID());
		reconnect();
	} catch (JMSException ex) {
	} catch (IOException ex) {
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:00:07 AM)
 */
private void query() {
	getRemoveFromListButton().setEnabled(false);
	if (adminDbServer == null) {
		try {
			cbit.sql.ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
			cbit.sql.KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
			adminDbServer = new cbit.vcell.modeldb.LocalAdminDbServer(conFactory, keyFactory, log);
		} catch (Exception ex) {
			log.exception(ex);
		}
	}
	StringBuffer conditions = new StringBuffer();
	String text = getQuerySimField().getText();
	if (text != null && text.trim().length() > 0) {
		try {
			int simID = Integer.parseInt(text);
			conditions.append(SimulationJobTable.table.simRef.getQualifiedColName() + "=" + simID);
		} catch (NumberFormatException ex) {
		}
	}

	text = getQueryHostField().getText();
	if (text != null && text.trim().length() > 0) {
		if (conditions.length() > 0) {
			conditions.append(" AND ");
		}
		conditions.append("lower(" + SimulationJobTable.table.computeHost.getQualifiedColName() + ")='" + text.toLowerCase() + "'");
	}

	text = getQueryServerIDField().getText();
	if (text != null && text.trim().length() > 0) {
		if (conditions.length() > 0) {
			conditions.append(" AND ");
		}
		conditions.append("lower(" + SimulationJobTable.table.serverID.getQualifiedColName() + ")='" + text.toLowerCase() + "'");
	}
		
	text = getQueryUserField().getText();
	if (text != null && text.trim().length() > 0) {
		if (conditions.length() > 0) {
			conditions.append(" AND ");
		}
		conditions.append(cbit.vcell.modeldb.UserTable.table.userid.getQualifiedColName() + "='" + text + "'");
	}

	StringBuffer status = new StringBuffer();
	int index = 0;	
	if (!getQueryAllStatusCheck().isSelected()) {
		Iterator iter = statusChecks.iterator();
		for (; iter.hasNext() ; index ++) {
			JCheckBox box = (JCheckBox)iter.next();	
			if (box.isSelected()) {
				if (status.length() > 0) {
					status.append(" OR ");
				}
					
				status.append(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + index);		
			}
		}			
	}

	if (status.length() > 0) {
		if (conditions.length() > 0) {
			conditions.append(" AND ");
		}
		conditions.append("(" + status + ")");
	}

	if (getQuerySubmitDateCheck().isSelected()) {
		String d1 = getQuerySubmitFromDate().getDate();
		String d2 = getQuerySubmitToDate().getDate();
		if (conditions.length() > 0) {
			conditions.append(" AND ");
		}
		conditions.append("(" + SimulationJobTable.table.submitDate.getQualifiedColName() 
			+ " BETWEEN to_date('" + d1 + " 00:00:00', 'mm/dd/yyyy HH24:MI:SS') AND to_date('" + d2 + " 23:59:59', 'mm/dd/yyyy HH24:MI:SS'))");		
	}
	
	if (getQueryStartDateCheck().isSelected()) {
		String d1 = getQueryStartFromDate().getDate();
		String d2 = getQueryStartToDate().getDate();
		if (conditions.length() > 0) {
			conditions.append(" AND ");
		}
		conditions.append("(" + SimulationJobTable.table.startDate.getQualifiedColName() 
			+ " BETWEEN to_date('" + d1 + "00:00:00', 'mm/dd/yyyy HH24:MI:SS') AND to_date('" + d2 + " 23:59:59', 'mm/dd/yyyy HH24:MI:SS'))");		
	}
		
	if (getQueryEndDateCheck().isSelected()) {
		String d1 = getQueryEndFromDate().getDate();
		String d2 = getQueryEndToDate().getDate();
		if (conditions.length() > 0) {
			conditions.append(" AND ");
		}
		conditions.append("(" + SimulationJobTable.table.endDate.getQualifiedColName() 
			+ " BETWEEN to_date('" + d1 + "00:00:00', 'mm/dd/yyyy HH24:MI:SS') AND to_date('" + d2 + " 23:59:59', 'mm/dd/yyyy HH24:MI:SS'))");		
	}
	
	try {
		List resultList = adminDbServer.getSimulationJobStatus(conditions.toString());
		getNumResultsLabel().setText("  " + resultList.size() + " result(s) returned");
		((JobTableModel)getQueryResultTable().getModel()).setData(resultList);
	} catch (Exception ex) {
		getNumResultsLabel().setText("Query failed, please try again!");
		((JobTableModel)getQueryResultTable().getModel()).setData(null);
	}
}


/**
 * Comment
 */
public void queryAllStatusCheck_ItemStateChanged(ItemEvent itemEvent) {
	updateChecks(itemEvent);
	return;
}


/**
 * Comment
 */
public void queryCompletedCheck_ItemStateChanged(ItemEvent itemEvent) {
	updateChecks(itemEvent);
	return;
}


/**
 * Comment
 */
public void queryDispatchedCheck_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	updateChecks(itemEvent);
	return;
}


/**
 * Comment
 */
public void queryEndDateSubmit_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
		getQueryEndFromDate().setEnabled(true);
		getQueryEndToDate().setEnabled(true);
	} else {
		getQueryEndFromDate().setEnabled(false);
		getQueryEndToDate().setEnabled(false);
	}
	return;
}


/**
 * Comment
 */
public void queryFailedCheck_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	updateChecks(itemEvent);
	return;
}


/**
 * Comment
 */
public void queryGoButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	query();
	return;
}


/**
 * Comment
 */
public void queryQueuedCheck_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	updateChecks(itemEvent);
	return;
}


/**
 * Comment
 */
public void queryResetButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getQuerySimField().setText(null);
	getQueryHostField().setText(null);
	getQueryUserField().setText(null);
	getQueryAllStatusCheck().setSelected(true);
	getQueryStartFromDate().reset();
	getQueryStartToDate().reset();
	getQueryEndFromDate().reset();	
	getQueryEndToDate().reset();
	getQuerySubmitFromDate().reset();
	getQuerySubmitToDate().reset();
	getQuerySubmitDateCheck().setSelected(true);
	getQueryStartDateCheck().setSelected(false);
	getQueryEndDateCheck().setSelected(false);
	return;
}


/**
 * Comment
 */
public void queryResultTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	int srow = getQueryResultTable().getSelectedRow();
	if (srow < 0) {
		return;
	}
	if (mouseEvent.getClickCount() == 1) {
		getRemoveFromListButton().setEnabled(true);
	} else if (mouseEvent.getClickCount() == 2) {		
		SimpleJobStatus jobStatus = (SimpleJobStatus)((JobTableModel)getQueryResultTable().getModel()).getValueAt(srow);
		SimulationJobStatusDetailDialog dialog = new SimulationJobStatusDetailDialog(this, getQueryResultTable().getRowCount(), srow);
		dialog.setLocationRelativeTo(this);
		dialog.show();		
	}
}


/**
 * Comment
 */
public void queryRunningCheck_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	updateChecks(itemEvent);
	return;
}


/**
 * Comment
 */
public void queryStartDateCheck_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
		getQueryStartFromDate().setEnabled(true);
		getQueryStartToDate().setEnabled(true);
	} else {
		getQueryStartFromDate().setEnabled(false);
		getQueryStartToDate().setEnabled(false);
	}	
	return;
}

/**
 * Comment
 */
public void queryStoppedCheck_ItemStateChanged(ItemEvent itemEvent) {
	updateChecks(itemEvent);
	return;
}


/**
 * Comment
 */
public void querySubmitDateCheck_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
		getQuerySubmitFromDate().setEnabled(true);
		getQuerySubmitToDate().setEnabled(true);
	} else {
		getQuerySubmitFromDate().setEnabled(false);
		getQuerySubmitToDate().setEnabled(false);
	}
	return;
}


/**
 * Comment
 */
public void queryWaitingCheck_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	updateChecks(itemEvent);
	return;
}


/**
 * Comment
 */
private void reconnect() throws JMSException {
	jmsConnFactory = new JmsConnectionFactoryImpl();
	
	queueConn = jmsConnFactory.createQueueConnection();
	fileChannelReceiver = new JmsFileReceiver(queueConn, log);		
	fileChannelReceiver.toReceive();
	queueConn.startConnection();
	
	topicConn = jmsConnFactory.createTopicConnection();
	topicSession = topicConn.getAutoSession();
	VCellTopicSession listenSession = topicConn.getAutoSession();
	String filter = ManageConstants.MESSAGE_TYPE_PROPERTY + " NOT IN " 
		+ "('" + ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE + "'" 
		+ ",'" + ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE + "'" 
		+ ",'" + ManageConstants.MESSAGE_TYPE_ISSERVERALIVE_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_STOPBOOTSTRAP_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_STOPSERVICE_VALUE + "'"
		+ ")";
	listenSession.setupListener(JmsUtils.getTopicDaemonControl(), filter, new ControlMessageCollector(this));
	topicConn.startConnection();
}


/**
 * Comment
 */
private void refresh () {
	refresh(true);
}


/**
 * Comment
 */
private void refresh (boolean progress) {
	int tabIndex = getTabbedPane().getSelectedIndex();
	if (tabIndex == 0 || tabIndex == 1) {
		msgCounter = 0;
		
		((ServiceStatusTableModel)getServiceStatusTable().getModel()).clear();
		serviceList.clear();
		
		Iterator iter = serverList.iterator();
		while (iter.hasNext()) {
			VCServerInfo serverInfo = (VCServerInfo)iter.next();
			if (serverInfo.isServerManager() || serverInfo.isBootstrap()) {
				serverInfo.setAlive(false);
			}
		}

		filterServer();
		showService(serviceList);
		Thread pingThread = new Thread(new Runnable() {
			public void run() {
				pingAll();
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						disposeGatherInfoDialog();
					}
				});
			}
		});
		pingThread.setName("Refresh Thread");
		pingThread.start();	
			
		if (progress) {
			displayGatherInfoDialog();
		}
	} else if (tabIndex == 2) {
		userList.clear();
		try {
			if (vcellBootstrap == null) {
				vcellBootstrap = (cbit.vcell.server.VCellBootstrap) java.rmi.Naming.lookup(getLocalVCellBootstrapUrl());
				vcellServer = vcellBootstrap.getVCellServer(new User("Administrator",new KeyValue("2")), "icnia66");
			}		
			
			ServerInfo serverInfo = vcellServer.getServerInfo();
			User[] users = serverInfo.getConnectedUsers();
			for (int i = 0; i < users.length; i ++) {
				userList.add(new SimpleUserConnection(users[i], getConnectionTimeFromRMIbootstrapLogfile(users[i])));
			}
		} catch (Exception ex) {
			javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Exception:" + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		}
		showUsers(userList);
	}
		
	return;
}


/**
 * Comment
 */
public void refreshButton_ActionPerformed(ActionEvent actionEvent) {
	refresh();
	return;
}


/**
 * Comment
 */
public void removeFromListButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	int[] indexes = getQueryResultTable().getSelectedRows();
	for (int i = 0; i < indexes.length; i ++) {
		((JobTableModel)getQueryResultTable().getModel()).remove(indexes[i] - i);
	}
	getNumResultsLabel().setText("  " + getQueryResultTable().getRowCount() + " result(s) returned");
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:32:52 PM)
 * @param simKey cbit.sql.KeyValue
 */
public void resubmitSimulation(String userid, cbit.sql.KeyValue simKey) {
	try {
		User user = adminDbServer.getUser(userid);
		RpcDbServerProxy dbProxy = getDbProxy(user);
		BigString simxml = dbProxy.getSimulationXML(simKey);
		if (simxml == null) {
			javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Simulation [" + simKey + "] doesn't exit, might have been deleted.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		cbit.vcell.solver.Simulation sim = cbit.vcell.xml.XmlHelper.XMLToSim(simxml.toString());
		if (sim == null) {
			javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Simulation [" + simKey + "] doesn't exit, might have been deleted.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		cbit.vcell.messaging.server.RpcSimServerProxy simProxy = getSimProxy(user);
		simProxy.startSimulation(sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier());		
	} catch (Exception ex) {
		javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Resubmitting simulation failed:" + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
}


/**
 * Comment
 */
public void sendMessageButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	try {
		int n = javax.swing.JOptionPane.showConfirmDialog(this, "You are going to send message to " + getBroadcastMessageToTextField().getText() + ". Continue?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
		if (n == javax.swing.JOptionPane.NO_OPTION) {
			return;
		}	
		
		Message msg = topicSession.createObjectMessage(new BigString(getBroadcastMessageTextArea().getText()));
		String username = getBroadcastMessageToTextField().getText();

		if (username.equalsIgnoreCase("All")) {
			username = "All";
		}
			
		msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_BROADCASTMESSAGE_VALUE);
		msg.setStringProperty(MessageConstants.USERNAME_PROPERTY, username);
		
		log.print("sending broadcast message [" + JmsUtils.toString(msg) + "]");		
		topicSession.publishMessage(JmsUtils.getTopicClientStatus(), msg);		

	} catch (Exception ex) {
		log.exception(ex);
	}
}


/**
 * Comment
 */
public void serverManageConsole_Initialize() {
	initFilter();
	
	statusChecks.add(getQueryWaitingCheck());
	statusChecks.add(getQueryQueuedCheck());
	statusChecks.add(getQueryDispatchedCheck());
	statusChecks.add(getQueryRunningCheck());
	statusChecks.add(getQueryCompletedCheck());
	statusChecks.add(getQueryStoppedCheck());
	statusChecks.add(getQueryFailedCheck());

	getQueryResultTable().setDefaultRenderer(Date.class, new DateRenderer());
	return;
}


/**
 * Comment
 */
public void serverManageConsole_WindowClosed(java.awt.event.WindowEvent windowEvent) {
	try {
		dispose();
		if (topicConn != null) {
			topicConn.close();
		}
		if (queueConn != null) {
			queueConn.close();
		}
	} catch (JMSException ex) {
		log.exception(ex);
	} finally {
 		System.exit(0);	
 	}	
}


/**
 * Comment
 */
public void serverStatusOpenButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	openServer();
	return;
}


/**
 * Comment
 */
public void serverStatusTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	int selectedCount = getServerStatusTable().getSelectedRowCount();
	int[] selectedRows = getServerStatusTable().getSelectedRows();
	if (selectedRows == null || selectedCount < 1) {
		return;
	}

	if (selectedCount == 1) {		
		int srow = selectedRows[0];
		VCServerInfo serverInfo = (VCServerInfo)((ServerStatusTableModel)getServerStatusTable().getModel()).getValueAt(srow);		

		if (serverInfo.isRMIService() || !serverInfo.isAlive()) {
			getOpenServerLogButton().setEnabled(false);
			getServerStatusOpenButton().setEnabled(false);
		} else {
			getOpenServerLogButton().setEnabled(true);
			getServerStatusOpenButton().setEnabled(true);
		}
		
		if (mouseEvent.getClickCount() == 2 && (serverInfo.isServerManager() || serverInfo.isBootstrap())) {
			openServer();
			return;
		}
	} else {
		getOpenServerLogButton().setEnabled(false);
		getServerStatusOpenButton().setEnabled(false);
	}
	
	getStartBootstrapButton().setEnabled(false);
	getStopBootstrapButton().setEnabled(false);
	
	for (int i = 0; i < selectedCount; i ++){		
		int srow = selectedRows[i];
		VCServerInfo serverInfo = (VCServerInfo)((ServerStatusTableModel)getServerStatusTable().getModel()).getValueAt(srow);
		if (serverInfo.isBootstrap()) {
			if (serverInfo.isAlive()) {
				getStopBootstrapButton().setEnabled(true);
			} else {
				getStartBootstrapButton().setEnabled(true);
			}
		} else if (serverInfo.isRMIService()) { // RMI Service			
			getStartBootstrapButton().setEnabled(true);
			getStopBootstrapButton().setEnabled(true);
		}
	}
}


/**
 * Comment
 */
public void serviceStatusTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	int selectedCount = getServiceStatusTable().getSelectedRowCount();
	int[] selectedRows = getServiceStatusTable().getSelectedRows();
	if (selectedRows == null || selectedCount < 1) {
		return;
	}

	if (selectedCount == 1) {
		getOpenServiceLogButton().setEnabled(true);
	} else {
		getOpenServiceLogButton().setEnabled(false);
	}

	getStopServiceButton().setEnabled(false);
	getStartServiceButton().setEnabled(false);

	for (int i = 0; i < selectedCount; i ++){	
		int row = selectedRows[i];
		VCServiceInfo serviceInfo0 = (VCServiceInfo)((ServiceStatusTableModel)getServiceStatusTable().getModel()).getValueAt(row);		
		int index = serviceList.indexOf(serviceInfo0);	
		VCServiceInfo serviceInfo = (VCServiceInfo)serviceList.get(index);
		if (serviceInfo.isAlive()) {
			getStopServiceButton().setEnabled(true);
		} else {
			getStartServiceButton().setEnabled(true);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 1:20:48 PM)
 * @return cbit.vcell.messaging.admin.SimpleJobStatus
 */
public void setSelectedReturnedSimulationJobStatus(int selectedRow) {	
	getQueryResultTable().setRowSelectionInterval(selectedRow, selectedRow);
}


/**
 * Method generated to support the promotion of the userConnectionTableModel attribute.
 * @param arg1 cbit.vcell.messaging.admin.sorttable.SortTableModel
 */
public void setUserConnectionTableModel(cbit.vcell.messaging.admin.sorttable.SortTableModel arg1) {
	getUserConnectionTable().setModel(arg1);
}


/**
 * Insert the method's description here.
 * Creation date: (2/17/2004 1:21:46 PM)
 * @param serviceList0 java.util.List
 */
private void showServer(List serviceList0) {
	((ServerStatusTableModel)(getServerStatusTable().getModel())).setData(serviceList0);
	getNumServerLabel().setText(serviceList0.size() + "");
}


/**
 * Insert the method's description here.
 * Creation date: (2/17/2004 1:21:46 PM)
 * @param serviceList0 java.util.List
 */
private void showService(List serviceList0) {
	((ServiceStatusTableModel)(getServiceStatusTable().getModel())).setData(serviceList0);
	getNumServiceLabel().setText(serviceList0.size() + "");
}


/**
 * Insert the method's description here.
 * Creation date: (2/17/2004 1:21:46 PM)
 * @param serviceList0 java.util.List
 */
private void showUsers(List userList0) {
	((UserConnectionTableModel)(getUserConnectionTable().getModel())).setData(userList0);
	getNumUserConnectionLabel().setText(userList0.size() + "");
}


/**
 * Comment
 */
public void startBootstrapButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	startBootstraps();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 1:15:33 PM)
 */
private void startBootstraps() {
	try {
		int selectedCount = getServerStatusTable().getSelectedRowCount();
		int[] selectedRows = getServerStatusTable().getSelectedRows();
		if (selectedRows == null || selectedCount < 1) {
			return;
		}

		for (int i = 0; i < selectedCount; i++){					
			int row = selectedRows[i];
			VCServerInfo serverInfo = (VCServerInfo)((ServerStatusTableModel)getServerStatusTable().getModel()).getValueAt(row);
			int index = serverList.indexOf(serverInfo);		
			VCServerInfo vcServerInfo = (VCServerInfo)serverList.get(index);
			if (vcServerInfo.isServerManager() || vcServerInfo.isAlive() && vcServerInfo.isBootstrap()) {
				continue;
			}
			
			vcServerInfo.setAlive(true);
			
			Message msg = topicSession.createMessage();
				
			msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_STARTBOOTSTRAP_VALUE);
			msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, vcServerInfo.getHostName());
			msg.setStringProperty(ManageConstants.SERVER_TYPE_PROPERTY, vcServerInfo.getServerType());
			msg.setStringProperty(ManageConstants.SERVER_NAME_PROPERTY, vcServerInfo.getServerName());
			
			log.print("sending start bootstrap message [" + JmsUtils.toString(msg) + "]");		
			topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);
			
			((ServerStatusTableModel)getServerStatusTable().getModel()).setValueAt(row, vcServerInfo);
				bootstrapCounter ++;
		}

		clearServerStatusTab();
	} catch (Exception ex) {
		log.exception(ex);
	}		
}


/**
 * Comment
 */
public void startServiceButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	startServices();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 1:15:33 PM)
 */
private void startServices() {
	try {
		int selectedCount = getServiceStatusTable().getSelectedRowCount();
		int[] selectedRows = getServiceStatusTable().getSelectedRows();
		if (selectedRows == null || selectedCount < 1) {
			return;
		}

		for (int i = 0; i < selectedCount; i ++){					
			int row = selectedRows[i];
			VCServiceInfo serviceInfo0 = (VCServiceInfo)((ServiceStatusTableModel)getServiceStatusTable().getModel()).getValueAt(row);		
			int index = serviceList.indexOf(serviceInfo0);	
			VCServiceInfo serviceInfo = (VCServiceInfo)serviceList.get(index);
			if (serviceInfo.isAlive()) {
				continue;
			}
			serviceInfo.setAlive(true);
			
			Message msg = topicSession.createMessage();
				
			msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_STARTSERVICE_VALUE);
			msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, serviceInfo.getHostName());
			msg.setStringProperty(ManageConstants.SERVICETYPE_PROPERTY, serviceInfo.getServiceType());
			msg.setStringProperty(ManageConstants.SERVICENAME_PROPERTY, serviceInfo.getServiceName());
			
			log.print("sending start service message [" + JmsUtils.toString(msg) + "]");		
			topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);
			
			((ServiceStatusTableModel)getServiceStatusTable().getModel()).setValueAt(row, serviceInfo);
		}

		clearServiceStatusTab();
	} catch (Exception ex) {
		javax.swing.JOptionPane.showMessageDialog(this, "Failed!!: " + ex.getMessage(), "Bad News", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
}


/**
 * Comment
 */
public void stopBootstrapButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	stopBootstraps();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 1:15:33 PM)
 */
private void stopBootstraps() {	
	try {
		int selectedCount = getServerStatusTable().getSelectedRowCount();
		int[] selectedRows = getServerStatusTable().getSelectedRows();
		if (selectedRows == null || selectedCount < 1) {
			return;
		}

		for (int i = 0; i < selectedCount; i ++) {					
			int row = selectedRows[i];

			VCServerInfo serverInfo = (VCServerInfo)((ServerStatusTableModel)getServerStatusTable().getModel()).getValueAt(row);
			int index = serverList.indexOf(serverInfo);		
			VCServerInfo vcServerInfo = (VCServerInfo)serverList.get(index);
			if (vcServerInfo.isServerManager() || !vcServerInfo.isAlive() && vcServerInfo.isBootstrap()) {
				continue;
			}
			
			vcServerInfo.setAlive(false);
			
			Message msg = topicSession.createMessage();
				
			msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_STOPBOOTSTRAP_VALUE);
			msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, vcServerInfo.getHostName());
			msg.setStringProperty(ManageConstants.SERVER_TYPE_PROPERTY, vcServerInfo.getServerType());
			msg.setStringProperty(ManageConstants.SERVER_NAME_PROPERTY, vcServerInfo.getServerName());
			
			log.print("sending stop bootstrap message [" + JmsUtils.toString(msg) + "]");		
			topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);
			
			((ServerStatusTableModel)getServerStatusTable().getModel()).setValueAt(row, vcServerInfo);
			getStartBootstrapButton().setEnabled(true);
			getStopBootstrapButton().setEnabled(false);
			bootstrapCounter --;
		}

		clearServerStatusTab();
	} catch (Exception ex) {
		log.exception(ex);
	}	
}


/**
 * Comment
 */
public void stopServiceButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	stopServices();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 1:15:33 PM)
 */
private void stopServices() {
	try {
		int selectedCount = getServiceStatusTable().getSelectedRowCount();
		int[] selectedRows = getServiceStatusTable().getSelectedRows();
		if (selectedRows == null || selectedCount < 1) {
			return;
		}

		for (int i = 0; i < selectedCount; i ++){					
			int row = selectedRows[i];
			VCServiceInfo serviceInfo0 = (VCServiceInfo)((ServiceStatusTableModel)getServiceStatusTable().getModel()).getValueAt(row);		
			int index = serviceList.indexOf(serviceInfo0);	
			VCServiceInfo serviceInfo = (VCServiceInfo)serviceList.get(index);
			if (!serviceInfo.isAlive()) {
				continue;
			}
			
			serviceInfo.setAlive(false);
			
			Message msg = topicSession.createMessage();
				
			msg.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_STOPSERVICE_VALUE);
			msg.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, serviceInfo.getHostName());
			msg.setStringProperty(ManageConstants.SERVICETYPE_PROPERTY, serviceInfo.getServiceType());
			msg.setStringProperty(ManageConstants.SERVICENAME_PROPERTY, serviceInfo.getServiceName());
			
			log.print("sending stop service message [" + JmsUtils.toString(msg) + "]");		
			topicSession.publishMessage(JmsUtils.getTopicDaemonControl(), msg);
			
			((ServiceStatusTableModel)getServiceStatusTable().getModel()).setValueAt(row, serviceInfo);
		}

		clearServiceStatusTab();
	} catch (Exception ex) {
		javax.swing.JOptionPane.showMessageDialog(this, "Failed!!: " + ex.getMessage(), "Bad News", javax.swing.JOptionPane.ERROR_MESSAGE);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:32:52 PM)
 * @param simKey cbit.sql.KeyValue
 */
public void stopSimulation(String userid, cbit.sql.KeyValue simKey) {
	try {
		User user = adminDbServer.getUser(userid);
		RpcDbServerProxy dbProxy = getDbProxy(user);
		BigString simxml = dbProxy.getSimulationXML(simKey);
		if (simxml == null) {
			javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Simulation [" + simKey + "] doesn't exit, might have been deleted.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		cbit.vcell.solver.Simulation sim = cbit.vcell.xml.XmlHelper.XMLToSim(simxml.toString());
		if (sim == null) {
			javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Simulation [" + simKey + "] doesn't exit, might have been deleted.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		cbit.vcell.messaging.server.RpcSimServerProxy simProxy = getSimProxy(user);
		simProxy.stopSimulation(sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier());		
	} catch (Exception ex) {
		javax.swing.JOptionPane.showMessageDialog(getJFrame(), "Resubmitting simulation failed:" + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
}


/**
 * Comment
 */
public void tabbedPane_ChangeEvents() {    
    switch (getTabbedPane().getSelectedIndex()) {
        case 0 :
            getRefreshButton().setEnabled(true);
            break;

        case 1 :
            getRefreshButton().setEnabled(true);
            break;

        case 2 :
            getRefreshButton().setEnabled(true);
            break;
            
        case 3 :
            getRefreshButton().setEnabled(false);
            break;
    }

    return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/29/2003 4:57:15 PM)
 */
private void updateChecks(java.awt.event.ItemEvent event) {
	if (event.getSource() == getQueryAllStatusCheck()) {
		if (event.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
			getQueryWaitingCheck().setSelected(true);
			getQueryQueuedCheck().setSelected(true);
			getQueryDispatchedCheck().setSelected(true);
			getQueryRunningCheck().setSelected(true);
			getQueryCompletedCheck().setSelected(true);
			getQueryFailedCheck().setSelected(true);
			getQueryStoppedCheck().setSelected(true);
		}
	} else if (event.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
		if (getQueryAllStatusCheck().isSelected())
			getQueryAllStatusCheck().setSelected(false);		 
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 1:15:33 PM)
 */
private void updateServiceTable(LinkedList changes) {
	try {
		Iterator iter = changes.iterator();

		List newlist = new ArrayList();
		List deletelist = new ArrayList();
	
		while (iter.hasNext()) {
			VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
			switch (serviceInfo.getModifier()) {
				case ManageConstants.SERVICE_MODIFIER_NEW: {
					newlist.add(serviceInfo);
					break;
				}

				case ManageConstants.SERVICE_MODIFIER_DELETE: {
					deletelist.add(serviceInfo);
					break;
				}
				
			}
		}

		((ServiceStatusTableModel)getServiceStatusTable().getModel()).insert(newlist);
		((ServiceStatusTableModel)getServiceStatusTable().getModel()).remove(deletelist);

		newlist.clear();
		deletelist.clear();
		newlist = null;
		deletelist = null;
		
	} catch (Exception ex) {
		log.exception(ex);
	}	
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G2EECB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E154FD8FDC14C57AB89595999595F5D4DCE111D111C7C5C597F5D4F4D151C5E53594D7D4D454D1F197F5DC515D8A282C212222D21211D1E1112122A22202000222222CCAC54A6A2E303A6CAE3B8BAC0872FDE666FDE65FFD775D855768737BD98F6F4EB34F4C734C73BC73671D19F7C6D978C54834295905CA6554E9CA615F5A29CAE5107FD4256FD926FC44A56CD6281422FFDF824CD57E25BD971E499097
	7077C4D5017206FDF9904E00F051D3092AC570FB3EF2D9EF118FE46806CF7A48D4E53AF271CE4FFC2ACC48E7868D071B09E03381B281C71E8DFB9440BF56DCA4600B77B0BC4254EC2532340D503CE4AE96F0FEC09382108C10A15067838D93606C6AAA012E6362B0E54623166CD9A49FD11E8806B616B6C0F816A8BF1CDA25A63C72FA7305B4F19027849CA9FC0E7211B515702CE9BD56357170A13DEDF97FE11D41303CC7E735EA3A74462E659AED0F5ED8DD5D2633746BACBBB4C6CD172E5EE4341A8C3AC81AEF
	EBEF9E4DC7299C06F06DDE4AEBB96115C962A1DC3697719FD33CAF7C8E86085C0B655B70ED0DA0FF7B713B1495F72C7EE40E1B72456FE5654A586BA8DF31CFD276275496F62A730AFD8A159A209B409FA09CA09EE0B175019153F7702CBBE42BB77574180CF5E673E60356324324559960F7F5B5448838BD3A43A60BD629E4F61E5DDBAEA30FA7E0690ACFB99D53C958AA34F9781E494A245BAF4F1EA1231324306359A97B18AE3276B9F442F86FF54B3BF83F33CFBA735E2F94730E6B1D12B3CD06F74A01A9B995
	C2BE8D7B9D3C6D7B092D07292D35701B24A369997EDC0A0F966119FF4E26F892CE00384AF6740D7659A8CBCB6E354A0295FF1055C350837E59C16DCC0608F629ACEFD6103C135A994F6DDC16C5AB88FE78A7871E491273E33E90AE7ED92132F7A8D44C3F4E30107ABA6500C2158E10F7D021AA81288568GB09FC49F0BB37DB8C69FEB55D8EC1B7B74862D4E22E4E94FB0AF04274EEE33E81A0DD61B46F8D837CFE351EB8E99F4D625C0B341CCF4F048A95DC554778F830F2DBA234E223169CDC648DD53233369AC
	D6C693E41ECBE8ACBA9A2B56C3AD37E9FA4CCA4CB744BC9F1E860D5536572C5558F48E8A969FE6A6B669576C039CA183784D74B259CA7495G658F830862FA782A176437DDE7B16A8C5555ADA65331BE331A14C41957C974EC20EDC7BC50279CC49D5FE4A338960857F6E2BEBF774E93655320BFCC4457D886310E1D391C243183FDD0A77A4DBB6D68B78D33EED116BFF3192C5F349F1C129353497CC551A92DBF9750FC09ECCA8D3677054B5BE56A0D3DD33EBEF902A019B40C478F32BCF2CEB17F59C2FD126218
	1E0B7A094DCA20DCB5G9D9A5475E53DA54CF7B7EB2CBA7456DD96F0B41D5621F9A6673B9501702C87789C608B002FG96GFC8F3096E09D40B2121F1234AACA65A600E3G0DGDB81B4GFDG6B81F681ECG58C1FC94E0B7C09B40DE009100CB81EEG7889609700E6GBD408900FBGFAGCCG3FF2BDFE3E0248E70332858344822481949E02B283E8817C81A28112GF28FABD465GEA00E100D000F8008C00E22DC255GE0G88840886C8814857C1DB855081608B908EB085A08B207408C25582E0838886
	0885C883A86CC2DFD27D581ABDF6DBAE1004D16733853DA3FE1920FF39BC50A66B85DDEF92ECD3AF32C933C03BC33009588E7BC57A7FD520538876C29EFA4146E88F4477C9E4B9A158F5ACE53FF40CF4B70C116E1EB1527D7F884C37C2CE4937BE1104278979105FAA911E3467335C64B10772D5AA3FD6B29FA46D8C71D952EE212FEE92E809FFFE8BC07A4995G2BG48989D7DF23BC0C7465CBF08F890DFA56D1458CF257ECF0078736AB116FD6D9869560D112E717F819F98AFE0FD447996E2EBDD3F4EE8EB50
	19F5C6AD7C308AF117D8083F886317A3A61EA608624DB05E2907810615632F36901F5158EC967D21BE1B2ECDE73341EB07A3FE9265E56DBBE4F68C151057AD9662272681986FF07A5BACE42CF058E4B46AE8E74513887111F42C22B368FAC068FAD31F514670FF3612773AE33AC1E12CF41FF53A120CF58CFDE45C455E15A2B4241E18AD7A9E68824950CD695FC546B1E7759609F4D483B8724E5789CFDB970EC7863E4271C869500D4A180366EC39B1F5507597E5CF69E2630254AE47F8044974A71B3C6CF750B1
	130037B2D9A77DC46A2A4CEB9B2D0F7315762FC97D5CEB55D96A392E68580C8ED3C91DE463045D8304E7DEB7148920329B4789AB7AC8BC0E490481C23D49E0E0D9C1DCE0B7514D134B8BC565EB69E63AF17852127E82E2DFDDEF1F8E063AC4006D5C86DF774F13446D936C4162FF68A776B0084615E1FA425332825F2B1300DF86C0211E71D56B99EFC637E6A552E5C0B87F28032E7C28186EADCEE787FC0008AE42096E6AD5C827BC26D09581549CE3F42D4744F47BABF9FE008FG089068521D68962FC63ADA03
	C2558EE0B7B03AA803186EA5CE1757836FA4G35BD0CEE38C7CCF76D9A240B83FC3208CEE994536DDB0BF4AD00579A9DF43ECEF4B32B10AE817029A23ABC13186EBECE27863C41642073F722332CC3BA856053C5F485E6B15DB755C85789F8BBC0101951CDF122FB1353157602ADGBAFA99DDF02F186E1675C8178278AC91DD31C5CC2729C13AEE407BG04DA98DD3293DD698624AB33C299G34D6C697E295533D416952G1FEB03B1230D51E9EDE23A1BB7A2DDB860E3C5F4A9CEF487B7A1DDE59F0CBD814CFD
	0CAE3CCFCC3734167BBD60737B415F7B99DDF73F186EB5CE9785788491DD3A935D95F55CCF8740CF879CF47681B15D0E4DC8978B78A4002C81C6D7E597539554A31DBF60C381E26D0CAEF7D0CCB705532981EF98F464676FC4675700F429036CDDD3B944680A0644F41B9B112E89701DGBE82DD34935D34ADC817FF9CEC8150F01C51799C975345F1BA856053C5F48543E23A612DC827857CB0C070B023CBF2222BEDC23A72931097007604206793E23A7FF03A9840ABGB2853A2A13E23AFB1B114E8770C1GD1
	A799DDA6520D903AB336F13D0C30F761CEF81A81828116707CDA37F33F82FC3E8F78018F4B4F60A366DB58427D8A7089G6982DD6E990C0E75E17F790C742BF4EE722826DFE3DFEE9D0031C1F57536ADE4DE034DBF3ECD6790C89FDBF9864EB3DAE9BFE90030AFC070995887BFB98B6799830F05A9C3ADCF67381BC70D9464D01C616803274C6E4BE1FD62D5F6423368CC052A6CCC6C8F437A45F39DF5D62B69301ECE3388FD557E81F19F4DF09D83B361F9C4EF315A1AE190E547FE6D50G11174C4388E8C19F4F
	6B56A947B0D76B2ED3D7627C5E09ADE2BDC44FB9117D099BBD541C494A5FF126C38F5A7FE46430F9322BD6601CDDB044CF81E0FD7E634744FDFE7ED9B0EEGE887E0E30F6C15AB61292162ADE70FD61D6508495223531691CBAF578C581653716572BA9A5DC8FEAFC3BD7C6D87B246365231CD1BCDE36B336E02D16866BE1B4DE4BCD8A7492F45FD066B176D63BA39F6FE2DA0EF7C64EB15B5A387326566207D66F4E79B4EE23A88BC4B211302B71232CB8118DE8E2EC43DA483CD11AF6AC51FAB55CBAB4459FCF1
	AE7E41E2A20355E6B213F11B7E304E2BA2B199662FC2997CA07F98AE43469CB999B2398CCF5657D199E0F4FBBAC2A8F1AE75E1AEC77E5950E68130B17315F67118F99870E1G71G49E7231FAC2FA46F20073BB546AE5DF24176FE351FA9491C4E21C3BAED2B4628BBD8CF23A9DFEB99E14C787AD4A25FCC482F629CAC7F3BB6B95F6CBE87F93ED1F9FA3E797E6E4D028DB71BCCB6AB3C86193DD4A0ABC743EAAC47B04898494B315AA8D70EB4DE0E1BD71FDEB9EE682BC7BFB84D02886BCFEB309C4AF3219FBA97
	4BB14D292DB083BE88A07ADCAC07E98D19A3505BF4BD4B499FD25DF54C64AE45E806F82190C27F718919EF0037874B60FE0D1E3CD756F76B8E9FBB58AC4DC8DA821E13C37E47387C89A0DB1E9F4ADFE510133F568F6B530E1FB7228C3B7BF4FD700E640588028CA3EBD186B5646F4FE5C8B64B49904BE5F869CBAE43960D5EF0DAB22C2AC2999410FF41F9A8033FD1CE062673D006CBAD5C96FB7A0C465332459BDC068E421F4BB053A2A7C3BC176163C3DC06B62820E66F94A174FD6BF89B8F7997CDC0996EB449
	4950BA81E5F86A0BBA14215E54E3B668ECDE1AC310E3D7B54A21859E41DC0ECA2B1C9CD3A6E03D3A335A7BFA7565C2DE06BA0301754027D306F96B318C29A0DF4979D80662F952B62E9D62064F47B28C2EBF3DB6AEFFED8D16E32B69B47A5AE7397C81A0DB9C17BFFB3E1C7CCAFF740518E2C71D50D9F53653E9DFA754208C0510FF0BBF7633CB1D7AD9DF4047812481A8AFC0BD7E33064C3176187A2C3A6574EF3DC1FF78182B8EF710B8D421204346EA22C371D80C8EB88F6E9067A3AD01A3A32E439D9BD07EA2
	102B7582147F56BA297CFE90978E9043650F5C603D7C4B4D9B8479E12863DD81389F97EC64EDA348D6F0A17A411CEC39B1D75305A87F404653736334454486A6988337183ACE430F27F079BB086CDC7E29BDF2E30D788B510F0F98B78B7C59FC2337D2209E7DB6713695F894DD043E305BA27505D608338178DD043AFCF713773E201B33DED49F7B8C36D37684A67F76DA14BF8464D2DC04FAFCE03F34CDAE88GDD86206C3755FA5FA6C78CEE66FDDB5F219E3D2D01A6763A1F1FDD07E5E88279FC82308CBF8CC9
	4B9085F1C981688B5396703E210D8C9E3D96C3106381AEC7BA70A83B986538EA3AD40ECE08733D9865B83B3C9665E8B4EA0994CCAD27A6861321FFB34A908479A7F0991EF1FA17483B84EC8950FE895A736F1B3D37670DB337A8495C0041866DAAEB1D76E834FA6C263C32693AFAAC0781E48C3D844B7176C6177A8DF1C93C9CA1752757BEBD3525C1DA16536AEFBF6665C887994BAE45F27C55AAE70F4ECB51A76EDE31C3AAC7DB37E9G46B02721DAC13737B620CCB66097FEA96A2E28116075C6F87D509874C3
	3A32E5CC16D9D5DF72FE53E9FD66E0330856117F31C64CBF8572AE8E44B29F6A119B672983117FD30D5E7BE0C6ADEFD39A74D63346F6387B345E9D16EF41B2F403FCA13C8C6FEC135ACD81F1D93C8C17ED713E8CDB3EEDF132B97CBDCD1333327C13172560B2684BAF43323C31D32EACBE17218FEE9CEE1448F37A3EF77DD614A5907844F3D99E691256EBA2CB6E6528577C26532B57EBF4751232F8D92D05F2681B789890E4EC3D9C4B7167B552F278C1DCB8AFC7C2538CC7B98459366EA3E3A20B6E08C5E76DBE
	0D36E6E9B34A94837CB239CCF9AEEDA61129628FA853F94D27275B0B9A49DC2F4E2E77722DGFD63F5AEFF8348B67C87143F45A415BF926294DC7E9D5BCECF7EF7326B297EFBCC7D3AAD96D3CF0B5EFADA6FB7B7ED4BD7323DA4E9A0E3E9904AB9F7BBD93737F56B2DEC0CF84F20F40C588134BEG01BC4D3A6DC4469E27B971FF5555AA9D733FD1B37CE567FF3BEF884A8E8FE2733ED3029C733FD96FA7E597FC00733F035BD16725D7G7FABD06757384CBD86C2DC5495A8D772765353F971773534CFB1EAF750
	B5015319BF5E5002E5088779F22F44B21C57A7D7061AAB318C21AD2EF5F1D95BD222EFA6136B1B2EE86E166DDD7A975D375C8AF9EA2FF45DA3E917607CA14C76733EC157FDC22EE47BF909BC8C7FCF0A0F906185BB595027E2A02E74AA5C037A689071A1BF880782C483CC8148GA88C46751E3786481EA50FFBFC1B0D50AE9AB506B6274DAB385756CE789E538D8A61D0BB51955DEF22443D97D543C45795403385C08BB08C904CE578FEF0D4999A6D32B2B0BDCFDCA15D9F9E9D6C2CDB05A44C527D49A55D92C9
	B8CB120E79411CB6D2AE1975BC668E4416348EE7F40B6BF0619F952AAA008EGB3GDB774BB5648972787D916DF66189322E988361A4008C00022BE18C88508AE03B9A75B6B438E1D43D911FD41B6A8157E4B29D2B176EA7623A502D74CE07D617F4D29D86DEAD71F32A1B4DA3D25D0483DD82C04A55281B10DC54CD565538577453D55E49792DCBBA291C1557B827E3B27465208CAD57208C0FFBA9438BAE6916CB423E5748795B77F91F4B7B1B1AAF9FF13D1675C975CA768984C33E915720DE1F326337AEF157
	60DA733AC17AE1D208C2D584D09102E5BD27523B32863A24136A3BA9C4CE5F5F74213EBB398C5BD6FBA7C33BCBBA298CC132B2980CA8C38417E1769A6FE4A8F6C9A715A1B5C44E669B77AD1637B9FDEF59EC328B762E6E125ADBB9917AG002A09E86F1AE3D81E5609D81EA73DAC4FCB231647EF221CCECD3C1D8B61B21C3D56BB99AEF5C9A715C1A1AB4322DE14A11D4B302E4ABB99363A24134AD0F62D1C8C8FF25F2A3D96E5F85BCB99BE9AD5863B2C8CF59614A1004BB0F11D17FD28CBBA298CF132B23CEDC2
	9912398CFB2B3D136130CBBA298CC5574949F04C0AFBEEAA2EC3990A56FBA743B7AE6924B2F4CBE4E075D7B7C72E7E7AGED60F5D8FF5B6691B9C93B904E65CC72D24E37C615B3C5D6D76B66238CD9DC06009A6FE4385AA51DCBDF9CAA57A65E55C876420F32EFE83955E431594804B7B416ED701B6D28C5FDFF56AF56F7CBA89B135961699F0A7A7E590EE58D8D457E316CB87D768E4299G7957238E5AB6F827830DCBBA298ECA2F1773174CCDD27949382986E8DB2FC7795F2CC3793B2FC7796F5DC55FA9A09C
	85104065DF3851BB79CBDD523974EB574B795116EC2C774AC9A84373DE4A10BA2A8CD51364E4286A617DA1176142CD5E49906C124E25BF1CA4E747A033540EA1C0978B201804F6BC4F02F6CC1F04F65C5FC76CD8F68304815AEFC079F757FAA77FC117F4D2794DB748492FD74B35DBFEC09BFC834A7F1F7D2867C8AEE7E11DF7F2AEF5C9A715B3CDD64E3F774B49199B86635CB014D3BB04F2D604211CF1DE4A796C28F2F607494939BDC7369FG5AC0AE67D353F9BF40659C5E6C1D1C93DC52397483E1F2ED7042
	4D04CEE6DCAA1A7E45B285CE13FB474B02FC8BEE44B26577613BC8590DD826CD755E15E907CBBAE919BAEF14EB8FCAB721DE6DDC061C866FE4186B12CEAAC3042C8C9FDBD106B8AE43DDDE4A7010CB3A65123072A639F43D0D5E719BF1C967B27E3AC92E4CD770F63862A6ACF365966FE4586C124E255E5DA4D76F961C101B2371815A401B50C73F5E0EF206F3B97F6D251C53360EDA6FE4F535E4979FFFF199EEF649EBECB25CBD2A8C15B74B49206C66F3A1B7238C26A66FE4B86112CE6A233EB74B592BF23D54
	DE41C09783B065E63457BF57201CE9DC4E32E66F64DC63124EE51C96AE272BCD9B51DEB561A8C31A17B2FCB82A8C36F0B9DDFD34D02AABB24670875A10F05455D97CBDAC0A4B9931CD4ACF8A72F25EB7EA3AF4D9B9A38724E3213CDB942AF2005ADBD04E7786311FE83F8547C21B6918BCG42E1GB137607C693491B2FF2A00F086C07EE4052A8C2085209B40FFB24E1FBECE771AF8BE5F82BF68D41B78CF19C9D4219D5C6E1DFDAFF0C9A735EF68E4B93DBD5AAB35EFB450A583E4CCC63DBDEBC3BF4C7F935A37
	3E45BBB9F73B2413565906BF49550518BE14218362591999AABA368D04F09447F5D2DCBC0413B92E14760BE9904E0DC0DCA3ADD3D98463A54C2158487AD9CD84EF1FE9186C4597ECE313BB0BC28CB5912EEBB4E38546A3E8D49E5E1CFD0140FC7669A1775FACBFBFA47F4DF272107CB74B2F8F4DE5F848C3FC028B463FB364791B668F065CFF53DCBB6CDE4E2D4372F26E98161733ED58331C3F8C3B1373F32A73C7964E21692DD61D15AE08B4E8EC9AA16E8D9B79365F65BB6B1D1A9E1DE02F79CA450A1982CD23
	4DD43FE2A50E055FDB66663BAEB2A3EFE0F57403C1E99DA55F53F6002F19A3300E166B31FE78C5E05BB66F98E95BE2A02CGC867FE2DB2483531F937B25FAD06E7452D4EFEACFC37FE423DBD2EB9A1EF0F6BCF106F4A2C26BE8B79FA46D96761F45C44629C8DA25956C1D4A7FC4FCD535BB416AE1D0B4D22E9FA9667AE7DC3A75C591559A5F82513DDD6E33B7842F1B91DB700DE5AEFC51D8F0D204E4D37224E77CDA5BA8F03F08C0062D654797539F2BACF97741CFB9B64FF1B334E19FC2D2B1C64DB036D44D00E
	DCFE65C29E3570D44B6617DB6914DF95362F1D3CFD6D3E8D4B72AA7D5EBD8442D1G8937E1D91E33FB6A23D3EFF3ED83595A73ADF4FDA86BB636767C89475FC3714537BB70ECCDFA934513F08D44055C0E6B6A77D62A44E5D022CE3E6F1313ABB71249D3844F72C8B91D1C31DA1C5FAA1EDF2FD1AE3FDAA18FB5BC3BE573EBDC6324639568D3B3DD6A71B0248F81080CC41F32F461D8AABE926D704DD122075CBB942AE2004ABB508EF54708FCADF7B84B50A904EDF7385A0149173756C93ED5D8DE9BED977CA469
	0205F0045B7C221D72DB3942793C37D8C9BA0590CEF31B1F394AA9BFAEDF9C350772CE67F405C2386CCEF779152EF34A2F92735BE2A17955C852358AE12D5B7C944E792D64FBB5CCA4BF3BA41D3F908EF11B1FFF35587FD6F37FB3DBC9FE11C23AB86119A4BC535D6637EB3D13FC2B310E7FF18E56715C3B5037F4F309EF55C0D88DE0388BFD2BFD1E1C6F7B5E45F8865D650E777C9AA75E3C3D5AB8DFAE3FF0A11F983779BD6C145F0ACA6CB73B9A5D2CE7379D36188C86E1BD1B15FBCFBF5629A93C5CBD34ED4B
	0FD228CA81EA22305C87E95B260E129F47F54B60D99B96CC5FA30623D89BB615632F27F8BF91DE587FC971A49C88F119D15836CD5860D45E35D8060ADAACC3475DD806AC3A37BE904261G31F7E3995E217307C9F74B17A1DD864FE4DD51CA5B19BFCB4BD0CD7185FF16166141DDD806E2087379B35FAF37D1DC06D59568835B3365FCA0654FCC1ECCF8665FA3571E4E5D6424931E5F069E397CCA04BCAA6159A41B5FC3CE792D626DF36AFCE97B5C8169FDG826F4176B9414C67F16EC1BB845373C4D3A01C85D0
	984D5783E87BD29EADEF07DA99BC5377E3F4FEDE9DAD3543940A6F0C165A6157BE340381629222518EFE35CEF5117B52C4B5162172DEAC03EABF4943GE1DF0060FB318C7F5DCF5B20FB1DE50D9342C9773A2B375B6B1CF873F14602813EFE4EF95BE8BFDFF69F04815A6FC35E7AA1B97B1A6FE3BCFD05E750FD8E99986F7FB973DE073C4F627BF0426FC35E2FCC233603F096C061DF7898E73A6C786AAF42786AAF6E4AFD7BE6A75FDA093C6327E15B2B7E8B72FEE286616D8F61D000E84EFB2E6CB8E60A8CCFE7
	5E7D75CE3CF93F394CE5FD09C02A10DF165B32D4B7B865477B4DEC9B492FA046B9DDE94CE8723D53A06E67D6F2FCD823133DF87D3E1A566FAAC93EADC238D3069F4B6F5896A739F97FF77BC93401AD86EDF0271258A09C4231GC9B1E8036F68FCCA0604C75E7D42BB527D6EF8AF5F6A441B77FFDF6D1233C10510CF035B7CDEF14EEFAD1F836E164B2FDD4847GCF1F7B655A3EC9CDE29B2CD1E25B1735D65A7611F90FC048A36CFEEC7B8C6BD1FFB177237EF653EF95B3A1DC7000C2D576G6A6F8317BDBE345E
	BCA00C1D61597D001CFC871B4572117318D9FE8DAE6B348406C5F98448667755B6F1FE64FCBBD616D50B787A7C83D816B91F5275F988E7G64BF08E571B34971AEFD10712B02E74B03F23C5F603CDBED265695DC5766D639F74BCE484376A06ABA0D37137E8F22FC53E9BB998B61A4008CAEDF66202777317CD8F77D4C1DF48CD292AB6DE77EC271653152FE26090FD92AA0AEB89667C0670C381F97D8B8A2BFAFD0BC62FEDE602B115167854AC75C4F8B2C9A99FDDE20E6043CD79E52EB75966114B661DB706DE2
	FF2962B67B506832367A900C9D812A9EC21BE5733576560750E629740CBDBF8807GC4BD04B6FB41A6E7330407182DD29EF2579E8C350865DB476B47C332FDC62610CF7EDF5D65D7E2A1F3D7DA5D91CD1F41D6EFB0D9F53B4C42114900EF7FABA943FADE2E123F726FF469F903B63D0D6E0B897EABB99FFEBBB9A6D0981F5FBAC074FB44226901E1F91BD9F7D8E9FB1866E5C5BDA6C31A8C00C240D782289F463CBB685C6C369DBAE35FE60D0561BE2EA173B86C34A330290DCE630946784B797AFB7443287B0EE1
	227B8C886747012D62D077AB74D2DD5544B974539A672E3E1CC3675334F12CDE28B87ED20A779161D9FDA922F8928E06B865A3B877DCC277B1EAA1EC6338338DB8C76D43F157D3DCA804239EC19F1BBF4C67879E41F21EF7120E5B9FD5280A81AA9F657355A352F236BC6AA8DB67236E4A79A81D4332BD4A4AB303631F25F8FF911E15330362C9B89462729EC37923F3311C860E7BFC9A16534EF19B26E1B903B96E629CCC9B41F17F4A4134519CB7AF9753A6BF0665563954050C479CE54D0BF7D76E7DF6BA8F95
	AFBD277D9045D744CB4FE9FF133EC3526F09A0AEAC9EE5FA470EF266BF0EB25DBEC83EDF2802309A40869088904E636FE771427E8632F69577B8EBE3D8FC63E03B101E24F35756074B4429E5F0B6890E71B8C0F964A81D4F39150274FC5C59CA793335E7C842CCB71B69BCE39A704FFA9C75F5DD9F6AAB6FEF08FB10624AA1DC4BF1FEC6CC2B6638370D18361363E619B02D6F5F70FB793BDC5687493BDD9044077F8D6B5666C1EC67E27F06F5EBB5EDC332A0DC7804C2D57E845AE320CB6CE38D00EFFF4221D343
	93EE7BDCBA176773847325399C7FB045070B704C474AA91E04959027CD40B236537D5869904EFD92F193A92E8842E59CB71D37AB159CF73E815336F35C9386CCEB60380A9EDED7B9EED5AF268D663801DECC9B41F10ADEDED7B96EA5A326CD66382FCC18B60363FCCD18D6799462FE35E05AD20E1BE84134D59C174D577A9AB8CEE546345D9C37430CE907B9EE3299537AF15C39D6CC9B4EF10FD1DC8C0493B86E29B6FABEBE04B3B8EE2D1A1763E9741BEB67C85B2652279D3ED27534BB3FC92473F4ADCFCB5B26
	940A6FFCDA5AB69D1B0BED138D6294CF23CC26F9D86E1AA908CB1807F2B6F15C5E795C9FB86E4E795C9F26E0F97677E3F9028197B145D97E58A96E4A73B2EDBB95D3245F277D0362D327C83FCF33D2BC89E7C2DC47B3A8D3BFEDEBE3A0BC05635E9FC4B9B31F61FABF2E646D5C4D225F1547C7DBEFBD9D9076147D8EBCD673BD18454FAAD4954FBA6B3D61D9F7F6B053B3A55A1F155AE108623B1F155AA13B8E6DE0073824E7D16753B6931D57BE87ED73F3081B30137B4BF3E807352D8E5D570AFEBF32EB7CF574
	4A3872E0E5BEC477A687C3F943399E9E4EC6BDC4F15C9B5958C7A5F11C1A36376990D6AA90F7B7EF0373B9AE1162AAA05C20407E2D4D69DB38F6859BBF7840B3D0017DDBC9A15F7B29407E2DE1A11D7F02F096C0E1A25A6999B37AD3B960EA931D7DC71D684E1F8AAC54EF92257E7499455B93257EF4EDAF7A139F4425A57231AFED5BDB1F07BE6FF93E0F43465B676779B7C8FD8E9FFADE747BF05F787B53706F40E312DA5CDF04C339E31EF768FE4A736E6CF0024E6726BEAF9D431FC7714A9724E378B97BF18C
	DF84F143AF204E9734931DA7C3B803638ED0DCDE12C2D5144447EB6DE81B52A4344DE783E2591BG5F116410571C64CE76D4BA4F611BA415BD0362031324328F8D226C11C4263F23CCBEC3A867B0473DBC44FD0863269EA738F088A77C1D4F79B82D2F27GBE736F8EF973DFF4A77B3E69F4CE65C5296C07A83E6AC5296C2953D076D6080BFC113FC3CC67BAFE89F13BE6F09D3F447DFF1AF4BC517412C3360E175C49F9B00F70B53FA49D071AA9BE78A569B8741999B88E0D03B8F5B24AD4C647694990CE6738DA
	3A97BA8B4279AF73319A4595C1381C63BE2575399642EA0EBBE4437C3AB96E0E9E9C0359B86EBFBDB8BE7163381F793BC0604B281F2813D27DC43D6C50C97C4B6E7433053E53A63FAC554FFE0A4FFFC52A1FE8A56A278A62E2DFC1196A683E5F56D7219DFB15770B94E707309F475D417B4AC00E2B5E05F4D19CF755AE240B6538E23A27B69542199CF70262F2D340FFD29077C0B726ADCF41BE21320AFF471202FD4288BDC7518F42A1GD1A9284BDE176F3E92D29C7ACBC9F1274B07589CF10AF46C1AC071792F
	C9472E7B7979F7A590677B9AFF2FDBCB4B8961CC0EB32E65FB7BDEC75C0594D78661DA0E7BE5A326D5F3DC4CC6246BFC9D4BD835C8DAC65B6B0EF2793D6E2E0CE7513963605725E53C1062A3DE1716F14127D846D808ABFB83E53A063E7B78C3B81863F279FBC9984755D05C9488273E01368CEBC5DBE63D0136DC52C678D426AAD4ADG5AD4AC67BFF95BBC8C3800D467F205263AABA7EBEB23D2257D3BAF454725CA7B777CC16C5F9590D773A64ABF073743A1EF22CC5122F179C3225FBBFF0771F35738729076
	7F38FCC7C4667DE3217C0AB75106ABCDB8C6CBFF93EDB81F0E114A5E02B0C07BDB282F9996292F1A5FF2584D77ADF7B66C20E7FD843DA57555AE0A8FFFCB6A2B6FD851D7E3A02E749FFCBE0E3655FE908E62387DFCAEA71463127A10AE0E63B674A35D14FF603C6F9EDA9E51F60E657B75C62DE9001C86E65398ED56658D4DED2D3B5A9A8F6E5AF9303E85FE3073EAC28644F702D64DBCD376BE9982197F9066435304354F85D7CEE5F9BC710BB80F02AB8D59D35D6451A024ED951E644C9BE6BB8D1D97A0E9FCA1
	AEBA0D60AEF2BA070D147307E9EC4FB30B3F5FA91E749F87156278771C62092CE43062087F5A699C9DD26EDF04742CDC0FFD0D675BE7BD9621B45C616B763E545C3705FDD442B3A0650A29D9EB6CB94C57D6392C45F73E6D70AF5B5B4E3E6677B61EC313C0674F025F160E0712A8BE72ED69F86850B00E0762A12E629D7417A63A3FB59042E19C7770D1742178F730CE789FA3F768E4C23878DDE8EFGBA5E4538ABE89C3983075C9BC26E10A1774D3038A547C87DAF9668E78BF90CD62FEB5E9D1D4651462C25BC
	26A99D732BE3594BAD1DC7F5872C8E2DB550B320F4F69BDB13211F71AA075FA57A8AB6940D1ABE5AC078690C6403E72D307E7334817DBA166B32E8D86A8F8A11AE52A4FAD12623BFFCCC57AA8A53257E209A21EB0E69D2FF3868A47AC383440525236DAF9FC1FF487FA7628A2622BFD47C9365CC2063358E888F8384F3FC924513FBE6489DC6643EA306BFB4D55C8F907AD3E967BBC47443B2E9990F63CEBC46EADF311D4475DD9A4F6CFE1E4AE1774AE91E6C4EECFCE49A5AB81A68096691BE531107AAF774BC56
	67E29E895CA643346DCF01F0A6475DBA00F6AA7E976A7DD7BA0FD78B61F60E6BE6F70B9CE35FE7B15C3B831E7AE9121664F1AA7AF435CF1E5D930FE94A3155DBF76073AFBADFB6D8620146A014015549D9D5245DEA931DAB5D2A69523138FF4C18AB4471B31BC9A4CFFB76C9F2EF1873B5A80C004537503D273B493916CE69623EA57CB7DBCC9A6DE10D152E359AF09C733E6C5E78B810B969DF5817DFE3F4EE0712BEF178521F7BC6EF07CACD2E6DD0DA9F7AD7463F50C77F176F48B757B4869FB5E19EF96F6118
	6BDD6A0F04EF71FBB8665ACB577C5490B6G783E077E3755E52FC570FB8EFF0BF84F5D186BC95A2F453E475A37799C7FAADB239661D93B371E62C938B4C3210A49606FB6F4FFF8477B8A1579FD443DC9F1BE908E64388CBD1FF3FC1F0F2F0F921FCB02F096C051878AD595C0BB009DA090A07283FEBE9525AD916EC6A377BF11BB4D48BDE4648EB4F2EF9939EB0C512EBC6AA3563310970689AF2E0B8FDC75D3A570CDFAFF74FADBA4135E396E6EF51205B85D9CD1D85CDE4A5D5D29D232B15869764B42FD9DF4BD
	4E2AA377G6A344C7F4EB9063EB665832CABD1471C6BCA57EC07CFFF6E314FE6F9C69AB04F148F30DECC186348E3EB4F68F9F4703B6BB3F89E8F94B872F837F774BC72CC18C76E3FB10F5766BA7238EA8C75739C8B66D14873D8B25F114727565173D8E946BCCAF99EF5050EBC9EB20F1EC7049573287CB75A6995FAAF6691135146E8B2A9CD834437F21AB75B1CED29DD60603BD36D092FB05FBF876B324F3F791A6BDC7ECE75EF2CF3AFD1DC9C04D3B86EAD0A2B916E6D9366EC66CA7BB2C20F3F637E6DFDDF69
	28F393DCF8086B5969741584189E4FDD6850639B73BC6911515F3448C17F787C5175EE246F234ACCF83F4D646F9794D78561CE0EDB4A47AE36CC54719B834466A1190E399592A6F7A23270A09F33901A535105FB9BDC42F91C2E3E6500797685F671D931CD7FA17A8865F7EA44C05994G45002F788F3B93126D794A9FC21AG400782448224G64FE08FAED215FB714FEA8567FC90AEB7AD02C7FDBEBD17F668FD13F7DF5C45F41BC6CC343642EC9E1BCD7F73AF3BBA4EF4F7A7F7234F9F8329B53FF71E6D77D2F
	5A0C3A0D869927G94FD24D015FF446E5AE47A1F3893E99AG5F8EE0878882087A085F9F136D5CEEBDCE12887567456C515B4B029C9CD7A9F81E47FA1C732C7C4C11E70247BE0925CF6FF19D834EB0A21F340F709C712B1613EF75653E85B29B74F4684A46F96753F7C6526FAB33F01C27247B8A34909E86884CC21F793AD0BA4E8B4FF278C0CC163BF1DE94DDCF1D12A51DDBFB1062D33324F3EB5B96625CDA9644F5FE0CFE1EC67B30580F4575E1896D23D3BF9657075DFC1FCD41FF79F9DC967A9DBB8F1750B0
	3983150543FA453ECA68FE7B7668FEA79E3FF51BA44CD5D8DC6B43537CDE14F6A81799A09CA0663F6C8ED8E18F2A8DE99400CF83487F9F549900167F21DE6FD563B85B40F185FB89EE9842819C775CBE54FF647FD03FE75135D48504B3810A1409827EA20AA7F74CD6812E0363976C9F2D6D40FCC6538B61A9EF0F65237218269C5BDC041CAD19BD4A5BDD473B756DB80E72817999DDDF3BF33B301FEC49965A05675BC76F2BD78C20ED82A0CFE14F58G5AA60463B673FDC031DC576F507508D4C2B3B5D1D5B695
	719FD2BC39573781F05D9C6FB368C9EFA43D9417AA58A9CFA6CE5EB6D7F864A1E79334015C7C0F94180E070A9DBA4E9C9A3D6DADBACE86DC5DA60BFE88463B9A039A2C55EA326A5946F0E65BD5F45D1C3CB379CFC51DDF1303360962B836EF1758A60E63A278BE60B42E6B8E3AC6DE101D282AGE849C6FCB745133B143B81674F716FCC136A0D24F1D72E8AB7F185EE70429A4BF4A98FCFF5441DBD5C35FF4CB6CB17B8ECF3E446687E1FCAF59781F247E6F37F67FB83D239FEF4F9A570AC4C81BF87E8GB08384
	82C481A481A8F311361F5216C23881005C9FCD6E245642EFDF00D082F67E6D428C9FB772117C71F7D40E2BDE8BA538A4D1B8D0061E56B1815FA0BC094C8E7B7C572DAC62BD4CF26F7AD21B0867896476B80B6D0BBEB0D71497549915F33B56780512477D2B49B92E3B449177CF0B6BB86751CDFC4EBD975B4D8A1BEB7B3A55066D2B8268D85E67136BF404FCDE6C4178D49EBFF929435FFA0D237B5B93743B39CCC81FBF8D7D6D18124F35CFC39FEA19CA8A5581E1B300BFC094C09200F2FA222A9420E9BA527ECA
	E93BA16CC76204BB414360F79CC0AAG397B1B51BE20C49B133CC6EB6FC6038EC1B66A2F421348C6FDD614FF1300D3CE977B5BEBDC96F7BEF2AA10A372A574AD74C3317F221F4D92251B2FEC5F6E6A8F266DE86F3C9968B79337BB7B5E13DFB96C9F3FF374F769122F9D74376D9A3D6F58FA4019DF7DB78EDF3F29495557A7F5232F97014C0CCFE237A7BE4C964E67EE17C35A5A998E7B29E7387A8A9B0B3FC77737F4C33C739E060FA95EAE4233B17A5955FC0FA244254D40BA60C73F07EC4DCBD4E97390774586
	3E3FAC8FFD7DB10A8B07F0AC47BDCBF18A616EFA21CEECE89768D05E583C5377F7E71D75EF40EFBC24ED5AEF814C46160D4E76BF722D43FF4EDEBB3A7FBCC74F8FB8E232683AAC26BE23965B323B683E1EE450C9DA9E36E51715113CD97C3BF4BFCF3EAAD1D524C2FB6CDA04FDE59547BDD204B66AD021BD4E7B146ED100F0A84785D2DCAC0403CE220D96FF7ADB6E93A13C88CFE79BD57E26BC246BD542F70EA6E79BC5112B9F0535294566928F7925887232FC5A4C2E757928192F53F0DD5E52AAFD1F4E1B69A8
	F771CCE7BF2F00B02B1317507DCB8DB393A56F53FF2478F6911E555552B6FC1FB6C0DC42CC34F99EDDCB2C7CA4D1557E8962B494E700F0A0475D433F5D8A7B44718EC0F4B50507A75030F984FDE21E4B781D507E367576FB979E3F65BBB5FBAF7E063F97E490FD644338912089209BC078C62A9F477CFE00E3F3A875C622739888ABG5273D1CFEBDD3E1B4E1BA53279ACF76D7339F43FD345AC294DAF217806D9D21BFF5A0BB6EF073868D9E84BA5F4AD21E4F6222AF2B6627E483F8FEA1F0DF232BD273E908E65
	389E0A4B77D12892FC504E99D6318D886D69685CD7121E711865F47E5F69364DD29FE1F5D65567DC77DF788E02C27BEC7438B64B687F7B3D03DE5D6F091E1D25F9F3B9EDC7213C49G6C7B6B0069A413FD1A234209190DDA3D4DE421C7E6823255E2B26BAC36C1065DC7EF02E2F9CDA2D75A8A5FF9BFBB7DEBF7F9549D32ED5374EBF69DBA8AE3A335260B6B7708D56EBBE9CF4076D15DBF8377D1C57E79A6E554B98D59E47CA7370FCAB9075935C4F8163DFA45D407AE7E371C682B95628CG81GE1G51GD366
	20EE0AE79099494DCBD020BA1BCDF3383B07FCD20FA55B27578DB8506463EA665B5B06F02FC56A9C74775863686F798508CB21388A88B794707188DDEFEE053099401763DF21F8F2D7A23967185C17AD3C138F3B6B23C85A3168556C062E1573BE43AD0F316E197174BEAE1CB721A79F2D9BB40326BE9B4EA1766B512743C116D800B4G655CC42A8356C73EC857475D71AD1AAB6EA30F2849DC0AF87F46AE334E08671CB31ABABDD9645C65FAEF34907FD289D940245788EFB619ECD61BC5E3E684ACFE61AF73E9
	3C49AC09967CB76ED771BE40126FF7496E834C7CF5A83BDC103DD5F8127D0DB52FFC1143783CE5A0BABD22B1D849DE9166CFBF52EF52FD01BEE8AE7A58649368F7519CB7173EC7A6C3B8E3AE5AB8076E7BD24ECBD49582144DC37C9C0AA7F70313FBEB49BD678CFF5949F601FEB45B97FA20A9F39347F8CCBEA99DA31F2AFFF972D166E3FF615FEB554EE3BE568ECF83GA9B37331EF270E5D479E58D2A8729F7DE19DF7A0E1DDFA5BBC749F0140597FBEBAB2D7709FC93490FFE21A586E42FE5DDCD75C25FD2E75
	291377913ED03EE0D2DEG85GB9E3A69D1E854A3801515F8B7E56CD5E8B449716B13C2611681A1D183AC240AD9DA4F2546B483D0358068F68308E945FFC1D32780B5A6C39B2F500C055FC26DB6DFCC79D20EDBF9DEF7C404FE0881A0FE3202B69BE4584882782A88B715DBB275FD5C78A1B98A70CAD86BC75CBC5056E46CA0F50FDCA650552F75927A93E26D07AAE7B0B9D5FE5DBA0AE22906B6601C15CF7D038GF119FC0FFE55822C174F8D1742338342E6GDF00E0008800F800B4007C05092A7205C87FA625
	2F053096408FA09CA0C8386F1D5C17CE6EAAEFBF1F1FE9B99C60C18FE6A1FF420B70982DFE92394652CF788A32878B749D8B6446E65D9EE51B26E473B43314EC5F56E8BC47BAFFC4F813B9A1F7FB0619CFDD5FC57C7D1049C28F42A9DFC46AD4F4979967A5AC646BAE032EF5F39E1FE7B1C39ABF00880058C54CC6EC0C774951516B66EBEAD2F63A0FF03FC6CF4E5825174C71786B6A8BB09E7E7489F7E73338323D0598A739DB1B453FDC47735E53E7B43A64DD211B0F7175269E33C1E793128B71CFCF673C37E8
	7486673C15BCEF527819B1A5667D68D61EF71D4140FAEFF15EFFE4FB37ACFD42FE4C478C4EE1EB2FF3788B13F3780F9667F03359B97C309543EC6C7D85B932CB607D57B6B251EB548D1470CBEC37180C363225F5963D4630343CE4857C3F123C58B3DB3F426F89CBDA447A1C8CF86695A5527A47EC3DED6E68368E2D1805BA59EA127493CF0FF0DD0BEE379576227613CDB9B03831698FEB8C6C204E4D9ACB2B4920BFBCA84C9D045077CBA2F3C9917A6D0B7DE46C6CD80B122644791F8FE9DAF21FE9A724E5FD68
	3A01928F6589AD927721FB3663DEDA9C27613DDF2C6D0C67EDE79424CB87E0F3AB8D3B4839E2235CA9D6DD6D12B13DA5C068D38A87F14E246CD36CD3B653B549CE885B8182GD8BFE67F867B3170C957AA030CCF67480D651255A73243BEE5650B7F54510FD578A4E58B5F9A8D713D301FA58A5F2BF48EC93FD739F82A78FB13CB06245F2B10740E7858A16977AA4E69BF9812FE2F0269198D8E57FA7291C6F3D96DE87E40EC9FD76B4976351F09ED1FD6B2CFA959C7AD98DF68B7B7236D55104E7FB3347D049F2B
	46EAFB670C2971199D2E5B4C57FABEC35BBF37031EBB836142455057AD46791707F7A2ED43E234597BBBBD5B6C5BDD1EED7653AE4FB6BBFF17E71BFD1B63BA46D9E2F49D63840C10F175B0489D305839FF8DDD6C5ACFC749609062A5F1490BF13E50C67773E4AC164E9D0DD0FCDE31F46EE8BABF43299862FC0AF16CD3D3087346A99CF767829C8F65FD0E631B7B9652F9C588B7F15CA394C76E10EF3D8847B52D8B456F320456DD59AABD44F99AA34CD56A970EF56E481B39C3E1BC5BBBFA7DDC5DBB367A595DDB
	6201DF6767627A3938D55C2F749958150C4C069FD930EE5AA0CD7867D8B73F6EA0BEBAEA5D5CE6BAA4EA0E0576381EFF4B1772B95649DE7A9DE47112C4D5A5C04B922C139A9B52EA17E01D3C486639CE5EEF73DCA75F33F92E13DF77392B13EC6C773A1A6E09DC02FD6837FBDD47FE9B77E2DF1F36040D7DF23FC8D495811031B99B8F3C33EF747140B2AB9F3F35759D6A51133BB1F422B1569C4BFC6778AD96D38F21C11F1AB957B9DEED22317CFB9327BDC19502FC5D709C86E0B4E996B91AC0F806FD01BA7839
	9D47D2CCC7BF53FD3AB1904F74F18BFF87ADA8E17AA803E78DC0945747CF03236BA37EC7C7F9498BB7D707905F3DE61ED3BCD707907F262A40A91E290315336B38341CC4B6B54858DD023A78E9C40E46876283CBD0975B0E133AA77E56360D7CF5FCEA3B27F9EB131A69A9AE0768A93C84753418FF8B16A5682970CB5095GF917E3FAFA5863B7E742FE7A695C6E0DC62DC3CBFCAC3030C0940FDAE2F1978D4E9645214330721FB1C3DAFEA2D7AD48277E92F5E47DC40E46G71BEDF220E7EB74D354EAC1C0E6902
	3EE4E368887284A073D42CDD3BEA0627F6AD69CBF13B76F6911D3BE8519C5299D6627B8C3DE7E5E7DF8FCE6510C8A1EEC2B359D432C5EF3041D0D298D4F41B86605DC4FCF9A2238D2277C1BA510A08984D675FAC14526C24C7EC769CB2210C738D0B24B40D3DFD9A03D82E19B5AEB4F45A1EE544E896EDDEA025913DBF84F653F943BCF13B16AE6835F0A978AE0DFF36CFAE3E0C47DF6594DF331445EBF97CC92BDCFA3BA33EDEAE3D7FD2740917B7390EF58E581C5F595E6FF18E3FFE52550FBEBE09FE94321479
	D194BC638148DCBB7323B3151E7CA8E529580FA656B97C8867400255339DFE2433B0F389F1A92AA5CE36003F2EEE046D7B12C592DAD7DFD39DDFA021913911C0B34F3CD0C2A3F2A35C53722514C664C6B83EB8D1A821C1B7E2B63CEC2A5446C427195C4633EA6562733F427847F7C9FD00441770787D4DF269ABF93C59AC173E69AB7421F336BB7B473D5D2EF36A2D74DC168EC8E37E8A471FF7D461FE939F0E2B2C665F86FD05634F7FD453F99F9E4ED9CF42FE1763D8747CEA71B811501D6AD85093B09E21CEBC
	FE4B35EAD1BFDB65DA2FEE65F7BB26FC456AD596BC8BCA211F3A9C7B26B59B46709D7CF6B2575CE8575B1C6BC27A81722E3DC7F7442233F673360B4D4D7CC0DE85853AF7FEA0E3GBEAFD3EFB218AC4B8F11F5CCC6DBD46D208D216381892DC52745B25997213CE525EC7E3C961EEAG720E415A0A64A2CFED05F31F33EA2A503F301134D55C0ECF1ADFC84B57E36A57119E37C5EF35096B56DFBF157ABE1141D00A3EDDC5672F9AB5D69B8B1FE1F6756DD8FACE0D8F2489ACC5BF2E5F433FD76538C99417846174
	D27465A0FA4EE696048B3EC65CF594D786610EA0FEFEFF5BD8765010FCBC45939E6EFD3CFECCBCC673FDCFEB147315DF58C71FB32B26FBD64842F84B57E8873BFDECF3E601B4ED9D007F57B8E776145D13AF45FFAD7625543D444F1D3FAFE793A76C5D28054FB1A5FF0D6FC6C3234CB1050FB2477462A8F3CC85475D3D9B895FF073B3CB0B3EE1E5A97F46E1675AEF1CED2E7E8667966E227BDC3A3F114EADBCC071435FC867961A0DB83790GF1995F20DF6F245F4936FF1B28B2FC0B38B8FE46D3403768579750
	EFFB22A11C40F1C19497F805C21542F1DF74FA72C31276D45A5668EF6556BE2ABC72B855FDAA72EB982BAD2E6DF98BFF2F4BABE36DF989BCAB81088E183FC6DABD79EBCB1958DFFD9730B9DE23F6871D87102EF3969DA5ED3190A71A4146BDA79165A4AFFE1FA05B1AA2445DD5CE7CCEEF545B749A03FE089E225F860446AE3677A3351A44F702BC43GA1G11GF1E5386F636B65E4E3B63BB1D63A6723D15B254317B3641777237BFB97667CC846D1423D8BA495990FDB7424E6DAB1FD510F240FBBE402727510
	7C913F64C7E2F31B49CC11421D8CBF12FAE651F7F55B982900DF76A35DC327BBC2514CE6D63E0FAC250C75D7D970ACD816C86F45E6FD30737D0A72ED5FC7F41F0ACDF3C8CD2E99E07D66F1B2750D730EBFCA676B05E3AAF07CEBF72C8B2F46F572690B15B2F37C22DB43D85D3DDACF6AAE1D0F8739BB17611C522A1544167CDBC352E1D7D7EFB11886DA8437955A3D5775586E85AD437923C2FA16F78204D381145FE1DB7D51D112F79BA46FD662BE213DF40CD284B415G6DGE6GDF00606F50DF6E7E11ECFEF5
	37070A89C4FD4622519B057EF84B30EB9D7BF598ED91719D33D79CBC13G48D97442B7737C0E730C6FDC5B93E19D6F242B3D5E59683057AF9B9D76D8A3608CA6878E5FFF0FEB081EC4B7392DE0B6F9E384ED1277BD6874FB1CFF7BD1093A36FF0F3A7E151E47988161B800E4G1607DFBF661189380AF2346B6B4B973B5AF5B3DDCE95ACAB5C7F550F3C3A4B1157FFE91EA1900E82C8A847F51A2347447B8D485A0B3B3337D24B19CE73FFF05EEF40FCE189DD47E80138EEGFF00D000A800840094002C9F50A77E
	6B51A7D8019CBEA11C5F61F216D351F2070D4B17BB5B3BF6B976077328BD544B25FB8F0AA9DE3BDC3A77A074A41F2705B845F26C7BBE61FBEEEBFE44BE6D89BB99C7F673F0A28DABA76278CE63710C9EB9A0F90D15167198BAE59E27721E436A1EBF1FCF9C86798281228192GC8D9593E5CB93C2DCC85FC96C0F1C5222A862023825B10C41D239ED58A75681CC187EE1D007B065EC9A46C1529D8416B66DFE9BB0EF8065B7F2DF89E921BD796775997B2F33E22781AD52EF3202CF5E671E1EB24F3FF4EED778DAA
	11BC2B591C5D529362BE035E4183BA9A869D84D4603C1EC516A68C6223B94D1543F2B4D3A0BE15535CE21223D12EC8D4952EC01DBF5535931E2CCEED519C06FAB6387C30C587E5218D201645144991B452BD8E824132721225CC41CB1761DE258DBA71F9049547DB32F35C349D65AB18DF5788CFF226A12BEF555D585699A0AEF48536F5CF9C14694336DA74DAC99FF6A67DAE0724C9DA01EDDD9D6D978BD7A62A4A819AD6E23BBA33GF9F5GAEF0A5364DFB8A303D0CDF09F978D05A5CD56047G152BD01FF775
	137B3E44F35222BEE0793E46BD6A667A3A1603ED75FBF63534EC2E5BF3302E6D604E464606468616FE6E409A8F691BF66DE97EF957CE35FC8E68F77B86447B46D8BB7014477B0E6998F3155C18B9F954F4CBE45B1DCFBD5E67A587425DA2F43FC0772AC469FDDE94EF9361057B3C8651E6BE901782407A1BC76A086FB7D5A62A34G43G01G11G8915584F9CD9D32124E355FAD3CF0F49A874B6F4898D072CEA13398586278651F56B42F7813277BA0E562E4ED73ED66BEA9B4DE66FECE39AB51D3CEDA66F704E
	B65BF792DD27D4CAED23267874CA29ED7E3E83ED1385F11D2B319F0C5FF92A7CD99FFB9BBD7BA29672C9DA0D7DEE964525C338E08D621A68D82A94428D9C37080EF95AA1EC6338C37C7CD6BF0E3B0A62A2A19C3F86659D225F65A7C3B813634EE65F6A133B2FA7E1FF1E4F4F2FA1F452B2A44B60CE8504B3BDB88F397755531D23E47D392E907B0D7C35ECACDD8A4FAAGD2D67BDA321E767802920126F52DEB3934EB456F2B4B97BB7A70AA81F72ED1B2BEDEA96C2F6AF52D9FD3C77D5EC13EFE4C99B51DFC7DE8
	BA656FABD8BDF80D2E2D472C15568FF676F07CDAE97D9830E27DD0C0DCCD957A569159FB75BC8153DD169A3A005F22AFDF4664BB3297658B815994D5589FCE5E0B5F67962C437E30F49F21ED02F0A700FD9D7653D95F118FF3C65D3FA09ABC897B13FF427D49811C47BA3AC7A28E4249G196BD01ED2AB5296D4A36DF774FD2689421DG76EA6C1F4D833F2D0E1E99C09D85G0F38EA1469F3BB6AC8399EE53A086E3D2B02F0ABC077FA243D66B84A6F43E9A751770CA888A7G24F25A0BC710D6D90334C159B4DF
	883782F45770BD07B93FEDD96BF3F9DF8BBC22EAD02673F3312C69DC260FA9ED5986888334EFC05A3BE7207C668DC89BB3135006C1B886C041E9EF7E84E953B96DED33E93E9BA18C503E9147EE9B2723ECE64005ED44BC4E190E32A5EF44BC1E2434C51B605D9920E9936AABEA8671591DA68B1D43E07E7B5E11A2256C39CCF4410171F8AD8F656C5C04FCE751FE046C0F8E5A04F5214B30D8BEAF3AB23859E497FC7F29BE7CBEB10167F70795F1F9359CD70F385ADA544357FCFE418E3888CE5BB3424FD028C5BD
	C4D15A12BAC88B50DA07FA38C6A95603F07773D4ACE3F79D6659D00BE7D105F0DCEEAB16BBF7B362AEECC6DC8D475DE0C61CEDB3726D2AF25D7FFD320A6E5B580C7BB6DE2D423E29221E6D5BE802E787GB9DF174D25EE2FAE7150D65A6A45FD53A3CBC56FE8429CCD62FA54219F504654234CDF56208EB36BD107069A7A1DE7834881505900F688D9446DG3808864CA3F5913ECB24B6E09E8B0B289D9AA1ADC0EBA352FE7CA957F9A36A6805D657392FFF713BF8C39AD9FF9D854FF8GF287A31B7B7AF98F1F4F
	ECF4B777F56F9E57392F4375CA9EFFECC024AB213F5EFFC8520FD732F74FE75A246F1EC416CC601D3F854B325BCE5E5DCF65DD29E5FF5DCFEDDC1E1B6D2BBC2417FFD7E2B930746F5ADD479A3BCFF9DCAFFCFBC26BFBA9142DF68B0EA55FBC06EBA4EA0E6BF65CC748F10FD1DCB004A336609860DEFAC6DCB40493B8AE0E61A6ABD4A99C374B684DF776A4DFCF7189DB446705F4FB45E32CEB064C96CA032BDF8F70790B3C2D4C2FCB60D989C0F4406A7ACB9E3F73EF59AA2E6B85B6117F2E91763D086B7FDA663B
	A617BBAE883FCE484B36956B374D02755B9FF031DB314EC6DB30FEE7ED457A7D96252DECCAD435GE81B507F1F340A7D0F3D1326D73BDF8F79F775686BA1CAFA1E07CE5B25D38F1A05FB22272DA7FCCCC70ED8F5FC5DE5667A820EEB366229D82CECD78DE15906C15648A6AC5B2BFC9DB42D894BB6175E27DD5A8C63D7GF5B37AE5284B7D442381CBF71B17697EF14A6958585C0F2EE7981A25E3760BA95E5EAC9D33FFF694F5638BF1294DB817F0E003FB3B69B60CEEB7638679F5AC5B8657F52C418D726BD8A3
	9B446BD84239AF43A8EF4B36C4156FB634656343E84B68EDE84B0F58B95E904E5F8E6FFC5B11769FA7F08E2DE6BB5A77ED17FB1947E6270F3CCCD729744E3ECF52B5945BF629FD1F27F81F6DD27B9ED672FA8DF1693C3C9776FBA7F73017691EB965F42CDC967ABE22EE111677B845F736C84B1B4B5FDF8C9017500273E7DB9AC89BD8398346A3GB6GFF009000289D68732BB7117E575DBA4D9E13091EDBCAEEBA57680D62A59BDCF72DF5DFE7AAEAC72FB36BEA656B4C26DA57BA53D0ABDFE736550A6B8C436D
	9272A563D0328390E977561231AE6B23F6E80C1AAE52EC8B7DFF73B0FEEB913F837B7587797763699C778DDDDFAC5C1928AA5B093ED5B4C2463815BC3C1406495D4EACFC53C9EF4F52A1F90E4D5F9E741A47D8661C581C51D20FFBF2DAF60A7B6A3FD4BB660CD6893E9A4967FABB00B6F0A73ED76DE912FF2F02B7AA27F54F2CC19C3F476F44B62F1176F5393B92D545G153B304DDBFD9C5B3C16DD3867276738F44F4FBF1562BDBB579C773C6767496352BDBFF5CEFBFEF2065D6D79E1B27D4C5BE15FDDA87FA6
	7A8E978D61A9GE9DC7E1BB8EDEEAB521EC7E9EBA02C86B034A26DD343BCDFCE7BBFBA7F908D61A9GE91C76B5FE37D76EEE240D25E75754C0D88DE0580DED083E1178316FEEECAFDE2FA5FBC35C359775A62D0E6DB461F76BF6FB57F66E74B25D39CE6902F6FB2EBB613BF18D798E3A1E9433DB5A665EC771713B25ED6ED63EAFC381F1B5FB305E9794F8A77792AF536DBF65F4429D26F46DABE40F343C9FD3FC789EE9F94F616BEAD190D75C16287ADCC96ACF4CFC521E6A0D86BD69A5343A2DBAA317AD1AEC6F
	43FB74C43FF7FABCE776F40171689AD79E2CAEBCD5673E6FCB2CA3ED0350779DB171B4AFD13CD9E331559BB4D6AB473FC63FAD677B395AF4B6726D3AA37EED4A4B5AF7486CD845C2DE99F524AE1B860C34AF935E01F7BA747D3368772BBB46DF77D9634AC3187399A173CEDAFD3F9E9451269F52897BC8DF9E41364D278D5F5148FF244F375A9A98FD0B69B05D94A72479C109FDCAGCFF3D0C95A7143269E334988E65DE55143DF1A08259924E9C200BE1C27D95EBF47A1130447D6BA37F0B7D42064B65C5FBA58
	DF62211C69ED623E7436FED2DE13C38CBC1F359FE54FD5239CE756C966F3D689FBF8879C7D71DA21DEDFD008753A8A521B5558C7BCD3086F96E1EA6CA3F296905AE488E782946CC55A7797204E4B76228CBFACA4E1BAD6D362DE02238562FDC8C9037B5C1EFB523917155DFF2F73BEA461BD516A70678C516F935671776D4BFF879E063D5E7585C3DE263B5DA9DD6C5E51471B2C2F88226BC50A3D52BEE4A245276C1576A15F6DC3DFCB07B875BE6CB3D77EA415BBEA1FC306F8781D3C4FD9A6466796BAA71331CF
	4A7FCE0AD76E17725F4467FB72A14E3C9F79AFF3D9AB1C325F41AF95FEE76D1763FFB5DD1FAAE817723F01620B5B257CAB781E3FB20873EFC77EAFF8BCF3473D5D5E74B25DD2277DA0696DE335F78E9D3B677EA4ADAF3BDB3760A7E9F9AF33E3F90BA14E67A7BE2FF4DCAAF74ACF8E99B261F77E4FF27A7E0C2DF57CAC657F953B3371E7A97F70133CED0338601F117F65AE73974ADF9C7C8A61F759AFF27C73F368987997A97F85945F740B147F555948DF8DF1D13FA07F66DCAF47FADE268BF0CAD75CB1D6FB77
	52BB61ABBA24659D22781A8EE9F9F326E1F91BA0AE3C031FCDB0C3AAF7412F8E99CA61F7552FF27A2E7C046457722B147F860AEF7FD54A7F2FB3113F966262FEC57E911FC879179F90FDE7813F55876478FFB3076EE3BAA0657FA3455B8EC879C74D66EB2B9017F2G79B7CC774EEEFB3CCCF73ED33A2603E335F7638CA2FF47C1E9F9F7D2FC77C1E9F91F636B35B608D39C44723E15A7A7F7CD27F01E9FBC351DF2FBBD667179B23BA85EDF789D824FC800B8D11CF096D89F26CB6AF4ADEBBA60F2B5092AA20DB4
	5D1F1D5A7DF2A13ED6F82A61592D1113734F7518EED8634AAFGF021GD1AE7C3E77F25EF41D1769ACCE6962E5E4157703F2BAFF1A2C117A41EA0ACF53C87D60BE3EDE1D89F19D07508F1E1CAAA7F74CA1A64794F8269E1253EFF32D5C98ABCB20ADB81C28AABD6CDA16AA99DC8B60BA8F4B71D071F320EDB2697CG978C908190FBD83C0EF6E93338CC8AA1AECDF8AA35092AC200B22D547661E6CCD723F565578AB8AD009D40DFAB9E7787D675A96577FF10A3464829649EF6006C51682DBA6DE63279513145D7
	0E3045B42033C8A817F17BFE328E6D9B8232A934B86EEFDB076F8885BAFCC7B80352B6C13893402E4377016EEA71FE8426131756FB6757EFFB196E1B9AF13AG5DD86BC3B25D9F902A135607B7A9BEC2A72D8F169A54D7B444159C417AB0E911F7F2C7FA196E6DD3CE474AB5016EE570BDA2AD6F25949FF0C4DA5E62A2ACEFB044A93B303C591F497157F6B13D5B3B489EF7E79B886BF33B393FC13C228B7D6D50EE6EEF5D68EF81143689421DG76EE247559036F24819CB7CB0D38B81E7E937ACE2254C37FGD0
	2167E7947613F9AD33C5F7C4E7011AC1E79870DB558B69BBF793502A753827E5913F9F55C74F56390360998E20FC8877EF0C703B66E2756E76EF3C52633AFFE361D6A50FFF43A0DA6B965E6BB7FCA74289FBB7BEB63BFCB7GF2A800EF1A9E573F2FB1211EF30FA62AEA0E221E5E60F7935B0E221E9618693CA004E381120E221E324C6275EFE1DFD0BF6AB9032777E367039ECBD4B5GF49C637BF2AD5E79361F17696EBF65F44C778F50FD1E36E352BA51C571BE4724F5A25D3137G6252F9F9A71E725E94166E
	CFDE26CBBD65F42CDCE753BD81EA03343C97D2FC27C1DA5EC547799AA444A598781A6BD1A517658F225FA5C747FF9EE55D6F4063FC17EF5646166E8AAF53BDF54A69185DCCA708FDAAFB247634D3FCED0F541ED3CF20BDDBA0AE22876D19FEC24C3F5088E39D2367FED49823AB9D36CF967D56AB475FC6E77E8EBC6C830EB21DA17ABDF3E07CF9FFB52EBC0475748677EB9D7B9AC6DF67BF58A03F4E2FEBF0DD67BF5AA03F4EEFEEF07D465B423F212BB132FED78D4FEE003847315FFD1EFF0B67A3632BC29FAA73
	4D703AA6A50F3FF6A719FFF75C640AE79BED225F03921904F743D37EE6CA589768E53A293CFD8F9C259EA230FAFFB7DDB38FB3CA5B0387A8BE52A8ED8F36737DE4B190D7EA4276205AE9AE4F8F70412631348787CFC84B9BA1CA97AB1387136FF67A2E29B0C94F3EE06F20E9A2BC133B1162C9D8E906F7ACB34A7DA7977EF1E6874FA433BB7E9F537C524DD27EF9745DB837D74A7FA24E3F886206FB9D6717CB79A777BAF8E6743A63EF257965D9247CCFD2FC31C54AFF9A67DF81F10196BEC763F2A7CE264541B3
	5F6A0EFF959D5714D8257C6B597EC82B147F237CCE1CA6088B3762BE021EC6B2EE348AFB85583E1A2B69F71E85B6F8E73261DE1B57A92E9242CDB6147D9E3D127BD12C68778E7D783735C7FE879E9F96B84A14B357715B3FE07CF9DFBF2EBCD81B19B9C2FCE4A58FB750F6056CA3E76DEEC73312474D6CA7F76B084E24F2DD10DD3EF767761D3B766F64E98E7413674A1595C27872FE71BB0AF0FEDE7F37CA693EF3EC5B1D361D3736B42BF16E4765AE6E3169F42217691EBF65F42CAE8E50F55A8E1B34ED772178
	EE1B34EDD7AD607338902760756C4385E27EB5FD092A56BE4FEDBB5BB3D4372D841EDA20758188850886C8GC8854882A86C47F6601F0D1E6EDF62A6916DBBE46D4223747B358A4827369F653DFA1F126B6E86516F0AFD63DFEF1A47150730BF11FECF270672F677E37BF81C5F3B6E4FF1AFD2DC8404E339EEEE37B87491AD7A5DE899FF5D9C98D79E2C4C35745BAA8514B70D6B4150073AA998C05C9394D7896126813E9E596F5047AD225FEB7A47DFB73B461587AB73BABB56158EDEE615E8BF56BC516F2BCE79
	2C11D3AFF3643872906ECF24FBBF4DD0DEDFEE7BB778DE5EB00E5BC6477D7190CE663AA999F168E3196877EDDE6F2F9D3BEE9E9BD79E2C4C3750BDD299C487F654C362D454CDB94755E453F3BEA12C3523EE6665B87431D874BBB4FB7CF5F36F3872E0E53EA1976B0A1D17F9E01A231CBE225FAA17FB5FFF7BB2AF9DD79E2C4C4F506FF97D213CA15C76D3E722BF44F21C1662D2A11C45F5D37D09C39F75225F71B347DFB72F0DAB8FD6660769774B8503704EBB08FAF8EF966A260963764D26F393907699C45D14
	4FF168E3356877FD3347DFB74F0DAB8FD666EB69F7590103ECBC4978EE1F61A827DA747B6F53473F4C9F0CAB8F21DE5073201213952AF0EE7BA3AA7407A80EBB076212A01C41F14B683AC85E9078529062FE31E2FED59C97CDF18D906E6038F7FAB1BFB347B5516F94FDA19C6C2023631B888847F35CC514AE9942D99C77BC45953D8C3F0FA30E1D41D08A619A0EFB1262DAA05C41F1AFB491DC9648EC66385AE64CEF18634E2638G8807B9722BC77922B9EE66C6CC1B40F13BA8AE8542199CF745C614AFFF98F1
	67513E2CF8D86C03644D92FF2F977D0E9D99FF9F7C7B3872E0E5CE25FDF5A51439016BE1C7B67AE0AB4775D01C8D427E9CF72C965306F0DCBC4505C3B8166376EF43348A0E3B156252C878847F96FA9B72AD66B8BF0A2B05F0BB471550B3858C90766338A66AC741900E6238CBA9AEB7C5210A67383FD3DCB204B3B96E6BE6641BF792F1B7D1DCB904EBB82E62G6D33DE853DF05CE58710C78747BDCFF1E6887BF15CC6FE96EEB047BDE846F244F05CDF0FE0FED3B86E13AE4A9742D99C37378B4BD1B00238BFF4
	A10F12916CB7662E73EE3E608BAF53351FF2BAB61F70BE5D6B51B0A21DE778AF45370EC867994E657BC8BAA0AE066BE083BD132154C7DC07DF2FF1545BF7C53F6DB563DF07AF7E9DF88494B94AF4056877522271673DFEDCF9B01BFE7A9931E98D583445876DFC72B37475F60E3B1362FCA19C6C435F3FF6FB67474B3DCC57F54A69181F676E217BFFFC247EBF0B6223FD247EFFB95F579287F165E770F34876B86DCB82FC6899E3D9C31927164A9DA5CA97AF1307F087995D7312FC06F48EFF8245E71CA11D431F
	3B8F6770734ECCD499G58BC6D1B5B81A54C273EF82CC4A9398332C15F23B3DA75A6E3D9653A5565A52BABAA04B308B699D08F8910D75ED9386F64DC836EBB29BD8B770D5CC1E96D908EG8863345777609E13980EDB5D0BE72DE472742F2C25FB18FD93D5CDG1D3E28736BDD56BF4666AB37FB19AE6D1453B11B1CC7775D58FD25BEF689457B7ACAFDEC3199F59BGF1993C3C61AE5F22371F6D708F43596EFCA50B6E5B71B9DB6AABB3681E7F4033253EF2219DFDA58C62724FC17EDA51F7BABD225F9F7C8E73
	0FF3FE07EFF64EBF6EA8D32068776747471F771A5F01C75F89C719CE0AFE4FB8B17E3CCB4615876B07E650F9C0B3786B67539117CFF1FE00FBAE877D7BCAFE96C7A860134E41366795FE96C7613958769450BDB4AD9056828C1F0B344A1358F6851E0B7DE091BD17A57EDC362F0E157BAFC38EBD4709FE6F9E9AFF1D9F9DD79E2C4C3B0F234E3F4DC65C3E6328733BF39057CF67ED491D60C9DCDF33C6F0AC217443B666EE0AAB05F0954745D21C9A42B60E5B7E1B3D23A1AFD77DE9FFB39EB9CA57F3C205778E2E
	17C0BF7E5E467592454B1BC33FD5CD02F0BA474D26387C73E09CF29E62AE23B2A7790DED9FC97A98691C75B21972986B992727FA1F07B087C26BE28B142B73BC544B04132897DFDE5EC12A2BB088C7F31CAF45A5C1B80B63BE6136A43427E34BE819740CC7A967F13A673F4A69CC183BB2233F94CFC03DDC42578FEBA7E0F9B75239A1AD046D9C57CCF101900E6438840FF7310C95888FA9CE4BF93C791B70F0F74EAE9B3B5E51CD76F0D9CC83D6363F7B31EEEC4B27CC40FD4E9FF793F98E1B8CFDBDC62B308F3C
	D00F6DF88A17778439FECE3069B9225FF3960CFF1BDDB62EBC183F4CDD485BEC5ECFAEDC08ED76A73938D72FE51B7B7D02ED5BC65FAF5831CDFE3F6021ED2E7B853B36496F976C59A65EAF48F0961A2FC557856FA562F3016C5B644E8592760A6CF5760FFF6F45770C8C6E9F1DBF491CFD77936EBB4CBD1F5D99D48C4F8A00AAE52230B77112DE24E9BA5F5D5E4477FB89AF1D11A8C8AB78DE3E853F7B6FB89FE558EFF51541E8457C8D028C3E708C826860B21C55476FDDF1AB436BFD2EB2FC5C07B244C2BA06BB
	E4C75DCC61F2BDF55CD52EB779DD92A902DCD970AC70CFD4B5CCC5397E4B77ED16793B13CBF352D5AE7BC92C3FB57EA8C36C082B8CAF707BE9DB7D198C5D709C866866B224CFC51AC037B234E53B4AF0B41B1F1F600F3AC948C55DC4F33952E6384A151307BC638539D26019C9644AC6390AE6A2CD41856E64BABE53D52ECBE622DC259770B306BFF11561FDFEAFED5585CC06D6F8EA817C398CB3E773BBC35C4A50B35BD5865F59A8C3G17616E69248EEB8C7AAEA339EFCA09FB4172F7A8C97E145F895568F7B5
	7DD1C56A77919354F08B79BA8E3F5939032E4704823F884E335643B7BEBF51351A8420CD3AG67BBFE5C2964FC1E3612454E9E0DDD5F5357A3BAFF649FF43DA46F42C4D56105E877C59664DDFBA1BFDF44C2536B0D127483143693688C1CF61A87B917D0B90300B61453CF3290FA2B4E20BBEC53E91B216D35F37A3F507C7F877F9238FCF97C0E71E41E47FA7ADD8B4F43266BE1E77936D9C9DB184169D2E9780849E8E3F97D1766253C885E7DAFC23D6D5925647C754D3CEEDE04F984B74B5BAC12523682DD47C5
	A86B07F47DE89842FE9C37EAA366994973BCC2F1BA2D5EC6BC0F511D60F4F1C0474A7340C6A2FF92CF97B2A275C3067F5FD4FAD7AAF4DA2CCCFD2D8E3F54E60F6E173E5972E58C266DC4AA704F607A8A5A6D7033D7EB657D6CA33AE6D3981028AA89C0BD78EC433CDA8230BCF15B5C7B4E4B2CFF84DAFBG566911ED2317651AED723EFB974DAF94720A607C57363A675FCA57041200B615539FECF26F3B76A6745D420B314C77B771766CE24CA323C95EF7731B098D2BAE463256B40FDE567B1B1D7D7BB96A138D
	10C76B45E82FFB76B87C7B3E83A80F8F17671F8764ED2FBAC84754C09742E55AEB76A493CB17C64771B110A61E6B615DA348B71373DD5A65DA8756F5A1DD7EA5D887CEF611B216DC02B2CC6E72A4839BEBFE34CE49FF67D6BBFEDF30EE7C473913461587531D3AC63E9D28784C399DF8119CE52DE467B9DD76D90907FC4BAF91FF577D78E772BE719ADBB7815A26CB701DE2669E07FE670BFE876F9EFFDD5FB12EBC587BC3709EFC27581F073809FB701D6276DC44AD235F6F127BFA3BAF417757183D787E6A4F75
	F576BE020B02F0BC4785D0DCBA048BAEC55CA23EE7B77E925771CB148C6E54756758FBBC9EF73D88FBF88D5E2DC934F9194E7F145331B503E5BDF4AD66D269DA46CA0A2F39D43A16F18FDFA7EA0238F0EE371BFA447C8B8293D52501E3D9AF2B6E45F4D582FD0BA8DD27CC9ECC3EFDD5745B5FC0697D021D946FAB42B339D3691A938987C2DCE6A04AFD11D949E589927D7E52BC7E7538FADCF988FBA368D834E4B60C57AE63EB7B7CCEC51F4B7839G748C57F08847F05CAC0ACBA569AEC7DC60287B04C99E27A2
	E70C081E7138E5D49E27DAC727C9FE67A8B55F90FF906E635D2F575A3A993E58BE4B01EF5289E765B139C23C7CBEF83217695E70723BE2DF3A46D1F63934CEDF403EB73EDCDA2797726F0AEBA1AE145BFA5AC9B17F3CBFA42A0A7F6059362C9F7DF1087447B0BC36327756EB781CC1459F58FBEB93BCBBGEA97627826F4E474F15FE5743BD4F6C34CAA81D75BC36C456E125477EC516B8C50EDAB59FD749F0CC86F23A77CEC40174507BA1D8B6CA748937187BCBF62DDAEF72CA0F792BC538134DCEE6398646EBD
	60109B6F5FD4674CC6391BCCD61BA0B8132BD6A9151B704B8BC2391D4FBB769364A98E626F26A7DC678E0A9DB68892ECG4F8EGBFDE1609A711469664EE6E401F3ECF30322CDEA11CAB59B80F5B00DE1E5C5C60D41EBBDD6CC0F87A71723CE4141607448787219D3E63636F88C176B8F8A68104F359EF6C9D5D8EB32B9CF640BB1F1FDD456DC0AE2AF7127B38C5AAB7611741656EBDA2151B4467DD01F6F8251B54298A4E7FFB3D73B7F633771170AADE8F2F79896BE1B164213E021F7D416F6C723F8257B16368
	1E02D888A781E4F05A28DE1C7F4E3F9265784AC2474716BE616E024B797DE88DC0B3FCA526FD115F7795F9A572395BC63F6105F06ED560E3D7A16D0DFD482772AA64D3580FFCD8F8506EDC3E39FC1D56GE9C2F9DEBF8DA35F242B10EFA5ED138A0361FD8820A1185FC94657E9BB02116FC5A7C9780846E055F1DEB97C3E01C0200B676937AB11D76E9F1157B7D369D88742EAG439F11F6E1B672727DA372BAA3DB4CCBB87FA557391C5F67713306A0DD864F4F4A6F33AA3F9AF96FD491DAAD040781822FC65A0D
	B311F77855487B46192E657CBC9FF925GDD71B518DE170F3C5457A02F5AD904569F4221G511C36E2B6721AF28D723AEA361897FB2FEA582B64E50E6EA3F5DE748D2841E4D9EE51E91136EB1F0336557A05526DBE2495959565A5ABAA178969A66CF724FB35F7AC6904FE4062D91EDB5A9D795EF88A72A47C64C867EB7114EE550A7212152BD01EE7AD59707CE185267D426A6AB7FA3A670671392B4341670926AF25FC36180C363225F5969814AC85711804C86F536CEA2B4F8FB8725356973A6A6610C1F37898
	232DBD68207D5F063117F15BC657B2854A60949B9D651C546960755EA64FB6FBC46320ED59B6F6DF38F11B67FC538EB9727DF5CC7932F4F3340EF4CF1EC23A15BAC73A8DCDE3576FC5CD4EBE24E8F2556D52E607EEAFBD6660D3FA6054FC6819832EBE749F03A33FB5E64FBED4ECF4505EF3E46CE51C5E65DA26D699DCE017A37F5B68FC4FFE1DD5186FB9536408DBE9F2E7FBB616582366F730063031C4A9BC2BG4A3E46B3401F5BDB22F4BF16E88D914F99CDD86298D32C91F0E7971035541DFDBDE4C4D1EFB2
	9AF50749B771148647968F6C130EAD88FFED880EADEC7DF27176909C1B7F58C34A4CFED709FE5FEF987F775667471587FBC73D3F47F57E692FBD2E73CF3B69F8075C8F679F026F371F2FA1B8F2BFDC88477DE1AD3E7346F29DF650BB9053F978888D67CD4470BBEB47D24E74906F6624043DFAD5E361616D1C947379B4FE6FDA79C466738D70EC87C87996FDDE6D718E9973C4314FAF1E6D707935822EEAB1D9335FECB1E93407B5D65B8EFA912DCEEDD26B6C424B8D736BCD03D23FA6B278CEC43FDFB6A49DD313
	78A0EE138922FD0E170AFE977F8EFB9ED77F8EFB9E0B791E476F79DE0EAF791E472D39B86EFB183F330793BBCE44315C1C13387EDFFCAD0E7BFE26E7EE2BA1ECG703D96E9599D8B647DB978DA3C73676B32E2256CDD2A5C10AC6D8B7CEE9B65F5F8F661A247595E00B3DF07FC8EF219422EC319C2A9EDB204B3818AC211F6004BD446F1F91C17FFA872EA60650F85DC962775BF013CAA2FC7DE4FD1DA8304FD81022FC75A07793BC4A4473DCD771A11FDB2E99C37105F4D53BA89796F61FB3A7DG97B389E92FB4
	A37F4CC9487FA5CADBF1833C4F82F45E003437D910171D63420EA02F249B783EF817337847662F37FB19AE4DA9DDE118F37BD686619A890E4DAF5550F305DA432473CE0D945F91A61DF77A9B57E1B744450771BBBAFB457CABEE04766A46314CA5871F727D62425C351769DEF5CA57AE1151GE11F9B657474C39F5D27FF23D4CFEBA8BE64C6291E7EE263F59D628AEEC2BDCD6E937337815E6F263168E911177311DFFB196E00D33AE0090C91900E3DC9CECFE1345EAAEE126A69D60ACF39C92A27EA5EA624C31C
	7AE6FE170453BCE6946063EF9E0B1ED427FCB72220DFAF5335BA25CB16481881613CF0B9BDDDC967AF0A4325FA3A0E624B4225FAAA1F0AFA2A043820F0545362ECB17FAC40975CB296BD2D10BDDBFE74F41B3CCCE7F7CAD7AA11318A42AD3748696981BA5752F90BD4CF0FD13C6196291E54AA54139D6212EEC1BD554F94732F1D8C3EB6F9ACFA7AAA5F3B722E70B25DD127F45D929907A19CB0D9CECF11F41EA8F432D4CF77D2FC44E4291E364C6273DD90D772A754536A59E27E3E008F7A13E7BD317C06543C3D
	837A14BFE15F78001A0FEDA230EF1D35170EEDA0ECG700D607BCD763A5E357E9F3A5ECC769084C7E01EB777733D119138EFE3083ED3645F1A28AA3A956720372C41B1F4432DEC8C5D81CFB3G39B31A0D21A72FAD71D0B63FDB45E368FABABFA93A8FEAB5BBA37A8FD562F96C16D0F6A7F5B0240D3895479E057CEE51B840A9EF4332F48C60BE122A5BD0BF93A82D9942FEGA13721FEFA6D2E7A699F4431C7944F3313FE83C37696255F06F6FD5C651C463179638BDE26ABF4EA3F4BEE9F6B9971FF23E34C1A5B
	25FE7C9C45B75DAE7563CEBEFED5C3DC546D7CAEA45933EA0BA3199C95117272B448605B81E7G7009142EF9A6FB6CDB83A5F905C522EC1F1F74CE275FFB19CE6B14AE5ACD59DDEDD1C44F3CCC08145A62CB0ACF0A145A62A6EE0BD408EB39834BBBACEB0B08BB189C31F7484B2310412781CEF9E7222A70CE29ADE6F934C5591D4EF9555C0932ED73523F5B3DCCF731D33A56BB47EA0B6DD4375ABB2536D0D33C79CE29AD7E4E47D54390174C4B9B6F648B8DF7C17BF957D87A41FF49FE07E590527A884FC0C9DE
	E1B2F9C783AE81A065AE298D97FB7C36B7D312D7FE9416295D6C1DAD0EF8196E1A7FAB6FD800232A2ED071D3CDB5D43129D28795DB98539ADB0A8442C73E899089B3549ABF08859595C61C21D324345CFCA0D948CFBE62876952C9B58AEEC2C0C040A141A4A410510584820151EC32AF1BA51BFD4B6E5BE4891B58F36FFB773C77F6B74902A6F526193C37771CFB4F39671EFB6E773DFB1E0E4EB8AE5CBA2CE1EB2AFD63826B70A243170C8B2C4397F12DD58DF1B1D85E47C21E755D92AB4BD1207C9E09554BE50E
	8D16538AB89FDC913101F5715B665EF4B3A2005718D83EE717D6CF471E78D8F94CD2FC16B05CA2402D40F4CE3666CE9F8FEDF9BCAF57314B8473BF2989AF980077FA168F40FBBD8BAE73FD227771CCE672E53ECF748526DB4D1E69960C576AF01BC4F1A500B30F67BA7C16612CGCB28433DDED5EFC7B46199D27F6BF0E13F66A117F906B7D807333D41BA74327D27DBF4BA7CB9430DGDC8C6A70E9061B8E708254616FB4FB140FE942A58330DFF9FEG76ABCB3A03F5D85E9D2CC39B5B57DB2A53619006A393E0
	1DB9817D9EB1DCBE407BA6F09D0EC8D175B6DA93AECE69FF9D1E6C57BC64B29727866B70C0EA308E6FE06F8FD4CE506AB01A619AG67C29D2EE438A1G5F05BA0CB728FA1B2F89579B7ADF071EFE4DC3AEF37D06E09DFE3BA1D8070359FB9123F4BA3C0F6126GAE89F5789243AD81F8A56AF0FC362A37E91AF0F5F67F6BD06857BC64B2D767F09D3E02E77FCF65F09D9622AC0302FC5C04D7065B2F13EE160EAEB3AE5CB952E036B730BDAEF00EF4AB439744854E11B61F41FDB1080B0A63757E4F332164DE2348
	11B7B15910BFD18E9F1C287DAEDDCC10FF97FAD5ABE99A60D71A28AF0B7CBC7BA1769E323CCE5F345FC8FAFC163EFFCF51EEE3B17A70F16E47E79240F74AC43E069F3B9F4F21CC647B82CBD85A22C94906CA389AA671FD01B987027785669D647B823EC91C3E55447602G0E07EB51A42E2B772E737B378557C9E739E6BA459F9A5B97D9B5A950B6B2182F1C744901369109FBAEF990A7CD66653D9A729BA1CFCDD63EC1B839A75F82026E9BD0E40A1CEE0B72DBG3FC726283466A93CDF5A4546D66B942DDF3685
	5D84434FE9424D83609B793B81700DDC4B6684FEA8F3846AA1166122G0EC15CD9F97E8570A24445F772677EAB913714615EF9BC59D074B8479DE534E5GDB9177B7BF2775A1EE9BFB3EB89C609844DD8DD1A7BBB4BE2ABF5204E38620CE928620CE6E43BA190EFAD80BF512246A70AA2F13A5085B00BE3FD7A16E13AEDEA75B27F25C5DD7036BA49F6B2488531579F91D54A2EE946357B2D5DBA727B4ED23DE93DEBCGF371B583B097DF0CF371049474CB0AF3711318EE8B2B27C1201BC8545795069B8E709844
	FD4EE86381DE06385B990ECC03FA1A46F151EA1DA0EEAE4355826CC0DC2713658B708844950B1CF68C62CEB35CD3GAFC35CD8D7F09BBB2FD957B7EA422F8D409ABF7D87710152FBFDA63BF99BA353399E8EF9F89B4BC2DCD4ABBB6383F09962163772F6F78EF1E3512F2983F1CF5AF83B0BC45C7E6660F6B73D19530E44F4CB1AF93B0B1F4E678F6F5507BE23F9201E3EDB55EE333864E7ACB5AEBEA7F996E9EFF451B4AD1866DECC338C533CA50566EF12747C4BA4CE3B86E95BA5BD7F88CC13B5032709700766
	BF5A2F67BF99FD0BEDC75AB77CFA7E99DDBC4DEECC53CDC2733F176879BFCAB86D9124DDCC747C5F44B455186657192179CF4B5473CF4E64349711F623C14FFF270127F1E01A282C507C7F182567BFA70B537A11769FD9FA7E1B33F91A0899781D94760CEF788CEDBB3F790C5A36A3B561F74F74FFBBAF6E57BC64B2EF252FB812B1D0666928074EBA5E5E1290F78F432D84B8EBA647ED154F7781DC0238D96D1CDFAD628ABAB83F8644D5F6F0FEC3E672792C347F7A6661BF39CE3A8957CCA74F534F31731CD1B3
	83676F96069FB9B3F07E7EAA2E0DE2A0EE4BACDE5E8587F97E3ED941F3750890B8B91F67597AE7442C4073272F486BA28DDE4E7FB243D3F88A446547737C53FD2ABDE7EB42F78F40B7AA9F6957BC14FDD276CC7996A8EFD4BC4E892F60D802B8BF1B1347837C9462766532F4G2FC15CAFF3B9EDE6822E8598EEB740A508FB000D9D55GB7A4707E67D8F7683E2B15687D87443339049C17C4C27B7AD84A7ADE89F88FC27EED7E1E7DEA5C44460BE8C8BBAE011FD9FB3E336FB3EBEBBBC37B7558466646CB00578A
	4C7F2CFFB37C3608EEFB276814AC0ED2AC29CC637273779F364F66B264F475AD03290B5A0B5E4FDAB99B5B68BB3F1533394F0D5758B72C647882668F7F9C44D9E77377AA0CAC7FC81A9E735F59EB7E4ADC0165958FB48B106E303FEF3AB6BF17114C61BA1A5C955A86F68558C00E245AC0E48FB6B00A59C09E705E0E7C4D5DEF6B6A003EB6EB96BDF6FAG004B550A3E30AA116E216E1EEDE792CB6F0334C3667072CF6B357C4AFC419F5AF67E4E7492833C62B07F214E5EED67E1A7371D25A8C31558378CF6B158
	F686093CDE364C6536F3462B5ACE137C4D8B08AB1A4B65FB2435EF3D4EEE45B94DDCEEF3EFB37F5591GC74D65F2A7766ACBC526DB4B66455301A689691EE96E1BAE5B464B3686E53F64521F837DFDGFCC152438F8640CF7A75700EAEBD9CCF74F0DE0A9EDE1129078F9A7470DF327470A759FA782F35215B4C6EBAFD1B71316F34493A9ADA5737EDBCDA975A5FCA821B67E4A5A69B5EC964BA5C5DCE6D53ADDC768A9E29C2EC72FA4AF57E4BAA5A715D27C4EED377F628729C6D683B6EAEF770FC2B9379787C4D
	814AC38E5BB461957B7BFFFCB604F57E647B0D4F2F313DDDFAFE4611086BFE939FE387219EDEB4513A9E09706B8C0EC7384394CAD61A7E0753C7E448BC2E77ECCCE81B9CFDC5EF5313FC2A8D6504B1DE5F592977CDB51A4DEB16029ED622AD1F4C5567714295B50F185C3E73C84A25677B5BACEE1B5D491F9924B37F66F2FC2CAB384FCDE273A1BA2664BF415B47C7FE156624FF876971B9438165B823125709AE15EEE2E778F4ED5DAA5D3E307213535E6CB7927DF7E1EB681DFDFF546214BC2653D547ABEB6534
	973B681949BDFBF91E115F29F9AE3B9A3E2CFFA0AA5D5AEB504D13A9AA5DEDE165271CAD766B65FEB3D565E36A8E1F4FCF3AF7762E2BAF4B3F2AAD278E2B14F31B69EABE77DFC3B90F2ED369C4E77872CDF506DF173F9075BA31E528F97E2CB57CBCDFE83DB61D5C1A29667330ADFC1D8C56340BDD4D614B17EF531773E5CDBB49EE6F69BC2BF2FE39BD782C6CBD9DBD51A8BE12BF27855C877DC04993FCEEE3B6755D6F3CD9CF53841D7103FE2C54ADD86473FB46C26D79BD87702767A96F42BE67AD0DDD976406
	F1C639D8BD237C320B92FFD6447534AFCD53C6CC465E742DBCA7154F38A5F65179D98FE75CE8D114671EF505D4CFF4DEBE6A89BCB3DAC847202CF96A38E604703EF9FD0FF31581E9AE826C8A40A95F64E1798E013828F9382743F051G47A3EED39145BD8B70B244F5B05CAA8AA7F1DC0D190F675B9157FD0A62CAG2ECD626352B01607436E946A5CFC0FAE1A61E8FC8326DBD8C8F55281E9EA24AB8E21CEF45303710A9FE6CC6F42749F9769473BBACD1FF3FB11FE3C9C49649F843491F0C933A86E12195BE69C
	601260DA81D75EFCD89F4CC759CF850F091D1ABEEADA215EB7C9CE910715FEDB53379CB707EB17321DC41A7973F1B34893370CBDF7B4EB5B81657BD338CC348CACFE35AE1EFED0BEC5973F7B14B61E4E3F5FC85546FF230BDF2721176DF418D93B2FF68B48B5FCFE6FB62ABC4BB173E7C8EF60BBA00F1A79B3A4DA06EDCCC78F56DE0245E574F232CEA27FBAEBF7CBDE0B43782560F098E5CF5DF421E024BD2C6890A4127535CBF09A4B8449E2F7F8G2C11C417117A8A32B7C1ECF60DE4F1CBAA1C43220F0922C4
	3FAF6E12C82E1CC00349283340524FE913487AD3DE41ABD8A1D0814CE9A063345769E4F19914110B628C74A375A0898D2715BA9CF0BFAE6E14483A5302C710A41249842C96ED3AE0CE0D37314D8E998BD207C0D7BE0A2488EFACF7DAD5A833426E108477A123C4D20F0BG5734089DC6165B06B23B47E5111ADA28G9AFCA0C946E92159ADF8DAA412DE6E3303E0F92705B631DDB0D63845B6E3355D8328749A41E96522FB5CA4DBE725B36E7F6C25398B4FFA52487AAA72AEEDD820D7B0DBD87EDB4BAC12652C60
	26FDE285B138C4BBC4823E0A24FB584FBA8FED4F55E4CB73E9F921A5D8CBA549EDEF74CAG102D55B427C22FE4F794D6DAC0C83AGBBDCCD521D16B62836CDF2344362349556G1553F658C75FE4C9A9AD21EDCBF6488372EEE672D2AEA66432F9C26EFFF61ED8B2AE0D24D6108D8E98E1AAACCD12682EA2862905AAC4F4D8FD2E23B223CC36CE23FB9B69406EFDACBEAD2C097561DC68D65F2FA23217E6D700A97B5C241025247D1089D3D83FEC3138A1AB41DD851785324C473841BB248F930FEF3B18CAD214C2
	DEA65D645FBE303F754D16B63B638A844AC037224DC3E3BE843003390C8331F2D9E6D499268AA80F74747204871EB932E299B02BA06B5AAD8E2FGA26529293E7236B58A6E1B2207FD30712EC537D109DD34381C33818C5F690145F29BB0B7861419C7216826BB930A060DDEDD4FC4D7C9212D4A15842405B6C1ECABACEDEE067E9E99C00D817B8F10BDCDE38AC83349B7F46B09BF2DED848BABA3E9EDC5C56C3E075D0B69FD8F436CE118BD45D0EDABC82FFF0171BEDEE6E6168C78145A100F06BE710100B97A49
	CD4AC35BEE483BE32FB7955A932D9A072B45D225A8391AECECE41FA1202EB8204565298AE034E03F29CDE05C6F2B78A60A17E3EBDC16A6EA62862B455D4AB8D0998EDFA006E6372D11E5GAD77C245854816C1B47A0BEA32C1EA9120772336A611EDAAEB33855A1C92A1378E32967EDD6C0F1514796DDD8BC899C7BBB166D34DC37B8D390724BDE8E913E407EE01FA988170AD1C149915D999748F4C29192EB6E9A7A8BA212B7A5AED073B05EE1E4DFC75B765778FEF1A8B16D2C5B60AEA8413AE8368B7854F28CA
	EFCF7EF37437E5DBD2C9EA95D95F42EC9E945EC17DA8D23A1415F03D8E9DF72DA841384065BEA41114A2383E002ED50D0192E8A2F20DC6A638913AB72FC37204D28A43D5AA6316B5AC080EDAB7B048483A2486E6E9B92CCB22587C603E05228B3005D3E8973D9552DB91A3AAE6774F2E593EA95581452E9B94781CCFD3D292D4D2D57E7F83DD885FD797710424B652615A5E66F540CCC1F49A2B4A20555086G3D23113ADC04AED536BE9615C1DB928BED16ADD35B4094734C2C950D6CCF1EC3E0A7CF1BC615138E
	460B5A779ECADB7CE0DAAAC907F6A1FFA985C6D77A3993E89EEE3919244124C4EE7AC47120C17FF4F5D571A307C269DD36A559F2327E7736D42240B779F4D691A7A92A76AA4A476B47DC08BBFC729A00100F3FBB285444452123482A9B1BD2795CBFF0BE3F60B9F93E3FD479B51255A96F923AB6984B7CC966AF0475BFDC7237F6561F7F94FE4FDAAE153ADC479CD6F73995DA0D9B00C307CA9B251AB68B4CD6AD0E56D2277584187C31AB927DAC10721D1E8967A9FF8FCF23C44AF1D34FD339AFE9E87E8BD0CB87
	88FD7065BF43EEGG400C81GD0CB818294G94G88G88G2EECB1B6FD7065BF43EEGG400C81G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7DEEGGGG
**end of data**/
}
}