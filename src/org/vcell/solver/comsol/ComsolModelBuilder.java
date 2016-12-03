package org.vcell.solver.comsol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.vcell.solver.comsol.model.VCCConvectionDiffusionEquation;
import org.vcell.solver.comsol.model.VCCGeomFeature;
import org.vcell.solver.comsol.model.VCCGeomFeature.Keep;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCBlock;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCCone;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCCylinder;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCDifference;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCIntersection;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCMove;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCRotate;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCScale;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCSphere;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCUnion;
import org.vcell.solver.comsol.model.VCCGeomSequence;
import org.vcell.solver.comsol.model.VCCMeshSequence;
import org.vcell.solver.comsol.model.VCCModel;
import org.vcell.solver.comsol.model.VCCModelNode;
import org.vcell.solver.comsol.model.VCCStudy;
import org.vcell.solver.comsol.model.VCCStudyFeature;
import org.vcell.solver.comsol.model.VCCTransientStudyFeature;
import org.vcell.util.BeanUtils;

import cbit.vcell.geometry.CSGHomogeneousTransformation;
import cbit.vcell.geometry.CSGNode;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGPrimitive;
import cbit.vcell.geometry.CSGRotation;
import cbit.vcell.geometry.CSGScale;
import cbit.vcell.geometry.CSGSetOperator;
import cbit.vcell.geometry.CSGTransformation;
import cbit.vcell.geometry.CSGTranslation;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
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
	
	private static VCCGeomFeature csgVisitor(CSGNode node, ArrayList<VCCGeomFeature> geomFeatureList){
		final VCCGeomFeature newFeature;
		
		if (node instanceof CSGPrimitive){
			CSGPrimitive csg = (CSGPrimitive)node;
			switch (csg.getType()){
			case CONE:{
				newFeature = new VCCCone(csg.getName());
				break;
			}
			case CUBE:{
				String[] size = new String[] { "2","2","2" };
				String[] pos = new String[] { "-1","-1","-1" };
				VCCBlock vccblock = new VCCBlock(csg.getName(), size, pos);
				newFeature = vccblock;
				break;
			}
			case CYLINDER:{
				newFeature = new VCCCylinder(csg.getName());
				break;
			}
			case SPHERE:{
				String r = "1";
				String[] pos = new String[] { "0","0","0" };
				newFeature = new VCCSphere(csg.getName(), pos, r);
				break;
			}
			default:{
				throw new RuntimeException("csg primative type '"+csg.getType().name()+"' not yet supported in COMSOL model builder");
			}
			}
		}else if (node instanceof CSGSetOperator){
			CSGSetOperator setOp = (CSGSetOperator)node;
			
			ArrayList<VCCGeomFeature> childSubtrees = new ArrayList<VCCGeomFeature>();
			for (CSGNode child : setOp.getChildren()){
				VCCGeomFeature vccChild = csgVisitor(child, geomFeatureList);
				childSubtrees.add(vccChild);
			}
			
			switch (setOp.getOpType()){
			case DIFFERENCE:{
				if (childSubtrees.size()!=2){
					throw new RuntimeException("expecting exactly two children for CSG difference operator");
				}
				VCCDifference diff = new VCCDifference(setOp.getName(), Keep.off);
				diff.input.add(childSubtrees.get(0));
				diff.input2.add(childSubtrees.get(1));
				newFeature = diff;
				break;
			}
			case INTERSECTION:{
				if (childSubtrees.size()<2){
					throw new RuntimeException("expecting two or more children for CSG intersection operator");
				}
				VCCIntersection intersection = new VCCIntersection(setOp.getName(), Keep.off);
				intersection.input.add(childSubtrees.get(0));
				newFeature = intersection;
				break;
			}
			case UNION:{
				if (childSubtrees.size()<2){
					throw new RuntimeException("expecting two or more children for CSG union operator");
				}
				VCCUnion union = new VCCUnion(setOp.getName(), Keep.off);
				union.input.add(childSubtrees.get(0));
				newFeature = union;
				break;
			}
			default:{
				throw new RuntimeException("csg set operator '"+setOp.getOpType().name()+"' not yet supported in COMSOL model builder");
			}
			}
		}else if (node instanceof CSGTransformation){
			CSGTransformation transformation = (CSGTransformation)node;

			VCCGeomFeature vccChild = csgVisitor(transformation.getChild(), geomFeatureList);

			if (transformation instanceof CSGHomogeneousTransformation){
				throw new RuntimeException("unsupported CSG transformation type Homogeneous transformation");
			}else if (transformation instanceof CSGRotation){
				CSGRotation rotation = (CSGRotation)transformation;
				String[] axis = new String[] { Double.toString(rotation.getAxis().getX()), Double.toString(rotation.getAxis().getY()), Double.toString(rotation.getAxis().getZ()) };
				String[] pos = new String[] { "0.0", "0.0", "0.0" };
				String rot = Double.toString(rotation.getRotationRadians());
				VCCRotate vccrotate = new VCCRotate(rotation.getName(),axis,pos,rot,Keep.off);
				vccrotate.input.add(vccChild);
				newFeature = vccrotate;
			}else if (transformation instanceof CSGScale){
				CSGScale scale = (CSGScale)transformation;
				String[] factor = new String[] { Double.toString(scale.getScale().getX()), Double.toString(scale.getScale().getY()), Double.toString(scale.getScale().getZ()) };
				String[] pos = new String[] { "0.0", "0.0", "0.0" };
				VCCScale vccscale = new VCCScale(scale.getName(),pos,factor,Keep.off);
				vccscale.input.add(vccChild);
				newFeature = vccscale;
			}else if (transformation instanceof CSGTranslation){
				CSGTranslation translation = (CSGTranslation)transformation;
				String[] displ = new String[] { Double.toString(translation.getTranslation().getX()), Double.toString(translation.getTranslation().getY()), Double.toString(translation.getTranslation().getZ()) };
				VCCMove vccmove = new VCCMove(translation.getName(),displ,Keep.off);
				vccmove.input.add(vccChild);
				newFeature = vccmove;
			}else{
				throw new RuntimeException("unsupported CSG transformation type '"+transformation.getClass().getSimpleName()+"'");
			}
		}else{
			throw new RuntimeException("unsupported CSGNode type '"+node.getClass().getSimpleName()+"'");
		}
		geomFeatureList.add(newFeature);
		return newFeature;
	}

	public static VCCModel getVCCModel(SimulationJob vcellSimJob) throws ExpressionException{
		
		MathDescription vcellMathDesc = vcellSimJob.getSimulation().getMathDescription();
		Geometry vcellGeometry = vcellMathDesc.getGeometry();
		GeometrySpec vcellGeometrySpec = vcellGeometry.getGeometrySpec();
		int vcellDim = vcellGeometrySpec.getDimension();

		VCCModel model = new VCCModel("Model", vcellDim);

	    model.modelpath = "D:\\Developer\\eclipse\\workspace_refactor\\comsol_java\\src";

	    model.comments = "Untitled\n\n";

	    VCCModelNode comp1 = new VCCModelNode("comp1");
		model.modelnodes.add(comp1);
	    
//	    if (vcellDim != 2){
//	    	throw new RuntimeException("expecting 2D simulation");
//	    }
	    

		//
		// assume initial geometry is circle centered at 0.5, 0.5 of radius 0.3
		//
//	    String comsolOutsideDomainName = "dif1";
//	    String comsolInsideDomainName = "c1";
	    
	    VCCGeomSequence geom1 = new VCCGeomSequence("geom1", vcellDim);
	    model.geometrysequences.add(geom1);
	    
	    VCCMeshSequence mesh1 = new VCCMeshSequence("mesh1", geom1);
	    model.meshes.add(mesh1);

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

	    if (vcellGeometrySpec.getImage()!=null){
	    	throw new RuntimeException("image-based geometries not yet supported by VCell's COMSOL model builder");
	    }
	    if (vcellGeometrySpec.getNumSubVolumes()==0){
	    	throw new RuntimeException("no subvolumes defined in geometry");
	    }
	    if (vcellGeometrySpec.getNumAnalyticOrCSGSubVolumes() != vcellGeometrySpec.getNumSubVolumes()){
	    	throw new RuntimeException("only analytic and CSG subvolumes currently supported by VCell's COMSOL model builder");
	    }
	    HashMap<String,VCCGeomFeature> subvolumeNameFeatureMap = new HashMap<String,VCCGeomFeature>();
	    for (SubVolume subvolume : vcellGeometrySpec.getSubVolumes()){
	    	if (subvolume instanceof CSGObject){
	    		CSGObject vcellCSGObject = (CSGObject) subvolume;
	    		CSGNode vcellCSGNode = vcellCSGObject.getRoot();
	    		ArrayList<VCCGeomFeature> geomFeatureList = new ArrayList<VCCGeomFeature>();
	    		VCCGeomFeature feature = csgVisitor(vcellCSGNode, geomFeatureList);
	    		subvolumeNameFeatureMap.put(subvolume.getName(), feature);
	    		geom1.geomfeatures.addAll(geomFeatureList);
	    	}
	    }
	    
//	    VCCSquare sq1 = new VCCSquare("sq1");
//	    geom1.geomfeatures.add(sq1);
//	    VCCCircle c1 = new VCCCircle("c1", new String[]{"0.5", "0.5"}, "0.3");
//	    geom1.geomfeatures.add(c1);
//	    VCCDifference dif1 = new VCCDifference("dif1", VCCDifference.Keep.on);
//	    dif1.input.add(sq1);
//	    dif1.input2.add(c1);
//	    geom1.geomfeatures.add(dif1);

	    
	    Collection<SubDomain> a = vcellMathDesc.getSubDomainCollection();
	    SubVolume[] vcellSubVolumes = BeanUtils.getArray(vcellGeometrySpec.getAnalyticOrCSGSubVolumes(),SubVolume.class);
	    if (vcellSubVolumes.length != 2){
	    	throw new RuntimeException("expecting 2 subvolumes in vcell geometry");
	    }
	    SimulationSymbolTable symbolTable = new SimulationSymbolTable(vcellSimJob.getSimulation(), vcellSimJob.getJobIndex());
	    	    
	    //
	    // process variables and equations in outside domain
	    //
	    for (SubDomain subDomain : Collections.list(vcellMathDesc.getSubDomains())){
		    for (Equation equ : subDomain.getEquationCollection()){
		    	if (equ instanceof PdeEquation){
		    		PdeEquation pde = (PdeEquation)equ;
		    		VolVariable volVar = (VolVariable)pde.getVariable();
		    		if (!(subDomain instanceof CompartmentSubDomain)){
		    			throw new RuntimeException("subdomains of type '"+subDomain.getClass().getSimpleName()+"' not yet supported in COMSOL model builder");
		    		}
		    		VCCGeomFeature geomFeature = subvolumeNameFeatureMap.get(subDomain.getName());
		    		if (geomFeature == null){
		    			throw new RuntimeException("cannot find COMSOL geometry feature named "+subDomain.getName()+" in COMSOL model builder");
		    		}
		    		VCCConvectionDiffusionEquation cdeq = new VCCConvectionDiffusionEquation("cdeq_"+volVar.getName(), geom1, geomFeature);
		    		cdeq.fieldName = volVar.getName();
		    		cdeq.initial = MathUtilities.substituteModelParameters(pde.getInitialExpression(),symbolTable).flatten().infix();
		    		cdeq.sourceTerm_f = MathUtilities.substituteModelParameters(pde.getRateExpression(),symbolTable).flatten().infix();
		    		cdeq.diffTerm_c = MathUtilities.substituteModelParameters(pde.getDiffusionExpression(),symbolTable).flatten().infix();
		    		ArrayList<String> be = new ArrayList<String>();
		    		if (pde.getVelocityX()!=null){
		    			be.add(MathUtilities.substituteModelParameters(pde.getVelocityX(),symbolTable).flatten().infix());
	    			}else{
	    				be.add("0");
		    		}
		    		if (vcellDim>=2){
		    			if (pde.getVelocityY()!=null){
		    				be.add(MathUtilities.substituteModelParameters(pde.getVelocityY(),symbolTable).flatten().infix());
		    			}else{
		    				be.add("0");
		    			}
			    	}
		    		if (vcellDim==3){
		    			if (pde.getVelocityY()!=null){
		    				be.add(MathUtilities.substituteModelParameters(pde.getVelocityZ(),symbolTable).flatten().infix());
		    			}else{
		    				be.add("0");
		    			}
			    	}
		    		
		    		cdeq.advection_be = be.toArray(new String[vcellDim]);
		    		
		    	    model.physics.add(cdeq);
		    	}
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
