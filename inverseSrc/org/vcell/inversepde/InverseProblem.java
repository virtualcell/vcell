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


import org.vcell.inversepde.microscopy.MicroscopyData;
import org.vcell.inversepde.microscopy.ROIImage;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.VCDocument;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;

public class InverseProblem implements Matchable {
	private String name = null;
	private String description = null;
	
	private VCDocument nonlinearModel = null;  // original, user-defined model we are trying to fit to data.  The geometry matches the geometry used in the Linear Responses, ... etc.
	private cbit.vcell.opt.Parameter[] optParameters = null; // parameters from user-defined model to be fit (e.g. kon, koff).

	private MicroscopyData microscopyData = new MicroscopyData(); // point spread function, 3D image stack (optional???), 2D time series.
	
	//
	// this math model exercises the volume responses, membrane responses, and flux responses (via VCell client/server).
	//
	private LinearResponseModel linearResponseModel = new LinearResponseModel();  // everything necessary to generate and store basis response functions.
	private Geometry geometry = null; // 3D geometry
	
	private ExternalDataIdentifier exactSolutionEDI = null;  // if exact solution is known ... for testing purposes only.
	
	// we use matlab to solve for wijk to solve PDE and predict fluorescence at image ROIs for comparison with time series ....
	// all under the direction of a parameter estimation routine.
	private String matlabDataFileName = null; // holds all of the basis response functions and time series data in .mat format used as input to Matlab scripts.
	
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof InverseProblem){
			InverseProblem ip = (InverseProblem)obj;
			if (!Compare.isEqualOrNull(getName(), ip.getName())){
				return false;
			}
			if (!Compare.isEqualOrNull(getDescription(), ip.getDescription())){
				return false;
			}
			if (!Compare.isEqualOrNull(getMicroscopyData(), ip.getMicroscopyData())){
				return false;
			}
			if (!Compare.isEqualOrNull(getLinearResponseModel(), ip.getLinearResponseModel())){
				return false;
			}
			if (!Compare.isEqualOrNull(getExactSolutionEDI(), ip.getExactSolutionEDI())){
				return false;
			}
			if (!Compare.isEqualOrNull(getNonlinearModel(), ip.getNonlinearModel())){
				return false;
			}
			if (!Compare.isEqualOrNull(getOptParameters(), ip.getOptParameters())){
				return false;
			}
			if (!Compare.isEqualOrNull(getGeometry(), ip.getGeometry())){
				return false;
			}
			if (!Compare.isEqualOrNull(getMatlabDataFileName(), ip.getMatlabDataFileName())){
				return false;
			}
			
			return true;
		}
		return false;
	}
	
	public LinearResponseModel getLinearResponseModel(){
		return linearResponseModel;
	}

	public String getDescription() {
		return description;
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public String getName() {
		return name;
	}
	public boolean[] isMembraneFluxesDefined(SubVolume subvolume1, SubVolume subvolume2){
		if ((subvolume2.getName().equals(ROIImage.ROI_CELL) && subvolume1.getName().equals(ROIImage.ROI_EXTRACELLULAR)) ||
			(subvolume2.getName().equals(ROIImage.ROI_EXTRACELLULAR) && subvolume1.getName().equals(ROIImage.ROI_CELL))){
			//
			// this is the plasma membrane ... for now, we assume FRAP models don't exchange with membranes.
			//
			System.out.println("InverseProblem.isMembraneFluxDefined(): ASSUMING zero flux at plasma membrane");
			return new boolean[] { false, false };
		}else if ((subvolume2.getName().equals(ROIImage.ROI_BLEACHEDCELL) && subvolume1.getName().equals(ROIImage.ROI_CELL)) ||
				  (subvolume2.getName().equals(ROIImage.ROI_CELL) && subvolume1.getName().equals(ROIImage.ROI_BLEACHEDCELL))){
			//
			// this is really a pseudo-membrane for purposes of field generation.
			//
			return new boolean[] { true, true };
		}else{
			throw new RuntimeException("unexpected membrane between "+subvolume2.getName()+" and "+subvolume1.getName());
		}
	}
	public boolean isMembraneSourceDefined(SubVolume subvolume1, SubVolume subvolume2){
		if ((subvolume2.getName().equals(ROIImage.ROI_CELL) && subvolume1.getName().equals(ROIImage.ROI_EXTRACELLULAR)) ||
			(subvolume2.getName().equals(ROIImage.ROI_EXTRACELLULAR) && subvolume1.getName().equals(ROIImage.ROI_CELL))){
			//
			// this is the plasma membrane ... for now, we assume FRAP models don't exchange with membranes.
			//
			System.out.println("InverseProblem.isMembraneSourceDefined(): ASSUMING no membrane source terms at plasma membrane");
			return false;
		}else if ((subvolume2.getName().equals(ROIImage.ROI_BLEACHEDCELL) && subvolume1.getName().equals(ROIImage.ROI_CELL)) ||
				  (subvolume2.getName().equals(ROIImage.ROI_CELL) && subvolume1.getName().equals(ROIImage.ROI_BLEACHEDCELL))){
			// not even a real membrane
			return false;
		}else{
			throw new RuntimeException("unexpected membrane between "+subvolume2.getName()+" and "+subvolume1.getName());
		}
	}
	public boolean isVolumeSourceDefined(SubVolume subvolume){
		if (subvolume==null){
			return true;
		}
		if (subvolume.getName().equals(ROIImage.ROI_CELL)){
			return true;
		}
		if (subvolume.getName().equals(ROIImage.ROI_BLEACHEDCELL)){
			return true;
		}
		return false;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MicroscopyData getMicroscopyData() {
		return microscopyData;
	}

	public void setMicroscopyData(MicroscopyData microscopyData) {
		this.microscopyData = microscopyData;
	}
	public String getMatlabDataFileName() {
		return matlabDataFileName;
	}
	public void setMatlabDataFileName(String matlabDataFileName) {
		this.matlabDataFileName = matlabDataFileName;
	}
	public ExternalDataIdentifier getExactSolutionEDI() {
		return exactSolutionEDI;
	}
	public void setExactSolutionEDI(ExternalDataIdentifier exactSolutionEDI) {
		this.exactSolutionEDI = exactSolutionEDI;
	}
	public VCDocument getNonlinearModel() {
		return nonlinearModel;
	}
	public void setNonlinearModel(VCDocument nonlinearModel) {
		this.nonlinearModel = nonlinearModel;
	}
	public cbit.vcell.opt.Parameter[] getOptParameters() {
		return optParameters;
	}
	public void setOptParameters(cbit.vcell.opt.Parameter[] optParameters) {
		this.optParameters = optParameters;
	}

	public void setLinearResponseModel(LinearResponseModel linearResponseModel2) {
		this.linearResponseModel = linearResponseModel2;
	}
}
