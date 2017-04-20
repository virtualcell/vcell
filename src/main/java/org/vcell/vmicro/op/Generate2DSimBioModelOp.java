package org.vcell.vmicro.op;

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.Date;

import org.vcell.util.Extent;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionFlag;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;

public class Generate2DSimBioModelOp {
	private static final String SPECIES_NAME_PREFIX_MOBILE = "fluor_primary_mobile"; 
	private static final String SPECIES_NAME_PREFIX_SLOW_MOBILE = "fluor_secondary_mobile";
	private static final String SPECIES_NAME_PREFIX_BINDING_SITE = "binding_site";
	private static final String SPECIES_NAME_PREFIX_IMMOBILE = "fluor_immobile"; 
	private static final String SPECIES_NAME_PREFIX_COMBINED = "fluor_combined"; 
	private static double epsilon = 1e-8;
	
	private static final String ROI_EXTDATA_NAME = "roiData";

	public BioModel generateBioModel(	
			Extent extent,
			ROI cellROI_2D,
			double[] timeStamps,
			Integer indexFirstPostbleach,
			double primaryDiffusionRate,
			double primaryFraction,
			double bleachMonitorRate,
			Double secondaryDiffusionRate,
			double secondaryFraction,
			Double bindingSiteConcentration,
			double bindingOnRate,
			double bindingOffRate,
			String extracellularName,
			String cytosolName,
			User owner,
			KeyValue simKey) throws PropertyVetoException, ExpressionException, ModelException, GeometryException, ImageException, MappingException, MathException, MatrixException {

		if (owner == null){
			throw new IllegalArgumentException("Owner is not defined");
		}
		
		double df = primaryDiffusionRate;
		double ff = primaryFraction;
		double bwmRate = bleachMonitorRate;
		double dc = 0.0;
		double fc = 0.0;
		if (secondaryDiffusionRate != null){
			dc = secondaryDiffusionRate;
			fc = secondaryFraction;
		}
		double bs = 0.0;
		double onRate = 0.0;
		double offRate = 0.0;
		if (bindingSiteConcentration != null){
			bs = bindingSiteConcentration;
			onRate = bindingOnRate;
			offRate = bindingOffRate;
		}		
		//immobile fraction
		double fimm = 1-ff-fc;
		if(fimm < epsilon && fimm > (0 - epsilon))
		{
			fimm = 0;
		}
		if(fimm < (1+epsilon) && fimm > (1 - epsilon))
		{
			fimm = 1;
		}

		int startingIndexForRecovery = indexFirstPostbleach;
		
		TimeBounds timeBounds = new TimeBounds(0.0,timeStamps[timeStamps.length-1]-timeStamps[startingIndexForRecovery]);
		double timeStepVal = timeStamps[startingIndexForRecovery+1] - timeStamps[startingIndexForRecovery];
		
		ROI cellROI = cellROI_2D;
		int numX = cellROI.getISize().getX();
		int numY = cellROI.getISize().getY();
		int numZ = cellROI.getISize().getZ();
		short[] shortPixels = cellROI.getRoiImages()[0].getPixels();
		byte[] bytePixels = new byte[numX*numY*numZ];
		final byte EXTRACELLULAR_PIXVAL = 0;
		final byte CYTOSOL_PIXVAL = 1;
		for (int i = 0; i < bytePixels.length; i++) {
			if (shortPixels[i]!=0){
				bytePixels[i] = CYTOSOL_PIXVAL;
			}
		}
		VCImage maskImage;
		try {
			maskImage = new VCImageUncompressed(null,bytePixels,extent,numX,numY,numZ);
		} catch (ImageException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create mask image for geometry");
		}
		Geometry geometry = new Geometry("geometry",maskImage);
		if(geometry.getGeometrySpec().getNumSubVolumes() != 2){
			throw new IllegalArgumentException("Cell ROI has no ExtraCellular.");
		}
		
		String EXTRACELLULAR_NAME = extracellularName;
		String CYTOSOL_NAME = cytosolName;
		
		int subVolume0PixVal = ((ImageSubVolume)geometry.getGeometrySpec().getSubVolume(0)).getPixelValue();
		geometry.getGeometrySpec().getSubVolume(0).setName((subVolume0PixVal == EXTRACELLULAR_PIXVAL?EXTRACELLULAR_NAME:CYTOSOL_NAME));
		int subVolume1PixVal = ((ImageSubVolume)geometry.getGeometrySpec().getSubVolume(1)).getPixelValue();
		geometry.getGeometrySpec().getSubVolume(1).setName((subVolume1PixVal == CYTOSOL_PIXVAL?CYTOSOL_NAME:EXTRACELLULAR_NAME));
		geometry.getGeometrySurfaceDescription().updateAll();

		BioModel bioModel = new BioModel(null);
		bioModel.setName("unnamed");
		Model model = new Model("model");
		bioModel.setModel(model);
		model.addFeature(EXTRACELLULAR_NAME);
		Feature extracellular = (Feature)model.getStructure(EXTRACELLULAR_NAME);
		model.addFeature(CYTOSOL_NAME);
		Feature cytosol = (Feature)model.getStructure(CYTOSOL_NAME);
//			Membrane mem = model.addMembrane(EXTRACELLULAR_CYTOSOL_MEM_NAME);
//			model.getStructureTopology().setInsideFeature(mem, cytosol);
//			model.getStructureTopology().setOutsideFeature(mem, extracellular);
		

		String roiDataName = ROI_EXTDATA_NAME;
		
		final int SPECIES_COUNT = 4;
		final int FREE_SPECIES_INDEX = 0;
		final int BS_SPECIES_INDEX = 1;
		final int COMPLEX_SPECIES_INDEX = 2;
		final int IMMOBILE_SPECIES_INDEX =3;
		
		Expression[] diffusionConstants = null;
		Species[] species = null;
		SpeciesContext[] speciesContexts = null;
		Expression[] initialConditions = null;
		
		
		diffusionConstants = new Expression[SPECIES_COUNT];
		species = new Species[SPECIES_COUNT];
		speciesContexts = new SpeciesContext[SPECIES_COUNT];
		initialConditions = new Expression[SPECIES_COUNT];
				
		//total initial condition
		FieldFunctionArguments postBleach_first = new FieldFunctionArguments(roiDataName,"postbleach_first", new Expression(0), VariableType.VOLUME);
		FieldFunctionArguments prebleach_avg = new FieldFunctionArguments(roiDataName,"prebleach_avg", new Expression(0), VariableType.VOLUME);
		Expression expPostBleach_first = new Expression(postBleach_first.infix());
		Expression expPreBleach_avg = new Expression(prebleach_avg.infix());
		Expression totalIniCondition = Expression.div(expPostBleach_first, expPreBleach_avg);
		//Free Species
		diffusionConstants[FREE_SPECIES_INDEX] = new Expression(df);
		species[FREE_SPECIES_INDEX] = new Species(SPECIES_NAME_PREFIX_MOBILE,	"Mobile bleachable species");
		speciesContexts[FREE_SPECIES_INDEX] = new SpeciesContext(null,species[FREE_SPECIES_INDEX].getCommonName(),species[FREE_SPECIES_INDEX],cytosol);
		initialConditions[FREE_SPECIES_INDEX] = Expression.mult(new Expression(ff), totalIniCondition);
		
		//Immobile Species (No diffusion)
		//Set very small diffusion rate on immobile to force evaluation as state variable (instead of FieldData function)
		//If left as a function errors occur because functions involving FieldData require a database connection
		final String IMMOBILE_DIFFUSION_KLUDGE = "1e-14";
		diffusionConstants[IMMOBILE_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[IMMOBILE_SPECIES_INDEX] = new Species(SPECIES_NAME_PREFIX_IMMOBILE,"Immobile bleachable species");
		speciesContexts[IMMOBILE_SPECIES_INDEX] = new SpeciesContext(null,species[IMMOBILE_SPECIES_INDEX].getCommonName(),species[IMMOBILE_SPECIES_INDEX],cytosol);
		initialConditions[IMMOBILE_SPECIES_INDEX] = Expression.mult(new Expression(fimm), totalIniCondition);

		//BS Species
		diffusionConstants[BS_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[BS_SPECIES_INDEX] = new Species(SPECIES_NAME_PREFIX_BINDING_SITE,"Binding Site species");
		speciesContexts[BS_SPECIES_INDEX] = new SpeciesContext(null,species[BS_SPECIES_INDEX].getCommonName(),species[BS_SPECIES_INDEX],cytosol);
		initialConditions[BS_SPECIES_INDEX] = Expression.mult(new Expression(bs), totalIniCondition);
	
		//Complex species
		diffusionConstants[COMPLEX_SPECIES_INDEX] = new Expression(dc);
		species[COMPLEX_SPECIES_INDEX] = new Species(SPECIES_NAME_PREFIX_SLOW_MOBILE, "Slower mobile bleachable species");
		speciesContexts[COMPLEX_SPECIES_INDEX] = new SpeciesContext(null,species[COMPLEX_SPECIES_INDEX].getCommonName(),species[COMPLEX_SPECIES_INDEX],cytosol);
		initialConditions[COMPLEX_SPECIES_INDEX] = Expression.mult(new Expression(fc), totalIniCondition);
	
		// add reactions to species if there is bleachWhileMonitoring rate.
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
			//reaction with BMW rate, which should not be applied to binding site
			if(!(species[i].getCommonName().equals(SPECIES_NAME_PREFIX_BINDING_SITE)))
			{
				SimpleReaction simpleReaction = new SimpleReaction(model, cytosol,speciesContexts[i].getName()+"_bleach", true);
				model.addReactionStep(simpleReaction);
				simpleReaction.addReactant(speciesContexts[i], 1);
				MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction);
				simpleReaction.setKinetics(massActionKinetics);
				KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
				simpleReaction.getKinetics().setParameterValue(kforward, new Expression(new Double(bwmRate)));
			}
		}

		// add the binding reaction: F + BS <-> C
		SimpleReaction simpleReaction2 = new SimpleReaction(model, cytosol,"reac_binding", true);
		model.addReactionStep(simpleReaction2);
		simpleReaction2.addReactant(speciesContexts[FREE_SPECIES_INDEX], 1);
		simpleReaction2.addReactant(speciesContexts[BS_SPECIES_INDEX], 1);
		simpleReaction2.addProduct(speciesContexts[COMPLEX_SPECIES_INDEX], 1);
		MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction2);
		simpleReaction2.setKinetics(massActionKinetics);
		KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
		KineticsParameter kreverse = massActionKinetics.getReverseRateParameter();
		simpleReaction2.getKinetics().setParameterValue(kforward, new Expression(new Double(onRate)));
		simpleReaction2.getKinetics().setParameterValue(kreverse, new Expression(new Double(offRate)));
		
		//create simulation context		
		SimulationContext simContext = new SimulationContext(bioModel.getModel(),geometry);
		
		bioModel.addSimulationContext(simContext);
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		
		SubVolume cytSubVolume = geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME);
		SubVolume exSubVolume = geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME);
		SurfaceClass pmSurfaceClass = geometry.getGeometrySurfaceDescription().getSurfaceClass(exSubVolume, cytSubVolume);
		
		cytosolFeatureMapping.setGeometryClass(cytSubVolume);
		extracellularFeatureMapping.setGeometryClass(exSubVolume);
		
		cytosolFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		extracellularFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}

		MathMapping mathMapping = simContext.createNewMathMapping();
		MathDescription mathDesc = mathMapping.getMathDescription();
		//Add total fluorescence as function of mobile(optional: and slower mobile) and immobile fractions
		mathDesc.addVariable(new Function(SPECIES_NAME_PREFIX_COMBINED,
					             new Expression(species[FREE_SPECIES_INDEX].getCommonName()+"+"+species[COMPLEX_SPECIES_INDEX].getCommonName()+"+"+species[IMMOBILE_SPECIES_INDEX].getCommonName()), null));
		simContext.setMathDescription(mathDesc);

		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,mathDesc);
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getMeshSpecification().setSamplingSize(cellROI.getISize());
//			newSimulation.getSolverTaskDescription().setTimeStep(timeStep); // Sundials doesn't need time step
		newSimulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
		newSimulation.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(timeStepVal));//use exp time step as output time spec
		
		return bioModel;
	}

}
