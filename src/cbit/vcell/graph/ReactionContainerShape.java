package cbit.vcell.graph;

/*  A container shape for a reaction network, typically for one structure
 *  October 2010
 */

import java.awt.Color;
import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Structure;

public class ReactionContainerShape extends ContainerShape {

	protected Structure structure = null;
	protected StructureSuite structureSuite;
	public boolean isBeingDragged = false;

	public ReactionContainerShape(Structure structure, StructureSuite structureSuite, 
			GraphModel graphModel) {
		super(graphModel);
		this.structure = structure;
		this.structureSuite = structureSuite;
		bNoFill = true;
		defaultFGselect = Color.red;
		defaultBG = Color.lightGray;
		defaultBGselect = Color.lightGray;
		backgroundColor = defaultBG;
	}

	public Structure getStructure() { return structure; }
	public void setStructureSuite(StructureSuite structureSuite) { this.structureSuite = structureSuite; }
	public StructureSuite getStructureSuite() { return structureSuite; }
	@Override public Object getModelObject() { return structure; }

	public void randomize() {
		// randomize the locations of speciesContexts and of reactionSteps,
		// then draw in the reactionParticipant edges
		for(Shape child : childShapeList) {
			if (child instanceof SpeciesContextShape || child instanceof ReactionStepShape){
				// position normally about the center
				child.getSpaceManager().setRelPos(getRandomPosition());
			}	
		}
		// calculate locations and sizes of reactionParticipant edges
		for(Shape child : childShapeList) {
			if (child instanceof ReactionParticipantShape){
				ReactionParticipantShape reactionParticipantShape = (ReactionParticipantShape)child;
				reactionParticipantShape.refreshLayoutSelf();
			}
		}
		// position label
		int centerX = getSpaceManager().getSize().width/2;
		int currentY = labelSize.height;
		labelPos.x = centerX - labelSize.width/2;
		labelPos.y = currentY;
		currentY += labelSize.height;	
	}

	@Override
	public void refreshLabel() {
		setLabel(getStructure().getName());
	}

}
