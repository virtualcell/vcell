package org.vcell.sybil.models.miriam;

/*   RefGroup  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A group of MIRIAM references (data type plus value, e.g. UniProt P00533)
 */

import java.util.Set;

import org.vcell.sybil.rdf.RDFBag;
import org.vcell.sybil.rdf.RDFBox;

public interface RefGroup<B extends RDFBox> extends RDFBag<B> {
	public RefGroup<B> add(MIRIAMRef ref);
	public RefGroup<B> remove(MIRIAMRef ref);
	public boolean contains(MIRIAMRef ref);
	public Set<MIRIAMRef> refs();
	public RefGroup<B> removeAll();
	public void delete();
}