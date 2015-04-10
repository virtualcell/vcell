package org.vcell.vmicro.workflow.task;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.ImportImageROIsFrom2DVCellOp;
import org.vcell.vmicro.op.ImportImageROIsFrom2DVCellOp.ImageROIs;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.ROI;

public class ImportImageROIsFrom2DVCell extends Task {
	
	//
	// inputs
	//
	public final DataInput<File> vcellSimLogFile;
	public final DataInput<String> bleachedMaskVarName;
	public final DataInput<String> cellSubvolumeName;
	public final DataInput<String> ecSubvolumeName;
	
	//
	// outputs
	//
	public final DataOutput<ROI> cellROI_2D;
	public final DataOutput<ROI> bleachedROI_2D;
	public final DataOutput<ROI> backgroundROI_2D;
	

	public ImportImageROIsFrom2DVCell(String id){
		super(id);
		vcellSimLogFile = new DataInput<File>(File.class,"vcellSimLogFile",this);
		bleachedMaskVarName = new DataInput<String>(String.class,"bleachedMaskVarName",this);
		cellSubvolumeName = new DataInput<String>(String.class,"cellSubvolumeName",this);
		ecSubvolumeName = new DataInput<String>(String.class,"ecSubvolumeName",this);
		cellROI_2D = new DataOutput<ROI>(ROI.class,"cellROI_2D",this);
		bleachedROI_2D = new DataOutput<ROI>(ROI.class,"bleachedROI_2D",this);
		backgroundROI_2D = new DataOutput<ROI>(ROI.class,"backgroundROI_2D",this);
		addInput(vcellSimLogFile);
		addInput(bleachedMaskVarName);
		addInput(cellSubvolumeName);
		addInput(ecSubvolumeName);
		addOutput(cellROI_2D);
		addOutput(bleachedROI_2D);
		addOutput(backgroundROI_2D);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		File vcellSimLogFile = context.getData(this.vcellSimLogFile);
		String bleachedMaskVarName = context.getData(this.bleachedMaskVarName);
		String cellSubvolumeName = context.getData(this.cellSubvolumeName);
		String ecSubvolumeName = context.getData(this.ecSubvolumeName);
					
		// do op
		ImportImageROIsFrom2DVCellOp op = new ImportImageROIsFrom2DVCellOp();
		ImageROIs imageRois = op.importROIs(vcellSimLogFile, bleachedMaskVarName, cellSubvolumeName, ecSubvolumeName);
					
		// set output
		context.setData(cellROI_2D,imageRois.cellROI_2D);
		context.setData(bleachedROI_2D,imageRois.bleachedROI_2D);
		context.setData(backgroundROI_2D,imageRois.backgroundROI_2D);
	}

}
