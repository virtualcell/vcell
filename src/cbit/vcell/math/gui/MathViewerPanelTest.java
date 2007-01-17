package cbit.vcell.math.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
/**
 * This type was created in VisualAge.
 */
public class MathViewerPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MathViewerPanel aMathViewerPanel;
		aMathViewerPanel = new MathViewerPanel();
		frame.setContentPane(aMathViewerPanel);
		frame.setSize(aMathViewerPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		MathDescription mathDesc = MathDescriptionTest.getExample();
		aMathViewerPanel.setMathDescription(mathDesc);
		frame.setVisible(true);
	} catch (Throwable exception) {
		exception.printStackTrace(System.out);
		System.err.println("Exception occurred in main() of MathDescPanel");
	}
}
}
