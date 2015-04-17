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

import java.beans.PropertyVetoException;
import java.util.Arrays;

import org.vcell.inversepde.microscopy.ROIImage;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;
import org.vcell.util.document.ExternalDataIdentifier;

import cbit.image.VCImage;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;

public class LinearResponseModel  implements Matchable {

	//
	//  This class holds everything necessary to generate and store (within the MathModels) the basis response functions (r)
	//
	
	private SpatialBasisFunctions spatialBasisFunctions = new SpatialBasisFunctions(); // membrane and volume spatial basis functions.
	
	private ExternalDataIdentifier psfFieldDataEDI = null;      // 3D Point Spread Function used to simulate fluorescence.
	private ExternalDataIdentifier imageROIFieldDataEDI = null; // e.g. rings in VFrap, used for data reduction and comparison with experimental fluorescence image time series.

	//
	// math models are simulated (parameter scan over spatial basis functions) to steady state and post processed to calculate the r's.
	//
	private MathModel refVolumeSimMathModel = null;     // volume response to volume spatial basis functions (diffusion only)
	private MathModel refMembraneSimMathModel = null;   // surface response to surface spatial basis functions (diffusion only)
	private MathModel refFluxSimMathModel = null;       // volume response to flux from surface spatial basis functions (diffusion only)
	
	private final static double diffusionScaleValue = 0.1;
	private final static double deltaTValue = 0.1;

	public ExternalDataIdentifier getPsfFieldDataEDI() {
		return psfFieldDataEDI;
	}

	public void setPsfFieldDataEDI(ExternalDataIdentifier psfFieldDataEDI) {
		this.psfFieldDataEDI = psfFieldDataEDI;
	}
	
	public MathModel getRefVolumeSimMathModel() {
		return refVolumeSimMathModel;
	}
	
	public MathModel getRefMembraneSimMathModel() {
		return refMembraneSimMathModel;
	}
	
	public MathModel getRefFluxSimMathModel() {
		return refFluxSimMathModel;
	}
	
	public void setRefVolumeSimMathModel(MathModel refVolumeSimMathModel) {
		this.refVolumeSimMathModel = refVolumeSimMathModel;
	}

	public void setRefMembraneSimMathModel(MathModel refMembraneSimMathModel) {
		this.refMembraneSimMathModel = refMembraneSimMathModel;
	}

	public void setRefFluxSimMathModel(MathModel refFluxSimMathModel) {
		this.refFluxSimMathModel = refFluxSimMathModel;
	}

	public ExternalDataIdentifier getImageROIFieldDataEDI() {
		return imageROIFieldDataEDI;
	}
	public void setImageROIFieldDataEDI(ExternalDataIdentifier imageFieldDataEDI) {
		this.imageROIFieldDataEDI = imageFieldDataEDI;
	}
	
	public SpatialBasisFunctions getBasisFunctions(){
		return spatialBasisFunctions;
	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof LinearResponseModel){
			LinearResponseModel ip = (LinearResponseModel)obj;
			if (!Compare.isEqualOrNull(getPsfFieldDataEDI(), ip.getPsfFieldDataEDI())){
				return false;
			}
			if (!Compare.isEqualOrNull(getImageROIFieldDataEDI(), ip.getImageROIFieldDataEDI())){
				return false;
			}
			if (!Compare.isEqualOrNull(getBasisFunctions(), ip.getBasisFunctions())){
				return false;
			}
			if (!Compare.isEqualOrNull(getRefVolumeSimMathModel(), ip.getRefVolumeSimMathModel())){
				return false;
			}
			if (!Compare.isEqualOrNull(getRefMembraneSimMathModel(), ip.getRefMembraneSimMathModel())){
				return false;
			}
			if (!Compare.isEqualOrNull(getRefFluxSimMathModel(), ip.getRefFluxSimMathModel())){
				return false;
			}
			
			return true;
		}
		return false;
	}

	public void createRefVolumeSimMathModel(Geometry geometry, ROIImage basisROIImage, InverseProblem inverseProblem) throws PropertyVetoException, ExpressionException, MathException, DataAccessException {
		
		FieldFunctionArguments volBasisFieldFunctionArgs = new FieldFunctionArguments(spatialBasisFunctions.getBasisFieldDataEDI().getName(),InverseProblemContants.FIELDDATA_VARNAME_BASIS,new Expression(0.0),VariableType.VOLUME);
		FieldFunctionArguments psfFieldFunctionArgs = new FieldFunctionArguments(getPsfFieldDataEDI().getName(),InverseProblemContants.FIELDDATA_VARNAME_PSF,new Expression(0.0),VariableType.VOLUME);
		FieldFunctionArguments imageFieldFunctionArgs = new FieldFunctionArguments(getImageROIFieldDataEDI().getName(),InverseProblemContants.FIELDDATA_VARNAME_IMAGEROI,new Expression(0.0),VariableType.VOLUME);

		if (geometry.getDimension()==0){
			throw new RuntimeException("expecting spatial geometry");
		}
		MathModel mathModel = new MathModel(null);
		MathDescription mathDesc = mathModel.getMathDescription();
	
		mathDesc.setGeometry(geometry);
		try {
			if (geometry.getGeometrySurfaceDescription().getGeometricRegions() == null){
				geometry.getGeometrySurfaceDescription().updateAll();
			}
		}catch (cbit.image.ImageException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
		}catch (cbit.vcell.geometry.GeometryException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
		}
	
		Constant S = new Constant(InverseProblemContants.DIFFUSION_SCALE_NAME,new Expression(diffusionScaleValue));
		Constant deltaT = new Constant(InverseProblemContants.DELTA_T_NAME,new Expression(deltaTValue));
		Constant scanIndex = new Constant("scanIndex",new Expression(0.0));
		Function PSF_function = new Function(InverseProblemContants.PSF_FUNCTION_NAME,new Expression("field("+psfFieldFunctionArgs.toCSVString()+")"),null);
		VolVariable volVar = new VolVariable(InverseProblemContants.VOL_VAR_NAME,null);
		mathDesc.addVariable(volVar);
		mathDesc.addVariable(S);
		mathDesc.addVariable(deltaT);
		mathDesc.addVariable(scanIndex);
		mathDesc.addVariable(PSF_function);
		SubVolume subVolumes[] = geometry.getGeometrySpec().getSubVolumes();
		for (int i=0;i<subVolumes.length;i++){
			CompartmentSubDomain subDomain = new cbit.vcell.math.CompartmentSubDomain(subVolumes[i].getName(),subVolumes[i].getHandle());
			mathDesc.addSubDomain(subDomain);
			PdeEquation pde = new PdeEquation(volVar,false);
			pde.setDiffusionExpression(new Expression("("+S.getName()+" * (t + "+deltaT.getName()+"))"));
			pde.setInitialExpression(new Expression("field("+volBasisFieldFunctionArgs.toCSVString()+") == "+scanIndex.getName()));
			pde.setRateExpression(new Expression(0.0));
			pde.setBoundaryXm(new Expression(0.0));
			pde.setBoundaryXp(new Expression(0.0));
			pde.setBoundaryYm(new Expression(0.0));
			pde.setBoundaryYp(new Expression(0.0));
			pde.setBoundaryZm(new Expression(0.0));
			pde.setBoundaryZp(new Expression(0.0));
			subDomain.addEquation(pde);
			subDomain.setBoundaryConditionXm(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionXp(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionYm(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionYp(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionZm(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionZp(BoundaryConditionType.getNEUMANN());
		}
		//
		// add only those MembraneSubDomains corresponding to surfaces that actually exist in geometry.
		//
		GeometricRegion regions[] = geometry.getGeometrySurfaceDescription().getGeometricRegions();
		for (int i = 0; i < regions.length; i++){
			if (regions[i] instanceof SurfaceGeometricRegion){
				SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)regions[i];
				CompartmentSubDomain compartment1 = mathDesc.getCompartmentSubDomain(((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[0]).getSubVolume().getName());
				CompartmentSubDomain compartment2 = mathDesc.getCompartmentSubDomain(((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[1]).getSubVolume().getName());
				MembraneSubDomain membraneSubDomain = mathDesc.getMembraneSubDomain(compartment1,compartment2);
				if (membraneSubDomain==null){
					membraneSubDomain = new MembraneSubDomain(compartment1,compartment2);
					mathDesc.addSubDomain(membraneSubDomain);
					
					JumpCondition jumpCondition1 = new JumpCondition(volVar);
					jumpCondition1.setInFlux(new Expression(0.0));
					jumpCondition1.setOutFlux(new Expression(0.0));
					membraneSubDomain.addJumpCondition(jumpCondition1);
				}
			}
		}
		if (!mathDesc.isValid()){
			throw new RuntimeException("generated math is not valid, problem: "+mathDesc.getWarning());
		}
		Simulation sim = mathModel.addNewSimulation("mysim");
		sim.getSolverTaskDescription().setTimeStep(new TimeStep(1,deltaTValue,1));
		
		// setup parameter scan
		short[] volumeBasisPixelValues = getVolumeBasisPixelValues(basisROIImage);
		
		String[] valueList = new String[volumeBasisPixelValues.length];
		for (int i = 0; i < valueList.length; i++) {
			valueList[i] = String.valueOf(volumeBasisPixelValues[i]);
		}
		ConstantArraySpec constantArraySpec = ConstantArraySpec.createListSpec(scanIndex.getName(), valueList); 
		sim.getMathOverrides().putConstantArraySpec(constantArraySpec);				
//Constant constant = new Constant(scanIndex.getName(), new Expression(volumeBasisPixelValues[0])); 
//sim.getMathOverrides().putConstant(constant);
		
		int keepEvery = 1;
		sim.getSolverTaskDescription().setOutputTimeSpec(new DefaultOutputTimeSpec(keepEvery));
		sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0,1000));
//sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0,1));		
		sim.getSolverTaskDescription().setStopAtSpatiallyUniformErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
		sim.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
		VCImage geoImage = geometry.getGeometrySpec().getImage();
		sim.getMeshSpecification().setSamplingSize(new ISize(geoImage.getNumX(),geoImage.getNumY(),geoImage.getNumZ()));
		
		//
		// add processing instructions to simulation to post-process simulation results.
		//
		VolumeBasis[] volumeBases = spatialBasisFunctions.getVolumeBases();
		int[] volumePoints = new int[volumeBases.length];
		for (int i = 0; i < volumeBases.length; i++) {
			volumePoints[i] = volumeBases[i].getControlPointMeshIndex();
		}
		int numRegions = inverseProblem.getMicroscopyData().getTimeSeriesImageData().getROIImage(ROIImage.ROIIMAGE_IMAGEROIS).getROIs().length;
		int zSlice = inverseProblem.getMicroscopyData().getZStackImageData().getImageDataset().getSizeZ()/2;
		boolean storeEnabled = false;
		sim.setDataProcessingInstructions(DataProcessingInstructions.getVFrapInstructions(volumePoints, null, numRegions, 
				zSlice, getImageROIFieldDataEDI().getKey(), imageFieldFunctionArgs, storeEnabled));

		this.refVolumeSimMathModel = mathModel;
	}

	private short[] getVolumeBasisPixelValues(ROIImage basisROIImage) {
		short[] volumeBasisPixelValues = new short[basisROIImage.getROIs().length];
		for (int i = 0; i < volumeBasisPixelValues.length; i++) {
			volumeBasisPixelValues[i] = basisROIImage.getROIs()[i].getPixelValue();
		}
		Arrays.sort(volumeBasisPixelValues);
		return volumeBasisPixelValues;
	}
	
	public void createRefMembraneOrFluxSimMathModel(Geometry geometry, ROIImage basisROIImage, boolean bMembrane) throws PropertyVetoException,
			ExpressionException, MathException, ExpressionBindingException {
		
		MathModel mathModel = new MathModel(null);
		MathDescription mathDesc = mathModel.getMathDescription();
		FieldFunctionArguments volBasisFieldFunctionArgs = new FieldFunctionArguments(spatialBasisFunctions.getBasisFieldDataEDI().getName(),InverseProblemContants.FIELDDATA_VARNAME_BASIS,new Expression(0.0),VariableType.VOLUME);
		FieldFunctionArguments psfFieldFunctionArgs = new FieldFunctionArguments(getPsfFieldDataEDI().getName(),InverseProblemContants.FIELDDATA_VARNAME_PSF,new Expression(0.0),VariableType.VOLUME);

		mathDesc.setGeometry(geometry);
		try {
			if (geometry.getGeometrySurfaceDescription().getGeometricRegions() == null){
				geometry.getGeometrySurfaceDescription().updateAll();
			}
		}catch (cbit.image.ImageException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
		}catch (cbit.vcell.geometry.GeometryException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
		}
	
		Constant S = new Constant(InverseProblemContants.DIFFUSION_SCALE_NAME,new Expression(diffusionScaleValue));
		Constant deltaT = new Constant(InverseProblemContants.DELTA_T_NAME,new Expression(deltaTValue));
		Constant scanIndex = new Constant("scanIndex",new Expression(0.0));
		Function PSF_function = new Function(InverseProblemContants.PSF_FUNCTION_NAME,new Expression("field("+psfFieldFunctionArgs.toCSVString()+")"),null);
		VolVariable fieldVolVar = new VolVariable(InverseProblemContants.FIELD_VOL_VAR_NAME,null);
		VolVariable volVar = null;
		MemVariable memVar = null;
		if (bMembrane){
			memVar = new MemVariable(InverseProblemContants.MEM_VAR_NAME,null);
			mathDesc.addVariable(memVar);
		}else{
			volVar = new VolVariable(InverseProblemContants.VOL_VAR_NAME,null);
			mathDesc.addVariable(volVar);
		}
		mathDesc.addVariable(fieldVolVar);
		mathDesc.addVariable(S);
		mathDesc.addVariable(deltaT);
		mathDesc.addVariable(scanIndex);
		mathDesc.addVariable(PSF_function);
		SubVolume subVolumes[] = geometry.getGeometrySpec().getSubVolumes();
		for (int i=0;i<subVolumes.length;i++){
			CompartmentSubDomain subDomain = new cbit.vcell.math.CompartmentSubDomain(subVolumes[i].getName(),subVolumes[i].getHandle());
			mathDesc.addSubDomain(subDomain);
			PdeEquation pde2 = new PdeEquation(fieldVolVar,false);
			pde2.setDiffusionExpression(new Expression(0.0));
			//
			// the FIELD_SCALE_FACTOR (very small number) makes the volumeBases uniform within tolerance ... so that "stop when uniform" will work for variables of interest.
			//
			pde2.setInitialExpression(new Expression(InverseProblemContants.FIELD_SCALE_FACTOR+"*(field("+volBasisFieldFunctionArgs.toCSVString()+") == "+scanIndex.getName()+")"));
			pde2.setRateExpression(new Expression(0.0));
			pde2.setBoundaryXm(new Expression(0.0));
			pde2.setBoundaryXp(new Expression(0.0));
			pde2.setBoundaryYm(new Expression(0.0));
			pde2.setBoundaryYp(new Expression(0.0));
			pde2.setBoundaryZm(new Expression(0.0));
			pde2.setBoundaryZp(new Expression(0.0));
			subDomain.addEquation(pde2);
			if (!bMembrane){
				PdeEquation pde = new PdeEquation(volVar,false);
				pde.setDiffusionExpression(new Expression("("+S.getName()+" * (t + "+deltaT.getName()+"))"));
				pde.setInitialExpression(new Expression(0.0));
				pde.setRateExpression(new Expression(0.0));
				pde.setBoundaryXm(new Expression(0.0));
				pde.setBoundaryXp(new Expression(0.0));
				pde.setBoundaryYm(new Expression(0.0));
				pde.setBoundaryYp(new Expression(0.0));
				pde.setBoundaryZm(new Expression(0.0));
				pde.setBoundaryZp(new Expression(0.0));
				subDomain.addEquation(pde);
			}
			subDomain.setBoundaryConditionXm(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionXp(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionYm(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionYp(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionZm(BoundaryConditionType.getNEUMANN());
			subDomain.setBoundaryConditionZp(BoundaryConditionType.getNEUMANN());
		}
		//
		// add only those MembraneSubDomains corresponding to surfaces that actually exist in geometry.
		//
		GeometricRegion regions[] = geometry.getGeometrySurfaceDescription().getGeometricRegions();
		for (int i = 0; i < regions.length; i++){
			if (regions[i] instanceof SurfaceGeometricRegion){
				SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)regions[i];
				CompartmentSubDomain compartment1 = mathDesc.getCompartmentSubDomain(((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[0]).getSubVolume().getName());
				CompartmentSubDomain compartment2 = mathDesc.getCompartmentSubDomain(((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[1]).getSubVolume().getName());
				MembraneSubDomain membraneSubDomain = mathDesc.getMembraneSubDomain(compartment1,compartment2);
				if (membraneSubDomain==null){
					membraneSubDomain = new MembraneSubDomain(compartment1,compartment2);
					mathDesc.addSubDomain(membraneSubDomain);
					
					JumpCondition jumpCondition2 = new JumpCondition(fieldVolVar);
					jumpCondition2.setInFlux(new Expression(0.0));
					jumpCondition2.setOutFlux(new Expression(0.0));
					membraneSubDomain.addJumpCondition(jumpCondition2);
					
					if (!bMembrane){
						JumpCondition jumpCondition1 = new JumpCondition(volVar);
						jumpCondition1.setInFlux(new Expression("("+InverseProblemContants.FIELD_VOL_VAR_NAME+"_INSIDE>0)*((t < (1.5 * "+InverseProblemContants.DELTA_T_NAME+")) / "+InverseProblemContants.DELTA_T_NAME+")"));
						jumpCondition1.setOutFlux(new Expression("("+InverseProblemContants.FIELD_VOL_VAR_NAME+"_OUTSIDE>0)*((t < (1.5 * "+InverseProblemContants.DELTA_T_NAME+")) / "+InverseProblemContants.DELTA_T_NAME+")"));
						membraneSubDomain.addJumpCondition(jumpCondition1);
					}
					if (bMembrane){
						PdeEquation pde = new PdeEquation(memVar);
						pde.setDiffusionExpression(new Expression("("+S.getName()+" * (t + "+deltaT.getName()+"))"));
						pde.setInitialExpression(new Expression("("+InverseProblemContants.FIELD_VOL_VAR_NAME+"_INSIDE>0)"));
						pde.setRateExpression(new Expression(0.0));
						pde.setBoundaryXm(new Expression(0.0));
						pde.setBoundaryXp(new Expression(0.0));
						pde.setBoundaryYm(new Expression(0.0));
						pde.setBoundaryYp(new Expression(0.0));
						pde.setBoundaryZm(new Expression(0.0));
						pde.setBoundaryZp(new Expression(0.0));
						membraneSubDomain.addEquation(pde);
					}					
				}
			}
		}
		if (!mathDesc.isValid()){
			throw new RuntimeException("generated math is not valid, problem: "+mathDesc.getWarning());
		}
		Simulation sim = mathModel.addNewSimulation("mysim");
		sim.getSolverTaskDescription().setTimeStep(new TimeStep(1,deltaTValue,1));
		
		// setup parameter scan
		//
		// really should filter out the simulations that won't do anything
		//
		short[] volumeBasisPixelValues = getVolumeBasisPixelValues(basisROIImage);
		
		String[] valueList = new String[volumeBasisPixelValues.length];
		for (int i = 0; i < valueList.length; i++) {
			valueList[i] = String.valueOf(volumeBasisPixelValues[i]);
		}
		ConstantArraySpec constantArraySpec = ConstantArraySpec.createListSpec(scanIndex.getName(), valueList); 
		sim.getMathOverrides().putConstantArraySpec(constantArraySpec);
		
		int keepEvery = 1;
		sim.getSolverTaskDescription().setOutputTimeSpec(new DefaultOutputTimeSpec(keepEvery));
		sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0,1000));
		sim.getSolverTaskDescription().setStopAtSpatiallyUniformErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
		sim.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
		VCImage geoImage = geometry.getGeometrySpec().getImage();
		sim.getMeshSpecification().setSamplingSize(new ISize(geoImage.getNumX(),geoImage.getNumY(),geoImage.getNumZ()));
		if (bMembrane){
			this.refMembraneSimMathModel = mathModel;
		}else{
			this.refFluxSimMathModel = mathModel;
		}
	}

	public void setBasisFunctions(SpatialBasisFunctions basisFunctions2) {
		this.spatialBasisFunctions = basisFunctions2;
	}


}
