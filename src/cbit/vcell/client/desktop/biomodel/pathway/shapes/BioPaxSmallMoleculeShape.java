package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;
import org.vcell.pathway.SmallMolecule;

import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxSmallMoleculeShape extends BioPaxPhysicalEntityShape {

	public BioPaxSmallMoleculeShape(SmallMolecule smallMolecule, PathwayGraphModel graphModel) {
		super(smallMolecule, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.blue; }
	protected int getPreferredWidth() { return 12; }
	protected int getPreferredHeight() { return 12; }
			
	public SmallMolecule getConversion() {
		return (SmallMolecule) getModelObject();
	}

}