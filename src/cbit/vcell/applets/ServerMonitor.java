package cbit.vcell.applets;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.Timer;
import cbit.vcell.desktop.controls.ClientTask;
/**
 * Insert the class' description here.
 * Creation date: (8/20/2000 6:33:33 AM)
 * @author: John Wagner
 */
public class ServerMonitor extends javax.swing.JFrame implements java.awt.event.ActionListener, java.awt.event.WindowListener {
	private javax.swing.JMenu ivjFileMenu = null;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JMenuBar ivjServerMonitorJMenuBar = null;
	private ServerPanel ivjServerPanel = null;
	private javax.swing.JMenuItem ivjFileExit = null;
	private javax.swing.JRadioButtonMenuItem ivjSleep10 = null;
	private javax.swing.JRadioButtonMenuItem ivjSleep100 = null;
	private javax.swing.JRadioButtonMenuItem ivjSleep1000 = null;
	private javax.swing.JMenu ivjSleepIntervalMenu = null;
	private javax.swing.JRadioButtonMenuItem ivjOnDemand = null;
	private javax.swing.JMenuItem ivjRefresh = null;
	private javax.swing.Timer fieldTimer = null;
	private int fieldSleepInterval = 0;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ServerMonitor() {
	super();
	initialize();
}


/**
 * ServerMonitor constructor comment.
 * @param title java.lang.String
 */
public ServerMonitor(String title) {
	super(title);
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getFileExit()) 
		connEtoC2(e);
	if (e.getSource() == getOnDemand()) 
		connEtoC5(e);
	if (e.getSource() == getSleep10()) 
		connEtoC7(e);
	if (e.getSource() == getSleep100()) 
		connEtoC8(e);
	if (e.getSource() == getSleep1000()) 
		connEtoC9(e);
	if (e.getSource() == getRefresh()) 
		connEtoC10(e);
	// user code begin {2}
	if (e.getSource() == getTimer()) {
		getServerPanel().updateAll();
	}
	// user code end
}

/**
 * connEtoC1:  (ServerMonitor.window.windowClosed(java.awt.event.WindowEvent) --> ServerMonitor.serverMonitor_WindowClosed(Ljava.awt.event.WindowEvent;)V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.serverMonitor_WindowClosed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (Refresh.action.actionPerformed(java.awt.event.ActionEvent) --> ServerMonitor.refresh_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refresh_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (FileExit.action.actionPerformed(java.awt.event.ActionEvent) --> ServerMonitor.fileExit_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fileExit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (ServerMonitor.window.windowOpened(java.awt.event.WindowEvent) --> ServerMonitor.serverMonitor_WindowOpened(Ljava.awt.event.WindowEvent;)V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.serverMonitor_WindowOpened(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (OnDemand.action.actionPerformed(java.awt.event.ActionEvent) --> ServerMonitor.onDemand_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.onDemand_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (Sleep10.action.actionPerformed(java.awt.event.ActionEvent) --> ServerMonitor.sleep10_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sleep10_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (Sleep100.action.actionPerformed(java.awt.event.ActionEvent) --> ServerMonitor.sleep100_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sleep100_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (Sleep1000.action.actionPerformed(java.awt.event.ActionEvent) --> ServerMonitor.sleep1000_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sleep1000_ActionPerformed(arg1);
		// user code begin {2}
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
public void fileExit_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if (getTimer().isRunning()) getTimer().stop();
	dispose();
	System.exit(0);
}


/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getFileExit() {
	if (ivjFileExit == null) {
		try {
			ivjFileExit = new javax.swing.JMenuItem();
			ivjFileExit.setName("FileExit");
			ivjFileExit.setText("Exit");
			ivjFileExit.setActionCommand("FileExit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileExit;
}

/**
 * Return the FileMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getFileMenu() {
	if (ivjFileMenu == null) {
		try {
			ivjFileMenu = new javax.swing.JMenu();
			ivjFileMenu.setName("FileMenu");
			ivjFileMenu.setText("File");
			ivjFileMenu.add(getFileExit());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileMenu;
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
			getJFrameContentPane().add(getServerPanel(), "Center");
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
 * Return the OnDemand property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getOnDemand() {
	if (ivjOnDemand == null) {
		try {
			ivjOnDemand = new javax.swing.JRadioButtonMenuItem();
			ivjOnDemand.setName("OnDemand");
			ivjOnDemand.setSelected(true);
			ivjOnDemand.setText("On Demand");
			ivjOnDemand.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOnDemand;
}

/**
 * Return the Refresh property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRefresh() {
	if (ivjRefresh == null) {
		try {
			ivjRefresh = new javax.swing.JMenuItem();
			ivjRefresh.setName("Refresh");
			ivjRefresh.setText("Refresh");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefresh;
}

/**
 * Return the ServerMonitorJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getServerMonitorJMenuBar() {
	if (ivjServerMonitorJMenuBar == null) {
		try {
			ivjServerMonitorJMenuBar = new javax.swing.JMenuBar();
			ivjServerMonitorJMenuBar.setName("ServerMonitorJMenuBar");
			ivjServerMonitorJMenuBar.add(getFileMenu());
			ivjServerMonitorJMenuBar.add(getSleepIntervalMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerMonitorJMenuBar;
}

/**
 * Return the ServerPanel property value.
 * @return cbit.vcell.applets.ServerPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ServerPanel getServerPanel() {
	if (ivjServerPanel == null) {
		try {
			ivjServerPanel = new cbit.vcell.applets.ServerPanel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerPanel;
}

/**
 * Return the Sleep10 property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getSleep10() {
	if (ivjSleep10 == null) {
		try {
			ivjSleep10 = new javax.swing.JRadioButtonMenuItem();
			ivjSleep10.setName("Sleep10");
			ivjSleep10.setSelected(false);
			ivjSleep10.setText("10 Seconds");
			ivjSleep10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSleep10;
}

/**
 * Return the Sleep100 property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getSleep100() {
	if (ivjSleep100 == null) {
		try {
			ivjSleep100 = new javax.swing.JRadioButtonMenuItem();
			ivjSleep100.setName("Sleep100");
			ivjSleep100.setSelected(false);
			ivjSleep100.setText("100 Seconds");
			ivjSleep100.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSleep100;
}

/**
 * Return the Sleep1000 property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getSleep1000() {
	if (ivjSleep1000 == null) {
		try {
			ivjSleep1000 = new javax.swing.JRadioButtonMenuItem();
			ivjSleep1000.setName("Sleep1000");
			ivjSleep1000.setSelected(false);
			ivjSleep1000.setPreferredSize(new java.awt.Dimension(105, 19));
			ivjSleep1000.setText("1000 Seconds");
			ivjSleep1000.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSleep1000;
}

/**
 * Gets the sleepInterval property (int) value.
 * @return The sleepInterval property value.
 * @see #setSleepInterval
 */
public int getSleepInterval() {
	return fieldSleepInterval;
}


/**
 * Return the SleepIntervalMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getSleepIntervalMenu() {
	if (ivjSleepIntervalMenu == null) {
		try {
			ivjSleepIntervalMenu = new javax.swing.JMenu();
			ivjSleepIntervalMenu.setName("SleepIntervalMenu");
			ivjSleepIntervalMenu.setText("Sleep Interval");
			ivjSleepIntervalMenu.add(getOnDemand());
			ivjSleepIntervalMenu.add(getSleep10());
			ivjSleepIntervalMenu.add(getSleep100());
			ivjSleepIntervalMenu.add(getSleep1000());
			ivjSleepIntervalMenu.add(getRefresh());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSleepIntervalMenu;
}

/**
 * Gets the timer property (javax.swing.Timer) value.
 * @return The timer property value.
 * @see #setTimer
 */
public javax.swing.Timer getTimer() {
	return fieldTimer;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getFileExit().addActionListener(this);
	getOnDemand().addActionListener(this);
	getSleep10().addActionListener(this);
	getSleep100().addActionListener(this);
	getSleep1000().addActionListener(this);
	getRefresh().addActionListener(this);
	this.addWindowListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ServerMonitor");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setJMenuBar(getServerMonitorJMenuBar());
		setSize(600, 450);
		setTitle("Virtual Cell Server Monitor");
		setContentPane(getJFrameContentPane());
		initConnections();
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
		if (args.length!=2){
			System.out.println("usage:");
			System.out.println("    cbit.vcell.applets.ServerMonitor hostname port");
			System.exit(1);
		}
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		ServerMonitor aServerMonitor;
		aServerMonitor = new ServerMonitor();
		try {
			Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
			Class parmTypes[] = { java.awt.Window.class };
			Object parms[] = { aServerMonitor };
			java.lang.reflect.Constructor aCtor = aCloserClass.getConstructor(parmTypes);
			aCtor.newInstance(parms);
		} catch (java.lang.Throwable exc) {};
		aServerMonitor.getServerPanel().setAdminHost(hostname);
		aServerMonitor.getServerPanel().setAdminPort(port);
		aServerMonitor.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
public void onDemand_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if (getSleepInterval() != 0) {
		if (getTimer().isRunning()) getTimer().stop();
		setSleepInterval(0);
	}
	updateButtons();
}


/**
 * Comment
 */
public void refresh_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	Timer timer = getTimer();
	if (timer.isRunning()) timer.stop();
	timer.setDelay(100);
	timer.setRepeats(false);
	timer.start();
	updateButtons();
}


/**
 * Comment
 */
public void serverMonitor_WindowClosed(java.awt.event.WindowEvent windowEvent) {
	if (getTimer().isRunning()) getTimer().stop();
	dispose();
	System.exit(0);
}


/**
 * Comment
 */
public void serverMonitor_WindowOpened(java.awt.event.WindowEvent windowEvent) {
	setTimer (new javax.swing.Timer (100, this));
	getTimer().setRepeats(false);
	getTimer().start();
}


/**
 * Sets the sleepInterval property (int) value.
 * @param sleepInterval The new value for the property.
 * @see #getSleepInterval
 */
public void setSleepInterval(int sleepInterval) {
	fieldSleepInterval = sleepInterval;
}


/**
 * Sets the timer property (javax.swing.Timer) value.
 * @param timer The new value for the property.
 * @see #getTimer
 */
public void setTimer(javax.swing.Timer timer) {
	fieldTimer = timer;
}


/**
 * Comment
 */
public void sleep10_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	Timer timer = getTimer();
	if (getSleepInterval() != 10) {
		if (timer.isRunning()) timer.stop();
		timer.setDelay(10000);
		timer.setRepeats(true);
		timer.start();
		setSleepInterval(10);
	}
	updateButtons();
}


/**
 * Comment
 */
public void sleep100_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	Timer timer = getTimer();
	if (getSleepInterval() != 100) {
		if (timer.isRunning()) timer.stop();
		timer.setDelay(100000);
		timer.setRepeats(true);
		timer.start();
		setSleepInterval(100);
	}
	updateButtons();
}


/**
 * Comment
 */
public void sleep1000_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	Timer timer = getTimer();
	if (getSleepInterval() != 1000) {
		if (timer.isRunning()) timer.stop();
		timer.setDelay(1000000);
		timer.setRepeats(true);
		timer.start();
		setSleepInterval(1000);
	}
	updateButtons();
}


/**
 * Comment
 */
public void updateButtons() {
	int sleepInterval = getSleepInterval();
	getOnDemand().setSelected(sleepInterval == 0);
	getSleep10().setSelected(sleepInterval == 10);
	getSleep100().setSelected(sleepInterval == 100);
	getSleep1000().setSelected(sleepInterval == 1000);
	getRefresh().setEnabled(sleepInterval == 0);
}


/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowActivated(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowClosed(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == this) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowClosing(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowDeactivated(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowDeiconified(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowIconified(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowOpened(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == this) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE7FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BD0DCD516D9076C26F629B556E6B5DA18E29DF532A3AA16199D47E1CB9CB3E5F4B21AE148EE5C41292C265654AC63E44A14C68B37AE8DA4F43E182022C6E5D7CC08C184738302A6508D012401B610C09292F8508FE8F87D496B57FC92126CB977737AF577EBC8B0C355613DFB6E3D671CFB4E39671EFB6FEBD278E9E2DE362510106CBC427F8EE593327AB0A1EA5743EE012B5A1DE6A5063F7F85F8
	075C12178B4FA628EB6ACB33961094AD9F4A5DD05624B46B7B703E03BC29ED2E011798FCD6D5434B686C39D373E92F97FCB6507273E4B3BC378274828E1FC93B117FAB24086363B8DEC0360510D9255846123D136356C01BFC405582B8CBD97BA1F8B281E657D471F65D37ADA4EB1FD5ADDB100E012600A53CEFA6FF96133732676DC1DEFB33B773BEA5D06E86404167106301DD702CF50E8CFE5B5F67564AC77BE5C5A9177CFEC55682B5B56D32BAAA2BADBE2FDB73290494FC013482597934AFA1AB00CE8245A5
	DEC27A9D702EG44FDA1700317913F925E57E1F92F9853B73BEB394CDD17FFCA523F7664EE0BB126523DECAC4E3D21B1E50C3CE879006A7F9FEC2867F828CB86D88C309AA03FAC4DDA8A7091357BDD75DF40332ECF3379BC9E1F374E6F2FD7DCB0A8172C40FBCD8D54F05CC9395F273A88E1362D4CAEB311E7AAE07D0E5CF0BF2613057B041D13CEBEC4B29E3D5D3241C4A7998BAFDB0E16B1DDF41505F442F80F4764BDDE966E07613CBB09117778434BF372CCF84FCBD81E934F69A49AF8AFABC3DB97D2DB2742
	FBB6778F067F04628B3F8C6119CF5ECFFD864BA5D0B767CB619BDDDB05AC0E5CFBC8C1659D26FA187F0BF8CB6617CC065C72C8D95E20BCAB4A994FF7F4D972A7915FED40B3D9BC34BD1667C0DD61D7E9D666DF898DB8C793211C82108E10893039827C8B2036C278584B75DFDD270FB9A5D52B8F3A95172C9256B741769EBC65F1CD151A3D81CD72764B27A555AD75A9F20070B67759D087FDE17DFECCFD3F9FF89C133D32AAE9EE1F9728CB9ED91355GEB73288D47542F4A3436436D11010147CF845D1F5BF640
	D311825AA93FCB5264D08BD67F148DEDB2AA1D860AC0G5E19DE1EE924FA0471EBGF13A9E3E35A33D93326A15151A9A074FB79274F760C8C8FA856A79D69ABB6643FBF2055071AF0FA12E28B24DDAD1A968846D5B8DF46C6EFE94DFD2A7449C7B70B246E1FB257019D73B044F5837BCC84A167F23294F645F3DBC273712794AF8E5645CA921B4D1AED6CE3A54F50373588C6C1566730E717865CD611193834F743B2F99ED1588F224GE468BACE3697B31F2D1782327052B6959CCCF605B44E64FC7BF2B167037A
	4675E79B1FAFE86F4F81BE534734F5925B2E85BEBB3FCE335A819400F9GA9G2BGB66F93BA8EDC1EE5B286ABFFC67219CA871FF1B9E622BFA64B6D263264995A20DC1B38DCD351FB074BBD93D918ED6EEF407E72286C556C32DF763A60A5406B9EECC0B9F83C987069FD9E25F8BF44979BCC68000E7FD9834AA3E91A6A6E8BEAF23B2CE9EE6FE0287EC94AAB906C7307C21360352481476F9B037822377FF5834E4DFE1F57ABD3A7513B707A67E9EC10955983C25BFCC12F46704B9BF1FE0D4893BCB63D50381E
	E0ECD1029867D8BE12AC21FE7D2A5B832E072112446D23EBC5F611C1C77B8107F9D597BC15FDA2860CFDA6E2C04925FFA629BDFE0B599AB67F3EDBADCB7731B939F2DFA886B0191EBDE6AE7BB29A23B8BE40E4FD7DBB74030894094FB965B852A17BE1AD81A85BAF661C23896BC5DC63134D66D39426CADD47554D85E89379E2D00665G836889DDEFC71B37C7541D66BAE475CD4D28C34590FB498154E1632448B76DA0CFB74064FE2647147DCC8F2C5D46AB221D834AAE0038832C5D628346F64F696D728F26D9
	0B8F065A9DBDE8ECF74BD551AE8E708906F6A9E16D8EDE936D4821B4EB5121D03B2AC346F6FF505BCD82FE1E215D2230F68B08E8376E902BAF28E2CFC715311D24676173F8FD92FF2E666D183D5F7C90FD10664A435228B4DE9E9803F9D4D3F33C9157DC169B97D872F91C282896B970EA95FDE1884A33G66D78BFF7D4D9611834F9BD9C8BA1253F3E265754955CC0E746A103F16460F65B0DF7B1793483330A64DDAD2A37C6C29E663BADC9788787A5DB48560BE229EB77AB3430D9D5F884F81379A501AA1600C
	8BFF3AF29C65459C0123F94E642339946A268A787A81728FA37E013CC2036C8B813F8CE0ADC08E2577EF13DFE3BC228EDF4E9ECED99D70299E59D50A1AAD174634F29A7B4A6BE8F5833E6F91F61895BCC465D46406F13756D397C141911B4461BD27F93EFB4F0E23DC3E258F5DC3EA2FDD30186DBF66EC9B32B88F73FC6130A147CCFB51328A0069E15F9521074550A66B3050437BD6239E765726D907G62EB059E6E3A727D7430C0C6BD783CF659A3F9DDB72A87A67B9B3A6C09A0570ADAA17B7C81B3598B3E91
	7B46D96587B186A9326C2F2C38D1561C7783D7856F92205B7D0D60AD3B4DF84F53F94B6F9D8A713EE1660CF74FB541FBA150DD29733EFB580CF75137027741ADD5865EB74A1C715EC1846FB2206B7AD6703E439FB9EF10F7024EBBE59CF52E4A832A9C981A214E1F56F9A7835D555F8ADFAC4F467D8B44B317EF2C1CBD5AA06D08F644CEDA479D1149D574F1B5DD0F8C6BE0CF2711C424E8869AD4B4AE5B5FE68B590A0F405AFEC4487667E859EC0AAFF0DD32BDBE91CBB6CEE2DA59D8EE782E857755F3C126FB0F
	C467730FC56016C2196DB73B83747C63C8641EF80062579C095C933F2A0ABDB1B98A3EF1D46C119ADAF06DD98265D5GD9G05F5E9567DG9DF5A266671D58C7265903B6FBE1EB6815147630E4CF6C85CFA0DF484FF879099328C31A1F69FA88CEC61EAF786B42479E9FD1E67DFE90556F1308F2F2C4BFE63FA5F9A80B491A5BA2FB034D1A6C6179615EDCE3FE3884E8E5829454431A88405666177272B95D5AFA3186DFE9B42E41A95B2ED8B6192C8388DABD13EFDEFDE89D280AFF1D2F41FFEA459CA29D6A56G
	645B00AFC093008BE00ECD582859714DF4B6223E576133812E89F2B8DBE41E2D6B347A4A4CECF1AC2ADF24AD965A4CEC717BAF753342F0DB1C14DCEEDFFDD053FCDE5DAC5CAE37EEB033CBAA50CF37893B34DA7339EC19B631AFFCFB06E35BF4F53A311558437BB1991E9F90B2545A058C0FC4513ABE991E18D6864DD406843710A1DE17C13EB6B39982D17DA2E5D8ECAAC37A3048DFB3F499BEA0B313E1F7D43FC8998A9B4CE4F84EAF4E174B9A048C3F1E218C3F1BD686D703D93F39D167285747EFC1D43FC8FE
	13917C5839D8995DB74C033A47G986E87943734014D9906EB3B08381500DB2737732B085B5918E62DE8947A6A8B60DE3BD6AFAB345C2D17572A531DBBE15F296A278246E3CBE0BA9E314E2D2E87D8BCEDED09FDCEF22A457C1C64DC0B79B90954728E43838D63A92D28F737CCFD0EF231456C9C65E3BA4EE75E5BC6E98782F20086F83B24C93C6E956DDDE2F21E528A1BCA2E239DA4E9524A5BB4E8BE9B6E9BD8CC363E0FF7DBB156C73222917D63910B08413320BC1F5BB515BF591C6B4F95F32E781858373E1E
	0B39C897147DGF30E895FF9A54F6C0EA171D8383FACB214599AB96A447CF68960D781E48194B6C18E53A456468D2D5366AF8D63D364AFEF3646768552E66E8B6B5BF04F9D708555FEB9525EDB5A0A743A50AA0C2B9B1A0635F90F7657A4F5D00E72170FE9FFD6972B7F2E36D8BE456C6E3FE2347B815DDECFEF90766AE89276BA1B0F760A07723DGA9CD42DE6DD634D7FAD338FDD6C714338CE546FB7FD5A36FC37A3CCE9FC0FA854D617DCBA24AD54D1174D607512B52697D139B69B9A27A77C61435A8FA8957
	0C742AF5FAAF8CA33DB950BE4150A7B9025E62A8FACE3251C02F32C2507B2D5F4C5FD7B4470E0F0CDEEE36D13EFDBA7E69B0FC250E37D9B08E386481A928E8F4FB827BA7F62D037ABD0EB63EE091B9ECE633FE0EC0F1F44657CBAA0B8B0F0CE19EBB20CA9EF027F63F5CCF32E87F010BA2968CC1F992A081A085A063382077B71406665670641058CF60BD789D1DC417F3DCC51FE527DE60579A8D17DEEC4BF23E3B83461CEF4E89F65713844FD4G364FB914A36E23D7G2E40A1763837D4FFC8E246B3989DA14C
	57B55A3F897A75820CBBC4BEBF3ECB6473C9775FC31A124C4F54D6F6DC314CF5B07BADF2047279B5A3AFDA184D07F24DEC3E46915B66ECDFB71C07F26536C46E77B40AAFEC095C6FE5656AF720D0B727C52C697FC377AA19D04E52F1BD6B91175F8AFB0BD691B32FBAE3473C59CE731877F7CE73756FB667546B5F1D4ED8310A69EC6986B31DD534CE273367E85C3237B25DEC527199946FB260194EFAA99E4BF1D0373AD568E7186EA39CED6093ED8277A845E9D00E53F1C59B91B7974A49BA6E3FE97E3F984AAB
	F45C01C1EA83A8E769B8C2F374FCE71A3554A9F00F39C55FDA9D77B645B9215C2B637A06237B2E9996FD959DF7E084F133201C20635E70A1AE994A29BA2E5B9FCD6F8F9437944A99CEB11F03746C42B01D78A1874CDD3CED5682657666F6E7DBFBC3CFDBEB0F4D81AFECAEA50E993FBB295AF80B69FD8ADDCB1C4C46D962796EDD59EC1D0A2779AA76597F9D0C71BB44BDFE51F84E05B24E06FF6570E47519E17568F3FB08317E9355D80F32BE12EB2CF7065567997A33F17D6AF391A332FE15CC141F44457C1EE6
	51F7ECBCAB78F36E2ABB33D77D6ED28E23737CB923FE8A6ED2AC59B168941CE47DF71E8C459A966FDE5B9363CE81A32A42F438BCA0E23E7324D86B6786F18E4C01F2A240E3A745DA7FCD4E4C3ED9B9F1437D589CDDCD63703213EC0E66697857A8FE15814F666E259CF146378E6A3A5B4579569DE7D08F29D04EGD88B3039A34DDA865054A1E25D6FBBE1C91EBABF6C70799D908C95C378E27362DA2E08275D9DC2D7FE7A4D5810DE3EC44BF8AF464A163C2976AF586FC674E52E770F266471FD76C721BC6476DC
	9C17868B81DB4BAB4F913DEEF4BD7ABE3BFCC035CADE9791EBFEC7B736E37AFB19661B13B066F8DDDF6979B84F66C3B989A095A09DA0D32FFF0956637DE1B13F77AB6277F52CFEA05FB876CC2E4F5498FA1D9F01CF627C26566F35B09E1B8CF55723EF74D3EB0C36A17D7E95DDE382B25E614AAE16274D370A7C1C1C92F178D9EB389D464E076C7055C63C2F9B72296ECB10D1C9CA1D629E74625D9C4F7B1AB70A98D02053338CC6737DE0D0445BDA3D1DE3B01C6F528BA13E6F3B2766DB6956E38F50E3387FF48B
	9E13BA0F4261F09EFF6AC19EDC269151FEEEC8269174BB3FAA8F482AAA3B5A5D17E43D3DCC5B5F8BED137576DB7D617437CB21B1E4DF8C2FBB5007F5AC2E3C540734B2004E3AD3A20E6C6A5A8B4F5352B00FA08DAE3C9620A18410CE5587D937B6413074E61D7B52ED97E776DD5F139B44FA3368719F13477E5A9E730C3968B47367AA7EACFD794E6C2D7AD89CAE0C11C3D097873090A095A09DA07334981B278BE5441DB208DF27E9D27F903D6195817234DB9E8B21F1E340E87BD07D62BB122E4FE36987A89C07
	E52A2DEF98FC2EC39A546759FF84A2F5C3BAC3F33330B3FA1E32F5A2F7945703524E48FCF9AB45D7F5C6664B3787C53E5C84F5C91DA2163C1A03F6AE68829F6D9238B7A9EE993FC7E338D9B9B763FBAB64A15E8B8C6F0C471DB705C71E615D9817180D924F444E7BFFF446BC6FFF700CF95E7F6819294F35FEFEA67677C13FB8E37CBE084D3FD583040839F94F27E803C6FED74FF0EFED881F2F0E21D07B574FE27BB6FE274DF03F99C33D3633BBD75EAEFDB2BF042BE03816E12B81D721537C2E9A7B1FE477269C
	77B00B45EAD0E6655E0D1F12709C399D7F07D2E4C7C3E3876FDBB108E39E1014C068FC2E49FD7D7D5739237BD70C98F1CCAF335DA13DE47A103ED79EAB566FAB6DEE184278D1E6E9E5450132624A6ABD5CA62F74EDB55A1305B0DE77EA9F2E49EE2FDBF3CB0A7B92BDDCE807065EC19687CE517BEFBB78F4AFC09C40FC0024AE91DF547E7FA3B43E184416865720AC86AC781DF2457653EE57745F3D4AAEBC136973E91A4F037485FE4405B655FCFE0AE4B83F8B6D2F3A8707B4561463C7DD68AB0ABCC051AC1F79
	B34D29174038B23A445E69C2D01C2D13B3A2DEEC9CC5BFA9514B0534DC2B1793023166BA76793EB1007178C9CC9E5FB7BF8C65B0EFED9273948FDA98EEC351BBE2BE51FB63D1C921274AC22F6707E35B56B5BC3DEDBDF46FAB03CDBAA67C7CBCD49D46EF82FD838381D977213161829D579CF07A82EE74DAA6E33E066D95E9429704766C3C7BD3CD1CBF75028D4681928196F1FBACBD03ED024122E9F5725B9CDC275977FDF4BB464F379AC60DFD8B4F421E82785A6159F5D61CEF9D534479169FF08B4F0A732D7F
	AA8F3B4F4609DCD3D36F436FG9DEC90EC4FF72BA6F22615E7451E6FDFA94D22F3E9568A0026F3624CEBF8C46497BB9F78A15DDB465A4F761EE3BA08BB975A070A35A6073CE4ADA1537E964430AF3DB2AAF25C4567040CFF1CC0993320DC588D7B37EEB1CFDA34196DCB4F4D305F3C9B6E47720ECA1ADF583BD95E112D63EBA85EE940B3BBBF255B399B6AD23B45FE76359174111273E95623GAE00C90039G8B4F0BD83AF9844F04E36521A7FDBEFA2C0AA72C129B56D79ADAD5F85578981BA6269AE34A79D8B9
	17C4FFF7316CFC649807A9BE63FC6498DF1B90E3DC8DF50E8B422607B094F0D90E9A5E1F51EEC67EB335FDDF7A0B72E076499A099D5FF21469635BE645FC6D5A2EC42FDD6FAB66EB5747CA745A55B3A15629E45D960317C27A779B5E5F1C784B5BE27BCD798DC3AC486D41B10FCE9473F24605E83FDEF341F81739E0C26CD99DBD7AB7279338CE58E4DC47443EECCCB6464D1D1F9E3560FAE796B763FA987D641E703849467F4330F9388A5A2C6B19FA6D673FDD736018B67726D9CB819CGAE00F10039G778224
	740AD87120D2CAE2478A3600048284731347299ED6G0D1F660AF31CA7A8EE8D60F6678A9FDB60096D6777F926777307BC66FE3E48936D67BF73187B79939E231FB35CE2CED755AF4379B77A9EDC5F581A5FA2F925C13A77E0EB18348BE3954FEFBB5DAEED0861F70EEF89611BE4648FE5763B50ED39CB601974C71227110FCE3BD5ADA8A945B6D87B0A5912D74CBF9C54C8AEEEE2B412AF76B39A3136F90B59DEC5A39BABAB20C73F4F6B8AE8E493ECC1C2254D38A18995B7704D88D0C4D29A59CA332CE211E685
	D432BDBA4F2F7B7B977EBD2947DEE8A116E632FE088E2219640D61A8477DF4A8B0C4665BFB2E994F26574D6E0FF9F69F5F47FF4F579772ED661F93743EA983706B7A049FBE1377E5ACBF049DC03BC7D294CDD2C66A3CAEBCA7281F506450BE726AFA249F90EDF8A53F2BDA0FF28F9B7A7CBFD0CB8788D08E769E1894GG90BCGGD0CB818294G94G88G88GE7FBB0B6D08E769E1894GG90BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81G
	BAGGG5294GGGG
**end of data**/
}
}