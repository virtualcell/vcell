package org.vcell.sybil.util.http.uniprot.box.imp;

/*   UniProtBox  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   A wrapper for the RDF obtained from UniProt for one entry
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.util.collect.RelationHashMap;
import org.vcell.sybil.util.collect.RelationMap;
import org.vcell.sybil.util.http.uniprot.box.UniProtBox;

public class UniProtBoxImp implements UniProtBox {

	protected Map<String, Entry> entryDir = new HashMap<String, Entry>();
	protected RelationMap<Entry, Entry> replaceMap = new RelationHashMap<Entry, Entry>();
	protected Map<Entry, String> recommendedNamesMap = new HashMap<Entry, String>();
	
	@Override public Collection<Entry> entries() { return entryDir.values(); }

	@Override public Entry entry(String id) {
		Entry entry = entryDir.get(id);
		if(entry == null) {
			entry = new EntryImp(this, id);
			entryDir.put(id, entry);
		}
		return entry;
	}

	@Override public void setReplaces(Entry entryReplacer, Entry entryReplaced) {
		replaceMap.add(entryReplacer, entryReplaced);
	}

	@Override public boolean replaces(Entry entryReplacer, Entry entryReplaced) {
		return replaceMap.contains(entryReplacer, entryReplaced);
	}

	@Override public void remove(Entry entry) {
		entryDir.remove(entry.id());
		replaceMap.removeA(entry);
		replaceMap.removeB(entry);
	}

	@Override public void unsetReplaces(Entry entryReplacer, Entry entryReplaced) {
		replaceMap.remove(entryReplacer, entryReplaced);
	}

	@Override public Set<Entry> entriesReplacing(Entry entryReplaced) {
		return replaceMap.getASet(entryReplaced);
	}

	@Override public void removeEntriesReplacing(Entry entryReplaced) { 
		replaceMap.removeB(entryReplaced);
	}

	@Override public Set<Entry> entriesReplacedBy(Entry entryReplacer) {
		return replaceMap.getBSet(entryReplacer);
	}

	@Override public void removeEntriesReplacedBy(Entry entryReplacer) { 
		replaceMap.removeA(entryReplacer);
	}

	@Override public boolean entryIsObsolete(Entry entry) { return replaceMap.containsB(entry); }
	@Override public boolean entryIsObsolete(String id) { return entryIsObsolete(this.entry(id)); }

	@Override public String recommendedName(Entry entry) { return recommendedNamesMap.get(entry); }
	@Override public void setRecommendedName(Entry entry, String name) { recommendedNamesMap.put(entry, name); }

	@Override public void add(UniProtBox box) {
		for(Entry entry : box.entries()) {
			Entry entryThis = entry(entry.id());
			for(Entry entryReplaced : entry.replacedEntries()) {
				entryThis.addReplacedEntry(entryReplaced);
			}
			for(Entry entryReplacing : entry.replacingEntries()) {
				entryThis.addReplacingEntry(entryReplacing);
			}
			String recommendedName = entry.recommendedName();
			// System.out.println("copying " + recommendedName);
			if(recommendedName != null) {
				entryThis.setRecommendedName(recommendedName);				
			}
		}
		
	}

	
}
