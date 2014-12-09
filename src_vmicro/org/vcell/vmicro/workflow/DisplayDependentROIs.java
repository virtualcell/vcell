package org.vcell.vmicro.workflow;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.gui.DependentROIPanel;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.VirtualMicroscopy.ROI;

public class DisplayDependentROIs extends Task {
	
	//
	// inputs
	//
	public final DataInput<ROI[]> imageROIs;
	public final DataInput<ROI> cellROI;
	public final DataInput<String> title;
		
	//
	// outputs
	//
	public final DataHolder<Boolean> displayed;

	
	public DisplayDependentROIs(String id){
		super(id);
		imageROIs = new DataInput<ROI[]>(ROI[].class,"imageROIs", this);
		cellROI = new DataInput<ROI>(ROI.class,"cellROI", this);
		title = new DataInput<String>(String.class,"title",this);
		displayed = new DataHolder<Boolean>(Boolean.class,"displayed",this);
		addInput(imageROIs);
		addInput(cellROI);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		WindowListener listener = new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				displayed.setDirty();
				updateStatus();
			};
		};
		displayDependentROIs(imageROIs.getData(), cellROI.getData(), title.getData(), listener);
		displayed.setData(true);
	}
	
	public static void displayDependentROIs(ROI[] allROIs, ROI cellROI, String title, WindowListener listener) {
		DependentROIPanel aDependentROIPanel = new DependentROIPanel(allROIs, cellROI);

		JFrame jframe = new JFrame();
		jframe.setTitle(title);
		jframe.getContentPane().add(aDependentROIPanel);
		jframe.setSize(500,500);
		jframe.addWindowListener(listener);
		jframe.setVisible(true);
		
		aDependentROIPanel.refresh();
		
	}
	
}
