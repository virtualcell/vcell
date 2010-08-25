package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;
import java.awt.*;
/**
 * This type was created in VisualAge.
 */
public class GeometryContextStructureShape extends ContainerShape {
	Model model = null;

	/**
	 * This method was created in VisualAge.
	 * @param graphModel cbit.vcell.graph.GraphModel
	 * @param model cbit.vcell.model.Model
	 * @param featureContainerShape cbit.vcell.graph.ReactionContainerShape
	 */
	public GeometryContextStructureShape(GraphModel graphModel, Model model) {
		super(graphModel);
		this.model = model;
		defaultBG = java.awt.Color.lightGray;
		backgroundColor = defaultBG;
		setLabel("Physiology (structures)");
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 */
	@Override
	public Object getModelObject() {
		return model;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		Dimension d = super.getPreferedSize(g);
		Dimension childDim = null;
		if (childShapeList.size()!=0){
			childDim = (childShapeList.elementAt(0)).getPreferedSize(g);
		}else{
			childDim = new Dimension(100,100);
		}
		Dimension newDim = new Dimension(childDim.width, d.height+childDim.height);
		return newDim;
	}


	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void layout() throws LayoutException {

		super.layout();

		int centerX = shapeSize.width/2;
		int centerY = shapeSize.height/2;

		//
		// calculate total height and max width of SubVolumeContainerShape
		//
		int childHeight = 0;
		int childWidth = 0;
		for (int i=0;i<childShapeList.size();i++){
			Shape shape = childShapeList.elementAt(i);
			childHeight += shape.shapeSize.height;
			childWidth = Math.max(childWidth,shape.shapeSize.width);
		}

		int currY = Math.max(0,centerY - childHeight/2) + labelSize.height+2;

		for (int i=0;i<childShapeList.size();i++){
			Shape shape = childShapeList.elementAt(i);
			shape.relativePos.x = centerX - shape.shapeSize.width/2;
			shape.relativePos.y = currY;
			currY += shape.shapeSize.height;
			shape.layout();
		}
	}
}