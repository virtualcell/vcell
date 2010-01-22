package org.vcell.sybil.util.http.uniprot.box.imp;

/*   UniProtBox  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   A wrapper for a UniProt entry
 */

import java.util.Set;

import org.vcell.sybil.util.http.uniprot.box.UniProtBox;
import org.vcell.sybil.util.http.uniprot.box.UniProtBox.Entry;

public class EntryImp extends ThingImp implements UniProtBox.Entry {

	protected String id;
	
	public EntryImp(UniProtBox box, String id) { super(box); this.id = id; }

	public String id() { return id; }

	public boolean equals(Object object) {
		boolean equals = false;
		if(object instanceof UniProtBox.Entry) {
			equals = id.equals(((UniProtBox.Entry) object).id());
		} 
		return equals;
	}
	
	public int hashCode() { return id.hashCode(); }

	@Override public void addReplacedEntry(Entry entry) { box().setReplaces(this, entry); }
	@Override public void removeReplacedEntry(Entry entry) { box().unsetReplaces(this, entry); }
	@Override public void removeAllReplacedEntries() { box().removeEntriesReplacedBy(this); }
	@Override public Set<Entry> replacedEntries() { return box().entriesReplacedBy(this); }
	@Override public boolean replaces(Entry entry) { return box().replaces(this, entry); }
	@Override public void addReplacingEntry(Entry entry) { box().setReplaces(entry, this); }
	@Override public void removeReplacingEntry(Entry entry) { box().unsetReplaces(entry, this); }
	@Override public void removeAllReplacingEntries() { box().removeEntriesReplacing(this); }
	@Override public Set<Entry> replacingEntries() { return box().entriesReplacing(this); }
	@Override public boolean replacedBy(Entry entry) { return box().replaces(entry, this); }
	@Override public boolean isObsolete() { return box().entryIsObsolete(this); }
	@Override public String recommendedName() { return box().recommendedName(this); }
	@Override public void setRecommendedName(String name) { box().setRecommendedName(this, name); }

}
