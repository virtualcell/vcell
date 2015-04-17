/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.vcell.inversepde.BasisGenerator.Medoid;
import org.vcell.inversepde.microscopy.AnnotatedImageDataset_inv;
import org.vcell.inversepde.microscopy.FRAPStudy;
import org.vcell.inversepde.microscopy.ImageUtils;
import org.vcell.inversepde.microscopy.InverseProblemXmlproducer;
import org.vcell.inversepde.microscopy.ROIImage;
import org.vcell.inversepde.microscopy.ROIImage.ROIImageComponent;
import org.vcell.inversepde.services.InversePDERequestManager;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Origin;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;

import ucar.ma2.ArrayDouble;
import ucar.nc2.NetcdfFile;
import cbit.image.ImageException;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.field.db.FieldDataDBOperationResults;
import cbit.vcell.field.db.FieldDataDBOperationSpec;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatusPersistent;
import cbit.vcell.solvers.CartesianMesh;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLDouble;

public class InverseProblemUtilities {


	public static void displayImage(JComponent parent, short[] imageData, int numX, int numY, int numZ, String title) throws ImageException {
		OverlayEditorPanelJAI overlayEditorPanelJAI = new OverlayEditorPanelJAI();
		UShortImage image = new UShortImage(imageData,null,null,numX,numY,numZ);
		int min = imageData[0];
		int max = imageData[0];
		for (int i = 0; i < imageData.length; i++) {
			min = Math.min(min,imageData[i]);
			max = Math.max(max,imageData[i]);
		}
		double scale = 1.0;
		double offset = 0.0;
		overlayEditorPanelJAI.setImages(new ImageDataset(new UShortImage[] { image }, new double[] { 0.0 }, 1),scale,offset,new OverlayEditorPanelJAI.AllPixelValuesRange(min, max));
		
		overlayEditorPanelJAI.setMinimumSize(new Dimension(700,600));
		overlayEditorPanelJAI.setPreferredSize(new Dimension(700,600));
		overlayEditorPanelJAI.deleteROIName(null);//delete all names
		
		overlayEditorPanelJAI.setChannelNames(null);

		overlayEditorPanelJAI.setUnderlayState(true);

		overlayEditorPanelJAI.setContrastToMinMax();
		//overlayEditorPanelJAI.setAllROICompositeImage(vcellROI,OverlayEditorPanelJAI.FRAP_DATA_INIT_PROPERTY);
		final JDialog jDialog = new JDialog(JOptionPane.getFrameForComponent(parent));
		jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		jDialog.setTitle(title);
		jDialog.setModal(true);
		
//		overlayPanel.setROI(vcellROI,OverlayEditorPanelJAI.FRAP_DATA_UPDATEROI_WITHHIGHLIGHT_PROPERTY);

	}
	public static void calculateImageROIs(JComponent parent, InverseProblem inverseProblem) throws MathException, DataAccessException, IOException, ImageException{
		if (inverseProblem==null){
			throw new RuntimeException("inverse problem is null");
		}
		if (inverseProblem.getMicroscopyData().getTimeSeriesImageData()==null){
			throw new RuntimeException("time series is null");
		}
		if (inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_BASIS)==null){
			throw new RuntimeException("Basis ROI missing from zStackImage");
		}
		
		float percent = 0.04f;
		int levels = 1;
					
		ISize size = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getImageDataset().getISize();
		Origin origin = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getImageDataset().getAllImages()[0].getOrigin();
		Extent extent = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getImageDataset().getExtent();
		int numX = size.getX();
		int numY = size.getY();
		int numZ = size.getZ();
		int[] globalSegmentation = new int[numX*numY*numZ];
		int globalSegmentationOffset = 0;
	
		ROIImage segmentationROIImage = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_SEGMENTATION);
		ROIImageComponent[] rois = segmentationROIImage.getROIs();
		if (rois.length==0){
			ROIImage zstackSegImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_SEGMENTATION);
			if (zstackSegImage.getISize().compareEqual(segmentationROIImage.getISize())){
				ROIImageComponent[] zStackROIs = zstackSegImage.getROIs();
				for (int i = 0; i < zStackROIs.length; i++) {
					segmentationROIImage.addROI(zStackROIs[i].getPixelValue(), zStackROIs[i].getName());
					segmentationROIImage.getROI(zStackROIs[i].getName()).setPixels(zStackROIs[i].getPixelsXYZ());
				}
				rois = segmentationROIImage.getROIs();
			}
		}
		for (int i = 0; i < rois.length; i++) {
			short[] data = rois[i].getPixelsXYZ();
			float[] floatPixels = new float[data.length];
			for (int j = 0; j < floatPixels.length; j++) {
				floatPixels[j] = (float)data[j];
			}
			FloatImage originalImage = new FloatImage(floatPixels,origin,extent,numX,numY,numZ);
			try {
				int[] segmentation = new int[originalImage.getPixels().length];
				Arrays.fill(segmentation, -1);
				BasisGenerator seg = new BasisGenerator(originalImage,percent,levels);
				segmentation = seg.isoVolumesDetection();
				BasisGenerator.Medoid[] allSegmentCenters = seg.findMedoids(segmentation,Medoid.Euclidian);
				
				//
				// get list of meaningful cluster centers (representing meaningful clusters (with nonzero field values).
				//
				ArrayList<BasisGenerator.Medoid> segmentCenterArray = new ArrayList<BasisGenerator.Medoid>();
				for (int j = 0; j < allSegmentCenters.length; j++) {
					if ((allSegmentCenters[j].compartmentId == segmentation[allSegmentCenters[j].index]) && (originalImage.getPixels()[allSegmentCenters[j].index]!=0)){
						segmentCenterArray.add(allSegmentCenters[j]);
					}
				}
				//
				// merge with global segmentation (for those pixels where field is nonzero)
				//
				for (int k = 0; k < segmentCenterArray.size(); k++) {
					int mediod_compartment = segmentCenterArray.get(k).compartmentId;
					for (int j = 0; j < segmentation.length; j++) {
						if (originalImage.getPixels()[j]!=0.0f && segmentation[j]==mediod_compartment){
							globalSegmentation[j] = k + globalSegmentationOffset;
						}
					}
					
				}
				globalSegmentationOffset += segmentCenterArray.size();
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
		
		ROIImage roiImage = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_IMAGEROIS);
		short[] roiPixels = roiImage.getMaskImage().getPixels();
		for (int i = 0; i < globalSegmentation.length; i++) {
			roiPixels[i] = (short)globalSegmentation[i];
		}
		ROIImageComponent[] oldROIs = roiImage.getROIs();
		for (int i = 0; i < oldROIs.length; i++) {
			roiImage.removeROI(oldROIs[i]);
		}
		for (int i = 0; i < globalSegmentationOffset; i++) {
			roiImage.addROI((short)i, "roi"+i);
		}
		displayImage(parent, roiPixels, numX, numY, numZ, "image ROIs");
	
		System.out.println("finished");
	}
	
	public static void displayROIImage(JComponent parent, ROIImage roiImage) throws ImageException {
		short[] roiPixels = roiImage.getMaskImage().getPixels();
		ISize size = roiImage.getISize();
		displayImage(parent, roiPixels, size.getX(), size.getY(), size.getZ(), roiImage.getName());
	}

	public static void refreshDependentROIs(AnnotatedImageDataset_inv timeSeriesData){
		UShortImage cellROI_2D = null;
		UShortImage bleachedROI_2D = null;
		UShortImage dilatedROI_2D_1 = null;
    	UShortImage dilatedROI_2D_2 = null;
    	UShortImage dilatedROI_2D_3 = null;
    	UShortImage dilatedROI_2D_4 = null;
    	UShortImage dilatedROI_2D_5 = null;
    	UShortImage erodedROI_2D_0 = null;
    	UShortImage erodedROI_2D_1 = null;
    	UShortImage erodedROI_2D_2 = null;

    	try {
    		cellROI_2D = timeSeriesData.getRoi(ROIImage.ROI_CELL).makeBinaryImage();
    		bleachedROI_2D = timeSeriesData.getRoi(ROIImage.ROI_BLEACHED).makeBinaryImage();

    		dilatedROI_2D_1 = ImageUtils.fastDilate(bleachedROI_2D, 4, cellROI_2D);
			dilatedROI_2D_2 = ImageUtils.fastDilate(bleachedROI_2D, 10, cellROI_2D);
	    	dilatedROI_2D_3 = ImageUtils.fastDilate(bleachedROI_2D, 18, cellROI_2D);
	    	dilatedROI_2D_4 = ImageUtils.fastDilate(bleachedROI_2D, 28, cellROI_2D);
	    	dilatedROI_2D_5 = ImageUtils.fastDilate(bleachedROI_2D, 40, cellROI_2D);

	    	erodedROI_2D_0 = new UShortImage(bleachedROI_2D);
			
			// The erode always causes problems if eroding without checking the bleached length and hight.
			// we have to check the min length of the bleahed area to make sure erode within the length.
			Rectangle bleachRect = bleachedROI_2D.getNonzeroBoundingBox();
			int minLen = Math.min(bleachRect.height, bleachRect.width);
			if((minLen/2.0) < 5)
			{
				erodedROI_2D_1 = ImageUtils.erodeDilate(bleachedROI_2D, ImageUtils.createCircularBinaryKernel(1), bleachedROI_2D,true);
				erodedROI_2D_2 = ImageUtils.erodeDilate(bleachedROI_2D, ImageUtils.createCircularBinaryKernel(2), bleachedROI_2D,true);
			}
			else
			{
				erodedROI_2D_1 = ImageUtils.erodeDilate(bleachedROI_2D, ImageUtils.createCircularBinaryKernel(2), bleachedROI_2D,true);
				erodedROI_2D_2 = ImageUtils.erodeDilate(bleachedROI_2D, ImageUtils.createCircularBinaryKernel(5), bleachedROI_2D,true);
			}			
			
			UShortImage reverseErodeROI_2D_1 = new UShortImage(erodedROI_2D_1);
			reverseErodeROI_2D_1.reverse();
			erodedROI_2D_0.and(reverseErodeROI_2D_1);
			
			UShortImage reverseErodeROI_2D_2 = new UShortImage(erodedROI_2D_2);
			reverseErodeROI_2D_2.reverse();
			erodedROI_2D_1.and(reverseErodeROI_2D_2);
			
			UShortImage reverseDilateROI_2D_4 = new UShortImage(dilatedROI_2D_4);
			reverseDilateROI_2D_4.reverse();
			dilatedROI_2D_5.and(reverseDilateROI_2D_4);

			UShortImage reverseDilateROI_2D_3 = new UShortImage(dilatedROI_2D_3);
			reverseDilateROI_2D_3.reverse();
			dilatedROI_2D_4.and(reverseDilateROI_2D_3);

			UShortImage reverseDilateROI_2D_2 = new UShortImage(dilatedROI_2D_2);
			reverseDilateROI_2D_2.reverse();
			dilatedROI_2D_3.and(reverseDilateROI_2D_2);

			UShortImage reverseDilateROI_2D_1 = new UShortImage(dilatedROI_2D_1);
			reverseDilateROI_2D_1.reverse();
			dilatedROI_2D_2.and(reverseDilateROI_2D_1);

			UShortImage reverseBleach_2D = new UShortImage(bleachedROI_2D);
			reverseBleach_2D.reverse();
			dilatedROI_2D_1.and(reverseBleach_2D);

    	}catch (ImageException e){
    		e.printStackTrace(System.out);
    	}
    	ROIImage imageROI = timeSeriesData.getROIImage(ROIImage.ROIIMAGE_IMAGEROIS);
    	
    	ROIImageComponent roiBackground = imageROI.getROI((short)0);
    	if (roiBackground == null){
    		roiBackground = imageROI.addROI((short)0, ROIImage.ROI_BACKGROUND);
    	}
    	
		ROIImageComponent roiBleachedRing1_2D = imageROI.getROI(ROIImage.ROI_BLEACHED_RING1);
		if (roiBleachedRing1_2D==null){
			roiBleachedRing1_2D = imageROI.addROI((short)1,ROIImage.ROI_BLEACHED_RING1);
		}
		roiBleachedRing1_2D.setPixels(bleachedROI_2D);

		ROIImageComponent roiBleachedRing2_2D = timeSeriesData.getRoi(ROIImage.ROI_BLEACHED_RING2);
		if (roiBleachedRing2_2D==null){
			roiBleachedRing2_2D = imageROI.addROI((short)2,ROIImage.ROI_BLEACHED_RING2);
		}
		roiBleachedRing2_2D.setPixels(erodedROI_2D_1);

		ROIImageComponent roiBleachedRing3_2D = timeSeriesData.getRoi(ROIImage.ROI_BLEACHED_RING3);
		if (roiBleachedRing3_2D==null){
			roiBleachedRing3_2D = imageROI.addROI((short)3,ROIImage.ROI_BLEACHED_RING3);
		}
		roiBleachedRing3_2D.setPixels(erodedROI_2D_0);

		ROIImageComponent roiBleachedRing4_2D = timeSeriesData.getRoi(ROIImage.ROI_BLEACHED_RING4);
		if (roiBleachedRing4_2D==null){
			roiBleachedRing4_2D = imageROI.addROI((short)4,ROIImage.ROI_BLEACHED_RING4);
		}
		roiBleachedRing4_2D.setPixels(dilatedROI_2D_1);

		ROIImageComponent roiBleachedRing5_2D = timeSeriesData.getRoi(ROIImage.ROI_BLEACHED_RING5);
		if (roiBleachedRing5_2D==null){
			roiBleachedRing5_2D = imageROI.addROI((short)5,ROIImage.ROI_BLEACHED_RING5);
		}
		roiBleachedRing5_2D.setPixels(dilatedROI_2D_2);

		ROIImageComponent roiBleachedRing6_2D = timeSeriesData.getRoi(ROIImage.ROI_BLEACHED_RING6);
		if (roiBleachedRing6_2D==null){
			roiBleachedRing6_2D = imageROI.addROI((short)6,ROIImage.ROI_BLEACHED_RING6);
		}
		roiBleachedRing6_2D.setPixels(dilatedROI_2D_3);

		ROIImageComponent roiBleachedRing7_2D = timeSeriesData.getRoi(ROIImage.ROI_BLEACHED_RING7);
		if (roiBleachedRing7_2D==null){
			roiBleachedRing7_2D = imageROI.addROI((short)7,ROIImage.ROI_BLEACHED_RING7);
		}
		roiBleachedRing7_2D.setPixels(dilatedROI_2D_4);

		ROIImageComponent roiBleachedRing8_2D = timeSeriesData.getRoi(ROIImage.ROI_BLEACHED_RING8);
		if (roiBleachedRing8_2D==null){
			roiBleachedRing8_2D = imageROI.addROI((short)8,ROIImage.ROI_BLEACHED_RING8);
		}
		roiBleachedRing8_2D.setPixels(dilatedROI_2D_5);
	}

	
	public static Geometry createGeometry(InverseProblem inverseProblem) throws ImageException, PropertyVetoException{
		if (inverseProblem==null){
			throw new RuntimeException("inverseProblem is null");
		}
		if (inverseProblem.getMicroscopyData().getZStackImageData()==null){
			throw new RuntimeException("zStackImageData is null");
		}
		ROIImage segROIImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_SEGMENTATION);
		if (segROIImage==null){
			throw new RuntimeException("zStackImageData 'Segmentation' roi is missing");
		}
		int numX = segROIImage.getISize().getX();
		int numY = segROIImage.getISize().getY();
		int numZ = segROIImage.getISize().getZ();
		short[] shortPixels = segROIImage.getMaskImage().getPixels();
		byte[] bytePixels = new byte[shortPixels.length];
		for (int i = 0; i < bytePixels.length; i++) {
			bytePixels[i] = (byte)shortPixels[i];
		}
		/*
		 * sample code to see conversion between byte and int (considering that both are signed and overflow can mess things up in a round-trip
		 * int i = 200;
		 * byte b = (byte)i;
		 * int i2 = ((int)b)&0x00ff;
		 * System.out.println("i="+i+", b="+b+", i2="+i2);
		 *
		 */
		VCImageUncompressed vcImage = new VCImageUncompressed(null,bytePixels,segROIImage.getMaskImage().getExtent(),numX,numY,numZ);
		vcImage.setName("vcImage");
		Geometry geometry = new Geometry("geo",vcImage);
		geometry.getGeometrySpec().setExtent(segROIImage.getMaskImage().getExtent());
		geometry.getGeometrySpec().setOrigin(segROIImage.getMaskImage().getOrigin());
		int[] uniquePixelValues = vcImage.getUniquePixelValues();
		VCPixelClass[] pixelsClasses = vcImage.getPixelClasses();
		SubVolume[] subVolumes = new ImageSubVolume[uniquePixelValues.length];
		ROIImageComponent cellROI = segROIImage.getROI(ROIImage.ROI_CELL);
		ROIImageComponent ecROI = segROIImage.getROI(ROIImage.ROI_EXTRACELLULAR);
		for (int i = 0; i < subVolumes.length; i++) {
			if (pixelsClasses[i].getPixel()==cellROI.getPixelValue()){
				subVolumes[i] = new ImageSubVolume(null,new VCPixelClass(null,cellROI.getName(),cellROI.getPixelValue()),i);
			}else if (pixelsClasses[i].getPixel()==ecROI.getPixelValue()){
				subVolumes[i] = new ImageSubVolume(null,new VCPixelClass(null,ecROI.getName(),ecROI.getPixelValue()),i);
			}else{
				throw new RuntimeException("unexpected ROI pixel value "+pixelsClasses[i].getPixel());
			}
		}
		geometry.getGeometrySpec().setSubVolumes(subVolumes);
		return geometry;
	}

	public static void runRefSimMathModel(final InversePDERequestManager inversePDERequestManager, final MathModel mathModel) throws MathException, DataAccessException, IOException, ImageException{
		if (mathModel==null){
			throw new RuntimeException("mathModel is null");
		}
		
		//
		// construct/save/run a custom math model to construct scalar fields used in 3D basis determination (e.g. in Segmentation3D_)
		//
		final Simulation sim = mathModel.getSimulations()[0];
		final VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(sim.getVersion().getVersionKey(),inversePDERequestManager.getUser());
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("submitting simulation "+mathModel.getName()+":"+vcSimID.getID()+" ... ");
					inversePDERequestManager.startSimulation(vcSimID,sim.getMathOverrides().getScanCount());
					System.out.println("submitted simulation "+mathModel.getName()+":"+vcSimID.getID()+" ... ");
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public static void checkRefSimStatus(final InversePDERequestManager inversePDERequestManager, MathModel mathModel) throws ObjectNotFoundException, RemoteException, DataAccessException{
		if (mathModel==null){
			throw new RuntimeException("mathModel is null");
		}
		
		//
		// construct/save/run a custom math model to construct scalar fields used in 3D basis determination (e.g. in Segmentation3D_)
		//
		final Simulation sim = mathModel.getSimulations()[0];
		final VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(sim.getVersion().getVersionKey(),inversePDERequestManager.getUser());
		SimulationStatusPersistent simStatus = inversePDERequestManager.getSimulationStatus(sim.getVersion().getVersionKey());
		int scanCount = sim.getScanCount();
		StringBuffer buffer = new StringBuffer();
		buffer.append("status for referenceSimulation "+mathModel.getName()+":"+vcSimID.getID()+" - with "+scanCount+" jobs\n");
		for (int i = 0; i < scanCount; i++) {
			buffer.append("polling simulation, status = "+simStatus.getJobStatus(i).getSimulationExecutionStatus()+"\n");
		}
		System.out.println(buffer.toString());
	}

	public static void saveBasisROI(final InversePDERequestManager inversePDERequestManager, InverseProblem inverseProblem) throws MathException, DataAccessException, IOException, ImageException{
		if (inverseProblem==null){
			throw new RuntimeException("inverse problem is null");
		}
		if (inverseProblem.getMicroscopyData().getZStackImageData()==null){
			throw new RuntimeException("zstack is null");
		}
		if (inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_BASIS)==null){
			throw new RuntimeException("Basis ROI missing from zStackImage");
		}
		ROIImage roiImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_BASIS);
		String basisFieldDataName = InverseProblemContants.BASES_FIELD_DATA_PREFIX;
		ExternalDataIdentifier basisExternalDataID = saveAsFieldData(inversePDERequestManager,	roiImage.getMaskImage(), InverseProblemContants.FIELDDATA_VARNAME_BASIS, basisFieldDataName);
		inverseProblem.getLinearResponseModel().getBasisFunctions().setBasisFieldDataEDI(basisExternalDataID);
		System.out.println("finished");
	}
	
	private static ExternalDataIdentifier saveAsFieldData(final InversePDERequestManager inversePDERequestManager, UShortImage ushortImage, String varName, String basisFieldDataName)
			throws DataAccessException, IOException, ImageException {
		
		ExternalDataIdentifier externalDataID = null;
		//
		// choose unique name for the fieldData.
		//
		FieldDataDBOperationResults results = inversePDERequestManager.getDocumentManager().fieldDataDBOperation(FieldDataDBOperationSpec.createGetExtDataIDsSpec(inversePDERequestManager.getUser()));
		ExternalDataIdentifier[] idArray = results.extDataIDArr;
		boolean found = true;
		while (found){
			found = false;
			for (int i = 0; i < idArray.length; i++) {
				if (idArray[i].getName().equals(basisFieldDataName)){
					found = true;
					basisFieldDataName = TokenMangler.getNextEnumeratedToken(basisFieldDataName);
				}
			}
		}
		
	
		int dimension = 1;
		if (ushortImage.getNumY() > 1){
			dimension = 2;
		}
		if (ushortImage.getNumZ() > 1){
			dimension = 3;
		}
		Extent extent = ushortImage.getExtent();
		Origin origin = ushortImage.getOrigin();
		ISize isize = new ISize(ushortImage.getNumX(),ushortImage.getNumY(),ushortImage.getNumZ());
		double[] timesArray = new double[] { 0.0 };
	
	
		FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
		fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
		short[] shortPixels = ushortImage.getPixels();
		double[] pixels = new double[shortPixels.length];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] =	((int)shortPixels[i])&0x0000ffff;
		}
		fdos.doubleSpecData = new double[][][] { { pixels } };
		fdos.varNames = new String[] {varName};
		fdos.owner = inversePDERequestManager.getUser();
		fdos.times = timesArray;
		fdos.variableTypes = new VariableType[] {VariableType.VOLUME};
		fdos.origin = origin;
		fdos.extent = extent;
		fdos.isize = isize;
		fdos.cartesianMesh = CartesianMesh.createSimpleCartesianMesh(
				fdos.origin, fdos.extent, fdos.isize,
				new RegionImage(
						new VCImageUncompressed(
								null,
								new byte[fdos.isize.getXYZ()],//empty regions
								fdos.extent,
								fdos.isize.getX(),fdos.isize.getY(),fdos.isize.getZ()
								),dimension,extent,origin,RegionImage.NO_SMOOTHING));
		DocumentManager documentManager = inversePDERequestManager.getDocumentManager();
		fdos.specEDI = documentManager.fieldDataDBOperation( 
				FieldDataDBOperationSpec.createSaveNewExtDataIDSpec(inversePDERequestManager.getUser(),basisFieldDataName,"")
				).extDataID;
		documentManager.fieldDataFileOperation(fdos);
		FieldDataDBOperationResults finalResults = documentManager.fieldDataDBOperation(FieldDataDBOperationSpec.createGetExtDataIDsSpec(inversePDERequestManager.getUser()));
		ExternalDataIdentifier[] finalIDArray = finalResults.extDataIDArr;
		for (int i = 0; i < finalIDArray.length; i++) {
			if (finalIDArray[i].getName().equals(basisFieldDataName)){
				externalDataID = finalIDArray[i];
				break;
			}
		}
		return externalDataID;
	}


	public static void exportVFRAPData(InverseProblem inverseProblem, File outputFile) throws Exception {
		if (inverseProblem==null){
			throw new RuntimeException("inverse problem is null");
		}
		if (inverseProblem.getMicroscopyData().getTimeSeriesImageData()==null){
			throw new RuntimeException("time series image data is null");
		}
		FRAPStudy frapStudy = new FRAPStudy();
		AnnotatedImageDataset_inv timeSeriesData = inverseProblem.getMicroscopyData().getTimeSeriesImageData();
		ISize roiSize = timeSeriesData.getROIImages()[0].getISize();
		Extent roiExtent = timeSeriesData.getROIImages()[0].getMaskImage().getExtent();
		Origin roiOrigin = timeSeriesData.getROIImages()[0].getMaskImage().getOrigin();
		String[] vfrapROINames = new String[] {
				ROIImage.ROI_OLD_CELL,
				ROIImage.ROI_OLD_BLEACHED,
				ROIImage.ROI_OLD_BACKGROUND,
				ROIImage.ROI_OLD_BLEACHED_RING1,
				ROIImage.ROI_OLD_BLEACHED_RING2,
				ROIImage.ROI_OLD_BLEACHED_RING3,
				ROIImage.ROI_OLD_BLEACHED_RING4,
				ROIImage.ROI_OLD_BLEACHED_RING5,
				ROIImage.ROI_OLD_BLEACHED_RING6,
				ROIImage.ROI_OLD_BLEACHED_RING7,
				ROIImage.ROI_OLD_BLEACHED_RING8,
		};
		String[] roiNames = new String[] {
				ROIImage.ROI_CELL,
				ROIImage.ROI_BLEACHED,
				ROIImage.ROI_BACKGROUND,
				ROIImage.ROI_BLEACHED_RING1,
				ROIImage.ROI_BLEACHED_RING2,
				ROIImage.ROI_BLEACHED_RING3,
				ROIImage.ROI_BLEACHED_RING4,
				ROIImage.ROI_BLEACHED_RING5,
				ROIImage.ROI_BLEACHED_RING6,
				ROIImage.ROI_BLEACHED_RING7,
				ROIImage.ROI_BLEACHED_RING8,
		};
		ROIImage[] vfrapROIImages = new ROIImage[vfrapROINames.length];
		
		for (int i = 0; i < vfrapROIImages.length; i++) {
			vfrapROIImages[i] = new ROIImage(vfrapROINames[i],roiSize,roiExtent,roiOrigin);
			ROIImageComponent vfrapROI = vfrapROIImages[i].addROI((short)-1, vfrapROINames[i]);
			ROIImageComponent rOIImageComponent = timeSeriesData.getRoi(roiNames[i]);
			if (rOIImageComponent!=null){
				vfrapROI.setPixels(rOIImageComponent.getPixelsXYZ());
			}
		}
		AnnotatedImageDataset_inv newFrapData = new AnnotatedImageDataset_inv(timeSeriesData.getImageDataset(),vfrapROIImages);
		frapStudy.setFrapData(newFrapData);
		InverseProblemXmlproducer.writeXMLFile(frapStudy,outputFile,true,null,true);
		System.out.println("finished");
	}	

	public static void savePSF(final InversePDERequestManager inversePDERequestManager, InverseProblem inverseProblem) throws MathException, DataAccessException, IOException, ImageException{
		if (inverseProblem==null){
			throw new RuntimeException("inverse problem is null");
		}
		if (inverseProblem.getMicroscopyData().getPsfImageData()==null){
			throw new RuntimeException("PSF is null");
		}
		UShortImage psfImage = inverseProblem.getMicroscopyData().getPsfImageData().getImageDataset().getAllImages()[0];
		String psfFieldDataName = InverseProblemContants.PSF_FIELD_DATA_PREFIX;
		ExternalDataIdentifier fieldDataEDI = saveAsFieldData(inversePDERequestManager, psfImage, InverseProblemContants.FIELDDATA_VARNAME_PSF, psfFieldDataName);
		inverseProblem.getLinearResponseModel().setPsfFieldDataEDI(fieldDataEDI);
		System.out.println("finished");
	}

	public static void saveImageROI(final InversePDERequestManager inversePDERequestManager, InverseProblem inverseProblem) throws MathException, DataAccessException, IOException, ImageException{
		if (inverseProblem==null){
			throw new RuntimeException("inverse problem is null");
		}
		if (inverseProblem.getMicroscopyData().getTimeSeriesImageData()==null){
			throw new RuntimeException("PSF is null");
		}
		ROIImage roiImage = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_IMAGEROIS);
		String imageFieldDataName = InverseProblemContants.IMAGE_FIELD_DATA_PREFIX;
		ExternalDataIdentifier fieldDataEDI = saveAsFieldData(inversePDERequestManager, roiImage.getMaskImage(), InverseProblemContants.FIELDDATA_VARNAME_IMAGEROI, imageFieldDataName);
		inverseProblem.getLinearResponseModel().setImageROIFieldDataEDI(fieldDataEDI);
		System.out.println("finished");
	}
	
	
	public static SimpleReferenceData subsample(SimpleReferenceData refData, int maxNumRows) {
		if (refData==null){
			return refData;
		}
		int originalNumRows = refData.getNumDataRows();
		if (originalNumRows < maxNumRows) {
			return refData;
		}
		RowColumnResultSet rc = new RowColumnResultSet();
		String[] columnNames = refData.getColumnNames();
		for (int i = 0; i < columnNames.length; i++){
			rc.addDataColumn(new ODESolverResultSetColumnDescription(columnNames[i]));
		}
		for (int i = 0; i < refData.getNumDataRows(); i++){
			rc.addRow((double[])refData.getDataByRow(i).clone());
		}

		int desiredNumRows = refData.getNumDataRows() / 2;
		if (desiredNumRows<3){
			return refData;
		}
		try {
			rc.trimRows(desiredNumRows);
			double weights[] = null;
			if (refData.getColumnWeights()!=null){
				weights = (double[])refData.getColumnWeights().clone();
			}else{
				weights = new double[refData.getColumnNames().length];
				java.util.Arrays.fill(weights,1.0);
			}
			cbit.vcell.opt.SimpleReferenceData srd = new cbit.vcell.opt.SimpleReferenceData(rc,weights);
			int finalNumRows = srd.getNumDataRows();
			System.out.println("rows subsampled: orig="+originalNumRows+", final="+finalNumRows);
			return srd;
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		
	}

	public static MathModel saveMathModel(InversePDERequestManager inversePDERequestManager, MathModel mathModel, String mathModelNamePrefix) throws DataAccessException, PropertyVetoException {
		MathModelInfo[] mathModelInfos = inversePDERequestManager.getDocumentManager().getMathModelInfos();
		String mathModelName = mathModelNamePrefix;
		boolean nameFound = true;
		while (nameFound){
			nameFound = false;
			for (int i = 0; i < mathModelInfos.length; i++) {
				if (mathModelInfos[i].getVersion().getName().equals(mathModelName)){
					nameFound = true;
					break;
				}
			}
			if (nameFound){
				mathModelName = TokenMangler.getNextEnumeratedToken(mathModelName);
			}
		}
		mathModel.setName(mathModelName);
		MathModel savedMathModel = inversePDERequestManager.getDocumentManager().save(mathModel, null);
			
		return savedMathModel;
	}


	public static InverseProblem loadArtificialModel(InversePDERequestManager inversePDERequestManager, VCDocument vcDocument) throws DataAccessException, RemoteException, ImageException {
		InverseProblem inverseProblem = new InverseProblem();
		
		inverseProblem.setNonlinearModel(vcDocument);
		//
		// identify appropriate simulation
		//
		Simulation[] simulations = null;
		OutputContext outputContext = null;
		if (vcDocument instanceof BioModel){
			simulations = ((BioModel)vcDocument).getSimulations();
			OutputFunctionContext outputFunctionContext = ((BioModel)vcDocument).getSimulationContexts()[0].getOutputFunctionContext();
			outputContext = new OutputContext(outputFunctionContext.getOutputFunctionsList().toArray(new AnnotatedFunction[0]));
		}else if (vcDocument instanceof MathModel){
			simulations = ((MathModel)vcDocument).getSimulations();
			OutputFunctionContext outputFunctionContext = ((MathModel)vcDocument).getOutputFunctionContext();
			outputContext = new OutputContext(outputFunctionContext.getOutputFunctionsList().toArray(new AnnotatedFunction[0]));
		}else{
			throw new RuntimeException("unexpected model type : "+vcDocument);
		}
		if (simulations.length>1){
			throw new RuntimeException("more than one simulation found in imported BioModel, don't know which one to use");
		}
		Simulation sim = simulations[0];
		
		ExternalDataIdentifier exactSolutionEDI = new ExternalDataIdentifier(sim.getSimulationVersion().getVersionKey(),sim.getSimulationVersion().getOwner(),vcDocument.getName());
		inverseProblem.setExactSolutionEDI(exactSolutionEDI);
		
		SubVolume cellSubVolume = sim.getMathDescription().getGeometry().getGeometrySpec().getSubVolume(ROIImage.ROI_CELL);
		if (cellSubVolume==null){
			throw new RuntimeException("expecting geometry subVolume '"+ROIImage.ROI_CELL+"'");
			
		}
		//
		// getTimeSeriesData (must have __PSF__ trick)
		//
		SimulationSymbolTable simulationSymbolTable = new SimulationSymbolTable(sim,0);
		Function psfFunction = (Function)simulationSymbolTable.getVariable(InverseProblemContants.PSF_FUNCTION_NAME);
		if (psfFunction==null){
			throw new RuntimeException("can't find required variable named '"+InverseProblemContants.PSF_FUNCTION_NAME+"'");
		}
		Constant sampleZPlaneIndexConstant = (Constant)simulationSymbolTable.getVariable(InverseProblemContants.SAMPLE_Z_PLANE_INDEX);
		if (sampleZPlaneIndexConstant==null && sim.getMathDescription().getGeometry().getDimension()==3){
			throw new RuntimeException("can't find required variable named '"+InverseProblemContants.SAMPLE_Z_PLANE_INDEX+"'");
		}
		int sampleZPlane = 0; 
		if (sampleZPlaneIndexConstant!=null){
			try {
				sampleZPlane = (int)sampleZPlaneIndexConstant.getExpression().evaluateConstant();
			}catch (Exception ex){
				ex.printStackTrace(System.out);
				throw new RuntimeException(ex.getMessage());
			}
		}
		
		VCSimulationDataIdentifier vcdataID = new VCSimulationDataIdentifier(sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(),0);
		double[] times = inversePDERequestManager.getDataSetTimes(vcdataID);
		
		Origin geoOrigin = sim.getMathDescription().getGeometry().getOrigin();
		Extent geoExtent = sim.getMathDescription().getGeometry().getExtent();
		CartesianMesh mesh = inversePDERequestManager.getMesh(vcdataID);
		int numX = mesh.getSizeX();
		int numY = mesh.getSizeY();
		int numZ = mesh.getSizeZ();
		Origin origin = new Origin(geoOrigin.getX(),geoOrigin.getY(),geoOrigin.getZ()+geoExtent.getZ()*(sampleZPlane)/numZ);
		Extent extent = new Extent(geoExtent.getX(),geoExtent.getY(),geoExtent.getZ()/numZ);
		UShortImage[] images = new UShortImage[times.length];
		double timeSeriesMaxValue = -Double.MAX_VALUE;
		double timeSeriesMinValue = Double.MAX_VALUE;
		SimDataBlock firstSimDataBlock = inversePDERequestManager.getSimDataBlock(outputContext, vcdataID, InverseProblemContants.TOTAL_FLUORESCENCE_VAR_NAME, times[0]);
		double[] firstData = firstSimDataBlock.getData();
		for (int j = 0; j < firstData.length; j++) {
			int offset = sampleZPlane*numX*numY;
			timeSeriesMaxValue = Math.max(timeSeriesMaxValue,firstData[j+offset]);
			timeSeriesMinValue = Math.min(timeSeriesMinValue,firstData[j+offset]);
		}
		System.out.println("Importing fluorescence time series images, pixel values: min="+timeSeriesMinValue+", max="+timeSeriesMaxValue);
		for (int i = 0; i < images.length; i++) {
			SimDataBlock simDataBlock = inversePDERequestManager.getSimDataBlock(outputContext, vcdataID, InverseProblemContants.TOTAL_FLUORESCENCE_VAR_NAME, times[i]);
			double[] data = simDataBlock.getData();
			short[] pixels = new short[numX*numY];
			int offset = sampleZPlane*numX*numY;
			for (int j = 0; j < pixels.length; j++) {
				pixels[j] = (short)Math.max(0,data[j+offset]/timeSeriesMaxValue*32000);
			}
			images[i] = new UShortImage(pixels,origin,extent,numX,numY,1);
		}
		ImageDataset imageDataset = new ImageDataset(images,times,1);
		
		SimDataBlock laserSimDataBlock = inversePDERequestManager.getSimDataBlock(outputContext, vcdataID, InverseProblemContants.LASER_VAR_NAME, 0);
		double[] laserData = laserSimDataBlock.getData();
		short[] laserPixels = new short[numX*numY*numZ];
		for (int j = 0; j < laserPixels.length; j++) {
			laserPixels[j] = (short)laserData[j];
		}
		//
		// create time series ROIImages and set TimeSeriesImageData
		//
		ROIImage protocolROIImage = new ROIImage(ROIImage.ROIIMAGE_PROTOCOL,new ISize(numX,numY,1),extent,origin);
		if (numZ==1){
			// 2D artificial data (zStack and image timeseries are same
			ROIImageComponent zBleachedROI = protocolROIImage.addROI((short)1,ROIImage.ROI_BLEACHED);
			zBleachedROI.setPixels(laserPixels);
		}
		ROIImage timeSeriesSegmentationROIImage = new ROIImage(ROIImage.ROIIMAGE_SEGMENTATION,new ISize(numX,numY,1),extent,origin);
		ROIImage ringImageROIImage = new ROIImage(ROIImage.ROIIMAGE_IMAGEROIS,new ISize(numX,numY,1),extent,origin);
		ROIImage[] timeSeriesRoiImages = new ROIImage[] {timeSeriesSegmentationROIImage,protocolROIImage,ringImageROIImage };
		AnnotatedImageDataset_inv timeSeriesAnnotatedImageDataset = new AnnotatedImageDataset_inv(imageDataset,timeSeriesRoiImages);
		inverseProblem.getMicroscopyData().setTimeSeriesImageData(timeSeriesAnnotatedImageDataset);

		//
		// create zStack ROIImages (and populate segmentation from Geometry)
		//  then set zStack
		//
		ROIImage zStackSegmentationROIImage = new ROIImage(ROIImage.ROIIMAGE_SEGMENTATION,new ISize(numX,numY,numZ),geoExtent,geoOrigin);
		ROIImageComponent extracellularROI = zStackSegmentationROIImage.addROI((short)0, ROIImage.ROI_EXTRACELLULAR);
		ROIImageComponent cellROI = zStackSegmentationROIImage.addROI((short)1, ROIImage.ROI_CELL);
		for (int i = 0; i < zStackSegmentationROIImage.getMaskImage().getNumXYZ(); i++) {
			if (cellSubVolume.getHandle() == mesh.getSubVolumeFromVolumeIndex(i)){
				cellROI.setPixel(i);
			}
		}
		
		ROIImage zBasisROIImage = new ROIImage(ROIImage.ROIIMAGE_BASIS,new ISize(numX,numY,numZ),geoExtent,geoOrigin);
		ROIImage zProtocolROIImage = new ROIImage(ROIImage.ROIIMAGE_PROTOCOL,new ISize(numX,numY,numZ),geoExtent,geoOrigin);
		ROIImageComponent zBleachedROI = zProtocolROIImage.addROI((short)1,ROIImage.ROI_BLEACHED);
		zBleachedROI.setPixels(laserPixels);
		ROIImage[] zStackRoiImages = new ROIImage[] {zStackSegmentationROIImage,zProtocolROIImage,zBasisROIImage };
		AnnotatedImageDataset_inv zStackAnnotatedImageDataset = new AnnotatedImageDataset_inv(imageDataset,zStackRoiImages);
		inverseProblem.getMicroscopyData().setZStackImageData(zStackAnnotatedImageDataset);
		
		//
		// if the zstack and the time series images are same size, and if the segmentation for the 
		// time series is missing, then copy it from the zStack (the geometry).
		//
		if (timeSeriesSegmentationROIImage.getROIs().length==0){
			if (zStackSegmentationROIImage.getISize().compareEqual(timeSeriesSegmentationROIImage.getISize())){
				ROIImageComponent[] zStackROIs = zStackSegmentationROIImage.getROIs();
				for (int i = 0; i < zStackROIs.length; i++) {
					ROIImageComponent newROI = timeSeriesSegmentationROIImage.addROI(zStackROIs[i].getPixelValue(), zStackROIs[i].getName());
					newROI.setPixels(zStackROIs[i].getPixelsXYZ());
				}
			}
		}

		//
		// get PSF data from __PSF__ function
		//
		ExternalDataIdentifier psfFieldDataID = null;
		FunctionInvocation[] fieldFunctionInvocations = psfFunction.getExpression().getFunctionInvocations(new FieldUtilities.FieldFunctionFilter());
		if (fieldFunctionInvocations!=null && fieldFunctionInvocations.length==1){
			String fieldName = fieldFunctionInvocations[0].getArguments()[0].infix();
			String varName = fieldFunctionInvocations[0].getArguments()[1].infix();
			final double time;
			try {
				time = fieldFunctionInvocations[0].getArguments()[2].evaluateConstant();
			}catch (Exception e){
				e.printStackTrace(System.out);
				throw new RuntimeException(e.getMessage());
			}
			// assuming that time is 0 (zero).
			FieldDataDBOperationSpec fdDBopSpec = FieldDataDBOperationSpec.createGetExtDataIDsSpec(inversePDERequestManager.getUser());
			FieldDataDBOperationResults fdDbOpResults = inversePDERequestManager.getDocumentManager().fieldDataDBOperation(fdDBopSpec);
			ExternalDataIdentifier[] extDataIDs = fdDbOpResults.extDataIDArr;
			for (int i = 0; i < extDataIDs.length; i++) {
				if (extDataIDs[i].getName().equals(fieldName)){
					psfFieldDataID = extDataIDs[i];
					break;
				}
			}
			if (psfFieldDataID!=null){
				SimDataBlock psfSimDataBlock = inversePDERequestManager.getSimDataBlock(outputContext, psfFieldDataID, varName, time);
				double[] psfDoubleData = psfSimDataBlock.getData();
				// scale to maxValue (32K) and store as shorts
				short[] psfShortData = new short[psfDoubleData.length];
				double maxValue = -Double.MAX_VALUE;
				double minValue = Double.MAX_VALUE;
				for (int i = 0; i < psfShortData.length; i++) {
					minValue = Math.min(minValue,psfDoubleData[i]);
					maxValue = Math.max(maxValue,psfDoubleData[i]);
				}
				System.out.println("psf has values from ["+minValue+","+maxValue+"]");
//				for (int i = 0; i < psfShortData.length; i++) {
//					psfShortData[i] = (short)(Short.MAX_VALUE*psfDoubleData[i]/maxValue);
//				}
				for (int i = 0; i < psfShortData.length; i++) {
					int psfIntValue = (int)psfDoubleData[i];
					if (psfIntValue > 0xffff || psfIntValue < 0){
						throw new RuntimeException("psf pixel["+i+"] = "+psfDoubleData[i]+", maps to the integer ("+psfIntValue+") which is outside UShort range ["+0+","+(int)0xffff+"]");
					}
					psfShortData[i] = (short)psfDoubleData[i];
				}
				CartesianMesh psfMesh = inversePDERequestManager.getMesh(psfFieldDataID);
				UShortImage[] psfImages = new UShortImage[psfMesh.getSizeZ()];
				for (int i = 0; i < psfImages.length; i++) {
					Extent psfExtent = new Extent(psfMesh.getExtent().getX(),psfMesh.getExtent().getY(),psfMesh.getExtent().getZ()/psfImages.length);
					Origin psfOrigin = new Origin(psfMesh.getOrigin().getX(),psfMesh.getOrigin().getY(),psfMesh.getOrigin().getZ()*(1+(i/psfImages.length)));
					short[] psfSingleImageData = new short[psfShortData.length/psfImages.length];
					System.arraycopy(psfShortData, i*psfSingleImageData.length, psfSingleImageData, 0, psfSingleImageData.length);
					psfImages[i] = new UShortImage(psfSingleImageData,psfOrigin,psfExtent,psfMesh.getSizeX(),psfMesh.getSizeY(),1);
				}
				ImageDataset psfImageDataset = new ImageDataset(psfImages, new double[] { 0.0 }, psfImages.length);
				AnnotatedImageDataset_inv psfAnnotImageDataset = new AnnotatedImageDataset_inv(psfImageDataset,new ROIImage[0]);
				inverseProblem.getMicroscopyData().setPsfImageData(psfAnnotImageDataset);
				inverseProblem.getLinearResponseModel().setPsfFieldDataEDI(psfFieldDataID);
			}
		}else{
			throw new RuntimeException(psfFunction.getName()+" doesn't reference a fieldData");
		}
		return inverseProblem;
	}

	public static double[] correctTimes(double[] origTimes, double scale, double deltaT){
		//
		// correct times array for time dependent diffusion rate.
		//
		double[] correctedTimes = new double[origTimes.length];
		for (int j = 0; j < origTimes.length; j++) {
			correctedTimes[j] = scale/2.0*(origTimes[j]*origTimes[j]+deltaT*origTimes[j]);
		}
		return correctedTimes;
	}

	public static AnnotatedImageDataset_inv getTimeSeriesFromFrapStudy(FRAPStudy frapStudy) throws ImageException {
		if (frapStudy==null || frapStudy.getFrapData()==null){
			return null;
		}
		
		ImageDataset imageDataSet = new ImageDataset(frapStudy.getFrapData().getImageDataset());
		
		//
		// initialize ROI structure for TimeSeries data.
		//
		ROIImage segmentationROIImage = new ROIImage(ROIImage.ROIIMAGE_SEGMENTATION,imageDataSet.getISize(),imageDataSet.getExtent(),new Origin(0,0,0));
		segmentationROIImage.addROI((short)0,ROIImage.ROI_EXTRACELLULAR);
		segmentationROIImage.addROI((short)1,ROIImage.ROI_CELL);
		
		ROIImage dataROIImage = new ROIImage(ROIImage.ROIIMAGE_PROTOCOL,imageDataSet.getISize(),imageDataSet.getExtent(),new Origin(0,0,0));
		dataROIImage.addROI((short)1,ROIImage.ROI_BLEACHED);
		dataROIImage.addROI((short)2,ROIImage.ROI_BACKGROUND);
		
		ROIImage ringImageROIImage = new ROIImage(ROIImage.ROIIMAGE_IMAGEROIS,imageDataSet.getISize(),imageDataSet.getExtent(),new Origin(0,0,0));
		ringImageROIImage.addROI((short)1,ROIImage.ROI_BLEACHED_RING1);
		ringImageROIImage.addROI((short)2,ROIImage.ROI_BLEACHED_RING2);
		ringImageROIImage.addROI((short)3,ROIImage.ROI_BLEACHED_RING3);
		ringImageROIImage.addROI((short)4,ROIImage.ROI_BLEACHED_RING4);
		ringImageROIImage.addROI((short)5,ROIImage.ROI_BLEACHED_RING5);
		ringImageROIImage.addROI((short)6,ROIImage.ROI_BLEACHED_RING6);
		ringImageROIImage.addROI((short)7,ROIImage.ROI_BLEACHED_RING7);
		ringImageROIImage.addROI((short)8,ROIImage.ROI_BLEACHED_RING8);
		
		AnnotatedImageDataset_inv timeSeriesImageDataset = new AnnotatedImageDataset_inv(imageDataSet,new ROIImage[] { segmentationROIImage,dataROIImage,ringImageROIImage });
		
		//
		// copy ROIs from FrapData into new ROI structure.
		//
		Hashtable<String,ROIImageComponent> roiHash = new Hashtable<String, ROIImageComponent>();
		roiHash.put(ROIImage.ROI_OLD_BACKGROUND, timeSeriesImageDataset.getRoi(ROIImage.ROI_BACKGROUND));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED_RING1, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED_RING1));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED_RING2, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED_RING2));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED_RING3, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED_RING3));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED_RING4, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED_RING4));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED_RING5, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED_RING5));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED_RING6, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED_RING6));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED_RING7, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED_RING7));
		roiHash.put(ROIImage.ROI_OLD_BLEACHED_RING8, timeSeriesImageDataset.getRoi(ROIImage.ROI_BLEACHED_RING8));
		roiHash.put(ROIImage.ROI_OLD_CELL, timeSeriesImageDataset.getRoi(ROIImage.ROI_CELL));
		ROIImageComponent[] oldRois = frapStudy.getFrapData().getRois();
		for (int i = 0; i < oldRois.length; i++) {
			ROIImageComponent newROI = roiHash.get(oldRois[i].getName());
			if (newROI == null){
				System.out.println("ROI from frapStudy "+oldRois[i].getName()+" ignored");
			}else{
				newROI.setPixels(oldRois[i].getPixelsXYZ());
			}
		}
		
		return timeSeriesImageDataset;
	}

	public static AnnotatedImageDataset_inv getZStackFromFrapStudy(FRAPStudy frapStudy) throws ImageException {
		if (frapStudy==null || frapStudy.getFrapData()==null){
			return null;
		}
		
		ImageDataset imageDataSet = new ImageDataset(frapStudy.getFrapData().getImageDataset());
		
		//
		// initialize ROI structure for TimeSeries data.
		//
		ROIImage segmentationROIImage = new ROIImage(ROIImage.ROIIMAGE_SEGMENTATION,imageDataSet.getISize(),imageDataSet.getExtent(),imageDataSet.getAllImages()[0].getOrigin());
		segmentationROIImage.addROI((short)0,ROIImage.ROI_EXTRACELLULAR);
		segmentationROIImage.addROI((short)1,ROIImage.ROI_CELL);
		
		ROIImage basisROIImage = new ROIImage(ROIImage.ROIIMAGE_BASIS,imageDataSet.getISize(),imageDataSet.getExtent(),imageDataSet.getAllImages()[0].getOrigin());
	
		AnnotatedImageDataset_inv cellStack = new AnnotatedImageDataset_inv(imageDataSet,new ROIImage[] { segmentationROIImage,basisROIImage });
	
		//
		// copy ROIs from FrapData into new ROI structure.
		//
		Hashtable<String,ROIImageComponent> roiHash = new Hashtable<String, ROIImageComponent>();
		roiHash.put(ROIImage.ROI_OLD_CELL, cellStack.getRoi(ROIImage.ROI_CELL));
	
		ROIImageComponent[] oldRois = frapStudy.getFrapData().getRois();
		for (int i = 0; i < oldRois.length; i++) {
			ROIImageComponent newROI = roiHash.get(oldRois[i].getName());
			if (newROI == null){
				System.out.println("ROI from frapStudy "+oldRois[i].getName()+" ignored");
			}else{
				newROI.setPixels(oldRois[i].getPixelsXYZ());
			}
		}
		
		return cellStack;
	}

	/**
	 * This method assumes that a PSF dataset has been loaded into a FRAPStudy.
	 * @param frapStudy
	 * @param inverseProblem
	 * @throws ImageException
	 */
	public static void loadPSFFromFrapStudy(FRAPStudy frapStudy, InverseProblem inverseProblem) throws ImageException {
		if (frapStudy==null || frapStudy.getFrapData()==null){
			throw new RuntimeException("frapStudy or FRAPData is null");
		}
		if (inverseProblem==null || inverseProblem.getMicroscopyData().getZStackImageData()==null){
			throw new RuntimeException("inverseProblem or ZStackImageData is null");
		}
		
		ImageDataset imageDataSet = null;
		if (frapStudy.getFrapData().getDimension() == inverseProblem.getMicroscopyData().getZStackImageData().getDimension()){
			imageDataSet = new ImageDataset(frapStudy.getFrapData().getImageDataset());
			inverseProblem.getMicroscopyData().setPsfImageData(new AnnotatedImageDataset_inv(imageDataSet,new ROIImage[0]));
		}else if (frapStudy.getFrapData().getDimension()==3 && inverseProblem.getMicroscopyData().getZStackImageData().getDimension()==2){
			//
			// project PSF onto single plane
			//
			if (frapStudy.getFrapData().getImageDataset().getSizeC()>1){
				throw new RuntimeException("unexpected, more than one channel in PSF");
			}
			if (frapStudy.getFrapData().getImageDataset().getSizeT()>1){
				throw new RuntimeException("unexpected, more than one time in PSF");
			}
			UShortImage[] imageStack = frapStudy.getFrapData().getImageDataset().getAllImages();
			int numX = imageStack[0].getNumX();
			int numY = imageStack[0].getNumY();
			double max = 0;
			double[] doubleImage = new double[numX*numY]; 
			for (int i = 0; i < imageStack.length; i++) {
				short[] pixels = imageStack[i].getPixels();
				for (int j = 0; j < pixels.length; j++) {
					doubleImage[j] += ((int)pixels[j])&0x00ff;
					max = Math.max(max,doubleImage[j]);
				}
			}
			short[] newPixels = new short[doubleImage.length];
			for (int i = 0; i < newPixels.length; i++) {
				newPixels[i] = (short)(Short.MAX_VALUE*doubleImage[i]/max);   // scale so that max is 15 bits (32767)
			}
			UShortImage new2Dpsf = new UShortImage(newPixels,imageStack[imageStack.length/2].getOrigin(),imageStack[0].getExtent(),numX,numY,1);
			ImageDataset psfImageDataset = new ImageDataset(new UShortImage[] { new2Dpsf }, new double[] { 0.0 }, 1);
			inverseProblem.getMicroscopyData().setPsfImageData(new AnnotatedImageDataset_inv(psfImageDataset,new ROIImage[0]));
		}else{
			throw new RuntimeException("incompatible dimensions, psf dimension="+frapStudy.getFrapData().getDimension()+", zStack dimension="+inverseProblem.getMicroscopyData().getZStackImageData().getDimension());
		}
		System.out.println("hello");
	}
	
	public static void readRefData(InversePDERequestManager inversePDERequestManager, InverseProblem inverseProblem, String matlabFileName) throws ImageException, DataAccessException, RemoteException, IOException {
//		//
//		//Get grid spatial index "center point" for each sample group (region index collection) 
//		//
//		LinearResponseModel linearResponseModel = inverseProblem.getLinearResponseModel();
//		if (linearResponseModel.getBasisFunctions().getVolumeBases()==null){
//			throw new RuntimeException("volume bases not found");
//		}
//	
//		//
//		// get 2d ROI image to average Convolved data (at the focal plane).
//		//
//		UShortImage roiMaskImage2D = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_IMAGEROIS).getMaskImage();
//		int[] roiMaskImage2DPixels = new int[roiMaskImage2D.getNumXYZ()];
//		for (int i = 0; i < roiMaskImage2DPixels.length; i++) {
//			roiMaskImage2DPixels[i] = roiMaskImage2D.getPixels()[i];
//		}
//		VCImageUncompressed vcImage = new VCImageUncompressed(null,roiMaskImage2DPixels,new Extent(1,1,1),roiMaskImage2D.getNumX(),roiMaskImage2D.getNumY(),1);
//		int[] uniquePixelValues = vcImage.getUniquePixelValues();
//		Arrays.sort(uniquePixelValues);
//		
//		if (linearResponseModel.getRefVolumeSimMathModel()==null){
//			throw new RuntimeException("refMathModel not found");
//		}
//		if (linearResponseModel.getRefVolumeSimMathModel().getSimulations()==null || linearResponseModel.getRefVolumeSimMathModel().getSimulations()[0].getVersion()==null){
//			throw new RuntimeException("refMathModel has no saved simulations");
//		}
//	
//		Simulation volumeSimulation = linearResponseModel.getRefVolumeSimMathModel().getSimulations(0);
//		SimulationSymbolTable simulationSymbolTable = new SimulationSymbolTable(volumeSimulation,0);
//		String refSimsVarName = InverseProblemContants.VOL_VAR_NAME;
//		String refSimsConvolvedVarName = InverseProblemContants.VOL_VAR_NAME+"_Convolved";
//	
//		double scale;
//		try {
//			scale = ((Constant)simulationSymbolTable.getVariable("S")).getConstantValue();
//		} catch (ExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("failed to read variable 'scale' from simulation");
//		}
//		double deltaT;
//		try {
//			deltaT = ((Constant)simulationSymbolTable.getVariable(InverseProblemContants.DELTA_T_NAME)).getConstantValue();
//		} catch (ExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("failed to read variable 'deltaT' from simulation");
//		}		
//		System.out.println("done reading ... writing out to matlab");
//		
//		int scanCount = volumeSimulation.getScanCount();
//
//		ArrayList<MLArray> list = new ArrayList<MLArray>();
//
//		MLCell refSolutionCells = new MLCell("basisResponses",new int[] { 1, scanCount });
//		MLCell refImageCells = new MLCell("basisResponses2DConv",new int[] { 1, scanCount });
//		MLCell refTimesCells = new MLCell("basisTimes",new int[] { 1, scanCount });
//		
//		ImageDataset timeSeriesImageDataset = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getImageDataset();
//		double[] experimentalTimes = timeSeriesImageDataset.getImageTimeStamps();
//
//		MLDouble expTimes = new MLDouble("expTimes",new double[][] {experimentalTimes});
//		list.add(expTimes);
//
//		VCDocument nonlinearModel = inverseProblem.getNonlinearModel();
//		if (nonlinearModel==null){
//			throw new RuntimeException("nonlinear model is missing");
//		}
//		if (inverseProblem.getExactSolutionEDI()!=null){
//			DataProcessingOutput dpo = inversePDERequestManager.getDataProcessingOutput(inverseProblem.getExactSolutionEDI());
//			if (dpo != null){
//				MathDescription mathDescription = null;
//				if (nonlinearModel instanceof BioModel){
//					mathDescription = ((BioModel)nonlinearModel).getSimulations()[0].getMathDescription();
//				}else if (nonlinearModel instanceof MathModel){
//					mathDescription = ((MathModel)nonlinearModel).getSimulations()[0].getMathDescription();
//				}
//				ArrayList<VolVariable> volVarList = new ArrayList<VolVariable>();
//				Enumeration<Variable> enumVar = mathDescription.getVariables();
//				while (enumVar.hasMoreElements()) {
//					Variable variable = (Variable) enumVar.nextElement();
//					if (variable instanceof VolVariable){
//						volVarList.add((VolVariable)variable);
//					}
//				}
//				double[] exactTimes = inversePDERequestManager.getDataSetTimes(inverseProblem.getExactSolutionEDI());
//				MLDouble exactTimesVector = new MLDouble("exactTimes",new double[][] { exactTimes });
//				
//				MLCell exactSolutionCells = new MLCell("exactSolution",new int[] { 1, volVarList.size() });
//				byte[] nc_content = dpo.toBytes();
//				try {				
//					NetcdfFile ncfile = NetcdfFile.openInMemory("temp.inmemory", nc_content);
//					
//					for (int varIndex = 0; varIndex < volVarList.size(); varIndex++) {
//						String varName = volVarList.get(varIndex).getName();
//						ucar.nc2.Variable volVar = ncfile.findVariable(varName);
//						int[] shape = volVar.getShape();
//						int[] origin = new int[2];
//						int[] size = new int[] {shape[0], shape[1]};
//						double[][] result_volVar = new double[shape[0]][shape[1]];
//						
//						ArrayDouble.D2 data = null;
//						try{
//							data = (ArrayDouble.D2)volVar.read(origin, size);
//						}catch(Exception e){
//							e.printStackTrace(System.err);
//							throw new IOException("Can not read volVar data.");
//						}
//						for(int i=0;i<shape[0];i++)
//						{
//							for(int j=0;j<shape[1];j++)
//							{
//								result_volVar[i][j]= data.get(i,j);
//							}
//						}
//						MLDouble exactSolution = new MLDouble(varName,result_volVar);
//						exactSolutionCells.set(exactSolution, 0, varIndex);
//					}
//					
//				} catch (IOException e1) {			
//					e1.printStackTrace();
//				}
//				
//				list.add(exactTimesVector);
//				list.add(exactSolutionCells);
//			}
//		}				
//		ROIImageComponent[] imageROIs = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_IMAGEROIS).getROIs();
//		double[][] expData = new double[experimentalTimes.length][uniquePixelValues.length];
//		UShortImage[] timeSeriesImages = timeSeriesImageDataset.getAllImages();
//		for (int i = 0; i < timeSeriesImages.length; i++) {
//			short[] imageData = timeSeriesImages[i].getPixels();
//			for (int j = 0; j < imageROIs.length; j++) {
//				int[] indices = imageROIs[j].getIndices();
//				double sum = 0;
//				for (int k = 0; k < indices.length; k++) {
//					sum += ((int)imageData[indices[k]])&0xffff;
//				}
//				if (indices.length>0){
//					expData[i][j] = sum/indices.length;
//				}
//			}
//		}
//		MLDouble expDataMatrix = new MLDouble("expData", expData );
//		list.add(expDataMatrix);
//		
//		for (int scanIndex = 0; scanIndex < scanCount; scanIndex++) {
//			VCSimulationDataIdentifier vcdataID = new VCSimulationDataIdentifier(volumeSimulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(),scanIndex);
//			System.out.println("reading data for scan job "+scanIndex);
//			DataProcessingOutput dpo = inversePDERequestManager.getDataProcessingOutput(vcdataID);
//			byte[] nc_content = dpo.toBytes();
//			try {				
//				NetcdfFile ncfile = NetcdfFile.openInMemory("temp.inmemory", nc_content);
//				
//				// time
//				{
//					ucar.nc2.Variable time = ncfile.findVariable("t");
//					int[] shape = time.getShape();
//					double[] timePoints = new double[shape[0]];
//					ArrayDouble.D1 time_data = null;
//					try{
//						time_data = (ArrayDouble.D1)time.read();
//					}catch(IOException ioe){
//						ioe.printStackTrace(System.err);
//						throw new IOException("Can not read time points from the model.");
//					}
//					for(int i=0; i<shape[0]; i++)
//					{
//						timePoints[i] = time_data.get(i);
//					}
//					double[] correctedTimes = InverseProblemUtilities.correctTimes(timePoints, scale, deltaT);
//					MLDouble refTimes = new MLDouble("t_"+scanIndex,new double[][] {correctedTimes});
//					refTimesCells.set(refTimes, 0, scanIndex);
//				}
//				
//				{
//					ucar.nc2.Variable volVar = ncfile.findVariable(refSimsVarName);
//					int[] shape = volVar.getShape();
//					int[] origin = new int[2];
//					int[] size = new int[] {shape[0], shape[1]};
//					double[][] result_volVar = new double[shape[0]][shape[1]];
//					
//					ArrayDouble.D2 data = null;
//					try{
//						data = (ArrayDouble.D2)volVar.read(origin, size);
//					}catch(Exception e){
//						e.printStackTrace(System.err);
//						throw new IOException("Can not read volVar data.");
//					}
//					for(int i=0;i<shape[0];i++)
//					{
//						for(int j=0;j<shape[1];j++)
//						{
//							result_volVar[i][j]= data.get(i,j);
//						}
//					}
//					MLDouble refSolution = new MLDouble(refSimsVarName+"_"+scanIndex,result_volVar);
//					refSolutionCells.set(refSolution, 0, scanIndex);
//				}
//				
//				{
//					ucar.nc2.Variable volVar_Convolved = ncfile.findVariable(refSimsConvolvedVarName);
//					int[] shape = volVar_Convolved.getShape();
//					int[] origin = new int[2];
//					int[] size = new int[] {shape[0], shape[1]};
//					double[][] result_Convolved = new double[shape[0]][shape[1]];
//					
//					ArrayDouble.D2 data_Convolved = null;
//					try{
//						data_Convolved = (ArrayDouble.D2)volVar_Convolved.read(origin, size);
//					}catch(Exception e){
//						e.printStackTrace(System.err);
//						throw new IOException("Can not read convolved data.");
//					}
//					for(int i=0;i<shape[0];i++)
//					{
//						for(int j=0;j<shape[1];j++)
//						{
//							result_Convolved[i][j]= data_Convolved.get(i,j);
//						}
//					}
//					MLDouble refImage = new MLDouble(refSimsVarName+"_"+scanIndex,result_Convolved);
//					refImageCells.set(refImage, 0, scanIndex);
//				}
//				
//			} catch (IOException e1) {			
//				e1.printStackTrace();
//			}			
//		}
//		list.add(refTimesCells);
//		list.add(refSolutionCells);
//		list.add(refImageCells);
//		
//		//
//		// add experimental data for image
//		//
//		// add exact solution at control points
//		//
//		//
//		System.out.println("writing matlab file to "+matlabFileName);
//		MatFileWriter writer = new MatFileWriter();
//		writer.write(matlabFileName, list );
//		
//		inverseProblem.setMatlabDataFileName(matlabFileName);
	}
}