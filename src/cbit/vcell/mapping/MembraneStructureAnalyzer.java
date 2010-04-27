package cbit.vcell.mapping;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.HashMap;
import java.util.Vector;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
public class MembraneStructureAnalyzer extends StructureAnalyzer {
	private SubVolume innerSubVolume = null;
	private SubVolume outerSubVolume = null;
	private SurfaceClass surfaceClass = null;
	private ResolvedFlux resolvedFluxes[] = null;

	public static boolean bResolvedFluxCorrectionBug = false;
	public static boolean bResolvedFluxCorrectionBugExercised = false;
	public static boolean bNoFluxIfFixed = false;
	public static boolean bNoFluxIfFixedExercised = false;
/**
 * MembraneStructureAnalyzer constructor comment.
 * @param mathMapping cbit.vcell.mapping.MathMapping
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
public MembraneStructureAnalyzer(MathMapping mathMapping, SurfaceClass surfaceClass, SubVolume innerSubVolume, SubVolume outerSubVolume) {
	super(mathMapping);
	this.innerSubVolume = innerSubVolume;
	this.outerSubVolume = outerSubVolume;
	this.surfaceClass = surfaceClass;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.SubVolume
 */
public SubVolume getInnerSubVolume() {
	return innerSubVolume;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.SubVolume
 */
public SubVolume getOuterSubVolume() {
	return outerSubVolume;
}
/**
 * This method was created in VisualAge.
 * @return UndefinedObject[]
 */
public ResolvedFlux[] getResolvedFluxes() {
	return resolvedFluxes;
}
/**
 * This method was created in VisualAge.
 */
public void refresh() {
	super.refresh();
	try {
		refreshResolvedFluxes();
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 */
private void refreshResolvedFluxes() throws Exception {

//System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes()");

	GeometryContext geoContext = mathMapping.getSimulationContext().getGeometryContext();
	HashMap<SpeciesContext,ResolvedFlux> resolvedFluxMap = new HashMap<SpeciesContext,ResolvedFlux>();

	//
	// for each reaction, get all fluxReactions associated with this membrane
	//
	Vector<ReactionStep> fluxList = new Vector<ReactionStep>();
	ReactionSpec reactionSpecs[] = mathMapping.getSimulationContext().getReactionContext().getReactionSpecs();
	for (int j=0;j<reactionSpecs.length;j++){
		if (reactionSpecs[j].isExcluded()){
			continue;
		}
		ReactionStep rs = reactionSpecs[j].getReactionStep();
		if (mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(rs.getStructure()).getGeometryClass()==surfaceClass){
			if (rs instanceof FluxReaction){
				fluxList.addElement(rs);
			}
		}
	}

	//
	// for each FluxReaction, add fluxes to influx and subtract from outflux
	//
	for (int i=0;i<fluxList.size();i++){
		FluxReaction fr = (FluxReaction)fluxList.elementAt(i);
		if (fr.getFluxCarrier() == null) {
			continue;
		}
		//
		// if "inside" speciesContext is not "fixed", add flux to ResolvedFlux
		//
		SpeciesContext insideSpeciesContext = mathMapping.getSimulationContext().getModel().getSpeciesContext(fr.getFluxCarrier(),((Membrane)fr.getStructure()).getInsideFeature());
		SpeciesContextSpec insideSpeciesContextSpec = mathMapping.getSimulationContext().getReactionContext().getSpeciesContextSpec(insideSpeciesContext);
		//
		// introduce bug compatability mode for NoFluxIfFixed bug
		//
		//if (!insideSpeciesContextSpec.isConstant()){
		if (bNoFluxIfFixed || !insideSpeciesContextSpec.isConstant()){
			if (bNoFluxIfFixed && insideSpeciesContextSpec.isConstant()){
				bNoFluxIfFixedExercised = true;
			}
			ResolvedFlux insideResolvedFlux = resolvedFluxMap.get(insideSpeciesContext);
			if (insideResolvedFlux==null){
				insideResolvedFlux = new ResolvedFlux(insideSpeciesContext);
				resolvedFluxMap.put(insideSpeciesContext,insideResolvedFlux);
			}
			FeatureMapping insideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(((Membrane)fr.getStructure()).getInsideFeature());
			
			Expression residualVolumeFraction = insideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).renameBoundSymbols(mathMapping.getNameScope());
			Expression insideFluxCorrection = Expression.invert(residualVolumeFraction);
			//
			// introduce bug compatability mode for resolved flux correction
			//
			if (bResolvedFluxCorrectionBug && !residualVolumeFraction.compareEqual(new Expression(1.0))){
				bResolvedFluxCorrectionBugExercised = true;
				System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
				insideFluxCorrection = new Expression(1.0);
			}
			//
			// add flux term to ResolvedFlux.inFlux
			//
			if (fr.getKinetics() instanceof DistributedKinetics){
				Expression reactionRateParameter = new Expression(((DistributedKinetics)fr.getKinetics()).getReactionRateParameter(), mathMapping.getNameScope());
				if (insideResolvedFlux.getFlux().isZero()){
					insideResolvedFlux.setFlux(Expression.mult(reactionRateParameter,insideFluxCorrection).flatten());
				}else{
					insideResolvedFlux.setFlux(Expression.add(insideResolvedFlux.getFlux(),Expression.mult(reactionRateParameter,insideFluxCorrection).flatten()));
				}
			}else if (fr.getKinetics() instanceof LumpedKinetics){
				throw new RuntimeException("Lumped Kinetics for fluxes not yet supported");
			}else{
				throw new RuntimeException("unexpected Kinetic type in MembraneStructureAnalyzer.refreshResolvedFluxes()");
			}
		}
		SpeciesContext outsideSpeciesContext = mathMapping.getSimulationContext().getModel().getSpeciesContext(fr.getFluxCarrier(),((Membrane)fr.getStructure()).getOutsideFeature());
		SpeciesContextSpec outsideSpeciesContextSpec = mathMapping.getSimulationContext().getReactionContext().getSpeciesContextSpec(outsideSpeciesContext);
		//
		// introduce bug compatability mode for NoFluxIfFixed bug
		//
		//if (!outsideSpeciesContextSpec.isConstant()){
		if (bNoFluxIfFixed || !outsideSpeciesContextSpec.isConstant()){
			if (bNoFluxIfFixed && outsideSpeciesContextSpec.isConstant()){
				bNoFluxIfFixedExercised = true;
			}
			ResolvedFlux outsideResolvedFlux = resolvedFluxMap.get(outsideSpeciesContext);
			if (outsideResolvedFlux==null){
				outsideResolvedFlux = new ResolvedFlux(outsideSpeciesContext);
				resolvedFluxMap.put(outsideSpeciesContext,outsideResolvedFlux);
			}
			FeatureMapping outsideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(((Membrane)fr.getStructure()).getOutsideFeature());
			Expression residualVolumeFraction = outsideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).renameBoundSymbols(mathMapping.getNameScope());
			Expression outsideFluxCorrection = Expression.invert(residualVolumeFraction);
			//
			// introduce bug compatability mode for resolved flux correction
			//
			if (bResolvedFluxCorrectionBug && !residualVolumeFraction.compareEqual(new Expression(1.0))){
				bResolvedFluxCorrectionBugExercised = true;
				System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
				outsideFluxCorrection = new Expression(1.0);
			}
			//
			// sub flux term to resolvedFlux.outFlux
			//
			if (fr.getKinetics() instanceof DistributedKinetics){
				Expression reactionRateParameter = new Expression(((DistributedKinetics)fr.getKinetics()).getReactionRateParameter(), mathMapping.getNameScope());
				if (outsideResolvedFlux.getFlux().isZero()){
					outsideResolvedFlux.setFlux(Expression.mult(Expression.negate(reactionRateParameter),outsideFluxCorrection).flatten());
				}else{
					outsideResolvedFlux.setFlux(Expression.add(outsideResolvedFlux.getFlux(),Expression.mult(Expression.negate(reactionRateParameter),outsideFluxCorrection).flatten()));
				}
			}else if (fr.getKinetics() instanceof LumpedKinetics){
				throw new RuntimeException("Lumped Kinetics not yet supported for Flux Reaction: "+fr.getName());
			}else{
				throw new RuntimeException("unexpected Kinetics type for Flux Reaction "+fr.getName());
			}
//			rf.outFlux.bindExpression(mathMapping);
		}
	}
	
	//
	// for each reaction, incorporate all reactionSteps involving binding with volumetric species
	//
	for (int i=0;i<reactionSpecs.length;i++){
		if (reactionSpecs[i].isExcluded()){
			continue;
		}
		ReactionStep rs = reactionSpecs[i].getReactionStep();
		if (mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(rs.getStructure()).getGeometryClass()==surfaceClass){
			if (rs instanceof SimpleReaction){
				SimpleReaction sr = (SimpleReaction)rs;
				ReactionParticipant rp_Array[] = sr.getReactionParticipants();
				for (int k = 0; k < rp_Array.length; k++) {
					if (rp_Array[k] instanceof Reactant || rp_Array[k] instanceof Product){
						SpeciesContextSpec scs = mathMapping.getSimulationContext().getReactionContext().getSpeciesContextSpec(rp_Array[k].getSpeciesContext());
						//
						// for volume species that are not "fixed", add fluxes to "ResolvedFlux"
						//

						//
						// introduce bug compatability mode for NoFluxIfFixed bug
						//
						// if (rp_Array[k].getStructure() instanceof Feature && !scs.isConstant()){
						if (rp_Array[k].getStructure() instanceof Feature && (bNoFluxIfFixed || !scs.isConstant())){
							if (bNoFluxIfFixed && scs.isConstant()){
								bNoFluxIfFixedExercised = true;
							}
							//
							// for each Reactant or Product binding to this membrane...
							//

							//
							// get ResolvedFlux for this speciesContext
							//
							ResolvedFlux resolvedFlux = resolvedFluxMap.get(rp_Array[k].getSpeciesContext());
							if (resolvedFlux==null){
								resolvedFlux = new ResolvedFlux(rp_Array[k].getSpeciesContext());
								resolvedFluxMap.put(rp_Array[k].getSpeciesContext(),resolvedFlux);
							}
							
							Expression reactionRateExpression = sr.getReactionRateExpression(rp_Array[k]).renameBoundSymbols(mathMapping.getNameScope());
							Structure rpStructure = rp_Array[k].getSpeciesContext().getStructure();
							GeometryClass rpGeometryClass = mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(rpStructure).getGeometryClass();
							if (rpGeometryClass == surfaceClass.getSubvolume1()){
								//
								// for binding on inside, add to ResolvedFlux.inFlux
								//
								FeatureMapping insideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(rpStructure);
								Expression residualVolumeFraction = insideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).renameBoundSymbols(mathMapping.getNameScope());
								Expression insideFluxCorrection = Expression.div(new Expression(ReservedSymbol.KMOLE, mathMapping.getNameScope()), residualVolumeFraction).flatten();
								//
								// introduce bug compatability mode for resolved flux correction
								//
								if (bResolvedFluxCorrectionBug && !residualVolumeFraction.compareEqual(new Expression(1.0))){
									bResolvedFluxCorrectionBugExercised = true;
									System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
									insideFluxCorrection = new Expression(ReservedSymbol.KMOLE, mathMapping.getNameScope());
								}
								if (resolvedFlux.getFlux().isZero()){
									resolvedFlux.setFlux(Expression.mult(insideFluxCorrection, reactionRateExpression));
								}else{
									resolvedFlux.setFlux(Expression.add(resolvedFlux.getFlux(),Expression.mult(insideFluxCorrection, reactionRateExpression)));
								}
//								rf.inFlux.bindExpression(mathMapping);
							}else if (rpGeometryClass == surfaceClass.getSubvolume2()){
								//
								// for binding on outside, add to ResolvedFlux.outFlux
								//
								FeatureMapping outsideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(rpStructure);
								Expression residualVolumeFraction = outsideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).renameBoundSymbols(mathMapping.getNameScope());
								Expression outsideFluxCorrection = Expression.div(new Expression(ReservedSymbol.KMOLE, mathMapping.getNameScope()), residualVolumeFraction).flatten();
								//
								// introduce bug compatability mode for resolved flux correction
								//
								if (bResolvedFluxCorrectionBug && !residualVolumeFraction.compareEqual(new Expression(1.0))){
									bResolvedFluxCorrectionBugExercised = true;
									System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
									outsideFluxCorrection = new Expression(ReservedSymbol.KMOLE, mathMapping.getNameScope());
								}
								if (resolvedFlux.getFlux().isZero()){
									resolvedFlux.setFlux(Expression.mult(outsideFluxCorrection, reactionRateExpression));
								}else{
									resolvedFlux.setFlux(Expression.add(resolvedFlux.getFlux(),Expression.mult(outsideFluxCorrection, reactionRateExpression)));
								}
//								rf.outFlux.bindExpression(mathMapping);
							}else{
								throw new Exception("SpeciesContext "+rp_Array[k].getSpeciesContext().getName()+" doesn't border membrane "+surfaceClass.getName()+" but reacts there");
							}
						}
					}
				}					
			}
		}
	}

	if (resolvedFluxMap.size()>0){
		resolvedFluxes = resolvedFluxMap.values().toArray(new ResolvedFlux[0]);
	}else{
		resolvedFluxes=null;
	}
}
/**
 * Build list of structures (just one membrane) that are mapped to this volume subdomain
 */
protected void refreshStructures() {
	structures = mathMapping.getSimulationContext().getGeometryContext().getStructuresFromGeometryClass(surfaceClass);
}
public SurfaceClass getSurfaceClass() {
	return surfaceClass;
}
}
