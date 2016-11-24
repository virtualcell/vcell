package org.vcell.solver.comsol;

import java.util.Collection;

import org.vcell.solver.comsol.model.VCCConvectionDiffusionEquation;
import org.vcell.solver.comsol.model.VCCGeomSequence;
import org.vcell.solver.comsol.model.VCCGeomSequence.VCCCircle;
import org.vcell.solver.comsol.model.VCCGeomSequence.VCCDifference;
import org.vcell.solver.comsol.model.VCCGeomSequence.VCCSquare;
import org.vcell.solver.comsol.model.VCCMeshSequence;
import org.vcell.solver.comsol.model.VCCModel;
import org.vcell.solver.comsol.model.VCCModelNode;
import org.vcell.solver.comsol.model.VCCStudy;
import org.vcell.solver.comsol.model.VCCStudyFeature;
import org.vcell.solver.comsol.model.VCCTransientStudyFeature;
import org.vcell.util.BeanUtils;

import cbit.vcell.geometry.SubVolume;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Equation;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;

public class ComsolModelBuilder {

	public static VCCModel getVCCModel(SimulationJob vcellSimJob) throws ExpressionException{
		
	    VCCModel model = new VCCModel("Model");

	    model.modelpath = "D:\\Developer\\eclipse\\workspace_refactor\\comsol_java\\src";

	    model.comments = "Untitled\n\n";

	    VCCModelNode comp1 = new VCCModelNode("comp1");
		model.modelnodes.add(comp1);
	    
	    int vcellDim = vcellSimJob.getSimulation().getMathDescription().getGeometry().getGeometrySpec().getDimension();
	    if (vcellDim != 2){
	    	throw new RuntimeException("expecting 2D simulation");
	    }
	    

		//
		// assume initial geometry is circle centered at 0.5, 0.5 of radius 0.3
		//
	    String comsolOutsideDomainName = "dif1";
	    String comsolInsideDomainName = "c1";
	    
	    VCCGeomSequence geom1 = new VCCGeomSequence("geom1", vcellDim);
	    model.geometrysequences.add(geom1);
	    
	    VCCMeshSequence mesh1 = new VCCMeshSequence("mesh1", geom1);
	    model.meshes.add(mesh1);

//	    VCCConvectionDiffusionEquation cdeq = new VCCConvectionDiffusionEquation("cdeq", geom1);
//	    model.physics.add(cdeq);
	    
	    VCCStudy std1 = new VCCStudy("std1");
	    model.study = std1;
	    
	    TimeBounds timeBounds = vcellSimJob.getSimulation().getSolverTaskDescription().getTimeBounds();
	    TimeStep timeStep = vcellSimJob.getSimulation().getSolverTaskDescription().getTimeStep();
	    String beginTime = Double.toString(timeBounds.getStartingTime());
	    String endTime = Double.toString(timeBounds.getEndingTime());
	    String step = Double.toString(timeStep.getDefaultTimeStep());
	    VCCStudyFeature time = new VCCTransientStudyFeature("time", beginTime, step, endTime);
	    std1.features.add(time);
//	    time.activePhysics.add(cdeq);

	    VCCSquare sq1 = new VCCSquare("sq1");
	    geom1.geomfeatures.add(sq1);
	    VCCCircle c1 = new VCCCircle("c1", new String[]{"0.5", "0.5"}, "0.3");
	    geom1.geomfeatures.add(c1);
	    VCCDifference dif1 = new VCCDifference("dif1", VCCDifference.Keep.on);
	    dif1.input.add(sq1);
	    dif1.input2.add(c1);
	    geom1.geomfeatures.add(dif1);

	    
	    MathDescription vcellMathDesc = vcellSimJob.getSimulation().getMathDescription();
	    Collection<SubDomain> a = vcellMathDesc.getSubDomainCollection();
	    SubVolume[] vcellSubVolumes = BeanUtils.getArray(vcellMathDesc.getGeometry().getGeometrySpec().getAnalyticOrCSGSubVolumes(),SubVolume.class);
	    if (vcellSubVolumes.length != 2){
	    	throw new RuntimeException("expecting 2 subvolumes in vcell geometry");
	    }
	    SimulationSymbolTable symbolTable = new SimulationSymbolTable(vcellSimJob.getSimulation(), vcellSimJob.getJobIndex());
	    
	    CompartmentSubDomain vcellOutsideDomain = vcellMathDesc.getCompartmentSubDomain(vcellSubVolumes[0].getName());
	    CompartmentSubDomain vcellInsideDomain = vcellMathDesc.getCompartmentSubDomain(vcellSubVolumes[1].getName());
	    
	    //
	    // process variables and equations in outside domain
	    //
	    for (Equation equ : vcellOutsideDomain.getEquationCollection()){
	    	if (equ instanceof PdeEquation){
	    		PdeEquation pde = (PdeEquation)equ;
	    		VolVariable volVar = (VolVariable)pde.getVariable();
	    		VCCConvectionDiffusionEquation cdeq = new VCCConvectionDiffusionEquation("cdeq_"+volVar.getName(), geom1, c1);
	    		cdeq.sourceTerm_f = MathUtilities.substituteModelParameters(pde.getRateExpression(),symbolTable).flatten().infix();
	    		cdeq.diffTerm_c = MathUtilities.substituteModelParameters(pde.getDiffusionExpression(),symbolTable).flatten().infix();
	    		String[] be = new String[] { "0", "0" };
	    		if (pde.getVelocityX()!=null){
	    			be[0] = MathUtilities.substituteModelParameters(pde.getVelocityX(),symbolTable).flatten().infix();
	    		}
	    		if (pde.getVelocityY()!=null){
	    			be[1] = MathUtilities.substituteModelParameters(pde.getVelocityY(),symbolTable).flatten().infix();
	    		}
	    		cdeq.advection_be = be;
	    		cdeq.fieldName = volVar.getName();
	    		cdeq.initial = MathUtilities.substituteModelParameters(pde.getInitialExpression(),symbolTable).flatten().infix();
	    		
	    	    model.physics.add(cdeq);
	    	}
	    }
	    
	    //
	    //
	    // go through list of constants (add to model)
	    // go through list of variables (add to model for correct domain)
	    // go through list of functions (add to model)
	    // go through list of equations (add to model)
	    //
	    return model;

	}
}
