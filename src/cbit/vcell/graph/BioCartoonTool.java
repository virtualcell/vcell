package cbit.vcell.graph;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Issue;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.ZEnforcer;

import cbit.gui.graph.GraphPane;
import cbit.gui.graph.Shape;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Flux;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.model.gui.AddModelParamDialog;
import cbit.vcell.model.gui.EditSpeciesDialog;
import cbit.vcell.model.gui.FeatureDialog;
import cbit.vcell.model.gui.MembraneDialog;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;

public abstract class BioCartoonTool extends cbit.gui.graph.CartoonTool {
	private DocumentManager documentManager = null;

/**
 * This method was created by a SmartGuide.
 * @param canvas cbit.vcell.graph.CartoonCanvas
 */
public BioCartoonTool() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 8:26:55 PM)
 * @return cbit.vcell.clientdb.DocumentManager
 */
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	return documentManager;
}

/**
 * printIssues : using the issue list passed as argument, gethers them in a string buffer and uses PopupGenerator.showWarning dialog to display 
 * the issues to user. 
 * @param issueVector : vector of 'Issue's to be printed
 * @param guiRequestComponent : the parent component required to be passed to showWarningDialog
 * @return
 */
public static boolean printIssues(Vector<Issue> issueVector, Component guiRequestComponent) {
	// now print out the issue List as a warning popup
	if (issueVector.size() > 0) {
		StringBuffer messageBuffer = new StringBuffer("Issues encountered during pasting selected reactions:\n\n");
		for (int j = 0; j < issueVector.size(); j++) {
			Issue issue = issueVector.get(j);
			if (issue.getSeverity()==Issue.SEVERITY_ERROR || issue.getSeverity()==Issue.SEVERITY_WARNING) {
				messageBuffer.append(j+1 + ". " + issue.getCategory()+" "+issue.getSeverityName()+" : "+issue.getMessage()+"\n\n");
			}
		}
		if (issueVector.size()>0){
			String[] choices = new String[] {"Paste Anyway", "Cancel"};
			String resultStr = PopupGenerator.showWarningDialog(guiRequestComponent, messageBuffer.toString(), choices, "Cancel");
			if (resultStr != null && resultStr.equals("Paste Anyway")) {
				return true;
			}
		}
	}
	return false;
}


/**
 * pasteReactionSteps : this method clones the model argument and calls the private pasteReationSteps0 method with the cloned model to see if
 * there are any issues with the paste operation. If so, the issue list is popped up in a warning dialog, and user is given the option of proceeding
 * with the paste or cancelling the operation.
 *  
 * @param reactionStepsArr : reactions to be pasted
 * @param model : model where reactions are to be pasted
 * @param struct : strucure in 'model' where the reactions should be pasted
 * @param bNew : is it 'paste' or 'paste new' reaction (new reaction Participants are created if 'bNew' is <true>).
 * @param guiRequestComponent : the parent component for the warning dialog that pops up the issues, if any, encountered in the pasting process
 * @throws Exception
 */
protected static final void pasteReactionSteps(ReactionStep[] reactionStepsArrOrig,Model pasteModel, Structure struct, boolean bNew, Component guiRequestComponent) throws Exception {
	Model clonedModel = (Model)org.vcell.util.BeanUtils.cloneSerializable(pasteModel);
	Vector<Issue> issueList = pasteReactionSteps0(reactionStepsArrOrig, clonedModel, clonedModel.getStructure(struct.getName()), bNew);
	if (issueList.size() != 0) {
		if (!printIssues(issueList, guiRequestComponent)) {
			return;
		}
	}
	issueList.clear();
	issueList = pasteReactionSteps0(reactionStepsArrOrig, pasteModel, struct, bNew);
}


/**
 * pasteReactionSteps0 : does the actual pasting. First called with a cloned model, to track issues. If user still wants to proceed, the paste
 * is performed on the original model.
 * 
 * Insert the method's description here.
 * Creation date: (5/10/2003 3:55:25 PM)
 * @param model cbit.vcell.model.Model
 * @param struct cbit.vcell.model.Structure
 * @param bNew boolean
 */
private static final Vector<Issue> pasteReactionSteps0(ReactionStep[] reactionStepsArr,Model model, Structure struct, boolean bNew) throws Exception {

	if(reactionStepsArr == null || reactionStepsArr.length == 0 || model == null || struct == null){
		throw new IllegalArgumentException("CartoonTool.pasteReactionSteps Error "+
			(reactionStepsArr == null || reactionStepsArr.length == 0?"reactionStepsArr empty ":"")+
			(model == null?"model is null ":"")+
			(struct == null?"struct is null ":"")
			);
	}

	if(!model.contains(struct)){
		throw new IllegalArgumentException("CartoonTool.pasteReactionSteps model "+model.getName()+" does not contain structure "+struct.getName());
	}

	int counter = 0;
	Structure currentStruct = struct;
	String copiedStructName = reactionStepsArr[counter].getStructure().getName();
	Vector<Issue> issueVector = new Vector<Issue>();
	do{
		// create a new reaction, instead of cloning the old one; set struc
		ReactionStep oldReactionStep = reactionStepsArr[counter]; 
		String newName = oldReactionStep.getName();
		while(model.getReactionStep(newName) != null){
			newName = org.vcell.util.TokenMangler.getNextEnumeratedToken(newName);
		}
		ReactionStep newReactionStep = null;
		
		if (oldReactionStep instanceof SimpleReaction) {
			newReactionStep = new SimpleReaction(currentStruct, newName);
		} else if (oldReactionStep instanceof FluxReaction && currentStruct instanceof Membrane) {
			newReactionStep = new FluxReaction((Membrane)currentStruct, null, newName);
		}
		
		model.addReactionStep(newReactionStep);
		Structure newRxnStruct = newReactionStep.getStructure();
		Structure oldRxnStruct = oldReactionStep.getStructure();
		
		if(!newRxnStruct.getClass().equals(struct.getClass())){
			throw new Exception("Copied RectionStep structure type="+newReactionStep.getStructure().getClass().getName()+
								"\ndoes not equal target structure type="+struct.getClass().getName());
		}

		// add appropriate reactionParticipants to newReactionStep.
		IdentityHashMap<Species, Species> speciesHash = new IdentityHashMap<Species, Species>();
		IdentityHashMap<SpeciesContext, SpeciesContext> speciesContextHash = new IdentityHashMap<SpeciesContext, SpeciesContext>();
		ReactionParticipant[] rpArr = oldReactionStep.getReactionParticipants();
		Species fluxCarrierSp = null;
		for(int i=0;i<rpArr.length;i+= 1){
			Structure pasteStruct = currentStruct;
			if(newRxnStruct instanceof Membrane){
				Membrane oldMembr = (Membrane)oldRxnStruct;
				if(rpArr[i].getStructure().getName().equals(oldMembr.getOutsideFeature().getName())){
					pasteStruct = ((Membrane)currentStruct).getOutsideFeature();
				}else if(rpArr[i].getStructure().getName().equals(oldMembr.getInsideFeature().getName())){
					pasteStruct = ((Membrane)currentStruct).getInsideFeature();
				}
			}
			// this adds the speciesContexts and species (if any) to the model)
			SpeciesContext newSc = pasteSpecies(rpArr[i].getSpecies(),model,pasteStruct,bNew, speciesHash);
			// record the old-new speciesContexts (reactionparticipants) in the IdHashMap, this is useful, esp for 'Paste new', while replacing proxyparams. 
			SpeciesContext oldSc = rpArr[i].getSpeciesContext();
			if (speciesContextHash.get(oldSc) == null) {
				speciesContextHash.put(oldSc, newSc);
			}
			if (rpArr[i] instanceof Reactant) {
				newReactionStep.addReactionParticipant(new Reactant(null, (SimpleReaction)newReactionStep, newSc, rpArr[i].getStoichiometry()));
			} else if (rpArr[i] instanceof Product) {
				newReactionStep.addReactionParticipant(new Product(null, (SimpleReaction)newReactionStep, newSc, rpArr[i].getStoichiometry()));
			} else if (rpArr[i] instanceof Catalyst) {
				newReactionStep.addCatalyst(newSc);
			} else if (rpArr[i] instanceof Flux) {
				fluxCarrierSp = newSc.getSpecies(); 
			}
		}

		// If 'newReactionStep' is a fluxRxn, set its fluxCarrier
		if (newReactionStep instanceof FluxReaction) {
			if (fluxCarrierSp != null) {
				((FluxReaction)newReactionStep).setFluxCarrier(fluxCarrierSp, model);
			} else {
				throw new RuntimeException("Could not set FluxCarrier species for the flux reaction to be pasted");
			}
		}
		
		// For each kinetic parameter expression for new kinetics, replace the proxyParams from old kinetics with proxyParams in new kinetics
		// i.e., if the proxyParams are speciesContexts, replace with corresponding speciesContext in newReactionStep;
		// if the proxyParams are structureSizes or MembraneVoltages, replace with corresponding structure quantity in newReactionStep
		Kinetics oldKinetics = oldReactionStep.getKinetics();
		KineticsParameter[] oldKps = oldKinetics.getKineticsParameters(); 
		KineticsProxyParameter[] oldKprps = oldKinetics.getProxyParameters();
		Hashtable<String, Expression> paramExprHash = new Hashtable<String, Expression>();
		for (int i = 0; oldKps != null && i < oldKps.length; i++) {
			Expression newExpression = new Expression(oldKps[i].getExpression());
			for (int j = 0; oldKprps != null && j < oldKprps.length; j++) {
				// check if kinetic proxy parameter is in kinetic parameter expression
				if (newExpression.hasSymbol(oldKprps[j].getName())) {
					SymbolTableEntry ste = oldKprps[j].getTarget();
					if (ste instanceof SpeciesContext) {
						// if newRxnStruct is a feature/membrane, get matching spContexts from old reaction and replace them in new rate expr. 
						SpeciesContext oldSC = (SpeciesContext)ste;
						SpeciesContext newSC = speciesContextHash.get(oldSC);
						if (newSC == null) {
							// the speciesContext (ste) was not a rxnParticipant. If paste-model is different from copy/cut-model, 
							// check if oldSc is present in paste-model; if not, add it.
							if (!model.equals(oldReactionStep.getModel())) {
								if (model.getSpeciesContext(oldSC.getName()) == null) {
									// if paste-model has oldSc struct, paste it there, 
									Structure newSCStruct = model.getStructure(oldSC.getStructure().getName()); 
									if (newSCStruct != null) {
										newSC = pasteSpecies(oldSC.getSpecies(), model, newSCStruct, bNew, speciesHash);
										speciesContextHash.put(oldSC, newSC);
									} else {
										// oldStruct wasn't found in paste-model, paste it in newRxnStruct and add warning to issues list
										newSC = pasteSpecies(oldSC.getSpecies(), model, newRxnStruct, bNew, speciesHash);
										speciesContextHash.put(oldSC, newSC);
										Issue issue = new Issue(oldSC, "Species Context",
												"SpeciesContext '" + oldSC.getSpecies().getCommonName() + "' was not found in compartment '" +
												oldSC.getStructure().getName() + "' in the model; the species was added to the compartment '" +
												newRxnStruct.getName() + "' where the reaction was pasted.",
												Issue.SEVERITY_WARNING);
										issueVector.add(issue);
									}
								}
							}
							// if models are the same and newSc is null, then oldSc is not a rxnParticipant. Leave it as is in the expr. 
						}
						if (newSC != null) {
							newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(newSC.getName()));
						}
//							SpeciesContext sc = null;
//							Species newSp = model.getSpecies(oldSc.getSpecies().getCommonName());
//							if  (oldSc.getStructure() == (oldRxnStruct)) {
//								sc = model.getSpeciesContext(newSp, newRxnStruct);
//							} else {
//								if (newRxnStruct instanceof Membrane) {
//									// for a membrane, we need to make sure that inside-outside spContexts used are appropriately replaced.
//									if (oldSc.getStructure() == ((Membrane)oldRxnStruct).getOutsideFeature()) {
//										// old speciesContext is outside (old) membrane, new spContext should be outside new membrane
//										sc = model.getSpeciesContext(newSp, ((Membrane)newRxnStruct).getOutsideFeature());
//									} else if (oldSc.getStructure() == ((Membrane)oldRxnStruct).getInsideFeature()) {
//										// old speciesContext is inside (old) membrane, new spContext should be inside new membrane
//										sc = model.getSpeciesContext(newSp, ((Membrane)newRxnStruct).getInsideFeature());
//									}
//								}
//							}
//							if (sc != null) {
//								newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(sc.getName()));
//							}
					} else if (ste instanceof StructureSize) {
						Structure str = ((StructureSize)ste).getStructure();
						// if the structure size used is same as the structure in which the reaction is present, change the structSize to appropriate new struct  
						if (str.compareEqual(oldRxnStruct)) {
							newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(newRxnStruct.getStructureSize().getName()));
						} else {
							if (oldRxnStruct instanceof Membrane) {
								if (str.equals(((Membrane)oldRxnStruct).getOutsideFeature())) {
									newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(((Membrane)newRxnStruct).getOutsideFeature().getStructureSize().getName()));
								} else if (str.equals(((Membrane)oldRxnStruct).getInsideFeature())) {
									newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(((Membrane)newRxnStruct).getInsideFeature().getStructureSize().getName()));
								}
							}
						}
					} else if (ste instanceof MembraneVoltage) {
						Membrane membr = ((MembraneVoltage)ste).getMembrane();
						// if the MembraneVoltage used is same as that of the membrane in which the reaction is present, change the MemVoltage 
						if ((oldRxnStruct instanceof Membrane) && (membr.compareEqual((Membrane)oldRxnStruct))) {
							newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(((Membrane)newRxnStruct).getMembraneVoltage().getName()));
						} 
					} else if (ste instanceof ModelParameter) {
						// see if model has this global parameter (if rxn is being pasted into another model, it won't)
						if (!model.equals(oldReactionStep.getModel())) {
							ModelParameter oldMp = (ModelParameter)ste;
							ModelParameter mp = model.getModelParameter(oldMp.getName());
							boolean bNonNumeric = false;
							String newMpName = oldMp.getName();
							if (mp != null) {
								// new model has a model parameter with same name - are they the same param?
								if (!mp.getExpression().equals(oldMp.getExpression())) {
									// no, they are not the same param, so mangle the 'ste' name and add as global in the other model
									while (model.getModelParameter(newMpName) != null) {
										newMpName = TokenMangler.getNextEnumeratedToken(newMpName);
									}
									// if expression if numeric, add it as such. If not, set it to 0.0 and add it as global
									Expression exp = oldMp.getExpression(); 
									if (!exp.flatten().isNumeric()) {
										exp = new Expression(0.0);
										bNonNumeric = true;
									}
									ModelParameter newMp = model.new ModelParameter(newMpName, exp, Model.ROLE_UserDefined, oldMp.getUnitDefinition());
									String annotation = "Copied from model : " + oldReactionStep.getModel().getNameScope();
									newMp.setModelParameterAnnotation(annotation);
									model.addModelParameter(newMp);
									// if global param name had to be changed, make sure newExpr is updated as well.
									if (!newMpName.equals(oldMp.getName())) {
										newExpression.substituteInPlace(new Expression(oldMp.getName()), new Expression(newMpName));
									}
								}
							} else {
								// no global param with same name was found in other model, so add it to other model.
								// if expression if numeric, add it as such. If not, set it to 0.0 and add it as global
								Expression exp = oldMp.getExpression(); 
								if (!exp.flatten().isNumeric()) {
									exp = new Expression(0.0);
									bNonNumeric = true;
								}
								ModelParameter newMp = model.new ModelParameter(newMpName, exp, Model.ROLE_UserDefined, oldMp.getUnitDefinition());
								String annotation = "Copied from model : " + oldReactionStep.getModel().getNameScope();
								newMp.setModelParameterAnnotation(annotation);
								model.addModelParameter(newMp);	
							}
							// if a non-numeric parameter was encountered in the old model, it was added as a numeric (0.0), warn user of change.
							if (bNonNumeric) {
								Issue issue = new Issue(oldMp, "Global Parameters",
										"Global parameter '" + oldMp.getName() + "' was non-numeric; it has been added " +
										"as global parameter '" + newMpName + "' in the new model with value = 0.0. " +
										"Please update its value, if required, before using it.",
										Issue.SEVERITY_WARNING);
								issueVector.add(issue);
							}
						}
					}
				} // end - if newExpr.hasSymbol(ProxyParam)
			} // end for - oldKprps (old kinetic proxy parameters)
			// now if store <param names, new expression> in hashTable
			if (paramExprHash.get(oldKps[i].getName()) == null) {
				paramExprHash.put(oldKps[i].getName(), newExpression);
			}
		} // end for - oldKps (old kinetic parameters)
		
		// use this new expression to generate 'vcml' for the (new) kinetics (easier way to transfer all kinetic parameters)
		String newKineticsStr = oldKinetics.writeTokensWithReplacingProxyParams(paramExprHash);
		// convert the kinetics 'vcml' to tokens.
		CommentStringTokenizer kineticsTokens = new CommentStringTokenizer(newKineticsStr);
		// skip the first token; 
		kineticsTokens.nextToken();
		// second token is the kinetic type; use this to create a dummy kinetics
		String kineticType = kineticsTokens.nextToken();
		Kinetics newkinetics = KineticsDescription.fromVCMLKineticsName(kineticType).createKinetics(newReactionStep);
		// use the remaining tokens to construct the new kinetics
		newkinetics.fromTokens(kineticsTokens);
		// bind newkinetics to newReactionStep and add it to newReactionStep 
		newkinetics.bind(newReactionStep);
		newReactionStep.setKinetics(newkinetics);
		
		counter+= 1;
		if(counter == reactionStepsArr.length){
			break;
		}
		
		if(!copiedStructName.equals(oldRxnStruct.getName())){
			if(currentStruct instanceof Feature){
				currentStruct = ((Feature)currentStruct).getMembrane();
			}else if (currentStruct instanceof Membrane){
				currentStruct = ((Membrane)currentStruct).getInsideFeature();
			}
		}
		copiedStructName = oldRxnStruct.getName();
	}while(true);
	return issueVector;
}

private static Species getNewSpecies(IdentityHashMap<Species, Species> speciesHash, Species oldSpecies, Model newModel, boolean bNew, Structure newStruct) {
	Species newSpecies = speciesHash.get(oldSpecies);
	if (newSpecies != null) {
		return newSpecies;
	} 

	
	try {
		if(bNew){
			String newName = oldSpecies.getCommonName();
			while(newModel.getSpecies(newName) != null){
				newName = org.vcell.util.TokenMangler.getNextEnumeratedToken(newName);
			}
			newSpecies = new Species(newName,oldSpecies.getAnnotation(),oldSpecies.getDBSpecies());
			newModel.addSpecies(newSpecies);
			speciesHash.put(oldSpecies, newSpecies);
		} else {
			//Find a matching existing species if possible
			if(!newModel.contains(oldSpecies)){// Doesn't have Species (==)
				//see if we have a species with DBSpecies that matches
				Species[] speciesFromDBSpeciesArr = (oldSpecies.getDBSpecies() != null ? newModel.getSpecies(oldSpecies.getDBSpecies()) : null);
				if(speciesFromDBSpeciesArr != null && speciesFromDBSpeciesArr.length > 0){//DBSpecies match
					//Choose the species in struct if exists
					newSpecies = speciesFromDBSpeciesArr[0];
					for(int i=0;i<speciesFromDBSpeciesArr.length;i+= 1){
						if(newModel.getSpeciesContext(speciesFromDBSpeciesArr[i], newStruct) != null){
							newSpecies = speciesFromDBSpeciesArr[i];
							break;
						}
					}
				}else{// No DBSpecies match
					//See if there is a species with same name
					newSpecies = newModel.getSpecies(oldSpecies.getCommonName());
					if( newSpecies == null){//No name matches
						String newName = oldSpecies.getCommonName();
						newSpecies = new Species(newName,oldSpecies.getAnnotation(),oldSpecies.getDBSpecies());
						newModel.addSpecies(newSpecies);
						speciesHash.put(oldSpecies, newSpecies);
					}
				}
			}else{// Has species (==)
				newSpecies = oldSpecies;
			}
		}
	}  catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not add species to model : " + e.getMessage());
	}

	return newSpecies;
}

/**
 * Insert the method's description here.
 * Creation date: (5/10/2003 3:55:25 PM)
 * @param newModel cbit.vcell.model.Model
 * @param newStruct cbit.vcell.model.Structure
 * @param bNew boolean
 */
protected static final SpeciesContext pasteSpecies(Species oldSpecies,Model newModel, Structure newStruct, boolean bNew, 
		IdentityHashMap<Species, Species> speciesHash) {

	if(!newModel.contains(newStruct)){
		throw new IllegalArgumentException("CartoonTool.pasteSpecies model "+newModel.getName()+" does not contain structure "+newStruct.getName());
	}
	
	Species newSpecies = null;
	if(oldSpecies != null){
		try {
			newSpecies = getNewSpecies(speciesHash, oldSpecies, newModel, bNew, newStruct);
			//see if we have SpeciesContext
			SpeciesContext speciesContext = newModel.getSpeciesContext(newSpecies,newStruct);
			if(speciesContext == null){ //Has Species but not SpeciesContext
				newModel.addSpeciesContext(newSpecies,newStruct);
			}
		}catch(Exception e){
			cbit.vcell.client.PopupGenerator.showErrorDialog(e.getMessage());
		}
	}
	return newModel.getSpeciesContext(newSpecies,newStruct);
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 8:26:55 PM)
 * @return cbit.vcell.clientdb.DocumentManager
 */
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager argDocumentManager) {
	this.documentManager = argDocumentManager;
}


/**
 * This method was created by a SmartGuide.
 */
protected void showCreateSpeciesContextDialog(GraphPane myGraphPane, final Model model,Structure structure, java.awt.Point locationForSpeciesContextShape) {
	if(getDialogOwner(myGraphPane) == null){
		return;
	}
	//
	JFrame parent = (JFrame)BeanUtils.findTypeParentOfComponent(myGraphPane, javax.swing.JFrame.class);
	EditSpeciesDialog createSpeciesContextDialog = new EditSpeciesDialog(parent);	
	createSpeciesContextDialog.initAddSpecies(model,structure,getDocumentManager());
	ZEnforcer.showModalDialogOnTop(createSpeciesContextDialog, myGraphPane);
	if (locationForSpeciesContextShape != null && createSpeciesContextDialog.getSpeciesContext()!=null){
		SpeciesContext speciesContext = model.getSpeciesContext(createSpeciesContextDialog.getSpeciesContext().getName());
		if (speciesContext!=null){
			Shape scShape = getGraphModel().getShapeFromModelObject(speciesContext);
			scShape.setLocation(locationForSpeciesContextShape);
		}
	}
}


/**
 * This method was created by a SmartGuide.
 */
protected void showEditSpeciesDialog(GraphPane myGraphPane,SpeciesContext speciesContext) {
	//
	if(getDialogOwner(myGraphPane) == null){
		return;
	}
	//
	JFrame parent = (JFrame)BeanUtils.findTypeParentOfComponent(myGraphPane, javax.swing.JFrame.class);
	EditSpeciesDialog editSpeciesDialog = new EditSpeciesDialog(parent);
	editSpeciesDialog.initEditSpecies(speciesContext,getDocumentManager());
	//
	ZEnforcer.showModalDialogOnTop(editSpeciesDialog, myGraphPane);
}


/**
 * This method was created by a SmartGuide.
 */
protected void showCreateGlobalParamDialog(GraphPane myGraphPane, final Model model, java.awt.Point location) {
	if(getDialogOwner(myGraphPane) == null){
		return;
	}
	//
	AddModelParamDialog createGlobalParamDialog = new AddModelParamDialog();
	createGlobalParamDialog.initAddModelParam(model);
	if(location != null){
		createGlobalParamDialog.setLocation(location);
	}
	//
	getDialogOwner(myGraphPane).remove(createGlobalParamDialog);
	getDialogOwner(myGraphPane).add(createGlobalParamDialog, JDesktopPane.MODAL_LAYER);
	org.vcell.util.BeanUtils.centerOnComponent(createGlobalParamDialog, getDialogOwner(myGraphPane));
	createGlobalParamDialog.setVisible(true);
}


/**
 * This method was created by a SmartGuide.
 */
public static final void showFeaturePropertiesDialog(GraphPane myGraphPane,Model model,Feature parentFeature,Feature childFeature) {
	//
	// showFeaturePropertyDialog is invoked in two modes:
	//
	// 1) parent!=null and child==null
	//      upon ok, it adds a new feature to the supplied parent.
	//
	// 2) parent==null and child!=null
	//      upon ok, edits the feature name
	//
	if((parentFeature == null && childFeature == null) || (parentFeature != null && childFeature != null)){
		throw new IllegalArgumentException("Can't set FeatureProperties with current feature arguments");
	}
	JFrame parent = (JFrame)BeanUtils.findTypeParentOfComponent(myGraphPane, JFrame.class);
	FeatureDialog featureDialog = new FeatureDialog(parent);
	//
	featureDialog.setModel(model);
	featureDialog.setChildFeature(childFeature);
	featureDialog.setParentFeature(parentFeature);
	if(parentFeature != null){
		featureDialog.setTitle("Add New Feature to " + parentFeature.getName());
	}else{
		featureDialog.setTitle("Properties for " + childFeature.getName());
	}
	//
	ZEnforcer.showModalDialogOnTop(featureDialog, myGraphPane);
}


/**
 * This method was created by a SmartGuide.
 */
public static final void showMembranePropertiesDialog(GraphPane myGraphPane,Membrane membrane) {
	if(getDialogOwner(myGraphPane) == null){
		return;
	}
	JFrame parent = (JFrame)BeanUtils.findTypeParentOfComponent(myGraphPane, JFrame.class);
	MembraneDialog membraneDialog = new MembraneDialog(parent);
	membraneDialog.init(membrane);
	membraneDialog.setTitle("Properties for " + membrane.getName());
	ZEnforcer.showModalDialogOnTop(membraneDialog, myGraphPane);
}
}