package cbit.vcell.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 �*/
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyVetoException;
import java.io.FileOutputStream;
import java.util.IdentityHashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JViewport;

import org.vcell.util.BeanUtils;
import org.vcell.util.SimpleFilenameFilter;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.JInternalFrameEnhanced;
import org.vcell.util.gui.SimpleTransferable;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCFileChooser;
import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphEmbeddingManager;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.RubberBandEdgeShape;
import cbit.gui.graph.RubberBandRectShape;
import cbit.gui.graph.Shape;
import cbit.gui.graph.ShapeUtil;
import cbit.gui.graph.actions.ActionUtil;
import cbit.gui.graph.actions.CartoonToolEditActions;
import cbit.gui.graph.actions.CartoonToolMiscActions;
import cbit.gui.graph.actions.CartoonToolSaveAsImageActions;
import cbit.gui.graph.actions.GraphViewAction;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Flux;
import cbit.vcell.model.FluxReaction;
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
import cbit.vcell.model.gui.DBReactionWizardPanel;
import cbit.vcell.publish.ITextWriter;

public class ReactionCartoonTool extends BioCartoonTool {

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
	
	public static enum LineType {
		NULL("<<?>>", Color.red, Cursor.MOVE_CURSOR), CATALYST("<<C A T A L Y S T>>", Color.GRAY), 
		PRODUCT("<<P R O D U C T>>"), REACTANT("<<R E A C T A N T>>"), FLUX("<<F L U X>>");
		
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

	private LineType getLineTypeFromAttachment(SpeciesContext speciesContext,
			Point worldPoint) throws Exception {
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
						if (rps[i] instanceof Reactant
								&& rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.REACTANT;
				}
				case Shape.ATTACH_CENTER: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Catalyst
								&& rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.CATALYST;
				}
				case Shape.ATTACH_RIGHT: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Product
								&& rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.PRODUCT;
				}
				}
			} else if (mouseOverShape instanceof FluxReactionShape) {
				switch (mouseOverShape.getAttachmentFromAbs(worldPoint)) {
				case Shape.ATTACH_LEFT: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Flux
								&& rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.FLUX;
				}
				case Shape.ATTACH_CENTER: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Catalyst
								&& rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.CATALYST;
				}
				case Shape.ATTACH_RIGHT: {
					for (int i = 0; i < rps.length; i++) {
						if (rps[i] instanceof Flux
								&& rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.FLUX;
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
				if (mouseOverShape instanceof SimpleReactionShape){
					for (int i = 0; i < rps.length; i++){
						if (rps[i] instanceof Reactant && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.REACTANT;
				}else if (mouseOverShape instanceof FluxReactionShape){
					for (int i = 0; i < rps.length; i++){
						if (rps[i] instanceof Flux && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
					return LineType.FLUX;
				}
			}
		}else if (mouseOverShape instanceof SpeciesContextShape){
			SpeciesContext speciesContext = (SpeciesContext)mouseOverShape.getModelObject();
			if (startingShape instanceof SpeciesContextShape){
				return LineType.PRODUCT;  // straight from one species to another ... will create a reaction upon release
			}else if (startingShape instanceof ReactionStepShape){
				ReactionStep reactionStep = (ReactionStep)startingShape.getModelObject();
				ReactionParticipant[] rps = reactionStep.getReactionParticipants();
				if (reactionStep instanceof FluxReaction){
					for (int i = 0; i < rps.length; i++){
						if (rps[i] instanceof Flux && rps[i].getSpeciesContext() == speciesContext) {
							return LineType.NULL;
						}
					}
				} else if (reactionStep instanceof SimpleReaction){
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

	private ReactionStep[] getReactionStepArray(Shape shape, String menuAction) {
		if (shape instanceof ReactionStepShape) {
			List<Shape> reactionStepShapes = getReactionCartoon().getSelectedShapes();
			if (reactionStepShapes != null && reactionStepShapes.size() > 0) {
				ReactionStep[] rxStepsArr = new ReactionStep[reactionStepShapes.size()];
				for (int i = 0; i < reactionStepShapes.size(); i += 1) {
					rxStepsArr[i] = (ReactionStep) reactionStepShapes.get(i).getModelObject();
				}
				return rxStepsArr;
			}
		}
		return null;
	}

	public void layout(String layoutName) throws Exception {
		if (getReactionCartoon().getStructureSuite().getStructures().size() != 1) {
			if (GraphEmbeddingManager.RANDOMIZER.equals(layoutName)) {
				getReactionCartoon().setRandomLayout(true);
				getGraphPane().repaint();
			} else {
				System.out.println(layoutName
						+ " only implemented for single compartment");
			}
			saveDiagram();
			return;
		}
		// for non-membranes, use RPI's layout stuff
		graphEmbeddingManager.layoutRPI(layoutName);
		saveDiagram();
	}

	public void layoutGlg() throws Exception {
		// ****In the case of Membranes DO as before!****
		if (getReactionCartoon().getStructureSuite().getStructures().size() != 1) {
			getReactionCartoon().setRandomLayout(true);
			getGraphPane().repaint();
			saveDiagram();
			return;
		}
		// Create graph object
		graphEmbeddingManager.layoutGLG();
		saveDiagram();
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
//				showCreateSpeciesContextDialog(getGraphPane(),
//						getReactionCartoon().getModel(),
//						((ReactionContainerShape) shape).getStructure(), null);
				SpeciesContext speciesContext = getReactionCartoon().getModel().createSpeciesContext(((ReactionContainerShape) shape).getStructure());
				getGraphModel().select(speciesContext);
			}
		} else if (menuAction.equals(CartoonToolEditActions.Copy.MENU_ACTION)) {
			if (shape instanceof SpeciesContextShape) {
				Species species = ((SpeciesContextShape) shape)
						.getSpeciesContext().getSpecies();
				VCellTransferable.sendToClipboard(species);
			} else if (shape instanceof ReactionStepShape) {
				ReactionStep[] reactionStepArr = getReactionStepArray(shape,
						menuAction);
				if (reactionStepArr != null) {
					VCellTransferable.sendToClipboard(reactionStepArr);
				}
			}
		} else if (menuAction.equals(CartoonToolEditActions.Paste.MENU_ACTION)
				|| menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape) {
				// See if Species
				Species species = (Species) SimpleTransferable
						.getFromClipboard(VCellTransferable.SPECIES_FLAVOR);
				if (species != null) {
					IdentityHashMap<Species, Species> speciesHash = new IdentityHashMap<Species, Species>();
					pasteSpecies(getGraphPane(), species, getModel(), 
							((ReactionContainerShape) shape).getStructure(), 
							menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION),/* true, */
					speciesHash, null);
				}
				// See if ReactionStep[]
				ReactionStep[] reactionStepArr = (ReactionStep[]) SimpleTransferable
						.getFromClipboard(VCellTransferable.REACTIONSTEP_ARRAY_FLAVOR);
				if (reactionStepArr != null) {
					try {
						pasteReactionSteps(
								reactionStepArr, getModel(),
								((ReactionContainerShape) shape).getStructure(),
								menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION),
								getGraphPane(), null);
					} catch (Exception e) {
						e.printStackTrace(System.out);
						DialogUtils.showErrorDialog(getGraphPane(),
								"Error while pasting reaction:\n"
										+ e.getMessage(), e);
					}
				}
			}
		} else if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)
				|| menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)) {
			try {
				if (shape instanceof ReactantShape
						|| shape instanceof ProductShape
						|| shape instanceof CatalystShape) {
					ReactionParticipant reactionParticipant = ((ReactionParticipantShape) shape)
							.getReactionParticipant();
					ReactionStep reactionStep = reactionParticipant
							.getReactionStep();
					reactionStep.removeReactionParticipant(reactionParticipant);
				}
				if (shape instanceof ReactionStepShape) {
					ReactionStep[] reactionStepArr = getReactionStepArray(
							shape, menuAction);
					if (reactionStepArr != null) {
						if (menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)) {
							VCellTransferable.sendToClipboard(reactionStepArr);
						}
					}
					for (int i = 0; i < reactionStepArr.length; i += 1) {
						getModel().removeReactionStep(reactionStepArr[i]);
					}
				}
				if (shape instanceof SpeciesContextShape) {
					getModel().removeSpeciesContext(
							((SpeciesContextShape) shape).getSpeciesContext());
					if (menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)) {
						VCellTransferable.sendToClipboard(((SpeciesContextShape) shape).getSpeciesContext().getSpecies());
					}
				}
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
		} else if (menuAction.equals(CartoonToolSaveAsImageActions.HighRes.MENU_ACTION)
				|| menuAction.equals(CartoonToolSaveAsImageActions.MedRes.MENU_ACTION)
				|| menuAction.equals(CartoonToolSaveAsImageActions.LowRes.MENU_ACTION)) {
			try {
				String resType = null;
				if (menuAction.equals(CartoonToolSaveAsImageActions.HighRes.MENU_ACTION)) {
					resType = ITextWriter.HIGH_RESOLUTION;
				} else if (menuAction.equals(CartoonToolSaveAsImageActions.MedRes.MENU_ACTION)) {
					resType = ITextWriter.MEDIUM_RESOLUTION;
				} else if (menuAction.equals(CartoonToolSaveAsImageActions.LowRes.MENU_ACTION)) {
					resType = ITextWriter.LOW_RESOLUTION;
				}
				if (shape instanceof ReactionContainerShape) {
					showSaveReactionImageDialog(
							((ReactionContainerShape) shape).getStructure(), resType);
				}
			} catch (Exception e) {
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

	@Override
	public void mouseClicked(MouseEvent event) {
		Point screenPoint = new Point(event.getX(), event.getY());
		Point worldPoint = screenToWorld(screenPoint);

		try {
			// if right mouse button, then do popup menu
			if ((event.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
				return;
			}
			switch (mode) {
			case SELECT: {
				if (event.getClickCount() == 2) {
					Shape selectedShape = getReactionCartoon().getSelectedShape();
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
						positionShapeForObject(reactionStep, worldPoint);
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
						Shape shape = getReactionCartoon()
								.getShapeFromModelObject(fluxReaction);
						showFluxReactionPropertiesDialog((FluxReactionShape) shape);
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
					getGraphModel().select(speciesContext);
					positionShapeForObject(speciesContext, worldPoint);
//					showCreateSpeciesContextDialog(getGraphPane(),
//							getReactionCartoon().getModel(),
//							((ReactionContainerShape) pickedShape).getStructure(), scShapeLocation);
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

	@Override
	public void mouseDragged(MouseEvent event) {
		if ((event.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
			return;
		}
		boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
		boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
		try {
			switch (mode) {
			case SELECT: {
				Point worldPoint = screenToWorld(event.getX(), event.getY());
				if (bMoving) {
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
					if (!bCntrl && !bShift && (shape instanceof SpeciesContextShape || 
							shape instanceof ReactionStepShape ||
							(shape instanceof ReactionContainerShape && worldPoint.y<25))){
						bMoving=true;
						movingShape = shape;
						movingPointWorld = shape.getSpaceManager().getAbsLoc();
						movingOffsetWorld = new Point(worldPoint.x-movingPointWorld.x,worldPoint.y-movingPointWorld.y);
						if (movingShape instanceof ReactionContainerShape){
							((ReactionContainerShape)movingShape).isBeingDragged = true;
						}
					} else if (shape instanceof ReactionContainerShape || bShift || bCntrl){
						bRectStretch = true;
						endPointWorld = new Point(worldPoint.x + 1,
								worldPoint.y + 1);
						rectShape = new RubberBandRectShape(worldPoint,
								endPointWorld, getReactionCartoon());
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
			// Always select with MousePress
			boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
			boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
			if (mode == Mode.SELECT
					|| (event.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
				selectEventFromWorld(startPointWorld, bShift, bCntrl);
			}
			// if mouse popupMenu event, popup menu
			if (event.isPopupTrigger() && mode == Mode.SELECT) {
				popupMenu(getReactionCartoon().getSelectedShape(), eventX, eventY);
				return;
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mousePressed: uncaught exception");
			e.printStackTrace(System.out);
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if(getReactionCartoon() == null){ return; }
		try {
			// Pick shape
			int eventX = event.getX();
			int eventY = event.getY();
			Point worldPoint = new Point(
					(int) (eventX * 100.0 / getReactionCartoon().getZoomPercent()),
					(int) (eventY * 100.0 / getReactionCartoon().getZoomPercent()));
			Shape endShape = getReactionCartoon().pickWorld(worldPoint);
			// if mouse popupMenu event, popup menu
			if (event.isPopupTrigger() && mode == Mode.SELECT) {
				popupMenu(getReactionCartoon().getSelectedShape(), event.getX(), event.getY());
				return;
			}
			if ((event.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
				return;
			}
			// else do select and move
			switch (mode) {
			case SELECT: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bMoving){
					if (movingShape instanceof ReactionContainerShape){
						((ReactionContainerShape)movingShape).isBeingDragged = false;
					}
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
			case LINE: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bLineStretch) {
					bLineStretch = false;
					// set label and color for line depending on attachment area
					// on ReactionStepShape
					SpeciesContext speciesContext = (SpeciesContext) 
						edgeShape.getStartShape().getModelObject();
					LineType lineType = getLineTypeFromAttachment(speciesContext, worldPoint);
					// remove temporary edge
					getReactionCartoon().removeShape(edgeShape);
					edgeShape = null;
					if (endShape instanceof SimpleReactionShape) {
						SimpleReaction simpleReaction = (SimpleReaction) endShape.getModelObject();
						// add reactionParticipant to model
						switch (lineType) {
						case CATALYST: {
							simpleReaction.addCatalyst(speciesContext);
							getReactionCartoon().notifyChangeEvent();
							setMode(Mode.SELECT);
							break;
						}
						case REACTANT: {
							simpleReaction.addReactant(speciesContext, 1);
							getReactionCartoon().notifyChangeEvent();
							setMode(Mode.SELECT);
							break;
						}
						case PRODUCT: {
							simpleReaction.addProduct(speciesContext, 1);
							getReactionCartoon().notifyChangeEvent();
							setMode(Mode.SELECT);
							break;
						}
						case NULL: {
							getGraphPane().repaint();
							break;
						}
						}
					} else if (endShape instanceof FluxReactionShape) {
						FluxReaction fluxReaction = (FluxReaction) endShape.getModelObject();
						// add reactionParticipant to model
						switch (lineType) {
						case CATALYST: {
							fluxReaction.addCatalyst(speciesContext);
							getReactionCartoon().notifyChangeEvent();
							setMode(Mode.SELECT);
							break;
						}
						case FLUX: {
							// assure that there are the appropriate
							// speciesContexts
							Membrane membrane = (Membrane) fluxReaction.getStructure();
							Feature feature = membrane.getOutsideFeature();
							SpeciesContext sc = getModel().getSpeciesContext(
											speciesContext.getSpecies(),
											feature);
							if (sc == null) {
								getModel().addSpeciesContext(
										speciesContext.getSpecies(), feature);
							}
							feature = membrane.getInsideFeature();
							sc = getModel().getSpeciesContext(
									speciesContext.getSpecies(), feature);
							if (sc == null) {
								getModel().addSpeciesContext(
										speciesContext.getSpecies(), feature);
							}

							fluxReaction.setFluxCarrier(speciesContext.getSpecies(), getModel());
							getReactionCartoon().notifyChangeEvent();
							setMode(Mode.SELECT);
							break;
						}
						case NULL: {
							break;
						}
						}
					}
				}
				getGraphPane().repaint();
				break;
			}
			case LINEDIRECTED: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bLineStretch) {
					bLineStretch = false;
					// set label and color for line depending on which shape the edge started. (rather than attachment area on ReactionStepShape)
					LineType lineType = getLineTypeFromDirection(startShape,worldPoint);
					if (endShape instanceof SimpleReactionShape){
						SimpleReaction simpleReaction = (SimpleReaction)endShape.getModelObject();
						Object startShapeObject = null;						
						if(startShape != null) {
							startShapeObject = startShape.getModelObject();
						}
						if(startShapeObject instanceof SpeciesContext) {
							SpeciesContext speciesContext = (SpeciesContext) startShapeObject;
							int stoichiometry = 1;
							Reactant reactant = null;
							for(ReactionParticipant participant : 
								simpleReaction.getReactionParticipants()) {
								if(participant instanceof Reactant && 
										participant.getSpeciesContext().equals(speciesContext)) {
									reactant = (Reactant) participant;
								}
							}
							if(reactant != null) {
								reactant.setStoichiometry(reactant.getStoichiometry() + 1);
								Shape shape = getReactionCartoon().getShapeFromModelObject(reactant);
								if(shape != null) {
									shape.refreshLabel();
								}
							} else {
								simpleReaction.addReactant(speciesContext, stoichiometry);								
							}
							getReactionCartoon().notifyChangeEvent();
							break;
						} else if(startShapeObject instanceof SimpleReaction) {
							SimpleReaction simpleReactionStart = (SimpleReaction) startShapeObject;
							Structure structureReaction = simpleReaction.getStructure();
							Structure structureReactionStart = simpleReactionStart.getStructure();
							Structure structureSpecies = null;
							if(structureReaction != null && structureReactionStart != null && 
									structureReaction.equals(structureReactionStart)) {
								structureSpecies = structureReaction;
							} else if(structureReaction != null && structureReactionStart == null) {
								structureSpecies = structureReaction;
							} else if(structureReactionStart != null && structureReaction == null) {
								structureSpecies = structureReactionStart;
							}
							if(structureSpecies != null) {
								SpeciesContext speciesContext = getReactionCartoon().getModel().createSpeciesContext(structureSpecies);
								simpleReactionStart.addProduct(speciesContext, 1);
								simpleReaction.addReactant(speciesContext, 1);
								getReactionCartoon().notifyChangeEvent();
								Point startLoc = startShape.getSpaceManager().getAbsLoc();
								Point endLoc = endShape.getSpaceManager().getAbsLoc();
								positionShapeForObject(speciesContext, new Point(
										(startLoc.x + endLoc.x) / 2, (startLoc.y + endLoc.y) / 2));
							}
						} else if(startShapeObject instanceof Structure) {
							Structure structure = (Structure) startShapeObject;
							SpeciesContext speciesContext =  getReactionCartoon().getModel().createSpeciesContext(structure);
							simpleReaction.addReactant(speciesContext, 1);
							getReactionCartoon().notifyChangeEvent();
							positionShapeForObject(speciesContext, edgeShape.getStart());
						}
					} else if (endShape instanceof SpeciesContextShape) {
						SpeciesContext speciesContext = (SpeciesContext) endShape.getModelObject();
						Object startShapeObject = null;
						if(startShape != null) {
							startShapeObject = startShape.getModelObject();							
						}
						if(startShapeObject instanceof SimpleReaction) {
							SimpleReaction simpleReaction = (SimpleReaction) startShapeObject;
							int stoichiometry = 1;
							Product product = null;
							for(ReactionParticipant participant : 
								simpleReaction.getReactionParticipants()) {
								if(participant instanceof Product && 
										participant.getSpeciesContext().equals(speciesContext)) {
									product = (Product) participant;
									Shape shape = getReactionCartoon().getShapeFromModelObject(product);
									if(shape != null) {
										shape.refreshLabel();
									}
								}
							}
							if(product != null) {
								product.setStoichiometry(product.getStoichiometry() + 1);
							} else {
								simpleReaction.addProduct(speciesContext, stoichiometry);								
							}
							getReactionCartoon().notifyChangeEvent();
							resetMouseActionHistory();
							break;
						} else if (startShapeObject instanceof SpeciesContext) {
							SpeciesContext speciesContextStart = (SpeciesContext) startShapeObject;
							Structure structure = speciesContext.getStructure();
							if(structure.equals(speciesContextStart.getStructure())) {
								Model model = getReactionCartoon().getModel();
								SimpleReaction reaction = model.createSimpleReaction(structure);
								reaction.addReactant(speciesContextStart, 1);
								reaction.addProduct(speciesContext, 1);
								getReactionCartoon().notifyChangeEvent();
								Point pickedShapePos = endShape.getSpaceManager().getAbsLoc();
								Point startShapePos = startShape.getSpaceManager().getAbsLoc();
								positionShapeForObject(reaction, new Point(
										(pickedShapePos.x + startShapePos.x)/2, 
										(pickedShapePos.y + startShapePos.y)/2));
								getReactionCartoon().notifyChangeEvent();
							}
						} else if(startShapeObject instanceof Structure) {
							Structure structure = (Structure) startShapeObject;
							Model model = getReactionCartoon().getModel();
							SimpleReaction reaction = model.createSimpleReaction(structure);
							reaction.addProduct(speciesContext, 1);
							getReactionCartoon().notifyChangeEvent();
							positionShapeForObject(reaction, edgeShape.getStart());
						}
					} else if (endShape instanceof FluxReactionShape){
						FluxReaction fluxReaction = (FluxReaction)endShape.getModelObject();
						switch (lineType){
						case FLUX:{
							// assure that there are the appropriate speciesContexts
							Membrane membrane = (Membrane)fluxReaction.getStructure();
							Feature feature = membrane.getOutsideFeature();
							SpeciesContext speciesContext = null;
							if (startShape instanceof SpeciesContextShape){
								speciesContext = 
									(SpeciesContext)startShape.getModelObject();
							} else if (edgeShape.getEndShape() instanceof SpeciesContextShape){
								speciesContext = 
									(SpeciesContext)edgeShape.getEndShape().getModelObject();
							}
							SpeciesContext sc = getModel().getSpeciesContext(speciesContext.getSpecies(),feature);
							if (sc==null){
								getModel().addSpeciesContext(speciesContext.getSpecies(),feature);
							}	
							feature = membrane.getInsideFeature();
							sc = getModel().getSpeciesContext(
									speciesContext.getSpecies(), feature);
							if (sc == null) {
								getModel().addSpeciesContext(
										speciesContext.getSpecies(), feature);
							}
							fluxReaction.setFluxCarrier(speciesContext.getSpecies(), getModel());
							getReactionCartoon().notifyChangeEvent();
							break;
						}
						case NULL: {
							break;
						}
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
						Point endPos = edgeShape.getEnd();
						if(startObject instanceof SimpleReaction) {
							SimpleReaction reaction = (SimpleReaction) startObject;
							SpeciesContext speciesContext = getReactionCartoon().getModel().createSpeciesContext(endStructure);
							reaction.addProduct(speciesContext, 1);
							getReactionCartoon().notifyChangeEvent();
							positionShapeForObject(speciesContext, endPos);
						} else if(startObject instanceof SpeciesContext) {
							SpeciesContext speciesContext = (SpeciesContext) startObject;
							SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(endStructure);
							reaction.addReactant(speciesContext, 1);
							getReactionCartoon().notifyChangeEvent();
							positionShapeForObject(reaction, endPos);
						} else if(startObject instanceof Structure) {
							Structure startStructure = (Structure) startObject;
							if(endStructure.equals(startStructure)) {
								SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(endStructure);
								SpeciesContext speciesContext2 = getReactionCartoon().getModel().createSpeciesContext(endStructure);
								SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(endStructure);
								reaction.addReactant(speciesContext1, 1);
								reaction.addProduct(speciesContext2, 1);
								getReactionCartoon().notifyChangeEvent();
								Point startPos = edgeShape.getStart();
								positionShapeForObject(speciesContext1, startPos);
								positionShapeForObject(speciesContext2, endPos);
								positionShapeForObject(reaction, new Point((startPos.x + endPos.x)/2, (startPos.y + endPos.y)/2));
							}
							else
							{
								if(endStructure instanceof Membrane && startStructure instanceof Feature)
								{
									Membrane endMembrane = (Membrane)endStructure;
									Feature startFeature = (Feature)startStructure;
									if(endMembrane.getOutsideFeature().equals(startFeature) || endMembrane.getInsideFeature().equals(startFeature))
									{
										SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(startFeature);
										SpeciesContext speciesContext2 = getReactionCartoon().getModel().createSpeciesContext(endMembrane);
										SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(endMembrane);
										reaction.addReactant(speciesContext1, 1);
										reaction.addProduct(speciesContext2, 1);
										getReactionCartoon().notifyChangeEvent();
										Point startPos = edgeShape.getStart();
										positionShapeForObject(speciesContext1, startPos);
										positionShapeForObject(speciesContext2, endPos);
										positionShapeForObject(reaction, new Point((startPos.x + endPos.x)/2, (startPos.y + endPos.y)/2));
									}
								}
								else if(endStructure instanceof Feature && startStructure instanceof Membrane)
								{
									Membrane startMembrane = (Membrane)startStructure;
									Feature endFeature = (Feature)endStructure;
									if(startMembrane.getOutsideFeature().equals(endFeature) || startMembrane.getInsideFeature().equals(endFeature))
									{
										SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(startMembrane);
										SpeciesContext speciesContext2 = getReactionCartoon().getModel().createSpeciesContext(endFeature);
										SimpleReaction reaction = getReactionCartoon().getModel().createSimpleReaction(startMembrane);
										reaction.addReactant(speciesContext1, 1);
										reaction.addProduct(speciesContext2, 1);
										getReactionCartoon().notifyChangeEvent();
										Point startPos = edgeShape.getStart();
										positionShapeForObject(speciesContext1, startPos);
										positionShapeForObject(speciesContext2, endPos);
										positionShapeForObject(reaction, new Point((startPos.x + endPos.x)/2, (startPos.y + endPos.y)/2));
									}
								}
								else if(endStructure instanceof Feature && startStructure instanceof Feature)
								{
									Feature startFeature = (Feature)startStructure;
									Membrane startFeatureMem = startFeature.getMembrane();
									Feature endFeature = (Feature)endStructure;
									Membrane endFeatureMem = endFeature.getMembrane();
									//flux from startFeature to endFeature
									Membrane fluxMem = null;
									if(startFeatureMem != null && startFeatureMem.getOutsideFeature().equals(endFeature))
									{
										fluxMem = startFeatureMem;
									}
									else if(endFeatureMem != null && endFeatureMem.getOutsideFeature().equals(startFeature))
									{
										fluxMem =endFeatureMem;
									}
									if(fluxMem != null)
									{
										SpeciesContext speciesContext1 = getReactionCartoon().getModel().createSpeciesContext(startFeature);
										Species fluxCarrier = speciesContext1.getSpecies();
										SpeciesContext speciesContext2 = new SpeciesContext(fluxCarrier, endFeature);
										getReactionCartoon().getModel().addSpeciesContext(speciesContext2);
										FluxReaction flux = getReactionCartoon().getModel().createFluxReaction(fluxMem);
										flux.setFluxCarrier(fluxCarrier, getReactionCartoon().getModel());
										getReactionCartoon().notifyChangeEvent();
										Point startPos = edgeShape.getStart();
										positionShapeForObject(speciesContext1, startPos);
										positionShapeForObject(speciesContext2, endPos);
										positionShapeForObject(flux, new Point((startPos.x + endPos.x)/2, (startPos.y + endPos.y)/2));
									}
								}
							}
						}
					}
				}
				break;
			}
			case LINECATALYST: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bLineStretch){
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
							speciesContext = (SpeciesContext) endObject;							
						} else if(endObject instanceof Structure) {
							Structure endStructure = (Structure) endObject;
							speciesContext = getReactionCartoon().getModel().createSpeciesContext(endStructure);
							getReactionCartoon().notifyChangeEvent();
							Point endPos = edgeShape.getEnd();
							positionShapeForObject(speciesContext, endPos);
						}
					} else if(startObject instanceof SpeciesContext) {
						speciesContext = (SpeciesContext) startObject;						
						if(endObject instanceof ReactionStep) {
							reactionStep = (ReactionStep) endObject;
						} else if(endObject instanceof Structure) {
							Structure endStructure = (Structure) endObject;
							reactionStep = getReactionCartoon().getModel().createSimpleReaction(endStructure);
							getReactionCartoon().notifyChangeEvent();
							Point endPos = edgeShape.getEnd();
							positionShapeForObject(reactionStep, endPos);
						}
					} else if (startObject instanceof Structure) {
						Structure startStructure = (Structure) startObject;
						if(endObject instanceof ReactionStep) {
							reactionStep = (ReactionStep) endObject;
							speciesContext = getReactionCartoon().getModel().createSpeciesContext(startStructure);
							getReactionCartoon().notifyChangeEvent();
							positionShapeForObject(speciesContext, startPointWorld);
						} else if(endObject instanceof SpeciesContext) {
							speciesContext = (SpeciesContext) endObject;
							reactionStep = getReactionCartoon().getModel().createSimpleReaction(startStructure);
							getReactionCartoon().notifyChangeEvent();
							positionShapeForObject(reactionStep, startPointWorld);
						} else if(endObject instanceof Structure) {
							Structure endStructure = (Structure) endObject;
							speciesContext = getReactionCartoon().getModel().createSpeciesContext(startStructure);
							reactionStep = getReactionCartoon().getModel().createSimpleReaction(endStructure);
							getReactionCartoon().notifyChangeEvent();
							positionShapeForObject(speciesContext, startPointWorld);
							Point endPos = edgeShape.getEnd();
							positionShapeForObject(reactionStep, endPos);
						}
					}
					if (reactionStep != null && speciesContext != null) {
						Catalyst catalyst = null;
							for(ReactionParticipant participant : 
								reactionStep.getReactionParticipants()) {
								if(participant instanceof Catalyst && 
										participant.getSpeciesContext().equals(speciesContext)) {
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
	
	public Model getModel() {
		return getReactionCartoon().getModel();
	}
	
	protected void positionShapeForObject(Object object, Point pos) {
		Shape shape = getReactionCartoon().getShapeFromModelObject(object);
		if(shape != null) {
			shape.getSpaceManager().setAbsLoc(pos);
		}
	}

	public void saveDiagram() throws Exception {
		for(Structure structure : getReactionCartoon().getStructureSuite().getStructures()) {
			getReactionCartoon().setPositionsFromReactionCartoon(
					getModel().getDiagram(structure));			
		}
	}

	private void selectEventFromWorld(Point worldPoint, boolean bShift, boolean bCntrl) {
		if (getReactionCartoon() == null) {
			return;
		}
		if (!bShift && !bCntrl) {
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

		} else if (bShift) {
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
			getReactionCartoon().selectShape(pickedShape);
		} else if (bCntrl) {
			Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);
			if (pickedShape == null) {
				return;
			}
			if (pickedShape instanceof ReactionContainerShape) {
				return;
			}
			if (pickedShape.isSelected()) {
				getReactionCartoon().deselectShape(pickedShape);
			} else {
				getReactionCartoon().selectShape(pickedShape);
			}
		}
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
		if (menuAction.equals(CartoonToolEditActions.Paste.MENU_ACTION)
				|| menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION)) {
			if (shape instanceof ReactionContainerShape) {
				return true;
			}
		}
		if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)) {
			if (shape instanceof ReactionStepShape
					|| shape instanceof ReactantShape
					|| shape instanceof ProductShape
					|| shape instanceof CatalystShape) {
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

		if (menuAction.equals(CartoonToolSaveAsImageActions.HighRes.MENU_ACTION)
				|| menuAction.equals(CartoonToolSaveAsImageActions.MedRes.MENU_ACTION)
				|| menuAction.equals(CartoonToolSaveAsImageActions.LowRes.MENU_ACTION)) {
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
			boolean bPasteNew = menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION);
			boolean bPaste = menuAction.equals(CartoonToolEditActions.Paste.MENU_ACTION);
			if (bPaste || bPasteNew) {
				// Paste if there is a species on the system clipboard and it
				// doesn't exist in structure
				Species species = (Species) 
					SimpleTransferable.getFromClipboard(VCellTransferable.SPECIES_FLAVOR);
				if (species != null) {
					if (getModel().contains(species)) {
						if (getModel().getSpeciesContext(
										species, ((ReactionContainerShape) shape).getStructure()) != null) {
							return bPasteNew ? true : false;
						} else {
							return bPasteNew ? false : true;
						}
					} else {
						return bPasteNew ? false : true;
					}
				}
				// Paste if there is a ReactionStepArr on the system clipboard
				// and structure types match
				ReactionStep[] reactionStepArr = (ReactionStep[]) SimpleTransferable
						.getFromClipboard(VCellTransferable.REACTIONSTEP_ARRAY_FLAVOR);
				if (reactionStepArr != null) {
					Structure targetStructure = ((ReactionContainerShape) shape).getStructure();
					for (int i = 0; i < reactionStepArr.length; i += 1) {
						if (!reactionStepArr[i].getStructure().getClass().equals(
							targetStructure.getClass())) {
							return false;
						}
					}
					return true;
				}
				return false;
			}
		}
		return true;
	}

	public void showFluxReactionPropertiesDialog(FluxReactionShape fluxReactionShape) {
//		if (getReactionCartoon() == null) {
//			return;
//		}
//		JFrame parent = (JFrame) BeanUtils.findTypeParentOfComponent(
//				getGraphPane(), JFrame.class);
//		FluxReaction_Dialog fluxReaction_Dialog = new FluxReaction_Dialog(parent, true);
//		fluxReaction_Dialog.init(fluxReactionShape.getFluxReaction(),
//				getReactionCartoon().getModel());
//		fluxReaction_Dialog.setTitle("Flux Reaction Editor");
//		ZEnforcer.showModalDialogOnTop(fluxReaction_Dialog, getJDesktopPane());
//		// update in case of name change (should really be a listener)
//		fluxReactionShape.refreshLabel();
//		getReactionCartoon().fireGraphChanged();
	}

	public void showProductPropertiesDialog(ProductShape productShape, Point location) {
		if (getReactionCartoon() == null
				|| getDialogOwner(getGraphPane()) == null) {
			return;
		}
		Product product = (Product) productShape.getModelObject();
		String typed = JOptionPane.showInputDialog(
				getDialogOwner(getGraphPane()), "Current stoichiometry is: "
						+ product.getStoichiometry(), "Input stoichiometry",
				JOptionPane.QUESTION_MESSAGE);
		if (typed != null) {
			try {
				product.setStoichiometry(Integer.parseInt(typed));
				productShape.refreshLabel();
				getReactionCartoon().fireGraphChanged();
			} catch (NumberFormatException exc) {
				JOptionPane.showMessageDialog(getDialogOwner(getGraphPane()),
						"You did not type a valid number", "Error:",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void showReactantPropertiesDialog(ReactantShape reactantShape, Point location) {
		if(getReactionCartoon() == null || getDialogOwner(getGraphPane()) == null){
			return;
		}
		Reactant reactant = (Reactant) reactantShape.getModelObject();
		String typed = JOptionPane.showInputDialog(
				getDialogOwner(getGraphPane()), "Current stoichiometry is: "
						+ reactant.getStoichiometry(), "Input stoichiometry",
				JOptionPane.QUESTION_MESSAGE);
		if (typed != null) {
			try {
				reactant.setStoichiometry(Integer.parseInt(typed));
				reactantShape.refreshLabel();
				getReactionCartoon().fireGraphChanged();
			} catch (NumberFormatException exc) {
				JOptionPane.showMessageDialog(getDialogOwner(getGraphPane()),
						"You did not type a valid number", "Error:",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void showReactionBrowserDialog(Structure struct,Point location) throws Exception{
		if(getReactionCartoon() == null || getDocumentManager() == null || getDialogOwner(getGraphPane()) == null){
			return;
		}
		JInternalFrameEnhanced jif = new JInternalFrameEnhanced(
				"Create Reaction within structure '" + struct.getName() + "'",
				true, true);
		DBReactionWizardPanel dbrqWiz = new DBReactionWizardPanel();
		dbrqWiz.setModel(getModel());
		dbrqWiz.setStructure(struct);
		dbrqWiz.setDocumentManager(getDocumentManager());
		jif.setContentPane(dbrqWiz);
		if(location != null){
			jif.setLocation(location);
		}
		getDialogOwner(getGraphPane()).add(jif, JLayeredPane.MODAL_LAYER);
		jif.pack();
		BeanUtils.centerOnComponent(jif, getDialogOwner(getGraphPane()));
		jif.show();
	}

	// TO DO: allow user preferences for directory selection.
	public void showSaveReactionImageDialog(Structure struct, String resLevel)
			throws Exception {
		if (struct == null || getModel() == null) { // or
			// throw
			// exception?
			System.err
					.println("Insufficient params for generating reactions image.");
			return;
		}
		if (resLevel == null) { // default resolution.
			resLevel = ITextWriter.HIGH_RESOLUTION;
		}
		System.out.println("Processing save as Image request for: "
				+ struct.getName() + " " + getModel().getName() + "(" + resLevel
				+ ")");
		// set file filter
		SimpleFilenameFilter gifFilter = new SimpleFilenameFilter("gif");
		final java.io.File defaultFile = new java.io.File(getModel().getName() + "_"
				+ struct.getName() + ".gif");
		ClientServerManager csm = (ClientServerManager) getDocumentManager()
				.getSessionManager();
		UserPreferences userPref = csm.getUserPreferences();
		String defaultPath = userPref.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
		VCFileChooser fileChooser = new VCFileChooser(defaultPath);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(gifFilter);
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
				//System.out.println("Saving reactions image to file: " + selectedFile.toString());
				getDocumentManager().generateReactionsImage(getModel(), struct, resLevel, 
						new FileOutputStream(selectedFile));
				//reset the user preference for the default path, if needed.
				String newPath = selectedFile.getParent();
				if (!newPath.equals(defaultPath)) {
					userPref.setGenPref(UserPreferences.GENERAL_LAST_PATH_USED,	newPath);
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

	@Override
	public void updateMode(Mode newMode) {
		if (newMode == mode) {
			return;
		}
		resetMouseActionHistory();
		if (getReactionCartoon() != null) {
			getReactionCartoon().clearSelection();
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
	
}