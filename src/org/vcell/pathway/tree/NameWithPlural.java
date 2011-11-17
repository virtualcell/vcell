package org.vcell.pathway.tree;

import org.sbpax.util.EnglishPluralizer;

public interface NameWithPlural {
	
	public String getSingular();
	public String getPlural();
	
	public static class SimpleNameWithPlural implements NameWithPlural {
		
		protected final String name, namePlural;
		
		public SimpleNameWithPlural(String name) { 
			this(name, EnglishPluralizer.pluralize(name)); 
		}
		
		public SimpleNameWithPlural(String name, String namePlural) {
			this.name = name;
			this.namePlural = namePlural;
		}

		public String getSingular() { return name; }
		public String getPlural() { return namePlural; }
		
	}
	
}