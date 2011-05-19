package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;
import org.vcell.pathway.Protein;

import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxProteinShape extends BioPaxPhysicalEntityShape {

	public BioPaxProteinShape(Protein protein, PathwayGraphModel graphModel) {
		super(protein, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.green; }
	protected int getPreferredWidth() { return 16; }
	protected int getPreferredHeight() { return 16; }
			
	public Protein getProtein() {
		return (Protein) getModelObject();
	}

}