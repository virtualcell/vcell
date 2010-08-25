package cbit.gui.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import java.awt.*;
/**
 * This type was created in VisualAge.
 */
public class SimpleContainerShape extends ContainerShape {
	private Object fieldObject = null;
	/**
	 * ReactionContainerShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public SimpleContainerShape(Object object, GraphModel graphModel, String argLabel) {
		super(graphModel);
		setLabel(argLabel);
		setRandomLayout(false);
		fieldObject = object;
		bNoFill = false;
		defaultFGselect = java.awt.Color.red;
		defaultBGselect = java.awt.Color.white;
		backgroundColor = java.awt.Color.white;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 */
	@Override
	public Object getModelObject() {
		return fieldObject;
	}
	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {

		//
		// get size when empty
		//
		Dimension emptySize = super.getPreferedSize(g);

		//
		// make larger than empty size so that children fit
		//
		for (int i = 0; i < childShapeList.size(); i++){
			Shape shape = childShapeList.elementAt(i);
			if (shape instanceof ElipseShape){
				emptySize.width = Math.max(emptySize.width,shape.getLocation().x+shape.getSize().width);
				emptySize.height = Math.max(emptySize.height,shape.getLocation().y+shape.getSize().height);
			}
		}
		return emptySize;
	}

	@Override
	public void layout() throws LayoutException {
		//System.out.println("ReactionContainerShape.layout(), bRandomize="+bRandomize);
		super.layout();
		//
		// make sure children fit
		//
		if(LayoutException.bActivated) {
			int width = getSize().width;
			int height = getSize().height;

			for (int i = 0; i < childShapeList.size(); i++){
				Shape child = childShapeList.elementAt(i);
				if (child.getSize().width+child.getLocation().x > width || child.getSize().height+child.getLocation().y > height){
					throw new LayoutException("cannot fit all reactions");
				}
			}		
		}
	}

	@Override
	public void randomize() {

		//System.out.println("ReactionContainerShape.randomize(), bRandomize="+bRandomize);
		//
		// randomize the locations of speciesContexts and of reactionSteps,
		// then draw in the reactionParticipant edges
		//
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.elementAt(i);
			if (child instanceof ElipseShape){
				//
				// position normally about the center
				//
				child.setLocation(getRandomPosition());
			}	
		}

		//
		// calculate locations and sizes of reactionParticipant edges
		//
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.elementAt(i);
			if (child instanceof EdgeShape){

				((EdgeShape)child).layout();

			}
		}

		//
		// position label
		//
		int centerX = shapeSize.width/2;
		int currentY = labelSize.height;
		labelPos.x = centerX - labelSize.width/2;
		labelPos.y = currentY;
		currentY += labelSize.height;	
	}
	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void refreshLabel() {
	}
}
