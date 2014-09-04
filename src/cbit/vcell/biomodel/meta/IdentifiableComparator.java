package cbit.vcell.biomodel.meta;

import java.io.Serializable;
import java.util.Comparator;

import org.vcell.util.document.Identifiable;

public final class IdentifiableComparator implements Comparator<Identifiable>, Serializable {
	private final IdentifiableProvider identifiableProvider;

	public IdentifiableComparator(IdentifiableProvider identifiableProvider) {
		this.identifiableProvider = identifiableProvider;
	}

	public int compare(Identifiable o1, Identifiable o2) {
		VCID vcid1 = identifiableProvider.getVCID(o1);
		VCID vcid2 = identifiableProvider.getVCID(o2);
		return vcid1.toASCIIString().compareTo(vcid2.toASCIIString());
	}
}