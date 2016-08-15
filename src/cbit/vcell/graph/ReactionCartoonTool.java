/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.SimpleFilenameFilter;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DialogUtils.TableListResult;
import org.vcell.util.gui.SimpleTransferable;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCFileChooser;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.GraphListener;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphPane;
import cbit.gui.graph.GraphResizeManager;
import cbit.gui.graph.RubberBandEdgeShape;
import cbit.gui.graph.RubberBandRectShape;
import cbit.gui.graph.Shape;
import cbit.gui.graph.ShapeUtil;
import cbit.gui.graph.actions.ActionUtil;
import cbit.gui.graph.actions.CartoonToolEditActions;
import cbit.gui.graph.actions.CartoonToolMiscActions;
import cbit.gui.graph.actions.CartoonToolSaveAsImageActions;
import cbit.gui.graph.actions.GraphLayoutTasks;
import cbit.gui.graph.actions.GraphViewAction;
import cbit.gui.graph.visualstate.VisualState.PaintLayer;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.graph.structures.AllStructureSuite;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GeneralLumpedKinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReactionStep.ReactionNameScope;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.StructureUtil;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.model.gui.DBReactionWizardPanel;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.publish.ITextWriter;

public class ReactionCartoonTool extends BioCartoonTool implements BioCartoonTool.RXPasteInterface{

	public static final int MIN_DRAG_DISTANCE_TO_CREATE_NEW_ELEMENTS_SQUARED = 114;
	
	private ReactionCartoon reactionCartoon = null;
	// for dragging speciesContext's around
	private boolean bMoving = false;
	private Shape startShape = null;
	private Shape movingShape = null;
	private Point startPointWorld = null;
	private Point movingPointWorld = null;
	private Point movingOffsetWorld = null;
	// for dragging rectangle around
	private boolean bRectStretch = false;
	private RubberBandRectShape rectShape = null;
	// for dragging line around
	private boolean bLineStretch = false;
	private Point endPointWorld = null;
	private RubberBandEdgeShape edgeShape = null;
	private Mode mode = null;

	private final static String SEARCHABLE_REACTIONS_CONTEXT_OBJECT = "SearchableReactionsContextObject";
	
	public static enum LineType {
		NULL("<<?>>", Color.red, Cursor.MOVE_CURSOR), CATALYST("<<C A T A L Y S T>>", Color.GRAY), 
		PRODUCT("<<P R O D U C T>>"), REACTANT("<<R E A C T A N T>>") /*, FLUX("<<F L U X>>")*/;
		
		private final String label;
		private final Color color;
		private final Cursor cursor;
		
		private LineType(String label, Color color, int cursorType) {
			this.label = label;
			this.color = color;
			this.cursor = Cursor.getPredefinedCursor(cursorType);
		};
		
		private LineType(String label, Color color) {
			this(label, color, Cursor.HAND_CURSOR);
		};
		
		private LineType(String label) {
			this(label, Color.BLACK);
		};
		
		public String getLabel() { return label; }
		public Color getColor() { return color; }
		public Cursor getCursor() { return cursor; }
	}

	public ReactionCartoonTool() {
		super();
	}

	@Override
	public GraphModel getGraphModel() {
		return getReactionCartoon();
	}

	private LineType getLineTypeFromAttachment(SpeciesContext speciesContext, Point worldPoint) throws Exception {
		Shape mouseOverShape = getReactionCartoon().pickWorld(worldPoint);
		if (mouseOverShape instanceof ReactionStepShape) {
			// check if the ReactionStep already has a ReactionParticipant for
			// this SpeciesContext
			ReactionStep reactionStep = (ReactionStep) mouseOverShape.getModelObject();
			ReactionParticipant[] rps = reactionStep.getReactionParticipants();
			if (mouseOverShape instanceof SimpleReactionShape) {
				switch (mouseOverShape.getAttachmentFromAbs(worldPoint)) {
				case Shape.ATTACH_LEFT: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Reactant && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.REACTANT;
				}
				case Shape.ATTACH_CENTER: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Catalyst && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.CATALYST;
				}
				case Shape.ATTACH_RIGHT: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Product && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.PRODUCT;
				}
				}
			} else if (mouseOverShape instanceof FluxReactionShape) {
				switch (mouseOverShape.getAttachmentFromAbs(worldPoint)) {
				case Shape.ATTACH_LEFT: {
//					for (int i = 0; i < rps.length; i++) {
//						if ((rps[i] instanceof Reactant || rps[i] instanceof Product) && rps[i].getSpeciesContext() == speciesContext) {
//							return LineType.NULL;
//						}
//					}
//					return LineType.FLUX;
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Reactant && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.REACTANT;
				}
				case Shape.ATTACH_CENTER: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Catalyst && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.CATALYST;
				}
				case Shape.ATTACH_RIGHT: {
					for (int i = 0; i < rps.length; i++) {
//						if ((rps[i] instanceof Reactant || rps[i] instanceof Product) && rps[i].getSpeciesContext() == speciesContext) {
//							return LineType.NULL;
//						}
//					}
//					return LineType.FLUX;
						if (rps[i] instanceof Product && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.PRODUCT;
				}
				}
			}
		}
		return LineType.NULL;
	}

	private LineType getLineTypeFromDirection(Shape startingShape, Point worldPoint) throws Exception {
		Shape mouseOverShape = getReactionCartoon().pickWorld(worldPoint);
		if (mouseOverShape instanceof ReactionStepShape){
			if (startingShape instanceof SpeciesContextShape){
				SpeciesContext speciesContext = (SpeciesContext)startingShape.getModelObject();
				// check if the ReactionStep already has a Product for this SpeciesContext
				ReactionStep reactionStep = (ReactionStep)mouseOverShape.getModelObject();
				ReactionParticipant[] rps = reactionStep.getReactionParticipants();
				if ((mouseOverShape instanceof SimpleReactionShape) || (mouseOverShape instanceof FluxReactionShape)){
					for (int i = 0; i < rps.length; i++){
						if (rps[i] instanceof Reactant && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.REACTANT;
						}
//				else if (mouseOverShape instanceof FluxReactionShape){
//					for (int i = 0; i < rps.length; i++){
//						if ((rps[i] instanceof Reactant || rps[i] instanceof Product) && rps[i].getSpeciesContext() == speciesContext) {
//							return LineType.NULL;
//						}
//					}
//					return LineType.FLUX;
//				}
					}
		}else if (mouseOverShape instanceof SpeciesContextShape){
			SpeciesContext speciesContext = (SpeciesContext)mouseOverShape.getModelObject();
			if (startingShape instanceof SpeciesContextShape){
				return LineType.PRODUCT;  // straight from one species to another ... will create a reaction upon release
			}else if (startingShape instanceof ReactionStepShape){
				ReactionStep reactionStep = (ReactionStep)startingShape.getModelObject();
				ReactionParticipant[] rps = reactionStep.getReactionParticipants();
//				if (reactionStep instanceof FluxReaction){
//					for (int i = 0; i < rps.length; i++){
//						if ((rps[i] instanceof Reactant || rps[i] instanceof Product) && rps[i].getSpeciesContext() == speciesContext) {
//							return LineType.NULL;
//						}
//					}
//				} else 
				if (reactionStep instanceof SimpleReaction || reactionStep instanceof FluxReaction){
					for (int i = 0; i < rps.length; i++){
						if (rps[i] instanceof Reactant && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.REACTANT;
				}
			}
		}
		return LineType.NULL;
	}
	
	public ReactionCartoon getReactionCartoon() {
		return reactionCartoon;
	}

	private ReactionStep[] getSelectedReactionStepArray() {
		List<Shape> allSelectedShapes = getReactionCartoon().getSelectedShapes();
		if (allSelectedShapes != null && allSelectedShapes.size() > 0) {
			ArrayList<ReactionStep> rxStepsArr = new ArrayList<ReactionStep>();
			for (int i = 0; i < allSelectedShapes.size(); i += 1) {
				if (allSelectedShapes.get(i).getModelObject() instanceof ReactionStep){
					rxStepsArr.add((ReactionStep) allSelectedShapes.get(i).getModelObject());
				}
			}
			return (rxStepsArr.size()==0?null:rxStepsArr.toArray(new ReactionStep[0]));
		}
		return null;
	}
	private SpeciesContext[] getSelectedSpeciesContextArray() {
		List<Shape> allSelectedShapes = getReactionCartoon().getSelectedShapes();
		if (allSelectedShapes != null && allSelectedShapes.size() > 0) {
			ArrayList<SpeciesContext> speciesContextArr = new ArrayList<SpeciesContext>();
			for (int i = 0; i < allSelectedShapes.size(); i += 1) {
				if (allSelectedShapes.get(i).getModelObject() instanceof SpeciesContext){
					speciesContextArr.add((SpeciesContext) allSelectedShapes.get(i).getModelObject());
				}
			}
			return (speciesContextArr.size()==0?null:speciesContextArr.toArray(new SpeciesContext[0]));
		}
		return null;
	}
	private ReactionParticipant[] getSelectedReactionParticipantArray() {
		List<Shape> allSelectedShapes = getReactionCartoon().getSelectedShapes();
		if (allSelectedShapes != null && allSelectedShapes.size() > 0) {
			ArrayList<ReactionParticipant> reactionParticipantArr = new ArrayList<ReactionParticipant>();
			for (int i = 0; i < allSelectedShapes.size(); i += 1) {
				if (allSelectedShapes.get(i).getModelObject() instanceof ReactionParticipant){
					reactionParticipantArr.add((ReactionParticipant) allSelectedShapes.get(i).getModelObject());
				}
			}
			return (reactionParticipantArr.size()==0?null:reactionParticipantArr.toArray(new ReactionParticipant[0]));
		}
		return null;
	}

	public void layout(String layoutName) throws Exception {
		layout(layoutName,true);
	}
	public void layout(String layoutName,boolean bWarn) throws Exception {
		System.out.println(layoutName);
		//-----Turn off user forced reaction diagram structure order
		if(getReactionCartoon().getStructureSuite() instanceof AllStructureSuite){
			AllStructureSuite allStructureSuite = ((AllStructureSuite)getReactionCartoon().getStructureSuite());
			if(allStructureSuite.getModelStructureOrder()){
				//make auto-sort
				allStructureSuite.setModelStructureOrder(false);
				//get auto-sorted structures
				List<Structure> autoSortedStructures = allStructureSuite.getStructures();
				ArrayList<Diagram> newDiagramOrderList = new ArrayList<Diagram>();
				for(Structure structure:autoSortedStructures){
					newDiagramOrderList.add(getModel().getDiagram(structure));
				}
				Diagram[] autosortDiagramOrder = newDiagramOrderList.toArray(new Diagram[0]);
				if(bWarn){
				final String OK = "OK";
				String response =
					DialogUtils.showWarningDialog(getGraphPane(), "Existing 'Structure' order preference set by user may be reset, continue?", new String[] {OK,"Cancel"},OK);
				if(response != null &&  response.equals(OK)){
					getModel().setDiagrams(autosortDiagramOrder);
				}else{
					allStructureSuite.setModelStructureOrder(true);
					return;
				}
				}else{
					getModel().setDiagrams(autosortDiagramOrder);
				}
			}
		}
		//-----
		GraphLayoutTasks.dispatchTasks(getGraphPane(), graphEmbeddingManager, this, layoutName);
	}

	@Override
	protected void menuAction(Shape shape, String menuAction) {
		if (shape == null) {
			return;
		}
		if (menuAction.equals(CartoonToolMiscActions.Properties.MENU_ACTION)) {
			if (shape instanceof FluxReactionShape) {
//				showFluxReactionPropertiesDialog((FluxReactionShape) shape);
			} else if (shape instanceof SimpleReactionShape) {
//				showSimpleReactionPropertiesDialog((SimpleReactionShape) shape);
			} else if (shape instanceof ReactantShape) {
//				Point locationOnScreen = shape.getSpaceManager().getAbsLoc();
//				Point graphPaneLocation = getGraphPane().getLocationOnScreen();
//				locationOnScreen.translate(graphPaneLocation.x,
//						graphPaneLocation.y);
//				showReactantPropertiesDialog((ReactantShape) shape,
//						locationOnScreen);
			} else if (shape instanceof ProductShape) {
//				Point locationOnScreen = shape.getSpaceManager().getAbsLoc();
//				Point graphPaneLocation = getGraphPane().getLocationOnScreen();
//				locationOnScreen.translate(graphPaneLocation.x,
//						graphPaneLocation.y);
//				showProductPropertiesDialog((ProductShape) shape,
//						locationOnScreen);
			} else if (shape instanceof SpeciesContextShape) {
//				showEditSpeciesDialog(getGraphPane(), getReactionCartoon()
//						.getModel(), ((SpeciesContextShape) shape)
//						.getSpeciesContext());
			} else if (shape instanceof ReactionContainerShape) {
//				ReactionContainerShape rcs = (ReactionContainerShape) shape;
//				if (rcs.getStructure() instanceof Feature) {
//					//
//					// showFeaturePropertyDialog is invoked in two modes:
//					//
//					// 1) parent!=null and child==null
//					// upon ok, it adds a new feature to the supplied parent.
//					//
//					// 2) parent==null and child!=null
//					// upon ok, edits the feature name
//					//
//					showFeaturePropertiesDialog(getGraphPane(),
//							(getReactionCartoon().getModel() == null ? null
//									: getReactionCartoon().getModel()), null,
//							(Feature) rcs.getStructure());
//				} else if (rcs.getStructure() instanceof Membrane) {
//					showMembranePropertiesDialog(getGraphPane(), (Membrane) rcs
//							.getStructure());
//				}
			}

		} else if (menuAction.equals(CartoonToolMiscActions.AddSpecies.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape) {
				getGraphModel().deselectShape(shape);
//				showCreateSpeciesContextDialog(getGraphPane(),
//						getReactionCartoon().getModel(),
//						((ReactionContainerShape) shape).getStructure(), null);
				SpeciesContext speciesContext = getReactionCartoon().getModel().createSpeciesContext(((ReactionContainerShape) shape).getStructure());
				getGraphModel().select(speciesContext);
			}
		} else if (menuAction.equals(CartoonToolEditActions.Copy.MENU_ACTION)) {
			if (shape instanceof SpeciesContextShape || shape instanceof ReactionStepShape) {
				VCellTransferable.ReactionSpeciesCopy reactionSpeciesCopy = new VCellTransferable.ReactionSpeciesCopy(getSelectedSpeciesContextArray(), getSelectedReactionStepArray());
				VCellTransferable.sendToClipboard(reactionSpeciesCopy);
			}
		} else if (/*menuAction.equals(CartoonToolEditActions.Paste.MENU_ACTION)
				|| */menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape) {
				pasteReactionsAndSpecies(((ReactionContainerShape) shape).getStructure());
			}
		} else if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)
				|| menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)) {
			try {
				if(getGraphModel().getSelectedShape() instanceof ReactionContainerShape && menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)){
					getModel().removeStructure(((ReactionContainerShape)getGraphModel().getSelectedShape()).getStructure());
					return;
				}
				if (getSelectedReactionStepArray()!=null || getSelectedSpeciesContextArray()!=null) {
					if(menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)){
						new VCellTransferable.ReactionSpeciesCopy(getSelectedSpeciesContextArray(), getSelectedReactionStepArray());
					}
					deleteReactionsAndSpecies(getGraphPane(),getSelectedReactionStepArray(),getSelectedSpeciesContextArray(),menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION));
				}
				if (getSelectedReactionParticipantArray() != null && menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)) {
					ReactionParticipant[] reactionParticipantArr = getSelectedReactionParticipantArray();
					String response = DialogUtils.showWarningDialog(getGraphPane(),
							"Delete "+reactionParticipantArr.length+" Reaction Stoichiometries",
							new String[] {RXSPECIES_DELETE,RXSPECIES_CANCEL}, RXSPECIES_CANCEL);
					if(response != null && response.equals(RXSPECIES_DELETE)){
						for (int i = 0; i < reactionParticipantArr.length; i++) {
							ReactionStep reactionStep = reactionParticipantArr[i].getReactionStep();
							reactionStep.removeReactionParticipant(reactionParticipantArr[i]);						
						}
					}
				}
			} catch(UserCancelException uce){
				return;
			} catch (PropertyVetoException e) {
				DialogUtils.showErrorDialog(getGraphPane(), e.getMessage());
			} catch (Exception e) {
				DialogUtils.showErrorDialog(getGraphPane(), e.getMessage(), e);
			}

		} else if (menuAction.equals(CartoonToolMiscActions.SearchReactions.MENU_ACTION)) {
			try {
				if (shape instanceof ReactionContainerShape) {
					showReactionBrowserDialog(((ReactionContainerShape) shape).getStructure(), null);
				}
			} catch (Exception e) {
				DialogUtils.showErrorDialog(getGraphPane(), e.getMessage(), e);
			}
		} else if (menuAction.equals(CartoonToolSaveAsImageActions.MenuAction.MENU_ACTION)) {
			try {
				String resType = null;
				if (shape instanceof ReactionContainerShape) {
					showSaveReactionImageDialog();
				}
			} catch (Exception e) {
				e.printStackTrace();
				DialogUtils.showErrorDialog(getGraphPane(), e.getMessage(), e);
			}
		} else if (menuAction.equals(CartoonToolMiscActions.Annotate.MENU_ACTION)) {
			if (shape instanceof ReactionStepShape) {
				// MIRIAMHelper.showMIRIAMAnnotationDialog(((SimpleReactionShape)shape).getReactionStep());
				// System.out.println("Menu action annotate activated...");
				ReactionStep rs = ((ReactionStepShape) shape).getReactionStep();
				VCMetaData vcMetaData = rs.getModel().getVcMetaData();
				try {
					String newAnnotation = DialogUtils.showAnnotationDialog(
							getGraphPane(), vcMetaData.getFreeTextAnnotation(rs));
					vcMetaData.setFreeTextAnnotation(rs, newAnnotation);
				} catch (UtilCancelException e) {
					// Do Nothing
				} catch (Throwable exc) {
					exc.printStackTrace(System.out);
					DialogUtils.showErrorDialog(getGraphPane(),
							"Failed to edit annotation!\n" + exc.getMessage(), exc);
				}
			}
		} else {
			// default action is to ignore
		}

	}
	
	private void pasteReactionsAndSpecies(Structure structure){
		final String RXSPECIES_PASTERX = "Reactions";
		final String RXSPECIES_SPECIES = "Species";
		VCellTransferable.ReactionSpeciesCopy reactionSpeciesCopy = (VCellTransferable.ReactionSpeciesCopy) SimpleTransferable.getFromClipboard(VCellTransferable.REACTION_SPECIES_ARRAY_FLAVOR);
		if(reactionSpeciesCopy != null){
			String response = null;
			if(reactionSpeciesCopy.getReactStepArr() != null &&  reactionSpeciesCopy.getSpeciesContextArr() != null){
				response = DialogUtils.showWarningDialog(getGraphPane(),"Choose Species or Reactions to paste",
						"There are "+reactionSpeciesCopy.getSpeciesContextArr().length+" Species and "+reactionSpeciesCopy.getReactStepArr().length+" Reactions on the clipboard, choose which set to paste.",
						new String[] {RXSPECIES_SPECIES,RXSPECIES_PASTERX,RXSPECIES_CANCEL}, RXSPECIES_CANCEL);
				if(response == null || response.equals(RXSPECIES_CANCEL)){
					return;
				}
			}
			if(reactionSpeciesCopy.getSpeciesContextArr() != null && (response==null || response.equals(RXSPECIES_SPECIES))){
				IdentityHashMap<Species, Species> speciesHash = new IdentityHashMap<Species, Species>();
				Vector<BioModelEntityObject> pastedSpeciesContextV = new Vector<BioModelEntityObject>();
				for (int i = 0; i < reactionSpeciesCopy.getSpeciesContextArr().length; i++) {
					String rootSC = speciesContextRootFinder(reactionSpeciesCopy.getSpeciesContextArr()[i]);
					pastedSpeciesContextV.add(pasteSpecies(getGraphPane(), reactionSpeciesCopy.getSpeciesContextArr()[i].getSpecies(), rootSC, getModel(),structure,true,speciesHash, null));
					copyRelativePosition(getGraphModel(),reactionSpeciesCopy.getSpeciesContextArr()[i], pastedSpeciesContextV.lastElement());
				}
				ReactionCartoonTool.selectAndSaveDiagram(ReactionCartoonTool.this, pastedSpeciesContextV);
			}
			if(reactionSpeciesCopy.getReactStepArr() != null && (response == null || response.equals(RXSPECIES_PASTERX))){
				pasteReactionSteps(getGraphPane(),reactionSpeciesCopy.getReactStepArr(), getModel(),structure,true, null,ReactionCartoonTool.this);
			}
		}

	}
	
	public static void selectAndSaveDiagram(RXPasteInterface rxPasteInterface,List<BioModelEntityObject> reactionAndSpecies){
		
		rxPasteInterface.getGraphPane().getGraphModel().clearSelection();
		rxPasteInterface.saveDiagram();
		for(BioModelEntityObject rxSpecies:reactionAndSpecies){
			rxPasteInterface.getGraphPane().getGraphModel().select(rxSpecies);
		}
	}
	
	public static void copyRelativePosition(GraphModel graphModel,BioModelEntityObject origEntity,BioModelEntityObject newEntity){
		Shape origEntityShape = graphModel.getShapeFromModelObject(origEntity);
		if(origEntityShape == null){//happens when using BioModel 'Searchable Reactions...'
			return;
		}
		Shape newEntityShape = graphModel.getShapeFromModelObject(newEntity);
		newEntityShape.setRelPos(new Point(origEntityShape.getRelPos()));
		
		//offset if completely overlap another shape
		for(Shape shape:graphModel.getShapes()){
			if(shape.getModelObject() instanceof BioModelEntityObject && shape != newEntityShape){
				if(shape.getRelPos().equals(newEntityShape.getRelPos())){
					newEntityShape.setRelPos(new Point(newEntityShape.getRelX()+5, newEntityShape.getRelY()+5));
					break;
				}
			}
		}
	}
	
	public static String speciesContextRootFinder(SpeciesContext speciesContext){
		String rootSC = speciesContext.getName();
		if(speciesContext.getStructure() != null && speciesContext.getStructure().getName() != null){
			String structSuffix = "_"+speciesContext.getStructure().getName();
			if(speciesContext.getName().endsWith(structSuffix)){
				rootSC = speciesContext.getName().substring(0, speciesContext.getName().length()-structSuffix.length());
			}
		}
		return rootSC;
	}
	
	private static final String RXSPECIES_BACK = "Back";
	private static final String RXSPECIES_CANCEL = "Cancel";
	private static final String RXSPECIES_DELETE = "Delete";
	private static final String RXSPECIES_CUT = "Cut";
	private static final String RXSPECIES_ERROR = "Error";

	private static TableListResult showDeleteDetails(Component requester,DeleteSpeciesInfo deleteSpeciesInfo,ReactionStep[] reactionStepArr,boolean bCut,boolean bShowErrorsOnly){
		if(reactionStepArr != null && reactionStepArr.length == 0){
			reactionStepArr = null;
		}
		String[][] rowData = null;
		String[] columnNames = null;
		String title = null;
		if(reactionStepArr != null){
			Arrays.sort(reactionStepArr, new Comparator<ReactionStep>() {
				@Override
				public int compare(ReactionStep o1, ReactionStep o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});			
		}
		final String OK_TO_DELETE = "ok to delete";
		final String ERROR_IF_DELETE = "ERROR if deleted";
		ArrayList<String[]> errorRows = new ArrayList<String[]>();
		if(deleteSpeciesInfo != null && reactionStepArr != null){
			title = (bCut?RXSPECIES_CUT:RXSPECIES_DELETE)+" Reactions/Species."+(deleteSpeciesInfo.bAnyUnresolvable()?"  User must resolve 'error' Flags to "+(bCut?RXSPECIES_CUT:RXSPECIES_DELETE)+".":"");
			columnNames = new String[] {"Type","Name","Flag","Reference"};
			rowData = new String[deleteSpeciesInfo.getRowData().length+reactionStepArr.length][4];
			final int STATUS_ROW = 2;
			for (int i = 0; i < rowData.length; i++) {
				if(i< reactionStepArr.length){
					rowData[i][0] = "Reaction";
					rowData[i][1] = reactionStepArr[i].getName();
					rowData[i][STATUS_ROW] = OK_TO_DELETE;
					rowData[i][3] = "";
				}else{
					int index = i-reactionStepArr.length;
					rowData[i][0] = "Species";
					rowData[i][1] = deleteSpeciesInfo.getRowData()[index][0];
					rowData[i][STATUS_ROW] = (deleteSpeciesInfo.getRowData()[index][1].equals(RXSPECIES_ERROR)?ERROR_IF_DELETE:OK_TO_DELETE);
					rowData[i][3] = deleteSpeciesInfo.getRowData()[index][2]+" "+deleteSpeciesInfo.getRowData()[index][3];
				}
				if(rowData[i][STATUS_ROW].equals(ERROR_IF_DELETE)){
					errorRows.add(rowData[i]);
				}
			}
		}else if(deleteSpeciesInfo != null && reactionStepArr == null){
			title = (bCut?RXSPECIES_CUT:RXSPECIES_DELETE)+" Species."+(deleteSpeciesInfo.bAnyUnresolvable()?"  User must resolve 'error' Flags to "+(bCut?RXSPECIES_CUT:RXSPECIES_DELETE)+".":"");
			columnNames = new String[] {"Species","Flag","Reference"};
			rowData = new String[deleteSpeciesInfo.getRowData().length][3];
			final int STATUS_ROW = 1;
			for (int i = 0; i < deleteSpeciesInfo.getRowData().length; i++) {
				rowData[i][0] = deleteSpeciesInfo.getRowData()[i][0];
				rowData[i][STATUS_ROW] = (deleteSpeciesInfo.getRowData()[i][1].equals(RXSPECIES_ERROR)?ERROR_IF_DELETE:OK_TO_DELETE);
				rowData[i][2] = deleteSpeciesInfo.getRowData()[i][2]+" "+deleteSpeciesInfo.getRowData()[i][3];				
				if(rowData[i][STATUS_ROW].equals(ERROR_IF_DELETE)){
					errorRows.add(rowData[i]);
				}
			}
		}else if(deleteSpeciesInfo == null && reactionStepArr != null){
			title = (bCut?RXSPECIES_CUT:RXSPECIES_DELETE)+" Reactions.";
			columnNames = new String[] {"Reactions"};
			rowData = new String[reactionStepArr.length][1];
			for (int i = 0; i < reactionStepArr.length; i++) {
				rowData[i][0] = reactionStepArr[i].getName();
			}
		}else{
			throw new IllegalArgumentException("Unknown parameter combo 'showDeleteDetails'.");
		}
		if(bShowErrorsOnly && errorRows.size() > 0){
			rowData = errorRows.toArray(new String[0][]);
		}
		TableListResult tableListResult = DialogUtils.showComponentOptionsTableList(requester, title,columnNames, rowData, null ,null,new String[] {RXSPECIES_BACK},RXSPECIES_BACK,null);
		return tableListResult;
	}
	public static void deleteReactionsAndSpecies(Component requester,ReactionStep[] reactionStepArr,SpeciesContext[] speciesContextArr, boolean bCut) throws Exception,UserCancelException{
		if((speciesContextArr == null && reactionStepArr == null) || 
				(reactionStepArr == null && speciesContextArr != null && speciesContextArr.length == 0) ||
				(speciesContextArr == null && reactionStepArr != null && reactionStepArr.length == 0) ||
				(speciesContextArr != null && speciesContextArr.length == 0 && reactionStepArr != null && reactionStepArr.length == 0)){
				return;
		}
		int rxCount = (reactionStepArr!=null?reactionStepArr.length:0);
		int speciesCount = (speciesContextArr!=null?speciesContextArr.length:0);
		boolean bHasBoth = speciesCount > 0 && rxCount > 0;
		
		ReactionCartoon rxCartoon = null;
		if(requester instanceof GraphPane && ((GraphPane)requester).getGraphModel() instanceof ReactionCartoon){
			rxCartoon = (ReactionCartoon)(((GraphPane)requester).getGraphModel());
		}else if(requester instanceof ReactionCartoonEditorPanel){
			rxCartoon = ((ReactionCartoonEditorPanel)requester).getReactionCartoon();
		}else{
			throw new IllegalArgumentException("ReactionCartoonTool.deleteSpeciesContext not implemented for type '"+requester.getClass().getName()+"'");
		}
		DeleteSpeciesInfo deleteSpeciesInfo = null;
		final String DETAILS = "Details...";
		while(true){
			String response = DialogUtils.showWarningDialog(requester,
					(bCut?RXSPECIES_CUT:RXSPECIES_DELETE)+" "+(bHasBoth?"Reactions and Species.":(rxCount > 0?"Reactions.":(speciesCount > 0?"Species.":""))),
					(bCut?RXSPECIES_CUT:RXSPECIES_DELETE)+" "+(bHasBoth?rxCount+" Reactions and "+speciesCount+" Species.":(rxCount > 0?rxCount+" Reactions.":(speciesCount > 0?speciesCount+" Species.":""))),
					new String[] {(bCut?RXSPECIES_CUT:RXSPECIES_DELETE),DETAILS,RXSPECIES_CANCEL},(bCut?RXSPECIES_CUT:RXSPECIES_DELETE));
			if(response == null || response.equals(RXSPECIES_CANCEL)){
				throw UserCancelException.CANCEL_GENERIC;
			}
			deleteSpeciesInfo = (deleteSpeciesInfo==null?detailsDeleteSpecies(requester, speciesContextArr,reactionStepArr,rxCartoon):deleteSpeciesInfo);
			if(response.equals(DETAILS)){
				TableListResult tableListResult = showDeleteDetails(requester,deleteSpeciesInfo,reactionStepArr,bCut,false);
//				if(!tableListResult.selectedOption.equals(RXSPECIES_BACK)){
//				//if(!tableListResult.selectedOption.equals((bCut?RXSPECIES_CUT:RXSPECIES_DELETE))){
//					throw UserCancelException.CANCEL_GENERIC;
//				}
			}else{
				break;
			}
		}
		if(deleteSpeciesInfo != null){
			for(Boolean errors:deleteSpeciesInfo.getbUnresolvableHashMap().values()){
				if(errors){
					while(true){
						String response = DialogUtils.showWarningDialog(requester, "Error warning.",
							"Warning: 1 or more SpeciesContexts have Model references that could cause Model corruption if "+(bCut?RXSPECIES_CUT:RXSPECIES_DELETE)+".", new String[] {DETAILS,RXSPECIES_CANCEL},DETAILS);
						if(response == null || response.equals(RXSPECIES_CANCEL)){
							throw UserCancelException.CANCEL_GENERIC;
						}
						showDeleteDetails(requester,deleteSpeciesInfo,reactionStepArr,bCut,true);
					}
				}
			}
		}

		if(reactionStepArr != null){
			for (int i = 0; i < reactionStepArr.length; i += 1) {
				rxCartoon.getModel().removeReactionStep(reactionStepArr[i]);
			}
		}
		
		if(deleteSpeciesInfo != null){
			//remove all ReactionParticipants ("lines" between reaction and species in "Reaction Diagram") that have this speciesContext before deleting SpeciesContext
			for (SpeciesContext objSpeciesContext:deleteSpeciesInfo.getRxPartHashMap().keySet()) {
				if(deleteSpeciesInfo.getbUnresolvableHashMap().get(objSpeciesContext)){
					continue;
				}
				Iterator<ReactionParticipant> iterRxPart = deleteSpeciesInfo.getRxPartHashMap().get(objSpeciesContext).iterator();
				while(iterRxPart.hasNext()){
					ReactionParticipant objRxPart = iterRxPart.next();
					if(rxCartoon.getObjects().contains(objRxPart)){//Reaction delete may have already removed
						objRxPart.getReactionStep().removeReactionParticipant(objRxPart);
					}
				}
			}					
			//remove the SpeciesContext
			for (int i = 0; i < speciesContextArr.length; i++) {
				if(deleteSpeciesInfo.getbUnresolvableHashMap().get(speciesContextArr[i])){
					continue;
				}
				if(rxCartoon.getObjects().contains(speciesContextArr[i])){//Reaction delete may have already removed
					rxCartoon.getModel().removeSpeciesContext(speciesContextArr[i]);
				}
			}
		}
	}
	
	private static class DeleteSpeciesInfo{
		private HashMap<SpeciesContext,HashSet<ReactionParticipant>> rxPartHashMap;
		private HashMap<SpeciesContext, Boolean> bUnresolvableHashMap;
		private String[][] rowData;
		public DeleteSpeciesInfo(
				HashMap<SpeciesContext, HashSet<ReactionParticipant>> rxPartHashMap,
				HashMap<SpeciesContext, Boolean> bUnresolvableHashMap,
				String[][] rowData) {
			this.rxPartHashMap = rxPartHashMap;
			this.bUnresolvableHashMap = bUnresolvableHashMap;
			this.rowData = rowData;
		}
		public HashMap<SpeciesContext, HashSet<ReactionParticipant>> getRxPartHashMap() {
			return rxPartHashMap;
		}
		public HashMap<SpeciesContext, Boolean> getbUnresolvableHashMap() {
			return bUnresolvableHashMap;
		}
		public String[][] getRowData(){
			return rowData;
		}
		public boolean bAllUnresolvable(){
			for(Boolean bUnresolvable:bUnresolvableHashMap.values()){
				if(!bUnresolvable){
					return false;
				}
			}
			return true;
		}
		public boolean bAnyUnresolvable(){
			for(Boolean bUnresolvable:bUnresolvableHashMap.values()){
				if(bUnresolvable){
					return true;
				}
			}
			return false;
		}
		public boolean hasReactionParticipant(ReactionParticipant reactionParticipant){
			for(HashSet<ReactionParticipant> rxPartHash:rxPartHashMap.values()){
				if(rxPartHash.contains(reactionParticipant)){
					return true;
				}
			}
			return false;
		}
	}
	private static DeleteSpeciesInfo detailsDeleteSpecies(Component requester,SpeciesContext[] speciesContextArr,ReactionStep[] toBeDeletedReactStepArr,ReactionCartoon rxCartoon) throws Exception,UserCancelException{
		if(speciesContextArr == null || speciesContextArr.length == 0){return null;}
		//Warn user that there may be some BioModel components that reference speciesContext to be removed
		//Get ReactionParticipant list
		Collection<Shape> rxPartColl = rxCartoon.getShapes();
		HashMap<SpeciesContext,HashSet<ReactionParticipant>> rxPartHashMap = new HashMap<SpeciesContext,HashSet<ReactionParticipant>>();
		for (Shape objShape:rxPartColl) {
			if(objShape instanceof ReactionParticipantShape){
				ReactionParticipant objReactionParticipant = ((ReactionParticipantShape)objShape).getReactionParticipant();
				if(Arrays.asList(speciesContextArr).contains(objReactionParticipant.getSpeciesContext())){
					if(!rxPartHashMap.containsKey(objReactionParticipant.getSpeciesContext())){
						rxPartHashMap.put(objReactionParticipant.getSpeciesContext(), new HashSet<ReactionParticipant>());
					}
					if(!rxPartHashMap.get(objReactionParticipant.getSpeciesContext()).contains(objReactionParticipant)){
						rxPartHashMap.get(objReactionParticipant.getSpeciesContext()).add(objReactionParticipant);
					}
				}
			}
		}
		int reactionParticipantCount = 0;
		for(HashSet<ReactionParticipant> objReactPart:rxPartHashMap.values()){
			reactionParticipantCount+= objReactPart.size();
		}
		//find bioModel and get SymbolTable references to SpeciesContext
		BioModelEditor bioModelEditor = (BioModelEditor)BeanUtils.findTypeParentOfComponent(requester, BioModelEditor.class);
		if(bioModelEditor == null){
			throw new Exception("Error deleting Speciescontext, Can't find BiomodelEditor");
		}
		BioModel bioModel = bioModelEditor.getBioModelWindowManager().getVCDocument();
		HashMap<SpeciesContext,HashSet<SymbolTableEntry>> referencingSymbolsHashMap = new HashMap<SpeciesContext,HashSet<SymbolTableEntry>>();
		for (int i = 0; i < speciesContextArr.length; i++) {
			List<SymbolTableEntry> referencingSymbolsList = bioModel.findReferences(speciesContextArr[i]);
			if(referencingSymbolsList != null && referencingSymbolsList.size() > 0){
				if(!referencingSymbolsHashMap.containsKey(speciesContextArr[i])){
					referencingSymbolsHashMap.put(speciesContextArr[i], new HashSet<SymbolTableEntry>());
				}
				referencingSymbolsHashMap.get(speciesContextArr[i]).addAll(referencingSymbolsList);
			}
		}
		int referencingSymbolsCount = 0;
		for(HashSet<SymbolTableEntry> objSimTableEntry:referencingSymbolsHashMap.values()){
			referencingSymbolsCount+= objSimTableEntry.size();
		}
		//Warn user about delete
		
		
		HashMap<SpeciesContext, Boolean> bUnresolvableHashMap = new HashMap<SpeciesContext, Boolean>();
		for (int i = 0; i < speciesContextArr.length; i++) {
			bUnresolvableHashMap.put(speciesContextArr[i], Boolean.FALSE);
		}
		String[][] rowData = null;
		if(rxPartHashMap.size() == 0 && referencingSymbolsHashMap.size() == 0){
			rowData = new String[speciesContextArr.length][4];
			for (int i = 0; i < speciesContextArr.length; i++) {
				rowData[i][0] = speciesContextArr[i].getName();
				rowData[i][1] = "";
				rowData[i][2] = "";
				rowData[i][3] = "";
			}
			Arrays.sort(rowData, new Comparator<String[]>() {
				@Override
				public int compare(String[] o1, String[] o2) {
					return o1[0].compareToIgnoreCase(o2[0]);
				}
			});
		}else{
			//find SpeciesContext that had no reference warnings
			Vector<SpeciesContext> speciesContextNoReferences = new Vector<SpeciesContext>();
			for (int i = 0; i < speciesContextArr.length; i++) {
				if(!rxPartHashMap.containsKey(speciesContextArr[i]) && !referencingSymbolsHashMap.containsKey(speciesContextArr[i])){
					speciesContextNoReferences.add(speciesContextArr[i]);
				}
			}
			rowData = new String[reactionParticipantCount+referencingSymbolsCount+speciesContextNoReferences.size()][4];
			int count = 0;
			for(SpeciesContext objSpeciesContext:speciesContextNoReferences){
				rowData[count][0] = objSpeciesContext.getName();
				rowData[count][1] = "";
				rowData[count][2] = "";
				rowData[count][3] = "";
				count++;
			}
			for (SpeciesContext objSpeciesContext:rxPartHashMap.keySet()) {
				Iterator<ReactionParticipant> iterRxPart = rxPartHashMap.get(objSpeciesContext).iterator();
				while(iterRxPart.hasNext()){
					rowData[count][0] = objSpeciesContext.getName();
					rowData[count][1] = "";
					rowData[count][2] = "Reaction Diagram stoichiometry '"+iterRxPart.next().getReactionStep().getName()+"'";
					rowData[count][3] = "";
					count++;
				}
			}
			for (SpeciesContext objSpeciesContext:referencingSymbolsHashMap.keySet()) {
				Iterator<SymbolTableEntry> iterSymbolTableEntry = referencingSymbolsHashMap.get(objSpeciesContext).iterator();
				while(iterSymbolTableEntry.hasNext()){
					rowData[count][0] = objSpeciesContext.getName();
					rowData[count][1] = "";
					SymbolTableEntry objSymbolTableEntry = iterSymbolTableEntry.next();
					boolean bKineticsParameter = objSymbolTableEntry instanceof KineticsParameter;
					if(bKineticsParameter){
						KineticsParameter kp = (KineticsParameter)objSymbolTableEntry;
						boolean isOK = kp.isRegenerated();
						for (int i = 0; toBeDeletedReactStepArr != null && i < toBeDeletedReactStepArr.length; i++) {
							if(toBeDeletedReactStepArr[i] == kp.getKinetics().getReactionStep()){
								//OK to delete this Speciescontext if were deleting the reaction that contained the reference
								isOK = true;
							}
						}
						rowData[count][1] = (isOK?"":RXSPECIES_ERROR);
						bUnresolvableHashMap.put(objSpeciesContext, bUnresolvableHashMap.get(objSpeciesContext) || !isOK);
					}
					boolean bReaction = objSymbolTableEntry.getNameScope() instanceof ReactionNameScope;
					rowData[count][2] = (bReaction?"Reaction":objSymbolTableEntry.getNameScope().getClass().getName())+"( "+objSymbolTableEntry.getNameScope().getName()+" )";
					rowData[count][3] = (bKineticsParameter?"Parameter":objSymbolTableEntry.getClass().getName())+"( "+objSymbolTableEntry.getName()+" )";
					count++;
				}
			}
//			for (SymbolTableEntry referencingSTE : referencingSymbols) {
//				System.out.println("REFERENCE   "+referencingSTE.getClass().getName()+"("+referencingSTE.getName()+") nameScope "+referencingSTE.getNameScope().getClass().getName()+"("+referencingSTE.getNameScope().getName()+")");
//			}
			Arrays.sort(rowData, new Comparator<String[]>() {
				@Override
				public int compare(String[] o1, String[] o2) {
					return o1[0].compareToIgnoreCase(o2[0]);
				}
			});
		}
		if(rowData == null || rowData.length == 0){
			return null;
		}
		return new DeleteSpeciesInfo(rxPartHashMap, bUnresolvableHashMap,rowData);

	}
	
	private int getDragDistanceSquared() {
		if(startPointWorld == null || endPointWorld == null) { return 0; }
		int dx = endPointWorld.x - startPointWorld.x;
		int dy = endPointWorld.y - startPointWorld.y;
		return dx*dx + dy*dy;
	}
	
	private MouseAdapter myStopEditAdapter = new MouseAdapter() {
		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			if(ReactionCartoonTool.this.getGraphPane().getComponentCount() == 0){
				return;
			}
			Component editcomponent = ReactionCartoonTool.this.getGraphPane().getComponent(0);
			MouseEvent newEvent = 
				SwingUtilities.convertMouseEvent(ReactionCartoonTool.this.getGraphPane(), e, editcomponent);

			if(newEvent.getX() >= 0 && newEvent.getY() >= 0 && newEvent.getX() < (editcomponent.getWidth()) && newEvent.getY() < (editcomponent.getHeight())){
				return;
			}
			stopEditing();
		}
	};
	
	private GraphListener myGraphListener = new GraphListener() {
		@Override
		public void graphChanged(GraphEvent event) {
			stopEditing();
		}
	};
	
	private void stopEditing(){
		getGraphPane().removeMouseListener(myStopEditAdapter);
		getGraphModel().removeGraphListener(myGraphListener);
		getGraphPane().requestFocus();
		if(getGraphPane().getComponentCount() > 0){
			getGraphPane().remove(0);
		}
		getGraphPane().validate();
		getGraphPane().repaint();
	}
	private void editInPlace(final Shape selectedShape,Point worldPoint){
		if(getGraphPane().getComponentCount() > 0){//remove any existing editor
			getGraphPane().remove(0);
		}
		Rectangle labelOutline = null;
		//What kind of thing is being edited
		if(selectedShape instanceof ReactionContainerShape){
			labelOutline = ((ReactionContainerShape)selectedShape).getLabelOutline(selectedShape.getAbsX(),selectedShape.getAbsY());
			if(!labelOutline.contains(worldPoint)){
				return;
			}
		}else if(selectedShape instanceof SpeciesContextShape){
			labelOutline = ((SpeciesContextShape)selectedShape).getLabelOutline(selectedShape.getAbsX(),selectedShape.getAbsY());
		}else if(selectedShape instanceof SimpleReactionShape){
			labelOutline = ((SimpleReactionShape)selectedShape).getLabelOutline(selectedShape.getAbsX(),selectedShape.getAbsY());
		}else if(selectedShape instanceof FluxReactionShape){
			labelOutline = ((FluxReactionShape)selectedShape).getLabelOutline(selectedShape.getAbsX(),selectedShape.getAbsY());
		}else if(selectedShape instanceof ReactionRuleDiagramShape){
			labelOutline = ((ReactionRuleDiagramShape)selectedShape).getLabelOutline(selectedShape.getAbsX(),selectedShape.getAbsY());
		}else{
			return;
		}
		//Add press 'Enter' action, 'Escape' action, editor gets focus and mouse 'Exit' parent action
		if(true){
			final JTextField jTextField = new JTextField(selectedShape.getLabel());
			jTextField.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						//Type specific edit actions
						if(selectedShape instanceof ReactionContainerShape){
							((ReactionContainerShape)selectedShape).getStructure().setName(jTextField.getText(),true);
						}else if(selectedShape instanceof SpeciesContextShape){
							((SpeciesContextShape)selectedShape).getSpeciesContext().setName(jTextField.getText());
						}else if(selectedShape instanceof SimpleReactionShape){
							((SimpleReactionShape)selectedShape).getReactionStep().setName(jTextField.getText());
						}else if(selectedShape instanceof FluxReactionShape){
							((FluxReactionShape)selectedShape).getReactionStep().setName(jTextField.getText());
						}else if(selectedShape instanceof ReactionRuleDiagramShape){
							((ReactionRuleDiagramShape)selectedShape).getReactionRule().setName(jTextField.getText());
						}
					}catch(Exception e2){
						e2.printStackTrace();
						DialogUtils.showErrorDialog(ReactionCartoonTool.this.getGraphPane(), e2.getMessage());
					}
					stopEditing();
				}
			});
			ReactionCartoonTool.this.getGraphPane().removeMouseListener(myStopEditAdapter);//just to be sure
			ReactionCartoonTool.this.getGraphPane().addMouseListener(myStopEditAdapter);
			
			getGraphModel().removeGraphListener(myGraphListener);
			getGraphModel().addGraphListener(myGraphListener);
			
			InputMap im = jTextField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	        ActionMap am = jTextField.getActionMap();
	        im.put(KeyStroke.getKeyStroke("ESCAPE"), "cancelChange");
	        am.put("cancelChange", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					stopEditing();
				}
			});
	        GraphResizeManager grm = getGraphModel().getResizeManager();
			jTextField.setBounds((int)grm.zoom(labelOutline.x),(int)grm.zoom(labelOutline.y),Math.max((int)grm.zoom(labelOutline.width+2),100), Math.max((int)grm.zoom(labelOutline.height+2),20));
			getGraphPane().add(jTextField);
			getGraphPane().validate();
			jTextField.requestFocus();
		}
		return;

	}

	@Override
	public void mouseClicked(MouseEvent event) {
		Point screenPoint = new Point(event.getX(), event.getY());
		Point worldPoint = screenToWorld(screenPoint);

		try {
			if(event.getButton() != MouseEvent.BUTTON1){
				return;
			}
			switch (mode) {
			case SELECT: {
				if (event.getClickCount() == 2) {
					final Shape selectedShape = getReactionCartoon().getSelectedShape();
					if(	selectedShape instanceof ReactionContainerShape ||
						selectedShape instanceof SpeciesContextShape ||
						selectedShape instanceof SimpleReactionShape ||
						selectedShape instanceof FluxReactionShape ||
						selectedShape instanceof ReactionRuleDiagramShape ||
						selectedShape instanceof RuleParticipantSignatureDiagramShape){
						editInPlace(selectedShape,worldPoint);
					}
					if (selectedShape != null) {
						menuAction(selectedShape, CartoonToolMiscActions.Properties.MENU_ACTION);
					}
				}
				break;
			}
			case STEP: {
				Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);

				if (pickedShape instanceof ReactionContainerShape) {
					Structure structure = ((ReactionContainerShape) pickedShape).getStructure();
					if (getReactionCartoon().getStructureSuite().areReactionsShownFor(structure)) {
						ReactionStep reactionStep = getReactionCartoon().getModel().createSimpleReaction(structure);
						positionShapeForObject(structure, reactionStep, worldPoint);
						saveDiagram();
					}
				}
				break;
			}
			case FLUX: {
				Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);

				if (pickedShape instanceof ReactionContainerShape) {
					Structure structure = ((ReactionContainerShape) pickedShape)
							.getStructure();
					if (structure instanceof Membrane) {
						Membrane membrane = (Membrane) structure;
						FluxReaction fluxReaction = getReactionCartoon().getModel().createFluxReaction(membrane);
						ReactionStepShape frShape = (ReactionStepShape) 
							getReactionCartoon().getShapeFromModelObject(fluxReaction);
						Point parentLocation = frShape.getParent().getSpaceManager().getAbsLoc();
						frShape.getSpaceManager().setRelPos(worldPoint.x - parentLocation.x, 
						worldPoint.y - parentLocation.y);
						saveDiagram();
						// setMode(SELECT_MODE);
					} else {
						// setMode(SELECT_MODE);
						// throw new Exception("fluxes only applicable to membranes");
					}
				}
				break;
			}
			case SPECIES: {
				Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);	
				if (pickedShape instanceof ReactionContainerShape){
					SpeciesContext speciesContext = getReactionCartoon().getModel().createSpeciesContext(((ReactionContainerShape) pickedShape).getStructure());
					reactionCartoon.clearSelection();
					getGraphModel().select(speciesContext);
					positionShapeForObject(speciesContext.getStructure(), speciesContext, worldPoint);
//					showCreateSpeciesContextDialog(getGraphPane(), getReactionCartoon().getModel(), ((ReactionContainerShape) pickedShape).getStructure(), scShapeLocation);
					saveDiagram();
				}
			}
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mouseClicked: uncaught exception");
			e.printStackTrace(System.out);
			Point canvasLoc = getGraphPane().getLocationOnScreen();
			canvasLoc.x += screenPoint.x;
			canvasLoc.y += screenPoint.y;
			DialogUtils.showErrorDialog(getGraphPane(), e.getMessage(), e);
		}
	}

	private HashMap<RXContainerDropTargetInfo, Boolean> lastRXContainerDropTargetInfoMap;
	private ReactionContainerShape getReactionContainerShapeAtPoint(Point dragPointWorld){
		Shape mouseShape = getReactionCartoon().pickWorld(dragPointWorld);
		if(mouseShape instanceof ReactionContainerShape){
			return (ReactionContainerShape)mouseShape;
		}else {
			while(mouseShape != null && !(mouseShape instanceof ContainerContainerShape)){
				mouseShape = mouseShape.getParent();
			}
			if(mouseShape != null){
				mouseShape = ((ContainerContainerShape)mouseShape).pick(dragPointWorld, PaintLayer.COMPARTMENT);
				if(mouseShape instanceof ReactionContainerShape){
					return (ReactionContainerShape)mouseShape;
				}
			}
		}
		return null;
	}
	private RXContainerDropTargetInfo getSelectedContainerDropTargetInfo(){
		if(lastRXContainerDropTargetInfoMap != null){
			for(RXContainerDropTargetInfo rxContainerDropTargetInfo:lastRXContainerDropTargetInfoMap.keySet()){
				if(lastRXContainerDropTargetInfoMap.get(rxContainerDropTargetInfo)){
					return rxContainerDropTargetInfo;
				}
			}
		}
		return null;
	}
	private HashMap<Rectangle, Boolean> getDropTargetRectangleMap(boolean bZoomAdjust,boolean bExcludeNonActionDropTargets){
		GraphResizeManager graphResizeManager = getGraphModel().getResizeManager();
		HashMap<Rectangle, Boolean> dropTargetRectangleMap = new HashMap<Rectangle, Boolean>();
		for(RXContainerDropTargetInfo rxContainerDropTargetInfo:lastRXContainerDropTargetInfoMap.keySet()){
			if(bExcludeNonActionDropTargets && (startShape == rxContainerDropTargetInfo.dropShape || startShape == rxContainerDropTargetInfo.closestNeighborShape)){
				continue;
			}
			Rectangle dropTargetRectangle = rxContainerDropTargetInfo.absoluteRectangle;
			if(bZoomAdjust){
				dropTargetRectangle = new Rectangle(
					(int)graphResizeManager.zoom(rxContainerDropTargetInfo.absoluteRectangle.x),
					(int)graphResizeManager.zoom(rxContainerDropTargetInfo.absoluteRectangle.y),
					(int)graphResizeManager.zoom(rxContainerDropTargetInfo.absoluteRectangle.width),
					(int)graphResizeManager.zoom(rxContainerDropTargetInfo.absoluteRectangle.height));
			}
			dropTargetRectangleMap.put(dropTargetRectangle,lastRXContainerDropTargetInfoMap.get(rxContainerDropTargetInfo));
		}
		return dropTargetRectangleMap;
	}
	@Override
	public void mouseDragged(MouseEvent event) {
		if(getGraphPane().getComponentCount() > 0){
			//we're editing, cancel
			stopEditing();
		}
		if (event.isPopupTrigger()) {
			return;
		}
		boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
		boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
		if(mode == Mode.SELECT && bStartRxContainerLabel){
			if(dragStructTimer != null){
				dragStructTimer.stop();
			}
			Point dragPointWorld = getGraphModel().getResizeManager().unzoom(event.getPoint());
			RXContainerDropTargetInfo lastTrueRXContainerDropTargetInfo = getSelectedContainerDropTargetInfo();
			lastRXContainerDropTargetInfoMap = updateRXContainerDropTargetInfoMap(dragPointWorld);
			RXContainerDropTargetInfo currentTrueRXContainerDropTargetInfo = getSelectedContainerDropTargetInfo();
//			System.out.println(lastTrueRXContainerDropTargetInfo+" "+currentTrueRXContainerDropTargetInfo);
			if(dragStructTimer != null || !Compare.isEqualOrNull(lastTrueRXContainerDropTargetInfo, currentTrueRXContainerDropTargetInfo)){
				activateDropTargetEnable();
				getGraphPane().repaint();
			}
			return;
		}
		try {
			switch (mode) {
			case SELECT: {
				Point worldPoint = screenToWorld(event.getX(), event.getY());
				if (bMoving) {
					if(movingShape instanceof  ReactionContainerShape)
					{
						bMoving = false;
						return;
					}
					List<Shape> selectedShapes = getReactionCartoon().getSelectedShapes();
					// constrain to stay within the corresponding parent for the
					// "movingShape" as well as all other selected (hence
					// moving) shapes.
					Point movingParentLoc = movingShape.getParent().getSpaceManager().getAbsLoc();
					Dimension movingParentSize = movingShape.getParent().getSpaceManager().getSize();
					worldPoint.x = Math.max(movingOffsetWorld.x
							+ movingParentLoc.x, Math.min(movingOffsetWorld.x
							+ movingParentLoc.x + movingParentSize.width
							- movingShape.getSpaceManager().getSize().width, worldPoint.x));
					worldPoint.y = Math.max(movingOffsetWorld.y
							+ movingParentLoc.y, Math.min(movingOffsetWorld.x
							+ movingParentLoc.y + movingParentSize.height
							- movingShape.getSpaceManager().getSize().height, worldPoint.y));
					for (Shape shape : selectedShapes) {
						if (shape != movingShape) {
							Point selectedParentLoc = shape.getParent().getSpaceManager().getAbsLoc();
							Dimension selectedParentSize = shape.getParent().getSpaceManager().getSize();
							int selectedMovingOffsetX = movingOffsetWorld.x + 
							(movingShape.getSpaceManager().getAbsLoc().x - 
									shape.getSpaceManager().getAbsLoc().x);
							int selectedMovingOffsetY = movingOffsetWorld.y + 
							(movingShape.getSpaceManager().getAbsLoc().y - 
									shape.getSpaceManager().getAbsLoc().y);
							worldPoint.x = Math.max(selectedMovingOffsetX + selectedParentLoc.x,
									Math.min(selectedMovingOffsetX + selectedParentLoc.x + 
											selectedParentSize.width - 
											shape.getSpaceManager().getSize().width, worldPoint.x));
							worldPoint.y = Math.max(selectedMovingOffsetY + selectedParentLoc.y,
									Math.min(selectedMovingOffsetY + selectedParentLoc.y + 
											selectedParentSize.height - 
											shape.getSpaceManager().getSize().height, worldPoint.y));
						}
					}
					getGraphPane().setCursor(
							Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					Point newMovingPoint = new Point(worldPoint.x
							- movingOffsetWorld.x, worldPoint.y
							- movingOffsetWorld.y);
					int deltaX = newMovingPoint.x - movingPointWorld.x;
					int deltaY = newMovingPoint.y - movingPointWorld.y;
					movingPointWorld = newMovingPoint;
					movingShape.getSpaceManager().setRelPos(movingPointWorld.x - movingParentLoc.x, 
					movingPointWorld.y - movingParentLoc.y);
					// for any other "movable" shapes that are selected, move
					// them also
					for (Shape shape : selectedShapes) {
						if (shape != movingShape) {
							shape.getSpaceManager().move(deltaX, deltaY);
						}
					}
					getGraphPane().invalidate();
					((JViewport) getGraphPane().getParent()).revalidate();
					getGraphPane().repaint();
				} else if (bRectStretch) {
					// constain to stay within parent
					Point parentLoc = rectShape.getParent().getSpaceManager().getAbsLoc();
					Dimension parentSize = rectShape.getParent().getSpaceManager().getSize();
					worldPoint.x = Math.max(1, Math.min(parentSize.width - 1,
							worldPoint.x - parentLoc.x))
							+ parentLoc.x;
					worldPoint.y = Math.max(1, Math.min(parentSize.height - 1,
							worldPoint.y - parentLoc.y))
							+ parentLoc.y;
					getGraphPane().setCursor(
							Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					// getGraphPane().repaint();
					Graphics2D g = (Graphics2D) getGraphPane().getGraphics();
					AffineTransform oldTransform = g.getTransform();
					g.scale(0.01 * getReactionCartoon().getZoomPercent(),
							0.01 * getReactionCartoon().getZoomPercent());
					g.setXORMode(Color.white);
					rectShape.setEnd(endPointWorld);
					rectShape.paint(g, 0, 0);
					endPointWorld = worldPoint;
					rectShape.setEnd(endPointWorld);
					rectShape.paint(g, 0, 0);
					g.setTransform(oldTransform);
				} else {
					Shape shape = (getGraphModel().getSelectedShape() != null ?
						getGraphModel().getSelectedShape():getReactionCartoon().pickWorld(worldPoint));
					if (!bCntrl && !bShift && (shape instanceof SpeciesContextShape 				|| shape instanceof ReactionStepShape ||
											   shape instanceof RuleParticipantSignatureDiagramShape || shape instanceof ReactionRuleDiagramShape)){
						bMoving=true;
						movingShape = shape;
						movingPointWorld = shape.getSpaceManager().getAbsLoc();
						movingOffsetWorld = new Point(worldPoint.x-movingPointWorld.x,worldPoint.y-movingPointWorld.y);
					} 
					else if (shape instanceof ReactionContainerShape || bShift || bCntrl){
						if(rectShape == null){
							return;
						}
						if(startPointWorld != null && worldPoint != null && startPointWorld.equals(worldPoint)){
							//Don't start stretching until mouse moves at least 1 from startpoint
							return;
						}
						bRectStretch = true;
						endPointWorld = new Point((startPointWorld!=null?startPointWorld.x:worldPoint.x),(startPointWorld!=null?startPointWorld.y:worldPoint.y));
						rectShape.setEnd(endPointWorld);
						if (!(shape instanceof ReactionContainerShape)) {
							shape.getParent().addChildShape(rectShape);
						} else {
							shape.addChildShape(rectShape);
						}
						Graphics2D g = (Graphics2D) getGraphPane()
								.getGraphics();
						AffineTransform oldTransform = g.getTransform();
						g.scale(0.01 * getReactionCartoon().getZoomPercent(),
								0.01 * getReactionCartoon().getZoomPercent());
						g.setXORMode(Color.white);
						rectShape.paint(g, 0, 0);
						g.setTransform(oldTransform);
					}
				}
				break;
			}
			case LINE: case LINEDIRECTED: case LINECATALYST: {
				int x = event.getX();
				int y = event.getY();
				Point worldPoint = new Point(
						(int) (x * 100.0 / getReactionCartoon().getZoomPercent()),
						(int) (y * 100.0 / getReactionCartoon().getZoomPercent()));
				if (bLineStretch) {
					// repaint last location with XOR
					Graphics2D g = (Graphics2D) getGraphPane().getGraphics();
					g.setXORMode(Color.white);
					edgeShape.setEnd(endPointWorld);
					AffineTransform oldTransform = g.getTransform();
					g.scale(getReactionCartoon().getZoomPercent() * 0.01,
							getReactionCartoon().getZoomPercent() * 0.01);
					edgeShape.paint_NoAntiAlias(g, 0, 0);
					g.setTransform(oldTransform);
					// set label and color for line depending on attachment area on ReactionStepShape
					LineType lineType;
					if(Mode.LINE.equals(mode)) {
						SpeciesContext speciesContext = 
							(SpeciesContext) edgeShape.getStartShape().getModelObject();
						lineType = getLineTypeFromAttachment(speciesContext, worldPoint);
					} else if(Mode.LINECATALYST.equals(mode)) {
						lineType = LineType.CATALYST;
					} else if (edgeShape.getStartShape() instanceof SpeciesContextShape){
						lineType = LineType.REACTANT;
					} else {
						lineType = LineType.PRODUCT;						
					}
					edgeShape.setLabel(lineType.getLabel());
					edgeShape.setForgroundColor(lineType.getColor());
					getGraphPane().setCursor(lineType.getCursor());
					// move line and paint with XOR
					endPointWorld = worldPoint;
					edgeShape.setEnd(worldPoint);
					oldTransform = g.getTransform();
					g.scale(getReactionCartoon().getZoomPercent() * 0.01,
							getReactionCartoon().getZoomPercent() * 0.01);
					edgeShape.paint_NoAntiAlias(g, 0, 0);
					g.setTransform(oldTransform);
				} else {
					if (edgeShape != null) {
						return;
					}	
					if (startShape instanceof SpeciesContextShape || 
							((Mode.LINEDIRECTED.equals(mode) || Mode.LINECATALYST.equals(mode)) 
									&& startShape instanceof ElipseShape)) {
						ElipseShape startElipseShape = (ElipseShape) startShape;
						bLineStretch = true;
						endPointWorld = worldPoint;
						edgeShape = new RubberBandEdgeShape(
								startElipseShape, null, getReactionCartoon());
						edgeShape.setEnd(worldPoint);
						Graphics2D g = (Graphics2D)getGraphPane().getGraphics();
						g.setXORMode(Color.white);
						AffineTransform oldTransform = g.getTransform();
						g.scale(getReactionCartoon().getZoomPercent() * 0.01,
								getReactionCartoon().getZoomPercent() * 0.01);
						edgeShape.paint(g, 0, 0);
						g.setTransform(oldTransform);
					} else if((Mode.LINEDIRECTED.equals(mode) || Mode.LINECATALYST.equals(mode)) 
							&& startShape instanceof ContainerShape) {
						bLineStretch = true;
						endPointWorld = worldPoint;
						edgeShape = new RubberBandEdgeShape((ElipseShape) null, null, getReactionCartoon());
						edgeShape.setStart(startPointWorld);
						edgeShape.setEnd(worldPoint);
						Graphics2D g = (Graphics2D)getGraphPane().getGraphics();
						g.setXORMode(Color.white);
						AffineTransform oldTransform = g.getTransform();
						g.scale(getReactionCartoon().getZoomPercent() * 0.01,
								getReactionCartoon().getZoomPercent() * 0.01);
						edgeShape.paint(g, 0, 0);
						g.setTransform(oldTransform);						
					}
				}
				break;
			}
			default: {
				break;
			}
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mouseDragged: uncaught exception");
			e.printStackTrace(System.out);
		}
	}

	private boolean bStartRxContainerLabel = false;
	@Override
	public void mouseMoved(MouseEvent event) {
		if(getReactionCartoon() == null){ return; }
		Point startPointWorld = 
				getGraphModel().getResizeManager().unzoom(event.getPoint());
		if(mode == Mode.STRUCTURE){
			RXContainerDropTargetInfo lastTrueRXContainerDropTargetInfo = getSelectedContainerDropTargetInfo();
			lastRXContainerDropTargetInfoMap = updateRXContainerDropTargetInfoMap(startPointWorld);
			RXContainerDropTargetInfo currentTrueRXContainerDropTargetInfo = getSelectedContainerDropTargetInfo();
			if(!Compare.isEqualOrNull(lastTrueRXContainerDropTargetInfo, currentTrueRXContainerDropTargetInfo)){
				activateDropTargetEnable();
				getGraphPane().repaint();
			}
			return;
		}
		Shape mouseShape = getReactionCartoon().pickWorld(startPointWorld);
		if(mouseShape instanceof ReactionContainerShape){
			Rectangle labelOutlineRectangle = ((ReactionContainerShape)mouseShape).getLabelOutline(mouseShape.getAbsX(), mouseShape.getAbsY());
			boolean bLabel = labelOutlineRectangle.contains(startPointWorld.x, startPointWorld.y);
			if(bLabel){
				if(!getGraphPane().getCursor().equals(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))){
					getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			}else{
				if(!getGraphPane().getCursor().equals(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))){
					getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}else{
			if(!getGraphPane().getCursor().equals(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))){
				getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	
	private void addStructure(boolean bMembrane,RXContainerDropTargetInfo selectedContainerDropTargetInfo){
		Integer insertFlag = selectedContainerDropTargetInfo.insertFlag;
		try{
			Structure myStructure = null;
			if(bMembrane){
				myStructure = ReactionCartoonTool.this.getModel().createMembrane();
			}else{
				myStructure = ReactionCartoonTool.this.getModel().createFeature();
			}		
			ArrayList<Structure> diagramStructures =  new ArrayList<Structure>(ReactionCartoonTool.this.getReactionCartoon().getStructureSuite().getStructures());
			diagramStructures.remove(myStructure);
			if(new Integer(RXContainerDropTargetInfo.INSERT_BEGINNING).equals(insertFlag)){
				diagramStructures.add(0, myStructure);
			}else if(new Integer(RXContainerDropTargetInfo.INSERT_END).equals(insertFlag)){
				diagramStructures.add(myStructure);
			}else{
				diagramStructures.add(diagramStructures.indexOf(selectedContainerDropTargetInfo.dropShape.getStructure()),myStructure);
			}
			((AllStructureSuite)getReactionCartoon().getStructureSuite()).setModelStructureOrder(true);
//			ReactionCartoonTool.this.getModel().setStructures(structures.toArray(new Structure[0]));
			ArrayList<Diagram> newDiagramOrderList = new ArrayList<Diagram>();
			for(Structure structure:diagramStructures){
				newDiagramOrderList.add(getModel().getDiagram(structure));
			}
			getModel().setDiagrams(newDiagramOrderList.toArray(new Diagram[0]));
		    lastRXContainerDropTargetInfoMap = updateRXContainerDropTargetInfoMap(new Point(-1,-1));
		    resetDropTargets(false,mode == Mode.STRUCTURE);
		    getGraphPane().repaint();
		}catch(Exception e2){
			e2.printStackTrace();
			DialogUtils.showErrorDialog(getGraphPane(), "Error adding structure: "+e2.getMessage());
		}
	}
	
	private Timer dragStructTimer;
	
	@Override
	public void mousePressed(MouseEvent event) {
		if(getReactionCartoon() == null){ return; }
		try {
			int eventX = event.getX();
			int eventY = event.getY();
			startPointWorld = new java.awt.Point(
					(int) (eventX * 100.0 / getReactionCartoon().getZoomPercent()),
					(int) (eventY * 100.0 / getReactionCartoon().getZoomPercent()));
			startShape = getReactionCartoon().pickWorld(startPointWorld);
			if(event.isPopupTrigger()){//Mac popup
				popupMenu(getReactionCartoon().getSelectedShape(),eventX,eventY);
			}else if(event.getButton() != MouseEvent.BUTTON1){
				//this may be a win, linux popup menu gesture on mouseRELEASED, let it pass through
				return;
			}else if(mode == Mode.STRUCTURE){
				final RXContainerDropTargetInfo selectedContainerDropTargetInfo = getSelectedContainerDropTargetInfo();
				if(selectedContainerDropTargetInfo != null){
					JPopupMenu jPopupMenu = new JPopupMenu();
					JMenuItem menuItem = new JMenuItem("Add Compartment");
				    menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addStructure(false,selectedContainerDropTargetInfo);
						}
					});
				    jPopupMenu.add(menuItem);
				    menuItem = new JMenuItem("Add Membrane");
				    menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addStructure(true,selectedContainerDropTargetInfo);
						}
					});
				    jPopupMenu.add(menuItem);
				    jPopupMenu.show(event.getComponent(),event.getX(), event.getY());
				}
			}else if(mode == Mode.SELECT){
					// User force select
					boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
					
					selectEventFromWorld(startPointWorld, bShift);

					if(startShape instanceof ReactionContainerShape){//setup potential compartment 'drag'
						rectShape = new RubberBandRectShape(startPointWorld,startPointWorld, getReactionCartoon());
						Rectangle labelOutlineRectangle = ((ReactionContainerShape)startShape).getLabelOutline(startShape.getAbsX(), startShape.getAbsY());
						bStartRxContainerLabel = labelOutlineRectangle.contains(startPointWorld.x, startPointWorld.y);
						if(bStartRxContainerLabel){
							lastRXContainerDropTargetInfoMap = updateRXContainerDropTargetInfoMap(startPointWorld);
							//delay showing structure drag drop targets in case user action will turn into
							//popup menu or name double-click
							if(dragStructTimer != null){
								dragStructTimer.stop();
							}
							dragStructTimer = new Timer(500, new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									
									activateDropTargetEnable();
									getGraphPane().repaint();
								}
							});
							dragStructTimer.setRepeats(false);
							dragStructTimer.start();
						}
					}
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mousePressed: uncaught exception");
			e.printStackTrace(System.out);
		}
	}

	private class RXContainerDropTargetInfo implements Matchable{
		public final static int INSERT_BEGINNING = -1;
		public final static int INSERT_END = -2;
		public ReactionContainerShape closestNeighborShape;
		public Integer insertFlag;
		public ReactionContainerShape dropShape;
		public Rectangle absoluteRectangle;
		
		public RXContainerDropTargetInfo(ReactionContainerShape dropShape,ReactionContainerShape closestNeighborShape,Integer insertFlag) {
			this.dropShape = dropShape;
			this.closestNeighborShape = closestNeighborShape;
			this.insertFlag = insertFlag;
			this.absoluteRectangle = initAbsoluteDropTargetRectangle();
		}
		private Rectangle initAbsoluteDropTargetRectangle(){
			int x = 0;
			int width = 0;
			final int BOX_RADIUS = 10;
			if(insertFlag == null){
				x = Math.max(dropShape.getAbsX(),closestNeighborShape.getAbsX())-BOX_RADIUS;
				width = 2*BOX_RADIUS;
			}else{
				if(insertFlag == RXContainerDropTargetInfo.INSERT_BEGINNING){
					x = dropShape.getAbsX();
					width = BOX_RADIUS;
				}else{
					x = dropShape.getAbsX()+dropShape.getWidth()-BOX_RADIUS;
					width = BOX_RADIUS;
				}
			}
			return new Rectangle(x, dropShape.getAbsY(), width, dropShape.getHeight());
		}
		@Override
		public boolean compareEqual(Matchable obj) {
			if(obj instanceof RXContainerDropTargetInfo){
				return
					(this.dropShape == ((RXContainerDropTargetInfo)obj).dropShape)
					&&
					(this.closestNeighborShape == ((RXContainerDropTargetInfo)obj).closestNeighborShape)
					&&
					Compare.isEqualOrNull(this.insertFlag, ((RXContainerDropTargetInfo)obj).insertFlag)
					&&
					this.absoluteRectangle.equals(((RXContainerDropTargetInfo)obj).absoluteRectangle);
			}
			return false;
		}
	}
	
	private HashMap<RXContainerDropTargetInfo, Boolean> updateRXContainerDropTargetInfoMap(Point pointWorld){
		HashMap<RXContainerDropTargetInfo, Boolean> rxContainerDropTargetInfoMap = null;
		if(rxContainerDropTargetInfoMap == null){
//			System.out.println("-----redoing map...");
			AllStructureSuite allStructureSuite = (getReactionCartoon().getStructureSuite() instanceof AllStructureSuite?(AllStructureSuite)getReactionCartoon().getStructureSuite():null);
			if(allStructureSuite == null){
				throw new RuntimeException("Expecting "+AllStructureSuite.class.getName());
			}
			rxContainerDropTargetInfoMap = new HashMap<ReactionCartoonTool.RXContainerDropTargetInfo, Boolean>();
			Structure[] originalOrderedStructArr = allStructureSuite.getStructures().toArray(new Structure[0]);
			for(int i=0;i<originalOrderedStructArr.length;i++){
				Shape currentRXContainerShape = getReactionCartoon().getShapeFromModelObject(originalOrderedStructArr[i]);
				if(i == 0){
					rxContainerDropTargetInfoMap.put(new RXContainerDropTargetInfo((ReactionContainerShape)currentRXContainerShape, null, RXContainerDropTargetInfo.INSERT_BEGINNING), false);
				}else{
					rxContainerDropTargetInfoMap.put(new RXContainerDropTargetInfo((ReactionContainerShape)currentRXContainerShape, (ReactionContainerShape)getReactionCartoon().getShapeFromModelObject(originalOrderedStructArr[i-1]), null), false);
				}
				if (i == (originalOrderedStructArr.length-1)){
					rxContainerDropTargetInfoMap.put(new RXContainerDropTargetInfo((ReactionContainerShape)currentRXContainerShape, null, RXContainerDropTargetInfo.INSERT_END), false);
				}
			}			
		}
//		System.out.println("-----Starting to update selection..."+pointWorld);
		for(RXContainerDropTargetInfo rxcContainerDropTargetInfo:rxContainerDropTargetInfoMap.keySet()){
			if(rxcContainerDropTargetInfo.absoluteRectangle.contains(pointWorld)){
//				System.out.println(rxcContainerDropTargetInfo.dropShape.getLabel()+" "+rxcContainerDropTargetInfo.closestNeighborShape);
				rxContainerDropTargetInfoMap.put(rxcContainerDropTargetInfo,true);
			}else{
				rxContainerDropTargetInfoMap.put(rxcContainerDropTargetInfo,false);
			}
		}
		return rxContainerDropTargetInfoMap;
	}
	
	private void activateDropTargetEnable(){
		resetDropTargets(null,mode == Mode.STRUCTURE);
		for(RXContainerDropTargetInfo rxContainerDropTargetInfo:lastRXContainerDropTargetInfoMap.keySet()){
			if((mode != mode.STRUCTURE) && (startShape == rxContainerDropTargetInfo.dropShape || startShape == rxContainerDropTargetInfo.closestNeighborShape)){
				continue;
			}
			boolean bSelected = lastRXContainerDropTargetInfoMap.get(rxContainerDropTargetInfo);
			if(rxContainerDropTargetInfo.insertFlag != null){
				if(rxContainerDropTargetInfo.insertFlag == RXContainerDropTargetInfo.INSERT_BEGINNING){
					rxContainerDropTargetInfo.dropShape.setDropTargetEnableLow(bSelected,mode == Mode.STRUCTURE);
				}else if(rxContainerDropTargetInfo.insertFlag == RXContainerDropTargetInfo.INSERT_END){
					rxContainerDropTargetInfo.dropShape.setDropTargetEnableHigh(bSelected,mode == Mode.STRUCTURE);
				}
			}else{
				rxContainerDropTargetInfo.dropShape.setDropTargetEnableLow(bSelected,mode == Mode.STRUCTURE);
				rxContainerDropTargetInfo.closestNeighborShape.setDropTargetEnableHigh(bSelected,mode == Mode.STRUCTURE);
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		if(getReactionCartoon() == null){ return; }
		try {
			if(dragStructTimer != null){dragStructTimer.stop();}
			endPointWorld = getReactionCartoon().getResizeManager().unzoom(event.getPoint());
			Shape endShape = getReactionCartoon().pickWorld(endPointWorld);
			if (event.isPopupTrigger() && mode == Mode.SELECT) {//win, linux popup
				popupMenu(getReactionCartoon().getSelectedShape(), event.getX(), event.getY());
				return;
			}
			if(mode == Mode.SELECT && bStartRxContainerLabel){
				resetDropTargets(null,mode == Mode.STRUCTURE);
				if(endShape != null && endShape instanceof ReactionContainerShape){
					Rectangle labelOutlineRectangle = ((ReactionContainerShape)endShape).getLabelOutline(endShape.getAbsX(), endShape.getAbsY());
					boolean bLabel = labelOutlineRectangle.contains(startPointWorld.x, startPointWorld.y);
					getGraphPane().setCursor((bLabel?Cursor.getPredefinedCursor(Cursor.HAND_CURSOR):Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)));
				}
				bStartRxContainerLabel = false;
				RXContainerDropTargetInfo trueRXContainerDropTargetInfo = getSelectedContainerDropTargetInfo();
				lastRXContainerDropTargetInfoMap = null;
				if(trueRXContainerDropTargetInfo == null){
					getGraphPane().repaint();//turn off rxDropTargetRectangles
					return;
				}
				StructureSuite structureSuite = null;
				structureSuite = getReactionCartoon().getStructureSuite();
				if(structureSuite != null){
					Structure[] originalOrderedStructArr = structureSuite.getStructures().toArray(new Structure[0]);
					//find where user wants to put the structure
					try{
						if(trueRXContainerDropTargetInfo != null){
							ArrayList<Structure> newStructOrderList = new ArrayList<Structure>(Arrays.asList(originalOrderedStructArr));
							newStructOrderList.remove(((ReactionContainerShape)startShape).getStructure());
							int indexEnd = newStructOrderList.indexOf(((ReactionContainerShape)trueRXContainerDropTargetInfo.dropShape).getStructure());
							int indexClosestNeighbor =
								(trueRXContainerDropTargetInfo.closestNeighborShape==null?(trueRXContainerDropTargetInfo.insertFlag==RXContainerDropTargetInfo.INSERT_BEGINNING?0:newStructOrderList.size()):newStructOrderList.indexOf(((ReactionContainerShape)trueRXContainerDropTargetInfo.closestNeighborShape).getStructure()));
							if(indexClosestNeighbor < indexEnd){
								newStructOrderList.add(indexEnd, ((ReactionContainerShape)startShape).getStructure());
							}else{
								newStructOrderList.add(indexClosestNeighbor, ((ReactionContainerShape)startShape).getStructure());
							}
							if(structureSuite instanceof AllStructureSuite){
								((AllStructureSuite)structureSuite).setModelStructureOrder(true);
							}
							ArrayList<Diagram> newDiagramOrderList = new ArrayList<Diagram>();
							for(Structure structure:newStructOrderList){
								newDiagramOrderList.add(getModel().getDiagram(structure));
							}
							getModel().setDiagrams(newDiagramOrderList.toArray(new Diagram[0]));

						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				return;
			}

			// else do select and move
			switch (mode) {
			case SELECT: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bMoving){
					getGraphPane().invalidate();
					((JViewport) getGraphPane().getParent()).revalidate();
					getGraphPane().repaint();
					saveDiagram();
				} else if (bRectStretch) {
					Point absLoc = rectShape.getSpaceManager().getRelPos();
					Dimension size = rectShape.getSpaceManager().getSize();
					// remove temporary rectangle
					getReactionCartoon().removeShape(rectShape);
					rectShape = null;
					Rectangle rect = new Rectangle(absLoc.x, absLoc.y, size.width, size.height);
					boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
					boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
					selectEventFromWorld(rect, bShift, bCntrl);
					getGraphPane().repaint();
				}
				bMoving = false;
				movingShape = null;
				bRectStretch = false;
				rectShape = null;
				break;
			}
			case LINEDIRECTED: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bLineStretch 
						&& getDragDistanceSquared() >= MIN_DRAG_DISTANCE_TO_CREATE_NEW_ELEMENTS_SQUARED) {
					bLineStretch = false;
					// set label and color for line depending on which shape the edge started. 
					// (rather than attachment area on ReactionStepShape)
					LineType lineType = getLineTypeFromDirection(startShape, endPointWorld);
					if (endShape instanceof SimpleReactionShape){
						SimpleReaction simpleReaction = (SimpleReaction)endShape.getModelObject();
						Object startShapeObject = null;						
						if(startShape != null) {
							startShapeObject = startShape.getModelObject();
						}
						if(startShapeObject instanceof SpeciesContext) {
							SpeciesContext speciesContext = (SpeciesContext) startShapeObject;
							lineAction(speciesContext, simpleReaction);
						} else if(startShapeObject instanceof Structure) {
							Structure structure = (Structure) startShapeObject;
							lineActon(structure, simpleReaction);
						}
					} else if (endShape instanceof SpeciesContextShape) {
						SpeciesContext speciesContextEnd = (SpeciesContext) endShape.getModelObject();
						Object startShapeObject = null;
						if(startShape != null) {
							startShapeObject = startShape.getModelObject();							
						}
						if(startShapeObject instanceof SimpleReaction) {
							SimpleReaction simpleReaction = (SimpleReaction) startShapeObject;
							lineAction(simpleReaction, speciesContextEnd);
						} else if (startShapeObject instanceof FluxReaction) {
							FluxReaction fluxReaction = (FluxReaction) startShapeObject;
							lineAction(fluxReaction, speciesContextEnd);
						} else if (startShapeObject instanceof SpeciesContext) {
							SpeciesContext speciesContextStart = (SpeciesContext) startShapeObject;
							if(!speciesContextStart.equals(speciesContextEnd)) {
								lineAction(speciesContextStart, speciesContextEnd);
									}
						} else if(startShapeObject instanceof Structure) {
							Structure startStructure = (Structure) startShapeObject;
							lineAction(startStructure, speciesContextEnd);
									}
					} else if (endShape instanceof FluxReactionShape){
						FluxReaction fluxReaction = (FluxReaction)endShape.getModelObject();
						fluxReaction.setModel(getModel());
						Object startShapeObject = null;						
						if(startShape != null) {
							startShapeObject = startShape.getModelObject();
								}
						if(startShapeObject instanceof SpeciesContext) {
							SpeciesContext speciesContext = (SpeciesContext) startShapeObject;
							lineAction(speciesContext, fluxReaction);
						} else if(startShapeObject instanceof Structure) {
							Structure structure = (Structure) startShapeObject;
							lineActon(structure, fluxReaction);
									}
						// remove temporary edge
						getReactionCartoon().removeShape(edgeShape);
						edgeShape = null;
					} else if(endShape instanceof ReactionContainerShape) {
						Structure endStructure = (Structure) endShape.getModelObject();
						Object startObject = null;
						if(startShape != null) {
							startObject = startShape.getModelObject();
									}
						if(startObject instanceof SimpleReaction) {
							SimpleReaction reaction = (SimpleReaction) startObject;
							lineAction(reaction, endStructure);
						} else if(startObject instanceof FluxReaction) {
							FluxReaction reaction = (FluxReaction) startObject;
							lineAction(reaction, endStructure);
						} else if(startObject instanceof SpeciesContext) {
							SpeciesContext speciesContextStart = (SpeciesContext) startObject;
							lineAction(speciesContextStart, endStructure);
						} else if(startObject instanceof Structure) {
							Structure startStructure = (Structure) startObject;
							lineAction(startStructure, endStructure, endShape);
						}
					}
				}
				saveDiagram();
				break;
			}
			case LINECATALYST: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bLineStretch
						&& getDragDistanceSquared() >= MIN_DRAG_DISTANCE_TO_CREATE_NEW_ELEMENTS_SQUARED){
					bLineStretch = false;
					// set label and color for line depending on which shape the edge started. 
					// (rather than attachment area on ReactionStepShape)
					Object startObject = startShape.getModelObject();
					Object endObject = endShape.getModelObject();
					ReactionStep reactionStep = null;
					SpeciesContext speciesContext = null;
					if(startObject instanceof ReactionStep) {
						reactionStep = (ReactionStep) startObject;
						if(endObject instanceof SpeciesContext) {
							if(StructureUtil.reactionHereCanHaveParticipantThere(reactionStep.getStructure(), ((SpeciesContext) endObject).getStructure())) {
								speciesContext = (SpeciesContext) endObject;															
							}
						} else if(endObject instanceof Structure) {
							Structure endStructure = (Structure) endObject;
							if(StructureUtil.reactionHereCanHaveParticipantThere(reactionStep.getStructure(), endStructure)) {
								speciesContext = getReactionCartoon().getModel().createSpeciesContext(endStructure);
								getReactionCartoon().notifyChangeEvent();
								Point endPos = edgeShape.getEnd();
								positionShapeForObject(endStructure, speciesContext, endPos);							
							}
						}
					} else if(startObject instanceof SpeciesContext) {
						speciesContext = (SpeciesContext) startObject;						
						if(endObject instanceof ReactionStep) {
							if(StructureUtil.reactionHereCanHaveParticipantThere(((ReactionStep) endObject).getStructure(),	speciesContext.getStructure())) {
								reactionStep = (ReactionStep) endObject;								
							}
						} else if(endObject instanceof Structure) {
							Structure endStructure = (Structure) endObject;
							if(StructureUtil.reactionHereCanHaveParticipantThere(endStructure, speciesContext.getStructure())) {
								reactionStep = getReactionCartoon().getModel().createSimpleReaction(endStructure);
								getReactionCartoon().notifyChangeEvent();
								Point endPos = edgeShape.getEnd();
								positionShapeForObject(endStructure, reactionStep, endPos);							
							}
						}
					} else if (startObject instanceof Structure) {
						Structure startStructure = (Structure) startObject;
						if(endObject instanceof ReactionStep) {
							reactionStep = (ReactionStep) endObject;
							if(StructureUtil.reactionHereCanHaveParticipantThere(reactionStep.getStructure(), startStructure)) {
								speciesContext = getReactionCartoon().getModel().createSpeciesContext(startStructure);
								getReactionCartoon().notifyChangeEvent();
								positionShapeForObject(startStructure, speciesContext, startPointWorld);							
							}
						} else if(endObject instanceof SpeciesContext) {
							speciesContext = (SpeciesContext) endObject;
							if(StructureUtil.reactionHereCanHaveParticipantThere(startStructure, speciesContext.getStructure())) {
								reactionStep = getReactionCartoon().getModel().createSimpleReaction(startStructure);
								getReactionCartoon().notifyChangeEvent();
								positionShapeForObject(startStructure, reactionStep, startPointWorld);
							}
						} else if(endObject instanceof Structure) {
							Structure endStructure = (Structure) endObject;
							if(StructureUtil.reactionHereCanHaveParticipantThere(startStructure, endStructure)) {
								speciesContext = 
									getReactionCartoon().getModel().createSpeciesContext(endStructure);
								reactionStep = 
									getReactionCartoon().getModel().createSimpleReaction(startStructure);
								getReactionCartoon().notifyChangeEvent();
								Point endPos = edgeShape.getEnd();
								positionShapeForObject(endStructure, speciesContext, endPos);
								positionShapeForObject(startStructure, reactionStep, startPointWorld);
							} else if(StructureUtil.reactionHereCanHaveParticipantThere(endStructure, startStructure)) {
								speciesContext = getReactionCartoon().getModel().createSpeciesContext(startStructure);
								reactionStep = getReactionCartoon().getModel().createSimpleReaction(endStructure);
								getReactionCartoon().notifyChangeEvent();
								positionShapeForObject(startStructure, speciesContext, startPointWorld);
								Point endPos = edgeShape.getEnd();
								positionShapeForObject(endStructure, reactionStep, endPos);								
							}
						}	
					}
					if (reactionStep != null && speciesContext != null) {
						Catalyst catalyst = null;
						for(ReactionParticipant participant : reactionStep.getReactionParticipants()) {
							if(participant instanceof Catalyst && participant.getSpeciesContext().equals(speciesContext)) {
								catalyst = (Catalyst) participant;
							}
						}
						if(catalyst == null) {
							reactionStep.addCatalyst(speciesContext);
							getReactionCartoon().notifyChangeEvent();
						}
						// add reactionParticipant to model
					} else{
						getGraphPane().repaint();
					}
				}
				saveDiagram();
				break;
			}
			default: {
				break;
			}
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mouseReleased: uncaught exception");
			e.printStackTrace(System.out);
		}
		resetMouseActionHistory();
		getGraphPane().repaint();
	}

	private void lineAction(Structure startStructure, Structure endStructure, Shape endShape) throws Exception, PropertyVetoException, ExpressionException {
		Point startPos = edgeShape.getStart();
		Point endPos = edgeShape.getEnd();
		if(endStructure.equals(startStructure)) {
			SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(endStructure);
			SpeciesContext speciesContext2 = getReactionCartoon().getModel().createSpeciesContext(endStructure);
			SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(endStructure);
			reaction.addReactant(speciesContext1, 1);
			reaction.addProduct(speciesContext2, 1);
			getReactionCartoon().notifyChangeEvent();
			positionShapeForObject(endStructure, speciesContext1, startPos);
			positionShapeForObject(endStructure, speciesContext2, endPos);
			positionShapeForObject(endStructure, reaction, new Point((startPos.x + endPos.x)/2, (startPos.y + endPos.y)/2));
			getGraphModel().clearSelection();
			getGraphModel().select(reaction);
		}
		else
		{
			if(endStructure instanceof Membrane && startStructure instanceof Feature)
			{
				Membrane endMembrane = (Membrane)endStructure;
				Feature startFeature = (Feature)startStructure;
//				if(structTopology.getOutsideFeature(endMembrane).equals(startFeature) || structTopology.getInsideFeature(endMembrane).equals(startFeature))
//				{
				SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(startFeature);
				SpeciesContext speciesContext2 = getReactionCartoon().getModel().createSpeciesContext(endMembrane);
				SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(endMembrane);
				reaction.addReactant(speciesContext1, 1);
				reaction.addProduct(speciesContext2, 1);
				getReactionCartoon().notifyChangeEvent();
				positionShapeForObject(startFeature, speciesContext1, startPos);
				positionShapeForObject(endMembrane, speciesContext2, endPos);
				//finding correct insertion point for reaction, statements below should be put into a utility if used often
				int memAbsXmin = endShape.getSpaceManager().getAbsLoc().x;
				int memAbsXmax = memAbsXmin + endShape.getSpaceManager().getSize().width;
				int reactionWidth = new SimpleReactionShape(reaction, getReactionCartoon()).getSpaceManager().getSize().width;
				int reactionAbsX = (startPos.x + endPos.x)/2;
				if((memAbsXmax - memAbsXmin)<=reactionWidth)
				{
					reactionAbsX = memAbsXmin;
				}
				else
				{
					reactionAbsX = Math.max(reactionAbsX, memAbsXmin);
					reactionAbsX = Math.min(reactionAbsX, (memAbsXmax-reactionWidth));
				}
				
				positionShapeForObject(endMembrane, reaction, new Point(reactionAbsX, (startPos.y + endPos.y)/2));
				getGraphModel().clearSelection();
				getGraphModel().select(reaction);
//				}
			}
			else if(endStructure instanceof Feature && startStructure instanceof Membrane)
			{
				Membrane startMembrane = (Membrane)startStructure;
				Feature endFeature = (Feature)endStructure;
//				if(structTopology.getOutsideFeature(startMembrane).equals(endFeature) || structTopology.getInsideFeature(startMembrane).equals(endFeature))
//				{
				SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(startMembrane);
				SpeciesContext speciesContext2 = getReactionCartoon().getModel().createSpeciesContext(endFeature);
				SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(startMembrane);
				reaction.addReactant(speciesContext1, 1);
				reaction.addProduct(speciesContext2, 1);
				getReactionCartoon().notifyChangeEvent();
				positionShapeForObject(startMembrane, speciesContext1, startPos);
				positionShapeForObject(endFeature, speciesContext2, endPos);
				//finding correct insertion point for reaction, statements below should be put into a utility if used often
				int memAbsXmin = startShape.getSpaceManager().getAbsLoc().x;
				int memAbsXmax = memAbsXmin + startShape.getSpaceManager().getSize().width;
				int reactionWidth = new SimpleReactionShape(reaction, getReactionCartoon()).getSpaceManager().getSize().width;
				int reactionAbsX = (startPos.x + endPos.x)/2;
				if((memAbsXmax - memAbsXmin)<=reactionWidth)
				{
					reactionAbsX = memAbsXmin;
				}
				else
				{
					reactionAbsX = Math.max(reactionAbsX, memAbsXmin);
					reactionAbsX = Math.min(reactionAbsX, (memAbsXmax-reactionWidth));
				}
				
				positionShapeForObject(startMembrane, reaction, new Point(reactionAbsX, (startPos.y + endPos.y)/2));
				getGraphModel().clearSelection();
				getGraphModel().select(reaction);
//				}
			}
			else if(endStructure instanceof Feature && startStructure instanceof Feature)
			{
				Feature startFeature = (Feature)startStructure;
				Feature endFeature = (Feature)endStructure;
				SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(startStructure);
				SpeciesContext speciesContext2 = getReactionCartoon().getModel().createSpeciesContext(endStructure);
				SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(startStructure);
				reaction.addReactant(speciesContext1, 1);
				reaction.addProduct(speciesContext2, 1);
				reaction.setKinetics(new GeneralLumpedKinetics(reaction));
				getReactionCartoon().notifyChangeEvent();
				positionShapeForObject(startStructure, speciesContext1, startPos);
				positionShapeForObject(endStructure, speciesContext2, endPos);
				positionShapeForObject(startStructure, reaction, new Point((startPos.x + endPos.x)/2, (startPos.y + endPos.y)/2));
				getGraphModel().clearSelection();
				getGraphModel().select(reaction);
			}
		    else if(endStructure instanceof Membrane && startStructure instanceof Membrane) 
		    { 
				SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(startStructure);
				SpeciesContext speciesContext2 = getReactionCartoon().getModel().createSpeciesContext(endStructure);
				SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(startStructure);
				reaction.addReactant(speciesContext1, 1);
				reaction.addProduct(speciesContext2, 1);
				reaction.setKinetics(new GeneralLumpedKinetics(reaction));
				getReactionCartoon().notifyChangeEvent();
				positionShapeForObject(startStructure, speciesContext1, startPos);
				positionShapeForObject(endStructure, speciesContext2, endPos);
				positionShapeForObject(startStructure, reaction, new Point((startPos.x + endPos.x)/2, (startPos.y + endPos.y)/2));
				getGraphModel().clearSelection();
				getGraphModel().select(reaction);
			}
		}
	}

	private void lineAction(SpeciesContext speciesContextStart,	Structure endStructure) throws PropertyVetoException, Exception {
		Point startPos = edgeShape.getStart();
		Point endPos = edgeShape.getEnd();
		Model model = getModel();
		StructureTopology structTopology = model.getStructureTopology();
		Structure startStructure = speciesContextStart.getStructure();
		ReactionStep reaction = null;
		Structure reactionStructure = null;
		if (endStructure != startStructure) 
		{
			if(startStructure instanceof Feature && endStructure instanceof Feature)
			{
				// FeatureStart-speciesContext ==> FeatureEnd with NO membrane in between : create lumped reaction in FeatureStart and pdt in FeatureEnd 
				reactionStructure = startStructure;
				reaction = model.createSimpleReaction(reactionStructure);
				reaction.addReactant(speciesContextStart, 1);
				SpeciesContext endSpeciesContext = model.createSpeciesContext(endStructure);
				reaction.addProduct(endSpeciesContext,1);
				reaction.setKinetics(new GeneralLumpedKinetics(reaction));
				positionShapeForObject(endStructure, endSpeciesContext, endPos);
			}
			else if (startStructure instanceof Feature && endStructure instanceof Membrane)
			{
				// Feature-speciesContext ==> Membrane : create lumped reaction in membrane, pdt in membrane
				reactionStructure = endStructure;
				reaction = model.createSimpleReaction(reactionStructure);
				reaction.addReactant(speciesContextStart, 1);
				SpeciesContext endSpeciesContext = model.createSpeciesContext(endStructure);
				reaction.addProduct(endSpeciesContext,1);
				reaction.setKinetics(new GeneralLumpedKinetics(reaction));
				positionShapeForObject(endStructure, endSpeciesContext, endPos);
			}
			else if (startStructure instanceof Membrane && endStructure instanceof Feature)
			{
				// Membrane-speciesContext ==> Feature : create reaction in Membrane, pdt in Feature
				reactionStructure = startStructure;
				reaction = model.createSimpleReaction(reactionStructure);
				reaction.addReactant(speciesContextStart, 1);
				SpeciesContext endSpeciesContext = model.createSpeciesContext(endStructure);
				reaction.addProduct(endSpeciesContext,1);
				positionShapeForObject(endStructure, endSpeciesContext, endPos);
			} 
			else if (startStructure instanceof Membrane && endStructure instanceof Membrane) {
				// MembraneStart-speciescontext ==> MembraneEnd : create lumped reaction in MembraneStart, pdt in MembraneEnd.
				reactionStructure = startStructure;
				reaction = model.createSimpleReaction(reactionStructure);
				reaction.addReactant(speciesContextStart, 1);
				SpeciesContext endSpeciesContext = model.createSpeciesContext(endStructure);
				reaction.addProduct(endSpeciesContext,1);
				reaction.setKinetics(new GeneralLumpedKinetics(reaction));
				positionShapeForObject(endStructure, endSpeciesContext, endPos);
			}
		} else {
			// startStructure and endStructure are the same 
			// Feature1 ==> Feature1 OR Membrane1 ==> Membrane1
			reaction = model.createSimpleReaction(startStructure);
			reaction.addReactant(speciesContextStart, 1);
		}

		positionShapeForObject(reactionStructure, reaction, new Point(((8*startPos.x+2*endPos.x)/10),(8*startPos.y+2*endPos.y)/10));
		getReactionCartoon().notifyChangeEvent();
		getGraphModel().clearSelection();
		getGraphModel().select(reaction);
	}

	private void lineAction(ReactionStep reactionStart, Structure endStructure) throws Exception {
		Structure startStructure = reactionStart.getStructure();
		if(StructureUtil.reactionHereCanHaveParticipantThere(startStructure, endStructure)) {
			// if reactionStart is a SimpleRxn OR FluxRxn without a product, add speciesContext as pdt
			if ( (reactionStart instanceof SimpleReaction) || ((reactionStart instanceof FluxReaction) && !reactionStart.hasProduct()) ) {
				SpeciesContext speciesContext = getModel().createSpeciesContext(endStructure);
				reactionStart.addProduct(speciesContext, 1);
				positionShapeForObject(endStructure, speciesContext,  edgeShape.getEnd());
			}
			if ( ( (startStructure instanceof Feature && endStructure instanceof Feature) || (startStructure instanceof Membrane && endStructure instanceof Membrane) ) && startStructure != endStructure) {
				// ============ change kinetics to lumped or warn user ?????????
				// reactionStart.setKinetics(new GeneralLumpedKinetics(reactionStart));
			}

			getReactionCartoon().notifyChangeEvent();
		}
	}

	private void lineAction(Structure startStructure, SpeciesContext speciesContextEnd) throws PropertyVetoException, Exception {
		Structure endStructure = speciesContextEnd.getStructure();
		Model model = getModel();
		ReactionStep  reaction = null;
		Point startPos = edgeShape.getStart();
		Point endPos = edgeShape.getEnd();
		Structure reactionStructure = null;
		if(startStructure != endStructure)
		{
			if (startStructure instanceof Feature && endStructure instanceof Feature) 
			{
				// Feature ==> Feature-speciesContext 
				Feature startFeature = (Feature)startStructure;
				Feature endFeature = (Feature)endStructure;
				// Feature ==> feature-speciesContext with no membrane between : create a 0th-order simpleReaction in startFeature with GeneralLumpedKinetics
				reactionStructure = startStructure;
				reaction = model.createSimpleReaction(reactionStructure);
				reaction.setKinetics(new GeneralLumpedKinetics(reaction));
			}
			else if (startStructure instanceof Feature && endStructure instanceof Membrane) 
			{
				// Feature ==> membrane-species : Create volume species ; create membrane reaction ; add volume species as reactant and membrane species(End) as product.
				reactionStructure = endStructure;
				reaction = model.createSimpleReaction(reactionStructure);
				SpeciesContext startingSpeciesContext = model.createSpeciesContext(startStructure);
				reaction.addReactant(startingSpeciesContext, 1);
				positionShapeForObject(startStructure, startingSpeciesContext, startPos); 
			} 
			else if (startStructure instanceof Membrane && endStructure instanceof Feature)
			{
				// Membrane ==> Feature-species : 0th-order reaction in membrane
				reactionStructure = startStructure;
				reaction = model.createSimpleReaction(reactionStructure);
			} else if (startStructure instanceof Membrane && endStructure instanceof Membrane)
			{
				// Membrane ==> membrane-species : the 2 membranes are different : create a 0th-order lumped simpleReaction in startMembrane
				reactionStructure = startStructure;
				reaction = model.createSimpleReaction(reactionStructure);
				reaction.setKinetics(new GeneralLumpedKinetics(reaction));
			}
		} else {
			// startStructure == endStructure : 0th-order simplereaction in structure
			reactionStructure = startStructure;
			reaction = model.createSimpleReaction(reactionStructure);
		}
		
		// speciesContextEnd should be added as a product to reaction (if not flux).
		if (!(reaction instanceof FluxReaction)) {
			reaction.addProduct(speciesContextEnd, 1);
		}
		positionShapeForObject(reactionStructure, reaction, new Point((2*startPos.x+8*endPos.x)/10, (2*startPos.y+8*endPos.y)/10));
		getReactionCartoon().notifyChangeEvent();
		getGraphModel().clearSelection();
		getGraphModel().select(reaction);
	}

	private void lineAction(SpeciesContext speciesContextStart, SpeciesContext speciesContextEnd) throws Exception {
		Structure endStructure = speciesContextEnd.getStructure();
		Structure startStructure = speciesContextStart.getStructure();
		Model model = getModel();
		ReactionStep reaction = null;
		Point startPos = edgeShape.getStart();
		Point endPos = edgeShape.getEnd();
		Structure reactionStructure = null;
		boolean bLumpedKinetics = false;
		if(startStructure != endStructure)
		{
			if (startStructure instanceof Feature && endStructure instanceof Feature) 
			{
				// Feature-speciesContext ==> Feature-speciesContext 
				Membrane membraneBetween = model.getStructureTopology().getMembrane((Feature)startStructure, (Feature)endStructure);
				// Feature-speciesContext ==> Feature-speciesContext with membrane in between : add reaction in Membrane (scStart : reactant; scEnd : pdt)
				if(membraneBetween != null)
				{
					reactionStructure = membraneBetween;
				} else {
					// Feature-speciesContext ==> Feature-speciesContext with no membrane between : create a lumped reaction in startFeature
					reactionStructure = startStructure;
					bLumpedKinetics = true;
				}
			}
			else if (startStructure instanceof Feature && endStructure instanceof Membrane) 
			{
				// Feature-speciesContext ==> Membrane-speciesContext : create membrane reaction ; add scStart : reactant and scEnd : pdt.
				reactionStructure = endStructure;
			} 
			else if (startStructure instanceof Membrane && endStructure instanceof Feature)
			{
				// Membrane-speciesContext ==> Feature-speciesContext : create reaction in membrane; scStart : reactant, scEnd : pdt.
				reactionStructure = startStructure;
			} 
			else if (startStructure instanceof Membrane && endStructure instanceof Membrane)
			{
				// Membrane-speciesContext ==> Membrane-speciesContext : the 2 membranes are different : create lumped reaction in endMembrane
				reactionStructure = endStructure;
				bLumpedKinetics = true;
			}
		} else {
			// startStructure == endStructure : create reaction in structure
			reactionStructure = startStructure;
		}
		
		
		reaction = model.createSimpleReaction(reactionStructure);
		if (bLumpedKinetics) {
			reaction.setKinetics(new GeneralLumpedKinetics(reaction));
		}
		reaction.addReactant(speciesContextStart, 1);
		reaction.addProduct(speciesContextEnd, 1);
		positionShapeForObject(reactionStructure, reaction, new Point((startPos.x + endPos.x)/2, (startPos.y + endPos.y)/2));
		getReactionCartoon().notifyChangeEvent();
		getGraphModel().clearSelection();
		getGraphModel().select(reaction);
	}

	private void lineAction(ReactionStep reactionStart, SpeciesContext speciesContextEnd) throws Exception {
		Structure startStructure = reactionStart.getStructure();
		Structure endStructure = speciesContextEnd.getStructure();
		if(StructureUtil.reactionHereCanHaveParticipantThere(startStructure, endStructure)) {
			int stoichiometry = 1;
			Product product = null;
			for(ReactionParticipant participant : reactionStart.getReactionParticipants()) {
				if(participant instanceof Product && participant.getSpeciesContext().equals(speciesContextEnd)) {
					product = (Product) participant;
					Shape shape = getReactionCartoon().getShapeFromModelObject(product);
					if(shape != null) {
						shape.refreshLabel();
					}
				}
			}
			if(product != null) {
				// only increase stoichiometry if reaction is SimpleReaction
				if (reactionStart instanceof SimpleReaction) {
					product.setStoichiometry(product.getStoichiometry() + 1);
				}
			} else {
				// add speciesContextEnd as pdt to reactionStart only if reactionStart is a SimpleRxn or if it is a FluxRxn and doesn't have a pdt.
				if (reactionStart instanceof SimpleReaction || ( (reactionStart instanceof FluxReaction) && !(reactionStart.hasProduct())) ) {
					reactionStart.addProduct(speciesContextEnd, stoichiometry);
				}
			}
			if ( (startStructure != endStructure) && 
				 ((startStructure instanceof Feature && endStructure instanceof Feature) || 
				  (startStructure instanceof Membrane && endStructure instanceof Membrane) || 
				  (startStructure instanceof Feature && endStructure instanceof Membrane) ) ) {
				// ============ change kinetics to lumped or warn user ?????????
				// simpleReactionStart.setKinetics(new GeneralLumpedKinetics(simpleReactionStart));
			}
			getReactionCartoon().notifyChangeEvent();
		}
	}

	private void lineActon(Structure startStructure, ReactionStep reactionEnd) throws Exception {
		Structure endStructure = reactionEnd.getStructure();
		if(StructureUtil.reactionHereCanHaveParticipantThere(endStructure, startStructure)) {
			// if reactionStart is a SimpleRxn OR FluxRxn without a product, add speciesContext as reactant
			if ( (reactionEnd instanceof SimpleReaction) || ((reactionEnd instanceof FluxReaction) && !reactionEnd.hasReactant()) ) { 
				SpeciesContext speciesContext =  getReactionCartoon().getModel().createSpeciesContext(startStructure);
				reactionEnd.addReactant(speciesContext, 1);
				positionShapeForObject(startStructure, speciesContext, edgeShape.getStart());
								}
			if ( ( (startStructure instanceof Feature && endStructure instanceof Feature) || (startStructure instanceof Membrane && endStructure instanceof Membrane) ) && startStructure != endStructure) {
				// ============ change kinetics to lumped or warn user ?????????
				// simpleReaction.setKinetics(new GeneralLumpedKinetics(simpleReaction));
			}
			getReactionCartoon().notifyChangeEvent();
		}
	}

	private void lineAction(SpeciesContext speciesContextStart, ReactionStep reactionEnd) throws Exception {
		Structure endStructure = reactionEnd.getStructure();
		Structure startStructure = speciesContextStart.getStructure();
		if(StructureUtil.reactionHereCanHaveParticipantThere(endStructure, startStructure)) {
			int stoichiometry = 1;
			Reactant reactant = null;
			for(ReactionParticipant participant : reactionEnd.getReactionParticipants()) {
				if(participant instanceof Reactant && participant.getSpeciesContext().equals(speciesContextStart)) {
					reactant = (Reactant) participant;
				}
			}
			if(reactant != null) {
				// only increase stoichiometry if reaction is SimpleReaction
				if (reactionEnd instanceof SimpleReaction) {
					reactant.setStoichiometry(reactant.getStoichiometry() + 1);
				}
				Shape shape = getReactionCartoon().getShapeFromModelObject(reactant);
				if(shape != null) {
					shape.refreshLabel();
				}
			} else {
				// add speciesContextEnd as pdt to reactionStart only if reactionStart is a SimpleRxn or if it is a FluxRxn and doesn't have a pdt.
				if (reactionEnd instanceof SimpleReaction || ( (reactionEnd instanceof FluxReaction) && !(reactionEnd.hasReactant())) ) {
					reactionEnd.addReactant(speciesContextStart, stoichiometry);
				}
			}
			if ( ( (startStructure instanceof Feature && endStructure instanceof Feature) || (startStructure instanceof Membrane && endStructure instanceof Membrane) ) && startStructure != endStructure) {
				// ============ change kinetics to lumped or warn user ?????????
				//simpleReactionEnd.setKinetics(new GeneralLumpedKinetics(simpleReactionEnd));
			}
			getReactionCartoon().notifyChangeEvent();
		}
	}

	public Model getModel() {
		return getReactionCartoon().getModel();
	}
	
	protected void positionShapeForObject(Structure structure, Object object, Point pos) {
		Shape structureShape = getReactionCartoon().getShapeFromModelObject(structure);
		Shape shape = getReactionCartoon().getShapeFromModelObject(object);
		if(shape != null) {
			if(structureShape != null) {
				Rectangle boundary = 
					getReactionCartoon().getContainerLayout().getBoundaryForAutomaticLayout(structureShape);
				shape.getSpaceManager().setAbsLoc(pos);
				int xMax = boundary.x + boundary.width - shape.getWidth();
				if(shape.getAbsX() < boundary.x) { 
					shape.setAbsX(boundary.x); 
				} else if(shape.getAbsX() > xMax) {
					shape.setAbsX(xMax);
				}
				int yMax = boundary.y + boundary.height - shape.getHeight();
				if(shape.getAbsY() < boundary.y) { 
					shape.setAbsY(boundary.y); 
				} else if(shape.getAbsY() > yMax) {
					shape.setAbsY(yMax);
				}
			} else {
				shape.getSpaceManager().setAbsLoc(pos);		
			}
		}
	}
	
	public void saveDiagram() {
		for(Structure structure : getReactionCartoon().getStructureSuite().getStructures()) {
			getReactionCartoon().setPositionsFromReactionCartoon(
					getModel().getDiagram(structure));			
		}
	}

	private void selectEventFromWorld(Point worldPoint, boolean bShift) {
		if (getReactionCartoon() == null) {
			return;
		}
		if (!bShift) {
			Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);
			if (pickedShape == null || !pickedShape.isSelected()) {
				getReactionCartoon().clearSelection();
			}
			if (pickedShape != null && pickedShape.isSelected()) {
				return;
			}
			if (pickedShape != null) {
				getReactionCartoon().selectShape(pickedShape);
			}

		} else{
			Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);
			if (pickedShape == null) {
				return;
			}
			if (pickedShape instanceof ReactionContainerShape) {
				return;
			}
			if (getReactionCartoon().getSelectedShape() instanceof ReactionContainerShape) {
				getReactionCartoon().clearSelection();
			}
			if (pickedShape.isSelected()) {
				getReactionCartoon().deselectShape(pickedShape);
			} else {
				getReactionCartoon().selectShape(pickedShape);
			}
		}
//		else if (bCntrl) {
//			Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);
//			if (pickedShape == null) {
//				return;
//			}
//			if (pickedShape instanceof ReactionContainerShape) {
//				return;
//			}
//			if (pickedShape.isSelected()) {
//				getReactionCartoon().deselectShape(pickedShape);
//			} else {
//				getReactionCartoon().selectShape(pickedShape);
//			}
//		}
	}

	private void selectEventFromWorld(Rectangle rect, boolean bShift,
			boolean bCntrl) {
		if (!bShift && !bCntrl) {
			getReactionCartoon().clearSelection();
			List<Shape> shapes = getReactionCartoon().pickWorld(rect);
			for (Shape shape : shapes) {
				if (ShapeUtil.isMovable(shape)) {
					getReactionCartoon().selectShape(shape);
				}
			}
		} else if (bShift) {
			if (getReactionCartoon().getSelectedShape() instanceof ReactionContainerShape) {
				getReactionCartoon().clearSelection();
			}
			List<Shape> shapes = getReactionCartoon().pickWorld(rect);
			for (Shape shape : shapes) {
				if (ShapeUtil.isMovable(shape)) {
					getReactionCartoon().selectShape(shape);
				}
			}
		} else if (bCntrl) {
			if (getReactionCartoon().getSelectedShape() instanceof ReactionContainerShape) {
				getReactionCartoon().clearSelection();
			}
			List<Shape> shapes = getReactionCartoon().pickWorld(rect);
			for (Shape shape : shapes) {
				if (ShapeUtil.isMovable(shape)) {
					if (shape.isSelected()) {
						getReactionCartoon().deselectShape(shape);
					} else {
						getReactionCartoon().selectShape(shape);
					}
				}
			}
		}
	}

	public void setReactionCartoon(ReactionCartoon newReactionCartoon) {
		reactionCartoon = newReactionCartoon;
	}

	@Override
	public boolean shapeHasMenuAction(Shape shape, String menuAction) {
		if (menuAction.equals(CartoonToolMiscActions.Annotate.MENU_ACTION)){
			if (shape instanceof ReactionStepShape){
				return true;
			}
		}
		if (menuAction.equals(CartoonToolEditActions.Copy.MENU_ACTION)) {
			if (shape instanceof SpeciesContextShape
					|| shape instanceof ReactionStepShape) {
				return true;
			}
		}
		if (/*menuAction.equals(CartoonToolEditActions.Paste.MENU_ACTION)
				|| */menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape) {
				return true;
			}
		}
		if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape || shape instanceof ReactionStepShape
					|| shape instanceof ReactantShape
					|| shape instanceof ProductShape
					|| shape instanceof CatalystShape
					|| shape instanceof ReactionRuleDiagramShape
					|| shape instanceof RuleParticipantSignatureDiagramShape) {
				return true;
			}
		}
		if (menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)) {
			if (shape instanceof SpeciesContextShape
					|| shape instanceof ReactionStepShape) {
				return true;
			}
		}
		if (menuAction.equals(CartoonToolMiscActions.AddSpecies.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape) {
				return true;
			}
		}
		if (menuAction.equals(CartoonToolMiscActions.SearchReactions.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape) {
				return true;
			}
		}

		if (menuAction.equals(CartoonToolSaveAsImageActions.MenuAction.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape) {
				return true;
			}
		}
		if (menuAction.equals(CartoonToolMiscActions.Properties.MENU_ACTION)) {
			if (shape instanceof ReactionStepShape
					|| shape instanceof SpeciesContextShape
					|| shape instanceof ReactantShape
					|| shape instanceof ProductShape
					|| shape instanceof CatalystShape
					|| shape instanceof ReactionContainerShape) {
				return true;
			}
		}
		GraphViewAction paintingAction = ActionUtil.getAction(paintingActions, menuAction);
		if(paintingAction != null) {
			return paintingAction.canBeAppliedToShape(shape);
		}
		GraphViewAction groupAction = ActionUtil.getAction(groupActions, menuAction);
		if(groupAction != null) {
			return groupAction.canBeAppliedToShape(shape);
		}
		return false;
	}

	@Override
	public boolean shapeHasMenuActionEnabled(Shape shape, String menuAction) {

		if (menuAction.equals(CartoonToolMiscActions.Properties.MENU_ACTION)) {
			if (shape instanceof CatalystShape) {
				return false;
			}
		}
		if (menuAction.equals(CartoonToolMiscActions.SearchReactions.MENU_ACTION)) {
			if (!(shape instanceof ReactionContainerShape)
					|| !getReactionCartoon().getStructureSuite().getStructures().contains(
							shape.getModelObject())) {
				return false;
			}
		}
		GraphViewAction paintingAction = ActionUtil.getAction(paintingActions, menuAction);
		if(paintingAction != null) {
			return paintingAction.isEnabledForShape(shape);
		}
		GraphViewAction groupAction = ActionUtil.getAction(groupActions, menuAction);
		if(groupAction != null) {
			return groupAction.isEnabledForShape(shape);
		}
		if (shape instanceof ReactionContainerShape) {
			if (menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION)) {
				VCellTransferable.ReactionSpeciesCopy reactionSpeciesCopy = (VCellTransferable.ReactionSpeciesCopy)VCellTransferable.getFromClipboard(VCellTransferable.REACTION_SPECIES_ARRAY_FLAVOR);
				if(reactionSpeciesCopy != null){
					Structure targetStructure = ((ReactionContainerShape) shape).getStructure();
					if(reactionSpeciesCopy.getReactStepArr() != null){
						for (int i = 0; i < reactionSpeciesCopy.getReactStepArr().length; i++) {
							if (!reactionSpeciesCopy.getReactStepArr()[i].getStructure().getClass().equals(targetStructure.getClass())) {
								return false;
							}
						}
					}
					return true;
				}else{
					return false;
				}
			}else if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)) {
				if(((ReactionContainerShape) shape).getStructureSuite().getStructures().size() == 1){
					return false;
				}
			}
		}
		return true;
	}

	public void showReactionBrowserDialog(Structure struct,Point location) throws Exception{
		if(getReactionCartoon() == null || getDocumentManager() == null || getDialogOwner(getGraphPane()) == null){
			return;
		}
		
		DBReactionWizardPanel dbrqWiz = new DBReactionWizardPanel();
		dbrqWiz.setModel(getModel());
		dbrqWiz.setStructure(struct);
		dbrqWiz.setDocumentManager(getDocumentManager());
		dbrqWiz.setRXPasteInterface(ReactionCartoonTool.this);
		
		ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(this.getGraphPane());
		ChildWindow childWindow = childWindowManager.addChildWindow( dbrqWiz, SEARCHABLE_REACTIONS_CONTEXT_OBJECT, "Create Reaction within structure '" + struct.getName() + "'" );
		
		dbrqWiz.setChildWindow(childWindow); // this is needed so that the wizard can close itself.
		
		childWindow.setIsCenteredOnParent();
		childWindow.pack();
		childWindow.show();
	}

	// TO DO: allow user preferences for directory selection.
	public void showSaveReactionImageDialog() throws Exception {
		// set file filter
		SimpleFilenameFilter jpgFilter = new SimpleFilenameFilter("jpg");
		final java.io.File defaultFile = new java.io.File(getModel().getName()+".jpg");
		ClientServerManager csm = (ClientServerManager) getDocumentManager().getSessionManager();
		UserPreferences userPref = csm.getUserPreferences();
		File defaultPath = userPref.getCurrentDialogPath();
		VCFileChooser fileChooser = new VCFileChooser(defaultPath);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(jpgFilter);
		fileChooser.setSelectedFile(defaultFile);
		fileChooser.setDialogTitle("Save Image As...");
		// a hack to fix the jdk 1.2 problem (?) of losing the selected file
		// name once the user changes the directory.
		class FileChooserFix implements java.beans.PropertyChangeListener {
			public void propertyChange(java.beans.PropertyChangeEvent ev) {
				JFileChooser chooser = (JFileChooser) ev.getSource();
				if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(ev
						.getPropertyName())) {
					chooser.setSelectedFile(defaultFile);
				}
			}
		}
		fileChooser.addPropertyChangeListener(new FileChooserFix());
		// process user input
		if (fileChooser.showSaveDialog(getDialogOwner(getGraphPane())) == JFileChooser.APPROVE_OPTION) {
			java.io.File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				if (selectedFile.exists()) {
					int question = javax.swing.JOptionPane.showConfirmDialog(
							getDialogOwner(getGraphPane()), "Overwrite file: "
									+ selectedFile.getPath() + "?");
					if (question == javax.swing.JOptionPane.NO_OPTION
							|| question == javax.swing.JOptionPane.CANCEL_OPTION) {
						return;
					}
				}

				BufferedImage bufferedImage = ITextWriter.generateDocReactionsImage(this.getModel(),ITextWriter.HIGH_RESOLUTION);
				FileOutputStream reactionImageOutputStream = null;
				try{
					reactionImageOutputStream = new FileOutputStream(selectedFile);
					reactionImageOutputStream.write(ITextWriter.encodeJPEG(bufferedImage).toByteArray());
				}finally{
					try{if(reactionImageOutputStream != null){reactionImageOutputStream.close();}}catch(Exception e){e.printStackTrace();}
				}

				//reset the user preference for the default path, if needed.
				File newPath = selectedFile.getParentFile();
				if (!newPath.equals(defaultPath)) {
					userPref.setCurrentDialogPath(newPath);
				}
			}
		} 
	}

	public void showSimpleReactionPropertiesDialog(SimpleReactionShape simpleReactionShape) {
//		JFrame parent = (JFrame) BeanUtils.findTypeParentOfComponent(
//				getGraphPane(), JFrame.class);
//		SimpleReactionPanelDialog simpleReactionDialog = new SimpleReactionPanelDialog(
//				parent, true);
//		simpleReactionDialog.setSimpleReaction(simpleReactionShape.getSimpleReaction());
//		simpleReactionDialog.setTitle("Reaction Kinetics Editor");
//		ZEnforcer.showModalDialogOnTop(simpleReactionDialog, getJDesktopPane());
//		// cleanup listeners after window closed for GC
//		simpleReactionDialog.cleanupOnClose();
//		// update in case of name change (should really be a listener)
//		simpleReactionShape.refreshLabel();
//		getReactionCartoon().fireGraphChanged();
	}

	private void resetDropTargets(Boolean bDropTargetFlag,Boolean bStructureMode){
		Structure[] structures = getModel().getStructures();
		for (int i = 0; i < structures.length; i++) {
			((ReactionContainerShape)getGraphModel().getShapeFromModelObject(structures[i])).setDropTargetEnableLow(bDropTargetFlag,bStructureMode);
			((ReactionContainerShape)getGraphModel().getShapeFromModelObject(structures[i])).setDropTargetEnableHigh(bDropTargetFlag,bStructureMode);
		}
	}
	
	@Override
	public void updateMode(Mode newMode) {
		if (newMode == mode) {
			return;
		}
		resetMouseActionHistory();
		if (getReactionCartoon() != null) {
			getReactionCartoon().clearSelection();
			//turn off all structure drop targets
			resetDropTargets(null,null);
			getGraphPane().repaint();
		}
		this.mode = newMode;
		if(getGraphPane() != null){
			switch (mode){
			case LINE: case LINEDIRECTED: case LINECATALYST: case STEP: case FLUX:{
				getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				break;
			}
			case SPECIES: case SELECT:{
				getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				break;
			}
			case STRUCTURE:{
				getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				resetDropTargets(false,true);
				getGraphPane().repaint();
				break;
			}
			default:{
				System.out.println("ERROR: mode " + newMode + "not defined");
				break;
			}
			}
		}
		return;
	}

	protected void resetMouseActionHistory() {
		bMoving = false;
		movingShape = null;
		bRectStretch = false;
		rectShape = null;
		bLineStretch = false;
		startShape = null;
		edgeShape = null;
		startPointWorld = null;
		endPointWorld = null;
	}

	public void saveNodePositions() {
		saveDiagram();
	}
	
}
