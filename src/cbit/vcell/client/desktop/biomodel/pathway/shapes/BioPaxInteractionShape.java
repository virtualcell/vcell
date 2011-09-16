package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;

import org.vcell.pathway.Interaction;

import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxInteractionShape extends BioPaxShape{
	public BioPaxInteractionShape(Interaction interaction, PathwayGraphModel graphModel) {
		super(interaction, graphModel);
	}
	protected Color getDefaultBackgroundColor() { return Color.yellow; }
	protected int getPreferredWidth() { return 10; }
	protected int getPreferredHeight() { return 10; }
			
	public Interaction getInteraction() {
		return (Interaction) getModelObject();
	}
}

