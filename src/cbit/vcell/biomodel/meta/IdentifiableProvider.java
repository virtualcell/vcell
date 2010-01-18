package cbit.vcell.biomodel.meta;

public interface IdentifiableProvider {
	VCID getVCID(Identifiable identifiable);
	Identifiable getIdentifiableObject(VCID vcid);
}
