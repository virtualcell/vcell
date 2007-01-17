package cbit.vcell.model.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class ParameterPanelTest {
/**
 * ParameterPanelTest constructor comment.
 */
public ParameterPanelTest() {
	super();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ParameterPanel aParameterPanel;
		aParameterPanel = new ParameterPanel();
		frame.setContentPane(aParameterPanel);
		frame.setSize(aParameterPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		Model model = ModelTest.getExample();
		SimpleReaction sr = (SimpleReaction)model.getReactionSteps()[0];
		Kinetics kinetics = sr.getKinetics();
		aParameterPanel.setKinetics(kinetics);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
