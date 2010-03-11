package org.vcell.sybil.models.probe;

/*   Probe  --- by Oliver Ruebenacker, UCHC --- February 2010
 *   An object to obtain a piece of diagnostic information
 */

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public interface Probe {

	public String key();
	public Set<Probe> probes();
	public Map<String, String> results();

	public static abstract class Abstract implements Probe {
		protected final String key;
		public Abstract(String key) { this.key = key; }
		public String key() { return key; }
	}
	
	public static interface Group extends Probe {

		public List<Probe> children();
		
		public static class Default extends Probe.Abstract implements Group {
			protected Vector<Probe> children = new Vector<Probe>();
			public Default(String key) { super(key); };

			public Default(String key, Probe... probes) { 
				this(key, Arrays.asList(probes)); 
			}

			public Default(String key, Collection<? extends Probe> children) { 
				super(key); 
				this.children.addAll(children); 
			};

			public Vector<Probe> children() { return children; }

			public Set<Probe> probes() {
				HashSet<Probe> diagnostics = new HashSet<Probe>();
				for(Probe child: children) { diagnostics.addAll(child.probes()); }
				return diagnostics;
			}
			
			public Map<String, String> results() {
				HashMap<String, String> results = new HashMap<String, String>();
				for(Probe diagnostic : probes()) { results.putAll(diagnostic.results()); }
				return results;
			}

		}
				
	}

	public static interface Element extends Probe {
		public String result();	

		public static abstract class Abstract extends Probe.Abstract implements Element {
			public Abstract(String key) { super(key); }
			public Set<Probe> probes() { return Collections.singleton((Probe) this); }
			public Map<String, String> results() { return Collections.singletonMap(key(), result()); }
		}
		
		public static class Fixed extends Abstract implements Element {
			protected final String result;
			public Fixed(String key, String result) { super(key); this.result = result; }
			public Fixed(Element element) { super(element.key()); this.result = element.result(); }
			public String result() { return result; }
		}
		
	}
	
	
}
