package org.vcell.vmicro.workflow.task;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.gui.DependentROIPanel;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

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
	public final DataOutput<Boolean> displayed;

	
	public DisplayDependentROIs(String id){
		super(id);
		imageROIs = new DataInput<ROI[]>(ROI[].class,"imageROIs", this);
		cellROI = new DataInput<ROI>(ROI.class,"cellROI", this);
		title = new DataInput<String>(String.class,"title",this);
		displayed = new DataOutput<Boolean>(Boolean.class,"displayed",this);
		addInput(imageROIs);
		addInput(cellROI);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		displayDependentROIs(context.getData(imageROIs), context.getData(cellROI), context.getDataWithDefault(title,"no title"), null);
		context.setData(displayed,true);
	}
	
	public static void displayDependentROIs(ROI[] allROIs, ROI cellROI, String title, WindowListener listener) {
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
