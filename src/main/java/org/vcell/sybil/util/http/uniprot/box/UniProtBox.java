/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.uniprot.box;

/*   UniProtBox  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   A wrapper for the RDF obtained from UniProt for one entry
 */

import java.util.Collection;
import java.util.Set;

public interface UniProtBox {

	public static interface Thing {
		public UniProtBox box();
	}
	
	public static interface Entry extends Thing {
		public String id();
		public void addReplacedEntry(Entry entry);
		public void removeReplacedEntry(Entry entry);
		public void removeAllReplacedEntries();
		public Set<Entry> replacedEntries();
		public boolean replaces(Entry entry);
		public void addReplacingEntry(Entry entry);
		public void removeReplacingEntry(Entry entry);
		public void removeAllReplacingEntries();
		public Set<Entry> replacingEntries();
		public boolean replacedBy(Entry entry);
		public boolean isObsolete();
		public void setRecommendedName(String name);
		public String recommendedName();
	}
	
	public Entry entry(String id);
	public Collection<Entry> entries();
	public Set<Entry> entriesReplacing(Entry entryReplaced);
	public void removeEntriesReplacing(Entry entryReplaced);
	public Set<Entry> entriesReplacedBy(Entry entryReplacer);
	public void removeEntriesReplacedBy(Entry entryReplacer);
	public void setReplaces(Entry entryReplacer, Entry entryReplaced);
	public void unsetReplaces(Entry entryReplacer, Entry entryReplaced);
	public boolean replaces(Entry entryReplacer, Entry entryReplaced);
	public void remove(Entry entry);
	public boolean entryIsObsolete(Entry entry);
	public boolean entryIsObsolete(String id);
	public void setRecommendedName(Entry entry, String name);
	public String recommendedName(Entry entry);
	public void add(UniProtBox box);
	
}
