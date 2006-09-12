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
		cbit.vcell.model.Model model = cbit.vcell.model.ModelTest.getExample();
		cbit.vcell.model.Feature feature = (Feature)model.getStructure("Cytosol");

		javax.swing.JFrame frame = new javax.swing.JFrame();
		FeatureDialog aFeatureDialog;
		aFeatureDialog = new FeatureDialog();
		frame.setContentPane(aFeatureDialog);
		frame.setSize(aFeatureDialog.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);

		aFeatureDialog.setTitle("Feature Dialog for feature="+feature.getName());
		aFeatureDialog.setModel(model);
		aFeatureDialog.setChildFeature(feature);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.model.FeatureDialog");
		exception.printStackTrace(System.out);
	}
}
}
