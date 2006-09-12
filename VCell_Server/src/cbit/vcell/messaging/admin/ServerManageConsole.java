package cbit.vcell.messaging.admin;
import java.text.SimpleDateFormat;
import cbit.vcell.server.ServerInfo;
import cbit.gui.PropertyLoader;
import cbit.util.KeyValue;
import cbit.vcell.server.VCellServer;
import cbit.vcell.messaging.server.RpcDbServerProxy;
import cbit.util.BigString;
import cbit.util.KeyValue;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.util.User;
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
	private SessionLog log = null;
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
		+ PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty) 
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
private cbit.vcell.messaging.server.RpcDbServerProxy getDbProxy(User user) throws JMSException, cbit.util.DataAccessException, java.rmi.RemoteException {
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
	int rmiPort = PropertyLoader.getIntProperty(PropertyLoader.rmiPortRegistry, 1099);
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
private RpcSimServerProxy getSimProxy(User user) throws JMSException, cbit.util.DataAccessException, java.rmi.RemoteException {
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
		log = new cbit.util.StdoutSessionLog("Console");
		PropertyLoader.loadProperties();
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
public void resubmitSimulation(String userid, KeyValue simKey) {
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
public void stopSimulation(String userid, KeyValue simKey) {
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
	D0CB838494G88G88G3C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1543D8FD8144576B8CEC545ADAAAA6AC3C5C5453DD2D1D1610D0A0A0A0A0A120A0A0A0A5B259B95372828CBC5357CD151C5C5C525C4C545C4A385C1C5C5858145C2A15D002EF4E50185D6165DE5F711C5D13FE7E65EB37B6E3B6F822E51737B79F8F8F74E1C19F3661CB3FF5E19F9E794394B7C26262567AA94E9D395423F4DE98A056E22B4C57965571EC01C173ED929907DDB8EB0D731C31589CFBF08AB
	7C32C51923E8BA108561E08847FC5022DC843F67ABF668725CA0C3A7FC12CE25A95E094973991D4F3EA348E7BA8DEF9E480667EC00904011E726219978B78E648978D8810F10162ED044D31AA38379822E9E426DGFEG4186C69F894F99147339DBAB843A36CB839449CF1952E711FCC4F9A2949BD95AA661D92078A2AD45C8F8DDCE65A4E9BCA09C86C08A1F21F8F2D3B9BCAB1B7AFAF6F4F6E8AD6B0EF6EAF43AF57D9A33D95D2355772CD3F775EB75DB379E54180EEACC7B54FAF50F264620B79BF49A4BG49
	773501AC1A8F6481E1050960D20A882F9088C781A4F07C3F5790FCAD7CEE83301830FC5BD7D78A72379E3FC7915F73580CB9CE4A67E9E2658AB6594A973236A363DBEA0B0B7A084E13A0AE575C2CAC82E8G5081F882AC26BE60FDE4ADBC2BBBACB5067EFE033E5AE85C256BB26DB3F4E9F470FB6BD60891F087B41D86D317C2416CFC55C0310CBC2381CBB7693453B11D8414214D838F5C2E083D7B4A7469B2BA098DB81E6EEFE63A88B55BF442F847B9659DEB3677C9FB5EAD8AB1EF453B9B26CF15611D7F5206
	69C9C2BE19969B6F8A8B31350E5A3A90FE13F4A4BD438FD17C08884F7C73EB6AB3A46C85F14996740D5659A8CBC3660D0A1C527F1355034FA31E692D03CC866320D416D7FEA0F97B8EB21EBFF3D9269793FC65D19B1E497289151104E3A12EE238D9497C6B4AFED2DF3D0FB7AB03GA2G62G94A34D4AFC0012917431570F2C9F270FB529CD16DD03DADD174624E0E92F6CDB88CF0D55E2D26F551BADEAFD2726D9ED522ABBF49A33C220393E0F682043AE1DBF757DCE6031C723571854962DC18F392B7BB5960D
	494CE8EE6B1BCBE8CC9A9A2B52C2AD37287B0D8A4CF7F25FFCF86A54E64B2F46AE35C5E323E0718F74911B9CD5B7C30E10817CE6FAF9D4CF745584656F8530F2BDD468C8FEBFEBCCFA0DEE6B56860321EF5028A2A5D1C40C90BD7F07369D1E705BFF84F57C10016032CFC0BBFE8273B1686609722955F692715526E12CE326B5A4CD8550371EC03F7920957D26F656ED0A62972F10759BAF70E137934CDFFCCEC86B4F891AAF11AD6C84367719EBDAE56ACD4589797AB49A344A24E1BC16487248B84D7CE78B75C9
	0AE3FA7E117687F1A0C3AAC07EC95475AD3A82663B3B54E68DFAEB23899CCD53E553BC13734D9F3C61D9833084E09940CA0085GBFGEC86588230066427A02D0AC23193208F208EE08F009AE090E09BC0A340F600FD44C781FE81B88870AB40A9004B81A6817C86F088E0AF0096008C8C6E876887B0G7C4E75B82B1848D78EE5EB83F08788840886C885A8BC857D8300854097A09CA091A0572DD9D98950856089908C908B10F9D6333298C085B082608F9089108C10FFF6333296C087608D908A908F10FDCE33
	329C208D409DA090A07A9C74254F57B4250F5F168BA4E15479ECC16F08DFA368DFAE8F3449B6C157BB855B5408EC32D7205DA758C4EC0796117EFF976854023D1007D630B15A83710392D9CE88F69DCF59AF9FA75D24F1525DBFCE3A7FBF8273AD2F352763DBCAC9F82A108F79BDC304A76D79ACA7F94C21FC950AD58A6603241DA1BECB5AAD74551D82AD714F55G241FAC85A883A0E3F4744B1F85BAB2667ED1440378AAE92744FEAA75FF82441FB70E336C1B47C937E51CF4F57F9F7001098256C7787593DBEB
	0EEA7416DA0DD1236F029FE6A1EED2BF7197E17C52ED60E9EE23F8A30CF7EAE000E166783B7B094F28AD961336E35022B92831D860354396FFBF65E59E6CB05A06CA486B11FE622706A1986FF07AA77A49D821532057EBE8E745138871CF52310AC627698721EB8C03FA8B43CF5113773ABE4D30B0D6FAC1BFCDC146BA3AC1B26EE26FCAC1EAD2CF0CA6EDBFF401E46826E8F0A763185926BC110ED221FDA76FFC8590D76E0E63112115B89EA9BCF633A26C30B1DDEECC6D7357CB523D5D5938A0405DB69EE1B205
	9B64E5FF0A0E19843C19497A375FC8DD15F9ED2375F13E423A0A544FDF4D9AD38D57959D1B51E1AA2913EC1C50EDA2BCA3C016D800E4F79CA76CB612F89C138983049A03CE473202B85539C4B78F2F499515AF7BDC269B1BAF158D64907BEA8686B5B054A5827C4CED30F5E01EA42ED930870B5FB5C06C21930DABFB4FA3BCBB0A713D5A97788583C48BFCF34FE33C995D77BFA2DDB004A34F3351A55A513D4A69AA4EEFD6B61DEF23B31EAF263BF89D520581BE86A075FCC61763A1265B331E678778A6G238723
	8B3223D316A01D62AF4D4ABC000A3FB0BA6BDF44F4D3B8DD846063G9497B03A4A8B44F41715A21D87607DAE30510559516D5E00F4599742B869C29BDD6D05E23AAFB81D976083C4F491F6F43717A1DD2EA70C21BCEDF48D1EE23A0EF2247381FC1008AE4A0EEE61C624AB3888F48250FA9123733EC8CC778A270B87FCAAC07E450C2E6DE2B15D351B104E9F7021975B68E26D68FE590CF4C517B4AB2BG3AAEE1F4FE170869F2AB10AE81708AAF50219723EB7792533D4C6982819F8E90AF5095DEAA263BE68B52
	69G6FFE290D4E5F0E2EF1AB52A583BE7BB2F8A73D0C5169AE93534D5B467D9E7011G0982DD5165E23A97B91D9570DE175B68026C687ED2497D549B7C545BC6D761AD265B3E9D695C816F8B906A4D68D26C6894BB102E618A50B1007B950CAE7C8AB1DDB8274B3D927C6ACADBFE8DD70A692CBB11AE60CA762E99F6A5230B31235BD005F4D97F876DB7C0557FB13A117F9353FD4A69A2819F8F106943682AFC44F4A1555C1F819F64E3230B3223536DC23A422B204E8234DD4568FC2F92531554A0DDA260B32F86
	BF39DA507355E23AF7B91D9B60FDGC2843A94BB3A5BEA112E6A9A28C3G56EB98DD48B5821DAF219B2863FA71E56F428570AC81D081AC6579D56D66FE8578C800C4DF16DF7135E23E19FB38DF815E8BA068DA413E829D6B43BE7F0E742BF4EE72087A285A3A4EBC84E3032DDBFF5ACD67B5687C2355C47ABE5247A6DF0B730CDFDBC81FD5FC9D34D7GED57E19F7C5CAC1CE7746E8BD0443EDBBA5D59BC2A65BAE1CE60BADB9F9C19D4BD0D7509C1E642B38662922E437E70018171DCC73559EC685452E996212F
	521944FDB4438D18E64233DBEBB2DB7642A04A0A7D5A100948CB66E18434200FD7B5DB9463182BF55629ABF0FE6F44EE319EC2671CC8D7BA51C32AD07E7C6BC5732D8655F4B6CFF6E1B14E5935C13CB7G6B739FD70A7B7CC840A7836479B5AB59586363628D70D4D3715631C7134654EDB075EB3A0A0825572907AC6B6878F2DDB50D2EA33F57209E1E7D110C314DF4ECF35022368C1A9BE1943AEB50E2B1688FD7CB72EBF01E6136B54DDCA7B74E2FC2F9EF3FD1D1F96AFF69F2F3509EF3FA530B7D18AED4FEB6
	1D44DD5611118C40745278A36A458FE8E27CD0AF9D05D23D645CGE3449BF0AE7E51FCA20359E2B012F11B36D363D211188C996BD006FA485F6D8614E173CAB999C2388C4F55D4D399E0F4FBA6C2A8F0AE759EAEC7A470483C915AE6BAE6DEB5AC9EB3D7823E974093406FC67413256B49BBE8E72FDA5F23D9A7585E236ABBD2BF559D9D1A2EA635DEF33806C6D33E66A2421871B52EC73EA110DF520DD87E04DCB95FAC3A89F93E3C7E4CFC7353DFF689B65CE5B0D84C709AE4F4D1012C9C65A5D80ECA1051F293
	16E35DEC39F28472F2DCD3F2E66518B4D803FEF00685E165C86165888399D3F8B9DE55084BD1628F7D904008BF1663F0A919A350DAB47D6B489FD25DB54C648E45588B71C2A1047EC3C9669B606D41B45C225612774A1ADECDE75F613D520C24A560B959643F3F142FCF00EC917EA87F2C9EB97995FF457A346F3FBBD006DF86B503700E640288028C068DA8C3AE645F70D714E1F21F1C8C6EDC06B717F399F62B353AB312E1E5994A608379C7F19954DAB999327E06B2DCEE6236B8B028571F11ADDE66B264C37E
	2A3F218CEF6A64E47064B2FC5541E5B8889554681AA2043E2F1C377110FF8C176142A3F2B264CCC2991EDFD60DB254987A0DBA0D45C5F388F26C5C08F294820F36C9A8478C3D1C9C5E1330DE7DED236B75EA79C2DE06EA1D0E7540E7D20674CDD806G10AF0E176103ECE99B1797G6363GAC03F6531935F1591BAB319CFB8CE75057BE4E65EF8259BC82D07E68D9F272078520AF04655B6A04462C311CC97BFAECB34A908D79E75F0C7D6C62BCF1BFDB8BF88F00C000301BD10F7F58CC66D87B8D03E64DBA7A37
	C6276D6CF35461BE9207AA94F4D8371568D0BC9623834E437B4479C8CBE04B086BF0FB854A9F83F2655C0272FFEC144ADF8FF1BAG37DBD0FE7F8A5765DFE75CAE488FC39D578A407DD83105370DA0DB54AD6887D9AB6446DCD937227CBDDB4E4C0F9397939B98E08C5CE068B983BF8E67726703EC2ADBD17EAF9C6AA199EBF85E0AFE5C2D5FA570E7730D2ECA01FA34EE65EDAB70083995FD6107FE29AF648442789920BE90F57966D657FDC1B3E71B28BE8E6AAC276D89CC7EED5BD07EF6104BA7907568FDC05A
	A6C7C1DCB2177D46ED2E3749C1433BF85FB658512F35545244AE7773DF73B2E45D562C2C3A8D4B700DC5DA86AB44795E063EB0F5816F9B8E1241234BE288F25CD509F284810F84AEC7FF1AD40E025B61FD7FF614635C62AA1423CE5FC524E0EAB9BDB1188C5D5BD106D6485F6BF6144137C4ACC384601381726EC0FBBE335DF5FB5EBCF337024C8D68AC502E325669003ACB0B5D14CBB6DD33834BD18CB2F65D415FA5748E759B4A614B4B6135634C5A2767F757CA4BF2C67D6D3FF9B902C04684DE0E0B564B5923
	A088FD625E52FDD2B98E769A06E08CF3862A95F4FB43CE14299C786902D0F7D9D5842F55436B07DA27BD26A9DA43E419D5311C771BF66BB307770AE8ED799F2A427C7DA16F58A07EBE5FABB74E4B3D9379BFD2653A8FA6D771B625D6EBB62AAD1D3DE7746E30349A4BD08472355F09E51823175A4D8762C2F9994E2DF63D8C3BD7B758599C7E1E2149D9D97E414B9285F2E65D457BFA13DCD92A6EC29F5CB1D2A711674CFD6F0ADDA80B8A78F8F2D9FC5224751A4892FE976AB5F55719556BCD1A9AC9D9DC2C56C2
	B9FE2B61E3C010B167EEAC4752D2E9B96AA1CEF7B716A32CE63A2D9C02ECFB1A4918482469B6E94C3DE750564C2BC5195C00DF8817697D8AB9191238CC4335E7265BCB6A48DC2F462AF5712DGFD63A5AEFFE6B03CBB85237C5F9F114AEF01B81FE014FFFB5D19497FC1FA8D55FF3F6128E63749505F20B51F517B4D55F5598A3617A490E40C67F2266DA66B7616DE2D190D91935324E3447C49D07FGD413B14D1A5DC446FE3BB9710F2B2BD097375F2888196EA9BB7F5BBB49A7DDB7194D7BFACF962D8D5C5011
	91F7834E7FEAF7234E6301A67F6F28731F8DD21D2BA04E7AF714ABEA771969BC7F07AA5A27683B7651B501B319BF7EF18F164193648B67E558E814ABC3AAAF03779E473A3866608A22EFA613631B2EE86ED658F7E9A27B16F36EE9D6965E6338C732C202EB00B05B4FEB9AA4B2375F43767392F998FE04620DA2BCABE366A07A149B44455F03FBD05F9CA4BED49F82E3A40091GEF00E0006890DC6F7946C476AC0D3A47F72F9E5AC53DDAF750EE738A6E35B5933EFD1AE1A1FCAF5D37C1771BA8F06F453EA1226B
	A46019FDAF68822092206DDE14214AB2268CF5D6D999181EAFAD166E8F9F3957DE37BE12B0CBF73DC33A2512F028A49D7303B987C939E4567318BB90DB52BA7CE83A388EC7C3DEA9G7977B5ABCBG583A5FD74AACC11E7A7B50EEB7D9493A229B04FD81028122G1281F26E0777447BD1EF17D8360F29B76213AAC38D606A8D063E9A69FEA22E0BDFFFF4CD071D8E6924BAD45DAF71F32A1BED4B243A51811D97007F7D281B2B8AD1B7217763DE53996BDC13F311C3BA291C4992B9198C7A15A8C3F6A84A70000B
	B2BC6D10EE1DA4DC9BAA67EFBFE4AD1177B795DFBE62FA3DFBD02AD732CF208D72B50622DEFF9F46EFDDBCC2F12DB9FC1834A1E1900E81C862E5BD3A5E35323ABB24136ABB6B81B9FDAFDB007AAEF8GE558DA621A8C758E6924B2344A4A20190DB298398CDF3BA8C3F669D8B284BCA0E7739D4D0B65EDCE5FDBF6992C023D67F4C86D9D8679458324BC00761E5B0D6549F9904B733003DC63AB4F14B14BD37F201CCEEBFBD006F6AE43608657E4B847A11DD4869FD999927BD006A0AE431AB257E4586C10CEAAC3
	022C8CBEDA14C171904A703A0BB23CBF268C958F494930309FE5E862B2DCDA6EE29F6A10CEAA03072C8C0F9CC1997C388CF59BDD136120C3BA298CB132B2D469F14FCD9217A1EB13EBB2ACF4C8A71521A8CC2E9F5CBCD32E7ED6812DAA8C6B6F2AD9C4CE52AE6842D04EC89765FCF5CCB97D4364F4251C0DB204F2995CB63BA643458E699C7A62B039B6711EBA3297FE0CFDC36B4C861345C2A63C2131BC883F590EDA54777C05E2FDE7BF4C46E49570ECF89875FDB08F4B5A75B0760F4B9753EF6FA09C8C9079B0
	6A202642B59D7C6210CE2A037807657CE50BC1AABF99B72542B3679114FF6DG4ADF74885FCBF6123ED3C0588A6075884ABFED0BEB7267BA24F368579E1173231DAB305E07F1991EF2D10618B1E5C8111521F4966F8F9FC599CEEEF5CD86CF07F48E7D6123F2F6B475C96D588EF46EGBE0F229DBB7B510EC10F229DCB0D440E89A49C5E2C4C8BC7792B3739A6FF23C3BA297CA561326377C639F62B9EE85B42D17E278F201EADDC4E4CCA57641C6710CEAAE7202C1CF78C4849998E34B1DC4ECD03A8E79217B344
	C5B99F9FD34E22476464DCF2D2369FGDA55E3A827FF9A6F879EC3B9F55BDD137318C3BA07FE60B139B6F861AEC2A7B3AE95CD7FE219AEDAA5770E978A79C671B23DD0026FA2893CCC6BF738D6266D8E6924E5AAF8DCF63C2FC73DD6BC0EB2FC62228CE9BB471241A8ABC378FA1441034BB049A12F7149B059A15DBAC9B86CF139F46AAA5778999C52B90C3FE44B2C6F65750E1739305AB599B6B824F328F7CF4855BB3F93F2F3B4D5C02BFA82FDF4379E6554BD01F23E63221C1F0EA9277F93F23A5AE96263AFAE
	43B53BDC1361E607F48E63AFD9992624713910881461F00DEBB274BB24137AE8ED041C3D96ED125A2B8D685CG3CA350DE3F16221C01DC4E3CDA57647C5EA11D43B8ADC2CED7AB36203DD2398C31AE4A705E18B214BFA9272BE766CAF5C546988DC05B7EA41F8B626FE156A7D1CE3FBAA9BFA9484BF97B186902E465FC7B14F4AC9481F409G0A27D04E42F46CA7721E423110B9031E57G61DEG3727F07E743BE3E47E54874241G11G89G59CFB7AB0BG9A1E46795337ACE4DDF87473AD7003CE15017F1419
	C4955A415D2E597704C3BA29FD3B1E16531B07CEEA5F9120738388FE9A75F636817DB072E934EF599E57642CF6C8A72D331911F2F5C1E7C49972A11E1D1961CD5BB395042D9C77BE45F9C2580F636E205F2884C2B81C6322AC8417A070E232D987497AD9EAA41671EC9AA6FB71D9F866205CD994E2C81548FEBA40F82C9C1307ABE7DFA0B01FCD98F47E4D720703725FACFFBAA87F4D721703F3999E72901F60027199C7C77F26D9F95479B74D3F8EB91733EDC8DE4E0EA1F9B9FB07C61753B864CC4EA5D467CFAE
	1CC3531B4D9AB3DD9029D5DB54C25CBB9672ED3F43F7567B557D9A41DE73959145B3851ABA0B2126F4830E05BFD963643BAEB2A32FE3F5742CE352BACA3E274DFF26D9D9728C5651AF5331FE54BF03ED1BD2C95AB6B7887BG84BD03FE1C1EAD5746C6BC43FCB7961EC94F587B31705D3A5539BD022D7276384FCA3EABB39B86CD646B99FB1DBFEC4D63F1368611EC6BA02A933E672669ADEAD30F4641E64F5174AC4ED97AD72D4E6C4A6C6279231DDDB6E23B983DD8CE67194F427B723328F373B254F949332873
	4665C4673D90F68370F996F5FEDD211C4E031EE5FA8E07E74C3376BAE772D52D3313EF9336936615F279A58AF9A826B4ABF327486517EC1FDF85362F6F8F72791EA9D8963D154E77C0588A60B5854B52B1BCDA9F9DB045318DE4EB4F9547C8FE21D3585A331263F7D3FC2C884F56241F226BC9A41C8AF16DD1382EFE5B7A9962B2A8D0A7058B64648A0FE2724440B3B1CACEA783A5627C4AF8FEC7E64B6527F80E65118B4F226764725BD8EA2763D27429G077773CAC85F8EE0F98EFDEACF870E25BC1FC3BBFC59
	C574908E61D8006467508E8B3B09FC594F5B4BD0A0044B1FF7348313AF45DE3EB2AC6F5E9E12DF3DA4DD1B90B6BA4DAFF003B83F8D25185FC7FDA4BFF7C9BA9FA19C68B43F76B23B7C38FC57EAC9FEE112F451C2B841E9FE3965F67915E3FE0B7BC9FE2912F4B9AF30F0618B4E720B304FEF836677449112DF05A4DD0390EEF71A5F48C6317FED647ED727A779D904F49E4253D7F886B94DEF67A6BB79B6E29DB74C44BA9E7E827A5666EC62DB29904E0DEED696C723EF2D1DA5677B35510CE7EB34B35E991B6DF8
	7376AAC736AE6904FC5C1C66F70FDDFE256558EF7654B9D94FBE58E9B268F442FAB6AB774F8B31CEF9C7E3393B9711F2C7C2B89EA015173BB50F48197B227CB82EC8864F5A30AB685CFE650B2C8DCB63786BA83EDE04E7ED5BEA0AA7E19544053C08ED5B318A3B72EE46B28C98308C79AFE1993653F9DC9504F5G6EAF7139AFFA56216FCB72E58812418BFB06CE51F666A5E9990ED3FC54CB52B2E41E42B244C2DC554BFC3F5C96F1994ACA5087EAD74879007F4BCC1E90F8C63EAC571E26ED3553894FEF132C0F
	4689F9A443B3ABC6AE3F49F679157176793E595276B99F52D78128E230FD3E052F7F6AE2508EC7F5C48F7E908E85080EC1BB745276A5B1C65E8E0AD71C59612F542E392FC86DF0BB45973CA23543E6A35A2198623CDEC1BBD8375955C56ECB66C6ACC372ABD80657FEA1F9943F5A2C2C85E8FB954B90F50036C12F5A4B6AA104FDDFF5D6EF37D55A7166630CF786787A792B48FBB7BD23A00104DF037155EB48FB5B201CFDCBDEE3BCEB05E76BEBB699986F8F6DF9EFC15E3F1E6436FB8DF987AB286DA09C8A904D
	F95F1CA6BB3E92F8A9E21D157B266DF63E3581F9BF368A5B5E5CD8649D311A70EE00F09740C8AC720EF158A7C240DB0627BD6F6E9DF63CF93FF9C4AF17DF0010DF2853322C5DE9179F6FB71F247BF222A4696247146F0D1D62FEEE83477BD459590B57EFF3AF6113A249B77BF596AEF85D195C072A6D64667D5F0ED3E8037257518655E94486BA883B83783E0EB678196AACD842A3C28847BA653DF4179DEF5E7F551A64EC10A464137906337C1E374FEFB36697AD6B1FF9C2BE45702CFAC32E6D3B32C6EC03CD8A
	EC7B5E5CA0ED7B483C078A7268FD835B3E46CD28BF37B7D0FF6574DB459088C781A43C017A7B2F439E9FDAEF5E94464E70ACFAD3CE3E46DA31FC64BCE6165FEA07F5DA821522BC1AE4731BDFA74E0F1CEF474AB2F59EDF1FFF934B720F79F4FD9E4241G11EF72771DA3F23C6385FEA9704C0E13637DB2675DE4B1B415F2DD5FF9CA6E5D32G72A80FC3DDAFD1E0DB55904747EA54D75DA16C8B909C47779F250F76BE9699672C1FF9074EFD4645C97B19A40ACF0C1376B34399584F24C0DC5BBFF08E34701873F9
	019F0E494F8B149EF3BEAFD0F1EC6CF9016AE34E67856A0F0DBDAF704BB172DE59216D521A04D35A04EF41F70B7D25025B6CA92D435AAA14BF9EA0659FE833B09D7AD44EDBE8B3EFFA46DEBD045B812CEF214D4EB748594C6BADE6AB7F371C3587C77608655B426B4700D1AE3F90A11FC827795DEAA2F3D7DD1AEE75204ED223B318B50DC66148E440677D131461F13EEE9477960EAD52699EED0B56C27745347D131C8F5FCEDCC3981F5FB9C4745BEDD27743307C20D15329A80F27F919D10FFE10A698A09AA099
	A0B79E73FE074E4D7E34CF239F5C25B6B15CD715E49E071DF684B63550E9BC51987F5EE3D8DFC662D1776F0C905D87C3B892A0BE9EF52F48106AAAB55E261F1C371D5517D3F4BE2D70EDD6AFE6F03CA745D709702C3E4C25F892EE03383037F16E795BECA2EB613F203E7F8BF1A733F10E3A0A632E2338AE88DB7F05BE161D4F67877E0565BCFBA99D37C3B896A079DFD8CE6BB2E9B9335F31152D609DE765FC144EE1153F434AB31D6327D0FC03884F4A79AB4513F09744C53C0372CFAE44F296A7A0EE61AAACE7
	8547EDDA0565EC63388BD6E2DAA3473D3F92530EF01C3290537AA5E039139C460D41893632C6A4B8AB77DBF43EABB6C1FACE7B3FA9BEA9C1FACE3B05626977C46FC2BF79AE4AB4BC0CF2C63E0BB26DB8C63EDFC801F06EFBE0F7G95006EBD0CEFE071427E8632F665719EEBE304F178F02B101E24F356566BE462F2E5F065929C6371AE65112130BF67D68A5273F1E7AB644F561EAE89B35DEC23FB338301FF68FB28AF5F85282F880E8B2538C488AB5EC75CD933B1EDAE477DEBB626AD6038E9F3B0ED6D7B783D
	7C2FA7246B03645D2E9562F56FE35D0ABB096D1C7B7BD837DE20FDEEA8042381925F67766A907BD8E6A23C37A45AF4DA1C684C47AE545036A2117952DC0E7725F81D884FFCAC0D62C95887628A7F0DE57C116E478E02F0B8479D25389888A7F05CEB5D281FE40EEB6F4134F99FA04E3F875396F35C54DEDED7B9AECB07E95BB82EC107E90D9CF71B0E57D50E8B56E2DABF0ECB51E25AE00E7B6D88268D63388D7D18B61E630E76E35A940EFB399F53E6FE0838B70EE05AA20EDB5107E9ABB96E1CBECCDB4FF1EDFA
	CC2B63B8BF0AF303309747796F276763C3B81863E6B472F2FC08FEE319A9ED1B62BF3479CA4A074E7C66D6BACF171DA4ED1B7ECE7185C95236E9FBB636CD6590671304B2D54F42F227F25C0D33D04E2C0F9037FAB677870E7360F52D62A3ACCF53C2ACCF9B600C9F594B6F7E1133723CCA675FFCBE12FE1F96CF71819FC93FCF53D1BC8907C0DC7E47A813016E33F50330B747FD70AD4A9972B15F67C206FDC2BB67AF7A7D63623156DB4F84980F5DFF820F23FCBC998BE5CE7A58DE6F191FB833431DF46EA86F93
	299D6E277822CF24F628B1229DAAA04E77935479EEBA2F23C80636B999F1CF1F607E124C47A427EC3ABFCB74FB66290957514A8965414AFC086ECDEE03726A389E42D6209E2C9C9737827BA8DF0E5BC76BE1900443B86EAEDEB7A3B96EF90ACB02F06627583F852F9477EFF91F3271C395BCD51FE27F76B15FFF2F7B947B3705F3697C9704C38122BFC5BB5D51077E14C8E8BF33771F5C4F1C7953FBF4AE3468B329BFA5D3FC45E7D2FF9A5121BF55C3DC60E7D846D3349D4F798F74F97FC15C57FA5EBE7F87653C
	55E87321E0516F6D4609772766BF010735D15CDF68205CEE7F31695E7BBF4E6C50F80036237F110E618FD1FC58FF24E378FF7C02E37898082BCCC11D3FC377DF7BC1B81863CA8E92DC8404E3B86E0A03E81B789434CD225D1DCBD91FC33B7D39CD5E124F1D49FE9F1D8F2E7DDCAA7B63945F7639D476868B4AEE0138184FD1268303A8E76597083BFB107B90473DC0F1BA88FBFD41672FCE0AE57787FC4897B6F9A33FF0A6FB919DAF46FDA1153D1C62D33E104AFE1F82E54F79926A69173C4DC863BA66386569DC
	47DF726F9ED6C94793D929B65972D31D4959CA6F18AAC9150EC335945F16AA9D07BE3B9A47219E90177B954A74A15DFB6D8761A00EBB0B5653D088C7F25CDB94978361C40E0B237B1595DFC3FEDFA3EE8D4F2F0863AE6845B1D8B947BD5B0B6313FA0EDB2FC57D283E665F561C126A477A35CDA71EDFBB534FF354DEFEDFCB75739645C7FEAD554F1EB454CF8A4439FF03B2F550F267A8DA1405G8CF7AB45D5C0381E63DE9A6032F35CA19352D9B9AE5404F46E9C77BE7515G8807F0DC837515F08847F05C255C
	FF9281477A04C5E57CBB16349621CF682067A856C3389D408A40F429F2786E4B8B62F89D917D3657E5G1BA306F87B316B5D949FA942B3DDEE65675F45C1DCEDBA4ABFC0776486C0B804631EAF65FB7BB86EE30ACB0030A283F1BB36E05ADC0E3BE98B5295E4E099E74E131631BC43D62E7A8CE7E57CB517561B8CE9995BA95E18A1AD63BB73310C6E901740E59A266FAA8D19AD4A36CC443D415F07FAB9EEA1FDD77106F0C0A65A725DD3E84B50CC346561F442A7994259D3418727E2B9D7A8301C1500EB1AEADF
	2E2E294E4AF9AB75F36B5496C97FBE1962BDC4F861BDA48D7BF79F08CB1D0A72D7F1DFEC1F06B27D1B10897D5EE7225F27B2A63E1F7519D09EEC0C96EF141B77F707727BCCC39B269F41B1DA50B43461F3FDF4FD14042737A87326233E9E53C9FD35E43A4DEE35531D59B00B5E1D52BADD6A2B73A9DEB7DD6A2BD76B51D75DA0AEFEBA5A70BA3AA721BE2BC5591A05388CA35A350B63BC0CC86741F16FF2BAEF4031B95E57699A19E8BB473A962D3E4BB0C4CE8333287596733A5A3D871B9A8F569DEE5CFF382681
	FE3073EA7C0644F702D64CBCDB76BE9982A1D9CCA7114253731B2B52D89ED38E09734839DA171E66A40F4C99ACED0E70A4E75E885F0653FD46A4CDAD440D4CA0F72FFEE7F7C6A0A9670FD3591EE7965FE097CF7A0F438AF17CC93BF8A22BBACB9C9FE497CF4AFDC8C84F4A7574AABC5FBE7569A005EE323B537BD2430572A4E0391E3DAA2D682A26E94C579EBCA675350219B67FAA1FE96FEB759096461BA7E83BB833C5B29E6A21F80B88AF1CE7B70263A1CF08CB1A097E32A8036E75D636A8FB15083BB293FD48
	D309F562DCA539C3A7844231G29G795998F7990DA3F76010FBC3489DB2643E999637C8C96AFF2CC0BFDB48E32CFA1DBA8E9ADB9BD3C2F9CCD55866D7473317DBBA0F6A8CD89DAA4926E7C0E92C9636A653CC5B514AEC62ABD7E4670D19FEF2B6612751138F1E3B04751F48EC74EBF72E4B3979D2FF70493669A2B05BDEAFE159688F1F51350A68EC29BF24AF23EB0E59D2FF70D80A7E10B98BECBF8BEDFF49B27407C80E1B319C7DA1E9964A79A4DD1B481F5D222C84E81B0D7897A91E5CB3C36EB0A2779DB17C
	216562FE00501FCEBB1FAF222F14C9AB4C815A71982FFD45F69257F7E9BC337BF519B63B7F38EAB43BB39B9FDE05B69EA1FA22F9BCBC5516C7FA6158F91497E29EDE3351A6530F52F9D18807F0DC25956D944B6D11C0F10AB9503F4DC15CA794C77A48B67E7D53B16BE87DB4C9CB72B89DFDBA5A6772D1F9CCD50C2F5EBA032AB9C41FDE43852350948BE5E0F5F2D685E9378E0A4E15EED277E8D85C7B53678AF17C4CE69249531EFB125C9BE6FF8D8AA3E0718DDBC9DB748BB95752AEDD44EA42FF1749206E6AD4
	1B69DA2B8E47B15FE74A0DE3BCC0E65FB958177B4C36EF07BE1AE673253B970C5D8EAD1963588E3D3DG7DABF88E7AE8EE16AD5F2FE60F43C767E09E91F3F04CF5911D27A4FCE36760186B935ABF656634A80B81EAF350A75FBEA9ED775AF2EC7EE64CF1B666B250F138FB8EEB5F66F37CD9949FAC4233F66F95BAEEA661F808F31B0BF527136E8F4F1F5722AC19073887A92E8A42AA0EFB320B4FB94EC359AF55901F7305F0A8C08CC08AC05EFCE81B81D4G1679C8FBA325A57727113B51487DCF64CEB3F28F99
	39830D5CDBC66E9AE33453B5EEE2BD8BF9E11870623A186F289FA283616BBBEF6CFA9BB38A0D30EF59CE9632D6B9C794963717F2F7F74ED030B15819764B4C07EB695A1BD9C36E8154F4B17FBB540D3E66BD9F6B4AC55D76F5E5E4064D27BF6E9E5B277F5203F97A4F47FAF17DCCDB9E793DE367D12A43BC02F99EAFABEDF9046B464E63F5AD66914E7378A25B1607714858F99C6A47BC22F99EE533EDF9FC50BFF69E537BB00FF81EC7739CDB9ED7770D1D4739FA4CA3195B699EFAAFE637C1EFE1B44FD01A4CDC
	48B397E96E5FEFEF4B619C9B5F0246517832FAFB61CC2C4BD539D81727723DA1DD39D8BF6F24B88F887BF3DCA845113B7D483DFD427CD73634AFA374785BA357753E52D6675A9CF8086B5919741584189EFD675A74F86F2C517448689F1AE7233FFE54FE01693807FEA390867A0867FA8F2338948897ACC05C62C5E80B7285287738C544666DBC7C9E8D13BB91D958348875C3E84EC4974EEDF0A167F1267A1683665B6765094F0A4DDAC8F4FBAD3FD3438D4A6683908B1084C04A4F76FCCD7D8EE91AA0CD9700BB
	00BFC078C2546BC5F4AEA1FE21D87F8FD1DC56A2317EBFB6207ECB96213E6FB192FD37717043B4CC6E1AE4613DE73CCEC3729EDD7F9DE74CE3B43BB17D7FBE6028FF8BBF1BE684E474860881C884A065E77AFF0B6FFD494C037E9E20822095401A076D16E905FD3B759699FA8875676595E33717B3D662384A076759B54BBE4F3D8BECF9BEB9EA9DE6695F1D65B886D44CC6BE0140070D452F21F74D4AFD8BE454E96950D5181F21B212FEBFAC8F47F95751790642EF617D90C075AD7A50FF9C3ED752FDEB738137
	EF1D0D73AE206B295E5FCA67562E207800EF25F3EB79F3F1EEAD94628A3EC3BFFF1C76E16E5F096BC38A4585FCA72E8FBF70FDB6D15F216CC9BA7A9DBB8FFFC143648ED496BEDBA776D5C2774737C757587178235BA4E1AFFB3FE3FD380D0F8F7296439899C0876086C04A4F6A4382FE7E198F608381A2819289CDBE1FDFE844F1F6B1473DC5F11590EE62381BFFC17DDB72D15F3F52B5D69F8807G44F03C1A62499D33A9004BDF0278F7FE992B6D40FC46520B0F8C0D30677540D8BC26AA46B797A1E7CBE60F2F
	8FB80EF7979C44F1D45592EC97769C34EF97F48B958AEC97828F0E5DD7E71850B6CDCB508E4DA634CDBB4765DA78DA9957F598DDAB8EA0B4G899C9FC97164DE5F4C25AD4A222508BFE099CDEFA43D9497A060A2E46264ED53B7AA8FB91B208D6466FF2440F4FC49A21B0E1F9A9C3B6DFDEF10982557E0529E03712EDA2782EBB5994CDA36B11C59F6AA1D5BA1AFAB8DCB799A5DC9340D156342CF22EDBCB86EEC855AA6106BFA9DFD670D02F092C076B244EF25F8F217F291609AB8BECCA1559BC9632CDCA4CFB9
	FC1493BC63710A830F516A08B3FBB8EB7F18EDB6FFEB33CDC57A587E7F8FBAD7E704323AAFC3FD1ED80D7A8C607A79DFD181BC23A11CG10D9G6DA1008A408A608B90D6003486CA9B8F614C65AD747EE8F2A7F5A17C2E8568A2F085769F512B5D1C48C772475F5682C73DC6CB6C63AB22D1495053BAA62449946243042750CFBB15C53C07D96EDDDFEA9371BC015C9EE731FD5187660A721A2F183B463EDD7BAF391E953F8BEB310FBB315896F7558647F10E5240675C17E33B791F750E6D6BECFE761D4FF2FC57
	75A343A1A11F3BFBB1BE0047BF3A4466EF3B35E37B5BFF693AF288240FDC0E7E76992F3FC94B5107DECEA3BE143F827C8C2081408A608B9086908F1035124F77D25AA288578310FB43491D603D705B83A0G005C7D4DE87725210DC9DEE335F7E3C13EA09B75D76199AFBC2D227C092C545FD6087D4D48E5F166A3278399A2DFC25FC2BF947BAF7A59ACD13A790AE13D23BFF871F90B081568B70F686DFD6FDFCBED767F7740586F529B8BEC74AF18466EBBEEAD3567573A5C666B0F77BB7A7AE0BA7AFA8C31BB65
	93B0EABB48EC11EFF7F6F5A224D5945A6C17DB68EC0C7E925D5FD2D4A85D4390C771950552BD8C3D9B799EC5088BAC44BA50C73F074CD95522ACDC0538748A3E3FEC957A7A6D94270330BB475DCBF164BEFAF2F7BD43D5D5348AF4A82F7B2ABFEEEC4EF87CD6015FF8C85B34BF82180DFF5DE2EF7F63ABEC7E73EE69587EF39FBDBF205BE05274188C037AAEEC4B96EF20FB43C0A7012B30AD3BE5A5491B45BFCF7773C4C2DC8C374772F958D726F05CB57351C6795F23BD8E510504FA88F7F1DCB74539C338759A
	34513F677F117BC488AF42535EC653F27FC89E5275AA26E32FA376B6FA261068112DCDFDFA24E0147C7C3F972FCD7D5867D81FEB7B78BA8D5765878E7B70A2C4650E7D5E5E4F133E47756297687AC866EA697B749B941F37DA7ABE2DC9477769E20873DA0DB65FC27B2BE4886795A16EB20AAB06300A6392F93F56DB4467DA53082E3CF978FE9AAE7165F32CE9D2FB915ABF365E36B97078A35F2959FBF18BEFF30309BEG92G3256407B509ADCAB4B9F40B1FF7D9A1CC3192FA5BAF703308FC0509A5453F4076F
	26A35608EC3E46D97BDCC77704A72D115A3C0962B357CAED7EF29F5ABC8F62C65622ADE3695CC89C0413B9EEAF5FC31E77834AB91B0E35EAA15C45F15FD2DCA458586BDAFE2625DEEC83C2FBA6BA2F1524977A85BB9EE75AB6CBFDC45863E1302F7BC92BA0A8344F5F99479E8F4CDFED23D70CBAFEE0E7E90651F793F7A82F9FG7B7E7A2FE9BF4033D9FDC4B8B1332ECBEBB11868111900ECB2990C9A13E518E11B69CDD0AC2F7B0B88EF16477BE92B1C65D15DE179C9FDD45D58F18446C6AAF58F576FC766513E
	151783360F6A6169380FAA783EDB94A167552613711F5CBE2AB0412EF142B3A4722AB41BAEF25792FD659443BB96C093C0AF408800F7B16A2614FE4FCFEEDE0282D5DBAC6A4E5EFE72C9BD162CD92B9932214947554C37DFBC06FBAD820A515F9BC7505FA3B94EC0F1C9904E7C114FCD51756E9C8817G54F2FCBF45133B9249BD47643EECE1EF460833BE0A249D0FDECB1C5065F05E59A71C71986F1E19515E47D9DDD817C1B6A668544306C18B4EA156E520CF6BC096F700C000B0G228356C75E16D9B08A5F18
	9F45FDE4370A4C250877EFB49AB5FABC671C51D4EB4904FB23633D51C27C1B85E41F9F3DC6F81741E0B1DBCCEAA3A3E0718B8F4D27718623A4DA705F085F457BG8BFEE8145D8718727B3174C4C1761CF5367D0D395A72990C4FBF3309CE3B55BAB359AB427C2908FE13DE8B742D6B5047A6ADC13F9B613819CB69788D42416B504629F4FFD79804238192B8BE0B6E87A3F70313FBEB49BD678CFF7152D601FEAC5BC70FC21360A40E711834D4BAC6BEDD7F9A4DC7190F05703B4B946B190F6541339800141979D8
	4172717B58A33BF3C57E236D54F0879256D2FE1A077EA390587B5F175DF3857F11C48B71C7D7096D4EF0BF97BA62169EF52CCF6F9F453256C2795AC8F981FCG4899B3C1244C0A0356315F8B1E6DA56F85628B4B98DEDDC7F44DCECCAD95F0378D93B9EAB4645EC1EC4307B4D88772EF3DC9113F2CAAFD2ECC9D2075E0BD53EDE1092D8E10A79B837DAF93775937166098681B95F47E984281GE1A5786E1D3A40D1C74F650AF1425E54932375CBB1A54E46CABD74CE164492693B6CG452716C85FE5BFBF016F32
	5925ADCAE3A9564D5413386FA01A632E64E36114D22C17036964C5A1FF837493G35GEDGC6GCF00C000C800448DC8FFBE25D71441BBB2C0BD008E20D5386F1D5C17CE6EAA4F8B407A7DE61A57A8FAA891727794F80CD5BF0D632021FEA9485EA65067EF109B1BCDCB9FCD36298AB6CFB3CB4176ED0D45F33C73C704B71993F236E7187954318E626F9D8693BD88A7310C5429CBBB48B84F2B8CFDF17EA9473A798E3F7B2D249C6C83E084F0AFE7B6E2E33C382E316B665BAAD2F63A0F30C52DA5E76C52CB66F8
	7CCDB5B9988FFF86053B33D9DC512F3998A739DB1B457F231A67FDE0D02FF7483BC4B39F63EB8C7DC61D46A2A49762DF1846F96FD6EBF576F9ABF85E2471B3E2CA4C7B29BDBC6FEA1D0E755E623CCDEC6F16E9D0580F6957E39F2E575907133576E1D33FFDF8E91FFD58D70FE1B676AEA747C86139E97B49441ADEB3D440AF315DED50DB0AD6D41B34EA5D0A6202D2783FE10D82ED9D4C6F8973ADE7FDCEB0BCA3GC87D93760A0DE30F38FF49AC54499E03241FF86194573568F6DB96F7F2A1D90402410DC55B29
	563103BAF729CDCD861D36F3D818BB380A6E39A1B24715235F3E3C100C1DEDEBD15214B87F7389CDCB6EB3AD5858A274219B96950CD21E2E0D62BE74404F38179647E9F86F97EBBB9F4B4336538A6902B6627E035AC6F22E5898F70AED5D6A10B13DA5C068D3E6FD0BF3A6899B31CF5976ADBD5BE1D30B328220890075E3566F319F8B7C5B0D0A2837CB274B0D6562D4A752FBB7895F39EC3275E311EBBBB2984F9F3E63FBE1B76177AA3FFDA77DDE6552B47177A697FEA77DDE05243745BF701D74FB957B749FFE
	A77DDE85538B3EE5984DC7984DCB06317C0159FE06F1B45BABB60BED1FD8B0CFA159C7AD981F595EF7GED1F8B699AB6236DAFD8DFB1DE5B5BE7CC0D4F6C90BF4057FAB6236D73E8BF9D8A61E800444DB87F12F592E9B3AB50E61BCE0EEE335E13235B6C6A13235BAC726468B6ABD869B846D9B45BF10C7351CAB22E2E84391BAAA45F9BD6B87653D6999C0227A44E2F82678B67D13D85D7C8670E16D0FCC405F46E48FF84670EE2A12EEA8B0EFDE64F41F9E3FF0E73484171D044969C5FF84D25730A904E5A0A38
	EBA80E5CA11F9308631A95F3456F320456D959C8DE639997B80ECF364D9D6F5C11ABF3074C2E2F770DDDBF676846D7BFF7698AC661D730D5DCBF97B7097B15C19D3B1211597045FE2C1B6510C637956B662AB66223E3564D1F8C9D2266D8E80F1768311E79EF45BA79B37D8EB2964249G595B30CEEE63EB35055B30CEEA8D235749EB8C235749E78C235749EF8C4E6AA49B7B05B452BD115B308F7D3C51F16C175304FDFD60B6B6768B07E78CG991B3371C058AFE30F87561879786D60E0C73F165C0D21910D31
	6618665B476FB6997A890D10BEE366DC7BF81501466277A6CF586D89CA9264AB2AECD1D682B01A877B65E8D4905FDB09BAA8BE08E3A92623E293893BC1BC5347A93E3699D5497411G4FD4G721E42747103E5ECFDC42E3715173CF0F3F588713D1B665945F3F5603E4799B9F671CC9D425D1DC72565A43265EEEFD196EDC7DD7CF0CC0E268A62D55BD19779C7C95D93FFEBFB107C35FDEAFBE06F1EFA95535355A7091EF45BD1CF7F66779804EEE7FA0A06E782GF917E3FA3ACAB1361E5E1A466DDE276F32E989
	63BB97660862D1CBAC6E12615922B8F498D67E03695272933994BBDA1439BBD0C7F55364E80AA13EEA876AA8E515E31D1973BD26EB5D414650C67284A073D42CDD73DEBDDA3B663BC35C2E7DBB0F4EDDB428BBB43A8D78BEF3023C6F6D9F6C47299C92A944DD30F799BCF7EBF59698CA8A030ADE43903C1B08AFCF945E8F68BC269D2D080851AC79FE2114E6BFBDE2333F43404F5055AD1252548D8C2AF5E239E6D6BA5050E9FB1611B0A65835C0CAA3FAFFD01DCB678D0B446DDA102057E8F832785B564B45A770
	780ED2F1FC2A90DF3893633FAF13CBDF4163FF6817CB5F3093FDE20541F12C332A447E1D6D29DE7B70004CFB3787BF8338FDA773A3ABBCBD8148DCBB7323D9E92379117FCE319F5DD0ED73A31C8373D54D36791146444CA544454FD8EAE78B786B68C6583EAFDDA421F57435994797C8E8C4EEA4504CB3AE1450085CC820E9DDAE25913911C03371C42E04865D085930A0DDEAE3225390EE637925F27111BC7E83135487C8FC9C0FB76B6552A77378AB5748254F2AC29F1A2437778F4FF447B975B2FAAECBBE24A9
	29427127CF996EB7296238C59B7937C1D5B87E0C5DC867FDF878DFB4DCFFB70EC5759B4563C4C2F73AE34151C01877336371C72ED55BFC7E0EB247FA654D6FF6742FE275AA941ED1G247C2CEF7A36E26C3EA974E7B257DCE755DA6C6BC2527F483B7681CD37C9E36E65ED971B1B59C52E8E92685EA8A2E3GBEAFD3E35099CC6BBA48BAA6237DF1230DB633F41EA32DC95305E57AE99E4A1BD045664F955550178110F78C56D65CB5FF3436423E4FA9CB937A97B612B60B5B713F4D4F25656BB79C55109E37C1EB
	36086B16CF2E5477098C4555685B73687CD51D5AECE161C3FD0E3EBD154EABD7C19AD5B57A31FB9A7FDE15635E21B8DF8887D523AF3FC2770A05C2B80A6362A9AE8142791351477B524733070664B3DAFC1413F84643FDDCBC46727D5156A867AB1A94E34F1975D1FD7E88103D8B6DF033E2FCF3E60F513455G8D3BF04EACA3EDB4DF725CA5762504DF091F5BFFDF4EA6CE583B51893E374FEF973E9B5D1ABE7A3B516B69233F9B4D49987D5D28BA43593B91335BF2FEAEDD0CD01644DDB6BBABEA6CED1EDB03F3
	8B6753FDAEC5B55239054BA83E32C6BA37702D9667961AA0AE38867DFAA9BD63A72F36C5D9DC0B3873785933CD356857BF537DABA390766238E60AD37D3DD9694FF12F770D6607A46D69342DA335F2EB9F1F0D4A63F47729482FE1FC22F3EC4FE772773A08DA561E4741B3D9101179EB3BFEB4FF4D2E937B2B7B82B6472B6F5AC76781246B1CF9C7C8DBAC4409E630F14F49E374DC84FE1FA05B1AA244C52EA6FE2755EBADDA35CEFB0C9E22FF908875BDEC6F47D79BC9FC8148D389508EE081702843FD9FDB5710
	0D126C46D8691E0F3A2E9E8D3E1CA13FA5EB1D5F3BD038160C2304FB97C8AAB29EB7E9C94DB4E37A9F5712BE2E43G656BA779A33EECAD313945E024C861CE0635241E1934BD3D96C6AA60F72C25FBDBB55D944DEC76B5FF7F742FE37DD5A8BC23G4879662C8F6E9E993B8FBE0F6ED33128BBD4641A8156EFFA1129F6A1FE75FA69FC3DF0CC850EFF2D36F5610D38CEBEED31C2E60EDFF4EB982B3B7D7C2E0B84103BE0B74EA915EDA036645F9A128EFB6B565DBA43D0036036C23BB7B9835B3D565DB8FF74CF1A
	27970483G42F6E3DB7DD4A6497BA0A46FD560BE21A3F40C9283B449GF9FBE09C88D08B5036877D655935E4B31433BDD4CCA06AB3A635D6AF7447BB739D6BD8F3BE5A423807594B831E3EG64ACFAE62F8717703D01FB9C5B93167772250E764A58EA3357219DB6FBEC92F0BA038D076F3F4755C4CF221B5CCA19CD621621CDA2C086C5BD4E3F9595202EAB6AD1574D85745CBD88FBG788130BC668FE19EA100CB2AC73B7EEB5DE2C73B6E224B2902E5992FC3C3482BE8AF727A8B4D339D42D6G2F3D38CEF324
	CF3C5F002C3DB8BBFBABE02FF0AE59DE7B7D864C974A361176AD7B2796E591C083C0970095408B409FA074A7740979BF0C66932CC0B61FE0EDFBE726F46FC14CCFB69BA77EE4EFEF454F589F1AD752B3D2FE166EBDB8C17105BFCB779ECCDD4967E9A14E67E77E8DA5BFA7BA75E73E6F0C4EB995B5E0F80806439E40715DB7CE7706B9831257F8E9990F3C13274B63F45EF3D85DDB4B6781AAC1BE9540880097GA9AB5B17FB9B1FCB8BGFCA8C0ACC0AAC07EBEECC3DE51586AD1B9361F43B65C9681777DCFB3B8
	2E342414574D476937C508E738165562F9C8ECDED95CF74BE466FCC57115E50EF3202CF5E671811B24F3FF76ED772499A2F9B632B93BB03BF9BDFA878F682892F450348F6775E6D865E8FAA1FE04535CB8A4C7638D71811CE6FD1F9CCD9844C7F31DCF6A588FCFD627F62BBB211E8D2F6BB4E920AC34816CE2B1C5F284F5F40F03C0302638E085D3700AB538D7E93BC6FC9EE1497106748CA7EDC762BE6657297BECE79A327AB627935B3A627D50E66C4736EE4AE119BEEC0FC95BA56943DABA098DC89A5F7D5856
	AD24F751C6C3B891A033915B55581948AB9FF02AC6EC1B8B1558DEFAB6E29E87683DB861900E85C8EEC4FD86ADA477FD096724C5FD403A663A832A3DB5558D078F569CE8ECE858D5FD60F07541437B6B6AEA6BEA05B948C51BC6C9DF5FF8E06FFF9B772B64F3C03FEBDCA45EB7465A012773465AB31C5DA4B7E6FEE54CF4CBE55B1DF95F1E6E9EE5566E5EC1778B94B535C86E738A2178F2911E592C7ADB34D995447983307E660DCA627BD93F34A88B81AA81D4G96G2FDF301FB9CE2E61A4E3559AC3FF3FC1AF
	74B6F4898D072CAA0331818627BA51F56B2CFCA55F1DEE7904364B68E81B9B87DC33CD50186964ED736108EB3671246B4F7E3FC8EDF38545877DA2354D5293E81BD008ABB8007D60AC077347468256474E27E77339C3BE3E87305F7D1D6202A09C45F1B7D3DCBC04B38FA2EE72F17A3E8A61F20ED370FD65759C37050D57A16CF990654D23FD3B9F04C3B8EEA545513B2F9F457E7C963E1F1C50C94B60A703BB9DE0BC9E65BC645ED74FF40E1275671B67E03F91F9100D2563611982C04ADA21226791659488B4B9
	AA47F2952A446F2B6B965B7A708A81F73EDEB2BE5E407007F50E756335FE576A473BE326132F9FABE46F9A9D3BFEBCC44B642612560F880A77D4C96B47AFFA2C9FBE90172AC25F2A123DD7EFB4E03AFB36913A00BF22AFDF43642BE8C2795AFF05316E2F589F3AB5617739D13FE2FF7899254DEA06FA8ED0510C7DF46A5A350AF16CDF908D1E980FEF8E627E6426E6642124FBA4BCA06C8790520C72FC560F34D11CB6155E7112558272GD434E07F3C4B74476AE81289F55484BCBCDAD0268FCD2823309614C9ED
	A634A9904EE9057720D6249DB80A72D735A22DF5181ED18EE1AF00GCE5B496F888C6334C7E8BB1B82611C5FA05F5F78D864649FDB56857C2E6CAA60E17D8DE57A7D9416B568B714E98A25CDA0617F423B7FFF117642F4143F643FC8FB59D4C25B8BE1B7G9FCEFBE29A5286F1DA7799B4DF92BE84799E42315B666FD1369240759E42BCCEFD0F32799D42BC1E2234B190CE8248EAC3FD5D391A786CFE03094EE1B07F7D041EDDA8F3AE93DDF0E0BC5EA8C2B98B5A10EFBCFD97A17B23DB5B30AE7468964B67C5D7
	86F7992C026FDFD50A5FA7FA717CFEAFC3DC8447F574A3CE71BB6AA1085B2882F0465F11B6055BA860F754C38D3D37A30E248548799F6AE1ED1AD88F42B81A5B2D68FF1867CF25F896D5BB472DB2E33943B96E96BD62D2B96E19B508ABBF0CFCFFAAF35C7F2DAE237BB68E633E0D474B30EFCABA4C76EDE43543BBA4GB9DF174D252E239F7BBBEBAB4B5B45FD5313ABC46FE8429CCD68A654E1BD503A35234C191BD107A16D2843464D74BBCF88E729212DD1239DCC73389DGE7D4E39E774D43F709GB56611C0
	E963C8DA001C8E241D124BF55E41773CA89C673E4A783BF5FB876B2F2D707484A0F7B032392FEB5278FCE60733392F2F539C673E2CBB94BC3EEFC824AB213FEE69107463656C5DF3E106745D134892823CA3F9D94ED610F77753F9D7EAE8297E6DA017A7D6D1B6CAFA79F7A516033067B34DF12CF176287790BB9F1BD6507D2F71D0B6C5A70EA56F6D46B512DC0E2B305DC748F157D0DC9B040D1DB88638081EC3BA82E1AF0E73E138271B157E9C37CC6B4AF776A45F51623DBA456705D43844E33CEB064C96D15D
	0EFE3D071F359A51497CBA8E1E491D62F513EB7BC62B6B59DD623A1EE391796FA6067B1F387EEFE63E3BF8C06A3BC4678510D7F997566F8D7DD83F9BG675E05F576D8BF566F50AE2C5F8114B69942599AF89F55207F1F500B7D0F3D13CE5D64FCBDA4FB53586BA18B68F99E1A2E9E0DEA58A85C937D5DA64247505DED5670F517E51BF2B8EE2F99CF45E2E57B7EB8162D92E435E830EC7A63D836C08D164D175246C3B895A0379B7D526CF0BF71D840521D65E23A7BCEBB9D9B1B37E492790B3B25E376F60A2F68
	160E595F65EBAA359097500DF389668A67F6BBD6B1365D4E2910DF477ACB0563BA564595726BD85E9562F5ACA66F49FC14B73B27C5D95B0336FCBC9FEDB9520336CC263441900E8408673471CBF08EAD35076F47F13817F9FCF6CAF6B15D0F852E5977A93A06D25EAB356F8B94DF55AB35EFDB812F579097544B4FD298F2CD6E55AE26EBBF6DF42CDCDF51F51CDC2D343C5368BD4F85DAE9F9AF61EBC945906725457933C3BBC99B188C611CA3506E81B4G3483D80F204F7F3415743F4E56E98E988C745CD2F253
	39DA2B97AF59603A6BB667F526EE5B58F5E67FB679BA235A66D8E7FE5BA6DFE78EEF935799066B9272A563D0328390E90FECA3E3DD56C76DD36B55BD2459967A7FE319782D0567916C578B791D19C19CF7BE45C5C3B861887A5670CAB246CD6661F3E8185C6D4C424A152E1E25C3729C1F3F953A4CE3BCF3CEEC4EA86CC441A8B459FD623E7A212D36B923B241D78F70F71BFC20D57561FB5501FA7977AAF82332DB773C1A3F83F9767175EC1AE7B804E381127B304DFBC101EDDE368E777CA85324FBFEBED20877
	6CECCF9BFD4F0FA1CD3A67275AEE4F4F8DE94E767C886FF069A8D32D8E653F0336D7A39076868854217C55D6248D67348547687754904E6DEFD19677A36D2BC3BC5FFE241DCEE9C7A06C8D9048E9FB7AF83E1CF6B79D5F24C2B8D78F796A318DB9D9C57C38D60F6D45175B485E90E76DC50D21CB43F61A20CFAE68F42D6DDC69E23AB63BF42D7A516B0ECE0FEB48975075A4B73D344D3D1C62BD7552B6B73F0B4F7BC1DC2A9E6B7DDB8E77EA0FCF6EA497532DD13A5647BCCE573E5A8D5272CE21F81DC1DA5ED6A5
	16578AF131G6470468CC558FC521EEA75BAAD69A53AB4FBB4FAAE5BE4FA1EA87B7D2068771EEF7F48F345647552BE21BCD8DDF82752F95F77C1A5E99B043E2F5B4053A4D33CD1ED32546854E6B347FFD1C95A9A3E1F6B2046C23EDD374527EFA73C4C039DC65BAA96721A311D54E543101E76E5F80E03CD5F5708FE971E76FE0553577D768965A14CEF9512F927AE6DD1ADA862207618C658C73A2B905B36AAA33E23D98A499EGD037251651B798BA6926B8A14D45055827B471B4E105249D6FB4749B8DFAB0EB
	23C98BFFE9A21666D51A269D68F5BCCD6471B9B619A4BC5E266FDDE4FF281F917737AE9AA998251CC1C6F1DF5AB4C24AEB3009A1645BBB023207F3B94E2E164C6714B17C32A1DBFF3CD928570767E03DCE0174A58358C7CC627793758EE09F71A2257503F088C09427FDA287F51EB000B2FC13C342F42C26403D84C7F2447B10E2071B1D1EFBD2E0E2E5EFB0596FC3E27E75185E664F4F0AFE3768A75E370DFF820F9DCD2E75858DAE26BB57AE1D3BE96C71A66BAB0E503B3AFDCC52BE44CC717EA6E99F124A5722
	02A0AE570CFDE626CAAA3755EC134193FE7B196DE5E2FCCE51B1CB30D94A5F0362434CD27EF379FCCFA44415D8107F178EEB0C5E969B3FG789DEA1163EF245F62C7D9247C07A9BE56A2651FBE4077F2C2DC43A0723F4D641A5D6EF7B1DD32DD3A2041715A7BC53A7695BEA8AD6F9B949FB5A8ADEF0F994B9B8BF1D5C7313CC70FCA6576BFEA13A184FEC79E1553F7A2DDCF0BBBAA657FA945A79E1572BFB94C5BB608EB9BC27E3D8EF708068D5978C5436F04A1B97EEF50B53A54A1A97FF7A8BE4BAA65EF64FBA7
	F2A14EEAC57E0B9D4EA09D1F5D5638182E43AEDD2CF53C76E6EBCBC9D6E9F99BA8BE55213CAF72F73B2CE16897077959848E7371D143B6996261F74A301C3E27CDA379E59F13721FC371F947247CFD26A27FC20873B8067C4F1EA6651FF8CC741D45F1E80B0E4B71CF216B14C547257C53689E337263D27E974C60EB2B9067FF9C79EF7F5EB53B7D6CE2BAF73BF4D9A36335770E55C47E7C91E9F977D0FC5108343CD1FC3D369C62FCC6303C7FAC1213BBD510A367C40B3270046CDE0F75182EC2945FA07CEE0727
	8540C3944752DDD70A69FCCFB816B588F061GB18E69BA6C4F6D966295A759B3971EC5A765646C617DC565C9C7FECD006B82301E14727B3B0B75B75C45F4DF5925731411D55E8F6E21755B6F24548F42A8BE7024548F76AA508FC2A0AE7F947AC11B6CBA1C5BA9A607B7BC83CE49693731D4EE0C95AA50C641B37E14E3D9D2E4F059EEAD4A82B7B99EEB79792B65EE0E696A815786E084F0F7932F235DE1372F48C7088B941EE1700C86C8F0135A7E79B518AED506DF4ED9D0A7GAAG9A4E920F7B074A8695727B
	BF4891E364D472D1F6009CD0EB4D1A2EDDE47323ED0B2F9CE103E1C8E312D02E61767D208C6D5B8E32791C05637ECD65780E90F5963EA3F415533E70EC50BBC0455978BE7073C671FE842613C7B6396657D1AE261B31D91C2E696C715607C7697E002E332575A112620DE7CB6B432F1BD1DFA39097F7B656876BBC5764BE4F45F48F1DF6BAD62E3679C47E5AF3246555D0FC53B95272FE4077C934C1DC58B9D85E68DCB97E056ECC6F956EE40F3B3D8DD8FE07F8FB528E71BE6E68EFB7A9383F39233FBDC7E9334E
	85FF8328B897E99FCE43F752A60E5B1F01B80FF3B1FD99BDAFB38C42B1GC967223FB68E10F9AD23C9532DB1C14D20B38C782DEAAF5DA710F59E0C9B4E43BDAD6F707BD12B4EE36B5C2D705481047D8377EFB4727BCF5D4FF336FF632EDE477D9BBF6CD27078F7F42235EE613DFE7BDA91CE583B21F67CEEG6470813E0167617A3761886AB99CF02967211E6EB902FAAEBF9F7574B62555C1589D4077FC5453FDFD6275EF167E60G6AB91827DFC15F8592A11C6981FD04877AC845187BBB65FDFB370B69CE1DF6
	BA667B79F41FE7390734CE2C2278AA8FE91D385D3637G6202F8F90B0F3BA677D6975319CFBB9DABD78EBD0FAC77AF5272FECB7185FF111677C63E5F2098623C7E02657D1DDE0149FEF70BFEFF18B17173A8D919934F43BD5FB53BDC66E23A27CFBB9D33DB6F92E21FE487FB8ED03C628229BD3F306DB900B86385E84FF7170879C783BE6102517BD1214FAE30597E95516F0E0209375171BF01C70655D626EC516FAB06A61E776D934AC3D8CF5F69FC2D23EF67586B7C161D726B7C47F6BA2E731F5AA93F4EFF6E
	CE47EF3CEF4A44FEAE7582566F66DE886FEDG9E6FE13F3B0D5F9BDBF52133FE773A950E7D6E7E9A850F3FF1BF19FF375D640AE79B3DFEA2D3100959225545F735EE9753DD4F5329E44AA687421EA9BAEE6A3D50E14F9445DBAE1436876F72399DB7080B3F905B039805D86EFACF300167F85A032F9C5E8D0D22F46EB2F9B079D6AE27F34E1E5233AF56D1FC20884F641ECC5756C9B88C628AAFC239F7DB257CBDAE3271743D4899FFEF3A269EF411147F75949FFE11147F774710FF8C44D5DE0C7C5FF4589B64F7
	310DE770454E789F2779C5DCAC65FF9E4547DEAC65BF0B73CF02B855A548DF57A7659FF2090DE764A54E785FC747B5F117C879BF4676C7DEA2655F500F7C333CE0BC6C057B884E2EA263C63330D7006D2BB1536FBC2300A64E8B775A5CCFF149904E3A144FA7F6A9389FDDA57A3D22EB625B5AEDFF820FC8252DCCAF0AFE7FCFB9713C07A61487EBB3FFABA4BE3201077FCA43E49FB9EBF78748A7AF78FD76F1F2370E68CCAA478559F53F6E7FF9FFE34BFE1EA675B8F9EEA8A9914265A362F79406BBB532DAA15D
	F70EED3B5D367326063DAAC1B71AB9AE0E41DDCC97F85A69D8DD5C1BC367F1AF15366D87A83E68D2E95B7E9ADF83AE07B89FDE4FA2F3447CD3811FF35968EDBB5BB354DCD7G4FC2202D8268829881708288G8885083E8C5B01E955235D3F44CDA25AF7485A85DF7A7DDA926423389C65B57E22603A9B967DCE9F73DE6BB32FB70BA71487AB73E295A9F3AE143768F2EC9F7771FB579BB8AE08620C90F6673AB9375F260F0BC53F9775CF3CEECAA61487AB73BC7AFD128F14B7106BE10791F59345F1B7D0DCB204
	333C79FA6400CD9F27C43F3386A6DEB74BA71487AB33520CF5A51F1779B5332D1C71225F7D6609AF73F9934A031579A27ABDE6891437561BFF4BE3C5FF6865387CE1027304309F574D4743B6FDFCA97A6DBEBC713A39F6C2F930B21F225F1F86939DF0BD5C4A3F23CF643859747B781CABE05CFC85BF63D034176C5F225FC77F04FDE517CCA88FD66661D3D8D7AAF81977AAEC65BCA07A7D5A2909AFF3720472906EC823FB129B203C6DD7206DDF48C4FFF06738CDD389AEG4221DCB75953EC7AD8A07AFD5D5409
	574DBD934A031579AA7A7DF29414B71E6B6131AC54CD561508AB22678495C3386ACA544D57B3EC7A18BE53765BEB46446B66668965A1FC73C83F4BD6DD4946130CEF6DEADBB97709FE3F747D441779438965414A7C857DCE5C6F2BE6250E5B7E709A7487AB471D22B8DF8887F31C220460A2A09C4FF1EF2D437CD2B82E06FEF71E79FF50DF7F9F3F633B8473AB613847A82E9642ED9CB719FE4FED0430A747EDE677D5C3B814634E263898609B45F11194978F61D40E9B265F2CE77B00ACBE081B5EC2F021103604
	63F4AD18DFA5475DC9F1CD906E6538137DA85F88477D5C0FE93DB84E0B627CA19C42F12F7623FC119C17CB5784E3FD44BE78004266F70F0BFEB7AAA65E87FBA71407B0CFC25B6BE4A8F366D5280718F474419C0EE35F1A14C338016386CAB1EDBB47BDCFF1BA883BF3DC41DCCC6B43F147A8AE1004B9EE32897946F25C0B9427383AC519F7B5622A6998349842759C57C56D5986E1AB475DC1F1618A70DF0E7B1C627CA09CE24B0F5F1F9C41F10FD3DCA204D3B94E1B16ADG724B390667D70ABC72B9EE87757D92
	8857F35CEF3CDE37F15C3BEB309CEE9CF7058273736638D7A9AEG42219CD7180665086238EEFE5EF45CB558EFA41739B6DF10DE6EDA3AED271D0E4DA744503D9E193E52F906FFD0FC0E2FF41E61C8B94EB364C31C1BAF77B5FAA6C33C2F388EBF3959D6EF1F977DBE34F9626B3065CF6051B15FD626BE516F64F9934FFB567C09EF235E2767DE2402CD332FC5BBB7ADC0DF4F63B88F0A2B05F05B357C8EE805EBFEECF0B1DD40E927936E07CF237BFF2E157A7FFE0A9F39D66A7FAF73FDAD9E9017484B7BF81A5D
	3E246BDA14DD570DE78D6520437772D6D1BACF19BC18FC13681E973F6B24F378F7D1FC70F552B97CADABF08EBF82620A2F87DB51F95A2FF783CA18CF3D3D3BC0A1398332D65B2F511B3586FDD179160D45859BCBCA04B3086674209E3CA02F086BF15FC9CB8F6EBBD178613E11F3A9ED85041BGFA7D10F63097771838F15CD5FDF856CA88CF3FED835D438C612C9BDA1485B7707BE30F3866AB673818AE743453B11B7C56C74BFF035447BAA83E7686290F3D4F7774B4C1DCB0AFEF2B43B903F9B75A7C2378C6E7
	3EF2A55D37D3F52354D7EE20F8550DD2DFA9B2232F74C2DC640D48FF05DE41F5710368F7701FB07F784C1F40E361082DCC4BC43F830ECFBC6F077F849E7AA536B28D09FEAB16CCBC6FEFA7140770EEC64F8CAF3929C539E49A3F9F04626A8177F2867A775319D83F3AG6FFB9336BDB54B316D0A3E895B1E813AA61D6D5F22AC842874C7DA758AEC3BD47E588F7E1D4EBBF97A33FDF5425EA905CD4F5D225F9F7E8973CED9FF42FB4C078A54796AF444A5ABD06777E6A0AE176E5BA4F7027BF2FD1DA84031C418BF
	36B1EB288D62A11C42F16594177B5796E579DF91F77F9F369F88F9B96AAF7A8F631121F0BCA7D4B8B7550AFAD17D95757241B01F0B60653D1A4E777AC2B808637ECAF11190CE643862E32D825DF8648A9AA71D3DDE6AA80F711EF1FA3A77F9B09DFCC64FC2487E9B7477FFC33DDC4675D27BB7AC6FD9D4873D909E61B8CF26AB8807F25C7CE3E8CBC2FBA6369C11C9AF56BD5A7BCCF848690C69601CBE7417D82E175CFE540BE29216B70C628AA1DC41F1CFD31C8A42960EBB7007742B040794D748F9747DA1BC1C
	1D334B462E97F412BDDCA64310196D6F766B44365CFB926EF3FE3E134853E9508D766B4D42BE70F77939647EDC5EC6F2A417E053D6516FFF4C1978B67B0B8965417C651FB93C4D667DE4C28E36594ACC5C2B272EF33EDF3037EE6C7D0283F5727B85076A9C778B0E54496F97BC3BCE3CDF10614E27791AB4BD70DEA2BE976822BA39F301186D6FEE32770FA71B70BDA3187B4786156359F7FBD5386FB0FC923BB3A8961EC9GA979AD425E44AE9D52E485B85B1B78040E70526809023A845FFBC3075F7D6787208C
	EB740EB2D469B17F62GA6C3ADBCDB817217208C3F9A793DABCEE588B1BA4AB04508B238C3BA06ABB723EE3C39DC7EC79D653A1F5F9F6BAF48958A4FA8004C25A8D7943FC3A14129DCE5430EF27DB40C75B7154BF065B1C799EE637783655C4CE4A802E7A5C091176191FEBF106AE6E7B22CBA61A8C36589FEFE424D281B9BCF21EEC6EEC6399ECCF7146BC5FE3F0427A017BFBCC308DC4BD02E77783E57A827F26D1B6AA8D757D414AB1E4B90B44DD106A7783D34A9028CB93740B89620014B901B456F8C394519
	8CBBE7B84A50BC83E5E83A85E5386BFBD20755BAED0F1E5CB725403DE04B6B95A4FF4A6F681A316FEA3AF28D295F5D862861A672F59CFE33F3BB1D376D82FEC64EF3D10973EFFCCA685A0A97507A5E02739D35F4A118257D6FFA32B12FDFED55768F760B4E9FB151351B88C893FD8B5A5D3B84F9ABEEC55E31A5B43DD6AFC91FCCE98B002E1853C616BA1773EDBA175D8A34DD1CFE7F86C2EF5668B41D96CD57DEE8FB2D1C3E074EF3FF837F3CEEC57902B620BD7CF89E7F2473A6BC8F0B261F1D657B6586529686
	F3BAAFFA36EF37C1EFE1F97D0D2E9705C1FC642D28371E1F941C7F95AD3CEE72BCA2DA64ED96C7E9F382212D8AC4D9A751F1C8A5046BB94E424F6B348472BBD1A8CE532535904FE3F48FF2BA8F20E3654920F7207A72F4BFA824FEA80C915269DD2950E931B2DD70334DAFBFCC9F5BAF67E44817F1B5FD3789G7E4101282F5B9AECFE76EE293C1F25D11BC5C31AB82E073DF3B12F6C5B30BC174EF56EBB37D07AE2202D388D6BF424E96C32A41A64FDB713367DDD10171173EFB5BB67BFC069FD01B600539F6FF7
	6E3B5E7D683B5137E119DF67B60D67F9646865FD37085ABA051735D5BFF6D9DF575B7B77A7F40DA973F6C8FFBB5AABEA1F4D3FD7703AD4F5BB4A736B86F95B77D3DA9550355F0EB299C7ED63D83AF974FBBFB7C863F9BB6A61C17EEDE488677B1142318EE425A1DD646DD887762511B246F199CE240DA6839BEB3EC12E4891FE3FA37AEDAC1F78F16EDFA61487535D664D726DC07A827BF660137D246C6CBC271E858523641BF83B783B6E6B97487B44BDF4BDA495E8336E6077EF2659742B927DFE7DCFD84F7E6C
	CF1887FABD8D5FA9C803AF6C0BC843F70A3BB391F7044D83D543B8688EFCFFCDD80E6F2F8DDCDF375179B5AB04BDB9AE046202A09C45F1BBD7E059881DD4B62B8C6E7475574AF9CC44DDAF423E2B9E5756A4D63A182E6D345331B5034F69DACE529D52350C74DE02CF3DC33A16F1A1DFA74A8AEAD16A0250EE47FA457C23G9F9FB41E75326B78B7AFA982FD761D36F485F7BADB8339030E054A6F145EAFF8AF45570A7042FDD694CF42AA088B399365BE5C276032740AFEFFF4E4626B714CD16F4DBDD3E0F551C0
	3F7B0EDB8B63353B30FE3E4C7707D45D05FA70257D3E8E42EE9CB7096282C8BA0EDBB766BEE11247694869A622E7BC760C4963F46B68D4496F8C051A1E13A35C475B226D3274B2FC3EF5168DDF2F914E4AE3F225ADF72DEE4EF1B1DD7DE927E3F560ED3A4611F01734CEFFC07149F7C96B7450F22C530A3B611D65EE34F5778AB17F8840475EBD3AEDD9BF7A4CA9529F4370584C5EDB3773BBAB126EE66F2DD941600FG0AB29C5FF8970CBD6E3B22164845EE08A993F0D5F4EC4A6E1254766F56EAF46463B3F69F
	FD77F269FD7404DFB970E5715B0644717502BC46E0BCBF62BCBE57619E4C647605E790C0A117FB6A3131659E781FCDEE3CFFD315B19B652EB718AD0260CC2E170FC965A67CA2385C2F9C154ACD62E303F10CB94B6AB8F7D0484B12A414A5EBB258G201E176566E124A91F6CEC6E407327ACDE160D258C77E35DBCEE83FAF9725EDA3B72BC68D09E4233FEB216C7FDC4DA9E925FB6996D90484F4DB0CEE632FB405397C047E57F21FFECBB4C2C3059816FFCFE290C5B01DCD4EFA777D77DD239893FE0AE77EE35D4
	EE929FB1996DF0CFA729D3A51C3F224B7E9B3B38A692AE6375F0C00575B09672487DBB2ECD5E2B42B92A063F63BA6655F42F01BB04FD8102B96D85BA1C7F0E7CBB4A71290E0E0FCD03425D853D7CFE344CFBC0FF77E05AA0BD72315C03FCAEB450EFF8A19C8E904BE90F9B11CF72BD4867DFC664A31CAFE436AF5FBF0F7273A8C2201D89413CFE98C23E3EA148171D2B9F8D61C4004CFB791DE47C7C6D7CFB112FFA18043B55BA330673FA719872D2811DA7CF3F64B8728A3F97F92550F3B2D2A11CFBDF0B3278BE
	24CD98C1DE3577A12F03A7443C986E5ED376657CAA1D1FB5846902F9FEF59948BB71BEFEF6B8DD3BAE3C9F6CG203A9FE9F326A2EF5D7D7CBDFC2AE3B9BF1C063C7C01AE16275FBC9DF96506A22FF9D904368142DDGA321C81BBE83F9F907A22F7E99E2DE6C3D6A37A685AF73656BC91D97FD832AB3185619B4DDC8BB720B0D7665F54B94CE77A1151614949714162F9152DDFF40162E3DE4BC69986D83A5234B7348C1DB3E4675631767ED15ADDD7106515215159697ECA8C3F9CEEEC807670F25FC3DE003235F
	4CA5AEA37009EC31717932F939144FEE035ED234225A84031295A09E13906983DB9CED3561B7DBFEF3E9BBA651CD07CE5D5947E8FF7D2F0D362AFF7CE53C305F31CCAF4A603E555B4AF97FA19B2F4F4730599BEDB65A85F3476F8B263923671B76BFDB3E3718466F8B0507ED69BE9ED5CF766976345B527DF69A7A3D355F5E0712E5F43BC3245B00EE9B9F0F52537321AD9B9CFDE8F62FAD3FDF560C6EC325DA9B6D850A711771C505E3191625B962E8D7AA7CF62773BDAD9A33B05F73FFC7ECF159C71C591E0DA5
	AE67E35148D0B6160807E78AC0C2B51E817E4D7282057331C44E8362B9238B165A46941B845C39B9E4ADF57FE0BF99D1549874FACDA779A61E526058623D9552318561DF78G0EAD7EFAD2AE3E62819C1BFF53C34AAC6C8B6A357D3E34E7625FDBEF19D09E6C9D7552DE477927ABFB9D671FA6537D9364FE380687707D76A8DD2FA1774335F3DCD2A93E733AF39DCE266BA6C1BC7C908DC770701E8D63A9E750832E4DC9B19E6D6362616A1C9473F99FFE6FDA62834C67B39FECD166BDA83E6F6D83D341A87914BC
	A876794533EDBE3FD940D5ACA6EB763BCC86F5D7275AEC59C7AF2255288CAA0DD5F839915EDD4CD23F7687BB55BE08FE7F28C5BA26A6712D8F22CD5EA74D9A6E4B977DBE31FC627D7E4A8965A16C21DB01FE7F835F4BF1768A747BBD19B86E53707B52F4209B7F87F1ACF7DB862E7F47BE486FE5DDC64F0DFB884636G358FA16D35F41E163CBF37BD04F77E2CAA4AD7485E254A8D49521A785DB6E18F61590537DBD12694401504590F5909CC3DE1FC8FB8BD875C8F42A1GD11CF676F114A90163F2B82F060711
	D78367658E38500711561B73CAFE98F93DC9E90B9F01328334BD426F819D46FCAD9C77A95DEBC6764984F21C4F91244BF99479C777A17FFA4039BD0A3479FC8FC848237CFEE29D1DE305F0D6F80B32A09CE94B75482B0263CE281117EFB87A7C1043D97C637357F3DDCC7720DD3A68F07B76AD814229929C1BDF1ACD4B197318F45EE9914567BFA61DF77263BAAC02B84F47303C1768447C13G1F7978F866127527FD3FB8CBB762E23A49F66972A4B296C3386AF1B9BDFDC35F29D50FCB7514C56F56EEFFDC2A27
	CB8D3C2EC3DC546328A7B72318FF7993AD4A7AA7462327F79D4E7A9FDFF97F63E23A923BF3355AA4B29AA16C7E041C1E0E51FA6B7304D4CF67D03C7F93D2BDE573B6A18862F2A378DD92F6670AD9816F99B19EBD3D66F0A741784A7B2E0B69D6581D8B6AA711B198429191F2FA525173BFE3A324FA9A24780488291E3E666702A6C3DC6B132827FF0F08790782BE6A497168A943453BC5673A186EA73BF4719299D3A01C7D141C1E3C69DCCB41D3D2BDDD4B6EAAFDCA2A276F79F7859590677B946AE941D4B1FF45
	5360EBCF0FC7CF1FBA5CF9B03E72E6CCF7ADDD25DD3AA2090C1590EEFADACECF6751F9222E2725FA3A04620DCFCB75141745673BA0AE6EE954537499E27E351160EB11236B0965F73FEDEC83747E1158B73ED74047B6115837EE24EB97394F405886207699FE1F778A473B56172FA469499E0236E7B04F5813FCEF64B338EF638D7ACE91896118E7F08E7A7A929CC3E7BE4B46507970AC81A0F7C633B174748D8523142D7ED971981A5D63A13A8FEAA3BBA3BAE903F89EBB7BE1F6A7F59B24B5BE0BE30FB0FEE70D
	8760421E4532E419F09FC94A33281FCDA6C2DBB2857A860076A9289F2559D1BF6C9EE70237CE41BC3F20B83237A8E88A5F7F6DE23D9DF6B15DDDF66992260C7B6E8CFA4EE46A94073BB3A8BEAB4A616E0CE52857DC0833C6E1F9EF149D7747C6B1B91222646549FC4E911F8738E2002A6724EB1EC3235ECB2B1264557B9C4A966CF0E66678F47A080B696C7B529119B24A5B628EBA5E77FACEEA0B900A77FDCEEA0BEAFECFD0G44E5BF4F3F7B107D2E4E78BC13437DF9F9F9FCE47001008B8308FEDEEA0B85235A
	A2C112D7AA176D000BBAEDF7B15DD5F669F2DE982FAD8ED25D967E3F720E85BA2A620A0A9A0F5406AA15D6342828D8E9CBADAAAAAA47EA05C32C1C92A010009142B7002034D54AB9BE72A18961A72048114FC350A390C24227A284CDA0848229A28984058AAF3B0F6CA61BFD6B5B37590D4150BB773D397B5E6E12ACE91363B94D496E4E3DB3774E4C1DBBB3F7663D39B3A234AD4EA13EE0C4E8DBCCA23B2A8862FA0F203B841A4C79E7444378999F4DBCB86F7BC8ED18E750969B3FD5A13C9C91F8DF82DCACFC7A
	440736611D2D1E6D9D9842ABAE1E57E9EC188F56685AE2EABB69B6DA56BA49ED48AF5806A3F1CDB5B7BE6C5EAF448BA3436E7DF271B6DC8CF11E113C3E1FC63CBBAFFE24DE0E29466F7C1156F2AD9F9561FC9060764227E2D4E8DBDCAB35A61B8BA13C34D16433841FFB460C5267AC9D779A62FA832EBF25AB46797AB90063C771FA2DF289147FBAD3787E8EFD674658C76C0477FA6EF771FD227769CC66C3AE3ECFF4005245619E43D40B8C27A1CE988DB64BE8AE43C2854F17813CFBB417E126A7A8372526706D
	CA474B30FF0766215779F6CF388CFBF942E5F884771F0E0EB64B500E388B0073108C7BA12EAB4037118C3B187C4E5FE48A2F88F43C8C37F5E89E060F4F26F0992EEE8A176127382F5757A24353089B8C3861A4433B90178270DC1221EABA3B5FEC8AE7DC6AF8992E6B50BC74BAE77C90AE431C9F42E5A8615EEEF602D906AD089381D710C0BE1790D78AF0D5821DB7B65D73F12FA91C578977F19C6E50BC74BA67E5064B30B0B3DC061578DE0443A2C38F623AGAE16E4B890F1FDG9EC0B26C169D14DB8FD3F8FD
	F6474B70138E4DC32F737A9CAE43E9F476FFD38E17618EAACBDF31FD364E236D24FB55C2B7A4A1DA9B69C15C9BC8CA8835119E565F0FCC8835117653FB10F3A16E4C985E6E9FED0AD46EC1E30C3B6F60B745883FB146FCAF5DABAD1168960F555308703BFB2C35AE7A736CA770BDE4FD1DDEDFA08AD7FC167E4F4202BCF1A77970F921001EC9G5FAEE3799AFED9A11DC3994B77858E9462B3890033932BF345C43EAF30F9E7783E40473B783E405EC4CEFFFC8B6E85819C831F5E0964B7A94C27CDF4ED9C5FCE3A
	A5D7FD5F2E2E833F46FD110109213A718744BF17982A9BC5AA176DF0085B1DC4FB01DA247CFBA699F790A6DD493740DB963A41C63AF863F7AA7C4EB751AE9F474725BBB6201DB64E625F132908917E17A9BCEAC3A778776C50BC74BABF074FA90B204E95A407AD78CC748C409E420D47E7FFDD816ECD38F89FFF6EBF00F0F39117G70EC420DC25F5499632BF3B70F6738DB7C1CF6AF61FE0B38F3GFB88275BC056B6F94464237AE9D378C8A7782B3E50897E2A77B473B669CAF278F9B3EF13D8421D7773B669C3
	3809A4570104CB8F70B6C9A25CDD915AA40FE8E7932E025AE4554B64EF9F79FD7C3239CD3EB635C30DA91C5A8936F8C6A734C9AA5962C397F05CEC32450FD33A3558CE45A01B93A42FEB90F78EE00DF0B7238CE3G6EC738E4448D86B809F0F7845B04F0CFA2EED5F2F5EEDEB2474DF7E23EGDFA05C00BACE2B91AE9EF1BD816EC7383D2E70BE16E8DA572718428D2ECE70175F21F968F5BE6B66FDECB049E1101B7731B842FD0EBE7A12814EF805FC2C5672FE779E61B2EDDC56F9047B50467B5D894265CA617D
	6E97922735D33A47A55E6FE2DE6176430C2A48E7B4F3CE31C5C7E32D64511F31FCD84FED12DEC4DBDD4F52B8A8CD832569C7E9A63AA3739794AB7FF795CEBB08E80FAAD67E5FD21AB8CA732DBFB27FE63F15FFF7BF27CDA25ABF7A2D7C47D01A991466C4F3E47E8D4DD67E57B5F35A79C47BC833157FB0CA33F882CF93C80F4C3FC73A157FC3691CF6AD510ECB3772FF1D5264D11A3A2C487CEF4C32723FB30B5396916DCBD9D67E13A8CD45842ED38BF12EBFB7415C4FFFE7FA4FEB20A9DC52896FFCFD53896FFC
	9D4067129A5439AB49611ECD3C3F45926ED9448DGB80EF0E5787CB299E0E1A27918EE617CD6916E6B4B1C1FC8B87DDE85462FF4A237E7DFACE81F9D1E58CE3AD5056D335FFF0F6B17B393C36D77A790EF1F98EA3F9FAA64763B876262293E5B492F735E14F0DB3DA282CE4F6799DC7FDCC889BDFFBAD4DF971970FA7EDFA11E41DDA0AEA505675FDF8D6A73632670C13563F57BEC0766E158EB787C7868A4D8CFCF62FA57DD233904F06F22CD9E83F0CF42BD06FB8F7DG9EC438350BB86D904295A1EE864042E4
	9AC7F06ED88C30B8197C59EC08BCF6A5EE347A8F98073604F19E6203483EBE4E607AE3B770AEA67EFF8DDC592F46D23443DC10B6B0191FD9CB7535FDE6AD37B132DF0FCDE89B7781DE7DA97F190D4CE935C3D11D5FABEECDF215D0CDF51AACBFFF7FA1094AF01F3F6DB2BC6FE77AE2753316043C58BB3F59D3384F8D41ADD0FD1F413969BD085BBC053FD79103B4A7D87AA9BC7FF92D662F536569F6A8505CC6F407222073D19987CF61B2FAA190D987F6B5DBF5E0139254011E4D11F520BF56F1B870CEA27E7784
	5E353481FBED760462F53283G3CDCCF85783C17BD15537D3D955DD90E3A33975216CE6575FFB80AF67B0BBF326ECCC739F80057A54AFFE5DD6B3A333D0E6BCEDFAA43303A364B10DA972EBBE9753CDD6227F25D297384F5E7925A38A990B71B4AF73A36ED39FEC73E2A57CE63BA779462AAGBEB30D17FB2D2DED5D79826D622EC093CBF47D253669A6C83CEE032671329755DB4F01F68D0137ABD65897827775DB6159A170AF1B2D7004907859F4AB3CA8438A8F48324273C360F59BA377192ACD56BE73933C27
	CD1755631B5A560DB11BA27BDB1903F6CE9C48AF01E4786F96261F2A7C1DCF76EAE50A4D67BDEC71DF66EF21F71F26F11D5267D643074F65365BCE77854772DDBC1D4F4F0F31ABB00D79F230A9DCDE58897791F4E89E7ABCD90AFB3B6C7CCC5EF4BEF72E21BBB60AC98E4FEFE1EDEDA778A504E3882E5E92292CAC7D7FCE9E1E08F93477ECCCE41DBC24D9F5FA04B728C3FD3536F5FA3B4F6A1B6A905AB5FDC18E8326F3DD7EF30EB50F3FE941BCF6ADEABB0F4AC56CFCFF032456BA5D7C19411AC601625755070F
	393BF14CE5F3CA4AF45EBF0A7CC11A91017542951FB3FCE29CE7B472BA9F88528D75C5CFF7EDD310EED8E3B4F4FA5A75FED1305E8BDB493E717ED1492DF973CF169FB9DA2527FD5D4F4ECC966E62F95E77FDB04FAAFF74E59D529C24CB388A3A54CBC13ADB2F220E5DC24A3D34A54827CBD4ED23270D8FECECDDD6078EFFD1F5189D32B64EEDDE8E6693FB957544E351865D9CE77465AB2D0B3EAD5754DBE5F2A9AD18676E5A68730C293DBA197CAABD184F3C1A68E5F24254AF84A97A723DA9D96B7928291FFC56
	F225732CFA7CADAD61E7E507DD3E920D312F06E37EDC988704D4EE5B94C46147734DEFD81A30B3FEB00E152832241F5F93361A4F6F6581FFF61E32AA150FB949267E71D0CE94E714F7864FA89FC7FD58D3406514EE6AA3CB37082D495BF8CE2A1FF1FB7187E61FDD610C9B2B0A715C73937469476CF2C7AA1F670A3632B9A8AEB5B82FA5C3F8EEEA5B73DC768CEB1A35G970460747977DB4C3794624E4C60F3728308F3819CC3385790578B60FE042B5E46F08399CC385F9C6373F99261920E638B8CB32BF3D74D
	647312CF4F4369162BD53EC7578DF1ACDE24F4CFEFE5B271C31ACA2D49A5D7ABAA733923279FCD698BA8FD4EB66BFC2718468E77B66BFC59954BDF8C349570591D024F7F0EF15D3C8438D87874074FF0784C26BCDEBD9EBEA77E4CF4E67C5E2DD65FA4338856534EB275793B0FC52B17463904E37C797872D930264AE17D69036366FE40785E841FFCD6870C2F32443FAD6836EBB07E86CBBC333F6724196387DA62B3CD7446F359E366FD3523D02EF333DA57D1E35F64B8FF06B4075E8149BD4E1FA131BA2CC699
	4D7C72334520D3B305CCCD58F05A29EABE49A59E12DDAED177544D968AA29BE195172C890B3F7448EE31D456A4274B8BE02526F8C466AB48E90358A5151A24EAC1F8A9C69FD4940D5DAF6E5104E5FA8293A62BDA02251F3BD69396FE6513FD329D82E5401C8532CE7A5CEE0C4BE20CBC8C1743AE29071230F0FA094B855FC7140D1A10F9D2764A1AA6E4E381AB14DACBF0E9252F264189994B1ADFE6AB9F232484679EF65B03D0F6195325496ABED193520EA8GD7BA943F0839ADAAF5FABD12E6F33082187021A4
	D9A7650B2A6CF5E8C2466100938A367C245C20B44AE2192AB40895CEAF20B2AAE5371D975D2B8ACBACDA7A7C5DFBA6470DBF6DCD9796168BEBD8478239025A42725FDEAAE952E9D9E5E3E2191063D11C908978F2A1430BBF19DE561FAB04D597CF6A8BAD59DE22E92A33462781A03CD341F25A61531C2E9DC7A5A8A4DB006D2F90B25CD283B45B8ABD5AA539EBF7D482153BF6FF002D6A971494E307478DD8A86FCAACAF6312CFDCD6BE396C430D47D286268BE9E542A2974CB0E512CDD354F2A1C7F3B001A8AEFB
	4073394EA89B77AF585EC68630FB1F2A4FAA1B0FE3B8AFF4FD604EEECB26AEA983D58E28428ECC494621FCCAE1BF6410D448CAD64B61A3C1161BA96E1A755AC7AF9ED9FDAECDD8E0D472BB21C578A0G7A0782034C17A1CBD6657CB2A835B6F66650FB920BE64FG12B2A133D1F279E448F8F9B055973E069AD93D21DF0FF53937A55F4C4A65E16271B24E9F0192DF149A1C2EA68814C21BA935047723ABBA8437B2A970089C688FEEAF2C219B20B4E208A8F89455A87F562185BD9E1C778D56A8C88ADDD8EFBBA0
	5DD1ABAB8DBBCAAEDE04E90098C0C3827BF5441E25498FC933A2507D1DE3A35F2A8145AB95529B8A8A703B903FF7326FC24494A226F0A73466EC21553F5078C00055F9C7GCA3254EA5CAC38EF75F54BFF3A4B17867D09095065F1C8650678AB045C9A3C06003962009E37BCD8D324857DCD3301F23F9F445B98DE0F2D74C8B62662B9F6C92DC78E2C8C7B4F8AB997555A9A4CGFA6E59323310ADC2AC7AC00530C8F348B07AB1DD530455C156A7A468F3C6045EBB0437605F03FF58267B9087C89D4786B1742966
	E56306BEC23291344426B9E1D8E09EC6GFC1B0CB251304AE0FCC027E616E6E303206206216ACB55895F925BBC7B5374878E5F5D4B9687AAD1AE64AA4188ACDD96502F883728CAEE99B33A5F7952D5E9C2DA393050013A898277B3BF0A0CEE41DC783C8A83F71522413C404B3DCF9396944067G8C2D41982801A9E219A8E241C5985EFCAE4D9BC9A808BBEA4CDB7628A0B6EBDD071008C35285D42925B8A4B16C6670310521BF56FB9CCB4E825B29E3B22800FD6DE4DF3BB0599E7C5E5BBE3D7EBAE02A2990D653
	E0797F8FE4F1713F154590C1C82BE1533533416782CBC1F10B652550EBD88700E1D0E4AE97E16C54358F2332D8CF4250CADDB34D9D4CD04F6CAAC544BF5D0620611FF50DF2B71B0C139BF76DCB1FF8EFFA1A10817DC23FA985E6D7F65D89F48FD56FA669E01468DDDF50ADB77C3334D5590F9C0AA4F7DD17F44DD99C2DAE95F738AE5D9830E845A0CD082FF2482245E4F5FA45DAB006EAC5CD91D539864CCC51699677088DCEB733FCC43BCF0628FC7104CB163C326815C0B11A941FAA7A9535DE1440860412ABBE
	ADDF2C16C5C905940A8AA3984C0A9059843CEC0A3BD1F64350EA137345720B0CD8F4B0AEAC9AADAD98D43DF922C74F40469EF158B4D18BAD28240927D960FC254F63D1D42D58E7F35872E53BAF9FAB69B3940ABEBF6606098D686B9AC158F2F92F21DD673A77955E385E164657B321EB9447BF74B54CA563D79466ADD8A330754E1ED3386E85FCBCFC747B032E2F5A8E3F27253A920F67204B2E9E3643C8208230EFDFC90DD659A00185AE396ACB5C76E3500D8FB6E96C2AA3636E21FD2798FFAFCFE3C46AF1A527
	D8396BCCB47F81D0CB8788918E612F15EFGG140D81GD0CB818294G94G88G88G3C0171B4918E612F15EFGG140D81G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4FEFGGGG
**end of data**/
}
}