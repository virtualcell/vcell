package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.event.*;

import cbit.gui.PropertyLoader;
import cbit.util.*;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2001 4:45:03 PM)
 * @author: 
 */
public class SimpleWebBrowserPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, HyperlinkListener {
	private cbit.gui.JEditorPaneBeanFix ivjJEditorPane1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JButton ivjJButtonBack = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JTextField ivjJTextFieldURL = null;
	private java.net.URL fieldCurrentURL = null;
	private SimpleHistory browsingHistory = new SimpleHistory();
	private javax.swing.JButton ivjJButtonForward = null;
	private javax.swing.JProgressBar ivjJProgressBar1 = null;
	private boolean loading = false;
	private java.lang.String fieldCurrentContent = new String();
	private static final Dimension prefSize = new Dimension(800,600);
	private static final Dimension minSize = new Dimension(200,200);
public SimpleWebBrowserPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonBack()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonForward()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldURL()) 
		connEtoM3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JEditorPane1.hyperlink.hyperlinkUpdate(javax.swing.event.HyperlinkEvent) --> HelpPanel.handleNavigation(Ljavax.swing.event.HyperlinkEvent;)V)
 * @param arg1 javax.swing.event.HyperlinkEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.HyperlinkEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.handleNavigation(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonBack.action.actionPerformed(java.awt.event.ActionEvent) --> SimpleWebBrowserPanel.goBack()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.goBack();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> SimpleWebBrowserPanel.goForward()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.goForward();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (SimpleWebBrowserPanel.currentURL --> SimpleWebBrowserPanel.updateHistory(Ljava.net.URL;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateHistory(this.getCurrentURL());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (SimpleWebBrowserPanel.currentURL --> SimpleWebBrowserPanel.startProgressBar()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.startProgressBar();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JEditorPane1.page --> SimpleWebBrowserPanel.setLoading(Z)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setLoading(false);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JEditorPane1.page --> SimpleWebBrowserPanel.jEditorPane1_Page(Ljava.net.URL;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jEditorPane1_Page(getJEditorPane1().getPage());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (SimpleWebBrowserPanel.currentURL --> JEditorPane1.page)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJEditorPane1().setPage(this.getCurrentURL());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (SimpleWebBrowserPanel.currentURL --> JTextFieldURL.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextFieldURL().setText(String.valueOf(this.getCurrentURL()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (JTextFieldURL.action.actionPerformed(java.awt.event.ActionEvent) --> SimpleWebBrowserPanel.currentURL)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setCurrentURL(new java.net.URL(getJTextFieldURL().getText()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Gets the currentContent property (java.lang.String) value.
 * @return The currentContent property value.
 * @see #setCurrentContent
 */
public java.lang.String getCurrentContent() {
	return fieldCurrentContent;
}
/**
 * Gets the currentURL property (java.net.URL) value.
 * @return The currentURL property value.
 * @see #setCurrentURL
 */
public java.net.URL getCurrentURL() {
	return fieldCurrentURL;
}
/**
 * Return the JButtonBack property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonBack() {
	if (ivjJButtonBack == null) {
		try {
			ivjJButtonBack = new javax.swing.JButton();
			ivjJButtonBack.setName("JButtonBack");
			ivjJButtonBack.setToolTipText("Go back");
			ivjJButtonBack.setText(" << ");
			ivjJButtonBack.setMaximumSize(new java.awt.Dimension(40, 20));
			ivjJButtonBack.setPreferredSize(new java.awt.Dimension(40, 20));
			ivjJButtonBack.setEnabled(false);
			ivjJButtonBack.setMinimumSize(new java.awt.Dimension(40, 20));
			ivjJButtonBack.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonBack;
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonForward() {
	if (ivjJButtonForward == null) {
		try {
			ivjJButtonForward = new javax.swing.JButton();
			ivjJButtonForward.setName("JButtonForward");
			ivjJButtonForward.setToolTipText("Go forward");
			ivjJButtonForward.setText(" >> ");
			ivjJButtonForward.setMaximumSize(new java.awt.Dimension(40, 20));
			ivjJButtonForward.setPreferredSize(new java.awt.Dimension(40, 20));
			ivjJButtonForward.setEnabled(false);
			ivjJButtonForward.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJButtonForward.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonForward;
}
/**
 * Return the JEditorPane1 property value.
 * @return cbit.gui.JEditorPaneBeanFix
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.JEditorPaneBeanFix getJEditorPane1() {
	if (ivjJEditorPane1 == null) {
		try {
			ivjJEditorPane1 = new cbit.gui.JEditorPaneBeanFix();
			ivjJEditorPane1.setName("JEditorPane1");
			ivjJEditorPane1.setAutoscrolls(true);
			ivjJEditorPane1.setDoubleBuffered(true);
			ivjJEditorPane1.setBounds(0, 0, 160, 120);
			ivjJEditorPane1.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPane1;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonBack = new java.awt.GridBagConstraints();
			constraintsJButtonBack.gridx = 1; constraintsJButtonBack.gridy = 1;
			constraintsJButtonBack.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButtonBack(), constraintsJButtonBack);

			java.awt.GridBagConstraints constraintsJButtonForward = new java.awt.GridBagConstraints();
			constraintsJButtonForward.gridx = 2; constraintsJButtonForward.gridy = 1;
			constraintsJButtonForward.insets = new java.awt.Insets(4, 0, 4, 4);
			getJPanel1().add(getJButtonForward(), constraintsJButtonForward);

			java.awt.GridBagConstraints constraintsJTextFieldURL = new java.awt.GridBagConstraints();
			constraintsJTextFieldURL.gridx = 4; constraintsJTextFieldURL.gridy = 1;
			constraintsJTextFieldURL.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldURL.weightx = 1.0;
			constraintsJTextFieldURL.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextFieldURL(), constraintsJTextFieldURL);

			java.awt.GridBagConstraints constraintsJProgressBar1 = new java.awt.GridBagConstraints();
			constraintsJProgressBar1.gridx = 3; constraintsJProgressBar1.gridy = 1;
			constraintsJProgressBar1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJProgressBar1(), constraintsJProgressBar1);
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
 * Return the JProgressBar1 property value.
 * @return javax.swing.JProgressBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JProgressBar getJProgressBar1() {
	if (ivjJProgressBar1 == null) {
		try {
			ivjJProgressBar1 = new javax.swing.JProgressBar();
			ivjJProgressBar1.setName("JProgressBar1");
			ivjJProgressBar1.setPreferredSize(new java.awt.Dimension(50, 14));
			ivjJProgressBar1.setMinimumSize(new java.awt.Dimension(50, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJProgressBar1;
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
			ivjJScrollPane1.setDoubleBuffered(true);
			getJScrollPane1().setViewportView(getJEditorPane1());
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
 * Return the JTextFieldURL property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldURL() {
	if (ivjJTextFieldURL == null) {
		try {
			ivjJTextFieldURL = new javax.swing.JTextField();
			ivjJTextFieldURL.setName("JTextFieldURL");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldURL;
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2004 3:49:20 PM)
 */
public Dimension getMinimumSize() {

	return minSize;

}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2004 3:44:30 PM)
 */
public Dimension getPreferredSize() {

	return prefSize;
}
/**
 * Comment
 */
private void goBack() {
	setCurrentURL((URL)browsingHistory.previous());
}
/**
 * Comment
 */
private void goForward() {
	setCurrentURL((URL)browsingHistory.next());
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
	setLoading(false);
}
/**
 * Comment
 */
private void handleNavigation(HyperlinkEvent hyperlinkEvent) {
	if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		try {
			setCurrentURL(hyperlinkEvent.getURL());
			getJEditorPane1().setPage(hyperlinkEvent.getURL());
		} catch (IOException exc) {
			handleException(exc);
		}
	}
	if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ENTERED) {
		getJEditorPane1().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.EXITED) {
		getJEditorPane1().setCursor(Cursor.getDefaultCursor());
	}
}
/**
 * Method to handle events for the HyperlinkListener interface.
 * @param e javax.swing.event.HyperlinkEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJEditorPane1()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(this);
	getJButtonBack().addActionListener(this);
	getJButtonForward().addActionListener(this);
	getJTextFieldURL().addActionListener(this);
	getJEditorPane1().addHyperlinkListener(this);
	getJEditorPane1().addPropertyChangeListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimpleWebBrowserPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(617, 308);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 2;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.weightx = 1.0;
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2001 7:38:25 PM)
 * @return boolean
 */
public boolean isLoading() {
	return loading;
}
/**
 * Comment
 */
public void jEditorPane1_Page(java.net.URL arg1) {
	setCurrentContent(getJEditorPane1().getText());
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleWebBrowserPanel aSimpleWebBrowserPanel;
		aSimpleWebBrowserPanel = new SimpleWebBrowserPanel();
		frame.setContentPane(aSimpleWebBrowserPanel);
		frame.setSize(aSimpleWebBrowserPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		new PropertyLoader();
		aSimpleWebBrowserPanel.setCurrentURL(new java.net.URL(PropertyLoader.getRequiredProperty(PropertyLoader.userGuideURLProperty)));
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("currentURL"))) 
		connEtoC5(evt);
	if (evt.getSource() == this && (evt.getPropertyName().equals("currentURL"))) 
		connEtoM2(evt);
	if (evt.getSource() == this && (evt.getPropertyName().equals("currentURL"))) 
		connEtoC4(evt);
	if (evt.getSource() == this && (evt.getPropertyName().equals("currentURL"))) 
		connEtoM1(evt);
	if (evt.getSource() == getJEditorPane1() && (evt.getPropertyName().equals("page"))) 
		connEtoC6(evt);
	if (evt.getSource() == getJEditorPane1() && (evt.getPropertyName().equals("page"))) 
		connEtoC7(evt);
	// user code begin {2}
	// user code end
}
/**
 * Sets the currentContent property (java.lang.String) value.
 * @param currentContent The new value for the property.
 * @see #getCurrentContent
 */
private void setCurrentContent(java.lang.String currentContent) {
	String oldValue = fieldCurrentContent;
	fieldCurrentContent = currentContent;
	firePropertyChange("currentContent", oldValue, currentContent);
}
/**
 * Sets the currentURL property (java.net.URL) value.
 * @param currentURL The new value for the property.
 * @see #getCurrentURL
 */
public void setCurrentURL(java.net.URL currentURL) {
	java.net.URL oldValue = fieldCurrentURL;
	fieldCurrentURL = currentURL;
	firePropertyChange("currentURL", oldValue, currentURL);
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2001 7:38:25 PM)
 * @param newLoading boolean
 */
private void setLoading(boolean newLoading) {
	loading = newLoading;
}
/**
 * Comment
 */
private void startProgressBar() {
	Thread thread = new Thread() {
		public void run() {
			int i = 0;
			while (loading) {
				try {
					getJProgressBar1().setValue(i);
					i += 10;
					if (i >= getJProgressBar1().getMaximum()) {
						i = 0;
					}
					sleep(100);
				} catch (InterruptedException exc) {
				}
			}
			getJProgressBar1().setValue(0);
		}
	};
	setLoading(true);
	thread.setPriority(Thread.NORM_PRIORITY + 2);
	thread.setName("SimpleWebBrowser progressBar");
	thread.start();
}
/**
 * Comment
 */
private void updateHistory(java.net.URL newURL) {
	browsingHistory.add(newURL);
	getJButtonForward().setEnabled(browsingHistory.hasNext());
	getJButtonBack().setEnabled(browsingHistory.hasPrevious());
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G3D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BC8BF494D5168103C48D987EC1C043CF8284080AEB56431EE1E6F1E5B46B44B34C8CBBE6DC9CB30ABB38628EF3E4DC56F979G4184089215F1A246951D20B1A690150988C98810C0138493E3D883696A6E6A6ECABADDCDF505A492E46FFB2FEEF5253BBA895971AC675CD37D6E3B6F5D6F3B6F3ED795C87E5BF1DB53B37289C95FCA34FF1F259332260610D7577B67A3AE465B12C38C7F5E81F805FCE9
	4B02E79C74C55F5D1213C77E63F2B63417C3BBA531A5678D783D0794B464D5430FB0FC56D741DC5DF73DB7B89F6FB164331D35D33AF261398BA089F07419A5D37E6B3B76EA78D48D0F101EC148BAC61369AD50F0355036824C84C814B97DEAF86EE01C47D49C56685AA64413CD8FA999BB69BC06B9910AFCFC2CC5FB96129753B779A82FE2EF2EB6A6925AABG287219A4A237841EC79ABBDDC76DB6C9AD39EC97BD1E9207686FD4E5DFC5C51354657308DF0B36EAC56E710BCA236095BD0478AE51B9D5EFB61B03
	1043505E42F079D64AE7B97CCE86C853710FB1FC255212538A60D3D037AF7EF2C4133D754AFDA44D75400E5DE1F40BD238CE09CAC037248FEC19FFE2FED8DAC96D3D817A727CADB97B81EA815CGD1GFFE67E3F775887702C322947652EAE59DB6573D5FB9C4AD959A1FA60F7C58574E838AFC53B2CB88861BEBE68ADB611E7B06063AA2FFB9C37C97CA174F74217CBC96ABDD3B3361B58A4B57ECAC61C1F5BA2499F3089675D971677DA7F40F89C487BEBE264CD1EB938FD2B896F021F9F5C3EC11B0728815E25
	AA75F59473F5A17CCE57620363A7B23CEA407358FC1361E9BB927A562B989B2D3BD096CB569C12D7B64D548E31BF0A4E30F4F3999C5D41325CAA5039E33AB94FB7F4D966B27CEA831E4B728413053649E5D8A7GBC3E6E2B26EB353727A5E7B2C08240AA00B4G525B12135F0BB17652310F0799E30D0222D6F7CB9E072890BE763E4A5761A9762A0AD067752B0257AE36880AA458BC221FE8B4ABAB298DEC8346BD40E25F8EBCCE09DED191D4C976426CC217280A0A1F53BCD2798A25D1C4565BACF50940204BC7
	F05E1FD76E0127C7702BDF799C02AA86A8F87F631554A71705961891A6005F5CAEE947293D4AC1FF8BC01BEE0773D5F43EB3228279222242A24B1D5D3EE62A89C96825F60EE13943873F23FA51466BEAA8EEA334337BF01E182AD78D7354C8F6AA3E2074619A9BEF257938208FE3665FDBB1E6EAF6AEA145BF1BE29AB3B1903F15FDBCD6DA7B0257CEAC1B134A455B0556D61375D250E73E0E2E97B80F3F1A72487C9B4C3FCB77D57DC96A2B8810FBA6408A5D468FD79572182D967CA2C6E9038281A6BA829667F2
	CE910A59134FC9F1546EF42324FB924D5DDBB539297FDF5375DB48462D811EDBG76FF8B718220824C84D8853081A07FCACB4E91G4795347D5356C403AD360661790A019F6A7B37305B7F4D272F8BA87B9ECDC6B359B3B5395F50E8377EBF4B4EE3620755D4B6713268D5EBC41F68F5408F3F56373A1A6A2065A9272C0FF90461FD10570EC3A2716B78B555D4F7C1D59549562D0ACD222ACADED720BF157172F75BFC0114083C1E2C267610FBA02F69746B2BE9CE304BDE2F4802D39F22757F9B4BC922C76C8221
	0F4B5DDE1563B79E276B3AD36C53F26226635B884DE91EEE1ADFF95CA68A54B7BEC56A021027A91A24DE2179EA01F72F41C6510023F5DDA2BCD7DD415C5373AE661E7D5FAEA4AB5BFD99E6FBE76C7CDBB37677735CF138BF10FB38CC3F29B1177D37ACB7EAF8BFDFDF4F65503889D322E9EBFE6CA9BADFAB7071GC476631AF71D207D18D73545FEDC76F838C9F5DBB71F4C233E91AFF50B309D5184FEC6375FB7A7DF8D6AEB51EC497B6DA729ADBD065C3F211F5AB2CD403AF7E6BF3763F26D192C59A376AA253B
	5D06F4072F727EC6F83A81222EF2BABE1F570EF4E9005F82D0709D27EF7B4EC8F75001F4AA60233F8B50A58C20FBCA27DB257527EA4FCD5F99650B93112E709A176B70B5CE57FB0D53A5DF23F47DCE240B394673DD3CC63F821EA5BA5FF297526D0DE849A985E80C607345C698793E285325C6846CC269B7C59875D865C63A2CD13C3FE09427AF06E7ADG27FBC9C23AB60DCE5568A2C77179785A785D813A8658D921C338AC741678FBE09DD7D41CE6A1474F842F48599A7D72D1D87BFF2850985CG6D2C51A043
	E8DCAF0F6E445AFFF2E7BCC9FD26ECFB38F3CD79E8ED2F9E9DD8AF499B2B367198FF3416720C053E7851985F0F1FB4569FD5FE3FEC17D86923456638D3467544F193CF3D8CCF27247855BAC8F83D98473127283C34B65250DA2D3853162B693A8278EE82606BD23551F96C5D0A82F3FF7525856D77ABBF15892AAAC5EDD4E417A27A7D5502D2F420C52B3D676C295474CDDEBA079C39F6B1436CAC94355B1DD1FC8337C3738D81FB2C1BE84B5CC401497620AEDB9C502C3D81E5AB31874B660B24743734679BF864
	816EB0C0DBA40EEB35535C29C2526732AE59E714B5DB98E3AAEB74B32E0C24C8AE633A48002C3513EC19ED13D056E9F61435E88C1C038138CC071DC619E247F019D60EC119DE329F04274092D689FFB40A0AD3D63AC4C7910D509221C7ADE1FBD8C9956BBEC1FF9FC0FF1C5EC7F5F14955023D13FA817D7A0D8365D9833CF2EFC4F9CE341B49D3F3A31E258B7E1C4B66BBA9ABBD026200A979FC3BC51C2F95E8E3EEC479AF0B66F172D869BEF8F633BA3CD6722B3252D7E4E17AF8C5358488BF27427239FFAC6259
	E9B54CCB46625C254E115A060FF7B803E5637C66BB115FE160659B0B3A679F25CF3617921CE31FEBE04C741F471809C8DECE766E99E79AB394624772D8C9999B08197C59364C225998B397DCE8534228161C56A87451BC51682398402F84C80BC23B2CF2FD8ECFF71FCFD4BC123733C47F450FBDC5064426D92796A9B483F1FD2766FFC267913C8E0FD8AFDC16DCAC39F0B78DB67EF3C31C3D6BC69DB603FC25B7210CCB5D54EEBE41A5F23AE7F5BAB7504469F44D9252F1194A7A284EFED13548028384A87A9F4A
	0C676531925E552C0571FC5E3FF4603CF9B743FEF0B34EBB3EE3603C0F1E79EFF8F61CF0C8900AB4571735B7C227C9CCEAF52F9CFC7790F1F3602CB4796650F393BF135F417609781B7919FC2B0EDF4470AB8CF8AEFF19027227C0DF79ADF80E4CBAC37729D8E8AF87C886C883480A06F3C7B46E8F05350748906774BAAF9C1F3D0227E9C0E10A6765DAEAAB28A53576AFEB0D714F6D304796EC074A68013A3785357938F7C24615845BB3E89C575BE6217BF8AC74AD87C886D88F10BB2EA5278840B28EF5F754FD
	B1146EACD61B6563002B057A6DF8F02D2D4B7A4FA354713110F17B025A1EF1834771D8FC2F086AE8D203B4C31AB9A9099E872F57F35D4175BABD4FC7421C31GFCCD7C289D57C462B82CC92E1EB456A4ABF6DF4D4889132356EB72E50FB75CC74A4D5B39AF5E2B27F256C21F83209FA096E08540DA00AD6375BB493A4F07698B1E25C3BD416DB94BBEB2BFAC8E99979CEB85371A79E1F7531F4C7DD05D2D2A6C7C40EDACBB3389BF7B165F0AE7FA41B1B2D96510F141324686494AE568EFC79992F599DE93C7A643
	3BA1630263B6BF46EC5CCFC2468503B93FDF8DB92E58145FE26748787D7D1063EAE24CF24F7C869A6FED50578B908310G10823081A0EF82467B765355C3453BFEF589B10FF786A689084BEBF70D4C0F17C246857B31E802D95C3F5E788EB13EAFF1F5CB9074012D31DA943CA7A5FAC660F968570AB18FD5CE60F948834F088918079E93E9FC527C93BB81637300FBE4FAFD96B2AED82F1489E663FEB7C2FE6952D0EBF23DA93FE8B60ECC9C7E9DAA9F77D14748647CBCE4DC483A8D12055FD3FF444E334550D789
	40F11BFD946700F67FC47457FC056262201D246392990E561F741C2079C7B13B67C5C89A26BD624250F19E0D0372986E7DE2383BE73E0EF71E89FFBF18FF467CFE704DB366770305E7DE61F8184378D6847B0B4E8CFEFFD8FA466C7E70AD26430FDF5F4D66767BC5BF5BC8EB84D5507A1ED6697DD948BDE2BDE49F02E71853369D9A4D89D5BE7E094E53E3892F3F5FE22EFF1F055EEF7865EE45AE866B98E1592B778512DD1DAAF6D1F1B84DCDEC3CAAA8AEB144C6B1ECBC6F8BB7FE3AA51C9D392ED9F6032EE769
	791167279AB75E69A4CF44BB1D2B6C5C19B529A527G20FC924677036D036D9F16C921F14B770F8F4E51F31B957A7B81A683A4GA4CF42FD630B33C3566BA7FA872957BF2E8F6F2FCF6B4D7DF524BE3C3F0E578F6DAFCBFDF8FFB5558F6D2F367A417D3546682F638775752FBA697A5F90E4EBB2193773A71B670EA2937C9140B582B8GFA35FE4EBB56E164FDC867BD2A1D720E8E1AABCEEBAF8F43FB3589BE95F09B81322634646C1DE264ED910D3C4BF57CB627B1FE8F6978DFBBF3B4FC23D3113B9A4BB49FAC
	EB889F93FF57E09E93AB9A424744BFB68C9D93498D61E362278DC347C4EA835545A6B9A4C53B2967BA5E66B25A240CE03D62BAC1F31DC91D5EE4D7E40F0716AC7ABAFF530F6B3CF88A2E736758DD18835A7DG132760BADF476A5204A921FEDB8938B5G6B037A381CD63751C7E5BA7E8349A87F2784775F396C5EE28B4C15BB956FA79C9DC65A4F74B9EAE47ABE47A374495DAA4460D4F617A7E37DD5886D43GD6GF52A71DE7B2E9EEAD3A7D41DE01AA61FE8A76EE96C5E6E920EDD83746B813281DF84D0BB8D
	E559FF0E5A47E04DB20EBF1BC96D44EF464B343531AC24D6CA1C9630512AE9213664779F4F33DC3BF69A3F7F582163DFE278CD86BC3F97390161E9BBAF96F40D453A6571F62CDB5646E2DEBDF8AE7C9A786BB973B5F06C1C79BEFE625C607B7859F361729917EFAABBAB96CD07DAEBBA4A7CDF8CE701F61B0E3339A84E8D6D889D17E227384950CE54F18DCD286F2AG5D850A4B3F8D7CF89B625E3CC8F1C7205D2C6316301A5C8D6DFE9DB7379D650B52F1D9D614EF320E932DA8DF1C0E2BE2386550CE51F105ED
	A8DF1A0E9BE3C5794AE7A06EA1AB4AD22B63E687E41E01EB72EDFF383D578DB47DB34245171D45CB4C0C606FF53A98BEEEC67077BA3FF7609DF0A274654FC419DEE0B66B05F60C0E2BE27E1B876D15BAEE0C9375DE23639219CFB7C3BBF7962E2D7B597DA624433272D94CF7AC97D0D98AE7619D668DB2FE57568B3844D9F89FDDE69DF0CFC4EF3BABAACEA91223DAF0D9D80660B96FE19967DCBF8BF35E9F581C0537C33EGE83C9D6F06FA5F473B2104BB6710145F183F2FDA57FCB543FDBB37F5746D0677D59F
	58B2B94FCFAEA14F5550CF6EE0BE38E43C37267ACB997CAC457B278C68276B48C50C7D8F8E6827727EB44B587F07817D54C68B3471DC37075FC35D36BC1CC8BC77C6067D4630768E2E13C7FB96BEB2BD3DE8FA63B66E47FF12703CB46F8E74EDE5A77AFB759D982BAF08743D725E38161CD2G8B00BB8E7B321C340F3E47256FE869FB52C83D6FA6914FB6141E5514F143BBB31586516DB59947F94C51F96CB0600743A378ECE5F6065A904756D0FB0D561EE7A2478EEDACC29250A4833C2671D8974757A42E4BBD
	E4892BF1D4D976B4CBBEFA97CA4AE7536FD797D2D5B41AFC7DFD8C6FCBBE9F686BF35171DDC22F54555D55A4FDAB923E66A630F5598B741133F1DDBEED273161D3C42728A82203D1930D1EB8A8FDA2502EG603ABA1BEAC8F81BE586C132C1D736AEE7876B7A74856A8F51CBBFA9F0E8FCDF3A00EBEA13AEE759C52613648D52290E654E6CB9103B66686F0544EDCCFF45A5F909E63B56EF28FD786F2836406F4597082E6FF8161BABE19E0BBED72BB53CFDAEB17ADE200D1C0376792C8D6D13B8A7B49670FB2AF0
	76D9B5A7E09F4EE32995ED1182FDDC66C7AE86E4FE08691F86FD9B67609A7D540FF93AE0AE62BED3F15D164E4575A709B406A274361F3569B7833C7D1F0E416E9E0CC067988EDD687A4C97074BE3A45F4FF19F6EE26BC968D6E5BFAB6D7C1A6D5EF762BB035839686F33FEBA4EA1F7C3BCD6F7BB1D74D39A34F507CE9F8F743C2E1C69443AB289F049G9BGF2673564944F43B99FE46BCEF4C8AA0DF2BE5763AE742BC527FBDA22716194BCFE9AE3BC174775983F97BF7C72E8536F10A8D867F19F285AB37A0F53
	35EFA0EF8AF907B7BC5FCC9B6138146B9E47EB1407FDF42FCE1897D8839C3F0661138CF8DEAF7CD12F97D6C1DF51FCFCE738391D7AA49A5A71GAB815682EC86483F93EB65A74F1512A16EA01AE51F850AE90F217C657AD5DC9A593760BF5C0E7B72727B9711249BEB423E0FAA3A137B30D6FBA6A5CFCF27CF2E5FAD82552F977AA683A48124G2481EC51757B704BE22E9F3D4E2ED2D5416EE6DF4720F2AD125893C0530392BFE3BDA7E1BC67AD60E72CA2F89ED9406FBDF90E7B3A73FA7600E6986BDE90586F7A
	97846F81D35F22F6505EB6518FB8B43C3FE1D7G2FFD0801E31C67E8DE8E3CA9E41FB5E8FD05BBD91F613B9CFAB6655F943CB5603B0FA8CD2F658B707E7EFDAB1E1BD7836EED423F774CA678EF6331D09C6EA55F5A096E6BA7F25157850B67121ADF54E43C9B4657A41E7B38B1BED0DF96C965BB78BCBF7946387E73EE73E4240719A7CA9BBF59B08F176BA8CDA71AED9E30533BF22F58D328FFB8D2A3C188508F410B4ACB0B8B8F169EC0FDFA1C01F1B5CD439FF71BABB06E350B461C467B7F419D681FA38EFF5E
	F5D2E05C2756610F4B6CB44853B61CF1BC96CA5A75D808473D77FD0F411656D05D4A3D01FEEF7B30E4E47B01530FFB8BD9086B2CDE9DEA1D996B09BC9837FFA1DFE76C1B1E05416B6C37C7597D06615B8F0E7F4249EAC925DB646DD1FADBFB376EC4BB54AC44B34E0B4E60B34E7344F8C6A9F6851FF1528774DFF4851FF12A520D7DB12E60B38E0E67B27E5EE534BD4F0BCF303FC3688799E397E29E5C537EA1BCDB048EAD43F365D99684A424881F2828FD9C4B3EF8557630C7691FD3E83FFFE9783D55B75CFAE5
	A440F96C7BDEF9F03B24DB435F95E5DB4D6F0AF6D94D6F0A722D033F53A930067FA67CDD2B711BF06E47BF4A5477C96043B5G1B81F297356494AEC21F5EE5255FB0513F3E895D57CEB8DC743E167E450D0A7CA78B61F51DA18C7DF7BA73847A7D1CCD06ED280B4E0F78C5827517AA7B98D23B0796280D944965D6B929063FD7203169911D8C4D6B17B7BA30FEB9823A290BF08FEA6F405AB0E1915EE30CE334E9348D103F78FA772609178841FC3173E31A174C3E7D8D6C1818831BD98E24FA96AF46F34107D6E3
	7D63GFC6F62416BFF6E5788056633E8200D87C886C883588C1017G398B60B000854081508F9083B08FA089E0B5C0AA4046840C039F58A1FD06DD5B5A85918B86C510E8B8E8F2BCC5C3D3734BD647FB616600E8BA8D66E930F508F635D9F0696B73500077E2D9CBDAF276AE99DCFF1E338E2918D707673B0D47386FCEF6931D7703833E69A8863E15C37066FABE655DE5DC23BC55E9FD4FFA69FA032335AA899E69DB7675E39390FADD5AF79E3E706BE717EF687573060F1FFFDCE2F31FCFFB77D16023E7F45969
	740B7ABAFD471727636A7C0D32DF2212F03D5F8AF967BF3CDC7A618847B92EFB9CBF2B24F752F55A3CA4788C73B243DB17841FE1A669B94083FDA9CBF00D1D57E32CFCE928FFEB8157960467677CEAD63F7A16F2BE9CF70A6122B41C169315C1F222B20C1C280449090AC9CED44264C4451893B96EFE0566A8972C5DD61D6E87BA6E3C0EDB456624382FA5076A56718FB139A83ED6248C911F42646AE138D68D77B3836EBCFE7B29503884155D3202E37FD5216B56A9F9BC5A591151C8DE88D35D9E6B190C12CFB0
	487D2C0EBB2F7B62F4FB28FF9A5A0D7E515E6FB8110E56C59C772493691E5763F4BB2BED796FBC436F699D5FFF1DF277774A036B6CE3361987BA2FDC0AF7D91B74F7DFEBF41C036176AF03336AB2FCEFA03C1FCB743C4642C47B363FF7E7G0FE1226568316D616F7B62980FC6183FED995668B9D62C512316195FC19B4F42B301A6F119613EEFD9F00DFE255D7C1EF392631D8274294B70ACA2DDA67A38E786BBEBFC82E70D32F27DDCF3C4BF63E6A71A559104042FA322FB823F177586783FB0AC7EBCEE2A8654
	8F15A0C3F36260FB9877F12BC9EE3BB0A037717F9BE23EF599DDB34F122DAA59FECAAE348976CE15E4959EB9D268D748EEDD40BA360257DDBED41179645C4C8C12D5C736710CD6C732E8DA2AA3DBD9562AA3191051FA69B1A04388AAF99926F7721B894A616851C27FA5725DC0AA8ABAB64B88A04D2B21662A9A7FAF3FC8E82F494FA099A010DB6492EC6D21814CE4203BF5911BB1FBC0ABF47ED01A41DAB81E8CB2BE03847D9B841B2DF96F609052B19A6D5E6440B563FD4766B17630771DA9F77318C93BBBF05F
	416B3BBA767E38709E488577E02DB85D769189DF67B5F5899E0FAAF8BA2B3C8EDA73D57729E2607CD2EF27737B11C66B64FDDF316FF6BA8CE37E97D0CB878875F6B07E0197GG7CC4GGD0CB818294G94G88G88G3D0171B475F6B07E0197GG7CC4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3B97GGGG
**end of data**/
}
}
