package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;

public class ReactionContainerShape extends ContainerShape {
	private Structure structure = null;

	public ReactionContainerShape(Structure structure, GraphModel graphModel) {
		super(graphModel);
		this.structure = structure;
		setRandomLayout(false);
		bNoFill = true;
		defaultFGselect = java.awt.Color.red;
	}

	@Override
	public Object getModelObject() {
		return structure;
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		// get size when empty
		Font origFont = g.getFont();
		g.setFont(getLabelFont(g));
		try{
			Dimension emptySize = super.getPreferedSize(g);
			// make larger than empty size so that children fit
			for (int i = 0; i < childShapeList.size(); i++){
				Shape shape = childShapeList.get(i);
				if (shape instanceof ReactionStepShape || shape instanceof SpeciesContextShape){
					emptySize.width = Math.max(emptySize.width, 
							shape.getSpaceManager().getRelPos().x + shape.getSpaceManager().getSize().width);
					emptySize.height = Math.max(emptySize.height,
							shape.getSpaceManager().getRelPos().y+shape.getSpaceManager().getSize().height);
				}
			}
			return emptySize;
		}finally{
			g.setFont(origFont);
		}
	}

	public Structure getStructure() {
		return structure;
	}

	@Override
	public void refreshLayout() throws LayoutException {
		super.refreshLayout();
		// make sure children fit
		if(LayoutException.bActivated) {
			int width = getSpaceManager().getSize().width;
			int height = getSpaceManager().getSize().height;
			for (Shape child : childShapeList){
				if (child.getSpaceManager().getSize().width + child.getSpaceManager().getRelPos().x > width || 
						child.getSpaceManager().getSize().height + child.getSpaceManager().getRelPos().y > height){
					throw new LayoutException("cannot fit all reactions");
				}
			}		
		}
	}

	@Override
	public void randomize() {
		// randomize the locations of speciesContexts and of reactionSteps,
		// then draw in the reactionParticipant edges
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof SpeciesContextShape || child instanceof ReactionStepShape){
				// position normally about the center
				child.getSpaceManager().setRelPos(getRandomPosition());
			}	
		}
		// calculate locations and sizes of reactionParticipant edges
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof ReactionParticipantShape){

				((ReactionParticipantShape)child).refreshLayout();

			}
		}
		// position label
		int centerX = getSpaceManager().getSize().width/2;
		int currentY = getLabelSize().height;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = currentY;
		currentY += getLabelSize().height;	
	}

	@Override
	public void refreshLabel() {
		if (getStructure() instanceof Membrane){
			setLabel(getStructure().getName() + " (Voltage=\"" + 
					((Membrane) getStructure()).getMembraneVoltage().getName() + "\")");
		} else {
			setLabel(getStructure().getName());
		}
	}
}