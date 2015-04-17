/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.gui;

import org.vcell.inversepde.InverseProblem;
import org.vcell.inversepde.LinearResponseModel;
import org.vcell.inversepde.MembraneBasis;
import org.vcell.inversepde.SpatialBasisFunctions;
import org.vcell.inversepde.VolumeBasis;
import org.vcell.inversepde.microscopy.AnnotatedImageDataset_inv;
import org.vcell.inversepde.microscopy.MicroscopyData;
import org.vcell.inversepde.microscopy.ROIImage;
import org.vcell.inversepde.microscopy.ROIImage.ROIImageComponent;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.VCDocument;

import cbit.image.VCImage;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mathmodel.MathModel;

public class InverseProblemReport {

	public static String getReport(MicroscopyData microscopyData){
		StringBuffer buffer = new StringBuffer();
		buffer.append("=================== Microscopy Data ============ \n");
		buffer.append("timeSeriesImageData: "+getReport(microscopyData.getTimeSeriesImageData())+"\n");
		buffer.append("\n");
		buffer.append("PSFImageData: "+getReport(microscopyData.getPsfImageData())+"\n");
		buffer.append("\n");
		buffer.append("zStackImageData: "+getReport(microscopyData.getZStackImageData())+"\n");
		buffer.append("================================================ \n");
		return buffer.toString();
	}


	public static String getReport(InverseProblem inverseProblem){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n\n\n\n");
		buffer.append("================================================\n");
		buffer.append(" I N V E R S E     P R O B L E M    R E P O R T \n");
		buffer.append("================================================\n");
		buffer.append("name='"+inverseProblem.getName()+"'\n");
		buffer.append("description='"+inverseProblem.getDescription()+"'\n");
		buffer.append("\n");
		buffer.append(getReport(inverseProblem.getMicroscopyData()));
		buffer.append("\n");
		buffer.append(getReport(inverseProblem.getLinearResponseModel().getBasisFunctions()));
		buffer.append("\n");
		buffer.append("exactSolutionEDI: "+getReport(inverseProblem.getExactSolutionEDI())+"\n");		
		buffer.append("\n");
		Geometry geometry = inverseProblem.getGeometry();
		if (geometry!=null){
			VCImage vcImage = geometry.getGeometrySpec().getImage();
			buffer.append("geometry:");
			buffer.append(" dim="+geometry.getDimension());
			buffer.append(", imageDim=["+vcImage.getNumX()+","+vcImage.getNumY()+","+vcImage.getNumZ()+"], extent="+geometry.getExtent()+", origin="+geometry.getOrigin()+", subvolumes=[");
			SubVolume[] subVolumes = geometry.getGeometrySpec().getSubVolumes();
			for (int i = 0; i < subVolumes.length; i++) {
				if (i>0){
					buffer.append(",");
				}
				buffer.append(subVolumes[i].getName());
			}
			buffer.append("]\n");
		}else{
			buffer.append("geometry: null\n");
		}
		VCDocument nonlinearModel = inverseProblem.getNonlinearModel();
		if (nonlinearModel!=null){
			buffer.append("NonlinearModel: "+nonlinearModel.toString()+"\n");
		}else{
			buffer.append("NonlinearModel: null\n");
		}
		buffer.append("\n");
		buffer.append(getReport(inverseProblem.getLinearResponseModel()));
		buffer.append("\n");
		String matlabDataFileName = inverseProblem.getMatlabDataFileName();
		if (matlabDataFileName!=null){
			buffer.append("matlabDataFile: "+matlabDataFileName+"\n");
		}else{
			buffer.append("matlabDataFile: null\n");
		}
		return buffer.toString();
	}

	public static String getReport(LinearResponseModel linearResponseModel){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n");
		buffer.append("psfFieldDataEDI: "+getReport(linearResponseModel.getPsfFieldDataEDI())+"\n");
		buffer.append("imageROIFieldDataEDI: "+getReport(linearResponseModel.getImageROIFieldDataEDI())+"\n");		
		buffer.append("\n");
		buffer.append("\n");
		MathModel refVolumeSimMathModel = linearResponseModel.getRefVolumeSimMathModel();
		if (refVolumeSimMathModel!=null){
			buffer.append("RefVolumeSimMathModel: "+refVolumeSimMathModel.toString()+"\n");
		}else{
			buffer.append("RefVolumeSimMathModel: null\n");
		}
		MathModel refMembraneSimMathModel = linearResponseModel.getRefMembraneSimMathModel();
		if (refMembraneSimMathModel!=null){
			buffer.append("RefMembraneSimMathModel: "+refMembraneSimMathModel.toString()+"\n");
		}else{
			buffer.append("RefMembraneSimMathModel: null\n");
		}
		MathModel refFluxSimMathModel = linearResponseModel.getRefFluxSimMathModel();
		if (refFluxSimMathModel!=null){
			buffer.append("RefFluxSimMathModel: "+refFluxSimMathModel.toString()+"\n");
		}else{
			buffer.append("RefFluxSimMathModel: null\n");
		}
		return buffer.toString();
	}

	public static String getReport(SpatialBasisFunctions spatialBasisFunctions){
		StringBuffer buffer = new StringBuffer();
		buffer.append("volumeBases: ");
		VolumeBasis[] volumeBases = spatialBasisFunctions.getVolumeBases();
		if (volumeBases!=null){
			buffer.append("length="+volumeBases.length+"\n");
			for (int i = 0; i < volumeBases.length; i++) {
				buffer.append("    volumeBasis["+i+"], "+volumeBases[i].getReport()+"\n");
			}
		}else{
			buffer.append("null\n");
		}
		// write extra info for matlab model.
		if (volumeBases!=null){
			buffer.append("within bleach indices: [");
			for (int i = 0; i < volumeBases.length; i++) {
				if (volumeBases[i].isWithinBleach()){
					buffer.append(volumeBases[i].getBasisIndex()+" ");
				}
			}
			buffer.append("]\n");
			buffer.append("within bleach mask: [");
			for (int i = 0; i < volumeBases.length; i++) {
				if (volumeBases[i].isWithinBleach()){
					buffer.append("1 ");
				}else{
					buffer.append("0 ");
				}
			}
			buffer.append("]\n");
		}
		buffer.append("membraneBases: ");
		MembraneBasis[] membraneBases = spatialBasisFunctions.getMembraneBases();
		if (membraneBases!=null){
			buffer.append("length="+membraneBases.length+"\n");
			for (int i = 0; i < membraneBases.length; i++) {
				buffer.append("    membraneBasis["+i+"], "+membraneBases[i].getReport()+"\n");
			}
		}else{
			buffer.append("null\n");
		}
		buffer.append("\n");
		buffer.append("basesFieldDataEDI: "+getReport(spatialBasisFunctions.getBasisFieldDataEDI())+"\n");
		MathModel fieldMathModel = spatialBasisFunctions.getFieldMathModel();
		if (fieldMathModel!=null){
			buffer.append("FieldMathModel: "+fieldMathModel.toString()+"\n");
		}else{
			buffer.append("FieldMathModel: null\n");
		}
		buffer.append("\n");
		return buffer.toString();
	}

	public static String getReport(AnnotatedImageDataset_inv annotImageDataset){
		StringBuffer buffer = new StringBuffer();
		if (annotImageDataset!=null){
			buffer.append(annotImageDataset.getImageDataset().getISize().toString()+" "+annotImageDataset.getImageDataset().getISize().getXYZ()+" pixels");
			ROIImage[] roiImages = annotImageDataset.getROIImages();
			for (int i = 0; i < roiImages.length; i++) {
				buffer.append("\n    ROIImage('"+roiImages[i].getName()+"') size="+roiImages[i].getISize().toString()+", ROIS=[");
				ROIImageComponent[] rois = roiImages[i].getROIs();
				for (int j = 0; j < rois.length; j++) {
					buffer.append(" '"+rois[j].getName()+"'<"+rois[j].countPixels()+" pixels>");
				}
				buffer.append("]");
			}
			buffer.append("\n   extent="+annotImageDataset.getImageDataset().getExtent());
			buffer.append("\n   origin="+annotImageDataset.getImageDataset().getImage(0,0,0).getOrigin());
		}else{
			buffer.append("null");
		}
		return buffer.toString();
	}

	public static String getReport(ExternalDataIdentifier externDataID){
		StringBuffer buffer = new StringBuffer();
		if (externDataID!=null){
			buffer.append("name='"+externDataID.getName()+"', key="+externDataID.getKey().toString()+", user="+externDataID.getOwner().toString());
		}else{
			buffer.append("null");
		}
		return buffer.toString();
	}

}
