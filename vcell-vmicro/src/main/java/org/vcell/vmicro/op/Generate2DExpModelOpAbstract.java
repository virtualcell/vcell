package org.vcell.vmicro.op;

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.Date;

import cbit.vcell.solver.*;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionFlag;

import cbit.image.ImageException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MicroscopeMeasurement.GaussianConvolutionKernel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Feature;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public abstract class Generate2DExpModelOpAbstract {
	
	public static class GeneratedModelResults {
		public BioModel bioModel_2D;
		public Simulation simulation_2D;
		public double bleachBlackoutBeginTime;
		public double bleachBlackoutEndTime;
	}
	
	public interface Context {

		User getDefaultOwner();

		KeyValue createNewKeyValue();
		
	}

	public final GeneratedModelResults generateModel(
			double deltaX,
			double bleachRadius,
			double cellRadius,
			double bleachDuration,
			double bleachRate,
			double postbleachDelay,
			double postbleachDuration,
			double psfSigma,
			double outputTimeStep,
			double primaryDiffusionRate,
			double primaryFraction,
			double bleachMonitorRate,
			double secondaryDiffusionRate,
			double secondaryFraction,
			String extracellularName,
			String cytosolName,
			Context context) throws PropertyVetoException, ExpressionException, GeometryException, ImageException, ModelException, MappingException, MathException, MatrixException {

		double domainSize = 2.2*cellRadius;
		Extent extent = new Extent(domainSize, domainSize, 1.0);
		Origin origin = new Origin(-extent.getX()/2.0, -extent.getY()/2.0, -extent.getZ()/2.0);
		
		String EXTRACELLULAR_NAME = extracellularName;
		String CYTOSOL_NAME = cytosolName;

		AnalyticSubVolume cytosolSubVolume = new AnalyticSubVolume(CYTOSOL_NAME,new Expression("pow(x,2)+pow(y,2)<pow("+cellRadius+",2)"));
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
		
		double bleachStart = 10*outputTimeStep - bleachDuration - postbleachDelay;
		double bleachEnd =  bleachStart + bleachDuration;
		Expression bleachRateExp = createBleachExpression(bleachRadius,	bleachRate, bleachMonitorRate, bleachStart, bleachEnd);		
		
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
		SimulationContext simContext = bioModel.addNewSimulationContext("simContext", SimulationContext.Application.NETWORK_DETERMINISTIC);
		simContext.setGeometry(geometry);
		
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		
		SubVolume cytSubVolume = geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME);
		SubVolume exSubVolume = geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME);
		//unused? SurfaceClass pmSurfaceClass = geometry.getGeometrySurfaceDescription().getSurfaceClass(exSubVolume, cytSubVolume);
		
		cytosolFeatureMapping.setGeometryClass(cytSubVolume);
		extracellularFeatureMapping.setGeometryClass(exSubVolume);
		
		cytosolFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		extracellularFeatureMapping.getUnitSizeParameter().setExpression(new Expression(1.0));
		
		double fixedFraction = 1.0 - primaryFraction - secondaryFraction;

		SpeciesContextSpec immobileSCS = simContext.getReactionContext().getSpeciesContextSpec(immobileSC);
		immobileSCS.getInitialConditionParameter().setExpression(new Expression(fixedFraction));
		immobileSCS.getDiffusionParameter().setExpression(new Expression(0.0));

		SpeciesContextSpec primarySCS = simContext.getReactionContext().getSpeciesContextSpec(primarySC);
		primarySCS.getInitialConditionParameter().setExpression(new Expression(primaryFraction));
		primarySCS.getDiffusionParameter().setExpression(new Expression(primaryDiffusionRate));

		SpeciesContextSpec secondarySCS = simContext.getReactionContext().getSpeciesContextSpec(secondarySC);
		secondarySCS.getInitialConditionParameter().setExpression(new Expression(secondaryFraction));
		secondarySCS.getDiffusionParameter().setExpression(new Expression(secondaryDiffusionRate));

		simContext.getMicroscopeMeasurement().addFluorescentSpecies(immobileSC);
		simContext.getMicroscopeMeasurement().addFluorescentSpecies(primarySC);
		simContext.getMicroscopeMeasurement().addFluorescentSpecies(secondarySC);
		simContext.getMicroscopeMeasurement().setConvolutionKernel(new GaussianConvolutionKernel(new Expression(psfSigma), new Expression(psfSigma)));
		
		MathMapping mathMapping = simContext.createNewMathMapping();
		MathDescription mathDesc = mathMapping.getMathDescription();		
		simContext.setMathDescription(mathDesc);

		
		User owner = context.getDefaultOwner();

		int meshSize = (int)(domainSize/deltaX);
		if (meshSize % 2== 0){
			meshSize = meshSize + 1; // want an odd-sized mesh in x and y ... so centered at the origin.
		}

		TimeBounds timeBounds = new TimeBounds(0.0,postbleachDuration);
		
		//
		// simulation to use for data generation (output time steps as recorded by the microscope)
		//
		double bleachBlackoutBegin = bleachStart - postbleachDelay;
		double bleachBlackoutEnd = bleachEnd + postbleachDelay;

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
		OutputTimeSpec fakeDataSimOutputTimeSpec = new UniformOutputTimeSpec(outputTimeStep);
		
		KeyValue fakeDataSimKey = context.createNewKeyValue();
		SimulationVersion fakeDataSimVersion = new SimulationVersion(fakeDataSimKey,"fakeDataSim",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation fakeDataSim = new Simulation(fakeDataSimVersion, mathDesc, new SimulationOwner.StandaloneSimulationOwner());
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
		Simulation fullExperimentSim = new Simulation(fullExperimentSimVersion, mathDesc, new SimulationOwner.StandaloneSimulationOwner());
		simContext.addSimulation(fullExperimentSim);
				
		OutputTimeSpec fullExperimentOutputTimeSpec = new UniformOutputTimeSpec(outputTimeStep/10.0);

		fullExperimentSim.getSolverTaskDescription().setTimeBounds(timeBounds);
		fullExperimentSim.getMeshSpecification().setSamplingSize(new ISize(meshSize, meshSize, 1));
		fullExperimentSim.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
		fullExperimentSim.getSolverTaskDescription().setOutputTimeSpec(fullExperimentOutputTimeSpec);
		
		GeneratedModelResults results = new GeneratedModelResults();
		results.bioModel_2D = bioModel;
		results.simulation_2D = fakeDataSim;
		results.bleachBlackoutBeginTime = bleachBlackoutBegin;
		results.bleachBlackoutEndTime = bleachBlackoutEnd;
		
		return results;
	}

	protected abstract Expression createBleachExpression(
			double bleachRadius,
			double bleachRate, 
			double bleachMonitorRate, 
			double bleachStart,
			double bleachEnd) throws ExpressionException;

}
