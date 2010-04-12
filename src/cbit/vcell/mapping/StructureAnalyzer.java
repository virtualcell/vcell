package cbit.vcell.mapping;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Vector;

import org.vcell.util.BeanUtils;

import cbit.vcell.matrix.RationalMatrix;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.matrix.RationalNumberMatrix;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
/**
 * class StructureAnalyzer
 * 
 * Create full matrix for a subdomain, fluxes (either binding or true fluxes) are
 * decoupled to maintain integer matrix elements
 *  
 * This class fills in the SpeciesContextMapping objects with the kinetics and dependency relationships
 *
 */
public abstract class StructureAnalyzer {

 	//
 	// fundamental information about system (sim context stuff)
 	//
	protected MathMapping mathMapping = null;

	//
	// working list of reactions and speciesContexts for indexing
	//
	// see refreshSpeciesContextMapping()
	//
	// order of speciesContextMapping array is ordered as [fast-non-diffusable , non-diffusable , diffusible ]
	//
	protected Structure             structures[] = null;
	protected ReactionStep          reactionSteps[] = null;
	protected SpeciesContextMapping speciesContextMappings[] = null;
	
	//
	// matrices needed for structural analysis
	//
	private RationalMatrix totalSchemeMatrix = null;
	private RationalMatrix totalNullSpaceMatrix = null;
	
	//
	// working list of fast reactions and associated speciesContexts for indexing
	//
	protected ReactionStep          fastReactionSteps[] = null;
	protected SpeciesContextMapping fastSpeciesContextMappings[] = null;
	
	//
	// matrices needed for fast system analysis
	//
	private RationalMatrix fastSchemeMatrix = null;
	private RationalMatrix fastNullSpaceMatrix = null;

	public static class Dependency {
		SpeciesContextMapping speciesContextMapping = null;
		String invariantSymbolName = null;
		Expression conservedMoietyExpression = null;
		Expression dependencyExpression = null;
	};

/**
 * StructureAnalyzer constructor comment.
 */
public StructureAnalyzer(MathMapping mathMapping) {
	this.mathMapping = mathMapping;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SpeciesContextMapping
 */
public SpeciesContextMapping[] getFastSpeciesContextMappings() {
	return fastSpeciesContextMappings;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionStep[]
 */
public ReactionStep[] getReactionSteps() {
	return reactionSteps;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SpeciesContextMapping[]
 */
public SpeciesContextMapping[] getSpeciesContextMappings() {
	return speciesContextMappings;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure[]
 */
public Structure[] getStructures() {
	return structures;
}


/**
 * This method was created in VisualAge.
 * @param o java.util.Observable
 * @param obj java.lang.Object
 */
public void refresh() {
	try {
//System.out.println("StructureAnalyzer.refresh()");
		refreshStructures();
		refreshTotalSpeciesContextMappings();
		if (speciesContextMappings!=null && reactionSteps!=null){
			refreshTotalMatrices();
			refreshTotalDependancies();
		}
		refreshFastSpeciesContextMappings();
		if (fastSpeciesContextMappings!=null){
			refreshFastMatrices();
			refreshFastSystem();
			substituteIntoFastSystem();
		}
	} catch (Exception e){
		System.out.println("Exception caught in StructureAnalyzer.update()");
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshFastMatrices() throws Exception {

//System.out.println("StructureAnalyzer.refreshFastMatrices()");
	//
	// update scheme matrix for fast system
	//
	fastSchemeMatrix = new RationalNumberMatrix(fastSpeciesContextMappings.length,fastReactionSteps.length);
	for (int i=0;i<fastSpeciesContextMappings.length;i++){
		for (int j=0;j<fastReactionSteps.length;j++){
			fastSchemeMatrix.set_elem(i,j,fastReactionSteps[j].getStoichiometry(fastSpeciesContextMappings[i].getSpeciesContext()));
		}
	}

	//
	// initialize rate expressions for speciesContext's due to scheme matrix
	//
	for (int i=0;i<fastSpeciesContextMappings.length;i++){
		SpeciesContextMapping scm = fastSpeciesContextMappings[i];
		SpeciesContext sc = scm.getSpeciesContext();
		//
		// collect fast rate expression
		//
		Expression exp = new Expression(0.0);
		for (int j=0;j<fastReactionSteps.length;j++){
			int stoichiometry = fastReactionSteps[j].getStoichiometry(sc);
			ReactionSpec reactionSpec = mathMapping.getSimulationContext().getReactionContext().getReactionSpec(fastReactionSteps[j]);
			if (stoichiometry != 0){
				if (!reactionSpec.isFast()){
					throw new Exception("expected only fast rates");
				}
				if (reactionSpec.isExcluded()){
					throw new Exception("expected only included rates");
				}
				ReactionParticipant[] rps = fastReactionSteps[j].getReactionParticipants();
				ReactionParticipant rp0 = null;
				for (ReactionParticipant rp : rps) {
					if (rp.getSpeciesContext() == sc) {
						rp0 = rp;
						break;
					}
				}
				//
				// if reaction is on membrane and reactionParticipant isn't, then add flux correction
				//
				if (rp0 != null) {
					Structure structure = fastReactionSteps[j].getStructure();
					Expression fastRateExpression = fastReactionSteps[j].getReactionRateExpression(rp0).renameBoundSymbols(mathMapping.getNameScope());
					if ((structure instanceof Membrane) && (rp0.getStructure()!=structure)){
						Membrane membrane = (Membrane)structure;
						MembraneMapping membraneMapping = (MembraneMapping)mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
						Expression fluxCorrection = new Expression(mathMapping.getFluxCorrectionParameter(membraneMapping,(Feature)rp0.getStructure()), mathMapping.getNameScope());
						exp = Expression.add(exp,Expression.mult(fluxCorrection, fastRateExpression));
					}else{
						exp = Expression.add(exp,new Expression(fastRateExpression));
					}
				}
			}
		}
//		exp.bindExpression(mathMapping);
		scm.setFastRate(exp.flatten());
	}	

//	System.out.println("StructureAnalyzer.refreshFastMatrices(), scheme matrix:");
//	fastSchemeMatrix.show();
	
	//
	// update null space matrix
	//
	fastNullSpaceMatrix = fastSchemeMatrix.findNullSpace();

//	if (fastNullSpaceMatrix==null){
//		System.out.println("fast system has full rank");
//	}else{
//		System.out.println("StructureAnalyzer.refreshFastMatrices(), nullSpace matrix:");
//		fastNullSpaceMatrix.show();
//	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshFastSpeciesContextMappings() {

//System.out.println("StructureAnalyzer.refreshFastSpeciesContextMappings()");

	//GeometryContext geoContext = mathMapping.getSimulationContext().getGeometryContext();
	Vector<SpeciesContextMapping> scFastList = new Vector<SpeciesContextMapping>();
	//
	// for each structure, get all independent, non-constant speciesContext's
	//
	if (structures == null){
		return;
	}
	for (int i=0;i<structures.length;i++){
		SpeciesContext speciesContexts[] = mathMapping.getSimulationContext().getReactionContext().getModel().getSpeciesContexts(structures[i]);
		for (int j=0;j<speciesContexts.length;j++){
			SpeciesContext sc = speciesContexts[j];
			SpeciesContextMapping scm = mathMapping.getSpeciesContextMapping(sc);
			SpeciesContextSpec scs = mathMapping.getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
			if (scm.getDependencyExpression()==null && scs.isConstant()==false){   // right now all independent vars
				scFastList.addElement(scm);
			}
		}
	}
	if (scFastList.size()>0){
		fastSpeciesContextMappings = new SpeciesContextMapping[scFastList.size()];
		scFastList.copyInto(fastSpeciesContextMappings);
//for (int i=0;i<fastSpeciesContextMappings.length;i++){
//System.out.println("fastSpeciesContextMappings["+i+"] = "+fastSpeciesContextMappings[i].getSpeciesContext().getName());
//}
	}else{
		fastSpeciesContextMappings=null;
	}
//System.out.println("StructureAnalyzer.refreshFastSpeciesContextMapping(), fastSpeciesContextMappings.length = "+scFastList.size());
	
	//
	// for each reaction, get all reactionSteps associated with these structures
	//
	Vector<ReactionStep> rsFastList = new Vector<ReactionStep>();
	ReactionSpec reactionSpecs[] = mathMapping.getSimulationContext().getReactionContext().getReactionSpecs();
	for (int i=0;i<reactionSpecs.length;i++){
		ReactionStep rs = reactionSpecs[i].getReactionStep();
		if (reactionSpecs[i].isExcluded()){
			continue;
		}
		for (int j=0;j<structures.length;j++){
			if (rs.getStructure()==structures[j]){
				if (reactionSpecs[i].isFast()){
					rsFastList.addElement(rs);
				}
			}
		}
	}
	if (rsFastList.size()>0){
		fastReactionSteps = new ReactionStep[rsFastList.size()];
		rsFastList.copyInto(fastReactionSteps);
	}else{
		fastReactionSteps=null;
		fastSpeciesContextMappings=null;
	}
//System.out.println("StructureAnalyzer.refreshFastSpeciesContextMapping(), reactionSteps.length = "+scFastList.size());
}


/**
 * Find which species are fast-independent, use their rates as the "fastRate"s.
 * null space rows correspond to "fastInvariant".
 */
private void refreshFastSystem() throws Exception {

	//
	// reset fast system characteristics for all species contexts
	//
	for (int v=0;v<fastSpeciesContextMappings.length;v++){
		fastSpeciesContextMappings[v].setFastInvariant(null);
	}

	if (fastNullSpaceMatrix == null){
		//System.out.println("the fast matrix has full rank, there are no fast invariants");
		return;
	}
		
	if (fastSpeciesContextMappings.length != fastNullSpaceMatrix.getNumCols()){
		throw new Exception("varName array not same dimension as b matrix");
	}
				
	StructureAnalyzer.Dependency[] dependencies = refreshTotalDependancies(fastNullSpaceMatrix,fastSpeciesContextMappings,mathMapping,true);

	for (int i = 0; i < dependencies.length; i++){
		//String constantName = dependencies[i].invariantSymbolName;
		Expression constantExp = dependencies[i].conservedMoietyExpression;
		SpeciesContextMapping firstSCM = dependencies[i].speciesContextMapping;
		//Expression exp = dependencies[i].dependencyExpression;
		
		//
		// store fastInvariant (e.g. (K_fast_total= xyz + wzy)
		//
		constantExp.bindExpression(mathMapping);
		firstSCM.setFastInvariant(constantExp.flatten());
	}	

}


/**
 * This method was created in VisualAge.
 */
protected abstract void refreshStructures();


/**
 * This method was created by a SmartGuide.
 * @param b cbit.vcell.math.Matrix
 * @param vars java.lang.String[]
 */
private void refreshTotalDependancies() throws Exception {

//System.out.println("StructureAnalyzer.refreshTotalDependancies()");
	//SimulationContext simContext = mathMapping.getSimulationContext();

	//
	// reset dependancy relationships for all species contexts
	//
	for (int v=0;v<speciesContextMappings.length;v++){
		speciesContextMappings[v].setDependencyExpression(null);
	}

	if (totalNullSpaceMatrix == null){
		//System.out.println("the matrix has full rank, there are no dependencies");
		return;
	}
		
	if (speciesContextMappings.length != totalNullSpaceMatrix.getNumCols()){
		throw new Exception("varName array not same dimension as b matrix");
	}
				
	StructureAnalyzer.Dependency[] dependencies = refreshTotalDependancies(totalNullSpaceMatrix,speciesContextMappings,mathMapping,false);

	for (int i = 0; i < dependencies.length; i++){
		String constantName = dependencies[i].invariantSymbolName;
		Expression constantExp = dependencies[i].conservedMoietyExpression;
		SpeciesContextMapping firstSCM = dependencies[i].speciesContextMapping;
		Expression exp = dependencies[i].dependencyExpression;
		//
		// store totalMass parameter (e.g. K_xyz_total = xyz_init + wzy_init) 
		//
		MathMapping.MathMappingParameter totalMassParameter = mathMapping.addMathMappingParameter(constantName,constantExp.flatten(),MathMapping.PARAMETER_ROLE_TOTALMASS,VCUnitDefinition.UNIT_uM);
		//
		// store dependency parameter (e.g. xyz = K_xyz_total - wzy)
		//
		exp.bindExpression(mathMapping);
		firstSCM.setDependencyExpression(exp);
	}	
}


/**
 * This method was created by a SmartGuide.
 * @param b cbit.vcell.math.Matrix
 * @param vars java.lang.String[]
 */
public static StructureAnalyzer.Dependency[] refreshTotalDependancies(RationalMatrix nullSpaceMatrix, SpeciesContextMapping[] speciesContextMappings, 
		MathMapping mathMapping_temp, boolean bFast) throws Exception {

//System.out.println("StructureAnalyzer.refreshTotalDependancies()");
	SimulationContext simContext_temp = mathMapping_temp.getSimulationContext();

	//
	// reset dependancy relationships for all species contexts
	//
	//for (int v=0;v<speciesContextMappings.length;v++){
		//speciesContextMappings[v].setDependencyExpression(null);
	//}

	if (nullSpaceMatrix == null){
		//System.out.println("the matrix has full rank, there are no dependencies");
		return new StructureAnalyzer.Dependency[0];
	}
		
	if (speciesContextMappings.length != nullSpaceMatrix.getNumCols()){
		//throw new Exception("varName array not same dimension as b matrix");
		System.out.println("varName array not same dimension as b matrix");
		nullSpaceMatrix.show();
		for (int i = 0; i < speciesContextMappings.length; i++){
			System.out.println("scm["+i+"] is "+speciesContextMappings[i].getSpeciesContext().getName());
		}
	}
				
//System.out.println("there are "+nullSpaceMatrix.rows+" dependencies");

	Vector<Dependency> dependencyList = new Vector<Dependency>();
	
	for (int i=0;i<nullSpaceMatrix.getNumRows();i++){
		//
		// find first variable
		//
		Expression exp = null;
		Expression constantExp = null;
		String     constantName = null;
		SpeciesContextMapping firstSCM = null;
		boolean bFirst = true;
		for (int j=0;j<nullSpaceMatrix.getNumCols();j++){
			RationalNumber coeff = nullSpaceMatrix.get_elem(i,j);
			if (coeff.doubleValue() != 0.0){
				if (bFirst){
					if (coeff.doubleValue() != 1.0){
						System.out.println("i="+i+" j="+j);
						nullSpaceMatrix.show();
						throw new Exception("expecting a coefficient of 1.0, instead coeff = "+coeff);
					}
					firstSCM = speciesContextMappings[j];
					//
					// first term of dependancy expression   ("K_CalciumCyt")
					//
					SpeciesContext firstSC = firstSCM.getSpeciesContext();
					constantName = "K_"+firstSC.getName()+"_total";
					exp = new Expression(constantName);
					//
					// first term of K expression
					//
					StructureMapping firstSM = simContext_temp.getGeometryContext().getStructureMapping(firstSC.getStructure());
					SpeciesContextSpec firstSCS = simContext_temp.getReactionContext().getSpeciesContextSpec(firstSC);
					SymbolTableEntry scSTE = null;
					if (bFast){
						scSTE = firstSCS.getSpeciesContext();
					}else{
						scSTE = firstSCS.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
					}
					constantExp = Expression.mult(new Expression(coeff.toString()),firstSM.getNormalizedConcentrationCorrection(simContext_temp),
																new Expression(scSTE, mathMapping_temp.getNameScope()));
					bFirst = false;
				}else{
					//
					// add term to dependancy expression     (" - 2*IP3Cyt ")
					//
					SpeciesContextMapping scm = speciesContextMappings[j];
					SpeciesContext sc = scm.getSpeciesContext();
					StructureMapping sm = simContext_temp.getGeometryContext().getStructureMapping(sc.getStructure());
					SpeciesContextSpec scs = simContext_temp.getReactionContext().getSpeciesContextSpec(sc);
					exp = Expression.add(exp,Expression.negate(Expression.mult(new Expression(coeff.toString()), 
							sm.getNormalizedConcentrationCorrection(simContext_temp), new Expression(sc, mathMapping_temp.getNameScope()))));
					//
					// add term to K expression
					//
					SymbolTableEntry scSTE = null;
					if (bFast){
						scSTE = sc;
					}else{
						scSTE = scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
					}
					constantExp = Expression.add(constantExp, Expression.mult(new Expression(coeff.toString()), sm.getNormalizedConcentrationCorrection(simContext_temp),
																				new Expression(scSTE, mathMapping_temp.getNameScope())));
				}
			}
		}
		if (firstSCM!=null){
			//
			// store totalMass parameter (e.g. K_xyz_total = xyz_init + wzy_init) 
			//
			//MathMapping.MathMappingParameter totalMassParameter = mathMapping.addMathMappingParameter(constantName,constantExp.flatten(),MathMapping.PARAMETER_ROLE_TOTALMASS,cbit.vcell.units.VCUnitDefinition.UNIT_uM);
			//
			// store dependency parameter (e.g. xyz = K_xyz_total - wzy)
			//
			StructureMapping sm = simContext_temp.getGeometryContext().getStructureMapping(firstSCM.getSpeciesContext().getStructure());
			exp = Expression.mult(exp,Expression.invert(sm.getNormalizedConcentrationCorrection(simContext_temp)));
			exp = exp.flatten();
			//exp.bindExpression(mathMapping_temp);
			//firstSCM.setDependencyExpression(exp);
			
			Dependency dependency = new Dependency();
			dependency.dependencyExpression = exp;
			dependency.speciesContextMapping = firstSCM;
			dependency.invariantSymbolName = constantName;
			dependency.conservedMoietyExpression = constantExp.flatten();

			dependencyList.add(dependency);
		}
	}
	return (StructureAnalyzer.Dependency[])BeanUtils.getArray(dependencyList,StructureAnalyzer.Dependency.class);
}


/**
 * This method was created in VisualAge.
 */
private void refreshTotalMatrices() throws Exception {

//System.out.println("StructureAnalyzer.refreshTotalMatrices()");
	//
	// update scheme matrix for full system (slow and fast)
	//
	ReactionSpec[] reactionSpecs = new ReactionSpec[reactionSteps.length];
	for (int j=0;j<reactionSteps.length;j++){
		reactionSpecs[j] = mathMapping.getSimulationContext().getReactionContext().getReactionSpec(reactionSteps[j]);
	}
	//
	// initialize rate expressions for speciesContext's due to scheme matrix
	//
	totalSchemeMatrix = new RationalNumberMatrix(speciesContextMappings.length,reactionSteps.length);
	for (int i=0;i<speciesContextMappings.length;i++){
		SpeciesContextMapping scm = speciesContextMappings[i];
		SpeciesContext sc = scm.getSpeciesContext();
		//
		// collect slow rate expression (fast handled by FastSystem)
		//
		Expression exp = new Expression(0.0);
		for (int j=0;j<reactionSteps.length;j++){
			int stoichiometry = reactionSteps[j].getStoichiometry(sc);
			
			totalSchemeMatrix.set_elem(i,j,stoichiometry);
			
			if (stoichiometry != 0){
				if (!(reactionSteps[j] instanceof DiffusionReactionStep) && !(reactionSteps[j] instanceof EventReactionStep) &&
					!reactionSpecs[j].isFast() && !reactionSpecs[j].isExcluded()){
					ReactionParticipant[] rps1 = reactionSteps[j].getReactionParticipants();
					ReactionParticipant rp0 = null;
					for (ReactionParticipant rp : rps1) {
						if (rp.getSpeciesContext() == sc) {
							rp0 = rp;
							break;
						}
					}
					Structure structure = reactionSteps[j].getStructure();
					//
					// if reaction is in one compartment and reactionParticipant is in another, then add a flux correction.
					// (e.g. if reaction is on membrane and reactionParticipant is in a feature)
					//
					if (rp0 != null) {
						Expression reactRateExp = reactionSteps[j].getReactionRateExpression(rp0).renameBoundSymbols(mathMapping.getNameScope());
						if ((structure instanceof Membrane) && (sc.getStructure()!=structure)){
							Membrane membrane = (Membrane)structure;
							MembraneMapping membraneMapping = (MembraneMapping)mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
							Parameter fluxCorrectionParameter = mathMapping.getFluxCorrectionParameter(membraneMapping,(Feature)sc.getStructure());
							Expression fluxCorrection = new Expression(fluxCorrectionParameter, mathMapping.getNameScope());
							if (reactionSteps[j] instanceof FluxReaction){
								exp = Expression.add(exp, Expression.mult(fluxCorrection, reactRateExp));
								//Expression.add(exp,new Expression(fluxCorrectionParameterSymbolName+"*"+expInfix));
							}else if (reactionSteps[j] instanceof SimpleReaction){
								exp = Expression.add(exp, Expression.mult(fluxCorrection, new Expression(ReservedSymbol.KMOLE, mathMapping.getNameScope()), reactRateExp));
//								exp = Expression.add(exp,new Expression(fluxCorrectionParameterSymbolName+"*"+ReservedSymbol.KMOLE.getName()+"*"+expInfix));
							}else{
								throw new RuntimeException("Internal Error: expected ReactionStep "+reactionSteps[j]+" to be of type SimpleReaction or FluxReaction");
							}
						}else{
							exp = Expression.add(exp, reactRateExp);
						}
					}
				}
			}
		}
//		exp.bindExpression(mathMapping);
		scm.setRate(exp.flatten());
	}	
//System.out.println("StructureAnalyzer.refreshTotalMatrices(), reactionSteps");
//for (int i=0;i<reactionSteps.length;i++){
//	System.out.println("reactionStep["+i+"] = "+reactionSteps[i].toString());
//}
	
//	System.out.println("StructureAnalyzer.refreshTotalMatrices(), scheme matrix:");
//	totalSchemeMatrix.show();
	
	//
	// update null space matrix
	//
	if (totalSchemeMatrix.getNumRows()>1){
		totalNullSpaceMatrix = (RationalMatrix)totalSchemeMatrix.findNullSpace();
	}else{
		totalNullSpaceMatrix = null;
	}
		

//	if (totalNullSpaceMatrix==null){
//		System.out.println("total system has full rank");
//	}else{
//		System.out.println("StructureAnalyzer.refreshTotalMatrices(), nullSpace matrix:");
//		totalNullSpaceMatrix.show();
//	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshTotalSpeciesContextMappings() throws java.beans.PropertyVetoException {

	if (structures == null){
		return;
	}
	
//System.out.println("StructureAnalyzer.refreshSpeciesContextMappings()");

	//GeometryContext geoContext = mathMapping.getSimulationContext().getGeometryContext();
	Model model = mathMapping.getSimulationContext().getReactionContext().getModel();
	//
	// note, the order of species is specified such that the first species have priority
	//       when the null space is solved for dependent variables.  So the order of priority
	//       for elimination are as follows:
	//
	//          1) Species involved with fast reactions.
	//          2) Species not involved with fast reactions.
	//
	Vector<SpeciesContextMapping> scmList = new Vector<SpeciesContextMapping>();

	//
	// for each structure, get all non-constant speciesContext's that participate in fast reactions
	//
	for (int i=0;i<structures.length;i++){
		SpeciesContext speciesContexts[] = model.getSpeciesContexts(structures[i]);
		for (int j=0;j<speciesContexts.length;j++){
			SpeciesContext sc = speciesContexts[j];
			SpeciesContextMapping scm = mathMapping.getSpeciesContextMapping(sc);
			SpeciesContextSpec scs = mathMapping.getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
			if (scm.isFastParticipant() && !scs.isConstant()){
				scmList.addElement(scm);
			}
		}
	}
	//
	// for each structure, get all non-constant speciesContext's that DO NOT participate in fast reactions
	//
	for (int i=0;i<structures.length;i++){
		SpeciesContext speciesContexts[] = model.getSpeciesContexts(structures[i]);
		for (int j=0;j<speciesContexts.length;j++){
			SpeciesContext sc = speciesContexts[j];
			SpeciesContextMapping scm = mathMapping.getSpeciesContextMapping(sc);
			SpeciesContextSpec scs = mathMapping.getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
			if (!scm.isFastParticipant() && !scs.isConstant()){
				scmList.addElement(scm);
			}
		}
	}
	if (scmList.size()>0){
		speciesContextMappings = new SpeciesContextMapping[scmList.size()];
		scmList.copyInto(speciesContextMappings);
		for (int i=0;i<speciesContextMappings.length;i++){
			speciesContextMappings[i].setRate(new Expression(0.0));
//System.out.println("speciesContextMappings["+i+"] = "+speciesContextMappings[i].getSpeciesContext().getName());
		}
	}else{
		speciesContextMappings=null;
	}
//System.out.println("StructureAnalyzer.refreshTotalSpeciesContextMapping(), speciesContextMappings.length = "+scmList.size());
	
	//
	// get all reactionSteps associated with these structures
	//
	Vector<ReactionStep> rsList = new Vector<ReactionStep>();
	ReactionSpec allReactionSpecs[] = mathMapping.getSimulationContext().getReactionContext().getReactionSpecs();
	for (int i=0;i<allReactionSpecs.length;i++){
		if (allReactionSpecs[i].isExcluded()){
			continue;
		}
		ReactionStep rs = allReactionSpecs[i].getReactionStep();
		for (int j=0;j<structures.length;j++){
			if (rs.getStructure()==structures[j]){
				rsList.addElement(rs);
			}
		}
	}
	//
	// add DiffusionReactionStep for each diffusing species and EventReactionStep for each speciesContext that has 
	// event assignment(s) [that is a target variable in event assignment(s)] to eliminate those species from conserved moieties
	//
	for (int i=0;i<scmList.size();i++){
		SpeciesContextMapping scm = (SpeciesContextMapping)scmList.elementAt(i);
		if (scm.isPDERequired()){
			rsList.addElement(new DiffusionReactionStep("DiffusionReactionStep"+i,scm.getSpeciesContext().getStructure(), scm.getSpeciesContext()));
		}
		if (scm.hasEventAssignment()){
			rsList.addElement(new EventReactionStep("EventReactionStep"+i,scm.getSpeciesContext().getStructure(), scm.getSpeciesContext()));
		}
	}
	if (rsList.size()>0){
		reactionSteps = new ReactionStep[rsList.size()];
		rsList.copyInto(reactionSteps);
	}else{
		reactionSteps=null;
	}
//System.out.println("StructureAnalyzer.refreshTotalSpeciesContextMapping(), reactionSteps.length = "+scmList.size());
}


/**
 * This method was created in VisualAge.
 */
private void substituteIntoFastSystem() throws Exception {
	
//for (int i=0;i<fastSpeciesContextMappings.length;i++){
//	SpeciesContextMapping scm = fastSpeciesContextMappings[i];
//	if (scm.fastInvariant!=null){
//		System.out.println("fastInvariant for "+scm.getSpeciesContext().getName()+" = "+scm.fastInvariant.toString());
//	}else{
//		System.out.println("fastRate for "+scm.getSpeciesContext().getName()+" = "+scm.fastRate.toString());
//	}
//}

	//
	// substitute independent variables where possible
	//
	for (int j=0;j<speciesContextMappings.length;j++){
		if (speciesContextMappings[j].getDependencyExpression() != null){
			Expression dependentVar = new Expression(speciesContextMappings[j].getSpeciesContext(), mathMapping.getNameScope());
			Expression dependentExp = new Expression(speciesContextMappings[j].getDependencyExpression());
//System.out.println("trying to substitute '"+dependentExp.toString()+"' for variable '"+dependentVar.toString()+"'"); 
			for (int i=0;i<fastSpeciesContextMappings.length;i++){
				SpeciesContextMapping scm = fastSpeciesContextMappings[i];
				if (scm.getFastInvariant()!=null){
					scm.getFastInvariant().substituteInPlace(dependentVar,dependentExp);
					scm.setFastInvariant(scm.getFastInvariant().flatten());
				}else{
					scm.getFastRate().substituteInPlace(dependentVar,dependentExp);
					scm.setFastRate(scm.getFastRate().flatten());
				}
			}
		}
	}
	
//for (int i=0;i<fastSpeciesContextMappings.length;i++){
//	SpeciesContextMapping scm = fastSpeciesContextMappings[i];
//	if (scm.fastInvariant!=null){
//		System.out.println("fastInvariant for "+scm.getSpeciesContext().getName()+" = "+scm.fastInvariant.toString());
//	}else{
//		System.out.println("fastRate for "+scm.getSpeciesContext().getName()+" = "+scm.fastRate.toString());
//	}
//}
}
}