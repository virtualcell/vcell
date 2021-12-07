/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml.vcell;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.vcell.util.TokenMangler;

import cbit.vcell.constraints.AbstractConstraint;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.ConstraintSolver;
import cbit.vcell.constraints.GeneralConstraint;
import cbit.vcell.constraints.SimpleBounds;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.units.VCUnitDefinition;
import net.sourceforge.interval.ia_math.RealInterval;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2006 3:03:22 PM)
 * @author: Jim Schaff
 */
public class StructureSizeSolver {

	private static class SolverParameter {
		public final StructureMappingParameter parameter;
		public final String name;
		public final Double knownValue;
		public Double solution;

		private SolverParameter(StructureMappingParameter parameter, String name, Double knownValue) {
			this.parameter = parameter;
			this.name = name;
			this.knownValue = knownValue;
		}

	}
	
	private static class SolverParameterCollection {
		private ArrayList<SolverParameter> solverParameters = new ArrayList<SolverParameter>();
		
		public void add(SolverParameter solverParameter){
			solverParameters.add(solverParameter);
		}

		public SolverParameter get(String name){
			for (SolverParameter solverParameter : solverParameters){
				if (solverParameter.name.equals(name)){
					return solverParameter;
				}
			}
			return null;
		}

		public SolverParameter get(StructureMappingParameter param){
			for (SolverParameter solverParameter : solverParameters){
				if (solverParameter.parameter == param){
					return solverParameter;
				}
			}
			return null;
		}
		
		public String getName(StructureMappingParameter param){
			for (SolverParameter solverParameter : solverParameters){
				if (solverParameter.parameter == param){
					return solverParameter.name;
				}
			}
			return null;
		}
		
		public List<String> getNames(){
			ArrayList<String> names = new ArrayList<String>();
			for (SolverParameter solverParameter : solverParameters){
				names.add(solverParameter.name);
			}
			return names;
		}

		public List<StructureMappingParameter> getStructureMappingParameters(){
			ArrayList<StructureMappingParameter> structureMappingParameters = new ArrayList<StructureMappingParameter>();
			for (SolverParameter solverParameter : solverParameters){
				structureMappingParameters.add(solverParameter.parameter);
			}
			return structureMappingParameters;
		}
	}
	
	public static void updateAbsoluteStructureSizes(SimulationContext simContext, Structure struct, double structSize, VCUnitDefinition structSizeUnit) throws Exception {
		StructureMapping[] structMappings = simContext.getGeometryContext().getStructureMappings();
		try {
			StructureTopology structTopology = simContext.getModel().getStructureTopology();

			SolverParameterCollection solverParameters = new SolverParameterCollection();
			ArrayList<String> unknownVars = new ArrayList<String>();
			
			for (StructureMapping sm : structMappings){
				System.out.println("Structure "+ sm.getStructure().getDisplayName() + " size " + sm.getStructure().getStructureSize().getExpression());
				if (sm.getStructure() instanceof Membrane){
					MembraneMapping mm = (MembraneMapping)sm;
					StructureMappingParameter svRatioParam = mm.getSurfaceToVolumeParameter();
					StructureMappingParameter volFractParam = mm.getVolumeFractionParameter();
					StructureMappingParameter sizeParam = mm.getSizeParameter();
					solverParameters.add(new SolverParameter(svRatioParam, TokenMangler.mangleToSName("sv_"+mm.getMembrane().getName()), svRatioParam.getExpression().evaluateConstant()));
					solverParameters.add(new SolverParameter(volFractParam, TokenMangler.mangleToSName("vf_"+mm.getMembrane().getName()), volFractParam.getExpression().evaluateConstant()));
				}
				StructureMappingParameter sizeParam = sm.getSizeParameter();
				Double priorKnownValue = null;
				String varName = TokenMangler.mangleToSName("size_"+sm.getStructure().getName());
				if (sizeParam.getExpression()!=null){
					priorKnownValue = sizeParam.getExpression().evaluateConstant();
				}else if (sm.getStructure() == struct){
					priorKnownValue = structSize;
				}else{
					unknownVars.add(varName);
				}
				solverParameters.add(new SolverParameter(sizeParam, varName, priorKnownValue));
			}

			ArrayList<Expression> expressions = new ArrayList<Expression>();
			for (int i = 0; i < structMappings.length; i++){
				if (structMappings[i] instanceof MembraneMapping){
					MembraneMapping membraneMapping = (MembraneMapping)structMappings[i];

					Feature insideFeature = structTopology.getInsideFeature(membraneMapping.getMembrane());
					Feature outsideFeature = structTopology.getOutsideFeature(membraneMapping.getMembrane());
					
					StructureMappingParameter sizeParameter = membraneMapping.getSizeParameter();
					StructureMappingParameter volFractParameter = membraneMapping.getVolumeFractionParameter();
					StructureMappingParameter surfToVolParameter = membraneMapping.getSurfaceToVolumeParameter();
					
					//
					// EC eclosing cyt, which contains er and golgi
					// "(cyt_size+ er_size + golgi_size) * cyt_svRatio - PM_size"  ... implicit equation, exp == 0 is implied
					//
					Expression sumOfInsideVolumeExp = new Expression(0.0);
					for (int j = 0; j < structMappings.length; j++){
						if (structMappings[j] instanceof FeatureMapping && structTopology.enclosedBy(structMappings[j].getStructure(), insideFeature)) {
							FeatureMapping childFeatureMappingOfInside = ((FeatureMapping)structMappings[j]);
							sumOfInsideVolumeExp = Expression.add(sumOfInsideVolumeExp,new Expression(solverParameters.getName(childFeatureMappingOfInside.getSizeParameter())));
						}
					}
					Expression tempExpr = Expression.mult(sumOfInsideVolumeExp, new Expression(solverParameters.getName(surfToVolParameter)));
					tempExpr = Expression.add(tempExpr, new Expression("-"+solverParameters.getName(sizeParameter)));
					expressions.add(tempExpr);

					//
					// EC eclosing cyt, which contains er and golgi
					// (EC_size + cyt_size + er_size + golgi_size) * cyt_vfRatio - (cyt_size + er_size + golgi_size)  ... implicit equation, exp == 0 is implied
					//
					Expression sumOfParentVolumeExp = new Expression(0.0);
					for (int j = 0; j < structMappings.length; j++){
						if (structMappings[j] instanceof FeatureMapping && structTopology.enclosedBy(structMappings[j].getStructure(), outsideFeature)){
							FeatureMapping childFeatureMappingOfParent = ((FeatureMapping)structMappings[j]);
							sumOfParentVolumeExp = Expression.add(sumOfParentVolumeExp,new Expression(TokenMangler.mangleToSName(solverParameters.getName(childFeatureMappingOfParent.getSizeParameter()))));
						}
					}
					Expression exp = Expression.mult(sumOfParentVolumeExp,new Expression(solverParameters.getName(volFractParameter)));
					exp = Expression.add(exp, Expression.negate(sumOfInsideVolumeExp));
					expressions.add(exp);
				}
			}

			if (expressions.size()!=unknownVars.size()){
				throw new RuntimeException("number of unknowns is "+unknownVars.size()+", number of equations is "+expressions.size());
			}
			if (unknownVars.size()==0 && expressions.size()==0){
				StructureMappingParameter sizeParam = simContext.getGeometryContext().getStructureMapping(struct).getSizeParameter();
				sizeParam.setExpression(new Expression(structSize));
				return;
			}
			RationalExp[][] rowColData = new RationalExp[unknownVars.size()][unknownVars.size()+1];
			for (int row=0; row<unknownVars.size(); row++){
				//
				// verify that there is no "constant" term (without an unknown)
				//
				//System.out.println("equation("+row+"): "+expressions.get(row).infix());
				Expression constantTerm = new Expression(expressions.get(row));
				for (String var : unknownVars){
					constantTerm.substituteInPlace(new Expression(var), new Expression(0.0));
				}
				constantTerm = constantTerm.flatten();
				
				//
				// we can use the derivative to find the coefficients (just verify that the coefficient doesn't contain an unknown).
				//
				for (int col=0; col<unknownVars.size(); col++){
					Expression equation = new Expression(expressions.get(row));
					String colVariable = unknownVars.get(col);
					Expression deriv = equation.differentiate(colVariable).flatten();
					String[] symbols = deriv.getSymbols();
					if (symbols!=null){
						for (String symbol : symbols){
							if (unknownVars.contains(symbol)){
								throw new RuntimeException("equation is not linear in the unknowns");
							}
						}
					}
					rowColData[row][col] = RationalExpUtils.getRationalExp(deriv);
				}
				rowColData[row][unknownVars.size()] = RationalExpUtils.getRationalExp(constantTerm).minus();
			}
			RationalExpMatrix rationalExpMatrix = new RationalExpMatrix(rowColData);
			//rationalExpMatrix.show();
			RationalExp[] solutions = rationalExpMatrix.solveLinearExpressions();
			double[] solutionValues = new double[solutions.length];
			for (int i=0; i<unknownVars.size(); i++){
				Expression vcSolution = new Expression(solutions[i].infixString());
				String[] symbols = vcSolution.getSymbols();
				if (symbols!=null){
					for (String symbol : symbols){
						SolverParameter p = solverParameters.get(symbol);
						if (p.knownValue==null){
							throw new RuntimeException("solution for var "+unknownVars.get(i)+" is a function of unknown var "+p.name);
						}
						vcSolution.substituteInPlace(new Expression(symbol), new Expression(p.knownValue.doubleValue()));
					}
				}
				double value = vcSolution.flatten().evaluateConstant();
				//System.out.println(unknownVars.get(i)+" = "+value+" = "+solutions[i].infixString());
				solutionValues[i] = value;
			}
			for (int i=0; i<unknownVars.size(); i++){
				SolverParameter p = solverParameters.get(unknownVars.get(i));
				p.parameter.setExpression(new Expression(solutionValues[i]));
			}
			//
			// set the one known value (if not set already by the gui).
			//
			StructureMappingParameter sizeParam = simContext.getGeometryContext().getStructureMapping(struct).getSizeParameter();
			sizeParam.setExpression(new Expression(structSize));
			
			System.out.println("done");
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new Exception(e.getMessage());
		}
	}

	
/**
 * Insert the method's description here.
 * Creation date: (5/17/2006 10:33:38 AM)
 * @return double[]
 * @param structName java.lang.String
 * @param structSize double
 */
public static void updateUnitStructureSizes(SimulationContext simContext, GeometryClass geometryClass) {
	if (simContext.getGeometryContext().getGeometry().getDimension() == 0) {
		return;
	}
	
	StructureMapping[] myStructMappings = simContext.getGeometryContext().getStructureMappings(geometryClass);
	if (myStructMappings != null && myStructMappings.length == 1) {
		// if the unitSizeParameter is dimensionless, then features are mapped to SubVolumes or Membranes are mapped to surfaces (should sum to 1)
		boolean bDimensionless = myStructMappings[0].getUnitSizeParameter().getUnitDefinition().isEquivalent(simContext.getModel().getUnitSystem().getInstance_DIMENSIONLESS());
		if (bDimensionless){
			try {
				myStructMappings[0].getUnitSizeParameter().setExpression(new Expression(1.0));
				return;
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	if (myStructMappings!=null && myStructMappings.length==0){
		// nothing to solve, there are no mappings for this geometryClass
		return;
	}
	StructureMapping[] structMappings = simContext.getGeometryContext().getStructureMappings();
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();

		Structure struct = null;
		Expression totalVolExpr = new Expression(0.0);
		StructureTopology structureTopology = simContext.getModel().getStructureTopology();
		for (int i = 0; i < structMappings.length; i++){
			if (structMappings[i].getGeometryClass()!=geometryClass){
				continue;
			}
			// new model with unit sizes already
			if (structMappings[i].getUnitSizeParameter() != null && structMappings[i].getUnitSizeParameter().getExpression() != null) {
				return;
			}
			if (struct==null){
				struct = structMappings[i].getStructure();
			}
			if (structMappings[i] instanceof MembraneMapping){
				MembraneMapping membraneMapping = (MembraneMapping)structMappings[i];

				Membrane membrane = membraneMapping.getMembrane();
				String membraneSizeName = TokenMangler.mangleToSName(membrane.getName()+"_size");
				ccImpl.addSimpleBound(new SimpleBounds(membraneSizeName,new RealInterval(0,100000),AbstractConstraint.PHYSICAL_LIMIT,"definition"));
				Feature insideFeature = structureTopology.getInsideFeature(membrane);

				String volFractName = TokenMangler.mangleToSName(insideFeature.getName()+"_volFract");
				String svRatioName = TokenMangler.mangleToSName(insideFeature.getName()+"_svRatio");

				StructureMapping.StructureMappingParameter volFractParameter = membraneMapping.getVolumeFractionParameter();
				double volFractValue = volFractParameter.getExpression().evaluateConstant();
				ccImpl.addSimpleBound(new SimpleBounds(volFractName,new RealInterval(volFractValue,volFractValue),AbstractConstraint.MODELING_ASSUMPTION,"from model"));

				StructureMapping.StructureMappingParameter surfToVolParameter = membraneMapping.getSurfaceToVolumeParameter();
				double svRatioValue = surfToVolParameter.getExpression().evaluateConstant();
				ccImpl.addSimpleBound(new SimpleBounds(svRatioName,new RealInterval(svRatioValue,svRatioValue),AbstractConstraint.MODELING_ASSUMPTION,"from model"));

				// membrane mapped to volume 
				if (geometryClass instanceof SubVolume) {
					//
					// EC eclosing cyt, which contains er and golgi
					// "(cyt_size+ er_size + golgi_size) * cyt_svRatio - PM_size == 0"
					//
					Expression sumOfInsideVolumeExp = new Expression(0.0);
					for (int j = 0; j < structMappings.length; j++){
						if (structMappings[j] instanceof FeatureMapping && structureTopology.enclosedBy(structMappings[j].getStructure(),insideFeature)) {
							Feature childFeatureOfInside = ((FeatureMapping)structMappings[j]).getFeature();
							if (simContext.getGeometryContext().getStructureMapping(childFeatureOfInside).getGeometryClass() == geometryClass) {
								sumOfInsideVolumeExp = Expression.add(sumOfInsideVolumeExp,new Expression(TokenMangler.mangleToSName(childFeatureOfInside.getName()+"_size")));
							}
						}
					}
					Expression tempExpr = Expression.mult(sumOfInsideVolumeExp, new Expression(svRatioName));
					tempExpr = Expression.add(tempExpr, new Expression("-"+membraneSizeName));
					ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression(tempExpr.infix()+"==0"),AbstractConstraint.MODELING_ASSUMPTION,"svRatio definition"));
	
					//
					// EC eclosing cyt, which contains er and golgi
					// (EC_size + cyt_size + er_size + golgi_size) * cyt_vfRatio - (cyt_size + er_size + golgi_size) == 0
					//
					Feature outsideFeature = structureTopology.getOutsideFeature(membrane);
					Expression sumOfParentVolumeExp = new Expression(0.0);
					for (int j = 0; j < structMappings.length; j++){
						if (structMappings[j] instanceof FeatureMapping && structureTopology.enclosedBy(structMappings[j].getStructure(),outsideFeature)){
							Feature childFeatureOfParent = ((FeatureMapping)structMappings[j]).getFeature();
							if (simContext.getGeometryContext().getStructureMapping(childFeatureOfParent).getGeometryClass() == geometryClass) {
								sumOfParentVolumeExp = Expression.add(sumOfParentVolumeExp,new Expression(TokenMangler.mangleToSName(childFeatureOfParent.getName()+"_size")));
							}
						}
					}
					Expression exp = Expression.mult(sumOfParentVolumeExp,new Expression(volFractName));
					exp = Expression.add(exp, Expression.negate(sumOfInsideVolumeExp));
					ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression(exp.infix()+"==0.0"),AbstractConstraint.MODELING_ASSUMPTION,"volFract definition"));
				}
			}else if (structMappings[i] instanceof FeatureMapping){
				FeatureMapping featureMapping = (FeatureMapping)structMappings[i];
				String featureSizeName = TokenMangler.mangleToSName(featureMapping.getFeature().getName()+"_size");
				totalVolExpr = Expression.add(totalVolExpr, new Expression(featureSizeName));
				ccImpl.addSimpleBound(new SimpleBounds(featureSizeName,new RealInterval(0,1),AbstractConstraint.PHYSICAL_LIMIT,"definition"));
			}
		}
		if (geometryClass instanceof SubVolume) {
			ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression(totalVolExpr.infix()+"==1.0"),AbstractConstraint.MODELING_ASSUMPTION,"total volume"));
		}
		//ccImpl.show();
		
		ConstraintSolver constraintSolver = new ConstraintSolver(ccImpl);
		constraintSolver.resetIntervals();

		int numTimesNarrowed = 0;
		RealInterval[] lastSolution = null;
		boolean bChanged = true;
		while (constraintSolver.narrow() && bChanged && numTimesNarrowed<125){
			numTimesNarrowed++;
			bChanged = false;
			
			RealInterval[] thisSolution = constraintSolver.getIntervals();
			if (lastSolution!=null){
				for (int i = 0; i < thisSolution.length; i++){
					if (!thisSolution[i].equals(lastSolution[i])){
						bChanged = true;
					}
				}
			}else{
				bChanged = true;
			}
			lastSolution = thisSolution;
		}
		System.out.println("num of times narrowed = "+numTimesNarrowed);
		
		if (numTimesNarrowed>0){
			String[] symbols = constraintSolver.getSymbols();
			net.sourceforge.interval.ia_math.RealInterval[] solution = constraintSolver.getIntervals();
			double totalArea = 0;
			double totalVolume = 0;
			for (int i = 0; i < symbols.length; i++){
				System.out.println("solution["+i+"] \""+symbols[i]+"\" = "+solution[i]);
				
				for (int j = 0; j < structMappings.length; j++){
					if (symbols[i].equals(TokenMangler.mangleToSName(structMappings[j].getStructure().getName()+"_size"))){
						if (!Double.isInfinite(solution[i].lo()) && !Double.isInfinite(solution[i].hi())) {
							double value = (solution[i].lo()+solution[i].hi())/2;
							Expression exp = new Expression(value);
							if (structMappings[j] instanceof FeatureMapping){
								FeatureMapping fm = (FeatureMapping)structMappings[j];
								totalVolume += value;
								if (geometryClass instanceof SubVolume){
									fm.getVolumePerUnitVolumeParameter().setExpression(exp);
								}else if (geometryClass instanceof SurfaceClass){
									fm.getVolumePerUnitAreaParameter().setExpression(exp);
								}
							}else if (structMappings[j] instanceof MembraneMapping){
								MembraneMapping mm = (MembraneMapping)structMappings[j];
								totalArea += value;
								if (geometryClass instanceof SubVolume){
									mm.getAreaPerUnitVolumeParameter().setExpression(exp);
								}else if (geometryClass instanceof SurfaceClass){
									mm.getAreaPerUnitAreaParameter().setExpression(exp);
								}
							}
						}
					}
				}
			}
			//
			// normalize all so that total volume is 1.0 for subVolumes or
			// total area is 1.0 for surfaceClasses
			//
			double scaleFactor=1;
			if (geometryClass instanceof SubVolume){
				scaleFactor = totalVolume;
			}else if (geometryClass instanceof SurfaceClass){
				scaleFactor = totalArea;
			}else{
				throw new RuntimeException("unexpected GeometryClass");
			}
			for (int j = 0; j < structMappings.length; j++){
				if (structMappings[j].getGeometryClass()==geometryClass){
					if (structMappings[j] instanceof FeatureMapping){
						FeatureMapping fm = (FeatureMapping)structMappings[j];
						if (geometryClass instanceof SubVolume){
							fm.getVolumePerUnitVolumeParameter().setExpression(new Expression(fm.getVolumePerUnitVolumeParameter().getExpression().evaluateConstant()/scaleFactor));
						}else if (geometryClass instanceof SurfaceClass){
							fm.getVolumePerUnitAreaParameter().setExpression(new Expression(fm.getVolumePerUnitAreaParameter().getExpression().evaluateConstant()/scaleFactor));
						}
					}else if (structMappings[j] instanceof MembraneMapping){
						MembraneMapping mm = (MembraneMapping)structMappings[j];
						if (geometryClass instanceof SubVolume){
							mm.getAreaPerUnitVolumeParameter().setExpression(new Expression(mm.getAreaPerUnitVolumeParameter().getExpression().evaluateConstant()/scaleFactor));
						}else if (geometryClass instanceof SurfaceClass){
							mm.getAreaPerUnitAreaParameter().setExpression(new Expression(mm.getAreaPerUnitAreaParameter().getExpression().evaluateConstant()/scaleFactor));
						}
					}
				}
			}
		}else{
			throw new RuntimeException("cannot solve for size");
		}
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/17/2006 10:33:38 AM)
 * @return double[]
 * @param structName java.lang.String
 * @param structSize double
 */
public static void updateRelativeStructureSizes(SimulationContext simContext) throws Exception {

	if (simContext.getGeometry().getDimension() > 0){
		throw new RuntimeException("not yet supported for spatial applications");
	}
	
	StructureMapping[] structureMappings = simContext.getGeometryContext().getStructureMappings();
	try {
		// This is rewritten in Feb 2008. Siblings and children are correctly taken into account when calculating the volume fractions.
		StructureTopology structTopology = simContext.getModel().getStructureTopology(); 
		for(int i =0; i< structureMappings.length; i++)
		{
			if(structureMappings[i] instanceof MembraneMapping)
			{
				//calculate the sum of features' sizes inside this membrane, this is used for calculating both surface volume ratio and volume fraction.
				double sumOfSubFeatures = 0;
				Membrane membrane = ((MembraneMapping)structureMappings[i]).getMembrane();
				Enumeration<Feature> subFeatures = structTopology.getSubFeatures(structTopology.getInsideFeature(membrane));
				while(subFeatures.hasMoreElements())
				{
					Feature feature = subFeatures.nextElement();
					sumOfSubFeatures = sumOfSubFeatures + simContext.getGeometryContext().getStructureMapping(feature).getSizeParameter().getExpression().evaluateConstant();
				}
				//calculate the sum of features's sizes inside the membrance's parent feature, this is used for calculating the volume fraction.
				double sumOfParentMemSubFeatures = 0;
				Feature parentFeature = structTopology.getOutsideFeature(membrane);
				if(parentFeature != null)
				{
					Enumeration<Feature> parentSubFeatures = structTopology.getSubFeatures(parentFeature);
					while(parentSubFeatures.hasMoreElements())
					{
						Feature feature = parentSubFeatures.nextElement();
						sumOfParentMemSubFeatures = sumOfParentMemSubFeatures + simContext.getGeometryContext().getStructureMapping(feature).getSizeParameter().getExpression().evaluateConstant();
					}
					//set surface volume ratio
					((MembraneMapping)structureMappings[i]).getSurfaceToVolumeParameter().setExpression(new Expression(((MembraneMapping)structureMappings[i]).getSizeParameter().getExpression().evaluateConstant()/sumOfSubFeatures));
					//set volume fraction
					((MembraneMapping)structureMappings[i]).getVolumeFractionParameter().setExpression(new Expression(sumOfSubFeatures/sumOfParentMemSubFeatures));
				}
			}
		}
	}catch (NullPointerException e){
		e.printStackTrace(System.out);
		//DialogUtils.showErrorDialog("structure sizes must all be specified");
		throw new Exception("structure sizes must all be specified");
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new Exception(e.getMessage());
	}
}
}
