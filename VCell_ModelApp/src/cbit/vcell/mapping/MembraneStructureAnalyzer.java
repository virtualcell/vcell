package cbit.vcell.mapping;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import cbit.vcell.model.*;
import java.util.*;

import org.vcell.modelapp.FeatureMapping;
import org.vcell.modelapp.GeometryContext;
import org.vcell.modelapp.ReactionSpec;
import org.vcell.modelapp.SpeciesContextSpec;

import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
public class MembraneStructureAnalyzer extends StructureAnalyzer {
	private SubVolume innerSubVolume = null;
	private SubVolume outerSubVolume = null;
	private Membrane membrane = null;
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
public MembraneStructureAnalyzer(MathMapping mathMapping, Membrane membrane, SubVolume innerSubVolume, SubVolume outerSubVolume) {
	super(mathMapping);
	this.innerSubVolume = innerSubVolume;
	this.outerSubVolume = outerSubVolume;
	this.membrane = membrane;
	refresh();
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
 * @return cbit.vcell.model.Membrane
 */
public Membrane getMembrane() {
	return membrane;
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
	}
}
/**
 * This method was created in VisualAge.
 */
private void refreshResolvedFluxes() throws Exception {

//System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes()");

	GeometryContext geoContext = mathMapping.getSimulationContext().getGeometryContext();
	Vector resolvedFluxList = new Vector();

	//
	// for each reaction, get all fluxReactions associated with this membrane
	//
	Vector fluxList = new Vector();
	ReactionSpec reactionSpecs[] = mathMapping.getSimulationContext().getReactionContext().getReactionSpecs();
	for (int j=0;j<reactionSpecs.length;j++){
		if (reactionSpecs[j].isExcluded()){
			continue;
		}
		ReactionStep rs = reactionSpecs[j].getReactionStep();
		if (rs.getStructure()==getMembrane()){
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
		ResolvedFlux rf = null;
		for (int j=0;j<resolvedFluxList.size();j++){
			ResolvedFlux rf_tmp = (ResolvedFlux)resolvedFluxList.elementAt(j);
			if (rf_tmp.getSpecies() == fr.getFluxCarrier()){
				rf = rf_tmp;
			}
		}
		//
		// if "inside" speciesContext is not "fixed", add flux to ResolvedFlux
		//
		SpeciesContext insideSpeciesContext = mathMapping.getSimulationContext().getModel().getSpeciesContext(fr.getFluxCarrier(),getMembrane().getInsideFeature());
		SpeciesContextSpec insideSpeciesContextSpec = mathMapping.getSimulationContext().getReactionContext().getSpeciesContextSpec(insideSpeciesContext);
		//
		// introduce bug compatability mode for NoFluxIfFixed bug
		//
		//if (!insideSpeciesContextSpec.isConstant()){
		if (bNoFluxIfFixed || !insideSpeciesContextSpec.isConstant()){
			if (bNoFluxIfFixed && insideSpeciesContextSpec.isConstant()){
				bNoFluxIfFixedExercised = true;
			}
			if (rf == null){
				rf = new ResolvedFlux(fr.getFluxCarrier());
				resolvedFluxList.addElement(rf);
			}
			FeatureMapping insideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(((Membrane)fr.getStructure()).getInsideFeature());
			Expression insideFluxCorrection = (new Expression("1.0/("+insideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).infix(mathMapping.getNameScope())+")"));
			//
			// introduce bug compatability mode for resolved flux correction
			//
			if (bResolvedFluxCorrectionBug && !insideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).compareEqual(new Expression(1.0))){
				bResolvedFluxCorrectionBugExercised = true;
				System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
				insideFluxCorrection = new Expression(1.0);
			}
			//
			// add flux term to ResolvedFlux.inFlux
			//
			if (rf.inFlux.isZero()){
				rf.inFlux = Expression.mult(new Expression(mathMapping.getNameScope().getSymbolName(fr.getKinetics().getRateParameter())),insideFluxCorrection).flatten();
			}else{
				rf.inFlux = Expression.add(rf.inFlux,Expression.mult(new Expression(mathMapping.getNameScope().getSymbolName(fr.getKinetics().getRateParameter())),insideFluxCorrection).flatten());
			}
			rf.inFlux.bindExpression(mathMapping);
		}
		SpeciesContext outsideSpeciesContext = mathMapping.getSimulationContext().getModel().getSpeciesContext(fr.getFluxCarrier(),getMembrane().getOutsideFeature());
		SpeciesContextSpec outsideSpeciesContextSpec = mathMapping.getSimulationContext().getReactionContext().getSpeciesContextSpec(outsideSpeciesContext);
		//
		// introduce bug compatability mode for NoFluxIfFixed bug
		//
		//if (!outsideSpeciesContextSpec.isConstant()){
		if (bNoFluxIfFixed || !outsideSpeciesContextSpec.isConstant()){
			if (bNoFluxIfFixed && outsideSpeciesContextSpec.isConstant()){
				bNoFluxIfFixedExercised = true;
			}
			if (rf == null){
				rf = new ResolvedFlux(fr.getFluxCarrier());
				resolvedFluxList.addElement(rf);
			}
			FeatureMapping outsideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(((Membrane)fr.getStructure()).getOutsideFeature());
			Expression outsideFluxCorrection = (new Expression("1.0/("+outsideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).infix(mathMapping.getNameScope())+")"));
			//
			// introduce bug compatability mode for resolved flux correction
			//
			if (bResolvedFluxCorrectionBug && !outsideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).compareEqual(new Expression(1.0))){
				bResolvedFluxCorrectionBugExercised = true;
				System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
				outsideFluxCorrection = new Expression(1.0);
			}
			//
			// sub flux term to resolvedFlux.outFlux
			//
			if (rf.outFlux.isZero()){
				rf.outFlux = Expression.mult(new Expression("-"+mathMapping.getNameScope().getSymbolName(fr.getKinetics().getRateParameter())),outsideFluxCorrection).flatten();
			}else{
				rf.outFlux = Expression.add(rf.outFlux,Expression.mult(new Expression("-"+mathMapping.getNameScope().getSymbolName(fr.getKinetics().getRateParameter())),outsideFluxCorrection).flatten());
			}
			rf.outFlux.bindExpression(mathMapping);
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
		if (rs.getStructure()==getMembrane()){
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
							// get ResolvedFlux for this species
							//
							ResolvedFlux rf = null;
							for (int j=0;j<resolvedFluxList.size();j++){
								ResolvedFlux rf_tmp = (ResolvedFlux)resolvedFluxList.elementAt(j);
								if (rf_tmp.getSpecies() == rp_Array[k].getSpecies()){
									rf = rf_tmp;
								}
							}
							if (rf == null){
								rf = new ResolvedFlux(rp_Array[k].getSpecies());
								resolvedFluxList.addElement(rf);
							}
							
							if (rp_Array[k].getStructure() == getMembrane().getInsideFeature()){
								//
								// for binding on inside, add to ResolvedFlux.inFlux
								//
								FeatureMapping insideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(getMembrane().getInsideFeature());
								Expression insideFluxCorrection = (new Expression(mathMapping.getNameScope().getSymbolName(ReservedSymbol.KMOLE)+"/("+insideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).infix(mathMapping.getNameScope())+")")).flatten();
								//
								// introduce bug compatability mode for resolved flux correction
								//
								if (bResolvedFluxCorrectionBug && !insideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).compareEqual(new Expression(1.0))){
									bResolvedFluxCorrectionBugExercised = true;
									System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
									insideFluxCorrection = new Expression(ReservedSymbol.KMOLE.getName());
								}
								if (rf.inFlux.isZero()){
									rf.inFlux = Expression.mult(insideFluxCorrection,new Expression(sr.getRateExpression(rp_Array[k]).infix(mathMapping.getNameScope())));
								}else{
									rf.inFlux = Expression.add(rf.inFlux,Expression.mult(insideFluxCorrection,new Expression(sr.getRateExpression(rp_Array[k]).infix(mathMapping.getNameScope()))));
								}
								rf.inFlux.bindExpression(mathMapping);
							}else if (rp_Array[k].getStructure() == getMembrane().getOutsideFeature()){
								//
								// for binding on outside, add to ResolvedFlux.outFlux
								//
								FeatureMapping outsideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(getMembrane().getOutsideFeature());
								Expression outsideFluxCorrection = (new Expression(mathMapping.getNameScope().getSymbolName(ReservedSymbol.KMOLE)+"/("+outsideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).infix(mathMapping.getNameScope())+")")).flatten();
								//
								// introduce bug compatability mode for resolved flux correction
								//
								if (bResolvedFluxCorrectionBug && !outsideFeatureMapping.getResidualVolumeFraction(mathMapping.getSimulationContext()).compareEqual(new Expression(1.0))){
									bResolvedFluxCorrectionBugExercised = true;
									System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
									outsideFluxCorrection = new Expression(ReservedSymbol.KMOLE.getName());
								}
								if (rf.outFlux.isZero()){
									rf.outFlux = Expression.mult(outsideFluxCorrection,new Expression(sr.getRateExpression(rp_Array[k]).infix(mathMapping.getNameScope())));
								}else{
									rf.outFlux = Expression.add(rf.outFlux,Expression.mult(outsideFluxCorrection,new Expression(sr.getRateExpression(rp_Array[k]).infix(mathMapping.getNameScope()))));
								}
								rf.outFlux.bindExpression(mathMapping);
							}else{
								throw new Exception("SpeciesContext "+rp_Array[k].getSpeciesContext().getName()+" doesn't border membrane "+getMembrane().getName()+" but reacts there");
							}
						}
					}
				}					
			}
		}
	}

	
	//
	// copy Vector into resolvedFluxes[] array
	//
	if (resolvedFluxList.size()>0){
		resolvedFluxes = new ResolvedFlux[resolvedFluxList.size()];
		resolvedFluxList.copyInto(resolvedFluxes);
	}else{
		resolvedFluxes=null;
	}
}
/**
 * Build list of structures (just one membrane) that are mapped to this volume subdomain
 */
protected void refreshStructures() {
	structures = new Structure[1];
	structures[0] = membrane;
}
}
