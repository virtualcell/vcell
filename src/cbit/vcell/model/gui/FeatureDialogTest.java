package cbit.vcell.model.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class FeatureDialogTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		javax.swing.JDesktopPane desktop = new cbit.gui.JDesktopPaneEnhanced();
		frame.setSize(500,500);
		frame.setContentPane(desktop);

		
		cbit.vcell.model.Model model = cbit.vcell.model.ModelTest.getExample();
		cbit.vcell.model.Feature feature = (Feature)model.getStructure("Cytosol");

		FeatureDialog aFeatureDialog = new FeatureDialog();
		aFeatureDialog.setModel(model);
		aFeatureDialog.setChildFeature(feature);
		aFeatureDialog.setVisible(true);

		desktop.add(aFeatureDialog, aFeatureDialog.getName());
		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.model.FeatureDialog");
		exception.printStackTrace(System.out);
	}
}
}
