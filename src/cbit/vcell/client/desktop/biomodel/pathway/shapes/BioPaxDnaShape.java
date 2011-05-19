package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;
import org.vcell.pathway.Dna;

import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxDnaShape extends BioPaxPhysicalEntityShape {

	public BioPaxDnaShape(Dna dna, PathwayGraphModel graphModel) {
		super(dna, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.magenta; }
	protected int getPreferredWidth() { return 16; }
	protected int getPreferredHeight() { return 16; }
			
	public Dna getDna() {
		return (Dna) getModelObject();
	}

}