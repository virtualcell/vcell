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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import org.vcell.inversepde.BasisGenerator.Medoid;
import org.vcell.inversepde.microscopy.ROIImage;
import org.vcell.inversepde.microscopy.ROIImage.ROIImageComponent;
import org.vcell.inversepde.services.InversePDERequestManager;
import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.MathModelInfo;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.db.SimulationJobStatusPersistent;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.MembraneElement;

public class SpatialBasisFunctions implements Matchable {

	private MathModel fieldMathModel = null;
	private VolumeBasis[] volumeBases = null;
	private MembraneBasis[] membraneBases = null;
	private ExternalDataIdentifier basisFieldDataEDI = null;

	public ExternalDataIdentifier getBasisFieldDataEDI() {
		return basisFieldDataEDI;
	}

	public void setBasisFieldDataEDI(ExternalDataIdentifier basisFieldDataEDI) {
		this.basisFieldDataEDI = basisFieldDataEDI;
	}
	
	public VolumeBasis[] getVolumeBases() {
		return volumeBases;
	}
	
	public VolumeBasis getVolumeBasesByName(String basisName){
		for (int i = 0; volumeBases!=null && i < volumeBases.length; i++) {
			if (volumeBases[i].getName().equals(basisName)){
				return volumeBases[i];
			}
		}
		return null;
	}
	public VolumeBasis getVolumeBasesByImageValue(int basesImageValue){
		for (int i = 0; volumeBases!=null && i < volumeBases.length; i++) {
			if (volumeBases[i].getBasisImageValue() == basesImageValue){
				return volumeBases[i];
			}
		}
		return null;
	}

	public void setVolumeBases(VolumeBasis[] volumeBases) {
		this.volumeBases = volumeBases;
	}
	public MembraneBasis[] getMembraneBases() {
		return membraneBases;
	}
	public MembraneBasis getMembraneBasesByName(String basisName){
		for (int i = 0; membraneBases!=null && i < membraneBases.length; i++) {
			if (membraneBases[i].getName().equals(basisName)){
				return membraneBases[i];
			}
		}
		return null;
	}
	public void setMembraneBases(MembraneBasis[] membraneBases) {
		this.membraneBases = membraneBases;
	}
	public MathModel getFieldMathModel(){
		return fieldMathModel;
	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof SpatialBasisFunctions){
			SpatialBasisFunctions ip = (SpatialBasisFunctions)obj;
			if (!Compare.isEqualOrNull(getBasisFieldDataEDI(), ip.getBasisFieldDataEDI())){
				return false;
			}
			if (!Compare.isEqualOrNull(getVolumeBases(), ip.getVolumeBases())){
				return false;
			}
			if (!Compare.isEqualOrNull(getMembraneBases(), ip.getMembraneBases())){
				return false;
			}
			if (!Compare.isEqualOrNull(getFieldMathModel(), ip.getFieldMathModel())){
				return false;
			}
			return true;
		}
		return false;
	}

	public static MathModel createFieldMathModel(String name, InverseProblem inverseProblem) throws PropertyVetoException, ExpressionException, MathException {
		MathModel mathModel = new MathModel(null);
		MathDescription mathDesc = mathModel.getMathDescription();
		Geometry geometry = createBasisGeometry(inverseProblem);
		mathDesc.setGeometry(geometry);
		try {
			if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions() == null){
				geometry.getGeometrySurfaceDescription().updateAll();
			}
		}catch (cbit.image.ImageException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
		}catch (cbit.vcell.geometry.GeometryException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("Geometric surface generation error: \n"+e.getMessage());
		}

		SubVolume subVolumes[] = geometry.getGeometrySpec().getSubVolumes();
		for (int i=0;i<subVolumes.length;i++){
			CompartmentSubDomain subDomain = new cbit.vcell.math.CompartmentSubDomain(subVolumes[i].getName(),subVolumes[i].getHandle());
			mathDesc.addSubDomain(subDomain);

			if (inverseProblem.isVolumeSourceDefined(subVolumes[i])){
				VolVariable volVar = new VolVariable(InverseProblemContants.VAR_PREFIX+subDomain.getName(),null);
				mathDesc.addVariable(volVar);
				PdeEquation pde = new PdeEquation(volVar,false);
				pde.setDiffusionExpression(new Expression(1.0));
				pde.setInitialExpression(new Expression(0.0));
				pde.setRateExpression(new Expression("-"+volVar.getName()));
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
				SubVolume subvolume1 = ((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[0]).getSubVolume();
				SubVolume subvolume2 = ((VolumeGeometricRegion)surfaceRegion.getAdjacentGeometricRegions()[1]).getSubVolume();
				CompartmentSubDomain compartment1 = mathDesc.getCompartmentSubDomain(subvolume1.getName());
				CompartmentSubDomain compartment2 = mathDesc.getCompartmentSubDomain(subvolume2.getName());
				MembraneSubDomain membraneSubDomain = mathDesc.getMembraneSubDomain(compartment1,compartment2);
				if (membraneSubDomain==null){
					membraneSubDomain = new MembraneSubDomain(compartment1,compartment2);
					mathDesc.addSubDomain(membraneSubDomain);
			
					boolean[] fluxDefined = inverseProblem.isMembraneFluxesDefined(subvolume1, subvolume2);
					if (inverseProblem.isVolumeSourceDefined(subvolume1)){
						VolVariable volVar1 = (VolVariable)mathDesc.getVariable(InverseProblemContants.VAR_PREFIX+compartment1.getName());
						JumpCondition jumpCondition1 = new JumpCondition(volVar1);
						if (fluxDefined[0]){
							jumpCondition1.setInFlux(new Expression(1.0));
						}
						membraneSubDomain.addJumpCondition(jumpCondition1);
					}
					
					if (inverseProblem.isVolumeSourceDefined(subvolume2)){
						VolVariable volVar2 = (VolVariable)mathDesc.getVariable(InverseProblemContants.VAR_PREFIX+compartment2.getName());
						JumpCondition jumpCondition2 = new JumpCondition(volVar2);
						if (fluxDefined[0]){
							jumpCondition2.setOutFlux(new Expression(1.0));
						}
						membraneSubDomain.addJumpCondition(jumpCondition2);
					}
				}
			}
		}
		if (!mathDesc.isValid()){
			throw new RuntimeException("math is not valid, problem: "+mathDesc.getWarning());
		}
		Simulation sim = mathModel.addNewSimulation("mysim");
		sim.getSolverTaskDescription().setTimeStep(new TimeStep(1,1,1));
		int keepEvery = 1;
		sim.getSolverTaskDescription().setOutputTimeSpec(new DefaultOutputTimeSpec(keepEvery));
		sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0,10));
		sim.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
		VCImage geoImage = geometry.getGeometrySpec().getImage();
		sim.getMeshSpecification().setSamplingSize(new ISize(geoImage.getNumX(),geoImage.getNumY(),geoImage.getNumZ()));
		mathModel.setName(name);
		return mathModel;
	}

	public static Geometry createBasisGeometry(InverseProblem inverseProblem) {
		try {
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
			ROIImage protocolROIImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_PROTOCOL);
			if (protocolROIImage==null){
				throw new RuntimeException("zStackImageData 'Protocol' roi is missing");
			}
			
			//
			// merge segmentation and protocol ROIImages to make geometry with extracellular/cell/bleached subvolumes
			//
			ROIImage combinedROIImage = new ROIImage(segROIImage);
			if (protocolROIImage.getROI(ROIImage.ROI_BLEACHED).countPixels()>0){
				short unusedROIPixelValue = 0;
				while (combinedROIImage.getROI(unusedROIPixelValue)!=null){
					unusedROIPixelValue++;
				}
				ROIImageComponent cellROI = combinedROIImage.getROI(ROIImage.ROI_CELL);
				ROIImageComponent bleachedROI = protocolROIImage.getROI(ROIImage.ROI_BLEACHED);
				ROIImageComponent combinedBleachedROI = combinedROIImage.addROI(unusedROIPixelValue, ROIImage.ROI_BLEACHEDCELL);
				int imageSize = combinedROIImage.getMaskImage().getNumXYZ();
				for (int i = 0; i < imageSize; i++) {
					if (cellROI.isPixelInside(i) && bleachedROI.isPixelInside(i)){
						combinedBleachedROI.setPixel(i);
					}
				}
			}
			int numX = combinedROIImage.getISize().getX();
			int numY = combinedROIImage.getISize().getY();
			int numZ = combinedROIImage.getISize().getZ();
			short[] shortSegPixels = combinedROIImage.getMaskImage().getPixels();
			byte[] bytePixels = new byte[shortSegPixels.length];
			for (int i = 0; i < bytePixels.length; i++) {
				bytePixels[i] = (byte)shortSegPixels[i];
			}
			/*
			 * sample code to see conversion between byte and int (considering that both are signed and overflow can mess things up in a round-trip
			 * int i = 200;
			 * byte b = (byte)i;
			 * int i2 = ((int)b)&0x00ff;
			 * System.out.println("i="+i+", b="+b+", i2="+i2);
			 *
			 */
			VCImageUncompressed vcImage = new VCImageUncompressed(null,bytePixels,combinedROIImage.getMaskImage().getExtent(),numX,numY,numZ);
			int[] uniquePixelValues = vcImage.getUniquePixelValues();
			Geometry geometry = new Geometry("geo",vcImage);
			vcImage.setName("vcImage");
			geometry.getGeometrySpec().setExtent(combinedROIImage.getMaskImage().getExtent());
			geometry.getGeometrySpec().setOrigin(combinedROIImage.getMaskImage().getOrigin());
			VCPixelClass[] pixelsClasses = vcImage.getPixelClasses();
			SubVolume[] subVolumes = new ImageSubVolume[uniquePixelValues.length];
			ROIImageComponent cellROI = combinedROIImage.getROI(ROIImage.ROI_CELL);
			ROIImageComponent ecROI = combinedROIImage.getROI(ROIImage.ROI_EXTRACELLULAR);
			ROIImageComponent bleachedcellROI = combinedROIImage.getROI(ROIImage.ROI_BLEACHEDCELL);
			for (int i = 0; i < subVolumes.length; i++) {
				if (pixelsClasses[i].getPixel()==cellROI.getPixelValue()){
					subVolumes[i] = new ImageSubVolume(null,new VCPixelClass(null,cellROI.getName(),cellROI.getPixelValue()),i);
				}else if (pixelsClasses[i].getPixel()==ecROI.getPixelValue()){
					subVolumes[i] = new ImageSubVolume(null,new VCPixelClass(null,ecROI.getName(),ecROI.getPixelValue()),i);
				}else if (pixelsClasses[i].getPixel()==bleachedcellROI.getPixelValue()){
					subVolumes[i] = new ImageSubVolume(null,new VCPixelClass(null,bleachedcellROI.getName(),bleachedcellROI.getPixelValue()),i);
				}else{
					throw new RuntimeException("unexpected ROI pixel value "+pixelsClasses[i].getPixel());
				}
			}
			geometry.getGeometrySpec().setSubVolumes(subVolumes);
			return geometry;
		}catch (PropertyVetoException ex){
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}catch (ImageException ex2){
			ex2.printStackTrace(System.out);
			throw new RuntimeException(ex2.getMessage());
		}
	}

	public void calculateBasis(final InversePDERequestManager inversePdeRequestManager, InverseProblem inverseProblem) throws MathException, DataAccessException, IOException, ImageException, PropertyVetoException, ExpressionException{
		if (inverseProblem==null){
			throw new RuntimeException("inverse problem is null");
		}
		if (inverseProblem.getMicroscopyData().getZStackImageData()==null){
			throw new RuntimeException("zstack is null");
		}
		if (inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_BASIS)==null){
			throw new RuntimeException("Basis ROI missing from zStackImage");
		}
		
		//
		// construct/save/run a custom math model to construct scalar fields used in 3D basis determination (e.g. in Segmentation3D_)
		//
		DocumentManager documentManager = inversePdeRequestManager.getDocumentManager();
		MathModelInfo[] mathModelInfos = documentManager.getMathModelInfos();
		String mathModelName = "tempMath";
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
		MathModel mathModel = createFieldMathModel(mathModelName,inverseProblem);
		System.out.println(mathModel.getMathDescription().getVCML_database());
		MathModel savedMathModel = documentManager.save(mathModel, null);
		final Simulation sim = savedMathModel.getSimulations()[0];
		OutputFunctionContext outputFunctionContext = savedMathModel.getOutputFunctionContext();
		OutputContext outputContext = new OutputContext(outputFunctionContext.getOutputFunctionsList().toArray(new AnnotatedFunction[0]));
		final VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(sim.getVersion().getVersionKey(),inversePdeRequestManager.getUser());
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("starting simulation");
					inversePdeRequestManager.startSimulation(vcSimID,sim.getMathOverrides().getScanCount());
				} catch (DataAccessException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		SimulationJobStatusPersistent status = null;
		do {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			//status = clientServerManager.getJobManager().getServerSimulationStatus(vcSimID).getJobStatus(0);
			status = inversePdeRequestManager.getSimulationStatus(sim.getVersion().getVersionKey()).getJobStatus(0);
			System.out.println("polling simulation, status = "+status.getSimulationExecutionStatus());
		} while (!status.getSchedulerStatus().isDone());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
		}
		if (status.getSchedulerStatus().isFailed()){
			throw new RuntimeException("solverFailed: "+status.getSimulationMessage().getDisplayMessage());
		}
		//
		// read simulation results (solution is the "field" used for iso-surfaces ... also, the larger the field, the smaller the basis element).
		//
		System.out.println("reading results");
		VCSimulationDataIdentifier vcdataID = new VCSimulationDataIdentifier(vcSimID,0);
		Enumeration<Variable> vars = savedMathModel.getMathDescription().getVariables();
		ArrayList<VolVariable> volVarList = new ArrayList<VolVariable>();
		while (vars.hasMoreElements()){
			Variable var = vars.nextElement();
			if (var instanceof VolVariable){
				volVarList.add((VolVariable)var);
			}
		}
		VolVariable[] volVars = volVarList.toArray(new VolVariable[volVarList.size()]);

		//
		// delete mathModel
		//
//		System.out.println("deleting math model");
//		clientServerManager.getDocumentManager().delete(clientServerManager.getDocumentManager().getMathModelInfo(savedMathModel.getVersion().getVersionKey()));

		System.out.println("analyzing results");
		//
		// analyze each domain's decomposition separately
		//
		CartesianMesh mesh = inversePdeRequestManager.getMesh(vcdataID);
		double[] simulationTimes = inversePdeRequestManager.getDataSetTimes(vcdataID);
		int numX = sim.getMeshSpecification().getSamplingSize().getX();
		int numY = sim.getMeshSpecification().getSamplingSize().getY();
		int numZ = sim.getMeshSpecification().getSamplingSize().getZ();
		float percent = 0.15f; // 0.04f;
		int levels = 3;
					
		int[] globalSegmentation = new int[numX*numY*numZ];
		Vector<Integer> collocationPointVector = new Vector<Integer>();
		int STARTING_OFFSET = 1;
		int  globalSegmentationOffset = STARTING_OFFSET; // for visibility in matlab ... 

		
		ArrayList<VolumeBasis> volumeBasisList = new ArrayList<VolumeBasis>();
		for (int i = 0; i < volVars.length; i++) {
			String compartmentName = volVars[i].getName().substring(InverseProblemContants.VAR_PREFIX.length());
			if (compartmentName.equals(ROIImage.ROI_BLEACHEDCELL)){
				percent = 0.2f;
				levels = 2;
			}else if (compartmentName.equals(ROIImage.ROI_CELL)){
				percent = 0.2f;
				levels = 4;
			}else if (compartmentName.equals(ROIImage.ROI_EXTRACELLULAR)){
				percent = 0.1f;
				levels = 3;
			}
			SimDataBlock simDataBlock = inversePdeRequestManager.getSimDataBlock(outputContext, vcdataID, volVars[i].getName(), simulationTimes[simulationTimes.length-1]);
			double[] data = simDataBlock.getData();
			//
			// transform data into more appropriate field
			//
			double[] newData = new double[data.length];
			double minData = Double.MAX_VALUE;
			double maxData = -Double.MAX_VALUE;
			//
			// take log - scales data better ... but needs normalization
			//
			for (int j = 0; j < newData.length; j++) {
				if (data[j]>0){
					newData[j] = Math.log(Math.max(0.001,data[j]));
					minData = Math.min(minData,newData[j]);
					maxData = Math.max(maxData,newData[j]);
				}
			}
			//
			// scale to fit to [0,1].
			//
			for (int j = 0; j < newData.length; j++) {
				if (data[j]>0){
					newData[j] = 0.1 + (newData[j]-minData)*0.9/(maxData-minData);
				}
			}
			data = newData;
			
			float[] floatPixels = new float[data.length];
			for (int j = 0; j < floatPixels.length; j++) {
				floatPixels[j] = (float)data[j];
			}
			FloatImage originalImage = new FloatImage(floatPixels,mesh.getOrigin(),mesh.getExtent(),numX,numY,numZ);
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
					int mediod_index = segmentCenterArray.get(k).index;
					int mediod_compartment = segmentCenterArray.get(k).compartmentId;
					int globalImageValue = k + globalSegmentationOffset;
					for (int j = 0; j < segmentation.length; j++) {
						if (originalImage.getPixels()[j]!=0.0f && segmentation[j]==mediod_compartment){
							globalSegmentation[j] = globalImageValue;
						}
					}
					collocationPointVector.add(mediod_index);
					
					VolumeBasis volumeBasis = new VolumeBasis();
					volumeBasis.setBasisImageValue(globalImageValue);
					volumeBasis.setControlPointIndex(mediod_index);
					volumeBasis.setSubvolumeName(compartmentName);
					volumeBasis.setName(compartmentName+"_"+k);
					volumeBasis.setControlPointCoord(mesh.getCoordinateFromVolumeIndex(mediod_index));
					volumeBasis.setBasisIndex(volumeBasisList.size());
					if (compartmentName.equals(ROIImage.ROI_BLEACHEDCELL)){
						volumeBasis.setSubvolumeName(ROIImage.ROI_CELL);
						volumeBasis.setWithinBleach(true);
					}else{
						volumeBasis.setWithinBleach(false);
					}
					volumeBasisList.add(volumeBasis);
					
				}
				globalSegmentationOffset += segmentCenterArray.size();
				
//short[] pixels = new short[globalSegmentation.length];
//for (int j = 0; j < globalSegmentation.length; j++) {
//pixels[j] = (short)globalSegmentation[j];
//}
//displayImage(pixels, numX, numY, numZ, "segmentationImage for volVar "+volVars[i].getName());
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
		
//short[] pixels = new short[globalSegmentation.length];
//for (int j = 0; j < globalSegmentation.length; j++) {
//pixels[j] = (short)globalSegmentation[j];
//}
//displayImage(pixels, numX, numY, numZ, "final segmentationImage");
		ROIImage roiImage = inverseProblem.getMicroscopyData().getZStackImageData().getROIImage(ROIImage.ROIIMAGE_BASIS);
		ROIImageComponent[] oldROIs = roiImage.getROIs();
		for (int i = 0; i < oldROIs.length; i++) {
			roiImage.removeROI(oldROIs[i]);
		}
		short[] roiPixels = roiImage.getMaskImage().getPixels();
		for (int i = 0; i < globalSegmentation.length; i++) {
			roiPixels[i] = (short)globalSegmentation[i];
		}
		for (int i = STARTING_OFFSET; i < globalSegmentationOffset; i++) {
			roiImage.addROI((short)i, "bases"+i);
		}

		setVolumeBases(volumeBasisList.toArray(new VolumeBasis[volumeBasisList.size()]));
		
		MembraneElement[] membraneElements = mesh.getMembraneElements();
		ArrayList<MembraneBasis> membraneBases = new ArrayList<MembraneBasis>();
		HashMap<MembraneBasis, ArrayList<MembraneElement>> membraneBasisElements = new HashMap<MembraneBasis, ArrayList<MembraneElement>>();
		for (int i = 0; i < membraneElements.length; i++) {
			int insideVolumeIndex = membraneElements[i].getInsideVolumeIndex();
			int outsideVolumeIndex = membraneElements[i].getOutsideVolumeIndex();
			int volumeBasisImageValue = globalSegmentation[insideVolumeIndex];
			VolumeBasis adjacentVolumeBasis = getVolumeBasesByImageValue(volumeBasisImageValue);
			MembraneBasis existingMembraneBasis = null;
			for (int j = 0; j < membraneBases.size(); j++) {
				if (membraneBases.get(j).getAdjacentInsideVolumeBases()==adjacentVolumeBasis){
					existingMembraneBasis = membraneBases.get(j);
					break;
				}
			}
			if (existingMembraneBasis==null){
				MembraneBasis membraneBasis = new MembraneBasis();
				membraneBasis.setAdjacentInsideVolumeBases(adjacentVolumeBasis);
				membraneBasis.setBasisIndex(membraneBases.size());
				SubVolume insideSV = sim.getMathDescription().getGeometry().getGeometrySpec().getSubVolumes(mesh.getSubVolumeFromVolumeIndex(insideVolumeIndex));
				SubVolume outsideSV = sim.getMathDescription().getGeometry().getGeometrySpec().getSubVolumes(mesh.getSubVolumeFromVolumeIndex(outsideVolumeIndex));
				membraneBasis.setInsideSubvolumeName(insideSV.getName());
				membraneBasis.setOutsideSubvolumeName(outsideSV.getName());
				membraneBasis.setName(outsideSV.getName()+"_"+insideSV.getName()+"_"+membraneBases.size());
				if ((membraneBasis.getOutsideSubvolumeName().equals(ROIImage.ROI_CELL) &&
					membraneBasis.getInsideSubvolumeName().equals(ROIImage.ROI_BLEACHEDCELL)) ||
					(membraneBasis.getOutsideSubvolumeName().equals(ROIImage.ROI_CELL) &&
					membraneBasis.getInsideSubvolumeName().equals(ROIImage.ROI_BLEACHEDCELL))){
					// skip
				}else{
					membraneBases.add(membraneBasis);
					membraneBasisElements.put(membraneBasis, new ArrayList<MembraneElement>());
					existingMembraneBasis = membraneBasis;
				}
			}
			
			if (existingMembraneBasis!=null){
				ArrayList<MembraneElement> memElementList = membraneBasisElements.get(existingMembraneBasis);
				memElementList.add(membraneElements[i]);
			}
		}
		//
		// for each MembraneBasis, calculate the centroid and find membraneElementIndex that is closest to the center
		// then save it's index and Coordinate
		//
		for (int i = 0; i < membraneBases.size(); i++) {
			ArrayList<MembraneElement> memElementList = membraneBasisElements.get(membraneBases.get(i));
			double sumX = 0;
			double sumY = 0;
			double sumZ = 0;
			for (int j = 0; j < memElementList.size(); j++) {
				Coordinate coord = memElementList.get(j).getCentroid();
				sumX += coord.getX();
				sumY += coord.getY();
				sumZ += coord.getZ();
			}
			Coordinate averageCoord = new Coordinate(sumX/memElementList.size(),sumY/memElementList.size(),sumZ/memElementList.size());
			MembraneElement closestMembraneElement = null;
			double closestDistance = Double.MAX_VALUE;
			for (int j = 0; j < memElementList.size(); j++) {
				double distance = memElementList.get(j).getCentroid().distanceTo(averageCoord);
				if (distance < closestDistance){
					closestMembraneElement = memElementList.get(j);
					closestDistance = distance;
				}
			}
			membraneBases.get(i).setControlPointIndex(closestMembraneElement.getMembraneIndex());
			membraneBases.get(i).setControlPointCoord(closestMembraneElement.getCentroid());
		}
		setMembraneBases(membraneBases.toArray(new MembraneBasis[membraneBases.size()]));
		System.out.println("finished");
	}


}
