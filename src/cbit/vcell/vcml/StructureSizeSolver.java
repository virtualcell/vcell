package cbit.vcell.vcml;
import cbit.vcell.model.Feature;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.parser.Expression;
import cbit.vcell.constraints.GeneralConstraint;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.AbstractConstraint;
import cbit.vcell.constraints.SimpleBounds;
import cbit.gui.DialogUtils;
import cbit.util.TokenMangler;
import net.sourceforge.interval.ia_math.RealInterval;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2006 3:03:22 PM)
 * @author: Jim Schaff
 */
public class StructureSizeSolver implements StructureSizeEvaluator {
/**
 * StructureSizeSolver constructor comment.
 */
public StructureSizeSolver() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/17/2006 10:33:38 AM)
 * @return double[]
 * @param structName java.lang.String
 * @param structSize double
 */
public void updateAbsoluteStructureSizes(cbit.vcell.mapping.SimulationContext simContext, cbit.vcell.model.Structure struct, double structSize, cbit.vcell.units.VCUnitDefinition structSizeUnit) {
	cbit.vcell.mapping.StructureMapping[] structMappings = simContext.getGeometryContext().getStructureMappings();
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
		cbit.vcell.constraints.ConstraintSolver constraintSolver = new cbit.vcell.constraints.ConstraintSolver(ccImpl);
		
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
			DialogUtils.showErrorDialog("cannot solve for size");
			throw new RuntimeException("cannot solve for size");
		}
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
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
public void updateRelativeStructureSizes(cbit.vcell.mapping.SimulationContext simContext) {

	if (simContext.getGeometry().getDimension() > 0){
		throw new RuntimeException("not yet supported for spatial applications");
	}
	
	cbit.vcell.mapping.StructureMapping[] structureMappings = simContext.getGeometryContext().getStructureMappings();
	try {
		for (int i = 0; i < structureMappings.length; i++){
			if (structureMappings[i] instanceof MembraneMapping){
				MembraneMapping membraneMapping = (MembraneMapping)structureMappings[i];
				Expression membraneSizeExp = membraneMapping.getSizeParameter().getExpression();
				Expression insideFeatureSizeExp = simContext.getGeometryContext().getStructureMapping(membraneMapping.getMembrane().getInsideFeature()).getSizeParameter().getExpression();
				// set surface/volume ratio
				membraneMapping.getSurfaceToVolumeParameter().setExpression(new Expression(membraneSizeExp.evaluateConstant()/insideFeatureSizeExp.evaluateConstant()));

				//
				// sum up entire volume of parent feature and all sibling features (including self)
				// volume fraction will be size of enclosed volume divided by total size of surrounding volume.
				//
				Expression outsideFeatureSizeExp = simContext.getGeometryContext().getStructureMapping(membraneMapping.getMembrane().getOutsideFeature()).getSizeParameter().getExpression();
				double sumOfParentAndSiblingVolumes = outsideFeatureSizeExp.evaluateConstant();

				for (int j=0;j<structureMappings.length;j++){
					if (structureMappings[j] instanceof MembraneMapping){
						MembraneMapping mm = (MembraneMapping)structureMappings[j];
						if (mm.getMembrane().getOutsideFeature() == membraneMapping.getMembrane().getOutsideFeature()){
							Expression childVolumeExp = simContext.getGeometryContext().getStructureMapping(mm.getMembrane().getInsideFeature()).getSizeParameter().getExpression();
							sumOfParentAndSiblingVolumes += childVolumeExp.evaluateConstant();
						}
					}
				}
				// set volume fraction
				membraneMapping.getVolumeFractionParameter().setExpression(new Expression(insideFeatureSizeExp.evaluateConstant()/sumOfParentAndSiblingVolumes));
			}
		}
	}catch (NullPointerException e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog("structure sizes must all be specified");
		throw new RuntimeException("structure sizes must all be specified");
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}