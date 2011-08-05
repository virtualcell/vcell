/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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

	@Override
	public boolean equals(Object object) {
		boolean equals = false;
		if(object instanceof UniProtBox.Entry) {
			equals = id.equals(((UniProtBox.Entry) object).id());
		} 
		return equals;
	}
	
	@Override
	public int hashCode() { return id.hashCode(); }

	public void addReplacedEntry(Entry entry) { box().setReplaces(this, entry); }
	public void removeReplacedEntry(Entry entry) { box().unsetReplaces(this, entry); }
	public void removeAllReplacedEntries() { box().removeEntriesReplacedBy(this); }
	public Set<Entry> replacedEntries() { return box().entriesReplacedBy(this); }
	public boolean replaces(Entry entry) { return box().replaces(this, entry); }
	public void addReplacingEntry(Entry entry) { box().setReplaces(entry, this); }
	public void removeReplacingEntry(Entry entry) { box().unsetReplaces(entry, this); }
	public void removeAllReplacingEntries() { box().removeEntriesReplacing(this); }
	public Set<Entry> replacingEntries() { return box().entriesReplacing(this); }
	public boolean replacedBy(Entry entry) { return box().replaces(entry, this); }
	public boolean isObsolete() { return box().entryIsObsolete(this); }
	public String recommendedName() { return box().recommendedName(this); }
	public void setRecommendedName(String name) { box().setRecommendedName(this, name); }

}
