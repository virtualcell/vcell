package cbit.vcell.client.desktop.biomodel.pathway;

import org.vcell.pathway.Conversion;
import org.vcell.pathway.PhysicalEntity;

public class BioPaxConversionEdge {

	protected final Conversion conversion;
	protected final PhysicalEntity physicalEntity;
	
	public BioPaxConversionEdge(Conversion conversion, PhysicalEntity physicalEntity) {
		this.conversion = conversion;
		this.physicalEntity = physicalEntity;
	}
	
	public Conversion getConversion() { return conversion; }
	public PhysicalEntity getPhysicalEntity() { return physicalEntity; }
	
	public boolean equals(Object object) {
		if(object instanceof BioPaxConversionEdge) {
			BioPaxConversionEdge edge = (BioPaxConversionEdge) object;
			return conversion.equals(edge.conversion) && physicalEntity.equals(edge.physicalEntity);
		}
		return false;
	}
	
	public int hashCode() {
		return conversion.hashCode() + physicalEntity.hashCode();
	}
	
}
