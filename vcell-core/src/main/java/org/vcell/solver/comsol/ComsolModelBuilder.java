package org.vcell.solver.comsol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.vcell.solver.comsol.model.VCCConvectionDiffusionEquation;
import org.vcell.solver.comsol.model.VCCGeomFeature;
import org.vcell.solver.comsol.model.VCCGeomFeature.Keep;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCBlock;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCCone;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCCylinder;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCDifference;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCIntersection;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCIntersectionSelection;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCMove;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCRotate;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCScale;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCSphere;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCUnion;
import org.vcell.solver.comsol.model.VCCGeomSequence;
import org.vcell.solver.comsol.model.VCCMeshSequence;
import org.vcell.solver.comsol.model.VCCModel;
import org.vcell.solver.comsol.model.VCCModelNode;
import org.vcell.solver.comsol.model.VCCPhysicsFeature.VCCFluxBoundary;
import org.vcell.solver.comsol.model.VCCStudy;
import org.vcell.solver.comsol.model.VCCStudyFeature;
import org.vcell.solver.comsol.model.VCCTransientStudyFeature;

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
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Equation;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;

public class ComsolModelBuilder {
	
	private static VCCGeomFeature csgVisitor(CSGNode node, ArrayList<VCCGeomFeature> geomFeatureList, String namePrefix){
		final VCCGeomFeature newFeature;
		
		if (node instanceof CSGPrimitive){
			CSGPrimitive csg = (CSGPrimitive)node;
			switch (csg.getType()){
			case CONE:{
				newFeature = new VCCCone(namePrefix+csg.getName());
				break;
			}
			case CUBE:{
				String[] size = new String[] { "2","2","2" };
				String[] pos = new String[] { "-1","-1","-1" };
				VCCBlock vccblock = new VCCBlock(namePrefix+csg.getName(), size, pos);
				newFeature = vccblock;
				break;
			}
			case CYLINDER:{
				newFeature = new VCCCylinder(namePrefix+csg.getName());
				break;
			}
			case SPHERE:{
				String r = "1";
				String[] pos = new String[] { "0","0","0" };
				newFeature = new VCCSphere(namePrefix+csg.getName(), pos, r);
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
				VCCGeomFeature vccChild = csgVisitor(child, geomFeatureList, namePrefix);
				childSubtrees.add(vccChild);
			}
			
			switch (setOp.getOpType()){
			case DIFFERENCE:{
				if (childSubtrees.size()!=2){
					throw new RuntimeException("expecting exactly two children for CSG difference operator");
				}
				VCCDifference diff = new VCCDifference(namePrefix+setOp.getName(), Keep.off);
				diff.input.add(childSubtrees.get(0));
				diff.input2.add(childSubtrees.get(1));
				newFeature = diff;
				break;
			}
			case INTERSECTION:{
				if (childSubtrees.size()<2){
					throw new RuntimeException("expecting two or more children for CSG intersection operator");
				}
				VCCIntersection intersection = new VCCIntersection(namePrefix+setOp.getName(), Keep.off);
				intersection.input.add(childSubtrees.get(0));
				newFeature = intersection;
				break;
			}
			case UNION:{
				if (childSubtrees.size()<2){
					throw new RuntimeException("expecting two or more children for CSG union operator");
				}
				VCCUnion union = new VCCUnion(namePrefix+setOp.getName(), Keep.off);
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

			VCCGeomFeature vccChild = csgVisitor(transformation.getChild(), geomFeatureList, namePrefix);

			if (transformation instanceof CSGHomogeneousTransformation){
				throw new RuntimeException("unsupported CSG transformation type Homogeneous transformation");
			}else if (transformation instanceof CSGRotation){
				CSGRotation rotation = (CSGRotation)transformation;
				String[] axis = new String[] { Double.toString(rotation.getAxis().getX()), Double.toString(rotation.getAxis().getY()), Double.toString(rotation.getAxis().getZ()) };
				String[] pos = new String[] { "0.0", "0.0", "0.0" };
				String rot = Double.toString(rotation.getRotationRadians());
				VCCRotate vccrotate = new VCCRotate(namePrefix+rotation.getName(),axis,pos,rot,Keep.off);
				vccrotate.input.add(vccChild);
				newFeature = vccrotate;
			}else if (transformation instanceof CSGScale){
				CSGScale scale = (CSGScale)transformation;
				String[] factor = new String[] { Double.toString(scale.getScale().getX()), Double.toString(scale.getScale().getY()), Double.toString(scale.getScale().getZ()) };
				String[] pos = new String[] { "0.0", "0.0", "0.0" };
				VCCScale vccscale = new VCCScale(namePrefix+scale.getName(),pos,factor,Keep.off);
				vccscale.input.add(vccChild);
				newFeature = vccscale;
			}else if (transformation instanceof CSGTranslation){
				CSGTranslation translation = (CSGTranslation)transformation;
				String[] displ = new String[] { Double.toString(translation.getTranslation().getX()), Double.toString(translation.getTranslation().getY()), Double.toString(translation.getTranslation().getZ()) };
				VCCMove vccmove = new VCCMove(namePrefix+translation.getName(),displ,Keep.off);
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
	    
	    //
	    // add geometry for all subvolumes
	    //
	    HashMap<String,VCCGeomFeature> subvolumeNameFeatureMap = new HashMap<String,VCCGeomFeature>();
	    SubVolume[] subVolumes = vcellGeometrySpec.getSubVolumes();
		for (int i=0; i<subVolumes.length; i++){
			SubVolume subvolume = subVolumes[i];
	    	if (subvolume instanceof CSGObject){
	    		CSGObject vcellCSGObject = (CSGObject) subvolume;
	    		CSGNode vcellCSGNode = vcellCSGObject.getRoot();
	    		ArrayList<VCCGeomFeature> geomFeatureList = new ArrayList<VCCGeomFeature>();
	    		VCCGeomFeature feature = csgVisitor(vcellCSGNode, geomFeatureList, subvolume.getName());
	    		geom1.geomfeatures.addAll(geomFeatureList);
	    		if (i==0){
	    			// first subvolume (on top in ordinals) doesn't need any differencing
		    		subvolumeNameFeatureMap.put(subvolume.getName(), feature);
	    		}else{
	    			// have to subtract union of prior subvolumes
	    			ArrayList<VCCGeomFeature> priorFeatures = new ArrayList<VCCGeomFeature>();
	    			for (int j=0;j<i;j++){
	    	    		CSGObject priorCSGObject = (CSGObject) subVolumes[j];
	    	    		CSGNode priorCSGNode = priorCSGObject.getRoot();
		    			geomFeatureList.clear();
	    	    		VCCGeomFeature priorFeature = csgVisitor(priorCSGNode, geomFeatureList, subvolume.getName());
	    	    		priorFeatures.add(priorFeature);
	    	    		geom1.geomfeatures.addAll(geomFeatureList);
	    			}
	    			VCCDifference diff = new VCCDifference("diff"+subvolume.getName(), Keep.off);
	    			diff.input.add(feature);
	    			diff.input2.addAll(priorFeatures);
	    			geom1.geomfeatures.add(diff);
	    			subvolumeNameFeatureMap.put(subvolume.getName(), diff);
	    		}
	    	}else{
	    		throw new RuntimeException("only CSG subvolumes currently supported by VCell's COMSOL model builder");
	    	}
	    }
		//
		// add geometry for all surfaceClasses
		//
	    HashMap<String,VCCGeomFeature> surfaceclassNameFeatureMap = new HashMap<String,VCCGeomFeature>();
		SurfaceClass[] surfaceClasses = vcellGeometry.getGeometrySurfaceDescription().getSurfaceClasses();
		for (int i=0;i<surfaceClasses.length;i++){
			SurfaceClass surfaceClass = surfaceClasses[i];
			Set<SubVolume> adjacentSubvolumes = surfaceClass.getAdjacentSubvolumes();
			if (adjacentSubvolumes.size()!=2){
				throw new RuntimeException("expecting two adjacent subvolumes for surface "+surfaceClass.getName()+" in COMSOL model builder");
			}
			// find adjacent Geometry Features (for subvolumes)
			Iterator<SubVolume> svIter = adjacentSubvolumes.iterator();
			SubVolume subvolume0 = svIter.next();
			SubVolume subvolume1 = svIter.next();
			ArrayList<VCCGeomFeature> adjacentFeatures = new ArrayList<VCCGeomFeature>();
			adjacentFeatures.add(subvolumeNameFeatureMap.get(subvolume0.getName()));
			adjacentFeatures.add(subvolumeNameFeatureMap.get(subvolume1.getName()));
			
			String name = "inter_"+subvolume0.getName()+"_"+subvolume1.getName();
			int entitydim = vcellDim-1;  // surfaces are dimension N-1
			VCCIntersectionSelection intersect_subvolumes = new VCCIntersectionSelection(name, entitydim);
			intersect_subvolumes.input.addAll(adjacentFeatures);
			
			geom1.geomfeatures.add(intersect_subvolumes);
			surfaceclassNameFeatureMap.put(surfaceClass.getName(), intersect_subvolumes);
		}
	    
	    SimulationSymbolTable symbolTable = new SimulationSymbolTable(vcellSimJob.getSimulation(), vcellSimJob.getJobIndex());
	    	    
	    //
	    // process variables and equations in outside domain
	    //
	    for (SubDomain subDomain : Collections.list(vcellMathDesc.getSubDomains())){
		    for (Equation equ : subDomain.getEquationCollection()){
		    	if (equ instanceof PdeEquation || equ instanceof OdeEquation){
		    		VCCGeomFeature geomFeature = null;
		    		final int dim;
		    		if (subDomain instanceof CompartmentSubDomain){
		    			geomFeature = subvolumeNameFeatureMap.get(subDomain.getName());
		    			dim = vcellDim;
		    		}else if (subDomain instanceof MembraneSubDomain){
		    			geomFeature = surfaceclassNameFeatureMap.get(subDomain.getName());
		    			dim = vcellDim-1;
		    		}else{
		    			throw new RuntimeException("subdomains of type '"+subDomain.getClass().getSimpleName()+"' not yet supported in COMSOL model builder");
		    		}
		    		if (geomFeature == null){
		    			throw new RuntimeException("cannot find COMSOL geometry feature named "+subDomain.getName()+" in COMSOL model builder");
		    		}
		    		VCCConvectionDiffusionEquation cdeq = new VCCConvectionDiffusionEquation("cdeq_"+equ.getVariable().getName(), geom1, geomFeature, dim);
		    		cdeq.fieldName = equ.getVariable().getName();
		    		cdeq.initial = MathUtilities.substituteModelParameters(equ.getInitialExpression(),symbolTable).flatten().infix();
		    		cdeq.sourceTerm_f = MathUtilities.substituteModelParameters(equ.getRateExpression(),symbolTable).flatten().infix();

		    		if (equ instanceof PdeEquation){
			    		PdeEquation pde = (PdeEquation)equ;
			    		cdeq.diffTerm_c = MathUtilities.substituteModelParameters(pde.getDiffusionExpression(),symbolTable).flatten().infix();
			    		
			    		if (subDomain instanceof CompartmentSubDomain){
			    			CompartmentSubDomain compartmentSubdomain = (CompartmentSubDomain)subDomain;
			    			
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
	
				    		//
				    		// look for membrane boundary conditions for this variable
				    		//
				    		MembraneSubDomain[] membraneSubdomains = vcellMathDesc.getMembraneSubDomains(compartmentSubdomain);
				    		for (MembraneSubDomain membraneSubdomain : membraneSubdomains){
				    			JumpCondition jumpCondition = membraneSubdomain.getJumpCondition((VolVariable)pde.getVariable());
				    			if (jumpCondition!=null){
					    			Expression fluxExpr = null;
					    			if (membraneSubdomain.getInsideCompartment()==compartmentSubdomain){
					    				fluxExpr = jumpCondition.getInFluxExpression();
					    			}else if (membraneSubdomain.getOutsideCompartment()==compartmentSubdomain){
					    				fluxExpr = jumpCondition.getOutFluxExpression();
					    			}
					    			String name = equ.getVariable().getName()+"_flux_"+membraneSubdomain.getName();
									VCCGeomFeature selection = surfaceclassNameFeatureMap.get(membraneSubdomain.getName());
									VCCFluxBoundary fluxBoundary = new VCCFluxBoundary(name,selection,vcellDim-1);
									fluxBoundary.flux_g = MathUtilities.substituteModelParameters(fluxExpr,symbolTable).flatten().infix();

					    			cdeq.features.add(fluxBoundary);
				    			}
				    		}
			    		}
		    		}		    		
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
