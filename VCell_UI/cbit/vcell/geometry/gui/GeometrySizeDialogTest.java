package cbit.vcell.geometry.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
/**
 * This type was created in VisualAge.
 */
public class GeometrySizeDialogTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		GeometrySizeDialog aGeometrySizeDialog;
		aGeometrySizeDialog = new GeometrySizeDialog(new javax.swing.JFrame(),true);
		aGeometrySizeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aGeometrySizeDialog.init(GeometryTest.getImageExample2D());
		aGeometrySizeDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Dialog");
		exception.printStackTrace(System.out);
	}
}
}
