/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.scratch;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import javax.media.jai.KernelJAI;
import javax.media.jai.operator.AndDescriptor;

import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.gui.DialogUtils;
import org.vcell.vmicro.workflow.data.LocalWorkspace;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMesh;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLDouble;

/**
 * Insert the type's description here.
 * Creation date: (1/24/2007 4:18:01 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */

public class FRAPData extends AnnotatedImageDataset implements Matchable, VFrap_ROISourceData{

	public static enum VFRAP_ROI_ENUM {
	ROI_BLEACHED,
	ROI_BACKGROUND,
	ROI_CELL,
	ROI_BLEACHED_RING1,
	ROI_BLEACHED_RING2,
	ROI_BLEACHED_RING3,
	ROI_BLEACHED_RING4,
	ROI_BLEACHED_RING5,
	ROI_BLEACHED_RING6,
	ROI_BLEACHED_RING7,
	ROI_BLEACHED_RING8
//	ROI_NUCLEUS,
//	ROI_EXTRACELLULAR
}

/**
 * FRAPData constructor comment.
 * @param argImageDataset ImageDataset
 * @param argROIs ROI[]
 */
	public FRAPData(ImageDataset argImageDataset, ROI[] argROIs) {
		super(argImageDataset, argROIs);
	}

	/**
	 * Constructor for FRAPData.
	 * @param argImageDataset ImageDataset
	 * @param argROITypes ROI.RoiType[]
	 */
	public FRAPData(ImageDataset argImageDataset, String[] argROINames) {
		super(argImageDataset, argROINames);
	}

/**
 * Method crop.
 * @param rect Rectangle
 * @return FRAPData
 * @throws ImageException
 */
@Override
public FRAPData crop(Rectangle rect) throws ImageException {
	ImageDataset croppedImageDataset = getImageDataset().crop(rect);
	ROI[] rois = getRois();
	ROI[] croppedROIs = new ROI[rois.length];
	ROI currentlyDisplayedROI = getCurrentlyDisplayedROI();
	ROI croppedCurrentROI = null;
	for (int i = 0; i < croppedROIs.length; i++) {
		croppedROIs[i] = rois[i].crop(rect);
		if (currentlyDisplayedROI == rois[i]){
			croppedCurrentROI = croppedROIs[i];
		}
	}
	FRAPData croppedFrapData = new FRAPData(croppedImageDataset,croppedROIs);
	setCurrentlyDisplayedROI(croppedCurrentROI);
	return croppedFrapData;
}

public double[] getAvgBackGroundIntensity()
{
	return FRAPDataAnalysis.getAverageROIIntensity(this,this.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()),null,null);
}

public boolean compareEqual(Matchable obj) 
{
	if (this == obj) {
		return true;
	}
	if (obj != null && obj instanceof FRAPData) 
	{
		FRAPData fData = (FRAPData) obj;
		if (!Compare.isEqualOrNull(getImageDataset(), fData.getImageDataset())){
			return false;
		}
		if (!Compare.isEqualOrNull(getRois(), fData.getRois())){
			return false;
		}
		return true;
	}
	return false;
}

public void chopImages(int startTimeIndex, int endTimeIndex) 
{
	UShortImage[] origImages = getImageDataset().getAllImages();
	double[] origTimeSteps = getImageDataset().getImageTimeStamps();
	int numOfZ = getImageDataset().getSizeZ();
	int numOfC = getImageDataset().getSizeC();
	int tolImgLen = (endTimeIndex - startTimeIndex + 1)*numOfZ*numOfC;
	UShortImage[] newImages = new UShortImage[tolImgLen];
	double[] newTimeSteps = new double[endTimeIndex - startTimeIndex + 1];
	
	System.arraycopy(origImages, startTimeIndex*numOfZ*numOfC, newImages, 0, tolImgLen);
	System.arraycopy(origTimeSteps, startTimeIndex, newTimeSteps, 0, (endTimeIndex - startTimeIndex + 1));
	//shift time to start from 0, it's not necessary 
//	double firstTimePoint = newTimeSteps[0];
//	for(int i = 0; i < newTimeSteps.length; i++)
//	{
//		newTimeSteps[i] = newTimeSteps[i] - firstTimePoint;
//	}
	
	
	ImageDataset imgDataset = new ImageDataset(newImages, newTimeSteps, numOfZ);
	setImageDataset(imgDataset);
}

public void changeImageExtent(double imageSizeX, double imageSizeY) {
	double imageSizeZ = getImageDataset().getExtent().getZ();
	getImageDataset().setExtent(new Extent(imageSizeX, imageSizeY, imageSizeZ));
}

private static UShortImage fastDilate(UShortImage dilateSource,int radius,UShortImage mask) throws ImageException{
	short[] sourcePixels = dilateSource.getPixels();
	short[] targetPixels = dilateSource.getPixels().clone();
	KernelJAI dilateKernel = UShortImage.createCircularBinaryKernel(radius);
	float[] kernelData = dilateKernel.getKernelData();
	BitSet kernelBitSet = new BitSet(kernelData.length);
	for (int i = 0; i < kernelData.length; i++) {
		if(kernelData[i] == 1.0f){
			kernelBitSet.set(i);
		}
	}
	boolean bNeedsFill = false;
	for (int y = 0; y < dilateSource.getNumY(); y++) {
		int yOffset = y*dilateSource.getNumX();
		int yMinus = yOffset-dilateSource.getNumX();
		int yPlus = yOffset+dilateSource.getNumX();
		for (int x = 0; x < dilateSource.getNumX(); x++) {
			bNeedsFill = false;
			if(sourcePixels[x+yOffset] != 0){
				if(x-1 >= 0 && sourcePixels[(x-1)+yOffset] == 0){
					bNeedsFill = true;
				}else
				if(y-1 >= 0 && sourcePixels[x+yMinus] == 0){
					bNeedsFill = true;
				}else
				if(x+1 < dilateSource.getNumX() && sourcePixels[(x+1)+yOffset] == 0){
					bNeedsFill = true;
				}else
				if(y+1 < dilateSource.getNumY() && sourcePixels[x+yPlus] == 0){
					bNeedsFill = true;
				}
				if(bNeedsFill){
					int masterKernelIndex = 0;
					for (int y2 = y-radius; y2 <= y+radius; y2++) {
						if(y2>= 0 && y2 <dilateSource.getNumY()){
							int kernelIndex = masterKernelIndex;
							int targetYIndex = y2*dilateSource.getNumX();
							for (int x2 = x-radius; x2 <= x+radius; x2++) {
								if(kernelBitSet.get(kernelIndex) &&
									x2>= 0 && x2 <dilateSource.getNumX()){
									targetPixels[targetYIndex+x2] = 1;
								}
								kernelIndex++;
							}
						}
						masterKernelIndex+= dilateKernel.getWidth();
					}
				}
			}
		}
	}
	UShortImage resultImage =
		new UShortImage(targetPixels,
				dilateSource.getOrigin(),
				dilateSource.getExtent(),
				dilateSource.getNumX(),
				dilateSource.getNumY(),
				dilateSource.getNumZ());
	resultImage.and(mask);
	return resultImage;
}

public boolean checkROIConstraints(Component componentToDisplayMsg) throws Exception{
	short[] cellPixels = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getPixelsXYZ();
	short[] bleachPixels = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getPixelsXYZ();
	short[] backgroundPixels = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()).getPixelsXYZ();
	boolean bFixedBleach = false;
	boolean bFixedBackground = false;
	for (int i = 0; i < cellPixels.length; i++) {
		if(cellPixels[i] == 0 && bleachPixels[i] != 0){
			bFixedBleach = true;
			bleachPixels[i] = 0;
		}
		if(cellPixels[i] != 0 && backgroundPixels[i] != 0){
			bFixedBackground = true;
			backgroundPixels[i] = 0;
		}
	}
	if(bFixedBackground || bFixedBleach){
		final String FIX_AUTO = "Fix Automatically";
		final String NO_THANKS = "No, Thanks";
		String result = DialogUtils.showWarningDialog(componentToDisplayMsg,
				(bFixedBleach?"Bleach ROI extends beyond Cell ROI":"")+
				(bFixedBackground &&bFixedBleach?" and" :"")+
				(bFixedBackground?"Background ROI overlaps Cell ROI":"")+
				".  Ensure that the Bleach ROI is completely inside the Cell ROI and the Background ROI is completely outside the Cell ROI.\nDo you want Virtual Frap to fix it automatically?",
				new String[] {FIX_AUTO,NO_THANKS}, FIX_AUTO);
		if(result != null && result.equals(FIX_AUTO)){
			if(bFixedBleach){
				UShortImage ushortImage =
					new UShortImage(bleachPixels,
						getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
						getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
						getCurrentlyDisplayedROI().getISize().getX(),
						getCurrentlyDisplayedROI().getISize().getY(),
						getCurrentlyDisplayedROI().getISize().getZ());
				ROI newBleachROI = new ROI(ushortImage,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
				addReplaceRoi(newBleachROI);
			}
			if(bFixedBackground){
				UShortImage ushortImage =
					new UShortImage(backgroundPixels,
						getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
						getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
						getCurrentlyDisplayedROI().getISize().getX(),
						getCurrentlyDisplayedROI().getISize().getY(),
						getCurrentlyDisplayedROI().getISize().getZ());
				ROI newBackgroundROI = new ROI(ushortImage,FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
				addReplaceRoi(newBackgroundROI);
			}
			return true;
		}
	}
	else //nothing to fix
	{
		return true;
	}
	return false;
}

public void refreshDependentROIs()
{
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
		cellROI_2D =
			UShortImage.convertToUShortImage(UShortImage.binarize(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getRoiImages()[0]),
				getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getRoiImages()[0].getOrigin(),
				getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getRoiImages()[0].getExtent());
		bleachedROI_2D =
			UShortImage.convertToUShortImage(
					AndDescriptor.create(UShortImage.binarize(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getRoiImages()[0]),
						UShortImage.binarize(cellROI_2D), null).createInstance(),
					getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getRoiImages()[0].getOrigin(),
					getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getRoiImages()[0].getExtent());
//		writeUShortFile(cellROI_2D, new File("D:\\developer\\eclipse\\workspace\\cellROI_2D.bmp"));
//		writeUShortFile(bleachedROI_2D, new File("D:\\developer\\eclipse\\workspace\\bleachedROI_2D.bmp"));

		dilatedROI_2D_1 = UShortImage.fastDilate(bleachedROI_2D, 4, cellROI_2D);
//			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(8), cellROI_2D,false);

		dilatedROI_2D_2 = 
			fastDilate(bleachedROI_2D, 10, cellROI_2D);
//			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(16), cellROI_2D,false);

    	dilatedROI_2D_3 = 
    		fastDilate(bleachedROI_2D, 18, cellROI_2D);
//			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(24), cellROI_2D,false);

    	dilatedROI_2D_4 = 
    		fastDilate(bleachedROI_2D, 28, cellROI_2D);
//			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(32), cellROI_2D,false);

    	dilatedROI_2D_5 = 
    		fastDilate(bleachedROI_2D, 40, cellROI_2D);
//			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(40), cellROI_2D,false);
		
		erodedROI_2D_0 = new UShortImage(bleachedROI_2D);
		
		// The erode always causes problems if eroding without checking the bleached length and hight.
		// we have to check the min length of the bleahed area to make sure erode within the length.
		Rectangle bleachRect = bleachedROI_2D.getNonzeroBoundingBox();
		int minLen = Math.min(bleachRect.height, bleachRect.width);
		if((minLen/2.0) < 5)
		{
			erodedROI_2D_1 = UShortImage.erodeDilate(bleachedROI_2D, UShortImage.createCircularBinaryKernel(1), bleachedROI_2D,true);
			erodedROI_2D_2 = UShortImage.erodeDilate(bleachedROI_2D, UShortImage.createCircularBinaryKernel(2), bleachedROI_2D,true);
		}
		else
		{
			erodedROI_2D_1 = UShortImage.erodeDilate(bleachedROI_2D, UShortImage.createCircularBinaryKernel(2), bleachedROI_2D,true);
			erodedROI_2D_2 = UShortImage.erodeDilate(bleachedROI_2D, UShortImage.createCircularBinaryKernel(5), bleachedROI_2D,true);
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
//		writeUShortFile(dilatedROI_2D_3, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_3.bmp"));
		reverseDilateROI_2D_3.reverse();
//		writeUShortFile(reverseDilateROI_2D_3, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_3_reverse.bmp"));
//		writeUShortFile(dilatedROI_2D_4, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_4.bmp"));
		dilatedROI_2D_4.and(reverseDilateROI_2D_3);
//		writeUShortFile(dilatedROI_2D_4, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_4_anded.bmp"));

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
	ROI roiBleachedRing1_2D = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name());
	if (roiBleachedRing1_2D==null){
		roiBleachedRing1_2D = new ROI(erodedROI_2D_2,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name());
		addReplaceRoi(roiBleachedRing1_2D);
	}else{
		System.arraycopy(erodedROI_2D_2.getPixels(), 0, roiBleachedRing1_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_2.getPixels().length);
	}
	ROI roiBleachedRing2_2D = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name());
	if (roiBleachedRing2_2D==null){
		roiBleachedRing2_2D = new ROI(erodedROI_2D_1,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name());
		addReplaceRoi(roiBleachedRing2_2D);
	}else{
		System.arraycopy(erodedROI_2D_1.getPixels(), 0, roiBleachedRing2_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_1.getPixels().length);
	}
	ROI roiBleachedRing3_2D = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name());
	if (roiBleachedRing3_2D==null){
		roiBleachedRing3_2D = new ROI(erodedROI_2D_0,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name());
		addReplaceRoi(roiBleachedRing3_2D);
	}else{
		System.arraycopy(erodedROI_2D_0.getPixels(), 0, roiBleachedRing3_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_0.getPixels().length);
	}
	ROI roiBleachedRing4_2D = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name());
	if (roiBleachedRing4_2D==null){
		roiBleachedRing4_2D = new ROI(dilatedROI_2D_1,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name());
		addReplaceRoi(roiBleachedRing4_2D);
	}else{
		System.arraycopy(dilatedROI_2D_1.getPixels(), 0, roiBleachedRing4_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_1.getPixels().length);

	}
	ROI roiBleachedRing5_2D = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name());
	if (roiBleachedRing5_2D==null){
		roiBleachedRing5_2D = new ROI(dilatedROI_2D_2,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name());
		addReplaceRoi(roiBleachedRing5_2D);
	}else{
		System.arraycopy(dilatedROI_2D_2.getPixels(), 0, roiBleachedRing5_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_2.getPixels().length);
	}
	ROI roiBleachedRing6_2D = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name());
	if (roiBleachedRing6_2D==null){
		roiBleachedRing6_2D = new ROI(dilatedROI_2D_3,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name());
		addReplaceRoi(roiBleachedRing6_2D);
	}else{
		System.arraycopy(dilatedROI_2D_3.getPixels(), 0, roiBleachedRing6_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_3.getPixels().length);
	}
	ROI roiBleachedRing7_2D = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name());
	if (roiBleachedRing7_2D==null){
		roiBleachedRing7_2D = new ROI(dilatedROI_2D_4,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name());
		addReplaceRoi(roiBleachedRing7_2D);
	}else{
		System.arraycopy(dilatedROI_2D_4.getPixels(), 0, roiBleachedRing7_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_4.getPixels().length);
	}
	ROI roiBleachedRing8_2D = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name());
	if (roiBleachedRing8_2D==null){
		roiBleachedRing8_2D = new ROI(dilatedROI_2D_5,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name());
		addReplaceRoi(roiBleachedRing8_2D);
	}else{
		System.arraycopy(dilatedROI_2D_5.getPixels(), 0, roiBleachedRing8_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_5.getPixels().length);
	}
}


public void saveImageDatasetAsExternalData(LocalWorkspace localWorkspace, ExternalDataIdentifier newImageExtDataID, int startingIndexForRecovery, CartesianMesh cartesianMesh) throws ObjectNotFoundException, FileNotFoundException 
{
	ImageDataset imageDataset = getImageDataset();
	if (imageDataset.getSizeC()>1){
		throw new RuntimeException("FRAPData.saveImageDatasetAsExternalData(): multiple channels not yet supported");
	}
	Extent extent = imageDataset.getExtent();
	ISize isize = imageDataset.getISize();
	int numImageToStore = imageDataset.getSizeT()-startingIndexForRecovery; //not include the prebleach 
	double[][][] pixData = new double[numImageToStore][2][]; //original fluor data and back ground average
	double[] timesArray = new double[numImageToStore];
	double[] bgAvg = getAvgBackGroundIntensity();
	
	for (int tIndex = startingIndexForRecovery; tIndex < imageDataset.getSizeT(); tIndex++) {
		short[] originalData = imageDataset.getPixelsZ(0, tIndex);// images according to zIndex at specific time points(tIndex)
		double[] doubleData = new double[originalData.length];
		double[] expandBgAvg = new double[originalData.length];
		for(int i = 0; i < originalData.length; i++)
		{
			doubleData[i] = 0x0000ffff & originalData[i];
			expandBgAvg[i] = bgAvg[tIndex];
		}
		pixData[tIndex-startingIndexForRecovery][0] = doubleData;
		pixData[tIndex-startingIndexForRecovery][1] = expandBgAvg;
		timesArray[tIndex-startingIndexForRecovery] = imageDataset.getImageTimeStamps()[tIndex]-imageDataset.getImageTimeStamps()[startingIndexForRecovery];
	}
	//changed in March 2008. Though biomodel is not created, we still let user save to xml file.
	Origin origin = new Origin(0,0,0);
	
	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
	fdos.cartesianMesh = cartesianMesh;
	fdos.doubleSpecData =  pixData;
	fdos.specEDI = newImageExtDataID;
	fdos.varNames = new String[] {SimulationContext.FLUOR_DATA_NAME,"bg_average"};
	fdos.owner = LocalWorkspace.getDefaultOwner();
	fdos.times = timesArray;
	fdos.variableTypes = new VariableType[] {VariableType.VOLUME,VariableType.VOLUME};
	fdos.origin = origin;
	fdos.extent = extent;
	fdos.isize = isize;
	localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);	
}

// export the frap data to matlab file. The matlab file contains timestamps(1*Tn) , mask(numImgX * numImgY), 
// ImageDataSet(1*Tn) each cell of (1*Tn) point to a 2d image(numImgX * numImgY)
public void saveImageDatasetAsExternalMatlabData(LocalWorkspace localWorkspace, String matlabFileName,  int startingIndexForRecovery, CartesianMesh cartesianMesh) throws IOException
{
	ImageDataset imageDataset = getImageDataset();
	if (imageDataset.getSizeC()>1){
		throw new RuntimeException("FRAPData.saveImageDatasetAsExternalMatlabData(): multiple channels not yet supported");
	}
	int numX = cartesianMesh.getSizeX();
	int numY = cartesianMesh.getSizeY();
	//prepare variable to write into matlab file, listOfVars is the outmost structure to write to Matlab file.
	ArrayList<MLArray> listOfVars = new ArrayList<MLArray>();
	double[] timeArray = imageDataset.getImageTimeStamps();
	ROI cellROI = getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
	short[] shortCellMask  = cellROI.getPixelsXYZ();
	
	//add image data set to Matlab cell, each cell points to a numX*numY array
	MLCell imageCell =  new MLCell("ImageDataSet",new int[] {timeArray.length, 1});
	for (int tIndex = 0; tIndex < imageDataset.getSizeT(); tIndex++) {
		short[] originalData = imageDataset.getPixelsZ(0, tIndex);// images according to zIndex at specific time points(tIndex)
		double[] doubleImgData = new double[originalData.length];
		for(int i = 0; i < originalData.length; i++)
		{
			doubleImgData[i] = 0x0000ffff & originalData[i];
		}
		MLDouble mlDoublImgData = new MLDouble("ImageDataAtTime_" + tIndex, doubleImgData, numX);
		imageCell.set(mlDoublImgData, tIndex , 0);
	}
	listOfVars.add(imageCell);
	
	//create mask in a Matlab 2D double(numX*numY)
	double[] doubleCellMask = new double[shortCellMask.length];
	for(int i = 0; i < shortCellMask.length; i++)
	{
		doubleCellMask[i] = 0x0000ffff & shortCellMask[i];
	}
	MLDouble cellMask = new MLDouble("CellMask", doubleCellMask, numX);
	listOfVars.add(cellMask);
	
	//create times in a Matlab 2D double(1*numTimePoints)
	MLDouble times = new MLDouble("ExperimentalTimeStamps",new double[][]{timeArray});
	listOfVars.add(times);

	MatFileWriter writer = new MatFileWriter();
	writer.write(matlabFileName, listOfVars);
}

public void saveROIsAsExternalData(LocalWorkspace localWorkspace, ExternalDataIdentifier newROIExtDataID, int startingIndexForRecovery,	CartesianMesh cartesianMesh) throws ObjectNotFoundException, FileNotFoundException 
{
	ImageDataset imageDataset = getImageDataset();
	Extent extent = imageDataset.getExtent();
	ISize isize = imageDataset.getISize();
	int NumTimePoints = 1; 
	int NumChannels = 13;//actually it is total number of ROIs(cell,bleached + 8 rings)+prebleach+firstPostBleach+lastPostBleach
	double[][][] pixData = new double[NumTimePoints][NumChannels][]; // dimensions: time points, channels, whole image ordered by z slices. 
	

	double[] temp_background = getAvgBackGroundIntensity();
	double[] avgPrebleachDouble = calculatePreBleachAverageXYZ(startingIndexForRecovery);
	
	pixData[0][0] = avgPrebleachDouble; // average of prebleach with background subtracted
	// first post-bleach with background subtracted
	pixData[0][1] = createDoubleArray(imageDataset.getPixelsZ(0, startingIndexForRecovery), temp_background[startingIndexForRecovery], true);
//	adjustPrebleachAndPostbleachData(avgPrebleachDouble, pixData[0][1]);
	// last post-bleach image (at last time point) with background subtracted
	pixData[0][2] = createDoubleArray(imageDataset.getPixelsZ(0, imageDataset.getSizeT()-1), temp_background[imageDataset.getSizeT()-1], true);
	//below are ROIs, we don't need to subtract background for them.
	pixData[0][3] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getBinaryPixelsXYZ(1), 0, false);
	pixData[0][4] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getBinaryPixelsXYZ(1), 0, false);
	if (getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()) == null){
		//throw new RuntimeException("must first generate \"derived masks\"");
		pixData[0][5] = new double[imageDataset.getISize().getXYZ()];
		pixData[0][6] = new double[imageDataset.getISize().getXYZ()];
		pixData[0][7] = new double[imageDataset.getISize().getXYZ()];
		pixData[0][8] = new double[imageDataset.getISize().getXYZ()];
		pixData[0][9] = new double[imageDataset.getISize().getXYZ()];
		pixData[0][10] = new double[imageDataset.getISize().getXYZ()];
		pixData[0][11] = new double[imageDataset.getISize().getXYZ()];
		pixData[0][12] = new double[imageDataset.getISize().getXYZ()];
	}
	else{
		pixData[0][5] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()).getBinaryPixelsXYZ(1), 0, false);
		pixData[0][6] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name()).getBinaryPixelsXYZ(1), 0, false);
		pixData[0][7] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name()).getBinaryPixelsXYZ(1), 0, false);
		pixData[0][8] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name()).getBinaryPixelsXYZ(1), 0, false);
		pixData[0][9] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name()).getBinaryPixelsXYZ(1), 0, false);
		pixData[0][10] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name()).getBinaryPixelsXYZ(1), 0, false);
		pixData[0][11] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name()).getBinaryPixelsXYZ(1), 0, false);
		pixData[0][12] = createDoubleArray(getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()).getBinaryPixelsXYZ(1), 0, false);
	}

	Origin origin = new Origin(0,0,0);
	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
	fdos.cartesianMesh = cartesianMesh;
	fdos.doubleSpecData =  pixData;
	fdos.specEDI = newROIExtDataID;
	fdos.varNames = new String[] {
			"prebleach_avg",
			"postbleach_first",
			"postbleach_last",
			"bleached_mask", 
			"cell_mask", 
			"ring1_mask",
			"ring2_mask",
			"ring3_mask",
			"ring4_mask",
			"ring5_mask",
			"ring6_mask",
			"ring7_mask",
			"ring8_mask"};
	fdos.owner = LocalWorkspace.getDefaultOwner();
	fdos.times = new double[] { 0.0 };
	fdos.variableTypes = new VariableType[] {
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME,
			VariableType.VOLUME};
	fdos.origin = origin;
	fdos.extent = extent;
	fdos.isize = isize;
	localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
}

//this method calculates prebleach average for each pixel at different time points. back ground has been subtracted.
//should not subtract background from it when using it as a normalized factor.
public double[] calculatePreBleachAverageXYZ(int startingIndexForRecovery){
	long[] accumPrebleachImage = new long[getImageDataset().getISize().getXYZ()];//ISize: Image size including x, y, z. getXYZ()=x*y*z
	double[] avgPrebleachDouble = new double[accumPrebleachImage.length];
	double[] backGround = getAvgBackGroundIntensity();
	double accumAvgBkGround = 0; //accumulate background before starting index for recovery. used to subtract back ground.
	// changed in June, 2008 average prebleach depends on if there is prebleach images. 
	// Since the initial condition is normalized by prebleach avg, we have to take care the divided by zero error.
	if(startingIndexForRecovery > 0){
		if(backGround != null)//subtract background
		{
			for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
				short[] pixels = getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
				for (int i = 0; i < accumPrebleachImage.length; i++) {
					accumPrebleachImage[i] += 0x0000ffff&pixels[i];
				}
				accumAvgBkGround += backGround[timeIndex];
			}
			for (int i = 0; i < avgPrebleachDouble.length; i++) {
				avgPrebleachDouble[i] = ((double)accumPrebleachImage[i] - accumAvgBkGround)/startingIndexForRecovery;
			}
		}
		else //don't subtract background
		{
			for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
				short[] pixels = getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
				for (int i = 0; i < accumPrebleachImage.length; i++) {
					accumPrebleachImage[i] += 0x0000ffff&pixels[i];
				}
			}
			for (int i = 0; i < avgPrebleachDouble.length; i++) {
				avgPrebleachDouble[i] = (double)accumPrebleachImage[i]/(double)startingIndexForRecovery;
			}
		}
	}
	else{
		//if no prebleach image, use the last recovery image intensity as prebleach average.
		System.err.println("need to determine factor for prebleach average if no pre bleach images.");
		short[] pixels = getImageDataset().getPixelsZ(0, (getImageDataset().getSizeT() - 1));
		for (int i = 0; i < pixels.length; i++) {
			avgPrebleachDouble[i] = ((double)(0x0000ffff&pixels[i]) - backGround[getImageDataset().getSizeT() - 1]);
		}
	}
	//for each pixel if it's grater than 0, we add 1 offset to it. 
	//if it is smaller or equal to 0 , we set it to 1.
	for (int i = 0; i < avgPrebleachDouble.length; i++) {
		if(avgPrebleachDouble[i] <= FRAPOptimizationUtils.epsilon){
			avgPrebleachDouble[i] = 1;
		}
		else
		{
			avgPrebleachDouble[i]=avgPrebleachDouble[i] - FRAPOptimizationUtils.epsilon +1;
		}
	}
	return avgPrebleachDouble;
}

//when creating double array for firstPostBleach and last PostBleach, etc images
//We'll clamp all pixel value <= 0 to 0 and add offset 1 to the whole image.
//For ROI images, we don't have to do so.
private double[] createDoubleArray(short[] shortData, double bkGround, boolean isOffset1ProcessNeeded){
	double[] doubleData = new double[shortData.length];
	for (int i = 0; i < doubleData.length; i++) {
		doubleData[i] = ((0x0000FFFF&shortData[i]) - bkGround);
		if(isOffset1ProcessNeeded)
		{
			if(doubleData[i] <= FRAPOptimizationUtils.epsilon)
			{
				doubleData[i] = 1;
			}
			else
			{
				doubleData[i] = doubleData[i] - FRAPOptimizationUtils.epsilon + 1;
			}
		}
	}
	return doubleData;
}



}
