package cbit.vcell.model.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.JTabbedPane;

import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class ModelParametersDialog extends org.vcell.util.gui.JInternalFrameEnhanced {
	private JTabbedPane tabbedPane = null;
	private ModelParameterPanel modelParametersPanel = null;
	private ProblemsPanel problemsPanel = null;
	
/**
 * Constructor
 */
public ModelParametersDialog() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2005 4:16:19 PM)
 */
public void cleanupOnClose() {
	getModelParametersPanel().cleanupOnClose();
	getProblemsPanel().cleanupOnClose();
}

private JTabbedPane getTabbedPane() {
	if (tabbedPane == null) {
		try {
			tabbedPane = new JTabbedPane();
			tabbedPane.setName("JTabbedPane1");
			tabbedPane.insertTab("Parameters and Rate Expressions", null, getModelParametersPanel(), null, 0);
			tabbedPane.insertTab("Issues and Warnings", null, getProblemsPanel(), null, 1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tabbedPane;
}

public ModelParameterPanel getModelParametersPanel() {
	if (modelParametersPanel == null) {
		try {
			modelParametersPanel = new ModelParameterPanel();
			modelParametersPanel.setName("ModelParametersPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return modelParametersPanel;
}

private ProblemsPanel getProblemsPanel() {
	if (problemsPanel == null) {
		try {
			problemsPanel = new ProblemsPanel();
			problemsPanel.setName("ProblemsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return problemsPanel;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

public void init(Model model) {
	getModelParametersPanel().setModel(model);
	getProblemsPanel().setModel(model);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ModelParametersDialog");
		setTitle("Parameters and Rate Expressions");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setClosable(true);
		setSize(542, 495);
		setResizable(true);
		setContentPane(getTabbedPane());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ModelParametersDialog aModelParametersDialog;
		aModelParametersDialog = new ModelParametersDialog();
		aModelParametersDialog.init(ModelTest.getExample_GlobalParams());
		frame.setContentPane(aModelParametersDialog);
		frame.setSize(aModelParametersDialog.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		// frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JInternalFrame");
		exception.printStackTrace(System.out);
	}
}

}
