package org.vcell.vmicro.op.display;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.vcell.vmicro.workflow.gui.DependentROIPanel;

import cbit.vcell.VirtualMicroscopy.ROI;

public class DisplayDependentROIsOp {

	public void displayDependentROIs(ROI[] allROIs, ROI cellROI, String title, WindowListener listener) {
		DependentROIPanel aDependentROIPanel = new DependentROIPanel(allROIs, cellROI);

		JFrame jframe = new JFrame();
		jframe.setTitle(title);
		jframe.getContentPane().add(aDependentROIPanel);
		jframe.setSize(500,500);
		if (listener!=null){
			jframe.addWindowListener(listener);
		}
		jframe.setVisible(true);
		
		aDependentROIPanel.refresh();
	}
	
}
