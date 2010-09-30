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
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JViewport;

import org.vcell.util.BeanUtils;
import org.vcell.util.SimpleFilenameFilter;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.JInternalFrameEnhanced;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCFileChooser;
import org.vcell.util.gui.ZEnforcer;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphEmbeddingManager;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.RubberBandEdgeShape;
import cbit.gui.graph.RubberBandRectShape;
import cbit.gui.graph.Shape;
import cbit.gui.graph.actions.CartoonToolEditActions;
import cbit.gui.graph.actions.CartoonToolMiscActions;
import cbit.gui.graph.actions.CartoonToolSaveAsImageActions;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Flux;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GeneralKinetics;
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
import cbit.vcell.model.gui.FluxReaction_Dialog;
import cbit.vcell.model.gui.SimpleReactionPanelDialog;
import cbit.vcell.publish.ITextWriter;

import com.genlogic.GraphLayout.GlgGraphEdge;
import com.genlogic.GraphLayout.GlgGraphNode;

import edu.rpi.graphdrawing.Node;

public class ReactionSlicesCartoonTool extends BioCartoonTool {

	class SortStructureHeirarchy implements Comparator<ReactionStep> {
		private Model model = null;
		public SortStructureHeirarchy(Model argModel){
			this.model = argModel;
		}
		public int compare(ReactionStep o1, ReactionStep o2){
			String structName1 = o1.getStructure().getName();
			String structName2 = o2.getStructure().getName();
			Structure struct = model.getTopFeature();
			while(true){
				if(struct.getName().equals(structName1) && struct.getName().equals(structName2)){
					return 0;
				};
				if(struct.getName().equals(structName1)){
					return -1;
				}
				if(struct.getName().equals(structName2)){
					return 1;
				}
				if(struct instanceof Feature){
					struct = ((Feature)struct).getMembrane();
				}else if (struct instanceof Membrane){
					struct = ((Membrane)struct).getInsideFeature();
				}
			}
		}
	}

	private ReactionSlicesCartoon reactionCartoon = null;

	// for dragging speciesContext's around
	private boolean bMoving = false;
	private Shape movingShape = null;
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

	private static final int LINE_TYPE_NULL = 0;
	private static final int LINE_TYPE_CATALYST = 1;
	private static final int LINE_TYPE_PRODUCT = 2;
	private static final int LINE_TYPE_REACTANT = 3;
	private static final int LINE_TYPE_FLUX = 4;
	private static final String lineLabels[] =
	{ "<<?>>", "<<C A T A L Y S T>>", "<<P R O D U C T>>", "<<R E A C T A N T>>", "<<F L U X>>" };
	private static final Color lineColors[] =
	{ Color.red, Color.gray, Color.black, Color.black, Color.black };
	private static final Cursor lineCursors[] =
	{
		Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR),
		Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
		Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
		Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
		Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)};

	public ReactionSlicesCartoonTool () {
		super();
	}

	public GraphModel getGraphModel() {
		return getReactionCartoon();
	}

	private int getLineTypeFromWorld(Shape startingShape, Point worldPoint) throws Exception {
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
							return LINE_TYPE_NULL;
						}
					}
					return LINE_TYPE_REACTANT;
				}else if (mouseOverShape instanceof FluxReactionShape){
					for (int i = 0; i < rps.length; i++){
						if (rps[i] instanceof Flux && rps[i].getSpeciesContext() == speciesContext) {
							return LINE_TYPE_NULL;
						}
					}
					return LINE_TYPE_FLUX;
				}
			}
		}else if (mouseOverShape instanceof SpeciesContextShape){
			SpeciesContext speciesContext = (SpeciesContext)mouseOverShape.getModelObject();
			if (startingShape instanceof SpeciesContextShape){
				return LINE_TYPE_PRODUCT;  // straight from one species to another ... will create a reaction upon release
			}else if (startingShape instanceof ReactionStepShape){
				ReactionStep reactionStep = (ReactionStep)startingShape.getModelObject();
				ReactionParticipant[] rps = reactionStep.getReactionParticipants();
				if (reactionStep instanceof FluxReaction){
					for (int i = 0; i < rps.length; i++){
						if (rps[i] instanceof Flux && rps[i].getSpeciesContext() == speciesContext) {
							return LINE_TYPE_NULL;
						}
					}
				}else if (reactionStep instanceof SimpleReaction){
					for (int i = 0; i < rps.length; i++){
						if (rps[i] instanceof Reactant && rps[i].getSpeciesContext() == speciesContext) {
							return LINE_TYPE_NULL;
						}
					}
					return LINE_TYPE_REACTANT;
				}
			}
		}
		return LINE_TYPE_NULL;
	}

	public ReactionSlicesCartoon getReactionCartoon() {
		return reactionCartoon;
	}

	private ReactionStep[] getReactionStepArray(Shape shape, String menuAction) {
		if(shape instanceof ReactionStepShape){
			List<Shape> reactionStepShapeArr = getReactionCartoon().getSelectedShapes();
			if(reactionStepShapeArr != null && reactionStepShapeArr.size() > 0){
				ReactionStep[] rxStepsArr = new ReactionStep[reactionStepShapeArr.size()];
				for(int i=0;i<reactionStepShapeArr.size();i+= 1){
					rxStepsArr[i] = (ReactionStep)reactionStepShapeArr.get(i).getModelObject();
				}
				return rxStepsArr;
			}
		}
		return null;
	}

	public void layout(String layoutName) throws Exception {
		if (getReactionCartoon().getStructure() instanceof Membrane){
			if (GraphEmbeddingManager.RANDOMIZER.equals(layoutName)){
				getReactionCartoon().setRandomLayout(true);
				getGraphPane().repaint();
			}else{
				System.out.println(layoutName+" not yet implemented for Membranes");
			}
			saveDiagram();
			return;
		}

		// for non-membranes, use RPI's layout stuff
		edu.rpi.graphdrawing.Blackboard bb = new edu.rpi.graphdrawing.Blackboard();
		HashMap<String, Shape> nodeShapeMap = new HashMap<String, Shape>();
		// add nodes
		List<Shape> shapeEnum = getReactionCartoon().getShapes();
		for(Shape shape : shapeEnum) {
			edu.rpi.graphdrawing.Node newNode = null;
			if (shape instanceof SpeciesContextShape){
				newNode = bb.addNode(((SpeciesContextShape)shape).getLabel());
			}
			if (shape instanceof ReactionStepShape){
				newNode = bb.addNode(((ReactionStepShape)shape).getLabel());
			}
			// initialize node location to current absolute position
			if (newNode!=null){
				newNode.XY(shape.getSpaceManager().getAbsLoc().x, shape.getSpaceManager().getAbsLoc().y);
				nodeShapeMap.put(newNode.label(),shape);
			}
		}
		// add edges
		shapeEnum = getReactionCartoon().getShapes();
		for(Shape shape : shapeEnum) {
			if (shape instanceof ReactionParticipantShape){
				ReactionParticipantShape rpShape = (ReactionParticipantShape)shape;
				SpeciesContextShape scShape = (SpeciesContextShape) rpShape.getStartShape();
				ReactionStepShape rsShape = (ReactionStepShape) rpShape.getEndShape();
				if (rpShape instanceof ReactantShape){
					bb.addEdge(scShape.getLabel(),rsShape.getLabel());
				}else if (rpShape instanceof ProductShape){
					bb.addEdge(rsShape.getLabel(),scShape.getLabel());
				}else if (rpShape instanceof CatalystShape){
					bb.addEdge(scShape.getLabel(),rsShape.getLabel());
				}else if (rpShape instanceof FluxShape){
					//
					// check if coming or going
					//
					SpeciesContext sc = scShape.getSpeciesContext();
					if (sc.getStructure() == ((Membrane)rsShape.getReactionStep().getStructure()).getOutsideFeature()){
						bb.addEdge(scShape.getLabel(),rsShape.getLabel());
					}else{
						bb.addEdge(rsShape.getLabel(),scShape.getLabel());
					}
				}
			}
		}

		bb.setArea(0,0,getGraphPane().getWidth(),getGraphPane().getHeight());
		bb.globals.D(20);

		bb.addEmbedder(GraphEmbeddingManager.ANNEALER,new edu.rpi.graphdrawing.Annealer(bb));
		bb.addEmbedder(GraphEmbeddingManager.CIRCULARIZER,new edu.rpi.graphdrawing.Circularizer(bb));
		bb.addEmbedder(GraphEmbeddingManager.CYCLEIZER,new edu.rpi.graphdrawing.Cycleizer(bb));
		bb.addEmbedder(GraphEmbeddingManager.FORCEDIRECT,new edu.rpi.graphdrawing.ForceDirect(bb));
		bb.addEmbedder(GraphEmbeddingManager.LEVELLER,new edu.rpi.graphdrawing.Leveller(bb));
		bb.addEmbedder(GraphEmbeddingManager.RANDOMIZER,new edu.rpi.graphdrawing.Randomizer(bb));
		bb.addEmbedder(GraphEmbeddingManager.RELAXER,new edu.rpi.graphdrawing.Relaxer(bb));
		bb.addEmbedder(GraphEmbeddingManager.STABILIZER,new edu.rpi.graphdrawing.Stabilizer(bb));

		bb.setEmbedding(layoutName);

		@SuppressWarnings("unchecked")
		Vector<Node> nodeList = bb.nodes();
		for (int i = 0; i < nodeList.size(); i++){
			Node node = nodeList.elementAt(i);
			System.out.println("Node "+node.label()+" @ ("+node.x()+","+node.y()+")");
		}
		bb.PreprocessNodes();

		edu.rpi.graphdrawing.Embedder embedder = bb.embedder();
		embedder.Init();
		for (int i = 0; i < 1000; i++){
			embedder.Embed();
		}

		bb.removeDummies();
		@SuppressWarnings("unchecked")
		Vector<Node> nodes = bb.nodes();
		nodeList = nodes;
		// calculate offset and scaling so that resulting graph fits on canvas
		double lowX = 100000;
		double highX = -100000;
		double lowY = 100000;
		double highY = -100000;
		for (int i = 0; i < nodeList.size(); i++){
			Node node = nodeList.elementAt(i);
			lowX = Math.min(lowX,node.x());
			highX = Math.max(highX,node.x());
			lowY = Math.min(lowY,node.y());
			highY = Math.max(highY,node.y());
		}
		double scaleX = getGraphPane().getWidth()/(1.5*(highX-lowX));
		double scaleY = getGraphPane().getHeight()/(1.5*(highY-lowY));
		int offsetX = getGraphPane().getWidth()/6;
		int offsetY = getGraphPane().getHeight()/6;
		for (int i = 0; i < nodeList.size(); i++){
			Node node = nodeList.elementAt(i);
			Shape shape = (Shape)nodeShapeMap.get(node.label());
			Point parentLoc = shape.getParent().getSpaceManager().getAbsLoc();
			shape.getSpaceManager().setRelPos((int)(scaleX*(node.x()-lowX))+offsetX+parentLoc.x,(int)((scaleY*(node.y()-lowY))+offsetY+parentLoc.y));
			System.out.println("Shape "+shape.getLabel()+" @ "+shape.getSpaceManager().getAbsLoc());
		}

		getGraphPane().repaint();
		saveDiagram();
	}

	public void layoutGlg() throws Exception {
		//****In the case of Membranes DO as before!****
		if (getReactionCartoon().getStructure() instanceof Membrane) {
			getReactionCartoon().setRandomLayout(true);
			getGraphPane().repaint();
			saveDiagram();
			return;
		}
		//System.out.println("****************Begining of the layout code *********");
		//****For NON-membranes apply layout****
		//Create graph object
		com.genlogic.GraphLayout.GlgGraphLayout graph = new com.genlogic.GraphLayout.GlgGraphLayout();
		graph.SetUntangle(true); //true
		//specify dimensions for the graph! 400x400
		//System.out.println("H:"+getGraphPane().getHeight()+" W"+getGraphPane().getWidth());
		com.genlogic.GraphLayout.GlgCube graphDim = new com.genlogic.GraphLayout.GlgCube();
		com.genlogic.GraphLayout.GlgPoint newPoint = new com.genlogic.GraphLayout.GlgPoint(0,0,0);
		graphDim.p1 = newPoint;
		//newPoint = new com.genlogic.GlgPoint(getGraphPane().getWidth()-20, getGraphPane().getHeight()-10, 0);//400,400,0
		newPoint = new com.genlogic.GraphLayout.GlgPoint(1600,1600, 0);
		graphDim.p2 = newPoint;
		graph.dimensions = graphDim;
		//Add nodes (Vertex) to the graph
		List<Shape> shapeEnum = getReactionCartoon().getShapes();
		com.genlogic.GraphLayout.GlgGraphNode graphNode;
		HashMap<Shape, GlgGraphNode> nodeMap = new HashMap<Shape, GlgGraphNode>(); 
		for(Shape shape : shapeEnum) {
			//add to the graph			
			if (shape instanceof SpeciesContextShape) {
				graphNode = graph.AddNode(null, 0, null);
			} else if (shape instanceof ReactionStepShape) {
				graphNode = graph.AddNode(null, 0, null);
			} else {
				continue;
			}
			//add to the hashmap
			nodeMap.put(shape,graphNode);
		}
		//Add edges
		shapeEnum = getReactionCartoon().getShapes();
		for(Shape shape : shapeEnum) {		
			if (shape instanceof ReactionParticipantShape) {
				ReactionParticipantShape rpShape = (ReactionParticipantShape)shape;
				SpeciesContextShape scShape = (SpeciesContextShape)rpShape.getStartShape();
				ReactionStepShape rsShape =(ReactionStepShape)rpShape.getEndShape();
				if (rpShape instanceof ReactantShape) {
					graph.AddEdge((com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(scShape),(com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(rsShape),null, 0 ,null);
				} else if (rpShape instanceof ProductShape) {
					graph.AddEdge((com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(rsShape),(com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(scShape),null, 0 ,null);
				} else if (rpShape instanceof CatalystShape) {
					graph.AddEdge((com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(scShape),(com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(rsShape),null, 0 ,null);
				} else if (rpShape instanceof FluxShape) {
					//check if coming or going
					SpeciesContext sc = scShape.getSpeciesContext();
					if (sc.getStructure()== ((Membrane)rsShape.getReactionStep().getStructure()).getOutsideFeature()) {
						graph.AddEdge((com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(scShape),(com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(rsShape),null, 0 ,null);					
					} else {
						graph.AddEdge((com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(scShape),(com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(rsShape),null, 0 ,null);
					}
				} else {
					continue;
				}
			}
		}
		//call layout algorithm
		while (!graph.SpringIterate()) {
			;
		}
		graph.Update();
		//resize and scale the graph
		@SuppressWarnings("unchecked")
		Vector<GlgGraphEdge> edgeVector = graph.edge_array;
		double distance, minDistance = Double.MAX_VALUE;
		for (int i = 0; i < edgeVector.size(); i++){
			GlgGraphEdge edge = edgeVector.elementAt(i);
			distance = java.awt.geom.Point2D.distance(edge.start_node.display_position.x, edge.start_node.display_position.y, edge.end_node.display_position.x, edge.end_node.display_position.y);
			minDistance = distance<minDistance?distance:minDistance;
		}
		double ratio = 1.0;
		if (minDistance > 40) {
			ratio = 40.0/minDistance;
		}
		//Update positions
		shapeEnum = getReactionCartoon().getShapes();
		Point place;
		com.genlogic.GraphLayout.GlgPoint glgPoint;
		for(Shape shape : shapeEnum) {
			//test if it is contained in the nodeMap
			graphNode = (com.genlogic.GraphLayout.GlgGraphNode)nodeMap.get(shape);

			if (graphNode!= null) {
				glgPoint = graph.GetNodePosition(graphNode);
				//glgPoint = graphNode.display_position;
				place = new Point();
				place.setLocation(glgPoint.x*ratio+30, glgPoint.y*ratio+30);
				shape.getSpaceManager().setRelPos((int) (glgPoint.x*ratio+30), (int) (glgPoint.y*ratio+30));		
			}
		}	
		//	System.out.println("*************** END of the Layout code! **************");
		//getGraphPane().repaint();
		Dimension graphSize = new Dimension((int)(1600*ratio)+50,(int)(1600*ratio)+50);
		getGraphPane().setSize(graphSize);
		getGraphPane().setPreferredSize(graphSize);
		//update the window
		getGraphPane().invalidate();
		((JViewport)getGraphPane().getParent()).revalidate();
		saveDiagram();
	}

	protected void menuAction(Shape shape, String menuAction) {
		if(shape == null){ return; }
		if (menuAction.equals(CartoonToolMiscActions.Properties.MENU_ACTION)){
			if (shape instanceof FluxReactionShape){
				showFluxReactionPropertiesDialog((FluxReactionShape)shape);
			}else if (shape instanceof SimpleReactionShape){
				showSimpleReactionPropertiesDialog((SimpleReactionShape)shape);
			}else if (shape instanceof ReactantShape){
				Point locationOnScreen = shape.getSpaceManager().getAbsLoc();
				Point graphPaneLocation = getGraphPane().getLocationOnScreen();
				locationOnScreen.translate(graphPaneLocation.x,
						graphPaneLocation.y);
				showReactantPropertiesDialog((ReactantShape) shape,
						locationOnScreen);
			}else if (shape instanceof ProductShape){
				Point locationOnScreen = shape.getSpaceManager().getAbsLoc();
				Point graphPaneLocation = getGraphPane().getLocationOnScreen();
				locationOnScreen.translate(graphPaneLocation.x,
						graphPaneLocation.y);
				showProductPropertiesDialog((ProductShape) shape,
						locationOnScreen);
			}else if (shape instanceof SpeciesContextShape){
				showEditSpeciesDialog(getGraphPane(),getReactionCartoon().getModel(),((SpeciesContextShape)shape).getSpeciesContext());
			}else if (shape instanceof ReactionSliceContainerShape){
				ReactionSliceContainerShape rcs = (ReactionSliceContainerShape)shape;
				if (rcs.getStructure() instanceof Feature){
					// showFeaturePropertyDialog is invoked in two modes:
					// 1) parent!=null and child==null
					//      upon ok, it adds a new feature to the supplied parent.
					// 2) parent==null and child!=null
					//      upon ok, edits the feature name
					showFeaturePropertiesDialog(getGraphPane(),(getReactionCartoon().getModel() == null?null:getReactionCartoon().getModel()),null,(Feature)rcs.getStructure());
				}else if (rcs.getStructure() instanceof Membrane){
					showMembranePropertiesDialog(getGraphPane(),(Membrane)rcs.getStructure());
				}
			}
		}else if(menuAction.equals(CartoonToolMiscActions.AddSpecies.MENU_ACTION)){
			if(shape instanceof ReactionSliceContainerShape){
				showCreateSpeciesContextDialog(getGraphPane(),getReactionCartoon().getModel(),((ReactionSliceContainerShape)shape).getStructure(), null);
			}
		}else if(menuAction.equals(CartoonToolEditActions.Copy.MENU_ACTION)){
			if (shape instanceof SpeciesContextShape){
				Species species = ((SpeciesContextShape)shape).getSpeciesContext().getSpecies();
				VCellTransferable.sendToClipboard(species);
			}else if (shape instanceof ReactionStepShape){
				ReactionStep[] reactionStepArr = getReactionStepArray(shape,menuAction);
				if(reactionStepArr != null){
					VCellTransferable.sendToClipboard(reactionStepArr);
				}
			}		
		} else if (menuAction.equals(CartoonToolEditActions.Paste.MENU_ACTION) || 
				menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION)){
			if (shape instanceof ReactionSliceContainerShape){
				//See if Species
				Species species = (Species)VCellTransferable.getFromClipboard(VCellTransferable.SPECIES_FLAVOR);
				if(species != null){
					IdentityHashMap<Species, Species> speciesHash = new IdentityHashMap<Species, Species>();
					pasteSpecies(getGraphPane(), species,getReactionCartoon().getModel(),
							((ReactionSliceContainerShape)shape).getStructure(),
							menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION), speciesHash,null);
				}
				//See if ReactionStep[]
				ReactionStep[] reactionStepArr = (ReactionStep[])VCellTransferable.getFromClipboard(VCellTransferable.REACTIONSTEP_ARRAY_FLAVOR);
				if(reactionStepArr != null){
					try {
						pasteReactionSteps(reactionStepArr,getReactionCartoon().getModel(),
								((ReactionContainerShape)shape).getStructure(),
								menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION),
								getGraphPane(),null);
					} catch (Exception e) {
						e.printStackTrace(System.out);
						PopupGenerator.showErrorDialog(getGraphPane(), "Error while pasting reaction:\n" + e.getMessage());
					}
				}
			}
		} else if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION) || 
				menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)){
			try {
				if (shape instanceof ReactantShape || shape instanceof ProductShape || shape instanceof CatalystShape){
					ReactionParticipant reactionParticipant = ((ReactionParticipantShape)shape).getReactionParticipant();
					ReactionStep reactionStep = reactionParticipant.getReactionStep();
					reactionStep.removeReactionParticipant(reactionParticipant);
				}
				if (shape instanceof ReactionStepShape){
					ReactionStep[] reactionStepArr = getReactionStepArray(shape,menuAction);
					if(reactionStepArr != null){
						if (menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)){
							VCellTransferable.sendToClipboard(reactionStepArr);
						}
					}
					for(int i = 0;i<reactionStepArr.length;i+= 1){
						getReactionCartoon().getModel().removeReactionStep(reactionStepArr[i]);
					}
				}
				if (shape instanceof SpeciesContextShape){
					getReactionCartoon().getModel().removeSpeciesContext(((SpeciesContextShape)shape).getSpeciesContext());
					if (menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)){
						VCellTransferable.sendToClipboard(((SpeciesContextShape)shape).getSpeciesContext().getSpecies());
					}
				}
			}catch (java.beans.PropertyVetoException e){
				PopupGenerator.showErrorDialog(getGraphPane(), e.getMessage());
			}catch (Exception e){
				PopupGenerator.showErrorDialog(getGraphPane(), e.getMessage());
			}

		}else if (menuAction.equals(CartoonToolMiscActions.SearchReactions.MENU_ACTION)){
			try{
				if(shape instanceof ReactionSliceContainerShape){
					showReactionBrowserDialog(getReactionCartoon(),((ReactionSliceContainerShape)shape).getStructure(),null);
				}
			}catch(Exception e){
				PopupGenerator.showErrorDialog(getGraphPane(), e.getMessage());
			}
		} else if (menuAction.equals(CartoonToolSaveAsImageActions.HighRes.MENU_ACTION) || 
				menuAction.equals(CartoonToolSaveAsImageActions.MedRes.MENU_ACTION) ||
				menuAction.equals(CartoonToolSaveAsImageActions.LowRes.MENU_ACTION)) { 
			try {
				String resType = null;
				if (menuAction.equals(CartoonToolSaveAsImageActions.HighRes.MENU_ACTION)) {
					resType = ITextWriter.HIGH_RESOLUTION;
				} else if (menuAction.equals(CartoonToolSaveAsImageActions.MedRes.MENU_ACTION)) {
					resType = ITextWriter.MEDIUM_RESOLUTION;
				} else if (menuAction.equals(CartoonToolSaveAsImageActions.LowRes.MENU_ACTION)) {
					resType = ITextWriter.LOW_RESOLUTION;
				}
				if(shape instanceof ReactionSliceContainerShape){
					showSaveReactionImageDialog(((ReactionSliceContainerShape)shape).getStructure(), resType);
				}
			} catch(Exception e) {
				PopupGenerator.showErrorDialog(getGraphPane(), e.getMessage());
			}
		} else if(menuAction.equals(CartoonToolMiscActions.Annotate.MENU_ACTION)){
			if(shape instanceof ReactionStepShape){
				//MIRIAMHelper.showMIRIAMAnnotationDialog(((SimpleReactionShape)shape).getReactionStep());
				//System.out.println("Menu action annotate activated...");
				ReactionStep rs = ((ReactionStepShape)shape).getReactionStep();
				VCMetaData vcMetaData = rs.getModel().getVcMetaData();
				try{
					String newAnnotation = DialogUtils.showAnnotationDialog(getGraphPane(), vcMetaData.getFreeTextAnnotation(rs));
					vcMetaData.setFreeTextAnnotation(rs, newAnnotation);
				}catch(UtilCancelException e){
					//Do Nothing
				}catch (Throwable exc) {
					exc.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(getGraphPane(), "Failed to edit annotation!\n"+exc.getMessage());
				}
			}
		}else{
			// default action is to ignore
			System.out.println("unsupported menu action '"+menuAction+"' on shape '"+shape+"'");
		}

	}

	public void mouseClicked(MouseEvent event) {
		Point screenPoint = new Point(event.getX(),event.getY());
		Point worldPoint = screenToWorld(screenPoint);
		try {
			// if right mouse button, then do popup menu
			if ((event.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) != 0){
				return;
			}
			switch (mode) {
			case SELECT: {
				if (event.getClickCount()==2){
					Shape selectedShape = getReactionCartoon().getSelectedShape();
					if (selectedShape != null){
						menuAction(selectedShape, CartoonToolMiscActions.Properties.MENU_ACTION);
					}
				}
				break;		
			}	
			case STEP: {
				Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);	
				if (pickedShape instanceof ReactionSliceContainerShape){
					Structure structure = ((ReactionSliceContainerShape)pickedShape).getStructure();
					//					if (structure == getReactionCartoon().getStructure()){
					String newReactionStepName = getReactionCartoon().getModel().getFreeReactionStepName();
					ReactionStep reactionStep = new SimpleReaction(structure,newReactionStepName);
					getReactionCartoon().getModel().addReactionStep(reactionStep);
					ReactionStepShape rsShape = (ReactionStepShape)getReactionCartoon().getShapeFromModelObject(reactionStep);
					Point parentLocation = rsShape.getParent().getSpaceManager().getAbsLoc();
					rsShape.getSpaceManager().setRelPos(worldPoint.x-parentLocation.x,worldPoint.y-parentLocation.y);
					saveDiagram();
				}
				break;
			}	
			case FLUX: {
				Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);	
				if (pickedShape instanceof ReactionSliceContainerShape){
					Structure structure = ((ReactionSliceContainerShape)pickedShape).getStructure();
					if (structure instanceof Membrane){
						Membrane membrane = (Membrane)structure;
						String newFluxReactionName = getReactionCartoon().getModel().getFreeFluxReactionName();
						FluxReaction fluxReaction = new FluxReaction(membrane,null,getReactionCartoon().getModel(),newFluxReactionName);
						fluxReaction.setKinetics(new GeneralKinetics(fluxReaction));
						getReactionCartoon().getModel().addReactionStep(fluxReaction);
						ReactionStepShape frShape = (ReactionStepShape)getReactionCartoon().getShapeFromModelObject(fluxReaction);
						Point parentLocation = frShape.getParent().getSpaceManager().getAbsLoc();
						frShape.getSpaceManager().setRelPos(worldPoint.x-parentLocation.x,worldPoint.y-parentLocation.y);
						saveDiagram();
						Shape shape = getReactionCartoon().getShapeFromModelObject(fluxReaction);
						showFluxReactionPropertiesDialog((FluxReactionShape)shape);
					}
				}
				break;
			}	
			case SPECIES: {
				Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);	
				if (pickedShape instanceof ReactionSliceContainerShape){
					Point parentLocation = pickedShape.getSpaceManager().getAbsLoc();
					Point scShapeLocation = new Point(worldPoint.x-parentLocation.x,worldPoint.y-parentLocation.y);
					showCreateSpeciesContextDialog(getGraphPane(),getReactionCartoon().getModel(),((ReactionSliceContainerShape)pickedShape).getStructure(),scShapeLocation);
				}
			}
			default:
				break;
			}	
		}catch (Exception e){
			System.out.println("CartoonTool.mouseClicked: uncaught exception");
			e.printStackTrace(System.out);
			Point canvasLoc = getGraphPane().getLocationOnScreen();
			canvasLoc.x += screenPoint.x;
			canvasLoc.y += screenPoint.y;
			PopupGenerator.showErrorDialog(getGraphPane(), e.getMessage());
		}				
	}

	public void mouseDragged(MouseEvent event) {
		if ((event.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) != 0){
			return;
		}
		boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
		boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
		try {
			switch (mode){
			case SELECT: {
				Point worldPoint = screenToWorld(event.getX(),event.getY());
				if (bMoving){
					List<Shape> selectedShapes = getReactionCartoon().getSelectedShapes();
					// constrain to stay within the corresponding parent for the "movingShape" as well as all other selected (hence moving) shapes.
					Point movingParentLoc = movingShape.getParent().getSpaceManager().getAbsLoc();
					Dimension movingParentSize = movingShape.getParent().getSpaceManager().getSize();
					worldPoint.x = Math.max(movingOffsetWorld.x + movingParentLoc.x,
							Math.min(movingOffsetWorld.x + movingParentLoc.x + movingParentSize.width - 
									movingShape.getSpaceManager().getSize().width, worldPoint.x));
					worldPoint.y = Math.max(movingOffsetWorld.y + movingParentLoc.y,
							Math.min(movingOffsetWorld.x + movingParentLoc.y + movingParentSize.height - 
									movingShape.getSpaceManager().getSize().height, worldPoint.y));
					for(Shape shape : selectedShapes) {
						if (shape != movingShape){
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
					getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					Point newMovingPoint = new Point(worldPoint.x-movingOffsetWorld.x,worldPoint.y-movingOffsetWorld.y);
					int deltaX = newMovingPoint.x - movingPointWorld.x;
					int deltaY = newMovingPoint.y - movingPointWorld.y;
					movingPointWorld = newMovingPoint;
					movingShape.getSpaceManager().setRelPos(movingPointWorld.x - movingParentLoc.x,
							movingPointWorld.y - movingParentLoc.y);
					// for any other "movable" shapes that are selected, move them also
					for(Shape shape : selectedShapes) {
						if (shape != movingShape){
							shape.getSpaceManager().setRelPos(
									shape.getSpaceManager().getRelPos().x + deltaX, 
									shape.getSpaceManager().getRelPos().y + deltaY);
						}
					}
					getGraphPane().invalidate();
					((JViewport)getGraphPane().getParent()).revalidate();
					getGraphPane().repaint();
				}else if (bRectStretch){
					// constain to stay within parent
					Point parentLoc = rectShape.getParent().getSpaceManager().getAbsLoc();
					Dimension parentSize = rectShape.getParent().getSpaceManager().getSize();
					worldPoint.x = Math.max(1,Math.min(parentSize.width-1,worldPoint.x-parentLoc.x)) + parentLoc.x;
					worldPoint.y = Math.max(1,Math.min(parentSize.height-1,worldPoint.y-parentLoc.y)) + parentLoc.y;
					getGraphPane().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					//getGraphPane().repaint();
					Graphics2D g = (Graphics2D)getGraphPane().getGraphics();
					java.awt.geom.AffineTransform oldTransform = g.getTransform();
					g.scale(0.01*getReactionCartoon().getZoomPercent(),0.01*getReactionCartoon().getZoomPercent());
					g.setXORMode(Color.white);
					rectShape.setEnd(endPointWorld);
					rectShape.paint(g,0,0);
					endPointWorld = worldPoint;
					rectShape.setEnd(endPointWorld);
					rectShape.paint(g,0,0);
					g.setTransform(oldTransform);
				}else{
					Shape shape = (getGraphModel().getSelectedShape() != null?getGraphModel().getSelectedShape():getReactionCartoon().pickWorld(worldPoint));
					if (!bCntrl && !bShift && (shape instanceof SpeciesContextShape || 
							shape instanceof ReactionStepShape ||
							(shape instanceof ReactionSliceContainerShape && worldPoint.y<25))){
						bMoving=true;
						movingShape = shape;
						movingPointWorld = shape.getSpaceManager().getAbsLoc();
						movingOffsetWorld = new Point(worldPoint.x-movingPointWorld.x,worldPoint.y-movingPointWorld.y);
						if (movingShape instanceof ReactionSliceContainerShape){
							((ReactionSliceContainerShape)movingShape).isBeingDragged = true;
						}
					}else if (shape instanceof ReactionSliceContainerShape || bShift || bCntrl){
						bRectStretch = true;
						endPointWorld = new Point(worldPoint.x+1,worldPoint.y+1);
						rectShape = new RubberBandRectShape(worldPoint,endPointWorld,getReactionCartoon());
						rectShape.setEnd(endPointWorld);
						if(!(shape instanceof ReactionSliceContainerShape)){
							shape.getParent().addChildShape(rectShape);
						}else{
							shape.addChildShape(rectShape);
						}
						Graphics2D g = (Graphics2D)getGraphPane().getGraphics();
						java.awt.geom.AffineTransform oldTransform = g.getTransform();
						g.scale(0.01*getReactionCartoon().getZoomPercent(),0.01*getReactionCartoon().getZoomPercent());
						g.setXORMode(Color.white);
						rectShape.paint(g,0,0);
						g.setTransform(oldTransform);
					}		
				}		
				break;
			}
			case LINEDIRECTED: case LINECATALYST: {
				int x = event.getX();
				int y = event.getY();
				Point worldPoint = new Point((int)(x*100.0/getReactionCartoon().getZoomPercent()),(int)(y*100.0/getReactionCartoon().getZoomPercent()));
				if (bLineStretch){
					// repaint last location with XOR
					Graphics2D g = (Graphics2D)getGraphPane().getGraphics();
					g.setXORMode(Color.white);
					edgeShape.setEnd(endPointWorld);
					java.awt.geom.AffineTransform oldTransform = g.getTransform();
					g.scale(getReactionCartoon().getZoomPercent()*0.01,getReactionCartoon().getZoomPercent()*0.01);
					edgeShape.paint_NoAntiAlias(g,0,0);
					g.setTransform(oldTransform);
					// set label and color for line depending on attachment area on ReactionStepShape
					int lineType;
					if(Mode.LINECATALYST.equals(mode)) {
						lineType = LINE_TYPE_CATALYST;
					} else if (edgeShape.getStartShape() instanceof SpeciesContextShape){
						lineType = LINE_TYPE_REACTANT;
					} else {
						lineType = LINE_TYPE_PRODUCT;						
					}
					edgeShape.setLabel(lineLabels[lineType]);
					edgeShape.setForgroundColor(lineColors[lineType]);
					getGraphPane().setCursor(lineCursors[lineType]);
					// move line and paint with XOR
					endPointWorld = worldPoint;
					edgeShape.setEnd(worldPoint);
					oldTransform = g.getTransform();
					g.scale(getReactionCartoon().getZoomPercent()*0.01,getReactionCartoon().getZoomPercent()*0.01);
					edgeShape.paint_NoAntiAlias(g,0,0);
					g.setTransform(oldTransform);
				}else{
					if (edgeShape != null){
						return;
					}	
					Shape startShape = getReactionCartoon().pickWorld(worldPoint);
					if (startShape instanceof SpeciesContextShape || startShape instanceof SimpleReactionShape || startShape instanceof FluxReactionShape){
						ElipseShape startEllipseShape = (ElipseShape)startShape;
						bLineStretch = true;
						endPointWorld = worldPoint;
						edgeShape = new RubberBandEdgeShape(startEllipseShape,null,getReactionCartoon());
						edgeShape.setEnd(worldPoint);
						Graphics2D g = (Graphics2D)getGraphPane().getGraphics();
						g.setXORMode(Color.white);
						java.awt.geom.AffineTransform oldTransform = g.getTransform();
						g.scale(getReactionCartoon().getZoomPercent()*0.01,getReactionCartoon().getZoomPercent()*0.01);
						edgeShape.paint_NoAntiAlias(g,0,0);
						g.setTransform(oldTransform);
					}	
				}		
				break;
			}
			default: {
				break;
			}
			}		
		}catch (Exception e){
			System.out.println("CartoonTool.mouseDragged: uncaught exception");
			e.printStackTrace(System.out);
		}			
	}

	public void mousePressed(MouseEvent event) {
		if(getReactionCartoon() == null){return;}
		try {
			int eventX = event.getX();
			int eventY = event.getY();
			Point worldPoint = new Point((int)(eventX*100.0/getReactionCartoon().getZoomPercent()),(int)(eventY*100.0/getReactionCartoon().getZoomPercent()));
			//Always select with MousePress
			boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
			boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
			if(mode == Mode.SELECT || (event.getModifiers() & MouseEvent.BUTTON1_MASK) != 0){
				selectEventFromWorld(worldPoint,bShift,bCntrl);
			}
			// if mouse popupMenu event, popup menu
			if (event.isPopupTrigger() && mode == Mode.SELECT){
				popupMenu(getReactionCartoon().getSelectedShape(),eventX,eventY);
				return;
			}
		}catch (Exception e){
			System.out.println("CartoonTool.mousePressed: uncaught exception");
			e.printStackTrace(System.out);
		}				
	}

	public void mouseReleased(MouseEvent event) {
		if(getReactionCartoon() == null){return;}
		try {
			//Pick shape
			int eventX = event.getX();
			int eventY = event.getY();
			Point worldPoint = new Point((int)(eventX*100.0/getReactionCartoon().getZoomPercent()),(int)(eventY*100.0/getReactionCartoon().getZoomPercent()));
			Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);
			// if mouse popupMenu event, popup menu
			if (event.isPopupTrigger() && mode == Mode.SELECT){
				popupMenu(getReactionCartoon().getSelectedShape(),event.getX(),event.getY());
				return;
			}
			if ((event.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) != 0){
				return;
			}
			// else do select and move
			switch (mode){
			case SELECT:{
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bMoving){
					if (movingShape instanceof ReactionSliceContainerShape){
						((ReactionSliceContainerShape)movingShape).isBeingDragged = false;
					}
					getGraphPane().invalidate();
					((JViewport)getGraphPane().getParent()).revalidate();
					getGraphPane().repaint();
					saveDiagram();
				}else if (bRectStretch){
					Point absLoc = rectShape.getSpaceManager().getRelPos();
					Dimension size = rectShape.getSpaceManager().getSize();
					// remove temporary rectangle
					getReactionCartoon().removeShape(rectShape);
					rectShape = null;
					Rectangle rect = new Rectangle(absLoc.x,absLoc.y,size.width,size.height);
					boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
					boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
					selectEventFromWorld(rect,bShift,bCntrl);
					getGraphPane().repaint();
				}
				bMoving=false;
				movingShape=null;
				bRectStretch=false;
				rectShape=null;
				break;
			}
			case LINEDIRECTED: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bLineStretch){
					bLineStretch = false;
					// set label and color for line depending on which shape the edge started. (rather than attachment area on ReactionStepShape)
					int lineType = getLineTypeFromWorld(edgeShape.getStartShape(),worldPoint);
					if (pickedShape instanceof SimpleReactionShape){
						SimpleReaction simpleReaction = (SimpleReaction)pickedShape.getModelObject();
						Object startShapeObject = edgeShape.getStartShape().getModelObject();
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
						}
						// add reactionParticipant to model
					} else if (pickedShape instanceof SpeciesContextShape) {
						SpeciesContext speciesContext = (SpeciesContext) pickedShape.getModelObject();
						Object startShapeObject = edgeShape.getStartShape().getModelObject();
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
						}
					} else if (pickedShape instanceof FluxReactionShape){
						FluxReaction fluxReaction = (FluxReaction)pickedShape.getModelObject();
						switch (lineType){
						case LINE_TYPE_FLUX:{
							// assure that there are the appropriate speciesContexts
							Membrane membrane = (Membrane)fluxReaction.getStructure();
							Feature feature = membrane.getOutsideFeature();
							SpeciesContext speciesContext = null;
							if (edgeShape.getStartShape() instanceof SpeciesContextShape){
								speciesContext = 
									(SpeciesContext)edgeShape.getStartShape().getModelObject();
							}else if (edgeShape.getEndShape() instanceof SpeciesContextShape){
								speciesContext = 
									(SpeciesContext)edgeShape.getEndShape().getModelObject();
							}
							SpeciesContext sc = reactionCartoon.getModel().getSpeciesContext(speciesContext.getSpecies(),feature);
							if (sc==null){
								reactionCartoon.getModel().addSpeciesContext(speciesContext.getSpecies(),feature);
							}	
							feature = membrane.getInsideFeature();
							sc = reactionCartoon.getModel().getSpeciesContext(speciesContext.getSpecies(),feature);
							if (sc==null){
								reactionCartoon.getModel().addSpeciesContext(speciesContext.getSpecies(),feature);
							}	

							fluxReaction.setFluxCarrier(speciesContext.getSpecies(),reactionCartoon.getModel());
							getReactionCartoon().notifyChangeEvent();
							setMode(Mode.SELECT);
							break;
						}
						case LINE_TYPE_NULL:{
							getGraphPane().repaint();
							break;
						}
						}
						// remove temporary edge
						getReactionCartoon().removeShape(edgeShape);
						edgeShape = null;

					}else{
						getGraphPane().repaint();
					}
				}
				break;
			}
			case LINECATALYST: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bLineStretch){
					bLineStretch = false;
					// set label and color for line depending on which shape the edge started. (rather than attachment area on ReactionStepShape)
					Object startObject = edgeShape.getStartShape().getModelObject();
					Object pickedObject = pickedShape.getModelObject();
					ReactionStep reactionStep = null;
					SpeciesContext speciesContext = null;
					if(startObject instanceof ReactionStep && pickedObject instanceof SpeciesContext) {
						reactionStep = (ReactionStep) startObject;
						speciesContext = (SpeciesContext) pickedObject;
					} else if(startObject instanceof SpeciesContext && 
							pickedObject instanceof ReactionStep) {
						speciesContext = (SpeciesContext) startObject;						
						reactionStep = (ReactionStep) pickedObject;
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
			default:{
				break;
			}
			}
		}catch (Exception e){
			System.out.println("CartoonTool.mouseReleased: uncaught exception");
			e.printStackTrace(System.out);
		}			

	}

	public void saveDiagram() throws Exception {
		getReactionCartoon().setPositionsFromReactionCartoon(getReactionCartoon().getModel().getDiagram(getReactionCartoon().getStructure()));
	}

	private void selectEventFromWorld(Point worldPoint, boolean bShift, boolean bCntrl) {
		if(getReactionCartoon() == null){return;}
		if (!bShift && !bCntrl){
			Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);
			if (pickedShape == null || !pickedShape.isSelected()){
				getReactionCartoon().clearSelection();
			}
			if (pickedShape != null && pickedShape.isSelected()){
				return;
			}
			if(pickedShape != null){
				getReactionCartoon().select(pickedShape);
			}
		}else if (bShift){
			Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);
			if (pickedShape==null){
				return;
			}
			if (pickedShape instanceof ReactionSliceContainerShape){
				return;
			}
			if(getReactionCartoon().getSelectedShape() instanceof ReactionSliceContainerShape){
				getReactionCartoon().clearSelection();
			}
			getReactionCartoon().select(pickedShape);
		}else if (bCntrl){
			Shape pickedShape = getReactionCartoon().pickWorld(worldPoint);
			if (pickedShape==null){
				return;
			}
			if (pickedShape instanceof ReactionSliceContainerShape){
				return;
			}
			if (pickedShape.isSelected()){
				getReactionCartoon().deselect(pickedShape);
			}else{
				getReactionCartoon().select(pickedShape);
			}
		}
	}

	private void selectEventFromWorld(Rectangle rect, boolean bShift, boolean bCntrl) {
		if (!bShift && !bCntrl){
			getReactionCartoon().clearSelection();
			List<Shape> shapes = getReactionCartoon().pickWorld(rect);
			for(Shape shape : shapes) {
				if (shape instanceof ElipseShape){
					getReactionCartoon().select(shape);
				}
			}
		}else if (bShift){
			if(getReactionCartoon().getSelectedShape() instanceof ReactionSliceContainerShape){
				getReactionCartoon().clearSelection();
			}
			List<Shape> shapes = getReactionCartoon().pickWorld(rect);
			for(Shape shape : shapes) {
				if (shape instanceof ElipseShape){
					getReactionCartoon().select(shape);
				}
			}
		}else if (bCntrl){
			if(getReactionCartoon().getSelectedShape() instanceof ReactionSliceContainerShape){
				getReactionCartoon().clearSelection();
			}
			List<Shape> shapes = getReactionCartoon().pickWorld(rect);
			for(Shape shape : shapes) {
				if (shape instanceof ElipseShape){
					if (shape.isSelected()){
						getReactionCartoon().deselect(shape);
					}else{
						getReactionCartoon().select(shape);
					}
				}
			}
		}
	}

	public void setReactionCartoon(ReactionSlicesCartoon newReactionCartoon) {
		reactionCartoon = newReactionCartoon;
	}

	public boolean shapeHasMenuAction(Shape shape, String menuAction) {
		if (menuAction.equals(CartoonToolMiscActions.Annotate.MENU_ACTION)){
			if (shape instanceof ReactionStepShape){
				return true;
			}
		}
		if (menuAction.equals(CartoonToolEditActions.Copy.MENU_ACTION)){
			if (shape instanceof SpeciesContextShape ||
					shape instanceof ReactionStepShape){
				return true;
			}
		}
		if (menuAction.equals(CartoonToolEditActions.Paste.MENU_ACTION) || 
				menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION)){
			if (shape instanceof ReactionSliceContainerShape){
				return true;
			}
		}
		if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)){
			if (shape instanceof ReactionStepShape ||
					shape instanceof ReactantShape || 
					shape instanceof ProductShape || 
					shape instanceof CatalystShape){
				return true;
			}
		}
		if (menuAction.equals(CartoonToolEditActions.Cut.MENU_ACTION)){
			if (shape instanceof SpeciesContextShape ||
					shape instanceof ReactionStepShape){
				return true;
			}
		}

		if (menuAction.equals(CartoonToolMiscActions.AddSpecies.MENU_ACTION)){
			if (shape instanceof ReactionSliceContainerShape){
				return true;
			}
		}
		if (menuAction.equals(CartoonToolMiscActions.SearchReactions.MENU_ACTION)){
			if (shape instanceof ReactionSliceContainerShape){
				return true;
			}
		}

		if (menuAction.equals(CartoonToolSaveAsImageActions.HighRes.MENU_ACTION) || 
				menuAction.equals(CartoonToolSaveAsImageActions.MedRes.MENU_ACTION) ||
				menuAction.equals(CartoonToolSaveAsImageActions.LowRes.MENU_ACTION)) {       
			if (shape instanceof ReactionSliceContainerShape){
				return true;
			}
		}
		if (menuAction.equals(CartoonToolMiscActions.Properties.MENU_ACTION)){
			if (shape instanceof ReactionStepShape || 
					shape instanceof SpeciesContextShape || 
					shape instanceof ReactantShape || 
					shape instanceof ProductShape || 
					shape instanceof CatalystShape ||
					shape instanceof ReactionSliceContainerShape){
				return true;
			}
		}
		return false;
	}

	public boolean shapeHasMenuActionEnabled(Shape shape, java.lang.String menuAction) {
		if(menuAction.equals(CartoonToolMiscActions.Properties.MENU_ACTION)){
			if(shape instanceof CatalystShape){
				return false;
			}
		}
		if (menuAction.equals(CartoonToolMiscActions.SearchReactions.MENU_ACTION)){
			if (!(shape instanceof ReactionSliceContainerShape) ||
					!shape.getModelObject().equals(getReactionCartoon().getStructure())){
				return false;
			}
		}
		if (shape instanceof ReactionSliceContainerShape){
			boolean bPasteNew = menuAction.equals(CartoonToolEditActions.PasteNew.MENU_ACTION);
			boolean bPaste = menuAction.equals(CartoonToolEditActions.Paste.MENU_ACTION);
			if(bPaste || bPasteNew){
				//Paste if there is a species on the system clipboard and it doesn't exist in structure
				Species species = (Species)VCellTransferable.getFromClipboard(VCellTransferable.SPECIES_FLAVOR);
				if(species != null){
					if(getReactionCartoon().getModel().contains(species)) {
						if (getReactionCartoon().getModel().getSpeciesContext(species,((ReactionSliceContainerShape)shape).getStructure()) != null) {
							return bPasteNew ? true : false;
						} else {
							return bPasteNew ? false : true;
						}
					} else {
						return bPasteNew ? false : true;
					}
				}
				//Paste if there is a ReactionStepArr on the system clipboard and structure types match
				ReactionStep[] reactionStepArr = (ReactionStep[])VCellTransferable.getFromClipboard(VCellTransferable.REACTIONSTEP_ARRAY_FLAVOR);
				if(reactionStepArr != null){
					Structure targetStructure = ((ReactionContainerShape)shape).getStructure();
					for(int i=0;i<reactionStepArr.length;i+= 1){
						if(!reactionStepArr[i].getStructure().getClass().equals(targetStructure.getClass())){
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
		if(getReactionCartoon() == null){
			return;
		}
		JFrame parent = (JFrame)BeanUtils.findTypeParentOfComponent(getGraphPane(), JFrame.class);
		FluxReaction_Dialog fluxReaction_Dialog = new FluxReaction_Dialog(parent,true);
		fluxReaction_Dialog.init(fluxReactionShape.getFluxReaction(), getReactionCartoon().getModel());
		fluxReaction_Dialog.setTitle("Flux Reaction Editor");
		ZEnforcer.showModalDialogOnTop(fluxReaction_Dialog, getJDesktopPane());
		// update in case of name change (should really be a listener)
		fluxReactionShape.refreshLabel();
		getReactionCartoon().fireGraphChanged();
	}

	public void showProductPropertiesDialog(ProductShape productShape, Point location) {
		if(getReactionCartoon() == null || getDialogOwner(getGraphPane()) == null){
			return;
		}
		Product product = (Product)productShape.getModelObject();
		String typed = JOptionPane.showInputDialog(getDialogOwner(getGraphPane()), "Current stoichiometry is: " + product.getStoichiometry(), "Input stoichiometry", JOptionPane.QUESTION_MESSAGE);
		if (typed != null) {
			try {
				product.setStoichiometry(Integer.parseInt(typed));
				productShape.refreshLabel();
				getReactionCartoon().fireGraphChanged();
			} catch (NumberFormatException exc) {
				JOptionPane.showMessageDialog(getDialogOwner(getGraphPane()), "You did not type a valid number", "Error:", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void showReactantPropertiesDialog(ReactantShape reactantShape, Point location) {
		if(getReactionCartoon() == null || getDialogOwner(getGraphPane()) == null){
			return;
		}
		Reactant reactant = (Reactant)reactantShape.getModelObject();
		String typed = JOptionPane.showInputDialog(getDialogOwner(getGraphPane()), "Current stoichiometry is: " + reactant.getStoichiometry(), "Input stoichiometry", JOptionPane.QUESTION_MESSAGE);
		if (typed != null) {
			try {
				reactant.setStoichiometry(Integer.parseInt(typed));
				reactantShape.refreshLabel();
				getReactionCartoon().fireGraphChanged();
			} catch (NumberFormatException exc) {
				JOptionPane.showMessageDialog(getDialogOwner(getGraphPane()), "You did not type a valid number", "Error:", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void showReactionBrowserDialog(ReactionSlicesCartoon sCartoon, Structure struct,Point location) throws Exception{
		if(getReactionCartoon() == null || getDocumentManager() == null || getDialogOwner(getGraphPane()) == null){
			return;
		}
		JInternalFrameEnhanced jif = new JInternalFrameEnhanced("Create Reaction within structure '"+struct.getName()+"'",true,true);
		DBReactionWizardPanel dbrqWiz = new DBReactionWizardPanel();
		dbrqWiz.setModel(getReactionCartoon().getModel());
		dbrqWiz.setStructure(struct);
		dbrqWiz.setDocumentManager(getDocumentManager());
		jif.setContentPane(dbrqWiz);
		if(location != null){
			jif.setLocation(location);
		}
		getDialogOwner(getGraphPane()).add(jif, JDesktopPane.MODAL_LAYER);
		jif.pack();
		BeanUtils.centerOnComponent(jif, getDialogOwner(getGraphPane()));
		jif.show();
	}

	//TO DO: allow user preferences for directory selection. 
	public void showSaveReactionImageDialog(Structure struct, String resLevel) throws Exception {
		if (struct == null || getReactionCartoon().getModel() == null) {             //or throw exception?
			System.err.println("Insufficient params for generating reactions image.");
			return;
		}
		if (resLevel == null) {                                //default resolution.
			resLevel = ITextWriter.HIGH_RESOLUTION;
		}
		Model model = getReactionCartoon().getModel();
		System.out.println("Processing save as Image request for: " + struct.getName() + " " + model.getName() + "(" + resLevel + ")");
		//set file filter
		SimpleFilenameFilter gifFilter = new SimpleFilenameFilter("gif");
		final java.io.File defaultFile = new java.io.File(model.getName() + "_" + struct.getName() + ".gif");
		ClientServerManager csm = (ClientServerManager)getDocumentManager().getSessionManager();
		UserPreferences userPref = csm.getUserPreferences();
		String defaultPath = userPref.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
		VCFileChooser fileChooser = new VCFileChooser(defaultPath);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(gifFilter);
		fileChooser.setSelectedFile(defaultFile);
		fileChooser.setDialogTitle("Save Image As...");
		//a hack to fix the jdk 1.2 problem (?) of losing the selected file name once the user changes the directory.
		class FileChooserFix implements java.beans.PropertyChangeListener {
			public void propertyChange(java.beans.PropertyChangeEvent ev) {    
				JFileChooser chooser = (JFileChooser)ev.getSource();       
				if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(ev.getPropertyName())) {      
					chooser.setSelectedFile(defaultFile);     
				}  
			} 
		}
		fileChooser.addPropertyChangeListener(new FileChooserFix());
		//process user input
		if (fileChooser.showSaveDialog(getDialogOwner(getGraphPane())) == JFileChooser.APPROVE_OPTION) {
			java.io.File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				if (selectedFile.exists()) {
					int question = javax.swing.JOptionPane.showConfirmDialog(getDialogOwner(getGraphPane()), 
							"Overwrite file: " + selectedFile.getPath() + "?");
					if (question == javax.swing.JOptionPane.NO_OPTION || question == javax.swing.JOptionPane.CANCEL_OPTION) {
						return;
					}
				}
				//System.out.println("Saving reactions image to file: " + selectedFile.toString());
				getDocumentManager().generateReactionsImage(model, struct, resLevel, new java.io.FileOutputStream(selectedFile));
				//reset the user preference for the default path, if needed.
				String newPath = selectedFile.getParent();
				if (!newPath.equals(defaultPath)) {
					userPref.setGenPref(UserPreferences.GENERAL_LAST_PATH_USED, newPath);
				}
			}
		} 
	}

	public void showSimpleReactionPropertiesDialog(SimpleReactionShape simpleReactionShape) {
		JFrame parent = (JFrame)BeanUtils.findTypeParentOfComponent(getGraphPane(), JFrame.class);
		SimpleReactionPanelDialog simpleReactionDialog = new SimpleReactionPanelDialog(parent,true);
		simpleReactionDialog.setSimpleReaction(simpleReactionShape.getSimpleReaction());
		simpleReactionDialog.setTitle("Reaction Kinetics Editor");
		ZEnforcer.showModalDialogOnTop(simpleReactionDialog, getJDesktopPane());
		//cleanup listeners after window closed for GC
		simpleReactionDialog.cleanupOnClose();
		// update in case of name change (should really be a listener)
		simpleReactionShape.refreshLabel();
		getReactionCartoon().fireGraphChanged();
	}

	public void updateMode(Mode newMode) {
		if (newMode==mode){
			return;
		}
		if ((newMode == Mode.FLUX) && (getReactionCartoon() != null) && (!(getReactionCartoon().getStructure() instanceof Membrane))){
			setMode(mode);
			return;
		}
		bMoving = false;
		movingShape = null;
		bRectStretch = false;
		rectShape = null;
		bLineStretch = false;
		edgeShape = null;
		endPointWorld = null;
		if(getReactionCartoon() != null){
			getReactionCartoon().clearSelection();
		}
		this.mode = newMode;
		if(getGraphPane() != null){
			switch (mode){
			case LINEDIRECTED: case LINECATALYST: case STEP: case FLUX:{
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
}