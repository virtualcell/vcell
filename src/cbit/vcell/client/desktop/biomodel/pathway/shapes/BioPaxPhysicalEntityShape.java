package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import org.vcell.pathway.PhysicalEntity;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxPhysicalEntityShape extends BioPaxShape {

	public BioPaxPhysicalEntityShape(PhysicalEntity physicalEntity, PathwayGraphModel graphModel) {
		super(physicalEntity, graphModel);
	}
	
	public PhysicalEntity getPhysicalEntity() {
		return (PhysicalEntity) getModelObject();
	}

	protected int getPreferredWidth() { return 16; }
	protected int getPreferredHeight() { return 16; }

}