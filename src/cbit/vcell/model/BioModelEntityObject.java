package cbit.vcell.model;

import cbit.vcell.biomodel.meta.Identifiable;

public interface BioModelEntityObject extends Identifiable{
	String getName();
	String getTypeLabel();
	Structure getStructure();
}
