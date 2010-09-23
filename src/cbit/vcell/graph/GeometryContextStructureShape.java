package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Dimension;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Model;

public class GeometryContextStructureShape extends ContainerShape {
	Model model = null;

	public GeometryContextStructureShape(GraphModel graphModel, Model model) {
		super(graphModel);
		this.model = model;
		defaultBG = java.awt.Color.lightGray;
		backgroundColor = defaultBG;
		setLabel("Physiology (structures)");
	}


	@Override
	public Object getModelObject() {
		return model;
	}


	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		Dimension d = super.getPreferedSize(g);
		Dimension childDim = null;
		if (childShapeList.size()!=0){
			childDim = (childShapeList.get(0)).getPreferedSize(g);
		}else{
			childDim = new Dimension(100,100);
		}
		Dimension newDim = new Dimension(childDim.width, d.height+childDim.height);
		return newDim;
	}

	@Override
	public void refreshLayout() throws LayoutException {
		super.refreshLayout();
		int centerX = getSpaceManager().getSize().width/2;
		int centerY = getSpaceManager().getSize().height/2;
		// calculate total height and max width of SubVolumeContainerShape
		int childHeight = 0;
		int childWidth = 0;
		for (int i=0;i<childShapeList.size();i++){
			Shape shape = childShapeList.get(i);
			childHeight += shape.getSpaceManager().getSize().height;
			childWidth = Math.max(childWidth,shape.getSpaceManager().getSize().width);
		}
		int currY = Math.max(0,centerY - childHeight/2) + getLabelSize().height + 2;
		for (int i=0;i<childShapeList.size();i++){
			Shape shape = childShapeList.get(i);
			shape.getSpaceManager().setRelPos(centerX - shape.getSpaceManager().getSize().width/2, currY);
			currY += shape.getSpaceManager().getSize().height;
			shape.refreshLayout();
		}
	}
}