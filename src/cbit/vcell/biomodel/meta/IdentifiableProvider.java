package cbit.vcell.biomodel.meta;

import java.util.Set;

public interface IdentifiableProvider {
	VCID getVCID(Identifiable identifiable);
	Identifiable getIdentifiableObject(VCID vcid);
	Set<Identifiable> getAllIdentifiables();
}
