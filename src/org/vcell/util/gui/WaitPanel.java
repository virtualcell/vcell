package org.vcell.util.gui;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import javax.swing.text.*;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (1/18/2001 2:08:36 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class WaitPanel extends javax.swing.JPanel {
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextPane ivjJTextPaneMessage = null;
	private StyleContext ivjStyleContext1 = null;
	private javax.swing.JLabel ivjJLabelAnimation = null;
	private DefaultStyledDocument ivjDefaultStyledDocument1 = null;

	class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == WaitPanel.this && (evt.getPropertyName().equals("background"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == WaitPanel.this.getJTextPaneMessage() && (evt.getPropertyName().equals("background"))) 
				connPtoP1SetSource();
		};
	};
	/**
	 * WaitPanel constructor comment.
	 */
	public WaitPanel() {
		super();
		initialize();
	}
	/**
	 * connEtoC1:  (DefaultStyledDocumentFactory1.this --> WaitPanel.updateStyle()V)
	 * @param value javax.swing.text.DefaultStyledDocument
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(javax.swing.text.DefaultStyledDocument value) {
		try {
			// user code begin {1}
			// user code end
			this.updateStyle();
			connEtoM2();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}
	/**
	 * connEtoM1:  (WaitPanel.initialize() --> DefaultStyledDocumentFactory1.DefaultStyledDocument(javax.swing.text.StyleContext))
	 * @return javax.swing.text.DefaultStyledDocument
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.text.DefaultStyledDocument connEtoM1() {
		javax.swing.text.DefaultStyledDocument connEtoM1Result = null;
		try {
			// user code begin {1}
			// user code end
			connEtoM1Result = new javax.swing.text.DefaultStyledDocument(getStyleContext1());
			setDefaultStyledDocument1(connEtoM1Result);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
		return connEtoM1Result;
	}
	/**
	 * connEtoM2:  ( (DefaultStyledDocumentFactory1,this --> WaitPanel,updateStyle()V).normalResult --> JTextPaneMessage.document)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoM2() {
		try {
			// user code begin {1}
			// user code end
			getJTextPaneMessage().setDocument(getDefaultStyledDocument1());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}
	/**
	 * connPtoP1SetSource:  (WaitPanel.background <--> JTextPaneMessage.background)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				this.setBackground(getJTextPaneMessage().getBackground());
				// user code begin {2}
				// user code end
				ivjConnPtoP1Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP1Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}
	/**
	 * connPtoP1SetTarget:  (WaitPanel.background <--> JTextPaneMessage.background)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				getJTextPaneMessage().setBackground(this.getBackground());
				// user code begin {2}
				// user code end
				ivjConnPtoP1Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP1Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}
	/**
	 * Return the DefaultStyledDocumentFactory1 property value.
	 * @return javax.swing.text.DefaultStyledDocument
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.text.DefaultStyledDocument getDefaultStyledDocument1() {
		// user code begin {1}
		// user code end
		return ivjDefaultStyledDocument1;
	}
	/**
	 * Return the JLabel1 property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelAnimation() {
		if (ivjJLabelAnimation == null) {
			try {
				ivjJLabelAnimation = new javax.swing.JLabel();
				ivjJLabelAnimation.setName("JLabelAnimation");
				ivjJLabelAnimation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/planet.gif")));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelAnimation;
	}
	/**
	 * Return the JTextPaneMessage property value.
	 * @return javax.swing.JTextPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextPane getJTextPaneMessage() {
		if (ivjJTextPaneMessage == null) {
			try {
				ivjJTextPaneMessage = new javax.swing.JTextPane();
				ivjJTextPaneMessage.setName("JTextPaneMessage");
				ivjJTextPaneMessage.setEditable(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextPaneMessage;
	}
	/**
	 * Return the StyleContext1 property value.
	 * @return javax.swing.text.StyleContext
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.text.StyleContext getStyleContext1() {
		if (ivjStyleContext1 == null) {
			try {
				ivjStyleContext1 = new javax.swing.text.StyleContext();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStyleContext1;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
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
		this.addPropertyChangeListener(ivjEventHandler);
		getJTextPaneMessage().addPropertyChangeListener(ivjEventHandler);
		connPtoP1SetTarget();
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("WaitPanel");
			setLayout(new java.awt.GridBagLayout());
			setSize(339, 76);

			java.awt.GridBagConstraints constraintsJLabelAnimation = new java.awt.GridBagConstraints();
			constraintsJLabelAnimation.gridx = 0; constraintsJLabelAnimation.gridy = 0;
			constraintsJLabelAnimation.ipadx = 10;
			constraintsJLabelAnimation.ipady = 5;
			constraintsJLabelAnimation.insets = new java.awt.Insets(2, 2, 2, 2);
			add(getJLabelAnimation(), constraintsJLabelAnimation);

			java.awt.GridBagConstraints constraintsJTextPaneMessage = new java.awt.GridBagConstraints();
			constraintsJTextPaneMessage.gridx = 1; constraintsJTextPaneMessage.gridy = 0;
			constraintsJTextPaneMessage.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextPaneMessage.weightx = 1.0;
			constraintsJTextPaneMessage.weighty = 1.0;
			constraintsJTextPaneMessage.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getJTextPaneMessage(), constraintsJTextPaneMessage);
			initConnections();
			connEtoM1();
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
			javax.swing.JFrame frame = new javax.swing.JFrame();
			WaitPanel aWaitPanel;
			aWaitPanel = new WaitPanel();
			frame.setContentPane(aWaitPanel);
			frame.setSize(aWaitPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.show();
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
			aWaitPanel.setMessage("Test message\nsecond line");
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * Set the DefaultStyledDocumentFactory1 to a new value.
	 * @param newValue javax.swing.text.DefaultStyledDocument
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setDefaultStyledDocument1(javax.swing.text.DefaultStyledDocument newValue) {
		if (ivjDefaultStyledDocument1 != newValue) {
			try {
				ivjDefaultStyledDocument1 = newValue;
				connEtoC1(ivjDefaultStyledDocument1);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		};
		// user code begin {3}
		// user code end
	}
	/**
	 * 
	 * @param text java.lang.String
	 */
	public void setMessage(java.lang.String text) {
		try {
			getDefaultStyledDocument1().remove(0, getDefaultStyledDocument1().getLength());
			getDefaultStyledDocument1().insertString(0, text, getStyleContext1().getStyle(StyleContext.DEFAULT_STYLE));
		} catch (BadLocationException exc) {
			handleException(exc);
		}
	}
	/**
	 * Comment
	 */
	private void updateStyle() {
		Style style = getStyleContext1().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontSize(style, 14);
		StyleConstants.setForeground(style, Color.red);
	}
}
