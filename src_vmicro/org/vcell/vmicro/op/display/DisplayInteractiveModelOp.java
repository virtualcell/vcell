package org.vcell.vmicro.op.display;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.vmicro.workflow.gui.OptModelParamPanel;

import cbit.vcell.parser.ExpressionException;

public class DisplayInteractiveModelOp {
	
	public void displayOptModel(OptContext optContext, NormalizedSampleFunction[] rois, LocalWorkspace localWorkspace, String title, WindowListener listener) throws ExpressionException {
		JFrame frame = new javax.swing.JFrame();
		
		OptModelParamPanel optModelParamPanel = new OptModelParamPanel();
		frame.setContentPane(optModelParamPanel);
		frame.setSize(optModelParamPanel.getSize());
		if (listener!=null){
			frame.addWindowListener(listener);
		}
		if (title!=null){
			frame.setTitle(title);
		}
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		optModelParamPanel.init(optContext, rois, localWorkspace);
		
	}

	

}
