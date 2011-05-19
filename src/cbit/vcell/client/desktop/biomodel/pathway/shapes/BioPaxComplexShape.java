package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;

import org.vcell.pathway.Complex;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxComplexShape extends BioPaxPhysicalEntityShape {

	public BioPaxComplexShape(Complex complex, PathwayGraphModel graphModel) {
		super(complex, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.orange; }
	protected int getPreferredWidth() { return 18; }
	protected int getPreferredHeight() { return 18; }
			
	public Complex getComplex() {
		return (Complex) getModelObject();
	}

}