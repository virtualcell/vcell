package cbit.image.gui;
import org.vcell.util.Range;

import cbit.vcell.simdata.DisplayAdapterService;
import cbit.vcell.simdata.SourceDataInfo;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/12/00 10:21:50 AM)
 * @author: 
 */
public class ImagePlaneManagerPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ImagePlaneManagerPanel aImagePlaneManagerPanel;
		aImagePlaneManagerPanel = new ImagePlaneManagerPanel();
		if (args[1].equals("true")) {
			aImagePlaneManagerPanel.setMode(ImagePaneModel.MESH_MODE);
		}
		int w = Integer.valueOf(args[2]).intValue();
		int h = Integer.valueOf(args[3]).intValue();
		String type = args[0];
		DisplayAdapterService das = aImagePlaneManagerPanel.getDisplayAdapterServicePanel().getDisplayAdapterService();
		if (type.equals("double")) {
			//das = new DisplayAdapterService();
//			das.setActiveScaleRange(new Range(0, 450));
			das.setValueDomain(new Range(-50, 550));
			das.addColorModelForValues(DisplayAdapterService.createGrayColorModel(), DisplayAdapterService.createGraySpecialColors(), "Gray");
			das.addColorModelForValues(DisplayAdapterService.createBlueRedColorModel(), DisplayAdapterService.createBlueRedSpecialColors(), "BlueRed");
			das.setActiveColorModelID("Gray");
		} else if (type.equals("index")) {
			//das = new DisplayAdapterService();
			das.addColorModelForIndexes(DisplayAdapterService.createGrayColorModel(), "Gray");
			das.addColorModelForIndexes(DisplayAdapterService.createBlueRedColorModel(), "BlueRed");
			das.setActiveColorModelID("Gray");
		}else{
			das.addColorModelForIndexes(DisplayAdapterService.createGrayColorModel(), "Gray");
			das.addColorModelForIndexes(DisplayAdapterService.createBlueRedColorModel(), "BlueRed");
			das.setActiveColorModelID("Gray");
		}
		//aImagePlaneManagerPanel.setDisplayAdapterService(das);
		frame.setContentPane(aImagePlaneManagerPanel);
		frame.setSize(aImagePlaneManagerPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		SourceDataInfo sdi = ImagePaneScrollerTest.getExampleSDI(type, w, h);
		aImagePlaneManagerPanel.setSourceDataInfo(sdi);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}