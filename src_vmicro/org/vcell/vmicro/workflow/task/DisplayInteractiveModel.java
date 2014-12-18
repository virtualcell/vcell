package org.vcell.vmicro.workflow.task;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.gui.OptModelParamPanel;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.parser.ExpressionException;

public class DisplayInteractiveModel extends Task {
	
	//
	// inputs
	//
	public final DataInput<OptContext> optContext;
	public final DataInput<ROI[]> rois;
	public final DataInput<String> title;
	
	//
	// outputs
	//
	public final DataHolder<Boolean> displayed;
	

	public DisplayInteractiveModel(String id){
		super(id);
		optContext = new DataInput<OptContext>(OptContext.class,"optContext", this);
		rois = new DataInput<ROI[]>(ROI[].class,"rois", this);
		title = new DataInput<String>(String.class,"title",this,true);
		displayed = new DataHolder<Boolean>(Boolean.class,"displayed",this);
		addInput(optContext);
		addInput(rois);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		String titleString = "no title - not connected";
		if (title.bOptional && title.getSource()==null){
			titleString = "no title";
		}else{
			titleString = title.getData();
		}
		WindowListener listener = new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				displayed.setDirty();
				updateStatus();
			};
		};
		displayOptModel(optContext.getData(), rois.getData(), getLocalWorkspace(), titleString, listener);
		displayed.setData(true);
	}
	
	public static void displayOptModel(OptContext optContext, ROI[] rois, LocalWorkspace localWorkspace, String title, WindowListener listener) throws ExpressionException {
		JFrame frame = new javax.swing.JFrame();
		
		OptModelParamPanel optModelParamPanel = new OptModelParamPanel();
		frame.setContentPane(optModelParamPanel);
		frame.setSize(optModelParamPanel.getSize());
		frame.addWindowListener(listener);
		frame.setTitle(title);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		
		optModelParamPanel.init(optContext, rois, localWorkspace);
		
	}

	

}
