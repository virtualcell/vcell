package org.vcell.vmicro.workflow;

import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.scratch.VCellSimReader;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;

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
	public final DataHolder<ROI> cellROI_2D;
	public final DataHolder<ROI> bleachedROI_2D;
	public final DataHolder<ROI> backgroundROI_2D;
	

	public ImportImageROIsFrom2DVCell(String id){
		super(id);
		vcellSimLogFile = new DataInput<File>(File.class,"vcellSimLogFile",this);
		bleachedMaskVarName = new DataInput<String>(String.class,"bleachedMaskVarName",this);
		cellSubvolumeName = new DataInput<String>(String.class,"cellSubvolumeName",this);
		ecSubvolumeName = new DataInput<String>(String.class,"ecSubvolumeName",this);
		cellROI_2D = new DataHolder<ROI>(ROI.class,"cellROI_2D",this);
		bleachedROI_2D = new DataHolder<ROI>(ROI.class,"bleachedROI_2D",this);
		backgroundROI_2D = new DataHolder<ROI>(ROI.class,"backgroundROI_2D",this);
		addInput(vcellSimLogFile);
		addInput(bleachedMaskVarName);
		addInput(cellSubvolumeName);
		addInput(ecSubvolumeName);
		addOutput(cellROI_2D);
		addOutput(bleachedROI_2D);
		addOutput(backgroundROI_2D);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		VCSimulationIdentifier vcSimulationIdentifier = VCellSimReader.getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile.getData());
		VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier,0);
		
		DataSetControllerImpl dataSetControllerImpl = VCellSimReader.getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile.getData());

		CartesianMesh cartesianMesh = dataSetControllerImpl.getMesh(vcSimulationDataIdentifier);
		
		 //get rois from log file
		if(bleachedMaskVarName == null) {
			throw new RuntimeException("bleachedMathVarName not set");
		}
		
		//set message to load cell ROI variable 
		if(clientTaskStatusSupport != null)
		{
			clientTaskStatusSupport.setMessage("Loading ROIs...");
		}
		double[] rawROIBleached = dataSetControllerImpl.getSimDataBlock(null,vcSimulationDataIdentifier, bleachedMaskVarName.getData(), 0).getData();
		short[] scaledCellDataShort = new short[rawROIBleached.length];
		short[] scaledBleachedDataShort = new short[rawROIBleached.length];
		short[] scaledBackgoundDataShort = new short[rawROIBleached.length];
		for (int j = 0; j < scaledCellDataShort.length; j++) {
			boolean isCell = cartesianMesh.getCompartmentSubdomainNamefromVolIndex(j).equals(cellSubvolumeName.getData());
			boolean isBackground = cartesianMesh.getCompartmentSubdomainNamefromVolIndex(j).equals(ecSubvolumeName.getData());
			if(isCell)
			{
				scaledCellDataShort[j]= 1;
			}
			if(isBackground)
			{
				scaledBackgoundDataShort[j]= 1;
			}
			if(rawROIBleached[j] > 0.2)
			{
				scaledBleachedDataShort[j]= 1;
			}
			
		}
		UShortImage cellImage =
			new UShortImage(
				scaledCellDataShort,
				cartesianMesh.getOrigin(),
				cartesianMesh.getExtent(),
				cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
		UShortImage bleachedImage =
			new UShortImage(
					scaledBleachedDataShort,
				cartesianMesh.getOrigin(),
				cartesianMesh.getExtent(),
				cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
		UShortImage backgroundImage =
			new UShortImage(
					scaledBackgoundDataShort,
				cartesianMesh.getOrigin(),
				cartesianMesh.getExtent(),
				cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());

		if (clientTaskStatusSupport != null){
			clientTaskStatusSupport.setProgress(100);
		}
		
		ROI cellROI = new ROI(cellImage,"cellROI");
		ROI bleachedROI = new ROI(bleachedImage,"bleachedROI");
		ROI backgroundROI = new ROI(backgroundImage,"backgroundROI");
		
		cellROI_2D.setData(cellROI);
		bleachedROI_2D.setData(bleachedROI);
		backgroundROI_2D.setData(backgroundROI);
	}

}
