package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.ImmutableVisualState;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

public abstract class StructureShape extends ElipseShape {
	private Structure structure = null;
	private Model model = null;
	protected static final int defaultSpacingX = 30;
	protected static final int defaultSpacingY = 10;


	public StructureShape (Structure structure, Model model, GraphModel graphModel) {
		super(graphModel);
		this.structure = structure;
		this.model = model;
	}

	@Override
	public VisualState createVisualState() {
		return new ImmutableVisualState(this, VisualState.PaintLayer.COMPARTMENT);
	}

	public Font getLabelFont(Graphics g) {
		return getBoldFont(g);
	}

	public cbit.vcell.model.Model getModel() {
		return model;
	}

	@Override
	public Object getModelObject() {
		return structure;
	}

	public Structure getStructure() {
		return structure;
	}

	@Override
	public void paintSelf ( Graphics2D g, int absPosX, int absPosY ) {
		g.setColor(backgroundColor);
		g.fillOval(absPosX + 1, absPosY + 1, getSpaceManager().getSize().width - 1, getSpaceManager().getSize().height - 1);
		g.setColor(forgroundColor);
		g.drawOval(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
		if (getLabel()!=null && getLabel().length()>0){
			Font origFont = g.getFont();
			g.setFont(getLabelFont(g));
			if(this instanceof FeatureShape){
				if(isSelected()){
					drawRaisedOutline(absPosX + getLabelPos().x - 5, 
							absPosY + getLabelPos().y - labelSize.height + 3, 
							labelSize.width + 10,labelSize.height, g, Color.white, Color.black, 
							Color.black);
				}
				g.setColor(Color.black);
				g.drawString(getLabel(), getLabelPos().x + absPosX, 
						getLabelPos().y + absPosY);
			} else {
				int textX = absPosX + getSpaceManager().getSize().width/2 - labelSize.width/2;
				int textY = absPosY + labelSize.height -3;
				if(isSelected()){
					drawRaisedOutline(textX - 5, textY - labelSize.height + 3, labelSize.width + 10,
							labelSize.height, g, Color.white, Color.black, Color.black);
				}
				g.setColor(Color.black);
				g.drawString(getLabel(),textX,textY);
			}
			g.setFont(origFont);
		}
	}

	@Override
	public void refreshLabel() {
		setLabel(getStructure().getName());
	}
}