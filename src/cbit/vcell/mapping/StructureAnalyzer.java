package cbit.vcell.mapping;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import cbit.vcell.model.*;
import cbit.vcell.geometry.*;
import cbit.vcell.parser.*;
import cbit.vcell.math.*;
import cbit.vcell.matrix.RationalMatrixFast;
import cbit.vcell.matrix.RationalNumber;
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
	private RationalMatrixFast totalSchemeMatrix = null;
	private RationalMatrixFast totalNullSpaceMatrix = null;
	
	//
	// working list of fast reactions and associated speciesContexts for indexing
	//
	protected ReactionStep          fastReactionSteps[] = null;
	protected SpeciesContextMapping fastSpeciesContextMappings[] = null;
	
	//
	// matrices needed for fast system analysis
	//
	private RationalMatrixFast fastSchemeMatrix = null;
	private RationalMatrixFast fastNullSpaceMatrix = null;

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
	}catch (Exception e){
		System.out.println("Exception caught in StructureAnalyzer.update()");
		e.printStackTrace(System.out);
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
	fastSchemeMatrix = new RationalMatrixFast(fastSpeciesContextMappings.length,fastReactionSteps.length);
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
				ReactionParticipant[] rps = fastReactionSteps[j].getReactionParticipants(sc);
				//
				// if reaction is on membrane and reactionParticipant isn't, then add flux correction
				//
				if (rps.length > 0) {
					Structure structure = fastReactionSteps[j].getStructure();
					if ((structure instanceof Membrane) && (rps[0].getStructure()!=structure)){
						Membrane membrane = (Membrane)structure;
						MembraneMapping membraneMapping = (MembraneMapping)mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
						Parameter fluxCorrectionParameter = mathMapping.getFluxCorrectionParameter(membraneMapping,(Feature)rps[0].getStructure());
						exp = Expression.add(exp,new Expression(mathMapping.getNameScope().getSymbolName(fluxCorrectionParameter)+"* ("+fastReactionSteps[j].getRateExpression(rps[0]).infix(mathMapping.getNameScope())+")"));
					}else{
						exp = Expression.add(exp,new Expression(fastReactionSteps[j].getRateExpression(rps[0]).infix(mathMapping.getNameScope())));
					}
				}
			}
		}
		exp.bindExpression(mathMapping);
		scm.setFastRate(exp.flatten());
	}	

//	System.out.println("StructureAnalyzer.refreshFastMatrices(), scheme matrix:");
//	fastSchemeMatrix.show();
	
	//
	// update null space matrix
	//
	fastNullSpaceMatrix = (RationalMatrixFast)fastSchemeMatrix.findNullSpace();

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

	GeometryContext geoContext = mathMapping.getSimulationContext().getGeometryContext();
	Vector scFastList = new Vector();
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
	Vector rsFastList = new Vector();
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
		String constantName = dependencies[i].invariantSymbolName;
		Expression constantExp = dependencies[i].conservedMoietyExpression;
		SpeciesContextMapping firstSCM = dependencies[i].speciesContextMapping;
		Expression exp = dependencies[i].dependencyExpression;
		
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
	SimulationContext simContext = mathMapping.getSimulationContext();

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
		MathMapping.MathMappingParameter totalMassParameter = mathMapping.addMathMappingParameter(constantName,constantExp.flatten(),MathMapping.PARAMETER_ROLE_TOTALMASS,cbit.vcell.units.VCUnitDefinition.UNIT_uM);
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
public static StructureAnalyzer.Dependency[] refreshTotalDependancies(RationalMatrixFast nullSpaceMatrix, SpeciesContextMapping[] speciesContextMappings, MathMapping mathMapping_temp, boolean bFast) throws Exception {

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

	Vector dependencyList = new Vector();
	
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
					constantExp = Expression.mult(Expression.mult(new Expression(coeff.toString()),firstSM.getTotalVolumeCorrection(simContext_temp)),
																new Expression(mathMapping_temp.getNameScope().getSymbolName(scSTE)));
					bFirst = false;
				}else{
					//
					// add term to dependancy expression     (" - 2*IP3Cyt ")
					//
					SpeciesContextMapping scm = speciesContextMappings[j];
					SpeciesContext sc = scm.getSpeciesContext();
					StructureMapping sm = simContext_temp.getGeometryContext().getStructureMapping(sc.getStructure());
					SpeciesContextSpec scs = simContext_temp.getReactionContext().getSpeciesContextSpec(sc);
					exp = Expression.add(exp,Expression.negate(Expression.mult(new Expression(coeff.toString()),Expression.mult(new Expression(sm.getTotalVolumeCorrection(simContext_temp)),new Expression(sc.getName())))));
					//
					// add term to K expression
					//
					SymbolTableEntry scSTE = null;
					if (bFast){
						scSTE = sc;
					}else{
						scSTE = scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
					}
					constantExp = Expression.add(constantExp,
												Expression.mult(new Expression(coeff.toString()),
																Expression.mult(new Expression(sm.getTotalVolumeCorrection(simContext_temp)),
																				new Expression(mathMapping_temp.getNameScope().getSymbolName(scSTE)))));
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
			exp = Expression.mult(exp,Expression.invert(sm.getTotalVolumeCorrection(simContext_temp)));
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
	return (StructureAnalyzer.Dependency[])cbit.util.BeanUtils.getArray(dependencyList,StructureAnalyzer.Dependency.class);
}


/**
 * This method was created in VisualAge.
 */
private void refreshTotalMatrices() throws Exception {

//System.out.println("StructureAnalyzer.refreshTotalMatrices()");
	//
	// update scheme matrix for full system (slow and fast)
	//
	totalSchemeMatrix = new RationalMatrixFast(speciesContextMappings.length,reactionSteps.length);
	for (int i=0;i<speciesContextMappings.length;i++){
		for (int j=0;j<reactionSteps.length;j++){
			totalSchemeMatrix.set_elem(i,j,reactionSteps[j].getStoichiometry(speciesContextMappings[i].getSpeciesContext()));
		}
	}

	//
	// initialize rate expressions for speciesContext's due to scheme matrix
	//
	for (int i=0;i<speciesContextMappings.length;i++){
		SpeciesContextMapping scm = speciesContextMappings[i];
		SpeciesContext sc = scm.getSpeciesContext();
		//
		// collect slow rate expression (fast handled by FastSystem)
		//
		Expression exp = new Expression(0.0);
		for (int j=0;j<reactionSteps.length;j++){
			int stoichiometry = reactionSteps[j].getStoichiometry(sc);
			ReactionSpec reactionSpec = mathMapping.getSimulationContext().getReactionContext().getReactionSpec(reactionSteps[j]);
			if (stoichiometry != 0){
				if (!(reactionSteps[j] instanceof DiffusionReactionStep) && !reactionSpec.isFast() && !reactionSpec.isExcluded()){
					ReactionParticipant[] rps = reactionSteps[j].getReactionParticipants(sc);
					Structure structure = reactionSteps[j].getStructure();
					//
					// if reaction is in one compartment and reactionParticipant is in another, then add a flux correction.
					// (e.g. if reaction is on membrane and reactionParticipant is in a feature)
					//
					if (rps.length > 0) {
						if ((structure instanceof Membrane) && (sc.getStructure()!=structure)){
							Membrane membrane = (Membrane)structure;
							MembraneMapping membraneMapping = (MembraneMapping)mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
							Parameter fluxCorrectionParameter = mathMapping.getFluxCorrectionParameter(membraneMapping,(Feature)sc.getStructure());
							if (reactionSteps[j] instanceof FluxReaction){
								exp = Expression.add(exp,new Expression(mathMapping.getNameScope().getSymbolName(fluxCorrectionParameter)+"*"+reactionSteps[j].getRateExpression(rps[0]).infix(mathMapping.getNameScope())));
							}else if (reactionSteps[j] instanceof SimpleReaction){
								exp = Expression.add(exp,new Expression(mathMapping.getNameScope().getSymbolName(fluxCorrectionParameter)+"*"+ReservedSymbol.KMOLE.getName()+"*"+reactionSteps[j].getRateExpression(rps[0]).infix(mathMapping.getNameScope())));
							}else{
								throw new RuntimeException("Internal Error: expected ReactionStep "+reactionSteps[j]+" to be of type SimpleReaction or FluxReaction");
							}
						}else{
							exp = Expression.add(exp,new Expression(reactionSteps[j].getRateExpression(rps[0]).infix(mathMapping.getNameScope())));
						}
					}
				}
			}
		}
		exp.bindExpression(mathMapping);
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
		totalNullSpaceMatrix = (RationalMatrixFast)totalSchemeMatrix.findNullSpace();
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

//System.out.println("StructureAnalyzer.refreshSpeciesContextMappings()");

	GeometryContext geoContext = mathMapping.getSimulationContext().getGeometryContext();
	Model model = mathMapping.getSimulationContext().getReactionContext().getModel();
	//
	// note, the order of species is specified such that the first species have priority
	//       when the null space is solved for dependent variables.  So the order of priority
	//       for elimination are as follows:
	//
	//          1) Species involved with fast reactions.
	//          2) Species not involved with fast reactions.
	//
	Vector scmList = new Vector();
	if (structures == null){
		return;
	}
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
	Vector rsList = new Vector();
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
	// add DiffusionReactionStep for each diffusing species to eliminate 
	// those species from conserved moieties
	//
	for (int i=0;i<scmList.size();i++){
		SpeciesContextMapping scm = (SpeciesContextMapping)scmList.elementAt(i);
		if (scm.isDiffusing()){
			rsList.addElement(new DiffusionReactionStep("DiffusionReactionStep"+i,scm.getSpeciesContext().getStructure(), scm.getSpeciesContext()));
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
			Expression dependentVar = new Expression(mathMapping.getNameScope().getSymbolName(speciesContextMappings[j].getSpeciesContext()));
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