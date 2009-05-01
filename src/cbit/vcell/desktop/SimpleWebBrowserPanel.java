package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.event.*;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SimpleHistory;

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
		new org.vcell.util.PropertyLoader();
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
	D0CB838494G88G88GAFFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BC8DD4DC5519D9CF343607EAF625B64674ECB4D9C5C51B5A54E5AD5D26B55D0D15F6915146169C29322754350A3BF44DF6F33A17818C98129093E3BCE2C4CD9AE290A1A1868909C07899E0C8A6E1A0C45070E05E4CBCF8B3EF7266CD18A4246EF76FFD774E1B19B7C058FA16F33E7366FD773B777B3D5F7D6EFDB7C15B5FDED2DCE0590ED0C1B1527F8E97A0145906D07F9DC3DB982EE1EFC6A9B27C3D
	833095BD6FAE0227955A6AA7B2CA4B516BD3A570BE8C6FA1A92374F5783D8D25B84ADB61C79CBE39AD88FDF27722BFCE4F6715F646E783F97FFEC799BCB7830CG8EBF1377E27E0FF56C547173F5BC03828BC257D7E11A67BBAAF4DC9E5014G2E8DE0300A527B61C985383631C1279B7E6BA4147FD35532890FE39813C13A5EB7DFFFD622BF94AC5EC76C51D926772906F7A7GD63E904DAB2B01E713FD42FDECECD452EAAE0C0932DC639483931A62EFEC6C133CFED9BCA30E362A4AE4C0D46D02CF14915A79BE
	9E73DCE789998321EC98EF9941FDDD44FC066137861070BE43BBDC981F8B3F517E0C521D7B19EEC777B669328FDE7EFB14F940DB34A50EEE357B29CE037B433A357956DA5EA0FE68A676CE04369400B400BC00726A0C52DA00B7097FCF353F8F4F16D16D386275AA3E963F3FD5F62A3D0AD31461F7E3A334683853621822BA9122BEFE2B335AC41E6900765BFF557D28CD128EB2FFA71F3E8FE5FD77E64B86931BE4A5DD36D82B29ADC62A43B6213C1DF1F94F2B0E0C47C85EE71011F74877C4E131896F4421A78A
	9375F1128C3C57D4E3DF9790DF2F065F85FAFCD07CC6022F78A00C2731F99309997CDE8DED8BBEE031B1381949E2AB3A9D1557FD53548E8BFF1CE0497F004AD0D693ADCB099B0F5DD0C3F93E4EE529A0F83F81CFE5791448085F13204D79E1C6A90D2FBEAB1E2B15359925CDG8EG99A081A089A03516455822768FE799E3F6C155DA03126C94D5C47B76F56C00279852D4214B9750845F18B8A02812B0AA0B8124538CF7E09B0CC67493C86C0F810FCE51A72A02A6A9BE98DD700A1A2886A84DF847D6CC230A24
	55A1F9C5E06075A3B62E5A318D1E3290507A7DCEC1934394347DD287764985E1GC6048160B7354B3CAEEC2FAC50BF9F20280E596177E43C9ED105FC5158E8D31409205F01B5C10EBAEC674BA4F76C045F35F54C46D35D9837905E1773F12ABBDFB50C53A60DE1718575A21BE39BDCB89F2F2AE3B1737CA00B1936CD77226A473FE19AB3A543CF94665651D8C18722674ED6B2A6160B3E5FA28E1A4C17D5F5667368EA0172385F14C761DFE07C4D5CD74F76E0DFD5013EEDG4E834C466E4ECA9A332DC2C0E4D1FA
	D205G931DE10BD3B9B73B2B49130E09F1586E9B81701A04F3F731AEB7767FEBDC3FF7C93FA960398CA08DA09F2062A04882A083A482248264G1455B35B0F38169BECD19C0767D683BF266FDF42EE7FB71F5E48E55F264BE8A6FB21AE776BBAED717F336CB4A6BE33E259448B22CFEB937D224F89BF82FA1B530AF5507314CB61FD46895E8FF96DB8A412G477B2DD8F7C153D4E9B42809FD2226C9BEF738BDC4F88502237EF0CAE43C3E30E2FBA8131057B87DFCAB4E89E30A4FA7126064DD74766BC8CE92E551
	8BC29FD702BE0D62933A703C1E90AF6AB9F1C1574B8867B4B90873AB0D5B6582760DDF153C9072B8C5237975B8DF7DCD67CE030D6A81076B3AC1F84A75AC77CC3E47F24F6ECBF72115677C963335F3615F5DE8C92B2739A33BBE1CFB28CCFF55EDAE7BD7C8EE547181BA3FDE580863A6CE09264F7977FA71F868D0C669F6006AC3EC4E977660F616D77549FEDC11E5EAD2EE6B3C1EF26C9B71FCD0046588A7709EEE3F17FADE0DEA9B50EDC95B7F27875BD2B6647E44C358161F39D95D5BF6085AF1D8FFEA0728BD
	1ABFA2B52B0751E56B6D6BF0FFE82B7D08525171FE436992G3F8CE055C7143E6830116E9E0951D5G3E7EF0184E91C1F7EE1C514907E97BFC7D397830D13EB7A6985D6A43D42EEC1D2E320152E98D186E771C2E210166BBFB8325F7423306733DD7E6F4AB813F86E0DD839D2F79E3A35F0017514D7FB8EC97CC3F78E3239EC7FD0CAE79E35A3ECA274F04E79EG251BD498DDD1A3252BE824F4550DF4BCBAB77EE39F1E83E42FB0AEDC90C2B501C918470D0D5DA464681EE0BDD957B17DF0A32B7DF710F5A7915E
	1381D6B532797268A6D67BA7CEA4A10772F8E13CFDCD16AE87BA921EAFC5E91305B4463DA7B04FE6E8339FE171FD49E62CBFDA8281E5CCA2250F9E1B7BFA0D730962EAFADF0127CBD283DA97A43C900B637ADEACAF2E0DF434DEABDEC7EAC82C2B9378AE86207372F7EEBC4ED8D0D5E16C7E53B6E63F3581AC93D4D52AE6D79537AA86822D02DA35EFC02F3DEF5F562C6B1BF65F6D28690B4FADE6FB216B37F8AC193A9D569B6C517078DA0B8D004A566CE132D91BB2CA67B5B1596E74C44B3673A8262F92379BF8
	24GFDB6C051D1566FD18F4E1D9AA4FDAA6B3D3B0C321688571A4A1A70DB37E564A815717A23E1D917001CAB382C659213B59DE8CA0E417E104874344FA8D373B1AA13679813E9D2BAGCF01A42C9A7A300B2ACBD13D22338AC7E80DB0295510B52C2605B437635F7B18BF3AF7E1DD5CCA2BB0B6013D407C1A49651982DEAB38BCFE0F19BC4F9DE3FB690AB74B48F89D0ABAA928CE98120EF77DB89B8FFD12D15A7089137F05F173B879D56DAEF886C99DEE15821A22DE2C3291FDFC22D68304C73030F46C568936
	F7724338C9FC6C3BA666EA9B5AFF6DC434EC145FAE19714B86DEBB1B196E5B0F61A7D9CB919B631BF2E44CCC1DE5B1B3AFED855A396D6B26B103415ECCE3A5549C0E9979193516F94F3018F9C2E6B6DD8DB4280579A8B3A2E69AG6F81C8E8E1F6B96ABD82CF4FC53F284A12EF22067F225B1EAAC3E2532DE3E5943A01283EB7EFBF044791FCCED9BCA1DC105CA439D0B7CD577F08A14E96FB198ECBC03EB5DC463DBEECB73F6096A91D4B47684ADBB3CA2D2D0C6EE71C0E4AD4F7916B9C90B51BA2B8C1002ACFB1
	B31A1711424EEA66C1FFBA6E92050D1B8238BCBE6E86FF64380F763C8B4F71F627842108F3FD5DB9BBB41A44A42DFB8B2C51E78FD54743FB252663317BA63AA77F9AD9A76C47691E3C18633FC170CE831E4ADF5143648FC1DB56F1360FDCE2476BD4F39B5495G9AC082C0B2C0DA9BDB9FD2FB8F22997669DDBE58BE7B8439AF22B0E57B659358D6D0CB6A6F97CE98631F5AA1419DED075C36C85D0B5AA35FE93FC5B17DEA226D99550F6A7D47D3F89DEF0636E1G8DE081408A00F4007CF626BBBAF9F4A65DC92C
	BA14630033C27DF6BC3A566632F6F8662623A3265F2E28776D9D117DE8AC6E2942BA1A54A08EC8B39D12A8BBE93D5EF4BE3ADE477B79EA983399C0BF3F94591C986CE0B549158EE3CD123A650AE5E31C9C35C017EFF9C7B8C759FADE52EB129F75E1B973202D2CB323F4B7C0B30093E09E4032CEFEA6F97248ACFDC133F42CA728BDF7C8F373436E18FE5131362A534C8FDB7A5EB077C3EBD053487E015A788D12F7707EAF2B136D69FFB9C7D9F3E71435599A590F4AF0270749B0E8E5B25CB0BEB7999647740B0E
	5BD42BD9BF6B44CCE741667C86E66C17E94AEF37BCB7FE87E66C771C55AC77347463F8AF6A023D8FC0830083A084108810524562FD69495619621D9FDDC24C33B3831384C4654D0A11F7F6FEFCB626DF349F533B4C62FE077D9DE47CDE628ECA9074612531D594FC9D925EA350BC74339EE39E4A6D22F9E8FBF7C6E9D5B74BC39B499C417927391B45676D5E396935BC26DF34DE21EE33FE32EFEE7CAE45740B1E138BCC7995AB38DFD2F7ECAD908FE83FBB14394939A226DF4C3C0D12051ED36F34E25FE4C2DBAE
	G45BD5305F1E5BD1057FB183F9674E01C955EC7B86ED6024375A75EA7686720BDE6673C8CF05F5958429A070E72B8B2AD0F591EAF46BBFB267378AEFB7C734165F6737341876C66670329762D948FE3983F0A3076D576694F8F536CE66707EF919D9E5E31050C9D880881321036891A20377DC64367E7B16708A7A07BA03607F948DD2A5334EB4A71C31CE74969787AEFB9ED2E7FEB277179C6C0892AE3E2340E9527F77236F0326B52C4AF9607526CA17DB5C1F50BB1B62AA67DE9DB3C7E07CE4733A355750A47
	20EBAF5EBF527C241DE7E7BADA8FBB5359CB76A449703E8AA00B473CD31CEE7D48B709DB3AFE7C53G5E371558E0CE81B481B8GB49BDBB71EBCBDE33D5E9E1A26DEFF24AF3E3FB27A4C7D3526AF3E3F1E6A1B59DF39FD717D75C25F4C7EDA57B73D3FFAA5E3EC9E6073FF170267FF22AD5256C97AFB2A4DBCF7241B60F3G378E202C975675DE5ACEF9EF9DB772BE48F9A7FBB06F7A5E48312C7A7BF02FB9EF3F89FEBE609682A4832C0C607D13885E759C7F4504B1FE8FF27C103CD1475BDD2A62355769BE6869
	0F9F93277B4DE3E230BFFECC1C6B1FB9A6BC7D71E3C2691FB9A6C27DD817D149A9297AC9BD55F11B57E813BA446A95F7BB4EF5A6F5FA5F182A48B2AED978BCAF3631F91E594B66795BE41E171D00F98950F4024D73F2321EBACE447A4D8338A90085D1EDD44EA70DF27656F17CD21FD17E0F90DBFFABC8ED348C46DAF1021DCF2CD50C340779989FDA71779CD9382884B508411338FF071555DF2B21FFB6C08960ABCE9A4F353FB309ED6A022A93CC53679747D0399D777DC3976BBB85748BG16832483641DE432
	6CBE056DE330E61DBEB78B311D6849F81DBEB71ED223F36320BDECA3599EEBCBFA7EF1AB7141BCBBBD7FA86578A402DFEC4053F3118F889E3F27C0DB45A9D637DC96D95DB26F944B2BBF9F08BF879E9FB01F833F9AB0DF47FFBDB07DBA7E5B01F8790C4A57CCFC1D8E3265F2196FA438FCF8AFBA4DF0E5FE0CAB0777AA0EDBBA01F1CD70BE48F1CDC3CCDF19636E57B0AE955E53B9EEBD4165407BFA0E7B9E61DB5E87F15F47F0BF77B079EAB96EC309495744F18BA4A61F15639EA7369F067790479D9518FC899C
	375B45644B62B84945E44963380CB1AEF39F1B132F58622D3D658E504391AF3EEEA2715260083E2F73AD0237BA226F6B6C7131B360C1E8CBF5B01976921BD576437E2F1F614E931C8D5EBD7D113645FACFF1DCB6716992F8DF514F6656C1851FC3C2BA2C4B6A47B4DD249F16E5F5BFBB4374D9593D364A81707DGBB0F2E9B09B8A742275D0D0D1D2A64EC955CB612A1E84EFB404A46DCB040F2DE8E99F3B53CE7832C9BE0E7C321BF31332164BBEE079D3D79772AEC4795CB7999EA6B7AB3613321C15FDA8B65D9
	594DF87A21BD698C461D68B61EDBE37DA58B5DCB5176F9916DF89E3911317D7E08F6AC6FE3C546761C08F6EC23BB757ED437C776B05D56BF329C495F1B9F770EE11E2E5376C17ADC10F3CB414AB7AF97D2BF7E274A76CB36C166DB3F467C6D9FE431FA1B8FFFD7DE896FEBG72814A4F32367BC89B7E0EDB2FFFD72D66ED87FDECEF0369C9CDB9B83BBD531AA83A15A67DA88F9B67D1EA404F06C7745E4AEC8F15F8165CA7905B74F75B59D8B9CA753E8CC600C683F8CD67F17DD9BAA7593C5C064EA8F88D5394C5
	F6C8FEFC960A324E627B2B6D1FA3CE730F8A7B9EC35BBC6742ED6F79F1FF2F90123CC1EF1FF4C9C4F44E9DA1733272D3704127ECDEBEB801E3432F0AAED1D5C5A721C6BA7DE3A48F8F822D93006A5AB75406625B2CB08AB4032EE4DEFE9A2D6B439A76076843D78A1CBA5FEC0D4D2945DC4EEDC12213640B52E9FF9053AE873AD4CE1B263CCC74D75D128F69364B9F4176213FF70F06FFF70EA12E6FBF10DC1F8B636473311E93635B271864724AA1306790330FC3E076999C0A0D85F61FAA1EFD642130FDA80FDE
	9733C5885A284C634E304C7FCD560E84E8DBB8446668BBB61627D7F15C3B3DEC5E2E9962F76148B7272AE1763E143C63BB837A1DD75FF4E78FC640E34C06AEF6FE364D1A47DC6E4FD19F9EA373C988EACA0014F6815DF6837CBBDA73B073F7038D77F3AAC1084756204B052F52B0DBAF6274F62027F5E51A426A4A9140E9G09GABGB27918AF11F9A7BAA58DC7B99DEB73F96657FCCEB7226078F089F2G47984D65CBA60D77459BDE3946749E9206124F288FAA74677A579669F7A03518EFF833734DB5F36C77
	7D2B6EC7EB148748DDAF47E761B9C071BFA47891831E568B4F72FAC10636744F58B743C4917B247E730CD2BB0087E09E409200544FD92D7C4519DAB4439904C3715B2018168D65AF55EFCF775C6E027FE883DB17D7BCF8B7CA392EAD6E7728744F298F7374E755B3379460A755EF2F9B6BD7F98E6AD400910090C0824032F3CC3FFFBED3CD75434759AD1AA60CF9486D98265C00A4CE0651F823C477D8EF87D8BC271C23FB2CF4F8661C23671EB447FDC53B1AB5E0BD74AD9F892FF73BC72257001B5F42F6503FB6
	618B9CBABEF0F2F3982FDF44E0FDDC27F0DE8EFFA9A4579A74364ACD244DF0AF876FCD691D0243CEE36DD4BBC2759A9EE167778F09EC5F6C875C5B085E77ACC16CAF6F13D89CDBCBF6C8087B7A57E54C579577ACC5ED3FE8333C974757C9A354476BC64275E5CE2032100E73AFC34679DF3EC83694449927D6207D1B0470B8D42EDF0EA3EE1B0593782C5CA7CED6720BA3ED9204G3E88DED5DFDBDDF920F69F53E71B9C6ED7B7B47BFE9FF9437DDE520CB90D361F75055B6FD6E6BF6E14926E77D3F1767D7E569F
	6EF7C018CDBF9A8BBDCE9E8B82DBFBFFFCBEBC1653952BDBFAA05C3EF1F6B2127560140D2DADC9821BE79F774EB44F0C75C48A74CB9368BCA3F7FA0468F976EF47487906616E8745FBE9AD2986C57A7E9D7ECE6D706F8A3343F3825B633C6F0F5E633C080CFB14E1FF749E27A0227D86FF749E272540583E529F3D47E17D290CFB4E9BEDCF736295D2B76E9E4DA8ED9EE5F9705B4E7D709C9046758CCF15A7D9901010A27C222ADD24D8F263D5DF43D23B309F696F1F98FE67D9E7DB2F4C85A80F522EAF1387354B
	374478E7C54BC47333223BC57333226562745FF4D24478F742FFA89A6F04D3BF5E9A443E9F819FCE81AC81D881301A7B3457056FB061FFFD933B2E35BB5D783C967F0B9B0D712FF547577530FB66FF2753CC6E000EAA308CF971788C5F61467E5294BFC16A6750649B13AA39BD9AA55571275CB8B6E551C55034FE698C307AA587F42B98E3EB500D81D69BBA4658B9468F88ED82FE87C89D3B5A3529E692A116AFB6FD087312595D5F700A49F2200F64C02CE766985B972C0E78AED4664CA82DF4CEDF7FD33FD604
	F0BE2B87DABB008610G3084A085A08DA09BA09F20CC4CA85D8D50GE083988170834C87D8A832B89812A0FD461D5B7A81918986D510F0B868F2DCF391F13F5CA66D09B786C4D3B71867646838B826B984B71F1FF627D17FE410E725B83D7EB4E76D6AE5F9F5F63E4B7B047A6E69CB0873FEA9625BF7A6704D1D01B755739AED33F10E52D42737FDD543738D36561AA44852A5F27B318F88FDEE7D1EC7B07E7C39BBB8737C79EE106EFF5C226322DF7F76119244FBF445658A08FC1E7EA0D84EF1DD813B921030A4
	D46F5E405C76A2C3F36C17F8557D68DE25CE4373F43D983D07E9A47892D7749E66A11E8336C3DB484566D80047D816AB563FF900ABF21B5DF1F80A542FBB5D148F456590DC2D0E53F3E2E81A1C981AC5CE8C454909A1131C980A1393C3461CC8F183A11C235CB0F787B95D90471D653891B2A6461D111C1A0763DDC4AE0C370A18A1434BC42EC9029B54F1678D38336C6EE78847A5286CD1D45677F2884FDB17A44B7A5E1150C8BE88D3EE0F79C4C649AF98647EAA471D653EF0BBE37DA3BB0D7E21384DFEC6076B
	A20A5B66E7F4AF72B8BD0C531A7E3B59707B614017DF27BC7D257220BAFF0D584686BAFB5C7C1B8C7F76B545F1D304AE8D5EF35C6C3B017027B244739A89937DEEFFE8D3984F42C44F5137B9631F77BDC8F82C03710BBC2CC63F131F7D56FA4C4F200DFB61B6209974984E7BBC51B57ACDCE73F34EFB886F90504F77303DC87194627D2E1DEE2FF188769AF575FCDF33066FF117FB4C6A8804625791D5D7423FFB7E9C666F1F95FF9AB7EBA36A07DC10E13D096DE2E3A15FA4377DD7C4EE237FB7C40B6B5B70127C
	BBD42C218D1DCA6528B0B62121224A26264A000A36F0813B488C4E3EF030317019250596D45405DE26992D8B95613454050AC9566AC2051051C2F89BE9B10206DE0161DD74E482F3B8F62CB2F09E7DB9128A834796998124F9B556DCADB7BC790B64F3ED5BAD488282F9C4AAC171A48EE0A283DE2D2B4808A591EF31634752CC77467A23E97ADBD0545FB458925DFB87E6100E506867A67B3EB01EF72C3BF6AC6EF9E7E80246CC42C4783C0356F77FCA3E6D2E86FC4E842B958FF9BEC0716B3CBE2FA04B1AA0CF34
	781C3866EB3D2809617D4B339EBCFE0051680D346DC5F24FEA5C5067FF81D0CB87885E075707E497GG7CC4GGD0CB818294G94G88G88GAFFBB0B65E075707E497GG7CC4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1E97GGGG
**end of data**/
}
}
