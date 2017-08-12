package org.vcell.vmicro.op;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateDependentImageROIsOp {
	
	private enum VFRAP_ROI_ENUM {
		ROI_BLEACHED_RING1,
		ROI_BLEACHED_RING2,
		ROI_BLEACHED_RING3,
		ROI_BLEACHED_RING4,
		ROI_BLEACHED_RING5,
		ROI_BLEACHED_RING6,
		ROI_BLEACHED_RING7,
		ROI_BLEACHED_RING8
	};
	
	public ROI[] generate(ROI cellROI_2d, ROI bleachedROI_2d) throws Exception {
		
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

		cellROI_2D = cellROI_2d.getRoiImages()[0];
		bleachedROI_2D = bleachedROI_2d.getRoiImages()[0];

		dilatedROI_2D_1 = UShortImage.fastDilate(bleachedROI_2D, 4, cellROI_2D);
		dilatedROI_2D_2 = UShortImage.fastDilate(bleachedROI_2D, 10, cellROI_2D);
    	dilatedROI_2D_3 = UShortImage.fastDilate(bleachedROI_2D, 18, cellROI_2D);
    	dilatedROI_2D_4 = UShortImage.fastDilate(bleachedROI_2D, 28, cellROI_2D);
    	dilatedROI_2D_5 = UShortImage.fastDilate(bleachedROI_2D, 40, cellROI_2D);
		erodedROI_2D_0 = new UShortImage(bleachedROI_2D);
		
		// The erode always causes problems if eroding without checking the bleached length and height.
		// we have to check the min length of the bleached area to make sure erode within the length.
		Rectangle bleachRect = bleachedROI_2D.getNonzeroBoundingBox();
		if (bleachRect==null || bleachRect.isEmpty()){
			throw new RuntimeException("bleach ROI is null or empty");
		}
		int minLen = Math.min(bleachRect.height, bleachRect.width);
		if((minLen/2.0) < 5) {
			erodedROI_2D_1 = UShortImage.erodeDilate(bleachedROI_2D, UShortImage.createCircularBinaryKernel(1), bleachedROI_2D,true);
			erodedROI_2D_2 = UShortImage.erodeDilate(bleachedROI_2D, UShortImage.createCircularBinaryKernel(2), bleachedROI_2D,true);
		} else {
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


		ArrayList<ROI> rois = new ArrayList<ROI>();
//		rois.add(new ROI(this.bleachedROI_2D.getData())); // copy this one through
		rois.add(new ROI(erodedROI_2D_2,VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()));
		rois.add(new ROI(erodedROI_2D_1,VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name()));
		rois.add(new ROI(erodedROI_2D_0,VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name()));
		rois.add(new ROI(dilatedROI_2D_1,VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name()));
		rois.add(new ROI(dilatedROI_2D_2,VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name()));
		rois.add(new ROI(dilatedROI_2D_3,VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name()));
		rois.add(new ROI(dilatedROI_2D_4,VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name()));
		rois.add(new ROI(dilatedROI_2D_5,VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()));
		
		Iterator<ROI> roiIter = rois.iterator();
		while (roiIter.hasNext()){
			ROI roi = roiIter.next();
			short[] roiPixels = roi.getBinaryPixelsXYZ(1);
			int numPixels = roiPixels.length;
			int count=0;
			for (int p=0; p<numPixels; p++){
				if (roiPixels[p] != 0){
					count++;
				}
			}
			if (count==0){
				roiIter.remove();
			}
		}
		
//		ArrayList<NormalizedSampleFunction> roiSampleFunctions = new ArrayList<NormalizedSampleFunction>();
//		for (ROI roi : rois){
//			roiSampleFunctions.add(NormalizedSampleFunction.fromROI(roi));
//		}
//		
//		return roiSampleFunctions.toArray(new NormalizedSampleFunction[0]);
		
		return rois.toArray(new ROI[0]);
	}

}
