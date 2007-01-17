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
			ivjBroadcastMessageTextArea.setText("The Virtual Cell is going to reboot in 5 minutes due to. Please save your work and logout. We are sorry for any inconvenience. If you have any questions, please contact the Virtual Cell at VCell_Support@uchc.edu.");
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
	java.io.File file = new java.io.File("\\\\ms3\\vcell\\" 
		+ cbit.vcell.server.PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.vcellServerIDProperty) 
		+ "\\RMIbootstrap.log");
	if (!file.exists()) {
		throw new java.io.FileNotFoundException("RMI bootstrap log file doesn't exist: " + file.getAbsolutePath());
	}

	String cmd = "grep \"LocalVCellBootstrap.getVCellConnection(" + user.getName() + ",\" " + file.getAbsolutePath();
	cbit.util.Executable exe = new cbit.util.Executable(cmd);
	exe.start();
		
	String output = exe.getStdoutString();
	java.util.StringTokenizer st = new java.util.StringTokenizer(output, "\n");

	String line = null;
	while (st.hasMoreTokens()) {
		line = st.nextToken();
	}
	
	//  now line is the last line
	st = new java.util.StringTokenizer(line, " ");
	st.nextToken();
	st.nextToken();
	String time = st.nextToken();
	for (int i = 0; i < 5; i ++) {
		if (st.hasMoreTokens()) {
			time += " " + st.nextToken();
		}
	}
	SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",java.util.Locale.US);

	Date date =	formatter.parse(time);
	return date;
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
			ivjJSplitPane1.setDividerLocation(200);
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
		setSize(1072, 662);
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
	getQueryResultTable().getColumnModel().getColumn(2).setCellRenderer(new SolverTaskDescriptionRenderer());
	getUserConnectionTable().getColumnModel().getColumn(1).setCellRenderer(new UserConnectionDateRenderer());
	getUserConnectionTable().getColumnModel().getColumn(2).setCellRenderer(new UserConnectionTimeCellRenderer());
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
	D0CB838494G88G88GADFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1543D8DDCD4D57AB8CEC5C5C65FE823160A0A0A0A0ADA5A5822E2B7EAE923DD5A9029582222160A5DE50BB6AAEAE99BF9D1D0D4D4CCD2D2B4D4D4C4D4C4D4D490D0D4D4D2D4ACD4C6E61451E1E61899E0D0545FF34E394F19BB775E99F00CBE7F3F9F9F6E1C67BC67BCAF67B9AF771CF34FD1954E8F48991FD928D20D4FD1897F560DD72974570DD7254FD871A362EA16446629C47F96GCCD73D7BE3B6BC
	1BA1AE7A31F1F9852A6E0339906E0030477D6372E6436F192AF74DC59E102193BE2997462BBEAFBC26F74D67E3937219C84307AD79701C8A5089B8728C06E7012A57D2A460BD85BC4278CC154A3B0C50F8DB0A85DCAA5064822E9920230C51DB61498438F2C30DC057F1C310AA6D4F664CA9A49FD11E88F1C25AF461D9227AF2FC40D2426BF7D4CE122692425DGC479AC550B55D5702CEDBDFDF2F357F11D35223FCB2B57D774E8AD9675C91D61E405DA5323B3EC5830DFEB6E571AF72B8D6A135A862341E254EB
	0B1711FC3F326452FCD42AA448B308627CD792DE1D705B8660338C71B7D492FC8A7C4EDE9E1BD73C9C755B3424D6103F7D6C932A3855BD59531C68D7351C6955315C2E1F4970E6663734ACD619094D7DA0AE9CA086A08D2030BCB62F8AE0AE75011A5EE5702CBFEEEDB07674988D75A653D63D463C5B28516A6177068D90A36076E93B0CE60DCA454A79E9CB3902BC2E0025FB712253B11B842D41B28F5E7708AA61311BB3A7AA58A4A168ECE6CBB93305265CEE9346BB536A0C37E73923CFBA72BE2492730E3A6A
	552C9C855EC916D7337C04FC02C53C934BC9D99750320E065FA49DC94F70F3A93EF4059D4F7C73EE6AB3A4DC83F17EAB50B75A2722ACAD59F72B8AD67DDA518EFE4FF9E72AAA188CC595D2D94E2DA6F9B7D6B01E3B38AC8FD1FC12884FE479B5151104BDD74666A52E0A4DE37E3541CA6AEBEDD5ECDEBB0089408BA088A092A03E8AFD6C325E15A37431D635593A35CF2757E84DAA16F603799BF8EAEDD633FA0741E2D59B3A348755E61D7A38DEEBD189B48DE6E203638E691A296FF7810F6DDA03562C366A0C86
	48DD5D2335EA4D96C67303F9BA21B1EBE9EC1B8EEA39D55DE3D2E13E074DB36129D7DB2C87CC9A35D5EB27E071EAB3A913FE75C14891B2005F4CAEA77B083D52C17F023531F9A5EB518E89B43FDDDA33C12B5F302145E8BC5DE7EAA31A28CCEB091D3F351236238AFE372CC59BF77593DCA80423F9BEB3ADB3C479B46A3A0878EA73A05631EFAAC91AC420D7D5235FFC580EFE53B865E1D579ABB7A97ACD3B75552C32EA66AFF555527A330066CBE46B2E4676BEE8E53BC23DA13CAF260E93D0D54B53B09E4FAA72
	483A487C278A75C90AE3F69EBBC04A4C8BE48884082CC6DB6F3114B05F5D2A36E851DB771A4151349A3B65191CD72C7605E78340F7G73813E871885309CE09D40FA0025A4BF95E9D5D42AADG27811AG368328817AGB6826C855884301B78A8408FG7B818EGDCG389160FE0043GBF816CG50811C83F89A20874088F0045B71D1AADF8268163DAEB62F9C208DE088A090A09AA08D2038A6B62F91C08F608B908E1084103F9E7A8D008EGCF00E000B8008C00528D6033GD6GFF00C8009400420D31F935
	G9AGEF00D00084006CDA608F5086B0D40B3EF4FBE5EB66484BF216A40CB61FAA589D71CB857BAB6501E532D130759621EC9AC4E532C3205DAD1409389C8E096CFFC420D38B6505BCF4C299E3F9A03ECFA24BB921DCC7227B0DA3243BFF04F4CF0F106E7F0F40FC2BEA55457856E4C9B8C748077C4E9361C9FBBE45C99E53A8DF15EA110A79A0E9E7084F12F68BFDF50BC0CB7CF389G69A7D7812C81A0E3F4744BDD829D99F32F9071A03ECA5AA9311FCA7D1FG716735A354FD5D8869560F102E697F83BEB0DA
	407A08C6ABA9EBED3F56E0ED541A34868D7C3088F13B2C44DF04714B89A3CF7383451BE03C53G838C8B479F32921FD1DB2DE65D71BE2BF63F56EA05578EFB7CB14A4B52F75CE49FAAA12F93D662274681986FF07A53D6B2D668B29A8CDA5AD971A4C2FC8F9D2BE8755A9E903A4158E730B23C250F3C571D568E8AE325013E89AAB256517711F197FBD78AD113FAE2B26BFA208BA4C3B7D5DAAD9947D4DA0BC4B68A849CF9670B02E7C2AD0EC7863E4771C8691987D49147CC19CAE3EA3FFB2F4F2C5D4446852D1B
	6C6391A6D3F73F326C86BAE6927096A66BFD07C9DDD5F8ED2375F1264A3608544F83962D39015B0A0E4D68B01554C9B6CE18B4C8F81AC096CFG7FCDB8CE78E7BF0947B119B0C0E8B06A75ACAB084B58C4EC232DAC9469973A0959466ECB2F8D941072557676E9E128CB8458454B602D0199123803C2F930787787C8F968C5634A02AD046704B578DE5D38997AEF00214D0CEF78E6461B517D1953F5C0583A45CE673BC5CCF73E8A6992A11CAE22AB2A93532DDE0BF45D0077G882CE3F4E18EF47F62F40900CF87
	A82AE7F46D75E23A5FD4A3DD9060A3G92853A122DE2BA4BBA24B3815E8BA0E8AB23CBF2202B2EC13A4A06583C6686BBDDF703186E1F1CAE947051A23A94873ABB56A3DDCDE3ECDEEB231D4E54A8265B37816942819FA722CBF3201B3091696A1A4096CDF6BADB13186EA9CE978578C4G55B6C6D73BCDCCF72E96693C819F8890A950E5EF975355EFC23A9640EB365B69BC9D68BE65F4B100CF8648EFE6F44D4DE23A304DC8678378A000E801AEF707186E6C9624EB833C9E40FB87230BF6205BD207F405BBE19C
	3953CE5732D3CC7789277387FCA8C07CCEC6D738CBCC77F8BD77FB40DB81FCF7B13A98873A412DC8D752921BD753E227EBEF91532DE860FE8A788891DD22835DBB1C2EFCB70C3981B43B99DD40EEB1DDE0A3522581BEFF8F683C0751697708693A1B38DF81BEEE0FBD3FB4873A62EDC8573A173DEBF66FE5F49EFB45F42FF23A9040C781A48BF4252DE23ADB36A31D95705EG412D0CAE59012E23197B738FE0339F6CF4369F44F485BB10AE9A70C9G597B98DD63BEB1DD9C277385FCB0C034C01768C0F75DCE24
	AB5B9F1BD7875039DF3073FEB15D8F3B10AE99702AB6E8D75A98DDE71BC0F700504DEA61F6E9E36F42D1700C8748G184773CB62F49D104E8A60FB006597F7C04CB7E8B7772B0360D7G6D870572BD4868D89F76459C522F523949D36AFE352D42B2GE3038D9BF6EEA3739AEC7E71D6BA07C07AD87F03B84FD8C25FED63A01C8210FD887B6031D3F01E5177F410AA706AD7321C4D23969F9266848E597B6034578632D81F58F606707400B81FC3589F2AED62390EFA0B4558252353ACC2DFD5B0A86E2399EE5660
	A4F81E5019AD569DB00832E13FB6F71048CB66E18434E00F57356BD5A3182BF556292BF0FE6F5CB6319D42271D4B1C6C448E01027E11A2BB78F7FD1E456649D62E41B93B6CF6E8FB81D81FFFEA06384F37825E9FA082000DBDEEDE339A1EEAAADE85FB34EA4DA70C669E2D260C14F405FA40DAC147179575B43A097CDE0AF6786B8AB2463650314DFE2B5A5AE7598B23502DFDD62B51F02CDE12DF0B738CB7AEBD48EDF2774CBA2C030F5C2D2A3DF0B4D3E98E5AEBDAF7E6DCBB33C5C63B5DA61AD75E4C3481B03B
	FC426D52FC985A1843E817894B25F68903380443B8977F47E2A20345EAB411F11B2ECB6B16CACC067B2BD006D4483F6CC714610D25CAB2F47E08B20CED2827B2406876D204D061DC6ACFEBD18EAB708886E0E3666DE745E366A440977C941BD785507C937A49F3EB49BBE8D7375AF0D2DBA1143DD75D9C12177A78F12D26D5ED509EEB2051142F250CB0E6FC8B2B11EFA7646777936A6FBDD749B7E3B84F335517661B1F7E30D5A8432DC62355822FC1A6B78D48740867FAA4010C459D28C7EC21129EED9D284716
	F51726477DFD8D68871728885343FF9D6A518DB286F0BD0CFA319E7100D79D017E76886A11D5C36688F4D6EDCF8579C32A3B169539CC0D9D90AFA8A1749F1349FC833CBD18878F29F5643D3221DB5BF57A588EE9C6D28DF8CEF6790F55707589104DF484653F3BC7C97E20A3D81FF67F3899E57821CF5B876FC8EE08A048B0F3BD4A908E7927F199F61A15E4A8BF0AB23C3D004B30CD2D53DF128CAFF0996AA0FF5BD11461B323128CA1DC069B4D3CAC7675998C17D496E7B7208C1104BF1741EAD112216A984A70
	55F1AE43FE2820A6778CA174FD9BF99B8F79FB9CC399CAFA15E48863B2444E2FC7999A0CBDA63D566AE6F188F27C1D4B918DBC32BBD10EBB2CCAF254F6E23D5AD96BFE3DDA708D5721DE2FE7BD702568F0DFAD6A508A72F9F19DFEB5D35A46C5C0DC9257E14A26CBEB63725755229E5B0D1750571E5A04722703EC15EA145FA6EB23097C5DEA740508E2FB1D50DA3456CBE9DF67EDC69906A07FD0B576334F4C937733A9002FBC8E7D84C077F13463151B499CEB0F314F222D20FF9B743A2E53F29B6EA6F1E8C241
	06CD9B088D45E3B1BA60BC36DB1C0FD483FBC65C066FF079BDC02E3063A87F6383D279D3A12E308B46B4DDA8FF739677652FB0ED926407210EFB8AF0BF8E5C425BC6104D5605FE90DC26B4668A6172CF2E3BB4BFCE19CB4A4088E360966349CB70636EBA5E2E9259397CF7486AA199EBD4E9500FCF98368A7C59FC233BD2209D0B6BF95B8ABCBCB4688B2FDB243E9086F189G299A346595756E7B02F65AC6D1FD6C53DBAF5A93187CEFF379F335707E2DC5BB569C163649B6087357226C5B363A5FA6078CEE65FD
	DB5F719E1D3511A6F63B1F3FFBAB6A908272A5F39D6EB9AB552164C4ECDE638974051CD93CEF58CF860FEE0BA148F1308165E8879EBEA7D00E6939D2B922A0AE054BF1E5F99D4A51E45090A918D9AECE8CA64324C614C1F5B2B62F66A44A30E21DD88693607D81A2CEE2F976B63ADF1E8FCC5A26A2F383FAAB342B2CF55A275668301BF22BCCDF62FA44010C795D2847FF2C327A8D710D5D28C7D5532535CF315B9A253ADCD2FFFBC39377891051076BB12FDA29BC223A51A71EDC35DBAA477EEE63G0CE1AE4134
	02ED1B3621CC89402FD007368B26F81D81DEBF54FA5D99ED59D2A64B141A853C5FF4D81FB936C3C4EB4F7FEB1EFF8B646D29C31D238DCA633CF04E7F44F677FDB02D0E37A90DBA0BC9ED6D6A3E24F7076736238EB1A0DF6EA954E109D9DAEEF59027B905BAACEDF6DF07EDCBDA9C4A9C7EDEE211B3DD2EECC6DDECA0E788572524DFC91764D368031B071AA472DC3A6F2D5F0132E4G1F2A53A84BCDB952FACDE4511FC63B866C3834FADD2DED106862E6359674780A6B6181B206F1BD2A56CA75C80538C2BD6A21
	59B9512E07A05B7603E4CCE4561EB0EBAD5D175056BC3E93E5AA83FE1DFABE0F34C5C9A6BFAE53F73BAE4D3657B71139DE2DCD6766DB817A4619DDA8FFB048164465FF45A4153F38877C238765FF6792657FB033015A3F47582F5DE6B6763468AC1774FE3339A5DF45761234010C5EDC4EFBDB483A3D35DBE7E1E3445C8952B1E2A450A683E470B4AF359099FB9C66447FD3DF27324F7F06CD74D61C7F6D3E5FAF33504066FDEB8D76795F5624B7B3B5C9B87FBBE5B75A5C9BE8A28DE873AF7B24B64F003892A34A
	E55DFDE9B6AFDEDEC77B9403E6B7DD933814796331DC07AA10CFEFC49D127A15F48864BA546C115745257B9792FBB31964EF3A2239DB36F769CE3AEFB98C720CB64A77C8A6CAF0E990E67BF9FD693ACF2E096D67A572B07C9D94DFA442B39D978F20CF15C11C37897720DEB1C4FCA89542793D31F925G35G9DGC33D385EF34FA0593364F20F6F8E83340B8635FE3F4366955CEBFB0670BD2D9D9442C74E90DB517DA6AA5CFB7171B9E2EBBF60998A908D108410EDC699124E8CABC313CDD186E66735EB247B43
	CB4D0E362D1304D93A86D93AF91230C6120E7941347DC4AF0575BC668E24ACE99D0EF02843C310D7GC0A4C0BCGDB773BEBC62EA0CF2A994BED7B85322ED8E60131ABC087008D409FA08CA04102F6DBF3F65330F6A3BE59E6ECGDC3351F83AC13A1F085B628BB7EDB831EAB89BE6D8A4FECEED7376A229ED8A2D70CE8250E2C55BECDA0E3651D8F12F6943B2FEA3137369E1657437BA26E3B24CD806B204F29954EB5D1341A8CBD7A18927D8157CEDF96EF74A7E5646170F38DDDF99105A156C93486E0B4DAB6A
	C33BDEB80B5F3AD47661DA737BE7C99B528DE18FG3FBE54F5CE35FB3A164952C96D9D5227E46FBF4EC5FBC7F1995EF2D30614E1E5D075AB4910B78BE5A86AC7996EDE671E8CA132F4D299DA7B154AFC7341394AE5CE5FDB369AEDC2F987F6CB4B3B9B727386886847723ECF0F7A04F1FD34B56E69E31125136A132AE8533F75208C3983A843376B5D13E109AC1DD4063A81A599AEB3238C6DDC06175C14610DE1E570D114E10589E58862B2DC3641BD992E1725134A1028A843C39614A11D4B30F6231BFD28AC1D
	D406CA1B128C07FAD106E69B4A708FB7E578FCD899BC94E5F8056F397163B284573AA743D332F4D299E2A4B2307A7BC601D27DCD865A8C9B565F97E792B9C93BD0B808F21AB639A767F9D9BA291CAD03CA367ACDA14A2061B214EEF6CF065532F4323EF8D029CDFC22096C059FE65FD00545E836DA4904B7B4167B61B75BD10B76FE22D8EC6F50C1B6A6CB04E75AA05A7B73F928EB7E996C9F1FDFC03F3D03F08700758C5A6083B7ED706930B670BE2364AFDF75C965A76326C0208DBB0372FFB8007247F0791F4B
	216F94E761BD8D2066AC4A7F609677648F13251375EBE7157C686FE5D86F3B398C06BA77E4981225134A9020A843ABB3F9FF48E5D8D86F1E8C1532F4327E702CD2B9AEB0CB4BB1F7A8B62F9C20EE884BF106954B31FD884BB1116E1D77A1E10008A1147FBDB7657FE4D879630794476F87145A2DD4204DBE07729B8F231D0B4F716F8E373AA7676332F4D2B95B4EA949395F26A4279EE8BD381C2F1FC5B97D381C9D8D6E49F9CA16CE568FA84A79EC36E2BFG3499DC4E66DC5E8F1CC7B98B9A5D13F31EAC1D2C9F
	B82F5486FF331550A90CCBC5533F28532AF225F7BC8D64EBBB0FBAE956613B088F57E91C1BBA3DB32CCED167155A0325FDE857C4AE432F1B5C1361DED9BA298CC59714E4B8D00DB2D4DEC099F6EDF3CF064332F49512F0778525F4B95B5D63B7D316CEB67ED254B94B486B9D57B94ACD99DE9DD606188BCA756E398BCAF3B449C01BF181FD74A8FFEFA874C0B92FEDF6CF4EDBE469E4754EC3F13C5C4F47DFDC06ADBB5C13E137AC1DEC7C25A8C3F48E1F8B61B2E46DF4CF066932F4D29FCD71D0AA2F30DAE9F9E5
	DF961BD786D0FB9916576935A8E75BE5A8E7381BF27ED916CEB6CE3BCCF19EE38B16D7A01741E317FBB27C1FAC1DD406044B14EC55DBA4359599E32481ED6E65E82BFC7E9ED6F2B94A5958A265A785E5B9778D1B2E7DF2A5B947E7CB47C2A62073858862F256CD40FEA262F29C8BDDBF111E57G6102AB40872E407953BF28487CE99D04BBG2CGBEG21GB1GE9D7607C69D5E7493A306B73AD70034EB6A37F29B009AA34033B5DAB5F8532F452724D77D4325BBA0B34FCCB012E992043936D360A379BD6CFAC
	5F575C1473BDD9BAE91D8D76D42A8BF3797BC8A44433B3B39E27E35B8C8817DC0938A9349F2E02F0B3470D2538B6886BB96EBFD4A71FAB99AFA65B2D83E47DAC70CA54B1100649DEFC96AE9FD0BA0BC28C01D74A57E8C68A0CC75330BC5CB97B8201792C771073EF16EF9CD27EE67916A165EF166F9C1A4E7010077884970C3FFF4875B74D3F99F27ECD7397671C4B29BA2FAC6704734AF2661FF7ADE761F9E7F2FEC7ED7E62B753E8FA0BC5EB218BA20DEA2BDA087B40CA3E6D17FDE73DC75D23954AEB26CA3FE6
	12C053E4B5B62CDA0DE361CF16BA792E0B4C486BD99D3DE7C8DAC74977341160EB71D7E29DCD1F0075A375CAEC5B5E4AA5EDDB59D570BE8850FE957A71FDB3145AD853D54CF7BD6169F715239F8B5F2DDFF0DE9E6D97144B63688572DD194558E7A6DF4FB85ADCCB47CDAC4E5EA012ED9D44F442775CB43DD5EDBE291515D99FCD4F621C25BFFF41D93932F2292CF2A8173558AE5A96AA59BC986C92F1955AFC76E234F97CD5E873CF16901B97DC8DFD89C05D55E8732D4B15EC5EFEB533339E1E9ED7BB5A1C4917
	64A8DFB536933317A96567AB6491844FF0457C7C56BA64D7036D6B949B1F6F399AF571B9C367FB3C20FD852871C2DDBC4F3A6A23DB3D945629695A730FC3A4BF0D97DBFB1E4C715D946FA94233B569943A1EC442019017FB8D2E2B7FD01DA756C105B6091A2BA4177E9AA60F87BCFD2FD132492CF5627C56707C264ED24AAFC848A39C1EB10A797DED1D030DD721CF5566C8FDAA8952670E81FF9A03BE15500DE3292AB13C9CCE93BB68A16C8960BF864B61A1BD11AFF40C238CD1C2B8E10C3C9C18FC7EB58E722D
	C1FD5F6AA17925CA52E5DF4B42C557BA4B2FF53DB83F552BB03F2B4CA43FF2C93ABAA15C66B43F6C8D8E79F179D61BC8FE5D12F4C3C25847E9FEE10E79D571F5B5FA9EDD20A4DD18900EF61A1FFE23C3FE2BB13F0EDE12DF22A4DD1A904E75F616DFC92D587F56F27FFB1D6E7DA89652D58A4FC661596EB43F3F57BA4837966B784C69D847755E68DBEF4CA03E958861F000B8EF742D3F4ED4727D9401276ABAE73C6F5F64401B37D72105CA79958A7914B94D6F274D8E3EDA057D6649A6A76B597B3B4CC63DDED8
	4FE6FAFFD40CF52A76BA54FB52BC223795425EG0157215E9973081C618A729008D14033B6EC7DC212DF52F52C8D9B4F71DBA9BED504E7ED5BDF68DAG89E7C0DC677FE15BB6EF0B033E6BD007357D28C3647F218E63691E1D8C8897DE9F1BD7FEBD6A7049G512171FAE59D5A9570CC566A89341D39DE2A43960A37DDAF55A1AA87F5700438646B79FEB9879D56D4228FA417A979C00B8F1327931ED69F2576745EBA871B707CDED774D1AFA18FFFF806A866F7385EC1BE5EBE9F1DA5ED1FA3A1FDB2C0068F364F
	076D73B8B7E0B9AC22670936C0D883B0F4831643ECDAFFFDEFD0AE07A085BC33F79BAD57709B2465F0046223EE1016437B7DD88EF190D773ABAC07622D8EF5117B526C0328037F2FD0078B6DA40FB88827GE45F08BAD88F53B668C6C7D9AB05F0630D4E6A6D5B0E3C79B863DA9BDFBF3F91F97F130EB3FCC898A00273FEDBF11C91AF70CC911EAADF3B8C0C770D8D8E3C57A36FAF32F95979A26FF67A7E5F82E18D4010AF725E1D2BB83E92F88579BA53FBFB2303EF2DC65E3AF2ECFB43B9EFFD85611D86617C1B
	603D7BA6642D136D77A0D0FB1373F7CB46FBD21383EF5EEFD674A96557AA6427D148176577D7477CF83F79A35D93E31324739ED63E4B37097B39559CDF3B5D213CF87D4EB592BE8112FCC305F014D3393FF648EF8D6F7FBA47E399A45C04E55015C54A2070E6E8BF819AEF46B26822E3170E1B9DF9180430674D4EF8BF576C401B77FF9D7DCAE560A764936CB43F7E9D8E792D437C4666A86597A16493874F641B155A3E8DBB45E5D02D42364FE33D346DA3739E9910C7412F316DCB2DC57B157D9A6D778A7DD631
	9342B6G1FDF733D8532FDEA345E7CDA98BB43B36657CA72FD62A09FB90FD9985B4B560A89A40972C8D74C6F09DD627C4879F6CC17870A787A3C9F6AF2659C3ABE8F618EG2B9F6AB23DD70937379F6397G4FD0BFA55EE7DB10F72B55583A0A5B3ACF3627133CDBC6C19E89FEE86B753C2DCA63726D277DED79AD6027G9D3720FCCD93DC3D0FD9EFF1564FE45179812FDB247DCCBE457B5EA26DE7EE1A087DCCG44E55F0AF320D1AA677382AF2B146785DED1B91F97C8D08DBFAF701E4A793CC00AEA78F901CF
	69FB65F11DC6E796CEE9933E855FAD76179ADEE603A6595AAA686F8D90F0ABDF2B3620CF055D0AE556C04F58CB05F06EED709EFD9B16D935E2FBD0F39BAB2B165B1C3587D3770865DB4F6BC74900D2FE1DC2BED627793DC4672EB45A936ABE3D35C1EF34E8771A04A313819FF19B51A1196FA1703A8D479625FDF4BED8E7257BE2327D49797054E50571792374BC3A93E6F58F8C4B771B34DD2A84FF1A975FFB528C618E00A1GFF00F0FF4CBB0F4E756E5C2DB574EDD51B996E2BDAB20F43CEBB02B23552E9BC51
	987F0DA12CAF2537236D73AE905BF7C0588A60FDBB5A7EABD9FD893C5DEE1F305B1D55176F693BD2746D2CDE64F1FCA545A70B7042B973537305F0769D60AFF7605C736F69FE6DE888A7F05C4219B8C71D4CF1DBA9AE7FCE701BBB5147C296727901BBD14F450B68389D421EG7EF7221E450B25FA065EE957AD6ACEE7FA1EACA379A55C4974194871960ACF9361191EDF50B9AF924E8FG5983D07E434BD14FB80EFBEAB96A1948F12FAFC7BD336FC2DC45B2CCDB44F157AF4334259C771BE5183679AE54FB06CD
	2AF747DDF6DDCDF7B953BB1B4EB7F95EA5BD27FD9A457B5DA5BD277DE6BAEE255F93C1DC415DA853EDE7D1CE6B5DA8D3679079FEA1G4261G89G99G0577707DCBACDE585FC0562EAA6FE1ED8C0B7F4F59F6A1BDC9672C2DA779C9F1618A7489929C63B1F928DDE897721C64CFC0FABE6ED41572595A93A5E1E61B37695E6CB610D3F38F5A2B2E986DE56238CE0A7305F09047AD1A05E943B9EE4CACCC9B45F18F4E4234A9776077727F48122E8F12F7BBD5A058BB906B16B5875B3972C02CDBA7E9DF28017090
	00EFA01647BB5DE29F8B86FCC4205D26F1014EFC2CC4CF5B0AC066CB53B9FE9545975EEB478BBEC6677EC8388E62226FC59D1F237B315B6F0B4D535F073849F4FF348704FDB84E540376716738CFFBB0ED84472DB7E05AB80E3B55406BAA475DEE46345977A36E9DB326AD62382D96DED7B9EE078953B6F35CF5A6CC5B41F19F1AB0EDB7473DE041345E9CB7590AE983B8CEEF4534419CE7694534B19C77C4AF26CD6238A2B326CD65380F2D1836B088F1EB69FAE29904EBB82E79GBD9F9F429D9C7770812EC790
	7ACD51F4E95B649DE4771500A0E7FE33F7A6710350A0E95B74A345C785C95B26F7E6E05B14GF1F58F20CC6F4DC43D83B9EEDBA14A9942F1FFA9647E40F1653C2EA5BE007A249523BE598FC279BF68A8FF79034E74B9CF675F6A9E14FE1F66C5712D8FCA3FCFAB20F8926E04384887D1261973E9D9BC941BD77B9062FED502F2F6BE447739104F6E04F62ED974FB6C026156DBAF85980FFF7E82BCB6707124A7686C7710235D039FF2D68EFDF4CEA962A1E9B9DC207818072465B011FFA71888F10D41E873C9F4DE
	A78842619CF7FDB67717E0AC075A89F65BB709FE7FB5E774ED74412872E0BAFFCD77A6E7BF8C6D4543E8874DD234C38947F9AE45BE2A1163BE26F5339D425D9CF71057CDAB4775D31C9F04039F467EED5FB2F17F9671B09BBFA443B363E16C5FEE1E4577FEBE027D5BD333697C9704B5GC30FE0B99DB523BF79925AC79C7DA77C91E77EF48B1D8B0DF9C46ACF77D0FC62A3D2FFDAEEC1FFCA0538365F70719FEDF743A09C4DF18F73FD9C293FC1B9FF9C307BD01768F7CA7F687BD34E30FBB5AE1DC771C1F1FFD1
	98921BD796E237FDED0833F278044E673606C84770DFD3FCF708F48CFFE5BB0E61BDA0AEA984EDFEAD5DFF5D7CA874EB0FA2AE0E62CC9076603875BFE259F8BF0AE56363B08E8F81FC6423F6F9639FF5A67BD1BA5ECEF9D4AA7BA90A4F7E2DD47654B3A8FBB144F97C96E57A6CAC4A1944F10706388FF15CG4595BE867D79E3A87B7F32453237G3E73B13B3C56471C499ECD67C83C9E134A9ECF71810FC9E5BFBA91E58F033862D05EE664F29BF35C73395C46217C3D21DCBA1E8889354B96996ACC4E8CBA779C
	9FAA9D07CE21F7CFE57FCEBA8EB5D760B834926242FF07B2F9503D574D0F476635BF0E38A6DACFB59036F25CFD946781E1DF0E6B257B1503A09C4EF1CFD5E3FEB19CB75F08E330840E6BB26278A41563A20DE81F0C4779B70A632576A9F942EE132AA71C569FBAAF5C7C0454BE5959F45D708929FDF4D9E81FG08AB8FC3194E50B1E5980423B96E0F94178861D40E7B1037ED999CE769C73A12A7F95D6BC73AF20ECB266FAD2D906E64389DF4BF109E429E9C77FF93B02D6F1358A7042D675F31BC09FDC296BD03
	B0954239CF816F2750161F4A3ECF2BF94AEE3F16271C59F287BD8B2E73A96958350D622DCFC9472EEF70736F3CA0AE65A9147FEA3AA73775E95073E92E7BDA3E370F63F2298F79C0B808635E5E02E943B92E318E69221EC69D9FA9126A187034DD2F54271D6978851DBB4E8E176A18BD17600B4225BADEBB87F5AC07B81FF014290C3E1724C1B87B777C9D2997F5AC603867A92E96422D3F4732FCF88216256677D8165F6791BE7E908E85087EBD6A391937EFC900CB7F3D23DE79FFF026678BF48E3A648F527EFD
	AC45D77EC15A3F9F4E417E3D8E62827F0072F7F0DF4CFD86E54A237307429C0E6877ED93C73F1F8D9DD59EEC0CF6236C3BFAB26FDF8E7A57BD03E578288947E86D4FE099FA18697AA88983C4F0FBD9E577A946BFE3AF3714E71C156193F4CEDA95A1755567A83EB0C26A2BBB7A50D74BA04EBB824BF0B7ED0BD2A12CFA96F13F656DD3BE472D9BC03ACA0E739BC03A5AE7F15E77EB3ADEAE5A4ED1F1C8E750988748E9E0D63541EA29E85C313FF56F7E26E3FB779CEBE8019F6C3C1A0081711D20B513AED73CCF06
	C0673342FC38704C18F56BF816474BBF0973A838D51FB95EC99E41C25AB061C94E3CE1E5B7156ACE5224C0DC69F384D76BF0C68C51F3C58E5B734C628D8E71247FB826924787B84493D955396278D807F82277CFC2FA2657DF9661797699FF89D169FF6769743ED47DF342BEAAFCA65DB2BEE44959AC66EB09C3D2DF0BFA4E6EDF894FB97ADA6AF3F88E4DB9BA871F7DC769F8689A0AAF7E23F4BCB46D820E072AA04E6F0F68AF059369DEEF8897C4A26EC6BE9E2A0A44BA31F48639C32793421EG01G11BC2E1A
	4611BBF0483DA1648E99F25F8C0BFBFABA297F1E827DD4A10F616AF56088E86CEDCC9C651123324F2F0EE4AF37F49E559930BA94B7031E812535D9591A4C17548D12A26998FDE65130698F4FA47C3486724133C6D87FB14DC43FAEFF9E6D753B05D2FF28FB5EEE0B36679D6D527DBC7A432DF42DE268F929BF5CC7713E4FCB7D21FC917AC3B044957C894BFE4DE27487AB47BD3C847D416FCFA8E78FDD330804F092C0768B089F24F8F24FCC3AF05F9143FF3DC45C8F907A0BE967899F7C1D74A7F9DAE18E302CDD
	023B989EE21FD11AF7E7653EF5323D5C47AEF7D56E2C0C334A310CCBC1E71607B65F1E47FD4B064FA3FE9966D173821649D2BAAF548261CE0EBBFA864B49131747A47A2E968461880E1BC5F1240F4C665FBF791FF155CF1334C197D9E67272791DCB9EB92A1155DBE7107C826DA74F1638200993F4904E762EA16D56FE5139522D6A13DA96772F095305B8FEE6B30964E92FBCCF6E8DF33C8605913078168D240178011CEB6910AEEA89613F55ECD4EB3A54963A562A47F14C56DC25F1CCE5D4ECDEE39476653533
	9C5B211B2658FD69D071706D503333646D50B54568DF9DD16823E1D36D795EB5EB84BEBA8B73B0C56118EB811D27A4FCBD23F04CF5B5FD4F8B07F09CC0CA94EFB7E47BEE32DF347BDB510B4E46DCD9F49CDE7EA2EB5FE6F27CAC0A6F9061D93B57C3571DC8589B624A7E0CF5A71D6E8F0F04F0BC476D5791DCB204B3B86E68E9BE67789714FD311E78DCA304B5G9EG81G91G09G99G4551C83B0E52127B53485DE8647EA7F2279939070C5C01C66EADA3F70D89F5723407584EC2DE985608FEA77EC5EE1FG
	01EF23C21C94BCDC50B0D91EF11005B40253C4E1F1FB29F4F7E71E0A0D41AE35DF963E8320EBF496AD3987D02BE17E172AC7DF2B0D463A323047312E149458FD7A6A1E61FDFA1E8173EC0946FA51B05D1E4723066173781399736860F91C1DE14F6330E578BC4CA64CC34F7338E326BD0F1C5E6173C835E09EC3BC0F578A6DF9DCE19DBE8F7FDE4C431B677165ACFB9EAB4D4367B1278F7370676554C26F45BCE1B4D8994DF1CA938C71E11C66588147327CEE361DEF6481D7FCD93DDDB99D6BF2F2B4566587E670
	F72A17304E9D20E332CA8837F0DCA745113B7D483DFD421A478CE9DFC66871F765CB6E771576BA271AA965A12EE71752D792E0F62CAB325B71C821ABBBB27A2EEF6D748DAE7B85E1AD79BBE23BEE300777CBFC8E166282A09C45F14FFC07E511406D6EB90F14F96EDFB17CFFB41C4C4B601BF9E89FC2F3A936F0DE86ABB90FCB353792B05FDEB6DFFCD6EC48DF09BD6A6723ED4BA0DC8760896077D7F6A7A45B7375C089522483BE7FE5F87F82E881503F0CF67D013EBBFB3FAC367F9594977232587E0F73790578
	17513EE769F73A59B1983E120649DD13ACBC651257E9C85E2E6D3F7812F938AAB7E67F85B6397DEB7859B425A0E3AD008700EF8C3BEB13597FBE7EEDFCB060A3G9281D4E3E1ECB0965B2DEF16BA36DBD7112B45047AF3F669706D6543CBF1DCD547734CADF44C735FF36DF976382C43AC7DF50572B1E0E0A172E99B0B67085FD6C13E55D77A964824575121AB9B670D216FA7245F6F9E0B633C55F45EBE9A42C9G99E351E7EE13FD7BD798EB770132D8E7633C0AA212DFED2CF4EEEDBD453746CA6756A2E6635C
	1A866222E2514F7BE8FFDCBECEDC9F6E273856F1627A70BA5F33E29B07325FE9215F31737083B4CC6EC0E561D596312F923A1F3FBD5A69406367EE1318FFB4D9657521030F8FA2C02FF80042B898BF4731BBE0D9FD0864FBD06BG5F86E08570A5B4F1FCFE61A00E3363B86EAA0ACB02F0BA476DEEC77B973F02767D022E2556C13893400363F38E53750EDDB06E86DCA447DF5BBEDC5B01798CE717BA859A46A3F0D89EB92A114DC5A815A5AB0F3B8F4B473BCF9E46F1D472AB58AE7C6B30E33BD0C0BE65935A05
	569F076F2B6F37E159243F026570258D4BA677D5440571F90672D751569A3A065CCAE8GFCB85EC47164DE5FE04045F07CE7AE6782C8FAA92ED540199462144BE65AD9D7BC144A844BC0E97EC78A4C46EB6659EDEC989A3E6D7D3F21EF6059EDB46B4E40F8D72DEF0352EAB5DAF4ECE3B8AB5B87683AB9F9E7CA63B61F1503E5D3729A623AF330ECAAB9EE71C4AC1B3657505631F4AD5C86E1BF00D00EFF0B6249DD4AB100CB63F80DEC4D0624F126171F13B81B93BC63F1CE4643D59DF1D69E4E5ABFD6B6EF4C37
	17CDC26E707EFFA51D2BAB0A873F0EC7FBAE28C0FB364623FDB2D71640F38842BEG41G71G99GA5FF0377ED006E3FA16DCCCA6B8D61603F317B23491D545170BB85A07FF5F677B42335D5F8B8110F640F3F898FE97CD03CA32ED1C413912FDC8EBE82BED8F8F68BE904FE5A29AC62BD4CCA6F7A52B2914F93A86DF19617AF7A40F4D1DEB3D53FDB69582E7D0F18D4088BBF629837E021BD6E46EA79B86765BEBE677EBA361BDE557276754EEAECDF6BDE47F75D466FC95EAC3E5908712DBC7EE4295D5F5EB08E
	6FEFC3743B39CEC8EFFD1D6F154D62F36D2F73FEAF0BA89C8961F800B40012B7400FG3A813C81C25E60733D5904B6864229EF30FB43491D60858950C6G34A6303B3F992DB18B4B1864B5DCFBB79CC48A32D1FF951E5EC23EA5227CC3045F5DEF087D6DD7DC96E7BEF2B110A572A574AD74C3317F221FCD91251B293A41AC7707A0BE0FE3CAC03FF95D6C68FBE396594BBF27FF78F7693FAD36537F4BA5BD6337374611DF46923B2F77D8653EEE1E003E6EC14A1D7239F702ABBE2CACFE6FF0FE36AF248DCA3017
	DFF80233B17AG5D5F9213A05D43F09945A7A6C877B0645572BD0A905776A656017CCDF4EF8F0423B96E1E4DFCFF591B683FFB695E134237203DFE0B6FDB24B8F29FBD393BDE7826E0F33BC00772163F75730D4D990FAFB763B79E52B66D67GD6461FEDF1AC7F79CB6D7E73755A617D67A77A3D7F8923D9FB52EC6CB3E830ADDB45760601CD5A5E4236EC4FB212B70B37507DBCD60873F80B6F13A9423EB20063364C41B20A646571B54525C2B87FED5EC651EFD54AA12CEAC35C8DF3FE4EFDA204D72A280C990F
	87FFD69E5275AAE663AD3D0EE5543B1C58112DCD85761638482F65ED715AD4C22F3CBEFF524B57E938ADFFAB7B664E7436DDEF4F379D7D5C6FEDDCAF7EB5DD9F89FEDB7ABEFDB745C73CADFD1FDE1A076F53F190D71308E5FE002E7178C3B802639E2138B888E7F0DCEE8E5FEB758EBF5FBF07582A1607DF2361F86E9347E47B4A896D4FDBEFEDB29EBF67BBB5FBAF3670B6378364B78178G04G443C03EBE5FF9940B1FF6ABB7CDCF0A331F9593B31F9F5G6D6F22DD9EB7494A7CDDD1193F6B2CFDCE237B1D7C
	5E15167957949F7C2E344C2FB2E319C7C0DC695F795EE53A166085E1FF0ECB675F87C57C9D657CBD9DEB25C0B8BF091FA1CBF1568360C387F9BB5EA7AE83C2FBA9B6A77C64655C6D4063D25BE6290F30BA2B69F72C7B3E241804767945910C871ED8E1277F4D00ABFAF61626063E1B14033D1A13701B719D392441B928BEA51C18592451D90DE6FAE4A6A0DB4DC61356EC9DE4580374A6A81657B1F27D21701D7775390B1C65D1FF5C3AD35D2F5EFB7C940C0D5A54A739FDE7AAFEF76D8A58BE2AE7A762BE2A5027
	9ED405DD5518C946FFCA7B283A853F70921EE5FF3FE53C5D96E11544DEE1909787108ED0709E78ACC06DFBE81BD76859A36466A5D0285EEAD5F7F57710CF6AD13303BA6D009DCDBE2EE63E7D79906E35E8FD0F6F31BE0F7EEE65389BA84E8F42416F21EFDFC177DD05C1B89EA005632F27F8F2D7A23967185C17AD6C4D38602C0FA2E9C7E257F8A7F4E11CF705D39EA35DB3636AFD1C5505659349C7C8FA7520314F0AF308DBA722CF973E8FE3B600B6006E77198DD89F79E0EE098B3E9E6F0B7B4893EDE4AEC53C
	FFE32FC9EB40F34E99CD3D0EAC146E155F9B0D67691410C5D0FA0D70D62351EA311A55A6C6A04C5F7FB413469BCD12E841FF230E08778116AC5F2B388FB07D4819CCDFC17630776D7B9B53ADA5D90C4F55B309CDCF28759632D70479D3B47DA6BD8568D59F200F6DAAC53FAB6538C7A82E99429D9FE09987507DDD5D909E82706178E00AA7F70313FBEBD3395F2DDE54AE508FD776C3AEE8FC1C44B19E3B96C94748976BDF2EFCD41827DE0CBE967481733188F846FD406EC3E7BE9639E464BE765C36C2117F683A
	345C01D87CBFF64EC07F91889C7D6F3FA7268B7EA309966267140B4B1D61BEDAA64755596475E94AA06A1A827AE5A7033EGF5G640C1976E43A5EF1E67877023FF61377827105E58C2FEEA236E6A7262E92F0AF1FA5F2B4E8493D0358068FE8318E94BFF40F2AF8FEDD66F405BAC0A0A01959B6BA59DE87E8BABA5EB9130B7B6CD57F40B1506DCB697CB104DB813A7F416F15AD165B28EF2E98A72C9FE63B6A17BC7E61EC2C74DF3ACF49779F52F7599C3A6FBE709F52F7596B3271DDB694620A7E09F553A78777
	9D8CF15CB2BEC79B70CF3EC7BF0FCC9EC5C2B89EA085A07BDF108FC095C09B00954077DFC8BF175287C1B89AA09520B0055D4BCC6EFBA77725133B4AA3BA115E126B63428E0427D560C5F88CD7BF0BC6C0C37D721FCC76EC011E68A7EF97EEF5A9DB0E0A4D53CCD131FDDB43719C697C9161CD66041C6D99E6BEB5CDC77C7D3851CC8F4271CD21F36CBAB24E2BC9C1DF3CBFC7DEB707F4D86F6201AE9520685FD0BF7F4D4A080D717ACE8FDFB75FEBA33A53FD040754BAF246AE3DE40E475F53D000717027CF38BB
	1B45159DA844B8495D5AAC7E1DFA1E773EBE03C116F725F6A646B798FBCCFA2DD5C8AE440F1B40F9EFD36B740EF92BF85E2471B3E1CA4C7B4F5BF95E75FABD6B3D45F9CFE4FB374CFD42FE4C358647701BE64770B5A64770C42BE37831DE47702ABE8C3331F7BCB9428A4FCDBBC06ECDB7E887CA78A5365B0C86EB5942FA33CE2DDFD8DE328A7E2FA613AF2C2C5B27E3B9B67E1B75B99D70B47D1B55BFD65691B306AF6B404AA9E8136DC6C9BFB16E82373568F6DBE11F9B5D8C85039B2B2ECB2DE787F5EED51BDB
	0DFADD5720B0F730096E2FA1B2FB7D9B7D766CF7E46CECDF0B122644791FDF533464BE5328FFE39F7A6AF7A5AE74497FD05C076E5B05FBE9F11C0677FE895F784D473633845235FF087B8F9A7712C29966CE318D9BE49953DB8204BE655179B8E76273A176A9EFD3FED190CE84C887E07D18EDB176E341775D2D723B6215AC2531DCE25B394C020F18FED59F597BB1CF431B190C674BA5FCAF6CC7783D4AD7A5526FD5EE98AF7E5EE465826977AAA43DBDDE3DC07A3D0AE37A9B97C83FD741742C8C3A7BDD798823
	3935FFB8FFE0E57FD23F2B328F7AC8DC76A9A5B3D412FD54C26133326F34E15907C33A340F306C472C2C99E959BBE6CC8B1F15C3100D2F7524E259A750FDA99A888F817826627CCBF4B6528627E219FD196D3A4C4AF3DC1759519C57E56613633A4CA2174A47B8CF974A47B857AFA3636AA410BBBDD5723D614772FE3AC48107D0A509EB7E98678B6F23F66B78D8BAF774A8451BBE164E9D55E4615C11A744A5FF4C3FE31B057346AD7F61671F4C46711069BFB8DE2922734B7E908E6138AD94C76E108F53A06ECF
	33456F32045619EE7EAE62DC0D91262B5E1EBD5239A3F7668ED939FA1B072F1FFFB00F2CFEFEECAEF141AF6ABF627AB937D55C2F746959150C2C8CBD2DD8B793A0CD61A7D8B797F5909F9D36EE6EB49E97B547F8CF11956BD94BA7D8A73F24FCBCA16C8F907A895649FF72355A68CF30CE96493E7BF32CD3BBE55F7DB95649A159F7FF0E699F11FD77076959582F63A05D931906FD686D8765E33F50035857372531311F9E1E9EE9ECEC4E46831A76614783CBADFC7C363F6FF80F0E5C0D21950D312619E7BA46EF
	B39BFB880D10BEEB52F44778B6A30D456FCDCEBB6C897293640B01E7D29A1EFFF25C2AC413814F02CF5186B107F1AC45EC94E3A361B208E776781E3F275ABEE5767001E760276CBD05596365913C27C62F346BCBDE3839B9300F2B1E61904F4DA1447FA32F40A11E190369796610D4CFA2DBB8489873A95A62E51592CDB244E7F0DBC48C113AA77E56F6BF79EB7F54F65F0E6D4DED4CCE1B69FAC761E7E8279B78BD861A4F181D0660697399FB17E3F65AB4F1F8BB3DBB0117FB13C1E3379246F7FDD3A00AC7AB31
	386B87270A6250E1187E1F67CA75A7F28501FC611F7173607215E862A0BE79B33411FF393C4EBC466FA3D4FD4E4650C56479B91B27E26DDACD052BF62D71F3F13B766FA2BAF75122BE2E552F467799FA6F4D1E3E9E1C4AA111C25C189DE43350B61D5E8AC3C9E1D051ED9C00F7937165090C560F1EF967C0ABA2E2B45FAD7EC6CA33079E3159F35C08B24E544F1652B475762975E239A6554AE86834BD4B08514C5EBACBCAA3FAFF4858C4678DD70A5B35F6412EC370E4714F2FD10A7761711F2C9347878A7151BC
	3EE41DD27AC49E3F37D7A9FD5A6768936F774947BA0FD7BB3E339DB3BA06EF9CAF77238747239F6526B3BFAA01E795G19EBE7FE34B24B159F35240B7D682EFA3B9F619C18FF5BD43B9FE94D2C380438243CF98EE581FF65EE046D7B3C5992DA392F651D1DA521913911C0B343740D04C664C682CD7B82A90D480D841A35678AA5B468C62C8C37E6CB4B185834B39D4B3839C2A95E4A63A78CC8FD0044FB7178B9FDCA697DF97CA3AB14520724238FBDA31D9318A01FD30F236732C4C21A78F4FE4652865CEF124C
	F1E19B7937C1DF60D873DCAD1D7761612BE9B875B40627550A4709046EE247022E0071A8F36071F32ED55BFD3EF5033CDE2D5D04752A658BD62FB4703481907DD95F745466617B2670DDE42E39492633BA5605542364DDFB1F7604D9EB6966ED971B1BF91F9C47AC50DDCE47GFCDE264128B71AAB0E13F5CCC6BBF6231DF64E5A99F2DA33D603BAFD486FE07479024D1F8741B39C003CE3303662609CD7ED05E31F33E63C503F301134C55C0E5FB733106A57E36C57129E37C5E7310A6BD66DDC296F9399623EC0
	5FFE164EDFB529ADD621DFB04BFD7BC53A5FAB9952E4FC414F4F64E7B914FC093807A82E91426DDF226F5ECE8F0951C05846F1C194678361489E442D4C9B499E9A120F2BF81B13F84623F9C4BC0673FDD7EB14B3D57A6C61674C2E26F382E4D1BE74CBAC076733C7B6E7768ECDDB8F1076A57A527C9CD73ED475DF31AFA59FA0FE6E78FDB91BB8E16FC637703DFD4D7F45F72397A638FEB77AE2026BF7236593DD3F9B9D19686C5D0815DB8CBFAB5DC35045773F76F28E7A2FE319077F9767962ACDF45F60FF25F3
	8B75941F74DF695CC21491679652A12EA3837D7AA57A1DEC840463B8EEAD7F0EA7BD83FD78CB0AAB7DAAB62F66AB7E8D895D5312E1084DEB61B8EF33ABBFA4E9AF26EDAD7DCAE96D63DE17BCAEF61F0A729A465D96F9FB7EA47F6E587495EB4F3D6069AF48487CF5DA1FABFF8D7DCA6C2F1E33589C2FC1331B4E83C857B90BCE1136D808934DE0631E93DD85490B5FA748362688F1A6BA2E5099F4D61DDA2FBBC38F515F8F040613EC6F47DD35A4BE8A64C982487D9F341DG157F43FD9FEF2DA41B42590D3152BD
	9FCD1A13DAFCB9C3FE4FD6BA3FF7A12A120C2304FB97C8AAB29EB76BC84D34E07A3115240FBBEE847DFAC87E08FF2D12143955E822C861CE06CAD24F4C3A135DD6C6AA605F2DA47E28571E20E8D6E6A567F89F7CBF56DFE960E97B9F3B971B7541DE67076F030368BE952B7AF89B39E600751BD51BD5BCFE49CA69FC3DF0CC850EFFED76F5613538CEBEE12ECAE10EDFF4EB982B3B2778DD97BEA0F7547FF0CEE94DEAD2167CDBC352E1EF5830CDEF9CE8915CD6E877FEBB915BBD5557B8FF14C8732C01F0ABC077
	5758D63F10CB725E8F493BD538CFE86A96E29F8F20718708G0887C88148D60D937C453C2ACC65FC8F9593087A0CD92DB3887D715F974A6B58177C2E4BA2481B14D7A5BC9B8148D9742C3C0E17723D0100D7AE2F67CB6565F5FF1D3D3CFE5AECAF0FEA6C5F0CF69C3E7F1ED593BB09EEF2DBA54C73AD46B2B1018CC1G42DC5DE234F5A2413339BACADBB4FEDCDEA5C0B3G4BE36DA0665189B8BFGD62E6FD74C15176BD63A1CAA14AC63F5EE90F9450CC7DE8F51BCF3B3476515G54G30F51AD327457B8D485A
	0B333337DAB319CD2D4293779BB0DFF82D1E34EF219097831086101F853CGEAGDAGB4D96893CF38748926105DA7D85BFEC1F65715C7163D0CFD339C4BBB88422C5F1B3E0C1E1182E1473D87F3A9BEDA04E7363FE7A91F2705383A6CF1C25F37091F939D88B8A653F8BA6794434313E9383B9F7B3C9B33AFF60FB449EB2434429E240B66F1B16FB92C6E7D014F25A501FC99G25B9D02EGC4D736AF371E4F253582DE83608990889089406A4C5F3476FAD4A554232B866D387582EEB13DDFC958ABD3390A574D
	BF2D4C9561996E509271BCA4B62F42BB4BFC05B9DFD1FC6D9A799CA8EB1D05363DDABA776758F65F1FA712E7AD1B33EB4B131EBBC4EC1484B6C887E0B48BE4F7A5931A0289E05789C8336E3C92CDAD4437F21A43C6A51AEE089F1A00B62F556D01A72BD35B54DDD04F86AB3A4CDA5005B6009A96D326C450C477B88884CB4BCB96B283AFDC0AFB15B6E9456791D61EED494CF252F678CEE0FE9DA8BC4919062C3E856A302D0B03387C0958563DFCCC218F5BEE56E9A4FDD88ABDD70824E91C485B3AD3747CB6887B
	8284CF44F655E3BA720A84DC46C4EC1B2326E3FBD9150BF9FCC6E975907684704FC5FB36FDC76E7B924FC90B7A000A03CD7B5AF6B454379C5B5F30EFEFCB4B567AFD476A779F5B53545458544852BF72DD350B744DFB776D78F16F1EB6659C506FBE79CE3CEFCC787EEC7EF0FB06C3F347A90C194F8D1BEE1EE23B7378306924405A5D7DA5747DA4F71C64BE2F8E0ACF9061D9193DB79F4BAC9962DA724665317E667286627BA1900E86C88248GA81E846D52A46CE76657D42A68D835415853E3B4883D8DDDC243
	A1EB1B51548203D33D683AF526DFDC49456A474A661F7D7232B9A13B6BE7E4E5E39DB61DF259BC1665DE593452FDF7AD1324E5F318625BA7C94B665DACAC9B8D44C5CD42FEF02C4BFD02CA407A58C76959976513E1DCB7997B5D7FD2DCBB04ED9C371D5E45648D61E00E5B11C95FD7A11C40F13E197CBBB70EFB09620A734765D565233C4174BB51E688F7F25C9394C76F3E9E626791E7E1DFCB6824BAB4AB60AE86980FC31C07527B6A254ED1327E7C8DFE5610B51F0D253D6199GC0F4CD04E7012AE1F609C093
	2620D73400E3BEDFB1575E075788382B8D127171EA061FEA11570FB1D6776A075F3069146B4758E153A9578F83BDE338EC0A34FE8CD0FC5594E97DD075E17D28033840A968DB9FA95EF56A8A186D4C87218B78B97A7225CC3E0403A8DF6ED4986BCE457E307490FE1FEB1B0A7D612D0788ED880423G9227E2BF1D310CFC5CB86C7E85516009713867C75C1F1C4EF97C065E33D1B98D5E05GBA2721BC37DA1156B68DE983689DAFA1900E82C81C067D737B361F57C63B783BD8BA7028ACC019EE9CC49BF59720CC
	B9740E07G88078144F05AD967D07EE4CEDB2C22E714CF077E8A20F5BA52CE9C0F345D5311F6AAEDFF83A09C8690B31D0FC5F2FEDEDD1F64F7E5A7830F1299A853788928EB7B8C1449C26F5B76A1E10088CE3BB28F650F673455790436E066383CB2003A19C83BE08A52364FC45A32821AAF8983C44C44315B9B65A8DBBC608A8AB10F6FD720EC4D051807E185217500309FC0C8A15A6BC0857159BDC6B31D43E07E7B4909A2156239CCF441017138FCA54A994579E62CA1B8FA006DAC2C8BA775F3157322AB03DB
	0DB641771755607709B533B03FC36B90E762382BFA9197B48B6D90BB9E6D10883822EF10F6CEA6BFC3619B3403B61350FAC1B8GA06C9B34C3CB16588EAC7DA03F4BB50667F921824F224A1D0D38CD8328371E63FE68C3DCA0473D3782F18933116F3F564B77FFE7EE207BB60A46897BB6CEEE403E498FF0E45FC688BCA38148793AECAE75650DA5AE5A4A04A2F15F7462C251BB1AB0C7F3E4935AB095E84B3EC5196F5D04B66C7C96ED18CAE97DA09C829075AD1643B75F72F2GDC519C4C63683778AE51BA8773
	702634DE908EG88633416395C66F350C6099364F3DF1F73EFDEF367327E3A841ED5G648EC6B677F50C6FA7E9196BEC6E6B39DC795CD7F1130A471F9E9059CA682F8F9D177463D56C5D73F559FBAE112593F8DB6722AEF7E413F7770BF9D7EAB9D4FFF8BF17A7BDFB0D0B744A6FCAAC87615DB7C7BE56384365BD444E47269FD05BF803EEC1F3F1AC595D03EBA4619C77017DBEC20E5BC94FB749AE86BFA946B1C0ADDDDFA905F08D47EDE7B84F583C960E7B2B511D6F6CC93E2E62EB04F846638337F80CF44D10
	1545059E39DFFF466FABB695B33F7602273FA09B2B6BADAE77DF06960B6BFA01D5643F558CF7D4DC7F57B15F7D336C0E8BE273A848AB21986B77BB962C5FE900AB7F8E6B6CD23E67D2739D566FC3F44F25BF04C381223FC37FAF6F937B9FFBA7FD205679FAC8C86D706BA1CF527B4D351A135A36C113F0CF749F6805F54693A7ACDA3E6E72475A820E5BE141D33118EE4D67D037A42257BC544D679C6A56B68FF57B83257506F0A0C078BC74CB53440B2DC7AC5DF9B753BDF15169047BFB689955F17324E376A394
	1FB8CFBAE6FF1F2F2924C0DC6BFC1CCB183D45F9394D5BB2FC39AD59223C0E35E20BFC9DEB759665F52C1AAD62F5ACA66F426FD15ED010B5E5BE1665296F31ACCBCB30AC7DA8ED87042DG5EA5C86BD50AF3E801A5D83EE3E477B20F2C1C7C5CCCB7762253896FE474DE67049259BB9945A717485E491670FA8DF16D8BD05F0D036E493D434DF417DFF4BA2657FC7A0E913EC02AEF9945C7AD106A7BA8FFFF0903381A05B8FF76F593E9837DA19C8610G1086107BBD0C7B3FC71F7F300E743F4E56E976990D745C
	D2F25339DAE790AF59603AEB3D73BA730F7A616B4CFF6A156BCCFA3D3C4EFCD52FDCE732360A6B8C4365EEE57912F1A859810834D3371231AE6B23F62B8D6A132459967A7F1BF371DB0B2A6F31DFFF2E8C7B7AF60EDBC44F189D02304F7768DB331711B12EBF8FAF20E1F237B38BBF30545D33F4C81EA37337675C66B112B9A7B6E754C64FCBF3C6937A3D382FFEE603FD4EE88D363D7C7DB392E8B33E47772AFD4D4A6FD570C665306ED9415FD32BCA314D9B206F29FA88FB827817E21BF7F2A236F92100E3FBF2
	6E1CA85D737331CA3CE7E76CC457FBFE32F225FBFE6A9D767C2C4DF536670749641D07B225F079DFA8A57217AE9A17D78B50368865BFBC04347AC5C8DBF31EFECF8D61F000B8CEEBBC4F7365347EF47E21F4B1648B503698E9EFB072FC97A32D2917668B61F00038455806AC5CCE7CB8E5B13697F7EEA5FBC31C35978DC60D166DB461E7C8695CEBBB435CCC7731C3BA559257F527F0892EA14F27771B17AD11353994DF35C456661E6273FE909738846B7DD532FB35C7A6372F1B69DE3A68F4429D2674DE6E5CB2
	293E960AAFAC136A1B415755CAA04E93009C161B250A18C95AD31DC12FA33D04C63BDDEB60329DAED1715F47C53F7FB57F67BCD7CC59AE59A52349035505EB9B1C77FD3FEAA0ED0350771DB072B4FE94EFD21B2D8DFA35454271F7500BB278FE2E7DDAAB79F65D9EFF9F65E569BBEE322FE2A12F079BC8DDB68E98E8DF4674271FDD893F0F09FEA7E70D3E6D47DF74FE050B815637C4ADA373CE9ADD3F8E8C31DFF7C6AB6CA3CDDC06EDDBF2993E234DD8CE76G00392D0D0C3E4558C5B74589E94A17E31F124E53
	9CDDCE5A71AEE30F49E800E25DEB5641DF1A08253151B439CB476595AE45B41D19536CB2C9F88C5239BB32BF34996859FB6FC319A5AE74ECDFAA6ECB1368B9CFC63B98C23E99FCCED04F65383CDEB21F330661678F587B63F5C23D4E7A866BF5G240FDF0AFD442EEF705D22E099769103143699421DG36E5C8FBFAB65A5CE7994AF04FEC9226E3B5956EA5B8D5A05E0714B0F8506939A7D14B186EE94B9C77A189EB26FDF6FFBEA77ABD21EF74FDFB7EAF40635D036E758529EE26DBFAC81C2EFC797063CD56D7
	CCE5674BAF1776A133A93EE539348F89B8043E568EF1614B314F8C7AD1AAF7C939DD06AA785DDC6EA8936373BD1D1369A81772AF27786EF2A97F5F71799EAB444517A37FBBE5EB0535AB6C7CDA6137E605927FC27AAD3EED05147FF7946F39C24A7F3E813E17936252D6A07F9F5CFC3FBC66E6BABF07F46D95A3AD6FC17A7E292F106AFBB9455BAA247ACEBE037AFAC2DCF2856ABB671CD46E1615F699BA6137F525123DFD68F856EB25147FAD946F3BD24AFF210A37ED9017DD097C73E5F7F1F7D75A798D41EF1F
	CAA57E1753B53A40CAA97FEBA9BE24D24A3FB09B7907C3DC49AA647F0C6C6E3611155BCBEE261B60B09F6E39EA24657D81FD2F72DBA555B7156283D7C9759D1C007A06C0DC61EA5437AACFAA37ED35DD86EF789D30DA495E8F52B5455055D27E0FD1FC44EAA97F5AFC649F8DF115EB107F62A9D27E3EEB6C7C0260F7789AA57E7EF41DB2E60D147F3D941F30C64A3F22002F2DC2DCCB9572FF275C3DF27B484DF4E5AB4469C22AC6DA5E6F52B55548AA293E7F227818AA293E569528EF8244552DC5FD2FDE29A4F7
	60DAA6C798BC2357AA6D7598DF056992C571E9426F5CEA98EFGD4D65B6304EF2CEAB0DDE335DC57F6406981BCE469BA5609657495620304E7B8BCE32A15643C369753A5A970CB87DC7EBA286F6B247C62E56D4E484A73FDB7534D4F94272BDAB7D2BF789BDDD3EEDEA77503F7A93EED1D548FCEE7229FF4C2DC64BA7403730AFB444AEA189C3570EC2DD1322FE7055298CBA3505A6069DDA357A5C081978A38A8C59EDB7937D7898A69D281173DFEDCDE91C079FA71BA5AC107FDBDF5C2DC1B706C066790004FFA
	E9597F1357494075F2FEE1000B86C884C8DBAF9E7797EDA083DC256D9D6408B1F2AA390B9DA07B54BA0BD63315ECFE34EF71D5A2ECB18EE84D924A253CFC2F5F00651B3B816A79869C773F36815F91EC9B787E180D34AF04F094C06286FC9FC859A85E0F40EC523549BD3F6EF5B35D038E6952B70C34BEE8B6931D72B7CA6BC38F4597ED1456074F785E03D208735A08752178DB7764DE66E63A2E0BCE4774D24DA1722748744D2378F4193E3F627BA432EB4765F557223E3639CA7C23EB195D93EB499EF747B2E0
	794D676BF3391B405FB6B94E67D2FF5B047EF693258D01F094C0A2273DB2975FC953B9EE46A444D5EE46743B6877B95D9076G705B0C7E7A3F81B22FE5B2EBCFE84DD0B3688C83FE2BBA1B1E13958234611BF1CF4B4D7CFE54644DEC1DDB358546C0G5D37627E0D7F717BCF4B37B85B3F21B64A77EF3C5C2C62719F68C5EB5D42FB7D26E5A21C30F743A87FEEG6428833EEDDBF07DFBDEAF5AD98F3840ADE827937C5C7C04ADE8A7DFCADBD8876DA1C0E39D5AC9E7962FFF337419B634F3C79D269FCB5728FCA1
	9C8C90D907BE720145BD5F7E544DF495EE6E8B7D8BFD27CC28135609B80ACF2E135609A3D63E8689F16D75286F9E593B4F48646EF0B35DCD971D0E69351A2EB30457CB755DC871D175D2FDA379FE03B8082B590A7A52AD20589E09FE27486E337AB9017148499DFD9EE55F3BD7AE55EE26B3DEF4BAD6EE79F40D49FF2B34BCE7D1FC50D6E9F95EE15FF3GF1C58DD81E57150A798F815E2741F5BFAA4C59AD31177DB9516F89CBFE01B93BDF004722B3F61DAAC43F1F989CFD5EB1234AC3D8CF5F66FC2DE35A3661
	57790B36A92F734F5BA6DF677FFE1B72BA7F52ED72EF3C1F61FBA2839BD83F9B8E4F98004AFB305F7DDB996F1B95FCD558C738D45E6F7EE7070A475F3D074C3F5BEFF245330DFA3333851904F64D4DB91AAF5DCC37064F6DE48CD38F9104F7FABAD7D350A8ED8F52A83E38D15A9E1C63F3BBE590675D086D015EE14FD6AA60331BC6529E5CA01B93AA9225ABD748C368FF68F97CF5CD63A4E7DFEC2378B6911E497D92ED5FC8389B62221BD06E23C3D27E155B6CBC9B37B9639FCC7B3F76EDD27ECFD03CFE1B147F
	6673485F83621236A1FF6BF9A97F666DF61E9D5B1D71AF20FB43CD5B257C67D23C67F6A97F3BD5485F8F62B236A37F1B0DD27E1D4DF61E56E6E77C1368B8502BD94A7FA30A77EF16726F6FC57EA190D738837791AC5ECE460D96E12F00F06751A9D287EDC0633583775A34D21CBF04C3F6206C0B4851FC58560AFEBFF16A97E8EBFF819E2699F61D86C53FB3E70CBE6F39234A0335191FAFA3BE321A079B1613B059C74E5A5DD93BD4BC2EB6135C2DA3BA13CA3EA0DBF1E04F2EBDFB8F6D61E9FC68B74A2BABABF1
	2DB8D37C0EA21CA51835C4A55DF70EED3B43367356169DED02ED723EF12F0D1E6DE63A560BCE476A623F69BAED648EE95B7E9945476C10366D97789AF08244556D647BFFE70B798782BEEC276B361D6D997AF2D789BC2301B699A0FF57383CD2009A00D6G8D4050AEEC879E5C6E6A7EA5DEA422FD072CDD28235F2F79C1BEC13BD05EC2623A026D3E937D3E6F5068571BE7C615875379997AB2958E7A466C427671E3FEDED39A47355133190ADA60DD3D85ED3354EA3747EA516F27AD23EF1B38D16541F4FE1C6E
	592F83FD5BDA508E6F7623EDEC9C57C43F717007F088374D9C1B5D9E5F0BFE878F0C3EED1E9FD59ECC675F8CE2DD0964BADF903D23F809FECFF7F94F784F237332D16541F4DEC53FF30887FDD3F8599F39007ED0309BF19194D78561665DFCEC24325B63CE516FB255685B26FED4F9B01D3F275F1FF6909BF0BB6C6577A87BF25CEFA9AE8C42515CB6DE223DE4B708FE4F9955BDDF423B4E2872E0BAFFB7816BCAA257795F936CFAFEA67AFDE142686B6CB72ABC04BB52681E64B450B7F78F167D40E47407F20EFB
	1D62DAA12C5903368949375B63C9516F2D7923EF1B1FC61507F0A6947DFE59867AFAF3BB682621EDC2B8AE1A6262A01C4CEDF3F7015D9E8F09FED7950C3EEDF60FAA8F617EE27A7DEF469EB61E9476172E306B793168771995232F730D234A0369FC877DCE3C7971583C423DD876D915688FA59C77BD45B5C23803630A2A884E84E1EF0E3BEAAD669740F19FD0DCB004A3B9EEF69566974FF13D941782616CD644ED235F1D97C1380A63047B2AA12C6138A10A73G3EB60E7B9345F9C3B81063AC741B75D088C7F2
	5C3DC788CE83E963B9EE52D14CAF096376D2DCBA048BFEC05C5D3DA8DFA947CD33E25A9A0EEB23389688F7F25C9DD6144F4AF155F47F2B678FE29FFCBB536EF749225F3D19236F03E3C615875379A6F607B4689C4C6D70A5BF3BAE0C6366503B7492A01C46F177D5E05A5CFD081BC8FD3A9042659C37A2975356F15C556CECE4926638849B727564388C0A8B02F084479D26636EB88827F25CE4DA1E597B4765156CC75CC10A53BF991BD745F18194578C61CE0ECB6677A71BB82E071ED1608B61C00E2B21752195
	728B63385BEB10C7A4473DCBF17190CE6538219548A33B8DF163ABD00FB20E7BD5B666D74BF12FD1DCAB04B59C37A0877530F1DC973F675B2B8D7B0D1BB638B7DFF00F1B6992AEBA1D309F146E7588EE136D87257830B669BC43640DB84F9089F1E58750864F52B3993C8F086B70294D76FAEB917D4E58B47AF5F856665167B1E10EDD27E9225FFE5F0EBE6FC7C71587AB536B69399701D026213C1C5366222FC7F0DCF9B16D43A01CFD104F6F3A797D789AB753695D7C6EBC0FFEDFDEF4D06A7FB3A83E7420547F
	EF637BDAAAA14E176BBB46615C53F440679F9A499A4A6CC9D239CBC4692A9472E072BDC3773CB49F124E617F1962BB8EC96770FFD80CF378A6080B83E07334F76F8614B01FFA2427C4A5398332D15723B5D8F4C6C3D9557A3565A5EBABAB05B3087E5003F628E987BFE847FDA7938C386FA4289D770DD451B34692A11C8ED0F0181FDF453FE1AC63387B0DF856CA67E13E3F271A6EE106F088C054E134F9E92FFB3E325A4DF427AFBA9DAB13DCB3557F30544726D3FC4AE1290F5D4A777424C3DC470F286FC459B9
	0391BF5A7DA36EC7E73E52C47770A77FA87515D60A4F78D16AAB2F8D222F947C8463341F78F90122B94757C53F3B2C235F5E8D7D82BCEA4E5BF52A977D7E733951677D77DF00470CD23BCEF3C53F833F9FFD5E3F9FD59E2C9F8A2467E344033F1203D7996E810ACB855CEBD9685F377033A572BB4665B5F6E05B734E92EC3B06BA306D19D6C6E8C3A19C8D1044E93FAE4336AB23031F9FC4671D2A0E30FDF5427B33E82E69CB516FABFE01F9A77FDFE05E690A09E873A51908731A08B6FFB28BF16CDE95F2A7F863
	913457CD25B81668BE02EDCC7DA2BA0E04F0G4735D0DCB80493B8EEDC694F259B72125BAF65E7631125121F934AEC50B704F64960F619F01E4FC59CC5FDFFCBF10D90EE67385F33BB8DA06C4BF10D675B853A114855BEC2BAC73B9C21BCC6FA4669455E6741ECF0AD55AD94640BBA0AF6D922C23B24F0FD3F21736085474665159E6367F6D0DCA304B59C97446764896D2514E529C2FA4663D94E63D24FFFD53299334143C674974FE3E8174D3DE817A02E6FBFA9AE9A42099C77A945E5C0383893F1C33DBF070F
	A7AA58A41A733D69E761616C1CDD613B68D3E4A143EC9C30307D5DEBCFE1DBDE5B097B1CCF1EA272F49975FDBD860B308F7C7CA9EC47DB38CDBEA1DB6170DDD5747B4AEFC63F4D3EE3D4F988F3C033F91B4D7BC96F5958E6CF4E463DFAB9AD4E778B66378C3FDFF0D60B72FE4139AD727D02A5AD4A7B85973708778BB25CF21A2FD9FB925ECB4467822DEAD1BA97C8B89F711023FF749C42770C8E6E9F2F7EA8BF7B6E5F7C3ED8BD5011BB03BC61698790708A6ECD4C65778A05F4BA5B1BF85AC2F8E98D44C09A41
	77AE3760F77F11DC06172CF2991278FD34F1028CA970D4294765C5F2993E6867772E281D4950B1A01741B200B214C3BA060B9FC45B542AD12E66F3F2390E596F66D1B339B4703481843F0AF2D9AF707B661C4A755A8539DC9FDE407A9B48E55820F077658F7C6E4BB0C10698F8A68144F099CEE4A1CD06D399DE4C124B1018454FCFB80E3659B681EDD3FA9C65BA1EA717EB101F3BD9F51C4955824FCE001857D02E7F1B0CB436634E647AF832DC2E5CFC144B1B4B50B6C5AE4369A918FF00A0C398BC238152388C
	9E53781DA1CEE5787BB4398CDFCEC39952398C87D710BA2C566BCE9A48FDD3AA5C8B767C9E95491F721BD3B97CDDCD9B68DDD2A70CD0434D646BB87CE6E79FDD0F496F9A17D75405BCD7D6B97F4627192E2D54GEDE3974EF7A46FD5F1BE7FDBCB76837728ED3A1E3E9E5179A35F50B5949324996A42F22F2BC25EC11C37D795CD2FB3C85287D05AA8200B6334C7EA1C4B59CF672DD51AF1F9799ADE5F2A893DC52B57F6D9351A9D50765AB83D1A4A77B57C2B5120FC8FD6E3F9B473BC5E2473A6BC8F2B36071D65
	7BA93DE33603530DD917CD6DED308A72537522EE0837EA50EE13FFD0F17EEB0E723A4973B8F1D4394C4ED35AB0200B64327E050EC312A01C4AF1B57C3C4EE2AD66F91A62349A1D15F81E50E6F23ACA20E37A3CE0A572B772F43BB325FE487041F41F3795BAAD26D345BE3BDFCE4F995EAFD765A86B38034E7F37827F8EAD5A6B077DF6BF1B37D2594F2A691A4D90247152229D8E66E2DE21DC1FBBF21D7B4E9314BE8EE8938118AE1F580657E56620326FAE27EDFF7E8928E3A7103F4945F7F457507E2391E8DBB9
	7DC22BF35F5DE8C55F9DBA01BAFB72B27566F9BCE1D5765D17E9D9871CC0DD3F368E2F2BE71F23FF5FCA571802A10F3093D8DE56B63BFF7F298665C96672FCD123DC7653695D3A99C017FB92E53AF105AB19D83AE5F48D2A8C52D41DC4BBBC1D0DFCBBCFA25F8F3365F5A0A787692CA7318E6C24F7DAF9F1994E3874E7E16F8B3DB7D65893A47ABDFD63AF30FFECD4F9B05B45EFD2EE876E1B6B588E7C7AG511D1D67B4393844C53E3EA7455FF5B794AB7B44CFF4BDA490E8C3CE62BB856DAE855D0BC43F6F7A85
	3E21FBEAD4F93077073BF2711D028C2C05F56ADCFC27F8AC9BF195747BDDF2DFEF4CC9FCFFD5AD467757B4EE2F67A92E249B6AC7B762E2A82E9D42B60EBB3498F5A3F4D259CA94F097EFBF9367B19AF73DB09E11BD6E2DC9443A19EE32413D350CA03A166357ADDD4BF81462833B25EB999B8CB8579A82F105BA3E1759615B959B603DF5A3D9AF8B666982847AD0D13AA805BC0475B2BA96CA50C96F97FC1162D3C4F8E13D0C62C9B88362BACF215CD349ABA52E5D09FE877CACF39C2EF5FFF2D4F9303AB80F6E7B
	707AFBECDE64A93E3FE9906BE7B237432EB3F43F4969F1F9E52791F71062DAA1EC6238B7077D2E1164F1B1F2160968057D1C43723858BA1AA3791D25D25375D361BE5EC3BA0D351B610BEDD36C78E62DF0D61E306FF809FBF573E1B7533DFF5169D89D38022EF1781C1656E9EF0A77BFAD2D53334A30CE87C1DC3E9E4BBAFB29183F89701EFA57E5AB1C17B0017447B0BC36307756317C5D59CF4F5EDBC36099899074AFFCEF8DAE9DFE5CF7D3A3110B5D9033C64055751072E2F7C96AFA366934FA6836D56CBE7A
	730B24775193FE894017456FBFAB0ECF9564A96A4173A36E6077C51677B0399B61598E904D657EEE84F795779E354B0D77EF36E5CDC5391B0D962BA0B8136B2373D23989BFD38F4AEDB8A7151B44FB766098736E738AF7E5F3B97D84DDC2609989104AF5592CC21A049EE7F3875EBBF339AEEBD7B15C0A26993C8C686549BB9A9D7459BFDE2A8F61194A753956A0550744E79B309CDE6567E69499186C1570EC84A8CCC1598F748EDF8E13EA6C6500F7BE3F3A06178339285EC16EA5E6295C04DF078165FE6324D4
	EE92EFB2707BFECE5177DA3B7F1EF27C464E7390892F3157431F309EFAC29E6186DC1BBC72934ED12599F09DF3B35DEBD0EE84BBGF49811364202735FD6A34AF10B050E0F4DFD425D85797CFE34E020C962E95B7A10CF3189792C6C275F70C2D88F60E9C25A798348475F44679D86108F8BFFB468285F557673A8A0CDFEAF6675B23F272B319779BEC83F519D0230AFC0B0273D7582720D6CC53EB9B4FCC22D37E8B92FC1FEDFD38650D519B17D337C1EAF3D191F49CD4F4A8A04F0B8C09C27754EC2DEA9E66475
	F9161817B097B441D14F3B7278D9C396A89B8B66778FFEC7172F85F9C7CEA2345190CE82486034211311F72195F94F4F176BF96394645582F41ED6CC7F863F63AB5C0A3C9E1FCAE853A01C5FB7AE2F348FE96F1B063CEA7B10577482B1AF76DE7555C195577921B5244E0B3E81559B4D95E62D86E969D14FF8E7775A79AA277B10D6D5D616172C2ADAAA24EBB8ECCF17DFB512F44219B7D52E65B97123BD5F7155A317671A1F6C69963BCC37E6D5F9496AB5A80F2D1AECF2DE318A53BEDBAD771B496BD45C362623
	F6BE379CD9A065334DE83016AD2CB74320E4A10847A4C47A1DC765E5756AB1FBFEEBFB8B6536B92ED7F71DE634DFF45AE9332DA357F12FD52E53AD8A38B72DF6BD0F9D37732A28F0DDE617F759E997660E5C97AE643A4E77DE0DBD5F3136117BC214561EEE12F964697EF5421ECEF591765DE7F574215B94EC7B1E4836ADBDF6BE65B59767C3EF5648FD683786FBFE56952EFD6895231D766A6C116B981BAD5729B8C70E6B4C3167DFD6CC7A19C3DA0BB05F3331579E773449D95933314421C97C8E56BEB6167006
	E7G004F17F84F5D6C45A5AA67E30930BE711C5118F976B1C535003B32002C256E696BA1A30A862341206DA25F44D39A9CDBE4AE110EAD887F68BE9CDBBC1B29941F5807E373C7E859305FCF08FE5799C67F3D75702872903EA5B74A671F1A0C727927BFD29C399FAE2D0F3F5F52759AF2BFDCEEBF1F132A46F75E72FEBEA7C557CD5AF978E59AB67170216A1168C9523BEFCB5308F838BBA745FCFEBBDFBF706DE7BE9F8C4F08FE71FDEF378F163848A73EDF6C73F3275AFDFE1D002B19CB566C371A0DEACD175A
	E25DCDAF225536995B34B66165067935579929DF37FC951B17520FFEEF163D1311F855G1649B5E5AAEE3BDF09FE4FAD9BFD3F2F9AD59E4CCF67AEC53FDF4E77F214ACC53FA7973F30F15F97FC0CD98836E9994031DC7DA4DC7F779C40F15F3B745C30F088478124F05A5F51FDF8647DB95B06F77EACAAABD6A95E254A8B12259DF396470C5DB6BC3B70CFC3A8D3G6062ED48E76AB91429E090E55ACF5F851AA15C89E09BC45A4597D0A69F0E3B779C72CA9B6477841EC7DE65E74665E94E70FB82AFA0AF7FB348
	4BFA015046C1B885A07BAC52EAD418EFB1470D216F87E41FCC9B47BDE7C03A3033487FFFC6641F8A3832A1245DE6C27E1DC3487FDA931DE305F088C09427BDEAC6DE099CB7239BF9B51EC31F1F7B13FB73C7CB5DCCF75CA15D50B947764D874201929C1BDF7AADFDA78DBBA71DF7FA1A62A34FC9671D9A4DE84398082BBA0F7A2EF1B8C7528F704167C7B217BC63226F77E669CA5CCCF758A1DD04C446B888A71FD73253BD741DBA633C54CE41941FFBC1EA27357D3C2EC31C6D827FDEE2C04CBF81702997C6E227
	6B5CBC3772B6B75345B9244B76F014318842659ECAF61AC76BED1D0754CECBA83E45C3EA27905EA634C3DC38873FCB620218FF49E560EB170D44CE975CBC637BBAB7537D49A1DD33C4468E881BAED332D3C196BDE76CB2291D3E25F81F4B24F63A1B1F8D6E8FF12A4B51CEB7E40979EBGEF3BFCA4F63ADFF6EE5C48747D1D1B69BEF4C867AD11B1G422117AB5929E6B251BB6AF2291D6AA9BE6EF2291D7EBC896D1488F10DD7201D1E1CAC669F847870ABC6E227DBE4F79E0CCC5F7B5DCC176810AEC6A2E39204
	532FD0325332E9C46FFCCF291D56D0FC112754CE61D379FC9744F9F9221D9E1AA6661F82F855152E6D4472FB2A1437F7C05FF2A5760D190B785866CA6CDBF753B38643A19C8710F2A54EDFD5AD165F353E1EFE8FC27690E4DF05F926E7723D11D7613E0D94F65EB304BD2E42B9685FD46198BA78AAB6060E04E7BCG39B3DAF8EF2CAEF121DB6AD58EFB8D689900227B205632B322D56B44735821036CCE6A6C2B476595DD0DE38F230DFF3388386E2BD117A7EC380FA460EA344FFB14B69E4229G39DEE81F2786
	6576F9668C0EBDCA3CB04F8768B80F6CADEA7742F27D5A4DFABBC576ED644852EDF3C86763B552B3628FD2BF8A74127A71B10A8F71127AF1B2BFDFB79C62CA2EC1FD1FDA24B46E773C064961F70D32BC418A7888404581A4DFA3DD733C7E2CAB5BE4C872AA980332C54B4E4C9C19CDDFF7B35DCC073E3CF44CC84B6285BA5E2F99A3AD0B980AEF9CA3AD0B43FC2D209562C2393E4BAFA815C55135CC0E72EB156529D34037812E9BE068DAE9D9BC68F21C63A349AB70DAFEEE3A6C7C50115974D3B753D5B80CEB42
	1C68AEAF0BE4BAFE0A3ED6DA969FD3FC7C355232B8F3814BA289621A3DD1DF1DCA4CBF9870915EA369872F9F2FD406F1C25AE46119A1492B60BAF95E25002B81E839CEDA06AF385C8F53A9494BFA9DDF73B639D796B35CCC374BA15D7FAB6FDA0023A852B016782A43BCF8C854A82F2851C265AC6A0AD23C92CF85AC70D1620902D62E64BCD4D450D8F2681D135DGC9008DAF858A14C1F929A1598422880289A401E00EF0A447C3D287135D496E4A6E4EB2BB1BEC486B7E7EE73AE7E6F7C9960EF0D69D45EC7A7F
	3A7F3F3B7F7E3B7B1F1E691E6BE354B1F69BFE0C771429B7C636E18E6EB5CE3FB1328D1383348DC7C35C6A84DADF079C2BAD5A9354F2A4E9FF53924C659A1390DD4EC900E54015199059962B5C5D69A6A751ACEBDDA2DD33EB45671E76C4F54E523E870D58C140CE30F4B3707DE3AF506D0934DEE38C6F720CB3048F452C6F653A3A5369653A343D29C13ACE34166D492C8D52F522BDACDDG1F69A619F4F8A3E269000DE1BAFC9E31C9C0CFE7BA3C3BD9575B8306706E665E57E1FD2F662156F9F7CB348E4BDA22
	F578A62EBF4DB669B08777B366A4C1DFCAE267A2A3D6G748FC96C9E3DCB575BB6C378F71D3D2F4327FBB58F6D0C4F2EE89DBE54952D4317F1DD6DC812D1077390F38396E6BA5C077EF8BF20D3198EBF356AFADBEF8827DF01332346DD013323522D51BA3C579A2D43793836BB4A2443AF901BG58D42643DF901B8574C7CC07FB9769FABBE088BF13577BBAFC3DD773D06B7C4C22E89DBE3FA8DA079F60FB91CB130DBADCE943F777815B19CCF55800D88D508D49D40776FCDDEF5F9B420F5AFADF0753FAB58F35
	4E0F66D39D3E4E767E0F4F27BAACE2E5F9E4632579BACFDDA25F42CD2677901363751146615AC05F64C89F69C9441312A3FD243ACDECDD8C62387E2C6F44FC8F5359DF7B769D7C6D27050776B7FE176EEF16D8FC2335B493606F747E663A284F337F0D7E0A766E4C8E1E3B603374EF0B6D85FC89BB4367059D6C1984485DBA003D2BD54A76218CE06BA62578CC8268D1F0CD984056CD3E0DDE9758799DDD9748E07C6F9461DA504067F26DF09D9CC8F5950ABA53A63EB63E7A927946DEB41FEA83A771CC1CB383A3
	ED2391F16F40C85BF8132D3904A1EEBA2B6FE367E365DFB6C87B8661208B1DADF01F69FE5F2D25EB577EA6415FB4836F18C16C3CE49C5BA78DB21E4B56E0B87BC5B4045F3E826740AC3CECE7EA443A34738CF15DEF8654B91369219C770EF0B7017FF593455E4333B78A00BE4830F0BBFD6E5F403094447AG1D423037F16DEBA4501398F6E79B654DE0589308E58F06FC87D36C6B0E68B6195C2137439F8D6153D760DC20F02F662156799FDD34CD8A189E6E6B22ED3213E156CE5AA6B58C1B4B74FA06E12BD91B
	74E558760E68B69957C1F9D3D83ABFF550B6994B30CAAC4BB3030DED7236417F7E40906E93F3CD697236494D3D1A07DA67BE9C1D3FA7E6D16C9A0E4E5FBF31F43FC53FFBA668E6AE53572768F7E727401C11C2318731CD6CC057B3AC9FF56D863AAF43FEE4675B24B06CB4E2E3011E42304D789CFDA650B9B7D36C95BF65DD47303FA3D686F4BD439A8351FDEC7EB93D8DED067060C06F376728DE4DC3EBBB1976B1B75343EBB26DE38A435E77619E9F20C7B26C101F763BC7983656C5F5BD05E13BDD345F4DE558
	D0D7F43FABF4D35E4537D06C301B76BB7BAD54FFC8BA95FB0F663D27483BD54DAEA120BEE319483EE7D941F8BFBCC75238D91AE5B2CDD34F52DC970AADBFADE4167FE00872BA996F2C10D93E251926D1D89ACBE7EC796BBB4D72CBBBA9EFDF466B6AB44B6FE0E98632B419D93165AF331865EF31D05EB446FB52E216AF31B423D91AF5F93165FF1FE716DF1BC7F99FE33CD76519658FE6E92630B4B6DBEC795F584C724BED14F786638D5A4C72EFE0E9B219CDFD0EF3FD762D46FEBE1EFCEE085E2F9A42A7E3BE17
	393C7DDC6E55BC54BA7F8B1FCB2E03BA975C4A465EAD343F6DE458CB08B5G2DB02C91ED60FA2053993652CA650DE5D808FDCBE2824386D9293CD937327DFDBBAE4D8FFF739279B616DE1A7FBE8E6FDF385428FBBB449727C65D5B15D27FFDB5443527527A6EE15F1B4FC80D7655B3E3E0EABE8EBB3E2BF3DB647ED39F622B8D381A7F7BF83FC4682D90576FB61A7F69D65D1E5D06701CF06F5BF6DE2FFE23D23531E968D74F067AF237D33BABE37E5FE206BD0E189D68B206CD46351AFA201D8CAB31D15EG430E
	A2B69068F4062D44E77823011EF0BB9DFF1E7CB27658755EA6737981D785B996F7E2F36C33BE82F87FB19DE44FE47273BAAEFC2E461768B72D9C72DCAEBF041E6910583E3A9B7D29B26FEF0FFD2E47F8744DEAC05609A1EC9FD45B8A786B16E44FF9492F885EF2D6D315E7FD9BFD7F21AFAB4343EDBD176165B6E2AF66F356B2D196F967F75490FA66C69A6EC5D063DFC07F7B91081BB4043ED7D1007361DC12FEA84DBF3FDB9DE8F3096A07824F8F0CEFEAC74FFCF91D340C6E21D4C78DDD31EDE0C21659864607
	F49B58E609ED8315B87606C1F6DFA67F5976CFCDEDC0DE1BBDA285BDE483GAD572CF6BA6F0DE2FC1B3B311D9F51F6B2A06DACD67FE3F16CBBED6E0CEDBB89281755431E4B5DB80C663F475F3D6D9C77D35BB9B20C1661FDFF4FE578C40A361D820936CB7BB0EABB36965DF67271B9D6BF08CBE165DB6C6FD92FDB58D9550F8D23B697C4AC93E8EEB8ADF7092BE75BF1228FDCGBCBB99DF253BE73E96B72D1BF3B8ADFB46B973BE502D3219FEB6E42657B41B691ACEB3BDB0025EEBB15357C650CDF9E67A3E887A
	044DCC8F0D20CBB7456EB35EAD66BED30C5FE9D3F5756C161EEDE34E1658672D6462D93C8A682F8F53E12B055827AC1E8B09C125CAF204021526734B86B0DFF868F0EAD3752DFAF97AD9FBEE3B5FDBE93E23075379F9FCA91121791E06704952AB60FB76EA9E6ABCD90FEB3BE47F4C1461F46EFC38084E31B3199EBCF652560BC7D0BA08341D519F9645AAABC9FF797431529EAB0FCB5D9B935BA65FBDEF366916733A8D9D0FE32E1C5EE6BE1B6ADD1CA70F009E9AC6D0DB1E31441C47ADEDFA9E55361E73705848
	7EFE1FA03BBCFE7A4C6061F60E45FF93E34C5D07E3AE19D37A0D207DA323C36719597EB9F741678C5FE95B99353C2CDDBA1F6FA278B6F3BA5F2B676361D353FE5B46F3666F425611DF7C7E2860D70205C72B8F9C2C5776543613BD13455BE91E55D9FA1EF3BB62AF2B5722733D5596BFDF22D5675B9AD7FEEA5A8D1D66F2BF102D4B39BFAE9D2BE92D9D9B3A57557E4A4375156420BCED5F26E1DF650E4E786BB9F7014E37569FFF797C7E785BF23FE456891F2B677906AF7EBC777AAFCEA77B7374FC163A625709
	5B50878745452726CDF01B6B791421DF7EEC3950FED6B57E9ECB74DE593FD8AF44232DCB61181F8A63C07A886A5BCCA96AF95C79AB1E1D9B35478F4631F2D994547DFBE9F6637E3DA9A01F6C277C080DB9F38DE34038A5F16CD1AE5177A81FC4FBA835D3BD2DB57451870A786E742DBDA7D57738CD6EA07E59857638112AE84FBD2761792444AFDF1AC667398C1C0314B4FDDE3B9E4229E9BD4FF323A252BC86745488CC1DFFAD45787E8644F1F750B979A744D682EDE758CD08D5GDD4F308F91BBC3E806BDFD18
	4E67FD99B62F965F8183FA6C9DF4DEDA033CDE0FDFBCAE53B5BABBE2A4FE82CB57D8CCF45282E96A14D62FF8DC124906FAB5FD9BCBBF15253F3758BC5FDDE7985B161718674B2DA5780CGF8B3611A1E0AEB5E0729EDEE3C937CF138CE409506AB65CEF6DE451168B9719E4358F62058FCB6C9FFC62BE97198E0AD6D9F8E47EB172A1DAC2E254F4747C0F9B6BECA300ADAE3BFA0F2EF002B105481631BCD714BB855F7556353CD71E4601BE3B1463FE40A1FEF60D76DF4D92DF1DDEDB614AB7B2E6EEDD4350B2D35
	7499529C768E4836DA7A8C0954E1956AA879585E459CB772ADEE3E422DBF661115106065770BDEAF2F1E54CDEE94F8B242CADED1818D85C4BFDFA1AA02479B8432CE1182BCB9AB48630058A5F50AA0ABBAED4368FD1224106F0B8794AEDFCDE0C0969E9760564F6FD238857F8C09A1518901AA90CE828B0F067CFE0CDBC884858816CBBED28FA5A1E1EB39578B3F87248D8AB77F28989495054B418256C8AED350D697EA74F9A0E3D1E991491D0FD6D2C6AF2A74BBF5AA274A63D5C4F9972FF016839250F5EE2905
	475C72AABC410020B85C24G86BC12E561D131C99603EE054B2E8CFB20E0CB0F0ABE29D9642BE4494757F802GE55709FEA7ADFAD06616182C7471E1252FCDFA65D8504AAD286656100E85FA8533055BFFE705208847C4190C09D5DCEEC072C0A46055DCF6907F4C8F127EDC432DECBA2A5EE8094EF2C511BD0DA185886E139A12D3D1C871F80B8E8AD0C8F283363B064B768BBEE83665EA34D7703B0A6A004B6F5A9D8E9043ACAF43C19F97E0213CAB303CC4CAA1133262217CCD9B8E4D98E365ACD5DC1E97E618
	AA4121C8F2B5172B3809C2A42FB39C78D19514036FE812350DEC903716D51FD4369047F0DA6833615B7AAD1939248ACCB9ACF3C518120CC305AC05F33FDB10A1ABD12E06CB00ACB732382BBED7B6CFBE302A4142E5E915BC47F5F2DF04417E96B489BE0F3795829520DB4995A4B11B01EC41A36320D8791899D1C6E19554C7F9692D0943277DB0FB96882B6266B78B5E1088C5DA2A27BA9C72B50A7235A387FD36E8E046EFC80983243AD4F2AE983EBF88B74BBE904EC75419C63122978E18E89FF4773C1F3168BA
	AB74D53511003548A5CA3E227226A6986F19GE8B1907F9993CF5294C624D99ECE7E6450739FB70205D5F0D61F5D0E3F4578DBC2FE0B91A9C62438841AEDB6576D3F4878B02DB31AA5925B088D05C9686BB09450E6F6EE326ED8F57552446DA18B74A752B45E00DB285614DC43ADEA444F901023B82047AD5595003CE03F96879877DA9DF790DC0D2D8B888EE26239CEC1BE0B92C899F61F62F21BE4D7A3E6GBD77D455A94896A9923D27064BD35CA20CFE445694EE15AE7A08GFDCE0BD0FB8777B17C8F60BF2C
	69AE44GD4B1B2086119EAC1B2EE28A3A499C14B9D0A870685F24288104B18D346EBDE990C8FF8281929B549A0A879E1282A15BD70AB104533A75E382BF2D82AE392D8CAB537C852A330F48B01FFF934C7D51E70628BA3CFD72C34F016EAEE019BED9E945EC24ED1A4FCD919F03D8383F73D24403CC04B3DCB61326CF06D0121D50F019A98A272F99E8B4E43709672AA41D8CAC16C20B6EFB96322482CF5B5D2BC8EC9E75052ECB8A491F4E37458C2602D2C07936A9B72D3C4FE6C48D4023F2597ED5F046BBB4D2E
	4FE87409302126DCD4CD75727F9F68C27CEFF5B10163AC0DE43A7678C2DE7094A4BFDFDD813D06F4G989DF9F264A28C292A75E154C25213B034C235CCE387534CB327DE62711F6AC330C11EF40DEABF190CB31A376F323EBA42EA613221DF28DFCA015915FC6E843A072CF693ABB8A5EA5767D44F8D7F195A2A6AD7CE45523BEACB2A65AC7E5F5BD219C6DF9BB6D945D8051BD26F96F9134B6A8972AEF006DC3CA271325888EEA66F7173253C4F63A71E8F6F8C0990D5489F710AC2D064038298C62B94127996C9
	BE4B8B6083C2C52410D2489F97F9C10694128CA398CC1690598A329C123FD97443506A908B796AA6424C3B0994920D1E968C2A4182BE20E660A00FB89C8A2FC496D4D078E3A4F03AAE9488C832D296F2389D0522B3D408158CE986452EDFF343A406557B9A0E7B2AEB27E6DD8D49695C5CEB9C96FABF93F90F32F434FA8F33D17B4BF3733256F064FE67C55C97F68670F638546F87FDF50AFC6C7E1870CBF9A0304F6B14AB1DB09248C06C5AD55E285479847040856F59F23F7390F463FD2D8A795411766D21D7CE
	9179C11AC60BD4637E0C6FB87DE260798FD0CB878846BC14561FEFGG140D81GD0CB818294G94G88G88GADFBB0B646BC14561FEFGG140D81G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG59EFGGGG
**end of data**/
}
}