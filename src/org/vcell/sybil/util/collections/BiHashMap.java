package org.vcell.sybil.util.collections;

/*   BiMap  --- by Oliver Ruebenacker, UCHC --- November 2007
 *   An implementation of the  bijective Map. Not only keys are unique, but values are unique, too. 
 *   The method teg() is a reverse get, to obtain a key given a value. 
 *   Note that trying to add a value that duplicates a present value will remove the present conflicting 
 *   key-value pair. When adding entire collections with duplicate values, one key-value pair 
 *   will be picked arbitrarily.
 */


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BiHashMap<A, B> extends HashMap<A, B> implements BiMap<A, B> {

	private static final long serialVersionUID = 6303421113191002897L;

	protected HashMap<B, A> pam;
	
	public BiHashMap() { super(); pam = new HashMap<B, A>(); }
	
	public BiHashMap(int initialCapacity) { super(initialCapacity); pam = new HashMap<B, A>(initialCapacity); }

	public BiHashMap(int initialCapacity, float loadFactor) { 
		super(initialCapacity, loadFactor);
		pam = new HashMap<B, A>(initialCapacity, loadFactor); 
	}
	
	public BiHashMap(Map<? extends A, ? extends B> map) {
		pam = new HashMap<B, A>();
		putAll(map);
	}
		
	
	public A getKey(B b) { return pam.get(b); }
	
	public boolean containsValue(Object b) { return pam.containsKey(b); }
	
	public void clear() {
		super.clear();
		pam.clear();
	}
	
	@SuppressWarnings("unchecked")
	public BiHashMap<A, B> clone() {
		BiHashMap<A, B> newMap = (BiHashMap<A, B>) super.clone();
		newMap.pam = (HashMap<B, A>) pam.clone();
		return newMap;
	}
	
	public B put(A a, B b) {
		A oldA = pam.get(b);
		B oldB = get(a);
		if(oldA != null && !oldA.equals(a)) { super.remove(oldA); }
		if(oldB != null && !oldB.equals(b)) { pam.remove(oldB); }
		super.put(a, b);
		pam.put(b, a);
		return oldB;
	}
	
	public B remove(Object a) {
		B b = super.remove(a);
		pam.remove(b);
		return b;
	}
	
	public void putAll(Map<? extends A, ? extends B> map) {
		Set<B> values = new HashSet<B>();
		// BiMap<A, B> entries = new BiHashMap<A, B>();
		for(Map.Entry<? extends A, ? extends B> entry : map.entrySet()) {
			B value = entry.getValue();
			if(!values.contains(value)) {
				values.add(value);
				// entries.put(entry.getKey(), value);
				put(entry.getKey(), value);				
			}
		}
	}

	public A removeValue(B b) {
		A a = pam.remove(b);
		super.remove(a);
		return a;
	}

}
