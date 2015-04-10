package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vmicro.op.Generate2DSimBioModelOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.biomodel.BioModel;

public class Generate2DSimBioModel extends Task {
	//
	// inputs
	//
	public final DataInput<Extent> extent;
	public final DataInput<ROI> cellROI_2D;
	public final DataInput<double[]> timeStamps;
	public final DataInput<Integer> indexFirstPostbleach;
	public final DataInput<Double> primaryDiffusionRate;
	public final DataInput<Double> primaryFraction;
	public final DataInput<Double> bleachMonitorRate;
	public final DataInput<Double> secondaryDiffusionRate;
	public final DataInput<Double> secondaryFraction;
	public final DataInput<Double> bindingSiteConcentration;
	public final DataInput<Double> bindingOnRate;
	public final DataInput<Double> bindingOffRate;
	public final DataInput<String> extracellularName;
	public final DataInput<String> cytosolName;
	public final DataInput<User> owner;
	public final DataInput<KeyValue> simKey;
	//
	// outputs
	//
	public final DataOutput<BioModel> bioModel_2D;
	

	public Generate2DSimBioModel(String id){
		super(id);
		extent = new DataInput<Extent>(Extent.class,"extent", this);
		cellROI_2D = new DataInput<ROI>(ROI.class,"cellROI_2D",this);
		timeStamps = new DataInput<double[]>(double[].class,"timeStamps",this);
		indexFirstPostbleach = new DataInput<Integer>(Integer.class,"indexFirstPostbleach",this);
		primaryDiffusionRate = new DataInput<Double>(Double.class,"primaryDiffusionRate",this);
		primaryFraction = new DataInput<Double>(Double.class,"primaryFraction", this);
		bleachMonitorRate = new DataInput<Double>(Double.class,"bleachMonitorRate", this);
		secondaryDiffusionRate = new DataInput<Double>(Double.class,"secondaryDiffusionRate", this, true);	
		secondaryFraction = new DataInput<Double>(Double.class,"secondaryFraction", this, true);
		bindingSiteConcentration = new DataInput<Double>(Double.class,"bindingSiteConcentration", this, true);
		bindingOnRate = new DataInput<Double>(Double.class,"bindingOnRate", this, true);
		bindingOffRate = new DataInput<Double>(Double.class,"bindingOffRate", this, true);
		extracellularName = new DataInput<String>(String.class,"extracellularName", this);
		cytosolName = new DataInput<String>(String.class,"cytosolName", this);
		owner = new DataInput<User>(User.class,"owner",this);
		simKey = new DataInput<KeyValue>(KeyValue.class,"simKey", this);
		bioModel_2D = new DataOutput<BioModel>(BioModel.class,"bioModel_2D",this);
		addInput(extent);
		addInput(cellROI_2D);
		addInput(timeStamps);
		addInput(indexFirstPostbleach);
		addInput(primaryDiffusionRate);
		addInput(primaryFraction);
		addInput(bleachMonitorRate);
		addInput(secondaryDiffusionRate);
		addInput(secondaryFraction);
		addInput(bindingSiteConcentration);
		addInput(bindingOnRate);
		addInput(bindingOffRate);
		addInput(extracellularName);
		addInput(cytosolName);
		addInput(owner);
		addInput(simKey);
		addOutput(bioModel_2D);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get inputs
		Extent extent = context.getData(this.extent);
		ROI cellROI_2D = context.getData(this.cellROI_2D);
		double[] timeStamps = context.getData(this.timeStamps);
		Integer indexFirstPostbleach = context.getData(this.indexFirstPostbleach);
		double primaryDiffusionRate = context.getData(this.primaryDiffusionRate);
		double primaryFraction = context.getData(this.primaryFraction);
		double bleachMonitorRate = context.getData(this.bleachMonitorRate);
		Double secondaryDiffusionRate = context.getData(this.secondaryDiffusionRate);
		double secondaryFraction = context.getData(this.secondaryFraction);
		double bindingSiteConcentration = context.getData(this.bindingSiteConcentration);
		double bindingOnRate = context.getData(this.bindingOnRate);
		double bindingOffRate = context.getData(this.bindingOffRate);
		String extracellularName = context.getData(this.extracellularName);
		String cytosolName = context.getData(this.cytosolName);
		User owner = context.getData(this.owner);
		KeyValue simKey = context.getData(this.simKey);

		
		// do op
		Generate2DSimBioModelOp op = new Generate2DSimBioModelOp();
		BioModel bioModel = op.generateBioModel(
				extent, 
				cellROI_2D, 
				timeStamps, 
				indexFirstPostbleach, 
				primaryDiffusionRate, 
				primaryFraction, 
				bleachMonitorRate, 
				secondaryDiffusionRate, 
				secondaryFraction, 
				bindingSiteConcentration, 
				bindingOnRate, 
				bindingOffRate, 
				extracellularName, 
				cytosolName, 
				owner, 
				simKey);
		
		// set output
		context.setData(this.bioModel_2D,bioModel);
	}

}
