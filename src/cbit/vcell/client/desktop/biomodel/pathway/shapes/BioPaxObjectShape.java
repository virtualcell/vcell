package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;

import org.vcell.pathway.BioPaxObject;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxObjectShape extends BioPaxShape {

	public BioPaxObjectShape(BioPaxObject bioPaxObject, PathwayGraphModel graphModel) {
		super(bioPaxObject, graphModel);
	}

	protected int getPreferredWidth() { return 14; }
	protected int getPreferredHeight() { return 14; }
	
	public Color getDefaultBackgroundColor() { return Color.gray; }
	
}