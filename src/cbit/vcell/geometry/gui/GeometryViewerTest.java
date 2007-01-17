package cbit.vcell.geometry.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
import cbit.util.*;
import cbit.vcell.geometry.*;
import cbit.vcell.server.*;
/**
 * This type was created in VisualAge.
 */
public class GeometryViewerTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		GeometryViewer aGeometryViewer = new GeometryViewer();
		//
		javax.swing.JFrame top = new javax.swing.JFrame();
		//top.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		top.setSize(827, 621);
		//
		javax.swing.JDesktopPane jdp = new cbit.gui.JDesktopPaneEnhanced();
		javax.swing.JInternalFrame jif = new cbit.gui.JInternalFrameEnhanced();
		jif.setResizable(true);
		jif.setBounds(37, 20, 673, 546);
		jif.setContentPane(aGeometryViewer);
		jdp.add(jif);
		GeometryFilamentCurveDialog gfcd = aGeometryViewer.getGeometryFilamentCurveDialog1();
		gfcd.setClosable(true);
		jdp.add(gfcd);
		jdp.setLayer(gfcd, javax.swing.JLayeredPane.MODAL_LAYER.intValue());
		top.setContentPane(jdp);
		//

		top.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		boolean b3D = args[0].equalsIgnoreCase("true");
		Geometry newGeometry = new Geometry("aaa", (b3D?3:2));
		newGeometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("subVolume2",new Expression("pow(x,2)+pow(y,2)"+(b3D?"+pow(z,2)":"")+"<1")));			
		newGeometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("subVolume1",new Expression(1.0)));
		
		aGeometryViewer.setGeometry(newGeometry);
		
		top.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of GeometryViewerTest");
		exception.printStackTrace(System.out);
	}
}
}
