package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;

public class MembraneShape extends StructureShape {
	private static final int memSpacingX = 15;
	private static final int memSpacingY = 15;
	private static double AngleOffset = Math.PI/6;
	private static double BeginAngle = Math.PI/2.0 + AngleOffset;
	private static double EndAngle = 3.0*Math.PI/2.0 - AngleOffset;
	private static double TotalAngle = EndAngle - BeginAngle;

	public MembraneShape(Membrane membrane, Model model, GraphModel graphModel) {
		super(membrane, model, graphModel);
		defaultBG = java.awt.Color.lightGray;
		backgroundColor = defaultBG;
	}

	public Membrane getMembrane() {
		return (Membrane)getStructure();
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		// has 1 child (featureShape)
		FeatureShape featureShape = (FeatureShape)childShapeList.get(0);
		Dimension featureDim = featureShape.getPreferedSize(g);
		getSpaceManager().setSizePreferred((featureDim.width + memSpacingX*2), (featureDim.height + memSpacingY*2));
		return getSpaceManager().getSizePreferred();
	}

	private Point getRadialPosition(double angleRad) {
		//  assuming (x/a)^2 + (y/b)^2 = 1
		int a = getSpaceManager().getSize().width/2 - memSpacingX/2;
		int b = getSpaceManager().getSize().height/2 - memSpacingY/2;
		if (Math.abs(angleRad - Math.PI/2) < 1E-6){
			return new Point(0,b);
		}
		if (Math.abs(angleRad - 3*Math.PI/2) < 1E-6){
			return new Point(0,-b);
		}
		double tan_alpha = Math.tan(angleRad);
		double absX = Math.sqrt(1.0/(1.0/(a*a) + tan_alpha*tan_alpha/(b*b)));
		double absY = Math.abs(tan_alpha) * absX;
		double x, y;
		if (angleRad > Math.PI/2 && angleRad < 3*Math.PI/2){
			x = -absX;
		}else{
			x = absX;
		}		
		if (angleRad > Math.PI && angleRad < 2*Math.PI){
			y = -absY;
		}else{
			y = absY;
		}		
		return new Point((int)x+a,(int)y+b);
	}

	@Override
	public void refreshLayout() {
		// this is like a row/column layout  (1 column)
		int centerX = getSpaceManager().getSize().width/2;
		// find featureShape child
		FeatureShape featureShape = null;
		for (int i=0;i<countChildren();i++){
			if (childShapeList.get(i) instanceof FeatureShape){
				featureShape = (FeatureShape)childShapeList.get(i);
			}
		}
		// calculate total height of all children (not including label)
		// position featureShape (and label)
		int currentY = 0;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = currentY;
		currentY += memSpacingY;
		featureShape.getSpaceManager().setRelPos(centerX - featureShape.getSpaceManager().getSize().width/2, currentY);
		currentY += featureShape.getSpaceManager().getSize().height + memSpacingY;
		// position speciesContextShapes
		// angle = 0 at north pole and increases counter clockwise
		int numSpeciesContexts = countChildren()-1;
		if (numSpeciesContexts>0){
			double deltaAngle = TotalAngle/(numSpeciesContexts+1);
			double currentAngle = BeginAngle + deltaAngle;
			for (int i=0;i<countChildren();i++){
				Shape shape = childShapeList.get(i);
				if (shape instanceof SpeciesContextShape){
					shape.getSpaceManager().setRelPos(getRadialPosition(currentAngle));
					currentAngle = (currentAngle + deltaAngle) % (2*Math.PI);
				}	
			}	
		}				
	}

	@Override
	public void resize(Graphics2D g, Dimension newSize) throws Exception {
		int deltaX = newSize.width - getSpaceManager().getSize().width;
		int deltaY = newSize.height - getSpaceManager().getSize().height;
		getSpaceManager().setSize(newSize);
		// allocate all extra new space to featureShape
		FeatureShape featureShape = (FeatureShape)childShapeList.get(0);
		Dimension featureSize = new Dimension(featureShape.getSpaceManager().getSize());
		featureSize.width += deltaX;
		featureSize.height += deltaY;
		featureShape.resize(g,featureSize);
		refreshLayout();
	}
}