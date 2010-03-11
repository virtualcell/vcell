package org.vcell.sybil.sb.taxon;

/*   OrganismRef  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   An organism reference according to the NCBI taxonomy
 */

import java.util.List;
import java.util.Vector;

public interface OrganismRef {

	public int id();
	public String taxonName();
	public List<String> names();
	
	public static class Imp implements OrganismRef {
		
		protected int id;
		protected String taxonName;
		protected List<String> names = new Vector<String>();

		public Imp(int id, String taxonName) { 
			this.id = id; this.taxonName = taxonName; names.add(taxonName); 
		}

		public Imp(int id, String taxonName, String name) { 
			this(id, taxonName); names.add(name); 
		}
		
		public Imp(int id, String taxonName, String name1, String name2) { 
			this(id, taxonName); names.add(name1); names.add(name2); 
		}
		
		public Imp(int id, String taxonName, String name1, String name2, String name3) { 
			this(id, taxonName); names.add(name1); names.add(name2); names.add(name3); 
		}

		public Imp(int id, String taxonName, List<String> names) { 
			this(id, taxonName); names.addAll(names); 
		}
		
		public int id() { return id; }
		public String taxonName() { return taxonName; }
		public List<String> names() { return names; }
		public int hashCode() { return id(); }

		public boolean equals(Object o) {
			boolean equals = false;
			if(o instanceof OrganismRef) { equals = id() == ((OrganismRef) o).id(); }
			return equals;
		}
		
	}
	
}
