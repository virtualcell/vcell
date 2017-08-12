package org.vcell.vmicro.op;

import java.io.File;

import org.vcell.vmicro.workflow.data.VCellSimReader;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;

public class ImportImageROIsFrom2DVCellOp {
	
	public static class ImageROIs {
		public ROI cellROI_2D;
		public ROI bleachedROI_2D;
		public ROI backgroundROI_2D;
	}	

	public ImageROIs importROIs(File vcellSimLogFile, String bleachedMaskVarName, String cellSubvolumeName, String ecSubvolumeName) throws Exception {
		VCSimulationIdentifier vcSimulationIdentifier = VCellSimReader.getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile);
		VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier,0);
		
		DataSetControllerImpl dataSetControllerImpl = VCellSimReader.getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile);

		CartesianMesh cartesianMesh = dataSetControllerImpl.getMesh(vcSimulationDataIdentifier);
		
		 //get rois from log file
		if(bleachedMaskVarName == null) {
			throw new RuntimeException("bleachedMathVarName not set");
		}
		
		double[] rawROIBleached = dataSetControllerImpl.getSimDataBlock(null,vcSimulationDataIdentifier, bleachedMaskVarName, 0).getData();
		short[] scaledCellDataShort = new short[rawROIBleached.length];
		short[] scaledBleachedDataShort = new short[rawROIBleached.length];
		short[] scaledBackgoundDataShort = new short[rawROIBleached.length];
		for (int j = 0; j < scaledCellDataShort.length; j++) {
			boolean isCell = cartesianMesh.getCompartmentSubdomainNamefromVolIndex(j).equals(cellSubvolumeName);
			boolean isBackground = cartesianMesh.getCompartmentSubdomainNamefromVolIndex(j).equals(ecSubvolumeName);
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

		ROI cellROI = new ROI(cellImage,"cellROI");
		ROI bleachedROI = new ROI(bleachedImage,"bleachedROI");
		ROI backgroundROI = new ROI(backgroundImage,"backgroundROI");
		
		ImageROIs imageRois = new ImageROIs();
		imageRois.cellROI_2D = cellROI;
		imageRois.bleachedROI_2D = bleachedROI;
		imageRois.backgroundROI_2D = backgroundROI;
		return imageRois;
	}

}
