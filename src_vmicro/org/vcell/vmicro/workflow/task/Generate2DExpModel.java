package org.vcell.vmicro.workflow.task;

import java.math.BigDecimal;
import java.util.Date;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionFlag;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MicroscopeMeasurement.GaussianConvolutionKernel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.Feature;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;

public class Generate2DExpModel extends Task {
	
	//
	// inputs
	//
	public final DataInput<Double> deltaX;
	public final DataInput<Double> bleachRadius;
	public final DataInput<Double> cellRadius;
	public final DataInput<Double> bleachDuration;
	public final DataInput<Double> bleachRate;
	public final DataInput<Double> postbleachDelay;
	public final DataInput<Double> postbleachDuration;
	public final DataInput<Double> psfSigma;
	public final DataInput<Double> outputTimeStep;
	public final DataInput<Double> primaryDiffusionRate;
	public final DataInput<Double> primaryFraction;
	public final DataInput<Double> bleachMonitorRate;
	public final DataInput<Double> secondaryDiffusionRate;
	public final DataInput<Double> secondaryFraction;
	public final DataInput<String> extracellularName;
	public final DataInput<String> cytosolName;
	//
	// outputs
	//
	public final DataOutput<BioModel> bioModel_2D;
	public final DataOutput<Simulation> simulation_2D;
	public final DataOutput<Double> bleachBlackoutBeginTime;
	public final DataOutput<Double> bleachBlackoutEndTime;
	

	public Generate2DExpModel(String id){
		super(id);
		deltaX = new DataInput<Double>(Double.class,"deltaX", this);
		bleachRadius = new DataInput<Double>(Double.class,"bleachRadius",this);
		cellRadius = new DataInput<Double>(Double.class,"cellRadius",this);
		bleachDuration = new DataInput<Double>(Double.class,"bleachDuration",this);
		bleachRate = new DataInput<Double>(Double.class,"bleachRate",this);
		postbleachDelay = new DataInput<Double>(Double.class,"postbleachDelay",this);
		postbleachDuration = new DataInput<Double>(Double.class,"postbleachDuration",this);
		psfSigma = new DataInput<Double>(Double.class,"psfSigma",this);
		outputTimeStep = new DataInput<Double>(Double.class,"outputTimeStep",this);
		primaryDiffusionRate = new DataInput<Double>(Double.class,"primaryDiffusionRate",this);
		primaryFraction = new DataInput<Double>(Double.class,"primaryFraction", this);
		bleachMonitorRate = new DataInput<Double>(Double.class,"bleachMonitorRate", this);
		secondaryDiffusionRate = new DataInput<Double>(Double.class,"secondaryDiffusionRate", this, true);	
		secondaryFraction = new DataInput<Double>(Double.class,"secondaryFraction", this, true);
		extracellularName = new DataInput<String>(String.class,"extracellularName", this);
		cytosolName = new DataInput<String>(String.class,"cytosolName", this);
		addInput(deltaX);
		addInput(bleachRadius);
		addInput(cellRadius);
		addInput(bleachDuration);
		addInput(bleachRate);
		addInput(postbleachDelay);
		addInput(postbleachDuration);
		addInput(psfSigma);
		addInput(outputTimeStep);
		addInput(primaryDiffusionRate);
		addInput(primaryFraction);
		addInput(bleachMonitorRate);
		addInput(secondaryDiffusionRate);
		addInput(secondaryFraction);
		addInput(extracellularName);
		addInput(cytosolName);
		
		bioModel_2D = new DataOutput<BioModel>(BioModel.class,"bioModel_2D",this);
		simulation_2D = new DataOutput<Simulation>(Simulation.class,"simulation_2D",this);
		bleachBlackoutBeginTime = new DataOutput<Double>(Double.class,"bleachBlackoutBeginTime",this);
		bleachBlackoutEndTime = new DataOutput<Double>(Double.class,"bleachBlackoutEndTime",this);
		addOutput(bioModel_2D);
		addOutput(simulation_2D);
		addOutput(bleachBlackoutBeginTime);
		addOutput(bleachBlackoutEndTime);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		double domainSize = 2.2*context.getData(cellRadius);
		Extent extent = new Extent(domainSize, domainSize, 1.0);
		Origin origin = new Origin(-extent.getX()/2.0, -extent.getY()/2.0, -extent.getZ()/2.0);
		
		String EXTRACELLULAR_NAME = context.getData(extracellularName);
		String CYTOSOL_NAME = context.getData(cytosolName);

		AnalyticSubVolume cytosolSubVolume = new AnalyticSubVolume(CYTOSOL_NAME,new Expression("pow(x,2)+pow(y,2)<pow("+context.getData(cellRadius)+",2)"));
		AnalyticSubVolume extracellularSubVolume = new AnalyticSubVolume(EXTRACELLULAR_NAME,new Expression(1.0));
		Geometry geometry = new Geometry("geometry",2);
		geometry.getGeometrySpec().setExtent(extent);
		geometry.getGeometrySpec().setOrigin(origin);
		geometry.getGeometrySpec().addSubVolume(extracellularSubVolume);
		geometry.getGeometrySpec().addSubVolume(cytosolSubVolume, true);
		geometry.getGeometrySurfaceDescription().updateAll();

		BioModel bioModel = new BioModel(null);
		bioModel.setName("unnamed");
		Model model = new Model("model");
		bioModel.setModel(model);
		model.addFeature(EXTRACELLULAR_NAME);
		Feature extracellular = (Feature)model.getStructure(EXTRACELLULAR_NAME);
		model.addFeature(CYTOSOL_NAME);
		Feature cytosol = (Feature)model.getStructure(CYTOSOL_NAME);

		SpeciesContext immobileSC = model.createSpeciesContext(cytosol);
		SpeciesContext primarySC = model.createSpeciesContext(cytosol);
		SpeciesContext secondarySC = model.createSpeciesContext(cytosol);
		
		//
		// common bleaching rate for all species
		//
		
		double bleachStart = 10*context.getData(outputTimeStep) - context.getData(bleachDuration) - context.getData(postbleachDelay);
		double bleachEnd =  bleachStart + context.getData(bleachDuration);
		Expression bleachRateExp = new Expression(context.getData(bleachMonitorRate)+" + ((pow(x,2)+pow(y,2)<pow("+context.getData(bleachRadius)+",2))&&(t>="+bleachStart+")&&(t<="+bleachEnd+"))*("+(context.getData(bleachRate)-context.getData(bleachMonitorRate))+")");		
		
		{
		SimpleReaction immobileBWM = model.createSimpleReaction(cytosol);
		GeneralKinetics immobileBWMKinetics = new GeneralKinetics(immobileBWM);
		immobileBWM.setKinetics(immobileBWMKinetics);
		immobileBWM.addReactant(immobileSC, 1);
		immobileBWMKinetics.getReactionRateParameter().setExpression(Expression.mult( bleachRateExp, new Expression(immobileSC.getName())));
		}
		{
		SimpleReaction primaryBWM = model.createSimpleReaction(cytosol);
		GeneralKinetics primaryBWMKinetics = new GeneralKinetics(primaryBWM);
		primaryBWM.setKinetics(primaryBWMKinetics);
		primaryBWM.addReactant(primarySC, 1);
		primaryBWMKinetics.getReactionRateParameter().setExpression(Expression.mult( bleachRateExp, new Expression(primarySC.getName())));
		}
		{
		SimpleReaction secondaryBWM = model.createSimpleReaction(cytosol);
		GeneralKinetics secondaryBWMKinetics = new GeneralKinetics(secondaryBWM);
		secondaryBWM.setKinetics(secondaryBWMKinetics);
		secondaryBWM.addReactant(secondarySC, 1);
		secondaryBWMKinetics.getReactionRateParameter().setExpression(Expression.mult( bleachRateExp, new Expression(secondarySC.getName())));
		}
				
		//create simulation context		
		SimulationContext simContext = bioModel.addNewSimulationContext("simContext", false, false);
		simContext.setGeometry(geometry);
		
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		
		SubVolume cytSubVolume = geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME);
		SubVolume exSubVolume = geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME);
		
		cytosolFeatureMapping.setGeometryClass(cytSubVolume);
		extracellularFeatureMapping.setGeometryClass(exSubVolume);
		
		cytosolFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		extracellularFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		
		double fixedFraction = 1.0 - context.getData(primaryFraction) - context.getData(secondaryFraction);

		SpeciesContextSpec immobileSCS = simContext.getReactionContext().getSpeciesContextSpec(immobileSC);
		immobileSCS.getInitialConditionParameter().setExpression(new Expression(fixedFraction));
		immobileSCS.getDiffusionParameter().setExpression(new Expression(0.0));

		SpeciesContextSpec primarySCS = simContext.getReactionContext().getSpeciesContextSpec(primarySC);
		primarySCS.getInitialConditionParameter().setExpression(new Expression(context.getData(primaryFraction)));
		primarySCS.getDiffusionParameter().setExpression(new Expression(context.getData(primaryDiffusionRate)));

		SpeciesContextSpec secondarySCS = simContext.getReactionContext().getSpeciesContextSpec(secondarySC);
		secondarySCS.getInitialConditionParameter().setExpression(new Expression(context.getData(secondaryFraction)));
		secondarySCS.getDiffusionParameter().setExpression(new Expression(context.getData(secondaryDiffusionRate)));

		simContext.getMicroscopeMeasurement().addFluorescentSpecies(immobileSC);
		simContext.getMicroscopeMeasurement().addFluorescentSpecies(primarySC);
		simContext.getMicroscopeMeasurement().addFluorescentSpecies(secondarySC);
		simContext.getMicroscopeMeasurement().setConvolutionKernel(new GaussianConvolutionKernel(new Expression(context.getData(psfSigma)), new Expression(context.getData(psfSigma))));
		
		MathMapping mathMapping = simContext.createNewMathMapping();
		MathDescription mathDesc = mathMapping.getMathDescription();		
		simContext.setMathDescription(mathDesc);

		
		User owner = context.getDefaultOwner();

		int meshSize = (int)(domainSize/context.getData(deltaX));
		if (meshSize % 2== 0){
			meshSize = meshSize + 1; // want an odd-sized mesh in x and y ... so centered at the origin.
		}

		TimeBounds timeBounds = new TimeBounds(0.0,context.getData(postbleachDuration));
		
		//
		// simulation to use for data generation (output time steps as recorded by the microscope)
		//
		double bleachBlackoutBegin = bleachStart-context.getData(postbleachDelay);
		double bleachBlackoutEnd = bleachEnd+context.getData(postbleachDelay);

//		ArrayList<Double> times = new ArrayList<Double>();
//		double time = 0;
//		while (time<=timeBounds.getEndingTime()){
//			if (time<=bleachBlackoutBegin || time>bleachBlackoutEnd){
//				// postbleachDelay is the time it takes to switch the filters.
//				times.add(time);
//			}
//			time += outputTimeStep.getData();
//		}
//		double[] timeArray = new double[times.size()];
//		for (int i=0;i<timeArray.length;i++){
//			timeArray[i] = times.get(i);
//		}
//		OutputTimeSpec fakeDataSimOutputTimeSpec = new ExplicitOutputTimeSpec(timeArray);
		OutputTimeSpec fakeDataSimOutputTimeSpec = new UniformOutputTimeSpec(context.getData(outputTimeStep));
		
		KeyValue fakeDataSimKey = context.createNewKeyValue();
		SimulationVersion fakeDataSimVersion = new SimulationVersion(fakeDataSimKey,"fakeDataSim",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation fakeDataSim = new Simulation(fakeDataSimVersion, mathDesc);
		simContext.addSimulation(fakeDataSim);
		
		fakeDataSim.getSolverTaskDescription().setTimeBounds(timeBounds);
		fakeDataSim.getMeshSpecification().setSamplingSize(new ISize(meshSize, meshSize, 1));
		fakeDataSim.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
		fakeDataSim.getSolverTaskDescription().setOutputTimeSpec(fakeDataSimOutputTimeSpec);

		//
		// simulation to use for viewing the protocol (output time steps to understand the physics)
		//
		KeyValue fullExperimentSimKey = context.createNewKeyValue();
		SimulationVersion fullExperimentSimVersion = new SimulationVersion(fullExperimentSimKey,"fullExperiment",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation fullExperimentSim = new Simulation(fullExperimentSimVersion, mathDesc);
		simContext.addSimulation(fullExperimentSim);
				
		OutputTimeSpec fullExperimentOutputTimeSpec = new UniformOutputTimeSpec(context.getData(outputTimeStep)/10.0);

		fullExperimentSim.getSolverTaskDescription().setTimeBounds(timeBounds);
		fullExperimentSim.getMeshSpecification().setSamplingSize(new ISize(meshSize, meshSize, 1));
		fullExperimentSim.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
		fullExperimentSim.getSolverTaskDescription().setOutputTimeSpec(fullExperimentOutputTimeSpec);
		
		context.setData(this.bioModel_2D,bioModel);
		context.setData(this.simulation_2D,fakeDataSim);
		context.setData(this.bleachBlackoutBeginTime,bleachBlackoutBegin);
		context.setData(this.bleachBlackoutEndTime,bleachBlackoutEnd);
	}

}
