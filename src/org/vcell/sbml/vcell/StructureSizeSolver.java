package org.vcell.sbml.vcell;
import java.util.Enumeration;

import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.constraints.ConstraintSolver;
import cbit.vcell.constraints.GeneralConstraint;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.AbstractConstraint;
import cbit.vcell.constraints.SimpleBounds;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import net.sourceforge.interval.ia_math.RealInterval;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2006 3:03:22 PM)
 * @author: Jim Schaff
 */
public class StructureSizeSolver {

/**
 * Insert the method's description here.
 * Creation date: (5/17/2006 10:33:38 AM)
 * @return double[]
 * @param structName java.lang.String
 * @param structSize double
 */
public static void updateAbsoluteStructureSizes(SimulationContext simContext, Structure struct, double structSize, VCUnitDefinition structSizeUnit) throws Exception {
	StructureMapping[] structMappings = simContext.getGeometryContext().getStructureMappings();
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();

		for (int i = 0; i < structMappings.length; i++){
			if (structMappings[i] instanceof MembraneMapping){
				MembraneMapping membraneMapping = (MembraneMapping)structMappings[i];

				String membraneSizeName = TokenMangler.mangleToSName(membraneMapping.getMembrane().getName()+"_size");
				ccImpl.addSimpleBound(new SimpleBounds(membraneSizeName,new RealInterval(0,100000),AbstractConstraint.PHYSICAL_LIMIT,"definition"));

				String volFractName = TokenMangler.mangleToSName(membraneMapping.getMembrane().getInsideFeature().getName()+"_volFract");
				String svRatioName = TokenMangler.mangleToSName(membraneMapping.getMembrane().getInsideFeature().getName()+"_svRatio");
				

				cbit.vcell.mapping.StructureMapping.StructureMappingParameter volFractParameter = membraneMapping.getVolumeFractionParameter();
				double volFractValue = volFractParameter.getExpression().evaluateConstant();
				ccImpl.addSimpleBound(new SimpleBounds(volFractName,new RealInterval(volFractValue,volFractValue),AbstractConstraint.MODELING_ASSUMPTION,"from model"));

				cbit.vcell.mapping.StructureMapping.StructureMappingParameter surfToVolParameter = membraneMapping.getSurfaceToVolumeParameter();
				double svRatioValue = surfToVolParameter.getExpression().evaluateConstant();
				ccImpl.addSimpleBound(new SimpleBounds(svRatioName,new RealInterval(svRatioValue,svRatioValue),AbstractConstraint.MODELING_ASSUMPTION,"from model"));

				//
				// EC eclosing cyt, which contains er and golgi
				// "(cyt_size+ er_size + golgi_size) * cyt_svRatio - PM_size == 0"
				//
				Feature insideFeature = membraneMapping.getMembrane().getInsideFeature();
				Expression sumOfInsideVolumeExp = new Expression(0.0);
				for (int j = 0; j < structMappings.length; j++){
					if (structMappings[j] instanceof FeatureMapping && ((FeatureMapping)structMappings[j]).getFeature().enclosedBy(insideFeature)) {
						Feature childFeatureOfInside = ((FeatureMapping)structMappings[j]).getFeature();
						sumOfInsideVolumeExp = Expression.add(sumOfInsideVolumeExp,new Expression(TokenMangler.mangleToSName(childFeatureOfInside.getName()+"_size")));
					}
				}
				Expression tempExpr = Expression.mult(sumOfInsideVolumeExp, new Expression(svRatioName));
				tempExpr = Expression.add(tempExpr, new Expression("-"+membraneSizeName));
				ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression(tempExpr.infix()+"==0"),AbstractConstraint.MODELING_ASSUMPTION,"svRatio definition"));

				//
				// EC eclosing cyt, which contains er and golgi
				// (EC_size + cyt_size + er_size + golgi_size) * cyt_vfRatio - (cyt_size + er_size + golgi_size) == 0
				//
				Feature outsideFeature = membraneMapping.getMembrane().getOutsideFeature();
				Expression sumOfParentVolumeExp = new Expression(0.0);
				for (int j = 0; j < structMappings.length; j++){
					if (structMappings[j] instanceof FeatureMapping && ((FeatureMapping)structMappings[j]).getFeature().enclosedBy(outsideFeature)){
						Feature childFeatureOfParent = ((FeatureMapping)structMappings[j]).getFeature();
						sumOfParentVolumeExp = Expression.add(sumOfParentVolumeExp,new Expression(TokenMangler.mangleToSName(childFeatureOfParent.getName()+"_size")));
					}
				}
				Expression exp = Expression.mult(sumOfParentVolumeExp,new Expression(volFractName));
				exp = Expression.add(exp, Expression.negate(sumOfInsideVolumeExp));
				ccImpl.addGeneralConstraint(new GeneralConstraint(new Expression(exp.infix()+"==0.0"),AbstractConstraint.MODELING_ASSUMPTION,"volFract definition"));
			}else if (structMappings[i] instanceof FeatureMapping){
				FeatureMapping featureMapping = (FeatureMapping)structMappings[i];
				String featureSizeName = TokenMangler.mangleToSName(featureMapping.getFeature().getName()+"_size");
				ccImpl.addSimpleBound(new SimpleBounds(featureSizeName,new RealInterval(0,1000000),AbstractConstraint.PHYSICAL_LIMIT,"definition"));
			}
		}
		
		ccImpl.addSimpleBound(new SimpleBounds(struct.getName()+"_size",new RealInterval(structSize,structSize),AbstractConstraint.OBSERVED_CONSTRAINT,"user input"));
		ConstraintSolver constraintSolver = new ConstraintSolver(ccImpl);

//try {
	//javax.swing.JFrame frame = new javax.swing.JFrame();
	//cbit.vcell.constraints.gui.ConstraintSolverPanel aConstraintSolverPanel;
	//aConstraintSolverPanel = new cbit.vcell.constraints.gui.ConstraintSolverPanel();
	//frame.setContentPane(aConstraintSolverPanel);
	//frame.setSize(aConstraintSolverPanel.getSize());
	//frame.addWindowListener(new java.awt.event.WindowAdapter() {
		//public void windowClosing(java.awt.event.WindowEvent e) {
			//System.exit(0);
		//};
	//});
	//frame.show();
	//java.awt.Insets insets = frame.getInsets();
	//frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
	
	//aConstraintSolverPanel.setConstraintContainerImpl(ccImpl);
	
	//frame.setVisible(true);
//} catch (Throwable exception) {
	//System.err.println("Exception occurred in main() of javax.swing.JPanel");
	//exception.printStackTrace(System.out);
//}
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
			for (int i = 0; i < symbols.length; i++){
				System.out.println("solution["+i+"] \""+symbols[i]+"\" = "+solution[i]);
				for (int j = 0; j < structMappings.length; j++){
					if (symbols[i].equals(TokenMangler.mangleToSName(structMappings[j].getStructure().getName()+"_size"))){
						if (!Double.isInfinite(solution[i].lo()) && !Double.isInfinite(solution[i].hi())) {
							structMappings[j].getSizeParameter().setExpression(new Expression((solution[i].lo()+solution[i].hi())/2));
						}
					}
				}
			}
		}else{
			//DialogUtils.showErrorDialog("cannot solve for size");
			throw new Exception("cannot solve for size");
		}
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new Exception(e.getMessage());
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new Exception(e.getMessage());
	}

	//try {
		//for (int i = 0; i < structMappings.length; i++){
			//structMappings[i].getSizeParameter().setExpression(new Expression(1.0));
		//}
	//} catch (java.beans.PropertyVetoException e) {
		//e.printStackTrace(System.out);
		//throw new RuntimeException("Error setting Structure size " + e.getMessage());
	//} catch (cbit.vcell.parser.ExpressionException e) {
		//e.printStackTrace(System.out);
		//throw new RuntimeException("Error setting Structure size " + e.getMessage());
	//}
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
	StructureMapping[] structMappings = simContext.getGeometryContext().getStructureMappings();
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();

		Structure struct = null;
		Expression totalVolExpr = new Expression(0.0);
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

				String membraneSizeName = TokenMangler.mangleToSName(membraneMapping.getMembrane().getName()+"_size");
				ccImpl.addSimpleBound(new SimpleBounds(membraneSizeName,new RealInterval(0,100000),AbstractConstraint.PHYSICAL_LIMIT,"definition"));

				String volFractName = TokenMangler.mangleToSName(membraneMapping.getMembrane().getInsideFeature().getName()+"_volFract");
				String svRatioName = TokenMangler.mangleToSName(membraneMapping.getMembrane().getInsideFeature().getName()+"_svRatio");
				

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
					Feature insideFeature = membraneMapping.getMembrane().getInsideFeature();
					Expression sumOfInsideVolumeExp = new Expression(0.0);
					for (int j = 0; j < structMappings.length; j++){
						if (structMappings[j] instanceof FeatureMapping && ((FeatureMapping)structMappings[j]).getFeature().enclosedBy(insideFeature)) {
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
					Feature outsideFeature = membraneMapping.getMembrane().getOutsideFeature();
					Expression sumOfParentVolumeExp = new Expression(0.0);
					for (int j = 0; j < structMappings.length; j++){
						if (structMappings[j] instanceof FeatureMapping && ((FeatureMapping)structMappings[j]).getFeature().enclosedBy(outsideFeature)){
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
		for(int i =0; i< structureMappings.length; i++)
		{
			if(structureMappings[i] instanceof MembraneMapping)
			{
				//calculate the sum of features' sizes inside this membrane, this is used for calculating both surface volume ratio and volume fraction.
				double sumOfSubFeatures = 0;
				Enumeration<Feature> subFeatures = ((MembraneMapping)structureMappings[i]).getMembrane().getInsideFeature().getSubFeatures();
				while(subFeatures.hasMoreElements())
				{
					Feature feature = subFeatures.nextElement();
					sumOfSubFeatures = sumOfSubFeatures + simContext.getGeometryContext().getStructureMapping(feature).getSizeParameter().getExpression().evaluateConstant();
				}
				//calculate the sum of features's sizes inside the membrance's parent feature, this is used for calculating the volume fraction.
				double sumOfParentMemSubFeatures = 0;
				Feature parentFeature = ((MembraneMapping)structureMappings[i]).getMembrane().getOutsideFeature();
				if(parentFeature != null)
				{
					Enumeration<Feature> parentSubFeatures = parentFeature.getSubFeatures();
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
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new Exception(e.getMessage());
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new Exception(e.getMessage());
	}
}
}