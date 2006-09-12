package cbit.vcell.graph;
import cbit.gui.*;
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.geometry.*;
import cbit.vcell.graph.*;
import cbit.vcell.mapping.*;
import cbit.vcell.model.*;
import cbit.vcell.model.gui.*;
import cbit.vcell.model.gui.EditSpeciesDialog;
import cbit.vcell.model.gui.FluxReaction_Dialog;
import cbit.vcell.model.gui.SimpleReactionPanelDialog;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import cbit.vcell.clientdb.DocumentManager;

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
 * Insert the method's description here.
 * Creation date: (5/10/2003 3:55:25 PM)
 * @param model cbit.vcell.model.Model
 * @param struct cbit.vcell.model.Structure
 * @param bNew boolean
 */
protected static final void pasteReactionSteps(ReactionStep[] reactionStepsArr,Model model, Structure struct, boolean bNew) {

	//Save original lists in case we have to undo (shallow clone)
	Species[] originalSpecies = (Species[])model.getSpecies().clone();
	SpeciesContext[] originalSpeciesContexts = (SpeciesContext[])model.getSpeciesContexts().clone();
	ReactionStep[] originalReactionSteps = (ReactionStep[])model.getReactionSteps().clone();

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

	try{
		int counter = 0;
		Structure currentStruct = struct;
		String copiedStructName = reactionStepsArr[counter].getStructure().getName();
		do{
			ReactionStep newReactionStep = (ReactionStep)cbit.util.BeanUtils.cloneSerializable(reactionStepsArr[counter]);
			newReactionStep.refreshDependencies();
			if(!newReactionStep.getStructure().getClass().equals(struct.getClass())){
				throw new Exception("Copied RectionStep structure type="+newReactionStep.getStructure().getClass().getName()+
									"\ndoes not equal target structure type="+struct.getClass().getName());
			}
			ReactionParticipant[] rpArr = newReactionStep.getReactionParticipants();
			for(int i=0;i<rpArr.length;i+= 1){
				Structure pasteStruct = currentStruct;
				if(newReactionStep.getStructure() instanceof Membrane && currentStruct instanceof Membrane){
					Membrane membr = (Membrane)newReactionStep.getStructure();
					if(rpArr[i].getStructure().getName().equals(membr.getOutsideFeature().getName())){
						pasteStruct = ((Membrane)currentStruct).getOutsideFeature();
					}else if(rpArr[i].getStructure().getName().equals(membr.getInsideFeature().getName())){
						pasteStruct = ((Membrane)currentStruct).getInsideFeature();
					}
				}
				SpeciesContext sc = pasteSpecies(rpArr[i].getSpecies(),model,pasteStruct,bNew);
				rpArr[i].setSpeciesContext(sc);
			}
			String newName = newReactionStep.getName();
			while(model.getReactionStep(newName) != null){
				newName = cbit.util.TokenMangler.getNextEnumeratedToken(newName);
			}
			newReactionStep.setName(newName);
			newReactionStep.setStructure(currentStruct);
			model.addReactionStep(newReactionStep);
			counter+= 1;
			if(counter == reactionStepsArr.length){
				break;
			}
			
			if(!copiedStructName.equals(reactionStepsArr[counter].getStructure().getName())){
				if(currentStruct instanceof Feature){
					currentStruct = ((Feature)currentStruct).getMembrane();
				}else if (currentStruct instanceof Membrane){
					currentStruct = ((Membrane)currentStruct).getInsideFeature();
				}
			}
			copiedStructName = reactionStepsArr[counter].getStructure().getName();
		}while(true);
	}catch(Exception e){

		//Undo anything we've done already
		String removeErrors = "";
		Species[] currentSpecies = (Species[])model.getSpecies().clone();
		SpeciesContext[] currentSpeciesContexts = (SpeciesContext[])model.getSpeciesContexts().clone();
		ReactionStep[] currentReactionSteps = (ReactionStep[])model.getReactionSteps().clone();

		boolean isOriginal;
		
		for(int i=0; i < currentReactionSteps.length;i+= 1){
			isOriginal = false;
			for(int j=0; j < originalReactionSteps.length;j+= 1){
				if(originalReactionSteps[j] == currentReactionSteps[i]){
					isOriginal = true;
					break;
				}
			}
			if(!isOriginal){
				try{
					model.removeReactionStep(currentReactionSteps[i]);
				}catch(Exception e2){
					removeErrors+= removeErrors+"\n"+e.getMessage();
				}
			}
		}

		for(int i=0; i < currentSpeciesContexts.length;i+= 1){
			isOriginal = false;
			for(int j=0; j < originalSpeciesContexts.length;j+= 1){
				if(originalSpeciesContexts[j] == currentSpeciesContexts[i]){
					isOriginal = true;
					break;
				}
			}
			if(!isOriginal){
				try{
					model.removeSpeciesContext(currentSpeciesContexts[i]);
				}catch(Exception e2){
					removeErrors+= removeErrors+"\n"+e.getMessage();
				}
			}
		}
		
		for(int i=0; i < currentSpecies.length;i+= 1){
			isOriginal = false;
			for(int j=0; j < originalSpecies.length;j+= 1){
				if(originalSpecies[j] == currentSpecies[i]){
					isOriginal = true;
					break;
				}
			}
			if(!isOriginal){
				try{
					model.removeSpecies(currentSpecies[i]);
				}catch(Exception e2){
					removeErrors+= removeErrors+"\n"+e.getMessage();
				}
			}
		}
		
		cbit.vcell.client.PopupGenerator.showErrorDialog(e.getMessage()+(removeErrors.length() == 0?"":removeErrors+"\n\nModel may be corrupt"));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2003 3:55:25 PM)
 * @param model cbit.vcell.model.Model
 * @param struct cbit.vcell.model.Structure
 * @param bNew boolean
 */
protected static final SpeciesContext pasteSpecies(Species species,Model model, Structure struct, boolean bNew) {

	if(!model.contains(struct)){
		throw new IllegalArgumentException("CartoonTool.pasteSpecies model "+model.getName()+" does not contain structure "+struct.getName());
	}
	Species newSpecies = null;
	if(species != null){
		try{
			if(bNew){
				String newName = species.getCommonName();
				while(model.getSpecies(newName) != null){
					newName = cbit.util.TokenMangler.getNextEnumeratedToken(newName);
				}
				newSpecies = new Species(newName,species.getAnnotation(),species.getDBSpecies());
				model.addSpecies(newSpecies);
				model.addSpeciesContext(newSpecies,struct);
				
			}else{
				//Find a matching existing species if possible
				if(!model.contains(species)){// Doesn't have Species (==)
					//see if we have a species with DBSpecies that matches
					Species[] speciesFromDBSpeciesArr = (species.getDBSpecies() != null?model.getSpecies(species.getDBSpecies()):null);
					if(speciesFromDBSpeciesArr != null && speciesFromDBSpeciesArr.length > 0){//DBSpecies match
						//Choose the species in struct if exists
						newSpecies = speciesFromDBSpeciesArr[0];
						for(int i=0;i<speciesFromDBSpeciesArr.length;i+= 1){
							if(model.getSpeciesContext(speciesFromDBSpeciesArr[i],struct) != null){
								newSpecies = speciesFromDBSpeciesArr[i];
								break;
							}
						}
					}else{// No DBSpecies match
						//See if there is a species with same name
						newSpecies = model.getSpecies(species.getCommonName());
						if( newSpecies == null){//No name matches
							return pasteSpecies(species,model,struct,true);
						}
					}
				}else{// Has species (==)
					newSpecies = species;
				}
				
				////Add the species if necessary
				//if(!model.contains(newSpecies)){
					//model.addSpecies(newSpecies);
				//}
				
				//see if we have SpeciesContext
				SpeciesContext speciesContext = model.getSpeciesContext(newSpecies,struct);
				if(speciesContext == null){ //Has Species but not SpeciesContext
					model.addSpeciesContext(newSpecies,struct);
				}//else(...){...}// Has SpeciesContext
			}
		}catch(Exception e){
			cbit.vcell.client.PopupGenerator.showErrorDialog(e.getMessage());
		}
	}
	return model.getSpeciesContext(newSpecies,struct);
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
protected void showCreateSpeciesContextDialog(GraphPane myGraphPane, final Model model,Structure structure, java.awt.Point location, final java.awt.Point locationForSpeciesContextShape) {
	if(getDialogOwner(myGraphPane) == null){
		return;
	}
	//
	final cbit.vcell.model.gui.EditSpeciesDialog createSpeciesContextDialog = new cbit.vcell.model.gui.EditSpeciesDialog();
	createSpeciesContextDialog.addInternalFrameListener(
			new javax.swing.event.InternalFrameListener() {
				public void internalFrameActivated(InternalFrameEvent e) {}
				public void internalFrameClosed(InternalFrameEvent e) {
					if (locationForSpeciesContextShape != null && createSpeciesContextDialog.getSpeciesContext()!=null){
						SpeciesContext speciesContext = model.getSpeciesContext(createSpeciesContextDialog.getSpeciesContext().getName());
						if (speciesContext!=null){
							Shape scShape = getGraphModel().getShapeFromModelObject(speciesContext);
							scShape.setLocation(locationForSpeciesContextShape);
						}
					}
					//setMode(SELECT_MODE);
				}
				public void internalFrameClosing(InternalFrameEvent e) {}
				public void internalFrameDeactivated(InternalFrameEvent e) {}
				public void internalFrameDeiconified(InternalFrameEvent e) {}
				public void internalFrameIconified(InternalFrameEvent e) {}
				public void internalFrameOpened(InternalFrameEvent e) {}
			}
		);
	//
	createSpeciesContextDialog.initAddSpecies(model,structure,getDocumentManager());
	if(location != null){createSpeciesContextDialog.setLocation(location);}
	//
	getDialogOwner(myGraphPane).remove(createSpeciesContextDialog);
	getDialogOwner(myGraphPane).add(createSpeciesContextDialog, JDesktopPane.MODAL_LAYER);
	cbit.util.BeanUtils.centerOnComponent(createSpeciesContextDialog, getDialogOwner(myGraphPane));
	createSpeciesContextDialog.setVisible(true);
}


/**
 * This method was created by a SmartGuide.
 */
protected void showEditSpeciesDialog(GraphPane myGraphPane,SpeciesContext speciesContext, java.awt.Point location) {
	//
	if(getDialogOwner(myGraphPane) == null){
		return;
	}
	//
	cbit.vcell.model.gui.EditSpeciesDialog editSpeciesDialog = new cbit.vcell.model.gui.EditSpeciesDialog();
	editSpeciesDialog.initEditSpecies(speciesContext,getDocumentManager());
	editSpeciesDialog.setLocation(location);
	//
	getDialogOwner(myGraphPane).remove(editSpeciesDialog);
	getDialogOwner(myGraphPane).add(editSpeciesDialog, JDesktopPane.MODAL_LAYER);
	cbit.util.BeanUtils.centerOnComponent(editSpeciesDialog, getDialogOwner(myGraphPane));
	editSpeciesDialog.setVisible(true);
}


/**
 * This method was created by a SmartGuide.
 */
public static final void showFeaturePropertiesDialog(GraphPane myGraphPane,Model model,Feature parentFeature,Feature childFeature, java.awt.Point location) {
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
	cbit.vcell.model.gui.FeatureDialog featureDialog = new cbit.vcell.model.gui.FeatureDialog();
	//
	featureDialog.setModel(model);
	featureDialog.setChildFeature(childFeature);
	featureDialog.setParentFeature(parentFeature);
	if(parentFeature != null){
		featureDialog.setTitle("Add New Feature to "+parentFeature.getName());
	}else{
		featureDialog.setTitle("Edit Feature "+childFeature.getName());
	}
	featureDialog.setLocation(location);
	//
	getDialogOwner(myGraphPane).add(featureDialog, JDesktopPane.MODAL_LAYER);
	cbit.util.BeanUtils.centerOnComponent(featureDialog, getDialogOwner(myGraphPane));
	featureDialog.show();
}


/**
 * This method was created by a SmartGuide.
 */
public static final void showMembranePropertiesDialog(GraphPane myGraphPane,Membrane membrane, java.awt.Point location) {
	if(getDialogOwner(myGraphPane) == null){
		return;
	}
	cbit.vcell.model.gui.MembraneDialog membraneDialog = new cbit.vcell.model.gui.MembraneDialog();
	membraneDialog.init(membrane);
	membraneDialog.setTitle("Membrane Dialog for "+membrane.getName());
	membraneDialog.setLocation(location);
	getDialogOwner(myGraphPane).add(membraneDialog, JDesktopPane.MODAL_LAYER);
	cbit.util.BeanUtils.centerOnComponent(membraneDialog, getDialogOwner(myGraphPane));
	membraneDialog.show();
}
}