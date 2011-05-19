package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;
import org.vcell.pathway.Conversion;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxConversionShape extends BioPaxShape {

	public BioPaxConversionShape(Conversion conversion, PathwayGraphModel graphModel) {
		super(conversion, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.yellow; }
	protected int getPreferredWidth() { return 10; }
	protected int getPreferredHeight() { return 10; }
			
	public Conversion getConversion() {
		return (Conversion) getModelObject();
	}

}