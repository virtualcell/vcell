package org.vcell.vmicro.workflow;

import java.math.BigDecimal;
import java.util.Date;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionFlag;
import org.vcell.vmicro.workflow.scratch.FRAPOptimizationUtils;
import org.vcell.vmicro.workflow.scratch.FRAPStudy;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.VariableType;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;

public class Generate2DSimBioModel extends Task {
	
	//
	// inputs
	//
	public final DataInput<Extent> extent;
	public final DataInput<ROI> cellROI_2D;
	public final DataInput<double[]> timeStamps;
	public final DataInput<Integer> indexFirstPostbleach;
	public final DataInput<Double> primaryDiffusionRate;
	public final DataInput<Double> primaryFraction;
	public final DataInput<Double> bleachMonitorRate;
	public final DataInput<Double> secondaryDiffusionRate;
	public final DataInput<Double> secondaryFraction;
	public final DataInput<Double> bindingSiteConcentration;
	public final DataInput<Double> bindingOnRate;
	public final DataInput<Double> bindingOffRate;
	public final DataInput<String> extracellularName;
	public final DataInput<String> cytosolName;
	public final DataInput<User> owner;
	public final DataInput<KeyValue> simKey;
	//
	// outputs
	//
	public final DataHolder<BioModel> bioModel_2D;
	

	public Generate2DSimBioModel(String id){
		super(id);
		extent = new DataInput<Extent>(Extent.class,"extent", this);
		cellROI_2D = new DataInput<ROI>(ROI.class,"cellROI_2D",this);
		timeStamps = new DataInput<double[]>(double[].class,"timeStamps",this);
		indexFirstPostbleach = new DataInput<Integer>(Integer.class,"indexFirstPostbleach",this);
		primaryDiffusionRate = new DataInput<Double>(Double.class,"primaryDiffusionRate",this);
		primaryFraction = new DataInput<Double>(Double.class,"primaryFraction", this);
		bleachMonitorRate = new DataInput<Double>(Double.class,"bleachMonitorRate", this);
		secondaryDiffusionRate = new DataInput<Double>(Double.class,"secondaryDiffusionRate", this, true);	
		secondaryFraction = new DataInput<Double>(Double.class,"secondaryFraction", this, true);
		bindingSiteConcentration = new DataInput<Double>(Double.class,"bindingSiteConcentration", this, true);
		bindingOnRate = new DataInput<Double>(Double.class,"bindingOnRate", this, true);
		bindingOffRate = new DataInput<Double>(Double.class,"bindingOffRate", this, true);
		extracellularName = new DataInput<String>(String.class,"extracellularName", this);
		cytosolName = new DataInput<String>(String.class,"cytosolName", this);
		owner = new DataInput<User>(User.class,"owner",this);
		simKey = new DataInput<KeyValue>(KeyValue.class,"simKey", this);
		bioModel_2D = new DataHolder<BioModel>(BioModel.class,"bioModel_2D",this);
		addInput(extent);
		addInput(cellROI_2D);
		addInput(timeStamps);
		addInput(indexFirstPostbleach);
		addInput(primaryDiffusionRate);
		addInput(primaryFraction);
		addInput(bleachMonitorRate);
		addInput(secondaryDiffusionRate);
		addInput(secondaryFraction);
		addInput(bindingSiteConcentration);
		addInput(bindingOnRate);
		addInput(bindingOffRate);
		addInput(extracellularName);
		addInput(cytosolName);
		addInput(owner);
		addInput(simKey);
		addOutput(bioModel_2D);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		if (owner == null){
			throw new Exception("Owner is not defined");
		}
		
		double df = primaryDiffusionRate.getData();
		double ff = primaryFraction.getData();
		double bwmRate = bleachMonitorRate.getData();
		double dc = 0.0;
		double fc = 0.0;
		if (secondaryDiffusionRate.getSource()!=null){
			dc = secondaryDiffusionRate.getData();
			fc = secondaryFraction.getData();
		}
		double bs = 0.0;
		double onRate = 0.0;
		double offRate = 0.0;
		if (bindingSiteConcentration.getSource()!=null){
			bs = bindingSiteConcentration.getData();
			onRate = bindingOnRate.getData();
			offRate = bindingOffRate.getData();
		}		
		//immobile fraction
		double fimm = 1-ff-fc;
		if(fimm < FRAPOptimizationUtils.epsilon && fimm > (0 - FRAPOptimizationUtils.epsilon))
		{
			fimm = 0;
		}
		if(fimm < (1+FRAPOptimizationUtils.epsilon) && fimm > (1 - FRAPOptimizationUtils.epsilon))
		{
			fimm = 1;
		}

		Extent extent = this.extent.getData();
		double[] timeStamps = this.timeStamps.getData();
		int startingIndexForRecovery = this.indexFirstPostbleach.getData();
		
		TimeBounds timeBounds = new TimeBounds(0.0,timeStamps[timeStamps.length-1]-timeStamps[startingIndexForRecovery]);
		double timeStepVal = timeStamps[startingIndexForRecovery+1] - timeStamps[startingIndexForRecovery];
		
		ROI cellROI = cellROI_2D.getData();
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
			throw new Exception("Cell ROI has no ExtraCellular.");
		}
		
		String EXTRACELLULAR_NAME = extracellularName.getData();
		String CYTOSOL_NAME = cytosolName.getData();
		
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
		

		String roiDataName = FRAPStudy.ROI_EXTDATA_NAME;
		
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
		species[FREE_SPECIES_INDEX] = new Species(FRAPStudy.SPECIES_NAME_PREFIX_MOBILE,	"Mobile bleachable species");
		speciesContexts[FREE_SPECIES_INDEX] = new SpeciesContext(null,species[FREE_SPECIES_INDEX].getCommonName(),species[FREE_SPECIES_INDEX],cytosol);
		initialConditions[FREE_SPECIES_INDEX] = Expression.mult(new Expression(ff), totalIniCondition);
		
		//Immobile Species (No diffusion)
		//Set very small diffusion rate on immobile to force evaluation as state variable (instead of FieldData function)
		//If left as a function errors occur because functions involving FieldData require a database connection
		final String IMMOBILE_DIFFUSION_KLUDGE = "1e-14";
		diffusionConstants[IMMOBILE_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[IMMOBILE_SPECIES_INDEX] = new Species(FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE,"Immobile bleachable species");
		speciesContexts[IMMOBILE_SPECIES_INDEX] = new SpeciesContext(null,species[IMMOBILE_SPECIES_INDEX].getCommonName(),species[IMMOBILE_SPECIES_INDEX],cytosol);
		initialConditions[IMMOBILE_SPECIES_INDEX] = Expression.mult(new Expression(fimm), totalIniCondition);

		//BS Species
		diffusionConstants[BS_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[BS_SPECIES_INDEX] = new Species(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE,"Binding Site species");
		speciesContexts[BS_SPECIES_INDEX] = new SpeciesContext(null,species[BS_SPECIES_INDEX].getCommonName(),species[BS_SPECIES_INDEX],cytosol);
		initialConditions[BS_SPECIES_INDEX] = Expression.mult(new Expression(bs), totalIniCondition);
	
		//Complex species
		diffusionConstants[COMPLEX_SPECIES_INDEX] = new Expression(dc);
		species[COMPLEX_SPECIES_INDEX] = new Species(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE, "Slower mobile bleachable species");
		speciesContexts[COMPLEX_SPECIES_INDEX] = new SpeciesContext(null,species[COMPLEX_SPECIES_INDEX].getCommonName(),species[COMPLEX_SPECIES_INDEX],cytosol);
		initialConditions[COMPLEX_SPECIES_INDEX] = Expression.mult(new Expression(fc), totalIniCondition);
	
		// add reactions to species if there is bleachWhileMonitoring rate.
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
			//reaction with BMW rate, which should not be applied to binding site
			if(!(species[i].getCommonName().equals(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE)))
			{
				SimpleReaction simpleReaction = new SimpleReaction(model, cytosol,speciesContexts[i].getName()+"_bleach");
				model.addReactionStep(simpleReaction);
				simpleReaction.addReactant(speciesContexts[i], 1);
				MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction);
				simpleReaction.setKinetics(massActionKinetics);
				KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
				simpleReaction.getKinetics().setParameterValue(kforward, new Expression(new Double(bwmRate)));
			}
		}

		// add the binding reaction: F + BS <-> C
		SimpleReaction simpleReaction2 = new SimpleReaction(model, cytosol,"reac_binding");
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
//			Membrane plasmaMembrane = model.getStructureTopology().getMembrane(cytosol, extracellular);
//			MembraneMapping plasmaMembraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(plasmaMembrane);
		
		SubVolume cytSubVolume = geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME);
		SubVolume exSubVolume = geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME);
		SurfaceClass pmSurfaceClass = geometry.getGeometrySurfaceDescription().getSurfaceClass(exSubVolume, cytSubVolume);
		
		cytosolFeatureMapping.setGeometryClass(cytSubVolume);
		extracellularFeatureMapping.setGeometryClass(exSubVolume);
//			plasmaMembraneMapping.setGeometryClass(pmSurfaceClass);
		
		cytosolFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		extracellularFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
//			plasmaMembraneMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}

		MathMapping mathMapping = simContext.createNewMathMapping();
		MathDescription mathDesc = mathMapping.getMathDescription();
		//Add total fluorescence as function of mobile(optional: and slower mobile) and immobile fractions
		mathDesc.addVariable(new Function(FRAPStudy.SPECIES_NAME_PREFIX_COMBINED,
					             new Expression(species[FREE_SPECIES_INDEX].getCommonName()+"+"+species[COMPLEX_SPECIES_INDEX].getCommonName()+"+"+species[IMMOBILE_SPECIES_INDEX].getCommonName()), null));
		simContext.setMathDescription(mathDesc);

		SimulationVersion simVersion = new SimulationVersion(simKey.getData(),"sim1",owner.getData(),new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,mathDesc);
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getMeshSpecification().setSamplingSize(cellROI.getISize());
//			newSimulation.getSolverTaskDescription().setTimeStep(timeStep); // Sundials doesn't need time step
		newSimulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
		newSimulation.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(timeStepVal));//use exp time step as output time spec
		
		this.bioModel_2D.setData(bioModel);
	}

}
